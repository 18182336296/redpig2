package com.redpigmall.api.beans.exception;

import java.beans.PropertyChangeEvent;

@SuppressWarnings({ "serial" })
public abstract class PropertyAccessException extends PropertyException {
	private PropertyChangeEvent propertyChangeEvent;

	public PropertyAccessException(PropertyChangeEvent propertyChangeEvent,
			String msg, Throwable ex) {
		super(msg, ex);
		this.propertyChangeEvent = propertyChangeEvent;
	}

	public PropertyAccessException(String msg, Throwable ex) {
		super(msg, ex);
	}

	public PropertyChangeEvent getPropertyChangeEvent() {
		return this.propertyChangeEvent;
	}
}
