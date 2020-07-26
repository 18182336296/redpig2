package com.redpigmall.manage.admin.action;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.lucene.search.Sort;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.redpigmall.api.annotation.SecurityMapping;
import com.redpigmall.api.mv.RedPigJModelAndView;
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.api.tools.ClusterSyncTools;
import com.redpigmall.api.tools.CommUtil;
import com.redpigmall.api.tools.Md5Encrypt;
import com.redpigmall.api.tools.WebForm;
import com.redpigmall.lucene.LuceneResult;
import com.redpigmall.lucene.LuceneUtil;
import com.redpigmall.lucene.LuceneVo;
import com.redpigmall.manage.admin.action.base.BaseAction;
import com.redpigmall.module.weixin.view.tools.EmojiTools;
import com.redpigmall.module.weixin.view.tools.GetWxToken;
import com.redpigmall.domain.Accessory;
import com.redpigmall.domain.Goods;
import com.redpigmall.domain.Group;
import com.redpigmall.domain.ReplyContent;
import com.redpigmall.domain.Role;
import com.redpigmall.domain.SysConfig;
import com.redpigmall.domain.User;
import com.redpigmall.domain.VMenu;

/**
 * 
 * <p>
 * Title: RedPigWeixinManageAction.java
 * </p>
 * 
 * <p>
 * Description:后台微商城管理
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
 * @date 2014-11-20
 * 
 * @version redpigmall_b2b2c 8.0
 */
@SuppressWarnings({ "unchecked", "rawtypes", "unused" })
@Controller
public class RedPigWeixinManageAction extends BaseAction {

	/**
	 * 微商城配置
	 * 
	 * @param request
	 * @param response
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "微商城配置", value = "/weixin_plat_set*", rtype = "admin", rname = "微信基本设置", rcode = "weixin_plat_admin", rgroup = "运营")
	@RequestMapping({ "/weixin_plat_set" })
	public ModelAndView weixin_plat_set(HttpServletRequest request,
			HttpServletResponse response, String currentPage) {
		ModelAndView mv = new RedPigJModelAndView(
				"admin/blue/weixin_plat_set.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		return mv;
	}

	/**
	 * 基本设置保存
	 * 
	 * @param request
	 * @param response
	 * @param id
	 * @param weixin_account
	 * @param weixin_token
	 * @param weixin_appId
	 * @param weixin_appSecret
	 * @param weixin_welecome_content
	 * @param weixin_store
	 * @return
	 */
	@SecurityMapping(title = "基本设置保存", value = "/weixin_plat_set_save*", rtype = "admin", rname = "微信基本设置", rcode = "weixin_plat_admin", rgroup = "运营")
	@RequestMapping({ "/weixin_plat_set_save" })
	public ModelAndView weixin_plat_set_saveEntity(HttpServletRequest request,
			HttpServletResponse response, String id, String weixin_account,
			String weixin_token, String weixin_appId, String weixin_appSecret,
			String weixin_welecome_content, String weixin_store) {

		SysConfig obj = this.configService.getSysConfig();

		SysConfig sysConfig = null;
		if (id.equals("")) {
			sysConfig = (SysConfig) WebForm.toPo(request, SysConfig.class);
			sysConfig.setAddTime(new Date());
		} else {
			sysConfig = (SysConfig) WebForm.toPo(request, obj);
		}

		sysConfig.setWeixin_store(CommUtil.null2Int(weixin_store));
		sysConfig.setWeixin_account(weixin_account);
		sysConfig.setWeixin_token(weixin_token);
		sysConfig.setWeixin_appId(weixin_appId);
		sysConfig.setWeixin_appSecret(weixin_appSecret);
		sysConfig.setWeixin_welecome_content(weixin_welecome_content);

		String uploadFilePath = sysConfig.getUploadFilePath();
		String saveFilePathName = request.getSession().getServletContext()
				.getRealPath("/")
				+ uploadFilePath + File.separator + "system";

		Map<String, Object> map = Maps.newHashMap();
		try {
			String fileName = sysConfig.getStore_weixin_logo() == null ? ""
					: sysConfig.getStore_weixin_logo().getName();
			map = CommUtil.saveFileToServer(request, "store_weixin_logo",
					saveFilePathName, fileName, null);
			if (fileName.equals("")) {
				if (map.get("fileName") != "") {
					Accessory store_weixin_logo = new Accessory();
					store_weixin_logo.setName(CommUtil.null2String(map
							.get("fileName")));
					store_weixin_logo.setExt((String) map.get("mime"));
					store_weixin_logo.setSize(BigDecimal.valueOf(Double
							.parseDouble(map.get("fileSize").toString())));
					store_weixin_logo.setPath(uploadFilePath + "/system");
					store_weixin_logo.setWidth(((Integer) map.get("width"))
							.intValue());
					store_weixin_logo.setHeight(((Integer) map.get("height"))
							.intValue());
					store_weixin_logo.setAddTime(new Date());
					this.accessoryService.saveEntity(store_weixin_logo);
					sysConfig.setStore_weixin_logo(store_weixin_logo);
				}
			} else if (map.get("fileName") != "") {
				Accessory store_weixin_logo = sysConfig.getStore_weixin_logo();
				store_weixin_logo.setName(CommUtil.null2String(map
						.get("fileName")));
				store_weixin_logo.setExt(CommUtil.null2String(map.get("mime")));
				store_weixin_logo.setSize(BigDecimal.valueOf(Double
						.parseDouble(map.get("fileSize").toString())));
				store_weixin_logo.setPath(uploadFilePath + "/system");
				store_weixin_logo.setWidth(CommUtil.null2Int(map.get("width")));
				store_weixin_logo
						.setHeight(CommUtil.null2Int(map.get("height")));
				this.accessoryService.updateById(store_weixin_logo);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		map.clear();
		try {
			map = CommUtil.saveFileToServer(request, "qr_img",
					saveFilePathName, null, null);
			String fileName = sysConfig.getWeixin_qr_img() != null ? sysConfig
					.getWeixin_qr_img().getName() : "";
			if (fileName.equals("")) {
				if (map.get("fileName") != "") {
					Accessory qr_img = new Accessory();
					qr_img.setName(CommUtil.null2String(map.get("fileName")));
					qr_img.setExt(CommUtil.null2String(map.get("mime")));
					qr_img.setSize(BigDecimal.valueOf(Double.parseDouble(map
							.get("fileSize").toString())));
					qr_img.setPath(uploadFilePath + "/system");
					qr_img.setWidth(CommUtil.null2Int(map.get("width")));
					qr_img.setHeight(CommUtil.null2Int(map.get("heigh")));
					qr_img.setAddTime(new Date());
					this.accessoryService.saveEntity(qr_img);
					sysConfig.setWeixin_qr_img(qr_img);
				}
			} else if (map.get("fileName") != "") {
				Accessory qr_img = sysConfig.getWeixin_qr_img();
				qr_img.setName(CommUtil.null2String(map.get("fileName")));
				qr_img.setExt(CommUtil.null2String(map.get("mime")));
				qr_img.setSize(BigDecimal.valueOf(Double.parseDouble(map.get(
						"fileSize").toString())));
				qr_img.setPath(uploadFilePath + "/system");
				qr_img.setWidth(CommUtil.null2Int(map.get("width")));
				qr_img.setHeight(CommUtil.null2Int(map.get("height")));
				this.accessoryService.updateById(qr_img);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		map.clear();
		try {
			String fileName = sysConfig.getWelcome_img() == null ? ""
					: sysConfig.getWelcome_img().getName();
			map = CommUtil.saveFileToServer(request, "welcome_img",
					saveFilePathName, fileName, null);
			if (fileName.equals("")) {
				if (map.get("fileName") != "") {
					Accessory wel_img = new Accessory();
					wel_img.setName(CommUtil.null2String(map.get("fileName")));
					wel_img.setExt((String) map.get("mime"));
					wel_img.setSize(BigDecimal.valueOf(Double.parseDouble(map
							.get("fileSize").toString())));
					wel_img.setPath(uploadFilePath + "/system");
					wel_img.setWidth(((Integer) map.get("width")).intValue());
					wel_img.setHeight(((Integer) map.get("height")).intValue());
					wel_img.setAddTime(new Date());
					this.accessoryService.saveEntity(wel_img);
					sysConfig.setWelcome_img(wel_img);
				}
			} else if (map.get("fileName") != "") {
				Accessory wel_img = sysConfig.getWelcome_img();
				wel_img.setName(CommUtil.null2String(map.get("fileName")));
				wel_img.setExt(CommUtil.null2String(map.get("mime")));
				wel_img.setSize(BigDecimal.valueOf(Double.parseDouble(map.get(
						"fileSize").toString())));
				wel_img.setPath(uploadFilePath + "/system");
				wel_img.setWidth(CommUtil.null2Int(map.get("width")));
				wel_img.setHeight(CommUtil.null2Int(map.get("height")));
				this.accessoryService.updateById(wel_img);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (id.equals("")) {
			this.configService.saveEntity(sysConfig);
		} else {
			this.configService.updateById(sysConfig);
		}
		ModelAndView mv = new RedPigJModelAndView("admin/blue/success.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		mv.addObject("op_title", "基本设置成功");
		mv.addObject("list_url", CommUtil.getURL(request)
				+ "/weixin_plat_set");
		return mv;
	}

	/**
	 * 微信客户端商品
	 * 
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @param goods_name
	 * @param query_type
	 * @return
	 */
	@SecurityMapping(title = "微信客户端商品", value = "/weixin_goods*", rtype = "admin", rname = "微商城商品", rcode = "admin_weixin_goods", rgroup = "运营")
	@RequestMapping({ "/weixin_goods" })
	public ModelAndView weixin_goods(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,
			String orderType, String goods_name, String query_type) {

		ModelAndView mv = new RedPigJModelAndView(
				"admin/blue/weixin_goods.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);

		Map<String, Object> params = this.redPigQueryTools.getParams(
				currentPage, orderBy, orderType);

		params.put("goods_status", 0);

		if ((goods_name != null) && (!goods_name.equals(""))) {
			params.put("goods_name_like", goods_name);

		}
		if ((query_type != null) && (!query_type.equals(""))) {
			if (query_type.equals("0")) {
				params.put("weixin_hot", 1);
			}

			if (query_type.equals("1")) {
				params.put("weixin_recommend", 1);
			}
		}

		IPageList pList = this.redPigGoodsService.list(params);
		CommUtil.saveIPageList2ModelAndView("", "", null, pList, mv);
		mv.addObject("goods_name", goods_name);
		mv.addObject("query_type", query_type);
		return mv;
	}

	/**
	 * 微信客户端商品
	 * 
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @param goods_name
	 * @param query_type
	 * @return
	 */
	@SecurityMapping(title = "微信客户端商品", value = "/weixin_items*", rtype = "admin", rname = "微商城商品", rcode = "admin_weixin_goods", rgroup = "运营")
	@RequestMapping({ "/weixin_items" })
	public ModelAndView weixin_items(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,
			String orderType, String goods_name, String query_type) {

		ModelAndView mv = new RedPigJModelAndView(
				"admin/blue/weixin_goods.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);

		Map<String, Object> params = this.redPigQueryTools.getParams(
				currentPage, orderBy, orderType);

		params.put("goods_status", 0);

		if ((goods_name != null) && (!goods_name.equals(""))) {
			params.put("goods_name_like", goods_name);

		}

		if ((query_type != null) && (!query_type.equals(""))) {
			if (query_type.equals("0")) {
				params.put("weixin_hot", 1);

			}

			if (query_type.equals("1")) {
				params.put("weixin_recommend", 1);
			}
		}
		
		IPageList pList = this.redPigGoodsService.list(params);
		CommUtil.saveIPageList2ModelAndView("", "", null, pList, mv);
		mv.addObject("goods_name", goods_name);
		mv.addObject("query_type", query_type);
		return mv;
	}

	/**
	 * 微信商品AJAX更新
	 * 
	 * @param request
	 * @param response
	 * @param id
	 * @param fieldName
	 */
	@SecurityMapping(title = "微信商品AJAX更新", value = "/weixin_goods_ajax*", rtype = "admin", rname = "微商城商品", rcode = "admin_weixin_goods", rgroup = "运营")
	@RequestMapping({ "/weixin_goods_ajax" })
	public void weixin_goods_ajax(HttpServletRequest request,
			HttpServletResponse response, String id, String fieldName) {

		Goods obj = this.redPigGoodsService.selectByPrimaryKey(Long.valueOf(Long
				.parseLong(id)));

		boolean val = false;
		if (fieldName.equals("weixin_recommend")) {
			if (obj.getWeixin_recommend() == 1) {
				obj.setWeixin_recommend(0);
				obj.setWeixin_recommendTime(null);
				val = false;
			} else {
				obj.setWeixin_recommend(1);
				obj.setWeixin_recommendTime(new Date());
				val = true;
			}
		}

		if (fieldName.equals("weixin_hot")) {
			if (obj.getWeixin_hot() == 1) {
				obj.setWeixin_hot(0);
				obj.setWeixin_hotTime(null);
				val = false;
			} else {
				obj.setWeixin_hot(1);
				obj.setWeixin_hotTime(new Date());
				val = true;
			}
		}

		this.redPigGoodsService.updateById(obj);
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		try {
			PrintWriter writer = response.getWriter();
			writer.print(val);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 微信生活购购商品列表
	 * 
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @param gg_name
	 * @param weixin_recommend
	 * @return
	 */
	@SecurityMapping(title = "微信生活购购商品列表", value = "/weixin_grouplifegoods*", rtype = "admin", rname = "微商城团购", rcode = "admin_weixin_group", rgroup = "运营")
	@RequestMapping({ "/weixin_grouplifegoods" })
	public ModelAndView weixin_grouplifegoods(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,
			String orderType, String gg_name, String weixin_recommend) {

		ModelAndView mv = new RedPigJModelAndView(
				"admin/blue/weixin_grouplifegoods.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);

		Map<String, Object> params = Maps.newHashMap();
		params.put("beginTime", new Date());
		params.put("endTime", new Date());
		params.put("status", Integer.valueOf(0));
		params.put("group_type", 1);
		List<Group> groups = this.groupService.queryPageList(params);

		if (groups.size() > 0) {

			params = this.redPigQueryTools.getParams(currentPage, orderBy,
					orderType);
			params.put("group_id", groups.get(0).getId());
			params.put("group_status", 1);

			Date today = new Date();
			params.put("beginTime", today);
			params.put("endTime", today);

			if ((gg_name != null) && (!gg_name.equals(""))) {
				params.put("gg_name_like", CommUtil.null2String(gg_name));

			}
			if ((weixin_recommend != null) && (!weixin_recommend.equals(""))) {
				boolean recommend = false;
				if (weixin_recommend.equals("1")) {
					recommend = true;
				}
				params.put("weixin_shop_recommend", Boolean.valueOf(recommend));
			}

			IPageList pList = this.grouplifeGoodsService.list(params);
			CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
			mv.addObject("gg_name", gg_name);
			mv.addObject("weixin_recommend", weixin_recommend);
		}
		return mv;
	}

	/**
	 * 微信生活购购商品列表
	 * 
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @param gg_name
	 * @param weixin_recommend
	 * @return
	 */

	@SecurityMapping(title = "微信生活购购商品列表", value = "/weixin_grouplifeitems*", rtype = "admin", rname = "微商城团购", rcode = "admin_weixin_group", rgroup = "运营")
	@RequestMapping({ "/weixin_grouplifeitems" })
	public ModelAndView weixin_grouplifeitems(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,
			String orderType, String gg_name, String weixin_recommend) {

		ModelAndView mv = new RedPigJModelAndView(
				"admin/blue/weixin_grouplifegoods.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);

		Map<String, Object> params = Maps.newHashMap();
		params.put("beginTime", new Date());
		params.put("endTime", new Date());
		params.put("status", 0);
		params.put("group_type", 1);
		List<Group> groups = this.groupService.queryPageList(params);

		if (groups.size() > 0) {

			params = this.redPigQueryTools.getParams(currentPage, orderBy,
					orderType);

			params.put("group_id", groups.get(0).getId());

			params.put("obj_gg_status", 1);

			Date today = new Date();

			params.put("beginTime", today);
			params.put("endTime", today);

			if ((gg_name != null) && (!gg_name.equals(""))) {
				params.put("gg_name_like", CommUtil.null2String(gg_name));

			}

			if ((weixin_recommend != null) && (!weixin_recommend.equals(""))) {
				boolean recommend = false;
				if (weixin_recommend.equals("1")) {
					recommend = true;
				}
				params.put("weixin_shop_recommend", Boolean.valueOf(recommend));

			}

			IPageList pList = this.grouplifeGoodsService.list(params);
			CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
			mv.addObject("gg_name", gg_name);
			mv.addObject("weixin_recommend", weixin_recommend);
		}
		return mv;
	}

	/**
	 * 
	 * @param request
	 * @param response
	 * @param signature
	 * @param timestamp
	 * @param nonce
	 * @param echostr
	 * @param redriver
	 * @param msg_signature
	 * @param encrypt_type
	 * @throws InterruptedException
	 */
	@RequestMapping({ "/weixin_develop_action" })
	public void weixin_develop_action(HttpServletRequest request,
			HttpServletResponse response, String signature, String timestamp,
			String nonce, String echostr, String redriver,
			String msg_signature, String encrypt_type)
			throws InterruptedException {
		System.out.println("进入请求");
		response.setCharacterEncoding("UTF-8");
		response.setContentType("text/xml");
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(
					request.getInputStream(), "UTF-8"));
			String line = null;
			Map<String, String> map;
			if ((encrypt_type == null) || (encrypt_type.equals("raw"))) {
				StringBuilder sb = new StringBuilder();
				while ((line = br.readLine()) != null) {
					sb.append(line);
				}
				System.out.println("信息：" + sb.toString());
				map = this.weixinTools.parse_xml(sb.toString());
				System.out.println(sb.toString());
			} else {
				StringBuilder sb = new StringBuilder();
				while ((line = br.readLine()) != null) {
					sb.append(line);
				}
				String de = this.weixinTools.decryptMsg(msg_signature,
						timestamp, nonce, sb.toString(), GetWxToken.instance()
								.getEncodingAESKey());
				map = this.weixinTools.parse_xml(de);
			}

			String ToUserName = (String) map.get("ToUserName");
			String FromUserName = (String) map.get("FromUserName");
			String CreateTime = (String) map.get("CreateTime");
			String MsgType = (String) map.get("MsgType");
			String Content = CommUtil.null2String(map.get("Content"));
			System.out.println((String) map.get("Content"));
			String MsgId = (String) map.get("MsgId");
			String Event = CommUtil.null2String(map.get("Event"));
			System.out.println("Event" + Event);
			String EventKey = CommUtil.null2String(map.get("EventKey"));
			System.out.println("EventKey" + EventKey);
			String scene_id = CommUtil.null2String(map.get("scene_id"));
			String user_id = CommUtil.null2String(map.get("user_id"));
			String reply_xml = "";
			String reply_title = "";
			String reply_content = "";
			String reply_bottom = "";
			String web_url = CommUtil.getURL(request);
			String reply_all = this.configService.getSysConfig()
					.getWeixin_welecome_content();
			String reply_info = "";

			PrintWriter writer = response.getWriter();
			if ("text".equals(MsgType)) {
				if ((Content != null) && (!"".equals(Content))
						&& (Content.length() > 1)) {
					String result = Content.trim();
					char ss = result.charAt(0);
					if (('#' == result.charAt(0)) || ('@' == result.charAt(0))) {
						int in = result.length();
						if (in > 1) {
							String sub = result.substring(1, in);
							String path = ClusterSyncTools.getClusterRoot()
									+ File.separator + "luence"
									+ File.separator + "goods";
							LuceneUtil lucene = LuceneUtil.instance();
							LuceneUtil.setIndex_path(path);
							Map<String, Object> params = Maps.newHashMap();
							List temp_list = this.goodsClassService
									.queryGoodsClassByIds(params);

							LuceneUtil.setGc_size(temp_list.size());
							String order_by = null;
							String currentPage = null;
							String goods_inventory = null;
							String goods_type = null;
							String goods_transfee = null;
							String goods_cod = null;
							Sort sort = null;
							String query_gc = "";
							LuceneResult pList = lucene.search(sub,
									CommUtil.null2Int(currentPage),
									goods_inventory, goods_type, query_gc,
									goods_transfee, goods_cod, sort, null,
									null, null, null, null, null);
							List<LuceneVo> list = pList.getVo_list();
							String count = "0";
							if (list.size() > 5) {
								count = "5";
							} else {
								count = Integer.toString(list.size());
							}
							if ("0".equals(count)) {
								reply_info = "未搜索到您需要的商品\n输入#或@加您要搜索的商品或者直接发送语音进行快捷搜索";
								reply_xml = this.weixinTools.reply_xml("text",
										map, reply_info, request);
							} else {
								reply_xml = this.weixinTools
										.reply_listnews_xml("news", map, count,
												list, request);
							}
						} else {
							reply_info = "请你在#或@后填写您要搜索的内容";
							reply_xml = this.weixinTools.reply_xml("text", map,
									reply_info, request);
						}
					} else if ("1".equals(Content)) {
						reply_info = "输入#或@加您要搜索的商品或者直接发送语音进行快捷搜索";
						reply_xml = this.weixinTools.reply_xml("text", map,
								reply_info, request);
					} else {
						reply_info = "输入#或@加您要搜索的商品或者直接发送语音进行快捷搜索";
						reply_xml = this.weixinTools.reply_xml("text", map,
								reply_info, request);
					}
				} else {
					reply_info = "请输入有效的信息，输入#或@加您要搜索的商品或者直接发送语音进行快捷搜索";
					reply_xml = this.weixinTools.reply_xml("text", map,
							reply_info, request);
				}
				System.out.println(reply_xml);
				if ((encrypt_type == null) || (encrypt_type.equals("raw"))) {
					writer.print(reply_xml);
				} else {
					reply_xml = this.weixinTools.ecryptMsg(reply_xml,
							timestamp, nonce, GetWxToken.instance()
									.getEncodingAESKey());
					System.out.println(reply_xml);
					writer.print(reply_xml);
				}
			}
			if ("voice".equals(MsgType)) {
				String Recognition = CommUtil.null2String(map
						.get("Recognition"));
				List<Goods> goods_list = Lists.newArrayList();
				String MediaID = CommUtil.null2String(map.get("MediaID"));
				String Format = CommUtil.null2String(map.get("Format"));
				String MsgID = CommUtil.null2String(map.get("MsgID"));
				if ((Recognition != null) && (!"".equals(Recognition))
						&& (Recognition.length() > 1)) {
					String path = ClusterSyncTools.getClusterRoot()
							+ File.separator + "luence" + File.separator
							+ "goods";
					LuceneUtil lucene = LuceneUtil.instance();
					LuceneUtil.setIndex_path(path);
					Map<String, Object> params = Maps.newHashMap();
					List temp_list = this.goodsClassService
							.queryPageList(params);

					LuceneUtil.setGc_size(temp_list.size());
					String order_by = null;
					String currentPage = null;
					String goods_inventory = null;
					String goods_type = null;
					String goods_transfee = null;
					String goods_cod = null;
					Sort sort = null;
					String query_gc = "";
					LuceneResult pList = lucene.search(Recognition,
							CommUtil.null2Int(currentPage), goods_inventory,
							goods_type, query_gc, goods_transfee, goods_cod,
							sort, null, null, null, null, null, null);
					List<LuceneVo> list = pList.getVo_list();
					String count = "0";
					if (list.size() > 5) {
						count = "5";
					} else {
						int a = list.size();
						count = Integer.toString(a);
					}
					if ("0".equals(count)) {
						reply_info = "未搜索到您需要的商品\n输入#或@加您要搜索的商品或者直接发送语音进行快捷搜索";
						reply_xml = this.weixinTools.reply_xml("text", map,
								reply_info, request);
					} else {
						reply_xml = this.weixinTools.reply_listnews_xml("news",
								map, count, list, request);
					}
				} else {
					reply_info = "未能获取到语音中的关键字，输入#或@加您要搜索的商品或者直接发送语音进行快捷搜索";
					reply_xml = this.weixinTools.reply_xml("text", map,
							reply_info, request);
				}
				if ((encrypt_type == null) || (encrypt_type.equals("raw"))) {
					writer.print(reply_xml);
				} else {
					reply_xml = this.weixinTools.ecryptMsg(reply_xml,
							timestamp, nonce, GetWxToken.instance()
									.getEncodingAESKey());
					System.out.println(reply_xml);
					writer.print(reply_xml);
				}
			}
			if ((!Event.equals("")) && (Event != null)) {
				if (Event.equals("subscribe")) {
					String[] key = EventKey.split("_");
					if (key[0].endsWith("qrscene")) {
						System.out.println("key[0]=" + key[0]);
						System.out.println("key[1]=" + key[1]);

						binding(FromUserName, key[1], request);
					} else {
						reply_all = this.configService.getSysConfig()
								.getWeixin_welecome_content();
						user_add(FromUserName, request);
						send_welcome(map, request);
					}
				}
				if (Event.equals("unsubscribe")) {
					String openid = (String) map.get("FromUserName");
					Map<String, Object> params = Maps.newHashMap();
					params.put("openId", openid);

					List<User> users = this.userService.queryPageList(params);
					if (users.size() == 1) {
						User user = (User) users.get(0);
						user.setOpenId("");
						this.userService.updateById(user);
					}
				}
				if ((Event.equals("click")) || (Event.equals("CLICK"))) {
					Map menu_map = Maps.newHashMap();
					menu_map.put("menu_key", EventKey);
					List<VMenu> vMeuns = this.vMenuService
							.queryPageList(menu_map);

					String count = "0";
					if (vMeuns.size() > 0) {
						Long menu_id = ((VMenu) vMeuns.get(0)).getId();
						Map m = Maps.newHashMap();
						m.put("reply_content_id", menu_id);
						List<ReplyContent> list = this.replycontentService
								.queryPageList(m);

						if (list.size() > 5) {
							count = "5";
						} else {
							int size = list.size();
							count = CommUtil.null2String(Integer.valueOf(size));
						}
						if ("0".equals(count)) {
							reply_all = ((VMenu) vMeuns.get(0))
									.getMenu_key_content();
							reply_xml = this.weixinTools.reply_xml("text", map,
									reply_all, request);
						} else {
							reply_xml = this.weixinTools.reply_list_xml("news",
									map, count, list, request);
						}
					}
				}
				if ((EventKey.equals("findany"))
						|| (EventKey.equals("FINDANY"))) {
					Map menu_map = Maps.newHashMap();
					menu_map.put("menu_key", EventKey);
					List<VMenu> vMeuns = this.vMenuService
							.queryPageList(menu_map);

					if (vMeuns.size() > 0) {
						reply_all = ((VMenu) vMeuns.get(0))
								.getMenu_key_content();
						reply_xml = this.weixinTools.reply_xml("text", map,
								reply_all, request);
					}
				}
			}
			String s = (String) map.get("EventKey");
			if ((echostr != null) && (!echostr.equals(""))) {
				System.out.println(echostr);
				writer.print(echostr);
			} else if ((map.get("EventKey") != null)
					&& (!"".equals(map.get("EventKey")))) {
				if ((encrypt_type == null) || (encrypt_type.equals("raw"))) {
					writer.print(reply_xml);
				} else {
					reply_xml = this.weixinTools.ecryptMsg(reply_xml,
							timestamp, nonce, GetWxToken.instance()
									.getEncodingAESKey());
					writer.print(reply_xml);
				}
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	/**
	 * 微商城菜单配置
	 * 
	 * @param request
	 * @param response
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "微商城菜单配置", value = "/weixin_plat_menu*", rtype = "admin", rname = "微信基本设置", rcode = "weixin_plat_admin", rgroup = "运营")
	@RequestMapping({ "/weixin_plat_menu" })
	public ModelAndView weixin_plat_menu(HttpServletRequest request,
			HttpServletResponse response, String currentPage) {

		ModelAndView mv = new RedPigJModelAndView(
				"admin/blue/weixin_plat_menu.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);

		Map<String, Object> params = Maps.newHashMap();
		params.put("parent", -1);

		List<VMenu> weixin_menus = this.vMenuService.queryPageList(params);

		mv.addObject("weixin_menus", weixin_menus);
		return mv;
	}

	/**
	 * 微商城菜单添加
	 * 
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param menu_id
	 * @param pmenu_id
	 * @return
	 */

	@SecurityMapping(title = "微商城菜单添加", value = "/weixin_menu_add*", rtype = "admin", rname = "微信基本设置", rcode = "weixin_plat_admin", rgroup = "运营")
	@RequestMapping({ "/weixin_menu_add" })
	public ModelAndView weixin_menu_add(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String menu_id,
			String pmenu_id) {

		ModelAndView mv = new RedPigJModelAndView(
				"admin/blue/weixin_menu_add.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		VMenu obj = this.vMenuService.selectByPrimaryKey(CommUtil
				.null2Long(menu_id));
		if ((menu_id != null) && (!"".equals(menu_id))) {
			Map<String, Object> m = Maps.newHashMap();
			m.put("reply_content_id", CommUtil.null2Long(menu_id));
			List<ReplyContent> list = this.replycontentService.queryPageList(m);
			mv.addObject("size", Integer.valueOf(list.size()));
			mv.addObject("list", list);
			mv.addObject("edit", "edit");
		}
		Map<String, Object> params = Maps.newHashMap();
		params.put("way", 0);
		List<ReplyContent> replylist = this.replycontentService
				.queryPageList(params);

		mv.addObject("replylist", replylist);
		params.clear();
		params.put("way", 1);
		List<ReplyContent> rc_list = this.replycontentService
				.queryPageList(params);
		mv.addObject("rc_list", rc_list);
		mv.addObject("obj", obj);

		mv.addObject("pmenu_id", obj == null ? pmenu_id
				: (obj.getParent() != null ? obj.getParent().getId() : ""));
		return mv;
	}

	/**
	 * 微商城菜单Ajax
	 * 
	 * @param request
	 * @param response
	 * @param info
	 * @param way
	 * @param endTime
	 * @param beginTime
	 */

	@SecurityMapping(title = "微商城菜单Ajax", value = "/weixin_menu_ajax*", rtype = "admin", rname = "微信基本设置", rcode = "weixin_plat_admin", rgroup = "运营")
	@RequestMapping({ "/weixin_menu_ajax" })
	public void weixin_menu_ajax(HttpServletRequest request,
			HttpServletResponse response, String info, String way,
			String endTime, String beginTime) {

		if ((endTime != null) && (!"".equals(endTime)) && (beginTime != null)
				&& (!"".equals(beginTime)) && (info != null)
				&& (!"".equals(info))) {
			Map<String, Object> map = Maps.newHashMap();
			map.put("title_like", info);
			map.put("way", Integer.valueOf(CommUtil.null2Int(way)));
			map.put("beginTime", CommUtil.formatDate(beginTime));
			map.put("endTime", CommUtil.formatDate(endTime));

			List<ReplyContent> r_list = this.replycontentService
					.queryPageList(map);

			extrad_menu_ajax(response, r_list);
		} else {
			if ((info != null) && (!"".equals(info))) {
				Map<String, Object> map = Maps.newHashMap();
				map.put("title_like", "%" + info + "%");
				map.put("way", Integer.valueOf(CommUtil.null2Int(way)));
				List<ReplyContent> r_list = this.replycontentService
						.queryPageList(map);
				extrad_menu_ajax(response, r_list);
			}
			if ((endTime != null) && (!"".equals(endTime))
					&& (beginTime != null) && (!"".equals(beginTime))) {
				Map<String, Object> map = Maps.newHashMap();
				map.put("beginTime", CommUtil.formatDate(beginTime));
				map.put("endTime", CommUtil.formatDate(endTime));
				map.put("way", Integer.valueOf(CommUtil.null2Int(way)));
				List<ReplyContent> r_list = this.replycontentService
						.queryPageList(map);

				extrad_menu_ajax(response, r_list);
			}
		}
	}

	private void extrad_menu_ajax(HttpServletResponse response,
			List<ReplyContent> r_list) {
		List<Map> jsonlist = Lists.newArrayList();
		for (ReplyContent rc : r_list) {
			Map m = Maps.newHashMap();
			m.put("id", rc.getId());
			m.put("title", rc.getTitle());
			jsonlist.add(m);
		}
		String temp = JSON.toJSONString(jsonlist);
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		try {
			PrintWriter writer = response.getWriter();
			writer.print(temp);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 微商城菜单保存
	 * 
	 * @param request
	 * @param response
	 * @param menu_id
	 * @param pmenu_id
	 * @param menu_url
	 * @param menu_key_content
	 * @param menu_key
	 * @return
	 * @throws IOException
	 */
	@SecurityMapping(title = "微商城菜单保存", value = "/weixin_menu_save*", rtype = "admin", rname = "微信基本设置", rcode = "weixin_plat_admin", rgroup = "运营")
	@RequestMapping({ "/weixin_menu_save" })
	public String weixin_menu_saveEntity(HttpServletRequest request,
			HttpServletResponse response, String menu_id, String pmenu_id,
			String menu_url, String menu_key_content, String menu_key)
			throws IOException {

		VMenu parent = this.vMenuService.selectByPrimaryKey(CommUtil
				.null2Long(pmenu_id));

		String url = menu_url == null ? "" : menu_url;
		String content = menu_key_content == null ? "" : menu_key_content;
		String appId = CommUtil.null2String(this.configService.getSysConfig()
				.getWeixin_appId());
		String oauth_url = "https://open.weixin.qq.com/connect/oauth2/authorize?appid="
				+ appId
				+ "&redirect_uri="
				+ CommUtil.getURL(request)
				+ "/catchopenid&response_type=code&scope=snsapi_base&state="
				+ menu_key + "#wechat_redirect";
		if (!CommUtil.null2String(menu_id).equals("")) {
			VMenu obj = this.vMenuService.selectByPrimaryKey(CommUtil
					.null2Long(menu_id));
			obj = (VMenu) WebForm.toPo(request, obj);
			if (url.trim().equals(content.trim())) {
				obj.setMenu_url(oauth_url);
				obj.setMenu_key_content(menu_key_content);
			}
			obj.setParent(parent);
			this.vMenuService.updateById(obj);
		} else {
			VMenu obj = (VMenu) WebForm.toPo(request, VMenu.class);
			if (url.trim().equals(content.trim())) {
				obj.setMenu_url(oauth_url);
				obj.setMenu_key_content(menu_key_content);
			}
			obj.setParent(parent);
			this.vMenuService.saveEntity(obj);
		}
		return "redirect:weixin_plat_menu";
	}

	private void extrad(String menu_key_content, String menu_type,
			String reply_id, String way, String rc_id, String r_id,
			String oauth_url, VMenu obj) {
		if ("click".equals(menu_type)) {
			if ((reply_id != null) && (!"".equals(reply_id)) && (way != null)
					&& (!"".equals(way))) {
				String[] ids = reply_id.split(",");
				for (String rid : ids) {
					ReplyContent rc = this.replycontentService
							.selectByPrimaryKey(CommUtil.null2Long(rid));
					int size = obj.getReply_list().size();
					if (size == 0) {
						rc.setReply_content(obj);
						this.replycontentService.updateById(rc);
					} else if (size < 5) {
						for (ReplyContent r : obj.getReply_list()) {
							if (r.getWay() == CommUtil.null2Int(way)) {
								rc.setReply_content(obj);
								this.replycontentService.updateById(rc);
							}
						}
					}
				}
			}
			if ((rc_id != null) && (!"".equals(rc_id)) && (way != null)
					&& (!"".equals(way))) {
				String[] rids = rc_id.split(",");
				for (String rid : rids) {
					ReplyContent rc = this.replycontentService
							.selectByPrimaryKey(CommUtil.null2Long(rid));
					int size = obj.getReply_list().size();
					if (size == 0) {
						rc.setReply_content(obj);
						this.replycontentService.updateById(rc);
					} else if (size < 5) {
						for (ReplyContent r : obj.getReply_list()) {
							if (r.getWay() == CommUtil.null2Int(way)) {
								rc.setReply_content(obj);
								this.replycontentService.updateById(rc);
							}
						}
					}
				}
			}
			if ((r_id != null) && (!"".equals(r_id)) && (way != null)
					&& (!"".equals(way))) {
				String[] ids = r_id.split(",");
				for (String rid : ids) {
					ReplyContent rc = this.replycontentService
							.selectByPrimaryKey(CommUtil.null2Long(rid));
					int size = obj.getReply_list().size();
					if (size == 0) {
						rc.setReply_content(obj);
						this.replycontentService.updateById(rc);
					} else if (size < 5) {
						for (ReplyContent r : obj.getReply_list()) {
							if (r.getWay() == CommUtil.null2Int(way)) {
								rc.setReply_content(obj);
								this.replycontentService.updateById(rc);
							}
						}
					}
				}
			}
		} else {
			obj.setMenu_url(oauth_url);
			obj.setMenu_key_content(menu_key_content);
		}
	}

	/**
	 * 微商城菜单删除
	 * 
	 * @param request
	 * @param response
	 * @param menu_id
	 * @param id
	 * @return
	 * @throws IOException
	 */

	@SecurityMapping(title = "微商城菜单删除", value = "/weixin_menu_delete*", rtype = "admin", rname = "微信基本设置", rcode = "weixin_plat_admin", rgroup = "运营")
	@RequestMapping({ "/weixin_menu_delete" })
	public String weixin_menu_deleteById(HttpServletRequest request,
			HttpServletResponse response, String menu_id, String id)
			throws IOException {

		if (!CommUtil.null2String(menu_id).equals("")) {
			Map<String, Object> map = Maps.newHashMap();
			map.put("reply_content_id", CommUtil.null2Long(menu_id));
			List<ReplyContent> list = this.replycontentService
					.queryPageList(map);

			for (ReplyContent rc : list) {
				rc.setReply_content(null);
				this.replycontentService.updateById(rc);
			}

			this.vMenuService.deleteById(CommUtil.null2Long(menu_id));
		}
		if ((id != null) && (!"".equals(id))) {
			ReplyContent rc = this.replycontentService
					.selectByPrimaryKey(CommUtil.null2Long(id));
			rc.setReply_content(null);
			this.replycontentService.updateById(rc);
		}
		return "redirect:weixin_plat_menu";
	}

	/**
	 * 微商城菜单创建
	 * 
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	@SecurityMapping(title = "微商城菜单创建", value = "/weixin_plat_menu_create*", rtype = "admin", rname = "微信基本设置", rcode = "weixin_plat_admin", rgroup = "运营")
	@RequestMapping({ "/weixin_plat_menu_create" })
	public void weixin_plat_menu_create(HttpServletRequest request,
			HttpServletResponse response) throws IOException {

		int ret = createMenu();
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		try {
			PrintWriter writer = response.getWriter();
			writer.print(ret);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 微商城菜单验证
	 * 
	 * @param request
	 * @param response
	 * @param menu_id
	 * @param menu_key
	 * @param store_id
	 * @throws IOException
	 */

	@SecurityMapping(title = "微商城菜单验证", value = "/weixin_menukey_verify*", rtype = "admin", rname = "微信基本设置", rcode = "weixin_plat_admin", rgroup = "运营")
	@RequestMapping({ "/weixin_menukey_verify" })
	public void weixin_menukey_verify(HttpServletRequest request,
			HttpServletResponse response, String menu_id, String menu_key,
			String store_id) throws IOException {

		boolean ret = true;
		Map<String, Object> params = Maps.newHashMap();
		params.put("menu_key", menu_key);
		params.put("id_no", CommUtil.null2Long(menu_id));
		List<VMenu> VMenus = this.vMenuService.queryPageList(params);

		if ((VMenus != null) && (VMenus.size() > 0)) {
			ret = false;
		}
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		try {
			PrintWriter writer = response.getWriter();
			writer.print(ret);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@RequestMapping({ "/catchopenid" })
	public void catchopenid(HttpServletRequest request,
			HttpServletResponse response) {
		String code = request.getParameter("code");
		if ((code != null) && (!code.equals(""))) {
			String state = request.getParameter("state");

			String appId = CommUtil.null2String(this.configService
					.getSysConfig().getWeixin_appId());
			String secret = CommUtil.null2String(this.configService
					.getSysConfig().getWeixin_appSecret());
			String action = "https://api.weixin.qq.com/sns/oauth2/access_token?appid="
					+ appId
					+ "&secret="
					+ secret
					+ "&code="
					+ code
					+ "&grant_type=authorization_code";
			try {
				URL urlGet = new URL(action);
				HttpURLConnection http = (HttpURLConnection) urlGet
						.openConnection();
				http.setRequestMethod("GET");
				http.setRequestProperty("Content-Type",
						"application/x-www-form-urlencoded");
				http.setDoOutput(true);
				http.setDoInput(true);
				System.setProperty("sun.net.client.defaultConnectTimeout",
						"30000");
				System.setProperty("sun.net.client.defaultReadTimeout", "30000");
				System.setProperty("jsse.enableSNIExtension", "false");
				http.connect();
				InputStream is = http.getInputStream();
				int size = is.available();
				byte[] jsonBytes = new byte[size];
				is.read(jsonBytes);
				String message = new String(jsonBytes, "UTF-8");
				Map<String, Object> map = JSON.parseObject(message);
				String openid = map.get("openid").toString();
				Map<String, Object> params = Maps.newHashMap();
				params.put("openId", openid);
				List<User> user = this.userService.queryPageList(params);

				if (user.size() == 1) {
					String userName = ((User) user.get(0)).getUserName();
					String password = ((User) user.get(0)).getPassword();
					params.clear();
					params.put("menu_key", state);
					List<VMenu> vms = this.vMenuService.queryPageList(params);

					String his_url = CommUtil.getURL(request) + "/index";
					request.getSession(false).setAttribute("his_url", his_url);
					request.getSession().removeAttribute("verify_code");
					response.sendRedirect(CommUtil.getURL(request)
							+ "/redpigmall_login?username="
							+ CommUtil.encode(userName) + "&password="
							+ "redpigmall_thid_login_" + password
							+ "&encode=true&login_role=user");
				}
				String[] s = state.split("_");
				if ((s.length == 3) && (s[0].equals("order"))) {
					response.sendRedirect(CommUtil.getURL(request)
							+ "/weixin/pay/wx_pay?openid=" + openid + "&id="
							+ s[1] + "&type=" + s[2]);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private int createMenu() throws IOException {
		int ret = 0;
		Map weixin_plat_menu = Maps.newHashMap();
		List<Weixin_Menu> list = Lists.newArrayList();
		Map<String, Object> params = Maps.newHashMap();
		params.put("parent", -1);
		List<VMenu> vmenus = this.vMenuService.queryPageList(params);

		for (VMenu vmenu : vmenus) {
			Weixin_Menu menu = new Weixin_Menu();
			menu.setKey(vmenu.getMenu_key());
			menu.setName(vmenu.getMenu_name());
			menu.setType(vmenu.getMenu_type());
			menu.setUrl(vmenu.getMenu_url());
			for (VMenu c_vmenu : vmenu.getChilds()) {
				Weixin_Menu c_menu = new Weixin_Menu();
				c_menu.setKey(c_vmenu.getMenu_key());
				c_menu.setName(c_vmenu.getMenu_name());
				c_menu.setType(c_vmenu.getMenu_type());
				c_menu.setUrl(c_vmenu.getMenu_url());
				menu.getSub_button().add(c_menu);
			}
			list.add(menu);
		}
		weixin_plat_menu.put("button", list);
		String user_define_menu = JSON.toJSONString(weixin_plat_menu);
		String appId = CommUtil.null2String(this.configService.getSysConfig()
				.getWeixin_appId());
		String appSecret = CommUtil.null2String(this.configService
				.getSysConfig().getWeixin_appSecret());
		String access_token = GetWxToken.instance()
				.getWxToken(appId, appSecret);
		String action = "https://api.weixin.qq.com/cgi-bin/menu/create?access_token="
				+ access_token;
		System.out.println("access_token====" + access_token);
		try {
			URL url = new URL(action);
			HttpURLConnection http = (HttpURLConnection) url.openConnection();
			http.setRequestMethod("POST");
			http.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded");
			http.setDoOutput(true);
			http.setDoInput(true);
			System.setProperty("sun.net.client.defaultConnectTimeout", "30000");
			System.setProperty("sun.net.client.defaultReadTimeout", "30000");
			System.setProperty("jsse.enableSNIExtension", "false");
			http.connect();
			OutputStream os = http.getOutputStream();
			os.write(user_define_menu.getBytes("UTF-8"));
			os.flush();
			os.close();
			InputStream is = http.getInputStream();
			int size = is.available();
			byte[] jsonBytes = new byte[size];
			is.read(jsonBytes);
			String message = new String(jsonBytes, "UTF-8");
			System.out.println("menu:" + message);
			Map ret_map = JSON.parseObject(message);
			ret = CommUtil.null2Int(ret_map.get("errcode"));
			is.close();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return ret;
	}

	private int send_welcome(Map map, HttpServletRequest request) {
		int ret = 0;
		String appId = CommUtil.null2String(this.configService.getSysConfig()
				.getWeixin_appId());
		String appSecret = CommUtil.null2String(this.configService
				.getSysConfig().getWeixin_appSecret());
		String access_token = GetWxToken.instance()
				.getWxToken(appId, appSecret);
		String action = "https://api.weixin.qq.com/cgi-bin/message/custom/send?access_token="
				+ access_token;
		Map text_json = Maps.newLinkedHashMap();
		text_json.put("touser", map.get("FromUserName").toString());
		text_json.put("msgtype", "news");
		List<Map> maps = Lists.newArrayList();
		Map map1 = Maps.newHashMap();
		map1.put("title", "欢迎关注"
				+ this.configService.getSysConfig().getWebsiteName());
		map1.put("description", this.configService.getSysConfig()
				.getWeixin_welecome_content());
		map1.put("url", CommUtil.getURL(request) + "/wap/index");
		map1.put("picurl", CommUtil.getURL(request) + "/"
				+ this.configService.getSysConfig().getWelcome_img().getPath()
				+ "/"
				+ this.configService.getSysConfig().getWelcome_img().getName());
		maps.add(map1);
		Map map2 = Maps.newHashMap();
		map2.put("articles", maps);
		text_json.put("news", map2);
		String news_json = JSON.toJSONString(text_json);
		try {
			URL url = new URL(action);
			HttpURLConnection http = (HttpURLConnection) url.openConnection();
			http.setRequestMethod("POST");
			http.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded");
			http.setDoOutput(true);
			http.setDoInput(true);
			System.setProperty("sun.net.client.defaultConnectTimeout", "30000");
			System.setProperty("sun.net.client.defaultReadTimeout", "30000");
			System.setProperty("jsse.enableSNIExtension", "false");
			http.connect();
			OutputStream os = http.getOutputStream();
			os.write(news_json.getBytes("UTF-8"));
			System.out.println(news_json);
			os.flush();
			os.close();
			InputStream is = http.getInputStream();
			int size = is.available();
			byte[] jsonBytes = new byte[size];
			is.read(jsonBytes);
			String message = new String(jsonBytes, "UTF-8");
			System.out.println("menu:" + message);
			Map ret_map = JSON.parseObject(message);
			ret = CommUtil.null2Int(ret_map.get("errcode"));
			is.close();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return ret;
	}

	public void binding(String openId, String user_id,
			HttpServletRequest request) {
		User user = this.userService.selectByPrimaryKey(CommUtil
				.null2Long(user_id));
		System.out.println("openId=" + openId);
		if (user != null) {
			user.setOpenId(openId);
			System.out.println(user.getOpenId());
			this.userService.updateById(user);
		}
	}

	public String code(String user_id) {
		String ret = "";
		User user = null;
		String appId = CommUtil.null2String(this.configService.getSysConfig()
				.getWeixin_appId());
		String appSecret = CommUtil.null2String(this.configService
				.getSysConfig().getWeixin_appSecret());
		String access_token = GetWxToken.instance()
				.getWxToken(appId, appSecret);
		System.out.println("access_token" + access_token);
		String action = "https://api.weixin.qq.com/cgi-bin/qrcode/create?access_token="
				+ access_token;
		Map new_json = Maps.newLinkedHashMap();
		new_json.put("expire_seconds", "1800");
		new_json.put("action_name", "QR_SCENE");
		Map new_json_child = new LinkedHashMap();
		Map new_json_child_child = Maps.newLinkedHashMap();
		new_json_child_child.put("scene_id", user_id);
		new_json_child.put("scene", new_json_child_child);
		new_json.put("action_info", new_json_child);
		String json = JSON.toJSONString(new_json);
		try {
			URL url = new URL(action);
			HttpURLConnection http = (HttpURLConnection) url.openConnection();
			http.setRequestMethod("POST");
			http.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded");
			http.setDoOutput(true);
			http.setDoInput(true);
			System.setProperty("sun.net.client.defaultConnectTimeout", "30000");
			System.setProperty("sun.net.client.defaultReadTimeout", "30000");
			System.setProperty("jsse.enableSNIExtension", "false");
			http.connect();
			OutputStream os = http.getOutputStream();
			os.write(json.getBytes("UTF-8"));
			System.out.println(json);
			os.flush();
			os.close();
			InputStream is = http.getInputStream();
			int size = is.available();
			byte[] jsonBytes = new byte[size];
			is.read(jsonBytes);
			String message = new String(jsonBytes, "UTF-8");
			System.out.println("menu:" + message);

			Map ret_map = JSON.parseObject(message);
			ret = CommUtil.null2String(ret_map.get("ticket"));
			System.out.println("ret" + ret);
			is.close();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return ret;
	}

	public void user_add(String openId, HttpServletRequest request)
			throws IOException, InterruptedException {
		String userInfo = getWeiXin_userInfo(openId);
		Map<String, Object> map = JSON.parseObject(userInfo);
		Map<String, Object> params = Maps.newHashMap();
		String unionid = map.get("unionid") != null ? map.get("unionid")
				.toString() : "";
		params.put("weixin_unionID", unionid);
		List<User> users = this.userService.queryPageList(params);

		if (users.size() == 0) {
			String sub = map.get("subscribe").toString();
			if (sub.equals("1")) {
				User new_user = new User();
				new_user.setAddTime(new Date());
				new_user.setOpenId(map.get("openid").toString());
				new_user.setWeixin_unionID(unionid);
				this.userService.saveEntity(new_user);
				new_user.setUserRole("BUYER");
				new_user.setPassword(Md5Encrypt.md5(map.get("openid")
						.toString().substring(0, 8)));
				String mark = this.base64Tools.encodeStr(map.get("openid")
						.toString().substring(0, 8))
						+ CommUtil.randomString(4);
				new_user.setUserMark(mark);
				new_user.setUserName(mark);
				int sex = CommUtil.null2Int(map.get("sex"));
				params.clear();
				params.put("type", "BUYER");
				List<Role> roles = this.roleService.queryPageList(params);

				new_user.getRoles().addAll(roles);
				switch (sex) {
				case 1:
					new_user.setSex(1);
					break;
				case 2:
					new_user.setSex(0);
					break;
				case 0:
					new_user.setSex(-1);
				}
				String fileName = getWeiXin_userPhoto(map.get("headimgurl")
						.toString(), request);
				Accessory photo = new Accessory();
				photo.setAddTime(new Date());
				photo.setExt(".jpg");
				photo.setName(fileName);
				String uploadFilePath = this.configService.getSysConfig()
						.getUploadFilePath();
				photo.setPath(uploadFilePath);
				this.accessoryService.saveEntity(photo);

				new_user.setPhoto(photo);
				String nickName = EmojiTools.filterEmoji(map.get("nickname")
						.toString());
				new_user.setNickName(nickName);
				this.userService.updateById(new_user);
			}
		} else if ((users.size() == 1)
				&& (((User) users.get(0)).getOpenId() == null)) {
			((User) users.get(0)).setOpenId(map.get("openid").toString());
			this.userService.updateById((User) users.get(0));
		}
	}

	private String getWeiXin_userInfo(String openId) {
		String appId = CommUtil.null2String(this.configService.getSysConfig()
				.getWeixin_appId());
		String appSecret = CommUtil.null2String(this.configService
				.getSysConfig().getWeixin_appSecret());
		String access_token = GetWxToken.instance()
				.getWxToken(appId, appSecret);
		String action = "https://api.weixin.qq.com/cgi-bin/user/info?access_token="
				+ access_token + "&openid=" + openId + "&lang=zh_CN";
		String message = null;
		try {
			URL url = new URL(action);
			HttpURLConnection http = (HttpURLConnection) url.openConnection();
			http.setRequestMethod("POST");
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
			is.close();
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return getWeiXin_userInfo(openId);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return message;
	}

	private String getWeiXin_userPhoto(String urlString,
			HttpServletRequest request) throws IOException {
		URL url = new URL(urlString);
		URLConnection con = url.openConnection();
		con.setConnectTimeout(30000);
		InputStream is = con.getInputStream();
		String uploadFilePath = this.configService.getSysConfig()
				.getUploadFilePath();
		String saveFilePathName = request.getSession().getServletContext()
				.getRealPath("/")
				+ uploadFilePath;
		byte[] bs = new byte['Ѐ'];

		File sf = new File(saveFilePathName);
		if (!sf.exists()) {
			sf.mkdirs();
		}
		String fileName = CommUtil.formatTime("yyyyMMddHHmmss", new Date())
				+ ".jpg";
		OutputStream os = new FileOutputStream(sf.getPath() + File.separator
				+ fileName);
		int len;
		while ((len = is.read(bs)) != -1) {
			os.write(bs, 0, len);
		}
		os.close();
		is.close();
		return fileName;
	}

	@Async
	@Transactional
	private String asyncLogin(String userName, String password) {
		return "redirect:redpigmall_login?username=" + userName + "&password="
				+ password + "&encode=true";
	}

	public class Weixin_Menu {
		private String type;
		private String name;
		private String key;
		private String url;
		private List<Weixin_Menu> sub_button = Lists.newArrayList();

		public Weixin_Menu() {
		}

		public String getType() {
			return this.type;
		}

		public void setType(String type) {
			this.type = type;
		}

		public String getName() {
			return this.name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getKey() {
			return this.key;
		}

		public void setKey(String key) {
			this.key = key;
		}

		public String getUrl() {
			return this.url;
		}

		public void setUrl(String url) {
			this.url = url;
		}

		public List<Weixin_Menu> getSub_button() {
			return this.sub_button;
		}

		public void setSub_button(List<Weixin_Menu> sub_button) {
			this.sub_button = sub_button;
		}
	}
}
