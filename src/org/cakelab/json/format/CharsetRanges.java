package org.cakelab.json.format;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;
import java.nio.charset.UnsupportedCharsetException;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.cakelab.json.JSONException;
import org.cakelab.json.parser.basic.DefaultParser;

public abstract class CharsetRanges {

	private static class CharsetRangesImpl extends CharsetRanges {
		char ranges[];
		int size;
		int capacity;

		private CharsetRangesImpl() {
			capacity = 8;
			ranges = new char[capacity];
		}

		
		public boolean valid(char c) {
			// a range set with zero ranges is not supported
			assert (size > 0);
			// sizes are so tiny that linear search is faster than anything else
			int i = 0;
			for (; i < size && c >= ranges[i]; i++);
			// i points at end of interval, which contains c
			return isValidRangeEndBoundary(i);
		}

		public String toString() {
			String s = "";
			for (int i = 0; i < size-1; i += 2) {
				s += "[" + hex(ranges[i]) + ',' + hex((int)(ranges[i + 1])-1) + ']';
			}
			if (size % 2 == 1) {
				int i = size-1;
				s += "[" + hex(ranges[i]) + ",inf]";
			}
			return s;
		}

		private void add(char boundary) {
			grow();
			ranges[size++] = boundary;
		}

		private boolean isValidRangeEndBoundary(int index) {
			return (index % 2 == 1);
		}

		private void grow() {
			if (size == capacity) {
				capacity += 8;
				ranges = Arrays.copyOf(ranges, capacity);
			}
		}

		public int size() {
			return size/2;
		}


		public boolean empty() {
			return size == 0;
		}

	}


	private static class Builder {
		
		private static final int SET_SIZE_LIMIT = 15;

		private static final boolean TEST_INTERNAL = false;
		
		private Charset charset;

		private CharsetEncoder encoder;
		private CharsetDecoder decoder;
		private CharBuffer charBuffer;
		private ByteBuffer byteBuffer;

		private DefaultParser parser;
		private JSONFormatterPlain formatter;

		private CharsetRangesImpl ranges;

		public Builder(Charset charset) {
			this.charset = charset;
			setupEncoderDecoder();
			
			if(TEST_INTERNAL) {
				parser = new DefaultParser(false);
				setupFormatter();
			}
			
			ranges = new CharsetRangesImpl();
		}
		

		public CharsetRanges build() throws UnsupportedCharsetException {
			boolean validRange = true;
			char begin = 0;
			
			ranges.add(begin);
			
			for (int i = '\0' + 1; i <= 0xFFFF; i++) {
				char c = toChar(i);
				validRange = evaluate(c, validRange);
			}

			return ranges;
		}
		
		private void setupFormatter() {
			try {
				JSONFormatterConfiguration cfg = new JSONFormatterConfiguration()
						.charset(charset)
						.unicodeValues(false);
				formatter = new JSONFormatterPlain(cfg);
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
					if (ranges.size() > SET_SIZE_LIMIT) {
						throw new UnsupportedCharsetException(charset.name());
					}
					ranges.add(c);
					return true;
				}
			} else {
				if (validRange) {
					ranges.add(c);
					return false;
				}
			}
			return validRange;
		}

		private boolean canEncodeDecode(char c) {
			try {
				if (c == encodeDecode(c)) {
					if (TEST_INTERNAL) {
						String strValue = String.valueOf(c);
						String jsonString = formatter.format(strValue);
						String resultString = parser.parse(jsonString);
						return strValue.equals(resultString);
					} else {
						return true;
					}
				}
			} catch (Throwable e) {
				// intentionally empty
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

	
	public abstract int size();
	public abstract boolean empty();
	public abstract boolean valid(char c);
	
	
	static final CharsetRanges FULL_RANGE = new CharsetRanges() {
		@Override
		public int size() {return 0;}
		@Override
		public boolean empty() {return true;}
		@Override
		public boolean valid(char c) {return true;}
	};
	
	private static final CharsetRanges NOT_SUPPORTED = new CharsetRanges() {
		@Override
		public int size() {return 0;}
		@Override
		public boolean empty() {return true;}
		@Override
		public boolean valid(char c) {return false;}
	};
	
	private static Map<Charset, CharsetRanges> cache = new ConcurrentHashMap<>();
	
	
	public static CharsetRanges get(Charset charset) throws JSONException {
		CharsetRanges result = cache.get(charset);
		if (result == null) {
			result = build(charset);
			cache.put(charset, result);
		}
		if (result == NOT_SUPPORTED) throw new JSONException("character encoding not supported: " + charset.name());
		return result;
	}

	public static CharsetRanges build(Charset charset) {
		try {
			Builder builder = new Builder(charset);
			return builder.build();
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


}
