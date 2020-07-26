package com.redpigmall.module.weixin.view.tools;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

@Component
public class EmojiTools {
	/**
	 * 检测是否有emoji字符
	 * 
	 * @param source
	 * @return 一旦含有就抛出
	 */
	public static boolean containsEmoji(String source) {
		if (StringUtils.isBlank(source)) {
			return false;
		}
		int len = source.length();
		for (int i = 0; i < len; i++) {
			char codePoint = source.charAt(i);
			if (isEmojiCharacter(codePoint)) {
				return true;
			}
		}
		return false;
	}

	private static boolean isEmojiCharacter(char codePoint) {
		return (codePoint == 0) || (codePoint == '\t') || (codePoint == '\n')
				|| (codePoint == '\r')
				|| ((codePoint >= ' ') && (codePoint <= 55295))
				|| ((codePoint >= 57344) && (codePoint <= 65533))
				|| ((codePoint >= 65536) && (codePoint <= 1114111));
	}
	/**
	 * 过滤emoji 或者 其他非文字类型的字符
	 * 
	 * @param source
	 * @return
	 */
	public static String filterEmoji(String source) {
		if (!containsEmoji(source)) {
			return source;
		}
		StringBuilder buf = null;

		int len = source.length();
		for (int i = 0; i < len; i++) {
			char codePoint = source.charAt(i);
			if (isEmojiCharacter(codePoint)) {
				if (buf == null) {
					buf = new StringBuilder(source.length());
				}
				buf.append(codePoint);
			}
		}
		if (buf == null) {
			return source;
		}
		if (buf.length() == len) {
			buf = null;
			return source;
		}
		return buf.toString();
	}
}
