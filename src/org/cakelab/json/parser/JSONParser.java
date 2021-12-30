package org.cakelab.json.parser;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

import org.cakelab.json.JSONException;
import org.cakelab.json.JSONObject;

/** A JSON Parser turns JSON strings into a JSON object tree. 
 * 
 * Can be created through {@link JSONParserFactory} */
public interface JSONParser {
	
	/**
	 * Explicitly expects a json object to be parsed.
	 * @param jsonString Json string to be parsed.
	 * @return
	 * @throws IOException
	 * @throws JSONException
	 */
	JSONObject parseObject(String jsonString) throws JSONException;
	/**
	 * Explicitly expects a json object to be parsed.
	 * @param inputStream Stream with json input having default character encoding.
	 * @return parsed JSON object.
	 * @throws IOException
	 * @throws JSONException
	 */
	JSONObject parseObject(InputStream inputStream) throws JSONException;
	/**
	 * Explicitly expects a json object to be parsed.
	 * @param inputStream Stream with json input
	 * @param charset Character set used by stream.
	 * @return parsed JSON object.
	 * @throws IOException
	 * @throws JSONException
	 */
	JSONObject parseObject(InputStream inputStream, Charset charset) throws JSONException;

	/**
	 * Parses any legal json element.
	 * @param <T> One of JSONObject, JSONArray, Double, Boolean, String or simply Object.
	 * @param jsonString Json string to be parsed.
	 * @return parsed json element.
	 * @throws IOException
	 * @throws JSONException
	 */
	<T> T parse(String jsonString) throws JSONException;
	/**
	 * Parses any legal json element.
	 * @param <T> One of JSONObject, JSONArray, Double, Boolean, String or simply Object.
	 * @param inputStream Stream with json input having default character encoding.
	 * @return parsed json element.
	 * @throws IOException
	 * @throws JSONException
	 */
	<T> T parse(InputStream inputStream) throws JSONException;
	/**
	 * Parses any legal json element.
	 * @param <T> One of JSONObject, JSONArray, Double, Boolean, String or simply Object.
	 * @param inputStream Stream with json input
	 * @param charset Character set used by stream.
	 * @return parsed json element.
	 * @throws IOException
	 * @throws JSONException
	 */
	<T> T parse(InputStream inputStream, Charset charset) throws JSONException;


}
