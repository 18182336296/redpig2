package com.redpigmall.api.beans;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
@SuppressWarnings({"rawtypes"})
public class MethodParameter {
	private Method method;
	private Constructor constructor;
	private final int parameterIndex;

	public MethodParameter(Method method, int parameterIndex) {
		Assert.notNull(method, "Method must not be null");
		Assert.isTrue(parameterIndex >= 0,
				"Parameter index must not be negative");
		Assert.isTrue(
				parameterIndex < method.getParameterTypes().length,
				"Parameter index must not exceed "
						+ (method.getParameterTypes().length - 1));
		this.method = method;
		this.parameterIndex = parameterIndex;
	}

	public MethodParameter(Constructor constructor, int parameterIndex) {
		Assert.notNull(constructor, "Constructor must not be null");
		Assert.isTrue(parameterIndex >= 0,
				"Parameter index must not be negative");
		Assert.isTrue(
				parameterIndex < constructor.getParameterTypes().length,
				"Parameter index must not exceed "
						+ (constructor.getParameterTypes().length - 1));
		this.constructor = constructor;
		this.parameterIndex = parameterIndex;
	}

	public Method getMethod() {
		return this.method;
	}

	public Constructor getConstructor() {
		return this.constructor;
	}

	public int getParameterIndex() {
		return this.parameterIndex;
	}

	public static MethodParameter forMethodOrConstructor(
			Object methodOrConstructor, int parameterIndex) {
		if ((methodOrConstructor instanceof Method)) {
			return new MethodParameter((Method) methodOrConstructor,
					parameterIndex);
		}
		if ((methodOrConstructor instanceof Constructor)) {
			return new MethodParameter((Constructor) methodOrConstructor,
					parameterIndex);
		}
		throw new IllegalArgumentException("Given object ["
				+ methodOrConstructor
				+ "] is neither a Method nor a Constructor");
	}
}
