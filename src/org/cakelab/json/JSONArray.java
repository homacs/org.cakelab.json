package org.cakelab.json;

import java.util.ArrayList;


public class JSONArray extends ArrayList<Object>{

	private static final long serialVersionUID = 1L;

	@Override
	public String toString() {
		return toString(new JSONPrettyprint(true));
	}
	public String toString(JSONPrettyprint sb) {
			
		sb.append("[");
		if (!isEmpty()) {
			sb.appendNewLine();
			sb.indentInc();
			int i = 0;
			sb.appendIndent();
			JSONObject.appendValue(sb, get(i));
			for (i = 1; i < size(); i++) {
				sb.append(", ");
				sb.appendNewLine();
				sb.appendIndent();
				JSONObject.appendValue(sb, get(i));
			}
			sb.appendNewLine();
			sb.indentDec();
			sb.appendIndent();
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
