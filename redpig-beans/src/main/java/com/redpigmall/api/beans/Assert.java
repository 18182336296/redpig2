package com.redpigmall.api.beans;

import java.util.Collection;
import java.util.Map;
@SuppressWarnings({"rawtypes","unchecked"})
public abstract class Assert {
	public static void isTrue(boolean expression, String message) {
		if (!expression) {
			throw new IllegalArgumentException(message);
		}
	}

	public static void isTrue(boolean expression) {
		isTrue(expression, "[Assertion failed] - this expression must be true");
	}

	public static void isNull(Object object, String message) {
		if (object != null) {
			throw new IllegalArgumentException(message);
		}
	}

	public static void isNull(Object object) {
		isNull(object, "[Assertion failed] - the object argument must be null");
	}

	public static void notNull(Object object, String message) {
		if (object == null) {
			throw new IllegalArgumentException(message);
		}
	}

	public static void notNull(Object object) {
		notNull(object,
				"[Assertion failed] - this argument is required; it cannot be null");
	}

	public static void hasLength(String text, String message) {
		if (!StringUtils.hasLength(text)) {
			throw new IllegalArgumentException(message);
		}
	}

	public static void hasLength(String text) {
		hasLength(
				text,
				"[Assertion failed] - this String argument must have length; it cannot be <code>null</code> or empty");
	}

	public static void hasText(String text, String message) {
		if (!StringUtils.hasText(text)) {
			throw new IllegalArgumentException(message);
		}
	}

	public static void hasText(String text) {
		hasText(text,
				"[Assertion failed] - this String argument must have text; it cannot be <code>null</code>, empty, or blank");
	}

	public static void doesNotContain(String textToSearch, String substring,
			String message) {
		if ((StringUtils.hasLength(textToSearch))
				&& (StringUtils.hasLength(substring))
				&& (textToSearch.indexOf(substring) != -1)) {
			throw new IllegalArgumentException(message);
		}
	}

	public static void doesNotContain(String textToSearch, String substring) {
		doesNotContain(textToSearch, substring,
				"[Assertion failed] - this String argument must not contain the substring ["
						+ substring + "]");
	}

	public static void notEmpty(Object[] array, String message) {
		if ((array == null) || (array.length == 0)) {
			throw new IllegalArgumentException(message);
		}
	}

	public static void notEmpty(Object[] array) {
		notEmpty(
				array,
				"[Assertion failed] - this array must not be empty: it must contain at least 1 element");
	}

	public static void notEmpty(Collection collection, String message) {
		if ((collection == null) || (collection.isEmpty())) {
			throw new IllegalArgumentException(message);
		}
	}

	public static void notEmpty(Collection collection) {
		notEmpty(
				collection,
				"[Assertion failed] - this collection must not be empty: it must contain at least 1 element");
	}

	public static void notEmpty(Map map, String message) {
		if ((map == null) || (map.isEmpty())) {
			throw new IllegalArgumentException(message);
		}
	}

	public static void notEmpty(Map map) {
		notEmpty(
				map,
				"[Assertion failed] - this map must not be empty; it must contain at least one entry");
	}

	public static void isInstanceOf(Class clazz, Object obj) {
		isInstanceOf(clazz, obj, "");
	}

	public static void isInstanceOf(Class clazz, Object obj, String message) {
		notNull(clazz,
				"The clazz to perform the instanceof assertion cannot be null");
		isTrue(clazz.isInstance(obj), message + "Object of class '"
				+ (obj != null ? obj.getClass().getName() : "[null]")
				+ "' must be an instance of '" + clazz.getName() + "'");
	}

	public static void isAssignable(Class superType, Class subType) {
		isAssignable(superType, subType, "");
	}

	public static void isAssignable(Class superType, Class subType,
			String message) {
		notNull(superType, "superType cannot be null");
		notNull(subType, "subType cannot be null");
		isTrue(superType.isAssignableFrom(subType), message + "Type ["
				+ subType.getName() + "] is not assignable to type ["
				+ superType.getName() + "]");
	}

	public static void state(boolean expression, String message) {
		if (!expression) {
			throw new IllegalStateException(message);
		}
	}

	public static void state(boolean expression) {
		state(expression,
				"[Assertion failed] - this state invariant must be true");
	}
}
