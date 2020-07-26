package com.redpigmall.api.beans.exception;

@SuppressWarnings({ "serial", "rawtypes" })
public class NullValueInNestedPathException extends PropertyException {
	public NullValueInNestedPathException(Class beanClass, String propertyName) {
		super(beanClass, propertyName, "Value of nested property '"
				+ propertyName + "' is null");
	}

	public NullValueInNestedPathException(Class beanClass, String propertyName,
			String msg) {
		super(beanClass, propertyName, msg);
	}
}
