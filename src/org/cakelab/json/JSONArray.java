package org.cakelab.json;

import java.util.ArrayList;

public class JSONArray extends ArrayList<Object>{

	private static final long serialVersionUID = 1L;

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer("[");
		if (!isEmpty()) {
			int i = 0;
			JSONObject.appendValue(sb, get(i));
			for (i = 1; i < size(); i++) {
				sb.append(", ");
				JSONObject.appendValue(sb, get(i));
			}
		}
		sb.append("]");
		return sb.toString();
	}

	public double getDouble(int index) {
		return JSONObject.doublevalue(get(index));
	}

	public long getLong(int index) {
		return JSONObject.longvalue(get(index));
	}
	
}
