package org.cakelab.json.parser;

import org.cakelab.json.JSONDefaults;

/**
 * Creates JSONParser instances.
 * 
 * <h3>Multi-Threading</h3>
 * Implementations are supposed to be thread-safe.
 * 
 * @author homac
 *
 */
public abstract class JSONParserFactory {
	
	/**
	 * Create a parser.
	 * @param ignoreNull Whether to ignore attributes, which have a null value and not add them to the object representation.
	 * @return new parser instance.
	 */
	public abstract JSONParser create(boolean ignoreNull);

	/** Create a parser using {@link JSONDefaults#CODEC_IGNORE_NULL}. */
	public JSONParser create() {
		return create(JSONDefaults.IGNORE_NULL);
	}

	
}
