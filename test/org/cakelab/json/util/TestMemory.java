package org.cakelab.json.util;

import static org.junit.jupiter.api.Assertions.*;

import org.cakelab.json.codec.samples.Version;
import org.junit.jupiter.api.Test;

class TestMemory {


	@Test
	void testNewInstance() throws InstantiationException {
		Version v = Memory.newInstance(Version.class);
		
		assertNotNull(v);
		assertEquals(Version.class, v.getClass());
		assertEquals("0.0", v.toString());
	}

}
