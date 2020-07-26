package com.redpigmall.manage.seller.action;

import java.awt.Font;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;
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
import com.redpigmall.api.sec.SecurityUserHolder;
import com.redpigmall.api.tools.CommUtil;
import com.redpigmall.api.tools.ObjectUtils;
import com.redpigmall.api.tools.RedPigCommonUtil;
import com.redpigmall.api.tools.WebForm;
import com.redpigmall.domain.Accessory;
import com.redpigmall.domain.Album;
import com.redpigmall.domain.Area;
import com.redpigmall.domain.ComplaintGoods;
import com.redpigmall.domain.Goods;
import com.redpigmall.domain.GoodsBrand;
import com.redpigmall.domain.GoodsCart;
import com.redpigmall.domain.GoodsClass;
import com.redpigmall.domain.GoodsFormat;
import com.redpigmall.domain.GoodsSpecProperty;
import com.redpigmall.domain.GoodsSpecification;
import com.redpigmall.domain.GoodsType;
import com.redpigmall.domain.GoodsTypeProperty;
import com.redpigmall.domain.GroupGoods;
import com.redpigmall.domain.ShipAddress;
import com.redpigmall.domain.Store;
import com.redpigmall.domain.StoreGrade;
import com.redpigmall.domain.Transport;
import com.redpigmall.domain.User;
import com.redpigmall.domain.UserGoodsClass;
import com.redpigmall.domain.WaterMark;
import com.redpigmall.domain.ZTCGoldLog;
import com.redpigmall.manage.seller.action.base.BaseAction;

/**
 * 
 * <p>
 * Title: RedPigGoodsSellerAction.java
 * </p>
 * 
 * <p>
 * Description:商家后台商品管理控制器
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
 * @date 2014-5-7
 * 
 * @version redpigmall_b2b2c v8.0 2016版
 */
@SuppressWarnings({ "unchecked", "rawtypes", "unused" })
@Controller
public class RedPigGoodsSellerAction extends BaseAction{
	/**
	 * 发布商品第一步
	 * @param request
	 * @param response
	 * @param id
	 * @param add_type
	 * @return
	 */
	@SecurityMapping(title = "发布商品第一步", value = "/add_goods_first*", rtype = "seller", rname = "发布新商品", rcode = "goods_seller", rgroup = "商品管理")
	@RequestMapping({ "/add_goods_first" })
	public ModelAndView add_goods_first(HttpServletRequest request,
			HttpServletResponse response, String id, String add_type) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/sellercenter/seller_error.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		request.getSession(false).removeAttribute("goods_class_info");
		int store_status = user.getStore() == null ? 0 : user.getStore().getStore_status();
		if (store_status == 15) {
			StoreGrade grade = user.getStore().getGrade();
			int user_goods_count = user.getStore().getGoods_list().size();
			if ((grade.getGoodsCount() == 0)
					|| (user_goods_count < grade.getGoodsCount())) {
				mv = new RedPigJModelAndView(
						"user/default/sellercenter/add_goods_first.html",
						this.configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 0, request,
						response);
				String json_staples = "";
				if ((user.getStaple_gc() != null)
						&& (!user.getStaple_gc().equals(""))) {
					json_staples = user.getStaple_gc();
				}
				if (!json_staples.equals("")) {
					List<Map> staples = JSON
							.parseArray(json_staples, Map.class);
					mv.addObject("staples", staples);
				}
				List<GoodsClass> gcs = this.storeTools.query_store_gc(user.getStore());
				mv.addObject("gcs", gcs);
				mv.addObject("id", CommUtil.null2String(id));
				mv.addObject("add_type", add_type);
			} else {
				mv.addObject("op_title", "您的店铺等级只允许上传" + grade.getGoodsCount()
						+ "件商品!");
				mv.addObject("url", CommUtil.getURL(request)
						+ "/store_grade");
			}
		}
		if (store_status == 0) {
			mv.addObject("op_title", "您尚未开通店铺，不能发布商品");
			mv.addObject("url", CommUtil.getURL(request) + "/index");
		} else if (store_status == 10) {
			mv.addObject("op_title", "您的店铺在审核中，不能发布商品");
			mv.addObject("url", CommUtil.getURL(request) + "/index");
		}
		if (store_status == 20) {
			mv.addObject("op_title", "您的店铺已被关闭，不能发布商品");
			mv.addObject("url", CommUtil.getURL(request) + "/index");
		} else {
			mv.addObject("op_title", "店铺信息错误，不能发布商品");
			mv.addObject("url", CommUtil.getURL(request) + "/index");
		}
		return mv;
	}
	
	/**
	 * 商品运费模板分页显示
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @param ajax
	 * @return
	 */
	@SecurityMapping(title = "商品运费模板分页显示", value = "/goods_transport*", rtype = "seller", rname = "发布新商品", rcode = "goods_seller", rgroup = "商品管理")
	@RequestMapping({ "/goods_transport" })
	public ModelAndView goods_transport(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,
			String orderType, String ajax) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/sellercenter/goods_transport.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		if (CommUtil.null2Boolean(ajax)) {
			mv = new RedPigJModelAndView(
					"user/default/sellercenter/goods_transport_list.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request,
					response);
		}
		String url = this.configService.getSysConfig().getAddress();
		if ((url == null) || (url.equals(""))) {
			url = CommUtil.getURL(request);
		}
		String params = "";
		
		Map<String,Object> maps= this.redPigQueryTools.getParams(currentPage,1, orderBy, orderType);
		
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder.getCurrentUser().getId());
		
		user = user.getParent() == null ? user : user.getParent();
		Store store = user.getStore();
		maps.put("store_id", store.getId());
		
		IPageList pList = this.transportService.list(maps);
		
		CommUtil.saveIPageList2ModelAndView(url + "/goods_transport",
				"", params, pList, mv);
		mv.addObject("transportTools", this.transportTools);
		return mv;
	}
	
	/**
	 * 发布商品第二步
	 * @param request
	 * @param response
	 * @return
	 */
	@SuppressWarnings("deprecation")
	@SecurityMapping(title = "发布商品第二步", value = "/add_goods_second*", rtype = "seller", rname = "发布新商品", rcode = "goods_seller", rgroup = "商品管理")
	@RequestMapping({ "/add_goods_second" })
	public ModelAndView add_goods_second(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/sellercenter/seller_error.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		int store_status = CommUtil.null2Int(user.getStore().getStore_status());
		if (store_status == 15) {
			if (request.getSession(false).getAttribute("goods_class_info") != null) {
				mv = new RedPigJModelAndView(
						"user/default/sellercenter/add_goods_second.html",
						this.configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 0, request,
						response);

				Map map = Maps.newHashMap();
				map.put("store_id", user.getStore().getId());
				map.put("orderBy", "sequence");
				map.put("orderType", "asc");
				
				List<GoodsTypeProperty> gtps = this.gtpService.queryPageList(map);
				
				if (gtps.size() > 0) {
					mv.addObject("gtps", gtps);
				}
				GoodsClass gc = (GoodsClass) request.getSession(false).getAttribute("goods_class_info");
				gc = this.goodsClassService.selectByPrimaryKey(gc.getId());
				String goods_class_info = generic_goods_class_info(gc);
				mv.addObject("goods_class",this.goodsClassService.selectByPrimaryKey(gc.getId()));
				mv.addObject("goods_class_info", goods_class_info.substring(0,goods_class_info.length() - 1));
				request.getSession(false).removeAttribute("goods_class_info");

				List<GoodsSpecification> color_spec_list = Lists.newArrayList();
				List<GoodsSpecification> other_spec_list = Lists.newArrayList();

				if (gc.getLevel() == 2) {
					Map spec_map = Maps.newHashMap();
					spec_map.put("store_id", user.getStore().getId());
					spec_map.put("orderBy", "sequence");
					spec_map.put("orderType", "asc");
					
					List<GoodsSpecification> goods_spec_list = this.goodsSpecificationService.queryPageList(spec_map);
					
					for (GoodsSpecification gspec : goods_spec_list) {

						for (GoodsClass spec_goodsclass_detail : gspec
								.getSpec_goodsClass_detail()) {
							if (gc.getId().equals(
									spec_goodsclass_detail.getId())) {
								if (gspec.getSpec_color() == 1) {
									color_spec_list.add(gspec);
								} else {
									other_spec_list.add(gspec);
								}
							}
						}
					}
				} else if (gc.getLevel() == 1) {
					Map spec_map = Maps.newHashMap();
					spec_map.put("store_id", user.getStore().getId());
					spec_map.put("goodsclass_id", gc.getId());
					spec_map.put("orderBy", "sequence");
					spec_map.put("orderType", "asc");
					
					other_spec_list = this.goodsSpecificationService.queryPageList(spec_map);
					
					for (GoodsSpecification gspec : other_spec_list) {
						if (gspec.getSpec_color() == 1) {
							color_spec_list.add(gspec);
						} else {
							other_spec_list.add(gspec);
						}
					}
				}
				mv.addObject("color_spec_list", color_spec_list);
				mv.addObject("goods_spec_list", other_spec_list);
				String path = this.storeTools.createUserFolder(request,
						user.getStore());
				double csize = CommUtil.fileSize(new File(path));
				double img_remain_size = 0.0D;
				if (user.getStore().getGrade().getSpaceSize() > 0.0F) {
					img_remain_size = CommUtil.div(
							Double.valueOf(user.getStore().getGrade()
									.getSpaceSize()
									* 1024.0F - csize), Integer.valueOf(1024));
					mv.addObject("img_remain_size",
							Double.valueOf(img_remain_size));
				}
				
				Map<String,Object> params = Maps.newHashMap();
				params.put("user_id", user.getId());
				params.put("display", Boolean.valueOf(true));
				params.put("parent", -1);
				params.put("orderBy", "sequence");
				params.put("orderType", "asc");
				
				List<UserGoodsClass> ugcs = this.userGoodsClassService.queryPageList(params);
				
				params.clear();
				
				GoodsClass goods_class = null;
				if (gc.getLevel() == 2) {
					goods_class = gc.getParent().getParent();
				}
				if (gc.getLevel() == 1) {
					goods_class = gc.getParent();
				}
				
				params.clear();
				
				params.put("audit", Integer.valueOf(1));
				params.put("orderBy", "first_word");
				params.put("orderType", "asc");
				
				List<GoodsBrand> gbs = this.goodsBrandService.queryPageList(params);
				
				mv.addObject("gbs", gbs);
				mv.addObject("ugcs", ugcs);
				mv.addObject("imageSuffix", this.storeViewTools
						.genericImageSuffix(this.configService.getSysConfig()
								.getImageSuffix()));

				String[] strs = this.configService.getSysConfig()
						.getImageSuffix().split("\\|");
				StringBuffer sb = new StringBuffer();

				for (String str : strs) {

					sb.append("." + str + ",");
				}
				mv.addObject("imageSuffix1", sb);
				Date now = new Date();
				now.setDate(now.getDate() + 1);
				mv.addObject("default_publish_day",
						CommUtil.formatShortDate(now));
				String goods_session = CommUtil.randomString(32);
				mv.addObject("goods_session", goods_session);
				request.getSession(false).setAttribute("goods_session",
						goods_session);
				mv.addObject("store", user.getStore());

				params.clear();
				params.put("gf_store_id", user.getStore().getId());
				List gfs = this.goodsFormatService.queryPageList(params);
				
				mv.addObject("gfs", gfs);
				params.clear();
				params.put("parent", -1);
				List<Area> areas = this.areaService.queryPageList(params);
				
				mv.addObject("areas", areas);
				params.clear();
				params.put("user_id", user.getId());
				params.put("orderBy", "album_sequence");
				params.put("orderType", "asc");
				
				List<Album> albums = this.albumService.queryPageList(params);
				
				mv.addObject("albums", albums.size() > 0 ? albums
						: new ArrayList());
				params.clear();
				params.put("sa_user_id", user.getId());
				
				List<ShipAddress> addresses = this.shipAddressService.queryPageList(params);
				
				mv.addObject("shipaddresses", addresses);
				
				params.put("sa_default", Integer.valueOf(1));
				List<ShipAddress> default_address = this.shipAddressService.queryPageList(params);
				
				if (default_address.size() > 0) {
					mv.addObject("de_area", this.areaService.selectByPrimaryKey(default_address.get(0).getSa_area_id()));
				}
				Store store = user.getStore();
				List<Map> ms_list = Lists.newArrayList();
				if (store.getStore_service_info() != null) {
					ms_list = JSON.parseArray(store.getStore_service_info(),Map.class);
				}
				mv.addObject("ms_list", ms_list);
			} else {
				mv.addObject("op_title", "Session信息丢失，请重新发布商品");
				mv.addObject("url", CommUtil.getURL(request) + "/add_goods_first");
			}
		}
		if (store_status == 0) {
			mv.addObject("op_title", "您尚未开通店铺，不能发布商品");
			mv.addObject("url", CommUtil.getURL(request) + "/index");
		}
		if (store_status == 10) {
			mv.addObject("op_title", "您的店铺在审核中，不能发布商品");
			mv.addObject("url", CommUtil.getURL(request) + "/index");
		}
		if (store_status == 20) {
			mv.addObject("op_title", "您的店铺已被关闭，不能发布商品");
			mv.addObject("url", CommUtil.getURL(request) + "/index");
		}
		return mv;
	}
	
	/**
	 * 产品规格显示
	 * @param request
	 * @param response
	 * @param goods_spec_ids
	 * @param supplement
	 * @return
	 */
	@SecurityMapping(title = "产品规格显示", value = "/goods_inventory*", rtype = "seller", rname = "发布新商品", rcode = "goods_seller", rgroup = "商品管理")
	@RequestMapping({ "/goods_inventory" })
	public ModelAndView goods_inventory(HttpServletRequest request,
			HttpServletResponse response, String goods_spec_ids,
			String supplement) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/sellercenter/goods_inventory.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		String[] spec_ids = goods_spec_ids.split(",");
		List<GoodsSpecProperty> gsps = Lists.newArrayList();

		for (String spec_id : spec_ids) {

			if (!spec_id.equals("")) {
				GoodsSpecProperty gsp = this.specPropertyService.selectByPrimaryKey(Long.valueOf(Long.parseLong(spec_id)));
				gsps.add(gsp);
			}
		}
		Set<GoodsSpecification> specs = Sets.newHashSet();
		for (GoodsSpecProperty gsp2 : gsps) {
			specs.add(gsp2.getSpec());
		}
		
		for (Iterator ite = specs.iterator(); ite.hasNext();) {
			GoodsSpecification spec = (GoodsSpecification) ite.next();
			spec.getProperties().clear();
			
			List<GoodsSpecProperty> listGsps = spec.getProperties();
			
			for(GoodsSpecProperty gsp : listGsps){
				gsp.setSpec(null);
				this.redPigGoodsSpecPropertyService.updateById(gsp);
			}
			
			for (GoodsSpecProperty goodsSpecProperty : gsps) {
				if (goodsSpecProperty.getSpec().getId().equals(spec.getId())) {
					goodsSpecProperty.setSpec(spec);
					this.redPigGoodsSpecPropertyService.updateById(goodsSpecProperty);
				}
			}
		}
		
		GoodsSpecification[] spec_list = (GoodsSpecification[]) specs.toArray(new GoodsSpecification[specs.size()]);
		Arrays.sort(spec_list, new Comparator() {
			public int compare(Object obj1, Object obj2) {
				GoodsSpecification a = (GoodsSpecification) obj1;
				GoodsSpecification b = (GoodsSpecification) obj2;
				if (a.getSequence() == b.getSequence()) {
					return 0;
				}
				return a.getSequence() > b.getSequence() ? 1 : -1;
			}
		});
		Object gsp_list = generic_spec_property(specs);
		mv.addObject("specs", Arrays.asList(spec_list));
		mv.addObject("gsps", gsp_list);
		if ((supplement != null) && (!supplement.equals(""))) {
			mv.addObject("supplement", supplement);
		}
		return mv;
	}
	
	/**
	 * 
	 * @param list
	 * @return
	 */
	public static GoodsSpecProperty[][] list2group(List<List<GoodsSpecProperty>> list) {
		GoodsSpecProperty[][] gps = new GoodsSpecProperty[list.size()][];
		for (int i = 0; i < list.size(); i++) {
			gps[i] = ((GoodsSpecProperty[]) ((List) list.get(i))
					.toArray(new GoodsSpecProperty[((List) list.get(i)).size()]));
		}
		return gps;
	}
	
	/**
	 * 
	 * @param specs
	 * @return
	 */
	private List<List<GoodsSpecProperty>> generic_spec_property(
			Set<GoodsSpecification> specs) {
		List<List<GoodsSpecProperty>> result_list = Lists.newArrayList();
		List<List<GoodsSpecProperty>> list = Lists.newArrayList();
		int max = 1;
		for (GoodsSpecification spec : specs) {
			list.add(spec.getProperties());
		}
		GoodsSpecProperty[][] gsps = list2group(list);
		for (int i = 0; i < gsps.length; i++) {
			max *= gsps[i].length;
		}
		for (int i = 0; i < max; i++) {
			List<GoodsSpecProperty> temp_list = Lists.newArrayList();
			int temp = 1;
			for (int j = 0; j < gsps.length; j++) {
				temp *= gsps[j].length;
				temp_list.add(j, gsps[j][(i / (max / temp) % gsps[j].length)]);
			}
			GoodsSpecProperty[] temp_gsps = (GoodsSpecProperty[]) temp_list
					.toArray(new GoodsSpecProperty[temp_list.size()]);
			Arrays.sort(temp_gsps, new Comparator() {
				public int compare(Object obj1, Object obj2) {
					GoodsSpecProperty a = (GoodsSpecProperty) obj1;
					GoodsSpecProperty b = (GoodsSpecProperty) obj2;
					if (a.getSpec().getSequence() == b.getSpec().getSequence()) {
						return 0;
					}
					return a.getSpec().getSequence() > b.getSpec()
							.getSequence() ? 1 : -1;
				}
			});
			result_list.add(Arrays.asList(temp_gsps));
		}
		return result_list;
	}
	
	/**
	 * 上传商品图片
	 * @param request
	 * @param response
	 * @param album_id
	 * @param session_u_id
	 * @param fileName
	 */
	@SecurityMapping(title = "上传商品图片", value = "/goods_image_upload*", rtype = "seller", rname = "发布新商品", rcode = "goods_seller", rgroup = "商品管理")
	@RequestMapping({ "/goods_image_upload" })
	public void goods_image_upload(HttpServletRequest request,
			HttpServletResponse response, String album_id, String session_u_id,
			String fileName) {
		Map json_map = Maps.newHashMap();
		User user = null;
		if (SecurityUserHolder.getCurrentUser() != null) {
			user = this.userService.selectByPrimaryKey(SecurityUserHolder
					.getCurrentUser().getId());
		} else {
			int len = session_u_id.length();
			session_u_id = session_u_id.substring(0, len - 5);
			session_u_id = session_u_id.substring(5, session_u_id.length());
			user = this.userService
					.selectByPrimaryKey(CommUtil.null2Long(session_u_id));
		}
		user = user.getParent() == null ? user : user.getParent();
		String path = this.storeTools
				.createUserFolder(request, user.getStore());
		String url = this.storeTools.createUserFolderURL(user.getStore());
		double csize = CommUtil.fileSize(new File(path));
		double img_remain_size = 0.0D;
		if (user.getStore().getGrade().getSpaceSize() > 0.0F) {
			img_remain_size = CommUtil.div(
					Double.valueOf(user.getStore().getGrade().getSpaceSize()
							* 1024.0F - csize), Integer.valueOf(1024));
			json_map.put("remainSpace", Double.valueOf(img_remain_size));
		}
		if (img_remain_size >= 0.0D) {
			try {
				if ((fileName == null) || ("".equals(fileName))) {
					fileName = "imgFile";
				}
				Map map = CommUtil.saveFileToServer(request, fileName, path,
						null, null);
				if (CommUtil.null2String(map.get("fileName")).equals("")) {

				}
				Map params = Maps.newHashMap();
				params.put("store_id", user.getStore().getId());
				
				List<WaterMark> wms = this.waterMarkService.queryPageList(params);
				
				if (wms.size() > 0) {
					WaterMark mark = (WaterMark) wms.get(0);
					if ((mark.getWm_image_open())
							&& (mark.getWm_image() != null)) {
						String pressImg = request.getSession()
								.getServletContext().getRealPath("")
								+ File.separator
								+ mark.getWm_image().getPath()
								+ File.separator + mark.getWm_image().getName();
						String targetImg = path + File.separator
								+ map.get("fileName");
						int pos = mark.getWm_image_pos();
						float alpha = mark.getWm_image_alpha();
						CommUtil.waterMarkWithImage(pressImg, targetImg, pos,alpha);
					}
					if (mark.getWm_text_open()) {
						String targetImg = path + File.separator
								+ map.get("fileName");
						int pos = mark.getWm_text_pos();
						String text = mark.getWm_text();
						String markContentColor = mark.getWm_text_color();
						CommUtil.waterMarkWithText(
								targetImg,
								targetImg,
								text,
								markContentColor,
								new Font(mark.getWm_text_font(), 1, mark
										.getWm_text_font_size()), pos, 100.0F);
					}
				}
				Accessory image = new Accessory();
				image.setAddTime(new Date());
				image.setExt((String) map.get("mime"));
				image.setPath(url);
				image.setWidth(CommUtil.null2Int(map.get("width")));
				image.setHeight(CommUtil.null2Int(map.get("height")));
				image.setName(CommUtil.null2String(map.get("fileName")));
				image.setUser(user);
				Album album = null;
				if ((album_id != null) && (!album_id.equals(""))) {
					album = this.albumService.selectByPrimaryKey(CommUtil
							.null2Long(album_id));
				} else {
					album = this.albumService.getDefaultAlbum(user.getId());
					if (album == null) {
						album = new Album();
						album.setAddTime(new Date());
						album.setAlbum_name("默认相册");
						album.setAlbum_sequence(55536);
						album.setAlbum_default(true);
						this.albumService.saveEntity(album);
					}
				}
				image.setAlbum(album);
				this.accessoryService.saveEntity(image);
				
				String ext = image.getExt().indexOf(".") < 0 ? "."
						+ image.getExt() : image.getExt();
				String source = request.getSession().getServletContext()
						.getRealPath("/")
						+ image.getPath() + File.separator + image.getName();
				String target = source + "_small" + ext;
				CommUtil.createSmall(source, target, this.configService
						.getSysConfig().getSmallWidth(), this.configService
						.getSysConfig().getSmallHeight());

				String midext = image.getExt().indexOf(".") < 0 ? "."
						+ image.getExt() : image.getExt();
				String midtarget = source + "_middle" + ext;
				CommUtil.createSmall(source, midtarget, this.configService
						.getSysConfig().getMiddleWidth(), this.configService
						.getSysConfig().getMiddleHeight());
				json_map.put("url", CommUtil.getURL(request) + "/" + url + "/"
						+ image.getName());
				json_map.put("id", image.getId());
				double csize2 = CommUtil.fileSize(new File(path));
				double img_remain_size2 = 0.0D;
				if (user.getStore().getGrade().getSpaceSize() > 0.0F) {
					img_remain_size2 = CommUtil.div(
							Double.valueOf(user.getStore().getGrade()
									.getSpaceSize()
									* 1024.0F - csize2), Integer.valueOf(1024));
					json_map.put("remainSpace",
							Double.valueOf(img_remain_size2));
				} else {
					json_map.put("remainSpace", "null");
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			json_map.put("url", "");
			json_map.put("id", "");
			json_map.put("remainSpace", Integer.valueOf(-1));
		}
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		try {
			PrintWriter writer = response.getWriter();
			writer.print(JSON.toJSONString(json_map));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 商品图片删除
	 * @param request
	 * @param response
	 * @param image_id
	 */
	@SecurityMapping(title = "商品图片删除", value = "/goods_image_del*", rtype = "seller", rname = "发布新商品", rcode = "goods_seller", rgroup = "商品管理")
	@RequestMapping({ "/goods_image_del" })
	public void goods_image_del(HttpServletRequest request,
			HttpServletResponse response, String image_id) {
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		String path = this.storeTools
				.createUserFolder(request, user.getStore());
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		try {
			Map map = Maps.newHashMap();
			Accessory img = this.accessoryService.selectByPrimaryKey(CommUtil
					.null2Long(image_id));
			Boolean ret = Boolean.valueOf(false);
			if (img != null) {
				for (Goods goods : img.getGoods_main_list()) {
					goods.setGoods_main_photo(null);
					this.goodsService.updateById(goods);
				}
				for (Goods goods1 : img.getGoods_list()) {
					goods1.getGoods_photos().remove(img);
					this.goodsService.updateById(goods1);
				}
				this.accessoryService.deleteById(img.getId());
				ret = true;
				if (ret.booleanValue()) {
					CommUtil.deleteFile(request.getSession()
							.getServletContext().getRealPath("/")
							+ img.getPath()
							+ File.separator
							+ img.getName()
							+ "_middle." + img.getExt());
					
					RedPigCommonUtil.del_acc(request, img);
				}
				double csize2 = CommUtil.fileSize(new File(path));
				double img_remain_size2 = 0.0D;
				if (user.getStore().getGrade().getSpaceSize() > 0.0F) {
					img_remain_size2 = CommUtil.div(
							Double.valueOf(user.getStore().getGrade()
									.getSpaceSize()
									* 1024.0F - csize2), Integer.valueOf(1024));
					map.put("remainSpace", Double.valueOf(img_remain_size2));
				} else {
					map.put("remainSpace", "null");
				}
			}
			map.put("result", ret);
			PrintWriter writer = response.getWriter();
			writer.print(JSON.toJSONString(map));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 发布商品第三步
	 * @param request
	 * @param response
	 * @param id
	 * @param goods_class_id
	 * @param image_ids
	 * @param goods_main_img_id
	 * @param user_class_ids
	 * @param goods_brand_id
	 * @param goods_spec_ids
	 * @param goods_properties
	 * @param intentory_details
	 * @param goods_session
	 * @param transport_type
	 * @param transport_id
	 * @param publish_goods_status
	 * @param publish_day
	 * @param publish_hour
	 * @param publish_min
	 * @param f_code_profix
	 * @param f_code_count
	 * @param advance_date
	 * @param goods_top_format_id
	 * @param goods_bottom_format_id
	 * @param delivery_area_id
	 * @param goods_spec_id_value
	 * @param color_info
	 * @param inventory_type
	 * @param serve_ids
	 * @param earnest
	 * @param rest_start_date
	 * @param rest_end_date
	 * @param edit_advance_info
	 * @param special_goods_type
	 * @return
	 */
	@SecurityMapping(title = "发布商品第三步", value = "/add_goods_finish*", rtype = "seller", rname = "发布新商品", rcode = "goods_seller", rgroup = "商品管理")
	@RequestMapping({ "/add_goods_finish" })
	public ModelAndView add_goods_finish(HttpServletRequest request,
			HttpServletResponse response, String id, String goods_class_id,
			String image_ids, String goods_main_img_id, String user_class_ids,
			String goods_brand_id, String goods_spec_ids,
			String goods_properties, String intentory_details,
			String goods_session, String transport_type, String transport_id,
			String publish_goods_status, String publish_day,
			String publish_hour, String publish_min, String f_code_profix,
			String f_code_count, String advance_date,
			String goods_top_format_id, String goods_bottom_format_id,
			String delivery_area_id, String goods_spec_id_value,
			String color_info, String inventory_type, String serve_ids,
			String earnest, String rest_start_date, String rest_end_date,
			String edit_advance_info, String special_goods_type) {
		ModelAndView mv = null;
		String goods_session1 = CommUtil.null2String(request.getSession(false).getAttribute("goods_session"));
		if (goods_session1.equals("")) {
			mv = new RedPigJModelAndView(
					"user/default/sellercenter/seller_error.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request,
					response);
			mv.addObject("op_title", "禁止重复提交表单");
			mv.addObject("url", CommUtil.getURL(request) + "/items");
		} else {
			User user = this.userService.selectByPrimaryKey(SecurityUserHolder.getCurrentUser().getId());
			Store store = user.getStore();
			if (user.getParent() != null) {
				store = user.getParent().getStore();
			}
			GoodsClass gc = this.goodsClassService.selectByPrimaryKey(Long.valueOf(Long.parseLong(goods_class_id)));
			boolean m = false;
			
			if ((store.getGc_detail_info() == null 
					|| store.getGc_detail_info().length() == 0) 
					|| (store.getGc_detail_info().equals(""))) {
				
				if (gc.getParent().getId().equals(store.getGc_main_id())) {
					m = true;
				}
				if (gc.getParent().getParent() != null) {
					if (gc.getParent().getParent().getId().equals(store.getGc_main_id())) {
						m = true;
					}
				}
			} else {
				Set<GoodsClass> store_gcs = this.storeTools.query_store_DetailGc(store.getGc_detail_info());
				m = store_gcs.contains(gc.getParent());
			}
			
			if (m == false) {
				mv = new RedPigJModelAndView(
						"user/default/sellercenter/seller_error.html",
						this.configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 0, request,
						response);
				mv.addObject("op_title", "该商品主营类目不存在！");
				mv.addObject("url", CommUtil.getURL(request) + "/items");
				return mv;
			}
			
			if ((goods_session1.equals(goods_session)) && (m)) {
				WebForm wf = new WebForm();
				Goods goods = null;
				String obj_status = null;
				Map temp_params = Maps.newHashMap();
				Set<Long> temp_ids = new HashSet();
				if (id.equals("")) {
					goods = (Goods) WebForm.toPo(request, Goods.class);
					goods.setAddTime(new Date());
					goods.setGoods_store(store);
				} else {
					Goods obj = this.goodsService.selectByPrimaryKey(Long.valueOf(Long.parseLong(id)));
					System.out.println(obj.getGoods_main_photo());
					
					//编辑时候将所有关联关系的数据都清空
					this.goodsService.batchDeleteGoodsPhotos(obj.getId(), obj.getGoods_photos());
					obj.getGoods_photos().clear();
					this.goodsService.batchDeleteGoodsSpecProperty(obj.getId(), obj.getGoods_specs());
					obj.getGoods_specs().clear();
					this.goodsService.batchDeleteUserGoodsClass(obj.getId(), obj.getGoods_ugcs());
					obj.getGoods_ugcs().clear();
					
					BigDecimal old_price = obj.getGoods_current_price();
					obj_status = CommUtil.null2String(Integer.valueOf(obj.getGoods_status()));
					goods = (Goods) WebForm.toPo(request, obj);
					goods.setPrice_history(old_price);
				}
				if ((serve_ids != null) && (!"".equals(serve_ids))) {
					List list = Lists.newArrayList();
					String[] arr = serve_ids.split(",");
					for (int i = 0; i < arr.length; i++) {
						String s_id = arr[i];
						list.add(i, s_id);
					}
					goods.setMerchantService_info(JSON.toJSONString(list));
				}
				if (goods.getGroup_buy() != 2) {
					goods.setGoods_current_price(goods.getStore_price());
				}
				goods.setGoods_name(Jsoup.clean(goods.getGoods_name(),Whitelist.none()));
				String goods_details = CommUtil.filterHTML(goods.getGoods_details());
				
				goods_details = goods_details.replaceAll(CommUtil.getURL(request), "");
				
				goods.setGoods_details(goods_details);
				
				String goods_details_mobile = CommUtil.filterHTML(goods.getGoods_details_mobile());
				goods_details_mobile = goods_details_mobile.replaceAll(CommUtil.getURL(request), "");
				goods.setGoods_details_mobile(goods_details_mobile);
				
				if (store.getGrade().getGoods_audit() == 0) {
					goods.setGoods_status(-5);
					goods.setPublish_goods_status(CommUtil.null2Int(publish_goods_status));
					if (publish_goods_status.equals("0")) {
						goods.setGoods_seller_time(new Date());
					}
					if (publish_goods_status.equals("2")) {
						String str = publish_day + " " + publish_hour + ":" + publish_min;
						Date date = CommUtil.formatDate(str, "yyyy-MM-dd HH:mm");
						goods.setGoods_seller_time(date);
					}
				} else if (store.getGrade().getGoods_audit() == 1) {
					if (publish_goods_status.equals("0")) {
						goods.setGoods_seller_time(new Date());
						goods.setGoods_status(0);
					}
					if (publish_goods_status.equals("1")) {
						goods.setGoods_seller_time(new Date());
						goods.setGoods_status(1);
					}
					if (publish_goods_status.equals("2")) {
						String str = publish_day + " " + publish_hour + ":" + publish_min;
						Date date = CommUtil.formatDate(str, "yyyy-MM-dd HH:mm");
						goods.setGoods_seller_time(date);
						goods.setGoods_status(2);
					}
				}
				Accessory main_img = null;
				if ((goods_main_img_id != null) && (!goods_main_img_id.equals(""))) {
					main_img = this.accessoryService.selectByPrimaryKey(Long.valueOf(Long.parseLong(goods_main_img_id)));
				}
				goods.setGoods_main_photo(main_img);
				
				goods.getGoods_ugcs().clear();
				
				String[] ugc_ids = user_class_ids.split(",");
				temp_ids.clear();
				List<UserGoodsClass> u_class_list = Lists.newArrayList();
				
				for (String ugc_id : ugc_ids) {
					if (!ugc_id.equals("")) {
						UserGoodsClass ugc = this.userGoodsClassService.selectByPrimaryKey(Long.valueOf(Long.parseLong(ugc_id)));
						u_class_list.add(ugc);
					}
				}
				
				goods.setGoods_ugcs(u_class_list);
				String[] img_ids = image_ids.split(",");
				
				goods.getGoods_photos().clear();
				temp_ids.clear();
				for (String img_id : img_ids) {
					if (!img_id.equals("")) {
						temp_ids.add(CommUtil.null2Long(img_id));
					}
				}
				
				if (!temp_ids.isEmpty()) {
					temp_params.clear();
					temp_params.put("ids", temp_ids);
					
					List<Accessory> temp_list = this.accessoryService.queryPageList(temp_params);
					
					goods.getGoods_photos().addAll((Collection) temp_list);
				}
				if ((goods_brand_id != null) && (!goods_brand_id.equals(""))) {
					GoodsBrand goods_brand = this.goodsBrandService.selectByPrimaryKey(Long.valueOf(Long.parseLong(goods_brand_id)));
					goods.setGoods_brand(goods_brand);
				}
				
				goods.getGoods_specs().clear();
				String[] spec_ids = goods_spec_ids.split(",");
				temp_ids.clear();
				
				for (String spec_id : spec_ids) {
					if (!spec_id.equals("")) {
						temp_ids.add(CommUtil.null2Long(spec_id));
					}
				}
				if (!temp_ids.isEmpty()) {
					temp_params.clear();
					temp_params.put("ids", temp_ids);
					
					List<GoodsSpecProperty> gsps = Lists.newArrayList();
					for (Long gspId : temp_ids) {
						GoodsSpecProperty gsp = new GoodsSpecProperty();
						gsp.setId(gspId);
						
						gsps.add(gsp);
					}
					
					this.goodsService.batchDeleteGoodsSpecProperty(goods.getId(), gsps);
					
					goods.getGoods_specs().addAll(gsps);
					
				}
				
				if (!goods_spec_id_value.isEmpty()) {
					String[] id_values = goods_spec_id_value.split(",");
					List list = Lists.newArrayList();
					for (String id_value : id_values) {
						String[] single_id_value = id_value.split(":");
						Map map = Maps.newHashMap();
						map.put("id", single_id_value[0]);
						map.put("name", single_id_value[1]);
						list.add(map);
					}
					goods.setGoods_specs_info(JSON.toJSONString(list));
				}
				List maps = Lists.newArrayList();
				String[] properties = goods_properties.split(";");
				for (String property : properties) {
					
					if (!property.equals("")) {
						String[] list = property.split(",");
						Map map = Maps.newHashMap();
						map.put("id", list[0]);
						map.put("val", list[1]);
						map.put("name",this.goodsTypePropertyService.selectByPrimaryKey(Long.valueOf(Long.parseLong(list[0]))).getName());
						maps.add(map);
					}
				}
				
				goods.setGoods_property(JSON.toJSONString(maps));
				if (inventory_type.equals("spec")) {
					((List) maps).clear();
					boolean warn_suppment = false;
					String[] inventory_list = intentory_details.split(";");
					for (String inventory : inventory_list) {
						if (!inventory.equals("")) {
							String[] list = inventory.split(",");
							Map map = Maps.newHashMap();
							map.put("id", list[0]);
							map.put("count", list[1]);
							map.put("supp", list[2]);
							map.put("price", list[3]);
							map.put("code", list[4]);
							((List) maps).add(map);
							if (CommUtil.null2Int(list[2]) > CommUtil.null2Int(list[1])) {
								warn_suppment = true;
							}
						}
					}
					if (warn_suppment) {
						goods.setWarn_inventory_status(-1);
					} else {
						goods.setWarn_inventory_status(0);
					}
					goods.setGoods_inventory_detail(JSON.toJSONString(maps));
				} else {
					goods.setGoods_inventory_detail(null);
				}
				if ((color_info != null) && (!"".equals(color_info))) {
					Map color_map = Maps.newHashMap();
					List<Map> lists = Lists.newArrayList();
					String[] colors = color_info.split("\\|");
					for (String color : colors) {
						if (!((String) color).equals("")) {
							Map map = Maps.newHashMap();
							String[] all_img_ids = ((String) color).split("#");
							String color_id = all_img_ids[1];
							String other_img_ids = all_img_ids[0];
							map.put("color_id", color_id);
							map.put("img_ids", other_img_ids);
							lists.add(map);
						}
					}
					color_map.put("data", lists);
					goods.setGoods_color_json(JSON.toJSONString(color_map));
				} else {
					goods.setGoods_color_json(null);
				}
				goods.setGoods_type(1);
				if (goods.getInventory_type().equals("all")) {
					Integer inventory = goods.getGoods_inventory();
					if (CommUtil.null2Int(inventory)
							- CommUtil
									.null2Int(goods.getGoods_warn_inventory()) > 0) {
						goods.setWarn_inventory_status(0);
					} else {
						goods.setWarn_inventory_status(-1);
					}
				}
				if (CommUtil.null2Int(transport_type) == 0) {
					Transport trans = this.transportService.selectByPrimaryKey(CommUtil
							.null2Long(transport_id));
					goods.setTransport(trans);
				}
				if (CommUtil.null2Int(transport_type) == 1) {
					goods.setTransport(null);
				}
				if (CommUtil.null2String(special_goods_type).equals("1")) {
					goods.setF_sale_type(1);
					goods.setAdvance_sale_type(0);
					goods.setGoods_limit(0);
					if (CommUtil.null2Int(f_code_count) > 0) {
						Set<String> set = new HashSet();
						while (set.size() != CommUtil.null2Int(f_code_count)) {
							set.add((f_code_profix + CommUtil.randomString(12)).toUpperCase());
						}
						Object f_code_maps = Lists.newArrayList();
						if (!CommUtil.null2String(goods.getGoods_f_code())
								.equals("")) {
							f_code_maps = JSON.parseArray(
									goods.getGoods_f_code(), Map.class);
						}
						for (Iterator color = set.iterator(); ((Iterator) color).hasNext();) {
							String code = (String) ((Iterator) color).next();
							Map f_code_map = Maps.newHashMap();
							f_code_map.put("code", code);
							f_code_map.put("status", Integer.valueOf(0));
							((List) f_code_maps).add(f_code_map);
						}
						if (((List) f_code_maps).size() > 0) {
							goods.setGoods_f_code(JSON.toJSONString(f_code_maps));
						}
					}
				} else if (CommUtil.null2String(special_goods_type).equals("2")) {
					if (edit_advance_info.equals("add")) {
						goods.setAdvance_sale_type(1);
						goods.setGoods_limit(0);
						goods.setF_sale_type(0);
						goods.setGoods_status(-5);
						goods.setGoods_cod(-1);
						goods.setAdvance_date(CommUtil.formatDate(advance_date));
						List lists = Lists.newArrayList();
						Map map1 = Maps.newHashMap();
						map1.put("advance_price",
								goods.getStore_price());
						map1.put("earnest", new BigDecimal(earnest));
						map1.put(
								"final_earnest",
								goods.getStore_price().subtract(
										new BigDecimal(earnest)));
						map1.put("rest_start_date", rest_start_date);
						map1.put("rest_end_date", rest_end_date);
						map1.put("advance_date", advance_date);
						lists.add(map1);
						String temp = JSON.toJSONString(lists);
						goods.setAdvance_sale_info(temp);
					}
				} else if (CommUtil.null2String(special_goods_type).equals("3")) {
					goods.setGoods_limit(1);
					goods.setAdvance_sale_type(0);
					goods.setF_sale_type(0);
				} else {
					goods.setGoods_limit(0);
					goods.setF_sale_type(0);
					goods.setAdvance_sale_type(0);
				}
				goods.setGoods_top_format_id(CommUtil
						.null2Long(goods_top_format_id));
				GoodsFormat gf = this.goodsFormatService.selectByPrimaryKey(CommUtil
						.null2Long(goods_top_format_id));
				if (gf != null) {
					goods.setGoods_top_format_content(gf.getGf_content());
				} else {
					goods.setGoods_top_format_content(null);
				}
				goods.setGoods_bottom_format_id(CommUtil
						.null2Long(goods_bottom_format_id));
				gf = this.goodsFormatService.selectByPrimaryKey(CommUtil
						.null2Long(goods_bottom_format_id));
				if (gf != null) {
					goods.setGoods_bottom_format_content(gf.getGf_content());
				} else {
					goods.setGoods_bottom_format_content(null);
				}
				goods.setDelivery_area_id(CommUtil.null2Long(delivery_area_id));
				Area de_area = this.areaService.selectByPrimaryKey(CommUtil
						.null2Long(delivery_area_id));
				String delivery_area = de_area.getParent().getParent()
						.getAreaName()
						+ de_area.getParent().getAreaName()
						+ de_area.getAreaName();
				goods.setDelivery_area(delivery_area);
				
				goods.setGc(gc);
				
				if (id.equals("")) {
					
					
					this.goodsService.saveEntity(goods);
					
					String qr_img_path = this.goodsTools.createGoodsQR(request,goods);
					goods.setQr_img_path(qr_img_path);
					
					this.goodsService.update(goods);
					
					this.goodsService.saveGoodsPhotos(goods.getId(), goods.getGoods_photos());
					this.goodsService.saveGoodsSpecProperty2(goods.getId(), goods.getGoods_specs());
					
					this.goodsService.saveGoodsUserGoodsClass(goods.getId(),goods.getGoods_ugcs());
					
					
					this.goodsTools.addGoodsLucene(goods);
				} else {
					if (("0".equals(obj_status)) && ("0".equals(publish_goods_status))) {
						this.goodsTools.addGoodsLucene(goods);
					}
					if (("0".equals(obj_status)) 
							&& (("1".equals(publish_goods_status)) 
							|| ("2".equals(publish_goods_status)))) {
						this.goodsTools.deleteGoodsLucene(goods);
					}
					if ((("1".equals(obj_status)) 
							|| ("2".equals(obj_status)))
							&& ("0".equals(publish_goods_status))) {
						this.goodsTools.addGoodsLucene(goods);
					}
					
					String qr_img_path = this.goodsTools.createGoodsQR(request,goods);
					goods.setQr_img_path(qr_img_path);
					//编辑时候,如果有多对多、一对多的关联数据,先需要删除所有的关联关系,然后在新增
					
					this.goodsService.saveGoodsPhotos(goods.getId(), goods.getGoods_photos());
					
					this.goodsService.saveGoodsSpecProperty2(goods.getId(), goods.getGoods_specs());
					
					List<UserGoodsClass> goods_ugcs = Lists.newArrayList();
					UserGoodsClass ugc = new UserGoodsClass();
					ugc.setId(Long.parseLong(goods_class_id.trim()));
					goods_ugcs.add(ugc);
					this.goodsService.saveGoodsUserGoodsClass(goods.getId(),goods_ugcs);
					
					goods.setGc(gc);
					
					this.goodsService.updateById(goods);
				}
				String goods_view_url = this.configService.getSysConfig().getIndexUrl()+ "/items_"+ goods.getId() + "";
				
				if ((this.configService.getSysConfig().getSecond_domain_open())
						&& (goods.getGoods_store().getStore_second_domain() != "")
						&& (goods.getGoods_type() == 1)) {
					String store_second_domain = "http://"
							+ goods.getGoods_store().getStore_second_domain()
							+ "." + CommUtil.generic_domain(request);
					goods_view_url = store_second_domain + "/goods_"
							+ goods.getId() + "";
				}
				if ((id == null) || (id.equals(""))) {
					mv = new RedPigJModelAndView(
							"user/default/sellercenter/add_goods_finish.html",
							this.configService.getSysConfig(),
							this.userConfigService.getUserConfig(), 0, request,
							response);
					if (store.getGrade().getGoods_audit() == 0) {
						mv.addObject("op_title", "商品发布成功,运营商会尽快为您审核！");
						mv.addObject("url", CommUtil.getURL(request) + "/goods");
					}
					if (store.getGrade().getGoods_audit() == 1) {
						mv.addObject("op_title", "商品发布成功！");
						mv.addObject("url", CommUtil.getURL(request) + "/goods");
					}
				} else {
					mv = new RedPigJModelAndView(
							"user/default/sellercenter/seller_success.html",
							this.configService.getSysConfig(),
							this.userConfigService.getUserConfig(), 0, request,
							response);
					if (store.getGrade().getGoods_audit() == 0) {
						mv.addObject("op_title", "商品编辑成功,运营商会尽快为您审核");
						mv.addObject("url", CommUtil.getURL(request)
								+ "/items");
					}
					if (store.getGrade().getGoods_audit() == 1) {
						mv.addObject("op_title", "商品编辑成功");
						mv.addObject("url", CommUtil.getURL(request)
								+ "/items");
					}
				}
				mv.addObject("goods_view_url", goods_view_url);
				mv.addObject("goods_edit_url", CommUtil.getURL(request)
						+ "/goods_edit?id=" + goods.getId());
				request.getSession(false).removeAttribute("goods_session");
			} else {
				mv = new RedPigJModelAndView(
						"user/default/sellercenter/seller_error.html",
						this.configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 0, request,
						response);
				mv.addObject("op_title", "参数错误");
				mv.addObject("url", CommUtil.getURL(request) + "/items");
			}
		}
		return mv;
	}
	
	/**
	 * 加载商品分类
	 * @param request
	 * @param response
	 * @param pid
	 * @param session
	 */
	@SecurityMapping(title = "加载商品分类", value = "/load_goods_class*", rtype = "seller", rname = "发布新商品", rcode = "goods_seller", rgroup = "商品管理")
	@RequestMapping({ "/load_goods_class" })
	public void load_goods_class(HttpServletRequest request,
			HttpServletResponse response, String pid, String session) {
		GoodsClass obj = this.goodsClassService.selectByPrimaryKey(CommUtil
				.null2Long(pid));
		List<Map> list = Lists.newArrayList();
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		Store store = user.getStore();
		if (obj != null) {
			List<Integer> ls;
			if (obj.getLevel() == 0) {
				Map map = this.storeTools.query_MainGc_Map(
						CommUtil.null2String(obj.getId()),
						store.getGc_detail_info());
				if (map != null) {
					ls = (List) map.get("gc_list");
					if (ls != null) {
						for (Integer l : ls) {
							Map map_gc = Maps.newHashMap();
							GoodsClass gc = this.goodsClassService
									.selectByPrimaryKey(CommUtil.null2Long(l));
							map_gc.put("id", gc.getId());
							map_gc.put("className", gc.getClassName());
							map.put("level", Integer.valueOf(gc.getLevel()));
							list.add(map_gc);
						}
					}
				}
			} else if (obj.getLevel() == 1) {
				for (GoodsClass child : obj.getChilds()) {
					Map map_gc = Maps.newHashMap();
					map_gc.put("id", child.getId());
					map_gc.put("className", child.getClassName());
					map_gc.put("level", Integer.valueOf(child.getLevel()));
					list.add(map_gc);
				}
			}
			if (CommUtil.null2Boolean(session)) {
				request.getSession(false).setAttribute("goods_class_info", obj);
			}
		}
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		try {
			PrintWriter writer = response.getWriter();
			writer.print(JSON.toJSONString(list));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 添加用户常用商品分类
	 * @param request
	 * @param response
	 */
	@SecurityMapping(title = "添加用户常用商品分类", value = "/load_goods_class*", rtype = "seller", rname = "发布新商品", rcode = "goods_seller", rgroup = "商品管理")
	@RequestMapping({ "/add_goods_class_staple" })
	public void add_goods_class_staple(HttpServletRequest request,
			HttpServletResponse response) {
		String ret = "error";
		if (request.getSession(false).getAttribute("goods_class_info") != null) {
			GoodsClass session_gc = (GoodsClass) request.getSession(false)
					.getAttribute("goods_class_info");
			GoodsClass gc = this.goodsClassService.selectByPrimaryKey(session_gc
					.getId());
			User user = this.userService.selectByPrimaryKey(SecurityUserHolder
					.getCurrentUser().getId());
			user = user.getParent() == null ? user : user.getParent();
			String json = "";
			List<Map> list_map = Lists.newArrayList();
			if ((user.getStaple_gc() != null)
					&& (!user.getStaple_gc().equals(""))) {
				json = user.getStaple_gc();
				list_map = JSON.parseArray(json, Map.class);
			}
			if (list_map.size() > 0) {
				boolean flag = true;
				for (Map staple : list_map) {
					if (gc.getId().toString()
							.equals(CommUtil.null2String(staple.get("id")))) {
						flag = false;
						break;
					}
				}
				if (flag) {
					Map map = Maps.newHashMap();
					map.put("name",
							gc.getParent().getClassName() + ">"
									+ gc.getClassName());
					map.put("id", gc.getId());
					list_map.add(map);
					json = JSON.toJSONString(list_map);
					ret = JSON.toJSONString(map);
				}
			} else {
				Map map = Maps.newHashMap();
				map.put("name",
						gc.getParent().getClassName() + ">" + gc.getClassName());
				map.put("id", gc.getId());
				list_map.add(map);
				json = JSON.toJSONString(list_map);
				ret = JSON.toJSONString(map);
			}
			user.setStaple_gc(json);
			this.userService.updateById(user);
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
	
	/**
	 * 删除用户常用商品分类
	 * @param request
	 * @param response
	 * @param id
	 */
	@SecurityMapping(title = "删除用户常用商品分类", value = "/del_goods_class_staple*", rtype = "seller", rname = "发布新商品", rcode = "goods_seller", rgroup = "商品管理")
	@RequestMapping({ "/del_goods_class_staple" })
	public void del_goods_class_staple(HttpServletRequest request,
			HttpServletResponse response, String id) {
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		List<Map> list_map = JSON.parseArray(user.getStaple_gc(), Map.class);
		boolean ret = false;
		for (Map map : list_map) {
			if (CommUtil.null2String(map.get("id")).equals(id)) {
				ret = list_map.remove(map);
				break;
			}
		}
		user.setStaple_gc(JSON.toJSONString(list_map));
		this.userService.updateById(user);
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
	 * 根据用户常用商品分类加载分类信息
	 * @param request
	 * @param response
	 * @param id
	 * @param name
	 */
	@SecurityMapping(title = "根据用户常用商品分类加载分类信息", value = "/load_goods_class_staple*", rtype = "seller", rname = "发布新商品", rcode = "goods_seller", rgroup = "商品管理")
	@RequestMapping({ "/load_goods_class_staple" })
	public void load_goods_class_staple(HttpServletRequest request,
			HttpServletResponse response, String id, String name) {
		GoodsClass obj = null;
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		if ((id != null) && (!id.equals(""))) {
			List<Map> list_map = JSON.parseArray(user.getStaple_gc(), Map.class);
			for (Map map : list_map) {
				if (CommUtil.null2String(map.get("id")).equals(id)) {
					obj = this.goodsClassService.selectByPrimaryKey(CommUtil.null2Long(map.get("id")));
				}
			}
		}
		if ((name != null) && (!name.equals(""))) {
			Map params = Maps.newHashMap();
			params.put("className", name);
			List<GoodsClass> objs = this.goodsClassService.queryPageList(params,0,1);
			
			if (objs.size() > 0) {
				obj = (GoodsClass) objs.get(0);
			}
		}
		List<List<Map>> list = Lists.newArrayList();
		String json = "";
		if (obj != null) {
			List<GoodsClass> gcs = this.storeTools.query_store_gc(user.getStore());

			List<Map> second_list = Lists.newArrayList();
			List<Map> third_list = Lists.newArrayList();
			List<Map> other_list = Lists.newArrayList();
			
			List<Long> gcsId = Lists.newArrayList();
			
			for (GoodsClass gc : gcs) {
				gcsId.add(gc.getId());
			}
			List<Long> objIds = Lists.newArrayList();
			
			objIds.add(obj.getId());
			if(obj.getLevel()==1){
				objIds.add(obj.getParent().getId());
			}
			
			if(obj.getLevel()==2){
				objIds.add(obj.getParent().getId());
				objIds.add(obj.getParent().getParent().getId());
			}
			boolean flag = false;
			for(Long gId : gcsId){
				for(Long objId:objIds){
					if(gId.equals(objId)){
						flag = true;
					}
				}
			}
			
//			if (gcs.contains(obj.getParent())) {
			if (flag) {
				request.getSession(false).setAttribute("goods_class_info", obj);
				Map params = Maps.newHashMap();
				
				if (obj.getLevel() == 2) {
					params.put("parent_id", obj.getParent().getParent().getId());
					params.put("orderBy", "sequence");
					params.put("orderType", "asc");
					List<GoodsClass> second_gcs = this.goodsClassService.queryPageList(params);
					
					for (GoodsClass gc : second_gcs) {
						Map map = Maps.newHashMap();
						map.put("id", gc.getId());
						map.put("level", Integer.valueOf(gc.getLevel()));
						map.put("className", gc.getClassName());
						second_list.add(map);
					}
					
					params.clear();
					params.put("parent_id", obj.getParent().getId());
					params.put("orderBy", "sequence");
					params.put("orderType", "asc");
					
					List<GoodsClass> third_gcs = this.goodsClassService.queryPageList(params);
					
					for (GoodsClass gc : third_gcs) {
						Map map = Maps.newHashMap();
						map.put("id", ((GoodsClass) gc).getId());
						map.put("level",Integer.valueOf(((GoodsClass) gc).getLevel()));
						map.put("className", ((GoodsClass) gc).getClassName());
						third_list.add(map);
					}
				}
				
				if (obj.getLevel() == 1) {
					params.clear();
					params.put("parent_id", obj.getParent().getId());
					params.put("orderBy", "sequence");
					params.put("orderType", "asc");
					List<GoodsClass> third_gcs = this.goodsClassService.queryPageList(params);
					
					for (Iterator gc = third_gcs.iterator(); ((Iterator) gc).hasNext();) {
						GoodsClass gc1 = (GoodsClass) ((Iterator) gc).next();
						Map map = Maps.newHashMap();
						map.put("id", gc1.getId());
						map.put("level", Integer.valueOf(gc1.getLevel()));
						map.put("className", gc1.getClassName());
						second_list.add(map);
					}
				}
				Map map = Maps.newHashMap();
				String staple_info = generic_goods_class_info(obj);
				map.put("staple_info",staple_info.substring(0, staple_info.length() - 1));
				other_list.add(map);
				list.add(second_list);
				list.add(third_list);
				list.add(other_list);
				json = JSON.toJSONString(list);
			}
		}
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		try {
			PrintWriter writer = response.getWriter();
			writer.print(json);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private String generic_goods_class_info(GoodsClass gc) {
		String goods_class_info = gc.getClassName() + ">";
		if ((gc.getParent() != null) && (gc.getParent().getLevel() != 0)) {
			String class_info = generic_goods_class_info(gc.getParent());
			goods_class_info = class_info + goods_class_info;
		}
		return goods_class_info;
	}
	
	/**
	 * 出售中的商品列表
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @param goods_name
	 * @param goods_serial
	 * @param user_class_id
	 * @param goods_activity_status
	 * @return
	 */
	@SecurityMapping(title = "出售中的商品列表", value = "/items*", rtype = "seller", rname = "出售中的商品", rcode = "goods_list_seller", rgroup = "商品管理")
	@RequestMapping({ "/items" })
	public ModelAndView items(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,
			String orderType, String goods_name, String goods_serial,
			String user_class_id, String goods_activity_status) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/sellercenter/goods.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		
		Map<String,Object> maps= this.redPigQueryTools.getParams(currentPage, orderBy, orderType);
		maps.put("goods_status", 0);
		maps.put("goods_store_id", user.getStore().getId());
		maps.put("orderBy", "warn_inventory_status,addTime");
		maps.put("orderType", "desc");
		
		List activity = Lists.newArrayList();
		if ((goods_activity_status != null)
				&& (!goods_activity_status.equals(""))) {
			activity.add(goods_activity_status);
			this.queryTools.queryActivityGoods(maps, activity);
			mv.addObject("goods_activity_status", goods_activity_status);
		}
		
		if ((goods_name != null) && (!goods_name.equals(""))) {
			maps.put("goods_name_like", goods_name);
		}
		
		if ((goods_serial != null) && (!goods_serial.equals(""))) {
			maps.put("goods_serial", goods_serial);
		}
		
		if ((user_class_id != null) && (!user_class_id.equals(""))) {
			UserGoodsClass ugc = this.userGoodsClassService.selectByPrimaryKey(Long.valueOf(Long.parseLong(user_class_id)));
			maps.put("ugc_id", ugc.getId());
		}
		IPageList pList = this.goodsService.list(maps);
		
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		mv.addObject("storeTools", this.storeTools);
		mv.addObject("goodsViewTools", this.goodsViewTools);
		mv.addObject("goods_name", goods_name);
		mv.addObject("goods_serial", goods_serial);
		mv.addObject("user_class_id", user_class_id);
		return mv;
	}
	
	/**
	 * 仓库中的商品列表
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @param goods_name
	 * @param goods_serial
	 * @param user_class_id
	 * @return
	 */
	@SecurityMapping(title = "仓库中的商品列表", value = "/goods_storage*", rtype = "seller", rname = "仓库中的商品", rcode = "goods_storage_seller", rgroup = "商品管理")
	@RequestMapping({ "/goods_storage" })
	public ModelAndView goods_storage(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,
			String orderType, String goods_name, String goods_serial,
			String user_class_id) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/sellercenter/goods_storage.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		
		Map<String,Object> maps= this.redPigQueryTools.getParams(currentPage, orderBy, orderType);
		Set ids = Sets.newTreeSet();
		ids.add(Integer.valueOf(1));
		ids.add(Integer.valueOf(2));
		ids.add(Integer.valueOf(-5));
		ids.add(Integer.valueOf(-6));
		
		maps.put("goods_status_ids", ids);
		maps.put("goods_store_id", user.getStore().getId());
		maps.put("orderBy", "goods_seller_time");
		maps.put("orderType", "desc");
		
		
		if ((goods_name != null) && (!goods_name.equals(""))) {
			maps.put("goods_name_like", goods_name);
		}
		
		if ((goods_serial != null) && (!goods_serial.equals(""))) {
			maps.put("goods_serial", goods_serial);
		}
		
		if ((user_class_id != null) && (!user_class_id.equals(""))) {
			UserGoodsClass ugc = this.userGoodsClassService.selectByPrimaryKey(Long.valueOf(Long.parseLong(user_class_id)));
			maps.put("ugc_id", ugc.getId());
		}
		
		IPageList pList = this.goodsService.list(maps);
		
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		mv.addObject("storeTools", this.storeTools);
		mv.addObject("goodsViewTools", this.goodsViewTools);
		mv.addObject("goods_name", goods_name);
		mv.addObject("goods_serial", goods_serial);
		mv.addObject("user_class_id", user_class_id);
		return mv;
	}
	
	/**
	 * 违规下架商品
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @param goods_name
	 * @param goods_serial
	 * @param user_class_id
	 * @return
	 */
	@SecurityMapping(title = "违规下架商品", value = "/goods_out*", rtype = "seller", rname = "违规下架商品", rcode = "goods_out_seller", rgroup = "商品管理")
	@RequestMapping({ "/goods_out" })
	public ModelAndView goods_out(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,
			String orderType, String goods_name, String goods_serial,
			String user_class_id) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/sellercenter/goods_out.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		
		Map<String,Object> maps= this.redPigQueryTools.getParams(currentPage, orderBy, orderType);
		maps.put("goods_status", -2);
		maps.put("goods_store_id", user.getStore().getId());
		maps.put("orderBy", "goods_seller_time");
		maps.put("orderType", "desc");
		
		if ((goods_name != null) && (!goods_name.equals(""))) {
			maps.put("goods_name_like", goods_name);
		}
		
		if ((goods_serial != null) && (!goods_serial.equals(""))) {
			maps.put("goods_serial", goods_serial);
		}
		
		if ((user_class_id != null) && (!user_class_id.equals(""))) {
			UserGoodsClass ugc = this.userGoodsClassService.selectByPrimaryKey(Long.valueOf(Long.parseLong(user_class_id)));
			
			maps.put("ugc_id", ugc.getId());
		}
		
		IPageList pList = this.goodsService.list(maps);
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		mv.addObject("storeTools", this.storeTools);
		mv.addObject("goodsViewTools", this.goodsViewTools);
		mv.addObject("goods_name", goods_name);
		mv.addObject("goods_serial", goods_serial);
		mv.addObject("user_class_id", user_class_id);
		return mv;
	}
	
	/**
	 * 商品编辑
	 * @param request
	 * @param response
	 * @param id
	 * @param add_type
	 * @return
	 */
	@SuppressWarnings("deprecation")
	@SecurityMapping(title = "商品编辑", value = "/goods_edit*", rtype = "seller", rname = "出售中的商品", rcode = "goods_list_seller", rgroup = "商品管理")
	@RequestMapping({ "/goods_edit" })
	public ModelAndView goods_edit(HttpServletRequest request,
			HttpServletResponse response, String id, String add_type) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/sellercenter/add_goods_second.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		if (add_type == null) {
			request.getSession(false).removeAttribute("goods_class_info");
		}
		Goods obj = this.goodsService.selectByPrimaryKey(Long.valueOf(Long.parseLong(id)));
		
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder.getCurrentUser().getId());
		
		user = user.getParent() == null ? user : user.getParent();
		String uploadFilePath = this.configService.getSysConfig().getUploadFilePath();
		
		if (obj.getGoods_store().getUser().getId().equals(user.getId())) {
			String path = request.getSession().getServletContext()
					.getRealPath("/")
					+ File.separator
					+ uploadFilePath
					+ File.separator
					+ "store" + File.separator + user.getStore().getId();
			if (user.getStore().getGrade().getSpaceSize() > 0.0F) {
				double img_remain_size = CommUtil.div(
						Double.valueOf(user.getStore().getGrade()
								.getSpaceSize()
								* 1024.0F - CommUtil.fileSize(new File(path))),
						Integer.valueOf(1024));
				if (img_remain_size < 0.0D) {
					img_remain_size = -1.0D;
				}
				mv.addObject("img_remain_size", Double.valueOf(img_remain_size));
			}
			Map params = Maps.newHashMap();
			params.put("user_id", user.getId());
			params.put("display", Boolean.valueOf(true));
			params.put("parent", -1);
			params.put("orderBy", "sequence");
			params.put("orderType", "asc");
			
			List<UserGoodsClass> ugcs = this.userGoodsClassService.queryPageList(params);
			
			Map<String,Object> maps= this.redPigQueryTools.getParams(null, 8,"addTime", "desc");
			
			maps.put("user_id", user.getId());
			
			IPageList pList = this.accessoryService.list(maps);
			
			String photo_url = CommUtil.getURL(request) + "/load_photo";
			mv.addObject("photos", pList.getResult());
			mv.addObject(
					"gotoPageAjaxHTML",
					CommUtil.showPageAjaxHtml(photo_url, "",
							pList.getCurrentPage(), pList.getPages(),
							pList.getPageSize()));
			mv.addObject("ugcs", ugcs);
			mv.addObject("obj", obj);

			if (request.getSession(false).getAttribute("goods_class_info") != null) {
				GoodsClass session_gc = (GoodsClass) request.getSession(false)
						.getAttribute("goods_class_info");
				GoodsClass gc = this.goodsClassService.selectByPrimaryKey(session_gc
						.getId());
				mv.addObject("goods_class_info",
						this.storeTools.generic_goods_class_info(gc));
				mv.addObject("goods_class", gc);
				request.getSession(false).removeAttribute("goods_class_info");
				request.getSession(false).setAttribute("goods_class_info",this.storeTools.generic_goods_class_info(gc));
				params.clear();
				GoodsClass goods_class = new GoodsClass();
				if (gc.getLevel() == 2) {
					goods_class = gc.getParent().getParent();
				}
				if (gc.getLevel() == 1) {
					goods_class = gc.getParent();
				}
				params.clear();
				params.put("audit", Integer.valueOf(1));
		        maps.put("orderBy", "first_word");
		        maps.put("orderType", "asc");

				List<GoodsBrand> gbs = this.goodsBrandService.queryPageList(maps);
				
				mv.addObject("gbs", gbs);

				List<GoodsSpecification> color_spec_list = Lists.newArrayList();
				List<GoodsSpecification> other_spec_list = Lists.newArrayList();
				if (gc.getLevel() == 2) {
					Map spec_map = Maps.newHashMap();
					spec_map.put("store_id", user.getStore().getId());
					spec_map.put("orderBy", "sequence");
					spec_map.put("orderType", "asc");
					
					List<GoodsSpecification> goods_spec_list = this.goodsSpecificationService.queryPageList(spec_map);
					
					for (GoodsSpecification gspec : goods_spec_list) {
						for (GoodsClass spec_goodsclass_detail : gspec.getSpec_goodsClass_detail()) {
							if (gc.getId().equals(spec_goodsclass_detail.getId())) {
								if (gspec.getSpec_color() == 1) {
									color_spec_list.add(gspec);
								} else {
									other_spec_list.add(gspec);
								}
							}
						}
					}
				} else if (gc.getLevel() == 1) {
					Map spec_map = Maps.newHashMap();
					spec_map.put("store_id", user.getStore().getId());
					spec_map.put("goodsclass_id", gc.getId());
					spec_map.put("orderBy", "sequence");
					spec_map.put("orderType", "asc");
					
					other_spec_list = this.goodsSpecificationService.queryPageList(spec_map);
					
					for (GoodsSpecification gspec1 : other_spec_list) {
						if (gspec1.getSpec_color() == 1) {
							color_spec_list.add(gspec1);
						} else {
							other_spec_list.add(gspec1);
						}
					}
				}
				mv.addObject("color_spec_list", color_spec_list);
				mv.addObject("goods_spec_list", other_spec_list);
			} else if (obj.getGc() != null) {
				mv.addObject("goods_class_info",this.storeTools.generic_goods_class_info(obj.getGc()));
				mv.addObject("goods_class", obj.getGc());

				List<GoodsSpecification> color_spec_list = Lists.newArrayList();
				List<GoodsSpecification> other_spec_list = Lists.newArrayList();
				Map spec_map = Maps.newHashMap();
				spec_map.put("store_id", user.getStore().getId());
				spec_map.put("orderBy", "sequence");
				spec_map.put("orderType", "asc");
				
				List<GoodsSpecification> goods_spec_list = this.goodsSpecificationService.queryPageList(spec_map);
				
				List<GoodsSpecification> spec_list = Lists.newArrayList();
				for (GoodsSpecification gspec : goods_spec_list) {
					Iterator ite = gspec.getSpec_goodsClass_detail().iterator();
					while (ite.hasNext()) {
						GoodsClass spec_goodsclass_detail = (GoodsClass) ite
								.next();
						if ((obj.getGc().getId().equals(spec_goodsclass_detail
								.getId()))
								||

								(obj.getGc().getParent().getId()
										.equals(spec_goodsclass_detail.getId()))) {
							if (gspec.getSpec_color() == 1) {
								color_spec_list.add(gspec);
							} else {
								other_spec_list.add(gspec);
							}
						}
					}

					mv.addObject("color_spec_list", color_spec_list);
					mv.addObject("goods_spec_list", other_spec_list);
				}
				GoodsClass goods_class = null;
				if (obj.getGc().getLevel() == 2) {
					goods_class = obj.getGc().getParent().getParent();
				}
				if (obj.getGc().getLevel() == 1) {
					goods_class = obj.getGc().getParent();
				}
				params.clear();
				params.put("orderBy", "first_word");
				params.put("orderType", "asc");
				List<GoodsBrand> gbs = this.goodsBrandService.queryPageList(params);
				
				mv.addObject("gbs", gbs);
			}
			Map map = Maps.newHashMap();
			map.put("store_id", user.getStore().getId());
			map.put("orderBy", "sequence");
			map.put("orderType", "asc");
			List<GoodsTypeProperty> gtps = this.gtpService.queryPageList(map);
			
			if (gtps.size() > 0) {
				mv.addObject("gtps", gtps);
			}
			String goods_session = CommUtil.randomString(32);
			mv.addObject("goods_session", goods_session);
			request.getSession(false).setAttribute("goods_session",
					goods_session);
			mv.addObject("imageSuffix", this.storeViewTools
					.genericImageSuffix(this.configService.getSysConfig()
							.getImageSuffix()));

			String[] strs = this.configService.getSysConfig().getImageSuffix().split("\\|");
			StringBuffer sb = new StringBuffer();
			for (String str : strs) {
				sb.append("." + str + ",");
			}
			mv.addObject("imageSuffix1", sb);
			Date now = new Date();
			now.setDate(now.getDate() + 1);
			mv.addObject("default_publish_day", CommUtil.formatShortDate(now));
			mv.addObject("store", obj.getGoods_store());

			params.clear();
			params.put("gf_store_id", user.getStore().getId());
			List<GoodsFormat> gfs = this.goodsFormatService.queryPageList(params);
			
			mv.addObject("gfs", gfs);
			params.clear();
			params.put("parent", -1);
			List<Area> areas = this.areaService.queryPageList(params);
			
			mv.addObject("areas", areas);
			Area de_area = this.areaService.selectByPrimaryKey(obj.getDelivery_area_id());
			mv.addObject("de_area", de_area);
			mv.addObject("jsessionid", request.getSession().getId());
			params.clear();
			params.put("user_id", user.getId());
			params.put("orderBy", "album_sequence");
			params.put("orderType", "asc");
			
			List<Album> albums = this.albumService.queryPageList(params);
			
			mv.addObject("albums", albums);
			params.clear();
			params.put("sa_user_id", user.getId());
			List<ShipAddress> addresses = this.shipAddressService.queryPageList(params);
			
			mv.addObject("shipaddresses", addresses);
			mv.addObject("goodsTools", this.goodsTools);
			Object ms = Lists.newArrayList();
			if (obj.getMerchantService_info() != null) {
				ms = JSON.parseArray(obj.getMerchantService_info());
			}
			mv.addObject("ms", ms);
			List<Map> ms_list = Lists.newArrayList();
			Store store = user.getStore();
			if (store.getStore_service_info() != null) {
				ms_list = JSON.parseArray(store.getStore_service_info(),Map.class);
			}
			mv.addObject("ms_list", ms_list);
			if (obj.getGoods_status() == 0) {
				mv.addObject("edit", "edit");
			}
		} else {
			mv = new RedPigJModelAndView(
					"user/default/sellercenter/seller_error.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request,
					response);
			mv.addObject("op_title", "您没有该商品信息！");
			mv.addObject("url", CommUtil.getURL(request) + "/index");
		}
		mv.addObject("add_type", add_type);
		return mv;
	}
	
	/**
	 * 商品上下架
	 * @param request
	 * @param response
	 * @param mulitId
	 * @param uncheck_mulitId
	 * @param op
	 * @param brand_id
	 * @param goods_name
	 * @return
	 */
	@SecurityMapping(title = "商品上下架", value = "/goods_sale*", rtype = "seller", rname = "违规下架商品", rcode = "goods_out_seller", rgroup = "商品管理")
	@RequestMapping({ "/goods_sale" })
	public String goods_sale(HttpServletRequest request,
			HttpServletResponse response, String mulitId,
			String uncheck_mulitId, String op, String brand_id,
			String goods_name) {
		String url = "/items";
		int status_op = 0;
		if (CommUtil.null2String(op).equals("storage")) {
			url = "/goods_storage";
			status_op = 1;
		}
		if (CommUtil.null2String(op).equals("out")) {
			url = "/goods_out";
			status_op = -2;
		}
		String status = "0";
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		
		if ("all".equals(mulitId)) {
			List<Goods> list = generatorQuery(status_op, mulitId,
					uncheck_mulitId, brand_id, goods_name, user.getStore());
			for (int i = 0; i < list.size(); i++) {
				Goods goods = (Goods) list.get(i);
				if ((goods != null) && (goods.getGoods_status() != -5)) {
					if (goods.getGoods_store().getUser().getId()
							.equals(user.getId())) {
						int goods_status = goods.getGoods_status() == 0 ? 1 : 0;
						
						goods.setGoods_status(goods_status);
						
						this.goodsService.updateById(goods);
						if (goods.getGoods_status() == 0) {
							url = "/goods_storage";
							this.goodsTools.addGoodsLucene(goods);
						} else if (goods.getGoods_status() == 1) {
							this.goodsTools.deleteGoodsLucene(goods);
						}
					}
				}
			}
		} else {

			String[] ids = null;
			if (mulitId != null && mulitId.contains(",")) {
				ids = mulitId.split(",");
			} else if (mulitId != null && !mulitId.contains(",")) {
				ids = new String[] { mulitId.trim() };
			}
			if (ids != null) {
				for (String id : ids) {
						
							if (!id.equals("")) {
								Goods goods = this.goodsService.selectByPrimaryKey(Long.valueOf(Long.parseLong(id)));
								if ((goods != null) && (goods.getGoods_status() != -5)) {
									if (goods.getGoods_store().getUser().getId().equals(user.getId())) {
										int goods_status = goods.getGoods_status() == 0 ? 1 : 0;
										
										Map map = Maps.newHashMap();
										map.put("goods_status",Integer.valueOf(goods_status));
										goods.setGoods_status(goods_status);
										
										this.goodsService.updateById(goods);
										
										if (goods.getGoods_status() == 0) {
											url = "/goods_storage";
											this.goodsTools.addGoodsLucene(goods);
										} else if (goods.getGoods_status() == 1) {
											this.goodsTools.deleteGoodsLucene(goods);
										}
									}
								}
							}
					
				}
			}
		}
		return "redirect:" + url;
	}
	
	/**
	 * 商品删除
	 * @param request
	 * @param response
	 * @param mulitId
	 * @param op
	 * @param uncheck_mulitId
	 * @param brand_id
	 * @param goods_name
	 * @return
	 */
	@SecurityMapping(title = "商品删除", value = "/goods_del*", rtype = "seller", rname = "出售中的商品", rcode = "goods_list_seller", rgroup = "商品管理")
	@RequestMapping({ "/goods_del" })
	public String goods_del(HttpServletRequest request,
			HttpServletResponse response, String mulitId, String op,
			String uncheck_mulitId, String brand_id, String goods_name) {
		String url = "/items";
		int status = 0;
		if (CommUtil.null2String(op).equals("storage")) {
			url = "/goods_storage";
			status = 1;
		}
		if (CommUtil.null2String(op).equals("out")) {
			url = "/goods_out";
			status = -2;
		}
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();

		if ("all".equals(mulitId)) {
			List<Goods> list = generatorQuery(status, mulitId, uncheck_mulitId,
					brand_id, goods_name, user.getStore());
			for (int i = 0; i < list.size(); i++) {
				Goods goods = (Goods) list.get(i);
				goodsListDel(goods);
			}
		} else {
			String[] ids = mulitId.split(",");

			for (String id : ids) {
				if (!id.equals("")) {
					Goods goods = this.goodsService.selectByPrimaryKey(CommUtil
							.null2Long(id));
					if (goods.getGoods_store().getUser().getId()
							.equals(user.getId())) {
						goodsListDel(goods);
					}
				}
			}
		}
		return "redirect:" + url;
	}
	
	/**
	 * 商家商品相册列表
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param ajax_type
	 * @param album_name
	 * @return
	 */
	@SecurityMapping(title = "商家商品相册列表", value = "/goods_album*", rtype = "seller", rname = "发布新商品", rcode = "goods_seller", rgroup = "商品管理")
	@RequestMapping({ "/goods_album" })
	public ModelAndView goods_album(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String ajax_type,
			String album_name) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/sellercenter/goods_album.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		
		Map<String,Object> maps= this.redPigQueryTools.getParams(currentPage,5, "album_sequence", "asc");
		maps.put("user_id", user.getId());
		
		
		if (CommUtil.null2String(album_name) != "") {
			maps.put("album_name_like", album_name);
			mv.addObject("album_name", album_name);
		}
		
		IPageList pList = this.albumService.list(maps);
		
		String album_url = CommUtil.getURL(request) + "/goods_album";
		if (pList.getResult().size() > 0) {
			mv.addObject("albums", pList.getResult());
		}
		mv.addObject(
				"gotoPageAjaxHTML",
				CommUtil.showPageAjaxHtml(album_url, album_name,
						pList.getCurrentPage(), pList.getPages(),
						pList.getPageSize()));
		mv.addObject("ajax_type", ajax_type);
		mv.addObject("ImageTools", this.ImageTools);
		return mv;
	}
	
	/**
	 * 商家商品图片列表
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param type
	 * @param album_id
	 * @return
	 */
	@SecurityMapping(title = "商家商品图片列表", value = "/goods_img*", rtype = "seller", rname = "发布新商品", rcode = "goods_seller", rgroup = "商品管理")
	@RequestMapping({ "/goods_img" })
	public ModelAndView goods_img(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String type,
			String album_id) {
		ModelAndView mv = new RedPigJModelAndView("user/default/sellercenter/" + type
				+ ".html", this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		
		Map<String,Object> maps= this.redPigQueryTools.getParams(currentPage,20, "addTime", "desc");
		maps.put("album_id", CommUtil.null2Long(album_id));
		maps.put("user_id", user.getId());
		
		IPageList pList = this.accessoryService.list(maps);
		
		String photo_url = CommUtil.getURL(request) + "/goods_img";
		mv.addObject("photos", pList.getResult());
		mv.addObject("gotoPageAjaxHTML", CommUtil.showPageAjaxHtml(photo_url,
				"", pList.getCurrentPage(), pList.getPages(),
				pList.getPageSize()));
		mv.addObject("album_id", album_id);
		return mv;
	}
	
	/**
	 * 商品二维码生成
	 * @param request
	 * @param response
	 * @param mulitId
	 * @param currentPage
	 * @param uncheck_mulitId
	 * @param op
	 * @param brand_id
	 * @param goods_name
	 * @return
	 * @throws ClassNotFoundException
	 */
	@SecurityMapping(title = "商品二维码生成", value = "/goods_qr*", rtype = "seller", rname = "发布新商品", rcode = "goods_seller", rgroup = "商品管理")
	@RequestMapping({ "/goods_qr" })
	public String goods_qr(HttpServletRequest request,
			HttpServletResponse response, String mulitId, String currentPage,
			String uncheck_mulitId, String op, String brand_id,
			String goods_name) throws ClassNotFoundException {
		int status = 0;
		if (CommUtil.null2String(op).equals("storage")) {
			status = 1;
		}
		if (CommUtil.null2String(op).equals("out")) {
			status = -2;
		}
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();

		if ("all".equals(mulitId)) {
			List<Goods> list = generatorQuery(status, mulitId, uncheck_mulitId,
					brand_id, goods_name, user.getStore());
			for (int i = 0; i < list.size(); i++) {
				Goods goods = (Goods) list.get(i);
				String qr_img_path = this.goodsTools.createGoodsQR(request,goods);
				goods.setQr_img_path(qr_img_path);
				this.goodsService.updateById(goods);
			}
		} else {
			String[] ids = mulitId.split(",");
			for (String id : ids) {
				if (id != null) {
					Goods goods = this.goodsService.selectByPrimaryKey(CommUtil
							.null2Long(id));
					if (goods.getGoods_store().getId()
							.equals(user.getStore().getId())) {
						String qr_img_path = this.goodsTools.createGoodsQR(
								request, goods);
						goods.setQr_img_path(qr_img_path);
						this.goodsService.updateById(goods);
					}
				}
			}
		}
		return "redirect:items?currentPage=" + currentPage;
	}
	
	/**
	 * 打开关联商品版式
	 * @param request
	 * @param response
	 * @param mulitId
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "打开关联商品版式", value = "/goods_format*", rtype = "seller", rname = "发布新商品", rcode = "goods_seller", rgroup = "商品管理")
	@RequestMapping({ "/goods_format" })
	public ModelAndView goods_format(HttpServletRequest request,
			HttpServletResponse response, String mulitId, String currentPage) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/sellercenter/goods_format.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);

		User user = this.userService.selectByPrimaryKey(SecurityUserHolder
				.getCurrentUser().getId());
		Map<String,Object> params = Maps.newHashMap();
		params.put("gf_store_id", user.getStore().getId());
		
		List<GoodsFormat> gfs = this.goodsFormatService.queryPageList(params);
		
		mv.addObject("gfs", gfs);
		mv.addObject("mulitId", mulitId);
		mv.addObject("currentPage", currentPage);
		return mv;
	}
	
	/**
	 * 批量保存关联商品版式
	 * @param request
	 * @param response
	 * @param mulitId
	 * @param currentPage
	 * @param goods_top_format_id
	 * @param goods_bottom_format_id
	 * @param uncheck_mulitId
	 * @param op
	 * @param brand_id
	 * @param goods_name
	 */
	@SecurityMapping(title = "批量保存关联商品版式", value = "/goods_format_link*", rtype = "seller", rname = "发布新商品", rcode = "goods_seller", rgroup = "商品管理")
	@RequestMapping({ "/goods_format_link" })
	public void goods_format_link(HttpServletRequest request,
			HttpServletResponse response, String mulitId, String currentPage,
			String goods_top_format_id, String goods_bottom_format_id,
			String uncheck_mulitId, String op, String brand_id,
			String goods_name) {
		String url = "/items";
		int status_op = 0;
		if (CommUtil.null2String(op).equals("storage")) {
			url = "/goods_storage";
			status_op = 1;
		}
		if (CommUtil.null2String(op).equals("out")) {
			url = "/goods_out";
			status_op = -2;
		}
		String status = "0";
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		if (mulitId.contains("all")) {
			List<Goods> list = generatorQuery(status_op, mulitId,uncheck_mulitId, brand_id, goods_name, user.getStore());
			for (int i = 0; i < list.size(); i++) {
				Goods goods = (Goods) list.get(i);
				if (goods.getGoods_status() != -5) {
					if (goods.getGoods_store().getUser().getId()
							.equals(user.getId())) {
						linkFormat(goods, goods_top_format_id,goods_bottom_format_id);
					}
				}
			}
		} else {
			String[] ids = mulitId.split(",");
			for (String id : ids) {
				if ((!id.equals("")) && (!"on".equals(id))) {
					Goods goods = this.goodsService.selectByPrimaryKey(Long.valueOf(Long.parseLong(id)));
					if (goods.getGoods_status() != -5) {
						if (goods.getGoods_store().getUser().getId()
								.equals(user.getId())) {
							linkFormat(goods, goods_top_format_id,goods_bottom_format_id);
						}
					}
				}
			}
		}
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		try {
			PrintWriter writer = response.getWriter();
			writer.print(true);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 商品F码Excel下载
	 * @param request
	 * @param response
	 * @param id
	 * @throws IOException
	 */
	@SecurityMapping(title = "商品F码Excel下载", value = "/goods_self_f_code_download*", rtype = "seller", rname = "发布新商品", rcode = "goods_seller", rgroup = "商品管理")
	@RequestMapping({ "/goods_self_f_code_download" })
	public void goods_self_f_code_download(HttpServletRequest request,
			HttpServletResponse response, String id) throws IOException {
		Goods obj = this.goodsService.selectByPrimaryKey(CommUtil.null2Long(id));
		String excel_url = "";
		Store store = this.userService.selectByPrimaryKey(
				SecurityUserHolder.getCurrentUser().getId()).getStore();
		if ((obj.getF_sale_type() == 1) && (obj.getGoods_type() == 1)
				&& (obj.getGoods_store().getId().equals(store.getId()))
				&& (!CommUtil.null2String(obj.getGoods_f_code()).equals(""))) {
			List<Map> list = JSON.parseArray(obj.getGoods_f_code(), Map.class);
			String name = CommUtil.null2String(UUID.randomUUID());
			String path = request.getSession().getServletContext()
					.getRealPath("/")
					+ File.separator + "excel";
			CommUtil.createFolder(path);
			path = path + File.separator + name + ".xls";
			exportList2Excel("F码列表", new String[] { "F码信息", "F码状态" }, list,
					response, name);
		}
	}
	
	/**
	 * 商品补货
	 * @param request
	 * @param response
	 * @param id
	 * @return
	 * @throws IOException
	 */
	@SecurityMapping(title = "商品补货", value = "/goods_supplement*", rtype = "seller", rname = "出售中的商品", rcode = "goods_seller", rgroup = "商品管理")
	@RequestMapping({ "/goods_supplement" })
	public ModelAndView goods_supplement(HttpServletRequest request,
			HttpServletResponse response, String id) throws IOException {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/sellercenter/goods_supplement.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		Goods obj = this.goodsService.selectByPrimaryKey(CommUtil.null2Long(id));
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		String gsp_ids = "";
		for (GoodsSpecProperty gsp : obj.getGoods_specs()) {
			gsp_ids = gsp_ids + "," + gsp.getId();
		}
		if ((obj != null)
				&& (obj.getGoods_store().getId()
						.equals(user.getStore().getId()))) {
			Map<String,Object> spec_map = Maps.newHashMap();
			spec_map.put("store_id", user.getStore().getId());
			spec_map.put("orderBy", "sequence");
			spec_map.put("orderType", "asc");
			
			List<GoodsSpecification> goods_spec_list = this.goodsSpecificationService.queryPageList(spec_map);
			
			List<GoodsSpecification> spec_list = Lists.newArrayList();
			for (GoodsSpecification gspec : goods_spec_list) {
				for (GoodsClass spec_goodsclass_detail : gspec
						.getSpec_goodsClass_detail()) {
					if ((obj.getGc().getId().equals(spec_goodsclass_detail
							.getId()))
							||

							(obj.getGc().getParent().getId()
									.equals(spec_goodsclass_detail.getId()))) {
						spec_list.add(gspec);
					}
				}
				mv.addObject("goods_spec_list", spec_list);
			}
			mv.addObject("gsp_ids", gsp_ids);
			mv.addObject("obj", obj);
		}
		return mv;
	}
	
	/**
	 * 商品补货保存
	 * @param request
	 * @param response
	 * @param id
	 * @param inventory
	 * @param intentory_details
	 * @throws IOException
	 */
	@SecurityMapping(title = "商品补货保存", value = "/goods_supplement_save*", rtype = "seller", rname = "出售中的商品", rcode = "goods_seller", rgroup = "商品管理")
	@RequestMapping({ "/goods_supplement_save" })
	public void goods_supplement_saveEntity(HttpServletRequest request,
			HttpServletResponse response, String id, String inventory,
			String intentory_details) throws IOException {
		int code = -100;
		Goods obj = this.goodsService.selectByPrimaryKey(CommUtil.null2Long(id));
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		Map json_map = Maps.newHashMap();
		boolean warn_suppment = true;
		if ((obj != null)
				&& (obj.getGoods_store().getId()
						.equals(user.getStore().getId()))) {
			if (obj.getInventory_type().equals("all")) {
				obj.setGoods_inventory(CommUtil.null2Int(inventory));
				if (CommUtil.null2Int(inventory)
						- obj.getGoods_warn_inventory() > 0) {
					obj.setWarn_inventory_status(0);
				}
				code = 100;
			}
			if (obj.getInventory_type().equals("spec")) {
				List<Map> maps = JSON.parseArray(
						obj.getGoods_inventory_detail(), Map.class);

				for (Map map : maps) {
					String[] inventory_list = intentory_details.split(";");
					for (String temp_inventory : inventory_list) {
						if (!temp_inventory.equals("")) {
							String[] list = temp_inventory.split(",");
							if (list[0].equals(CommUtil.null2String(map
									.get("id")))) {
								map.put("count", list[1]);
								if (CommUtil.null2Int(map.get("count")) <= CommUtil
										.null2Int(map.get("supp"))) {
									warn_suppment = false;
								}
							}

						}
					}
				}

				if (warn_suppment) {
					obj.setWarn_inventory_status(0);
				}
				obj.setGoods_inventory_detail(JSON.toJSONString(maps));
				code = 100;
			}
			this.goodsService.updateById(obj);
		}
		json_map.put("code", Integer.valueOf(code));
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		try {
			PrintWriter writer = response.getWriter();
			writer.print(JSON.toJSONString(json_map));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 商品快速补库存
	 * @param request
	 * @param response
	 * @param mulitId
	 * @param uncheck_mulitId
	 * @param op
	 * @param brand_id
	 * @param goods_name
	 * @param count
	 */
	@SecurityMapping(title = "商品快速补库存", value = "/goods_quick_inventory*", rtype = "seller", rname = "出售中的商品", rcode = "goods_seller", rgroup = "商品管理")
	@RequestMapping({ "/goods_quick_inventory" })
	public void goods_quick_inventory(HttpServletRequest request,
			HttpServletResponse response, String mulitId,
			String uncheck_mulitId, String op, String brand_id,
			String goods_name, String count) {
		String url = "/items";
		int status_op = 0;
		if (CommUtil.null2String(op).equals("storage")) {
			url = "/goods_storage";
			status_op = 1;
		}
		if (CommUtil.null2String(op).equals("out")) {
			url = "/goods_out";
			status_op = -2;
		}
		String status = "0";
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		if ("all".equals(mulitId)) {
			List<Goods> list = generatorQuery(status_op, mulitId,
					uncheck_mulitId, brand_id, goods_name, user.getStore());
			for (int i = 0; i < list.size(); i++) {
				Goods goods = (Goods) list.get(i);
				goods.setGoods_inventory(goods.getGoods_inventory()
						+ CommUtil.null2Int(count));
				if (goods.getGoods_inventory() > goods
						.getGoods_warn_inventory()) {
					goods.setWarn_inventory_status(0);
				}
				this.goodsService.updateById(goods);
			}
		} else {
			String[] ids = mulitId.split(",");
			for (String id : ids) {
				if (!id.equals("")) {
					Goods goods = this.goodsService.selectByPrimaryKey(Long
							.valueOf(Long.parseLong(id)));
					if (goods.getGoods_store().getId().toString()
							.equals(user.getStore().getId().toString())) {
						goods.setGoods_inventory(goods.getGoods_inventory()
								+ CommUtil.null2Int(count));
						if (goods.getGoods_inventory() > goods
								.getGoods_warn_inventory()) {
							goods.setWarn_inventory_status(0);
						}
						this.goodsService.updateById(goods);
					}
				}
			}
		}
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		try {
			PrintWriter writer = response.getWriter();
			writer.print("success");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 商品AJAX更新
	 * @param request
	 * @param response
	 * @param id
	 * @param fieldName
	 * @param value
	 * @throws ClassNotFoundException
	 */
	@SecurityMapping(title = "商品AJAX更新", value = "/goods_ajax*", rtype = "seller", rname = "出售中的商品", rcode = "goods_seller", rgroup = "商品管理")
	@RequestMapping({ "/goods_ajax" })
	public void goods_ajax(HttpServletRequest request,
			HttpServletResponse response, String id, String fieldName,
			String value) throws ClassNotFoundException {
		Goods obj = this.goodsService.selectByPrimaryKey(Long.valueOf(Long
				.parseLong(id)));
		if (obj.getGoods_store().getUser().getId()
				.equals(SecurityUserHolder.getCurrentUser().getId())) {
			Field[] fields = Goods.class.getDeclaredFields();
			
			Object val = ObjectUtils.setValue(fieldName, value, obj, fields);
			
			if (fieldName.equals("store_recommend")) {
				if (obj.getStore_recommend()) {
					obj.setStore_recommend_time(new Date());
				} else {
					obj.setStore_recommend_time(null);
				}
			}
			this.goodsService.updateById(obj);
			if (obj.getGoods_status() == 0) {
				this.goodsTools.updateGoodsLucene(obj);
			} else {
				this.goodsTools.deleteGoodsLucene(obj);
			}
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
	}
	
	/**
	 * 商品详情中的图片上传
	 * @param request
	 * @param response
	 * @param album_id
	 * @param ajaxUploadMark
	 */
	@SecurityMapping(title = "商品详情中的图片上传", value = "/goods_detail_image_upload*", rtype = "seller", rname = "发布新商品", rcode = "goods_seller", rgroup = "商品管理")
	@RequestMapping({ "/goods_detail_image_upload" })
	public void album_image_upload(HttpServletRequest request,
			HttpServletResponse response, String album_id, String ajaxUploadMark) {
		Boolean html5Uploadret = Boolean.valueOf(false);
		Map ajaxUploadInfo = Maps.newHashMap();
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder
				.getCurrentUser().getId());

		user = user.getParent() == null ? user : user.getParent();
		String path = this.storeTools
				.createUserFolder(request, user.getStore());
		String url = this.storeTools.createUserFolderURL(user.getStore());
		double csize = CommUtil.fileSize(new File(path));
		double img_remain_size = 0.0D;
		if (user.getStore().getGrade().getSpaceSize() > 0.0F) {
			img_remain_size = CommUtil.div(
					Double.valueOf(user.getStore().getGrade().getSpaceSize()
							* 1024.0F - csize), Integer.valueOf(1024));
		}
		if (img_remain_size >= 0.0D) {
			try {
				Map map = CommUtil.saveFileToServer(request, "fileImage", path,
						null, null);
				Map<String,Object> params = Maps.newHashMap();
				params.put("store_id", user.getStore().getId());
				
				List<WaterMark> wms = this.waterMarkService.queryPageList(params);
				
				if (wms.size() > 0) {
					WaterMark mark = (WaterMark) wms.get(0);
					if (mark.getWm_image_open()) {
						String pressImg = request.getSession()
								.getServletContext().getRealPath("")
								+ File.separator
								+ mark.getWm_image().getPath()
								+ File.separator + mark.getWm_image().getName();
						String targetImg = path + File.separator
								+ map.get("fileName");
						int pos = mark.getWm_image_pos();
						float alpha = mark.getWm_image_alpha();
						// 生成水印图片
						CommUtil.waterMarkWithImage(pressImg, targetImg, pos,
								alpha);
					}
					
					if (mark.getWm_text_open()) {
						String targetImg = path + File.separator
								+ map.get("fileName");
						int pos = mark.getWm_text_pos();
						String text = mark.getWm_text();
						String markContentColor = mark.getWm_text_color();
						CommUtil.waterMarkWithText(
								targetImg,
								targetImg,
								text,
								markContentColor,
								new Font(mark.getWm_text_font(), 1, mark
										.getWm_text_font_size()), pos, 100.0F);
					}
				}
				Accessory image = new Accessory();
				image.setAddTime(new Date());
				image.setExt((String) map.get("mime"));
				image.setPath(url);
				image.setWidth(CommUtil.null2Int(map.get("width")));
				image.setHeight(CommUtil.null2Int(map.get("height")));
				image.setName(CommUtil.null2String(map.get("fileName")));
				image.setUser(user);
				Album album = null;
				if ((album_id != null) && (!album_id.equals(""))) {
					album = this.albumService.selectByPrimaryKey(CommUtil
							.null2Long(album_id));
				} else {
					album = this.albumService.getDefaultAlbum(user.getId());
					if (album == null) {
						album = new Album();
						album.setAddTime(new Date());
						album.setAlbum_name("默认相册");
						album.setAlbum_sequence(55536);
						album.setAlbum_default(true);
						this.albumService.saveEntity(album);
					}
				}
				image.setAlbum(album);
				this.accessoryService.saveEntity(image);
				html5Uploadret = true;
				ajaxUploadInfo.put("url",image.getPath() + "/" + image.getName());

				String ext = image.getExt().indexOf(".") < 0 ? "." + image.getExt() : image.getExt();
				String source = request.getSession().getServletContext()
						.getRealPath("/")
						+ image.getPath() + File.separator + image.getName();
				String target = source + "_small" + ext;
				CommUtil.createSmall(source, target, this.configService
						.getSysConfig().getSmallWidth(), this.configService
						.getSysConfig().getSmallHeight());

				String midext = image.getExt().indexOf(".") < 0 ? "."
						+ image.getExt() : image.getExt();
				String midtarget = source + "_middle" + ext;
				CommUtil.createSmall(source, midtarget, this.configService
						.getSysConfig().getMiddleWidth(), this.configService
						.getSysConfig().getMiddleHeight());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		try {
			PrintWriter writer = response.getWriter();
			writer.print(JSON.toJSONString(ajaxUploadInfo));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 发货地址的详细信息
	 * @param request
	 * @param response
	 * @param area_id
	 */
	@SecurityMapping(title = "发货地址的详细信息", value = "/address_detail*", rtype = "seller", rname = "发布新商品", rcode = "goods_seller", rgroup = "商品管理")
	@RequestMapping({ "/address_detail" })
	public void address_detail(HttpServletRequest request,
			HttpServletResponse response, String area_id) {
		Map json_map = Maps.newHashMap();
		Area area = this.areaService.selectByPrimaryKey(CommUtil.null2Long(area_id));
		if (area.getLevel() == 2) {
			json_map.put("level1_id", area.getParent().getParent().getId());
			List level1_list = Lists.newArrayList();
			Map map;
			for (Area a : area.getParent().getParent().getChilds()) {
				map = Maps.newHashMap();
				map.put("id", a.getId());
				map.put("areaName", a.getAreaName());
				level1_list.add(map);
			}
			json_map.put("level1_list", level1_list);

			json_map.put("level2_id", area.getParent().getId());
			List level2_list = Lists.newArrayList();
			for (Area a : area.getParent().getChilds()) {
				map = Maps.newHashMap();
				map.put("id", a.getId());
				map.put("areaName", a.getAreaName());
				level2_list.add(map);
			}
			json_map.put("level2_list", level2_list);
		}
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		try {
			PrintWriter writer = response.getWriter();
			writer.print(JSON.toJSONString(json_map));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 属性Ajax保存
	 * @param request
	 * @param response
	 * @param count
	 * @param params
	 * @param goodsType
	 * @param goodsclass
	 * @return
	 */
	@SecurityMapping(title = "属性Ajax保存", value = "/ajax_save_gtp*", rtype = "seller", rname = "属性管理", rcode = "gtp_seller", rgroup = "商品")
	@RequestMapping({ "/ajax_save_gtp" })
	public ModelAndView ajax_save_gtp(HttpServletRequest request,
			HttpServletResponse response, String count, String params,
			String goodsType, String goodsclass) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/sellercenter/goods_type_load.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		String[] gtps = params.substring(1).split("&");
		List gtpList = Lists.newArrayList();
		for (int i = 0; i < CommUtil.null2Int(count); i++) {
			if (gtps.length > 0) {
				String[] gtp_info = gtps[i].split("/");
				GoodsTypeProperty goodsTypeProperty = new GoodsTypeProperty();
				goodsTypeProperty.setName(gtp_info[0]);
				goodsTypeProperty.setValue(gtp_info[1]);
				goodsTypeProperty.setSequence(CommUtil.null2Int(gtp_info[2]));
				goodsTypeProperty.setStore_id(user.getStore().getId());
				goodsTypeProperty.setAddTime(new Date());
				goodsTypeProperty.setDisplay(true);
				GoodsType goodstype = this.goodsTypeService.selectByPrimaryKey(CommUtil
						.null2Long(goodsType));
				if (goodstype == null) {
					GoodsClass goodsClass = this.goodsClassService
							.selectByPrimaryKey(CommUtil.null2Long(goodsclass));
					goodstype = new GoodsType();
					goodstype.setAddTime(new Date());
					this.goodsTypeService.saveEntity(goodstype);
					goodsClass.setGoodsType(goodstype);
					this.goodsClassService.updateById(goodsClass);
				}
				goodsTypeProperty.setGoodsType(goodstype);
				this.gtpService.saveEntity(goodsTypeProperty);
				gtpList.add(goodsTypeProperty);
			}
		}

		Comparator comparator = new Comparator<GoodsTypeProperty>() {
			public int compare(GoodsTypeProperty g1, GoodsTypeProperty g2) {
				if (g1.getSequence() != g2.getSequence()) {
					return g1.getSequence() - g2.getSequence();
				}
				return g1.getSequence();
			}
		};
		Collections.sort(gtpList, comparator);
		mv.addObject("gtpList", gtpList);
		return mv;
	}
	
	/**
	 * 商品类型属性AJAX删除
	 * @param request
	 * @param response
	 * @param id
	 */
	@SecurityMapping(title = "商品类型属性AJAX删除", value = "/gtp_delete*", rtype = "seller", rname = "属性管理", rcode = "gtp_seller", rgroup = "商品管理")
	@RequestMapping({ "/gtp_delete" })
	public void goods_type_property_deleteById(HttpServletRequest request,
			HttpServletResponse response, String id) {
		boolean ret = false;
		if (!id.equals("")) {
			GoodsTypeProperty property = this.gtpService.selectByPrimaryKey(Long
					.valueOf(Long.parseLong(id)));
			property.setGoodsType(null);
			this.gtpService.deleteById(property.getId());
			ret = true;
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

	private void linkFormat(Goods goods, String goods_top_format_id,
			String goods_bottom_format_id) {
		goods.setGoods_top_format_id(CommUtil.null2Long(goods_top_format_id));
		GoodsFormat gf = this.goodsFormatService.selectByPrimaryKey(CommUtil
				.null2Long(goods_top_format_id));
		if (gf != null) {
			goods.setGoods_top_format_content(gf.getGf_content());
		} else {
			goods.setGoods_top_format_content(null);
		}
		goods.setGoods_bottom_format_id(CommUtil.null2Long(goods_bottom_format_id));
		gf = this.goodsFormatService.selectByPrimaryKey(CommUtil.null2Long(goods_bottom_format_id));
		if (gf != null) {
			goods.setGoods_bottom_format_content(gf.getGf_content());
		} else {
			goods.setGoods_bottom_format_content(null);
		}
		this.goodsService.updateById(goods);
	}

	private List<Goods> generatorQuery(int status, String mulitId,
			String uncheck_mulitId, String brand_id, String goods_name,
			Store store) {
		Map map = Maps.newHashMap();
		map.put("store_id", store.getId());
		map.put("goods_store_id", store.getId());
		map.put("goods_status", status);
		
		if ((uncheck_mulitId != null) && (!"".equals(uncheck_mulitId))) {
			uncheck_mulitId = uncheck_mulitId.substring(0,uncheck_mulitId.length() - 1);
			String[] ids = uncheck_mulitId.split(",");
			List<Long> goodsIds = Lists.newArrayList();
			for (String id : ids) {
				goodsIds.add(Long.parseLong(id.trim()));
			}
			map.put("id_no_in", goodsIds);
		}
		
		if ((brand_id != null) && (!brand_id.equals(""))) {
			map.put("goods_brand_id", brand_id);
		}
		
		if ((goods_name != null) && (!goods_name.equals(""))) {
			map.put("goods_name_like", goods_name);
		}

		List<Goods> list = this.goodsService.queryPageList(map);
		
		return list;
	}

	private void goodsListDel(Goods goods) {
		Map<String,Object> params = Maps.newHashMap();
		params.put("goods_id", goods.getId());
		List<ComplaintGoods> complaintGoodses = this.complaintGoodsService.queryPageList(params);
		
		for (ComplaintGoods cg : complaintGoodses) {
			this.complaintGoodsService.deleteById(cg.getId());
		}
		params.clear();
		
		params.put("gg_goods_id", goods.getId());
		List<GroupGoods> groupGoodses = this.groupGoodsService.queryPageList(params);
		
		for (GroupGoods gg : groupGoodses) {
			this.groupGoodsService.deleteById(gg.getId());
		}
		
		params.clear();
		params.put("zgl_goods_id", goods.getId());
		List<ZTCGoldLog> ztcGoldLogs = this.iztcGoldLogService.queryPageList(params);
		
		if (((List) ztcGoldLogs).size() > 0) {
			for (ZTCGoldLog ztcGoldLog : ztcGoldLogs) {
				this.iztcGoldLogService.deleteById(ztcGoldLog.getId());
			}
		}
		for (GoodsCart cart : goods.getCarts()) {
			this.cartService.deleteById(cart.getId());
		}
		goods.getCarts().clear();
		goods.getGoods_ugcs().clear();
		goods.getAg_goods_list().clear();
		goods.getGroup_goods_list().clear();
		goods.setDeleteStatus(1);
		this.goodsService.delete(goods.getId());

		this.goodsTools.deleteGoodsLucene(goods);
	}

	private String goodsListSale(Goods goods, String url) {
		int goods_status = goods.getGoods_status() == 0 ? 1 : 0;
		goods.setGoods_status(goods_status);
		this.goodsService.updateById(goods);
		if (goods_status == 0) {
			url = "/goods_storage";

			this.goodsTools.addGoodsLucene(goods);
		} else {
			this.goodsTools.deleteGoodsLucene(goods);
		}
		return url;
	}

	private static boolean exportList2Excel(String title, String[] headers,
			List<Map> list, HttpServletResponse response, String name) {
		boolean ret = true;

		HSSFWorkbook workbook = new HSSFWorkbook();

		HSSFSheet sheet = workbook.createSheet(title);

		sheet.setDefaultColumnWidth(20);

		HSSFCellStyle style = workbook.createCellStyle();

		style.setFillForegroundColor((short) 40);
		style.setFillPattern((short) 1);

		style.setBorderBottom((short) 1);
		style.setBorderLeft((short) 1);
		style.setBorderRight((short) 1);
		style.setBorderTop((short) 1);

		style.setAlignment((short) 2);

		HSSFFont font = workbook.createFont();
		font.setColor((short) 20);
		font.setFontHeightInPoints((short) 14);
		font.setBoldweight((short) 700);

		style.setFont(font);

		HSSFCellStyle style_ = workbook.createCellStyle();
		style_.setFillForegroundColor((short) 43);
		style_.setFillPattern((short) 1);
		style_.setBorderBottom((short) 1);
		style_.setBorderLeft((short) 1);
		style_.setBorderRight((short) 1);
		style_.setBorderTop((short) 1);
		style_.setAlignment((short) 2);
		style_.setVerticalAlignment((short) 1);

		HSSFFont font_ = workbook.createFont();

		font_.setFontHeightInPoints((short) 14);

		style_.setFont(font_);

		HSSFRow row = sheet.createRow(0);
		for (int i = 0; i < headers.length; i++) {
			HSSFCell cell = row.createCell(i);
			cell.setCellStyle(style);
			cell.setCellValue(new HSSFRichTextString(headers[i]));
		}
		int index = 0;
		for (Map map : list) {
			index++;
			row = sheet.createRow(index);
			String value = CommUtil.null2String(map.get("code"));
			HSSFCell cell = row.createCell(0);
			cell.setCellStyle(style_);
			cell.setCellValue(value);
			value = CommUtil.null2Int(map.get("status")) == 0 ? "未使用" : "已使用";
			cell = row.createCell(1);
			cell.setCellStyle(style_);
			cell.setCellValue(value);
		}
		try {
			response.setContentType("application/x-download");
			response.addHeader("Content-Disposition", "attachment;filename="
					+ name + ".xls");
			OutputStream os = response.getOutputStream();
			workbook.write(os);
			os.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return ret;
	}
}
