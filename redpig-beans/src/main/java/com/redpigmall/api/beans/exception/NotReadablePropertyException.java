package com.redpigmall.api.beans.exception;

@SuppressWarnings({ "serial", "rawtypes" })
public class NotReadablePropertyException extends PropertyException {
	public NotReadablePropertyException(Class beanClass, String propertyName) {
		super(
				beanClass,
				propertyName,
				"Bean property '"
						+ propertyName
						+ "' is not readable or has an invalid getter method: "
						+ "Does the return type of the getter match the parameter type of the setter?");
	}

	public NotReadablePropertyException(Class beanClass, String propertyName,
			String msg) {
		super(beanClass, propertyName, msg);
	}
}
