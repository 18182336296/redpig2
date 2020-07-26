package com.redpigmall.api.beans;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;
import java.util.Collection;
import java.util.Map;
@SuppressWarnings({"rawtypes","unchecked"})
public abstract class GenericCollectionTypeResolver {
	public static Class getCollectionParameterType(MethodParameter methodParam) {
		return getGenericParameterType(methodParam, Collection.class, 0);
	}

	public static Class getMapKeyParameterType(MethodParameter methodParam) {
		return getGenericParameterType(methodParam, Map.class, 0);
	}

	public static Class getMapValueParameterType(MethodParameter methodParam) {
		return getGenericParameterType(methodParam, Map.class, 1);
	}

	public static Class getCollectionReturnType(Method method) {
		return getGenericReturnType(method, Collection.class, 0);
	}

	public static Class getMapKeyReturnType(Method method) {
		return getGenericReturnType(method, Map.class, 0);
	}

	public static Class getMapValueReturnType(Method method) {
		return getGenericReturnType(method, Map.class, 1);
	}

	private static Class getGenericParameterType(MethodParameter methodParam,
			Class source, int typeIndex) {
		Assert.notNull(methodParam, "MethodParameter must not be null");
		int idx = methodParam.getParameterIndex();
		if (methodParam.getConstructor() != null) {
			return extractType(methodParam.getConstructor()
					.getGenericParameterTypes()[idx], source, typeIndex);
		}
		return extractType(
				methodParam.getMethod().getGenericParameterTypes()[idx],
				source, typeIndex);
	}

	private static Class getGenericReturnType(Method method, Class source,
			int typeIndex) {
		Assert.notNull(method, "Method must not be null");
		return extractType(method.getGenericReturnType(), source, typeIndex);
	}

	private static Class extractType(Type type, Class source, int typeIndex) {
		if ((type instanceof ParameterizedType)) {
			return extractTypeFromParameterizedType((ParameterizedType) type,
					source, typeIndex);
		}
		if ((type instanceof Class)) {
			return extractTypeFromClass((Class) type, source, typeIndex);
		}
		return null;
	}

	private static Class extractTypeFromParameterizedType(
			ParameterizedType ptype, Class source, int typeIndex) {
		if (!(ptype.getRawType() instanceof Class)) {
			return null;
		}
		Class rawType = (Class) ptype.getRawType();
		if (!source.isAssignableFrom(rawType)) {
			return null;
		}
		Class fromSuperclassOrInterface = extractType(rawType, source,
				typeIndex);
		if (fromSuperclassOrInterface != null) {
			return fromSuperclassOrInterface;
		}
		Type[] paramTypes = ptype.getActualTypeArguments();
		if ((paramTypes == null) || (typeIndex >= paramTypes.length)) {
			return null;
		}
		Type paramType = paramTypes[typeIndex];
		if ((paramType instanceof WildcardType)) {
			Type[] lowerBounds = ((WildcardType) paramType).getLowerBounds();
			if ((lowerBounds != null) && (lowerBounds.length > 0)) {
				paramType = lowerBounds[0];
			}
		}
		if ((paramType instanceof ParameterizedType)) {
			paramType = ((ParameterizedType) paramType).getRawType();
		}
		if ((paramType instanceof Class)) {
			return (Class) paramType;
		}
		return null;
	}

	private static Class extractTypeFromClass(Class clazz, Class source,
			int typeIndex) {
		if ((clazz.getSuperclass() != null)
				&& (source.isAssignableFrom(clazz.getSuperclass()))) {
			return extractType(clazz.getGenericSuperclass(), source, typeIndex);
		}
		Type[] ifcs = clazz.getGenericInterfaces();
		if (ifcs != null) {
			for (int i = 0; i < ifcs.length; i++) {
				Type ifc = ifcs[i];
				Type rawType = ifc;
				if ((ifc instanceof ParameterizedType)) {
					rawType = ((ParameterizedType) ifc).getRawType();
				}
				if (((rawType instanceof Class))
						&& (source.isAssignableFrom((Class) rawType))) {
					return extractType(ifc, source, typeIndex);
				}
			}
		}
		return null;
	}
}
