package com.redpigmall.api.beans.exception;

public abstract class NestedExceptionUtils {
	public static String buildMessage(String message, Throwable cause) {
		if (cause != null) {
			StringBuffer buf = new StringBuffer();
			if (message != null) {
				buf.append(message).append("; ");
			}
			buf.append("nested exception is ").append(cause);
			return buf.toString();
		}
		return message;
	}
}
