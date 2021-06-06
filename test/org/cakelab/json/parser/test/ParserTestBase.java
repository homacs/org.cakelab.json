package org.cakelab.json.parser.test;

import java.io.IOException;

import org.cakelab.json.JSONArray;
import org.cakelab.json.JSONException;
import org.cakelab.json.JSONObject;
import org.cakelab.json.parser.Parser;
import org.cakelab.json.parser.ParserFactory;

public class ParserTestBase {

	private static String testName;
	private static ParserFactory factory;
	
	
	static {
	    boolean hasAssertEnabled = false;
	    assert hasAssertEnabled = true;
	    if(!hasAssertEnabled){
	    	throw new RuntimeException("Asserts (argument -ea) has to be enabled for json tests.");
	    }
	}

	
	public static void setParserFactory(ParserFactory factory) {
		ParserTestBase.factory = factory;
	}
	
	public static void testAll() {
		test("{}", "toplevel object", true);
		test("{ \t\n}   \r\f", "whitespaces", true);
		test("{ \"empty\" : }", "empty value", false);
		test("{ \"exists\" : 1 , }", "missing NV pair", false);
		test("{ \"null\" : null}", "null parse test", true);
		test("{ \"true\" : true, \"false\" : false}", "boolean", true);
		testNumber();
		testString();
		test("{ \"one\" : 1, \"two\" : 2, \"_three\" : 3, \"four_4\" : 4 }", "legal names", true);
		test("{ \"1ins\" : 1", "illegal name", false);
		test("{ \"array\" : [] }", "empty array", true);
		test("{ \"array\" : [,] }", "empty array elements", false);
		test("{ \"array\" : [d,t] }", "illegal array elements", false);
		test("{ \"eins\" : { \"zwei\" : {}}, \"drei\" : {} }", "nested objects", true);
		testComplexObject();
		
		testValues();
	}
	
	
	private static void testValues() {
		testName = "testValues";
		try {
			testValue(1.0);
			testValue(true);
			testValue(false);
			testValue(null);
			
			pass();
		} catch (Throwable t) {
			fail(t);
		}
	}
	
	private static void testValue(Object expect) throws IOException, JSONException {
		Parser p = factory.create();
		Object value = p.parse(String.valueOf(expect));
		assert(expect == value || expect.equals(value));
		
	}


	private static void testComplexObject() {
		testName = "complex object";
		String jsonString = "{"
				+ "\"null_value\": null, \n" 
				+ "\"nv1\": 1 ,\n" 
				+ "\"a1\": [1,2], \n" 
				+ "\"array_with_object\": [3,{\"nv2\": 2}], \n" 
				+ "\"array_with_arrays\": [[4,5],[6]], \n" 
				+ "\"object_with_array\": {\"a2\":[7,8]}, \n" 
				+ "\"nv2\": 2 \n" 
				+ "}";
		try {
			Parser p = factory.create();
			JSONObject o = p.parseObject(jsonString);
			
			p = factory.create();
			o = p.parseObject(o.toString());
			
			o.toString();
			
			assert(o.get("null_value") == null);
			
			assert(o.getDouble("nv1") == 1.0);

			JSONArray a = (JSONArray) o.get("a1");
			assert(a.getDouble(0) == 1);
			assert(a.getDouble(1) == 2);
			
			a = (JSONArray) o.get("array_with_object");
			assert(a.getDouble(0) == 3);
			JSONObject o_nested = (JSONObject) a.get(1);
			assert(o_nested.getDouble("nv2") == 2);

			a = (JSONArray) o.get("array_with_arrays");
			JSONArray a_nested = (JSONArray) a.get(0);
			assert(a_nested.getDouble(0) == 4);
			assert(a_nested.getDouble(1) == 5);
			a_nested = (JSONArray) a.get(1);
			assert(a_nested.getDouble(0) == 6);

			o_nested = (JSONObject) o.get("object_with_array");
			a_nested = (JSONArray)o_nested.get("a2");
			assert(a_nested.getDouble(0) == 7);
			assert(a_nested.getDouble(1) == 8);
			
			pass();
		} catch (Throwable t) {
			fail(t);
		}
		
	}

	private static void testString() {
		testName = "string values";
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
		try {
			Parser p = factory.create();
			
			JSONObject o = p.parseObject(jsonString);
			p = factory.create();
			o = p.parseObject(o.toString());
			
			assert(((String) o.get("empty")).equals(""));
			assert(((String) o.get("common")).equals(common));
			assert(((String) o.get("unicode")).equals(unicodeDecoded));
			assert(((String) o.get("control")).equals(controlDecoded));
			assert(((String) o.get("url")).equals(url));
			pass();
		} catch (Throwable t) {
			fail(t);
		}
			
		
	}

	private static void testNumber() {
		testName = "number values";
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
				
		try {
			Parser p = factory.create();
			
			JSONObject o = p.parseObject(jsonString);
			p = factory.create();
			o = p.parseObject(o.toString());
			
			assert(o.getDouble("n1") == Double.MIN_VALUE);
			assert(o.getDouble("n2") == Double.MAX_VALUE);
			assert(o.getDouble("n3") == -1.0);
			assert(o.getDouble("n4") == +1.0);
			assert(o.getLong("n5") == -1);
			assert(o.getLong("n6") == +1);
			assert(o.getDouble("n7") == n7);
			pass();
		} catch (Throwable t) {
			fail(t);
		}
				
	}

	private static void test(String jsonString, String name, boolean expectedSuccess) {
		try {
			testName = name;
			Parser p = factory.create();
			
			JSONObject o = p.parseObject(jsonString);
			// reverse test
			p = factory.create();
			o = p.parseObject(o.toString());
			if (expectedSuccess) pass();
			else fail(null);
		} catch (Throwable t) {
			if (expectedSuccess) fail(t);
			else pass();
		}
	}

	private static void fail(Throwable t) {
		if (t != null) {
			System.err.println(testName + ": failed: " + t.getMessage());
			t.printStackTrace();
		} else {
			System.err.println(testName + ": failed: was expected to fail");
		}
	}

	private static void pass() {
		System.out.println(testName + ": passed");
	}
	
	
}
