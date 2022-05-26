package org.cakelab.json.codec;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.cakelab.json.JSONException;
import org.cakelab.json.codec.samples.Version;
import org.junit.Test;

public class TestJSONMapping {
	
	private static JSONMapping<Version, String> mapping = new JSONMapping<Version, String>(Version.class, String.class) {

		@Override
		public String toJson(JSONModeller modeller, Version javaValue) {
			return javaValue.toString();
		}

		@Override
		public Version toJava(JSONModeller modeller, String jsonValue) {
			return new Version(jsonValue);
		}

		@Override
		public void toJava(JSONModeller modeller, String jsonAny, Version javaValue) {
			javaValue.set(jsonAny);
		}
	};
	
	@Test
	public void testJSONMappingMap() throws JSONException {
		JSONMappingMap orig = new JSONMappingMap();
		JSONModeller modeller = new JSONModeller(new JSONCodecConfiguration());
		JSONMappingMap newSet = orig.add(mapping);
		assertNotNull(newSet);
		
		assertTrue(orig != newSet);
		
		JSONMapping<Version, String> m = orig.get(mapping.javaType);
		assertNull(m);
		
		m = newSet.get(mapping.javaType);
		assertNotNull(m);
		
		m = newSet.get(mapping.javaType, mapping.jsonType);
		assertNotNull(m);

		Version javaValue = m.toJava(modeller, "1.0");
		String jsonValue = m.toJson(modeller, javaValue);
		assertNotNull(jsonValue);
	}

	@Test
	public void testJSONCodecCfgMapping () {
		JSONCodecConfiguration orig = new JSONCodecConfiguration();
		JSONCodecConfiguration mod = orig.mapping(mapping);
		
		assertTrue(orig != mod);
		assertTrue(orig.mapping != mod.mapping);
		
		
	}
	@Test
	public void testJSONCodecWithMapping () throws JSONException {
		JSONCodecConfiguration config = new JSONCodecConfiguration().mapping(mapping);
		JSONModeller modeller = new JSONModeller(config);
		
		String versionNumber = "1.0";
		Version expected = new Version(versionNumber);
		Version decoded = modeller.toJavaObject(versionNumber, Version.class);
		
		assertEquals(expected, decoded);
		
	}
}
