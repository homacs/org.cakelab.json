package org.cakelab.json.perf;

import java.io.IOException;
import java.util.Random;
import java.util.Stack;

import org.cakelab.json.JSONArray;
import org.cakelab.json.JSONCompoundType;
import org.cakelab.json.JSONException;
import org.cakelab.json.JSONObject;
import org.cakelab.json.parser.JSONParser;
import org.cakelab.json.parser.JSONParserFactory;

public class ParserPerfBase {

	
	public static void measure(JSONParserFactory factory, String jsonString) {
		int iterations = 1000000;
		
		try {
			long start = System.currentTimeMillis();
			JSONParser p = factory.create();
			Object o = null;
			long probe = start;
			for (int i = 0; i < iterations; i++) {
				String json = "{\"probe\":" + (probe) + ", \"o\":" + jsonString + "}";
				o = p.parse(json);
				probe += (o != null) ? 1 : 0;
			}
			long stop = System.currentTimeMillis();
			System.out.println(factory.getClass().getSimpleName() + ": " + (double)(stop - start)/iterations + " ms/object");
			System.out.println("probe: " + probe );
			
		} catch (IOException | JSONException e) {
			e.printStackTrace();
		}
	}

	static final int MEMBER_OBJECT = 0;
	static final int MEMBER_ARRAY = 1;
	static final int LEAVE_COMPOUND = 2;
	static final int MEMBER_STRING = 3;
	static final int MEMBER_NUMBER = 4;
	static final int MEMBER_BOOL = 5;

	public static String generateJson(int entries) {
		Random rand = new Random();
		
		
		Stack<JSONCompoundType> stack = new Stack<JSONCompoundType>();

		JSONObject bottom = new JSONObject();
		stack.push(bottom);
		JSONCompoundType top = stack.peek();
		
		for (int i = 0; i < entries; i++) {
			switch(rand.nextInt(6)) {
			case MEMBER_OBJECT: // enter new object
				JSONObject o = new JSONObject();
				addMember(top, "object", o);
				stack.push(o);
				break;
			case MEMBER_ARRAY:
				JSONArray a = new JSONArray();
				addMember(top, "array", a);
				stack.push(a);
				break;
			case LEAVE_COMPOUND:
				if (top == bottom) 
					continue;
				else 
					stack.pop();
				break;
			case MEMBER_STRING:
				addMember(top, "string", "\"string\"");
				break;
			case MEMBER_NUMBER:
				addMember(top, "number", 4711.7353);
				break;
			case MEMBER_BOOL:
				addMember(top, "boolean", true);
				break;
			}
			top = stack.peek();
		}
		return bottom.toString();
	}

	private static void addMember(JSONCompoundType top, String name, Object value) {
		if (top instanceof JSONObject) {
			((JSONObject)top).put(name,  value);
		} else {
			((JSONArray)top).add(value);
		}
	}

}
