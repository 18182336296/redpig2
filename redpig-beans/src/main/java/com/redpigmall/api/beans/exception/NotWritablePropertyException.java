package com.redpigmall.api.beans.exception;

@SuppressWarnings({ "serial", "rawtypes" })
public class NotWritablePropertyException extends PropertyException {
	private String[] possibleMatches = null;

	public NotWritablePropertyException(Class beanClass, String propertyName) {
		super(
				beanClass,
				propertyName,
				"Bean property '"
						+ propertyName
						+ "' is not writable or has an invalid setter method: "
						+ "Does the return type of the getter match the parameter type of the setter?");
	}

	public NotWritablePropertyException(Class beanClass, String propertyName,
			String msg) {
		super(beanClass, propertyName, msg);
	}

	public NotWritablePropertyException(Class beanClass, String propertyName,
			String msg, Throwable ex) {
		super(beanClass, propertyName, msg, ex);
	}

	public NotWritablePropertyException(Class beanClass, String propertyName,
			String msg, String[] possibleMatches) {
		super(beanClass, propertyName, msg);
		this.possibleMatches = possibleMatches;
	}

	public String[] getPossibleMatches() {
		return this.possibleMatches;
	}
}
