package org.cakelab.json.parser.jni;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

import org.cakelab.json.JSONException;
import org.cakelab.json.JSONObject;
import org.cakelab.json.parser.Parser;

class NativeParser implements Parser {
	static {
		Library.load();
	}


	private boolean ignoreNull;

	public NativeParser(boolean ignoreNull) {
		this.ignoreNull = ignoreNull;
	}

	@Override
	public JSONObject parseObject(String jsonString) throws IOException, JSONException {
		return validJSONObject(parse(jsonString));
	}

	@Override
	public JSONObject parseObject(InputStream inputStream) throws IOException, JSONException {
		return parseObject(inputStream, Charset.defaultCharset());
	}

	@Override
	public JSONObject parseObject(InputStream inputStream, Charset charset) throws IOException, JSONException {
		return validJSONObject(parse(inputStream, charset));
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T parse(String jsonString) throws IOException, JSONException {
		return (T) _parse(jsonString);
	}


	@Override
	public <T> T parse(InputStream inputStream) throws IOException, JSONException {
		return parse(inputStream, Charset.defaultCharset());
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T parse(InputStream inputStream, Charset charset) throws IOException, JSONException {
		return (T) _parse(inputStream, charset);
	}

	private JSONObject validJSONObject(Object o) throws JSONException {
		if (o != null && !JSONObject.class.isAssignableFrom(o.getClass())) 
			throw new JSONException("given jsonString does not contain an object");
		return (JSONObject)o;
	}

	
	private native Object _parse(String jsonString);
	private native Object _parse(InputStream inputStream, Charset charset);

}
