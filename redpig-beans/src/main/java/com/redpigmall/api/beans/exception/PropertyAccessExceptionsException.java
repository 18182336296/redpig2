package com.redpigmall.api.beans.exception;

import java.io.PrintStream;
import java.io.PrintWriter;

import com.redpigmall.api.beans.ObjectUtils;

@SuppressWarnings({ "serial", "rawtypes" })
public class PropertyAccessExceptionsException extends PropertyException {
	private PropertyAccessException[] propertyAccessExceptions;

	public PropertyAccessExceptionsException(
			PropertyAccessException[] propertyAccessExceptions) {
		super("");
		if (ObjectUtils.isEmpty(propertyAccessExceptions)) {
			throw new IllegalArgumentException(
					"At least 1 PropertyAccessException required");
		}
		this.propertyAccessExceptions = propertyAccessExceptions;
	}

	public int getExceptionCount() {
		return this.propertyAccessExceptions.length;
	}

	public PropertyAccessException[] getPropertyAccessExceptions() {
		return this.propertyAccessExceptions;
	}

	public PropertyAccessException getPropertyAccessException(
			String propertyName) {
		for (int i = 0; i < this.propertyAccessExceptions.length; i++) {
			PropertyAccessException pae = this.propertyAccessExceptions[i];
			if (propertyName.equals(pae.getPropertyChangeEvent()
					.getPropertyName())) {
				return pae;
			}
		}
		return null;
	}

	public String getMessage() {
		return getExceptionCount() + " errors";
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(getClass().getName()).append(" (")
				.append(getExceptionCount());
		sb.append(" errors); nested PropertyAccessExceptions are:");
		for (int i = 0; i < this.propertyAccessExceptions.length; i++) {
			sb.append('\n').append("PropertyAccessException ").append(i + 1)
					.append(": ");
			sb.append(this.propertyAccessExceptions[i]);
		}
		return sb.toString();
	}

	public void printStackTrace(PrintStream ps) {
		ps.println(getClass().getName() + " (" + getExceptionCount()
				+ " errors); nested PropertyAccessException details are:");
		for (int i = 0; i < this.propertyAccessExceptions.length; i++) {
			ps.println("PropertyAccessException " + (i + 1) + ":");
			this.propertyAccessExceptions[i].printStackTrace(ps);
		}
	}

	public void printStackTrace(PrintWriter pw) {
		pw.println(getClass().getName() + " (" + getExceptionCount()
				+ " errors); nested PropertyAccessException details are:");
		for (int i = 0; i < this.propertyAccessExceptions.length; i++) {
			pw.println("PropertyAccessException " + (i + 1) + ":");
			this.propertyAccessExceptions[i].printStackTrace(pw);
		}
	}

	public boolean contains(Class exClass) {
		if (exClass == null) {
			return false;
		}
		if (exClass.isInstance(this)) {
			return true;
		}
		for (int i = 0; i < this.propertyAccessExceptions.length; i++) {
			PropertyAccessException pae = this.propertyAccessExceptions[i];
			if (pae.contains(exClass)) {
				return true;
			}
		}
		return false;
	}
}
