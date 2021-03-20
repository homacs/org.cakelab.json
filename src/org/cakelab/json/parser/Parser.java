package org.cakelab.json.parser;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

import org.cakelab.json.JSONException;
import org.cakelab.json.JSONObject;

/** Parser used by a JSONCodec to parse JSON strings. */
public interface Parser {
	

	public JSONObject parse(String jsonString) throws IOException, JSONException;

	public JSONObject parse(InputStream inputStream) throws IOException, JSONException;

	public JSONObject parse(InputStream inputStream, Charset charset) throws IOException, JSONException;


}
