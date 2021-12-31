package org.cakelab.json.format;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;
import java.nio.charset.UnsupportedCharsetException;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.cakelab.json.JSONException;
import org.cakelab.json.parser.JSONParser;
import org.cakelab.json.parser.basic.DefaultParser;

public abstract class CharsetRanges {

	private static class CharsetRangesImpl extends CharsetRanges {
		int ranges[];
		int size;
		int capacity;

		CharsetRangesImpl(boolean valid) {
			capacity = 0;
			size = 0;
			ranges = null;
		}
		
		
		CharsetRangesImpl() {
			capacity = 8;
			ranges = new int[capacity];
		}

		void add(char boundary) {
			grow();
			ranges[size++] = boundary;
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


	static class Builder {
		private CharsetEncoder encoder;
		private CharsetDecoder decoder;
		private CharBuffer charBuffer;
		private ByteBuffer byteBuffer;

		public Builder(Charset charset) {
			
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
		
		public CharsetRanges build(Charset charset) throws UnsupportedCharsetException {

			JSONParser parser = new DefaultParser(false);

			JSONFormatter formatter;
			try {
				JSONFormatterConfiguration cfg = new JSONFormatterConfiguration()
						.charset(charset)
						.unicodeValues(false);
				formatter = new JSONFormatterPlain(cfg);
			} catch (JSONException e) {
				throw new RuntimeException("internal error: ", e);
			}

			CharsetRangesImpl rangeSet = new CharsetRangesImpl();

			boolean validRange = true;
			int i = '\0' + 1;
			char begin = 0;
			rangeSet.add(begin);
			for (; i <= 0xFFFF; i++) {
				char c = toChar(i);
				String strValue = String.valueOf(c);
				String resultString = "ERROR";
				if (canEncodeDecode(c)) {
					try {
						String jsonString = formatter.format(strValue);
						resultString = parser.parse(jsonString);
					} catch (Throwable e) {
					}
				}
				if (strValue.equals(resultString)) {
					if (!validRange) {
						if (rangeSet.size() > 31) {
							throw new UnsupportedCharsetException(charset.name());
						}
						rangeSet.add(c);
						validRange = true;
					}
				} else {
					if (validRange) {
						rangeSet.add(c);
						validRange = false;
					}
				}
			}

			return rangeSet;
		}

		/**
		 * tests if provided encoder and decoder properly work for the given character
		 * 
		 * @throws CharacterCodingException
		 */
		private boolean canEncodeDecode(char c) {
			try {
				return c == encodeDecode(c);
			} catch (Throwable e) {
				return false;
			}
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
		
		
	}

	public abstract int size();
	public abstract boolean empty();
	public abstract boolean valid(char c);
	
	
	static final CharsetRanges FULL_RANGE;
	static {
		CharsetRangesImpl impl = new CharsetRangesImpl();
		impl.add(toChar(0));
		FULL_RANGE = impl;
	}
	
	static final CharsetRanges NOT_SUPPORTED = new CharsetRangesImpl(false);
	
	
	private static Map<Charset, CharsetRanges> sets = new ConcurrentHashMap<>();
	
	
	public static CharsetRanges get(Charset charset) throws JSONException {
		CharsetRanges result = sets.get(charset);
		if (result == null) {
			result = build(charset);
			sets.put(charset, result);
		}
		if (result == NOT_SUPPORTED) throw new JSONException("character encoding not supported: " + charset.name());
		return result;
	}

	public static CharsetRanges build(Charset charset) {
		try {
			Builder builder = new Builder(charset);
			return builder.build(charset);
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
