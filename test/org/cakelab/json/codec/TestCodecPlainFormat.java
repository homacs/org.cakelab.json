package org.cakelab.json.codec;

import org.cakelab.json.JSONDefaults;
import org.cakelab.json.JSONException;
import org.cakelab.json.format.JSONFormatterCompact;
import org.junit.jupiter.api.BeforeAll;

public class TestCodecPlainFormat extends JSONCodecTestSuite {

	@BeforeAll
	public static void setup() throws JSONException {
		JSONCodecConfiguration cfg = new JSONCodecConfiguration()
				.ignoreNull(true)
				.formatter(new JSONFormatterCompact(JSONDefaults.FORMATTER_CONFIG))
				;
		codec = new JSONCodec(cfg);
	}

	
	
	
}
