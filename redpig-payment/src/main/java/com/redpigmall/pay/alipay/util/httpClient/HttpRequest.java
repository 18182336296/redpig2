package com.redpigmall.pay.alipay.util.httpClient;

import org.apache.commons.httpclient.NameValuePair;
/* *
 *类名：HttpRequest
 *功能：Http请求对象的封装
 *详细：封装Http请求
 *版本：3.2
 *日期：2011-03-17
 *说明：
 *以下代码只是为了方便商户测试而提供的样例代码，商户可以根据自己网站的需要，按照技术文档编写,并非一定要使用该代码。
 *该代码仅供学习和研究支付宝接口使用，只是提供一个参考。
 */
public class HttpRequest {
	/** HTTP GET method */
	public static final String METHOD_GET = "GET";

	/** HTTP POST method */
	public static final String METHOD_POST = "POST";

	/**
	 * 待请求的url
	 */
	private String url = null;

	/**
	 * 默认的请求方式
	 */
	private String method = "POST";
	
	
	private int timeout = 0;
	
	
	private int connectionTimeout = 0;
	
	/**
	 * Post方式请求时组装好的参数值对
	 */
	private NameValuePair[] parameters = null;
	
	/**
	 * Get方式请求时对应的参数
	 */
	private String queryString = null;
	
	/**
	 * 默认的请求编码方式
	 */
	private String charset = "GBK";
	
	/**
	 * 请求发起方的ip地址
	 */
	private String clientIp;

	/**
	 * 请求返回的方式
	 */
	private HttpResultType resultType = HttpResultType.BYTES;

	public HttpRequest(HttpResultType resultType) {
		this.resultType = resultType;
	}

	public String getClientIp() {
		return this.clientIp;
	}

	public void setClientIp(String clientIp) {
		this.clientIp = clientIp;
	}

	public NameValuePair[] getParameters() {
		return this.parameters;
	}

	public void setParameters(NameValuePair[] parameters) {
		this.parameters = parameters;
	}

	public String getQueryString() {
		return this.queryString;
	}

	public void setQueryString(String queryString) {
		this.queryString = queryString;
	}

	public String getUrl() {
		return this.url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getMethod() {
		return this.method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public int getConnectionTimeout() {
		return this.connectionTimeout;
	}

	public void setConnectionTimeout(int connectionTimeout) {
		this.connectionTimeout = connectionTimeout;
	}

	public int getTimeout() {
		return this.timeout;
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	public String getCharset() {
		return this.charset;
	}

	public void setCharset(String charset) {
		this.charset = charset;
	}

	public HttpResultType getResultType() {
		return this.resultType;
	}

	public void setResultType(HttpResultType resultType) {
		this.resultType = resultType;
	}
}
