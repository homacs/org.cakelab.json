package org.cakelab.json.codec;

public class CodecBase {
	
	protected JSONCodecConfiguration cfg;
	
	protected CodecBase(JSONCodecConfiguration cfg) {
		this.cfg = new JSONCodecConfiguration(cfg);
	}

}
