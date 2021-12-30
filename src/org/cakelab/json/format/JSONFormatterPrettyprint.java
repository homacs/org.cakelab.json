package org.cakelab.json.format;

import java.io.PrintStream;
import java.util.Iterator;
import java.util.Map.Entry;

import org.cakelab.json.JSONArray;
import org.cakelab.json.JSONObject;

public class JSONFormatterPrettyprint extends JSONFormatterBase implements JSONFormatter {

	public static final int NON_UNICODE_VALUES = 1<<0;
	public static final int SORT_MEMBERS = 1<<1;

	private int indent;
	private String indentStr = "  ";
	private String currentIndentStr = "";

	
	public JSONFormatterPrettyprint(JSONFormatterConfiguration format) {
		super(format);
	}

	protected void enterScope() {
		indent++;
		currentIndentStr += indentStr;
	}
	
	protected void leaveScope() {
		indent--;
		currentIndentStr = "";
		for (int i = 0; i < indent; i++) {
			currentIndentStr += indentStr;
		}
	}
	
	protected void appendIndent(PrintStream pout) {
		pout.print(currentIndentStr);
	}

	@Override
	protected void append(PrintStream pout, JSONArray jsonArray) {
		pout.print("[");
		if (!jsonArray.isEmpty()) {
			appendNewLine(pout);
			enterScope();
			int i = 0;
			appendIndent(pout);
			appendAny(pout, jsonArray.get(i));
			for (i = 1; i < jsonArray.size(); i++) {
				pout.print(", ");
				appendNewLine(pout);
				appendIndent(pout);
				appendAny(pout, jsonArray.get(i));
			}
			appendNewLine(pout);
			leaveScope();
			appendIndent(pout);
		}
		pout.print("]");	}

	@Override
	protected void append(PrintStream pout, JSONObject jsonObject) {
		pout.print("{");
		enterScope();
		appendNewLine(pout);
		
		Iterator<Entry<String, Object>> it = iterator(jsonObject.entrySet());
		
		if (it.hasNext()) {
			appendIndent(pout);
			Entry<String, Object> e = it.next();
			pout.print('\"');
			pout.print(e.getKey());
			pout.print("\": ");
			appendAny(pout, e.getValue());
			while (it.hasNext()) {
				e = it.next();
				pout.print(", ");
				appendNewLine(pout);
				appendIndent(pout);
				pout.print('\"');
				pout.print(e.getKey());
				pout.print("\": ");
				appendAny(pout, e.getValue());
			}
			appendNewLine(pout);
		}
		leaveScope();
		appendIndent(pout);
		pout.print("}");
	}

	

}
