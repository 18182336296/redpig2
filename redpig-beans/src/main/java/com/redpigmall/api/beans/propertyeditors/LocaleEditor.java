package com.redpigmall.api.beans.propertyeditors;

import java.beans.PropertyEditorSupport;

import com.redpigmall.api.beans.StringUtils;

public class LocaleEditor extends PropertyEditorSupport {
	public void setAsText(String text) {
		setValue(StringUtils.parseLocaleString(text));
	}

	public String getAsText() {
		Object value = getValue();
		return value != null ? value.toString() : "";
	}
}
