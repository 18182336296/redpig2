package com.redpigmall.api.beans.propertyeditors;

import java.beans.PropertyEditorSupport;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

@SuppressWarnings("rawtypes")
public class PropertiesEditor extends PropertyEditorSupport {
	private static final String COMMENT_MARKERS = "#!";

	public void setAsText(String text) throws IllegalArgumentException {
		if (text == null) {
			throw new IllegalArgumentException("Cannot set Properties to null");
		}
		Properties props = new Properties();
		try {
			props.load(new ByteArrayInputStream(text.getBytes("ISO-8859-1")));
			dropComments(props);
		} catch (IOException ex) {
			throw new IllegalArgumentException("Failed to parse [" + text
					+ "] into Properties: " + ex.getMessage());
		}
		setValue(props);
	}

	@SuppressWarnings("unchecked")
	public void setValue(Object value) {
		if ((!(value instanceof Properties)) && ((value instanceof Map))) {
			Properties props = new Properties();
			props.putAll((Map) value);
			super.setValue(props);
		} else {
			super.setValue(value);
		}
	}

	private void dropComments(Properties props) {
		Iterator keys = props.keySet().iterator();
		while (keys.hasNext()) {
			String key = (String) keys.next();
			if ((key.length() > 0) && (COMMENT_MARKERS.indexOf(key.charAt(0)) != -1)) {
				keys.remove();
			}
		}
	}
}
