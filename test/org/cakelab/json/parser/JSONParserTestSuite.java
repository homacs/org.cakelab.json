package org.cakelab.json.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;

import org.cakelab.json.JSONArray;
import org.cakelab.json.JSONException;
import org.cakelab.json.JSONObject;
import org.cakelab.json.format.JSONFormatter;
import org.cakelab.json.format.JSONFormatterConfiguration;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public abstract class JSONParserTestSuite {

	protected static JSONParserFactory parserFactory;
	protected static JSONFormatter formatterFactory;
	protected static JSONFormatterConfiguration formatterConfig;
	
	@Test
	public void testObject() throws Throwable {
		test("{}", "toplevel object", true);
		test("{ \t\n}   \r\f", "whitespaces", true);
		test("{ \"empty\" : }", "empty value", false);
		test("{ \"exists\" : 1 , }", "missing NV pair", false);
		test("{ \"null\" : null}", "null parse test", true);
		test("{ \"true\" : true, \"false\" : false}", "boolean", true);
		test("{ \"one\" : 1, \"two\" : 2, \"_three\" : 3, \"four_4\" : 4 }", "legal names", true);
		test("{ \"1ins\" : 1", "illegal name", false);
		test("{ \"array\" : [] }", "empty array", true);
		test("{ \"array\" : [,] }", "empty array elements", false);
		test("{ \"array\" : [d,t] }", "illegal array elements", false);
		test("{ \"eins\" : { \"zwei\" : {}}, \"drei\" : {} }", "nested objects", true);
	}
	
	@Test
	public void testValues() throws IOException, JSONException {
		testValue(1.0);
		testValue(true);
		testValue(false);
		testValue(null);
	}
	
	
	private void testValue(Object expect) throws IOException, JSONException {
		JSONParser p = parserFactory.create();
		Object value = p.parse(String.valueOf(expect));
		assertTrue(expect == value || expect.equals(value));
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
		
		JSONParser p = parserFactory.create();
		JSONObject o = p.parseObject(jsonString);
		
		p = parserFactory.create();
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

		JSONParser p = parserFactory.create();
		
		JSONObject o = p.parseObject(jsonString);
		p = parserFactory.create();
		o = p.parseObject(o.toString());
		
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

		JSONParser p = parserFactory.create();
		
		JSONObject o = p.parseObject(jsonString);
		p = parserFactory.create();
		o = p.parseObject(o.toString());
		
		assertEquals(o.getDouble("n1"), Double.MIN_VALUE);
		assertEquals(o.getDouble("n2"), Double.MAX_VALUE);
		assertEquals(o.getDouble("n3"), -1.0);
		assertEquals(o.getDouble("n4"), +1.0);
		assertEquals(o.getLong("n5"), -1);
		assertEquals(o.getLong("n6"), +1);
		assertEquals(o.getDouble("n7"), n7);
	}

	protected void test(String jsonString, String name, boolean expectedSuccess) throws Throwable {
		try {
			JSONParser p = parserFactory.create();
			
			JSONObject o = p.parseObject(jsonString);
			// reverse test
			p = parserFactory.create();
			o = p.parseObject(o.toString(formatterFactory));
			if (!expectedSuccess) Assertions.fail();
		} catch (Throwable t) {
			if (expectedSuccess) throw t;
		}
	}

	
}
