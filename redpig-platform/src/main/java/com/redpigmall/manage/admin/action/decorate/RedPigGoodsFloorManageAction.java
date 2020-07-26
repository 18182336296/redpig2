package com.redpigmall.manage.admin.action.decorate;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.springframework.stereotype.Controller;
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
import com.redpigmall.api.tools.ObjectUtils;
import com.redpigmall.api.tools.WebForm;
import com.redpigmall.api.tools.httpClient.HttpRequester;
import com.redpigmall.domain.Accessory;
import com.redpigmall.domain.Advert;
import com.redpigmall.domain.AdvertPosition;
import com.redpigmall.domain.Goods;
import com.redpigmall.domain.GoodsClass;
import com.redpigmall.domain.GoodsFloor;
import com.redpigmall.domain.SysConfig;
import com.redpigmall.manage.admin.action.base.BaseAction;

/**
 * 
 * <p>
 * Title: RedPigGoodsFloorManageAction.java
 * </p>
 * 
 * <p>
 * Description: 商品楼层管理控制器，通过拖拽式管理完成首页楼层管理，平台管理员可以任意管理控制商城首页楼层信息
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
 * @date 2016年5月27日
 * 
 * @version redpigmall_b2b2c 6.0
 */
@SuppressWarnings({"rawtypes","unchecked"})
@Controller
public class RedPigGoodsFloorManageAction extends BaseAction{

	
	/**
	 * 生成静态楼层文件
	 * @param request
	 * @param response
	 * @param gf_type
	 */
	@SuppressWarnings("deprecation")
	@SecurityMapping(title = "生成静态楼层文件", value = "/goods_floor_static*", rtype = "admin", rname = "首页楼层", rcode = "goods_floor", rgroup = "装修")
	@RequestMapping({ "/goods_floor_static" })
	public void goods_floor_static(HttpServletRequest request,
			HttpServletResponse response, String gf_type) {
		Map<String, Object> params = Maps.newHashMap();
		params.put("gf_display", Boolean.valueOf(true));
		params.put("gf_type", Integer.valueOf(CommUtil.null2Int(gf_type)));
		params.put("parent", -1);
		
		List<GoodsFloor> floors = this.redPigGoodsfloorService.queryPageList(params);
		
		Properties p = new Properties();
		p.setProperty("file.resource.loader.path", request.getRealPath("/") + "vm" + File.separator);
		p.setProperty("input.encoding", "UTF-8");
		p.setProperty("output.encoding", "UTF-8");
		String floor_name = "floor";
		if (gf_type.equals("1")) {
			floor_name = "floor_wide";
		}
		try {
			Velocity.init(p);
			Template blank = Velocity.getTemplate(floor_name + ".vm", "UTF-8");
			int count = 1;
			for (GoodsFloor floor : floors) {
				VelocityContext context = new VelocityContext();
				context.put("floor", floor);
				context.put("obj", floor);
				context.put("velocityCount", Integer.valueOf(count));
				context.put("gf_tools", this.gf_tools);
				context.put("config", this.redPigSysConfigService.getSysConfig());
				context.put("webPath", CommUtil.getURL(request));
				if (this.redPigSysConfigService.getSysConfig().getImageWebServer() != null) {
					if (!this.redPigSysConfigService.getSysConfig().getImageWebServer()
							.equals("")) {
						context.put("imageWebServer", this.redPigSysConfigService
								.getSysConfig().getImageWebServer());
						break ;
					}
				}
				context.put("imageWebServer", CommUtil.getURL(request));

				StringWriter writer = new StringWriter();
				blank.merge(context, writer);

				String content = writer.toString();
				String path = ClusterSyncTools.getClusterRoot() + "statics";
				CommUtil.createFolder(path);
				PrintWriter pwrite = new PrintWriter(new OutputStreamWriter(
						new FileOutputStream(path + File.separator + floor_name
								+ "_" + floor.getId() + ".html", false),
						"UTF-8"));
				pwrite.print(content);
				pwrite.flush();
				pwrite.close();
				count++;
			}
			response.setContentType("text/plain");
			response.setHeader("Cache-Control", "no-cache");
			response.setCharacterEncoding("UTF-8");
			try {
				PrintWriter writer2 = response.getWriter();
				writer2.print("success");
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			response.setContentType("text/plain");
			response.setHeader("Cache-Control", "no-cache");
			response.setCharacterEncoding("UTF-8");
			try {
				PrintWriter writer2 = response.getWriter();
				writer2.print("error");
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
			e.printStackTrace();
		}
	}
	
	/**
	 * 楼层style2模板保存
	 * @param request
	 * @param response
	 * @param id
	 * @param type
	 * @param goods_id
	 * @param adv_id
	 * @param module_id
	 * @return
	 */
	@SecurityMapping(title = "楼层style2模板保存", value = "/goods_floor_style2_save*", rtype = "admin", rname = "首页楼层", rcode = "goods_floor", rgroup = "装修")
	@RequestMapping({ "/goods_floor_style2_save" })
	public String goods_floor_style2_save(
			HttpServletRequest request,
			HttpServletResponse response, 
			String id, 
			String type,
			String goods_id, 
			String adv_id, 
			String module_id) {

		GoodsFloor obj = this.redPigGoodsfloorService.selectByPrimaryKey(CommUtil.null2Long(id));

		List<Map> maps = Lists.newArrayList();

		if (!CommUtil.null2String(obj.getGf_style2_goods()).equals("")) {
			maps = JSON.parseArray(obj.getGf_style2_goods(), Map.class);
			boolean update = false;
			for (Map map : maps) {
				if (map.get("module_id").equals(module_id)) {
					Goods goods = this.redPigGoodsService.selectByPrimaryKey(CommUtil.null2Long(goods_id));

					if ((type.equals("goods")) && (goods != null)) {
						map.put("type", type);
						map.put("module_id", module_id);
						map.put("goods_id", goods_id);
						map.put("img_url", goods.getGoods_main_photo()
								.getPath()
								+ "/"
								+ goods.getGoods_main_photo().getName());
						map.put("goods_price", goods.getGoods_price());
						map.put("store_price", goods.getGoods_current_price());
						map.put("goods_name", goods.getGoods_name());
						map.put("href_url", CommUtil.getURL(request)
								+ "/goods_" + goods_id + "");
					}

					if (type.equals("img")) {
						String uploadFilePath = this.redPigSysConfigService.getSysConfig().getUploadFilePath();

						String saveFilePathName = request.getSession()
								.getServletContext().getRealPath("/")
								+ uploadFilePath + File.separator + "advert";

						Map map1 = Maps.newHashMap();
						try {
							map1 = CommUtil.saveFileToServer(request, "img",
									saveFilePathName, null, null);

							if (map1.get("fileName") != "") {
								map.put("type", type);
								map.put("module_id", module_id);
								map.put("goods_id", "");
								map.put("goods_price", "");
								map.put("store_price", "");
								map.put("goods_name", "");
								map.put("img_url",
										uploadFilePath
												+ "/advert/"
												+ CommUtil.null2String(map1
														.get("fileName")));
							}
						} catch (IOException e) {
							e.printStackTrace();
						}
						map.put("href_url", request.getParameter("adv_url"));
					}

					if (type.equals("adv")) {
						AdvertPosition ap = this.redPigAdvertPositionService.selectByPrimaryKey(CommUtil.null2Long(adv_id));
						
						String img_url = ap.getAp_acc().getPath() + "/"
								+ ap.getAp_acc().getName();
						String adv_url = CommUtil.getURL(request)
								+ "/advert_redirect?url=" + ap.getAp_acc_url()
								+ "&id=" + ap.getAdv_id();

						if ((ap.getAp_show_type() == 0)
								&& (ap.getAdvs().size() > 0)) {
							img_url =

							ap.getAdvs().get(0).getAd_acc()
									.getPath()
									+ "/"
									+ ap.getAdvs().get(0)
											.getAd_acc().getName();
							adv_url = CommUtil.getURL(request)
									+ "/advert_redirect?url="
									+ ap.getAdvs().get(0)
											.getAd_url() + "&id="
									+ ap.getAdvs().get(0).getId();
						}

						if ((ap.getAp_show_type() == 1)
								&& (ap.getAdvs().size() > 0)) {
							Random random = new Random();
							int i = random.nextInt(ap.getAdvs().size());
							img_url = ap.getAdvs().get(i)
									.getAd_acc().getPath()
									+ "/"
									+ ap.getAdvs().get(i)
											.getAd_acc().getName();
							adv_url = CommUtil.getURL(request)
									+ "/advert_redirect?url="
									+ ap.getAdvs().get(i)
											.getAd_url() + "&id="
									+ ap.getAdvs().get(i).getId();
						}

						map.put("type", type);
						map.put("module_id", module_id);
						map.put("goods_id", "");
						map.put("goods_price", "");
						map.put("store_price", "");
						map.put("goods_name", "");
						map.put("img_url", img_url);
						map.put("href_url", adv_url);
					}

					update = true;
				}
			}

			if (!update) {
				if (type.equals("goods")) {
					Map map = new HashMap();
					map.put("type", type);
					map.put("module_id", module_id);
					map.put("goods_id", goods_id);
					Goods goods = this.redPigGoodsService.selectByPrimaryKey(CommUtil.null2Long(goods_id));
					
					map.put("img_url", goods.getGoods_main_photo().getPath()
							+ "/" + goods.getGoods_main_photo().getName());
					map.put("goods_price", goods.getGoods_price());
					map.put("store_price", goods.getGoods_current_price());
					map.put("goods_name", goods.getGoods_name());
					map.put("href_url", CommUtil.getURL(request) + "/goods_"
							+ goods_id + "");
					maps.add(map);
				}
				if (type.equals("img")) {
					Map map = new HashMap();
					map.put("type", type);
					map.put("module_id", module_id);
					String uploadFilePath = this.redPigSysConfigService.getSysConfig().getUploadFilePath();
					
					String saveFilePathName = request.getSession()
							.getServletContext().getRealPath("/")
							+ uploadFilePath + File.separator + "advert";
					Map map1 = new HashMap();
					try {
						map1 = CommUtil.saveFileToServer(request, "img",
								saveFilePathName, null, null);
						if (map1.get("fileName") != "") {
							map.put("type", type);
							map.put("module_id", module_id);
							map.put("goods_id", "");
							map.put("goods_price", "");
							map.put("store_price", "");
							map.put("goods_name", "");
							map.put("img_url",
									uploadFilePath
											+ "/advert/"
											+ CommUtil.null2String(map1
													.get("fileName")));
							map.put("href_url", request.getParameter("adv_url"));
							maps.add(map);
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				if (type.equals("adv")) {
					Map map = new HashMap();
					AdvertPosition ap = this.redPigAdvertPositionService.selectByPrimaryKey(CommUtil.null2Long(adv_id));
					
					String img_url = ap.getAp_acc().getPath() + "/"
							+ ap.getAp_acc().getName();
					String adv_url = CommUtil.getURL(request)
							+ "/advert_redirect?url=" + ap.getAp_acc_url()
							+ "&id=" + ap.getAdv_id();
					if ((ap.getAp_show_type() == 0)
							&& (ap.getAdvs().size() > 0)) {
						img_url =

						ap.getAdvs().get(0).getAd_acc().getPath()
								+ "/"
								+ ap.getAdvs().get(0).getAd_acc()
										.getName();
						adv_url = CommUtil.getURL(request)
								+ "/advert_redirect?url="
								+ ap.getAdvs().get(0).getAd_url()
								+ "&id="
								+ ap.getAdvs().get(0).getId();
					}
					if ((ap.getAp_show_type() == 1)
							&& (ap.getAdvs().size() > 0)) {
						Random random = new Random();
						int i = random.nextInt(ap.getAdvs().size());
						img_url = ap.getAdvs().get(i).getAd_acc()
								.getPath()
								+ "/"
								+ ap.getAdvs().get(i).getAd_acc()
										.getName();
						adv_url = CommUtil.getURL(request)
								+ "/advert_redirect?url="
								+ ap.getAdvs().get(i).getAd_url()
								+ "&id="
								+ ap.getAdvs().get(i).getId();
					}
					map.put("type", type);
					map.put("module_id", module_id);
					map.put("goods_id", "");
					map.put("goods_price", "");
					map.put("store_price", "");
					map.put("goods_name", "");
					map.put("img_url", img_url);
					map.put("href_url", adv_url);
					maps.add(map);
				}
			}
			obj.setGf_style2_goods(JSON.toJSONString(maps));
		} else {
			Map map = new HashMap();
			map.put("type", type);
			map.put("module_id", module_id);
			Goods goods = this.redPigGoodsService.selectByPrimaryKey(CommUtil.null2Long(goods_id));
			
			if ((type.equals("goods")) && (goods != null)) {
				map.put("goods_id", goods_id);
				map.put("img_url", goods.getGoods_main_photo().getPath() + "/"
						+ goods.getGoods_main_photo().getName());
				map.put("goods_price", goods.getGoods_price());
				map.put("store_price", goods.getGoods_current_price());
				map.put("goods_name", goods.getGoods_name());
				map.put("href_url", CommUtil.getURL(request) + "/goods_"
						+ goods_id + "");
				maps.add(map);
			}
			if (type.equals("img")) {
				String uploadFilePath = this.redPigSysConfigService.getSysConfig()
						.getUploadFilePath();
				String saveFilePathName = request.getSession()
						.getServletContext().getRealPath("/")
						+ uploadFilePath + File.separator + "advert";
				Map map1 = new HashMap();
				try {
					map1 = CommUtil.saveFileToServer(request, "img",
							saveFilePathName, null, null);
					if (map1.get("fileName") != "") {
						map.put("goods_id", "");
						map.put("goods_price", "");
						map.put("store_price", "");
						map.put("goods_name", "");
						map.put("img_url", uploadFilePath + "/advert/"
								+ CommUtil.null2String(map1.get("fileName")));
						map.put("href_url", request.getParameter("adv_url"));
						maps.add(map);
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (type.equals("adv")) {
				AdvertPosition ap = this.redPigAdvertPositionService.selectByPrimaryKey(CommUtil.null2Long(adv_id));
				
				String img_url = ap.getAp_acc().getPath() + "/"
						+ ap.getAp_acc().getName();
				String adv_url = CommUtil.getURL(request)
						+ "/advert_redirect?url=" + ap.getAp_acc_url() + "&id="
						+ ap.getAdv_id();
				if ((ap.getAp_show_type() == 0) && (ap.getAdvs().size() > 0)) {
					img_url =

					ap.getAdvs().get(0).getAd_acc().getPath()
							+ "/"
							+ ap.getAdvs().get(0).getAd_acc()
									.getName();
					adv_url = CommUtil.getURL(request)
							+ "/advert_redirect?url="
							+ ap.getAdvs().get(0).getAd_url()
							+ "&id=" + ap.getAdvs().get(0).getId();
				}
				if ((ap.getAp_show_type() == 1) && (ap.getAdvs().size() > 0)) {
					Random random = new Random();
					int i = random.nextInt(ap.getAdvs().size());
					img_url = ((Advert) ap.getAdvs().get(i)).getAd_acc()
							.getPath()
							+ "/"
							+ ap.getAdvs().get(i).getAd_acc()
									.getName();
					adv_url = CommUtil.getURL(request)
							+ "/advert_redirect?url="
							+ ap.getAdvs().get(i).getAd_url()
							+ "&id=" + ap.getAdvs().get(i).getId();
				}
				map.put("goods_id", "");
				map.put("goods_price", "");
				map.put("store_price", "");
				map.put("goods_name", "");
				map.put("img_url", img_url);
				map.put("href_url", adv_url);
				maps.add(map);
			}
		}
		obj.setGf_style2_goods(JSON.toJSONString(maps));
		this.redPigGoodsfloorService.update(obj);
		return "redirect:goods_floor_template?id=" + obj.getParent().getId();
	}
	
	/**
	 * 楼层模板2右侧数据管理
	 * @param request
	 * @param response
	 * @param id
	 * @param index
	 * @param width
	 * @param height
	 * @param module_id
	 * @return
	 */
	@SecurityMapping(title = "楼层模板2右侧数据管理", value = "/goods_floor_style2*", rtype = "admin", rname = "首页楼层", rcode = "goods_floor", rgroup = "装修")
	@RequestMapping({ "/goods_floor_style2" })
	public ModelAndView goods_floor_style2(
			HttpServletRequest request,
			HttpServletResponse response, String id, String index,
			String width, String height, String module_id) {
		
		ModelAndView mv = new RedPigJModelAndView(
				"admin/blue/goods_floor_style2.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request, response);
		GoodsFloor obj = this.redPigGoodsfloorService.selectByPrimaryKey(CommUtil.null2Long(id));
		List<Map> maps = Lists.newArrayList();
		if (obj.getGf_style2_goods() != null) {
			maps = JSON.parseArray(obj.getGf_style2_goods(), Map.class);
		}
		Map obj_map = null;
		for (Map map : maps) {
			if (CommUtil.null2String(map.get("module_id")).equals(module_id)) {
				obj_map = map;
				break;
			}
		}
		List<AdvertPosition> aps = Lists.newArrayList();
		Map<String,Object> params = Maps.newHashMap();
		
		params.put("ap_status", Integer.valueOf(1));
		if ((CommUtil.null2Int(width) > 0) && (CommUtil.null2Int(height) > 0)) {
			params.put("ap_width",
					Integer.valueOf(CommUtil.null2Int(width)));
			params.put("ap_height",
					Integer.valueOf(CommUtil.null2Int(height)));
			params.put("ap_type", "img");
			aps = this.redPigAdvertPositionService.queryPageList(params);
			
		} else {
			params.put("ap_type", "img");
			params.put("ap_status", Integer.valueOf(1));
			aps = this.redPigAdvertPositionService.queryPageList(params);
			
		}
		
		params.clear();
		params.put("parent", -1);
		
		List<GoodsClass> gcs = this.redPigGoodsClassService.queryPageList(params);
		
		mv.addObject("obj_map", obj_map);
		mv.addObject("gcs", gcs);
		mv.addObject("width", width);
		mv.addObject("height", height);
		mv.addObject("aps", aps);
		mv.addObject("obj", obj);
		mv.addObject("module_id", module_id);
		mv.addObject("gf_tools", this.gf_tools);
		return mv;
	}
	
	/**
	 * 楼层ICON恢复初始
	 * @param request
	 * @param response
	 * @param id
	 * @throws IOException
	 */
	@SecurityMapping(title = "楼层ICON恢复初始", value = "/goods_floor_icon_restore*", rtype = "admin", rname = "首页楼层", rcode = "goods_floor", rgroup = "装修")
	@RequestMapping({ "/goods_floor_icon_restore" })
	public void restore_img(HttpServletRequest request,
			HttpServletResponse response, String id) throws IOException {
		Map<String, Object> map = Maps.newHashMap();
		GoodsFloor gf = this.redPigGoodsfloorService.selectByPrimaryKey(CommUtil
				.null2Long(id));
		if (gf.getIcon() != null) {
			Accessory icon = gf.getIcon();
			gf.setIcon(null);
			this.redPigGoodsfloorService.update(gf);
			this.redPigAccessoryService.delete(icon.getId());
		}
		map.put("path", CommUtil.getURL(request)
				+ "/resources/style/system/front/default/images/left_nav.png");
		
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		PrintWriter writer = response.getWriter();
		writer.print(JSON.toJSONString(map));
	}
	
	/**
	 * 楼层模板分类商品编辑
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param gc_id
	 * @param goods_name
	 * @param page
	 * @param module_id
	 * @param activity_name
	 * @return
	 */
	@SecurityMapping(title = "楼层模板分类商品编辑", value = "/redpig_goods_floor_list_goods_load*", rtype = "admin", rname = "首页楼层", rcode = "goods_floor", rgroup = "装修")
	@RequestMapping({ "/redpig_goods_floor_list_goods_load" })
	public ModelAndView redpig_goods_floor_list_goods_load(
			HttpServletRequest request, HttpServletResponse response,
			String currentPage, String gc_id, String goods_name, String page,
			String module_id, String activity_name) {
		ModelAndView mv = new RedPigJModelAndView(
				"admin/blue/redpig_goods_floor_list_goods_load.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request, response);
		
		if (!CommUtil.null2String(page).equals("")) {
			mv = new RedPigJModelAndView("admin/blue/" + page + ".html",
					this.redPigSysConfigService.getSysConfig(),
					this.redPigUserConfigService.getUserConfig(), 0, request,
					response);
		}
		
		Map<String,Object> maps = redPigQueryTools.getParams(currentPage, "addTime", "desc");
		
		if ((activity_name != null) && (!"".equals(activity_name))) {
			List<String> str_list = Lists.newArrayList();
			str_list.add(activity_name);
			this.redPigQueryTools.queryActivityGoods(maps, str_list);
		}
		if (!CommUtil.null2String(gc_id).equals("")) {
			Set<Long> ids = genericIds(this.redPigGoodsClassService.selectByPrimaryKey(CommUtil.null2Long(gc_id)));
			maps.put("redPig_ids", ids);
		}
		if (!CommUtil.null2String(goods_name).equals("")) {
			maps.put("redpig_goods_name", goods_name);
			
		}
		
		maps.put("goods_status", Integer.valueOf(0));
		
		IPageList pList = this.redPigGoodsService.list(maps);

		CommUtil.saveIPageList2ModelAndView(CommUtil.getURL(request)
				+ "/redpig_goods_floor_list_goods_load", "", "", pList,
				mv);
		mv.addObject("module_id", module_id);
		return mv;
	}
	
	/**
	 * 楼层模板分类商品编辑
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param gc_id
	 * @param goods_name
	 * @param page
	 * @param module_id
	 * @param activity_name
	 * @return
	 */
	@SecurityMapping(title = "楼层模板分类商品编辑", value = "/goods_floor_list_goods_load*", rtype = "admin", rname = "首页楼层", rcode = "goods_floor", rgroup = "装修")
	@RequestMapping({ "/goods_floor_list_goods_load" })
	public ModelAndView goods_floor_list_goods_load(
			HttpServletRequest request,
			HttpServletResponse response, 
			String currentPage, 
			String gc_id,
			String goods_name, 
			String page, 
			String module_id,
			String activity_name) 
	{
	
		ModelAndView mv = new RedPigJModelAndView(
				"admin/blue/goods_floor_list_goods_load.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request, response);
		if (!CommUtil.null2String(page).equals("")) {
			mv = new RedPigJModelAndView("admin/blue/" + page + ".html",
					this.redPigSysConfigService.getSysConfig(),
					this.redPigUserConfigService.getUserConfig(), 0, request,
					response);
		}
		
		Map<String,Object> maps = redPigQueryTools.getParams(currentPage, "addTime", "desc");
		
		if ((activity_name != null) && (!"".equals(activity_name))) {
			List<String> str_list = Lists.newArrayList();
			str_list.add(activity_name);
			this.redPigQueryTools.queryActivityGoods(maps, str_list);
		}
		
		if (!CommUtil.null2String(gc_id).equals("")) {
			Set<Long> ids = genericIds(this.redPigGoodsClassService.selectByPrimaryKey(CommUtil.null2Long(gc_id)));
			
			maps.put("gc_ids", ids);
			
		}
		if (!CommUtil.null2String(goods_name).equals("")) {
			
			maps.put("redpig_goods_name", goods_name);
			
		}
		
		IPageList pList = this.redPigGoodsService.list(maps);
		
		CommUtil.saveIPageList2ModelAndView(CommUtil.getURL(request)
				+ "/goods_floor_list_goods_load", "", "", pList, mv);
		mv.addObject("module_id", module_id);
		return mv;
	}
	
	private Set<Long> genericIds(GoodsClass gc) {
		Set<Long> ids = new HashSet();
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
	
	/**
	 * 楼层模板品牌加载
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param name
	 * @return
	 */
	@SecurityMapping(title = "楼层模板品牌加载", value = "/goods_floor_brand_load*", rtype = "admin", rname = "首页楼层", rcode = "goods_floor", rgroup = "装修")
	@RequestMapping({ "/goods_floor_brand_load" })
	public ModelAndView goods_floor_brand_load(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String name) {
		ModelAndView mv = new RedPigJModelAndView(
				"admin/blue/goods_floor_brand_load.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request, response);
		
		Map<String,Object> params = redPigQueryTools.getParams(currentPage, "sequence", "asc");
		params.put("audit", Integer.valueOf(1));
		if (!CommUtil.null2String(name).equals("")) {
			params.put("redPig_name", name.trim());
		}
		
		IPageList pList = this.redPigGoodsBrandService.list(params);
		CommUtil.saveIPageList2ModelAndView(CommUtil.getURL(request)
				+ "/goods_floor_brand_load.html", "",
				"&name=" + CommUtil.null2String(name), pList, mv);
		return mv;
	}
	
	
	/**
	 * 楼层模板品牌保存
	 * @param request
	 * @param response
	 * @param id
	 * @param ids
	 * @return
	 */
	@SecurityMapping(title = "楼层模板品牌保存", value = "/goods_floor_brand_save*", rtype = "admin", rname = "首页楼层", rcode = "goods_floor", rgroup = "装修")
	@RequestMapping({ "/goods_floor_brand_save" })
	public String goods_floor_brand_save(HttpServletRequest request,
			HttpServletResponse response, String id, String ids) {
		GoodsFloor obj = this.redPigGoodsfloorService.selectByPrimaryKey(CommUtil.null2Long(id));
		String[] id_list = ids.split(",");
		Map<String, Object> map = Maps.newHashMap();
		for (int i = 0; i < id_list.length; i++) {
			if (!id_list[i].equals("")) {
				map.put("brand_id" + i, id_list[i]);
			}
		}
		obj.setGf_brand_list(JSON.toJSONString(map));
		this.redPigGoodsfloorService.update(obj);
		return "redirect:goods_floor_template?id=" + obj.getId();
	}
		
	/**
	 * 楼层模板品牌编辑
	 * @param request
	 * @param response
	 * @param id
	 * @return
	 */
	@SecurityMapping(title = "楼层模板品牌编辑", value = "/goods_floor_brand*", rtype = "admin", rname = "首页楼层", rcode = "goods_floor", rgroup = "装修")
	@RequestMapping({ "/goods_floor_brand" })
	public ModelAndView goods_floor_brand(HttpServletRequest request,
			HttpServletResponse response, String id) {
		ModelAndView mv = new RedPigJModelAndView(
				"admin/blue/goods_floor_brand.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request, response);
		GoodsFloor obj = this.redPigGoodsfloorService.selectByPrimaryKey(CommUtil.null2Long(id));
		
		Map<String,Object> maps = redPigQueryTools.getParams("1", "sequence", "asc");
		maps.put("audit", Integer.valueOf(1));
		
		IPageList pList = this.redPigGoodsBrandService.list(maps);
		CommUtil.saveIPageList2ModelAndView(CommUtil.getURL(request)
				+ "/goods_floor_brand_load.html", "", "", pList, mv);
		mv.addObject("obj", obj);
		mv.addObject("gf_tools", this.gf_tools);
		return mv;
	}
	
	
	/**
	 * 楼层模板右下方广告保存
	 * @param request
	 * @param response
	 * @param type
	 * @param id
	 * @param adv_url
	 * @param adv_id
	 * @return
	 */
	@SecurityMapping(title = "楼层模板右下方广告保存", value = "/goods_floor_right_adv_save*", rtype = "admin", rname = "首页楼层", rcode = "goods_floor", rgroup = "装修")
	@RequestMapping({ "/goods_floor_right_adv_save" })
	public String goods_floor_right_adv_save(HttpServletRequest request,
			HttpServletResponse response, String type, String id,
			String adv_url, String adv_id) {
		GoodsFloor obj = this.redPigGoodsfloorService.selectByPrimaryKey(CommUtil.null2Long(id));
		Map<String, Object> map = Maps.newHashMap();
		if (type.equals("user")) {
			String uploadFilePath = this.redPigSysConfigService.getSysConfig()
					.getUploadFilePath();
			String saveFilePathName = request.getSession().getServletContext()
					.getRealPath("/")
					+ uploadFilePath + File.separator + "advert";
			Map json_map = Maps.newHashMap();
			try {
				map = CommUtil.saveFileToServer(request, "img",
						saveFilePathName, "", null);
				if (map.get("fileName") != "") {
					Accessory acc = new Accessory();
					acc.setName(CommUtil.null2String(map.get("fileName")));
					acc.setExt(CommUtil.null2String(map.get("mime")));
					acc.setSize(BigDecimal.valueOf(CommUtil.null2Double(map
							.get("fileSize"))));
					acc.setPath(uploadFilePath + "/advert");
					acc.setWidth(CommUtil.null2Int(map.get("width")));
					acc.setHeight(CommUtil.null2Int(map.get("height")));
					acc.setAddTime(new Date());
					this.redPigAccessoryService.save(acc);
					json_map.put("acc_id", acc.getId());
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			json_map.put("acc_url", adv_url);
			json_map.put("adv_id", "");
			obj.setGf_right_adv(JSON.toJSONString(json_map));
		}
		if (type.equals("adv")) {
			Map json_map = Maps.newHashMap();
			json_map.put("acc_id", "");
			json_map.put("acc_url", "");
			json_map.put("adv_id", adv_id);
			obj.setGf_right_adv(JSON.toJSONString(json_map));
		}
		this.redPigGoodsfloorService.update(obj);
		return "redirect:goods_floor_template?id=" + obj.getId();
	}
	
	/**
	 * 楼层模板右下方广告编辑
	 * @param request
	 * @param response
	 * @param id
	 * @return
	 */
	@SecurityMapping(title = "楼层模板右下方广告编辑", value = "/goods_floor_right_adv*", rtype = "admin", rname = "首页楼层", rcode = "goods_floor", rgroup = "装修")
	@RequestMapping({ "/goods_floor_right_adv" })
	public ModelAndView goods_floor_right_adv(HttpServletRequest request,
			HttpServletResponse response, String id) {
		ModelAndView mv = new RedPigJModelAndView(
				"admin/blue/goods_floor_right_adv.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request, response);
		GoodsFloor obj = this.redPigGoodsfloorService.selectByPrimaryKey(CommUtil.null2Long(id));
		
		Map<String, Object> params = Maps.newHashMap();
		params.put("ap_status", Integer.valueOf(1));
		params.put("ap_width", Integer.valueOf(205));
		params.put("ap_type", "img");
		List<AdvertPosition> aps = this.redPigAdvertPositionService.queryPageList(params);

		mv.addObject("aps", aps);
		mv.addObject("obj", obj);
		mv.addObject("gf_tools", this.gf_tools);
		return mv;
	}
	
	
	
	/**
	 * 楼层模板左下方广告保存
	 * @param request
	 * @param response
	 * @param type
	 * @param id
	 * @param adv_url
	 * @param adv_id
	 * @return
	 */
	@SecurityMapping(title = "楼层模板左下方广告保存", value = "/redpig_goods_floor_left_adv_save*", rtype = "admin", rname = "首页楼层", rcode = "goods_floor", rgroup = "装修")
	@RequestMapping({ "/redpig_goods_floor_left_adv_save" })
	public String redpig_goods_floor_left_adv_save(HttpServletRequest request,
			HttpServletResponse response, String type, String id,
			String adv_url, String adv_id) {
		GoodsFloor obj = this.redPigGoodsfloorService.selectByPrimaryKey(CommUtil.null2Long(id));
		
		Map<String, Object> map = Maps.newHashMap();
		if (type.equals("user")) {
			String uploadFilePath = this.redPigSysConfigService.getSysConfig()
					.getUploadFilePath();
			String saveFilePathName = request.getSession().getServletContext()
					.getRealPath("/")
					+ uploadFilePath + File.separator + "advert";
			Map json_map = JSON.parseObject(obj.getGf_left_adv());
			if (json_map == null) {
				json_map = Maps.newHashMap();
			}
			try {
				map = CommUtil.saveFileToServer(request, "img",
						saveFilePathName, "", null);
				if (map.get("fileName") != "") {
					Accessory acc = new Accessory();
					acc.setName(CommUtil.null2String(map.get("fileName")));
					acc.setExt(CommUtil.null2String(map.get("mime")));
					acc.setSize(BigDecimal.valueOf(CommUtil.null2Double(map
							.get("fileSize"))));
					acc.setPath(uploadFilePath + "/advert");
					acc.setWidth(CommUtil.null2Int(map.get("width")));
					acc.setHeight(CommUtil.null2Int(map.get("height")));
					acc.setAddTime(new Date());
					this.redPigAccessoryService.save(acc);
					json_map.put("acc_id", acc.getId());
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			if (StringUtils.isNotBlank(adv_url)) {
				json_map.put("acc_url", adv_url);
			}
			json_map.put("adv_id", "");
			obj.setGf_left_adv(JSON.toJSONString(json_map));
		}
		if (type.equals("adv")) {
			Map json_map = Maps.newHashMap();
			json_map.put("acc_id", "");
			json_map.put("acc_url", "");
			json_map.put("adv_id", adv_id);
			obj.setGf_left_adv(JSON.toJSONString(json_map));
		}
		this.redPigGoodsfloorService.update(obj);
		return "redirect:goods_floor_template?id=" + obj.getId() + "&gf_type=2";
	}	
	
	
	
	/**
	 * 楼层模板左下方广告保存
	 * @param request
	 * @param response
	 * @param type
	 * @param id
	 * @param adv_url
	 * @param adv_id
	 * @return
	 */
	@SecurityMapping(title = "楼层模板左下方广告保存", value = "/goods_floor_left_adv_save*", rtype = "admin", rname = "首页楼层", rcode = "goods_floor", rgroup = "装修")
	@RequestMapping({ "/goods_floor_left_adv_save" })
	public String goods_floor_left_adv_save(HttpServletRequest request,
			HttpServletResponse response, String type, String id,
			String adv_url, String adv_id) {
		GoodsFloor obj = this.redPigGoodsfloorService.selectByPrimaryKey(CommUtil.null2Long(id));
		
		Map<String, Object> map = Maps.newHashMap();
		if (type.equals("user")) {
			String uploadFilePath = this.redPigSysConfigService.getSysConfig()
					.getUploadFilePath();
			String saveFilePathName = request.getSession().getServletContext()
					.getRealPath("/")
					+ uploadFilePath + File.separator + "advert";
			Map json_map = JSON.parseObject(obj.getGf_left_adv());
			try {
				map = CommUtil.saveFileToServer(request, "img",saveFilePathName, "", null);
				if (map.get("fileName") != "") {
					Accessory acc = new Accessory();
					acc.setName(CommUtil.null2String(map.get("fileName")));
					acc.setExt(CommUtil.null2String(map.get("mime")));
					acc.setSize(BigDecimal.valueOf(CommUtil.null2Double(map.get("fileSize"))));
					acc.setPath(uploadFilePath + "/advert");
					acc.setWidth(CommUtil.null2Int(map.get("width")));
					acc.setHeight(CommUtil.null2Int(map.get("height")));
					acc.setAddTime(new Date());
					this.redPigAccessoryService.save(acc);
					if(json_map==null){
						json_map = Maps.newHashMap();
						json_map.put("acc_id", acc.getId());
					}else{
						json_map.put("acc_id", acc.getId());
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			if (StringUtils.isNotBlank(adv_url)) {
				json_map.put("acc_url", adv_url);
			}
			json_map.put("adv_id", "");
			obj.setGf_left_adv(JSON.toJSONString(json_map));
		}
		if (type.equals("adv")) {
			Map json_map = Maps.newHashMap();
			json_map.put("acc_id", "");
			json_map.put("acc_url", "");
			json_map.put("adv_id", adv_id);
			obj.setGf_left_adv(JSON.toJSONString(json_map));
		}
		this.redPigGoodsfloorService.update(obj);
		
		return "redirect:goods_floor_template?id=" + obj.getId();
	}
	
	
	/**
	 * 楼层模板左下方广告编辑
	 * @param request
	 * @param response
	 * @param id
	 * @return
	 */
	@SecurityMapping(title = "楼层模板左下方广告编辑", value = "/redpig_goods_floor_left_adv*", rtype = "admin", rname = "首页楼层", rcode = "goods_floor", rgroup = "装修")
	@RequestMapping({ "/redpig_goods_floor_left_adv" })
	public ModelAndView redpig_goods_floor_left_adv(HttpServletRequest request,
			HttpServletResponse response, String id) {
		ModelAndView mv = new RedPigJModelAndView(
				"admin/blue/redpig_goods_floor_left_adv.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request, response);
		
		GoodsFloor obj = this.redPigGoodsfloorService.selectByPrimaryKey(CommUtil.null2Long(id));
		
		Map<String, Object> map = Maps.newHashMap();
		if ((obj != null) && (obj.getGf_left_adv() != null)
				&& (!"".equals(obj.getGf_left_adv()))) {
			map = (Map) JSON.parseObject(obj.getGf_left_adv(), Map.class);
			if (("".equals(CommUtil.null2String(map.get("adv_id"))))
					&& (!"".equals(CommUtil.null2String(map.get("acc_id"))))) {
				Accessory acc = this.redPigAccessoryService.selectByPrimaryKey(CommUtil.null2Long(map.get("acc_id")));
				map.put("acc_img", acc.getPath() + "/" + acc.getName());
			}
		}
		mv.addObject("obj_map", map);
		Map<String, Object> params = Maps.newHashMap();
		params.put("ap_status", Integer.valueOf(1));
		params.put("ap_width", Integer.valueOf(156));
		params.put("ap_type", "img");
		List<AdvertPosition> aps = this.redPigAdvertPositionService.queryPageList(params);
		

		mv.addObject("aps", aps);
		mv.addObject("obj", obj);
		mv.addObject("gf_tools", this.gf_tools);
		return mv;
	}
	
	
	/**
	 * 楼层模板左下方广告编辑
	 * @param request
	 * @param response
	 * @param id
	 * @return
	 */
	@SecurityMapping(title = "楼层模板左下方广告编辑", value = "/goods_floor_left_adv*", rtype = "admin", rname = "首页楼层", rcode = "goods_floor", rgroup = "装修")
	@RequestMapping({ "/goods_floor_left_adv" })
	public ModelAndView goods_floor_left_adv(HttpServletRequest request,
			HttpServletResponse response, String id) {
		ModelAndView mv = new RedPigJModelAndView(
				"admin/blue/goods_floor_left_adv.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request, response);
		
		GoodsFloor obj = this.redPigGoodsfloorService.selectByPrimaryKey(CommUtil.null2Long(id));
		
		Map<String, Object> map = Maps.newHashMap();
		if ((obj != null) && (obj.getGf_left_adv() != null)
				&& (!"".equals(obj.getGf_left_adv()))) {
			map = (Map) JSON.parseObject(obj.getGf_left_adv(), Map.class);
			if (("".equals(CommUtil.null2String(map.get("adv_id"))))
					&& (!"".equals(CommUtil.null2String(map.get("acc_id"))))) {
				Accessory acc = this.redPigAccessoryService.selectByPrimaryKey(CommUtil.null2Long(map.get("acc_id")));
				
				map.put("acc_img", acc.getPath() + "/" + acc.getName());
			}
		}
		mv.addObject("obj_map", map);
		Map<String, Object> params = Maps.newHashMap();
		params.put("ap_status", Integer.valueOf(1));
		params.put("ap_width", Integer.valueOf(156));
		params.put("ap_type", "img");
		List<AdvertPosition> aps = this.redPigAdvertPositionService.queryPageList(params);
		
		mv.addObject("aps", aps);
		mv.addObject("obj", obj);
		mv.addObject("gf_tools", this.gf_tools);
		return mv;
	}
	
	/**
	 * 楼层模板右侧商品列表保存
	 * @param request
	 * @param response
	 * @param list_title
	 * @param id
	 * @param ids
	 * @return
	 */
	@SecurityMapping(title = "楼层模板右侧商品列表保存", value = "/redpig_gf_list_goods_banner*", rtype = "admin", rname = "首页楼层", rcode = "goods_floor", rgroup = "装修")
	@RequestMapping({ "/redpig_gf_list_goods_banner" })
	public String redpig_gf_list_goods_banner(HttpServletRequest request,
			HttpServletResponse response, String list_title, String id,
			String ids) {
		GoodsFloor obj = this.redPigGoodsfloorService.selectByPrimaryKey(CommUtil.null2Long(id));
		

		String[] id_list = ids.split(",");
		Map<String, Object> map = Maps.newHashMap();
		map.put("list_title", list_title);
		for (int i = 0; i < id_list.length; i++) {
			if (!id_list[i].equals("")) {
				map.put("goods_id" + i, id_list[i]);
			}
		}

		obj.setGf_list_goods(JSON.toJSONString(map));
		this.redPigGoodsfloorService.update(obj);
		return "redirect:goods_floor_template?id=" + obj.getId() + "&gf_type=2";
	}
	
	/**
	 * 楼层模板右侧商品列表保存
	 * @param request
	 * @param response
	 * @param list_title
	 * @param id
	 * @param ids
	 * @return
	 */
	@SecurityMapping(title = "楼层模板右侧商品列表保存", value = "/redpig_goods_floor_list_goods_save*", rtype = "admin", rname = "首页楼层", rcode = "goods_floor", rgroup = "装修")
	@RequestMapping({ "/redpig_goods_floor_list_goods_save" })
	public String redpig_goods_floor_list_goods_save(
			HttpServletRequest request, HttpServletResponse response,
			String list_title, String id, String ids) {
		GoodsFloor obj = this.redPigGoodsfloorService.selectByPrimaryKey(CommUtil.null2Long(id));
		
		String[] id_list = ids.split(",");
		Map<String, Object> map = Maps.newHashMap();
		map.put("list_title", list_title);
		for (int i = 0; i < id_list.length; i++) {
			if (!id_list[i].equals("")) {
				map.put("goods_id" + i, id_list[i]);
			}
		}
		obj.setGf_list_goods(JSON.toJSONString(map));
		this.redPigGoodsfloorService.update(obj);
		return "redirect:goods_floor_template?id=" + obj.getId() + "&gf_type=2";
	}
	
	/**
	 * 楼层模板右侧商品列表保存
	 * @param request
	 * @param response
	 * @param list_title
	 * @param id
	 * @param ids
	 * @return
	 */
	@SecurityMapping(title = "楼层模板右侧商品列表保存", value = "/goods_floor_list_goods_save*", rtype = "admin", rname = "首页楼层", rcode = "goods_floor", rgroup = "装修")
	@RequestMapping({ "/goods_floor_list_goods_save" })
	public String goods_floor_list_goods_save(HttpServletRequest request,
			HttpServletResponse response, String list_title, String id,
			String ids) {
		GoodsFloor obj = this.redPigGoodsfloorService.selectByPrimaryKey(CommUtil.null2Long(id));
		
		String[] id_list = ids.split(",");
		Map<String, Object> map = Maps.newHashMap();
		map.put("list_title", list_title);
		for (int i = 0; i < id_list.length; i++) {
			if (!id_list[i].equals("")) {
				map.put("goods_id" + i, id_list[i]);
			}
		}
		obj.setGf_list_goods(JSON.toJSONString(map));
		this.redPigGoodsfloorService.update(obj);
		return "redirect:goods_floor_template?id=" + obj.getId();
	}
	
	/**
	 * 楼层模板右侧商品列表编辑
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param id
	 * @return
	 */
	@SecurityMapping(title = "楼层模板右侧商品列表编辑", value = "/redpig_goods_floor_list_items*", rtype = "admin", rname = "首页楼层", rcode = "goods_floor", rgroup = "装修")
	@RequestMapping({ "/redpig_goods_floor_list_items" })
	public ModelAndView redpig_goods_floor_list_items(
			HttpServletRequest request, HttpServletResponse response,
			String currentPage, String id) {
		ModelAndView mv = new RedPigJModelAndView(
				"admin/blue/redpig_goods_floor_list_goods.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request, response);
		
		GoodsFloor obj = this.redPigGoodsfloorService.selectByPrimaryKey(CommUtil.null2Long(id));
		
		Map<String,Object> params = Maps.newHashMap();
		params.put("parent", -1);
		
		List<GoodsClass> gcs = this.redPigGoodsClassService.queryPageList(params);
		mv.addObject("gcs", gcs);
		mv.addObject("obj", obj);
		mv.addObject("gf_tools", this.gf_tools);
		mv.addObject("currentPage", currentPage);
		return mv;
	}	
	
	
	/**
	 * 楼层模板右侧商品列表编辑
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param id
	 * @return
	 */
	@SecurityMapping(title = "楼层模板右侧商品列表编辑", value = "/goods_floor_list_items*", rtype = "admin", rname = "首页楼层", rcode = "goods_floor", rgroup = "装修")
	@RequestMapping({ "/goods_floor_list_items" })
	public ModelAndView goods_floor_list_items(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String id) {
		ModelAndView mv = new RedPigJModelAndView(
				"admin/blue/goods_floor_list_goods.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request, response);
		
		GoodsFloor obj = this.redPigGoodsfloorService.selectByPrimaryKey(CommUtil.null2Long(id));
		
		
		Map<String,Object> params = Maps.newHashMap();
		params.put("parent", -1);
		
		List<GoodsClass> gcs = this.redPigGoodsClassService.queryPageList(params);
		
		
		mv.addObject("gcs", gcs);
		mv.addObject("obj", obj);
		mv.addObject("gf_tools", this.gf_tools);
		mv.addObject("currentPage", currentPage);
		return mv;
	}
	
	
	/**
	 * 楼层模板分类商品保存
	 * @param request
	 * @param response
	 * @param gf_name
	 * @param id
	 * @param ids
	 * @return
	 */
	@SecurityMapping(title = "楼层模板分类商品保存", value = "/redpig_goods_floor_gc_goods_save*", rtype = "admin", rname = "首页楼层", rcode = "goods_floor", rgroup = "装修")
	@RequestMapping({ "/redpig_goods_floor_gc_goods_save" })
	public String redpig_goods_floor_gc_goods_save(HttpServletRequest request,
			HttpServletResponse response, String gf_name, String id, String ids) {
		GoodsFloor obj = this.redPigGoodsfloorService.selectByPrimaryKey(CommUtil.null2Long(id));
		obj.setGf_name(gf_name);
		String[] id_list = ids.split(",");
		Map<String, Object> map = Maps.newHashMap();
		for (int i = 0; i < id_list.length; i++) {
			if (!id_list[i].equals("")) {
				map.put("goods_id" + i, id_list[i]);
			}
		}
		obj.setGf_gc_goods(JSON.toJSONString(map));
		this.redPigGoodsfloorService.update(obj);
		return "redirect:goods_floor_template?id=" + obj.getParent().getId()
				+ "&tab=" + id + "&gf_type=2";
	}
	
	/**
	 * 楼层模板分类商品保
	 * @param request
	 * @param response
	 * @param gf_name
	 * @param id
	 * @param ids
	 * @return
	 */
	@SecurityMapping(title = "楼层模板分类商品保存", value = "/goods_floor_gc_goods_save*", rtype = "admin", rname = "首页楼层", rcode = "goods_floor", rgroup = "装修")
	@RequestMapping({ "/goods_floor_gc_goods_save" })
	public String goods_floor_gc_goods_save(HttpServletRequest request,
			HttpServletResponse response, String gf_name, String id, String ids) {
		GoodsFloor obj = this.redPigGoodsfloorService.selectByPrimaryKey(CommUtil.null2Long(id));
		obj.setGf_name(gf_name);
		String[] id_list = ids.split(",");
		Map<String, Object> map = Maps.newHashMap();
		for (int i = 0; i < id_list.length; i++) {
			if (!id_list[i].equals("")) {
				map.put("goods_id" + i, id_list[i]);
			}
		}
		obj.setGf_gc_goods(JSON.toJSONString(map));
		this.redPigGoodsfloorService.update(obj);
		return "redirect:goods_floor_template?id=" + obj.getParent().getId()
				+ "&tab=" + id;
	}
	
	/**
	 * 楼层模板分类商品编辑
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param id
	 * @return
	 */
	@SecurityMapping(title = "楼层模板分类商品编辑", value = "/redpig_goods_floor_gc_items*", rtype = "admin", rname = "首页楼层", rcode = "goods_floor", rgroup = "装修")
	@RequestMapping({ "/redpig_goods_floor_gc_items" })
	public ModelAndView redpig_goods_floor_gc_items(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String id) {
		ModelAndView mv = new RedPigJModelAndView(
				"admin/blue/redpig_goods_floor_gc_goods.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request, response);
		
		GoodsFloor obj = this.redPigGoodsfloorService.selectByPrimaryKey(CommUtil.null2Long(id));
		
		Map<String,Object> params = Maps.newHashMap();
		params.put("parent", -1);
		params.put("orderBy", "sequence");
		params.put("orderType", "desc");
		List<GoodsClass> gcs = this.redPigGoodsClassService.queryPageList(params);
		
		mv.addObject("gcs", gcs);
		mv.addObject("obj", obj);
		mv.addObject("gf_tools", this.gf_tools);
		mv.addObject("currentPage", currentPage);
		return mv;
	}
	
	
	/**
	 * 楼层模板分类商品编辑
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param id
	 * @return
	 */
	@SecurityMapping(title = "楼层模板分类商品编辑", value = "/goods_floor_gc_items*", rtype = "admin", rname = "首页楼层", rcode = "goods_floor", rgroup = "装修")
	@RequestMapping({ "/goods_floor_gc_items" })
	public ModelAndView goods_floor_gc_items(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String id) {
		ModelAndView mv = new RedPigJModelAndView(
				"admin/blue/goods_floor_gc_goods.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request, response);
		GoodsFloor obj = this.redPigGoodsfloorService.selectByPrimaryKey(CommUtil.null2Long(id));
		
		Map<String,Object> params = Maps.newHashMap();
		params.put("parent", -1);
		
		List<GoodsClass> gcs = this.redPigGoodsClassService.queryPageList(params);
		
		mv.addObject("gcs", gcs);
		mv.addObject("obj", obj);
		mv.addObject("gf_tools", this.gf_tools);
		mv.addObject("currentPage", currentPage);
		return mv;
	}
	
	/**
	 * 楼层模板商品分类保存
	 * @param request
	 * @param response
	 * @param id
	 * @param ids
	 * @param gf_name
	 * @return
	 */
	@SecurityMapping(title = "楼层模板商品分类保存", value = "/redpig_goods_floor_class_save*", rtype = "admin", rname = "首页楼层", rcode = "goods_floor", rgroup = "装修")
	@RequestMapping({ "/redpig_goods_floor_class_save" })
	public String redpig_goods_floor_class_save(HttpServletRequest request,
			HttpServletResponse response, String id, String ids, String gf_name) {
		GoodsFloor obj = this.redPigGoodsfloorService.selectByPrimaryKey(CommUtil.null2Long(id));
		
		obj.setGf_name(gf_name);
		List<Map<String, Object>> gf_gc_list = Lists.newArrayList();
		String[] id_list = ids.split(",pid:");

		for (String t_id : id_list) {

			String[] c_id_list = t_id.split(",");
			Map<String, Object> map = Maps.newHashMap();
			for (int i1 = 0; i1 < c_id_list.length; i1++) {
				String c_id = c_id_list[i1];
				if (c_id.indexOf("cid") < 0) {
					map.put("pid", c_id);
				} else {
					map.put("gc_id" + i1, c_id.substring(4));
				}
			}
			map.put("gc_count", Integer.valueOf(c_id_list.length - 1));
			if (!map.get("pid").toString().equals("")) {
				gf_gc_list.add(map);
			}
		}
		obj.setGf_gc_list(JSON.toJSONString(gf_gc_list));
		this.redPigGoodsfloorService.update(obj);
		return "redirect:goods_floor_template?id=" + id + "&gf_type=2";
	}
	
	/**
	 * 楼层模板商品分类保存
	 * @param request
	 * @param response
	 * @param id
	 * @param ids
	 * @param gf_name
	 * @return
	 */
	@SecurityMapping(title = "楼层模板商品分类保存", value = "/goods_floor_class_save*", rtype = "admin", rname = "首页楼层", rcode = "goods_floor", rgroup = "装修")
	@RequestMapping({ "/goods_floor_class_save" })
	public String goods_floor_class_save(
			HttpServletRequest request,
			HttpServletResponse response, 
			String id, 
			String ids, 
			String gf_name) 
	{
		
		GoodsFloor obj = this.redPigGoodsfloorService.selectByPrimaryKey(CommUtil.null2Long(id));
		
		obj.setGf_name(gf_name);
		List gf_gc_list = Lists.newArrayList();
		String[] id_list = ids.split(",pid:");

		for (String t_id : id_list) {

			String[] c_id_list = t_id.split(",");
			Map<String, Object> map = Maps.newHashMap();
			for (int i1 = 0; i1 < c_id_list.length; i1++) {
				String c_id = c_id_list[i1];
				if (c_id.indexOf("cid") < 0) {
					map.put("pid", c_id);
				} else {
					map.put("gc_id" + i1, c_id.substring(4));
				}
			}
			map.put("gc_count", Integer.valueOf(c_id_list.length - 1));
			if (!map.get("pid").toString().equals("")) {
				gf_gc_list.add(map);
			}
		}
		obj.setGf_gc_list(JSON.toJSONString(gf_gc_list));
		this.redPigGoodsfloorService.update(obj);
		return "redirect:goods_floor_template?id=" + id;
	}
	
	/**
	 * 楼层模板商品分类加载
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param gc_id
	 * @return
	 */
	@SecurityMapping(title = "楼层模板商品分类加载", value = "/goods_floor_class_load*", rtype = "admin", rname = "首页楼层", rcode = "goods_floor", rgroup = "装修")
	@RequestMapping({ "/goods_floor_class_load" })
	public ModelAndView goods_floor_class_load(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String gc_id) {
		ModelAndView mv = new RedPigJModelAndView(
				"admin/blue/goods_floor_class_load.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request, response);
		GoodsClass gc = this.redPigGoodsClassService.selectByPrimaryKey(CommUtil.null2Long(gc_id));
		mv.addObject("gc", gc);
		return mv;
	}
	
	/**
	 * 楼层模板商品分类编辑
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param id
	 * @return
	 */
	@SecurityMapping(title = "楼层模板商品分类编辑", value = "/redpig_goods_floor_class*", rtype = "admin", rname = "首页楼层", rcode = "goods_floor", rgroup = "装修")
	@RequestMapping({ "/redpig_goods_floor_class" })
	public ModelAndView redpig_goods_floor_class(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String id) {
		ModelAndView mv = new RedPigJModelAndView(
				"admin/blue/redpig_goods_floor_class.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request, response);
		GoodsFloor obj = this.redPigGoodsfloorService.selectByPrimaryKey(CommUtil.null2Long(id));
		
		Map<String,Object> params = Maps.newHashMap();
		params.put("parent", -1);
		
		List<GoodsClass> gcs = this.redPigGoodsClassService.queryPageList(params);
		
		mv.addObject("gcs", gcs);
		mv.addObject("obj", obj);
		mv.addObject("gf_tools", this.gf_tools);
		mv.addObject("currentPage", currentPage);
		return mv;
	}
	
	/**
	 * 楼层模板商品分类编辑
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param id
	 * @return
	 */
	@SecurityMapping(title = "楼层模板商品分类编辑", value = "/goods_floor_class*", rtype = "admin", rname = "首页楼层", rcode = "goods_floor", rgroup = "装修")
	@RequestMapping({ "/goods_floor_class" })
	public ModelAndView goods_floor_class(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String id) {
		ModelAndView mv = new RedPigJModelAndView(
				"admin/blue/goods_floor_class.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request, response);
		
		GoodsFloor obj = this.redPigGoodsfloorService.selectByPrimaryKey(CommUtil.null2Long(id));
		
		Map<String,Object> params = Maps.newHashMap();
		params.put("parent", -1);
		
		List<GoodsClass> gcs = this.redPigGoodsClassService.queryPageList(params);
		
		mv.addObject("gcs", gcs);
		mv.addObject("obj", obj);
		mv.addObject("gf_tools", this.gf_tools);
		mv.addObject("currentPage", currentPage);
		return mv;
	}
	
	/**
	 * 楼层模板编辑
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param wide_template
	 * @param pos
	 * @param gf_id
	 * @param type
	 * @param adp_id
	 * @param adv_url
	 * @param gf_name
	 * @param ids
	 * @param brand_ids
	 * @return
	 */
	@SecurityMapping(title = "楼层模板编辑", value = "/goods_floor_wide_template_save*", rtype = "admin", rname = "首页楼层", rcode = "goods_floor", rgroup = "装修")
	@RequestMapping({ "/goods_floor_wide_template_save" })
	public String goods_floor_wide_template_save(
			HttpServletRequest request,
			HttpServletResponse response, 
			String currentPage,
			String wide_template, 
			String pos, 
			String gf_id, 
			String type,
			String adp_id, 
			String adv_url, 
			String gf_name, 
			String ids,
			String brand_ids) 
	{
		if ((adv_url != null) && (!adv_url.equals(""))
				&& (adv_url.indexOf("http://") < 0)) {
			adv_url = "http://" + adv_url;
		}
		String uploadFilePath = this.redPigSysConfigService.getSysConfig()
				.getUploadFilePath();
		String saveFilePathName = request.getSession().getServletContext()
				.getRealPath("/")
				+ uploadFilePath + File.separator + "goodsfloor";
		GoodsFloor gf = this.redPigGoodsfloorService.selectByPrimaryKey(CommUtil.null2Long(gf_id));
		
		List<Map> maps = Lists.newArrayList();
		Map target_map = null;
		if ((wide_template.equals("wide_adv_rectangle_four"))
				|| (wide_template.equals("wide_adv_five"))
				|| (wide_template.equals("wide_adv_square_four"))
				|| (wide_template.equals("wide_adv_eight"))
				|| (wide_template.equals("wide_adv_brand"))) {
			Accessory img = null;
			String source_json = null;
			if (wide_template.equals("wide_adv_rectangle_four")) {
				source_json = gf.getWide_adv_rectangle_four();
			}
			if (wide_template.equals("wide_adv_five")) {
				source_json = gf.getWide_adv_five();
			}
			if (wide_template.equals("wide_adv_square_four")) {
				source_json = gf.getWide_adv_square_four();
			}
			if (wide_template.equals("wide_adv_eight")) {
				source_json = gf.getWide_adv_eight();
			}
			if (wide_template.equals("wide_adv_brand")) {
				source_json = gf.getWide_adv_brand();
			}
			if (source_json != null) {
				maps = JSON.parseArray(source_json, Map.class);
				for (Map obj : maps) {
					if (CommUtil.null2String(obj.get("pos")).equals(pos)) {
						target_map = obj;
						break;
					}
				}
			}
			if (target_map == null) {
				target_map = Maps.newHashMap();
				maps.add(target_map);
			}
			if (target_map.get("id") != null) {
				img = this.redPigAccessoryService.selectByPrimaryKey(CommUtil.null2Long(target_map.get("id")));
			}
			if ((wide_template.equals("wide_adv_brand")) && (pos.equals("2"))) {
				String[] id_list = brand_ids.split(",");
				target_map.clear();
				for (int i = 0; i < id_list.length; i++) {
					if (!id_list[i].equals("")) {
						target_map.put("brand_id" + i, id_list[i]);
					}
				}
				target_map.put("pos", pos);
			} else {
				if (type.equals("img")) {
					Map upload_map = Maps.newHashMap();
					try {
						String fileName = img == null ? "" : img.getName();
						upload_map = CommUtil.saveFileToServer(request, "img",
								saveFilePathName, fileName, null);
						if (fileName.equals("")) {
							if (upload_map.get("fileName") != "") {
								if (img == null) {
									img = new Accessory();
								}
								img.setName(CommUtil.null2String(upload_map
										.get("fileName")));
								img.setExt((String) upload_map.get("mime"));
								img.setSize(BigDecimal.valueOf(CommUtil
										.null2Double(upload_map.get("fileSize"))));
								img.setPath(uploadFilePath + "/goodsfloor");
								img.setWidth(CommUtil.null2Int(upload_map
										.get("width")));
								img.setHeight(CommUtil.null2Int(upload_map
										.get("heigh")));
								img.setAddTime(new Date());
								this.redPigAccessoryService.save(img);
							}
						} else if (upload_map.get("fileName") != "") {
							img.setName(CommUtil.null2String(upload_map
									.get("fileName")));
							img.setExt((String) upload_map.get("mime"));
							img.setSize(BigDecimal.valueOf(CommUtil
									.null2Double(upload_map.get("fileSize"))));
							img.setPath(uploadFilePath + "/goodsfloor");
							img.setWidth(CommUtil.null2Int(upload_map
									.get("width")));
							img.setHeight(CommUtil.null2Int(upload_map
									.get("heigh")));
							img.setAddTime(new Date());
							this.redPigAccessoryService.update(img);
						}
						target_map.put("id", img.getId());
						target_map.put("src",
								img.getPath() + "/" + img.getName());
						target_map.put("url", adv_url);
						target_map.put("type", type);
						target_map.put("pos",
								Integer.valueOf(CommUtil.null2Int(pos)));
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				if (type.equals("adv")) {
					AdvertPosition ap = this.redPigAdvertPositionService.selectByPrimaryKey(CommUtil.null2Long(adp_id));
					
					String img_url = ap.getAp_acc().getPath() + "/"
							+ ap.getAp_acc().getName();
					String adv_url2 = CommUtil.getURL(request)
							+ "/advert_redirect?url=" + ap.getAp_acc_url()
							+ "&id=" + ap.getAdv_id();
					if ((ap.getAp_show_type() == 0)
							&& (ap.getAdvs().size() > 0)) {
						img_url =

						ap.getAdvs().get(0).getAd_acc().getPath()
								+ "/"
								+ ((Advert) ap.getAdvs().get(0)).getAd_acc()
										.getName();
						adv_url2 = CommUtil.getURL(request)
								+ "/advert_redirect?url="
								+ (ap.getAdvs().get(0)).getAd_url()
								+ "&id="
								+ ap.getAdvs().get(0).getId();
					}
					if ((ap.getAp_show_type() == 1)
							&& (ap.getAdvs().size() > 0)) {
						Random random = new Random();
						int i = random.nextInt(ap.getAdvs().size());
						img_url = ((Advert) ap.getAdvs().get(i)).getAd_acc()
								.getPath()
								+ "/"
								+ ap.getAdvs().get(i).getAd_acc()
										.getName();
						adv_url2 = CommUtil.getURL(request)
								+ "/advert_redirect?url="
								+ ap.getAdvs().get(i).getAd_url()
								+ "&id="
								+ ap.getAdvs().get(i).getId();
					}
					target_map.put("id", "");
					target_map.put("src", img_url);
					target_map.put("url", adv_url2);
					target_map.put("type", type);
					target_map.put("pos",
							Integer.valueOf(CommUtil.null2Int(pos)));
				}
			}
			String json = JSON.toJSONString(maps);
			if (wide_template.equals("wide_adv_rectangle_four")) {
				gf.setWide_adv_rectangle_four(json);
			}
			if (wide_template.equals("wide_adv_five")) {
				gf.setWide_adv_five(json);
			}
			if (wide_template.equals("wide_adv_square_four")) {
				gf.setWide_adv_square_four(json);
			}
			if (wide_template.equals("wide_adv_eight")) {
				gf.setWide_adv_eight(json);
			}
			if (wide_template.equals("wide_adv_brand")) {
				gf.setWide_adv_brand(json);
			}
		}
		if (wide_template.equals("wide_goods")) {
			gf.setGf_name(gf_name);
			String[] id_list = ids.split(",");
			Map<String, Object> map = Maps.newHashMap();
			for (int i = 0; i < id_list.length; i++) {
				if (!id_list[i].equals("")) {
					map.put("goods_id" + i, id_list[i]);
				}
			}
			gf.setWide_goods(JSON.toJSONString(map));
		}
		this.redPigGoodsfloorService.update(gf);
		
		return "redirect:/goods_floor_template?id=" + gf_id
				+ "&gf_type=1";
	}
	
	/**
	 * 楼层模板编辑
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param wide_template
	 * @param pos
	 * @param gf_id
	 * @return
	 */
	@SecurityMapping(title = "楼层模板编辑", value = "/goods_floor_wide_template_edit*", rtype = "admin", rname = "首页楼层", rcode = "goods_floor", rgroup = "装修")
	@RequestMapping({ "/goods_floor_wide_template_edit" })
	public ModelAndView goods_floor_wide_template_edit(
			HttpServletRequest request, 
			HttpServletResponse response,
			String currentPage, 
			String wide_template, 
			String pos, 
			String gf_id) 
	{
	
		ModelAndView mv = new RedPigJModelAndView(
				"admin/blue/goods_floor_wide_template_edit.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request, response);
		
		GoodsFloor obj = this.redPigGoodsfloorService.selectByPrimaryKey(CommUtil.null2Long(gf_id));
		
		wide_template = obj.getWide_template();
		if ((wide_template.equals("wide_adv_rectangle_four"))
				|| (wide_template.equals("wide_adv_eight"))
				|| (wide_template.equals("wide_adv_five"))
				|| (wide_template.equals("wide_adv_square_four"))
				|| (wide_template.equals("wide_adv_brand"))) {
			String source_json = null;
			Map<String, Object> params = Maps.newHashMap();
			if (wide_template.equals("wide_adv_rectangle_four")) {
				params.put("ap_width", Integer.valueOf(400));
				params.put("ap_height", Integer.valueOf(200));
				source_json = obj.getWide_adv_rectangle_four();
			}
			if (wide_template.equals("wide_adv_five")) {
				params.put("ap_width", Integer.valueOf(315));
				params.put("ap_height", Integer.valueOf(446));
				source_json = obj.getWide_adv_five();
			}
			if (wide_template.equals("wide_adv_square_four")) {
				params.put("ap_width", Integer.valueOf(395));
				params.put("ap_height", Integer.valueOf(395));
				source_json = obj.getWide_adv_square_four();
			}
			if (wide_template.equals("wide_adv_eight")) {
				params.put("ap_width", Integer.valueOf(395));
				params.put("ap_height", Integer.valueOf(90));
				source_json = obj.getWide_adv_eight();
			}
			if (wide_template.equals("wide_adv_brand")) {
				params.put("ap_width", Integer.valueOf(243));
				params.put("ap_height", Integer.valueOf(419));
				source_json = obj.getWide_adv_brand();
				mv.addObject("gf_tools", this.gf_tools);
			}
			params.put("ap_status", Integer.valueOf(1));
			List<AdvertPosition> aps = this.redPigAdvertPositionService.queryPageList(params);
			
			mv.addObject("aps", aps);
			Map target = this.gf_tools.generic_gf_wide_template(source_json,wide_template, pos);
			if (target != null) {
				mv.addObject("obj_map", target);
			}
		}
		
		if (wide_template.equals("wide_goods")) {
			Map<String,Object> params = Maps.newHashMap();
			params.put("parent", -1);
			
			List<GoodsClass> gcs = this.redPigGoodsClassService.queryPageList(params);
			
			mv.addObject("gcs", gcs);
			mv.addObject("gf_tools", this.gf_tools);
		}
		mv.addObject("obj", obj);
		mv.addObject("wide_template", wide_template);
		mv.addObject("gf_id", gf_id);
		mv.addObject("pos", pos);
		return mv;
	}
	
	
	/**
	 * 楼层模板编辑
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param id
	 * @param tab
	 * @param gf_type
	 * @return
	 */
	@SecurityMapping(title = "楼层模板编辑", value = "/goods_floor_template*", rtype = "admin", rname = "首页楼层", rcode = "goods_floor", rgroup = "装修")
	@RequestMapping({ "/goods_floor_template" })
	public ModelAndView goods_floor_template(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String id,
			String tab, String gf_type) {
		SysConfig sysConfig = this.redPigSysConfigService.getSysConfig();
		// 默认老版本的模板
		ModelAndView mv = new RedPigJModelAndView(
				"admin/blue/goods_floor_template.html", sysConfig,
				this.redPigUserConfigService.getUserConfig(), 0, request, response);
		GoodsFloor obj = this.redPigGoodsfloorService.selectByPrimaryKey(CommUtil.null2Long(id));
		
		mv.addObject("gf_type", gf_type);
		mv.addObject("obj", obj);
		String url = sysConfig.getImageWebServer();
		String gf_left_adv = obj.getGf_left_adv();
		
		String template = this.gf_tools.generic_adv(url, gf_left_adv);
		mv.addObject("advTemplate", template);
		mv.addObject("gf_tools", this.gf_tools);
		mv.addObject("currentPage", currentPage);
		mv.addObject("tab", tab);
		// mv.addObject("url", CommUtil.getURL(request));
		mv.addObject("url", sysConfig.getImageWebServer());

		return mv;
	}
	
	/**
	 * 楼层分类下级加载
	 * @param request
	 * @param response
	 * @param pid
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "楼层分类下级加载", value = "/goods_floor_data*", rtype = "admin", rname = "首页楼层", rcode = "goods_floor", rgroup = "装修")
	@RequestMapping({ "/goods_floor_data" })
	public ModelAndView goods_floor_data(HttpServletRequest request,
			HttpServletResponse response, String pid, String currentPage) {
		ModelAndView mv = new RedPigJModelAndView("admin/blue/goods_floor_data.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request, response);
		
		Map<String, Object> map = Maps.newHashMap();
		map.put("parent_id", CommUtil.null2Long(pid));
		
		List<GoodsFloor> gfs = this.redPigGoodsfloorService.queryPageList(map);
		
		
		mv.addObject("gfs", gfs);
		mv.addObject("currentPage", currentPage);
		return mv;
	}
	
	
	/**
	 * 楼层分类Ajax更新
	 * @param request
	 * @param response
	 * @param id
	 * @param fieldName
	 * @param value
	 * @throws ClassNotFoundException
	 */
	@SecurityMapping(title = "楼层分类Ajax更新", value = "/goods_floor_ajax*", rtype = "admin", rname = "首页楼层", rcode = "goods_floor", rgroup = "装修")
	@RequestMapping({ "/goods_floor_ajax" })
	public void goods_floor_ajax(HttpServletRequest request,
			HttpServletResponse response, String id, String fieldName,
			String value) throws ClassNotFoundException {
		GoodsFloor obj = this.redPigGoodsfloorService.selectByPrimaryKey(Long.valueOf(id));
		
		Field[] fields = GoodsFloor.class.getDeclaredFields();
		
		Object val = ObjectUtils.setValue(fieldName, value, obj, fields);
		
		this.redPigGoodsfloorService.update(obj);
		
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
	 * 楼层分类删除
	 * @param request
	 * @param response
	 * @param mulitId
	 * @param currentPage
	 * @param gf_type
	 * @return
	 */
	@SecurityMapping(title = "楼层分类删除", value = "/goods_floor_del*", rtype = "admin", rname = "首页楼层", rcode = "goods_floor", rgroup = "装修")
	@RequestMapping({ "/goods_floor_del" })
	public String goods_floor_del(HttpServletRequest request,
			HttpServletResponse response, String mulitId, String currentPage,
			String gf_type) {
		String[] ids = mulitId.split(",");
		for (String id : ids) {
			if (!id.equals("")) {
				this.redPigGoodsfloorService.delete(Long.valueOf(id));
			}
		}
		
		
		
		String url = "redirect:goods_floor_list?currentPage=" + currentPage;
		if ((gf_type != null) && (gf_type.equals("1"))) {
			url = "redirect:goods_floor_list?currentPage=" + currentPage
					+ "&gf_type=" + gf_type;
		}
		return url;
	}
	
	/**
	 * 楼层分类保存
	 * @param request
	 * @param response
	 * @param id
	 * @param currentPage
	 * @param pid
	 * @param list_url
	 * @param add_url
	 * @param gf_type
	 * @return
	 */
	@SecurityMapping(title = "楼层分类保存", value = "/redpig_goods_floor_save*", rtype = "admin", rname = "首页楼层", rcode = "goods_floor", rgroup = "装修")
	@RequestMapping({ "/redpig_goods_floor_save" })
	public ModelAndView redpig_goods_floor_save(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage,
			String pid, String list_url, String add_url, String gf_type) {

		GoodsFloor goodsfloor = null;
		if (id.equals("")) {
			goodsfloor = (GoodsFloor) WebForm.toPo(request, GoodsFloor.class);
			goodsfloor.setAddTime(new Date());
		} else {
			GoodsFloor obj = this.redPigGoodsfloorService.selectByPrimaryKey(Long.parseLong(id));
			
			goodsfloor = (GoodsFloor) WebForm.toPo(request, obj);
		}
		GoodsFloor parent = this.redPigGoodsfloorService.selectByPrimaryKey(CommUtil.null2Long(pid));
		
		if (parent != null) {
			goodsfloor.setParent(parent);
			goodsfloor.setGf_level(parent.getGf_level() + 1);
		}
		String uploadFilePath = this.redPigSysConfigService.getSysConfig()
				.getUploadFilePath();
		String saveFilePathName = request.getSession().getServletContext()
				.getRealPath("/")
				+ uploadFilePath + File.separator + "floor";
		Map<String, Object> map = Maps.newHashMap();
		try {
			String fileName = goodsfloor.getIcon() == null ? "" : goodsfloor
					.getIcon().getName();
			map = CommUtil.saveFileToServer(request, "icon_logo",
					saveFilePathName, fileName, null);
			if (fileName.equals("")) {
				if (map.get("fileName") != "") {
					Accessory icon = new Accessory();
					icon.setName(CommUtil.null2String(map.get("fileName")));
					icon.setExt((String) map.get("mime"));
					icon.setSize(BigDecimal.valueOf(CommUtil.null2Double(map
							.get("fileSize"))));
					icon.setPath(uploadFilePath + "/floor");
					icon.setWidth(((Integer) map.get("width")).intValue());
					icon.setHeight(((Integer) map.get("height")).intValue());
					icon.setAddTime(new Date());
					this.redPigAccessoryService.save(icon);
					goodsfloor.setIcon(icon);
				}
			} else if (map.get("fileName") != "") {
				Accessory icon = goodsfloor.getIcon();
				icon.setName(CommUtil.null2String(map.get("fileName")));
				icon.setExt(CommUtil.null2String(map.get("mime")));
				icon.setSize(BigDecimal.valueOf(CommUtil.null2Double(map
						.get("fileSize"))));
				icon.setPath(uploadFilePath + "/floor");
				icon.setWidth(CommUtil.null2Int(map.get("width")));
				icon.setHeight(CommUtil.null2Int(map.get("height")));
				this.redPigAccessoryService.update(icon);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		goodsfloor.setGf_type(CommUtil.null2Int(gf_type));
		if (id.equals("")) {
			this.redPigGoodsfloorService.save(goodsfloor);
		} else {
			this.redPigGoodsfloorService.update(goodsfloor);
		}
		ModelAndView mv = new RedPigJModelAndView("admin/blue/success.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request, response);
		
		mv.addObject("list_url", list_url + "?gf_type=" + gf_type);
		mv.addObject("op_title", "保存首页楼层成功");
		if (add_url != null) {
			mv.addObject("add_url", add_url + "?currentPage=" + currentPage
					+ "&gf_type=" + gf_type);
		}
		return mv;
	}
	
	/**
	 * 楼层分类保存
	 * @param request
	 * @param response
	 * @param id
	 * @param currentPage
	 * @param pid
	 * @param list_url
	 * @param add_url
	 * @param gf_type
	 * @return
	 */
	@SecurityMapping(title = "楼层分类保存", value = "/goods_floor_save*", rtype = "admin", rname = "首页楼层", rcode = "goods_floor", rgroup = "装修")
	@RequestMapping({ "/goods_floor_save" })
	public ModelAndView goods_floor_save(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage,
			String pid, String list_url, String add_url, String gf_type) {

		GoodsFloor goodsfloor = null;
		if (id.equals("")) {
			goodsfloor = (GoodsFloor) WebForm.toPo(request, GoodsFloor.class);
			goodsfloor.setAddTime(new Date());
		} else {
			GoodsFloor obj = this.redPigGoodsfloorService.selectByPrimaryKey(Long.valueOf(Long.parseLong(id)));
			
			goodsfloor = (GoodsFloor) WebForm.toPo(request, obj);
		}
		GoodsFloor parent = this.redPigGoodsfloorService.selectByPrimaryKey(CommUtil.null2Long(pid));
		
		if (parent != null) {
			goodsfloor.setParent(parent);
			goodsfloor.setGf_level(parent.getGf_level() + 1);
		}
		String uploadFilePath = this.redPigSysConfigService.getSysConfig()
				.getUploadFilePath();
		String saveFilePathName = request.getSession().getServletContext()
				.getRealPath("/")
				+ uploadFilePath + File.separator + "floor";
		Map<String, Object> map = Maps.newHashMap();
		try {
			String fileName = goodsfloor.getIcon() == null ? "" : goodsfloor
					.getIcon().getName();
			map = CommUtil.saveFileToServer(request, "icon_logo",
					saveFilePathName, fileName, null);
			if (fileName.equals("")) {
				if (map.get("fileName") != "") {
					Accessory icon = new Accessory();
					icon.setName(CommUtil.null2String(map.get("fileName")));
					icon.setExt((String) map.get("mime"));
					icon.setSize(BigDecimal.valueOf(CommUtil.null2Double(map
							.get("fileSize"))));
					icon.setPath(uploadFilePath + "/floor");
					icon.setWidth(((Integer) map.get("width")).intValue());
					icon.setHeight(((Integer) map.get("height")).intValue());
					icon.setAddTime(new Date());
					this.redPigAccessoryService.save(icon);
					goodsfloor.setIcon(icon);
				}
			} else if (map.get("fileName") != "") {
				Accessory icon = goodsfloor.getIcon();
				icon.setName(CommUtil.null2String(map.get("fileName")));
				icon.setExt(CommUtil.null2String(map.get("mime")));
				icon.setSize(BigDecimal.valueOf(CommUtil.null2Double(map
						.get("fileSize"))));
				icon.setPath(uploadFilePath + "/floor");
				icon.setWidth(CommUtil.null2Int(map.get("width")));
				icon.setHeight(CommUtil.null2Int(map.get("height")));
				this.redPigAccessoryService.update(icon);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		goodsfloor.setGf_type(CommUtil.null2Int(gf_type));
		if (id.equals("")) {
			this.redPigGoodsfloorService.save(goodsfloor);
		} else {
			this.redPigGoodsfloorService.update(goodsfloor);
		}
		ModelAndView mv = new RedPigJModelAndView("admin/blue/success.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request, response);
		
		mv.addObject("list_url", list_url + "?gf_type=" + gf_type);
		mv.addObject("op_title", "保存首页楼层成功");
		if (add_url != null) {
			mv.addObject("add_url", add_url + "?currentPage=" + currentPage
					+ "&gf_type=" + gf_type);
		}
		return mv;
	}
	
	/**
	 * 生成首页楼层静态文件
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping({ "/generate_floor_static" })
	public ModelAndView generate_floor_static(HttpServletRequest request,HttpServletResponse response) {
		
		ModelAndView mv = new RedPigJModelAndView("admin/blue/success.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request, response);
		try {
			HttpRequester req = new HttpRequester();
			req.setDefaultContentEncoding("utf-8");
			//先调用删除
			req.sendGet(this.redPigSysConfigService.getSysConfig().getIndexUrl() + "/generate_static_files");
			req.sendGet(this.redPigSysConfigService.getSysConfig().getIndexUrl() + "/index");
		} catch (Exception e) {
			mv = new RedPigJModelAndView("admin/blue/error.html",
					this.redPigSysConfigService.getSysConfig(),
					this.redPigUserConfigService.getUserConfig(), 0, request, response);
			
			String list_url="/goods_floor_list";
			mv.addObject("list_url", list_url);
			mv.addObject("op_title", "首页楼层静态文件生成失败!");
		}
		
			
		String list_url="/goods_floor_list";
		mv.addObject("list_url", list_url);
		mv.addObject("op_title", "首页楼层静态文件生成成功!");
		
		
		return mv;
	}
	
	/**
	 * 
	 * @param request
	 * @param response
	 * @param id
	 * @param currentPage
	 * @param gf_type
	 * @return
	 */
	@SecurityMapping(title = "楼层分类编辑", value = "/goods_floor_edit*", rtype = "admin", rname = "首页楼层", rcode = "goods_floor", rgroup = "装修")
	@RequestMapping({ "/goods_floor_edit" })
	public ModelAndView goods_floor_edit(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage,
			String gf_type) {
		ModelAndView mv = new RedPigJModelAndView("admin/blue/goods_floor_add.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request, response);
		
		if ((gf_type != null) && (gf_type.equals("1"))) {
			mv = new RedPigJModelAndView("admin/blue/goods_floor_wide_add.html",
					this.redPigSysConfigService.getSysConfig(),
					this.redPigUserConfigService.getUserConfig(), 0, request,
					response);
		}

		if ((gf_type != null) && (gf_type.equals("2"))) {
			mv = new RedPigJModelAndView("admin/blue/goods_floor_jd_add.html",
					this.redPigSysConfigService.getSysConfig(),
					this.redPigUserConfigService.getUserConfig(), 0, request,
					response);
		}

		if ((id != null) && (!id.equals(""))) {
			GoodsFloor goodsfloor = this.redPigGoodsfloorService.selectByPrimaryKey(Long.valueOf(Long.parseLong(id)));
			
			Map<String, Object> params = Maps.newHashMap();
			params.put("gf_level", Integer.valueOf(0));
			List<GoodsFloor> gfs = this.redPigGoodsfloorService.queryPageList(params);
			
			mv.addObject("gfs", gfs);
			mv.addObject("obj", goodsfloor);
			mv.addObject("currentPage", currentPage);
			mv.addObject("edit", Boolean.valueOf(true));
		}
		return mv;
	}
	
	/**
	 * 楼层分类添加
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param pid
	 * @param gf_type
	 * @return
	 */
	@SecurityMapping(title = "楼层分类添加", value = "/goods_floor_add*", rtype = "admin", rname = "首页楼层", rcode = "goods_floor", rgroup = "装修")
	@RequestMapping({ "/goods_floor_add" })
	public ModelAndView goods_floor_add(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String pid,
			String gf_type) {
		ModelAndView mv = new RedPigJModelAndView("admin/blue/goods_floor_add.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request, response);
		
		if (com.redpigmall.api.tools.StringUtils.isNotBlank(gf_type)
				&& "1".equals(gf_type.trim())) {
			mv = new RedPigJModelAndView("admin/blue/goods_floor_wide_add.html",
					this.redPigSysConfigService.getSysConfig(),
					this.redPigUserConfigService.getUserConfig(), 0, request,
					response);
		}

		if (com.redpigmall.api.tools.StringUtils.isNotBlank(gf_type)
				&& "2".equals(gf_type.trim())) {
			mv = new RedPigJModelAndView("admin/blue/goods_floor_jd_add.html",
					this.redPigSysConfigService.getSysConfig(),
					this.redPigUserConfigService.getUserConfig(), 0, request,
					response);
		}

		mv.addObject("currentPage", currentPage);
		Map<String, Object> params = Maps.newHashMap();
		params.put("gf_level", Integer.valueOf(0));
		List<GoodsFloor> gfs = this.redPigGoodsfloorService.queryPageList(params);
		
		mv.addObject("gfs", gfs);
		GoodsFloor obj = new GoodsFloor();
		GoodsFloor parent = this.redPigGoodsfloorService.selectByPrimaryKey(CommUtil.null2Long(pid));
		
		obj.setParent(parent);
		if (parent != null) {
			obj.setGf_level(parent.getGf_level() + 1);
		}
		obj.setGf_display(true);
		mv.addObject("obj", obj);
		mv.addObject("gf_type", gf_type);
		return mv;
	}
	
	/**
	 * 
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @param gf_type
	 * @return
	 */
	@SecurityMapping(title = "楼层分类列表", value = "/goods_floor_list*", rtype = "admin", rname = "首页楼层", rcode = "goods_floor", rgroup = "装修")
	@RequestMapping({ "/goods_floor_list" })
	public ModelAndView goods_floor_list(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,
			String orderType, String gf_type) {
		ModelAndView mv = new RedPigJModelAndView("admin/blue/goods_floor_list.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request, response);
		
		Map<String,Object> maps = redPigQueryTools.getParams(currentPage, "gf_sequence", "asc");
		
		if ((gf_type != null) && (gf_type.equals("1"))) {
			mv = new RedPigJModelAndView("admin/blue/goods_floor_wide_list.html",
					this.redPigSysConfigService.getSysConfig(),
					this.redPigUserConfigService.getUserConfig(), 0, request,
					response);
		}
		
		if ((gf_type != null) && (!gf_type.equals(""))) {

			maps.put("gf_type", Integer.valueOf(CommUtil.null2Int(gf_type)));
			
			mv.addObject("gf_type", gf_type);
		} else {
			

			maps.put("gf_type", 0);
		}
		
		maps.put("gf_level", 0);
		
		IPageList pList = this.redPigGoodsfloorService.list(maps);
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		return mv;
	}
	
	/**
	 * 首页管理设置保存
	 * @param request
	 * @param response
	 * @param index_type
	 */
	@SecurityMapping(title = "首页管理设置保存", value = "/set_index_ajax*", rtype = "admin", rname = "首页管理", rcode = "index_manage_admin", rgroup = "装修")
	@RequestMapping({ "/set_index_ajax" })
	public void set_index_ajax(
			HttpServletRequest request,
			HttpServletResponse response, 
			String index_type) {
		
		SysConfig sc = this.redPigSysConfigService.getSysConfig();
		sc.setIndex_type(CommUtil.null2Int(index_type));
		this.redPigSysConfigService.update(sc);
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		
		try {
			Map<String, Object> map = Maps.newHashMap();
			map.put("result", Integer.valueOf(CommUtil.null2Int(index_type)));
			PrintWriter writer = response.getWriter();
			writer.print(JSON.toJSONString(map));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 首页管理
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@SecurityMapping(title = "首页管理", value = "/set_index_setting*", rtype = "admin", rname = "首页管理", rcode = "index_manage_admin", rgroup = "装修")
	@RequestMapping({ "/set_index_setting" })
	public ModelAndView set_index_setting(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new RedPigJModelAndView(
				"admin/blue/set_index_setting.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request,
				response);
		SysConfig config = this.redPigSysConfigService.getSysConfig();
		mv.addObject("config", config);
		return mv;
	}
}
