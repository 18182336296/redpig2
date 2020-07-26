package com.redpigmall.api.beans.exception;

@SuppressWarnings({ "serial", "rawtypes" })
public class PropertyException extends BeansException {
	protected String propertyName;

	public PropertyException(String msg) {
		super(msg);
	}

	public PropertyException(String msg, Throwable ex) {
		super(msg, ex);
	}

	public PropertyException(Class beanClass, String propertyName, String msg) {
		this(beanClass, propertyName, msg, null);
	}

	public PropertyException(Class beanClass, String propertyName, String msg,
			Throwable ex) {
		super("Invalid property '" + propertyName + "' of bean class ["
				+ beanClass.getName() + "]: " + msg, ex);
		this.beanClass = beanClass;
		this.propertyName = propertyName;
	}

	public String getPropertyName() {
		return this.propertyName;
	}
}
