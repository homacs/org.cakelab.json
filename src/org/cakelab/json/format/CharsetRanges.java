package org.cakelab.json.format;

import java.io.IOException;
import java.io.StringReader;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;
import java.nio.charset.StandardCharsets;
import java.nio.charset.UnsupportedCharsetException;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.cakelab.json.JSONException;
import org.cakelab.json.parser.basic.DefaultParser;

/** Instances of this class manage valid code point ranges for a given charset used in encoding. */
public abstract class CharsetRanges {

	private static class CharsetRangeDouble extends CharsetRanges {
		private char firstLower;
		private char firstUpper;
		private char secondLower;
		private int secondUpper; ///< last upper can be larger than Character.MAX_VALUE

		public CharsetRangeDouble(char firstLower, char firstUpper, char secondLower, int secondUpper) {
			this.firstLower = firstLower;
			this.firstUpper = firstUpper;
			this.secondLower = secondLower;
			this.secondUpper = secondUpper;
		}

		@Override
		public boolean valid(char c) {
			return (firstLower <= c && c < firstUpper) ||
					(secondLower <= c && c < secondUpper) ;
		}

		@Override
		public boolean empty() {
			return false;
		}

	}

	private static class CharsetRangeSingle extends CharsetRanges {

		private char lowerBound;
		private char upperBound;

		public CharsetRangeSingle(char lowerBound, char upperBound) {
			this.lowerBound = lowerBound;
			this.upperBound = upperBound;
		}

		@Override
		public boolean valid(char c) {
			return lowerBound <= c && c < upperBound;
		}

		@Override
		public boolean empty() {
			return false;
		}

	}

	private static class CharsetRangesList extends CharsetRanges {
		char ranges[];

		CharsetRangesList(char ranges[]) {
			this.ranges = ranges;
			assert(ranges != null && ranges.length > 0);
		}
		
		public boolean valid(char c) {
			// a range set with zero ranges is not supported
			// sizes are so tiny that linear search is faster than anything else
			int i = 0;
			for (; i < ranges.length && c >= ranges[i]; i++);
			// i points at end of interval, which contains c
			return isValidRangeEndBoundary(i);
		}

		public String toString() {
			String s = "";
			for (int i = 0; i < ranges.length-1; i += 2) {
				s += "[" + hex(ranges[i]) + ',' + hex((int)(ranges[i + 1])-1) + ']';
			}
			if (ranges.length % 2 == 1) {
				int i = ranges.length-1;
				s += "[" + hex(ranges[i]) + ",inf]";
			}
			return s;
		}
		
		@Override
		public boolean empty() {
			return ranges.length == 0;
		}

		private boolean isValidRangeEndBoundary(int index) {
			return (index % 2 == 1);
		}

	}
	
	private static class Builder {
		
		private static final int SET_SIZE_LIMIT = 31;
		private static final boolean TEST_FORMATTER_AND_PARSER = true;
		
		private Charset charset;

		private CharsetEncoder encoder;
		private CharsetDecoder decoder;
		private CharBuffer charBuffer;
		private ByteBuffer byteBuffer;

		private DefaultParser parser;
		private JSONFormatterCompact formatter;

		static final int INITIAL_CAPACITY = 8;
		private int capacity;
		private char ranges[];
		private int size;

		public Builder(Charset charset) {
			this.charset = charset;
			setupEncoderDecoder();
			
			if(TEST_FORMATTER_AND_PARSER) {
				parser = new DefaultParser(false);
				setupFormatter();
			}
			
			ranges = new char[INITIAL_CAPACITY];
			capacity = INITIAL_CAPACITY;
			size = 0;
		}
		
		public CharsetRangesList build() throws UnsupportedCharsetException {
			boolean validRange = true;
			char begin = '\0';
			
			add(begin); // we define \0 to be always valid -> we always start with a valid boundary
			
			for (int i = begin + 1; i <= Character.MAX_VALUE; i++) {
				char c = toChar(i);
				validRange = evaluate(c, validRange);
			}
			shrink();
			return new CharsetRangesList(ranges);
		}

		private void add(char boundary) {
			grow();
			ranges[size++] = boundary;
		}

		private void shrink() {
			if (size < ranges.length) {
				ranges = Arrays.copyOf(ranges, size);
				capacity = size;
			}
		}
		
		private void grow() {
			if (size == capacity) {
				capacity *= 2;
				ranges = Arrays.copyOf(ranges, capacity);
			}
		}
		private void setupFormatter() {
			try {
				JSONFormatterConfiguration cfg = new JSONFormatterConfiguration()
						.charset(charset)
						.unicodeValues(false);
				formatter = new JSONFormatterCompact(cfg);
			} catch (JSONException e) {
				// formatter is not supposed to throw an exception
				// if cfg.unicodeValues is false.
				throw new RuntimeException("internal error: ", e);
			}
		}

		private void setupEncoderDecoder() {
			boolean encoderAvailable = charset.canEncode();
			encoder = null;
			decoder = null;
			charBuffer = CharBuffer.allocate(1);
			byteBuffer = ByteBuffer.allocate(8);
			if (encoderAvailable) {
				try {
					encoder = charset.newEncoder();
					decoder = charset.newDecoder();
					encodeDecode('\n');
				} catch (Throwable e) {
					encoderAvailable = false;
				}
			}

			if (!encoderAvailable) {
				throw new UnsupportedCharsetException(charset.name());
			}
		}

		private boolean evaluate(char c, boolean validRange) {
			if (canEncodeDecode(c)) {
				if (!validRange) {
					if (size > SET_SIZE_LIMIT) {
						throw new UnsupportedCharsetException(charset.name());
					}
					add(c);
					return true;
				}
			} else {
				if (validRange) {
					add(c);
					return false;
				}
			}
			return validRange;
		}

		private boolean canEncodeDecode(char c) {
			try {
				if (c == encodeDecode(c)) {
					if (TEST_FORMATTER_AND_PARSER) {
						String strValue = String.valueOf(c);
						String jsonString = formatter.format(strValue);
						String resultString = parser.parse(jsonString);
						return strValue.equals(resultString);
					} else {
						return true;
					}
				}
			} catch (Throwable e) {
				// part of the evaluation -> false
			}
			return false;
		}

		private char encodeDecode(char c) throws JSONException {
			// encode
			byteBuffer.position(0);
			charBuffer.position(0);
			charBuffer.put(0, c);
			encoder.reset();
			CoderResult result = encoder.encode(charBuffer, byteBuffer, true);
			if (result.isError() || result.isOverflow())
				throw new JSONException(hex(c) + ": " + result.toString());
			result = encoder.flush(byteBuffer);

			// decode
			decoder.reset();
			byteBuffer.position(0);
			charBuffer.position(0);
			result = decoder.decode(byteBuffer, charBuffer, true);
			if (result.isError() || result.isUnderflow())
				throw new JSONException(hex(c) + ": " + result.toString());
			result = decoder.flush(charBuffer);

			return charBuffer.get(0);
		}
		
	} // end of class Builder

	
	public abstract boolean valid(char c);
	
	public abstract boolean empty();

	
	private static CharsetRanges newCharsetRanges(char[] ranges) {
		switch(ranges.length) {
		case 1: assert(ranges[0] == '\0'); return FULL_RANGE;
		case 2: return new CharsetRangeSingle(ranges[0], ranges[1]);
		case 3: return new CharsetRangeDouble(ranges[0], ranges[1], ranges[2], 0x10000);
		case 4: return new CharsetRangeDouble(ranges[0], ranges[1], ranges[2], ranges[3]);
		default: return new CharsetRangesList(ranges);
		}
	}
	
	static final CharsetRanges FULL_RANGE = new CharsetRanges() {
		@Override
		public boolean valid(char c) {return true;}
		@Override
		public boolean empty() {return false;}
	};
	
	private static final CharsetRanges NOT_SUPPORTED = new CharsetRanges() {
		@Override
		public boolean valid(char c) {throw new UnsupportedOperationException();}
		@Override
		public boolean empty() {throw new UnsupportedOperationException();}
	};
	
	private static Map<Charset, CharsetRanges> cache = new ConcurrentHashMap<>();
	static {
		// generated by DefaultRangesCodeGen
		cache.put(StandardCharsets.UTF_8, newCharsetRanges(new char[]{ '\u0000', '\uD800', '\uE000', }));
		cache.put(StandardCharsets.UTF_16, newCharsetRanges(new char[]{ '\u0000', '\uD800', '\uE000', '\uFFFE', '\uFFFF', }));
		cache.put(StandardCharsets.US_ASCII, newCharsetRanges(new char[]{ '\u0000', '\u0080', }));
		cache.put(StandardCharsets.ISO_8859_1, newCharsetRanges(new char[]{ '\u0000', '\u0100', }));
	}
	
	public static CharsetRanges get(Charset charset) throws JSONException {
		CharsetRanges result = cache.get(charset);
		if (result == null) {
			result = build(charset);
			cache.put(charset, result);
		}
		if (result == NOT_SUPPORTED) throw new JSONException("character encoding not supported: " + charset.name());
		return result;
	}

	private static CharsetRanges build(Charset charset) {
		try {
			Builder builder = new Builder(charset);
			CharsetRangesList list = builder.build();
			return newCharsetRanges(list.ranges);
		} catch (UnsupportedCharsetException e) {
			return NOT_SUPPORTED;
		}
	}


	private static String hex(int c) {
		return Integer.toHexString(c & 0xFFFF).toUpperCase();
	}
	private static char toChar(int i) {
		return (char)(i & 0xFFFF);
	}

	static class DefaultRangesCodeGen {
		
		private static String charValueCode(char c) {
			String hexDigits = hex(c);
			while (hexDigits.length() < 4) hexDigits = "0" + hexDigits;
			return "'\\u" + hexDigits + "'";
		}
	
		private static String toCodeString(String name, char[] ranges) {
			String code = "cache.put(StandardCharsets." + getConstantName(name) + ", newCharsetRanges(";
			code += "new char[]{ ";
			String indent = "";
			int i = 0;
			for (; i < ranges.length; i++) {
				code += indent + charValueCode(ranges[i]) + ", ";
			}
	
			code += "}";
			code +=	"));";
			
			return code.toString();
		}
		
		static String getConstantName(String name) {
			StringReader r = new StringReader(name);
			StringBuilder s = new StringBuilder();
			int c;
			try {
				while (-1 < (c = r.read())) {
					if (isPunctuator((char) c)) s.append("_");
					else s.append(Character.toUpperCase((char) c));
				}
			} catch (IOException e) {
				// impossible
			}
			return s.toString();
		}
		static boolean isPunctuator(char c) {
			return c == '-' || c == '+' || c == '.';
		}
		static void gen(Charset charset) {
			CharsetRangesList ranges = new Builder(charset).build();
			String name = charset.name();
			String code = toCodeString(name, ranges.ranges);
			System.out.println(code);
		}
	}
	
	public static void main(String[] args) {
		DefaultRangesCodeGen.gen(StandardCharsets.UTF_8);
		DefaultRangesCodeGen.gen(StandardCharsets.UTF_16);
		DefaultRangesCodeGen.gen(StandardCharsets.US_ASCII);
		DefaultRangesCodeGen.gen(StandardCharsets.ISO_8859_1);
	}

}
