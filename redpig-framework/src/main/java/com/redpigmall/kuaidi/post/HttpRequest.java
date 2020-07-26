package com.redpigmall.kuaidi.post;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;

import com.google.common.collect.Lists;

/**
 * 
 * <p>
 * Title: HttpRequest.java
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
 * @version redpigmall_b2b2c v8.0 2016版
 */
public class HttpRequest {
	public static String addUrl(String head, String tail) {
		if (head.endsWith("/")) {
			if (tail.startsWith("/")) {
				return head.substring(0, head.length() - 1) + tail;
			}
			return head + tail;
		}
		if (tail.startsWith("/")) {
			return head + tail;
		}
		return head + "/" + tail;
	}

	public static synchronized String postData(String url,
			Map<String, String> params, String codePage)
			throws java.lang.Exception {

		final HttpClient httpClient = new HttpClient();
		httpClient.getHttpConnectionManager().getParams()
				.setConnectionTimeout(10 * 1000);
		httpClient.getHttpConnectionManager().getParams()
				.setSoTimeout(10 * 1000);

		final PostMethod method = new PostMethod(url);
		if (params != null) {
			method.getParams().setParameter(
					HttpMethodParams.HTTP_CONTENT_CHARSET, codePage);
			method.setRequestBody(assembleRequestParams(params));
		}
		String result = "";
		try {
			httpClient.executeMethod(method);
			result = new String(method.getResponseBody(), codePage);
		} catch (final Exception e) {
			throw e;
		} finally {
			method.releaseConnection();
		}
		return result;
	}
	public static synchronized String postData(String url, String codePage)
			throws java.lang.Exception {
		final HttpClient httpClient = new HttpClient();
		httpClient.getHttpConnectionManager().getParams()
				.setConnectionTimeout(10 * 1000);
		httpClient.getHttpConnectionManager().getParams()
				.setSoTimeout(10 * 1000);

		final GetMethod method = new GetMethod(url);
		String result = "";
		try {
			httpClient.executeMethod(method);
			result = new String(method.getResponseBody(), codePage);
		} catch (final Exception e) {
			throw e;
		} finally {
			method.releaseConnection();
		}
		return result;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static synchronized NameValuePair[] assembleRequestParams(
			Map<String, String> data) {
		List<NameValuePair> nameValueList = Lists.newArrayList();

		Iterator<Map.Entry<String, String>> it = data.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<String, String> entry = (Map.Entry) it.next();
			nameValueList.add(new NameValuePair((String) entry.getKey(),
					(String) entry.getValue()));
		}
		return (NameValuePair[]) nameValueList
				.toArray(new NameValuePair[nameValueList.size()]);
	}
}
