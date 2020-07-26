package com.redpigmall.api.tools;

import java.io.StringReader;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;

/**
 * 
 * <p>
 * Title: XMLUtil.java
 * </p>
 * 
 * <p>
 * Description: XML解析工具类，用来解析xml数据并封装为对应的数据格式，如解析XML数据封装为map，解析xml为json等等
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2014
 * </p>
 * 
 * <p>
 * Company: www.redpigmall.net
 * </p>
 * 
 * @author redpig
 * 
 * @date 2014-5-19
 * 
 * @version redpigmall_b2b2c v8.0 2016版
 */
public class XMLUtil {
	/**
	 * 解析xml数据，并将xml数据所有节点和值封装到map中返回，试用试用map.get("noteName")读取xml中节点值，
	 * 该方法只适合节点不重复的简单xml解析，系统用在充值接口回调数据的解析
	 * 
	 * @param xml
	 * @param igore_null
	 *            是否忽略空值节点
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static Map parseXML(String xml, boolean igore_null) {
		Map<String, Object> map = Maps.newHashMap();
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder
					.parse(new InputSource(new StringReader(xml)));
			Element root = doc.getDocumentElement();
			NodeList optionNodeList = root.getChildNodes();
			if (optionNodeList != null) {
				int totalNode = optionNodeList.getLength();
				for (int i = 0; i < totalNode; i++) {
					Node optionNode = optionNodeList.item(i);
					if (igore_null) {
						if (!CommUtil.null2String(optionNode.getTextContent())
								.equals("")) {
							map.put(optionNode.getNodeName(), CommUtil
									.null2String(optionNode.getTextContent()));
						}
					} else {
						map.put(optionNode.getNodeName(), CommUtil
								.null2String(optionNode.getTextContent()));
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}

	/**
	 * 将封装好的map转化为json数据并返回
	 * 
	 * @param map
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static String Map2Json(Map map) {
		String json = JSON.toJSONString(map);
		return json;
	}
}
