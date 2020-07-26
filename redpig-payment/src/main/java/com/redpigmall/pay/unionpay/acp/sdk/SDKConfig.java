package com.redpigmall.pay.unionpay.acp.sdk;

import java.util.Properties;

public class SDKConfig {
	public static final String FILE_NAME = "acp_sdk.properties";
	private String frontRequestUrl;
	private String backRequestUrl;
	private String singleQueryUrl;
	private String batchQueryUrl;
	private String batchTransUrl;
	private String fileTransUrl;
	private String signCertPath;
	private String signCertPwd;
	private String signCertType;
	private String encryptCertPath;
	private String validateCertDir;
	private String signCertDir;
	private String encryptTrackCertPath;
	private String cardRequestUrl;
	private String appRequestUrl;
	private String singleMode;
	public static final String SDK_FRONT_URL = "acpsdk.frontTransUrl";
	public static final String SDK_BACK_URL = "acpsdk.backTransUrl";
	public static final String SDK_SIGNQ_URL = "acpsdk.singleQueryUrl";
	public static final String SDK_BATQ_URL = "acpsdk.batchQueryUrl";
	public static final String SDK_BATTRANS_URL = "acpsdk.batchTransUrl";
	public static final String SDK_FILETRANS_URL = "acpsdk.fileTransUrl";
	public static final String SDK_CARD_URL = "acpsdk.cardTransUrl";
	public static final String SDK_APP_URL = "acpsdk.appTransUrl";
	public static final String SDK_SIGNCERT_PATH = "acpsdk.signCert.path";
	public static final String SDK_SIGNCERT_PWD = "acpsdk.signCert.pwd";
	public static final String SDK_SIGNCERT_TYPE = "acpsdk.signCert.type";
	public static final String SDK_ENCRYPTCERT_PATH = "acpsdk.encryptCert.path";
	public static final String SDK_ENCRYPTTRACKCERT_PATH = "acpsdk.encryptTrackCert.path";
	public static final String SDK_VALIDATECERT_DIR = "acpsdk.validateCert.dir";
	public static final String SDK_CVN_ENC = "acpsdk.cvn2.enc";
	public static final String SDK_DATE_ENC = "acpsdk.date.enc";
	public static final String SDK_PAN_ENC = "acpsdk.pan.enc";
	public static final String SDK_SINGLEMODE = "acpsdk.singleMode";
	private static SDKConfig config;
	private Properties properties;

	public static SDKConfig getConfig() {
		if (config == null) {
			config = new SDKConfig();
		}
		return config;
	}

	public void loadPropertiesFromPath(String rootPath) {

	}

	public void loadPropertiesFromSrc() {
	}

	public void loadProperties(Properties pro) {
		String value = null;
		value = pro.getProperty("acpsdk.singleMode");
		if ((SDKUtil.isEmpty(value)) || ("true".equals(value))) {
			this.singleMode = "true";
			LogUtil.writeLog("SingleCertMode:[" + this.singleMode + "]");

			value = pro.getProperty("acpsdk.signCert.path");
			if (!SDKUtil.isEmpty(value)) {
				this.signCertPath = value.trim();
			}
			value = pro.getProperty("acpsdk.signCert.pwd");
			if (!SDKUtil.isEmpty(value)) {
				this.signCertPwd = value.trim();
			}
			value = pro.getProperty("acpsdk.signCert.type");
			if (!SDKUtil.isEmpty(value)) {
				this.signCertType = value.trim();
			}
		} else {
			this.singleMode = "false";
			LogUtil.writeLog("SingleMode:[" + this.singleMode + "]");
		}
		value = pro.getProperty("acpsdk.encryptCert.path");
		if (!SDKUtil.isEmpty(value)) {
			this.encryptCertPath = value.trim();
		}
		value = pro.getProperty("acpsdk.validateCert.dir");
		if (!SDKUtil.isEmpty(value)) {
			this.validateCertDir = value.trim();
		}
		value = pro.getProperty("acpsdk.frontTransUrl");
		if (!SDKUtil.isEmpty(value)) {
			this.frontRequestUrl = value.trim();
		}
		value = pro.getProperty("acpsdk.backTransUrl");
		if (!SDKUtil.isEmpty(value)) {
			this.backRequestUrl = value.trim();
		}
		value = pro.getProperty("acpsdk.batchQueryUrl");
		if (!SDKUtil.isEmpty(value)) {
			this.batchQueryUrl = value.trim();
		}
		value = pro.getProperty("acpsdk.batchTransUrl");
		if (!SDKUtil.isEmpty(value)) {
			this.batchTransUrl = value.trim();
		}
		value = pro.getProperty("acpsdk.fileTransUrl");
		if (!SDKUtil.isEmpty(value)) {
			this.fileTransUrl = value.trim();
		}
		value = pro.getProperty("acpsdk.singleQueryUrl");
		if (!SDKUtil.isEmpty(value)) {
			this.singleQueryUrl = value.trim();
		}
		value = pro.getProperty("acpsdk.cardTransUrl");
		if (!SDKUtil.isEmpty(value)) {
			this.cardRequestUrl = value.trim();
		}
		value = pro.getProperty("acpsdk.appTransUrl");
		if (!SDKUtil.isEmpty(value)) {
			this.appRequestUrl = value.trim();
		}
		value = pro.getProperty("acpsdk.encryptTrackCert.path");
		if (!SDKUtil.isEmpty(value)) {
			this.encryptTrackCertPath = value.trim();
		}
	}

	public String getFrontRequestUrl() {
		return this.frontRequestUrl;
	}

	public void setFrontRequestUrl(String frontRequestUrl) {
		this.frontRequestUrl = frontRequestUrl;
	}

	public String getBackRequestUrl() {
		return this.backRequestUrl;
	}

	public void setBackRequestUrl(String backRequestUrl) {
		this.backRequestUrl = backRequestUrl;
	}

	public String getSignCertPath() {
		return this.signCertPath;
	}

	public void setSignCertPath(String signCertPath) {
		this.signCertPath = signCertPath;
	}

	public String getSignCertPwd() {
		return this.signCertPwd;
	}

	public void setSignCertPwd(String signCertPwd) {
		this.signCertPwd = signCertPwd;
	}

	public String getSignCertType() {
		return this.signCertType;
	}

	public void setSignCertType(String signCertType) {
		this.signCertType = signCertType;
	}

	public String getEncryptCertPath() {
		return this.encryptCertPath;
	}

	public void setEncryptCertPath(String encryptCertPath) {
		this.encryptCertPath = encryptCertPath;
	}

	public String getValidateCertDir() {
		return this.validateCertDir;
	}

	public void setValidateCertDir(String validateCertDir) {
		this.validateCertDir = validateCertDir;
	}

	public String getSingleQueryUrl() {
		return this.singleQueryUrl;
	}

	public void setSingleQueryUrl(String singleQueryUrl) {
		this.singleQueryUrl = singleQueryUrl;
	}

	public String getBatchQueryUrl() {
		return this.batchQueryUrl;
	}

	public void setBatchQueryUrl(String batchQueryUrl) {
		this.batchQueryUrl = batchQueryUrl;
	}

	public String getBatchTransUrl() {
		return this.batchTransUrl;
	}

	public void setBatchTransUrl(String batchTransUrl) {
		this.batchTransUrl = batchTransUrl;
	}

	public String getFileTransUrl() {
		return this.fileTransUrl;
	}

	public void setFileTransUrl(String fileTransUrl) {
		this.fileTransUrl = fileTransUrl;
	}

	public String getSignCertDir() {
		return this.signCertDir;
	}

	public void setSignCertDir(String signCertDir) {
		this.signCertDir = signCertDir;
	}

	public Properties getProperties() {
		return this.properties;
	}

	public void setProperties(Properties properties) {
		this.properties = properties;
	}

	public String getCardRequestUrl() {
		return this.cardRequestUrl;
	}

	public void setCardRequestUrl(String cardRequestUrl) {
		this.cardRequestUrl = cardRequestUrl;
	}

	public String getAppRequestUrl() {
		return this.appRequestUrl;
	}

	public void setAppRequestUrl(String appRequestUrl) {
		this.appRequestUrl = appRequestUrl;
	}

	public String getEncryptTrackCertPath() {
		return this.encryptTrackCertPath;
	}

	public void setEncryptTrackCertPath(String encryptTrackCertPath) {
		this.encryptTrackCertPath = encryptTrackCertPath;
	}

	public String getSingleMode() {
		return this.singleMode;
	}

	public void setSingleMode(String singleMode) {
		this.singleMode = singleMode;
	}
}
