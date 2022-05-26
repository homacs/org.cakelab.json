package org.cakelab.json.codec;

import org.cakelab.json.JSONException;

/**
 * <h3>Warning: Work in progress</h3>
 * 
 * This is an early prototype of a way to add own type mappings to the json modeller.
 * 
 * An implementation of this abstract class can be added to the {@link JSONCodecConfiguration}
 * as 'mapping'. A provided mapping gets priority over generic mappings.
 * 
 * Currently, custom mappings can be provided for any combination of Java and JSON type. However 
 * JSON types are limited to the value types of the JSON model, such as JSONObject, JSONArray,
 * Long and Double for JSON numbers, String or Boolean.
 * 
 * A mapping rule is always selected by its Java type first. However, there can exist multiple mappings
 * for the same type but different JSON types. 
 * 
 * When encoding to JSON, the modeller will then select one by random (since they are stored in a hash map).
 * 
 * When decoding from JSON to Java, the modeller will lookup the mapping with matching 
 * tuple of Java and JSON type.
 * 
 * @author homac
 *
 * @param <JAVAT> Java type
 * @param <JSONT> JSON type
 */
public abstract class JSONMapping<JAVAT, JSONT> {
	
	public final Class<JAVAT> javaType;
	public final Class<JSONT> jsonType;
	
	public JSONMapping(Class<JAVAT> javaType, Class<JSONT> jsonType) {
		this.javaType = javaType;
		this.jsonType = jsonType;
	}
	
	/** Implementation maps given javaValue to a valid JSON value. 
	 * @throws JSONException */
	public abstract JSONT toJson(JSONModeller modeller, JAVAT javaValue) throws JSONException;

	/** Instantiate a new Java object of type JAVAT and initialise it from JSON value of type JSONT.
	 * In case the Java value is a constant (such as an enum value) it is suggested 
	 * to return the matching constant instead. */
	public abstract JAVAT toJava(JSONModeller modeller, JSONT jsonValue) throws JSONException;
	
	/** Initialise given javaValue from give jsonValue.  */
	public abstract void toJava(JSONModeller modeller, JSONT jsonAny, JAVAT targetObject) throws JSONException;
}
