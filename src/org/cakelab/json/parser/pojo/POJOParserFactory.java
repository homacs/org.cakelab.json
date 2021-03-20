package org.cakelab.json.parser.pojo;

import org.cakelab.json.parser.Parser;
import org.cakelab.json.parser.ParserFactory;

public class POJOParserFactory extends ParserFactory {
	
	public Parser create(boolean ignoreNull){
		return new POJOParser(ignoreNull);
	}

}
