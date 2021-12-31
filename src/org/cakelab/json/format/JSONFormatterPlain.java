package org.cakelab.json.format;

import java.io.PrintStream;
import java.util.Iterator;
import java.util.Map.Entry;

import org.cakelab.json.JSONArray;
import org.cakelab.json.JSONException;
import org.cakelab.json.JSONObject;

public class JSONFormatterPlain extends JSONFormatterBase implements JSONFormatter {

	
	public JSONFormatterPlain(JSONFormatterConfiguration cfg) throws JSONException {
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
			pout.print('\"');
			pout.print(e.getKey());
			pout.print("\":");
			appendAny(pout,e.getValue());
			while (it.hasNext()) {
				e = it.next();
				pout.print(",");
				pout.print('\"');
				pout.print(e.getKey());
				pout.print("\":");
				appendAny(pout,e.getValue());
			}
		}
		pout.print("}");	
	}

}
