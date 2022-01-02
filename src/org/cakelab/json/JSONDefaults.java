package org.cakelab.json;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import org.cakelab.json.codec.JSONCodecConfiguration;
import org.cakelab.json.format.JSONFormatter;
import org.cakelab.json.format.JSONFormatterConfiguration;
import org.cakelab.json.format.JSONFormatterPrettyprint;
import org.cakelab.json.parser.JSONParser;
import org.cakelab.json.parser.JSONParserFactory;
import org.cakelab.json.parser.basic.DefaultParserFactory;

public class JSONDefaults {

	public static final Charset CHARSET = StandardCharsets.UTF_8;
	public static final boolean IGNORE_NULL = false;
	
	public static final boolean SORT_MEMBERS = true;
	public static final boolean WRITE_UNICODE_VALUES = true;
	
	public static final JSONFormatterConfiguration FORMATTER_CONFIG = new JSONFormatterConfiguration(CHARSET, SORT_MEMBERS, WRITE_UNICODE_VALUES, IGNORE_NULL);
	public static final JSONFormatter FORMATTER;
	static {
		try {
			FORMATTER = new JSONFormatterPrettyprint(FORMATTER_CONFIG);
		} catch (JSONException e) {
			// default character encoding (UTF8) will not cause this to happen.
			throw new RuntimeException(e);
		}
	}
	
	public static final JSONParserFactory PARSER_FACTORY = new DefaultParserFactory();

	public static final boolean IGNORE_MISSING_FIELDS = false;
	public static final boolean SUPPORT_CLASS_ATTRIB = false;
	
	public static final JSONCodecConfiguration CODEC_CONFIG = new JSONCodecConfiguration(CHARSET, IGNORE_NULL, IGNORE_MISSING_FIELDS, SUPPORT_CLASS_ATTRIB, PARSER_FACTORY, FORMATTER);
	
	public static JSONParser createDefaultParser() {
		return PARSER_FACTORY.create();
	}

}
