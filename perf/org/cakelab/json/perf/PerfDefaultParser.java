package org.cakelab.json.perf;

import org.cakelab.json.parser.JSONParserFactory;
import org.cakelab.json.parser.basic.DefaultParserFactory;

public class PerfDefaultParser extends ParserPerfBase {

	
	static final JSONParserFactory defaultParserFactory = new DefaultParserFactory();
	
	
	public static void main(String[] args) {
		int member = 10;
		
		String jsonString = generateJson(member);
		measure(defaultParserFactory, jsonString);

	}



}
