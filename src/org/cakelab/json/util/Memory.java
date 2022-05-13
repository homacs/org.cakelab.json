package org.cakelab.json.util;

import org.cakelab.json.util.unsafe.Unsafe;

/**
 * This class provides a method to allocate memory for objects 
 * of given classes without calling a constructor.
 * 
 * @author homac
 *
 */
public class Memory {
	
	private static Unsafe unsafe = Unsafe.theUnsafe;
	
	
	/** Instantiates an object of type T without calling a constructor. */
	@SuppressWarnings("unchecked")
	public static <T> T newInstance(Class<T> clazz) throws InstantiationException {
		return (T) unsafe.allocateInstance(clazz);
	}
	
}
