package org.cakelab.json;

import java.util.HashMap;

import org.cakelab.json.format.JSONFormatter;

public class JSONObject extends HashMap<String, Object> implements JSONCompoundType {
	private static final long serialVersionUID = 1L;

	@Override
	public Object put(String name, Object value) {
		return super.put(name, value);
	}
	
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

	public double getDouble(String key) {
		Object o = get(key);
		return doublevalue(o);
	}

	public long getLong(String key) {
		Object o = get(key);
		return longvalue(o);
	}
	
	public String getString(String key) {
		return (String)get(key);
	}

}
