package com.redpigmall.manage.admin.action.self.goods;

import java.awt.Font;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.redpigmall.api.annotation.SecurityMapping;
import com.redpigmall.api.mv.RedPigJModelAndView;
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.api.sec.SecurityUserHolder;
import com.redpigmall.api.tools.CommUtil;
import com.redpigmall.api.tools.ObjectUtils;
import com.redpigmall.api.tools.RedPigCommonUtil;
import com.redpigmall.api.tools.RedPigMaps;
import com.redpigmall.manage.admin.action.base.BaseAction;
import com.redpigmall.domain.Accessory;
import com.redpigmall.domain.Album;
import com.redpigmall.domain.Area;
import com.redpigmall.domain.Evaluate;
import com.redpigmall.domain.Goods;
import com.redpigmall.domain.GoodsBrand;
import com.redpigmall.domain.GoodsCart;
import com.redpigmall.domain.GoodsClass;
import com.redpigmall.domain.GoodsFormat;
import com.redpigmall.domain.GoodsSpecification;
import com.redpigmall.domain.GoodsTag;
import com.redpigmall.domain.MerchantServices;
import com.redpigmall.domain.ShipAddress;
import com.redpigmall.domain.StoreHouse;
import com.redpigmall.domain.User;
import com.redpigmall.domain.WaterMark;

/**
 * 
 * <p>
 * Title: RedPigGoodsSelfManageAction.java
 * </p>
 * 
 * <p>
 * Description:自营商品管理控制器，平台可发布商品并进行管理
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
 * @date 2014年4月25日
 * 
 * @version redpigmall_b2b2c 8.0
 */
@SuppressWarnings({ "unchecked", "rawtypes", "unused" })
@Controller
public class RedPigSelfGoodsManage3Action extends BaseAction{
	
	/**
	 * 商品图片上传
	 * @param request
	 * @param response
	 * @param album_id
	 * @param session_u_id
	 * @param fileName
	 */
	@SecurityMapping(title = "商品图片上传", value = "/swf_upload*", rtype = "admin", rname = "商品发布", rcode = "goods_self_add", rgroup = "自营")
	@RequestMapping({ "/swf_upload" })
	public void swf_upload(HttpServletRequest request,
			HttpServletResponse response, String album_id, String session_u_id,
			String fileName) {
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
		String path = this.storeTools.createAdminFolder(request);
		String url = this.storeTools.createAdminFolderURL();

		Map json_map = Maps.newHashMap();
		try {
			if ((fileName == null) || ("".equals(fileName))) {
				fileName = "imgFile";
			}
			Map map = CommUtil.saveFileToServer(request, fileName, path, null,
					null);
			if (!CommUtil.null2String(map.get("fileName")).equals("")) {
				Map params = Maps.newHashMap();
				params.put("user_id", user.getId());
				
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
						album.setAlbum_name("默认相册【" + user.getUserName() + "】");
						album.setAlbum_sequence(55536);
						album.setAlbum_default(true);
						album.setUser(user);
						this.albumService.saveEntity(album);
					}
				}
				image.setAlbum(album);
				this.accessoryService.saveEntity(image);
				json_map.put("url", CommUtil.getURL(request) + "/" + url + "/"
						+ image.getName());
				json_map.put("id", image.getId());

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
			}
		} catch (IOException e) {
			e.printStackTrace();
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

	private String generic_goods_class_info(GoodsClass gc) {
		String goods_class_info = gc.getClassName() + ">";
		if (gc.getParent() != null) {
			String class_info = generic_goods_class_info(gc.getParent());
			goods_class_info = class_info + goods_class_info;
		}
		return goods_class_info;
	}
	
	/**
	 * 商品图片删除
	 * @param request
	 * @param response
	 * @param image_id
	 */
	@SecurityMapping(title = "商品图片删除", value = "/goods_image_del*", rtype = "admin", rname = "商品发布", rcode = "goods_self_add", rgroup = "自营")
	@RequestMapping({ "/goods_image_del" })
	public void goods_image_del(HttpServletRequest request,
			HttpServletResponse response, String image_id) {
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		try {
			Map map = Maps.newHashMap();
			Accessory img = this.accessoryService.selectByPrimaryKey(CommUtil
					.null2Long(image_id));
			boolean ret = false;
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
				if (ret) {
					CommUtil.deleteFile(request.getSession()
							.getServletContext().getRealPath("/")
							+ img.getPath()
							+ File.separator
							+ img.getName()
							+ "_middle." + img.getExt());
					RedPigCommonUtil.del_acc(request, img);
				}
			}
			map.put("result", Boolean.valueOf(ret));
			PrintWriter writer = response.getWriter();
			writer.print(JSON.toJSONString(map));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 自营商品列表
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @param goods_status
	 * @param brand_id
	 * @param goods_name
	 * @param u_admin_id
	 * @param goods_serial
	 * @param goods_activity_status
	 * @return
	 */
	@SecurityMapping(title = "自营商品列表", value = "/goods_self_list*", rtype = "admin", rname = "商品管理", rcode = "goods_self", rgroup = "自营")
	@RequestMapping({ "/goods_self_list" })
	public ModelAndView goods_self_list(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,
			String orderType, String goods_status, String brand_id,
			String goods_name, String u_admin_id, String goods_serial,
			String goods_activity_status) {
		ModelAndView mv = new RedPigJModelAndView("admin/blue/goods_self_list.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		
		Map<String,Object> maps= this.redPigQueryTools.getParams(currentPage, orderBy, orderType);
        
		List activity = Lists.newArrayList();
		if ((goods_activity_status != null)
				&& (!goods_activity_status.equals(""))) {
			activity.add(goods_activity_status);
			this.queryTools.queryActivityGoods(maps, activity);
			mv.addObject("goods_activity_status", goods_activity_status);
		}
		if (goods_status == null) {
			goods_status = "0";
		}
		if (goods_status.equals("0")) {
			maps.put("goods_status", 0);
		}
		if (goods_status.equals("1")) {
			maps.put("goods_status", 1);
			mv = new RedPigJModelAndView("admin/blue/goods_self_storage.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request,
					response);
			List activity1 = Lists.newArrayList();
			if ((goods_activity_status != null)
					&& (!goods_activity_status.equals(""))) {
				activity1.add(goods_activity_status);
				this.queryTools.queryActivityGoods(maps, activity1);
				mv.addObject("goods_activity_status", goods_activity_status);
			}
		}
		if (goods_status.equals("-2")) {
			
			maps.put("goods_status",-2);
			mv = new RedPigJModelAndView("admin/blue/goods_self_outline.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request,
					response);
			List activity1 = Lists.newArrayList();
			if ((goods_activity_status != null)
					&& (!goods_activity_status.equals(""))) {
				activity1.add(goods_activity_status);
				this.queryTools.queryActivityGoods(maps, activity1);
				mv.addObject("goods_activity_status", goods_activity_status);
			}
		}
		if ((goods_name != null) && (!goods_name.equals(""))) {
			
			maps.put("goods_name_like",goods_name);
			mv.addObject("goods_name", goods_name);
		}
		if ((goods_serial != null) && (!goods_serial.equals(""))) {
			
			maps.put("goods_serial",goods_serial);
			mv.addObject("goods_serial", goods_serial);
		}
		if ((brand_id != null) && (!brand_id.equals(""))) {
			
			maps.put("goods_brand_id",CommUtil.null2Long(brand_id));
			mv.addObject("brand_id", brand_id);
		}
		if ((u_admin_id != null) && (!u_admin_id.equals(""))) {
			
			maps.put("user_admin_id",CommUtil.null2Long(u_admin_id));
			mv.addObject("u_admin_id", u_admin_id);
		}
		mv.addObject("goods_status", goods_status);
		
		maps.put("goods_type",0);
//		IPageList pList = this.goodsService.list(maps);
		IPageList pList = this.goodsService.queryPagesWithNoRelations(maps);
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		maps.clear();
	    maps.put("orderBy", "first_word");
	    maps.put("orderType", "asc");
	    
		List<GoodsBrand> gbs = this.goodsBrandService.queryPageList(maps);
		
		Map<String,Object> admin_map = Maps.newHashMap();
		admin_map.put("userRole", "ADMIN");
		
		List<User> user_admins = this.userService.queryPageList(admin_map);
		mv.addObject("user_admins", user_admins);
		mv.addObject("gbs", gbs);
		mv.addObject("goodsViewTools", this.goodsViewTools);
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
	@SecurityMapping(title = "商品编辑", value = "/goods_self_edit*", rtype = "admin", rname = "商品管理", rcode = "goods_self", rgroup = "自营")
	@RequestMapping({ "/goods_self_edit" })
	public ModelAndView goods_edit(HttpServletRequest request,
			HttpServletResponse response, String id, String add_type) {
		ModelAndView mv = new RedPigJModelAndView("admin/blue/add_goods_second.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		if (add_type == null) {
			request.getSession(false).removeAttribute("goods_class_info");
		}
		Goods obj = this.goodsService.selectByPrimaryKey(Long.valueOf(Long.parseLong(id)));
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder.getCurrentUser().getId());
		
		Map<String,Object> maps= this.redPigQueryTools.getParams(null,8, null, null);
		maps.put("user_userRole", "admin");
		
		IPageList pList = this.accessoryService.list(maps);
		String photo_url = CommUtil.getURL(request) + "/goods_img_album";
		mv.addObject("photos", pList.getResult());
		mv.addObject("gotoPageAjaxHTML", CommUtil.showPageAjaxHtml(photo_url,
				"", pList.getCurrentPage(), pList.getPages(),
				pList.getPageSize()));
		mv.addObject("edit", Boolean.valueOf(true));
		mv.addObject("img_remain_size", Integer.valueOf(100000));
		mv.addObject("obj", obj);
		if (obj.getGoods_status() == 0) {
			mv.addObject("ad_goods_edit", Boolean.valueOf(true));
		}
		if ((obj.getGoods_tags() != null) && (!obj.getGoods_tags().equals(""))) {
			Map map = JSON.parseObject(obj.getGoods_tags());
			List<Long> set = (List) map.get("custom_tag");
			List list = Lists.newArrayList();
			for (Object object : set) {
				list.add(CommUtil.null2Long(object));
			}
			if (list.size() > 0) {
				Map params = Maps.newHashMap();
				params.put("ids", list);
				
				List<GoodsTag> tags = this.goodsTagService.queryPageList(params);
				
				mv.addObject("goodstags", tags);
			}
		}
		Map<String, Object> spec_map = new HashMap<String, Object>();
		
		Map<String, Object> params = new HashMap<String, Object>();
		List<GoodsFormat> gfs;
		if (request.getSession(false).getAttribute("goods_class_info") != null) {
			GoodsClass session_gc = (GoodsClass) request.getSession(false).getAttribute("goods_class_info");
			GoodsClass gc = this.goodsClassService.selectByPrimaryKey(session_gc.getId());
			mv.addObject("goods_class_info",
					this.storeTools.generic_goods_class_info(gc));
			mv.addObject("goods_class", gc);
			HashMap params1 = Maps.newHashMap();
			GoodsClass goods_class = null;
			if (gc.getLevel() == 2) {
				goods_class = gc.getParent().getParent();
			}
			if (gc.getLevel() == 1) {
				goods_class = gc.getParent();
			}
			params1.clear();
			params1.put("audit", Integer.valueOf(1));
			params1.put("orderBy", "first_word");
			params1.put("orderType", "asc");
			
			List<GoodsBrand> gbs = this.goodsBrandService.queryPageList(params1);
			
			mv.addObject("gbs", gbs);
			
			List<GoodsSpecification> color_spec_list = Lists.newArrayList();
			List<GoodsSpecification> other_spec_list = Lists.newArrayList();
			
			if (gc.getLevel() == 2) {
				Map spec_map1 = Maps.newHashMap();
				spec_map1.put("spec_type", Integer.valueOf(0));
				spec_map1.put("orderBy", "sequence");
				spec_map1.put("orderType", "asc");
		        
				List<GoodsSpecification> goods_spec_list = this.goodsSpecificationService.queryPageList(spec_map1);
				
				for (GoodsSpecification gspec1 : goods_spec_list) {
					
					for (GoodsClass spec_goodsclass_detail : gspec1.getSpec_goodsClass_detail()) {
						if (gc.getId().equals(spec_goodsclass_detail.getId())) {
							if (gspec1.getSpec_color() == 1) {
								color_spec_list.add(gspec1);
							} else {
								other_spec_list.add(gspec1);
							}
						}
					}
				}
			} else if (gc.getLevel() == 1) {
				spec_map = Maps.newHashMap();
				spec_map.put("spec_type", Integer.valueOf(0));
				spec_map.put("goodsclass_id", gc.getId());
				spec_map.put("orderBy", "sequence");
				spec_map.put("orderType", "asc");
				
				List<GoodsSpecification> goods_spec_list = this.goodsSpecificationService.queryPageList(spec_map);
				
				for (GoodsSpecification gspec : goods_spec_list) {
					if (gspec.getSpec_color() == 1) {
						color_spec_list.add(gspec);
					} else {
						other_spec_list.add(gspec);
					}
				}
			}
			mv.addObject("color_spec_list", color_spec_list);
			mv.addObject("goods_spec_list", other_spec_list);
			request.getSession(false).removeAttribute("goods_class_info");
		} else if (obj.getGc() != null) {
			mv.addObject("goods_class_info",
					this.storeTools.generic_goods_class_info(obj.getGc()));
			mv.addObject("goods_class", obj.getGc());
			
			List<GoodsSpecification> color_spec_list = Lists.newArrayList();
			List<GoodsSpecification> other_spec_list = Lists.newArrayList();
			GoodsClass gc = obj.getGc();
			
			if (gc.getLevel() == 2) {
				
				spec_map.put("spec_type", Integer.valueOf(0));
				spec_map.put("orderBy", "sequence");
				spec_map.put("orderType", "asc");
				
				List<GoodsSpecification> goods_spec_list = this.goodsSpecificationService.queryPageList(spec_map);
				
				List<GoodsSpecification> spec_list = Lists.newArrayList();
				for (GoodsSpecification gspec1 : goods_spec_list) {
					List<GoodsClass> spec_goodsclass_details = gspec1.getSpec_goodsClass_detail();
					
					for (GoodsClass goodsClass : spec_goodsclass_details) {
						if (gc.getId().equals(goodsClass.getId())) {
							if (gspec1.getSpec_color() == 1) {
								color_spec_list.add(gspec1);
							} else {
								other_spec_list.add(gspec1);
							}
						}
					}
					
				}
			} else if (gc.getLevel() == 1) {
				
				spec_map.put("spec_type", Integer.valueOf(0));
				spec_map.put("goodsclass_id", gc.getId());
				spec_map.put("orderBy", "sequence");
				spec_map.put("orderType", "asc");
				
				List<GoodsSpecification> goods_spec_list = this.goodsSpecificationService.queryPageList(spec_map);
				
				for (GoodsSpecification gspec1 : goods_spec_list) {
					if (gspec1.getSpec_color() == 1) {
						color_spec_list.add(gspec1);
					} else {
						other_spec_list.add(gspec1);
					}
				}
			}
			mv.addObject("color_spec_list", color_spec_list);
			mv.addObject("goods_spec_list", other_spec_list);
			GoodsClass goods_class = null;
			if (obj.getGc().getLevel() == 2) {
				goods_class = obj.getGc().getParent().getParent();
			}
			if (obj.getGc().getLevel() == 1) {
				goods_class = obj.getGc().getParent();
			}
			params = Maps.newHashMap();
			params.put("orderBy", "first_word");
			params.put("orderType", "asc");
			
			List<GoodsBrand> gbs = this.goodsBrandService.queryPageList(params);
			
			mv.addObject("gbs", gbs);
			
			params.clear();
			params.put("gf_cat", Integer.valueOf(1));
			
			gfs = this.goodsFormatService.queryPageList(params);
			
			mv.addObject("gfs", gfs);
			params.clear();
			params.put("parent", -1);
			
			List<Area> areas = this.areaService.queryPageList(params);
			
			mv.addObject("areas", areas);
		}
		String goods_session = CommUtil.randomString(32);
		mv.addObject("goods_session", goods_session);
		request.getSession(false).setAttribute("goods_session", goods_session);
		mv.addObject("imageSuffix", this.storeViewTools
				.genericImageSuffix(this.configService.getSysConfig()
						.getImageSuffix()));

		String[] strs = this.configService.getSysConfig().getImageSuffix()
				.split("\\|");
		StringBuffer sb = new StringBuffer();

		for (String str : strs) {
			sb.append("." + str + ",");
		}
		mv.addObject("imageSuffix1", sb);
		mv.addObject("jsessionid", request.getSession().getId());

		params.clear();
		params.put("userRole", "ADMIN");
		params.put("orderBy", "album_sequence");
		params.put("orderType", "asc");
        
		List<Album> albums = this.albumService.queryPageList(params);
		
		mv.addObject("albums", albums);
		params.clear();
		params.put("sa_user_id", user.getId());
		
		List<ShipAddress> addresses = this.shipAddressService.queryPageList(params);
		
		mv.addObject("shipaddresses", addresses);
		params.put("sa_default", Integer.valueOf(1));
		
		List<ShipAddress> default_address = this.shipAddressService.queryPageList(params);
		
		if (default_address.size() > 0) {
			mv.addObject("default_shipaddress", default_address.get(0));
		}
		mv.addObject("goodsTools", this.goodsTools);
		params.clear();
		params.put("sh_status", Integer.valueOf(1));
		
		List<StoreHouse> storeHouses = this.storeHouseService.queryPageList(params);
		
		mv.addObject("storeHouses", storeHouses);
		List ms = Lists.newArrayList();
		if (obj.getMerchantService_info() != null) {
			ms = JSON.parseArray(obj.getMerchantService_info());
		}
		mv.addObject("ms", ms);
		
		List<MerchantServices> ms_list = this.merchantServicesService.queryPageList(RedPigMaps.newMap());
		
		mv.addObject("ms_list", ms_list);
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
	 * @param u_admin_id
	 * @return
	 */
	@SecurityMapping(title = "商品上下架", value = "/goods_self_sale*", rtype = "admin", rname = "商品管理", rcode = "goods_self", rgroup = "自营")
	@RequestMapping({ "/goods_self_sale" })
	public String goods_sale(HttpServletRequest request,
			HttpServletResponse response, String mulitId,
			String uncheck_mulitId, String op, String brand_id,
			String goods_name, String u_admin_id) {
		int status_op = 0;
		if (CommUtil.null2String(op).equals("storage")) {
			status_op = 1;
		}
		if (CommUtil.null2String(op).equals("out")) {
			status_op = -2;
		}
		String status = "0";
		Goods goods;
		int goods_status;
		if (mulitId.contains("all")) {
			List<Goods> list = generatorQuery(status_op, mulitId,uncheck_mulitId, brand_id, goods_name, u_admin_id);
			for (int i = 0; i < list.size(); i++) {
				goods = (Goods) list.get(i);
				goods_status = goods.getGoods_status() == 0 ? 1 : 0;
				goods.setGoods_status(goods_status);
				this.goodsService.updateById(goods);
				if (goods_status == 0) {
					status = "1";
					this.goodsTools.updateGoodsLucene(goods);
				} else {
					this.goodsTools.deleteGoodsLucene(goods);
				}
			}
		} else {
			String[] ids = mulitId.split(",");

			for (String id : ids) {
				if (!id.equals("")) {
					goods = this.goodsService
							.selectByPrimaryKey(CommUtil.null2Long(id));
					if (goods != null) {
						goods_status = goods.getGoods_status() == 0 ? 1 : 0;
						goods.setGoods_status(goods_status);
						this.goodsService.updateById(goods);
						if (goods_status == 0) {
							status = "1";

							this.goodsTools.updateGoodsLucene(goods);
						} else {
							this.goodsTools.deleteGoodsLucene(goods);
						}
					}
				}
			}
		}
		return "redirect:/goods_self_list?goods_status=" + status;
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
	 * @param u_admin_id
	 * @return
	 */
	@SecurityMapping(title = "商品删除", value = "/goods_self_del*", rtype = "admin", rname = "商品管理", rcode = "goods_self", rgroup = "自营")
	@RequestMapping({ "/goods_self_del" })
	public String goods_del(HttpServletRequest request,
			HttpServletResponse response, String mulitId, String op,
			String uncheck_mulitId, String brand_id, String goods_name,
			String u_admin_id) {
		int status = 0;
		if (CommUtil.null2String(op).equals("storage")) {
			status = 1;
		}
		if (CommUtil.null2String(op).equals("out")) {
			status = -2;
		}
		Goods goods;
		if ("all".equals(mulitId)) {
			List<Goods> list = generatorQuery(status, mulitId, uncheck_mulitId,
					brand_id, goods_name, u_admin_id);
			for (int i = 0; i < list.size(); i++) {
				goods = (Goods) list.get(i);
				goodsListDel(goods);
			}
		} else {
			String[] ids = mulitId.split(",");
			for (String id : ids) {
				if (!id.equals("")) {
					goods = this.goodsService
							.selectByPrimaryKey(CommUtil.null2Long(id));
					goodsListDel(goods);
				}
			}
		}
		return "redirect:/goods_self_list?goods_status=" + status;
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
	@SecurityMapping(title = "商品AJAX更新", value = "/goods_self_ajax*", rtype = "admin", rname = "商品管理", rcode = "goods_self", rgroup = "自营")
	@RequestMapping({ "/goods_self_ajax" })
	public void goods_self_ajax(HttpServletRequest request,
			HttpServletResponse response, String id, String fieldName,
			String value) throws ClassNotFoundException {
		Goods obj = this.goodsService.selectByPrimaryKey(Long.valueOf(Long
				.parseLong(id)));
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
	
	/**
	 * 商品相册列表
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param ajax_type
	 * @param album_name
	 * @return
	 */
	@SecurityMapping(title = "商品相册列表", value = "/goods_album*", rtype = "admin", rname = "商品发布", rcode = "goods_self_add", rgroup = "自营")
	@RequestMapping({ "/goods_album" })
	public ModelAndView goods_album(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String ajax_type,
			String album_name) {
		ModelAndView mv = new RedPigJModelAndView("admin/blue/goods_album.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		Map<String,Object> maps= this.redPigQueryTools.getParams(currentPage,5, "album_sequence", "asc");
        maps.put("redpig_user_userRole","admin");
        maps.put("redpig_user_userRole1","admin_seller");
		
		if (CommUtil.null2String(album_name) != "") {
			maps.put("album_name_like",album_name);
			mv.addObject("album_name", album_name);
		}
		
		IPageList pList = this.albumService.list(maps);
		String album_url = CommUtil.getURL(request) + "/goods_album";
		if (pList.getResult().size() > 0) {
			mv.addObject("albums", pList.getResult());
		}
		mv.addObject("gotoPageAjaxHTML", CommUtil.showPageAjaxHtml(album_url,
				"", pList.getCurrentPage(), pList.getPages(),
				pList.getPageSize()));
		mv.addObject("ajax_type", ajax_type);
		mv.addObject("ImageTools", this.ImageTools);
		return mv;
	}
	

	private List<Goods> generatorQuery(int status, String mulitId,
			String uncheck_mulitId, String brand_id, String goods_name,
			String u_admin_id) {
		
		Map<String,Object> maps = Maps.newHashMap();
        maps.put("goods_type",0);
        maps.put("goods_status",status);
        
        
		if ((uncheck_mulitId != null) && (!"".equals(uncheck_mulitId))) {
			uncheck_mulitId = uncheck_mulitId.substring(0,
					uncheck_mulitId.length() - 1);
			String[] uIds = uncheck_mulitId.split(",");
			List<Long> ids = Lists.newArrayList();
			for (String id : uIds) {
				ids.add(Long.parseLong(id.trim()));
			}
			
			maps.put("ids_no",ids);
			
		}
		if ((brand_id != null) && (!brand_id.equals(""))) {
			
			maps.put("goods_brand_id",brand_id);
		}
		if ((u_admin_id != null) && (!u_admin_id.equals(""))) {
			
			maps.put("user_admin_id",u_admin_id);
		}
		if ((goods_name != null) && (!goods_name.equals(""))) {
			
			maps.put("goods_name_like",goods_name);
		}
		
		List<Goods> list = this.goodsService.queryPageList(maps);
		
		return list;
	}
	

	private void goodsListDel(Goods goods) {
		Map map = Maps.newHashMap();
		map.put("goods_id", goods.getId());
		
		List<GoodsCart> goodCarts = this.goodsCartService.queryPageList(map);
		
		Long ofid = null;
		List<Evaluate> evaluates = goods.getEvaluates();
		for (Evaluate e : evaluates) {
			this.evaluateService.deleteById(e.getId());
		}
		
		this.goodsService.batchDeleteUserGoodsClass(goods.getId(), goods.getGoods_ugcs());
		this.goodsService.batchDeleteGoodsPhotos(goods.getId(), goods.getGoods_photos());
		this.goodsService.batchDeleteGoodsSpecProperty(goods.getId(), goods.getGoods_specs());
		
		for (GoodsCart gc : goods.getCarts()) {
			gc.getGsps().clear();
			this.goodsCartService.deleteById(gc.getId());
		}
		this.goodsService.deleteById(goods.getId());

		this.goodsTools.deleteGoodsLucene(goods);
	}

	
}
