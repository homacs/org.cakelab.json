package org.cakelab.json.format;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import org.cakelab.json.JSONException;
import org.junit.jupiter.api.Test;

public class TestEncodingRangeSet {

	@Test
	public void testBuild() throws JSONException {
		CharsetRanges rangeSet;
		rangeSet = CharsetRanges.get(StandardCharsets.ISO_8859_1);
		assertValid(rangeSet);
		rangeSet = CharsetRanges.get(StandardCharsets.US_ASCII);
		assertValid(rangeSet);
		rangeSet = CharsetRanges.get(StandardCharsets.UTF_16);
		assertValid(rangeSet);
		rangeSet = CharsetRanges.get(StandardCharsets.UTF_16BE);
		assertValid(rangeSet);
		rangeSet = CharsetRanges.get(StandardCharsets.UTF_16LE);
		assertValid(rangeSet);
		rangeSet = CharsetRanges.get(StandardCharsets.UTF_8);
		assertValid(rangeSet);
	}

	private void assertValid(CharsetRanges rangeSet) {
		assertNotNull(rangeSet);
		assertFalse(rangeSet.empty());
	}

	
	
	@Test
	public void testRangeSet() throws JSONException {
		int c;
		CharsetRanges rangeSet;
		// US-ASCII: (1): [0,7F]
		
		c = 0;
		rangeSet = CharsetRanges.get(StandardCharsets.US_ASCII);
		for (; c <= 0x7F; c++)
			assertTrue(rangeSet.valid(toChar(c)));
		for (; c <= 0xFFFF; c++)
			assertFalse(rangeSet.valid(toChar(c)));
		
		// ISO-8859-1: (1): [0,FF]
		c = 0;
		rangeSet = CharsetRanges.get(StandardCharsets.ISO_8859_1);
		for (; c <= 0xFF; c++)
			assertTrue(rangeSet.valid(toChar(c)));
		for (; c <= 0xFFFF; c++)
			assertFalse(rangeSet.valid(toChar(c)));

		// IBM273: [0,84][86,FF]
		c = 0;
		rangeSet = CharsetRanges.get(Charset.forName("IBM273"));
		for (; c <= 0x84; c++)
			assertTrue(rangeSet.valid(toChar(c)));
		for (; c < 0x86; c++)
			assertFalse(rangeSet.valid(toChar(c)));
		for (; c <= 0xFF; c++)
			assertTrue(rangeSet.valid(toChar(c)));
		for (; c <= 0xFFFF; c++)
			assertFalse(rangeSet.valid(toChar(c)));

		// UTF-16: (3): [0,D7FF][E000,FFFD][FFFF,inf]
		c = 0;
		rangeSet = CharsetRanges.get(StandardCharsets.UTF_16);
		for (; c <= 0xD7FF; c++)
			assertTrue(rangeSet.valid(toChar(c)));
		for (; c < 0xE000; c++)
			assertFalse(rangeSet.valid(toChar(c)));
		for (; c <= 0xFFFD; c++)
			assertTrue(rangeSet.valid(toChar(c)));
		for (; c < 0xFFFF; c++)
			assertFalse(rangeSet.valid(toChar(c)));
		for (; c <= 0xFFFF; c++)
			assertTrue(rangeSet.valid(toChar(c)));
		
	}
	
	private char toChar(int c) {
		return (char)(c & 0xFFFF);
	}

}
