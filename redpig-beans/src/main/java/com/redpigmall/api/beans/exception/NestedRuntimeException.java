package com.redpigmall.api.beans.exception;

import java.io.PrintStream;
import java.io.PrintWriter;

/**
 * 
 * <p>
 * Title: NestedRuntimeException.java
 * </p>
 * 
 * <p>
 * Description:内部异常
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2014
 * </p>
 * 
 * <p>
 * Company: www.redpigmall.net
 * </p>
 * 
 * @author redpig
 * 
 * @date 2014-4-24
 * 
 * @version redpigmall_b2b2c 8.0
 */
@SuppressWarnings({ "rawtypes" })
public abstract class NestedRuntimeException extends RuntimeException {
	private static final long serialVersionUID = 5439915454935047936L;
	private Throwable cause;

	public NestedRuntimeException(String msg) {
		super(msg);
	}

	public NestedRuntimeException(String msg, Throwable ex) {
		super(msg);
		this.cause = ex;
	}

	public Throwable getCause() {
		return this.cause == this ? null : this.cause;
	}

	public String getMessage() {
		return NestedExceptionUtils
				.buildMessage(super.getMessage(), getCause());
	}

	public void printStackTrace(PrintStream ps) {
		if (getCause() == null) {
			super.printStackTrace(ps);
		} else {
			ps.println(this);
			ps.print("Caused by: ");
			getCause().printStackTrace(ps);
		}
	}

	public void printStackTrace(PrintWriter pw) {
		if (getCause() == null) {
			super.printStackTrace(pw);
		} else {
			pw.println(this);
			pw.print("Caused by: ");
			getCause().printStackTrace(pw);
		}
	}

	public Throwable getRootCause() {
		Throwable cause = getCause();
		if ((cause instanceof NestedRuntimeException)) {
			return ((NestedRuntimeException) cause).getRootCause();
		}
		return cause;
	}

	public boolean contains(Class exClass) {
		if (exClass == null) {
			return false;
		}
		if (exClass.isInstance(this)) {
			return true;
		}
		Throwable cause = getCause();
		if ((cause instanceof NestedRuntimeException)) {
			return ((NestedRuntimeException) cause).contains(exClass);
		}
		return (cause != null) && (exClass.isInstance(cause));
	}
}
