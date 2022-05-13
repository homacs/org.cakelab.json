package org.cakelab.json.util.unsafe;

public class UnsafeHolder {
	
	public static Unsafe theUnsafe = create();
	
	private UnsafeHolder() {
		// pure static class
	}
	
	
	private static Unsafe create() {
		try {
			return new SunMiscUnsafe();
		} catch (Throwable e) {
		}

		try {
			return new DalvikPreGingerBred();
		} catch (Throwable e) {
		}

		try {
			return new DalvikPostGingerBred();
		} catch (Throwable e) {
		}

		return new Unsupported();
	}

}
