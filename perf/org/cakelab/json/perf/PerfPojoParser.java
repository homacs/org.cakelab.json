package org.cakelab.json.perf;

import org.cakelab.json.parser.ParserFactory;
import org.cakelab.json.parser.pojo.POJOParserFactory;

public class PerfPojoParser extends ParserPerfBase {

	
	static final ParserFactory pojoParserFactory = new POJOParserFactory();
	
	
	public static void main(String[] args) {
		int member = 10;
		
		String jsonString = generateJson(member);
		measure(pojoParserFactory, jsonString);

	}



}
