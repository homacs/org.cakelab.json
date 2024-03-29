package org.cakelab.json.parser.basic;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

import org.cakelab.json.JSONArray;
import org.cakelab.json.JSONDefaults;
import org.cakelab.json.JSONException;
import org.cakelab.json.JSONObject;
import org.cakelab.json.parser.JSONParser;

/** Default JSONParser. 
 * 
 * <h3>Multi-Threading</h3>
 * Not thread-safe.*/
public class DefaultParser implements JSONParser {
	
	private Scanner scanner;
	private boolean ignoreNull;
	
	public DefaultParser(boolean ignoreNull) {
		this.ignoreNull = ignoreNull;
	}

	public JSONObject parseObject(String jsonString) throws JSONException {
		try {
			setup(jsonString);
			return parseJSONObject();
		} catch (IOException e) {
			throw new JSONException(e);
		} catch (JSONException e) {
			throw e;
		}
	}

	public JSONObject parseObject(InputStream in, Charset charset) throws JSONException {
		try {
			setup(in, charset);
			return parseJSONObject();
		} catch (IOException e) {
			throw new JSONException(e);
		} catch (JSONException e) {
			throw e;
		}
	}

	public JSONObject parseObject(InputStream in) throws JSONException {
		return parseObject(in, JSONDefaults.CHARSET);
	}


	@SuppressWarnings("unchecked")
	@Override
	public <T> T parse(String jsonString) throws JSONException {
		try {
			setup(jsonString);
			return (T)parseValue();
		} catch (IOException e) {
			throw new JSONException(e);
		} catch (JSONException e) {
			throw e;
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <T> T parse(InputStream in, Charset charset) throws JSONException {
		try {
			setup(in, charset);
			return (T)parseValue();
		} catch (IOException e) {
			throw new JSONException(e);
		} catch (JSONException e) {
			throw e;
		}
	}

	@Override
	public <T> T parse(InputStream in) throws JSONException {
		return parse(in, JSONDefaults.CHARSET);
	}


	private void setup(String jsonString) throws JSONException, IOException {
		scanner = new Scanner(jsonString);
	}
	
	private void setup(InputStream in, Charset charset) throws JSONException, IOException {
		scanner = new Scanner(in, charset);
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
			// We cannot ignore null values here, because it affects 
			// position of subsequent elements in the array.
			o.add(value);
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
