package com.redpigmall.api.beans;

import java.io.Serializable;

@SuppressWarnings("serial")
public class PropertyValue implements Serializable {
	private final String name;
	private final Object value;

	public PropertyValue(String name, Object value) {
		if (name == null) {
			throw new IllegalArgumentException("Property name cannot be null");
		}
		this.name = name;
		this.value = value;
	}

	public PropertyValue(PropertyValue source) {
		Assert.notNull(source, "Source must not be null");
		this.name = source.getName();
		this.value = source.getValue();
	}

	public String getName() {
		return this.name;
	}

	public Object getValue() {
		return this.value;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof PropertyValue)) {
			return false;
		}
		PropertyValue otherPv = (PropertyValue) other;
		return (this.name.equals(otherPv.name))
				&& (ObjectUtils.nullSafeEquals(this.value, otherPv.value));
	}

	public int hashCode() {
		return this.name.hashCode() * 29
				+ (this.value == null ? 0 : this.value.hashCode());
	}

	public String toString() {
		return "PropertyValue: name='" + this.name + "', value=[" + this.value
				+ "]";
	}
}
