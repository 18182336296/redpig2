package com.redpigmall.filter;

import java.util.Enumeration;
import java.util.Map;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

/**
 * 
 * <p>
 * Title: JsRequest.java
 * </p>
 * 
 * <p>
 * Description:获取参数 js 脚本注入转义
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2015
 * </p>
 * 
 * <p>
 * Company: www.redpigmall.net
 * </p>
 * 
 * @author redpig
 * 
 * @date 2014-12-4
 * 
 * @version redpigmall_b2b2c 8.0
 */
@SuppressWarnings({"rawtypes","unchecked"})
public class JsRequest extends HttpServletRequestWrapper {
	private Map params;

	public JsRequest(HttpServletRequest request, Map newParams) {
		super(request);
		this.params = newParams;
	}

	public Map getParameterMap() {
		return this.params;
	}

	public Enumeration getParameterNames() {
		Vector l = new Vector(this.params.keySet());
		return l.elements();
	}

	public String[] getParameterValues(String name) {
		Object v = this.params.get(name);
		if (v == null) {
			return null;
		}
		if ((v instanceof String[])) {
			String[] value = (String[]) v;
			for (int i = 0; i < value.length; i++) {
				value[i] = value[i].replaceAll("<", "&lt;");
				value[i] = value[i].replaceAll(">", "&gt;");
			}
			return value;
		}
		if ((v instanceof String)) {
			String value = (String) v;
			value = value.replaceAll("<", "&lt;");
			value = value.replaceAll(">", "&gt;");
			return new String[] { value };
		}
		return new String[] { v.toString() };
	}

	public String getParameter(String name) {
		Object v = this.params.get(name);
		if (v == null) {
			return null;
		}
		if ((v instanceof String[])) {
			String[] strArr = (String[]) v;
			if (strArr.length > 0) {
				String value = strArr[0];
				value = value.replaceAll("<", "&lt;");
				value = value.replaceAll("<", "&gt;");
				return value;
			}
			return null;
		}
		if ((v instanceof String)) {
			String value = (String) v;
			value = value.replaceAll("<", "&lt;");
			value = value.replaceAll(">", "&gt;");
			return value;
		}
		return v.toString();
	}
}
