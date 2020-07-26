package com.redpigmall.module.weixin.view.tools;


public class AesException extends Exception {
	/**
	 */
	private static final long serialVersionUID = 1L;
	public static final int OK = 0;
	public static final int ValidateSignatureError = -40001;
	public static final int ParseXmlError = -40002;
	public static final int ComputeSignatureError = -40003;
	public static final int IllegalAesKey = -40004;
	public static final int ValidateCorpidError = -40005;
	public static final int EncryptAESError = -40006;
	public static final int DecryptAESError = -40007;
	public static final int IllegalBuffer = -40008;
	private int code;

	private static String getMessage(int code) {
		switch (code) {
		case -40001:
			return "签名验证错误";
		case -40002:
			return "xml解析失败";
		case -40003:
			return "sha加密生成签名失败";
		case -40004:
			return "SymmetricKey非法";
		case -40005:
			return "corpid校验失败";
		case -40006:
			return "aes加密失败";
		case -40007:
			return "aes解密失败";
		case -40008:
			return "解密后得到的buffer非法";
		}
		return null;
	}

	public int getCode() {
		return this.code;
	}

	AesException(int code) {
		super(getMessage(code));
		this.code = code;
	}
}
