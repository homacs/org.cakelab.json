package org.cakelab.json.util.unsafe;

import java.lang.reflect.InvocationTargetException;

public abstract class UnsafeBase implements Unsafe {

	@SuppressWarnings("unchecked")
	static <T extends Throwable> RuntimeException exception(Throwable e, Class<T> expectedType) throws T {
		if (e instanceof InvocationTargetException) {
			Throwable t = ((InvocationTargetException)e).getTargetException();
			if (t.getClass().isAssignableFrom(expectedType)) throw (T)t;
		}
		return exception(e);
	}


	static RuntimeException exception(Throwable e) {
		if (e instanceof RuntimeException) 
			return (RuntimeException)e;
		return new RuntimeException(e);
	}

}
