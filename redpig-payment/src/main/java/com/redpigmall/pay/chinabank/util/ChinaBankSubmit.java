package com.redpigmall.pay.chinabank.util;

import java.util.List;

import com.redpigmall.api.domain.virtual.SysMap;
import com.redpigmall.api.tools.CommUtil;

public class ChinaBankSubmit {
	public static String buildForm(List<SysMap> list) {
		StringBuffer sb = new StringBuffer();
		sb.append("<body onLoad=\"javascript:document.E_FORM.submit()\">");
		sb.append("<form action=\"https://pay3.chinabank.com.cn/PayGate\" method=\"POST\" name=\"E_FORM\">");
		for (SysMap sm : list) {
			sb.append("<input type=\"hidden\" name=\""
					+ CommUtil.null2String(sm.getKey()) + "\"    value=\""
					+ CommUtil.null2String(sm.getValue()) + "\" size=\"100\">");
		}
		sb.append("</form><body>");
		return sb.toString();
	}
}
