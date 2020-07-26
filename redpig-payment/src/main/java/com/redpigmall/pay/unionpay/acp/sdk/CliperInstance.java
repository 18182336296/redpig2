package com.redpigmall.pay.unionpay.acp.sdk;

import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class CliperInstance {
	private static ThreadLocal<Cipher> cipherTL = new ThreadLocal() {
		protected Cipher initialValue() {
			try {
				return Cipher.getInstance("RSA/ECB/PKCS1Padding",
						new BouncyCastleProvider());
			} catch (Exception e) {
			}
			return null;
		}
	};

	public static Cipher getInstance() throws NoSuchAlgorithmException,
			NoSuchPaddingException {
		return (Cipher) cipherTL.get();
	}
}
