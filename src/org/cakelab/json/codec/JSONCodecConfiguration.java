package org.cakelab.json.codec;

import java.nio.charset.Charset;

public class JSONCodecConfiguration {
	public Charset charset					= Charset.defaultCharset();
	public boolean ignoreNull 				= false;
	public boolean ignoreMissingFields		= false;
	public boolean considerClassAttribute	= false;
}
