package org.cakelab.json.parser.jni;

import org.cakelab.json.parser.Parser;
import org.cakelab.json.parser.ParserFactory;

public class NativeParserFactory extends ParserFactory {

	public static final boolean AVAILABLE = Library.AVAILABLE;

	
	public NativeParserFactory() {
		if (!AVAILABLE) throw new Error("without native library " + Library.LIBRARY_PATH + " you cannot use " + NativeParserFactory.class.getCanonicalName());
	}
	
	
	@Override
	public Parser create(boolean ignoreNull) {
		return new NativeParser(ignoreNull);
	}

}
