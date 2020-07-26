package com.redpigmall.module.circle.view.action;

import java.io.IOException;
import java.io.PrintWriter;
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
import com.redpigmall.module.circle.manage.admin.action.base.BaseAction;
import com.redpigmall.domain.Circle;
import com.redpigmall.domain.CircleClass;
import com.redpigmall.domain.CircleInvitation;
import com.redpigmall.domain.User;

/**
 * 
 * <p>
 * Title: RedPigCircleManageAction.java
 * </p>
 * 
 * <p>
 * Description:
 * 商城圈子控制器，用户可以申请圈子，由平台审核，审核通过后该用户成为该圈子管理员，其他用户可以进入该圈子发布帖子，帖子由圈子管理员审核，
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
@SuppressWarnings({"rawtypes"})
@Controller
public class RedPigCircleViewAction extends BaseAction{
	
	/**
	 * 圈子头部
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping({ "/circle/head" })
	public ModelAndView circle_head(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new RedPigJModelAndView("circle/circle_head.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		String type = CommUtil.null2String(request.getAttribute("type"));
		mv.addObject("type", type);
		return mv;
	}

	/**
	 * 圈子头部
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping({ "/circle/nav" })
	public ModelAndView circle_nav(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new RedPigJModelAndView("circle/circle_nav.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		Map<String, Object> map = Maps.newHashMap();
		map.put("nav_index", Integer.valueOf(1));
		map.put("orderBy", "sequence");
		map.put("orderType", "asc");
		
		List<CircleClass> class_list = this.circleclassService.queryPageList(map, 0, 12);
		
		mv.addObject("class_list", class_list);
		return mv;
	}

	/**
	 * 圈子首页
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping({ "/circle/index" })
	public ModelAndView circle_index(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new RedPigJModelAndView("circle/circle_index.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		Map<String, Object> params = Maps.newHashMap();
		params.put("recommend", Integer.valueOf(1));
		params.put("orderBy", "addTime");
		params.put("orderType", "desc");
		
		List<Circle> circle_recommend = this.circleService.queryPageList(params, 0, 8);
		
		params.clear();
		params.put("status", Integer.valueOf(5));
		params.put("orderBy", "attention_count");
		params.put("orderType", "desc");
		
		List<Circle> circle_hot = this.circleService.queryPageList(params, 0, 6);
		
		params.clear();
		params.put("orderBy", "reply_count");
		params.put("orderType", "desc");
		
		List<CircleInvitation> invitation_hot = this.invitationService.queryPageList(params, 0, 5);
		
		params.clear();
		params.put("orderBy", "addTime");
		params.put("orderType", "asc");
		params.put("recommend", Integer.valueOf(1));
		
		List<CircleClass> ccs = this.circleclassService.queryPageList(params);
		
		if (ccs.size() > 0) {
			params.clear();
			params.put("status", Integer.valueOf(5));
			params.put("class_id", ccs.get(0).getId());
			
			List<Circle> switch_first = this.circleService.queryPageList(params, 0, 9);
			
			mv.addObject("switch_first", switch_first);
		}
		
		int circle_atten_count = 0;
		int invi_count = 0;
		if (SecurityUserHolder.getCurrentUser() != null) {
			User user = this.userService.selectByPrimaryKey(SecurityUserHolder
					.getCurrentUser().getId());
			if (user.getCircle_attention_info() != null) {
				List<Map> attens = JSON.parseArray(
						user.getCircle_attention_info(), Map.class);
				if (attens.size() > 0) {
					circle_atten_count = attens.size();
				}
			}
			params.clear();
			params.put("user_id", user.getId());
			
			List<CircleInvitation> invis = this.invitationService.queryPageList(params);
			
			if (invis.size() > 0) {
				invi_count = invis.size();
			}
		}
		mv.addObject("ccs", ccs);
		mv.addObject("invitation_hot", invitation_hot);
		mv.addObject("circle_atten_count", Integer.valueOf(circle_atten_count));
		mv.addObject("invi_count", Integer.valueOf(invi_count));
		mv.addObject("circle_hot", circle_hot);
		mv.addObject("circle_recommend", circle_recommend);
		mv.addObject("circleViewTools", this.circleViewTools);
		return mv;
	}

	/**
	 * 圈子列表
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping({ "/circle/index_switch" })
	public ModelAndView index_switch(HttpServletRequest request,
			HttpServletResponse response, String cid) {
		ModelAndView mv = new RedPigJModelAndView("circle/circle_index_switch.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		Map<String, Object> params = Maps.newHashMap();
		params.put("status", Integer.valueOf(5));
		params.put("class_id", CommUtil.null2Long(cid));
		
		List<Circle> objs = this.circleService.queryPageList(params, 0, 9);
		
		mv.addObject("objs", objs);
		mv.addObject("circleViewTools", this.circleViewTools);
		return mv;
	}

	/**
	 * 圈子列表
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping({ "/circle/list" })
	public ModelAndView circle_list(HttpServletRequest request,
			HttpServletResponse response, String class_id, String currentPage) {
		ModelAndView mv = new RedPigJModelAndView("circle/search_circle.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		
		Map<String,Object> maps= this.redPigQueryTools.getParams(currentPage,"addTime", "desc");
		
		if ((class_id != null) && (!class_id.equals(""))) {
			
			maps.put("class_id", CommUtil.null2Long(class_id));
			mv.addObject("class_id", class_id);
		}
		
		IPageList pList = this.circleService.list(maps);
		CommUtil.saveIPageList2ModelAndView("", "", null, pList, mv);
		mv.addObject("circleViewTools", this.circleViewTools);
		
		Map<String, Object> params = Maps.newHashMap();
		params.put("recommend", Integer.valueOf(1));
		params.put("status", Integer.valueOf(5));
		
		List<Circle> recommends = this.circleService.queryPageList(params, 0, 5);
		
		mv.addObject("recommends", recommends);
		mv.addObject("circleViewTools", this.circleViewTools);
		int circle_atten_count = 0;
		int invi_count = 0;
		if (SecurityUserHolder.getCurrentUser() != null) {
			User user = this.userService.selectByPrimaryKey(SecurityUserHolder.getCurrentUser().getId());
			
			if (user.getCircle_attention_info() != null) {
				List<Map> attens = JSON.parseArray(
						user.getCircle_attention_info(), Map.class);
				if (attens.size() > 0) {
					circle_atten_count = attens.size();
				}
			}
			
			params.clear();
			params.put("user_id", user.getId());
			
			List<CircleInvitation> invis = this.invitationService.queryPageList(params);
			
			if (invis.size() > 0) {
				invi_count = invis.size();
			}
		}
		mv.addObject("circle_atten_count", Integer.valueOf(circle_atten_count));
		mv.addObject("invi_count", Integer.valueOf(invi_count));
		return mv;
	}

	/**
	 * 圈子列表
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@SuppressWarnings("unused")
	@RequestMapping({ "/circle/detail" })
	public ModelAndView circle_detail(HttpServletRequest request,
			HttpServletResponse response, String id) {
		ModelAndView mv = new RedPigJModelAndView("circle/circle_detail.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		Circle obj = this.circleService.selectByPrimaryKey(CommUtil.null2Long(id));
		if (obj != null) {
			if (obj.getStatus() == 5) {
				Map img_map = JSON.parseObject(obj.getPhotoInfo());
				User owner = this.userService.selectByPrimaryKey(Long.valueOf(obj.getUser_id()));
				Map<String, Object> params = Maps.newHashMap();
				params.put("status", Integer.valueOf(5));
				params.put("recommend", Integer.valueOf(1));
				
				List<Circle> recommends = this.circleService.queryPageList(params, 0, 8);
				
				if (recommends.size() == 0) {
					params.clear();
					params.put("status", Integer.valueOf(5));
					params.put("orderBy", "attention_count");
					params.put("orderType", "desc");
					
					recommends = this.circleService.queryPageList(params, 0, 8);
					
				}
				mv.addObject("recommends", recommends);
				mv.addObject("owner", owner);
				mv.addObject("obj", obj);
				mv.addObject("circleViewTools", this.circleViewTools);
			} else if (obj.getStatus() == 0) {
				mv = new RedPigJModelAndView("error.html",
						this.configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 1, request,
						response);
				mv.addObject("url", "/circle/index");
				mv.addObject("op_title", "该圈子未经过审核，暂不可查看");
			} else if (obj.getStatus() == -1) {
				mv = new RedPigJModelAndView("error.html",
						this.configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 1, request,
						response);
				mv.addObject("url", "/circle/index");
				mv.addObject("op_title", "该圈子违反平台相关规定，现已下线");
			}
		} else {
			mv = new RedPigJModelAndView("error.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("url", "/circle/index");
			mv.addObject("op_title", "您所访问的地址不存在");
		}
		return mv;
	}
	
	/**
	 * 圈子集合
	 * @param request
	 * @param response
	 * @param cid
	 * @param currentPage
	 * @param type
	 * @return
	 */
	@RequestMapping({ "/circle/circle_invitation_list" })
	public ModelAndView circle_invitation_list(HttpServletRequest request,
			HttpServletResponse response, String cid, String currentPage,
			String type) {
		ModelAndView mv = new RedPigJModelAndView(
				"circle/circle_invitation_list.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		Circle cir = this.circleService.selectByPrimaryKey(CommUtil.null2Long(cid));
		
		
		Map<String,Object> maps= this.redPigQueryTools.getParams(currentPage,20, null, null);
		
		maps.put("circle_id", CommUtil.null2Long(cid));
		
		if ((type != null) && (!type.equals("")) && (type.equals("jing"))) {
			maps.put("invitaion_perfect", 1);
			mv.addObject("type", type);
		}
		
		if ((type != null) && (!type.equals("")) && (type.equals("all"))) {
			maps.put("invitaion_top_no", 1);
			mv.addObject("type", type);
		}
		maps.put("orderBy", "invitaion_perfect desc,obj.addTime");
		maps.put("orderType", "desc");
		
		IPageList pList = this.invitationService.list(maps);
		String url = CommUtil.getURL(request)
				+ "/circle/circle_invitation_list";
		mv.addObject("objs", pList.getResult());
		mv.addObject("gotoPageAjaxHTML", CommUtil.showPageAjaxHtml(url, "",
				pList.getCurrentPage(), pList.getPages(), pList.getPageSize()));
		if ((type != null) 
				&& (!type.equals("")) 
				&& (!type.equals("jing"))
				&& (pList.getCurrentPage() <= 1)) {
			Map<String, Object> params = Maps.newHashMap();
			params.put("circle_id", cir.getId());
			params.put("invitaion_top", 1);
			
			List<CircleInvitation> top = this.invitationService.queryPageList(params);
			
			if (top.size() > 0) {
				mv.addObject("top", top.get(0));
			}
		}
		return mv;
	}

	@SecurityMapping(title = "用户圈子关注", value = "/circle/pay_attention*", rtype = "buyer", rname = "用户中心", rcode = "user_circle", rgroup = "圈子访问")
	@RequestMapping({ "/circle/pay_attention" })
	public void pay_attention(HttpServletRequest request,
			HttpServletResponse response, String cid) {
		int code = 100;
		Circle obj = this.circleService.selectByPrimaryKey(CommUtil.null2Long(cid));
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder.getCurrentUser().getId());
		Map<String, Object> map = Maps.newHashMap();
		List<Map> map_list = Lists.newArrayList();
		if ((user.getCircle_attention_info() != null)
				&& (!user.getCircle_attention_info().equals(""))) {
			map_list = JSON.parseArray(user.getCircle_attention_info(),
					Map.class);
			for (Map temp : map_list) {
				if (CommUtil.null2String(temp.get("id")).equals(cid)) {
					code = -100;
					break;
				}
			}
		}
		if (code == 100) {
			map.put("id", obj.getId());
			map.put("name", obj.getTitle());
			map_list.add(map);
			String temp_json = JSON.toJSONString(map_list);
			user.setCircle_attention_info(temp_json);
			this.userService.updateById(user);
			obj.setAttention_count(obj.getAttention_count() + 1);
			this.circleService.updateById(obj);
		}
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		try {
			PrintWriter writer = response.getWriter();
			writer.print(code);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@SecurityMapping(title = "用户取消圈子关注", value = "/circle/cancle_attention*", rtype = "buyer", rname = "用户中心", rcode = "user_circle", rgroup = "圈子访问")
	@RequestMapping({ "/circle/cancle_attention" })
	public void cancle_attention(HttpServletRequest request,
			HttpServletResponse response, String cid) {
		int code = 100;
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder.getCurrentUser().getId());
		Circle obj = this.circleService.selectByPrimaryKey(CommUtil.null2Long(cid));
		List<Map> map_list = Lists.newArrayList();
		List<Map> temp_list = Lists.newArrayList();
		if ((user.getCircle_attention_info() != null)
				&& (!user.getCircle_attention_info().equals(""))) {
			map_list = JSON.parseArray(user.getCircle_attention_info(),
					Map.class);
			for (Map temp : map_list) {
				if (!CommUtil.null2String(temp.get("id")).equals(cid)) {
					temp_list.add(temp);
					code = -100;
				} else {
					code = 100;
				}
			}
			if (temp_list.size() > 0) {
				String temp_json = JSON.toJSONString(temp_list);
				user.setCircle_attention_info(temp_json);
			} else {
				user.setCircle_attention_info(null);
			}
			this.userService.updateById(user);
			obj.setAttention_count(obj.getAttention_count() - 1);
			this.circleService.updateById(obj);
		} else {
			code = -100;
		}
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		try {
			PrintWriter writer = response.getWriter();
			writer.print(code);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	
	@RequestMapping({ "/circle/search_list" })
	public ModelAndView search_list(HttpServletRequest request,
			HttpServletResponse response, String keyword, String type,
			String currentPage) {
		ModelAndView mv = new RedPigJModelAndView("circle/search_circle.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		if ((type == null) || (type.equals(""))) {
			type = "circle";
		}
		if (type.equals("circle")) {
			if (!keyword.equals("")) {
				
				Map<String,Object> maps= this.redPigQueryTools.getParams(currentPage,15,"addTime", "desc");
				maps.put("status", 5);
				
				if ((keyword != null) && (!keyword.equals(""))) {
					maps.put("title_like", CommUtil.null2String(keyword));
				}
				
				IPageList pList = this.circleService.list(maps);
				CommUtil.saveIPageList2ModelAndView("", "", null, pList, mv);
			}
			
			Map<String, Object> params = Maps.newHashMap();
			params.put("recommend", Integer.valueOf(1));
			params.put("status", Integer.valueOf(5));
			
			List<Circle> recommends = this.circleService.queryPageList(params, 0, 5);
			
			mv.addObject("recommends", recommends);
		} else if (type.equals("invitation")) {
			mv = new RedPigJModelAndView("circle/search_invitation.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			if (!keyword.equals("")) {
				Map<String,Object> maps= this.redPigQueryTools.getParams(currentPage,15,"addTime", "desc");
				
				if ((keyword != null) && (!keyword.equals(""))) {
					maps.put("title_like", CommUtil.null2String(keyword));
				}
				
				IPageList pList = this.invitationService.list(maps);
				CommUtil.saveIPageList2ModelAndView("", "", null, pList, mv);
			}
			
			Map<String, Object> params = Maps.newHashMap();
			params.put("orderBy", "reply_count");
			params.put("orderType", "desc");
			
			List<CircleInvitation> hots = this.invitationService.queryPageList(params, 0, 10);
			
			mv.addObject("hots", hots);
		}
		Map<String, Object> params = Maps.newHashMap();
		int circle_atten_count = 0;
		int invi_count = 0;
		if (SecurityUserHolder.getCurrentUser() != null) {
			User user = this.userService.selectByPrimaryKey(SecurityUserHolder
					.getCurrentUser().getId());
			if (user.getCircle_attention_info() != null) {
				List<Map> attens = JSON.parseArray(
						user.getCircle_attention_info(), Map.class);
				if (attens.size() > 0) {
					circle_atten_count = attens.size();
				}
			}
			params.clear();
			params.put("user_id", user.getId());
			
			List<CircleInvitation> invis = this.invitationService.queryPageList(params);
			
			if (invis.size() > 0) {
				invi_count = invis.size();
			}
		}
		mv.addObject("circle_atten_count", Integer.valueOf(circle_atten_count));
		mv.addObject("invi_count", Integer.valueOf(invi_count));
		mv.addObject("type", type);
		mv.addObject("keyword", keyword);
		mv.addObject("circleViewTools", this.circleViewTools);
		return mv;
	}

	@RequestMapping({ "/circle/error" })
	public ModelAndView circle_error(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new RedPigJModelAndView("circle/error.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		mv.addObject("op_title", "系统未开启圈子功能");
		mv.addObject("url", CommUtil.getURL(request) + "/index");
		return mv;
	}
}
