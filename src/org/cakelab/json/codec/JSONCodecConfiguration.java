package org.cakelab.json.codec;

import static org.cakelab.json.JSONDefaults.CHARSET;
import static org.cakelab.json.JSONDefaults.CODEC_IGNORE_MISSING_FIELDS;
import static org.cakelab.json.JSONDefaults.CODEC_IGNORE_NULL;
import static org.cakelab.json.JSONDefaults.CODEC_SUPPORT_CLASS_ATTRIB;
import static org.cakelab.json.JSONDefaults.FORMATTER;
import static org.cakelab.json.JSONDefaults.PARSER_FACTORY;

import java.nio.charset.Charset;

import org.cakelab.json.format.JSONFormatter;
import org.cakelab.json.parser.JSONParser;
import org.cakelab.json.parser.JSONParserFactory;

/**
 * Configuration of the JSON codec (see {@link JSONCodec}).
 * 
 * <h3>Multi-Threading</h3>
 * Immutable.
 * 
 * @author homac
 *
 */
public class JSONCodecConfiguration {

	/** character encoding of the json string. Can be different from formatters character encoding.
	 * @see JSONFormatterConfig */
	public final Charset charset;
	
	/** codec removes all members with null values in a given object during conversion into a string.*/
	public final boolean ignoreNull;
	
	/** codec ignores fields that exist in the json string but not in the corresponding java class.*/
	public final boolean ignoreMissingFields;
	
	/** considers the special member "class" in a JSON Object as type of the compound type 
	 * which contains it - much like the static member 'class' of each Java class.*/
	public final boolean supportClassAttribute;

	/** defines the parser factory to be used to create instances of {@link JSONParser}.*/
	public final JSONParserFactory parserFactory;
	
	/** defines the default formatter used to render JSON strings. */
	public final JSONFormatter formatter;
	

	
	public JSONCodecConfiguration(JSONCodecConfiguration that) {
		this.charset = that.charset;
		this.ignoreNull = that.ignoreNull;
		this.ignoreMissingFields = that.ignoreMissingFields;
		this.supportClassAttribute = that.supportClassAttribute;
		this.parserFactory = that.parserFactory;
		this.formatter = that.formatter;
	}
	
	public JSONCodecConfiguration(Charset charset, boolean ignoreNull, boolean ignoreMissingFields, boolean supportClassAttribute, JSONParserFactory parserFactory, JSONFormatter formatter) {
		this.charset = charset;
		this.ignoreNull = ignoreNull;
		this.ignoreMissingFields = ignoreMissingFields;
		this.supportClassAttribute = supportClassAttribute;
		this.parserFactory = parserFactory;
		this.formatter = formatter;
	}

	public JSONCodecConfiguration() {
		this.charset = CHARSET;
		this.ignoreNull = CODEC_IGNORE_NULL;
		this.ignoreMissingFields = CODEC_IGNORE_MISSING_FIELDS;
		this.supportClassAttribute = CODEC_SUPPORT_CLASS_ATTRIB;
		this.parserFactory = PARSER_FACTORY;
		this.formatter = FORMATTER;
	}
	
	public JSONCodecConfiguration charset(Charset charset) {
		return new JSONCodecConfiguration(charset, ignoreNull, ignoreMissingFields, supportClassAttribute, parserFactory, formatter);
	}
	public JSONCodecConfiguration ignoreNull(boolean ignoreNull) {
		return new JSONCodecConfiguration(charset, ignoreNull, ignoreMissingFields, supportClassAttribute, parserFactory, formatter);
	}
	public JSONCodecConfiguration ignoreMissingFields(boolean ignoreMissingFields) {
		return new JSONCodecConfiguration(charset, ignoreNull, ignoreMissingFields, supportClassAttribute, parserFactory, formatter);
	}
	public JSONCodecConfiguration supportClassAttribute(boolean supportClassAttribute) {
		return new JSONCodecConfiguration(charset, ignoreNull, ignoreMissingFields, supportClassAttribute, parserFactory, formatter);
	}
	public JSONCodecConfiguration parserFactory(JSONParserFactory parserFactory) {
		return new JSONCodecConfiguration(charset, ignoreNull, ignoreMissingFields, supportClassAttribute, parserFactory, formatter);
	}
	public JSONCodecConfiguration formatter(JSONFormatter formatter) {
		return new JSONCodecConfiguration(charset, ignoreNull, ignoreMissingFields, supportClassAttribute, parserFactory, formatter);
	}
}
