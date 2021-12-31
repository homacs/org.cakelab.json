package org.cakelab.json.parser;

import java.nio.charset.StandardCharsets;

import org.cakelab.json.JSONDefaults;
import org.cakelab.json.JSONException;
import org.cakelab.json.format.JSONFormatterConfiguration;
import org.cakelab.json.format.JSONFormatterPlain;
import org.cakelab.json.parser.basic.DefaultParserFactory;
import org.junit.jupiter.api.BeforeAll;

public class TestDefaultParserISO8859 extends JSONParserTestSuite {
	
	@BeforeAll
	public static void initDefaults() throws JSONException {
		parser = new DefaultParserFactory().create();
		
		JSONFormatterConfiguration cfg = JSONDefaults.FORMATTER_CONFIG
				.unicodeValues(true)
				.charset(StandardCharsets.ISO_8859_1);
		formatter = new JSONFormatterPlain(cfg);
	}



}
