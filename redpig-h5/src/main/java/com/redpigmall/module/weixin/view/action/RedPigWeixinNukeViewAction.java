package com.redpigmall.module.weixin.view.action;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.redpigmall.api.annotation.SecurityMapping;
import com.redpigmall.api.mv.RedPigJModelAndView;
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.api.sec.SecurityUserHolder;
import com.redpigmall.api.tools.CommUtil;
import com.redpigmall.domain.*;
import com.redpigmall.module.weixin.view.action.base.BaseAction;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.*;


/**
 * 
 * <p>
 * Title: RedPigWapGroupViewAction.java
 * </p>
 * 
 * <p>
 * Description:秒杀管理控制器，用来进入秒杀商品列表
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
 * @date 2014-5-28
 * 
 * @version redpigmall_b2b2c v8.0 2016版
 */
@SuppressWarnings({ "unchecked", "rawtypes", "unused" })
@Controller
public class RedPigWeixinNukeViewAction extends BaseAction{

	/**
	 * 列出秒杀的所有商品
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @param gc_id
	 * @param gpr_id
	 * @param ga_id
	 * @param type
	 * @param cga_id
	 * @return
	 */
	@RequestMapping({ "/nuke/index" })
	public ModelAndView nuke(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,
			String orderType, String gc_id, String gpr_id, String ga_id,
			String type, String cga_id) {
		ModelAndView mv = new RedPigJModelAndView("weixin/nuke/nuke_list.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);

		String url = this.configService.getSysConfig().getAddress();

		if ((url == null) || (url.equals(""))) {
			url = CommUtil.getURL(request);
		}

		String params = "";

		Map<String,Object> maps = this.redPigQueryTools.getParams(currentPage, orderBy, orderType);

		IPageList pList = this.nukeGoodsService.list(maps);
		CommUtil.saveIPageList2ModelAndView(url + "/nuke/nuke_list.html", "",params, pList, mv);
		return mv;
	}



	/*@RequestMapping({ "/nuke/data" })
	public ModelAndView group_data(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,
			String orderType, String gc_id, String gpr_id, String ga_id,
			String type) {
		ModelAndView mv = new RedPigJModelAndView("weixin/group_data.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		if (("".equals(type)) || ("goods".equals(type)) || (type == null)) {
			Map<String, Object> params = Maps.newHashMap();
			params.put("beginTime_more_than_equal", new Date());
			params.put("endTime_less_than_equal", new Date());
			params.put("group_type", Integer.valueOf(0));
			if ((orderBy == null) || (orderBy.equals(""))) {
				orderBy = "addTime";
			}
			if ((orderType == null) || (orderType.equals(""))) {
				orderType = "desc";
			}
			List<Group> groups = this.groupService.queryPageList(params);
			
			if (groups.size() > 0) {
				
				Map<String,Object> maps= this.redPigQueryTools.getParams(currentPage, orderBy, orderType);
				maps.put("group_id", groups.get(0).getId());
				
				if ((gc_id != null) && (!gc_id.equals(""))) {
					maps.put("gg_gc_id", CommUtil.null2Long(gc_id));
				}
				
				if ((ga_id != null) && (!ga_id.equals(""))) {
					maps.put("gg_ga_id", CommUtil.null2Long(ga_id));
					mv.addObject("ga_id", ga_id);
				}
				
				GroupPriceRange gpr = this.groupPriceRangeService.selectByPrimaryKey(CommUtil.null2Long(gpr_id));
				
				if (gpr != null) {
					maps.put("gg_price_more_than_equal", BigDecimal.valueOf(gpr.getGpr_begin()));
					maps.put("gg_price_less_than_equal", BigDecimal.valueOf(gpr.getGpr_end()));
				}
				maps.put("gg_status", 1);
				
				maps.put("beginTime_more_than_equal", new Date());
				maps.put("endTime_less_than_equal", new Date());
				
				maps.put("gg_goods_goods_status", 0);
				
				IPageList pList = this.groupGoodsService.list(maps);
				CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
				
				maps.clear();
				maps.put("gc_type", 0);
				maps.put("parent", -1);
				
				List<GroupClass> gcs = this.groupClassService.queryPageList(maps);
				
				maps.clear();
				maps.put("orderBy", "gpr_begin");
				maps.put("orderType", "asc");
				
				List<GroupPriceRange> gprs = this.groupPriceRangeService.queryPageList(maps);
				
				mv.addObject("gprs", gprs);
				mv.addObject("gcs", gcs);
				mv.addObject("group", groups.get(0));
				if ((orderBy == null) || (orderBy.equals(""))) {
					orderBy = "addTime";
				}
				if ((orderType == null) || (orderType.equals(""))) {
					orderType = "desc";
				}
				mv.addObject("gc_id", gc_id);
				mv.addObject("gpr_id", gpr_id);
			}
		}
		if ("life".equals(type)) {
			mv = new RedPigJModelAndView("weixin/group_life_data.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			Map<String, Object> params = Maps.newHashMap();
			params.put("beginTime_more_than_equal", new Date());
			params.put("endTime_less_than_equal", new Date());
			params.put("group_type", Integer.valueOf(1));
			
			List<Group> groups = this.groupService.queryPageList(params);
			
			if (groups.size() > 0) {
				
				Map<String,Object> maps= this.redPigQueryTools.getParams(currentPage, orderBy, orderType);
				maps.put("group_id", groups.get(0).getId());
				
				if ((gc_id != null) && (!gc_id.equals(""))) {
					maps.put("gg_gc_id", CommUtil.null2Long(gc_id));
				}
				
				if ((ga_id != null) && (!ga_id.equals(""))) {
					maps.put("gg_ga_id", CommUtil.null2Long(ga_id));
					mv.addObject("ga_id", ga_id);
				}
				
				GroupPriceRange gpr = this.groupPriceRangeService.selectByPrimaryKey(CommUtil.null2Long(gpr_id));
				
				if (gpr != null) {
					
					maps.put("group_price_more_than_equal", gpr.getGpr_begin());
					maps.put("group_price_less_than_equal", gpr.getGpr_end());
					
				}
				maps.put("group_status", 1);
				maps.put("beginTime_more_than_equal", new Date());
				maps.put("endTime_less_than_equal", new Date());
				
				IPageList pList = this.groupLifeGoodsService.list(maps);
				CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
				
				
				maps.clear();
				maps.put("gc_type", 1);
				maps.put("parent", -1);
				maps.put("orderBy", "gc_sequence");
				maps.put("orderType", "asc");
				
				List<GroupClass> gcs = this.groupClassService.queryPageList(maps);
				maps.clear();
				maps.put("orderBy", "gpr_begin");
				maps.put("orderType", "asc");
				
				List<GroupPriceRange> gprs = this.groupPriceRangeService.queryPageList(maps);
				
				mv.addObject("gprs", gprs);
				mv.addObject("gcs", gcs);
				mv.addObject("group", groups.get(0));
				if ((orderBy == null) || (orderBy.equals(""))) {
					orderBy = "addTime";
				}
				if ((orderType == null) || (orderType.equals(""))) {
					orderType = "desc";
				}
				mv.addObject("gc_id", gc_id);
				mv.addObject("gpr_id", gpr_id);
				mv.addObject("groupViewTools", this.groupViewTools);
			}
		}
		mv.addObject("type", type);
		mv.addObject("order_type", CommUtil.null2String(orderBy) + "_"
				+ CommUtil.null2String(orderType));
		return mv;
	}

	@RequestMapping({ "/nuke/area" })
	public ModelAndView group_area(HttpServletRequest request,
			HttpServletResponse response, String ga_id, String type) {
		ModelAndView mv = new RedPigJModelAndView("weixin/group_area.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		Map<String,Object> maps= Maps.newHashMap();
        maps.put("parent", -1);
        maps.put("orderBy", "ga_sequence");
        maps.put("orderType", "asc");
		
		List<GroupArea> gas = this.groupAreaService.queryPageList(maps);
		
		mv.addObject("gas", gas);
		if ((ga_id != null) && (!ga_id.equals(""))) {
			mv.addObject("ga",
					this.groupAreaService.selectByPrimaryKey(CommUtil.null2Long(ga_id))
							.getGa_name());
		} else {
			mv.addObject("ga", "全国");
		}
		mv.addObject("ga_id", ga_id);
		mv.addObject("type", type);
		return mv;
	}

	@RequestMapping({ "/nuke/view" })
	public ModelAndView group_view(HttpServletRequest request,
			HttpServletResponse response, String id) {
		ModelAndView mv = new RedPigJModelAndView("weixin/group_view.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		GroupGoods obj = this.groupGoodsService.selectByPrimaryKey(CommUtil
				.null2Long(id));
		User user = SecurityUserHolder.getCurrentUser();
		boolean view = false;
		if ((obj.getGroup().getBeginTime().before(new Date()))
				&& (obj.getGroup().getEndTime().after(new Date()))
				&& (obj.getGg_status() == 1)) {
			view = true;
		}
		if ((user != null) && (user.getUserRole().indexOf("ADMIN") >= 0)) {
			view = true;
		}
		if (view) {
			mv.addObject("obj", obj);
			Map<String, Object> params = Maps.newHashMap();
			params.put("beginTime_more_than_equal", new Date());
			params.put("endTime_less_than_equal", new Date());
			params.put("status", Integer.valueOf(0));
			params.put("group_type", Integer.valueOf(0));
			
			List<Group> groups = this.groupService.queryPageList(params);
			
			if (groups.size() > 0) {
				
				Map<String,Object> maps= this.redPigQueryTools.getParams("1",4,"gg_recommend", "desc");
				maps.put("gg_status", 1);
				maps.put("group_id", obj.getGroup().getId());
				maps.put("id_no", obj.getId());
				
				maps.put("beginTime_more_than_equal", new Date());
				maps.put("endTime_less_than_equal", new Date());
				
				IPageList pList = this.groupGoodsService.list(maps);
				mv.addObject("hot_ggs", pList.getResult());
				mv.addObject("group", groups.get(0));
			}
		} else {
			mv = new RedPigJModelAndView("weixin/error.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "团购商品参数错误");
			mv.addObject("url", CommUtil.getURL(request) + "/index");
		}
		return mv;
	}

	@RequestMapping({ "/nuke/view" })
	public ModelAndView grouplife_view(HttpServletRequest request,
			HttpServletResponse response, String id) {
		ModelAndView mv = new RedPigJModelAndView("weixin/grouplife_view.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		GroupLifeGoods obj = this.groupLifeGoodsService.selectByPrimaryKey(CommUtil
				.null2Long(id));
		User user = SecurityUserHolder.getCurrentUser();
		boolean view = false;
		if ((obj.getGroup().getBeginTime().before(new Date()))
				&& (obj.getGroup_status() == 1)
				&& (obj.getGroup().getEndTime().after(new Date()))) {
			view = true;
		}
		if ((user != null) && (user.getUserRole().indexOf("ADMIN") >= 0)) {
			view = true;
		}
		if (view) {
			mv.addObject("obj", obj);
			Map<String, Object> params = Maps.newHashMap();
			params.put("status", Integer.valueOf(0));
			params.put("group_type", Integer.valueOf(1));
			
			params.put("beginTime_more_than_equal", new Date());
			params.put("endTime_less_than_equal", new Date());
			
			List<Group> groups = this.groupService.queryPageList(params);
			
			if (groups.size() > 0) {
				
				Map<String,Object> maps= this.redPigQueryTools.getParams("1",4,"group_recommend", "desc");
				maps.put("group_status", 1);
				
				maps.put("group_id", obj.getGroup().getId());
				maps.put("id_no", obj.getId());
				
				maps.put("beginTime_more_than_equal", new Date());
				maps.put("endTime_less_than_equal", new Date());
				
				IPageList pList = this.groupLifeGoodsService.list(maps);
				mv.addObject("hot_ggs", pList.getResult());
				mv.addObject("group", groups.get(0));
			}
		} else {
			mv = new RedPigJModelAndView("weixin/error.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "团购商品参数错误");
			mv.addObject("url", CommUtil.getURL(request) + "/index");
		}
		mv.addObject("groupViewTools", this.groupViewTools);
		return mv;
	}

	@RequestMapping({ "/nuke/grouplifegoods_list" })
	public ModelAndView grouplifegoodsList(HttpServletRequest request,
			HttpServletResponse response, String gc_id, String currentPage) {
		ModelAndView mv = new RedPigJModelAndView("weixin/group_life_goodslist.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		List<GroupLifeGoods> glglist = Lists.newArrayList();
		Map<String, Object> params = Maps.newHashMap();
		params.put("group_type", Integer.valueOf(1));
		
		params.put("beginTime_more_than_equal", new Date());
		params.put("endTime_less_than_equal", new Date());
		
		List<Group> groups = this.groupService.queryPageList(params);
		
		GroupClass currentgc = this.groupClassService.selectByPrimaryKey(CommUtil.null2Long(gc_id));
		List<GroupClass> gcs = Lists.newArrayList();
		if ((currentgc != null) && (currentgc.getGc_level() == 0)) {
			params.clear();
			params.put("parent", currentgc.getId());
			params.put("gc_type", 1);
			params.put("orderBy", "gc_sequence");
		    params.put("orderType", "asc");
			
			gcs = this.groupClassService.queryPageList(params);
			
		} else if ((currentgc != null) && (currentgc.getGc_level() == 1)) {
			params.clear();
			params.put("parent", currentgc.getId());
			params.put("gc_type", 1);
			params.put("orderBy", "gc_sequence");
		    params.put("orderType", "asc");
		    
			gcs = this.groupClassService.queryPageList(params);
		}
		mv.addObject("gcs", gcs);
		if (groups.size() > 0) {
			Map<String,Object> maps= this.redPigQueryTools.getParams(currentPage,"addTime", "desc");
			
			maps.put("group_id", groups.get(0).getId());
			
			maps.put("group_status", 1);
            maps.put("beginTime_more_than_equal", new Date());
            maps.put("endTime_less_than_equal", new Date());

			if ((gc_id != null) && (!"".equals(gc_id))) {
				Set<Long> ids = getgcIds(CommUtil.null2Long(gc_id));
				
				maps.put("gg_gc_ids", ids);
			}
			IPageList pList = this.groupLifeGoodsService.list(maps);
			CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);

			mv.addObject("group", groups.get(0));

			mv.addObject("gc_id", gc_id);
			mv.addObject("currentgc", currentgc);
		}
		return mv;
	}

	@RequestMapping({ "/nuke/groupAreaChose" })
	public ModelAndView groupAreaChose(HttpServletRequest request,
			HttpServletResponse response, String gaid) {
		ModelAndView mv = new RedPigJModelAndView("weixin/group_life_arealist.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		Map<String, Object> params = Maps.newHashMap();
		params.put("audit", Integer.valueOf(1));
		params.put("orderBy", "sequence");
		params.put("orderType", "asc");

        
		List<Area> arealist = this.areaService.queryPageList(params);
		
		List all_list = Lists.newArrayList();
		Set set = new HashSet();
		String list_word = "A,B,C,D,E,F,G,H,I,J,K,L,M,N,O,P,Q,R,S,T,U,V,W,X,Y,Z";
		String[] words = list_word.split(",");
		for (String word : words) {
			Map area_map = Maps.newHashMap();
			List area_list = Lists.newArrayList();
			for (Area area : arealist) {
				if ((!CommUtil.null2String(area.getFirst_word()).equals(""))
						&& (word.equals(area.getFirst_word().toUpperCase()))) {
					Map<String, Object> map = Maps.newHashMap();
					map.put("name", area.getAreaName());
					map.put("id", area.getId());
					area_list.add(map);
					set.add(area.getFirst_word());
				}
			}
			if (area_list.size() > 0) {
				area_map.put("area_list", area_list);
				area_map.put("word", word);
				all_list.add(area_map);
			}
		}
		Area area = this.areaService.selectByPrimaryKey(CommUtil.null2Long(gaid));
		Arrays.sort(set.toArray());
		mv.addObject("area", area);
		mv.addObject("words", set);
		mv.addObject("all_list", all_list);
		return mv;
	}

	@RequestMapping({ "/nuke/grouplifegoods_search" })
	public ModelAndView grouplifegoods_search(HttpServletRequest request,
			HttpServletResponse response, String keyword, String currentPage,
			String ga_id) {
		ModelAndView mv = new RedPigJModelAndView("weixin/group_life_goodslist.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		List<GroupLifeGoods> glglist = Lists.newArrayList();
		Map<String, Object> params = Maps.newHashMap();
		params.put("group_type", Integer.valueOf(1));
		params.put("beginTime_more_than_equal", new Date());
		params.put("endTime_less_than_equal", new Date());
		
		List<Group> groups = this.groupService.queryPageList(params);
		
		if (groups.size() > 0) {
			
			Map<String,Object> maps= this.redPigQueryTools.getParams(currentPage,"addTime", "desc");
			maps.put("group_id", groups.get(0).getId());
			
			maps.put("group_status", 1);
			
			maps.put("beginTime_more_than_equal", new Date());
			maps.put("endTime_less_than_equal", new Date());
			
			if ((keyword != null) && (!keyword.equals(""))) {
				maps.put("gg_name_like", keyword);
			}
			
			if ((ga_id != null) && (!ga_id.equals(""))) {
				maps.put("gg_gai_area_id", CommUtil.null2Long(ga_id));
			} else {
				ga_id = CommUtil.null2String(request.getSession(false).getAttribute("ga_id"));
				maps.put("gg_gai_area_parent_id", CommUtil.null2Long(ga_id));
			}
			
			IPageList pList = this.groupLifeGoodsService.list(maps);
			CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
			System.out.println(groups.get(0));
			mv.addObject("group", groups.get(0));
		}
		return mv;
	}

	@RequestMapping({ "/nuke/grouplife/areaSearch" })
	public void areaSearch(HttpServletRequest request,
			HttpServletResponse response, String areaName) {
		Map<String, Object> params = Maps.newHashMap();
		params.put("areaName_like",areaName);
		params.put("orderBy", "sequence");
		params.put("orderType", "asc");
		
		List<Area> arealist = this.areaService.queryPageList(params);
		
		List list = Lists.newArrayList();
		if ((arealist != null) && (arealist.size() > 0)) {
			for (Area area : arealist) {
				Map<String, Object> map = Maps.newHashMap();
				map.put("id", area.getId());
				map.put("areaName", area.getAreaName());
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

	@RequestMapping({ "/nuke/group_evaluation" })
	public ModelAndView group_evaluation(HttpServletRequest request,
			HttpServletResponse response, String id, String goods_id,
			String currentPage) {
		Store store = this.storeService.selectByPrimaryKey(CommUtil.null2Long(id));
		ModelAndView mv = new RedPigJModelAndView("weixin/group_evaluate.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		Map<String,Object> maps= this.redPigQueryTools.getParams(currentPage,8,"addTime", "desc");
		
		maps.put("evaluate_goods_id", CommUtil.null2Long(goods_id));
		maps.put("evaluate_type", "goods");
		maps.put("evaluate_status", 0);
		
		IPageList pList = this.evaluateService.list(maps);
		CommUtil.saveIPageList2ModelAndView(CommUtil.getURL(request)
				+ "/group_evaluation.html", "", "", pList, mv);
		mv.addObject("storeViewTools", this.storeViewTools);
		mv.addObject("store", store);
		Goods goods = this.goodsService
				.selectByPrimaryKey(CommUtil.null2Long(goods_id));
		mv.addObject("goods", goods);
		mv.addObject("currentPage", currentPage);
		return mv;
	}

	@RequestMapping({ "/nuke/group_order" })
	public ModelAndView group_order(HttpServletRequest request,
			HttpServletResponse response, String goods_id, String currentPage) {
		ModelAndView mv = new RedPigJModelAndView("weixin/group_order.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		
		Map<String,Object> maps= this.redPigQueryTools.getParams(currentPage,8,"addTime", "desc");
		maps.put("evaluate_goods_id", CommUtil.null2Long(goods_id));
		
		IPageList pList = this.evaluateService.list(maps);
		CommUtil.saveIPageList2ModelAndView(CommUtil.getURL(request)
				+ "/group_order.html", "", "", pList, mv);
		System.out.println(pList.getPages());
		mv.addObject("currentPage", currentPage);
		return mv;
	}

	@SecurityMapping(title = "生活类团购订单购买", value = "/life_order*", rtype = "buyer", rname = "团购", rcode = "buyer_group", rgroup = "团购")
	@RequestMapping({ "/nuke/life_order" })
	public ModelAndView life_order(HttpServletRequest request,
			HttpServletResponse response, String gid) {
		ModelAndView mv = new RedPigJModelAndView("weixin/life_order.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder
				.getCurrentUser().getId());
		GroupLifeGoods group = this.groupLifeGoodsService.selectByPrimaryKey(CommUtil
				.null2Long(gid));
		if (group != null) {
			if (!user.getId().equals(group.getUser().getId())) {
				if (((group.getGroup_count() != 0) &&

				(group.getGroup_count() <= group.getGroupInfos().size()))
						|| (group.getEndTime().before(new Date()))) {
					mv = new RedPigJModelAndView("weixin/error.html",
							this.configService.getSysConfig(),
							this.userConfigService.getUserConfig(), 1, request,
							response);
					mv.addObject("op_title", "团购已到期或已售完。");
					mv.addObject("url", CommUtil.getURL(request)
							+ "/group/index?type=life");
				} else if ((user.getMobile() != null)
						&& (!"".equals(user.getMobile()))) {
					mv.addObject("user", user);
					mv.addObject("obj", group);
					String orderForm_session = CommUtil.randomString(32);
					request.getSession(false).setAttribute("orderForm_session",
							orderForm_session);
					mv.addObject("orderForm_session", orderForm_session);
				} else {
					mv = new RedPigJModelAndView("weixin/error.html",
							this.configService.getSysConfig(),
							this.userConfigService.getUserConfig(), 1, request,
							response);
					mv.addObject("op_title", "请先绑定您的手机");
					mv.addObject("url", CommUtil.getURL(request)
							+ "/buyer/account_mobile");
				}
			} else {
				mv = new RedPigJModelAndView("weixin/error.html",
						this.configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 1, request,
						response);
				mv.addObject("op_title", "无法购买自己的团购商品");
				mv.addObject("url", CommUtil.getURL(request)
						+ "/group/index?type=life");
			}
		} else {
			mv = new RedPigJModelAndView("weixin/error.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "您所访问的团购不存在");
			mv.addObject("url", CommUtil.getURL(request)
					+ "/group/index?type=life");
		}
		return mv;
	}
	*//**
	 * 生活类团购订单保存
	 * @param request
	 * @param response
	 * @param orderForm_session
	 * @param group_id
	 * @param order_count
	 * @return
	 *//*
	@SecurityMapping(title = "生活类团购订单保存", value = "/life_order_save*", rtype = "buyer", rname = "团购", rcode = "buyer_group", rgroup = "团购")
	@RequestMapping({ "/nuke/life_order_save" })
	public ModelAndView life_order_saveEntity(HttpServletRequest request,
			HttpServletResponse response, String orderForm_session,
			String group_id, String order_count) {
		if (CommUtil.null2Int(order_count) <= 0) {
			order_count = "1";
		}
		ModelAndView mv = new RedPigJModelAndView("weixin/life_order_pay.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder
				.getCurrentUser().getId());
		GroupLifeGoods group = null;
		if ((group_id != null) && (!group_id.equals(""))) {
			group = this.groupLifeGoodsService.selectByPrimaryKey(CommUtil
					.null2Long(group_id));
			String orderForm_session2 = CommUtil.null2String(request
					.getSession(false).getAttribute("orderForm_session"));
			double group_total_price = 0.0D;
			OrderForm orderForm = new OrderForm();
			if (orderForm_session2.equals(orderForm_session)) {
				this.groupLifeGoodsService.updateById(group);
				group_total_price = CommUtil
						.null2Double(group.getGroup_price())
						* CommUtil.null2Int(order_count);
				orderForm.setAddTime(new Date());
				orderForm.setUser_id(user.getId().toString());
				orderForm.setUser_name(user.getUserName());
				Map json = Maps.newHashMap();
				json.put("goods_id", group.getId().toString());
				json.put("goods_name", group.getGg_name());
				json.put("goods_type", Integer.valueOf(group.getGoods_type()));
				json.put("goods_price", group.getGroup_price());
				json.put("goods_count",
						Integer.valueOf(CommUtil.null2Int(order_count)));
				json.put("goods_total_price", Double.valueOf(group_total_price));
				json.put("goods_mainphoto_path", group.getGroup_acc().getPath()
						+ "/" + group.getGroup_acc().getName());
				orderForm.setGroup_info(JSON.toJSONString(json));
				if (group.getGoods_type() == 0) {
					if (group.getUser().getStore() != null) {
						orderForm.setStore_id(group.getUser().getStore()
								.getId().toString());
					}
					orderForm.setOrder_form(0);
				} else {
					orderForm.setOrder_form(1);
				}
				orderForm.setTotalPrice(BigDecimal.valueOf(group_total_price));
				orderForm.setOrder_id("life"
						+ SecurityUserHolder.getCurrentUser().getId()
						+ CommUtil.formatTime("yyyyMMddHHmmss", new Date()));
				orderForm.setOrder_status(10);
				orderForm.setOrder_cat(2);
				request.getSession(false).removeAttribute("orderForm_session");
				this.orderFormService.saveEntity(orderForm);
				mv.addObject("order_count", order_count);
				String orderpayment_session = CommUtil.randomString(32);
				request.getSession(false).setAttribute("orderpayment_session",
						orderpayment_session);
				mv.addObject("orderpayment_session", orderpayment_session);
				OrderFormLog ofl = new OrderFormLog();
				ofl.setAddTime(new Date());
				ofl.setOf(orderForm);
				ofl.setLog_info("提交订单");
				User cuser = SecurityUserHolder.getCurrentUser();
				ofl.setLog_user_id(cuser.getId());
				ofl.setLog_user_name(cuser.getUserName());
				this.orderFormLogService.saveEntity(ofl);
				mv.addObject("order", orderForm);
				mv.addObject("all_of_price",
						BigDecimal.valueOf(group_total_price));
				mv.addObject("paymentTools", this.paymentTools);
				mv.addObject("group", Boolean.valueOf(true));
			} else {
				mv = new RedPigJModelAndView("weixin/error.html",
						this.configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 1, request,
						response);
				mv.addObject("op_title", "禁止重复提交");
				mv.addObject("url", CommUtil.getURL(request)
						+ "/group/index?type=life");
				request.getSession(false).removeAttribute("orderForm_session");
			}
		} else {
			mv = new RedPigJModelAndView("weixin/error.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "此页面不存在");
			mv.addObject("url", CommUtil.getURL(request)
					+ "/group/index?type=life");
			request.getSession(false).removeAttribute("orderForm_session");
		}
		return mv;
	}
	
	*//**
	 * 团购订单数量
	 * @param request
	 * @param response
	 * @param group_id
	 * @param count
	 *//*
	@SecurityMapping(title = "团购订单数量", value = "/group_count_adjust*", rtype = "buyer", rname = "团购", rcode = "buyer_group", rgroup = "团购")
	@RequestMapping({ "/nuke/group_count_adjust" })
	public void group_count_adjust(HttpServletRequest request,
			HttpServletResponse response, String group_id, String count) {
		double group_total_price = 0.0D;
		String error = "100";
		GroupLifeGoods group = null;
		if ((group_id != null) && (!group_id.equals(""))) {
			group = this.groupLifeGoodsService.selectByPrimaryKey(CommUtil
					.null2Long(group_id));
		}
		if (CommUtil.null2Int(count) > group.getGroup_count()) {
			error = "200";
			count = CommUtil
					.null2String(Integer.valueOf(group.getGroup_count()));
		}
		group_total_price = CommUtil
				.null2Double(Double.valueOf(CommUtil.null2Double(group
						.getGroup_price()) * CommUtil.null2Int(count)));
		DecimalFormat df = new DecimalFormat("0.00");
		Map<String, Object> map = Maps.newHashMap();
		map.put("count", count);
		map.put("group_total_price",
				Double.valueOf(df.format(group_total_price)));
		map.put("error", error);
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

	private Set<Long> genericAreaIds(Long id) {
		Set<Long> ids = new HashSet();
		if (id != null) {
			ids.add(id);
			Map<String, Object> params = Maps.newHashMap();
			params.put("parent", id);
			
			List id_list = this.groupAreaService.queryPageList(params);
			
			ids.addAll(id_list);
			for (int i = 0; i < id_list.size(); i++) {
				Long cid = CommUtil.null2Long(id_list.get(i));
				Set<Long> cids = genericAreaIds(cid);
				ids.add(cid);
				ids.addAll(cids);
			}
		}
		return ids;
	}

	private Set<Long> getareaIds(Long id) {
		Set ids = new HashSet();
		if (id != null) {
			ids.add(id);
			Map<String, Object> params = Maps.newHashMap();
			params.put("parent", id);
			
			List id_list = this.groupAreaService.queryPageList(params);
			
			ids.addAll(id_list);
		}
		return ids;
	}

	private Set<Long> getgcIds(Long id) {
		Set ids = new HashSet();
		if (id != null) {
			ids.add(id);
			Map<String, Object> params = Maps.newHashMap();
			params.put("parent_id", id);
			
			List<GroupClass> id_list = this.groupClassService.queryPageList(params);
			
			for (int i = 0; i < id_list.size(); i++) {
				Long cid = CommUtil.null2Long(id_list.get(i).getId());
				Set cids = getgcIds(cid);
				ids.add(cid);
				ids.addAll(cids);
			}
		}
		return ids;
	}*/
}
