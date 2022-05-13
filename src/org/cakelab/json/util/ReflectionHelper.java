package org.cakelab.json.util;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReflectionHelper {
	
	private Map<Class<?>, List<Field>> fieldListsCache = new HashMap<>();
	private Map<Class<?>, Map<String, Field>> fieldMapsCache = new HashMap<>();
	
	public List<Field> getDeclaredFields(Class<?> type) {
		List<Field> fields = fieldListsCache.get(type);
		if (fields == null) {
			fields = new ArrayList<Field>();
			if (!type.equals(Object.class)) {
				List<Field> superFields = getDeclaredFields(type.getSuperclass());
				fields.addAll(superFields);
			}
			for (Field f : type.getDeclaredFields()) {
				fields.add(f);
			}
			fieldListsCache.put(type, fields);
		}
		return fields;
	}

	public Map<String, Field> getDeclaredFieldsMap(Class<?> type) {
		Map<String, Field> map = fieldMapsCache.get(type);
		if (map == null) {
			map = new HashMap<String, Field>();
			if (!type.equals(Object.class)) {
				Map<String, Field> superFields = getDeclaredFieldsMap(type.getSuperclass());
				map.putAll(superFields);
			}
			for (Field f : type.getDeclaredFields()) {
				map.put(f.getName(), f);
			}
			fieldMapsCache.put(type,  map);
		}
		return map;
	}


	public static boolean isPrimitive(Class<?> type) {
		return type.isPrimitive() || type.equals(String.class)
				 || type.equals(Long.class) || type.equals(Integer.class) || type.equals(Short.class)
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


	public static boolean isSubclassOf(Class<?> derived, Class<?> superclass) {
		if (derived.equals(superclass)) return true;
		
		if (derived.equals(Object.class)) return false;
		
		for (Class<?> interf : derived.getInterfaces()) {
			if (isSubclassOf(interf, superclass)) {
				return true;
			}
		}
		
		return isSubclassOf(derived.getSuperclass(), superclass);
	}



}
