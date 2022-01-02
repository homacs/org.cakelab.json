package org.cakelab.json.codec;

import static org.cakelab.json.JSONDefaults.CHARSET;
import static org.cakelab.json.JSONDefaults.IGNORE_MISSING_FIELDS;
import static org.cakelab.json.JSONDefaults.IGNORE_NULL;
import static org.cakelab.json.JSONDefaults.SUPPORT_CLASS_ATTRIB;
import static org.cakelab.json.JSONDefaults.FORMATTER;
import static org.cakelab.json.JSONDefaults.PARSER_FACTORY;

import java.nio.charset.Charset;

import org.cakelab.json.format.JSONFormatter;
import org.cakelab.json.parser.JSONParser;
import org.cakelab.json.parser.JSONParserFactory;
import org.cakelab.json.JSONDefaults;
/**
 * Configuration of {@link JSONCodec}s and {@link JSONModeller}s.
 * 
 * A configuration is intended to be used for multiple 
 * (if not all) instances of JSONCodec or JSONModeller.
 * 
 * This class is immutable and implements a variant of the 
 * Builder Pattern.
 * <ul>
 * <li>All fields are <code>public final</code>.</li>
 * <li>For each field exists a "setter" with the same name.</li>
 * <li>Each field "setter" creates a copy of the instance, 
 *     changing the field to the given value.</li>
 * </ul>
 * 
 * Following code creates a default configuration with ignoreNull 
 * and ignoreMissingFields altered to true.
 * <pre>
 * JSONCodecConfiguration cfg = new JSONCodecConfiguration()
 *     .ignoreNull(true)
 *     .ignoreMissingFields(true);
 * </pre>
 * 
 * <h3>Multi-Threading</h3>
 * Immutable.
 * 
 * @author homac
 *
 */
public class JSONCodecConfiguration {

	/** Character encoding of the JSON strings read from input streams.
	 * <br/><b>Default:</b>  {@link JSONDefaults#CHARSET} */
	public final Charset charset;
	
	/** Codec removes all members with null values in a given object during encoding/decoding.
	 * <br/><b>Default:</b>  {@link JSONDefaults#IGNORE_NULL} */
	public final boolean ignoreNull;
	
	/** Codec ignores fields in the JSON string, that do not exist in the corresponding Java class.
	 * <br/><b>Default:</b>  {@link JSONDefaults#IGNORE_MISSING_FIELDS} */
	public final boolean ignoreMissingFields;
	
	/** Considers the special attribute <code>class</code> in a JSON object 
	 * as type of the compound type which contains it - much like the static member <code>class</code> of each Java object.
	 * <br/><b>Default:</b>  {@link JSONDefaults#SUPPORT_CLASS_ATTRIB} */
	public final boolean supportClassAttribute;

	/** Defines the parser factory to be used to create instances of {@link JSONParser}.
	 * <br/><b>Default:</b>  {@link JSONDefaults#PARSER_FACTORY} */
	public final JSONParserFactory parserFactory;
	
	/** Defines the default {@link JSONFormatter} used to render JSON strings. 
	 * <br/><b>Default:</b>  {@link JSONDefaults#FORMATTER} */
	public final JSONFormatter formatter;
	


	public JSONCodecConfiguration() {
		this.charset = CHARSET;
		this.ignoreNull = IGNORE_NULL;
		this.ignoreMissingFields = IGNORE_MISSING_FIELDS;
		this.supportClassAttribute = SUPPORT_CLASS_ATTRIB;
		this.parserFactory = PARSER_FACTORY;
		this.formatter = FORMATTER;
	}
		
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
