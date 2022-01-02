package org.cakelab.json.codec;

import static org.cakelab.json.JSONDefaults.CODEC_CONFIG;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import org.cakelab.json.JSONCompoundType;
import org.cakelab.json.JSONDefaults;
import org.cakelab.json.JSONException;
import org.cakelab.json.JSONObject;
import org.cakelab.json.parser.JSONParser;

/**
 * This class implements serialising/deserialising 
 * of Java objects from and to JSON strings.
 * 
 * <h3>Multi-Threading</h3>
 * Not thread-safe.
 * 
 * @author homac
 *
 */
public class JSONCodec {

	private JSONParser parser;
	private JSONModeller modeller;
	private JSONCodecConfiguration cfg;
	
	/** Constructor using the default configuration.
	 * @see {@link JSONDefaults#CODEC_CONFIG} */
	public JSONCodec() {
		this(CODEC_CONFIG);
	}

	/** Constructor using a specific configuration.
	 * @param cfg {@link JSONCodecConfiguration} to be used by the new JSONCodec.*/
	public JSONCodec(JSONCodecConfiguration cfg) {
		this.cfg = cfg;
		this.parser = cfg.parserFactory.create(cfg.ignoreNull);
		this.modeller = new JSONModeller(cfg);
	}
	
	/** Decodes the given jsonString into the given target javaObject of type T. */
	public <T> T decodeObject(String jsonString, T javaObject) throws JSONException {
		JSONObject json = parser.parseObject(jsonString);
		return modeller.toJavaObject(json, javaObject);
	}
	
	/** Decodes the given JSON string from input stream into the given target object of type T. */
	public <T> T decodeObject(InputStream inputStream, T targetObject) throws JSONException {
		JSONObject json = parser.parseObject(inputStream, cfg.charset);
		return modeller.toJavaObject(json, targetObject);
	}

	
	/** Decodes the given JSON string from input stream into a Java object of the given target type T.*/
	public <T> T decodeObject(InputStream inputStream, Class<T> targetType)  throws JSONException {
		try {
			JSONObject json = parser.parseObject(inputStream, cfg.charset);
			
			return modeller.toJavaObject(json, targetType);
		} catch (JSONException e) {
			throw e;
		} catch (Exception e) {
			throw new JSONException(e);
		}
	}

	
	/** Decodes the given JSON string into a Java object of the given target type T. */
	public <T> T decodeObject(String jsonString, Class<T> targetType) throws JSONException {

		try {
			JSONParser parser = cfg.parserFactory.create(cfg.ignoreNull);
			JSONObject jsonObject = parser.parseObject(jsonString);
			
			return modeller.toJavaObject(jsonObject, targetType);
		} catch (JSONException e) {
			throw e;
		} catch (Exception e) {
			throw new JSONException(e);
		}
	}
	
	/** Encodes given Java object into a JSON string, which is returned. */
	public String encodeObject(Object o) throws JSONException {
		try {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			encodeObject(o, out);
			String charset = cfg.formatter.getConfiguration().charset.name();
			return out.toString(charset);
		} catch (Exception e) {
			throw new JSONException(e);
		}
	}

	/** Encodes given Java object into a JSON string, wirtten to out. */
	public void encodeObject(Object javaObject, OutputStream out) throws JSONException {
		Object json;
		
		if (javaObject == null) {
			throw new JSONException("Cannot encode a toplevel null object");
		}
		
		if (javaObject instanceof JSONCompoundType) {
			json = javaObject;
		} else {
			json = modeller.toJSON(javaObject, null);
		}
		
		cfg.formatter.format(out, json);
	}

}
