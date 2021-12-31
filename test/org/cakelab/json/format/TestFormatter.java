package org.cakelab.json.format;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.StandardCharsets;

import org.cakelab.json.JSONDefaults;
import org.cakelab.json.JSONException;
import org.cakelab.json.parser.JSONParser;
import org.junit.jupiter.api.Test;

public class TestFormatter {

	private JSONParser parser = JSONDefaults.PARSER_FACTORY.create();

	@Test
	public void testCharacterEncodingRangeSetup() throws JSONException {
		JSONFormatterConfiguration cfg = JSONDefaults.FORMATTER_CONFIG
				.charset(StandardCharsets.ISO_8859_1)
				.unicodeValues(true);
		
		JSONFormatter formatter = new JSONFormatterPrettyprint(cfg);
		
		CharsetEncoder encoder = StandardCharsets.ISO_8859_1.newEncoder();
		assertTrue (!encoder.canEncode('\uFFFF'));
		
		String str = "\uFFFF";
		String jsonStr = formatter.format(str);
		String decodedStr = parser.parse(jsonStr);
		assertEquals(str, decodedStr);
	}

	@Test
	public void testCharacterEncodingRanges() throws JSONException {
		
		testEncodingRange(StandardCharsets.US_ASCII);
		testEncodingRange(StandardCharsets.UTF_16);
		testEncodingRange(StandardCharsets.UTF_16BE);
		testEncodingRange(StandardCharsets.UTF_16LE);
		testEncodingRange(StandardCharsets.UTF_8);
		testEncodingRange(StandardCharsets.ISO_8859_1);
		
	}

	
	private void testEncodingRange(Charset charset) throws JSONException {
		
		
		if (!charset.canEncode()) {
			System.out.println(charset.name() + ": no encoding support");
			return;
		}
		
		JSONFormatterConfiguration cfg = JSONDefaults.FORMATTER_CONFIG
				.charset(charset)
				.unicodeValues(true);
		
		JSONFormatter formatter = new JSONFormatterPrettyprint(cfg);

		CharsetRanges encodingRange = CharsetRanges.get(charset);


		int c = '\0' + 1;
		for (; c <= 0xFFFF; c++) {
			String strValue = String.valueOf(toChar(c));
			String jsonString = formatter.format(strValue);
			if (!encodingRange.valid(toChar(c))) {
				final String prefix = "\"\\u";
				assertEquals(prefix, jsonString.substring(0, prefix.length()));
			}
			String resultString = parser.parse(jsonString);
			assertEquals(strValue, resultString);
		}
	}

	private char toChar(int c) {
		return (char)(c & 0xFFFF);
	}


}
