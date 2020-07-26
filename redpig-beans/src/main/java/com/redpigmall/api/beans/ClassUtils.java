package com.redpigmall.api.beans;

import java.beans.Introspector;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Sets;
@SuppressWarnings({"rawtypes","unchecked"})
public abstract class ClassUtils {
	public static final String ARRAY_SUFFIX = "[]";

	private static final Logger logger = LoggerFactory
			.getLogger(ClassUtils.class);
	private static final Map primitiveWrapperTypeMap = new HashMap(8);
	private static final Map primitiveTypeNameMap = new HashMap(8);

	static {
		primitiveWrapperTypeMap.put(Boolean.class, Boolean.TYPE);
		primitiveWrapperTypeMap.put(Byte.class, Byte.TYPE);
		primitiveWrapperTypeMap.put(Character.class, Character.TYPE);
		primitiveWrapperTypeMap.put(Double.class, Double.TYPE);
		primitiveWrapperTypeMap.put(Float.class, Float.TYPE);
		primitiveWrapperTypeMap.put(Integer.class, Integer.TYPE);
		primitiveWrapperTypeMap.put(Long.class, Long.TYPE);
		primitiveWrapperTypeMap.put(Short.class, Short.TYPE);
		for (Iterator it = primitiveWrapperTypeMap.values().iterator(); it
				.hasNext();) {
			Class primitiveClass = (Class) it.next();
			primitiveTypeNameMap.put(primitiveClass.getName(), primitiveClass);
		}
	}

	public static ClassLoader getDefaultClassLoader() {
		ClassLoader cl = Thread.currentThread().getContextClassLoader();
		if (cl == null) {
			cl = ClassUtils.class.getClassLoader();
		}
		return cl;
	}

	public static boolean isPresent(String className) {
		try {
			forName(className);
			return true;
		} catch (Throwable ex) {
			if (logger.isDebugEnabled()) {
				logger.debug("Class [" + className
						+ "] or one of its dependencies is not present: " + ex);
			}
		}
		return false;
	}

	public static Class forName(String name) throws ClassNotFoundException {
		return forName(name, getDefaultClassLoader());
	}

	public static Class forName(String name, ClassLoader classLoader)
			throws ClassNotFoundException {
		Class clazz = resolvePrimitiveClassName(name);
		if (clazz != null) {
			return clazz;
		}
		if (name.endsWith("[]")) {
			String elementClassName = name.substring(0,
					name.length() - "[]".length());
			Class elementClass = forName(elementClassName, classLoader);
			return Array.newInstance(elementClass, 0).getClass();
		}
		return Class.forName(name, true, classLoader);
	}

	public static Class resolvePrimitiveClassName(String name) {
		Class result = null;
		if ((name != null) && (name.length() <= 8)) {
			result = (Class) primitiveTypeNameMap.get(name);
		}
		return result;
	}

	public static String getShortName(String className) {
		Assert.hasLength(className, "Class name must not be empty");
		int lastDotIndex = className.lastIndexOf('.');
		int nameEndIndex = className.indexOf("$$");
		if (nameEndIndex == -1) {
			nameEndIndex = className.length();
		}
		String shortName = className.substring(lastDotIndex + 1, nameEndIndex);
		shortName = shortName.replace('$', '.');
		return shortName;
	}

	public static String getShortName(Class clazz) {
		return getShortName(getQualifiedName(clazz));
	}

	public static String getShortNameAsProperty(Class clazz) {
		return Introspector.decapitalize(getShortName(clazz));
	}

	public static String getQualifiedName(Class clazz) {
		Assert.notNull(clazz, "Class must not be null");
		if (clazz.isArray()) {
			return getQualifiedNameForArray(clazz);
		}
		return clazz.getName();
	}

	public static String getQualifiedMethodName(Method method) {
		Assert.notNull(method, "Method must not be null");
		return method.getDeclaringClass().getName() + "." + method.getName();
	}

	public static boolean hasMethod(Class clazz, String methodName,
			Class[] paramTypes) {
		Assert.notNull(clazz, "Class must not be null");
		Assert.notNull(methodName, "Method name must not be null");
		try {
			clazz.getMethod(methodName, paramTypes);
			return true;
		} catch (NoSuchMethodException ex) {
		}
		return false;
	}

	public static int getMethodCountForName(Class clazz, String methodName) {
		Assert.notNull(clazz, "Class must not be null");
		Assert.notNull(methodName, "Method name must not be null");
		int count = 0;
		do {
			for (int i = 0; i < clazz.getDeclaredMethods().length; i++) {
				Method method = clazz.getDeclaredMethods()[i];
				if (methodName.equals(method.getName())) {
					count++;
				}
			}
			clazz = clazz.getSuperclass();
		} while (clazz != null);
		return count;
	}

	public static boolean hasAtLeastOneMethodWithName(Class clazz,
			String methodName) {
		Assert.notNull(clazz, "Class must not be null");
		Assert.notNull(methodName, "Method name must not be null");
		do {
			for (int i = 0; i < clazz.getDeclaredMethods().length; i++) {
				Method method = clazz.getDeclaredMethods()[i];
				if (method.getName().equals(methodName)) {
					return true;
				}
			}
			clazz = clazz.getSuperclass();
		} while (clazz != null);
		return false;
	}

	public static Method getStaticMethod(Class clazz, String methodName,
			Class[] args) {
		Assert.notNull(clazz, "Class must not be null");
		Assert.notNull(methodName, "Method name must not be null");
		try {
			Method method = clazz.getDeclaredMethod(methodName, args);
			if ((method.getModifiers() & 0x8) != 0) {
				return method;
			}
		} catch (NoSuchMethodException localNoSuchMethodException) {
		}
		return null;
	}

	public static boolean isPrimitiveWrapper(Class clazz) {
		Assert.notNull(clazz, "Class must not be null");
		return primitiveWrapperTypeMap.containsKey(clazz);
	}

	public static boolean isPrimitiveOrWrapper(Class clazz) {
		Assert.notNull(clazz, "Class must not be null");
		return (clazz.isPrimitive()) || (isPrimitiveWrapper(clazz));
	}

	public static boolean isPrimitiveArray(Class clazz) {
		Assert.notNull(clazz, "Class must not be null");
		return (clazz.isArray()) && (clazz.getComponentType().isPrimitive());
	}

	public static boolean isPrimitiveWrapperArray(Class clazz) {
		Assert.notNull(clazz, "Class must not be null");
		return (clazz.isArray())
				&& (isPrimitiveWrapper(clazz.getComponentType()));
	}

	public static boolean isAssignable(Class targetType, Class valueType) {
		Assert.notNull(targetType, "Target type must not be null");
		Assert.notNull(valueType, "Value type must not be null");
		return (targetType.isAssignableFrom(valueType))
				|| (targetType.equals(primitiveWrapperTypeMap.get(valueType)));
	}

	public static boolean isAssignableValue(Class type, Object value) {
		Assert.notNull(type, "Type must not be null");
		return type.isPrimitive() ? false : value != null ? isAssignable(type,
				value.getClass()) : true;
	}

	public static String addResourcePathToPackagePath(Class clazz,
			String resourceName) {
		Assert.notNull(resourceName, "Resource name must not be null");
		if (!resourceName.startsWith("/")) {
			return classPackageAsResourcePath(clazz) + "/" + resourceName;
		}
		return classPackageAsResourcePath(clazz) + resourceName;
	}

	public static String classPackageAsResourcePath(Class clazz) {
		if ((clazz == null) || (clazz.getPackage() == null)) {
			return "";
		}
		return clazz.getPackage().getName().replace('.', '/');
	}

	public static Class[] getAllInterfaces(Object object) {
		Set interfaces = getAllInterfacesAsSet(object);
		return (Class[]) interfaces.toArray(new Class[interfaces.size()]);
	}

	public static Class[] getAllInterfacesForClass(Class clazz) {
		Set interfaces = getAllInterfacesForClassAsSet(clazz);
		return (Class[]) interfaces.toArray(new Class[interfaces.size()]);
	}

	public static Set getAllInterfacesAsSet(Object object) {
		return getAllInterfacesForClassAsSet(object.getClass());
	}

	public static Set getAllInterfacesForClassAsSet(Class clazz) {
		if (clazz.isInterface()) {
			return Collections.singleton(clazz);
		}
		Set interfaces = Sets.newHashSet();
		while (clazz != null) {
			for (int i = 0; i < clazz.getInterfaces().length; i++) {
				Class ifc = clazz.getInterfaces()[i];
				interfaces.add(ifc);
			}
			clazz = clazz.getSuperclass();
		}
		return interfaces;
	}

	private static String getQualifiedNameForArray(Class clazz) {
		StringBuffer buffer = new StringBuffer();
		while (clazz.isArray()) {
			clazz = clazz.getComponentType();
			buffer.append("[]");
		}
		buffer.insert(0, clazz.getName());
		return buffer.toString();
	}
}
