package com.redpigmall.api.beans.propertyeditors;

import java.beans.PropertyEditorSupport;
import java.text.NumberFormat;

import com.redpigmall.api.beans.NumberUtils;
import com.redpigmall.api.beans.StringUtils;

@SuppressWarnings("rawtypes")
public class CustomNumberEditor extends PropertyEditorSupport {
	private final Class numberClass;
	private final NumberFormat numberFormat;
	private final boolean allowEmpty;

	public CustomNumberEditor(Class numberClass, boolean allowEmpty)
			throws IllegalArgumentException {
		this(numberClass, null, allowEmpty);
	}

	public CustomNumberEditor(Class numberClass, NumberFormat numberFormat,
			boolean allowEmpty) throws IllegalArgumentException {
		if ((numberClass == null)
				|| (!Number.class.isAssignableFrom(numberClass))) {
			throw new IllegalArgumentException(
					"Property class must be a subclass of Number");
		}
		this.numberClass = numberClass;
		this.numberFormat = numberFormat;
		this.allowEmpty = allowEmpty;
	}

	public void setAsText(String text) throws IllegalArgumentException {
		if ((this.allowEmpty) && (!StringUtils.hasText(text))) {
			setValue(null);
		} else if (this.numberFormat != null) {
			setValue(NumberUtils.parseNumber(text, this.numberClass,
					this.numberFormat));
		} else {
			setValue(NumberUtils.parseNumber(text, this.numberClass));
		}
	}

	public String getAsText() {
		Object value = getValue();
		if (value == null) {
			return "";
		}
		if (this.numberFormat != null) {
			return this.numberFormat.format(value);
		}
		return value.toString();
	}
}
