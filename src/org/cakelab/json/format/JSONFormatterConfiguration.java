package org.cakelab.json.format;

import static org.cakelab.json.JSONDefaults.*;
import java.nio.charset.Charset;

/**
 * Determines configuration of JSON string formatter.
 * @author homac
 *
 * <h3>Multi-Threading</h3>
 * Immutable.
 */
public class JSONFormatterConfiguration {
	

	/** sorts members of compound types when converting objects into a json string.
	 * <p><b>Default:</b> <em>true</em></p>*/
	public final boolean sortMembers;
	
	/** Uses unicode conform encoding of special characters (i.e. &#92;u&lt;UNICODE_NUMBER&gt;) 
	 * when converting objects into strings. 
	 * <p><b>Default:</b> <em>true</em></p>*/
	public final boolean unicodeValues;
	
	/** Charset for encoding */
	public final Charset charset;
	
		
	public JSONFormatterConfiguration(JSONFormatterConfiguration that) {
		this.sortMembers = that.sortMembers;
		this.unicodeValues = that.unicodeValues;
		this.charset = that.charset;
	}
	
	public JSONFormatterConfiguration() {
		this(CHARSET, 
				FORMATTER_SORT_MEMBERS, 
				FORMATTER_UNICODE_VALUES);
	}
	
	public JSONFormatterConfiguration(Charset charset, boolean sortMembers, boolean unicodeValues) {
		this.charset = charset;
		this.sortMembers = sortMembers;
		this.unicodeValues = unicodeValues;
	}

	public JSONFormatterConfiguration charset(Charset charset) {
		return new JSONFormatterConfiguration(
				charset,
				sortMembers,
				unicodeValues);
	}
	
	public JSONFormatterConfiguration sortMembers(boolean sortMembers) {
		return new JSONFormatterConfiguration(
				charset,
				sortMembers,
				unicodeValues);
	}
	
	public JSONFormatterConfiguration unicodeValues(boolean unicodeValues) {
		return new JSONFormatterConfiguration(
				charset,
				sortMembers,
				unicodeValues);
	}
	
	
}
