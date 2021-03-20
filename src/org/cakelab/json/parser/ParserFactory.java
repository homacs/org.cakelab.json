package org.cakelab.json.parser;

import org.cakelab.json.parser.pojo.POJOParserFactory;

public abstract class ParserFactory {
	
	public static final ParserFactory DEFAULT = new POJOParserFactory();


	/**
	 * Create a parser.
	 * @param ignoreNull Whether to ignore attributes, which have a null value and not add them to the object representation.
	 * @return new parser instance.
	 */
	public abstract Parser create(boolean ignoreNull);

	
	
	public Parser create() {
		return create(false);
	}

	
}
