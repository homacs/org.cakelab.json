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

public class JSONModeller extends CodecBase {
	protected static final String SPECIAL_ATTRIBUTE_CLASS = "class";
	protected ReflectionHelper reflectionHelper = new ReflectionHelper();
	protected UnsafeAllocator allocator = UnsafeAllocator.create();

	public JSONModeller(JSONCodecConfiguration cfg) {
		super(cfg);
	}
	
	
	public <T> T decodeObject(JSONObject jsonObject, Class<T> targetType) throws JSONException {
		try {
			return _decodeObject(jsonObject, targetType);
		} catch (InstantiationException e) {
			throw new JSONException(e);
		}
	}

	@SuppressWarnings("unchecked")
	public <T> T decodeObject(JSONObject json, T targetObject) throws JSONException {
		return (T) _decodeObject(json, targetObject);
	}

	
	
	Object _decodeObject(Object jsonAny, Object targetObject) throws JSONException {
		if (jsonAny instanceof JSONObject) {
			return json2object((JSONObject) jsonAny, targetObject);
		} else if (jsonAny instanceof JSONArray) {
			return json2array((JSONArray) jsonAny, targetObject);
		} else {
			return json2primitive(jsonAny.toString(), targetObject.getClass());
		}
	}

	@SuppressWarnings("unchecked")
	<T> T _decodeObject(Object jsonAny, Class<T> targetType) throws JSONException, InstantiationException {
		if (jsonAny == null) return null;
		if (jsonAny instanceof JSONObject) {
			if (cfg.supportClassAttribute) {
				String clazz = ((JSONObject) jsonAny).getString(SPECIAL_ATTRIBUTE_CLASS);
				if (clazz != null) {
					try {
						Class<?> derived = JSONCodec.class.getClassLoader().loadClass(clazz);
						if (!ReflectionHelper.isSubclassOf(derived, targetType)) throw new JSONException("class " + clazz + " is not a subclass of " + targetType.getSimpleName());
						else targetType = (Class<T>) derived;
					} catch (ClassNotFoundException e) {
						throw new JSONException(e);
					}
				}
			}
			if (targetType.isEnum()) {
				return (T) json2enum((JSONObject) jsonAny, targetType);
			} else {
				return (T) json2object((JSONObject) jsonAny, allocator.newInstance(targetType));
			}
		} else if (jsonAny instanceof JSONArray) {
			return (T) json2array((JSONArray) jsonAny, Array.newInstance(targetType.getComponentType(), ((JSONArray) jsonAny).size()));
		} else {
			return (T) json2primitive(jsonAny.toString(), targetType);
		}
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
						field.set(targetObject, json2primitive(jsonField.getValue().toString(), field.getType()));
					} else {
						field.set(targetObject, _decodeObject(jsonField.getValue(), field.getType()));
					}
					field.setAccessible(accessible);
				} catch (NoSuchFieldException ex) {
					if (!cfg.ignoreMissingFields) throw new JSONException(ex);
				}
			}
		} catch (IllegalArgumentException | IllegalAccessException | InstantiationException e) {
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
		try {
			for (int i = 0; i < json.size(); i++) {
				Object jsonValue = json.get(i);
				Object value;
					value = _decodeObject(jsonValue, target.getClass().getComponentType());
				Array.set(target, i, value);
			}
		} catch (InstantiationException e) {
			throw new JSONException(e);
		}
		return target;
	}

	private Object json2primitive(String body, Class<?> type) {
		if (type.equals(String.class)) {
			return body;
		} else if (type.equals(Character.class)) {
			body = body.trim(); 
			return (body.length() > 0) ? body.charAt(0) : null;
		} else if (type.equals(Long.class) || type.equals(long.class)) {
			return Long.decode(body);
		} else if (type.equals(Integer.class) || type.equals(int.class)) {
			return Integer.decode(body);
		} else if (type.equals(Double.class) || type.equals(double.class)) {
			return Double.parseDouble(body);
		} else if (type.equals(Float.class) || type.equals(float.class)) {
			return Float.parseFloat(body);
		} else if (type.equals(Short.class) || type.equals(short.class)) {
			return Short.parseShort(body);
		} else if (type.equals(Byte.class) || type.equals(byte.class)) {
			return Byte.parseByte(body);
		} else if (type.equals(Boolean.class) || type.equals(boolean.class)) {
			return Boolean.parseBoolean(body);
		}
		return null;
	}

	/** returns a JSONObject, JSONArray or primitive value (including String)
	 * depending on the given object and reference type.
	 * @param o Object to be encoded.
	 * @param referenceType Class of the object or some subclass, in case you want just a particular subset of the members */
	public Object encodeObjectJSON(Object o, Class<?> referenceType) throws JSONException {
		try {
			// TODO: needs refactoring: see decodeObject(JSONObject) and decodeObject(String)
		
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

	/** Encodes the given object into a JSONObject, JSONArray 
	 * or a JSON supported primitive type (incl. String), 
	 * depending on the given object type.
	 * @param o
	 * @return
	 * @throws JSONException 
	 */
	public Object encodeObjectJSON(Object o) throws JSONException {
		return encodeObjectJSON(o, o.getClass());
	}

	
	private Object primitive2json(Object o) {
		return o;
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
				json.put(field.getName(), encodeObjectJSON(value, field.getType()));
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
				json.add(encodeObjectJSON(value, o.getClass().getComponentType()));
			}
		}
		return json;
	}
	

	private boolean isIgnoredField(Field field) {
		int mod = field.getModifiers();
		return Modifier.isTransient(mod) || Modifier.isStatic(mod) || Modifier.isNative(mod);
	}

}
