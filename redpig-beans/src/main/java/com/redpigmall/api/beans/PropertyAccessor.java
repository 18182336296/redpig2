package com.redpigmall.api.beans;

import java.util.Map;

import com.redpigmall.api.beans.exception.BeansException;
@SuppressWarnings({"rawtypes"})
public abstract interface PropertyAccessor {
	public static final String NESTED_PROPERTY_SEPARATOR = ".";
	public static final char NESTED_PROPERTY_SEPARATOR_CHAR = '.';
	public static final String PROPERTY_KEY_PREFIX = "[";
	public static final char PROPERTY_KEY_PREFIX_CHAR = '[';
	public static final String PROPERTY_KEY_SUFFIX = "]";
	public static final char PROPERTY_KEY_SUFFIX_CHAR = ']';

	public abstract boolean isReadableProperty(String paramString)
			throws BeansException;

	public abstract boolean isWritableProperty(String paramString)
			throws BeansException;

	public abstract Class getPropertyType(String paramString)
			throws BeansException;

	public abstract Object getPropertyValue(String paramString)
			throws BeansException;

	public abstract void setPropertyValue(String paramString, Object paramObject)
			throws BeansException;

	public abstract void setPropertyValue(PropertyValue paramPropertyValue)
			throws BeansException;

	public abstract void setPropertyValues(Map paramMap) throws BeansException;

	public abstract void setPropertyValues(PropertyValues paramPropertyValues)
			throws BeansException;

	public abstract void setPropertyValues(PropertyValues paramPropertyValues,
			boolean paramBoolean) throws BeansException;

	public abstract void setPropertyValues(PropertyValues paramPropertyValues,
			boolean paramBoolean1, boolean paramBoolean2) throws BeansException;
}
