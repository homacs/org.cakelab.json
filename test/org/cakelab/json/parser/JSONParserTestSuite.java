package org.cakelab.json.parser;

import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;

import org.cakelab.json.JSONArray;
import org.cakelab.json.JSONException;
import org.cakelab.json.JSONObject;
import org.cakelab.json.format.JSONFormatter;
import org.cakelab.json.format.JSONFormatterConfiguration;
import org.junit.jupiter.api.Test;

public abstract class JSONParserTestSuite {

	protected static JSONParser parser;
	protected static JSONFormatter formatter;
	protected static JSONFormatterConfiguration formatterConfig;
	
	@Test
	public void testObject() throws Throwable {
		testPassFail(true,  "{}", "toplevel object");
		testPassFail(true,  "{ \t\n}   \r\f", "whitespaces");
		testPassFail(false, "{ \"empty\" : }", "empty value");
		testPassFail(false, "{ \"exists\" : 1 , }", "missing NV pair");
		testPassFail(true,  "{ \"null\" : null}", "null parse test");
		testPassFail(true,  "{ \"true\" : true, \"false\" : false}", "boolean");
		testPassFail(true,  "{ \"one\" : 1, \"two\" : 2, \"_three\" : 3, \"four_4\" : 4 }", "legal names");
		testPassFail(false, "{ \"1ins\" : 1", "illegal name");
		testPassFail(true,  "{ \"array\" : [] }", "empty array");
		testPassFail(false, "{ \"array\" : [,] }", "empty array elements");
		testPassFail(false, "{ \"array\" : [d,t] }", "illegal array elements");
		testPassFail(true,  "{ \"eins\" : { \"zwei\" : {}}, \"drei\" : {} }", "nested objects");
	}
	
	@Test
	public void testValues() throws IOException, JSONException {
		testPrimitiveValue("String");
		testPrimitiveValue(1.0);
		testPrimitiveValue(2L);
		testPrimitiveValue(true);
		testPrimitiveValue(false);
		testPrimitiveValue(null);
	}
	
	
	@Test
	public void testComplexObject() throws IOException, JSONException {
		String jsonString = "{"
				+ "\"null_value\": null, \n" 
				+ "\"nv1\": 1 ,\n" 
				+ "\"a1\": [1,2], \n" 
				+ "\"array_with_object\": [3,{\"nv2\": 2}], \n" 
				+ "\"array_with_arrays\": [[4,5],[6]], \n" 
				+ "\"object_with_array\": {\"a2\":[7,8]}, \n" 
				+ "\"nv2\": 2 \n" 
				+ "}";
		
		JSONParser p = parser;
		JSONObject o = p.parseObject(jsonString);
		
		p = parser;
		o = p.parseObject(o.toString());
		
		o.toString();
		
		assertEquals(o.get("null_value"), null);
		
		assertEquals(o.getDouble("nv1"), 1.0);

		JSONArray a = (JSONArray) o.get("a1");
		assertEquals(a.getDouble(0), 1);
		assertEquals(a.getDouble(1), 2);
		
		a = (JSONArray) o.get("array_with_object");
		assertEquals(a.getDouble(0), 3);
		JSONObject o_nested = (JSONObject) a.get(1);
		assertEquals(o_nested.getDouble("nv2"), 2);

		a = (JSONArray) o.get("array_with_arrays");
		JSONArray a_nested = (JSONArray) a.get(0);
		assertEquals(a_nested.getDouble(0), 4);
		assertEquals(a_nested.getDouble(1), 5);
		a_nested = (JSONArray) a.get(1);
		assertEquals(a_nested.getDouble(0), 6);

		o_nested = (JSONObject) o.get("object_with_array");
		a_nested = (JSONArray)o_nested.get("a2");
		assertEquals(a_nested.getDouble(0), 7);
		assertEquals(a_nested.getDouble(1), 8);
		
	}

	@Test
	public void testString() throws IOException, JSONException {
		String unicode = "\\u2000";
		String unicodeDecoded = "\u2000";
		String common = "blah blah";
		String control = "\\\" \\\\ \\/ \\b \\f \\n \\r \\t";
		String controlDecoded = "\" \\ / \b \f \n \r \t";
		String url = "http://json.cakelab.org";
		String jsonString = "{"
				+ "\"empty\": \"" + "\",\n" 
				+ "\"common\": \"" + common +"\", \n" 
				+ "\"unicode\": \"" + unicode + "\", \n" 
				+ "\"control\": \""+ control + "\", \n" 
				+ "\"url\": \""+ url + "\" \n" 
				+ "}";

	
		JSONObject o = parser.parseObject(jsonString);
		o = parser.parseObject(o.toString());
		
		assert(((String) o.get("empty")).equals(""));
		assert(((String) o.get("common")).equals(common));
		assert(((String) o.get("unicode")).equals(unicodeDecoded));
		assert(((String) o.get("control")).equals(controlDecoded));
		assert(((String) o.get("url")).equals(url));
	}

	@Test
	public void testNumber() throws IOException, JSONException {
		double n7 = 0e-10000;
		String jsonString = "{"
				+ "\"n1\": " + Double.MIN_VALUE + ",\n" 
				+ "\"n2\": " + Double.MAX_VALUE + ",\n" 
				+ "\"n3\": " + -1.0 + ",\n" 
				+ "\"n4\": " + +1.0 + ",\n" 
				+ "\"n5\": " + -1 + ",\n" 
				+ "\"n6\": " + +1 + ",\n" 
				+ "\"n7\": " + n7 + "\n"
				+ "}";

		
		JSONObject o = parser.parseObject(jsonString);
		o = parser.parseObject(o.toString());
		
		assertEquals(o.getDouble("n1"), Double.MIN_VALUE);
		assertEquals(o.getDouble("n2"), Double.MAX_VALUE);
		assertEquals(o.getDouble("n3"), -1.0);
		assertEquals(o.getDouble("n4"), +1.0);
		assertEquals(o.getLong("n5"), -1);
		assertEquals(o.getLong("n6"), +1);
		assertEquals(o.getDouble("n7"), n7);
	}

	@Test
	public void testUtf8 () throws JSONException {

		testPrimitiveValue("\u0001");
		testPrimitiveValue("\u0010");
		testPrimitiveValue("\u0100");
		testPrimitiveValue("\u1000");
		testPrimitiveValue("äÄöÖüÜß²³'áéúíàèùì");
	}
	
	
	protected void testPassFail(boolean expectSuccess, String jsonString, String name)  {
		try {
			JSONObject jsonObject = parser.parseObject(jsonString);
			String jsonString2 = formatter.format(jsonObject);
			// reverse test
			jsonObject = parser.parseObject(jsonString2);
			if (!expectSuccess) fail(name + ": expected to fail");
		} catch (JSONException t) {
			if (expectSuccess) fail(name + ": expected to succeed");
		}
	}

	/** expect can be one of the supported primitive types:
	 * Boolean (true|false)
	 * Double
	 * Long
	 * String
	 * null
	 */
	private void testPrimitiveValue(Object expect) throws JSONException {
		String jsonString = formatter.format(expect);
		Object value = parser.parse(jsonString);
		assertTrue(expect == value || expect.equals(value));
	}
	
}
