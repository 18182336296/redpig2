package com.redpigmall.api.exception;

/**
 * 
 * <p>
 * Title: CanotRemoveObjectException.java
 * </p>
 * 
 * <p>
 * Description:删除对象异常，继承在RuntimeException，后续系统将会自定义更多异常，方便程序员调试程序
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
public class CanotRemoveObjectException extends RuntimeException {

	@Override
	public void printStackTrace() {
		super.printStackTrace();
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

}
