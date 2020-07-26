package com.redpigmall.view.web.action;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
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
import com.redpigmall.api.tools.ClusterSyncTools;
import com.redpigmall.api.tools.CommUtil;
import com.redpigmall.domain.Area;
import com.redpigmall.domain.Goods;
import com.redpigmall.domain.GoodsClass;
import com.redpigmall.domain.Group;
import com.redpigmall.domain.GroupArea;
import com.redpigmall.domain.GroupAreaInfo;
import com.redpigmall.domain.GroupClass;
import com.redpigmall.domain.GroupFloor;
import com.redpigmall.domain.GroupGoods;
import com.redpigmall.domain.GroupIndex;
import com.redpigmall.domain.GroupLifeGoods;
import com.redpigmall.domain.GroupPriceRange;
import com.redpigmall.domain.OrderForm;
import com.redpigmall.domain.OrderFormLog;
import com.redpigmall.domain.Store;
import com.redpigmall.domain.User;
import com.redpigmall.lucene.LuceneResult;
import com.redpigmall.lucene.RedPigLuceneUtil;
import com.redpigmall.view.web.action.base.BaseAction;


/**
 * 
 * <p>
 * Title: RedPigGroupViewAction.java
 * </p>
 * 
 * <p>
 * Description:团购管理控制器，超级后台用来发起团购、审核团购商品，添加团购商品类目、价格区间等等
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
public class RedPigGroupViewAction extends BaseAction{
	
	/**
	 * 团购
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @param gc_id
	 * @param gpr_id
	 * @param ga_id
	 * @param cga_id
	 * @param type
	 * @param group_area_id
	 * @return
	 */
	@RequestMapping({ "/group/index" })
	public ModelAndView group(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,
			String orderType, String gc_id, String gpr_id, String ga_id,
			String cga_id, String type, String group_area_id) {
		ModelAndView mv = new RedPigJModelAndView("group/group.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		if (("".equals(type)) || ("life".equals(type)) || (type == null)) {
			mv = new RedPigJModelAndView("group/group_life_index.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			this.areaViewTools.getUserAreaInfo(request, mv);
			this.areaViewTools.setDefaultArea(request, group_area_id);
			mv.addObject("group_area_id", group_area_id);
			Map<String, Object> params = Maps.newHashMap();
			
			params.put("group_type", 1);
			params.put("status", 0);
			
			List<Group> groups = this.groupService.queryPageList(params);
			
			if (groups.size() > 0) {
				
				Map<String,Object> maps= this.redPigQueryTools.getParams(currentPage, orderBy, orderType);
				maps.put("group_id", groups.get(0));
				
				if ((gc_id != null) && (!gc_id.equals(""))) {
					maps.put("gg_gc_id", CommUtil.null2Long(gc_id));
				}
				
				if ((ga_id != null) && (!ga_id.equals(""))) {
					GroupArea ga = this.groupAreaService.selectByPrimaryKey(CommUtil.null2Long(ga_id));
					Set<Long> ids = null;
					if (ga != null) {
						ids = genericAreaIds(ga.getId());
					}
					if ((ids != null) && (ids.size() > 0)) {
						maps.put("gg_ga_ids", ids);
					}
					mv.addObject("ga_id", ga_id);
					mv.addObject("areas", ga.getChilds());
				}
				GroupPriceRange gpr = this.groupPriceRangeService.selectByPrimaryKey(CommUtil.null2Long(gpr_id));
				if (gpr != null) {
					maps.put("group_price_more_than_equal", BigDecimal.valueOf(gpr.getGpr_begin()));
					maps.put("group_price_less_than_equal", BigDecimal.valueOf(gpr.getGpr_end()));
				}
				
				maps.put("group_status", 1);
				
				maps.put("add_Time_more_than_equal", new Date());
				maps.put("add_Time_less_than_equal", new Date());
				
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
			}
			params.clear();
			params.put("common", true);
			
			List<Area> hot_areas = this.areaService.queryPageList(params);
			
			mv.addObject("hot_areas", hot_areas);
		}
		if ("goods".equals(type)) {
			Map<String, Object> params = Maps.newHashMap();
			params.put("beginTime_more_than_equal", new Date());
			params.put("endTime_less_than_equal", new Date());
			params.put("group_type", 0);
			params.put("status", 0);
			
			List<Group> groups = this.groupService.queryPageList(params);
			
			if (groups.size() > 0) {
				
				Map<String,Object> maps= this.redPigQueryTools.getParams(currentPage, orderBy, orderType);
				
				maps.put("group_id", groups.get(0).getId());
				
				if ((gc_id != null) && (!gc_id.equals(""))) {
					maps.put("gg_gc_id", CommUtil.null2Long(gc_id));
				}
				
				if ((ga_id != null) && (!ga_id.equals(""))) {
					GroupArea ga = this.groupAreaService.selectByPrimaryKey(CommUtil.null2Long(ga_id));
					
					Set<Long> ids = null;
					if (ga != null) {
						ids = genericAreaIds(ga.getId());
					}
					if ((ids != null) && (ids.size() > 0)) {
						Map paras = Maps.newHashMap();
						if ((cga_id != null) && (!cga_id.equals(""))) {
							maps.put("gg_ga_ids", genericAreaIds(CommUtil.null2Long(cga_id)));
						} else {
							maps.put("gg_ga_ids", ids);
						}
					}
					
					mv.addObject("ga_id", ga_id);
					if (ga != null) {
						mv.addObject("areas", ga.getChilds());
					}
				}
				GroupPriceRange gpr = this.groupPriceRangeService.selectByPrimaryKey(CommUtil.null2Long(gpr_id));
				if (gpr != null) {
					maps.put("gg_price_more_than_equal", BigDecimal.valueOf(gpr.getGpr_begin()));
					maps.put("gg_price_less_than_equal", BigDecimal.valueOf(gpr.getGpr_end()));
				}
				maps.put("gg_status", 1);
				
				maps.put("endTime_less_than_equal", new Date());
				maps.put("beginTime_more_than_equal", new Date());
				
				maps.put("gg_goods_goods_status", 0);
				
				IPageList pList = this.groupGoodsService.list(maps);
				CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
				
				maps.clear();
				maps.put("gc_type", 0);
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
			}
		}
		Map<String,Object> params = Maps.newHashMap();
		params.put("gid_default", 1);
		
		List<GroupIndex> gilist = this.groupindexService.queryPageList(params);
		
		if ((gilist != null) && (gilist.size() > 0)) {
			mv.addObject("obj", gilist.get(0));
		}
		mv.addObject("groupViewTools", this.groupViewTools);
		mv.addObject("areaViewTools", this.areaViewTools);
		mv.addObject("cga_id", cga_id);
		mv.addObject("type", type);
		mv.addObject("order_type", CommUtil.null2String(orderBy) + "_" + CommUtil.null2String(orderType));
		return mv;
	}
	
	/**
	 * 团购head部分
	 * @param request
	 * @param response
	 * @param type
	 * @param keyword
	 * @param group_area_id
	 * @return
	 */
	@RequestMapping({ "/group/head" })
	public ModelAndView group_head(HttpServletRequest request,
			HttpServletResponse response, String type, String keyword,
			String group_area_id) {
		ModelAndView mv = new RedPigJModelAndView("group/group_head.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		String ga_id = CommUtil.null2String(request.getSession(false)
				.getAttribute("default_area_id"));
		if ((ga_id != null) && (!ga_id.equals(""))) {
			Area area = this.AreaService.selectByPrimaryKey(CommUtil.null2Long(ga_id));
			if (area != null) {
				if (area.getLevel() == 1) {
					mv.addObject("city", area);
				}
				if (area.getLevel() == 2) {
					mv.addObject("city", area.getParent());
				}
			}
		}
		mv.addObject("group_area_id", group_area_id);
		mv.addObject("type", type);
		mv.addObject("keyword", keyword);
		return mv;
	}
	
	/**
	 * 团购信息
	 * @param request
	 * @param response
	 * @param id
	 * @return
	 */
	@RequestMapping({ "/group/view" })
	public ModelAndView group_view(HttpServletRequest request,
			HttpServletResponse response, String id) {
		ModelAndView mv = new RedPigJModelAndView("group/group_view.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		GroupGoods obj = this.groupGoodsService.selectByPrimaryKey(CommUtil
				.null2Long(id));
		if (obj == null) {
			mv = new RedPigJModelAndView("error.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "您所查看的商品已不存在");
			mv.addObject("url", CommUtil.getURL(request) + "/index");
			return mv;
		}
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
			params.put("add_Time_less_than_equal", new Date());
			params.put("add_Time_more_than_equal", new Date());
			params.put("status", 0);
			params.put("group_type", 0);
			
			List<Group> groups = this.groupService.queryPageList(params);
			
			if (groups.size() > 0) {
				Map<String,Object> maps= this.redPigQueryTools.getParams("1",4,"gg_recommend", "desc");
				maps.put("gg_status", 1);
				maps.put("group_id", obj.getGroup().getId());
				maps.put("id_no", obj.getId());
				maps.put("add_Time_less_than_equal", new Date());
				maps.put("add_Time_more_than_equal", new Date());
				
				IPageList pList = this.groupGoodsService.list(maps);
				
				mv.addObject("hot_ggs", pList.getResult());
				mv.addObject("group", groups.get(0));
				mv.addObject("groupViewTools", this.groupViewTools);
			}
		} else {
			mv = new RedPigJModelAndView("error.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "团购商品参数错误");
			mv.addObject("url", CommUtil.getURL(request) + "/index");
		}
		Map<String,Object> params = Maps.newHashMap();
		params.put("gid_default", 1);
		
		List<GroupIndex> gilist = this.groupindexService.queryPageList(params);
		
		if ((gilist != null) && (gilist.size() > 0)) {
			mv.addObject("gi", gilist.get(0));
		}
		params.clear();
		params.put("gc_type", 1);
		params.put("parent", -1);
		params.put("orderBy", "gc_sequence");
		params.put("orderType", "asc");
		
		List<GroupClass> gcs = this.groupClassService.queryPageList(params);
		
		mv.addObject("gcs", gcs);
		mv.addObject("groupViewTools", this.groupViewTools);
		return mv;
	}
	
	/**
	 * 团购页面详情
	 * @param request
	 * @param response
	 * @param id
	 * @return
	 */
	@RequestMapping({ "/grouplife/view" })
	public ModelAndView grouplife_view(HttpServletRequest request,
			HttpServletResponse response, String id) {
		ModelAndView mv = new RedPigJModelAndView("group/grouplife_view.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		GroupLifeGoods obj = this.groupLifeGoodsService.selectByPrimaryKey(CommUtil.null2Long(id));
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
			String desc = obj.getGg_describe();
			if ((desc != null) && (!desc.equals(""))) {
				Map<String, Object> map = Maps.newHashMap();
				map = JSON.parseObject(desc);
				mv.addObject("desc", map);
			}
			mv.addObject("obj", obj);
			Map<String, Object> params = Maps.newHashMap();
			
			params.put("status", 0);
			params.put("group_type", 1);
			
			List<Group> groups = this.groupService.queryPageList(params);
			
			if (groups.size() > 0) {
				Map<String,Object> maps= this.redPigQueryTools.getParams("1",4,  "group_recommend", "desc");
				maps.put("group_status", 1);
				maps.put("group_id", obj.getGroup().getId());
				maps.put("id_no", obj.getId());
				
				IPageList pList = this.groupLifeGoodsService.list(maps);
				mv.addObject("hot_ggs", pList.getResult());
				mv.addObject("group", groups.get(0));
				mv.addObject("groupViewTools", this.groupViewTools);
			}
		} else {
			mv = new RedPigJModelAndView("error.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "团购商品参数错误");
			mv.addObject("url", CommUtil.getURL(request) + "/index");
		}
		Map<String,Object> params = Maps.newHashMap();
		params.put("gid_default", 1);
		
		List<GroupIndex> gilist = this.groupindexService.queryPageList(params);
		
		if ((gilist != null) && (gilist.size() > 0)) {
			mv.addObject("gi", gilist.get(0));
		}
		params.clear();
		params.put("gc_type", 1);
		params.put("parent", -1);
		params.put("orderBy", "gc_sequence");
		params.put("orderType", "asc");
		
		List<GroupClass> gcs = this.groupClassService.queryPageList(params);
		
		mv.addObject("gcs", gcs);
		mv.addObject("groupViewTools", this.groupViewTools);
		return mv;
	}

	@RequestMapping({ "/group/nav" })
	public ModelAndView group_nav(HttpServletRequest request,
			HttpServletResponse response, String type) {
		ModelAndView mv = new RedPigJModelAndView("group/group_nav.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		Map<String, Object> params = Maps.newHashMap();
		params.put("display", Boolean.valueOf(true));
		params.put("parent", -1);
		params.put("orderBy", "sequence");
		params.put("orderType", "asc");
		
		List<GoodsClass> gcs = this.goodsClassService.queryPageList(params, 0, 12);
		
		mv.addObject("gcs", gcs);
		mv.addObject("navTools", this.navTools);
		mv.addObject("type", CommUtil.null2String(request.getParameter("type")));
		mv.addObject("gcViewTools", this.gcViewTools);
		return mv;
	}

	@RequestMapping({ "/group_evaluation" })
	public ModelAndView group_evaluation(HttpServletRequest request,
			HttpServletResponse response, String id, String goods_id,
			String currentPage) {
		Store store = this.storeService.selectByPrimaryKey(CommUtil.null2Long(id));
		ModelAndView mv = new RedPigJModelAndView("group/group_evaluate.html",
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
		return mv;
	}

	@RequestMapping({ "/group_order" })
	public ModelAndView group_order(HttpServletRequest request,
			HttpServletResponse response, String id, String goods_id,
			String currentPage) {
		ModelAndView mv = new RedPigJModelAndView("group/group_order.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		
		Map<String,Object> maps= this.redPigQueryTools.getParams(currentPage,8, "addTime", "desc");
		maps.put("evaluate_goods_id", CommUtil.null2Long(goods_id));
		
		IPageList pList = this.evaluateService.list(maps);
		CommUtil.saveIPageList2ModelAndView(CommUtil.getURL(request)
				+ "/group_order.html", "", "", pList, mv);
		return mv;
	}
	
	/**
	 * 生活类团购订单购买
	 * @param request
	 * @param response
	 * @param gid
	 * @return
	 */
	@SecurityMapping(title = "生活类团购订单购买", value = "/life_order*", rtype = "buyer", rname = "团购", rcode = "buyer_group", rgroup = "团购")
	@RequestMapping({ "/life_order" })
	public ModelAndView life_order(HttpServletRequest request,
			HttpServletResponse response, String gid) {
		ModelAndView mv = new RedPigJModelAndView("group/life_order.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder
				.getCurrentUser().getId());
		GroupLifeGoods group = this.groupLifeGoodsService.selectByPrimaryKey(CommUtil
				.null2Long(gid));
		if (group != null) {
			if (!user.getId().equals(group.getUser().getId())) {
				if (group.getGroup_count() != 0) {
					if ((group.getGroup_count() <= group.getGroupInfos().size())
							&& (group.getEndTime().before(new Date()))) {
						mv = new RedPigJModelAndView("error.html",
								this.configService.getSysConfig(),
								this.userConfigService.getUserConfig(), 1,
								request, response);
						mv.addObject("op_title", "团购已到期或已售完。");
						mv.addObject("url", CommUtil.getURL(request)
								+ "/group/index?type=life");
					}
				}
				if ((user.getMobile() != null)
						&& (!"".equals(user.getMobile()))) {
					mv.addObject("user", user);
					mv.addObject("obj", group);
					String orderForm_session = CommUtil.randomString(32);
					request.getSession(false).setAttribute("orderForm_session",
							orderForm_session);
					mv.addObject("orderForm_session", orderForm_session);
				} else {
					mv = new RedPigJModelAndView("error.html",
							this.configService.getSysConfig(),
							this.userConfigService.getUserConfig(), 1, request,
							response);
					mv.addObject("op_title", "请先绑定您的手机");
					mv.addObject("url", CommUtil.getURL(request)
							+ "/buyer/account_mobile");
				}
			} else {
				mv = new RedPigJModelAndView("error.html",
						this.configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 1, request,
						response);
				mv.addObject("op_title", "无法购买自己的团购商品");
				mv.addObject("url", CommUtil.getURL(request)
						+ "/group/index?type=life");
			}
		} else {
			mv = new RedPigJModelAndView("error.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "您所访问的团购不存在");
			mv.addObject("url", CommUtil.getURL(request)
					+ "/group/index?type=life");
		}
		return mv;
	}
	
	/**
	 * 生活类团购订单保存
	 * @param request
	 * @param response
	 * @param orderForm_session
	 * @param group_id
	 * @param order_count
	 * @return
	 */
	@SecurityMapping(title = "生活类团购订单保存", value = "/life_order_save*", rtype = "buyer", rname = "团购", rcode = "buyer_group", rgroup = "团购")
	@RequestMapping({ "/life_order_save" })
	public ModelAndView life_order_saveEntity(HttpServletRequest request,
			HttpServletResponse response, String orderForm_session,
			String group_id, String order_count) {
		if (CommUtil.null2Int(order_count) <= 0) {
			order_count = "1";
		}
		ModelAndView mv = new RedPigJModelAndView("order_pay.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder
				.getCurrentUser().getId());
		mv.addObject("user",user);
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
				orderForm.setInvoiceType(0);
				orderForm.setOrder_main(0);
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
				mv.addObject("from", "grouplife");
			} else {
				mv = new RedPigJModelAndView("error.html",
						this.configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 1, request,
						response);
				mv.addObject("op_title", "禁止重复提交");
				mv.addObject("url", CommUtil.getURL(request)
						+ "/group/index?type=life");
				request.getSession(false).removeAttribute("orderForm_session");
			}
		} else {
			mv = new RedPigJModelAndView("error.html",
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
	
	/**
	 * 团购订单数量
	 * @param request
	 * @param response
	 * @param group_id
	 * @param count
	 */
	@SecurityMapping(title = "团购订单数量", value = "/group_count_adjust*", rtype = "user", rname = "团购", rcode = "buyer_group", rgroup = "团购")
	@RequestMapping({ "/group_count_adjust" })
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
	/**
	 * 团购商品搜索
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @param gc_id
	 * @param gpr_id
	 * @param ga_id
	 * @param gainfo_id
	 * @param type
	 * @param keyword
	 * @return
	 */
	@RequestMapping({ "/grouplife/search" })
	public ModelAndView grouplife_search(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,
			String orderType, String gc_id, String gpr_id, String ga_id,
			String gainfo_id, String type, String keyword) {
		ModelAndView mv = new RedPigJModelAndView("group/search_grouplife_list.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		GroupClass groupClass = null;
		if ((gc_id != null) && (!gc_id.equals(""))) {
			groupClass = this.groupClassService.selectByPrimaryKey(CommUtil
					.null2Long(gc_id));
		}
		String path = ClusterSyncTools.getClusterRoot() + File.separator
				+ "luence" + File.separator + "lifegoods";
		RedPigLuceneUtil lucene = RedPigLuceneUtil.instance();
		RedPigLuceneUtil.setIndex_path(path);
		boolean order_type = true;
		String order_by = "";
		String begin_price = null;
		String end_price = null;
		GroupPriceRange gpr = this.groupPriceRangeService.selectByPrimaryKey(CommUtil
				.null2Long(gpr_id));
		if (gpr != null) {
			begin_price = CommUtil.null2String(Integer.valueOf(gpr
					.getGpr_begin()));
			end_price = CommUtil.null2String(Integer.valueOf(gpr.getGpr_end()));
		}
		if (CommUtil.null2String(orderType).equals("asc")) {
			order_type = false;
		}
		if (CommUtil.null2String(orderType).equals("")) {
			orderType = "desc";
		}
		if (CommUtil.null2String(orderBy).equals("group_price")) {
			order_by = "store_price";
		}
		if (CommUtil.null2String(orderBy).equals("goods_salenum")) {
			order_by = "goods_salenum";
		}
		if (CommUtil.null2String(orderBy).equals("goods_rate")) {
			order_by = "goods_rate";
		}
		if (CommUtil.null2String(orderBy).equals("goods_addTime")) {
			order_by = "addTime";
		}
		Sort sort = null;
		if (!CommUtil.null2String(order_by).equals("")) {
			sort = new Sort(new SortField(order_by, SortField.Type.DOUBLE,
					order_type));
		}
		List<Long> ga_ids = null;
		if ((ga_id == null) || (ga_id.equals(""))) {
			ga_id = CommUtil.null2String(request.getSession(false)
					.getAttribute("default_area_id"));
		}
		List<Long> gai_ids = null;
		if ((ga_id != null) && (!ga_id.equals(""))) {
			gai_ids = genericAreaInfoIdsList(CommUtil.null2Long(ga_id));
		}
		List<Long> gc_ids = genericClassIds(groupClass);
		LuceneResult pList = lucene.searchGroupLife(keyword,
				CommUtil.null2Int(currentPage), gai_ids, gc_ids, sort,
				begin_price, end_price);
		CommUtil.saveLucene2ModelAndView(pList, mv);
		Map<String, Object> params = Maps.newHashMap();
		List<GroupClass> gcs2 = Lists.newArrayList();
		if (groupClass != null) {
			if (groupClass.getGc_level() != 2) {
				params.put("parent_id", groupClass.getId());
				params.put("gc_type", 1);
				params.put("orderBy", "gc_sequence");
				params.put("orderType", "asc");
				
				gcs2 = this.groupClassService.queryPageList(params);
				
			} else {
				gcs2 = groupClass.getParent().getChilds();
			}
		}
		params.clear();
		params.put("orderBy", "gpr_begin");
		params.put("orderType", "asc");
		
		List<GroupPriceRange> gprs = this.groupPriceRangeService.queryPageList(params);
		
		params.clear();
		params.put("add_Time_less_than_equal", new Date());
		params.put("add_Time_more_than_equal", new Date());
		params.put("group_type", 1);
		
		List<Group> groups = this.groupService.queryPageList(params);
		
		params.clear();
		params.put("group_type", 1);
		params.put("parent", -1);
		params.put("orderBy", "gc_sequence");
		params.put("orderType", "asc");
		
		List<GroupClass> gcs = this.groupClassService.queryPageList(params, 0, 8);
		
		if ((ga_id == null) || (ga_id.equals(""))) {
			ga_id = CommUtil.null2String(request.getSession(false)
					.getAttribute("default_area_id"));
		}
		
		if ((ga_id != null) && (!ga_id.equals(""))) {
			params.clear();
			Area area = this.areaService.selectByPrimaryKey(CommUtil.null2Long(ga_id));
			List<Area> gailist = Lists.newArrayList();
			if (area.getLevel() == 2) {
				params.put("parent", area.getParent().getId());
				params.put("level", 2);
				
				gailist = this.areaService.queryPageList(params);
			} else {
				gailist = area.getChilds();
			}
			mv.addObject("gailist", gailist);
			if (area.getLevel() == 1) {
				this.areaViewTools.getUserAreaInfo(request, mv);
				this.areaViewTools.setDefaultArea(request,
						ga_id != null ? ga_id : "");
				mv.addObject("group_area_id", ga_id);
			} else {
				this.areaViewTools.getUserAreaInfo(request, mv);
				this.areaViewTools.setDefaultArea(request, area.getParent()
						.getId() != null ? area.getParent().getId().toString()
						: "");
				mv.addObject("group_area_id", area.getParent().getId());
			}
		}
		mv.addObject("ga_id", ga_id);
		mv.addObject("keyword", keyword);
		mv.addObject("gprs", gprs);
		mv.addObject("gcs", gcs);
		mv.addObject("gcs2", gcs2);
		
		if (groups.size() > 0) {
			mv.addObject("group", groups.get(0));
		}
		
		if ((orderBy == null) || (orderBy.equals(""))) {
			orderBy = "addTime";
		}
		if ((orderType == null) || (orderType.equals(""))) {
			orderType = "desc";
		}
		
		params.clear();
		params.put("gid_default", 1);
		
		List<GroupIndex> gilist = this.groupindexService.queryPageList(params);
		
		if ((gilist != null) && (gilist.size() > 0)) {
			mv.addObject("obj", gilist.get(0));
		}
		params.clear();
		params.put("common", true);
		
		List<Area> hot_areas = this.areaService.queryPageList(params);
		
		mv.addObject("hot_areas", hot_areas);
		mv.addObject("gc_id", gc_id);
		mv.addObject("gpr_id", gpr_id);
		mv.addObject("groupViewTools", this.groupViewTools);
		mv.addObject("type", "life");
		mv.addObject("order_type", CommUtil.null2String(orderBy) + "_" + CommUtil.null2String(orderType));
		mv.addObject("orderType", orderType);
		mv.addObject("gcViewTools", this.gcViewTools);
		return mv;
	}

	@RequestMapping({ "/group/search" })
	public ModelAndView group_search(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,
			String orderType, String gc_id, String gpr_id, String type,
			String keyword) {
		ModelAndView mv = new RedPigJModelAndView("group/search_group_list.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		GroupClass groupClass = null;
		if ((gc_id != null) && (!gc_id.equals(""))) {
			groupClass = this.groupClassService.selectByPrimaryKey(CommUtil
					.null2Long(gc_id));
		}
		String path = ClusterSyncTools.getClusterRoot() + File.separator
				+ "luence" + File.separator + "groupgoods";
		RedPigLuceneUtil lucene = RedPigLuceneUtil.instance();
		RedPigLuceneUtil.setIndex_path(path);
		boolean order_type = true;
		String order_by = "";
		String begin_price = null;
		String end_price = null;
		GroupPriceRange gpr = this.groupPriceRangeService.selectByPrimaryKey(CommUtil
				.null2Long(gpr_id));
		if (gpr != null) {
			begin_price = CommUtil.null2String(Integer.valueOf(gpr
					.getGpr_begin()));
			end_price = CommUtil.null2String(Integer.valueOf(gpr.getGpr_end()));
		}
		if (CommUtil.null2String(orderType).equals("asc")) {
			order_type = false;
		}
		if (CommUtil.null2String(orderType).equals("")) {
			orderType = "desc";
		}
		if (CommUtil.null2String(orderBy).equals("group_price")) {
			order_by = "store_price";
		}
		if (CommUtil.null2String(orderBy).equals("goods_salenum")) {
			order_by = "goods_salenum";
		}
		if (CommUtil.null2String(orderBy).equals("goods_collect")) {
			order_by = "goods_collect";
		}
		if (CommUtil.null2String(orderBy).equals("goods_addTime")) {
			order_by = "addTime";
		}
		
		Sort sort = null;
		if (!CommUtil.null2String(order_by).equals("")) {
			sort = new Sort(new SortField(order_by, SortField.Type.DOUBLE,
					order_type));
		}
		List<Long> gc_ids = genericClassIds(groupClass);

		LuceneResult pList = lucene.searchGroupGoods(keyword,
				CommUtil.null2Int(currentPage), sort, gc_ids, begin_price,
				end_price);
		
		CommUtil.saveLucene2ModelAndView(pList, mv);
		Map<String,Object> params = Maps.newHashMap();
		params.put("orderBy", "gpr_begin");
		params.put("orderType", "asc");
		
        
		List<GroupPriceRange> gprs = this.groupPriceRangeService.queryPageList(params);
		
		params.clear();
		params.put("add_Time_less_than_equal", new Date());
		params.put("add_Time_more_than_equal", new Date());
		params.put("group_type", 0);
		
		List<Group> groups = this.groupService.queryPageList(params);
		
		mv.addObject("keyword", keyword);
		mv.addObject("gprs", gprs);
		if (groups.size() > 0) {
			mv.addObject("group", groups.get(0));
		}
		if ((orderBy == null) || (orderBy.equals(""))) {
			orderBy = "addTime";
		}
		if ((orderType == null) || (orderType.equals(""))) {
			orderType = "desc";
		}
		params.clear();
		params.put("gid_default", 1);
		
		List<GroupIndex> gilist = this.groupindexService.queryPageList(params);
		
		if ((gilist != null) && (gilist.size() > 0)) {
			mv.addObject("obj", gilist.get(0));
		}
		
		params.clear();
		params.put("gc_type", 0);
		params.put("parent", -1);
		params.put("orderBy", "gc_sequence");
		params.put("orderType", "asc");
		
		List<GroupClass> gcs = this.groupClassService.queryPageList(params, 0, 8);
		
		mv.addObject("gcs", gcs);
		mv.addObject("groupViewTools", this.groupViewTools);
		mv.addObject("gc_id", gc_id);
		mv.addObject("gpr_id", gpr_id);
		mv.addObject("groupViewTools", this.groupViewTools);
		mv.addObject("type", "goods");
		mv.addObject("order_type", CommUtil.null2String(orderBy) + "_"
				+ CommUtil.null2String(orderType));
		mv.addObject("orderType", orderType);
		return mv;
	}
	
	/**
	 * 团购楼层
	 * @param request
	 * @param response
	 * @param group_area_id
	 * @return
	 */
	@RequestMapping({ "/group/floor_list" })
	public ModelAndView group_floor_list(HttpServletRequest request,
			HttpServletResponse response, String group_area_id) {
		ModelAndView mv = new RedPigJModelAndView("group/group_floor_list.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		
		this.areaViewTools.getUserAreaInfo(request, mv);
		this.areaViewTools.setDefaultArea(request,group_area_id != null ? group_area_id : "");
		mv.addObject("group_area_id", group_area_id != null ? group_area_id : "");
		Map<String,Object> params = Maps.newHashMap();
		params.put("common", 1);
		
		List<GroupFloor> gf_list = this.groupFloorService.queryPageList(params);
		
		mv.addObject("gf_list", gf_list);
		mv.addObject("groupViewTools", this.groupViewTools);
		return mv;
	}

	@RequestMapping({ "/group/city_index" })
	public ModelAndView group_city_index(HttpServletRequest request,
			HttpServletResponse response, String type) {
		ModelAndView mv = new RedPigJModelAndView("group/group_city_index.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		this.areaViewTools.getUserAreaInfo(request, mv);
		
		Map<String,Object> maps = Maps.newHashMap();
        maps.put("level", 0);
        
		List<Area> area_list = this.areaService.queryPageList(maps);
		
		mv.addObject("area_list", area_list);
		maps.clear();
		maps.put("level", 1);
		
		List<Area> area_letter_list = this.areaService.queryPageList(maps);
		
		mv.addObject("area_letter_list", area_letter_list);
		maps.clear();
		maps.put("gc_level",0);
		List<GroupClass> groupClass_list = this.groupClassService.queryPageList(maps);
		
		mv.addObject("groupClass_list", groupClass_list);
		maps.clear();
		maps.put("gc_level",1);
		
		List<GroupClass> c_gc_list = this.groupClassService.queryPageList(maps);
		maps.clear();
		maps.put("gid_default",1);
		
		List<GroupIndex> gilist = this.groupindexService.queryPageList(maps);
		
		if ((gilist != null) && (gilist.size() > 0)) {
			mv.addObject("obj", gilist.get(0));
		}
		maps.clear();
		maps.put("gc_type",1);
		maps.put("parent", 1);
		maps.put("orderBy", "gc_sequence");
		maps.put("orderType", "asc");
		
		List<GroupClass> gcs = this.groupClassService.queryPageList(maps);
		
		maps.clear();
		maps.put("common",true);
		List<Area> hot_areas = this.areaService.queryPageList(maps);
		
		mv.addObject("hot_areas", hot_areas);
		mv.addObject("gcs", gcs);
		mv.addObject("c_gc_list", c_gc_list);
		mv.addObject("groupViewTools", this.groupViewTools);
		mv.addObject("type", type);
		return mv;
	}

	@RequestMapping({ "/group_city_search" })
	public void group_city_search(HttpServletRequest request,
			HttpServletResponse response, String area_name) {
		List<Map> list = Lists.newArrayList();
		if ((area_name != null) && (!"".equals(area_name))) {
			Map<String, Object> map = Maps.newHashMap();
			map.put("area_name_like", area_name);
			map.put("level", 1);
			
			List<Area> area_list = this.areaService.queryPageList(map);
			
			for (Area area : area_list) {
				Map m = Maps.newHashMap();
				m.put("id", area.getId());
				m.put("name", area.getAreaName());
				list.add(m);
			}
		}
		String temp = JSON.toJSONString(list);
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

	@RequestMapping({ "/group/getSecondArea" })
	public void getSecondArea(HttpServletRequest request,
			HttpServletResponse response, String id) {
		List<Area> arealist = Lists.newArrayList();
		List<HashMap<String, String>> areas = Lists.newArrayList();
		Map<String, Object> params = Maps.newHashMap();
		if ((id != null) && (!id.equals(""))) {
			params.put("parent", CommUtil.null2Long(id));
			
			arealist = this.areaService.queryPageList(params);
			
		}
		if (arealist.size() > 0) {
			for (Area area : arealist) {
				HashMap map = Maps.newHashMap();
				map.put("id", area.getId());
				map.put("areaName", area.getAreaName());
				areas.add(map);
			}
		}
		String temp = JSON.toJSONString(areas);
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

	private List<Long> genericAreaInfoIdsList(Long id) {
		List<Long> area_info_ids = Lists.newArrayList();
		Set<Long> area_ids = new HashSet();
		if (id != null) {
			area_ids.add(id);
			Map<String, Object> params = Maps.newHashMap();
			params.put("parent_id", id);
			
			List<Area> id_list = this.AreaService.queryPageList(params);
			
			for (Area area : id_list) {
				area_ids.add(area.getId());
			}
			
			for (int i = 0; i < id_list.size(); i++) {
				Long cid = CommUtil.null2Long(id_list.get(i).getId());
				List<Long> cids = genericAreaInfoIdsList(cid);
				area_ids.add(cid);
				area_ids.addAll(cids);
			}
			
			Map<String,Object> ids_map = Maps.newHashMap();
			ids_map.put("area_ids", area_ids);
			
			List<GroupAreaInfo> gais_ids = this.groupAreaInfoService.queryPageList(ids_map);
			
			for (int i = 0; i < gais_ids.size(); i++) {
				Long cid = CommUtil.null2Long(gais_ids.get(i).getId());
				area_info_ids.add(cid);
			}
		} else {
			return null;
		}
		return area_info_ids;
	}

	private List<Long> genericClassIds(GroupClass gc) {
		List<Long> ids = Lists.newArrayList();
		if (gc != null) {
			ids.add(gc.getId());
			for (GroupClass child : gc.getChilds()) {
				List<Long> cids = genericClassIds(child);
				for (Long cid : cids) {
					ids.add(cid);
				}
				ids.add(child.getId());
			}
		}
		return ids;
	}
}
