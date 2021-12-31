package org.cakelab.json.format;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.cakelab.json.JSONArray;
import org.cakelab.json.JSONCompoundType;
import org.cakelab.json.JSONException;
import org.cakelab.json.JSONObject;

public abstract class JSONFormatterBase implements JSONFormatter {
	
	protected static final Comparator<Map.Entry<String, Object>> ENTRY_COMPARATOR = new Comparator<Map.Entry<String, Object>> (){
		@Override
		public int compare(Map.Entry<String, Object> o1, Map.Entry<String, Object> o2) {
			return String.CASE_INSENSITIVE_ORDER.compare(o1.getKey(), o2.getKey());
		}
	};
	
	protected final JSONFormatterConfiguration cfg;
	private final CharsetRanges encodingRanges;
	
	
	protected JSONFormatterBase(JSONFormatterConfiguration cfg) throws JSONException {
		this.cfg = new JSONFormatterConfiguration(cfg);
		encodingRanges = setupEncodingRange();
	}
	
	
	CharsetRanges setupEncodingRange() throws JSONException {
		if (!cfg.unicodeValues) 
			return CharsetRanges.FULL_RANGE;

		return CharsetRanges.get(cfg.charset);
	}

	
	@Override
	public JSONFormatterConfiguration getConfiguration() {
		return cfg;
	}

	@Override
	public String format(JSONObject jsonObject) throws JSONException {
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		format(bout, jsonObject);
		return toString(bout);
	}

	@Override
	public String format(JSONArray jsonArray) throws JSONException {
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		format(bout, jsonArray);
		return toString(bout);
	}
	
	@Override
	public String format(Object jsonValue) throws JSONException {
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		format(bout, jsonValue);
		return toString(bout);
	}
	
	@Override
	public String format(JSONCompoundType json) throws JSONException {
		if (json instanceof JSONArray) {
			return format((JSONArray)json);
		} else {
			return format((JSONObject)json);
		}
	}
	
	@Override
	public void format(OutputStream out, JSONCompoundType json) throws JSONException {
		if (json instanceof JSONArray) {
			format(out, (JSONArray)json);
		} else {
			format(out, (JSONObject)json);
		}
	}
	
	@Override
	public void format(OutputStream out, JSONObject jsonObject) throws JSONException {
		PrintStream pout = createPrintStream(out);
		append(pout, jsonObject);
		pout.flush();
	}

	@Override
	public void format(OutputStream out, JSONArray jsonArray) throws JSONException {
		PrintStream pout = createPrintStream(out);
		append(pout, jsonArray);
		pout.flush();
	}
	

	@Override
	public void format(OutputStream out, Object jsonValue) throws JSONException {
		PrintStream pout = createPrintStream(out);
		appendAny(pout, jsonValue);
		pout.flush();
	}

	
	protected abstract void append(PrintStream pout, JSONArray o) throws JSONException;
	protected abstract void append(PrintStream pout, JSONObject o) throws JSONException;
	
	protected void appendPrimitiveValue(PrintStream pout, Object primitiveValue) {
		pout.print(primitiveValue.toString());
	}

	protected void appendAny(PrintStream pout, Object anyJsonValue) throws JSONException {
		if (anyJsonValue == null) {
			pout.print("null");
		} else if (anyJsonValue instanceof String) {
			pout.print('\"');
			
			appendUnicodeString(pout, (String)anyJsonValue);
			
			pout.print('\"');
		} else if (anyJsonValue instanceof JSONArray) {
			append(pout, (JSONArray)anyJsonValue);
		} else if (anyJsonValue instanceof JSONObject) {
			append(pout, (JSONObject)anyJsonValue);
		} else {
			appendPrimitiveValue(pout, anyJsonValue);
		}
	}
	
	protected void appendNewLine(PrintStream pout) {
		pout.print("\n");
	}

	protected void appendUnicodeString(PrintStream pout, String str) throws JSONException {
		StringReader reader = new StringReader(str);
		int read;
		try {
			// read one character at a time
			while ((read = reader.read()) > 0) {
				appendUnicodeCharacter(pout, (char)read);
			}
		} catch (JSONException e) {
			throw e;
		} catch (IOException e) {
			throw new JSONException(e);
		}
	}

	protected void appendUnicodeCharacter(PrintStream pout, char c) throws JSONException {
		if (Character.isSupplementaryCodePoint(c)) 
			throw new JSONException("Supplimentary code points are not supported by JSON");
		switch (c) {
		case '\"':
			pout.print("\\\"");
			break;
		case '\\':
			pout.print("\\\\");
			break;
		case '\b':
			pout.print("\\b");
			break;
		case '\f':
			pout.print("\\f");
			break;
		case '\n':
			pout.print("\\n");
			break;
		case '\r':
			pout.print("\\r");
			break;
		case '\t':
			pout.print("\\t");
			break;
		default:
			if (exceedsEncodingRange(c)) {
				pout.print("\\u");
				// Unicode codepoints are similar to character 
				// values in UCS2 (JVM character encoding).
				String s = Integer.toHexString(c);
				// add missing leading zeros
				for (int i = s.length(); i < 4 ; i++) pout.print('0');
				pout.print(s);
			} else {
				pout.print(c);
			}
			break;
		}
	}

	protected boolean exceedsEncodingRange(char c) {
		return !encodingRanges.valid(c);
	}
	
	protected Iterator<Entry<String, Object>> iterator(Set<Entry<String, Object>> entrySet) {
		if (cfg.sortMembers) {
			ArrayList<Entry<String, Object>> entries = new ArrayList<>(entrySet);
			Collections.sort(entries, ENTRY_COMPARATOR);
			return entries.iterator();
		} else {
			return entrySet.iterator();
		}
	}

	private PrintStream createPrintStream(OutputStream out) throws JSONException {
		try {
			return new PrintStream(out, false, cfg.charset.name());
		} catch (UnsupportedEncodingException e) {
			throw new JSONException(e);
		}
	}

	private String toString(ByteArrayOutputStream bout) throws JSONException {
		try {
			bout.flush();
			return bout.toString(cfg.charset.name());
		} catch (IOException e) {
			throw new JSONException(e);
		}
	}


}
