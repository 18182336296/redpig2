package com.redpigmall.manage.admin.action.goods;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.redpigmall.api.annotation.SecurityMapping;
import com.redpigmall.api.mv.RedPigJModelAndView;
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.api.tools.CommUtil;
import com.redpigmall.api.tools.ObjectUtils;
import com.redpigmall.api.tools.WebForm;
import com.redpigmall.manage.admin.action.base.BaseAction;
import com.redpigmall.domain.Accessory;
import com.redpigmall.domain.AdvertPosition;
import com.redpigmall.domain.Channel;
import com.redpigmall.domain.GoodsBrand;
import com.redpigmall.domain.GoodsClass;
import com.redpigmall.domain.GoodsType;
import com.redpigmall.domain.ShowClass;
import com.redpigmall.domain.WeixinChannel;

/**
 * 
 * <p>
 * Title: RedPigShowClassManageAction.java
 * </p>
 * 
 * <p>
 * Description:展示类目
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
 * @date 2014年4月25日
 * 
 * @version redpigmall_b2b2c 8.0
 */
@SuppressWarnings({ "unchecked", "rawtypes", "unused" })
@Controller
public class RedPigShowClassManageAction extends BaseAction{
	/**
	 * 展示类目列表
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @return
	 */
	@SecurityMapping(title = "展示类目列表", value = "/showclass_list*", rtype = "admin", rname = "展示类目", rcode = "showclass", rgroup = "商品")
	@RequestMapping({ "/showclass_list" })
	public ModelAndView showclass_list(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,
			String orderType) {
		ModelAndView mv = new RedPigJModelAndView("admin/blue/showclass_list.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		String url = this.configService.getSysConfig().getAddress();
		if ((url == null) || (url.equals(""))) {
			url = CommUtil.getURL(request);
		}
		String params = "";
		
		Map<String,Object> maps= this.redPigQueryTools.getParams(currentPage, "sequence", "asc");
		maps.put("parent", -1);
		
		IPageList pList = this.showclassService.list(maps);
		
		CommUtil.saveIPageList2ModelAndView(url + "/showclass_list.html", "", params, pList, mv);
		return mv;
	}
	
	/**
	 * 新增展示类目
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param pid
	 * @return
	 */
	@SecurityMapping(title = "新增展示类目", value = "/showclass_add*", rtype = "admin", rname = "展示类目", rcode = "showclass", rgroup = "商品")
	@RequestMapping({ "/showclass_add" })
	public ModelAndView showclass_add(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String pid) {
		ModelAndView mv = new RedPigJModelAndView("admin/blue/showclass_add.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		Map<String,Object> maps= Maps.newHashMap();
		maps.put("parent", -1);
		
		List<GoodsClass> gcs = this.goodsclassService.queryPageList(maps);
		maps.clear();
		List<Channel> css = this.channelService.queryPageList(maps);
		maps.put("parent", -1);
		maps.put("orderBy", "sequence");
		maps.put("orderType", "asc");
		
		List<ShowClass> scs = this.showclassService.queryPageList(maps);
		
		maps.clear();
		List<GoodsType> gts = this.goodsTypeService.queryPageList(maps);
		
		Map<String, Object> params = Maps.newHashMap();
		params.put("ap_status", Integer.valueOf(1));
		params.put("ap_type", "img");
		maps.put("orderBy", "addTime");
		maps.put("orderType", "desc");
		List<AdvertPosition> aps = this.advertPositionService.queryPageList(maps);
		
		if ((pid != null) && (!pid.equals(""))) {
			ShowClass obj = new ShowClass();
			ShowClass parent = this.showclassService.selectByPrimaryKey(Long.valueOf(Long.parseLong(pid)));
			obj.setParent(parent);
			mv.addObject("parent", parent);
			mv.addObject("obj", obj);
		}
		maps.clear();
		List<WeixinChannel> wxcs = this.weiXinChannelService.queryPageList(maps);
		
		mv.addObject("wxcs", wxcs);
		mv.addObject("gts", gts);
		mv.addObject("aps", aps);
		mv.addObject("gcs", gcs);
		mv.addObject("css", css);
		mv.addObject("scs", scs);
		mv.addObject("currentPage", currentPage);
		return mv;
	}
	
	/**
	 * 展示类目编辑管理
	 * @param request
	 * @param response
	 * @param id
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "展示类目编辑管理", value = "/showclass_edit*", rtype = "admin", rname = "展示类目", rcode = "showclass", rgroup = "商品")
	@RequestMapping({ "/showclass_edit" })
	public ModelAndView showclass_edit(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage) {
		ModelAndView mv = new RedPigJModelAndView("admin/blue/showclass_add.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		if ((id != null) && (!id.equals(""))) {
			ShowClass showclass = this.showclassService.selectByPrimaryKey(Long.valueOf(Long.parseLong(id)));
			mv.addObject("obj", showclass);
			mv.addObject("currentPage", currentPage);
			mv.addObject("edit", Boolean.valueOf(true));
			Map<String,Object> maps= Maps.newHashMap();
			maps.put("parent", -1);
			
			List<GoodsClass> gcs = this.goodsclassService.queryPageList(maps);
			
			if ((showclass != null) && (showclass.getParent() != null)) {
				Map<String, Object> map = Maps.newHashMap();
				map.put("showClass_id", showclass.getId());
				
				List<GoodsClass> list = this.goodsclassService.queryPageList(maps);
				
				mv.addObject("list", list);
				String gc_ids = "";
				for (GoodsClass gc : list) {
					gc_ids = gc_ids + "," + gc.getId();
				}
				mv.addObject("gc_ids", gc_ids);
			}
			maps.clear();
			
			List<Channel> css = this.channelService.queryPageList(maps);
			maps.put("parent", -1);
			maps.put("orderBy", "sequence");
			maps.put("orderType", "asc");
			
			List<ShowClass> scs = this.showclassService.queryPageList(maps);
			
			maps.clear();
			List<GoodsType> gts = this.goodsTypeService.queryPageList(maps);
			
			Map<String, Object> params = Maps.newHashMap();
			params.put("ap_status", Integer.valueOf(1));
			params.put("ap_type", "img");
			
			List<AdvertPosition> aps = this.advertPositionService.queryPageList(maps);
			
			if ((showclass.getScb_info() != null)
					&& (!showclass.getScb_info().equals(""))) {
				List<Map> map_list = JSON.parseArray(showclass.getScb_info(),Map.class);
				List gbs = Lists.newArrayList();
				for (Map map : map_list) {
					GoodsBrand gb = this.goodsbrandService.selectByPrimaryKey(CommUtil.null2Long(map.get("id")));
					if (gb != null) {
						gbs.add(gb);
					}
				}
				mv.addObject("gbs", gbs);
			}
			
			if (showclass.getSc_advert() != null) {
				Map<String, Object> map = JSON.parseObject(showclass
						.getSc_advert());
				if ("0".equals(CommUtil.null2String(map.get("adv_type")))) {
					AdvertPosition aap = this.advertPositionService.selectByPrimaryKey(CommUtil.null2Long(map.get("adv_id")));
					if (aap != null) {
						mv.addObject("aap_id", aap.getId());
					}
				}
				mv.addObject("adv_type",
						CommUtil.null2String(map.get("adv_type")));
				mv.addObject("adv_id", CommUtil.null2String(map.get("adv_id")));
				Accessory acc = this.accessoryService.selectByPrimaryKey(CommUtil
						.null2Long(map.get("acc_id")));
				if (acc != null) {
					mv.addObject("acc_img", CommUtil.getURL(request) + "/"
							+ acc.getPath() + "/" + acc.getName());
				}
				if (map.get("acc_url") != null) {
					mv.addObject("acc_url",
							CommUtil.null2String(map.get("acc_url")));
				}
			}
			maps.clear();
			List<WeixinChannel> wxcs = this.weiXinChannelService.queryPageList(maps);
			
			mv.addObject("wxcs", wxcs);
			mv.addObject("gts", gts);
			mv.addObject("aps", aps);
			mv.addObject("gcs", gcs);
			mv.addObject("css", css);
			mv.addObject("scs", scs);
		}
		mv.addObject("showClassTools", this.showClassTools);
		return mv;
	}
	
	/**
	 * 展示类目保存管理
	 * @param request
	 * @param response
	 * @param id
	 * @param currentPage
	 * @param list_url
	 * @param add_url
	 * @param cid
	 * @param pid
	 * @param url
	 * @param type
	 * @param display
	 * @param icon_type
	 * @param icon_sys
	 * @param sc_color
	 * @param adv_type
	 * @param scbrand_ids
	 * @param sc_adv_url
	 * @param ap_id
	 * @param gc_ids
	 * @param wxcid
	 * @return
	 */
	@SecurityMapping(title = "展示类目保存管理", value = "/showclass_save*", rtype = "admin", rname = "展示类目", rcode = "showclass", rgroup = "商品")
	@RequestMapping({ "/showclass_save" })
	public ModelAndView showclass_save(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage,
			String list_url, String add_url, String cid, String pid,
			String url, String type, String display, String icon_type,
			String icon_sys, String sc_color, String adv_type,
			String scbrand_ids, String sc_adv_url, String ap_id, String gc_ids,
			String wxcid) {
		WebForm wf = new WebForm();
		ShowClass showclass = null;
		if (id.equals("")) {
			showclass = (ShowClass) WebForm.toPo(request, ShowClass.class);
			showclass.setAddTime(new Date());
		} else {
			ShowClass obj = this.showclassService.selectByPrimaryKey(Long.valueOf(Long.parseLong(id)));
			showclass = (ShowClass) WebForm.toPo(request, obj);
		}
		ShowClass parent = this.showclassService.selectByPrimaryKey(CommUtil.null2Long(pid));
		if (parent != null) {
			showclass.setParent(parent);
			showclass.setLevel(parent.getLevel() + 1);
		} else {
			showclass.setParent(null);
			showclass.setLevel(0);
		}
		showclass.setShow_type(CommUtil.null2Int(type));
		if ("0".equals(type)) {
			showclass.setUrl(url);
		}
		if ((wxcid != null) && (!wxcid.equals(""))) {
			showclass.setWeixinChannel_id(CommUtil.null2Long(wxcid));
		}
		if ("1".equals(type)) {
			showclass.setChannel_id(CommUtil.null2Long(cid));
		}
		if ("true".equals(display)) {
			showclass.setDisplay(true);
		}
		if ("false".equals(display)) {
			showclass.setDisplay(false);
		}
		if ("0".equals(icon_type)) {
			showclass.setIcon_sys(icon_sys);
		}
		String uploadFilePath = this.configService.getSysConfig()
				.getUploadFilePath();
		String saveFilePathName = request.getSession().getServletContext()
				.getRealPath("/")
				+ uploadFilePath + File.separator + "show_icon";
		Accessory photo;
		if ("1".equals(icon_type)) {
			Map<String, Object> map = Maps.newHashMap();
			try {
				Accessory acc = this.accessoryService.selectByPrimaryKey(showclass.getPhoto_id());
				String fileName = acc == null ? "" : acc.getName();
				map = CommUtil.saveFileToServer(request, "icon_acc",saveFilePathName, "", null);
				if (fileName.equals("")) {
					if (map.get("fileName") != "") {
						photo = new Accessory();
						photo.setName(CommUtil.null2String(map.get("fileName")));
						photo.setExt(CommUtil.null2String(map.get("mime")));
						photo.setSize(BigDecimal.valueOf(CommUtil
								.null2Double(map.get("fileSize"))));
						photo.setPath(uploadFilePath + "/show_icon");
						photo.setWidth(CommUtil.null2Int(map.get("width")));
						photo.setHeight(CommUtil.null2Int(map.get("height")));
						photo.setAddTime(new Date());
						this.accessoryService.saveEntity(photo);
						showclass.setPhoto_id(photo.getId());
					}
				} else if (map.get("fileName") != "") {
					acc.setName(CommUtil.null2String(map.get("fileName")));
					acc.setExt(CommUtil.null2String(map.get("mime")));
					acc.setSize(BigDecimal.valueOf(CommUtil.null2Double(map
							.get("fileSize"))));
					acc.setPath(uploadFilePath + "/show_icon");
					acc.setWidth(CommUtil.null2Int(map.get("width")));
					acc.setHeight(CommUtil.null2Int(map.get("height")));
					acc.setAddTime(new Date());
					this.accessoryService.updateById(acc);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		showclass.setSc_color(sc_color);
		GoodsBrand gb;
		Map map_temp;
		if ((scbrand_ids != null) && (!scbrand_ids.equals(""))) {
			String[] scb_ids = scbrand_ids.split(",");
			List list_temp = Lists.newArrayList();
			for (String scb_id : scb_ids) {
				if ((scb_id != null) && (!scb_id.equals(""))) {
					gb = this.goodsbrandService.selectByPrimaryKey(CommUtil.null2Long(scb_id));
					map_temp = Maps.newHashMap();
					map_temp.put("id", gb.getId());
					map_temp.put("src", gb.getBrandLogo().getPath() + "/"
							+ gb.getBrandLogo().getName());
					map_temp.put("name", gb.getName());
					list_temp.add(map_temp);
				}
			}
			showclass.setScb_info(JSON.toJSONString(list_temp));
		} else {
			showclass.setScb_info(null);
		}
		Map map2 = Maps.newHashMap();
		try {
			String fileName = "";
			map2 = CommUtil.saveFileToServer(request, "sc_adv",
					saveFilePathName, fileName, null);
			if (fileName.equals("")) {
				if (map2.get("fileName") != "") {
					photo = new Accessory();
					photo.setName(CommUtil.null2String(map2.get("fileName")));
					photo.setExt(CommUtil.null2String(map2.get("mime")));
					photo.setSize(BigDecimal.valueOf(CommUtil.null2Double(map2
							.get("fileSize"))));
					photo.setPath(uploadFilePath + "/show_icon");
					photo.setWidth(CommUtil.null2Int(map2.get("width")));
					photo.setHeight(CommUtil.null2Int(map2.get("height")));
					photo.setAddTime(new Date());
					this.accessoryService.saveEntity(photo);
					map_temp = Maps.newHashMap();
					map_temp.put("acc_id", photo.getId());
					showclass.setSc_advert(JSON.toJSONString(map_temp));
				}
			} else if (map2.get("fileName") != "") {

				if (showclass.getSc_advert() != null) {
					Map json_map = JSON.parseObject(showclass.getSc_advert());
					String acc_id = CommUtil
							.null2String(json_map.get("acc_id"));
					photo = this.accessoryService.selectByPrimaryKey(CommUtil
							.null2Long(acc_id));
				}
				photo = this.accessoryService.selectByPrimaryKey(showclass
						.getPhoto_id());
				photo.setName(CommUtil.null2String(map2.get("fileName")));
				photo.setExt(CommUtil.null2String(map2.get("mime")));
				photo.setSize(BigDecimal.valueOf(CommUtil.null2Double(map2
						.get("fileSize"))));
				photo.setPath(uploadFilePath + "/show_icon");
				photo.setWidth(CommUtil.null2Int(map2.get("width")));
				photo.setHeight(CommUtil.null2Int(map2.get("height")));
				photo.setAddTime(new Date());
				this.accessoryService.updateById(photo);
				map_temp = Maps.newHashMap();
				map_temp.put("acc_id", photo.getId());
				showclass.setSc_advert(JSON.toJSONString(map_temp));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (CommUtil.null2Int(adv_type) == 0) {
			AdvertPosition ap = this.advertPositionService.selectByPrimaryKey(CommUtil
					.null2Long(ap_id));
			map_temp = Maps.newHashMap();
			if (ap != null) {
				map_temp.put("adv_id", ap.getId());
			} else {
				map_temp.put("adv_id", "");
			}
			showclass.setSc_advert(JSON.toJSONString(map_temp));
		}
		String ad = showclass.getSc_advert();
		if ((ad != null) && (!"".equals(ad))) {
			Map ad_map = JSON.parseObject(ad);
			if ((ad_map != null) && (!"".equals(ad_map))) {
				ad_map.put("acc_url", sc_adv_url);
				showclass.setSc_advert(JSON.toJSONString(ad_map));
			}
		} else {
			Map ad_map = Maps.newHashMap();
			ad_map.put("acc_url", sc_adv_url);
			showclass.setSc_advert(JSON.toJSONString(ad_map));
		}
		if (showclass.getSc_advert() != null) {
			map_temp = JSON.parseObject(showclass.getSc_advert());
			map_temp.put("adv_type",
					Integer.valueOf(CommUtil.null2Int(adv_type)));
			showclass.setSc_advert(JSON.toJSONString(map_temp));
		} else {
			map_temp = Maps.newHashMap();
			map_temp.put("adv_type",
					Integer.valueOf(CommUtil.null2Int(adv_type)));
			showclass.setSc_advert(JSON.toJSONString(map_temp));
		}
		boolean ret = false;
		if (id.equals("")) {
			this.showclassService.saveEntity(showclass);
			ret = true;
		} else {
			this.showclassService.updateById(showclass);
			ret = true;
		}
		if ((ret) && (showclass.getParent() != null)) {
			Map pmap = Maps.newHashMap();
			pmap.put("showClass_id", showclass.getId());
			
			List<GoodsClass> list = this.goodsclassService.queryPageListWithNoRelations(pmap);
			
			for (GoodsClass gc : list) {
				gc.setShowClass_id(null);
				this.goodsclassService.updateById(gc);
			}
			
			if ((gc_ids != null) && (!"".equals(gc_ids))) {
				String[] arr = gc_ids.split(",");
				for (String gc_id : arr) {
					GoodsClass gc = this.goodsclassService.selectByPrimaryKey(CommUtil.null2Long(gc_id));
					if (gc != null) {
						gc.setShowClass_id(showclass.getId());
						this.goodsclassService.updateById(gc);
					}
				}
			}
		}
		ModelAndView mv = new RedPigJModelAndView("admin/blue/success.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		mv.addObject("list_url", list_url);
		mv.addObject("op_title", "保存展示类目成功");
		if (add_url != null) {
			mv.addObject("add_url", add_url + "?pid=" + pid);
		}
		return mv;
	}
	
	/**
	 * 展示类目删除
	 * @param request
	 * @param response
	 * @param mulitId
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "展示类目删除", value = "/showclass_del*", rtype = "admin", rname = "展示类目", rcode = "showclass", rgroup = "商品")
	@RequestMapping({ "/showclass_del" })
	public String delete(HttpServletRequest request,
			HttpServletResponse response, String mulitId, String currentPage) {
		if (mulitId == null) {
			mulitId = "";
		}
		String[] ids = mulitId.split(",");

		for (String id : ids) {

			if (!id.equals("")) {
				ShowClass showclass = this.showclassService.selectByPrimaryKey(Long
						.valueOf(Long.parseLong(id)));
				if (showclass.getChilds().size() > 0) {
					for (ShowClass sc : showclass.getChilds()) {
						this.showclassService.deleteById(sc.getId());
					}
				}
				this.showclassService.deleteById(Long.valueOf(Long.parseLong(id)));
			}
		}
		return "redirect:showclass_list?currentPage=" + currentPage;
	}
	
	/**
	 * 展示类目ajax更新
	 * @param request
	 * @param response
	 * @param id
	 * @param fieldName
	 * @param value
	 * @throws ClassNotFoundException
	 */
	@SecurityMapping(title = "展示类目ajax更新", value = "/showclass_ajax*", rtype = "admin", rname = "展示类目", rcode = "showclass", rgroup = "商品")
	@RequestMapping({ "/showclass_ajax" })
	public void ajax(HttpServletRequest request, HttpServletResponse response,
			String id, String fieldName, String value)
			throws ClassNotFoundException {
		ShowClass obj = this.showclassService.selectByPrimaryKey(Long.valueOf(Long
				.parseLong(id)));
		Field[] fields = ShowClass.class.getDeclaredFields();
		
		Object val = ObjectUtils.setValue(fieldName, value, obj, fields);
		this.showclassService.updateById(obj);
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		try {
			PrintWriter writer = response.getWriter();
			writer.print(val.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 展示类目下级加载
	 * @param request
	 * @param response
	 * @param pid
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "展示类目下级加载", value = "/showclass_data*", rtype = "admin", rname = "展示类目", rcode = "showclass", rgroup = "商品")
	@RequestMapping({ "/showclass_data" })
	public ModelAndView showclass_data(HttpServletRequest request,
			HttpServletResponse response, String pid, String currentPage) {
		ModelAndView mv = new RedPigJModelAndView("admin/blue/showclass_data.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		Map<String, Object> map = Maps.newHashMap();
		map.put("parent_id", Long.valueOf(Long.parseLong(pid)));
		map.put("orderBy", "sequence");
		map.put("orderType", "asc");
		
		List<ShowClass> scs = this.showclassService.queryPageList(map);
		
		mv.addObject("scs", scs);
		mv.addObject("currentPage", currentPage);
		return mv;
	}
	
	/**
	 * 查看类目
	 * @param request
	 * @param response
	 * @param id
	 * @return
	 */
	@SecurityMapping(title = "查看类目", value = "/show_goodsclass*", rtype = "admin", rname = "展示类目", rcode = "showclass", rgroup = "商品")
	@RequestMapping({ "/show_goodsclass" })
	public ModelAndView show_goodsclass(HttpServletRequest request,
			HttpServletResponse response, String id) {
		ModelAndView mv = new RedPigJModelAndView("admin/blue/show_goodsclass.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		if ((id != null) && (!id.equals(""))) {
			ShowClass showclass = this.showclassService.selectByPrimaryKey(Long
					.valueOf(Long.parseLong(id)));
			List<GoodsClass> gcs = Lists.newArrayList();
			Map<String, Object> map = Maps.newHashMap();
			if (showclass.getLevel() == 0) {
				Set<Long> sc_ids = Sets.newHashSet();
				sc_ids.add(showclass.getId());
				for (ShowClass sc : showclass.getChilds()) {
					sc_ids.add(sc.getId());
				}
				map.put("showClass_ids", sc_ids);
				
				gcs = this.goodsclassService.queryPageList(map);
			} else {
				map.clear();
				map.put("showClass_ids", showclass.getId());
				gcs = this.goodsclassService.queryPageList(map);
			}
			mv.addObject("obj", showclass);
			mv.addObject("gcs", gcs);
		}
		return mv;
	}
	
	/**
	 * 通过类型id查询相应品牌
	 * @param request
	 * @param response
	 * @param sc_id
	 */
	@SecurityMapping(title = "通过类型id查询相应品牌", value = "/showclass_brand*", rtype = "admin", rname = "展示类目", rcode = "showclass", rgroup = "商品")
	@RequestMapping({ "/showclass_brand" })
	public void showclass_brand(HttpServletRequest request,
			HttpServletResponse response, String sc_id) {
		ShowClass sc = this.showclassService.selectByPrimaryKey(CommUtil
				.null2Long(sc_id));
		String temp = "";
		if (sc != null) {
			temp = sc.getScb_info();
		}
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
	 * 选择商品分类页面
	 * @param request
	 * @param response
	 * @param id
	 * @return
	 */
	@SecurityMapping(title = "选择商品分类页面", value = "/select_goodsclass*", rtype = "admin", rname = "展示类目", rcode = "showclass", rgroup = "商品")
	@RequestMapping({ "/select_goodsclass" })
	public ModelAndView select_goodsclass(HttpServletRequest request,
			HttpServletResponse response, String id) {
		ModelAndView mv = new RedPigJModelAndView(
				"admin/blue/select_goodsclass.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		ShowClass showclass = this.showclassService.selectByPrimaryKey(CommUtil
				.null2Long(id));
		Map<String, Object> map = Maps.newHashMap();
		if ((showclass != null) && (showclass.getParent() != null)) {
			
			map.put("showClass_id", showclass.getId());
			map.put("orderBy", "sequence");
			map.put("orderType", "asc");
			
			List<GoodsClass> list = this.goodsclassService.queryPageList(map);
			mv.addObject("list", list);
		}
		map.clear();
		map.put("parent", -1);
		map.put("orderBy", "sequence");
		map.put("orderType", "asc");
		List gcs = this.goodsclassService.queryPageList(map);
		mv.addObject("gcs", gcs);
		return mv;
	}
	
	/**
	 * 加载商品分类信息
	 * @param request
	 * @param response
	 * @param id
	 * @param name
	 * @return
	 */
	@SecurityMapping(title = "加载商品分类信息", value = "/load_goodsclass*", rtype = "admin", rname = "展示类目", rcode = "showclass", rgroup = "商品")
	@RequestMapping({ "/load_goodsclass" })
	public ModelAndView load_goodsclass(HttpServletRequest request,
			HttpServletResponse response, String id, String name) {
		ModelAndView mv = new RedPigJModelAndView(
				"admin/blue/show_load_goodsclass.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		List<GoodsClass> gcs = Lists.newArrayList();
		if ((name != null) && (!name.equals(""))) {
			Map<String, Object> params = Maps.newHashMap();
			params.put("className_like", name);
			List<GoodsClass> cc_list = this.goodsclassService.queryPageList(params);
			
			for (GoodsClass gc : cc_list) {
				if (gc.getParent() == null) {
					gcs.remove(gc);
					gcs.add(gc);
				} else if (gc.getParent().getParent() == null) {
					gcs.remove(gc.getParent());
					gcs.add(gc.getParent());
				} else if (gc.getParent().getParent().getParent() == null) {
					gcs.remove(gc.getParent().getParent());
					gcs.add(gc.getParent().getParent());
				}
			}
		} else {
			Map<String, Object> params = Maps.newHashMap();
			params.put("parent", -1);
			
			gcs = this.goodsclassService.queryPageList(params);
		}
		mv.addObject("gcs", gcs);
		return mv;
	}
	
	/**
	 * 查询相应品牌
	 * @param request
	 * @param response
	 * @param brand_name
	 * @param brand_class
	 * @param currentPage
	 * @param sc_id
	 * @return
	 */
	@SecurityMapping(title = "查询相应品牌", value = "/search_brand*", rtype = "admin", rname = "展示类目", rcode = "showclass", rgroup = "商品")
	@RequestMapping({ "/search_brand" })
	public ModelAndView search_brand(HttpServletRequest request,
			HttpServletResponse response, String brand_name,
			String brand_class, String currentPage, String sc_id) {
		ModelAndView mv = new RedPigJModelAndView("admin/blue/search_brand.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		
		Map<String,Object> maps= this.redPigQueryTools.getParams(currentPage,24, "sequence", "asc");
		
		maps.put("audit", 1);
		if ((brand_name != null)
				&& (!CommUtil.null2String(brand_name).equals(""))) {
			
			maps.put("name_like", brand_name.trim());
		}
		if ((brand_class != null)
				&& (!CommUtil.null2String(brand_class).equals(""))) {
			
			maps.put("category_name_like", brand_class.trim());
			
		}
		if ((sc_id != null) && (!"".equals(sc_id))) {
			ShowClass showclass = this.showclassService.selectByPrimaryKey(Long
					.valueOf(Long.parseLong(sc_id)));
			if ((showclass.getScb_info() != null)
					&& (!showclass.getScb_info().equals(""))) {
				List<Map> map_list = JSON.parseArray(showclass.getScb_info(),
						Map.class);
				List gbs = Lists.newArrayList();
				for (Map map : map_list) {
					GoodsBrand gb = this.goodsbrandService.selectByPrimaryKey(CommUtil
							.null2Long(map.get("id")));
					if (gb != null) {
						gbs.add(gb);
					}
				}
				mv.addObject("gbs", gbs);
			}
		}
		IPageList pList = this.goodsbrandService.queryPagesWithNoRelations(maps);
		
		mv.addObject("PageSize", Integer.valueOf(pList.getPages()));
		CommUtil.saveIPageList2ModelAndView(CommUtil.getURL(request)
				+ "/search_brand.html", "", "", pList, mv);
		mv.addObject(
				"scshowPageAjaxHtml",
				scshowPageAjaxHtml(CommUtil.getURL(request)
						+ "/search_brand.html", null,
						CommUtil.null2Int(currentPage), pList.getPages(),
						pList.getPageSize()));
		return mv;
	}

	private Set<Long> genericIds(ShowClass sc) {
		Set<Long> ids = new HashSet();
		ids.add(sc.getId());
		for (ShowClass child : sc.getChilds()) {
			Set<Long> cids = genericIds(child);
			for (Long cid : cids) {
				ids.add(cid);
			}
			ids.add(child.getId());
		}
		return ids;
	}

	public static String scshowPageAjaxHtml(String url, String params,
			int currentPage, int pages, int pageSize) {
		String s = "";
		if (pages > 0) {
			String address = url + "?1=1" + params;
			if (currentPage >= 1) {
				s = s
						+ "<a href='javascript:void(0);' onclick='return ajaxPage(\""
						+ address + "\",1,this)'>首页</a> ";
				s = s
						+ "<a href='javascript:void(0);' onclick='return ajaxPage(\""
						+ address + "\"," + (currentPage - 1)
						+ ",this)'><b><</b>上一页</a> ";
			}
			int beginPage = currentPage - 3 < 1 ? 1 : currentPage - 3;
			if (beginPage <= pages) {
				s = s + "第　";
				int i = beginPage;
				for (int j = 0; (i <= pages) && (j < 4); j++) {
					if (i == currentPage) {
						s =

						s
								+ "<a class='this' href='javascript:void(0);' onclick='return ajaxPage(\""
								+ address + "\"," + i + ",this)'>" + i
								+ "</a> ";
					} else {
						s =

						s
								+ "<a href='javascript:void(0);' onclick='return ajaxPage(\""
								+ address + "\"," + i + ",this)'>" + i
								+ "</a> ";
					}
					i++;
				}
				s = s + "页　";
			}
			if (currentPage <= pages) {
				s =

				s + "<a href='javascript:void(0);' onclick='return ajaxPage(\""
						+ address + "\"," + (currentPage + 1)
						+ ",this)'>下一页<b class='next'>></b></a> ";
				s = s
						+ "<a href='javascript:void(0);' onclick='return ajaxPage(\""
						+ address + "\"," + pages + ",this)'>末页</a> ";
			}
			s = s + "共<strong>" + pages + "</strong>页 ";
		}
		return s;
	}
}
