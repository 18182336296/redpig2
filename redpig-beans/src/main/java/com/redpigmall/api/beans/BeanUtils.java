package com.redpigmall.api.beans;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;

import com.redpigmall.api.beans.exception.BeansException;
@SuppressWarnings({"rawtypes","unchecked"})
public abstract class BeanUtils {
	private static final TypeConverter converter = new TypeConverter(
			new BeanWrapper());

	public static Object convertType(Object v, Class requiredType) {
		return converter.convertIfNecessary(v, requiredType,
				(MethodParameter) null);
	}

	public static Object instantiateClass(Class clazz) throws BeansException {
		Assert.notNull(clazz, "指定的类名不能为null");
		if (clazz.isInterface()) {
			throw new BeansException("指定的class是一个接口，不能初始化!", clazz);
		}
		try {
			return instantiateClass(clazz.getDeclaredConstructor(), null);
		} catch (NoSuchMethodException ex) {
			throw new BeansException("找不到默认构造函数!", ex, clazz);
		}
	}

	public static Object instantiateClass(Constructor contor, Object[] args)
			throws BeansException {
		Assert.notNull(contor, "构造子不能为null");
		try {
			if ((!Modifier.isPublic(contor.getModifiers()))
					|| (!Modifier.isPublic(contor.getDeclaringClass()
							.getModifiers()))) {
				contor.setAccessible(true);
			}
			return contor.newInstance(args);
		} catch (Exception ex) {
			throw new BeansException("构造实例错误!", ex, contor.getDeclaringClass());
		}
	}

	public static Method findMethod(Class clazz, String methodName,
			Class[] paramTypes) {
		try {
			return clazz.getMethod(methodName, paramTypes);
		} catch (NoSuchMethodException ex) {
		}
		return findDeclaredMethod(clazz, methodName, paramTypes);
	}

	public static Method findDeclaredMethod(Class clazz, String methodName,
			Class[] paramTypes) {
		try {
			return clazz.getDeclaredMethod(methodName, paramTypes);
		} catch (NoSuchMethodException ex) {
			if (clazz.getSuperclass() != null) {
				return findDeclaredMethod(clazz.getSuperclass(), methodName,
						paramTypes);
			}
		}
		return null;
	}

	public static Method findMethodWithMinimalParameters(Class clazz,
			String methodName) throws IllegalArgumentException {
		Method targetMethod = doFindMethodWithMinimalParameters(
				clazz.getDeclaredMethods(), methodName);
		if (targetMethod == null) {
			return findDeclaredMethodWithMinimalParameters(clazz, methodName);
		}
		return targetMethod;
	}

	public static Method findDeclaredMethodWithMinimalParameters(Class clazz,
			String methodName) throws IllegalArgumentException {
		Method targetMethod = doFindMethodWithMinimalParameters(
				clazz.getDeclaredMethods(), methodName);
		if ((targetMethod == null) && (clazz.getSuperclass() != null)) {
			return findDeclaredMethodWithMinimalParameters(
					clazz.getSuperclass(), methodName);
		}
		return targetMethod;
	}

	private static Method doFindMethodWithMinimalParameters(Method[] methods,
			String methodName) throws IllegalArgumentException {
		Method targetMethod = null;
		int numMethodsFoundWithCurrentMinimumArgs = 0;
		for (int i = 0; i < methods.length; i++) {
			if (methods[i].getName().equals(methodName)) {
				int numParams = methods[i].getParameterTypes().length;
				if ((targetMethod == null)
						|| (numParams < targetMethod.getParameterTypes().length)) {
					targetMethod = methods[i];
					numMethodsFoundWithCurrentMinimumArgs = 1;
				} else if (targetMethod.getParameterTypes().length == numParams) {
					numMethodsFoundWithCurrentMinimumArgs++;
				}
			}
		}
		if (numMethodsFoundWithCurrentMinimumArgs > 1) {
			throw new IllegalArgumentException(
					"Cannot resolve method '"
							+ methodName
							+ "' to a unique method. Attempted to resolve to overloaded method with "
							+ "the least number of parameters, but there were "
							+ numMethodsFoundWithCurrentMinimumArgs
							+ " candidates.");
		}
		return targetMethod;
	}

	public static Method resolveSignature(String signature, Class clazz) {
		Assert.hasText(signature, "Signature must not be null or zero-length");
		Assert.notNull(clazz, "Class must not be null");

		int firstParen = signature.indexOf("(");
		int lastParen = signature.indexOf(")");
		if ((firstParen > -1) && (lastParen == -1)) {
			throw new IllegalArgumentException("Invalid method signature '"
					+ signature + "': expected closing ')' for args list");
		}
		if ((lastParen > -1) && (firstParen == -1)) {
			throw new IllegalArgumentException("Invalid method signature '"
					+ signature + "': expected opening '(' for args list");
		}
		if ((firstParen == -1) && (lastParen == -1)) {
			return findMethodWithMinimalParameters(clazz, signature);
		}
		String methodName = signature.substring(0, firstParen);
		String[] parameterTypeNames = StringUtils
				.commaDelimitedListToStringArray(signature.substring(
						firstParen + 1, lastParen));
		Class[] parameterTypes = new Class[parameterTypeNames.length];
		for (int i = 0; i < parameterTypeNames.length; i++) {
			String parameterTypeName = parameterTypeNames[i].trim();
			try {
				parameterTypes[i] = ClassUtils.forName(parameterTypeName,
						clazz.getClassLoader());
			} catch (ClassNotFoundException ex) {
				throw new IllegalArgumentException(
						"Invalid method signature: unable to locate type ["
								+ parameterTypeName + "] for argument " + i);
			}
		}
		return findMethod(clazz, methodName, parameterTypes);
	}

	public static PropertyDescriptor[] getPropertyDescriptors(Class clazz)
			throws BeansException {
		CachedIntrospectionResults cr = CachedIntrospectionResults
				.forClass(clazz);
		return cr.getBeanInfo().getPropertyDescriptors();
	}

	public static PropertyDescriptor getPropertyDescriptor(Class clazz,
			String propertyName) throws BeansException {
		CachedIntrospectionResults cr = CachedIntrospectionResults
				.forClass(clazz);
		return cr.getPropertyDescriptor(propertyName);
	}

	public static PropertyDescriptor findPropertyForMethod(Method method)
			throws BeansException {
		Assert.notNull(method, "Method must not be null");
		PropertyDescriptor[] pds = getPropertyDescriptors(method
				.getDeclaringClass());
		for (int i = 0; i < pds.length; i++) {
			if ((method.equals(pds[i].getReadMethod()))
					|| (method.equals(pds[i].getWriteMethod()))) {
				return pds[i];
			}
		}
		return null;
	}

	public static Class findPropertyType(String propertyName,
			Class[] beanClasses) {
		if (beanClasses != null) {
			for (int i = 0; i < beanClasses.length; i++) {
				PropertyDescriptor pd = getPropertyDescriptor(beanClasses[i],
						propertyName);
				if (pd != null) {
					return pd.getPropertyType();
				}
			}
		}
		return Object.class;
	}

	public static String canonicalPropertyName(String propertyName) {
		if (propertyName == null) {
			return "";
		}
		StringBuffer buf = new StringBuffer(propertyName);
		int searchIndex = 0;
		while (searchIndex != -1) {
			int keyStart = buf.toString().indexOf("[", searchIndex);
			searchIndex = -1;
			if (keyStart != -1) {
				int keyEnd = buf.toString().indexOf("]",
						keyStart + "[".length());
				if (keyEnd != -1) {
					String key = buf.substring(keyStart + "[".length(), keyEnd);
					if (((key.startsWith("'")) && (key.endsWith("'")))
							|| ((key.startsWith("\"")) && (key.endsWith("\"")))) {
						buf.delete(keyStart + 1, keyStart + 2);
						buf.delete(keyEnd - 2, keyEnd - 1);
						keyEnd -= 2;
					}
					searchIndex = keyEnd + "]".length();
				}
			}
		}
		return buf.toString();
	}

	public static boolean isSimpleProperty(Class clazz) {
		Assert.notNull(clazz, "Class must not be null");
		return (clazz.isPrimitive()) || (ClassUtils.isPrimitiveArray(clazz))
				|| (ClassUtils.isPrimitiveWrapper(clazz))
				|| (ClassUtils.isPrimitiveWrapperArray(clazz))
				|| (clazz.equals(String.class))
				|| (clazz.equals(String[].class))
				|| (clazz.equals(Class.class)) || (clazz.equals(Class[].class));
	}

	public static void copyProperties(Object source, Object target)
			throws BeansException {
		copyProperties(source, target, null, null);
	}

	public static void copyProperties(Object source, Object target,
			Class editable) throws BeansException {
		copyProperties(source, target, editable, null);
	}

	public static void copyProperties(Object source, Object target,
			String[] ignoreProperties) throws BeansException {
		copyProperties(source, target, null, ignoreProperties);
	}

	private static void copyProperties(Object source, Object target,
			Class editable, String[] ignoreProperties) throws BeansException {
		Assert.notNull(source, "Source must not be null");
		Assert.notNull(target, "Target must not be null");

		Class actualEditable = target.getClass();
		if (editable != null) {
			if (!editable.isInstance(target)) {
				throw new IllegalArgumentException("Target class ["
						+ target.getClass().getName()
						+ "] not assignable to Editable class ["
						+ editable.getName() + "]");
			}
			actualEditable = editable;
		}
		PropertyDescriptor[] targetPds = getPropertyDescriptors(actualEditable);
		List ignoreList = ignoreProperties != null ? Arrays
				.asList(ignoreProperties) : null;
		for (int i = 0; i < targetPds.length; i++) {
			PropertyDescriptor targetPd = targetPds[i];
			if ((targetPd.getWriteMethod() != null)
					&& ((ignoreProperties == null) || (!ignoreList
							.contains(targetPd.getName())))) {
				PropertyDescriptor sourcePd = getPropertyDescriptor(
						source.getClass(), targetPd.getName());
				if ((sourcePd != null) && (sourcePd.getReadMethod() != null)) {
					try {
						Method readMethod = sourcePd.getReadMethod();
						if (!Modifier.isPublic(readMethod.getDeclaringClass()
								.getModifiers())) {
							readMethod.setAccessible(true);
						}
						Object value = readMethod.invoke(source, new Object[0]);
						Method writeMethod = targetPd.getWriteMethod();
						if (!Modifier.isPublic(writeMethod.getDeclaringClass()
								.getModifiers())) {
							writeMethod.setAccessible(true);
						}
						writeMethod.invoke(target, new Object[] { value });
					} catch (Exception ex) {
						throw new BeansException(
								"Could not copy properties from source to target",
								ex);
					}
				}
			}
		}
	}

	public static boolean checkLazyloadNull(Object value) {
		if (value == null) {
			return true;
		}
		if (value.getClass().toString().indexOf("$$EnhancerByCGLIB") > 0) {
			try {
				value.toString();
			} catch (NullPointerException e) {
				return true;
			}
		}
		return false;
	}
}
