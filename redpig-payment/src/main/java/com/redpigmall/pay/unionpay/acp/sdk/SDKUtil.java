package com.redpigmall.pay.unionpay.acp.sdk;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;

import com.google.common.collect.Maps;
@SuppressWarnings({"rawtypes","unchecked"})
public class SDKUtil {
	public static String encoding_UTF8 = "UTF-8";
	public static String encoding_GBK = "GBK";
	public static String version = "5.0.0";

	public static Map<String, String> signData(Map<String, ?> contentData,
			String encoding) {
		Map.Entry<String, String> obj = null;
		Map<String, String> submitFromData = Maps.newHashMap();
		for (Iterator<?> it = contentData.entrySet().iterator(); it.hasNext();) {
			obj = (Map.Entry) it.next();
			String value = (String) obj.getValue();
			if (StringUtils.isNotBlank(value)) {
				submitFromData.put((String) obj.getKey(), value.trim());
				System.out.println((String) obj.getKey() + "-->"
						+ String.valueOf(value));
			}
		}
		sign(submitFromData, encoding);
		return submitFromData;
	}

	public static Map<String, String> signData(Map<String, ?> contentData,
			String certPath, String certPwd, String encoding) {
		Map.Entry<String, String> obj = null;
		Map<String, String> submitFromData = Maps.newHashMap();
		System.out.println("打印请求报文域 :");
		for (Iterator<?> it = contentData.entrySet().iterator(); it.hasNext();) {
			obj = (Map.Entry) it.next();
			String value = (String) obj.getValue();
			if (StringUtils.isNotBlank(value)) {
				submitFromData.put((String) obj.getKey(), value.trim());
				System.out.println((String) obj.getKey() + "-->"
						+ String.valueOf(value));
			}
		}
		signByCertInfo(submitFromData, encoding, certPath, certPwd);

		return submitFromData;
	}

	public static Map<String, String> submitUrl(
			Map<String, String> submitFromData, String requestUrl,
			String encoding) {
		String resultString = "";
		LogUtil.writeLog("请求银联地址:" + requestUrl);

		HttpClient hc = new HttpClient(requestUrl, 30000, 30000);
		try {
			int status = hc.send(submitFromData, encoding);
			if (200 == status) {
				resultString = hc.getResult();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		Map<String, String> resData = Maps.newHashMap();
		if ((resultString != null) && (!"".equals(resultString))) {
			resData = convertResultStringToMap(resultString);
			if (validate(resData, encoding)) {
				LogUtil.writeLog("验证签名成功,可以继续后边的逻辑处理");
			} else {
				LogUtil.writeLog("验证签名失败,必须验签签名通过才能继续后边的逻辑...");
			}
		}
		return resData;
	}

	public static Map<String, String> submitDate(Map<String, ?> contentData,
			String requestUrl, String encoding) {
		Map<String, String> submitFromData = signData(contentData, encoding);
		return submitUrl(submitFromData, requestUrl, encoding);
	}

	public static boolean validate(Map<String, String> resData, String encoding) {
		LogUtil.writeLog("验签处理开始");
		if (isEmpty(encoding)) {
			encoding = "UTF-8";
		}
		String stringSign = (String) resData.get("signature");

		String certId = (String) resData.get("certId");

		LogUtil.writeLog("对返回报文串验签使用的验签公钥序列号：[" + certId + "]");

		String stringData = coverMap2String(resData);

		LogUtil.writeLog("待验签返回报文串：[" + stringData + "]");
		try {
			return SecureUtil.validateSignBySoft(
					CertUtil.getValidateKey(certId),
					SecureUtil.base64Decode(stringSign.getBytes(encoding)),
					SecureUtil.sha1X16(stringData, encoding));
		} catch (UnsupportedEncodingException e) {
			LogUtil.writeErrorLog(e.getMessage(), e);
		} catch (Exception e) {
			LogUtil.writeErrorLog(e.getMessage(), e);
		}
		return false;
	}

	public static String createAutoFormHtml(String action,
			Map<String, String> hiddens, String encoding) {
		StringBuffer sf = new StringBuffer();
		sf.append("<html><head><meta http-equiv=\"Content-Type\" content=\"text/html; charset="
				+ encoding + "\"/></head><body>");
		sf.append("<form id = \"pay_form\" action=\"" + action
				+ "\" method=\"post\">");
		if ((hiddens != null) && (hiddens.size() != 0)) {
			Set<Map.Entry<String, String>> set = hiddens.entrySet();
			Iterator<Map.Entry<String, String>> it = set.iterator();
			while (it.hasNext()) {
				Map.Entry<String, String> ey = (Map.Entry) it.next();
				String key = (String) ey.getKey();
				String value = (String) ey.getValue();
				sf.append("<input type=\"hidden\" name=\"" + key + "\" id=\""
						+ key + "\" value=\"" + value + "\"/>");
			}
		}
		sf.append("</form>");
		sf.append("</body>");
		sf.append("<script type=\"text/javascript\">");
		sf.append("document.all.pay_form.submit();");
		sf.append("</script>");
		sf.append("</html>");
		return sf.toString();
	}

	/* Error */
	public static String enCodeFileContent(String filePath) {
		return filePath;

	}

	public static void deCodeFileContent(Map<String, String> resData,
			String filePathRoot, String encoding) {
		String fileContent = (String) resData.get("fileContent");
		if ((fileContent != null) && (!"".equals(fileContent))) {
			try {
				byte[] fileArray = SecureUtil.inflater(SecureUtil
						.base64Decode(fileContent.getBytes(encoding)));
				String filePath = null;
				if (isEmpty((String) resData.get("fileName"))) {
					filePath =

					filePathRoot + File.separator
							+ (String) resData.get("merId") + "_"
							+ (String) resData.get("batchNo") + "_"
							+ (String) resData.get("txnTime") + ".txt";
				} else {
					filePath = filePathRoot + File.separator
							+ (String) resData.get("fileName");
				}
				File file = new File(filePath);
				if (file.exists()) {
					file.delete();
				}
				file.createNewFile();
				FileOutputStream out = new FileOutputStream(file);
				out.write(fileArray, 0, fileArray.length);
				out.flush();
				out.close();
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static String getFileContent(String fileContent, String encoding)
			throws UnsupportedEncodingException, IOException {
		return new String(SecureUtil.inflater(SecureUtil
				.base64Decode(fileContent.getBytes())), encoding);
	}

	public static String getCustomerInfo(Map<String, String> customerInfoMap,
			String accNo, String encoding) {
		StringBuffer sf = new StringBuffer("{");
		for (Iterator<String> it = customerInfoMap.keySet().iterator(); it
				.hasNext();) {
			String key = (String) it.next();
			String value = (String) customerInfoMap.get(key);
			if (key.equals("pin")) {
				if ((accNo == null) || ("".equals(accNo.trim()))) {
					LogUtil.writeLog("送了密码（PIN），必须在getCustomerInfoWithEncrypt参数中上传卡号");
					return "{}";
				}
				value = encryptPin(accNo, value, encoding);
			}
			if (it.hasNext()) {
				sf.append(key + "=" + value + "&");
			} else {
				sf.append(key + "=" + value);
			}
		}
		sf.append("}");

		String customerInfo = sf.toString();
		LogUtil.writeLog("组装的customerInfo明文：" + customerInfo);
		try {
			return new String(SecureUtil.base64Encode(sf.toString().getBytes(
					encoding)));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return customerInfo;
	}

	public static String getCustomerInfoWithEncrypt(
			Map<String, String> customerInfoMap, String accNo, String encoding) {
		StringBuffer sf = new StringBuffer("{");

		StringBuffer encryptedInfoSb = new StringBuffer("");
		for (Iterator<String> it = customerInfoMap.keySet().iterator(); it
				.hasNext();) {
			String key = (String) it.next();
			String value = (String) customerInfoMap.get(key);
			if ((key.equals("phoneNo")) || (key.equals("cvn2"))
					|| (key.equals("expired"))) {
				encryptedInfoSb.append(key + "=" + value + "&");
			} else {
				if (key.equals("pin")) {
					if ((accNo == null) || ("".equals(accNo.trim()))) {
						LogUtil.writeLog("送了密码（PIN），必须在getCustomerInfoWithEncrypt参数中上传卡号");
						return "{}";
					}
					value = encryptPin(accNo, value, encoding);
				}
				if (it.hasNext()) {
					sf.append(key + "=" + value + "&");
				} else {
					sf.append(key + "=" + value);
				}
			}
		}
		if (!encryptedInfoSb.toString().equals("")) {
			encryptedInfoSb.setLength(encryptedInfoSb.length() - 1);

			LogUtil.writeLog("组装的customerInfo encryptedInfo明文："
					+ encryptedInfoSb.toString());
			if (sf.toString().equals("{")) {
				sf.append("encryptedInfo=");
			} else {
				sf.append("&encryptedInfo=");
			}
			sf.append(encryptEpInfo(encryptedInfoSb.toString(), encoding));
		}
		sf.append("}");

		String customerInfo = sf.toString();
		LogUtil.writeLog("组装的customerInfo明文：" + customerInfo);
		try {
			return new String(SecureUtil.base64Encode(sf.toString().getBytes(
					encoding)));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return customerInfo;
	}

	@SuppressWarnings("unused")
	public static String getCardTransData(Map<String, ?> contentData,
			String encoding) {
		StringBuffer cardTransDataBuffer = new StringBuffer();

		String ICCardData = "uduiadniodaiooxnnxnnada";
		String ICCardSeqNumber = "123";
		String track2Data = "testtrack2Datauidanidnaidiadiada231";
		String track3Data = "testtrack3Datadadaiiuiduiauiduia312117831";
		String transSendMode = "b";

		StringBuffer track2Buffer = new StringBuffer();
		track2Buffer.append(contentData.get("merId")).append("|")
				.append(contentData.get("orderId")).append("|")
				.append(contentData.get("txnTime")).append("|")
				.append(contentData.get("txnAmt")).append("|")
				.append(track2Data);

		String encryptedTrack2 = encryptTrack(track2Buffer.toString(), encoding);

		StringBuffer track3Buffer = new StringBuffer();
		track3Buffer.append(contentData.get("merId")).append("|")
				.append(contentData.get("orderId")).append("|")
				.append(contentData.get("txnTime")).append("|")
				.append(contentData.get("txnAmt")).append("|")
				.append(track3Data);

		String encryptedTrack3 = encryptTrack(track3Buffer.toString(), encoding);

		Map<String, String> cardTransDataMap = Maps.newHashMap();
		cardTransDataMap.put("ICCardData", ICCardData);
		cardTransDataMap.put("ICCardSeqNumber", ICCardSeqNumber);
		cardTransDataMap.put("track2Data", encryptedTrack2);
		cardTransDataMap.put("track3Data", encryptedTrack3);
		cardTransDataMap.put("transSendMode", transSendMode);

		return "{" + coverMap2String(cardTransDataMap) + "}";
	}

	public static boolean sign(Map<String, String> data, String encoding) {
		if (isEmpty(encoding)) {
			encoding = "UTF-8";
		}
		data.put("certId", CertUtil.getSignCertId());

		String stringData = coverMap2String(data);
		LogUtil.writeLog("待签名请求报文串:[" + stringData + "]");

		byte[] byteSign = null;
		String stringSign = null;
		try {
			byte[] signDigest = SecureUtil.sha1X16(stringData, encoding);
			byteSign = SecureUtil.base64Encode(SecureUtil.signBySoft(
					CertUtil.getSignCertPrivateKey(), signDigest));
			stringSign = new String(byteSign);

			data.put("signature", stringSign);
			return true;
		} catch (Exception e) {
			LogUtil.writeErrorLog("签名异常", e);
		}
		return false;
	}

	public static boolean signByCertInfo(Map<String, String> data,
			String encoding, String certPath, String certPwd) {
		if (isEmpty(encoding)) {
			encoding = "UTF-8";
		}
		if ((isEmpty(certPath)) || (isEmpty(certPwd))) {
			LogUtil.writeLog("Invalid Parameter:CertPath=[" + certPath
					+ "],CertPwd=[" + certPwd + "]");
			return false;
		}
		data.put("certId", CertUtil.getCertIdByKeyStoreMap(certPath, certPwd));

		String stringData = coverMap2String(data);

		byte[] byteSign = null;
		String stringSign = null;
		try {
			byte[] signDigest = SecureUtil.sha1X16(stringData, encoding);
			byteSign = SecureUtil
					.base64Encode(SecureUtil.signBySoft(
							CertUtil.getSignCertPrivateKeyByStoreMap(certPath,
									certPwd), signDigest));
			stringSign = new String(byteSign);

			data.put("signature", stringSign);
			return true;
		} catch (Exception e) {
			LogUtil.writeErrorLog("签名异常", e);
		}
		return false;
	}

	public static String encryptPin(String card, String pwd, String encoding) {
		return SecureUtil.EncryptPin(pwd, card, encoding,
				CertUtil.getEncryptCertPublicKey());
	}

	public static String encryptCvn2(String cvn2, String encoding) {
		return SecureUtil.EncryptData(cvn2, encoding,
				CertUtil.getEncryptCertPublicKey());
	}

	public static String decryptCvn2(String base64cvn2, String encoding) {
		return SecureUtil.DecryptedData(base64cvn2, encoding,
				CertUtil.getSignCertPrivateKey());
	}

	public static String encryptAvailable(String date, String encoding) {
		return SecureUtil.EncryptData(date, encoding,
				CertUtil.getEncryptCertPublicKey());
	}

	public static String decryptAvailable(String base64Date, String encoding) {
		return SecureUtil.DecryptedData(base64Date, encoding,
				CertUtil.getSignCertPrivateKey());
	}

	public static String encryptPan(String pan, String encoding) {
		return SecureUtil.EncryptData(pan, encoding,
				CertUtil.getEncryptCertPublicKey());
	}

	public static String decryptPan(String base64Pan, String encoding) {
		return SecureUtil.DecryptedData(base64Pan, encoding,
				CertUtil.getSignCertPrivateKey());
	}

	public static String encryptEpInfo(String encryptedInfo, String encoding) {
		return SecureUtil.EncryptData(encryptedInfo, encoding,
				CertUtil.getEncryptCertPublicKey());
	}

	public static String decryptEpInfo(String base64EncryptedInfo,
			String encoding) {
		return SecureUtil.DecryptedData(base64EncryptedInfo, encoding,
				CertUtil.getSignCertPrivateKey());
	}

	public static String encryptTrack(String trackData, String encoding) {
		return SecureUtil.EncryptData(trackData, encoding,
				CertUtil.getEncryptTrackCertPublicKey());
	}

	public static String encryptTrack(String trackData, String encoding,
			String modulus, String exponent) {
		return SecureUtil.EncryptData(trackData, encoding,
				CertUtil.getEncryptTrackCertPublicKey(modulus, exponent));
	}

	public static String getEncryptCertId() {
		return CertUtil.getEncryptCertId();
	}

	public static byte[] base64Decode(byte[] inputByte) throws IOException {
		return Base64.decodeBase64(inputByte);
	}

	public static byte[] base64Encode(byte[] inputByte) throws IOException {
		return Base64.encodeBase64(inputByte);
	}

	public static String coverMap2String(Map<String, String> data) {
		TreeMap<String, String> tree = new TreeMap();
		Iterator<Map.Entry<String, String>> it = data.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<String, String> en = (Map.Entry) it.next();
			if (!"signature".equals(((String) en.getKey()).trim())) {
				tree.put((String) en.getKey(), (String) en.getValue());
			}
		}
		it = tree.entrySet().iterator();
		StringBuffer sf = new StringBuffer();
		while (it.hasNext()) {
			Map.Entry<String, String> en = (Map.Entry) it.next();
			sf.append((String) en.getKey() + "=" + (String) en.getValue() + "&");
		}
		return sf.substring(0, sf.length() - 1);
	}

	public static Map<String, String> coverResultString2Map(String result) {
		return convertResultStringToMap(result);
	}

	public static Map<String, String> convertResultStringToMap(String result) {
		Map<String, String> map = null;
		try {
			if (StringUtils.isNotBlank(result)) {
				if ((result.startsWith("{")) && (result.endsWith("}"))) {
					System.out.println(result.length());
					result = result.substring(1, result.length() - 1);
				}
				map = parseQString(result);
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return map;
	}

	public static Map<String, String> parseQString(String str)
			throws UnsupportedEncodingException {
		Map<String, String> map = Maps.newHashMap();
		int len = str.length();
		StringBuilder temp = new StringBuilder();

		String key = null;
		boolean isKey = true;
		boolean isOpen = false;
		char openName = '\000';
		if (len > 0) {
			for (int i = 0; i < len; i++) {
				char curChar = str.charAt(i);
				if (isKey) {
					if (curChar == '=') {
						key = temp.toString();
						temp.setLength(0);
						isKey = false;
					} else {
						temp.append(curChar);
					}
				} else {
					if (isOpen) {
						if (curChar == openName) {
							isOpen = false;
						}
					} else {
						if (curChar == '{') {
							isOpen = true;
							openName = '}';
						}
						if (curChar == '[') {
							isOpen = true;
							openName = ']';
						}
					}
					if ((curChar == '&') && (!isOpen)) {
						putKeyValueToMap(temp, isKey, key, map);
						temp.setLength(0);
						isKey = true;
					} else {
						temp.append(curChar);
					}
				}
			}
			putKeyValueToMap(temp, isKey, key, map);
		}
		return map;
	}

	private static void putKeyValueToMap(StringBuilder temp, boolean isKey,
			String key, Map<String, String> map)
			throws UnsupportedEncodingException {
		if (isKey) {
			key = temp.toString();
			if (key.length() == 0) {
				throw new RuntimeException("QString format illegal");
			}
			map.put(key, "");
		} else {
			if (key.length() == 0) {
				throw new RuntimeException("QString format illegal");
			}
			map.put(key, temp.toString());
		}
	}

	public static boolean isEmpty(String s) {
		return (s == null) || ("".equals(s.trim()));
	}
}
