package org.cakelab.json.parser;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

import org.cakelab.json.JSONException;
import org.cakelab.json.JSONObject;

/** Parser used by a JSONCodec to parse JSON strings. */
public interface Parser {
	
	/**
	 * Explicitly expects a json object to be parsed.
	 * @param jsonString Json string to be parsed.
	 * @return
	 * @throws IOException
	 * @throws JSONException
	 */
	JSONObject parseObject(String jsonString) throws IOException, JSONException;
	/**
	 * Explicitly expects a json object to be parsed.
	 * @param inputStream Stream with json input having default character encoding.
	 * @return parsed JSON object.
	 * @throws IOException
	 * @throws JSONException
	 */
	JSONObject parseObject(InputStream inputStream) throws IOException, JSONException;
	/**
	 * Explicitly expects a json object to be parsed.
	 * @param inputStream Stream with json input
	 * @param charset Character set used by stream.
	 * @return parsed JSON object.
	 * @throws IOException
	 * @throws JSONException
	 */
	JSONObject parseObject(InputStream inputStream, Charset charset) throws IOException, JSONException;

	/**
	 * Parses any legal json element.
	 * @param <T> One of JSONObject, JSONArray, Double, Boolean, String or simply Object.
	 * @param jsonString Json string to be parsed.
	 * @return parsed json element.
	 * @throws IOException
	 * @throws JSONException
	 */
	<T> T parse(String jsonString) throws IOException, JSONException;
	/**
	 * Parses any legal json element.
	 * @param <T> One of JSONObject, JSONArray, Double, Boolean, String or simply Object.
	 * @param inputStream Stream with json input having default character encoding.
	 * @return parsed json element.
	 * @throws IOException
	 * @throws JSONException
	 */
	<T> T parse(InputStream inputStream) throws IOException, JSONException;
	/**
	 * Parses any legal json element.
	 * @param <T> One of JSONObject, JSONArray, Double, Boolean, String or simply Object.
	 * @param inputStream Stream with json input
	 * @param charset Character set used by stream.
	 * @return parsed json element.
	 * @throws IOException
	 * @throws JSONException
	 */
	<T> T parse(InputStream inputStream, Charset charset) throws IOException, JSONException;


}
