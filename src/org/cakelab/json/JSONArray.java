package org.cakelab.json;

import java.util.ArrayList;

import org.cakelab.json.format.JSONFormatter;


public class JSONArray extends ArrayList<Object> implements JSONCompoundType {

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

	
	public JSONObject getObject(int index) {
		return objectvalue(get(index));
	}
	
	public JSONObject getObject(int index, JSONObject defaultValue) {
		return objectvalue(get(index), defaultValue);
	}

	public JSONArray getArray(int index) {
		return arrayvalue(get(index));
	}
	
	public JSONArray getArray(int index, JSONArray defaultValue) {
		return arrayvalue(get(index), defaultValue);
	}
	
	public String getString(int index) {
		return stringvalue(get(index));
	}
	
	public String getString(int index, String defaultValue) {
		return stringvalue(get(index), defaultValue);
	}
	
	public Double getDouble(int index) {
		return doublevalue(get(index));
	}

	public Double getDouble(int index, Double defaultValue) {
		return doublevalue(get(index), defaultValue);
	}

	public Long getLong(int index) {
		return longvalue(get(index));
	}
	
	public Long getLong(int index, Long defaultValue) {
		return longvalue(get(index), defaultValue);
	}
	
	public Boolean getBoolean(int index) {
		return booleanvalue(get(index));
	}

	public Boolean getBoolean(int index, Boolean defaultValue) {
		return booleanvalue(get(index), defaultValue);
	}

	
}
