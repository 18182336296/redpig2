package com.redpigmall.module.sns.manage.buyer.action;

import java.io.IOException;
import java.io.PrintWriter;
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
import com.redpigmall.module.sns.manage.base.BaseAction;
import com.redpigmall.domain.Circle;
import com.redpigmall.domain.CircleInvitation;
import com.redpigmall.domain.CircleInvitationReply;
import com.redpigmall.domain.Consult;
import com.redpigmall.domain.Evaluate;
import com.redpigmall.domain.Favorite;
import com.redpigmall.domain.SnsAttention;
import com.redpigmall.domain.User;
import com.redpigmall.domain.UserDynamic;
import com.redpigmall.domain.UserShare;

/**
 * 
 * <p>
 * Title: RedPigSnsBuyerAction.java
 * </p>
 * 
 * <p>
 * Description:用户SNS功能控制器
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
 * @date 2014-11-21
 * 
 * @version redpigmall_b2b2c 2016
 */
@Controller
public class RedPigSnsBuyerAction extends BaseAction{
	
	/**
	 * 买家sns首页
	 * @param request
	 * @param response
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	@SecurityMapping(title = "买家sns首页", value = "/buyer/my_sns_index*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping({ "/buyer/my_sns_index" })
	public ModelAndView my_sns_index(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/usercenter/sns/my_sns_index.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder
				.getCurrentUser().getId());
		mv.addObject("user", user);
		
		Map<String, Object> params = Maps.newHashMap();
		params.put("fromUser_id", CommUtil.null2Long(user.getId()));
		
		List<SnsAttention> tempSnss = this.snsAttentionService.queryPageList(params,0,10);
		
		List<Map<String, Object>> userAttsList = Lists.newArrayList();
		for (SnsAttention sns : tempSnss) {
			Map<String,Object> map = Maps.newHashMap();
			
			map.put("user_id", sns.getToUser().getId());
			map.put("user_name", sns.getToUser().getUserName());
			map.put("sns_time", sns.getAddTime());
			if (sns.getToUser().getPhoto() != null) {
				map.put("user_photo", sns.getToUser().getPhoto().getPath()
						+ "/" + sns.getToUser().getPhoto().getName());
			}
			userAttsList.add(map);
		}
		mv.addObject("userAttsList", userAttsList);
		
		params.clear();
		params.put("toUser_id", CommUtil.null2Long(user.getId()));
		
		tempSnss = this.snsAttentionService.queryPageList(params,0,10);
		
		List<Map<String, Object>> userFansList = Lists.newArrayList();
		for (SnsAttention sns : tempSnss) {
			Map<String, Object> map = Maps.newHashMap();
			map.put("user_id", sns.getFromUser().getId());
			map.put("user_name", sns.getFromUser().getUserName());
			map.put("sns_time", sns.getAddTime());
			if (sns.getFromUser().getPhoto() != null) {
				map.put("user_photo", sns.getFromUser().getPhoto().getPath()
						+ "/" + sns.getFromUser().getPhoto().getName());
			}
			userFansList.add(map);
		}
		mv.addObject("userFansList", userFansList);
		
		mv.addObject("userShare",
				this.snsTools.querylastUserShare(user.getId()));
		
		mv.addObject("fav", this.snsTools.queryLastUserFav(user.getId()));
		
		params.clear();
		params.put("evaluate_user_id", user.getId());
		params.put("evaluate_status", 0);
		
		params.put("evaluate_photos", -1);
		
		List<Evaluate> evas = this.evaluateService.queryPageList(params,0, 2);
				
		mv.addObject("evas", evas);
		params.clear();
		params.put("evaluate_user_id", user.getId());
		params.put("evaluate_status", 0);
		params.put("evaluate_photos_no", -1);
		
		List<Evaluate> evaPhotos = this.evaluateService.queryPageList(params,0, 2);
		
		mv.addObject("evaPhotos", evaPhotos);

		params.clear();
		params.put("user_id", user.getId());
		
		List<CircleInvitation> invitations = this.invitationService.queryPageList(params,0, 1);
		
		if (invitations.size() > 0) {
			mv.addObject("invi", invitations.get(0));
		}
		if ((user.getCircle_attention_info() != null)
				&& (!user.getCircle_attention_info().equals(""))) {
			Set<Long> ids = Sets.newHashSet();
			
			List<Map> maps = JSON.parseArray(user.getCircle_attention_info(),Map.class);
			
			for (Map map : maps) {
				ids.add(CommUtil.null2Long(map.get("id")));
			}
			if (!ids.isEmpty()) {
				params.clear();
				params.put("ids", ids);
				List<Circle> circles = this.circleService.queryPageList(params,0,3);
				
				mv.addObject("circles", circles);
				mv.addObject("circleViewTools", this.circleViewTools);
			}
		}
		params.clear();
		params.put("user_id", user.getId());
		
		List<UserDynamic> userDynamics = this.dynamicService.queryPageList(params,0,1);
		
		if (userDynamics.size() > 0) {
			mv.addObject("userDynamics", userDynamics.get(0));
		}
		mv.addObject("snsTools", this.snsTools);
		return mv;
	}
	
	/**
	 * 买家sns头部
	 * @param request
	 * @param response
	 * @return
	 */
	@SecurityMapping(title = "买家sns头部", value = "/buyer/my_sns_head*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping({ "/buyer/my_sns_head" })
	public ModelAndView my_sns_head(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/usercenter/sns/my_sns_head.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder
				.getCurrentUser().getId());
		int attsCount = this.snsTools.queryAtts(user.getId().toString());
		int fansCount = this.snsTools.queryFans(user.getId().toString());
		int favsCount = this.snsTools.queryfavCount(user.getId().toString());
		mv.addObject("attsCount", Integer.valueOf(attsCount));
		mv.addObject("fansCount", Integer.valueOf(fansCount));
		mv.addObject("favsCount", Integer.valueOf(favsCount));
		mv.addObject("user", user);
		return mv;
	}
	
	/**
	 * 买家sns导航
	 * @param request
	 * @param response
	 * @return
	 */
	@SecurityMapping(title = "买家sns导航", value = "/buyer/my_sns_head*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping({ "/buyer/my_sns_nav" })
	public ModelAndView my_sns_nav(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/usercenter/sns/my_sns_nav.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		String op = CommUtil.null2String(request.getAttribute("op"));
		mv.addObject("op", op);
		return mv;
	}
	
	/**
	 * 买家sns开启访问权限
	 * @param request
	 * @param response
	 */
	@SecurityMapping(title = "买家sns开启访问权限", value = "/buyer/sns_lock_on*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping({ "/buyer/sns_lock_on" })
	public void sns_lock_on(HttpServletRequest request,
			HttpServletResponse response) {
		int ret = 1;
		if (SecurityUserHolder.getCurrentUser() != null) {
			User user = this.userService.selectByPrimaryKey(SecurityUserHolder
					.getCurrentUser().getId());
			user.setWhether_attention(0);
			this.userService.updateById(user);
			ret = 0;
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
	 * 买家sns关闭访问权限
	 * @param request
	 * @param response
	 */
	@SecurityMapping(title = "买家sns关闭访问权限", value = "/buyer/sns_lock_off*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping({ "/buyer/sns_lock_off" })
	public void sns_lock_off(HttpServletRequest request,
			HttpServletResponse response) {
		int ret = 1;
		if (SecurityUserHolder.getCurrentUser() != null) {
			User user = this.userService.selectByPrimaryKey(SecurityUserHolder
					.getCurrentUser().getId());
			user.setWhether_attention(1);
			this.userService.updateById(user);
			ret = 0;
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
	 * 买家sns分享列表
	 * @param request
	 * @param response
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "买家sns分享列表", value = "/buyer/my_sns_share*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping({ "/buyer/my_sns_share" })
	public ModelAndView my_sns_share(HttpServletRequest request,
			HttpServletResponse response, String currentPage) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/usercenter/sns/my_sns_share.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		String url = this.configService.getSysConfig().getAddress();
		if ((url == null) || (url.equals(""))) {
			url = CommUtil.getURL(request);
		}
		String param = "";
		
		Map<String,Object> maps= this.redPigQueryTools.getParams(currentPage,15,"addTime", "desc");
		maps.put("user_id",SecurityUserHolder.getCurrentUser().getId());
        
		IPageList pList = this.userShareService.list(maps);
		CommUtil.saveIPageList2ModelAndView(url + "/buyer/my_sns_share.html","", param, pList, mv);
		mv.addObject("currentPage", currentPage);
		return mv;
	}
	
	/**
	 * 买家sns分享删除
	 * @param request
	 * @param response
	 * @param share_id
	 */
	@SecurityMapping(title = "买家sns分享删除", value = "/buyer/my_sns_share_del*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping({ "/buyer/my_sns_share_del" })
	public void my_sns_share_del(HttpServletRequest request,
			HttpServletResponse response, String share_id) {
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder
				.getCurrentUser().getId());
		boolean ret = true;
		UserShare userShare = this.userShareService.selectByPrimaryKey(CommUtil
				.null2Long(share_id));
		if (userShare.getUser_id().equals(user.getId())) {
			this.userShareService.deleteById(userShare.getId());
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
	 * 买家sns收藏
	 * @param request
	 * @param response
	 * @return
	 */
	@SecurityMapping(title = "买家sns收藏", value = "/buyer/my_sns_fav*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping({ "/buyer/my_sns_fav" })
	public ModelAndView my_sns_fav(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/usercenter/sns/my_sns_fav.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		User user = SecurityUserHolder.getCurrentUser();
		Map<String, Object> params = Maps.newHashMap();
		params.put("user_id", user.getId());
		params.put("type", 0);
		
		List<Favorite> favorites = this.favoriteService.queryPageList(params,0,10);
		
		mv.addObject("favorites", favorites);
		return mv;
	}
	
	/**
	 * 买家sns收藏ajax
	 * @param request
	 * @param response
	 * @param size
	 * @return
	 */
	@SecurityMapping(title = "买家sns收藏ajax", value = "/buyer/sns_ajax_favs*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping({ "/buyer/sns_ajax_favs" })
	public ModelAndView sns_ajax_favs(HttpServletRequest request,
			HttpServletResponse response, String size) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/usercenter/sns/sns_ajax_favs.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		User user = SecurityUserHolder.getCurrentUser();
		int current_size = CommUtil.null2Int(size);
		int begin = current_size * 5;
		int end = begin + 10;
		Map<String, Object> params = Maps.newHashMap();
		params.put("user_id", user.getId());
		params.put("type", 0);
		
		List<Favorite> favorites = this.favoriteService.queryPageList(params,begin, end);
		
		mv.addObject("favorites", favorites);
		return mv;
	}
	
	/**
	 * 买家sns评价
	 * @param request
	 * @param response
	 * @return
	 */
	@SecurityMapping(title = "买家sns评价", value = "/buyer/my_sns_evas*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping({ "/buyer/my_sns_evas" })
	public ModelAndView my_sns_evas(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/usercenter/sns/my_sns_evas.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		User user = SecurityUserHolder.getCurrentUser();
		Map<String, Object> params = Maps.newHashMap();
		params.put("evaluate_user_id", user.getId());
		params.put("evaluate_status",0);
		params.put("evaluate_photos",-1);
		
		List<Evaluate> evas = this.evaluateService.queryPageList(params, 0, 10);
		
		mv.addObject("evas", evas);
		return mv;
	}
	
	/**
	 * 买家sns评价ajax
	 * @param request
	 * @param response
	 * @param size
	 * @return
	 */
	@SecurityMapping(title = "买家sns评价ajax", value = "/buyer/sns_ajax_evas*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping({ "/buyer/sns_ajax_evas" })
	public ModelAndView sns_ajax_evas(HttpServletRequest request,
			HttpServletResponse response, String size) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/usercenter/sns/sns_ajax_evas.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		User user = SecurityUserHolder.getCurrentUser();
		int current_size = CommUtil.null2Int(size);
		int begin = current_size * 5;
		int end = begin + 10;
		Map<String, Object> params = Maps.newHashMap();
		params.put("user_id", user.getId());
		params.put("evaluate_status",0);
		params.put("evaluate_photos",-1);
		
		List<Evaluate> evas = this.evaluateService.queryPageList(params, begin, end);
		
		mv.addObject("evas", evas);
		return mv;
	}
	
	/**
	 * 买家sns晒单
	 * @param request
	 * @param response
	 * @return
	 */
	@SecurityMapping(title = "买家sns晒单", value = "/buyer/my_sns_evaps*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping({ "/buyer/my_sns_evaps" })
	public ModelAndView my_sns_evaps(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/usercenter/sns/my_sns_evaps.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		User user = SecurityUserHolder.getCurrentUser();
		Map<String, Object> params = Maps.newHashMap();
		params.put("user_id", user.getId());
		params.put("evaluate_status",0);
		params.put("evaluate_photos_no",-1);
		
		List<Evaluate> evas = this.evaluateService.queryPageList(params,0,10);
		
		mv.addObject("evas", evas);
		mv.addObject("evaluateViewTools", this.evaluateViewTools);
		return mv;
	}
	
	/**
	 * 买家sns晒单ajax
	 * @param request
	 * @param response
	 * @param size
	 * @return
	 */
	@SecurityMapping(title = "买家sns晒单ajax", value = "/buyer/sns_ajax_evas*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping({ "/buyer/sns_ajax_evaps" })
	public ModelAndView sns_ajax_evaps(HttpServletRequest request,
			HttpServletResponse response, String size) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/usercenter/sns/sns_ajax_evaps.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		User user = SecurityUserHolder.getCurrentUser();
		int current_size = CommUtil.null2Int(size);
		int begin = current_size * 5;
		int end = begin + 10;
		Map<String, Object> params = Maps.newHashMap();
		params.put("user_id", user.getId());
		params.put("evaluate_status",0);
		params.put("evaluate_photos_no",-1);
		
		List<Evaluate> evas = this.evaluateService.queryPageList(params, begin, end);
		
		mv.addObject("evas", evas);
		mv.addObject("evaluateViewTools", this.evaluateViewTools);
		return mv;
	}
	
	/**
	 * 买家sns
	 * @param request
	 * @param response
	 * @return
	 */
	@SecurityMapping(title = "买家sns", value = "/buyer/my_sns_cons*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping({ "/buyer/my_sns_cons" })
	public ModelAndView my_sns_cons(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/usercenter/sns/my_sns_cons.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		User user = SecurityUserHolder.getCurrentUser();
		Map<String, Object> params = Maps.newHashMap();
		params.put("consult_user_id", user.getId());
		
		List<Consult> cons = this.consultService.queryPageList(params,0,10);
		
		mv.addObject("cons", cons);
		mv.addObject("freeTools", this.freeTools);
		return mv;
	}
	
	/**
	 * 买家sns
	 * @param request
	 * @param response
	 * @param size
	 * @return
	 */
	@SecurityMapping(title = "买家sns", value = "/buyer/sns_ajax_cons*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping({ "/buyer/sns_ajax_cons" })
	public ModelAndView sns_ajax_cons(HttpServletRequest request,
			HttpServletResponse response, String size) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/usercenter/sns/sns_ajax_cons.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		User user = SecurityUserHolder.getCurrentUser();
		int current_size = CommUtil.null2Int(size);
		int begin = current_size * 5;
		int end = begin + 10;
		Map<String, Object> params = Maps.newHashMap();
		params.put("consult_user_id", user.getId());
		
		List<Consult> cons = this.consultService.queryPageList(params, begin, end);
		
		mv.addObject("cons", cons);
		mv.addObject("freeTools", this.freeTools);
		return mv;
	}
	
	/**
	 * 买家sns关注人
	 * @param request
	 * @param response
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "买家sns关注人", value = "/buyer/my_sns_atts*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping({ "/buyer/my_sns_atts" })
	public ModelAndView my_sns_atts(HttpServletRequest request,
			HttpServletResponse response, String currentPage) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/usercenter/sns/my_sns_atts.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		User user = SecurityUserHolder.getCurrentUser();
		String url = this.configService.getSysConfig().getAddress();
		if ((url == null) || (url.equals(""))) {
			url = CommUtil.getURL(request);
		}
		String param = "";
		
		Map<String,Object> maps= this.redPigQueryTools.getParams(currentPage,16,"addTime", "desc");
		maps.put("fromUser_id", user.getId());
		
		IPageList pList = this.snsAttentionService.list(maps);
		CommUtil.saveIPageList2ModelAndView(url + "/buyer/my_sns_atts.html",
				"", param, pList, mv);
		mv.addObject("snsTools", this.snsTools);
		return mv;
	}
	
	/**
	 * 买家sns粉丝
	 * @param request
	 * @param response
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "买家sns粉丝", value = "/buyer/my_sns_fans*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping({ "/buyer/my_sns_fans" })
	public ModelAndView my_sns_fans(HttpServletRequest request,
			HttpServletResponse response, String currentPage) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/usercenter/sns/my_sns_fans.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		User user = SecurityUserHolder.getCurrentUser();
		String url = this.configService.getSysConfig().getAddress();
		if ((url == null) || (url.equals(""))) {
			url = CommUtil.getURL(request);
		}
		String param = "";
		
		Map<String,Object> maps= this.redPigQueryTools.getParams(currentPage,16,"addTime", "desc");
		maps.put("toUser_id", user.getId());
		
		IPageList pList = this.snsAttentionService.list(maps);
		CommUtil.saveIPageList2ModelAndView(url + "/buyer/my_sns_fans.html",
				"", param, pList, mv);
		mv.addObject("snsTools", this.snsTools);
		return mv;
	}
	
	/**
	 * 我的圈子
	 * @param request
	 * @param response
	 * @param type
	 * @param currentPage
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	@SecurityMapping(title = "我的圈子", value = "/buyer/my_sns_circle*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping({ "/buyer/my_sns_circle" })
	public ModelAndView my_sns_circle(HttpServletRequest request,
			HttpServletResponse response, String type, String currentPage) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/usercenter/sns/my_sns_circle_atten.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		Map<String,Object> params;
		
		if ((type != null) && (!type.equals(""))) {
			mv = new RedPigJModelAndView(
					"user/default/usercenter/sns/my_sns_circle.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request,
					response);
			List<Map> maps = Lists.newArrayList();
			User user = this.userService.selectByPrimaryKey(SecurityUserHolder.getCurrentUser().getId());
			if (user.getCircle_create_info() != null) {
				maps = JSON.parseArray(user.getCircle_create_info(), Map.class);
			}
			if (maps.size() > 0) {
				List<Circle> cirs = Lists.newArrayList();
				for (Map map : maps) {
					Circle cir = this.circleService.selectByPrimaryKey(CommUtil.null2Long(map.get("id")));
					cirs.add(cir);
				}
				
				Map<String,Object> pmaps= this.redPigQueryTools.getParams(currentPage,20,"invitaion_perfect", "asc");
				pmaps.put("circle_id", cirs.get(0).getId());
				pmaps.put("invitaion_top_no", 1);
				
				IPageList pList = this.invitationService.list(pmaps);
				CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
				if ((currentPage == null) || (CommUtil.null2Int(currentPage) == 1)) {
					params = Maps.newHashMap();
					params.put("invitaion_top", Integer.valueOf(1));
					
					List<CircleInvitation> tops = this.invitationService.queryPageList(params,0,1);
					
					if (tops.size() > 0) {
						mv.addObject("top", tops.get(0));
					}
				}
				mv.addObject("cirs", cirs);
			}
			mv.addObject("type", type);
		} else {
			User user = this.userService.selectByPrimaryKey(SecurityUserHolder
					.getCurrentUser().getId());
			if (user.getCircle_attention_info() != null) {
				List<Map> circle_list = JSON.parseArray(user.getCircle_attention_info(), Map.class);
				List<Circle> objs = Lists.newArrayList();
				List<Map> remove_maps = Lists.newArrayList();
				for (Map map : circle_list) {
					Circle temp = this.circleService.selectByPrimaryKey(CommUtil.null2Long(map.get("id")));
					if (temp != null) {
						objs.add(temp);
					} else {
						remove_maps.add(map);
					}
				}
				if (remove_maps.size() > 0) {
					circle_list.removeAll(remove_maps);
					if (circle_list.size() > 0) {
						user.setCircle_attention_info(JSON
								.toJSONString(circle_list));
					} else {
						user.setCircle_attention_info(null);
					}
					this.userService.updateById(user);
				}
				mv.addObject("objs", objs);
			}
		}
		mv.addObject("circleViewTools", this.circleViewTools);
		return mv;
	}
	
	/**
	 * 我的圈子
	 * @param request
	 * @param response
	 * @param cid
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "我的圈子", value = "/buyer/my_sns_circle_invitation*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping({ "/buyer/my_sns_circle_invitation" })
	public ModelAndView my_sns_circle_invitation(HttpServletRequest request,
			HttpServletResponse response, String cid, String currentPage) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/usercenter/sns/my_sns_circle_invitation.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		Circle cir = this.circleService.selectByPrimaryKey(CommUtil.null2Long(cid));
		
		Map<String,Object> maps= this.redPigQueryTools.getParams(currentPage,20,"", "");
		maps.put("circle_id", cir.getId());
        maps.put("orderBy", "invitaion_perfect desc,addTime");
        maps.put("orderType", "desc");
        
		IPageList pList = this.invitationService.list(maps);
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		mv.addObject("cir", cir);
		mv.addObject("circleViewTools", this.circleViewTools);
		return mv;
	}
	
	/**
	 * 我的圈子
	 * @param request
	 * @param response
	 * @param operate
	 * @param id
	 * @param cid
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "我的圈子", value = "/buyer/my_sns_circle_invitation_operate*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping({ "/buyer/my_sns_circle_invitation_operate" })
	public String my_sns_circle_invitation_operate(HttpServletRequest request,
			HttpServletResponse response, String operate, String id,
			String cid, String currentPage) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/usercenter/sns/my_sns_circle.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		CircleInvitation obj = this.invitationService.selectByPrimaryKey(CommUtil.null2Long(id));
		
		if (operate.equals("top")) {
			Map<String, Object> params = Maps.newHashMap();
			params.put("invitaion_top", Integer.valueOf(1));
			params.put("circle_id", CommUtil.null2Long(cid));
			
			List<CircleInvitation> objs = this.invitationService.queryPageList(params);
			
			for (CircleInvitation temp_obj : objs) {
				temp_obj.setInvitaion_top(0);
				this.invitationService.updateById(temp_obj);
			}
			obj.setInvitaion_top(1);
			this.invitationService.updateById(obj);
		}
		if (operate.equals("perfect")) {
			obj.setInvitaion_perfect(1);
			this.invitationService.updateById(obj);
		}
		if (operate.equals("delete")) {
			Map<String, Object> params = Maps.newHashMap();
			params.put("invitation_id", obj.getId());
			
			List<CircleInvitationReply> reply_ids = this.invitationReplyService.queryPageList(params);
			
			List<Long> dele_ids = Lists.newArrayList();
			for (CircleInvitationReply temp_id : reply_ids) {
				dele_ids.add(temp_id.getId());
			}
			
			this.invitationReplyService.batchDeleteByIds(dele_ids);
			
			
			Circle cir = this.circleService.selectByPrimaryKey(Long.valueOf(obj
					.getCircle_id()));
			cir.setInvitation_count(cir.getInvitation_count() - 1);
			this.circleService.updateById(cir);
			this.invitationService.deleteById(obj.getId());
		}
		if (operate.equals("cancle_top")) {
			obj.setInvitaion_top(0);
			this.invitationService.updateById(obj);
		}
		if (operate.equals("cancle_perfect")) {
			obj.setInvitaion_perfect(0);
			this.invitationService.updateById(obj);
		}
		mv.addObject("circleViewTools", this.circleViewTools);
		return "redirect:/buyer/my_sns_circle_invitation?type=my_circle&currentPage="
				+ currentPage + "&cid=" + cid;
	}
	
	/**
	 * 我的帖子
	 * @param request
	 * @param response
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "我的帖子", value = "/buyer/my_sns_invitation*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping({ "/buyer/my_sns_invitation" })
	public ModelAndView my_sns_invitation(HttpServletRequest request,
			HttpServletResponse response, String currentPage) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/usercenter/sns/my_sns_invitation.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		
		Map<String,Object> maps= this.redPigQueryTools.getParams(currentPage,"addTime", "desc");
		maps.put("user_id",SecurityUserHolder.getCurrentUser().getId());
		
		IPageList pList = this.invitationService.list(maps);
		CommUtil.saveIPageList2ModelAndView("", "", null, pList, mv);
		return mv;
	}
}
