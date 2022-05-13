package org.cakelab.json.util.unsafe;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class SunMiscUnsafe extends UnsafeBase {
	
	private Object unsafe;
	private Method allocateInstance;

	SunMiscUnsafe() throws Exception {
		Class<?> unsafeClass = Class.forName("sun.misc.Unsafe");
		Field f = unsafeClass.getDeclaredField("theUnsafe");
		f.setAccessible(true);
		unsafe = f.get(null);
		allocateInstance = unsafeClass.getMethod("allocateInstance", Class.class);
	}
	
	@Override
	public Object allocateInstance(Class<?> cls) throws InstantiationException {
		try {
			return allocateInstance.invoke(unsafe, cls);
		} catch (InvocationTargetException | IllegalAccessException | IllegalArgumentException e) {
			throw exception(e, InstantiationException.class);
		}
	}

}
