package com.redpigmall.kuaidi.pojo;

import java.util.ArrayList;

import org.codehaus.jackson.annotate.JsonIgnore;

import com.google.common.collect.Lists;
@SuppressWarnings({ "unchecked", "rawtypes" })
public class LastResult {
	@JsonIgnore
	private String nu = "";
	@JsonIgnore
	private String ischeck = "";
	@JsonIgnore
	private String com = "";
	@JsonIgnore
	private ArrayList<ResultItem> data = Lists.newArrayList();
	@JsonIgnore
	private String state = "";

	public LastResult clone() {
		LastResult r = new LastResult();
		r.setCom(getCom());
		r.setIscheck(getIscheck());
		r.setNu(getNu());
		r.setState(getState());
		r.setData((ArrayList) getData().clone());
		return r;
	}

	public String getNu() {
		return this.nu;
	}

	public void setNu(String nu) {
		this.nu = nu;
	}

	public String getCom() {
		return this.com;
	}

	public void setCom(String com) {
		this.com = com;
	}

	public ArrayList<ResultItem> getData() {
		return this.data;
	}

	public void setData(ArrayList<ResultItem> data) {
		this.data = data;
	}

	public String getIscheck() {
		return this.ischeck;
	}

	public void setIscheck(String ischeck) {
		this.ischeck = ischeck;
	}

	public String getState() {
		return this.state;
	}

	public void setState(String state) {
		this.state = state;
	}
}
