package com.redpigmall.api.beans;

public abstract class PropertyAccessorUtils {
	public static String getPropertyName(String propertyPath) {
		int separatorIndex = propertyPath.indexOf('[');
		return separatorIndex != -1 ? propertyPath.substring(0, separatorIndex)
				: propertyPath;
	}

	public static int getFirstNestedPropertySeparatorIndex(String propertyPath) {
		return getNestedPropertySeparatorIndex(propertyPath, false);
	}

	public static int getLastNestedPropertySeparatorIndex(String propertyPath) {
		return getNestedPropertySeparatorIndex(propertyPath, true);
	}

	private static int getNestedPropertySeparatorIndex(String propertyPath,
			boolean last) {
		boolean inKey = false;
		int length = propertyPath.length();
		int i = last ? length - 1 : 0;
		while (last ? i >= 0 : i < length) {
			switch (propertyPath.charAt(i)) {
			case '[':
			case ']':
				inKey = !inKey;
				break;
			case '.':
				if (!inKey) {
					return i;
				}
				break;
			}
			if (last) {
				i--;
			} else {
				i++;
			}
		}
		return -1;
	}
}
