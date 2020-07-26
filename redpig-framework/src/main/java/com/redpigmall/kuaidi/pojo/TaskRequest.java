package com.redpigmall.kuaidi.pojo;

import java.util.HashMap;
import java.util.Map;

import com.google.common.collect.Maps;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.mapper.DefaultMapper;

/**
 * 
 * <p>
 * Title: TaskRequest.java
 * </p>
 * 
 * <p>
 * Description: 该实体类来自快递100提供的接口
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
 * @date 2014-12-9
 * 
 * @version redpigmall_b2b2c 2016
 */
public class TaskRequest {
	private static XStream xstream;
	private String company;
	private String number;
	private String from;
	private String to;
	private String key;
	private String src;
	private HashMap<String, String> parameters = Maps.newHashMap();

	public String getCompany() {
		return this.company;
	}

	public void setCompany(String company) {
		this.company = company;
	}

	public String getNumber() {
		return this.number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getFrom() {
		return this.from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getTo() {
		return this.to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public String getKey() {
		return this.key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getSrc() {
		return this.src;
	}

	public void setSrc(String src) {
		this.src = src;
	}

	public HashMap<String, String> getParameters() {
		return this.parameters;
	}

	public void setParameters(HashMap<String, String> parameters) {
		this.parameters = parameters;
	}

	@SuppressWarnings("deprecation")
	private static XStream getXStream() {
		if (xstream == null) {
			xstream = new XStream();
			xstream.registerConverter(new MapCustomConverter(new DefaultMapper(
					XStream.class.getClassLoader())));
			xstream.autodetectAnnotations(true);
			xstream.alias("orderRequest", TaskRequest.class);
			xstream.alias("property", Map.Entry.class);
		}
		return xstream;
	}

	public String toXml() {
		return "<?xml version='1.0' encoding='UTF-8'?>\r\n"
				+ getXStream().toXML(this);
	}

	public static TaskRequest fromXml(String sXml) {
		return (TaskRequest) getXStream().fromXML(sXml);
	}
}
