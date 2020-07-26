package com.redpigmall.api.enums;

public enum Lang {

	zh_cn("zh_cn","中文"),
	
	yn("yn","Indonesia"),
	
	en("en","English");
	
	private Lang(String lang, String ctx) {
		this.lang = lang;
		this.ctx = ctx;
	}

	private String lang;
	
	private String ctx;

	public String getLang() {
		return lang;
	}

	public void setLang(String lang) {
		this.lang = lang;
	}

	public String getCtx() {
		return ctx;
	}

	public void setCtx(String ctx) {
		this.ctx = ctx;
	}
	
	
}
