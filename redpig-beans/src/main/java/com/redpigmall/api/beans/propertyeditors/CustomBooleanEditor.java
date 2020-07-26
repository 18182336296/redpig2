package com.redpigmall.api.beans.propertyeditors;

import java.beans.PropertyEditorSupport;

import com.redpigmall.api.beans.StringUtils;

public class CustomBooleanEditor extends PropertyEditorSupport {
	public static final String VALUE_TRUE = "true";
	public static final String VALUE_FALSE = "false";
	public static final String VALUE_ON = "on";
	public static final String VALUE_OFF = "off";
	public static final String VALUE_YES = "yes";
	public static final String VALUE_NO = "no";
	public static final String VALUE_1 = "1";
	public static final String VALUE_0 = "0";
	private final String trueString;
	private final String falseString;
	private final boolean allowEmpty;

	public CustomBooleanEditor(boolean allowEmpty) {
		this(null, null, allowEmpty);
	}

	public CustomBooleanEditor(String trueString, String falseString,
			boolean allowEmpty) {
		this.trueString = trueString;
		this.falseString = falseString;
		this.allowEmpty = allowEmpty;
	}

	public void setValue(Object value) {
		if ((value instanceof Number)) {
			if (((Number) value).intValue() > 0) {
				super.setValue(Boolean.TRUE);
			} else {
				super.setValue(Boolean.FALSE);
			}
		} else {
			super.setValue(value);
		}
	}

	public void setAsText(String text) throws IllegalArgumentException {
		if ((this.allowEmpty) && (!StringUtils.hasText(text))) {
			setValue(null);
		} else if ((this.trueString != null)
				&& (text.equalsIgnoreCase(this.trueString))) {
			setValue(Boolean.TRUE);
		} else if ((this.falseString != null)
				&& (text.equalsIgnoreCase(this.falseString))) {
			setValue(Boolean.FALSE);
		} else if ((this.trueString == null)
				&& ((text.equalsIgnoreCase("true"))
						|| (text.equalsIgnoreCase("on"))
						|| (text.equalsIgnoreCase("yes")) || (text.equals("1")))) {
			setValue(Boolean.TRUE);
		} else if ((this.falseString == null)
				&& ((text.equalsIgnoreCase("false"))
						|| (text.equalsIgnoreCase("off"))
						|| (text.equalsIgnoreCase("no")) || (text.equals("0")))) {
			setValue(Boolean.FALSE);
		} else {
			throw new IllegalArgumentException("Invalid boolean value [" + text
					+ "]");
		}
	}

	public String getAsText() {
		if (Boolean.TRUE.equals(getValue())) {
			return this.trueString != null ? this.trueString : "true";
		}
		if (Boolean.FALSE.equals(getValue())) {
			return this.falseString != null ? this.falseString : "false";
		}
		return "";
	}
}
