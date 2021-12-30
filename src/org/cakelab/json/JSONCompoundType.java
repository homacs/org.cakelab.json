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

	default double doublevalue(Object o) {
		if (o instanceof Long) {
			return (Long)o;
		} else {
			return (Double)o;
		}
	}

	default long longvalue(Object o) {
		if (o instanceof Long) {
			return (Long)o;
		} else {
			return ((Double)o).longValue();
		}

	}

}
