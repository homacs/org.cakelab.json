package org.cakelab.json.format;

import java.io.OutputStream;

import org.cakelab.json.JSONArray;
import org.cakelab.json.JSONCompoundType;
import org.cakelab.json.JSONException;
import org.cakelab.json.JSONObject;

/**
 * An object implementing this interface is used during conversion of a JSONObject instance into a String.
 * The implementing string formatter can format the string output such as to add indenting.
 * 
 * @author homac
 *
 */
public interface JSONFormatter {

	
	public JSONFormatterConfiguration getConfiguration();
	
	String format(JSONObject jsonObject) throws JSONException;
	String format(JSONArray jsonArray) throws JSONException;
	String format(JSONCompoundType jsonCompound) throws JSONException;
	String format(Object jsonValue) throws JSONException;
	
	void format(OutputStream out, JSONObject jsonObject) throws JSONException;
	void format(OutputStream out, JSONArray jsonArray) throws JSONException;
	void format(OutputStream out, JSONCompoundType jsonCompound) throws JSONException;
	void format(OutputStream out, Object jsonValue) throws JSONException;
	
}
