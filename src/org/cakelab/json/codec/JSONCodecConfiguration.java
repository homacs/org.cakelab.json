package org.cakelab.json.codec;

import java.nio.charset.Charset;

/**
 * Configuration of the JSON codec (see {@link JSONCodec}).
 * @author homac
 *
 */
public class JSONCodecConfiguration {
	/** character encoding of the json string.
	 * <p><b>Default:</b> <em>Charset.defaultCharset()</em></p>*/
	public Charset charset					         = Charset.defaultCharset();
	
	/** removes all members with null values in a given object during conversion into a string.
	 * <p><b>Default:</b> <em>false</em></p>*/
	public boolean ignoreNull 				         = false;
	
	/** ignores fields that exist in the json string but not in the corresponding java class.
	 * <p><b>Default:</b> <em>false</em></p>*/
	public boolean ignoreMissingFields		         = false;
	
	/** considers the special member "class" in a JSON Object as type of the compound type 
	 * which contains it - much like the static member 'class' of each Java class.
	 * <p><b>Default:</b> <em>false</em></p>*/
	public boolean considerClassAttribute	         = false;

	/** defines the default format used to render JSON strings. 
	 * <p><b>Default:</b> <em>new JSONStringFormatterConfiguration()</em></p>
	 * @see {@link JSONStringFormatterConfiguration}*/
	public JSONStringFormatterConfiguration format   = new JSONStringFormatterConfiguration();
	
	/**
	 * Configuration:
	 * <ul>
	 * <li>{@link #charset} = charset</li>
	 * <li>{@link #ignoreNull} = ignoreNull</li>
	 * <li>{@link #ignoreMissingFields} = ignoreMissingFields</li>
	 * <li>{@link #considerClassAttribute} = considerClassAttribute</li>
	 * </ul>
	 */
	public JSONCodecConfiguration(Charset charset, boolean ignoreNull, boolean ignoreMissingFields, boolean considerClassAttribute) {
		this.charset = charset;
		this.ignoreNull = ignoreNull;
		this.ignoreMissingFields = ignoreMissingFields;
		this.considerClassAttribute = considerClassAttribute;
	}
	
	/**
	 * Configuration:
	 * <ul>
	 * <li>{@link #charset} = charset</li>
	 * <li>{@link #ignoreNull} = ignoreNull</li>
	 * <li>{@link #ignoreMissingFields} = ignoreMissingFields</li>
	 * <li>{@link #considerClassAttribute} = false</li>
	 * </ul>
	 */
	public JSONCodecConfiguration(Charset charset, boolean ignoreNull, boolean ignoreMissingFields) {
		this(charset, ignoreNull, ignoreMissingFields, false);
	}
	
	/**
	 * Configuration:
	 * <ul>
	 * <li>{@link #charset} = charset</li>
	 * <li>{@link #ignoreNull} = ignoreNull</li>
	 * <li>{@link #ignoreMissingFields} = false</li>
	 * <li>{@link #considerClassAttribute} = false</li>
	 * </ul>
	 */
	public JSONCodecConfiguration(Charset charset, boolean ignoreNull) {
		this(charset, ignoreNull, false, false);
	}
	
	/**
	 * Configuration:
	 * <ul>
	 * <li>{@link #charset} = charset</li>
	 * <li>{@link #ignoreNull} = false</li>
	 * <li>{@link #ignoreMissingFields} = false</li>
	 * <li>{@link #considerClassAttribute} = false</li>
	 * </ul>
	 */
	public JSONCodecConfiguration(Charset charset) {
		this(charset, false, false, false);
	}
	
	/**
	 * Configuration:
	 * <ul>
	 * <li>{@link #charset} = {@link Charset#defaultCharset()}</li>
	 * <li>{@link #ignoreNull} = ignoreNull</li>
	 * <li>{@link #ignoreMissingFields} = ignoreMissingFields</li>
	 * <li>{@link #considerClassAttribute} = considerClassAttribute</li>
	 * </ul>
	 */
	public JSONCodecConfiguration(boolean ignoreNull, boolean ignoreMissingFields, boolean considerClassAttribute) {
		this(Charset.defaultCharset(), ignoreNull, ignoreMissingFields, considerClassAttribute);
	}
	
	/**
	 * Configuration:
	 * <ul>
	 * <li>{@link #charset} = {@link Charset#defaultCharset()}</li>
	 * <li>{@link #ignoreNull} = ignoreNull</li>
	 * <li>{@link #ignoreMissingFields} = ignoreMissingFields</li>
	 * <li>{@link #considerClassAttribute} = false</li>
	 * </ul>
	 */
	public JSONCodecConfiguration(boolean ignoreNull, boolean ignoreMissingFields) {
		this(Charset.defaultCharset(), ignoreNull, ignoreMissingFields, false);
	}
	
	/**
	 * Configuration:
	 * <ul>
	 * <li>{@link #charset} = {@link Charset#defaultCharset()}</li>
	 * <li>{@link #ignoreNull} = ignoreNull</li>
	 * <li>{@link #ignoreMissingFields} = false</li>
	 * <li>{@link #considerClassAttribute} = false</li>
	 * </ul>
	 */
	public JSONCodecConfiguration(boolean ignoreNull) {
		this(Charset.defaultCharset(), ignoreNull, false, false);
	}
	
	/**
	 * Configuration:
	 * <ul>
	 * <li>{@link #charset} = {@link Charset#defaultCharset()}</li>
	 * <li>{@link #ignoreNull} = false</li>
	 * <li>{@link #ignoreMissingFields} = false</li>
	 * <li>{@link #considerClassAttribute} = false</li>
	 * </ul>
	 */
	public JSONCodecConfiguration() {
		this(Charset.defaultCharset(), false, false, false);
	}
	
	
}
