package org.cakelab.json.util.unsafe;

public interface Unsafe {

	static Unsafe theUnsafe = UnsafeHolder.theUnsafe;
	
    /** Allocate an instance but do not run any constructor.
    	Initializes the class if it has not yet been. */
	Object allocateInstance(Class<?> cls) throws InstantiationException;
	

}
