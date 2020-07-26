package com.redpigmall.api.beans.propertyeditors;

import java.beans.PropertyEditorSupport;

import com.redpigmall.api.beans.ClassUtils;
import com.redpigmall.api.beans.StringUtils;

@SuppressWarnings({ "rawtypes" })
public class ClassArrayEditor extends PropertyEditorSupport {
	private final ClassLoader classLoader;

	public ClassArrayEditor() {
		this(null);
	}

	public ClassArrayEditor(ClassLoader classLoader) {
		this.classLoader = (classLoader != null ? classLoader : ClassUtils
				.getDefaultClassLoader());
	}

	public void setAsText(String text) throws IllegalArgumentException {
		if (StringUtils.hasText(text)) {
			String[] classNames = StringUtils
					.commaDelimitedListToStringArray(text);
			Class[] classes = new Class[classNames.length];
			for (int i = 0; i < classNames.length; i++) {
				String className = classNames[i];
				try {
					classes[i] = ClassUtils.forName(className.trim(),
							this.classLoader);
				} catch (ClassNotFoundException ex) {
					throw new IllegalArgumentException("Class not found: "
							+ ex.getMessage());
				}
			}
			setValue(classes);
		} else {
			setValue(null);
		}
	}

	public String getAsText() {
		Class[] classes = (Class[]) getValue();
		if (classes == null) {
			return "";
		}
		return StringUtils.arrayToCommaDelimitedString(classes);
	}
}
