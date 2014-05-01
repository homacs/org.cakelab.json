package org.cakelab.json;

import java.io.IOException;

public class Parser {
	Scanner scanner;
	
	
	
	public Parser(String jsonString) throws IOException {
		scanner = new Scanner(jsonString);
	}

	public JSONObject parse() throws IOException {
		return parseObject();
	}

	private JSONObject parseObject() throws IOException {
		JSONObject o = new JSONObject();
		
		scanCharToken(Token.TYPE_LEFTBRACE);

		parseNVSequence(o, Token.TYPE_RIGHTBRACE);
		
		scanCharToken(Token.TYPE_RIGHTBRACE);
		
		return o;
	}

	private void parseNVSequence(JSONObject o, int endToken) throws IOException {
		char lookahead = scanner.getLookahead();
		if (lookahead == endToken) return;
		while (true) {
			parseNameValuePair(o);
			lookahead = scanner.getLookahead();
			if (lookahead == endToken) break;
			
			scanCharToken(Token.TYPE_COMMA);
		}
		
	}

	private void parseValueSequence(JSONArray o, int endToken) throws IOException {
		char lookahead = scanner.getLookahead();
		if (lookahead == endToken) return;
		while (true) {
			o.add(parseValue());
			lookahead = scanner.getLookahead();
			if (lookahead == endToken) break;
			
			scanCharToken(Token.TYPE_COMMA);
		}
		
	}

	private void parseNameValuePair(JSONObject parent) throws IOException {
		if (scanner.nextName() != Token.TYPE_NAME) error("expected a name");
		String name = scanner.getName();
		
		scanCharToken(Token.TYPE_COLON);

		
		Object value = parseValue();
		
		
		parent.put(name, value);
	}

	private Object parseValue() throws IOException {
		Object value = null;
		char lookahead = scanner.getLookahead();
		switch(lookahead) {
		case Token.TYPE_LEFTBRACE:
			value = parseObject();
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

	private JSONArray parseArray() throws IOException {
		JSONArray a = new JSONArray();
		scanCharToken(Token.TYPE_LEFTBRACKET);
		if (scanner.getLookahead() != Token.TYPE_RIGHTBRACKET) {
			/* non-empty array */
			parseValueSequence(a, Token.TYPE_RIGHTBRACKET);
		}
		scanCharToken(Token.TYPE_RIGHTBRACKET);
		return a;
	}

	private void scanCharToken(int tokenCharacter) throws IOException {
		if (scanner.next() != (char)tokenCharacter) error("expected token '"+  (char)tokenCharacter + "'");
		
	}

	private void error(String string) {
		throw new Error(":" + scanner.getLine() + ":" + scanner.getColumn() + ": " + string);
	}

}
