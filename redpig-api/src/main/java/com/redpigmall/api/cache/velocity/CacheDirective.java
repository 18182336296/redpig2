package com.redpigmall.api.cache.velocity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;

import com.redpigmall.redis.RedisCache;
import org.apache.velocity.context.InternalContextAdapter;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.exception.TemplateInitException;
import org.apache.velocity.runtime.RuntimeServices;
import org.apache.velocity.runtime.directive.Directive;
import org.apache.velocity.runtime.parser.node.Node;
import org.apache.velocity.runtime.parser.node.SimpleNode;


import com.redpigmall.api.tools.CommUtil;
import com.redpigmall.api.tools.Md5Encrypt;

public class CacheDirective extends Directive {
	private static String region = "redpigmall";
	private static String mark = "@@";
	private static boolean flush = true;
	private static String prefix = "_old";

	public String getName() {
		return "cache";
	}

	public int getType() {
		return 1;
	}

	public void init(RuntimeServices rs, InternalContextAdapter context,
			Node node) throws TemplateInitException {
		super.init(rs, context, node);
		region = rs.getString("userdirective.cache.region", "redpigmall");
		flush = rs.getBoolean("userdirective.cache.flush", true);
	}

	public boolean render(
			InternalContextAdapter context, 
			Writer writer,
			Node node) 
			throws 	IOException, 
					ResourceNotFoundException,
					ParseErrorException, 
					MethodInvocationException {
		

		
		int node_size = node.jjtGetNumChildren();
		String node_type = "html";
		
		if (node_size == 4) {
			SimpleNode timenode = (SimpleNode) node.jjtGetChild(2);
			node_type = CommUtil.null2String(timenode.value(context));
		}
		SimpleNode sn_key = (SimpleNode) node.jjtGetChild(0);
		String key = (String) sn_key.value(context);
		Node body = node.jjtGetChild(node_size - 1);
		String tpl_key = key + mark + region;
		Object cache_html = RedisCache.getObject(tpl_key);
		if (flush) {
			String body_tpl = Md5Encrypt.md5(body.literal()).toLowerCase();
			Object old_body_tpl = RedisCache.getObject(tpl_key + prefix);
			if ((cache_html == null) || (!body_tpl.equals(CommUtil.null2String(old_body_tpl)))) {
				if (node_type.equals("html")) {
					StringWriter sw = new StringWriter();
					body.render(context, sw);
					cache_html = sw.toString();
					RedisCache.putObject(tpl_key, cache_html);

					RedisCache.putObject(tpl_key + prefix, body_tpl);
				}
				if (node_type.equals("url")) {
					cache_html = getHttpContent(key, "UTF-8", "POST");
					RedisCache.putObject(tpl_key, cache_html);
					RedisCache.putObject(tpl_key + prefix, body_tpl);
				}
				if (node_type.equals("script")) {
					cache_html =

					"<script>" + getHttpContent(key, "UTF-8", "POST")
							+ "</script>";
					RedisCache.putObject(tpl_key, cache_html);
					RedisCache.putObject(tpl_key + prefix, body_tpl);
				}
			}
		} else if (cache_html == null) {
			if (node_type.equals("html")) {
				StringWriter sw = new StringWriter();
				body.render(context, sw);
				cache_html = sw.toString();
				RedisCache.putObject(tpl_key, cache_html);
			}
			if (node_type.equals("url")) {
				cache_html = getHttpContent(key, "UTF-8", "POST");
				RedisCache.putObject(tpl_key, cache_html);
			}
			if (node_type.equals("script")) {
				cache_html = "<script>" + getHttpContent(key, "UTF-8", "POST") + "</script>";
				RedisCache.putObject(tpl_key, cache_html);
			}
		}
		writer.write(cache_html.toString());
		return true;
	}
	
	public static String getHttpContent(
			String url, 
			String charSet,
			String method) {
		
		HttpURLConnection connection = null;
		String content = "";
		try {
			URL address_url = new URL(url);
			connection = (HttpURLConnection) address_url.openConnection();
			connection.setRequestMethod("GET");
			connection.setConnectTimeout(1000000);
			connection.setReadTimeout(1000000);
			
			int response_code = connection.getResponseCode();
			if (response_code == 200) {
				InputStream in = connection.getInputStream();
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(in, charSet));
				String line = null;
				while ((line = reader.readLine()) != null) {
					content = content + line;
				}
				return content;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (connection != null) {
				connection.disconnect();
			}
		}

		return "";
	}
}
