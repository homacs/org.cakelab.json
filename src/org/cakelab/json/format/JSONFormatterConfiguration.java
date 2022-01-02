package org.cakelab.json.format;

import static org.cakelab.json.JSONDefaults.CHARSET;
import static org.cakelab.json.JSONDefaults.IGNORE_NULL;
import static org.cakelab.json.JSONDefaults.SORT_MEMBERS;
import static org.cakelab.json.JSONDefaults.WRITE_UNICODE_VALUES;

import java.nio.charset.Charset;

import org.cakelab.json.JSONDefaults;

/**
 * Configuration for JSONFormatter instances.
 * 
 * <h3>Multi-Threading</h3>
 * Immutable.
 * 
 * @author homac
 *
 */
public class JSONFormatterConfiguration {
	
	/** Charset for encoding */
	public final Charset charset;

	/** sorts members of compound types when converting objects into a json string.
	 * <p><b>Default:</b> <em>true</em></p>*/
	public final boolean sortMembers;
	
	/** Uses unicode conform encoding of special characters (i.e. &#92;u&lt;UNICODE_CODEPOINT&gt;) 
	 * when converting objects into strings. 
	 * This is especially required for character encodings, with limited 
	 * range such as ISO-8859 (has just 8bits).
	 * <p><b>Default:</b> {@link JSONDefaults#UNICODE_VALUES}</p>*/
	public final boolean unicodeValues;
	
	/** will not write entries of objects, which have a null value. */
	public final boolean ignoreNull;
	
		
	public JSONFormatterConfiguration(JSONFormatterConfiguration that) {
		this.charset = that.charset;
		this.sortMembers = that.sortMembers;
		this.unicodeValues = that.unicodeValues;
		this.ignoreNull = that.ignoreNull;
	}
	
	/** create a default configuration similar to {@link JSONDefaults#FORMATTER_CONFIG}
	 * @see JSONDefaults
	 */
	public JSONFormatterConfiguration() {
		this(CHARSET, 
				SORT_MEMBERS, 
				WRITE_UNICODE_VALUES,
				IGNORE_NULL);
	}
	
	public JSONFormatterConfiguration(Charset charset, boolean sortMembers, boolean unicodeValues, boolean ignoreNull) {
		this.charset = charset;
		this.sortMembers = sortMembers;
		this.unicodeValues = unicodeValues;
		this.ignoreNull = ignoreNull;
	}

	public JSONFormatterConfiguration charset(Charset charset) {
		return new JSONFormatterConfiguration(
				charset,
				sortMembers,
				unicodeValues,
				ignoreNull);
	}
	
	public JSONFormatterConfiguration sortMembers(boolean sortMembers) {
		return new JSONFormatterConfiguration(
				charset,
				sortMembers,
				unicodeValues,
				ignoreNull);
	}
	
	public JSONFormatterConfiguration unicodeValues(boolean unicodeValues) {
		return new JSONFormatterConfiguration(
				charset,
				sortMembers,
				unicodeValues,
				ignoreNull);
	}
	
	public JSONFormatterConfiguration ignoreNull(boolean ignoreNull) {
		return new JSONFormatterConfiguration(
				charset,
				sortMembers,
				unicodeValues,
				ignoreNull);
	}
	
}
