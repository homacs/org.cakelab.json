package org.cakelab.json.parser;

import org.cakelab.json.JSONDefaults;
import org.cakelab.json.format.JSONFormatterPrettyprint;
import org.cakelab.json.parser.basic.DefaultParserFactory;
import org.junit.jupiter.api.BeforeAll;

public class TestDefaultParserPrettyprint extends JSONParserTestSuite {

	@BeforeAll
	public static void initDefaults() {
		parserFactory = new DefaultParserFactory();
		formatterFactory = new JSONFormatterPrettyprint(JSONDefaults.FORMATTER_CONFIG);
	}
	


}