package com.redpigmall.pay.unionpay.acp.sdk;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

public class HttpClient {
	private URL url;
	private int connectionTimeout;
	private int readTimeOut;
	private String result;

	public String getResult() {
		return this.result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public HttpClient(String url, int connectionTimeout, int readTimeOut) {
		try {
			this.url = new URL(url);
			this.connectionTimeout = connectionTimeout;
			this.readTimeOut = readTimeOut;
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}

	public int send(Map<String, String> data, String encoding) throws Exception {
		try {
			HttpURLConnection httpURLConnection = createConnection(encoding);
			if (httpURLConnection == null) {
				throw new Exception("创建联接失败");
			}
			requestServer(httpURLConnection,
					getRequestParamString(data, encoding), encoding);
			this.result = response(httpURLConnection, encoding);
			LogUtil.writeLog("同步返回报文:[" + this.result + "]");
			return httpURLConnection.getResponseCode();
		} catch (Exception e) {
			throw e;
		}
	}

	private void requestServer(java.net.URLConnection connection,
			String message, String encoder) throws Exception {

	}

	private String response(HttpURLConnection connection, String encoding)
			throws URISyntaxException, IOException, Exception {
		InputStream in = null;
		StringBuilder sb = new StringBuilder(1024);
		BufferedReader br = null;
		try {
			if (200 == connection.getResponseCode()) {
				in = connection.getInputStream();
				sb.append(new String(read(in), encoding));
			} else {
				in = connection.getErrorStream();
				sb.append(new String(read(in), encoding));
			}
			LogUtil.writeLog("HTTP Return Status-Code:["
					+ connection.getResponseCode() + "]");
			return sb.toString();
		} catch (Exception e) {
			throw e;
		} finally {
			if (br != null) {
				br.close();
			}
			if (in != null) {
				in.close();
			}
			if (connection != null) {
				connection.disconnect();
			}
		}
	}

	public static byte[] read(InputStream in) throws IOException {
		byte[] buf = new byte['Ѐ'];
		int length = 0;
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		while ((length = in.read(buf, 0, buf.length)) > 0) {
			bout.write(buf, 0, length);
		}
		bout.flush();
		return bout.toByteArray();
	}

	private HttpURLConnection createConnection(String encoding)
			throws ProtocolException {
		HttpURLConnection httpURLConnection = null;
		try {
			httpURLConnection = (HttpURLConnection) this.url.openConnection();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		httpURLConnection.setConnectTimeout(this.connectionTimeout);
		httpURLConnection.setReadTimeout(this.readTimeOut);
		httpURLConnection.setDoInput(true);
		httpURLConnection.setDoOutput(true);
		httpURLConnection.setUseCaches(false);
		httpURLConnection.setRequestProperty("Content-type",
				"application/x-www-form-urlencoded;charset=" + encoding);
		httpURLConnection.setRequestMethod("POST");
		if ("https".equalsIgnoreCase(this.url.getProtocol())) {
			HttpsURLConnection husn = (HttpsURLConnection) httpURLConnection;
			husn.setSSLSocketFactory(new BaseHttpSSLSocketFactory());
			husn.setHostnameVerifier(new BaseHttpSSLSocketFactory.TrustAnyHostnameVerifier());
			return husn;
		}
		return httpURLConnection;
	}

	private String getRequestParamString(Map<String, String> requestParam,
			String coder) {
		if ((coder == null) || ("".equals(coder))) {
			coder = "UTF-8";
		}
		StringBuffer sf = new StringBuffer("");
		String reqstr = "";
		if ((requestParam != null) && (requestParam.size() != 0)) {
			for (Map.Entry<String, String> en : requestParam.entrySet()) {
				try {
					sf.append((String) en.getKey()
							+ "="
							+ ((en.getValue() == null)
									|| ("".equals(en.getValue())) ? ""
									: URLEncoder.encode((String) en.getValue(),
											coder)) + "&");
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
					return "";
				}
			}
			reqstr = sf.substring(0, sf.length() - 1);
		}
		LogUtil.writeLog("请求报文(已做过URLEncode编码):[" + reqstr + "]");
		return reqstr;
	}
}
