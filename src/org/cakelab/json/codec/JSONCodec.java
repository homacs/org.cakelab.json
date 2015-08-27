package org.cakelab.json.codec;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.charset.Charset;
import java.util.Map.Entry;

import org.cakelab.json.JSONArray;
import org.cakelab.json.JSONObject;
import org.cakelab.json.JSONException;
import org.cakelab.json.Parser;

public class JSONCodec {
	
	private UnsafeAllocator allocator = UnsafeAllocator.create();
	private boolean ignoreNull;
	
	
	public JSONCodec(boolean ignoreNull) {
		this.ignoreNull = ignoreNull;
	}
	
	
	
	/** decodes the given json string into the given target object. 
	 * @throws JSONCodecException 
	 */
	public Object decodeObject(String jsonString, Object target) throws JSONCodecException {

		try {
			Parser parser = new Parser(jsonString);
			JSONObject json = parser.parse();
			
			return _decodeObject(json, target);
		} catch (JSONCodecException e) {
			throw e;
		} catch (Exception e) {
			throw new JSONCodecException(e);
		}
	}
	
	/** decodes the given json string from input stream into the given target object. 
	 * @throws JSONCodecException 
	 */
	public Object decodeObject(InputStream inputStream, Object target) throws JSONCodecException {

		try {
			Parser parser = new Parser(inputStream);
			JSONObject json = parser.parse();
			
			return _decodeObject(json, target);
		} catch (JSONCodecException e) {
			throw e;
		} catch (Exception e) {
			throw new JSONCodecException(e);
		}
	}

	
	/** decodes the given json string from input stream into an object of the given target type. 
	 * @throws JSONCodecException 
	 */
	public Object decodeObject(InputStream inputStream,
			Class<?> target)  throws JSONCodecException {
		try {
			Parser parser = new Parser(inputStream);
			JSONObject json = parser.parse();
			
			return _decodeObject(json, target);
		} catch (JSONCodecException e) {
			throw e;
		} catch (Exception e) {
			throw new JSONCodecException(e);
		}
	}


	
	/** decodes the given json string into an object of the given target type. 
	 * @throws JSONCodecException 
	 */
	public Object decodeObject(String jsonString, Class<?> type) throws JSONCodecException {

		try {
			Parser parser = new Parser(jsonString, ignoreNull);
			JSONObject json = parser.parse();
			
			return _decodeObject(json, type);
		} catch (JSONCodecException e) {
			throw e;
		} catch (Exception e) {
			throw new JSONCodecException(e);
		}
	}
	
	
	
	
	private Object _decodeObject(Object json, Object target) throws JSONCodecException {

		if (json instanceof JSONObject) {
			return json2object((JSONObject) json, target);
		} else if (json instanceof JSONArray) {
			return json2array((JSONArray) json, target);
		} else {
			return json2primitive(json.toString(), target.getClass());
		}
		
	}

	private Object _decodeObject(Object json, Class<?> type) throws JSONCodecException, InstantiationException {
		if (json == null) return null;
		if (json instanceof JSONObject) {
			return json2object((JSONObject) json, allocator.newInstance(type));
		} else if (json instanceof JSONArray) {
			return json2array((JSONArray) json, Array.newInstance(type.getComponentType(), ((JSONArray) json).size()));
		} else {
			return json2primitive(json.toString(), type);
		}
		
	}


	private Object json2object(JSONObject json, Object target) throws JSONCodecException {
		Class<? extends Object> type = target.getClass();
		try {
			for (Entry<String, Object> e : json.entrySet()) {
					Field field = ReflectionHelper.getDeclaredField(type, e.getKey());
					boolean accessible = field.isAccessible();
					field.setAccessible(true);
					if (ReflectionHelper.isPrimitive(field.getType())) {
						field.set(target, json2primitive(e.getValue().toString(), field.getType()));
					} else {
						field.set(target, _decodeObject(e.getValue(), field.getType()));
					}
					field.setAccessible(accessible);
			}
		} catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException | InstantiationException e) {
			throw new JSONCodecException(e);
		}
		
		return target;
	}

	private Object json2array(JSONArray json, Object target) throws JSONCodecException {
		try {
			for (int i = 0; i < json.size(); i++) {
				Object jsonValue = json.get(i);
				Object value;
					value = _decodeObject(jsonValue, target.getClass().getComponentType());
				Array.set(target, i, value);
			}
		} catch (InstantiationException e) {
			throw new JSONCodecException(e);
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


	public String encodeObject(Object o) throws JSONCodecException {
		Object json;
		
		if (o == null) {
			throw new JSONCodecException("Cannot encode a toplevel null object");
		}
		
		try {
			json = _encodeObject(o);
			return json.toString();
		} catch (Exception e) {
			throw new JSONCodecException(e);
		}
	}

	public void encodeObject(Object o, OutputStream out) throws JSONCodecException {
		Object json;
		
		if (o == null) {
			throw new JSONCodecException("Cannot encode a toplevel null object");
		}
		
		try {
			json = _encodeObject(o);
			byte[] bytes = json.toString().getBytes();
			out.write(bytes);
		} catch (Exception e) {
			throw new JSONCodecException(e);
		}
	}

	/** returns a JSONObject or JSONArray depending on the given 
	 * object to be encoded */
	private Object _encodeObject(Object o) throws JSONCodecException {
		try {
		
			if (ReflectionHelper.isPrimitive(o.getClass())) {
				return primitive2json(o);
			} else if (o.getClass().isArray()) {
				return array2json(o);
			} else {
				return object2json(o);
			}
		} catch (JSONCodecException e) {
			throw e;
		} catch (Exception e) {
			throw new JSONCodecException(e);
		}
	}

	private Object primitive2json(Object o) {
		return o;
	}

	private JSONObject object2json(Object o) throws JSONCodecException, IllegalArgumentException, IllegalAccessException {
		JSONObject json = new JSONObject();
		Class<?> type = o.getClass();
		for (Field field : ReflectionHelper.getDeclaredFields(type)) {
			
			// ignore transient fields
			if (isIgnoredField(field)) continue;
			boolean accessible = field.isAccessible();
			field.setAccessible(true);
			Object value = field.get(o);
			if (value == null) {
				if (!ignoreNull) json.put(field.getName(), null);
			} else {
				json.put(field.getName(), _encodeObject(value));
			}
			field.setAccessible(accessible);
		}
		return json;
	}

	private boolean isIgnoredField(Field field) {
		int mod = field.getModifiers();

		return Modifier.isTransient(mod) || Modifier.isStatic(mod) || Modifier.isNative(mod);
	}



	private JSONArray array2json(Object o) throws ArrayIndexOutOfBoundsException, IllegalArgumentException, IOException, JSONException, JSONCodecException {
		JSONArray json = new JSONArray();
		for (int i = 0; i < Array.getLength(o); i++) {
			Object value = Array.get(o, i);
			if (value == null) {
				if (!ignoreNull) json.add(null);
			} else {
				json.add(_encodeObject(value));
			}
		}
		return json;
	}






}
