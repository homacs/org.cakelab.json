package org.cakelab.json.util.unsafe;

import java.io.ObjectStreamClass;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class DalvikPostGingerBred extends Unsupported {
	
	private Method newInstance;
	private Integer constructorId;



	DalvikPostGingerBred() throws Throwable {
		Method getConstructorId = ObjectStreamClass.class
				.getDeclaredMethod("getConstructorId", Class.class);
		getConstructorId.setAccessible(true);
		constructorId = (Integer) getConstructorId.invoke(null,
				Object.class);
		newInstance = ObjectStreamClass.class
				.getDeclaredMethod("newInstance", Class.class, int.class);
		newInstance.setAccessible(true);
	}

	public static Unsafe create() {
		try {
			return new DalvikPostGingerBred();
		} catch (Throwable e) {
			return null;
		}
	}
	
	
	
	@Override
	public Object allocateInstance(Class<?> cls) throws InstantiationException {
		try {
			return newInstance.invoke(null, cls, constructorId);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			throw exception(e, InstantiationException.class);
		}
	}
	
	
}
