package org.cakelab.json.parser.basic;

import org.cakelab.json.parser.JSONParser;
import org.cakelab.json.parser.JSONParserFactory;

public class DefaultParserFactory extends JSONParserFactory {
	
	public JSONParser create(boolean ignoreNull){
		return new DefaultParser(ignoreNull);
	}

}
