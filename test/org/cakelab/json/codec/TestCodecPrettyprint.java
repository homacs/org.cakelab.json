package org.cakelab.json.codec;

import org.junit.jupiter.api.BeforeAll;

public class TestCodecPrettyprint extends JSONCodecTestSuite {

	@BeforeAll
	public static void setup() {
		codec = new JSONCodec(new JSONCodecConfiguration().ignoreNull(true));
	}

	
	
	
}
