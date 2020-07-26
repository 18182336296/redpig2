package com.redpigmall.api.beans.exception;

import java.beans.PropertyChangeEvent;

@SuppressWarnings({ "serial" })
public class MethodInvocationException extends PropertyAccessException {
	public static final String ERROR_CODE = "methodInvocation";

	public MethodInvocationException(PropertyChangeEvent propertyChangeEvent,
			Throwable ex) {
		super(propertyChangeEvent, "Property '"
				+ propertyChangeEvent.getPropertyName() + "' threw exception",
				ex);
	}

	public String getErrorCode() {
		return "methodInvocation";
	}
}
