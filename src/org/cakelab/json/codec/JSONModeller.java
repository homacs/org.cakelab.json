package org.cakelab.json.codec;

import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Map.Entry;

import org.cakelab.json.JSONArray;
import org.cakelab.json.JSONException;
import org.cakelab.json.JSONObject;

/**
 * Turns Java object into JSON object tree and vice versa.
 * 
 * @author homac
 *
 */
public class JSONModeller {
	
	protected static final String SPECIAL_ATTRIBUTE_CLASS = "class";
	protected ReflectionHelper reflectionHelper = new ReflectionHelper();
	protected UnsafeAllocator allocator = UnsafeAllocator.create();

	protected JSONCodecConfiguration cfg;

	
	public JSONModeller(JSONCodecConfiguration cfg) {
		this.cfg = new JSONCodecConfiguration(cfg);
	}

	@SuppressWarnings("unchecked")
	public <T> T toJavaObject(Object jsonAny, T targetObject) throws JSONException {
		if (jsonAny instanceof JSONObject) {
			Class<T> type = (Class<T>) targetObject.getClass();
			if (type.isEnum()) {
				return (T)json2enum((JSONObject)jsonAny, type);
			} else {
				return (T)json2object((JSONObject) jsonAny, targetObject);
			}
		} else if (jsonAny instanceof JSONArray) {
			return (T)json2array((JSONArray) jsonAny, targetObject);
		} else {
			return (T)json2primitive(jsonAny, targetObject.getClass());
		}
	}


	@SuppressWarnings("unchecked")
	public <T> T toJavaObject(Object jsonAny, Class<T> targetType) throws JSONException {
		try {
			if (jsonAny == null) return null;
			if (jsonAny instanceof JSONObject) {
				Class<? extends T> effectiveType = getDerivedType(jsonAny, targetType);
				if (effectiveType.isEnum()) {
					return (T) json2enum((JSONObject) jsonAny, effectiveType);
				} else {
					return (T) json2object((JSONObject) jsonAny, allocator.newInstance(effectiveType));
				}
			} else if (jsonAny instanceof JSONArray) {
				return (T) json2array((JSONArray) jsonAny, Array.newInstance(targetType.getComponentType(), ((JSONArray) jsonAny).size()));
			} else {
				return (T) json2primitive(jsonAny, targetType);
			}
		} catch (InstantiationException e) {
			throw new JSONException(e);
		}
	}


	/** Encodes the given Java object into a JSON object. */
	public Object toJSON(Object javaObject) throws JSONException {
		return toJSON(javaObject, javaObject.getClass());
	}

	/** returns a JSONObject, JSONArray or primitive value (including String)
	 * depending on the given object and reference type.
	 * @param o Object to be encoded.
	 * @param referenceType Class of the object or some subclass, in case you want just a particular subset of the members */
	public Object toJSON(Object o, Class<?> referenceType) throws JSONException {
		try {
			Class<?> type = o.getClass();
			
			if (ReflectionHelper.isPrimitive(type)) {
				return primitive2json(o);
			} else if (type.isArray()) {
				return array2json(o);
			} else {
				return object2json(o, referenceType);
			}
		} catch (JSONException e) {
			throw e;
		} catch (Exception e) {
			throw new JSONException(e);
		}
	}

	@SuppressWarnings("unchecked")
	private <T> Class<? extends T> getDerivedType(Object jsonAny, Class<T> targetType) throws JSONException {
		if (cfg.supportClassAttribute) {
			String clazz = ((JSONObject) jsonAny).getString(SPECIAL_ATTRIBUTE_CLASS);
			if (clazz != null) {
				try {
					Class<?> derived = JSONCodec.class.getClassLoader().loadClass(clazz);
					if (!ReflectionHelper.isSubclassOf(derived, targetType)) 
						throw new JSONException("class " + clazz + " is not a subclass of " + targetType.getSimpleName());
					else 
						return (Class<? extends T>)derived;
				} catch (ClassNotFoundException e) {
					throw new JSONException(e);
				}
			}
		}
		return targetType;
	}

	private Object json2object(JSONObject jsonObject, Object targetObject) throws JSONException {
		Class<? extends Object> type = targetObject.getClass();
		try {
			for (Entry<String, Object> jsonField : jsonObject.entrySet()) {
				try {
					if (cfg.supportClassAttribute && jsonField.getKey().equals(SPECIAL_ATTRIBUTE_CLASS)) continue;
					
					Field field = ReflectionHelper.getDeclaredField(type, jsonField.getKey());
					if (isIgnoredField(field)) continue;
					boolean accessible = field.isAccessible();
					field.setAccessible(true);
					if (ReflectionHelper.isPrimitive(field.getType())) {
						field.set(targetObject, json2primitive(jsonField.getValue(), field.getType()));
					} else {
						field.set(targetObject, toJavaObject(jsonField.getValue(), field.getType()));
					}
					field.setAccessible(accessible);
				} catch (NoSuchFieldException ex) {
					if (!cfg.ignoreMissingFields) throw new JSONException(ex);
				}
			}
		} catch (IllegalArgumentException | IllegalAccessException e) {
			throw new JSONException(e);
		}
		
		return targetObject;
	}


	@SuppressWarnings("unchecked")
	private <T> T json2enum(JSONObject jsonEnum, Class<T> targetType) throws JSONException {
		
		try {
			Method valueOf = targetType.getMethod("valueOf", String.class);
			String name = jsonEnum.getString("name");
			if (name == null) throw new JSONException("missing name of enum value for enum " + targetType.getCanonicalName());
			return (T) valueOf.invoke(null, name);
		} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			throw new JSONException(e);
		}
	}

	private Object json2array(JSONArray json, Object target) throws JSONException {
		for (int i = 0; i < json.size(); i++) {
			Object jsonValue = json.get(i);
			Object value;
				value = toJavaObject(jsonValue, target.getClass().getComponentType());
			Array.set(target, i, value);
		}
		return target;
	}

	private Object json2primitive(Object value, Class<?> type) {
		if (type.equals(String.class)) {
			return (String)value;
		} else if (type.equals(Character.class) || type.equals(char.class)) {
			return (char)toLong(value);
		} else if (type.equals(Long.class) || type.equals(long.class)) {
			return toLong(value);
		} else if (type.equals(Integer.class) || type.equals(int.class)) {
			return (int)toLong(value);
		} else if (type.equals(Double.class) || type.equals(double.class)) {
			return toDouble(value);
		} else if (type.equals(Float.class) || type.equals(float.class)) {
			return (float)toDouble(value);
		} else if (type.equals(Short.class) || type.equals(short.class)) {
			return (short)toLong(value);
		} else if (type.equals(Byte.class) || type.equals(byte.class)) {
			return (byte)toLong(value);
		} else if (type.equals(Boolean.class) || type.equals(boolean.class)) {
			return (boolean)value;
		}
		return null;
	}
	
	private long toLong(Object o) {
		if (o instanceof Double) return (long)(double)o;
		return (long)o;
	}

	private double toDouble(Object o) {
		if (o instanceof Long) return (double)(long)o;
		return (double)o;
	}


	
	private Object primitive2json(Object o) {
		if (o == null) {
			return o;
		} else if (o instanceof Byte) {
			return (long)(byte)o;
		} else if (o instanceof Character) {
			return (long)(char)o;
		} else if (o instanceof Short) {
			return (long)(short)o;
		} else if (o instanceof Integer) {
			return (long)(int)o;
		} else if (o instanceof Float) {
			return (double)(float)o;
		} else {
			// long, double, boolean and String
			return o;
		}
	}

	private JSONObject object2json(Object o, Class<?> referenceType) throws JSONException, IllegalArgumentException, IllegalAccessException {
		JSONObject json = new JSONObject();
		Class<?> type = o.getClass();
		for (Field field : reflectionHelper.getDeclaredFields(type)) {
			
			// ignore transient fields
			if (isIgnoredField(field)) continue;
			boolean accessible = field.isAccessible();
			field.setAccessible(true);
			Object value = field.get(o);
			if (value == null) {
				if (!cfg.ignoreNull) json.put(field.getName(), null);
			} else {
				json.put(field.getName(), toJSON(value, field.getType()));
			}
			field.setAccessible(accessible);
		}
		if (referenceType != null && referenceType != type) {
			json.put(SPECIAL_ATTRIBUTE_CLASS, type.getName());
		}
		return json;
	}

	private JSONArray array2json(Object o) throws ArrayIndexOutOfBoundsException, IllegalArgumentException, IOException, JSONException, JSONException {
		JSONArray json = new JSONArray();
		for (int i = 0; i < Array.getLength(o); i++) {
			Object value = Array.get(o, i);
			if (value == null) {
				if (!cfg.ignoreNull) json.add(null);
			} else {
				json.add(toJSON(value, o.getClass().getComponentType()));
			}
		}
		return json;
	}
	

	private boolean isIgnoredField(Field field) {
		int mod = field.getModifiers();
		return Modifier.isTransient(mod) || Modifier.isStatic(mod) || Modifier.isNative(mod);
	}

}
