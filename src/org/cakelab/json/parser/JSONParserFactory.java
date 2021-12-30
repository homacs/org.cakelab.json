package org.cakelab.json.parser;

public abstract class JSONParserFactory {
	
	/**
	 * Create a parser.
	 * @param ignoreNull Whether to ignore attributes, which have a null value and not add them to the object representation.
	 * @return new parser instance.
	 */
	public abstract JSONParser create(boolean ignoreNull);

	public JSONParser create() {
		return create(false);
	}

	
}
