package org.cakelab.json.parser.jni;

public class Library {
	
	
	static final boolean AVAILABLE;
	static final String LIBRARY_PATH = "/home/homac/aaa/repos/git/cakelab.org/playground/org.cakelab.json.c/lib/libcakelab-json.so";
	
	static {
		boolean available = false;
		try {
			System.load(LIBRARY_PATH);
			available = true;
		} catch (Throwable t) {
			System.err.println("error loading native library " + LIBRARY_PATH);
		}
		AVAILABLE = available;
	}

	public static void load() {
		// intentionally empty
	}
	
	
	
}
