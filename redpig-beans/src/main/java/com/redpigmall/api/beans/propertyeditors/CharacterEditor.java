package com.redpigmall.api.beans.propertyeditors;

import java.beans.PropertyEditorSupport;

import com.redpigmall.api.beans.StringUtils;

@SuppressWarnings({ "unused" })
public class CharacterEditor extends PropertyEditorSupport {
	private static final String UNICODE_PREFIX = "\\u";
	private static final int UNICODE_LENGTH = 6;
	private final boolean allowEmpty;

	public CharacterEditor(boolean allowEmpty) {
		this.allowEmpty = allowEmpty;
	}

	public void setAsText(String text) throws IllegalArgumentException {
		if ((this.allowEmpty) && (!StringUtils.hasText(text))) {
			setValue(null);
		} else {
			if (text == null) {
				throw new IllegalArgumentException(
						"null String cannot be converted to char type");
			}
			if (isUnicodeCharacterSequence(text)) {
				setAsUnicode(text);
			} else {
				if (text.length() != 1) {
					throw new IllegalArgumentException("String [" + text
							+ "] with length " + text.length()
							+ " cannot be converted to char type");
				}
				setValue(new Character(text.charAt(0)));
			}
		}
	}

	public String getAsText() {
		Object value = getValue();
		return value != null ? value.toString() : "";
	}

	private void setAsUnicode(String text) {
		int code = Integer.parseInt(text.substring("\\u".length()), 16);
		setValue(new Character((char) code));
	}

	private static boolean isUnicodeCharacterSequence(String sequence) {
		return (sequence.startsWith("\\u")) && (sequence.length() == 6);
	}
}
