package com.redpigmall.api.beans.exception;

import java.io.PrintStream;
import java.io.PrintWriter;

@SuppressWarnings({ "rawtypes" })
public abstract class NestedCheckedException extends Exception {
	private static final long serialVersionUID = 7100714597678207546L;
	private Throwable cause;

	public NestedCheckedException(String msg) {
		super(msg);
	}

	public NestedCheckedException(String msg, Throwable ex) {
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
		if ((cause instanceof NestedCheckedException)) {
			return ((NestedCheckedException) cause).getRootCause();
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
		if ((cause instanceof NestedCheckedException)) {
			return ((NestedCheckedException) cause).contains(exClass);
		}
		return (cause != null) && (exClass.isInstance(cause));
	}
}
