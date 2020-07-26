package com.redpigmall.manage.admin.action.self.goods;

import java.io.IOException;
import java.io.PrintWriter;
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
import com.redpigmall.api.sec.SecurityUserHolder;
import com.redpigmall.api.tools.CommUtil;
import com.redpigmall.api.tools.RedPigMaps;
import com.redpigmall.manage.admin.action.base.BaseAction;
import com.redpigmall.domain.Album;
import com.redpigmall.domain.Area;
import com.redpigmall.domain.GoodsBrand;
import com.redpigmall.domain.GoodsClass;
import com.redpigmall.domain.GoodsFormat;
import com.redpigmall.domain.GoodsSpecification;
import com.redpigmall.domain.MerchantServices;
import com.redpigmall.domain.Payment;
import com.redpigmall.domain.ShipAddress;
import com.redpigmall.domain.StoreHouse;
import com.redpigmall.domain.User;

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
public class RedPigSelfGoodsManage1Action extends BaseAction{
	/**
	 * 商品发布第一步
	 * @param request
	 * @param response
	 * @param id
	 * @param add_type
	 * @return
	 */
	@SecurityMapping(title = "商品发布第一步", value = "/add_goods_first*", rtype = "admin", rname = "商品发布", rcode = "goods_self_add", rgroup = "自营")
	@RequestMapping({ "/add_goods_first" })
	public ModelAndView add_goods_first(HttpServletRequest request,
			HttpServletResponse response, String id, String add_type) {
		ModelAndView mv = new RedPigJModelAndView("admin/blue/error.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder
				.getCurrentUser().getId());
		request.getSession(false).removeAttribute("goods_class_info");
		Map params = Maps.newHashMap();
		List<Payment> payments = Lists.newArrayList();
		params.put("install", Boolean.valueOf(true));
		payments = this.paymentService.queryPageList(params);
		
		if (payments.size() == 0) {
			mv.addObject("op_title", "请至少开通一种支付方式");
			mv.addObject("list_url", CommUtil.getURL(request)
					+ "/payment_list");
			return mv;
		}
		mv = new RedPigJModelAndView("admin/blue/add_goods_first.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		String json_staples = "";
		if ((user.getStaple_gc() != null) && (!user.getStaple_gc().equals(""))) {
			json_staples = user.getStaple_gc();
			List<Map> staples = JSON.parseArray(json_staples, Map.class);
			mv.addObject("goodsClassStaple", staples);
		}
		params.clear();
		params.put("parent", -1);
		params.put("orderBy", "sequence");
		params.put("orderType", "asc");
		
		List<GoodsClass> goodsClass = this.goodsClassService.queryPageList(params);
		
		mv.addObject("goodsClass", goodsClass);
		mv.addObject("id", CommUtil.null2String(id));
		mv.addObject("add_type", add_type);
		return mv;
	}
	
	/**
	 * 根据常用商品分类加载分类信息
	 * @param request
	 * @param response
	 * @param id
	 * @param name
	 */
	@SecurityMapping(title = "根据常用商品分类加载分类信息", value = "/load_goods_class_staple*", rtype = "admin", rname = "商品发布", rcode = "goods_self_add", rgroup = "自营")
	@RequestMapping({ "/load_goods_class_staple" })
	public void load_goods_class_staple(HttpServletRequest request,
			HttpServletResponse response, String id, String name) {
		GoodsClass obj = null;
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder
				.getCurrentUser().getId());
		if ((id != null) && (!id.equals(""))) {
			List<Map> list_map = JSON
					.parseArray(user.getStaple_gc(), Map.class);
			for (Map map : list_map) {
				if (CommUtil.null2String(map.get("id")).equals(id)) {
					obj = this.goodsClassService.selectByPrimaryKey(CommUtil
							.null2Long(map.get("id")));
				}
			}
		}
		if ((name != null) && (!name.equals(""))) {
			obj = this.goodsClassService.getObjByProperty( "className","=",name);
		}
		List<List<Map>> list = Lists.newArrayList();
		if (obj != null) {
			request.getSession(false).setAttribute("goods_class_info", obj);
			Map params = Maps.newHashMap();
			List<Map> second_list = Lists.newArrayList();
			List<Map> third_list = Lists.newArrayList();
			List<Map> other_list = Lists.newArrayList();
			if (obj.getLevel() == 2) {
				params.put("parent", obj.getParent().getParent().getId());
				params.put("orderBy", "sequence");
				params.put("orderType", "asc");
				
				List<GoodsClass> second_gcs = this.goodsClassService.queryPageList(params);
				
				for (GoodsClass gc1 : second_gcs) {
					HashMap map = Maps.newHashMap();
					map.put("id", gc1.getId());
					map.put("className", gc1.getClassName());
					map.put("level", Integer.valueOf(gc1.getLevel()));
					((List) second_list).add(map);
				}
				params.clear();
				params.put("parent", obj.getParent().getId());
				params.put("orderBy", "sequence");
				params.put("orderType", "asc");
				
				List<GoodsClass> third_gcs = this.goodsClassService.queryPageList(params);
				
				for (GoodsClass gc1 : third_gcs) {
					Map map = Maps.newHashMap();
					map.put("id", ((GoodsClass) gc1).getId());
					map.put("className", ((GoodsClass) gc1).getClassName());
					map.put("level",
							Integer.valueOf(((GoodsClass) gc1).getLevel()));
					third_list.add(map);
				}
			}
			if (obj.getLevel() == 1) {
				params.clear();
				params.put("parent", obj.getParent().getId());
				params.put("orderBy", "sequence");
				params.put("orderType", "asc");
				
				List<GoodsClass> third_gcs = this.goodsClassService.queryPageList(params);
				
				for (GoodsClass gc : third_gcs) {
					Map map = Maps.newHashMap();
					map.put("id", gc.getId());
					map.put("className", gc.getClassName());
					map.put("level", Integer.valueOf(gc.getLevel()));
					((List) second_list).add(map);
				}
			}
			Map map = Maps.newHashMap();
			String staple_info = generic_goods_class_info(obj);
			map.put("staple_info",
					staple_info.substring(0, staple_info.length() - 1));
			other_list.add(map);

			list.add(second_list);
			list.add(third_list);
			list.add(other_list);
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
	 * 加载商品分类
	 * @param request
	 * @param response
	 * @param pid
	 * @param session
	 */
	@SecurityMapping(title = "加载商品分类", value = "/load_goods_class*", rtype = "admin", rname = "商品发布", rcode = "goods_self_add", rgroup = "自营")
	@RequestMapping({ "/load_goods_class" })
	public void load_goods_class(HttpServletRequest request,
			HttpServletResponse response, String pid, String session) {
		GoodsClass obj = this.goodsClassService.selectByPrimaryKey(CommUtil.null2Long(pid));
		List<Map> list = Lists.newArrayList();
		if (obj != null) {
			for (GoodsClass gc : obj.getChilds()) {
				Map map = Maps.newHashMap();
				map.put("id", gc.getId());
				map.put("className", gc.getClassName());
				list.add(map);
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
	 * 添加常用商品分类
	 * @param request
	 * @param response
	 */
	@SecurityMapping(title = "添加常用商品分类", value = "/load_goods_class*", rtype = "admin", rname = "商品发布", rcode = "goods_self_add", rgroup = "自营")
	@RequestMapping({ "/add_goods_class_staple" })
	public void add_goods_class_staple(HttpServletRequest request,
			HttpServletResponse response) {
		String ret = "error";
		String json = "";
		Map map = Maps.newHashMap();
		boolean flag = true;
		if (request.getSession(false).getAttribute("goods_class_info") != null) {
			GoodsClass gc = this.goodsClassService
					.selectByPrimaryKey(((GoodsClass) request.getSession(false)
							.getAttribute("goods_class_info")).getId());
			User user = this.userService.selectByPrimaryKey(SecurityUserHolder
					.getCurrentUser().getId());
			List<Map> list_map = Lists.newArrayList();
			if ((user.getStaple_gc() != null)
					&& (!user.getStaple_gc().equals(""))) {
				list_map = JSON.parseArray(user.getStaple_gc(), Map.class);
			}
			if (list_map.size() > 0) {
				for (Map staple : list_map) {
					if (gc.getId().toString()
							.equals(CommUtil.null2String(staple.get("id")))) {
						flag = false;
						break;
					}
				}
				if (flag) {
					map.put("name",
							gc.getParent().getParent().getClassName() + ">"
									+ gc.getParent().getClassName() + ">"
									+ gc.getClassName());
					map.put("id", gc.getId());
					list_map.add(map);
					json = JSON.toJSONString(list_map);
				}
			} else {
				map.put("name",
						gc.getParent().getParent().getClassName() + ">"
								+ gc.getParent().getClassName() + ">"
								+ gc.getClassName());
				map.put("id", gc.getId());
				list_map.add(map);
				json = JSON.toJSONString(list_map);
			}
			if (flag) {
				user.setStaple_gc(json);
				this.userService.updateById(user);
				flag = true;
			}
			if (flag) {
				ret = "success";
			}
		}
		map.put("ret", ret);
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		try {
			PrintWriter writer = response.getWriter();
			writer.print(JSON.toJSONString(map));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 删除常用商品分类
	 * @param request
	 * @param response
	 * @param id
	 */
	@SecurityMapping(title = "删除常用商品分类", value = "/del_goods_class_staple*", rtype = "admin", rname = "商品发布", rcode = "goods_self_add", rgroup = "自营")
	@RequestMapping({ "/del_goods_class_staple" })
	public void del_goods_class_staple(HttpServletRequest request,
			HttpServletResponse response, String id) {
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder
				.getCurrentUser().getId());
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
	 * 商品发布第二步
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @return
	 */
	@SecurityMapping(title = "商品发布第二步", value = "/add_goods_second*", rtype = "admin", rname = "商品发布", rcode = "goods_self_add", rgroup = "自营")
	@RequestMapping({ "/add_goods_second" })
	public ModelAndView add_goods_second(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,
			String orderType) {
		ModelAndView mv = new RedPigJModelAndView("admin/blue/add_goods_second.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		if (request.getSession(false).getAttribute("goods_class_info") != null) {
			GoodsClass gc = (GoodsClass) request.getSession(false).getAttribute("goods_class_info");
			gc = this.goodsClassService.selectByPrimaryKey(gc.getId());
			String goods_class_info = generic_goods_class_info(gc);
			mv.addObject("goods_class",
					this.goodsClassService.selectByPrimaryKey(gc.getId()));
			mv.addObject("goods_class_info", goods_class_info.substring(0,
					goods_class_info.length() - 1));
			request.getSession(false).removeAttribute("goods_class_info");
			List<GoodsSpecification> color_spec_list = Lists.newArrayList();
			List<GoodsSpecification> other_spec_list = Lists.newArrayList();
			if (gc.getLevel() == 2) {
				Map spec_map = Maps.newHashMap();
				spec_map.put("spec_type", Integer.valueOf(0));
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
			Map params = Maps.newHashMap();
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
			mv.addObject("imageSuffix", this.storeViewTools
					.genericImageSuffix(this.configService.getSysConfig()
							.getImageSuffix()));
			
			params.clear();
			params.put("gfcat", 1);
			
			List<GoodsFormat> gfs = this.goodsFormatService.queryPageList(params);
			
			mv.addObject("gfs", gfs);
			params.clear();
			params.put("parent", -1);
			
			List<Area> areas = this.areaService.queryPageList(params);
			
			mv.addObject("areas", areas);
			String goods_session = CommUtil.randomString(32);
			mv.addObject("goods_session", goods_session);
			request.getSession(false).setAttribute("goods_session",
					goods_session);
			
			String[] strs = this.configService.getSysConfig().getImageSuffix().split("\\|");
			StringBuffer sb = new StringBuffer();
			
			for (String str : strs) {

				sb.append("." + str + ",");
			}
			mv.addObject("imageSuffix1", sb);
			
			User user = this.userService.selectByPrimaryKey(SecurityUserHolder.getCurrentUser().getId());
			user = user.getParent() == null ? user : user.getParent();
			params.clear();
			params.put("user_userRole1", "ADMIN");
			params.put("user_userRole2", "ADMIN_SELLER");
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
			
			if (((List) default_address).size() > 0) {
				mv.addObject("default_shipaddress",default_address.get(0));
			}
			
			mv.addObject("goodsTools", this.goodsTools);
			params.clear();
			params.put("sh_status", Integer.valueOf(1));
			
			List<StoreHouse> storeHouses = this.storeHouseService.queryPageList(params);
			
			mv.addObject("storeHouses", storeHouses);
			
			List<MerchantServices> ms_list = this.merchantServicesService.queryPageList(RedPigMaps.newMap());
			
			mv.addObject("ms_list", ms_list);
		}
		isAdminAlbumExist();
		return mv;
	}
	

	private void isAdminAlbumExist() {
		Map params = Maps.newHashMap();
		params.put("user_userRole1", "ADMIN");
		params.put("user_userRole2", "ADMIN_SELLER");
		
		List<Album> albums = this.albumService.queryPageList(params);
		
		if (albums.size() == 0) {
			Album album = new Album();
			album.setAddTime(new Date());
			album.setAlbum_default(true);
			album.setAlbum_name("默认相册");
			album.setAlbum_sequence(55536);
			album.setUser(SecurityUserHolder.getCurrentUser());
			this.albumService.saveEntity(album);
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
	
}
