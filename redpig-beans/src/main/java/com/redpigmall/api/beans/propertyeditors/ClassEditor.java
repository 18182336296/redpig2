package com.redpigmall.api.beans.propertyeditors;

import java.beans.PropertyEditorSupport;

import com.redpigmall.api.beans.ClassUtils;
import com.redpigmall.api.beans.StringUtils;

@SuppressWarnings({ "rawtypes" })
public class ClassEditor extends PropertyEditorSupport {
	private final ClassLoader classLoader;

	public ClassEditor() {
		this(null);
	}

	public ClassEditor(ClassLoader classLoader) {
		this.classLoader = (classLoader != null ? classLoader : ClassUtils
				.getDefaultClassLoader());
	}

	public void setAsText(String text) throws IllegalArgumentException {
		if (StringUtils.hasText(text)) {
			try {
				setValue(ClassUtils.forName(text.trim(), this.classLoader));
			} catch (ClassNotFoundException ex) {
				throw new IllegalArgumentException("Class not found: "
						+ ex.getMessage());
			}
		} else {
			setValue(null);
		}
	}

	public String getAsText() {
		Class clazz = (Class) getValue();
		if (clazz == null) {
			return "";
		}
		return ClassUtils.getQualifiedName(clazz);
	}
}
