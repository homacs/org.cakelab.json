package org.cakelab.json;

import java.util.ArrayList;

import org.cakelab.json.format.JSONFormatter;


public class JSONArray extends ArrayList<Object> implements JSONCompoundType {

	private static final long serialVersionUID = 1L;

	
	@Override
	public boolean add(Object o) {
		return super.add(o);
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

	public double getDouble(int index) {
		return doublevalue(get(index));
	}

	public long getLong(int index) {
		return longvalue(get(index));
	}

	
}
