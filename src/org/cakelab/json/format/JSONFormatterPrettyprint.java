package org.cakelab.json.format;

import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.Map.Entry;

import org.cakelab.json.JSONArray;
import org.cakelab.json.JSONException;
import org.cakelab.json.JSONObject;

/** Formatter which creates pretty printed string output */
public class JSONFormatterPrettyprint extends JSONFormatterBase<JSONFormatterPrettyprint.IndentingPrintStream> implements JSONFormatter {

	static class IndentingPrintStream extends PrintStream
	{
		private int indent;
		private String indentStr = "  ";
		private String currentIndentStr = "";
		
		private IndentingPrintStream(OutputStream out, boolean autoflush, String charset) throws UnsupportedEncodingException {
			super(out, autoflush, charset);
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
		
		protected void printIndent() {
			print(currentIndentStr);
		}
	}
	
	public JSONFormatterPrettyprint(JSONFormatterConfiguration format) throws JSONException {
		super(format);
	}


	@Override
	protected void append(IndentingPrintStream pout, JSONArray jsonArray) throws JSONException {
		pout.print("[");
		if (!jsonArray.isEmpty()) {
			pout.println();
			pout.enterScope();
			int i = 0;
			pout.printIndent();
			appendAny(pout, jsonArray.get(i));
			for (i = 1; i < jsonArray.size(); i++) {
				pout.print(", ");
				pout.println();
				pout.printIndent();
				appendAny(pout, jsonArray.get(i));
			}
			pout.println();
			pout.leaveScope();
			pout.printIndent();
		}
		pout.print("]");	
	}

	@Override
	protected void append(IndentingPrintStream pout, JSONObject jsonObject) throws JSONException {
		pout.print("{");
		pout.enterScope();
		
		Iterator<Entry<String, Object>> it = iterator(jsonObject.entrySet());
		if (it.hasNext()) {
			pout.println();
			
			Entry<String, Object> e = it.next();
			pout.printIndent();
			appendNameValue(pout, e);
			while (it.hasNext()) {
				e = it.next();
				pout.print(", ");
				pout.println();
				pout.printIndent();
				appendNameValue(pout, e);
			}
			
			pout.println();
			pout.printIndent();
		}
		
		pout.leaveScope();
		pout.print("}");
	}


	private void appendNameValue(IndentingPrintStream pout, Entry<String, Object> e) throws JSONException {
		pout.print('\"');
		pout.print(e.getKey());
		pout.print("\": ");
		appendAny(pout, e.getValue());
	}



	@Override
	protected IndentingPrintStream setupPrintStream(OutputStream out, boolean autoflush, String charset)
			throws UnsupportedEncodingException {
		return new IndentingPrintStream(out, autoflush, charset);
	}


}
