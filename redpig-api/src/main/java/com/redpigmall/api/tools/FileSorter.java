package com.redpigmall.api.tools;

import java.io.File;
import java.text.Collator;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;

/**
 * 
 * <p>
 * Title: FileSorter.java
 * </p>
 * 
 * <p>
 * Description: 物理文件排序管理类，该类可以对物理存在的文件进行相关排序
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
public class FileSorter implements Comparator<File> {
	public static final int TYPE_DEFAULT = -1;
	public static final int TYPE_MODIFIED_DATE_DOWN = 1;
	public static final int TYPE_MODIFIED_DATE_UP = 2;
	public static final int TYPE_SIZE_DOWN = 3;
	public static final int TYPE_SIZE_UP = 4;
	public static final int TYPE_NAME = 5;
	public static final int TYPE_DIR = 7;
	private int mType = -1;

	public FileSorter(int type) {
		if ((type < 0) || (type > 7)) {
			type = 7;
		}
		this.mType = type;
	}

	public int compare(File object1, File object2) {
		int result = 0;
		switch (this.mType) {
		case 1:
			result = compareByModifiedDateDown(object1, object2);
			break;
		case 2:
			result = compareByModifiedDateUp(object1, object2);
			break;
		case 3:
			result = compareBySizeDown(object1, object2);
			break;
		case 4:
			result = compareBySizeUp(object1, object2);
			break;
		case 5:
			result = compareByName(object1, object2);
			break;
		case 7:
			result = compareByDir(object1, object2);
			break;
		case 6:
		default:
			result = compareByDir(object1, object2);
		}
		return result;
	}

	private int compareByModifiedDateDown(File object1, File object2) {
		long d1 = object1.lastModified();
		long d2 = object2.lastModified();
		if (d1 == d2) {
			return 0;
		}
		return d1 < d2 ? 1 : -1;
	}

	private int compareByModifiedDateUp(File object1, File object2) {
		long d1 = object1.lastModified();
		long d2 = object2.lastModified();
		if (d1 == d2) {
			return 0;
		}
		return d1 > d2 ? 1 : -1;
	}

	private int compareBySizeDown(File object1, File object2) {
		if ((object1.isDirectory()) && (object2.isDirectory())) {
			return 0;
		}
		if ((object1.isDirectory()) && (object2.isFile())) {
			return -1;
		}
		if ((object1.isFile()) && (object2.isDirectory())) {
			return 1;
		}
		long s1 = object1.length();
		long s2 = object2.length();
		if (s1 == s2) {
			return 0;
		}
		return s1 < s2 ? 1 : -1;
	}

	private int compareBySizeUp(File object1, File object2) {
		if ((object1.isDirectory()) && (object2.isDirectory())) {
			return 0;
		}
		if ((object1.isDirectory()) && (object2.isFile())) {
			return -1;
		}
		if ((object1.isFile()) && (object2.isDirectory())) {
			return 1;
		}
		long s1 = object1.length();
		long s2 = object2.length();
		if (s1 == s2) {
			return 0;
		}
		return s1 > s2 ? 1 : -1;
	}

	private int compareByName(File object1, File object2) {
		Comparator<Object> cmp = Collator.getInstance(Locale.CHINA);

		return cmp.compare(object1.getName(), object2.getName());
	}

	private int compareByDir(File object1, File object2) {
		if ((object1.isDirectory()) && (object2.isFile())) {
			return -1;
		}
		if ((object1.isDirectory()) && (object2.isDirectory())) {
			return compareByName(object1, object2);
		}
		if ((object1.isFile()) && (object2.isDirectory())) {
			return 1;
		}
		return compareByName(object1, object2);
	}

	public static void testmai(String[] args) {
		File[] list = new File("/usr").listFiles();
		Arrays.sort(list, new FileSorter(4));
		printFileArray(list);
	}

	private static void printFileArray(File[] list) {
		
		for (File f : list) {
			System.out.println(f.length() + "\t\t"
					+ new Date(f.lastModified()).toString() + "\t\t"
					+ (f.isDirectory() ? "目录" : "文件") + "\t\t" + f.getName());
		}
	}
}
