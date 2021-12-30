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

public interface JSONDefaults {

	public static final Charset CHARSET = StandardCharsets.UTF_8;
	public static final boolean FORMATTER_SORT_MEMBERS = true;
	public static final boolean FORMATTER_UNICODE_VALUES = true;
	
	public static final JSONFormatterConfiguration FORMATTER_CONFIG = new JSONFormatterConfiguration(CHARSET, FORMATTER_SORT_MEMBERS, FORMATTER_UNICODE_VALUES);
	public static final JSONFormatter FORMATTER = new JSONFormatterPrettyprint(FORMATTER_CONFIG);

	public static final JSONParserFactory PARSER_FACTORY = new DefaultParserFactory();

	public static final boolean CODEC_IGNORE_NULL = false;
	public static final boolean CODEC_IGNORE_MISSING_FIELDS = false;
	public static final boolean CODEC_SUPPORT_CLASS_ATTRIB = false;
	
	public static final JSONCodecConfiguration CODEC_CONFIG = new JSONCodecConfiguration(CHARSET, CODEC_IGNORE_NULL, CODEC_IGNORE_MISSING_FIELDS, CODEC_SUPPORT_CLASS_ATTRIB, PARSER_FACTORY, FORMATTER);
	
	public static JSONParser createDefaultParser() {
		return PARSER_FACTORY.create();
	}

}
