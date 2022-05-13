package org.cakelab.json.util.unsafe;

import java.io.ObjectInputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class DalvikPreGingerBred extends Unsupported {
	
	private Method newInstance;

	DalvikPreGingerBred() throws Throwable {
		newInstance = ObjectInputStream.class.getDeclaredMethod("newInstance", Class.class, Class.class);
		newInstance.setAccessible(true);
	}

	
	
	@Override
	public Object allocateInstance(Class<?> cls) throws InstantiationException {
		try {
			return newInstance.invoke(null, cls, Object.class);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			throw exception(e, InstantiationException.class);
		}
	}
	
	
}
