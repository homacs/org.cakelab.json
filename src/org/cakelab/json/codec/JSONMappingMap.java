package org.cakelab.json.codec;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.cakelab.json.JSONException;

public class JSONMappingMap {
	private Map<Class<?>, Map<Class<?>,JSONMapping<?,?>>> entries = new HashMap<>();

	
	public JSONMappingMap() {
	}

	public JSONMappingMap(JSONMappingMap knownMappings) {
		for (Entry<Class<?>, Map<Class<?>, JSONMapping<?, ?>>> e : knownMappings.entries.entrySet()) {
			Class<?> javaType = e.getKey();
			Map<Class<?>, JSONMapping<?, ?>> mappings = e.getValue();
			entries.put(javaType, new HashMap<>(mappings));
		}
	}
	
	
	public JSONMappingMap(JSONMappingMap knownMappings, JSONMapping<?,?> mapping) {
		this(knownMappings);
		Map<Class<?>, JSONMapping<?, ?>> map = entries.get(mapping.javaType);
		if (map == null) {
			map = new HashMap<>();
			entries.put(mapping.javaType, map);
		}
		map.put(mapping.jsonType, mapping);
	}
	

	public JSONMappingMap add(JSONMapping<?,?> mapping) {
		return new JSONMappingMap(this, mapping);
	}
	
	@SuppressWarnings("unchecked")
	public <JAVAT, JSONT> JSONMapping<JAVAT, JSONT> get(Class<JAVAT> javaType) {
		Map<Class<?>, JSONMapping<?, ?>> jsonMap = entries.get(javaType);
		if (jsonMap == null) 
			return null;
		return (JSONMapping<JAVAT, JSONT>) jsonMap.values().iterator().next();

	}
	
	@SuppressWarnings("unchecked")
	public <JAVAT, JSONT> JSONMapping<JAVAT, JSONT> get(Class<JAVAT> javaType, Class<JSONT> jsonType) {
		Map<Class<?>, JSONMapping<?, ?>> jsonMap = entries.get(javaType);
		if (jsonMap == null) 
			return null;
		return (JSONMapping<JAVAT, JSONT>) jsonMap.get(jsonType);
	}

	public <T> T toJava(JSONModeller modeller, Object jsonAny, Class<T> javaType) throws JSONException {
		@SuppressWarnings("unchecked")
		JSONMapping<T, Object> mapping =  (JSONMapping<T, Object>) get(javaType, jsonAny.getClass());
		if (mapping != null) {
			return mapping.toJava(modeller, jsonAny);
		}
		return null;
	}

	public <T> T toJava(JSONModeller modeller, Object jsonAny, T targetObject) throws JSONException {
		@SuppressWarnings("unchecked")
		JSONMapping<T, Object> mapping =  (JSONMapping<T, Object>) get(targetObject.getClass(), jsonAny.getClass());
		if (mapping != null) {
			mapping.toJava(modeller, jsonAny, targetObject);
			return targetObject;
		}
		return null;
	}

	public <T> Object toJson(JSONModeller modeller, T javaValue) throws JSONException {
		@SuppressWarnings("unchecked")
		JSONMapping<T, Object> mapping =  (JSONMapping<T, Object>) get(javaValue.getClass());
		if (mapping != null) {
			return mapping.toJson(modeller, javaValue);
		}
		return null;
	}


}
