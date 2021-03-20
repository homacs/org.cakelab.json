package org.cakelab.json.parser.pojo;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

import org.cakelab.json.JSONArray;
import org.cakelab.json.JSONException;
import org.cakelab.json.JSONObject;
import org.cakelab.json.parser.Parser;

/** Parser used by a JSONCodec to parse JSON strings. */
public class POJOParser implements Parser {
	
	private Scanner scanner;
	private boolean ignoreNull;
	
	public POJOParser(boolean ignoreNull) {
		this.ignoreNull = ignoreNull;
	}

	private void setup(String jsonString) throws IOException {
		scanner = new Scanner(jsonString);
	}
	
	private void setup(InputStream in, Charset charset) throws IOException {
		scanner = new Scanner(in, charset);
	}

	public JSONObject parseObject(String jsonString) throws IOException, JSONException {
		setup(jsonString);
		return parseJSONObject();
	}

	public JSONObject parseObject(InputStream in, Charset charset) throws IOException, JSONException {
		setup(in, charset);
		return parseJSONObject();
	}

	public JSONObject parseObject(InputStream in) throws IOException, JSONException {
		return parseObject(in, Charset.defaultCharset());
	}


	@SuppressWarnings("unchecked")
	@Override
	public <T> T parse(String jsonString) throws IOException, JSONException {
		setup(jsonString);
		return (T)parseValue();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <T> T parse(InputStream in, Charset charset) throws IOException, JSONException {
		setup(in, charset);
		return (T)parseValue();
	}

	@Override
	public <T> T parse(InputStream in) throws IOException, JSONException {
		return parse(in, Charset.defaultCharset());
	}


	private JSONObject parseJSONObject() throws IOException, JSONException {
		JSONObject o = new JSONObject();
		
		scanCharToken(Token.TYPE_LEFTBRACE);

		parseNVSequence(o, Token.TYPE_RIGHTBRACE);
		
		scanCharToken(Token.TYPE_RIGHTBRACE);
		
		return o;
	}

	private void parseNVSequence(JSONObject o, int endToken) throws IOException, JSONException {
		char lookahead = scanner.getLookahead();
		if (lookahead == endToken) return;
		while (true) {
			parseNameValuePair(o);
			lookahead = scanner.getLookahead();
			if (lookahead == endToken) break;
			
			scanCharToken(Token.TYPE_COMMA);
		}
		
	}

	private void parseValueSequence(JSONArray o, int endToken) throws IOException, JSONException {
		char lookahead = scanner.getLookahead();
		if (lookahead == endToken) return;
		while (true) {
			Object value = parseValue();
			if (!(ignoreNull && value == null)) o.add(value);
			lookahead = scanner.getLookahead();
			if (lookahead == endToken) break;
			
			scanCharToken(Token.TYPE_COMMA);
		}
		
	}

	private void parseNameValuePair(JSONObject parent) throws IOException, JSONException {
		if (scanner.nextName() != Token.TYPE_NAME) error("expected a name");
		String name = scanner.getName();
		
		scanCharToken(Token.TYPE_COLON);

		
		Object value = parseValue();
		if (ignoreNull && value == null) {
			return;
		}
		
		
		parent.put(name, value);
	}

	private Object parseValue() throws IOException, JSONException {
		Object value = null;
		char lookahead = scanner.getLookahead();
		switch(lookahead) {
		case Token.TYPE_LEFTBRACE:
			value = parseJSONObject();
			break;
		case Token.TYPE_LEFTBRACKET:
			value = parseArray();
			break;
		default:
			int type = scanner.nextValue();
			if (type == Token.TYPE_UNKNOWN) {
				error("expected a value");
			}
			value = scanner.getValue();
		}
		return value;
	}

	private JSONArray parseArray() throws IOException, JSONException {
		JSONArray a = new JSONArray();
		scanCharToken(Token.TYPE_LEFTBRACKET);
		if (scanner.getLookahead() != Token.TYPE_RIGHTBRACKET) {
			/* non-empty array */
			parseValueSequence(a, Token.TYPE_RIGHTBRACKET);
		}
		scanCharToken(Token.TYPE_RIGHTBRACKET);
		return a;
	}

	private void scanCharToken(int tokenCharacter) throws IOException, JSONException {
		if (scanner.next() != (char)tokenCharacter) error("expected token '"+  (char)tokenCharacter + "'");
		
	}

	private void error(String string) throws JSONException {
		throw new JSONException(":" + scanner.getLine() + ":" + scanner.getColumn() + ": " + string);
	}

}
