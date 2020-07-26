package com.redpigmall.api.beans.propertyeditors;

import java.beans.PropertyEditorSupport;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import com.google.common.collect.Lists;

@SuppressWarnings({"rawtypes","unchecked"})
public class DateEditor extends PropertyEditorSupport {
	private DateFormat format;
	private List formats = Lists.newArrayList();

	public DateEditor() {
		this.format = new SimpleDateFormat("yyyy-MM-dd H:m:s");
		this.formats.add(new SimpleDateFormat());
		this.formats.add(new SimpleDateFormat("yyyy-MM-dd"));
	}

	public DateEditor(String formatText) {
		this.format = new SimpleDateFormat(formatText);
	}

	public DateEditor(DateFormat format) {
		this.format = format;
	}

	public void addFormats(DateFormat format) {
		this.formats.add(format);
	}

	public void setAsText(String text) throws IllegalArgumentException {
		if ((text == null) || ("".equals(text))) {
			return;
		}
		try {
			setValue(this.format.parse(text));
		} catch (ParseException e) {
			boolean op = false;
			for (int i = 0; i < this.formats.size(); i++) {
				try {
					DateFormat ft = (DateFormat) this.formats.get(i);
					setValue(ft.parse(text));
					op = true;
				} catch (ParseException localParseException1) {
				}
			}
			if (!op) {
				throw new IllegalArgumentException("日期格式不正确，不能正确解析: "
						+ e.getMessage());
			}
		}
	}
}
