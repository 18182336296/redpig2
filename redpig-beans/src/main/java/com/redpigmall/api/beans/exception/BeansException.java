package com.redpigmall.api.beans.exception;

@SuppressWarnings({ "serial", "rawtypes" })
public class BeansException extends NestedRuntimeException {
	protected Class beanClass;

	public BeansException(String msg) {
		super(msg);
	}

	public BeansException(String msg, Throwable ex) {
		super(msg, ex);
	}

	public BeansException(String msg, Class beanClass) {
		this(msg, null, beanClass);
	}

	public BeansException(String msg, Throwable ex, Class beanClass) {
		super("在" + beanClass.getName() + "上发生bean操作错误:" + msg, ex);
		this.beanClass = beanClass;
	}

	public Class getBeanClass() {
		return this.beanClass;
	}
}
