package com.redpigmall.api.beans.exception;

import java.beans.PropertyChangeEvent;

import com.redpigmall.api.beans.ClassUtils;

@SuppressWarnings({ "serial", "rawtypes" })
public class TypeMismatchException extends PropertyAccessException {
	public static final String ERROR_CODE = "typeMismatch";
	private Object value;
	private Class requiredType;

	public TypeMismatchException(PropertyChangeEvent propertyChangeEvent,
			Class requiredType) {
		this(propertyChangeEvent, requiredType, null);
	}

	public TypeMismatchException(PropertyChangeEvent propertyChangeEvent,
			Class requiredType, Throwable ex) {
		super(
				propertyChangeEvent,
				"Failed to convert property value of type ["
						+ (propertyChangeEvent.getNewValue() != null ? ClassUtils
								.getQualifiedName(propertyChangeEvent
										.getNewValue().getClass()) : null)
						+ "]"
						+ (requiredType != null ? " to required type ["
								+ ClassUtils.getQualifiedName(requiredType)
								+ "]" : "")
						+ (propertyChangeEvent.getPropertyName() != null ? " for property '"
								+ propertyChangeEvent.getPropertyName() + "'"
								: ""), ex);
		this.value = propertyChangeEvent.getNewValue();
		this.requiredType = requiredType;
	}

	public TypeMismatchException(Object value, Class requiredType) {
		this(value, requiredType, null);
	}

	public TypeMismatchException(Object value, Class requiredType, Throwable ex) {
		super(
				"Failed to convert value of type ["
						+ (value != null ? ClassUtils.getQualifiedName(value
								.getClass()) : null)
						+ "]"
						+ (requiredType != null ? " to required type ["
								+ ClassUtils.getQualifiedName(requiredType)
								+ "]" : ""), ex);
		this.value = value;
		this.requiredType = requiredType;
	}

	public Object getValue() {
		return this.value;
	}

	public Class getRequiredType() {
		return this.requiredType;
	}

	public String getErrorCode() {
		return "typeMismatch";
	}
}
