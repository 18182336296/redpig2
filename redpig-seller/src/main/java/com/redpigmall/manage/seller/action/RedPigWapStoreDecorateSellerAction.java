package com.redpigmall.manage.seller.action;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
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
import com.redpigmall.api.sec.SecurityUserHolder;
import com.redpigmall.api.tools.CommUtil;
import com.redpigmall.api.tools.RedPigMaps;
import com.redpigmall.domain.Accessory;
import com.redpigmall.domain.Album;
import com.redpigmall.domain.Goods;
import com.redpigmall.domain.GoodsClass;
import com.redpigmall.domain.Store;
import com.redpigmall.domain.User;
import com.redpigmall.manage.seller.action.base.BaseAction;

/**
 * 
 * <p>
 * Title: RedPigWapStoreDecorateSellerAction.java
 * </p>
 * 
 * <p>
 * Description:卖家中心运费管理控制器，V1.3版本开始，废除以前的配送方式管理
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
 * @date 2014-7-17
 * 
 * @version redpigmall_b2b2c v8.0 2016版
 */
@Controller
public class RedPigWapStoreDecorateSellerAction extends BaseAction{
	/**
	 * wap店铺首页装修
	 * @param request
	 * @param response
	 * @return
	 */
	@SecurityMapping(title = "wap店铺首页装修", value = "/wap_decorate*", rtype = "seller", rname = "wap店铺装修", rcode = "wap_store_decorate_seller", rgroup = "我的店铺")
	@RequestMapping({ "/wap_decorate" })
	public ModelAndView wap_decorate(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/sellercenter/decorate/wap_decorate.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		if (user != null) {
			Map<String, Object> params = Maps.newHashMap();
			params.put("user_id", user.getId());
			params.put("orderBy", "album_sequence");
			params.put("orderType", "asc");

			List<Album> albums = this.albumService.queryPageList(params);
			
			mv.addObject("albums", albums);
			params = RedPigMaps.newParent(-1, "sequence", "asc");
			
			List<GoodsClass> gcs = this.goodsClassService.queryPageList(params);
			
			mv.addObject("gcs", gcs);
			String[] strs = this.configService.getSysConfig().getImageSuffix().split("\\|");
			StringBuffer sb = new StringBuffer();

			for (String str : strs) {
				sb.append("." + str + ",");
			}
			mv.addObject("imageSuffix1", sb);
		}
		return mv;
	}
	
	/**
	 * wap店铺首页装修保存
	 * @param request
	 * @param response
	 * @param id
	 * @param title
	 * @param sequence
	 * @param status
	 * @param edit_type
	 * @param line_num
	 * @param line_type
	 * @param edit_mo
	 * @param line_info
	 * @param goods_li
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@SecurityMapping(title = "wap店铺首页装修保存", value = "/wap_index_save*", rtype = "seller", rname = "wap店铺装修", rcode = "wap_store_decorate_seller", rgroup = "我的店铺")
	@RequestMapping({ "/wap_index_save" })
	public void wap_index_saveEntity(HttpServletRequest request,
			HttpServletResponse response, String id, String title,
			String sequence, String status, String edit_type, String line_num,
			String line_type, String edit_mo, String line_info, String goods_li) {
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder
				.getCurrentUser().getId());
		boolean ret = true;
		if ((user != null) && (user.getStore() != null)) {
			Store store = user.getStore();
			String swd = store.getStore_wap_decorate_info();
			List<Map<String, String>> list = Lists.newArrayList();
			Map map = Maps.newHashMap();
			if ((swd != null) && (!"".equals(swd))) {
				List<Map> json_list = JSON.parseArray(swd, Map.class);
				if (json_list.size() > 0) {
					for (Map pmap : json_list) {
						if ((id != null) && (!"".equals(id))) {
							if (id.equals(CommUtil.null2String(pmap.get("id")))) {
								pmap.clear();
								map.put("id", id);
								continue;
							}
						}
						list.add(pmap);
					}
				}
			}
			if ((id == null) || ("".equals(id))) {
				Collections.sort(list, new IdComparator());
				int nid = 0;
				if (list.size() > 0) {
					nid = CommUtil.null2Int(((Map) list.get(list.size() - 1))
							.get("id"));
				}
				map.put("id", Integer.valueOf(nid + 1));
			}
			map.put("title", title);
			if ((sequence == null) || ("".equals(sequence))) {
				Collections.sort(list, new MapComparator());
				int se = 0;
				if (list.size() > 0) {
					se = CommUtil.null2Int(((Map) list.get(list.size() - 1))
							.get("sequence"));
				}
				map.put("sequence", Integer.valueOf(se + 1));
			} else {
				map.put("sequence", sequence);
			}
			map.put("status", status);
			map.put("edit_mo", edit_mo);
			if (!"7".equals(edit_mo)) {
				map.put("line_info", line_info);
			} else {
				map.put("line_info", goods_li);
			}
			list.add(map);
			Collections.sort(list, new MapComparator());
			String temp = JSON.toJSONString(list);
			store.setStore_wap_decorate_info(temp);
			
			this.storeService.updateById(store);
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
	
	/**
	 * wap店铺首页装修
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param album_id
	 * @return
	 */
	@SecurityMapping(title = "wap店铺首页装修", value = "/wap_index_imgs*", rtype = "seller", rname = "wap店铺装修", rcode = "wap_store_decorate_seller", rgroup = "我的店铺")
	@RequestMapping({ "/wap_index_imgs" })
	public ModelAndView wap_index_imgs(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String album_id) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/sellercenter/decorate/wap_index_imgs.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		
		Map<String,Object> maps= this.redPigQueryTools.getParams(currentPage,5, "addTime", "desc");
		
		maps.put("album_id", CommUtil.null2Long(album_id));
		
		maps.put("user_userRole", "seller");
		
		IPageList pList = this.accessoryService.list(maps);
		
		String photo_url = CommUtil.getURL(request) + "/wap_index_imgs";
		mv.addObject("photos", pList.getResult());
		mv.addObject("gotoPageAjaxHTML", CommUtil.showPageAjaxHtml(photo_url,
				"", pList.getCurrentPage(), pList.getPages(),
				pList.getPageSize()));
		mv.addObject("album_id", album_id);
		return mv;
	}
	
	/**
	 * wap店铺首页装修
	 * @param request
	 * @param response
	 * @param id
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@SecurityMapping(title = "wap店铺首页装修", value = "/wap_show_items*", rtype = "seller", rname = "wap店铺装修", rcode = "wap_store_decorate_seller", rgroup = "我的店铺")
	@RequestMapping({ "/wap_show_items" })
	public ModelAndView wap_show_goods(HttpServletRequest request,
			HttpServletResponse response, String id) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/sellercenter/decorate/wap_show_goods.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		List gcs = this.StoreTools.query_store_gc(user.getStore());
		mv.addObject("gcs", gcs);
		if ((user != null) && (user.getStore() != null) && (id != null)
				&& (!"".equals(id))) {
			Store store = user.getStore();
			String swd_info = store.getStore_wap_decorate_info();
			if ((swd_info != null) && (!"".equals(swd_info))) {
				List<Map> json_list = JSON.parseArray(swd_info, Map.class);
				for (Map map : json_list) {
					if (id.equals(CommUtil.null2String(map.get("id")))) {
						String floor_info = CommUtil.null2String(map
								.get("line_info"));
						if ((floor_info != null) && (!"".equals(floor_info))) {
							String[] arr = floor_info.split(",");
							List goods_list = Lists.newArrayList();

							for (String st : arr) {

								if ((st != null) && (!"".equals(st))) {
									Goods goods = this.goodsService
											.selectByPrimaryKey(CommUtil.null2Long(st));
									if (goods != null) {
										goods_list.add(goods);
									}
								}
							}
							mv.addObject("goods_list", goods_list);
						}
					}
				}
			}
		}
		return mv;
	}
	
	/**
	 * wap店铺装修商品加载
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param gc_id
	 * @param goods_name
	 * @param activity_name
	 * @return
	 */
	@SecurityMapping(title = "wap店铺装修商品加载", value = "/wap_store_goods_search*", rtype = "seller", rname = "wap店铺装修", rcode = "wap_store_decorate_seller", rgroup = "我的店铺")
	@RequestMapping({ "/wap_store_goods_search" })
	public ModelAndView wap_store_goods_search(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String gc_id,
			String goods_name, String activity_name) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/sellercenter/decorate/wap_index_goods_load.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		
		Map<String,Object> maps= this.redPigQueryTools.getParams(currentPage,5, "addTime","desc");
		
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder.getCurrentUser().getId());
		
		user = user.getParent() == null ? user : user.getParent();
		Store store = user.getStore();
		
		maps.put("goods_store_id", store.getId());
		
		if (!CommUtil.null2String(gc_id).equals("")) {
			Set<Long> ids = genericIds(this.goodsClassService.selectByPrimaryKey(CommUtil.null2Long(gc_id)));
			
			maps.put("gc_ids", ids);
		}
		if (!CommUtil.null2String(goods_name).equals("")) {
			maps.put("goods_name_like", goods_name);
		}
		
		maps.put("goods_status", 0);
		if ((activity_name != null) && (!"".equals(activity_name))) {
			List<String> str_list = Lists.newArrayList();
			str_list.add(activity_name);
			this.queryTools.queryActivityGoods(maps, str_list);
		}
		
		IPageList pList = this.goodsService.list(maps);
		
		CommUtil.saveIPageList2ModelAndView(CommUtil.getURL(request)
				+ "/wap_store_goods_search.html", "", "&gc_id=" + gc_id
				+ "&goods_name=" + goods_name + "&activity_name="
				+ activity_name, pList, mv);
		return mv;
	}
	
	/**
	 * wap店铺首页装修信息加载
	 * @param request
	 * @param response
	 * @param store_id
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	@SecurityMapping(title = "wap店铺首页装修信息加载", value = "/wap_index_preview*", rtype = "seller", rname = "wap店铺装修", rcode = "wap_store_decorate_seller", rgroup = "我的店铺")
	@RequestMapping({ "/wap_index_preview" })
	public ModelAndView wap_index_preview(HttpServletRequest request,
			HttpServletResponse response, String store_id) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/sellercenter/decorate/wap_index_preview.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder
				.getCurrentUser().getId());
		if (user != null) {
			Store store = user.getStore();
			String swd_info = store.getStore_wap_decorate_info();
			if ((swd_info != null) && (!"".equals(swd_info))) {
				List<Map> json_list = JSON.parseArray(swd_info, Map.class);
				mv.addObject("json_list", json_list);
				mv.addObject("weixinstoreViewTools", this.weixinstoreViewTools);
			}
		}
		return mv;
	}
	
	/**
	 * wap店铺首页图片上传
	 * @param request
	 * @param response
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@SecurityMapping(title = "wap店铺首页图片上传", value = "/store_index_image_upload*", rtype = "seller", rname = "wap店铺装修", rcode = "wap_store_decorate_seller", rgroup = "我的店铺")
	@RequestMapping({ "/store_index_image_upload" })
	public void app_index_image_upload(HttpServletRequest request,
			HttpServletResponse response) {
		Map ajaxUploadInfo = Maps.newHashMap();
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder
				.getCurrentUser().getId());
		String uploadFilePath = this.configService.getSysConfig()
				.getUploadFilePath();
		String saveFilePathName = request.getSession().getServletContext()
				.getRealPath("/")
				+ uploadFilePath + File.separator + "wap_store";
		try {
			Map<String, Object> map = CommUtil.saveFileToServer(request,"upload_img", saveFilePathName, null, null);
			if (!"".equals(CommUtil.null2String(map.get("fileName")))) {
				Accessory image = new Accessory();
				image.setAddTime(new Date());
				image.setExt((String) map.get("mime"));
				image.setPath(uploadFilePath + "/wap_store");
				image.setWidth(CommUtil.null2Int(map.get("width")));
				image.setHeight(CommUtil.null2Int(map.get("height")));
				image.setName(CommUtil.null2String(map.get("fileName")));
				image.setUser(user);
				Album album = this.albumService.getDefaultAlbum(user.getId());
				if (album == null) {
					album = new Album();
					album.setAddTime(new Date());
					album.setAlbum_name("默认相册【" + user.getUserName() + "】");
					album.setAlbum_sequence(55536);
					album.setAlbum_default(true);
					album.setUser(user);
					this.albumService.saveEntity(album);
				}
				image.setAlbum(album);
				this.accessoryService.saveEntity(image);
				ajaxUploadInfo.put(
						"url",
						image.getPath() + "/" + image.getName() + "?"
								+ image.getHeight() + "&" + image.getWidth());
			}
		} catch (IOException e) {
			e.printStackTrace();
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
	 * wap店铺装修楼层添加
	 * @param request
	 * @param response
	 * @return
	 */
	@SecurityMapping(title = "wap店铺装修楼层添加", value = "/wap_floor_add*", rtype = "seller", rname = "wap店铺装修", rcode = "wap_store_decorate_seller", rgroup = "我的店铺")
	@RequestMapping({ "/wap_floor_add" })
	public ModelAndView wap_floor_add(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/sellercenter/decorate/wap_floor_add.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		return mv;
	}
	
	/**
	 * wap店铺装修楼层编辑
	 * @param request
	 * @param response
	 * @param id
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked"})
	@SecurityMapping(title = "wap店铺装修楼层编辑", value = "/wap_floor_edit*", rtype = "seller", rname = "wap店铺装修", rcode = "wap_store_decorate_seller", rgroup = "我的店铺")
	@RequestMapping({ "/wap_floor_edit" })
	public ModelAndView wap_floor_edit(HttpServletRequest request,
			HttpServletResponse response, String id) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/sellercenter/decorate/wap_floor_add.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		if ((id != null) && (!"".equals(id))) {
			User user = this.userService.selectByPrimaryKey(SecurityUserHolder
					.getCurrentUser().getId());
			if (user != null) {
				Store store = user.getStore();
				String swd_info = store.getStore_wap_decorate_info();
				if ((swd_info != null) && (!"".equals(swd_info))) {
					List<Map> json_list = JSON.parseArray(swd_info, Map.class);
					for (Map map : json_list) {
						if (id.equals(CommUtil.null2String(map.get("id")))) {
							mv.addObject("obj", map);
							mv.addObject("sequence", map.get("sequence"));
							mv.addObject("edit_mo", map.get("edit_mo"));
							List list = Lists.newArrayList();
							String floor_info = CommUtil.null2String(map
									.get("line_info"));
							if ((floor_info != null)
									&& (!"".equals(floor_info))) {
								if ("7".equals(CommUtil.null2String(map
										.get("edit_mo")))) {
									String[] arr = floor_info.split(",");
									for (String st : arr) {
										if ((st != null) && (!"".equals(st))) {
											Goods goods = this.goodsService
													.selectByPrimaryKey(CommUtil
															.null2Long(st));
											if (goods != null) {
												list.add(goods);
											}
										}
									}
									if (!"".equals(CommUtil.null2String(map
											.get("line_info")))) {
										mv.addObject("goods_li",
												map.get("line_info"));
									}
								} else {

									for (String str : floor_info.split(";")) {
										if (str != "") {
											String[] arr = str.split(",");
											Map line_map = Maps.newHashMap();
											line_map.put("click_type", arr[0]);
											if ("goods".equals(arr[0])) {
												line_map.put("goods_id", arr[1]);
											}
											if ("url".equals(arr[0])) {
												line_map.put("url", arr[1]);
											}
											line_map.put("img_src", arr[2]);
											line_map.put("po", arr[3]);
											list.add(line_map);
										}
									}
								}
							}
							mv.addObject("list", list);
							mv.addObject("status", map.get("status"));
						}
					}
				}
			}
		}
		return mv;
	}
	
	/**
	 * wap店铺装修楼层删除
	 * @param request
	 * @param response
	 * @param id
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@SecurityMapping(title = "wap店铺装修楼层删除", value = "/wap_floor_del*", rtype = "seller", rname = "wap店铺装修", rcode = "wap_store_decorate_seller", rgroup = "我的店铺")
	@RequestMapping({ "/wap_floor_del" })
	public void wap_floor_del(HttpServletRequest request,
			HttpServletResponse response, String id) {
		boolean ret = false;
		if ((id != null) && (!id.equals(""))) {
			User user = this.userService.selectByPrimaryKey(SecurityUserHolder
					.getCurrentUser().getId());
			if (user != null) {
				Store store = user.getStore();
				String swd_info = store.getStore_wap_decorate_info();
				if ((swd_info != null) && (!"".equals(swd_info))) {
					List<Map> json_list = JSON.parseArray(swd_info, Map.class);
					List list = Lists.newArrayList();
					for (Map map : json_list) {
						if (id.equals(CommUtil.null2String(map.get("id")))) {
							map.clear();
							ret = true;
						} else {
							list.add(map);
						}
					}
					Collections.sort(list, new MapComparator());
					String temp = JSON.toJSONString(list);
					store.setStore_wap_decorate_info(temp);
					this.storeService.updateById(store);
				}
			}
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
	 * wap店铺装修楼层商品加载
	 * @param request
	 * @param response
	 * @param goods_name
	 * @param gc_id
	 * @param goods_ids
	 * @param activity_name
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@SecurityMapping(title = "wap店铺装修楼层商品加载", value = "/wap_index_goods_load*", rtype = "seller", rname = "wap店铺装修", rcode = "wap_store_decorate_seller", rgroup = "我的店铺")
	@RequestMapping({ "/wap_index_goods_load" })
	public void wap_index_goods_load(HttpServletRequest request,
			HttpServletResponse response, String goods_name, String gc_id,
			String goods_ids, String activity_name) {
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		
		Map<String, Object> params = Maps.newHashMap();
		params.put("goods_name_like", goods_name.trim());
		params.put("goods_status", Integer.valueOf(0));
		params.put("goods_store_id", user.getStore().getId());
		
		if ((gc_id != null) && (!gc_id.equals(""))) {
			GoodsClass gc = this.goodsClassService.selectByPrimaryKey(CommUtil
					.null2Long(gc_id));
			Set ids = genericIds(gc);
			params.put("gc_ids", ids);
		}
		if ((activity_name != null) && (!"".equals(activity_name))) {
			Map<String, Object> map = Maps.newHashMap();
			map.put("goods_status", Integer.valueOf(0));
			map.put("activity_status", Integer.valueOf(2));
			map.put("group_buy", Integer.valueOf(2));
			map.put("combin_status", Integer.valueOf(1));
			map.put("order_enough_give_status", Integer.valueOf(1));
			map.put("enough_reduce", Integer.valueOf(1));
			map.put("f_sale_type", Integer.valueOf(1));
			map.put("advance_sale_type", Integer.valueOf(1));
			map.put("order_enough_if_give", Integer.valueOf(1));
			String status = CommUtil.null2String(map.get(activity_name));
			if (!"".equals(status)) {
				params.put(activity_name, status);
			}
		}
		String[] ids = goods_ids.split(",");
		List ids_list = Arrays.asList(ids);
		List<Goods> goods = this.goodsService.queryPageList(params);
		List list = Lists.newArrayList();
		for (Goods obj : goods) {
			if (!ids_list.contains(obj.getId().toString())) {
				Map<String, Object> map = Maps.newHashMap();
				map.put("id", obj.getId());
				map.put("store_price", obj.getStore_price());
				map.put("goods_name", obj.getGoods_name());
				map.put("store_inventory",
						Integer.valueOf(obj.getGoods_inventory()));
				if (obj.getGoods_main_photo() != null) {
					map.put("img", obj.getGoods_main_photo().getPath() + "/"
							+ obj.getGoods_main_photo().getName() + "_small."
							+ obj.getGoods_main_photo().getExt());
				} else {
					map.put("img", this.configService.getSysConfig()
							.getGoodsImage().getPath()
							+ "/"
							+ this.configService.getSysConfig().getGoodsImage()
									.getName());
				}
				list.add(map);
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

	private Set<Long> genericIds(GoodsClass gc) {
		Set<Long> ids = Sets.newHashSet();
		ids.add(gc.getId());
		for (GoodsClass child : gc.getChilds()) {
			Set<Long> cids = genericIds(child);
			for (Long cid : cids) {
				ids.add(cid);
			}
			ids.add(child.getId());
		}
		return ids;
	}

	public class MapComparator implements Comparator<Map<String, String>> {
		public MapComparator() {
		}

		@SuppressWarnings("rawtypes")
		public int compare(Map o1, Map o2) {
			return CommUtil.null2Int(o1.get("sequence"))
					- CommUtil.null2Int(o2.get("sequence"));
		}
	}

	public class IdComparator implements Comparator<Map<String, String>> {
		public IdComparator() {
		}

		@SuppressWarnings("rawtypes")
		public int compare(Map o1, Map o2) {
			return CommUtil.null2Int(o1.get("id"))
					- CommUtil.null2Int(o2.get("id"));
		}
	}
}
