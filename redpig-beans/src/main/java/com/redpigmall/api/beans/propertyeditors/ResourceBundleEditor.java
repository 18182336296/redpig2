package com.redpigmall.api.beans.propertyeditors;

import java.beans.PropertyEditorSupport;
import java.util.Locale;
import java.util.ResourceBundle;

import com.redpigmall.api.beans.Assert;
import com.redpigmall.api.beans.StringUtils;

public class ResourceBundleEditor extends PropertyEditorSupport {
	public static final String BASE_NAME_SEPARATOR = "_";

	public void setAsText(String text) throws IllegalArgumentException {
		Assert.hasText(text);

		String rawBaseName = text.trim();
		int indexOfBaseNameSeparator = rawBaseName.indexOf("_");
		ResourceBundle bundle;
		if (indexOfBaseNameSeparator == -1) {
			bundle = ResourceBundle.getBundle(rawBaseName);
		} else {
			String baseName = rawBaseName
					.substring(0, indexOfBaseNameSeparator);
			if (!StringUtils.hasText(baseName)) {
				throw new IllegalArgumentException(
						"Bad ResourceBundle name : received '" + text
								+ "' as argument to 'setAsText(String value)'.");
			}
			String localeString = rawBaseName
					.substring(indexOfBaseNameSeparator + 1);
			Locale locale = StringUtils.parseLocaleString(localeString);
			bundle = StringUtils.hasText(localeString) ? ResourceBundle
					.getBundle(baseName, locale) : ResourceBundle
					.getBundle(baseName);
		}
		setValue(bundle);
	}
}
