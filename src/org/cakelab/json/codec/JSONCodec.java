package org.cakelab.json.codec;

import static org.cakelab.json.JSONDefaults.CODEC_CONFIG;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Map.Entry;

import org.cakelab.json.JSONArray;
import org.cakelab.json.JSONCompoundType;
import org.cakelab.json.JSONDefaults;
import org.cakelab.json.JSONException;
import org.cakelab.json.JSONObject;
import org.cakelab.json.parser.JSONParser;

/**
 * This class implements serialising/deserialising 
 * of Java classes to and from JSON strings.
 * 
 * <h3>Multi-Threading</h3>
 * Not thread safe.
 * 
 * @author homac
 *
 */
public class JSONCodec {
	
	private static final String SPECIAL_ATTRIBUTE_CLASS = "class";
	
	private UnsafeAllocator allocator = UnsafeAllocator.create();
	private JSONCodecConfiguration cfg;
	private JSONParser parser;
	private ReflectionHelper reflectionHelper = new ReflectionHelper();
	
	/**
	 * Constructor using the default configuration.
	 * @see {@link JSONCodecConfiguration}
	 * @see {@link JSONDefaults#CODEC_CONFIG}
	 */
	public JSONCodec() {
		this(CODEC_CONFIG);
	}

	/** Constructor using a specific configuration.
	 * @param config Configuration to be used by the codec.
	 */
	public JSONCodec(JSONCodecConfiguration config) {
		this.cfg = new JSONCodecConfiguration(config);
		this.parser = cfg.parserFactory.create(cfg.ignoreNull);
	}
	
	/** decodes the given json string into the given target object. 
	 * @throws JSONException 
	 */
	public Object decodeObject(String jsonString, Object target) throws JSONException {

		try {
			JSONObject json = parser.parseObject(jsonString);
			
			return _decodeObject(json, target);
		} catch (JSONException e) {
			throw e;
		} catch (Exception e) {
			throw new JSONException(e);
		}
	}
	
	/** decodes the given json string from input stream into the given target object. 
	 * @throws JSONException 
	 */
	public Object decodeObject(InputStream inputStream, Object target) throws JSONException {

		try {
			JSONObject json = parser.parseObject(inputStream, cfg.charset);
			
			return _decodeObject(json, target);
		} catch (JSONException e) {
			throw e;
		} catch (Exception e) {
			throw new JSONException(e);
		}
	}

	
	/** decodes the given json string from input stream into an object of the given target type. 
	 * @throws JSONException 
	 */
	public Object decodeObject(InputStream inputStream,
			Class<?> target)  throws JSONException {
		try {
			JSONObject json = parser.parseObject(inputStream, cfg.charset);
			
			return _decodeObject(json, target);
		} catch (JSONException e) {
			throw e;
		} catch (Exception e) {
			throw new JSONException(e);
		}
	}


	
	/** decodes the given json string into an object of the given target type. 
	 * @throws JSONException 
	 */
	public <T> T decodeObject(String jsonString, Class<T> type) throws JSONException {

		try {
			JSONParser parser = cfg.parserFactory.create(cfg.ignoreNull);
			JSONObject json = parser.parseObject(jsonString);
			
			return _decodeObject(json, type);
		} catch (JSONException e) {
			throw e;
		} catch (Exception e) {
			throw new JSONException(e);
		}
	}
	
	
	public <T> T decodeObject(JSONObject json, Class<T> clazz) throws JSONException {
		try {
			return _decodeObject(json, clazz);
		} catch (InstantiationException e) {
			throw new JSONException(e);
		}
	}


	@SuppressWarnings("unchecked")
	public <T> T decodeObject(JSONObject json, T target) throws JSONException {
		return (T) _decodeObject(json, target);
	}
	
	private Object _decodeObject(Object json, Object target) throws JSONException {

		if (json instanceof JSONObject) {
			return json2object((JSONObject) json, target);
		} else if (json instanceof JSONArray) {
			return json2array((JSONArray) json, target);
		} else {
			return json2primitive(json.toString(), target.getClass());
		}
		
	}

	@SuppressWarnings("unchecked")
	private <T> T _decodeObject(Object json, Class<T> type) throws JSONException, InstantiationException {
		if (json == null) return null;
		if (json instanceof JSONObject) {
			if (cfg.supportClassAttribute) {
				String clazz = ((JSONObject) json).getString(SPECIAL_ATTRIBUTE_CLASS);
				if (clazz != null) {
					try {
						Class<?> derived = JSONCodec.class.getClassLoader().loadClass(clazz);
						if (!ReflectionHelper.isSubclassOf(derived, type)) throw new JSONException("class " + clazz + " is not a subclass of " + type.getSimpleName());
						else type = (Class<T>) derived;
					} catch (ClassNotFoundException e) {
						throw new JSONException(e);
					}
				}
			}
			if (type.isEnum()) {
				return (T) json2enum((JSONObject) json, type);
			} else {
				return (T) json2object((JSONObject) json, allocator.newInstance(type));
			}
		} else if (json instanceof JSONArray) {
			return (T) json2array((JSONArray) json, Array.newInstance(type.getComponentType(), ((JSONArray) json).size()));
		} else {
			return (T) json2primitive(json.toString(), type);
		}
		
	}


	@SuppressWarnings("unchecked")
	private <T> T json2enum(JSONObject json, Class<T> type) throws JSONException {
		
		try {
			Method valueOf = type.getMethod("valueOf", String.class);
			String name = json.getString("name");
			if (name == null) throw new JSONException("missing name of enum value for enum " + type.getCanonicalName());
			return (T) valueOf.invoke(null, name);
		} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			throw new JSONException(e);
		}
	}

	private Object json2object(JSONObject json, Object target) throws JSONException {
		Class<? extends Object> type = target.getClass();
		try {
			for (Entry<String, Object> e : json.entrySet()) {
				try {
					if (cfg.supportClassAttribute && e.getKey().equals(SPECIAL_ATTRIBUTE_CLASS)) continue;
					
					Field field = ReflectionHelper.getDeclaredField(type, e.getKey());
					if (isIgnoredField(field)) continue;
					boolean accessible = field.isAccessible();
					field.setAccessible(true);
					if (ReflectionHelper.isPrimitive(field.getType())) {
						field.set(target, json2primitive(e.getValue().toString(), field.getType()));
					} else {
						field.set(target, _decodeObject(e.getValue(), field.getType()));
					}
					field.setAccessible(accessible);
				} catch (NoSuchFieldException ex) {
					if (!cfg.ignoreMissingFields) throw new JSONException(ex);
				}
			}
		} catch (IllegalArgumentException | IllegalAccessException | InstantiationException e) {
			throw new JSONException(e);
		}
		
		return target;
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


	public String encodeObject(Object o) throws JSONException {
		try {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			encodeObject(o, out);
			String charset = cfg.formatter.getConfiguration().charset.name();
			return out.toString(charset);
		} catch (Exception e) {
			throw new JSONException(e);
		}
	}

	public void encodeObject(Object o, OutputStream out) throws JSONException {
		Object json;
		
		if (o == null) {
			throw new JSONException("Cannot encode a toplevel null object");
		}
		
		if (o instanceof JSONCompoundType) {
			json = o;
		} else {
			json = encodeObjectJSON(o, null);
		}
		
		cfg.formatter.format(out, json);
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
