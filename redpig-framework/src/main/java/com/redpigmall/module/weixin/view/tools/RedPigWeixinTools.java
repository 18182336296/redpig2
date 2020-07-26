package com.redpigmall.module.weixin.view.tools;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.redpigmall.api.tools.CommUtil;
import com.redpigmall.lucene.LuceneVo;
import com.redpigmall.domain.ReplyContent;
import com.redpigmall.service.RedPigSysConfigService;

/**
 * 
 * <p>
 * Title: RedPigWeixinTools.java
 * </p>
 * 
 * <p>
 * Description:解析微信xml工具类
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
 * @date 2014-12-20
 * 
 * @version redpigmall_b2b2c 8.0
 */
@Component
public class RedPigWeixinTools {

	@Autowired
	private RedPigSysConfigService configService;


	public Map<String, String> parse_xml(String xml) {
		Map<String,String> map = Maps.newHashMap();
		if (!CommUtil.null2String(xml).equals("")) {
			try {
				Document doc = DocumentHelper.parseText(xml);
				String ToUserName = doc.selectSingleNode("xml/ToUserName") != null ? doc
						.selectSingleNode("xml/ToUserName").getText() : "";
				String FromUserName = doc.selectSingleNode("xml/FromUserName") != null ? doc
						.selectSingleNode("xml/FromUserName").getText() : "";
				String CreateTime = doc.selectSingleNode("xml/CreateTime") != null ? doc
						.selectSingleNode("xml/CreateTime").getText() : "";
				String MsgType = doc.selectSingleNode("xml/MsgType") != null ? doc
						.selectSingleNode("xml/MsgType").getText() : "";
				String Content = doc.selectSingleNode("xml/Content") != null ? doc
						.selectSingleNode("xml/Content").getText() : "";
				String MsgId = doc.selectSingleNode("xml/MsgId") != null ? doc
						.selectSingleNode("xml/MsgId").getText() : "";
				String Event = doc.selectSingleNode("xml/Event") != null ? doc
						.selectSingleNode("xml/Event").getText() : "";
				String EventKey = doc.selectSingleNode("xml/EventKey") != null ? doc
						.selectSingleNode("xml/EventKey").getText() : "";
				String Latitude = doc.selectSingleNode("xml/Latitude") != null ? doc
						.selectSingleNode("xml/Latitude").getText() : "";
				String Longitude = doc.selectSingleNode("xml/Longitude") != null ? doc
						.selectSingleNode("xml/Longitude").getText() : "";
				String Location_X = doc.selectSingleNode("xml/Location_X") != null ? doc
						.selectSingleNode("xml/Location_X").getText() : "";
				String Location_Y = doc.selectSingleNode("xml/Location_Y") != null ? doc
						.selectSingleNode("xml/Location_Y").getText() : "";
				String scene_id = doc.selectSingleNode("xml/scene_id") != null ? doc
						.selectSingleNode("xml/scene_id").getText() : "";
				String user_id = doc.selectSingleNode("xml/user_id") != null ? doc
						.selectSingleNode("xml/user_id").getText() : "";
				String MediaID = doc.selectSingleNode("xml/MediaID") != null ? doc
						.selectSingleNode("xml/MediaID").getText() : "";
				String Format = doc.selectSingleNode("xml/Format") != null ? doc
						.selectSingleNode("xml/Format").getText() : "";
				String Recognition = doc.selectSingleNode("xml/Recognition") != null ? doc
						.selectSingleNode("xml/Recognition").getText() : "";
				String MsgID = doc.selectSingleNode("xml/MsgID") != null ? doc
						.selectSingleNode("xml/MsgID").getText() : "";
				String Encrypt = doc.selectSingleNode("xml/Encrypt") != null ? doc
						.selectSingleNode("xml/Encrypt").getText() : "";
				map.put("ToUserName", ToUserName);
				map.put("FromUserName", FromUserName);
				map.put("CreateTime", CreateTime);
				map.put("MsgType", MsgType);
				map.put("Content", Content);
				map.put("MsgId", MsgId);
				map.put("Event", Event);
				map.put("Latitude", Latitude);
				map.put("Longitude", Longitude);
				map.put("Location_X", Location_X);
				map.put("Location_Y", Location_Y);
				map.put("EventKey", EventKey);
				map.put("scene_id", scene_id);
				map.put("user_id", user_id);
				map.put("MediaID", MediaID);
				map.put("Format", Format);
				map.put("Recognition", Recognition);
				map.put("MsgID", MsgID);
				map.put("Encrypt", Encrypt);
			} catch (DocumentException e) {
				e.printStackTrace();
			}
		}
		return map;
	}

	@SuppressWarnings("unused")
	public String reply_xml(String reply_type, Map<String, String> map,
			String content, HttpServletRequest request) {
		Document doc = DocumentHelper.createDocument();
		Element root = doc.addElement("xml");
		if (reply_type == null) {
			reply_type = "";
		}
		if ((reply_type.equals("text")) || (reply_type.equals("event"))) {
			Element ToUserName = root.addElement("ToUserName");
			ToUserName.addCDATA((String) map.get("FromUserName"));
			Element FromUserName = root.addElement("FromUserName");
			FromUserName.addCDATA((String) map.get("ToUserName"));
			Element CreateTime = root.addElement("CreateTime");
			CreateTime.setText((String) map.get("CreateTime"));
			Element MsgType = root.addElement("MsgType");
			MsgType.addCDATA("text");
			if ("findany".equals(map.get("EventKey"))) {
				Element Content = root.addElement("Content");
				Content.addCDATA(content);
			}
			System.out.println((String) map.get("Event"));
			if (("click".equals(map.get("Event")))
					|| ("CLICK".equals(map.get("Event")))) {
				Element Content = root.addElement("Content");
				Content.addCDATA(content);
			}
			if ("@乐呐喊".equals(map.get("Content"))) {
				Element Content = root.addElement("Content");
				Content.addCDATA(content);
			}
			if ("打印".equals(map.get("Content"))) {
				Element Content = root.addElement("Content");
				Content.addCDATA(content);
			}
		}
		if (reply_type.equals("news")) {
			Element ToUserName = root.addElement("ToUserName");
			ToUserName.addCDATA((String) map.get("FromUserName"));
			Element FromUserName = root.addElement("FromUserName");
			FromUserName.addCDATA((String) map.get("ToUserName"));
			Element CreateTime = root.addElement("CreateTime");
			CreateTime.setText((String) map.get("CreateTime"));
			Element MsgType = root.addElement("MsgType");
			MsgType.addCDATA("news");
			Element ArticleCount = root.addElement("ArticleCount");
			ArticleCount.setText("1");
			Element Articles = root.addElement("Articles");
			Element item = Articles.addElement("item");
			Element Title = item.addElement("Title");
			Element Description = item.addElement("Description");
			Element PicUrl = item.addElement("PicUrl");
			Element localElement1 = item.addElement("Url");
		}
		return doc.asXML().replaceAll("<\\?xml version=\"1.0\" encoding=\"UTF-8\"\\?>","").trim();
	}

	public String reply_news_xml(String reply_type, Map<String, String> map,
			String title, String description, String picUrl, String url,
			HttpServletRequest request) {
		Document doc = DocumentHelper.createDocument();
		Element root = doc.addElement("xml");
		if (reply_type == null) {
			reply_type = "";
		}
		if (reply_type.equals("news")) {
			Element ToUserName = root.addElement("ToUserName");
			ToUserName.addCDATA((String) map.get("FromUserName"));
			Element FromUserName = root.addElement("FromUserName");
			FromUserName.addCDATA((String) map.get("ToUserName"));
			Element CreateTime = root.addElement("CreateTime");
			CreateTime.setText((String) map.get("CreateTime"));
			Element MsgType = root.addElement("MsgType");
			MsgType.addCDATA("news");
			Element ArticleCount = root.addElement("ArticleCount");
			ArticleCount.setText("1");
			Element Articles = root.addElement("Articles");
			Element item = Articles.addElement("item");
			Element Title = item.addElement("Title");
			Title.addCDATA(title);
			Element Description = item.addElement("Description");
			Description.addCDATA(description);
			Element PicUrl = item.addElement("PicUrl");
			PicUrl.addCDATA(picUrl);
			Element Url = item.addElement("Url");
			Url.addCDATA(url);
		}
		return

		doc.asXML()
				.replaceAll("<\\?xml version=\"1.0\" encoding=\"UTF-8\"\\?>",
						"").trim();
	}

	public String reply_listnews_xml(String reply_type,
			Map<String, String> map, String count, List<LuceneVo> list,
			HttpServletRequest request) {
		String appId = CommUtil.null2String(this.configService.getSysConfig()
				.getWeixin_appId());
		Document doc = DocumentHelper.createDocument();
		Element root = doc.addElement("xml");
		if (reply_type == null) {
			reply_type = "";
		}
		if (reply_type.equals("news")) {
			Element ToUserName = root.addElement("ToUserName");
			ToUserName.addCDATA((String) map.get("FromUserName"));
			Element FromUserName = root.addElement("FromUserName");
			FromUserName.addCDATA((String) map.get("ToUserName"));
			Element CreateTime = root.addElement("CreateTime");
			CreateTime.setText((String) map.get("CreateTime"));
			Element MsgType = root.addElement("MsgType");
			MsgType.addCDATA("news");
			Element ArticleCount = root.addElement("ArticleCount");
			ArticleCount.setText(count);
			Element Articles = root.addElement("Articles");
			for (LuceneVo lv : list) {
				Element item = Articles.addElement("item");
				Element Title = item.addElement("Title");
				String title = lv.getVo_title();
				title = title.replaceAll("<[A-z/ =']*>", "");
				Title.addCDATA(title);
				Element Description = item.addElement("Description");
				Description.addCDATA(lv.getVo_content());
				Element PicUrl = item.addElement("PicUrl");
				PicUrl.addCDATA(CommUtil.getURL(request) + "/"
						+ lv.getVo_main_photo_url());
				Element Url = item.addElement("Url");
				Url.addCDATA("https://open.weixin.qq.com/connect/oauth2/authorize?appid="
						+ appId
						+ "&redirect_uri="
						+ CommUtil.getURL(request)
						+ "/catchopenid&response_type=code&scope=snsapi_base&state="
						+ "goods_"
						+ lv.getVo_id().toString()
						+ "#wechat_redirect");
			}
		}
		return

		doc.asXML()
				.replaceAll("<\\?xml version=\"1.0\" encoding=\"UTF-8\"\\?>",
						"").trim();
	}

	public String reply_list_xml(String reply_type, Map<String, String> map,
			String count, List<ReplyContent> list, HttpServletRequest request) {
//		String appId = CommUtil.null2String(this.configService.getSysConfig().getWeixin_appId());
		Document doc = DocumentHelper.createDocument();
		Element root = doc.addElement("xml");
		if (reply_type == null) {
			reply_type = "";
		}
		if (reply_type.equals("news")) {
			Element ToUserName = root.addElement("ToUserName");
			ToUserName.addCDATA((String) map.get("FromUserName"));
			Element FromUserName = root.addElement("FromUserName");
			FromUserName.addCDATA((String) map.get("ToUserName"));
			Element CreateTime = root.addElement("CreateTime");
			CreateTime.setText((String) map.get("CreateTime"));
			Element MsgType = root.addElement("MsgType");
			MsgType.addCDATA("news");
			Element ArticleCount = root.addElement("ArticleCount");
			ArticleCount.setText(count);
			Element Articles = root.addElement("Articles");
			String user_name = (String) map.get("FromUserName");
			int size = list.size();
			for (ReplyContent rc : list) {
				Element item = Articles.addElement("item");
				Element Title = item.addElement("Title");
				Element Description = item.addElement("Description");
				Element PicUrl = item.addElement("PicUrl");
				Element Url = item.addElement("Url");
				System.out.println("user_name:" + user_name);
				Url.addCDATA("http://www.redpigmall.net/wap/reply?id="
						+ rc.getId() + "&user_name=" + user_name);
				int way = rc.getWay();
				if (way == 1) {
					PicUrl.addCDATA(CommUtil.getURL(request) + "/"
							+ rc.getImg().getPath() + "/"
							+ rc.getImg().getName());
					String title = rc.getTitle();
					title = title.replaceAll("<[A-z/ =']*>", "");
					Title.addCDATA(title);
					String digest = rc.getDigest();
					digest = digest.replaceAll("<[A-z/ =']*>", "");
					Description.addCDATA(digest);
				} else {
					PicUrl.addCDATA("");
					if (size > 1) {
						String title = rc.getTitle();
						title = title.replaceAll("<[A-z/ =']*>", "");
						String digest = rc.getDigest();
						digest = digest.replaceAll("<[A-z/ =']*>", "");
						Title.addCDATA(title + "\n\t" + digest);
					} else {
						String title = rc.getTitle();
						title = title.replaceAll("<[A-z/ =']*>", "");
						Title.addCDATA(title);
						String digest = rc.getDigest();
						digest = digest.replaceAll("<[A-z/ =']*>", "");
						Description.addCDATA(digest);
					}
				}
			}
		}
		return

		doc.asXML()
				.replaceAll("<\\?xml version=\"1.0\" encoding=\"UTF-8\"\\?>",
						"").trim();
	}

	public String decryptMsg(String msgSignature, String timeStamp,
			String nonce, String encrypt_msg, String encodingAesKey) {
		String appId = CommUtil.null2String(this.configService.getSysConfig()
				.getWeixin_appId());
		String token = CommUtil.null2String(this.configService.getSysConfig()
				.getWeixin_token());
		String result = "";
		try {
			WXBizMsgCrypt pc = new WXBizMsgCrypt(token, encodingAesKey, appId);
			result = pc.DecryptMsg(msgSignature, timeStamp, nonce, encrypt_msg);
		} catch (AesException e) {
			e.printStackTrace();
		}
		return result;
	}

	public String ecryptMsg(String replayMsg, String timeStamp, String nonce,
			String encodingAesKey) {
		String appId = CommUtil.null2String(this.configService.getSysConfig()
				.getWeixin_appId());
		String token = CommUtil.null2String(this.configService.getSysConfig()
				.getWeixin_token());
		String result = "";
		try {
			WXBizMsgCrypt pc = new WXBizMsgCrypt(token, encodingAesKey, appId);
			result = pc.EncryptMsg(replayMsg, timeStamp, nonce);
		} catch (AesException e) {
			e.printStackTrace();
		}
		return result;
	}

	@SuppressWarnings({ "unused", "rawtypes", "unchecked" })
	public void send(HttpServletRequest request, String open_id,
			String content, String msgtype) {
		String appId = CommUtil.null2String(this.configService.getSysConfig()
				.getWeixin_appId());
		String secret = CommUtil.null2String(this.configService.getSysConfig()
				.getWeixin_appSecret());
		String action = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid="
				+ appId + "&secret=" + secret;

		String message = "";
		try {
			URL urlGet = new URL(action);
			HttpURLConnection http = (HttpURLConnection) urlGet
					.openConnection();
			http.setRequestMethod("GET");
			http.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded");
			http.setDoOutput(true);
			http.setDoInput(true);
			System.setProperty("sun.net.client.defaultConnectTimeout", "30000");
			System.setProperty("sun.net.client.defaultReadTimeout", "30000");
			System.setProperty("jsse.enableSNIExtension", "false");
			http.connect();
			InputStream is = http.getInputStream();
			int size = is.available();
			byte[] jsonBytes = new byte[size];
			is.read(jsonBytes);
			message = new String(jsonBytes, "UTF-8");
			Map<String, Object> map = JSON.parseObject(message);
			String access_token = (String) map.get("access_token");
			String json = "";
			List<Map> js = Lists.newArrayList();
			if ("text".equals(msgtype)) {
				Map cmap = new LinkedHashMap();
				Map m = Maps.newHashMap();
				m.put("content", content);
				cmap.put("touser", open_id);
				cmap.put("msgtype", "text");
				cmap.put("text", m);
				js.add(cmap);
			}
			String send_action = "https://api.weixin.qq.com/cgi-bin/message/custom/send?access_token="
					+ access_token;
			send_post(send_action, js);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("rawtypes")
	public void send_post(String send_action, List json) {
		try {
			URL url = new URL(send_action);
			HttpURLConnection connection = (HttpURLConnection) url
					.openConnection();
			connection.setDoOutput(true);
			connection.setDoInput(true);
			connection.setRequestMethod("POST");
			connection.setUseCaches(false);
			connection.setInstanceFollowRedirects(true);
			connection.setRequestProperty("Accept-Charset", "utf-8");
			connection.setRequestProperty("Content-Type", "UTF-8");
			connection.connect();
			DataOutputStream out = new DataOutputStream(
					connection.getOutputStream());
			String temp = JSON.toJSONString(json);
			temp = temp.replace("]", "");
			temp = temp.replace("[", "");
			byte[] a = temp.getBytes();
			out.write(a, 0, a.length);
			out.flush();
			out.close();

			BufferedReader reader = new BufferedReader(new InputStreamReader(
					connection.getInputStream()));

			StringBuffer sb = new StringBuffer("");
			String lines;
			while ((lines = reader.readLine()) != null) {
				lines = new String(lines.getBytes(), "utf-8");
				sb.append(lines);
			}
			System.out.println(sb);
			reader.close();

			connection.disconnect();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
