package org.cakelab.json.parser;

import org.cakelab.json.JSONDefaults;
import org.cakelab.json.JSONException;
import org.cakelab.json.format.JSONFormatterPlain;
import org.cakelab.json.parser.basic.DefaultParserFactory;
import org.junit.jupiter.api.BeforeAll;

public class TestDefaultParserPlainFormatter extends JSONParserTestSuite {

	
	
	@BeforeAll
	public static void initDefaults() throws JSONException {
		parser = new DefaultParserFactory().create();
		formatter = new JSONFormatterPlain(JSONDefaults.FORMATTER_CONFIG);
	}
	


}
