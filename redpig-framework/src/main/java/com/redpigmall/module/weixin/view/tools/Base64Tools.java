package com.redpigmall.module.weixin.view.tools;

import org.apache.commons.codec.binary.Base64;
import org.springframework.stereotype.Component;
/**
 * <p>
 * Title: Base64Tools.java
 * </p>
 * 
 * <p>
 * Description: Base64工具类
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
 * @date 2015-1-16
 * 
 * @version redpigmall_b2b2c v8.0 2016版
 */
@Component
public class Base64Tools {
	Base64 base64 = new Base64();
	 /** 
     * 解密 
     *  
     * @param pwd 
     * @return 
     * @see [类、类#方法、类#成员] 
     */  
	public String decodeStr(String pwd) {
		byte[] debytes = Base64.decodeBase64(new String(pwd).getBytes());
		return new String(debytes);
	}
    /** 
     * 加密 
     *  
     * @param pwd 
     * @return 
     * @see [类、类#方法、类#成员] 
     */  
	public String encodeStr(String pwd) {
		byte[] enbytes = Base64.encodeBase64Chunked(pwd.getBytes());
		return new String(enbytes);
	}
}
