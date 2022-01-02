package org.cakelab.json;

import org.cakelab.json.format.JSONFormatter;


/**
 * Common interface of JSONObject and JSONArray
 * 
 * @author homac
 *
 */
public interface JSONCompoundType {
	/**
	 * Convert to string using the default string formatter.
	 * @see JSONFormatter
	 * @return
	 */
	String toString();
	String toString(JSONFormatter formatter) throws JSONException;

	
	@SuppressWarnings("unchecked")
	default <T> T defaultvalue(Object o, T defaultValue) {
		return o != null ? (T)o : defaultValue;
	}
	
	default Double doublevalue(Object o, Double defaultValue) {
		if (o == null) return defaultValue;
		if (o instanceof Long) {
			return ((Long)o).doubleValue();
		} else {
			return (Double)o;
		}
	}
	
	default Double doublevalue(Object o) {
		return doublevalue(o, null);
	}
	
	default Long longvalue(Object o, Long defaultValue) {
		if (o == null) return defaultValue;
		if (o instanceof Long) {
			return (Long)o;
		} else {
			return ((Double)o).longValue();
		}
	}
	
	default Long longvalue(Object o) {
		return longvalue(o, null);
	}
	
	default Boolean booleanvalue(Object o, Boolean defaultValue) {
		return defaultvalue(o, defaultValue);
	}
	
	default Boolean booleanvalue(Object o) {
		return (Boolean)o;
	}
	
	default String stringvalue(Object o) {
		return (String)o;
	}
	
	default String stringvalue(Object o, String defaultValue) {
		return defaultvalue(o, defaultValue);
	}

	default JSONArray arrayvalue(Object o, JSONArray defaultValue) {
		return defaultvalue(o, defaultValue);
	}

	default JSONArray arrayvalue(Object o) {
		return (JSONArray)o;
	}

	default JSONObject objectvalue(Object o) {
		return (JSONObject)o;
	}
	
	default JSONObject objectvalue(Object o, JSONObject defaultValue) {
		return defaultvalue(o, defaultValue);
	}
}
