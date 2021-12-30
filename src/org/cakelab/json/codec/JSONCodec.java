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
 * of Java classes to and from JSON strings.
 * 
 * <h3>Multi-Threading</h3>
 * Not thread safe.
 * 
 * @author homac
 *
 */
public class JSONCodec extends CodecBase {
	

	private JSONParser parser;
	private JSONModeller modeller;
	
	/**
	 * Constructor using the default configuration.
	 * @see {@link JSONCodecConfiguration}
	 * @see {@link JSONDefaults#CODEC_CONFIG}
	 */
	public JSONCodec() {
		this(CODEC_CONFIG);
	}

	/** Constructor using a specific configuration.
	 * @param config Configuration to be used by the codec.
	 */
	public JSONCodec(JSONCodecConfiguration config) {
		super(config);
		this.parser = cfg.parserFactory.create(cfg.ignoreNull);
		this.modeller = new JSONModeller(config);
	}
	
	/** decodes the given json string into the given target object. 
	 * @throws JSONException 
	 */
	public Object decodeObject(String jsonString, Object targetObject) throws JSONException {
		JSONObject json = parser.parseObject(jsonString);
		return modeller._decodeObject(json, targetObject);
	}
	
	/** decodes the given json string from input stream into the given target object. 
	 * @throws JSONException 
	 */
	public Object decodeObject(InputStream inputStream, Object targetObject) throws JSONException {
		JSONObject json = parser.parseObject(inputStream, cfg.charset);
		return modeller._decodeObject(json, targetObject);
	}

	
	/** decodes the given json string from input stream into an object of the given target type. 
	 * @throws JSONException 
	 */
	public Object decodeObject(InputStream inputStream, Class<?> targetType)  throws JSONException {
		try {
			JSONObject json = parser.parseObject(inputStream, cfg.charset);
			
			return modeller._decodeObject(json, targetType);
		} catch (JSONException e) {
			throw e;
		} catch (Exception e) {
			throw new JSONException(e);
		}
	}


	
	/** decodes the given json string into an object of the given target type. 
	 * @throws JSONException 
	 */
	public <T> T decodeObject(String jsonString, Class<T> targetType) throws JSONException {

		try {
			JSONParser parser = cfg.parserFactory.create(cfg.ignoreNull);
			JSONObject jsonObject = parser.parseObject(jsonString);
			
			return modeller._decodeObject(jsonObject, targetType);
		} catch (JSONException e) {
			throw e;
		} catch (Exception e) {
			throw new JSONException(e);
		}
	}
	
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

	public void encodeObject(Object o, OutputStream out) throws JSONException {
		Object json;
		
		if (o == null) {
			throw new JSONException("Cannot encode a toplevel null object");
		}
		
		if (o instanceof JSONCompoundType) {
			json = o;
		} else {
			json = modeller.encodeObjectJSON(o, null);
		}
		
		cfg.formatter.format(out, json);
	}

}
