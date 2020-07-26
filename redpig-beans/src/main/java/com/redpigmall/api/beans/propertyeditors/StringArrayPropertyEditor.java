package com.redpigmall.api.beans.propertyeditors;

import java.beans.PropertyEditorSupport;

import com.redpigmall.api.beans.StringUtils;

public class StringArrayPropertyEditor extends PropertyEditorSupport {
	public static final String DEFAULT_SEPARATOR = ",";
	private final String separator;

	public StringArrayPropertyEditor() {
		this.separator = ",";
	}

	public StringArrayPropertyEditor(String separator) {
		this.separator = separator;
	}

	public void setAsText(String text) throws IllegalArgumentException {
		String[] array = StringUtils.delimitedListToStringArray(text,
				this.separator);
		setValue(array);
	}

	public String getAsText() {
		String[] array = (String[]) getValue();
		return StringUtils.arrayToDelimitedString(array, this.separator);
	}
}
