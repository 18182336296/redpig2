package com.redpigmall.manage.admin.action.decorate;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.parser.Feature;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.redpigmall.api.annotation.SecurityMapping;
import com.redpigmall.api.mv.RedPigJModelAndView;
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.api.tools.CommUtil;
import com.redpigmall.api.tools.ObjectUtils;
import com.redpigmall.api.tools.WebForm;
import com.redpigmall.manage.admin.action.base.BaseAction;
import com.redpigmall.manage.admin.tools.RedPigChannelTools;
import com.redpigmall.domain.Accessory;
import com.redpigmall.domain.Activity;
import com.redpigmall.domain.Channel;
import com.redpigmall.domain.ChannelFloor;
import com.redpigmall.domain.Goods;
import com.redpigmall.domain.GoodsClass;
import com.redpigmall.service.RedPigAccessoryService;
import com.redpigmall.service.RedPigActivityGoodsService;
import com.redpigmall.service.RedPigActivityService;
import com.redpigmall.service.RedPigChannelFloorService;
import com.redpigmall.service.RedPigChannelService;
import com.redpigmall.service.RedPigGoodsClassService;
import com.redpigmall.view.web.tools.RedPigQueryUtils;

/**
 * 
 * <p>
 * Title: ChannelFloorManageAction.java
 * </p>
 * 
 * <p>
 * Description:频道楼层管理类
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2014
 * </p>
 * 
 * <p>
 * Company: www.redpigmall.net
 * </p>
 * 
 * @author redpig
 * 
 * @date 2014-5-21
 * 
 * @version redpigmall_b2b2c v8.0 2016版
 */
@SuppressWarnings({ "unchecked", "rawtypes", "unused" })
@Controller
public class RedPigChannelFloorManageAction extends BaseAction{
	
	
	/**
	 * 频道楼层列表
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @param channel_id
	 * @param type
	 * @return
	 */
	@SecurityMapping(title = "频道楼层列表", value = "/channelfloor_list*", rtype = "admin", rname = "频道楼层", rcode = "channel_floor", rgroup = "装修")
	@RequestMapping({ "/channelfloor_list" })
	public ModelAndView list(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,
			String orderType, String channel_id, String type) {
		ModelAndView mv = new RedPigJModelAndView(
				"admin/blue/channelfloor_list.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		if ((type != null) && (!"".equals(type))) {
			mv = new RedPigJModelAndView("admin/blue/channelfloor_list_load.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request,
					response);
		}
		String url = this.configService.getSysConfig().getAddress();
		String params = "";
		
		
		Map<String,Object> maps= this.redPigQueryTools.getParams(currentPage, orderBy, orderType);
        maps.put("orderBy", "cf_sequence");
        maps.put("orderType", "asc");

		
		Channel channel = this.channelService.selectByPrimaryKey(CommUtil
				.null2Long(channel_id));
		
		if (channel != null) {
			mv.addObject("channel_id", channel.getId());
			maps.put("cf_ch_id", channel.getId());
			
		} else {
			
			maps.put("cf_ch_id", CommUtil.null2Long(""));
		}
		
		IPageList pList = this.channelfloorService.queryPagesWithNoRelations(maps);
		
		if ((url == null) || (url.equals(""))) {
			url = CommUtil.getURL(request)
					+ "/channelfloor_list?channel_id=" + channel_id;
		}
		CommUtil.saveIPageList2ModelAndView(url
				+ "/channelfloor_list?channel_id=" + channel_id, "",
				params, pList, mv);
		mv.addObject(
				"floorshowPageFormHtml",
				floorshowPageFormHtml(url, CommUtil.null2Int(currentPage),
						Integer.valueOf(pList.getPages()).intValue(), Integer
								.valueOf(pList.getPageSize()).intValue()));
		return mv;
	}
	
	/**
	 * 二级频道楼层添加
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param channel_id
	 * @return
	 */
	@SecurityMapping(title = "二级频道楼层添加", value = "/channelfloor_add*", rtype = "admin", rname = "频道楼层", rcode = "channel_floor", rgroup = "装修")
	@RequestMapping({ "/channelfloor_add" })
	public ModelAndView add(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String channel_id) {
		ModelAndView mv = new RedPigJModelAndView("admin/blue/channelfloor_add.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		mv.addObject("currentPage", currentPage);
		mv.addObject("channel_id", channel_id);
		return mv;
	}
	
	/**
	 * 频道楼层编辑
	 * @param request
	 * @param response
	 * @param id
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "频道楼层编辑", value = "/channelfloor_edit*", rtype = "admin", rname = "频道楼层", rcode = "channel_floor", rgroup = "装修")
	@RequestMapping({ "/channelfloor_edit" })
	public ModelAndView edit(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage) {
		ModelAndView mv = new RedPigJModelAndView("admin/blue/channelfloor_add.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		
		if ((id != null) && (!id.equals(""))) {
			ChannelFloor channelfloor = this.channelfloorService
					.selectByPrimaryKey(Long.valueOf(Long.parseLong(id)));
			mv.addObject("channel_id", channelfloor.getCf_ch_id());
			mv.addObject("obj", channelfloor);
			mv.addObject("currentPage", currentPage);
			mv.addObject("edit", Boolean.valueOf(true));
		}
		return mv;
	}
	
	/**
	 * 频道楼层保存
	 * @param request
	 * @param response
	 * @param id
	 * @param currentPage
	 * @param list_url
	 * @param add_url
	 * @param channel_id
	 * @param cf_type
	 * @return
	 */
	@SecurityMapping(title = "频道楼层保存", value = "/channelfloor_save*", rtype = "admin", rname = "频道楼层", rcode = "channel_floor", rgroup = "装修")
	@RequestMapping({ "/channelfloor_save" })
	public ModelAndView saveEntity(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage,
			String list_url, String add_url, String channel_id, String cf_type) {
		
		ChannelFloor channelfloor = null;
		if (id.equals("")) {
			channelfloor = (ChannelFloor) WebForm.toPo(request,
					ChannelFloor.class);
			channelfloor.setCf_type(CommUtil.null2Int(cf_type));
			channelfloor.setAddTime(new Date());
		} else {
			ChannelFloor obj = this.channelfloorService.selectByPrimaryKey(Long
					.valueOf(Long.parseLong(id)));
			channelfloor = (ChannelFloor) WebForm.toPo(request, obj);
		}
		
		if (id.equals("")) {
			channelfloor.setCf_ch_id(CommUtil.null2Long(channel_id));
			this.channelfloorService.saveEntity(channelfloor);
		} else {
			this.channelfloorService.updateById(channelfloor);
		}
		ModelAndView mv = new RedPigJModelAndView("admin/blue/success.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		mv.addObject("list_url", list_url);
		mv.addObject("op_title", "保存楼层成功");
		if (add_url != null) {
			mv.addObject("add_url", add_url + "&currentPage=" + currentPage);
		}
		return mv;
	}
	
	/**
	 * 频道楼层删除
	 * @param request
	 * @param response
	 * @param mulitId
	 * @param currentPage
	 * @param channel_id
	 * @return
	 */
	@SecurityMapping(title = "频道楼层删除", value = "/channelfloor_del*", rtype = "admin", rname = "频道楼层", rcode = "channel_floor", rgroup = "装修")
	@RequestMapping({ "/channelfloor_del" })
	public String deleteById(HttpServletRequest request,
			HttpServletResponse response, String mulitId, String currentPage,
			String channel_id) {
		String[] ids = mulitId.split(",");
		for (String id : ids) {
			if (!id.equals("")) {
				ChannelFloor channelfloor = this.channelfloorService
						.selectByPrimaryKey(Long.valueOf(Long.parseLong(id)));
				this.channelfloorService
						.deleteById(Long.valueOf(Long.parseLong(id)));
			}
		}
		return "redirect:channelfloor_list?currentPage=" + currentPage
				+ "&channel_id=" + channel_id;
	}
	
	/**
	 * 频道楼层模板编辑
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param cf_id
	 * @param channel_id
	 * @return
	 */
	@SecurityMapping(title = "频道楼层模板编辑", value = "/channelfloor_template*", rtype = "admin", rname = "频道楼层", rcode = "channel_floor", rgroup = "装修")
	@RequestMapping({ "/channelfloor_template" })
	public ModelAndView channelfloor_template(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String cf_id,
			String channel_id) {
		ModelAndView mv = new RedPigJModelAndView(
				"admin/blue/channelfloor_template.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		ChannelFloor obj = this.channelfloorService.selectByPrimaryKey(CommUtil
				.null2Long(cf_id));
		mv.addObject("obj", obj);
		if (obj != null) {
			mv.addObject("cf_type", Integer.valueOf(obj.getCf_type()));
			String ccl = obj.getCf_centent_list();
			if (ccl != null) {
				List<Map> goods_list = JSON.parseArray(ccl, Map.class);
				mv.addObject("goods_list", goods_list);
				List list = Lists.newArrayList();
				for (Map map : goods_list) {
					list.add(map);
				}
			}
			String cel = obj.getCf_extra_list();
			if (cel != null) {
				List<Map> r_list = JSON.parseArray(cel, Map.class);
				List<Map> rc_list = Lists.newArrayList();
				for (Map map : r_list) {
					if ("true".equals(CommUtil.null2String(map.get("show")))) {
						rc_list.add(map);
					}
				}
				mv.addObject("rc_list", rc_list);
			}
		}
		mv.addObject("currentPage", currentPage);
		mv.addObject("url", CommUtil.getURL(request));
		mv.addObject("cf_id", cf_id);

		mv.addObject("channel_id", channel_id);
		return mv;
	}
	
	/**
	 * 频道楼层更新
	 * @param request
	 * @param response
	 * @param id
	 * @param fieldName
	 * @param value
	 * @throws ClassNotFoundException
	 */
	@SecurityMapping(title = "频道楼层更新", value = "/channelfloor_ajax*", rtype = "admin", rname = "频道楼层", rcode = "channel_floor", rgroup = "装修")
	@RequestMapping({ "/channelfloor_ajax" })
	public void ajax(HttpServletRequest request, HttpServletResponse response,
			String id, String fieldName, String value)
			throws ClassNotFoundException {
		ChannelFloor obj = this.channelfloorService.selectByPrimaryKey(Long
				.valueOf(Long.parseLong(id)));
		Field[] fields = ChannelFloor.class.getDeclaredFields();
		
		Object val = ObjectUtils.setValue(fieldName, value, obj, fields);
		
		this.channelfloorService.updateById(obj);
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
	 * 左上角楼层名称编辑
	 * @param request
	 * @param response
	 * @param cf_id
	 * @param channel_id
	 * @return
	 */
	@SecurityMapping(title = "左上角楼层名称编辑", value = "/channelfloor_style1*", rtype = "admin", rname = "频道楼层", rcode = "channel_floor", rgroup = "装修")
	@RequestMapping({ "/channelfloor_style1" })
	public ModelAndView channelfloor_style1(HttpServletRequest request,
			HttpServletResponse response, String cf_id, String channel_id) {
		ModelAndView mv = new RedPigJModelAndView(
				"admin/blue/channelfloor_style1.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		ChannelFloor cf = this.channelfloorService.selectByPrimaryKey(CommUtil
				.null2Long(cf_id));
		if (cf != null) {
			String cel = cf.getCf_extra_list();
			if (cel != null) {
				List<Map> map_list = JSON.parseArray(cel, Map.class);
				mv.addObject("map_list", map_list);
			}
		}
		mv.addObject("cf", cf);
		mv.addObject("channel_id", channel_id);
		return mv;
	}
	
	/**
	 * 左上角楼层名称保存
	 * @param request
	 * @param response
	 * @param floor_name
	 * @param floor_style
	 * @param cf_id
	 * @param channel_id
	 * @return
	 */
	@SecurityMapping(title = "左上角楼层名称保存", value = "/channelfloor_style1_save*", rtype = "admin", rname = "频道楼层", rcode = "channel_floor", rgroup = "装修")
	@RequestMapping({ "/channelfloor_style1_save" })
	public String channelfloor_style1_saveEntity(HttpServletRequest request,
			HttpServletResponse response, String floor_name,
			String floor_style, String cf_id, String channel_id) {
		ChannelFloor obj = this.channelfloorService.selectByPrimaryKey(CommUtil
				.null2Long(cf_id));
		if (obj != null) {
			obj.setCf_name(floor_name);
			obj.setCf_style(floor_style);
			this.channelfloorService.updateById(obj);
		}
		return "redirect:channelfloor_template?cf_id=" + obj.getId()
				+ "&channel_id=" + channel_id;
	}
	
	/**
	 * 右上角分类/品牌名称编辑
	 * @param request
	 * @param response
	 * @param cf_id
	 * @param channel_id
	 * @return
	 */
	@SecurityMapping(title = "右上角分类/品牌名称编辑", value = "/channelfloor_style2*", rtype = "admin", rname = "频道楼层", rcode = "channel_floor", rgroup = "装修")
	@RequestMapping({ "/channelfloor_style2" })
	public ModelAndView channelfloor_style2(HttpServletRequest request,
			HttpServletResponse response, String cf_id, String channel_id) {
		ModelAndView mv = new RedPigJModelAndView(
				"admin/blue/channelfloor_style2.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		ChannelFloor cf = this.channelfloorService.selectByPrimaryKey(CommUtil
				.null2Long(cf_id));
		if (cf != null) {
			String cel = cf.getCf_extra_list();
			if (cel != null) {
				List<Map> map_list = JSON.parseArray(cel, Map.class);
				mv.addObject("map_list", map_list);
			}
		}
		mv.addObject("cf", cf);
		mv.addObject("channel_id", channel_id);
		return mv;
	}
	
	/**
	 * 右上角分类/品牌名称保存
	 * @param request
	 * @param response
	 * @param cf_id
	 * @param rc_name
	 * @param rc_url
	 * @param channel_id
	 * @return
	 */
	@SecurityMapping(title = "右上角分类/品牌名称保存", value = "/channelfloor_style2_save*", rtype = "admin", rname = "频道楼层", rcode = "channel_floor", rgroup = "装修")
	@RequestMapping({ "/channelfloor_style2_save" })
	public String channelfloor_style2_saveEntity(HttpServletRequest request,
			HttpServletResponse response, String cf_id, String rc_name,
			String rc_url, String channel_id) {
		ChannelFloor obj = this.channelfloorService.selectByPrimaryKey(CommUtil
				.null2Long(cf_id));
		String cel = obj.getCf_extra_list();
		List<Map> map_json = Lists.newArrayList();
		if (cel != null) {
			List<Map> map_list = JSON.parseArray(cel, Map.class);
			for (Map map : map_list) {
				this.channelfloorService.updateById(obj);
			}
		}
		return "redirect:channelfloor_template?cf_id=" + obj.getId()
				+ "&channel_id=" + channel_id;
	}
	
	
	/**
	 * 广告商品编辑
	 * @param request
	 * @param response
	 * @param cf_id
	 * @param channel_id
	 * @param position
	 * @return
	 */
	@SecurityMapping(title = "广告商品编辑", value = "/channelfloor_one*", rtype = "admin", rname = "频道楼层", rcode = "channel_floor", rgroup = "装修")
	@RequestMapping({ "/channelfloor_one" })
	public ModelAndView channelfloor_one(HttpServletRequest request,
			HttpServletResponse response, String cf_id, String channel_id,
			String position) {
		ModelAndView mv = new RedPigJModelAndView("admin/blue/channelfloor_one.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		ChannelFloor cf = this.channelfloorService.selectByPrimaryKey(CommUtil.null2Long(cf_id));
		Map obj = null;
		if (cf != null) {
			mv.addObject("cf", cf);
			String ccl = cf.getCf_centent_list();
			if (ccl != null) {
				List<Map> map_list = JSON.parseArray(ccl, Map.class);
				mv.addObject("map_list", map_list);
				for (Map map : map_list) {
					if ((position != null)
							&& (position.equals(map.get("position")))) {
						obj = map;
						Accessory accessory = this.accessoryService
								.selectByPrimaryKey(CommUtil.null2Long(map
										.get("img_id")));
						if (accessory != null) {
							mv.addObject("acc_url", CommUtil.getURL(request)
									+ "/" + accessory.getPath() + "/"
									+ accessory.getName());
						}
						if ("goods"
								.equals(CommUtil.null2String(map.get("type")))) {
							Goods goods = this.goodsService.selectByPrimaryKey(CommUtil
									.null2Long(map.get("goods_id")));
							if (goods != null) {
								mv.addObject("goods", goods);
							}
						}
					}
				}
			}
		}
		Map<String,Object> maps = Maps.newHashMap();
        maps.put("parent", -1);
        maps.put("orderBy", "sequence");
        maps.put("orderType", "asc");
        
		List<GoodsClass> gcs = this.goodsClassService.queryPageList(maps);
		
		mv.addObject("gcs", gcs);
		
		maps.clear();
		
		List<Activity> act = this.activityService.queryPageList(maps);
		mv.addObject("act", act);
		mv.addObject("obj", obj);
		mv.addObject("cf_id", cf_id);
		mv.addObject("position", position);
		mv.addObject("channel_id", channel_id);
		return mv;
	}
	
	/**
	 * 广告商品保存
	 * @param request
	 * @param response
	 * @param type
	 * @param cf_id
	 * @param img_url
	 * @param position
	 * @param ids
	 * @param channel_id
	 * @return
	 */
	@SecurityMapping(title = "广告商品保存", value = "/chennelfloor_one_save*", rtype = "admin", rname = "频道楼层", rcode = "channel_floor", rgroup = "装修")
	@RequestMapping({ "/chennelfloor_one_save" })
	public String channelfloor_one_saveEntity(HttpServletRequest request,
			HttpServletResponse response, String type, String cf_id,
			String img_url, String position, String ids, String channel_id) {
		ChannelFloor obj = this.channelfloorService.selectByPrimaryKey(CommUtil
				.null2Long(cf_id));
		if (obj != null) {
			if (type.equals("img")) {
				String ccl = obj.getCf_centent_list();
				List<Map> map_json = Lists.newArrayList();
				if (ccl != null) {
					List<Map> map_list = JSON.parseArray(ccl, Map.class);
					int ret = 0;
					for (Map map : map_list) {
						if ((position != null)
								&& (position.equals(map.get("position")))) {
							Accessory accessory = this.accessoryService
									.selectByPrimaryKey(CommUtil.null2Long(map
											.get("img_id")));
							map.clear();
							map.put("type", type);
							String uploadFilePath = this.configService
									.getSysConfig().getUploadFilePath();
							String saveFilePathName = request.getSession()
									.getServletContext().getRealPath("/")
									+ uploadFilePath
									+ File.separator
									+ "channelfloor";
							Map json_map = Maps.newHashMap();
							try {
								json_map = CommUtil.saveFileToServer(request,
										"img", saveFilePathName, "", null);
								if (json_map.get("fileName") != "") {
									if (accessory != null) {
										if ("img".equals(CommUtil
												.null2String(map.get("type")))) {
											this.accessoryService
													.deleteById(CommUtil
															.null2Long("img_id"));
										}
									}
									Accessory acc = new Accessory();
									acc.setName(CommUtil.null2String(json_map
											.get("fileName")));
									acc.setExt(CommUtil.null2String(json_map
											.get("mime")));
									acc.setSize(BigDecimal.valueOf(CommUtil
											.null2Double(json_map
													.get("fileSize"))));
									acc.setPath(uploadFilePath
											+ "/channelfloor");
									acc.setWidth(CommUtil.null2Int(json_map
											.get("width")));
									acc.setHeight(CommUtil.null2Int(json_map
											.get("height")));
									acc.setAddTime(new Date());
									this.accessoryService.saveEntity(acc);
									map.put("img_id", acc.getId());
									map.put("photo_url", "/" + acc.getPath()
											+ "/" + acc.getName());
								} else if (accessory != null) {
									if ("goods".equals(CommUtil.null2String(map
											.get("type")))) {
										Accessory acce = new Accessory();
										acce.setName(accessory.getName());
										acce.setExt(accessory.getExt());
										acce.setSize(accessory.getSize());
										acce.setPath(accessory.getPath());
										acce.setWidth(accessory.getWidth());
										acce.setHeight(accessory.getHeight());
										acce.setAddTime(new Date());
										this.accessoryService.saveEntity(acce);
										boolean re = true;
										map.put("img_id", acce.getId());
										map.put("photo_url",
												"/" + acce.getPath() + "/"
														+ acce.getName());
									} else {
										map.put("img_id", accessory.getId());
										map.put("photo_url",
												"/" + accessory.getPath() + "/"
														+ accessory.getName());
									}
								}
							} catch (IOException e) {
								e.printStackTrace();
							}
							map.put("img_url", img_url);
							map.put("position", position);
							ret = 1;
						}
						map_json.add(map);
					}
					if (ret == 0) {
						Map map = Maps.newHashMap();
						map.put("type", type);
						String uploadFilePath = this.configService
								.getSysConfig().getUploadFilePath();
						String saveFilePathName = request.getSession()
								.getServletContext().getRealPath("/")
								+ uploadFilePath
								+ File.separator
								+ "channelfloor";
						Map json_map = Maps.newHashMap();
						try {
							json_map = CommUtil.saveFileToServer(request,
									"img", saveFilePathName, "", null);
							if (json_map.get("fileName") != "") {
								Accessory acc = new Accessory();
								acc.setName(CommUtil.null2String(json_map
										.get("fileName")));
								acc.setExt(CommUtil.null2String(json_map
										.get("mime")));
								acc.setSize(BigDecimal.valueOf(CommUtil
										.null2Double(json_map.get("fileSize"))));
								acc.setPath(uploadFilePath + "/channelfloor");
								acc.setWidth(CommUtil.null2Int(json_map
										.get("width")));
								acc.setHeight(CommUtil.null2Int(json_map
										.get("height")));
								acc.setAddTime(new Date());
								this.accessoryService.saveEntity(acc);
								map.put("img_id", acc.getId());
								map.put("photo_url", "/" + acc.getPath() + "/"
										+ acc.getName());
							}
						} catch (IOException e) {
							e.printStackTrace();
						}
						map.put("img_url", img_url);
						map.put("position", position);
						map_json.add(map);
					}
				} else {
					Map map = Maps.newHashMap();
					map.put("type", type);
					String uploadFilePath = this.configService.getSysConfig()
							.getUploadFilePath();
					String saveFilePathName = request.getSession()
							.getServletContext().getRealPath("/")
							+ uploadFilePath + File.separator + "channelfloor";
					Object json_map = Maps.newHashMap();
					try {
						json_map = CommUtil.saveFileToServer(request, "img",
								saveFilePathName, "", null);
						if (((Map) json_map).get("fileName") != "") {
							Accessory acc = new Accessory();
							acc.setName(CommUtil.null2String(((Map) json_map)
									.get("fileName")));
							acc.setExt(CommUtil.null2String(((Map) json_map)
									.get("mime")));
							acc.setSize(BigDecimal.valueOf(CommUtil
									.null2Double(((Map) json_map)
											.get("fileSize"))));
							acc.setPath(uploadFilePath + "/channelfloor");
							acc.setWidth(CommUtil.null2Int(((Map) json_map)
									.get("width")));
							acc.setHeight(CommUtil.null2Int(((Map) json_map)
									.get("height")));
							acc.setAddTime(new Date());
							this.accessoryService.saveEntity(acc);
							map.put("img_id", acc.getId());
							map.put("photo_url", "/" + acc.getPath() + "/"
									+ acc.getName());
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
					map.put("img_url", img_url);
					map.put("position", position);
					map_json.add(map);
				}
				String json = JSON.toJSONString(map_json);
				obj.setCf_centent_list(json);
				this.channelfloorService.updateById(obj);
			}
			if (type.equals("goods")) {
				String ccl = obj.getCf_centent_list();
				List<Map> map_json = Lists.newArrayList();
				if ((ids != null) && (!"".equals(ids))) {
					String[] id_list = ids.split(",");
					for (int i = 0; i < id_list.length; i++) {
						if (!id_list[i].equals("")) {
							Goods goods = this.goodsService.selectByPrimaryKey(CommUtil
									.null2Long(id_list[i]));
							if (ccl != null) {
								List<Map> map_list = JSON.parseArray(ccl,
										Map.class);
								int ret = 0;
								for (Map map : map_list) {
									if (position != null) {
										if (position
												.equals(map.get("position"))) {
											if ("img".equals(CommUtil
													.null2String(map
															.get("type")))) {
												Accessory acc = this.accessoryService
														.selectByPrimaryKey(CommUtil.null2Long(map
																.get("img_id")));
												if (acc != null) {
													this.accessoryService
															.deleteById(CommUtil
																	.null2Long(map
																			.get("img_id")));
												}
											}
											map.clear();
											Accessory accessory = goods
													.getGoods_main_photo();
											map.put("type", type);
											if (accessory != null) {
												map.put("img_id",
														accessory.getId());
												map.put("photo_url",
														"/"
																+ accessory
																		.getPath()
																+ "/"
																+ accessory
																		.getName());
											}
											map.put("position", position);
											map.put("goods_id", id_list[i]);
											map.put("goods_name",
													goods.getGoods_name());
											map.put("goods_price",
													goods.getStore_price());
											map.put("before_price",
													goods.getGoods_price());
											ret = 1;
										}
									}
									map_json.add(map);
								}
								if (ret == 0) {
									Map map = Maps.newHashMap();
									map.put("type", type);
									Accessory accessory = goods
											.getGoods_main_photo();
									map.put("type", type);
									if (accessory != null) {
										map.put("img_id", accessory.getId());
										map.put("photo_url",
												"/" + accessory.getPath() + "/"
														+ accessory.getName());
									}
									map.put("position", position);
									map.put("goods_id", id_list[i]);
									map.put("goods_name", goods.getGoods_name());
									map.put("goods_price",
											goods.getStore_price());
									map.put("before_price",
											goods.getGoods_price());
									map_json.add(map);
								}
							} else {
								Map map = Maps.newHashMap();
								map.put("type", type);
								Accessory accessory = goods
										.getGoods_main_photo();
								map.put("type", type);
								if (accessory != null) {
									map
											.put("img_id", accessory.getId());
									map.put("photo_url", "/"
											+ accessory.getPath() + "/"
											+ accessory.getName());
								}
								map.put("position", position);
								map.put("goods_id", id_list[i]);
								map.put("goods_name",
										goods.getGoods_name());
								map.put("goods_price",
										goods.getStore_price());
								map.put("before_price",
										goods.getGoods_price());
								map_json.add(map);
							}
						}
					}
					String json = JSON.toJSONString(map_json);
					obj.setCf_centent_list(json);
					this.channelfloorService.updateById(obj);
				}
			}
		}
		return "redirect:channelfloor_template?cf_id=" + obj.getId()
				+ "&channel_id=" + channel_id;
	}
	
	/**
	 * 楼层内容更新
	 * @param request
	 * @param response
	 * @param cf_id
	 * @throws ClassNotFoundException
	 */
	@SecurityMapping(title = "楼层内容更新", value = "/channelfloor_load*", rtype = "admin", rname = "频道楼层", rcode = "channel_floor", rgroup = "装修")
	@RequestMapping({ "/channelfloor_load" })
	public void channelfloor_load(HttpServletRequest request,
			HttpServletResponse response, String cf_id)
			throws ClassNotFoundException {
		ChannelFloor obj = this.channelfloorService.selectByPrimaryKey(Long
				.valueOf(Long.parseLong(cf_id)));
		String json = "";
		if (obj != null) {
			json = obj.getCf_centent_list();
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
	
	/**
	 * 楼层内容商品更新
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
	@SecurityMapping(title = "楼层内容商品更新", value = "/channel_floor_goods_load*", rtype = "admin", rname = "频道楼层", rcode = "channel_floor", rgroup = "装修")
	@RequestMapping({ "/channel_floor_goods_load" })
	public ModelAndView channel_floor_goods_load(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String gc_id,
			String goods_name, String page, String module_id,
			String activity_name) {
		ModelAndView mv = new RedPigJModelAndView(
				"admin/blue/channel_floor_goods_load.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		if (!CommUtil.null2String(page).equals("")) {
			mv = new RedPigJModelAndView("admin/blue/" + page + ".html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request,
					response);
		}
		
		
		Map<String,Object> maps= this.redPigQueryTools.getParams(currentPage, 6, "addTime", "desc");

		
		if ((activity_name != null) && (!"".equals(activity_name))) {
			List<String> str_list = Lists.newArrayList();
			str_list.add(activity_name);
			
			this.queryTools.queryActivityGoods(maps, str_list);
		}
		if (!CommUtil.null2String(gc_id).equals("")) {
			Set<Long> ids = genericIds(this.goodsClassService
					.selectByPrimaryKey(CommUtil.null2Long(gc_id)));
			
			maps.put("gc_ids", ids);
		}
		
		if (!CommUtil.null2String(goods_name).equals("")) {
			
			maps.put("goods_name_like",goods_name);
		}
		
		if ((activity_name != null) && (!"".equals(activity_name))) {
			List<String> str_list = Lists.newArrayList();
			str_list.add(activity_name);
			this.queryTools.queryActivityGoods(maps, str_list);
			
		}
		
		
		maps.put("goods_status", 0);
		
		IPageList pList = this.goodsService.queryPagesWithNoRelations(maps);
		CommUtil.saveIPageList2ModelAndView(CommUtil.getURL(request)
				+ "/channel_floor_goods_load", "", "", pList, mv);
		mv.addObject("module_id", module_id);
		return mv;
	}
	
	/**
	 * 分类/品牌保存
	 * @param request
	 * @param response
	 * @param cf_id
	 * @param rc_name
	 * @param rc_url
	 * @param rc_on
	 * @return
	 * @throws ClassNotFoundException
	 */
	@SecurityMapping(title = "分类/品牌保存", value = "/channel_floor_rc_save*", rtype = "admin", rname = "频道楼层", rcode = "channel_floor", rgroup = "装修")
	@RequestMapping({ "/channel_floor_rc_save" })
	public ModelAndView channel_floor_rc_saveEntity(HttpServletRequest request,
			HttpServletResponse response, String cf_id, String rc_name,
			String rc_url, String rc_on) throws ClassNotFoundException {
		ModelAndView mv = new RedPigJModelAndView(
				"admin/blue/channel_floor_rc_load.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		ChannelFloor obj = this.channelfloorService.selectByPrimaryKey(Long
				.valueOf(Long.parseLong(cf_id)));
		String json = "";
		if (obj != null) {
			String cel = obj.getCf_extra_list();
			List<Map<String, String>> m_list = Lists.newArrayList();
			if ((cel != null) && (!"".equals(cel))) {
				m_list = (List) JSON.parseObject(cel, new TypeReference() {
				}, new Feature[0]);
			}
			Collections.sort(m_list, new IdComparator());
			int id = 0;
			int on = 0;
			if (m_list.size() > 0) {
				id = CommUtil.null2Int(((Map) m_list.get(m_list.size() - 1))
						.get("id"));
				Collections.sort(m_list, new MapComparator());
				on = CommUtil.null2Int(((Map) m_list.get(m_list.size() - 1))
						.get("on"));
			}
			Map map = Maps.newHashMap();
			map.put("name", rc_name);
			map.put("url", rc_url);
			map.put("show", "true");
			Pattern pattern = Pattern.compile("[0-9]*");
			boolean as = pattern.matcher(rc_on).matches();
			if ((rc_on != null) && (!"".equals(rc_on)) && (as)) {
				map.put("on", rc_on);
			} else {
				map.put("on", Integer.valueOf(on + 1));
			}
			map.put("id", Integer.valueOf(id + 1));
			m_list.add(map);
			Collections.sort(m_list, new MapComparator());
			json = JSON.toJSONString(m_list);
			obj.setCf_extra_list(json);
			this.channelfloorService.updateById(obj);
			List page_list = Lists.newArrayList();
			String currentPage = "1";
			int s_page = 5;
			int page = 0;
			if (m_list.size() % s_page == 0) {
				page = m_list.size() / s_page;
				if (page == 0) {
					page = 1;
				}
			} else {
				page = m_list.size() / s_page + 1;
			}
			if ((currentPage == null) || ("".equals(currentPage))
					|| ("0".equals(currentPage))) {
				currentPage = CommUtil.null2String(Integer.valueOf(1));
			}
			if ("1".equals(currentPage)) {
				if (page == 1) {
					page_list = m_list.subList(0, m_list.size());
				} else {
					page_list = m_list.subList(0, s_page);
				}
			} else if (CommUtil.null2Int(currentPage) > page) {
				page_list = m_list.subList(s_page * (page - 1), m_list.size());
				currentPage = CommUtil.null2String(Integer.valueOf(page));
			} else if (CommUtil.null2Int(currentPage) == page) {
				page_list = m_list.subList(
						s_page * (CommUtil.null2Int(currentPage) - 1),
						m_list.size());
			} else {
				page_list = m_list
						.subList(s_page * (CommUtil.null2Int(currentPage) - 1),
								s_page * (CommUtil.null2Int(currentPage) - 1)
										+ s_page);
			}
			if (page_list.size() > 0) {
				mv.addObject("objs", page_list);
				mv.addObject("totalPage", Integer.valueOf(page));
				mv.addObject("pageSize", Integer.valueOf(s_page));
				mv.addObject("currentPage", new Integer(currentPage));
				mv.addObject("gotoPageAjaxHTML", CommUtil.showPageAjaxHtml(
						null, null, new Integer(currentPage).intValue(),
						Integer.valueOf(page).intValue(), page));
			}
		}
		return mv;
	}
	
	/**
	 * 分类/品牌编辑保存
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param cf_id
	 * @return
	 * @throws ClassNotFoundException
	 */
	@SecurityMapping(title = "分类/品牌编辑保存", value = "/channel_floor_rc_ajax*", rtype = "admin", rname = "频道楼层", rcode = "channel_floor", rgroup = "装修")
	@RequestMapping({ "/channel_floor_rc_ajax" })
	public ModelAndView channel_floor_rc_ajax(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String cf_id)
			throws ClassNotFoundException {
		ModelAndView mv = new RedPigJModelAndView(
				"admin/blue/channel_floor_rc_load.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		ChannelFloor obj = this.channelfloorService.selectByPrimaryKey(Long
				.valueOf(Long.parseLong(cf_id)));
		String json = "";
		if (obj != null) {
			String cel = obj.getCf_extra_list();
			List<Map<String, String>> m_list = Lists.newArrayList();
			if ((cel != null) && (!"".equals(cel))) {
				m_list = (List) JSON.parseObject(cel, new TypeReference() {
				}, new Feature[0]);
			}
			Collections.sort(m_list, new MapComparator());
			List page_list = Lists.newArrayList();
			int s_page = 5;
			int page = 0;
			if (m_list.size() % s_page == 0) {
				page = m_list.size() / s_page;
				if (page == 0) {
					page = 1;
				}
			} else {
				page = m_list.size() / s_page + 1;
			}
			if ((currentPage == null) || ("".equals(currentPage))
					|| ("0".equals(currentPage))) {
				currentPage = CommUtil.null2String(Integer.valueOf(1));
			}
			if ("1".equals(currentPage)) {
				if (page == 1) {
					page_list = m_list.subList(0, m_list.size());
				} else {
					page_list = m_list.subList(0, s_page);
				}
			} else if (CommUtil.null2Int(currentPage) > page) {
				page_list = m_list.subList(s_page * (page - 1), m_list.size());
				currentPage = CommUtil.null2String(Integer.valueOf(page));
			} else if (CommUtil.null2Int(currentPage) == page) {
				page_list = m_list.subList(
						s_page * (CommUtil.null2Int(currentPage) - 1),
						m_list.size());
			} else {
				page_list = m_list
						.subList(s_page * (CommUtil.null2Int(currentPage) - 1),
								s_page * (CommUtil.null2Int(currentPage) - 1)
										+ s_page);
			}
			if (page_list.size() > 0) {
				mv.addObject("objs", page_list);
				mv.addObject("totalPage", Integer.valueOf(page));
				mv.addObject("pageSize", Integer.valueOf(s_page));
				mv.addObject("currentPage", new Integer(currentPage));
				mv.addObject("gotoPageAjaxHTML", CommUtil.showPageAjaxHtml(
						null, null, new Integer(currentPage).intValue(),
						Integer.valueOf(page).intValue(), page));
			}
		}
		return mv;
	}
	
	/**
	 * 分类/品牌删除
	 * @param request
	 * @param response
	 * @param cf_id
	 * @param rc_id
	 * @param currentPage
	 * @return
	 * @throws ClassNotFoundException
	 */
	@SecurityMapping(title = "分类/品牌删除", value = "/channel_floor_rc_del*", rtype = "admin", rname = "频道楼层", rcode = "channel_floor", rgroup = "装修")
	@RequestMapping({ "/channel_floor_rc_del" })
	public ModelAndView channel_floor_rc_del(HttpServletRequest request,
			HttpServletResponse response, String cf_id, String rc_id,
			String currentPage) throws ClassNotFoundException {
		ModelAndView mv = new RedPigJModelAndView(
				"admin/blue/channel_floor_rc_load.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		ChannelFloor obj = this.channelfloorService.selectByPrimaryKey(Long
				.valueOf(Long.parseLong(cf_id)));
		String json = "";
		if ((obj != null) && (rc_id != null)) {
			String cel = obj.getCf_extra_list();
			List<Map<String, String>> m_list = (List) JSON.parseObject(cel,
					new TypeReference() {
					}, new Feature[0]);
			for (Map map : m_list) {
				if (rc_id.equals(CommUtil.null2String(map.get("id")))) {
					m_list.remove(map);
					break;
				}
			}
			json = JSON.toJSONString(m_list);
			obj.setCf_extra_list(json);
			this.channelfloorService.updateById(obj);

			Collections.sort(m_list, new MapComparator());
			List page_list = Lists.newArrayList();
			int s_page = 5;
			int page = 0;
			if (m_list.size() % s_page == 0) {
				page = m_list.size() / s_page;
				if (page == 0) {
					page = 1;
				}
			} else {
				page = m_list.size() / s_page + 1;
			}
			if ((currentPage == null) || ("".equals(currentPage))
					|| ("0".equals(currentPage))) {
				currentPage = CommUtil.null2String(Integer.valueOf(1));
			}
			if ("1".equals(currentPage)) {
				if (page == 1) {
					page_list = m_list.subList(0, m_list.size());
				} else {
					page_list = m_list.subList(0, s_page);
				}
			} else if (CommUtil.null2Int(currentPage) > page) {
				page_list = m_list.subList(s_page * (page - 1), m_list.size());
				currentPage = CommUtil.null2String(Integer.valueOf(page));
			} else if (CommUtil.null2Int(currentPage) == page) {
				page_list = m_list.subList(
						s_page * (CommUtil.null2Int(currentPage) - 1),
						m_list.size());
			} else {
				page_list = m_list
						.subList(s_page * (CommUtil.null2Int(currentPage) - 1),
								s_page * (CommUtil.null2Int(currentPage) - 1)
										+ s_page);
			}
			if (page_list.size() > 0) {
				mv.addObject("objs", page_list);
				mv.addObject("totalPage", Integer.valueOf(page));
				mv.addObject("pageSize", Integer.valueOf(s_page));
				mv.addObject("currentPage", new Integer(currentPage));
				mv.addObject("gotoPageAjaxHTML", CommUtil.showPageAjaxHtml(
						null, null, new Integer(currentPage).intValue(),
						Integer.valueOf(page).intValue(), page));
			}
		}
		return mv;
	}
	
	/**
	 * 分类/品牌更新
	 * @param request
	 * @param response
	 * @param cf_id
	 * @param rc_id
	 * @param show
	 * @param value
	 * @throws ClassNotFoundException
	 */
	@SecurityMapping(title = "分类/品牌更新", value = "/channel_floor_rc_update*", rtype = "admin", rname = "频道楼层", rcode = "channel_floor", rgroup = "装修")
	@RequestMapping({ "/channel_floor_rc_update" })
	public void channel_floor_rc_updateById(HttpServletRequest request,
			HttpServletResponse response, String cf_id, String rc_id,
			String show, String value) throws ClassNotFoundException {
		ChannelFloor obj = this.channelfloorService.selectByPrimaryKey(Long
				.valueOf(Long.parseLong(cf_id)));
		String json = "";
		String ret = "";
		if ((obj != null) && (rc_id != null)) {
			String cel = obj.getCf_extra_list();
			List<Map<String, String>> m_list = (List) JSON.parseObject(cel,
					new TypeReference() {
					}, new Feature[0]);
			for (Map map : m_list) {
				if (rc_id.equals(CommUtil.null2String(map.get("id")))) {
					if ("c_url".equals(show)) {
						map.remove("url");
						map.put("url", value);
					}
					if ("c_name".equals(show)) {
						map.remove("name");
						map.put("name", value);
					}
					if ("c_id".equals(show)) {
						Pattern pattern = Pattern.compile("[0-9]*");
						boolean as = pattern.matcher(value).matches();
						if (as) {
							map.remove("on");
							map.put("on", value);
							Collections.sort(m_list, new MapComparator());
						}
					}
					if ("display".equals(show)) {
						if ("false"
								.equals(CommUtil.null2String(map.get("show")))) {
							map.remove("show");
							map.put("show", "true");
							ret = "true";
						} else {
							map.remove("show");
							map.put("show", "false");
							ret = "false";
						}
					}
				}
			}
			json = JSON.toJSONString(m_list);
			obj.setCf_extra_list(json);
			this.channelfloorService.updateById(obj);
		}
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		try {
			PrintWriter writer = response.getWriter();
			if (!"display".equals(show)) {
				writer.print(json);
			} else {
				writer.print(ret);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
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

	class MapComparator implements Comparator<Map<String, String>> {
		MapComparator() {
		}

		public int compare(Map o1, Map o2) {
			return CommUtil.null2Int(o1.get("on"))
					- CommUtil.null2Int(o2.get("on"));
		}
	}

	class IdComparator implements Comparator<Map<String, String>> {
		IdComparator() {
		}

		public int compare(Map o1, Map o2) {
			return CommUtil.null2Int(o1.get("id"))
					- CommUtil.null2Int(o2.get("id"));
		}
	}

	public static String floorshowPageFormHtml(String url, int currentPage,
			int pages, int pageSize) {
		String s = "";
		if (pages > 0) {
			if (currentPage >= 1) {
				s = s + "<a href='" + url + "&currentPage=1'>首页</a> ";
				if (currentPage > 1) {
					s = s + "<a href='" + url + "&currentPage="
							+ (currentPage - 1) + "'><b><</b>上一页</a> ";
				}
			}
			int beginPage = currentPage - 3 < 1 ? 1 : currentPage - 3;
			if (beginPage <= pages) {
				s = s + "第　";
				int i = beginPage;
				for (int j = 0; (i <= pages) && (j < 4); j++) {
					if (i == currentPage) {
						s = s + "<a class='this' href='" + url
								+ "&currentPage=" + i + "'>" + i + "</a> ";
					} else {
						s = s + "<a href='" + url + "&currentPage=" + i + "'>"
								+ i + "</a> ";
					}
					i++;
				}
				s = s + "页　";
			}
			if (currentPage <= pages) {
				if (currentPage < pages) {
					s =

					s + "<a href='" + url + "&currentPage=" + (currentPage + 1)
							+ "'>下一页<b class='next'>></b></a> ";
				}
				s = s + "<a href='" + url + "&currentPage=" + pages
						+ "'>末页</a> ";
			}
			s = s + "共<strong>" + pages + "</strong>页 ";
		}
		return s;
	}
}
