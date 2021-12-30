package org.cakelab.json.parser.basic;

public class Token {
	/* Token category: Character */
	static final int TYPE_CATEGORY_CHARACTER = 0;
	static final int TYPE_LEFTBRACE = '{';
	static final int TYPE_RIGHTBRACE = '}';
	static final int TYPE_COMMA = ',';
	static final int TYPE_COLON = ':';
	static final int TYPE_LEFTBRACKET = '[';
	static final int TYPE_RIGHTBRACKET = ']';
	static final int TYPE_DOUBLEQUOTES = '"';
	static final int TYPE_BACKSLASH = '\\';
	static final int TYPE_PLUS = '+';
	static final int TYPE_MINUS = '-';
	static final int TYPE_SLASH = '/';
	
	/* Token category: Value */
	static final int TYPE_CATEGORY_VALUE = TYPE_CATEGORY_CHARACTER + 1024;
	static final int TYPE_STRING = TYPE_CATEGORY_VALUE + 0;
	static final int TYPE_NUMBER = TYPE_CATEGORY_VALUE + 1;
	static final int TYPE_BOOLEAN = TYPE_CATEGORY_VALUE + 2;
	static final int TYPE_NULL = TYPE_CATEGORY_VALUE + 3;
	
	
	/* Token category: Symbol */
	static final int TYPE_CATEGORY_SYMBOL = TYPE_CATEGORY_VALUE + 1024;
	static final int TYPE_NAME = TYPE_CATEGORY_SYMBOL + 0;
	
	

	static final int TYPE_UNKNOWN = Integer.MAX_VALUE;
	


	public static boolean isWhitespace(char c) {
		return (" \r\n\t\b\f".indexOf(c) >= 0);
	}


	public static boolean isDigit(char c) {
		return "0123456789".indexOf(c) >= 0;
	}
	
	public static boolean isHexDigit(char c) {
		return "0123456789abcdefABCDEF".indexOf(c) >= 0;
	}


	public static boolean isAlpha(char c) {
		c = Character.toLowerCase(c);
		return c >= 'a' && c <= 'z';
	}


	public static boolean isLegalNameElement(char c) {
		return c == '_';
	}


}
