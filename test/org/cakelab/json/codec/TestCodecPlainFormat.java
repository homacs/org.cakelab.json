package org.cakelab.json.codec;

import org.cakelab.json.JSONDefaults;
import org.cakelab.json.format.JSONFormatterPlain;
import org.junit.jupiter.api.BeforeAll;

public class TestCodecPlainFormat extends JSONCodecTestSuite {

	@BeforeAll
	public static void setup() {
		JSONCodecConfiguration cfg = new JSONCodecConfiguration()
				.ignoreNull(true)
				.formatter(new JSONFormatterPlain(JSONDefaults.FORMATTER_CONFIG))
				;
		codec = new JSONCodec(cfg);
	}

	
	
	
}
