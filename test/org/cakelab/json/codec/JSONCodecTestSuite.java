package org.cakelab.json.codec;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.cakelab.json.JSONException;
import org.cakelab.json.codec.samples.A;
import org.cakelab.json.codec.samples.ArrayMembers;
import org.cakelab.json.codec.samples.ObjectInObject;
import org.cakelab.json.codec.samples.PolymorphismUser;
import org.cakelab.json.codec.samples.Primitives;
import org.cakelab.json.codec.samples.SampleString;
import org.cakelab.json.codec.samples.SimpleEnum;
import org.junit.jupiter.api.Test;

public abstract class JSONCodecTestSuite {

	
	static JSONCodec codec; 
	
	
	@Test
	public void testEnum() throws JSONException {
		SimpleEnum expected = SimpleEnum.TWO;
		JSONCodec codec = new JSONCodec(new JSONCodecConfiguration().supportClassAttribute(true));
		assertEncodeDecode(expected, codec);
	}
	
	@Test
	public void testUnicode() throws JSONException {
		SampleString ts = new SampleString();
		String expected = "-XX:ParallelGCThreads\u003d8";
		ts.setS(expected);

	
		JSONCodec codec = new JSONCodec(new JSONCodecConfiguration());
		String encoded = codec.encodeObject(ts);
		ts = (SampleString) codec.decodeObject(encoded, SampleString.class);
		
		assertEquals(expected, ts.getS());
	}


	@Test
	public void testPolymorphism() throws JSONException {
		/* given an object with reference on A while reference is of type SuperClass */
		PolymorphismUser object = new PolymorphismUser();
		object.ref_on_A_or_B = new A();

		/* configure and instantiate a codec which supports the "class" attribute. */
		JSONCodecConfiguration cfg = new JSONCodecConfiguration().supportClassAttribute(true);
		JSONCodec codec = new JSONCodec(cfg);

		/* encode object into a json string */
		String json = codec.encodeObject(object);

		/* and decode it again using a codec with the same configuration */
		PolymorphismUser o2 = (PolymorphismUser) codec.decodeObject(json, PolymorphismUser.class);

		/* member o2.ref_on_A_or_B is of type A again. */
		assertEquals(object.ref_on_A_or_B.getClass(), o2.ref_on_A_or_B.getClass());
	}


	@Test
	public void testArrayMembers() throws JSONException {
		ArrayMembers o = new ArrayMembers();
		assertEncodeDecode(o);
	}

	@Test
	public void testObjectInObject() throws JSONException {
		ObjectInObject o = new ObjectInObject();
		assertEncodeDecode(o);
	}

	@Test
	public void testPrimitives() throws JSONException {
		Primitives o = new Primitives();
		assertEncodeDecode(o);
	}

	protected void assertEncodeDecode(Object o) throws JSONException {
		assertEncodeDecode(o, codec);
	}


	protected void assertEncodeDecode(Object o, JSONCodec codec) throws JSONException {
		String json = codec.encodeObject(o);

		// encode to new
		Object target = codec.decodeObject(json, o.getClass());
		assertEquals(json, codec.encodeObject(target));
		assertEquals(o, target);
		
		// encode to existing
		Object inplaceTarget = codec.decodeObject(json, target);
		assertTrue(inplaceTarget == target);
		assertEquals(json, codec.encodeObject(target));
		assertEquals(o, target);
	}





	
}
