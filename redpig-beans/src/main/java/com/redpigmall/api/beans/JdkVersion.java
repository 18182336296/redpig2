package com.redpigmall.api.beans;

public class JdkVersion {
	public static final int JDK_1_2 = -1;
	public static final int JDK_1_3 = 0;
	public static final int JDK_1_4 = 1;
	public static final int JDK_1_5 = 2;
	public static final int JDK_1_6 = 3;
	public static final int JDK_1_7 = 4;
	private static final String version = System.getProperty("java.version");
	private static final int javaVersion;

	static {
		if (version.indexOf("1.7.") != -1) {
			javaVersion = 4;
		} else if (version.indexOf("1.6.") != -1) {
			javaVersion = 3;
		} else if (version.indexOf("1.5.") != -1) {
			javaVersion = 2;
		} else if (version.indexOf("1.4.") != -1) {
			javaVersion = 1;
		} else if (version.indexOf("1.3.") != -1) {
			javaVersion = 0;
		} else {
			javaVersion = -1;
		}
	}

	public static String getVersion() {
		return version;
	}

	public static int getJavaVersion() {
		return javaVersion;
	}

	public static boolean isAnnoatationSupport() {
		return javaVersion >= 2;
	}
}
