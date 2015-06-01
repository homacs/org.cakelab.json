package org.cakelab.json.codec;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

public class ReflectionHelper {

	private static final String METHOD_PREFIX_SETTER = "set";
	private static final String METHOD_PREFIX_GETTER = "get";
	private static final String METHOD_PREFIX_ARRAY_SETTER = "aSet";
	private static final String METHOD_PREFIX_ARRAY_GETTER = "aGet";
	private static final String METHOD_PREFIX_ARRAY_ADDER = "aAdd";

	private static HashMap<Class<?>, Vector<Field>> fieldLists = new HashMap<Class<?>, Vector<Field>> ();
	private static HashMap<Class<?>, HashMap<String, Field>> fieldMaps = new HashMap<Class<?>, HashMap<String, Field>> ();
	
	
	public static	List<Field> getDeclaredFields(Class<?> type) {
		Vector<Field> fields = fieldLists.get(type);
		if (fields == null) {
			fields = new Vector<Field>();
			if (!type.equals(Object.class)) {
				List<Field> superFields = getDeclaredFields(type.getSuperclass());
				fields.addAll(superFields);
			}
			for (Field f : type.getDeclaredFields()) {
				fields.add(f);
			}
			fieldLists.put(type, fields);
		}
		return fields;
	}

	public static	HashMap<String, Field> getDeclaredFieldsMap(Class<?> type) {
		HashMap<String, Field> map = fieldMaps.get(type);
		if (map == null) {
			map = new HashMap<String, Field>();
			if (!type.equals(Object.class)) {
				HashMap<String, Field> superFields = getDeclaredFieldsMap(type.getSuperclass());
				map.putAll(superFields);
			}
			for (Field f : type.getDeclaredFields()) {
				map.put(f.getName(), f);
			}
			fieldMaps.put(type,  map);
		}
		return map;
	}


	public static
	boolean isPrimitive(Class<?> type) {
		return type.isPrimitive() || type.equals(String.class)
				 || type.equals(Long.class) || type.equals(Integer.class)
				  || type.equals(Double.class) || type.equals(Float.class)
				   || type.equals(Byte.class) || type.equals(Character.class)
				    || type.equals(Boolean.class);
	}


	public static Field getDeclaredField(Class<? extends Object> class1,
			String name) throws NoSuchFieldException {
		Field f = null;
		try {
			f = class1.getDeclaredField(name);
		} catch (NoSuchFieldException e) {
			if (!class1.getSuperclass().equals(Object.class)) {
				return getDeclaredField(class1.getSuperclass(), name);
			} else {
				throw e;
			}
		}
		return f;
	}


	public static Method findSetter(Object target, Field f) throws JSONCodecException {
		return findSetter(target.getClass(), f);
	}

	public static Method findSetter(Class<?> target, Field f) throws JSONCodecException {
		String methodName = METHOD_PREFIX_SETTER + xetterName(f.getName());
		return findMethod(target, methodName, new Class<?>[]{f.getType()});
	}

	public static Method findGetter(Object target, Field f) throws JSONCodecException {
		return findGetter(target.getClass(), f);
	}

	public static Method findGetter(Class<?> target, Field f) throws JSONCodecException {
		String methodName = METHOD_PREFIX_GETTER + xetterName(f.getName());
		return findMethod(target, methodName, new Class<?>[]{});
	}


	public static Method findASetter(Object target, Field f) throws JSONCodecException {
		return findASetter(target.getClass(), f);
	}

	public static Method findASetter(Class<?> target, Field f) throws JSONCodecException {
		String methodName = METHOD_PREFIX_ARRAY_SETTER + xetterName(f.getName());
		return findMethod(target, methodName, new Class<?>[]{int.class, f.getType().getComponentType()});
	}

	public static Method findAGetter(Object target, Field f) throws JSONCodecException {
		return findAGetter(target.getClass(), f);
	}

	public static Method findAGetter(Class<?> target, Field f) throws JSONCodecException {
		String methodName = METHOD_PREFIX_ARRAY_GETTER + xetterName(f.getName());
		return findMethod(target, methodName, new Class<?>[]{int.class});
	}

	public static Method findAAdder(Object target, Field field) throws JSONCodecException {
		Method m = findAAdder(target.getClass(), field);
		return m;
	}

	public static Method findAAdder(Class<?> target, Field field) throws JSONCodecException {
		String methodName = METHOD_PREFIX_ARRAY_ADDER + xetterName(field.getName());
		Method m = findMethod(target, methodName, new Class<?>[]{field.getType().getComponentType()});
		return m;
	}

	public static void checkParameter(Method method, int param, Field field, Class<?> paramType) throws JSONCodecException {
		Class<?>[] params = method.getParameterTypes();
		if (params.length < param+1) {
			throw new JSONCodecException(method.getDeclaringClass() + "." + method.getName() + "(): needs a " + param + ". parameter");
		} else if (!params[param].equals(paramType)) {
			throw new JSONCodecException(method.getDeclaringClass() + "." + method.getName() + "(): " + Integer.toString(param+1) + ". parameter must have type " + paramType.getSimpleName());
		}

	}


	private static Method findMethod(Class<?> target, String methodName, Class<?>[] params) throws JSONCodecException {
		Method method = null;
		try {
			/* try direct access with parameter type */
			return target.getMethod(methodName, params);
		} catch (Exception e) {
			/* search manually */
			for (Method m : target.getDeclaredMethods()) {
				if (m.getName().equals(methodName)) {
					method = m;
					break;
				}
			}
		}
		if (method == null) {
			throw new JSONCodecException(target.getName() + '.' + methodName + "(): not found");
		} else if (!Modifier.isPublic(method.getModifiers())) {
			throw new JSONCodecException(target.getName() + '.' + methodName + "(): not public");
		}
		return method;
	}



	public static String xetterName(String name) {
		return name.substring(0, 1).toUpperCase() + name.substring(1);
	}


	public static void checkGetterReturnType(Method m, Class<?> class1) throws JSONCodecException {
		Class<?> t = m.getReturnType();
		if (!t.equals(class1)) {
			throw new JSONCodecException(m.getDeclaringClass() + "." + m.getName() + "(): return must have type " + class1.getSimpleName());
		}
	}



}
