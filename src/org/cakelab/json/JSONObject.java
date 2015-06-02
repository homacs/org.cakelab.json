package org.cakelab.json;

import java.io.StringReader;
import java.util.HashMap;
import java.util.Iterator;

public class JSONObject extends HashMap<String, Object>{
	private static final long serialVersionUID = 1L;

	public static void appendValue(StringBuffer sb, Object o) {
		if (o == null) {
			sb.append("null");
		} else if (o instanceof String) {
			sb.append('\"');
			StringReader reader = new StringReader(o.toString());
			int read;
			try {
				while ((read = reader.read()) > 0) {
					if (Character.isSupplementaryCodePoint(read)) throw new Error("Supplimentary code points (extended unicode) are not supported by JSON");
					appendCharacter(sb, (char)read);
				}
			} catch (Throwable e) {
				throw new Error(e);
			}
			sb.append('\"');
		} else {
			sb.append(o);
		}
	}
	
	private static void appendCharacter(StringBuffer sb, char c) {
		switch (c) {
		case '\"':
			sb.append("\\\"");
			break;
		case '\\':
			sb.append("\\\\");
			break;
		case '\b':
			sb.append("\\b");
			break;
		case '\f':
			sb.append("\\f");
			break;
		case '\n':
			sb.append("\\n");
			break;
		case '\r':
			sb.append("\\r");
			break;
		case '\t':
			sb.append("\\t");
			break;
		default:
			if (isNonAscii(c)) {
				sb.append("\\u");
				String s = Integer.toHexString(c);
				// add missing leading zeros
				for (int i = s.length(); i < 4 ; i++) sb.append('0');
				sb.append(s);
			} else {
				sb.append(c);
			}
			break;
		}
	}

	private static boolean isNonAscii(char c) {
		return c > 127;
	}

	@Override
	public String toString() {
		StringBuffer s = new StringBuffer("{");
		Iterator<java.util.Map.Entry<String, Object>> it = entrySet().iterator();
		if (it.hasNext()) {
			java.util.Map.Entry<String, Object> e = it.next();
			s.append('\"');
			s.append(e.getKey());
			s.append("\": ");
			appendValue(s, e.getValue());
			while (it.hasNext()) {
				e = it.next();
				s.append(", ");
				s.append('\"');
				s.append(e.getKey());
				s.append("\": ");
				appendValue(s, e.getValue());
			}
		}
		s.append("}");
		return s.toString();
	}

	public double getDouble(String key) {
		Object o = get(key);
		return doublevalue(o);
	}

	public long getLong(String key) {
		Object o = get(key);
		return longvalue(o);
	}
	
	static double doublevalue(Object o) {
		if (o instanceof Long) {
			return (Long)o;
		} else {
			return (Double)o;
		}
	}

	static long longvalue(Object o) {
		if (o instanceof Long) {
			return (Long)o;
		} else {
			return ((Double)o).longValue();
		}

	}

}
