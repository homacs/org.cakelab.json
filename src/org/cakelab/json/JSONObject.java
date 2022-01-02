package org.cakelab.json;

import java.util.HashMap;

import org.cakelab.json.format.JSONFormatter;

public class JSONObject extends HashMap<String, Object> implements JSONCompoundType {
	private static final long serialVersionUID = 1L;

	@Override
	public String toString() {
		try {
			return toString(JSONDefaults.FORMATTER);
		} catch (JSONException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public String toString(JSONFormatter formatter) throws JSONException {
		return formatter.format(this);
	}

	public JSONObject getObject(String key) {
		return objectvalue(get(key));
	}

	public JSONObject getObject(String key, JSONObject defaultValue) {
		return objectvalue(get(key), defaultValue);
	}

	public JSONArray getArray(String key) {
		return arrayvalue(get(key));
	}
	
	public JSONArray getArray(String key, JSONArray defaultValue) {
		return arrayvalue(get(key), defaultValue);
	}
	
	public String getString(String key) {
		return stringvalue(get(key));
	}
	
	public String getString(String key, String defaultValue) {
		return stringvalue(get(key), defaultValue);
	}
	
	public Double getDouble(String key) {
		return doublevalue(get(key));
	}

	public Double getDouble(String key, Double defaultValue) {
		return doublevalue(get(key), defaultValue);
	}

	public Long getLong(String key) {
		return longvalue(get(key));
	}

	public Long getLong(String key, Long defaultValue) {
		return longvalue(get(key), defaultValue);
	}

	public Boolean getBoolean(String key) {
		return booleanvalue(get(key));
	}
	
	public Boolean getBoolean(String key, Boolean defaultValue) {
		return booleanvalue(get(key), defaultValue);
	}
}	
