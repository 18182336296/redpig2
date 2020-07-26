package com.redpigmall.api.beans;

public abstract interface PropertyValues {
	public abstract PropertyValue[] getPropertyValues();

	public abstract PropertyValue getPropertyValue(String paramString);

	public abstract boolean contains(String paramString);

	public abstract boolean isEmpty();

	public abstract PropertyValues changesSince(
			PropertyValues paramPropertyValues);
}
