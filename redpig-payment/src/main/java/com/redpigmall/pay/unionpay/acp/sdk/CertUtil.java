package com.redpigmall.pay.unionpay.acp.sdk;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.PublicKey;
import java.security.Security;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.security.spec.RSAPublicKeySpec;
import java.util.Enumeration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import com.google.common.collect.Maps;
@SuppressWarnings({"rawtypes","unchecked"})
public class CertUtil {
	private static KeyStore keyStore = null;
	private static X509Certificate encryptCert = null;
	private static X509Certificate encryptTrackCert = null;
	private static X509Certificate validateCert = null;
	private static Map<String, X509Certificate> certMap = Maps.newHashMap();
	private static final ThreadLocal<KeyStore> certKeyStoreLocal = new ThreadLocal();
	private static final Map<String, KeyStore> certKeyStoreMap = new ConcurrentHashMap();

	static {
		SDKConfig.getConfig().loadPropertiesFromSrc();
		init();
	}

	public static void init() {
		if ("true".equals(SDKConfig.getConfig().getSingleMode())) {
			initSignCert();
		}
		initEncryptCert();
		initValidateCertFromDir();
	}

	public static void initSignCert() {
		if (keyStore != null) {
			keyStore = null;
		}
		try {
			keyStore = getKeyInfo(SDKConfig.getConfig().getSignCertPath(),
					SDKConfig.getConfig().getSignCertPwd(), SDKConfig
							.getConfig().getSignCertType());
			LogUtil.writeLog("InitSignCert Successful. CertId=["
					+ getSignCertId() + "]");
		} catch (IOException e) {
			LogUtil.writeErrorLog("InitSignCert Error", e);
		}
	}

	/**
	 * @deprecated
	 */
	public static void initSignCert(String certFilePath, String certPwd) {
		LogUtil.writeLog("加载证书文件[" + certFilePath + "]和证书密码[" + certPwd
				+ "]的签名证书开始.");
		certKeyStoreLocal.remove();
		File files = new File(certFilePath);
		if (!files.exists()) {
			LogUtil.writeLog("证书文件不存在,初始化签名证书失败.");
			return;
		}
		try {
			certKeyStoreLocal.set(getKeyInfo(certFilePath, certPwd, "PKCS12"));
		} catch (IOException e) {
			LogUtil.writeErrorLog("加载签名证书失败", e);
		}
		LogUtil.writeLog("加载证书文件[" + certFilePath + "]和证书密码[" + certPwd
				+ "]的签名证书结束.");
	}

	public static void loadRsaCert(String certFilePath, String certPwd) {
		KeyStore keyStore = null;
		try {
			keyStore = getKeyInfo(certFilePath, certPwd, "PKCS12");
			certKeyStoreMap.put(certFilePath, keyStore);
			LogUtil.writeLog("LoadRsaCert Successful");
		} catch (IOException e) {
			LogUtil.writeErrorLog("LoadRsaCert Error", e);
		}
	}

	public static void initEncryptCert() {
		if (!SDKUtil.isEmpty(SDKConfig.getConfig().getEncryptCertPath())) {
			encryptCert = initCert(SDKConfig.getConfig().getEncryptCertPath());
			LogUtil.writeLog("LoadEncryptCert Successful");
		} else {
			LogUtil.writeLog("WARN: acpsdk.encryptCert.path is empty");
		}
		if (!SDKUtil.isEmpty(SDKConfig.getConfig().getEncryptTrackCertPath())) {
			encryptTrackCert = initCert(SDKConfig.getConfig()
					.getEncryptTrackCertPath());
			LogUtil.writeLog("LoadEncryptTrackCert Successful");
		} else {
			LogUtil.writeLog("WARN: acpsdk.encryptTrackCert.path is empty");
		}
	}

	/* Error */
	private static X509Certificate initCert(String path) {
		return encryptCert;

	}

	public static void initValidateCertFromDir() {

	}

	public static PrivateKey getSignCertPrivateKey() {
		try {
			Enumeration<String> aliasenum = keyStore.aliases();
			String keyAlias = null;
			if (aliasenum.hasMoreElements()) {
				keyAlias = (String) aliasenum.nextElement();
			}
			return (PrivateKey) keyStore.getKey(keyAlias, SDKConfig.getConfig()
					.getSignCertPwd().toCharArray());
		} catch (KeyStoreException e) {
			LogUtil.writeErrorLog("getSignCertPrivateKey Error", e);
			return null;
		} catch (UnrecoverableKeyException e) {
			LogUtil.writeErrorLog("getSignCertPrivateKey Error", e);
			return null;
		} catch (NoSuchAlgorithmException e) {
			LogUtil.writeErrorLog("getSignCertPrivateKey Error", e);
		}
		return null;
	}

	/**
	 * @deprecated
	 */
	public static PrivateKey getSignCertPrivateKeyByThreadLocal(
			String certPath, String certPwd) {
		if (certKeyStoreLocal.get() == null) {
			initSignCert(certPath, certPwd);
		}
		try {
			Enumeration<String> aliasenum = ((KeyStore) certKeyStoreLocal.get())
					.aliases();
			String keyAlias = null;
			if (aliasenum.hasMoreElements()) {
				keyAlias = (String) aliasenum.nextElement();
			}
			return (PrivateKey) ((KeyStore) certKeyStoreLocal.get()).getKey(
					keyAlias, certPwd.toCharArray());
		} catch (Exception e) {
			LogUtil.writeErrorLog("获取[" + certPath + "]的签名证书的私钥失败", e);
		}
		return null;
	}

	public static PrivateKey getSignCertPrivateKeyByStoreMap(String certPath,
			String certPwd) {
		if (!certKeyStoreMap.containsKey(certPath)) {
			loadRsaCert(certPath, certPwd);
		}
		try {
			Enumeration<String> aliasenum = ((KeyStore) certKeyStoreMap
					.get(certPath)).aliases();
			String keyAlias = null;
			if (aliasenum.hasMoreElements()) {
				keyAlias = (String) aliasenum.nextElement();
			}
			return (PrivateKey) ((KeyStore) certKeyStoreMap.get(certPath))
					.getKey(keyAlias, certPwd.toCharArray());
		} catch (KeyStoreException e) {
			LogUtil.writeErrorLog("getSignCertPrivateKeyByStoreMap Error", e);
			return null;
		} catch (UnrecoverableKeyException e) {
			LogUtil.writeErrorLog("getSignCertPrivateKeyByStoreMap Error", e);
			return null;
		} catch (NoSuchAlgorithmException e) {
			LogUtil.writeErrorLog("getSignCertPrivateKeyByStoreMap Error", e);
		}
		return null;
	}

	public static PublicKey getEncryptCertPublicKey() {
		if (encryptCert == null) {
			String path = SDKConfig.getConfig().getEncryptCertPath();
			if (!SDKUtil.isEmpty(path)) {
				encryptCert = initCert(path);
				return encryptCert.getPublicKey();
			}
			LogUtil.writeLog("ERROR: acpsdk.encryptCert.path is empty");
			return null;
		}
		return encryptCert.getPublicKey();
	}

	public static PublicKey getEncryptTrackCertPublicKey() {
		if (encryptTrackCert == null) {
			String path = SDKConfig.getConfig().getEncryptTrackCertPath();
			if (!SDKUtil.isEmpty(path)) {
				encryptTrackCert = initCert(path);
				return encryptTrackCert.getPublicKey();
			}
			LogUtil.writeLog("ERROR: acpsdk.encryptTrackCert.path is empty");
			return null;
		}
		return encryptTrackCert.getPublicKey();
	}

	public static PublicKey getValidateKey() {
		if (validateCert == null) {
			return null;
		}
		return validateCert.getPublicKey();
	}

	public static PublicKey getValidateKey(String certId) {
		X509Certificate cf = null;
		if (certMap.containsKey(certId)) {
			cf = (X509Certificate) certMap.get(certId);
			return cf.getPublicKey();
		}
		initValidateCertFromDir();
		if (certMap.containsKey(certId)) {
			cf = (X509Certificate) certMap.get(certId);
			return cf.getPublicKey();
		}
		LogUtil.writeErrorLog("缺少certId=[" + certId + "]对应的验签证书.");
		return null;
	}

	public static String getSignCertId() {
		try {
			Enumeration<String> aliasenum = keyStore.aliases();
			String keyAlias = null;
			if (aliasenum.hasMoreElements()) {
				keyAlias = (String) aliasenum.nextElement();
			}
			X509Certificate cert = (X509Certificate) keyStore
					.getCertificate(keyAlias);
			return cert.getSerialNumber().toString();
		} catch (Exception e) {
			LogUtil.writeErrorLog("getSignCertId Error", e);
		}
		return null;
	}

	public static String getEncryptCertId() {
		if (encryptCert == null) {
			String path = SDKConfig.getConfig().getEncryptCertPath();
			if (!SDKUtil.isEmpty(path)) {
				encryptCert = initCert(path);
				return encryptCert.getSerialNumber().toString();
			}
			LogUtil.writeLog("ERROR: acpsdk.encryptCert.path is empty");
			return null;
		}
		return encryptCert.getSerialNumber().toString();
	}

	public static String getEncryptTrackCertId() {
		if (encryptTrackCert == null) {
			String path = SDKConfig.getConfig().getEncryptTrackCertPath();
			if (!SDKUtil.isEmpty(path)) {
				encryptTrackCert = initCert(path);
				return encryptTrackCert.getSerialNumber().toString();
			}
			LogUtil.writeLog("ERROR: acpsdk.encryptTrackCert.path is empty");
			return null;
		}
		return encryptTrackCert.getSerialNumber().toString();
	}

	public static PublicKey getSignPublicKey() {
		try {
			Enumeration<String> aliasenum = keyStore.aliases();
			String keyAlias = null;
			if (aliasenum.hasMoreElements()) {
				keyAlias = (String) aliasenum.nextElement();
			}
			Certificate cert = keyStore.getCertificate(keyAlias);
			return cert.getPublicKey();
		} catch (Exception e) {
			LogUtil.writeErrorLog(e.toString());
		}
		return null;
	}

	public static KeyStore getKeyInfo(String pfxkeyfile, String keypwd,
			String type) throws IOException {
		FileInputStream fis = null;
		try {
			KeyStore ks = null;
			if ("JKS".equals(type)) {
				ks = KeyStore.getInstance(type);
			} else if ("PKCS12".equals(type)) {
				String jdkVendor = System.getProperty("java.vm.vendor");
				String javaVersion = System.getProperty("java.version");
				LogUtil.writeLog("java.vm.vendor=[" + jdkVendor + "]");
				LogUtil.writeLog("java.version=[" + javaVersion + "]");
				if ((jdkVendor != null) && (jdkVendor.startsWith("IBM"))) {
					Security.insertProviderAt(new BouncyCastleProvider(), 1);
					printSysInfo();
				} else {
					Security.addProvider(new BouncyCastleProvider());
				}
				ks = KeyStore.getInstance(type);
			}
			LogUtil.writeLog("Load RSA CertPath=[" + pfxkeyfile + "],Pwd=["
					+ keypwd + "]");
			fis = new FileInputStream(pfxkeyfile);
			char[] nPassword = null;
			nPassword = (keypwd == null) || ("".equals(keypwd.trim())) ? null
					: keypwd.toCharArray();
			if (ks != null) {
				ks.load(fis, nPassword);
			}
			return ks;
		} catch (Exception e) {
			if (Security.getProvider("BC") == null) {
				LogUtil.writeLog("BC Provider not installed.");
			}
			LogUtil.writeErrorLog("getKeyInfo Error", e);
			if (((e instanceof KeyStoreException)) && ("PKCS12".equals(type))) {
				Security.removeProvider("BC");
			}
			return null;
		} finally {
			if (fis != null) {
				fis.close();
			}
		}
	}

	public static void printSysInfo() {
		LogUtil.writeLog("================= SYS INFO begin====================");
		LogUtil.writeLog("os_name:" + System.getProperty("os.name"));
		LogUtil.writeLog("os_arch:" + System.getProperty("os.arch"));
		LogUtil.writeLog("os_version:" + System.getProperty("os.version"));
		LogUtil.writeLog("java_vm_specification_version:"
				+ System.getProperty("java.vm.specification.version"));
		LogUtil.writeLog("java_vm_specification_vendor:"
				+ System.getProperty("java.vm.specification.vendor"));
		LogUtil.writeLog("java_vm_specification_name:"
				+ System.getProperty("java.vm.specification.name"));
		LogUtil.writeLog("java_vm_version:"
				+ System.getProperty("java.vm.version"));
		LogUtil.writeLog("java_vm_name:" + System.getProperty("java.vm.name"));
		LogUtil.writeLog("java.version:" + System.getProperty("java.version"));
		printProviders();
		LogUtil.writeLog("================= SYS INFO end=====================");
	}

	public static void printProviders() {
		LogUtil.writeLog("Providers List:");
		Provider[] providers = Security.getProviders();
		for (int i = 0; i < providers.length; i++) {
			LogUtil.writeLog(i + 1 + "." + providers[i].getName());
		}
	}

	static class CerFilter implements FilenameFilter {
		public boolean isCer(String name) {
			if (name.toLowerCase().endsWith(".cer")) {
				return true;
			}
			return false;
		}

		public boolean accept(File dir, String name) {
			return isCer(name);
		}
	}

	/**
	 * @deprecated
	 */
	public static String getCertIdByThreadLocal(String certPath, String certPwd) {
		initSignCert(certPath, certPwd);
		try {
			Enumeration<String> aliasenum = ((KeyStore) certKeyStoreLocal.get())
					.aliases();
			String keyAlias = null;
			if (aliasenum.hasMoreElements()) {
				keyAlias = (String) aliasenum.nextElement();
			}
			X509Certificate cert = (X509Certificate) ((KeyStore) certKeyStoreLocal
					.get()).getCertificate(keyAlias);
			return cert.getSerialNumber().toString();
		} catch (Exception e) {
			LogUtil.writeErrorLog("获取签名证书的序列号失败", e);
		}
		return "";
	}

	public static String getCertIdByKeyStoreMap(String certPath, String certPwd) {
		if (!certKeyStoreMap.containsKey(certPath)) {
			loadRsaCert(certPath, certPwd);
		}
		return getCertIdIdByStore((KeyStore) certKeyStoreMap.get(certPath));
	}

	private static String getCertIdIdByStore(KeyStore keyStore) {
		Enumeration<String> aliasenum = null;
		try {
			aliasenum = keyStore.aliases();
			String keyAlias = null;
			if (aliasenum.hasMoreElements()) {
				keyAlias = (String) aliasenum.nextElement();
			}
			X509Certificate cert = (X509Certificate) keyStore
					.getCertificate(keyAlias);
			return cert.getSerialNumber().toString();
		} catch (KeyStoreException e) {
			LogUtil.writeErrorLog("getCertIdIdByStore Error", e);
		}
		return null;
	}

	public static Map<String, X509Certificate> getCertMap() {
		return certMap;
	}

	public static void setCertMap(Map<String, X509Certificate> certMap) {
		CertUtil.certMap = certMap;
	}

	public static PublicKey getPublicKey(String modulus, String exponent) {
		try {
			BigInteger b1 = new BigInteger(modulus);
			BigInteger b2 = new BigInteger(exponent);
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			RSAPublicKeySpec keySpec = new RSAPublicKeySpec(b1, b2);
			return keyFactory.generatePublic(keySpec);
		} catch (Exception e) {
			LogUtil.writeErrorLog("构造RSA公钥失败：" + e);
		}
		return null;
	}

	public static PublicKey getEncryptTrackCertPublicKey(String modulus,
			String exponent) {
		if ((SDKUtil.isEmpty(modulus)) || (SDKUtil.isEmpty(exponent))) {
			LogUtil.writeErrorLog("[modulus] OR [exponent] invalid");
			return null;
		}
		return getPublicKey(modulus, exponent);
	}
}
