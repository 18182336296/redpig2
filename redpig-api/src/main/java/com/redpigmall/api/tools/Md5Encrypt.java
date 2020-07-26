package com.redpigmall.api.tools;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 
 * <p>
 * Title: Md5Encrypt.java
 * </p>
 * 
 * <p>
 * Description: MD5加密类 功能：将支付宝提交的相关参数按照传入编码进行MD5加密 接口名称：标准实物双接口 版本：2.0
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
 * @version redpigmall_b2b2c v8.0 2016版
 */
public class Md5Encrypt {
	/**
	 * Used building output as Hex
	 */
	private static final char[] DIGITS = { '0', '1', '2', '3', '4', '5', '6',
			'7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };

	/**
	 * 对字符串进行MD5加密
	 * 
	 * @param text
	 *            明文
	 * 
	 * @return 密文
	 */
	public static String md5(String text) {
		MessageDigest msgDigest = null;
		try {
			msgDigest = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			throw new IllegalStateException(
					"System doesn't support MD5 algorithm.");
		}
		try {
			msgDigest.update(text.getBytes("utf-8"));// 注意改接口是按照utf-8编码形式加密
		} catch (UnsupportedEncodingException e) {
			throw new IllegalStateException(
					"System doesn't support your  EncodingException.");
		}
		byte[] bytes = msgDigest.digest();

		String md5Str = new String(encodeHex(bytes));

		return md5Str;
	}
	
	public static char[] encodeHex(byte[] data) {
		int l = data.length;

		char[] out = new char[l << 1];

		int i = 0;
		// two characters form the hex value.
		for (int j = 0; i < l; i++) {
			out[(j++)] = DIGITS[((0xF0 & data[i]) >>> 4)];
			out[(j++)] = DIGITS[(0xF & data[i])];
		}
		return out;
	}
}
