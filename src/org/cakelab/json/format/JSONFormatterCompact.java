package org.cakelab.json.format;

import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.Map.Entry;

import org.cakelab.json.JSONArray;
import org.cakelab.json.JSONException;
import org.cakelab.json.JSONObject;

/** Formatter which outputs the most compact string representation */
public class JSONFormatterCompact extends JSONFormatterBase<PrintStream> implements JSONFormatter {

	
	public JSONFormatterCompact(JSONFormatterConfiguration cfg) throws JSONException {
		super(cfg);
	}


	@Override
	protected void append(PrintStream pout, JSONArray jsonArray) throws JSONException {
		pout.print("[");
		if (!jsonArray.isEmpty()) {
			int i = 0;
			appendAny(pout,jsonArray.get(i));
			for (i = 1; i < jsonArray.size(); i++) {
				pout.print(",");
				appendAny(pout,jsonArray.get(i));
			}
		}
		pout.print("]");
	}

	@Override
	protected void append(PrintStream pout, JSONObject jsonObject) throws JSONException {
		pout.print("{");
		
		Iterator<Entry<String, Object>> it = iterator(jsonObject.entrySet());
		
		if (it.hasNext()) {
			Entry<String, Object> e = it.next();
			appendNameValue(pout, e);
			while (it.hasNext()) {
				e = it.next();
				pout.print(",");
				appendNameValue(pout, e);
			}
		}
		pout.print("}");	
	}



	private void appendNameValue(PrintStream pout, Entry<String, Object> e) throws JSONException {
		pout.print('\"');
		pout.print(e.getKey());
		pout.print("\":");
		appendAny(pout, e.getValue());
	}


	@Override
	protected PrintStream setupPrintStream(OutputStream out, boolean autoflush, String charset)
			throws UnsupportedEncodingException {
		return new PrintStream(out, autoflush, charset);
	}

}
