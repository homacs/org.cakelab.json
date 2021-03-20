package org.cakelab.json.parser;

import org.cakelab.json.parser.pojo.POJOParserFactory;

public abstract class ParserFactory {
	
	public static final ParserFactory DEFAULT = new POJOParserFactory();


	
	public abstract Parser create(boolean ignoreNull);

	
	
	public Parser create() {
		return create(false);
	}

	
}
