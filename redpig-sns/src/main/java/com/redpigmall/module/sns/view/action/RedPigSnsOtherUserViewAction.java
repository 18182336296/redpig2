package com.redpigmall.module.sns.view.action;

import java.io.IOException;
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
import com.redpigmall.api.mv.RedPigJModelAndView;
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.api.sec.SecurityUserHolder;
import com.redpigmall.api.tools.CommUtil;
import com.redpigmall.module.sns.manage.base.BaseAction;
import com.redpigmall.domain.Circle;
import com.redpigmall.domain.CircleInvitation;
import com.redpigmall.domain.Evaluate;
import com.redpigmall.domain.Favorite;
import com.redpigmall.domain.SnsAttention;
import com.redpigmall.domain.User;
import com.redpigmall.domain.UserDynamic;

/**
 * 
 * <p>
 * Title: RedPigSnsViewAction.java
 * </p>
 * 
 * <p>
 * Description:前台sns控制器
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
public class RedPigSnsOtherUserViewAction extends BaseAction{
	
	/**
	 * 用户查看其他人的个人主页
	 * 
	 * @param request
	 * @param response
	 * @param id
	 * @return
	 * @throws IOException
	 */
	@RequestMapping({ "/sns/other_sns" })
	public String other_sns(HttpServletRequest request,
			HttpServletResponse response, String id) throws IOException {
		String url = "/sns/to_other_sns?id=" + id;
		if (SecurityUserHolder.getCurrentUser() != null) {
			if (SecurityUserHolder.getCurrentUser().getId().toString()
					.equals(id)) {
				url = "/buyer/my_sns_index";
			}
		}
		return "redirect:" + url;
	}
	/**
	 * 跳转到他人个人主页
	 * 
	 * @param request
	 * @param response
	 * @param id
	 * @return
	 * @throws IOException
	 */
	@SuppressWarnings("rawtypes")
	@RequestMapping({ "/sns/to_other_sns" })
	public ModelAndView to_other_sns(HttpServletRequest request,
			HttpServletResponse response, String id) throws IOException {
		ModelAndView mv = new RedPigJModelAndView("sns/other_sns_index.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		User user = this.userService.selectByPrimaryKey(CommUtil.null2Long(id));
		if (user != null) {
			if (user.getWhether_attention() == 0) {
				mv = new RedPigJModelAndView("error.html",
						this.configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 1, request,
						response);
				mv.addObject("op_title", "该用户禁止其他用户访问其主页");
				mv.addObject("url", CommUtil.getURL(request) + "/index");
				return mv;
			}
			Map<String, Object> params = Maps.newHashMap();
			params.put("fromUser_id", CommUtil.null2Long(user.getId()));
			
			List<SnsAttention> tempSnss = this.snsAttentionService.queryPageList(params, 0, 10);
			
			List<Map<String, Object>> userAttsList = Lists.newArrayList();
			for (SnsAttention sns : tempSnss) {
				Map<String, Object> map = Maps.newHashMap();
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
			
			tempSnss = this.snsAttentionService.queryPageList(params, 0, 10);
			
			List<Map<String, Object>> userFansList = Lists.newArrayList();
			for (SnsAttention sns : tempSnss) {
				Map<String, Object> map = Maps.newHashMap();
				map.put("user_id", sns.getFromUser().getId());
				map.put("user_name", sns.getFromUser().getUserName());
				map.put("sns_time", sns.getAddTime());
				if (sns.getFromUser().getPhoto() != null) {
					map.put("user_photo", sns.getFromUser().getPhoto()
							.getPath()
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
			
			List<Evaluate> evas = this.evaluateService.queryPageList(params,0,2);
			
			mv.addObject("evas", evas);
			params.clear();
			params.put("user_id", user.getId());
			params.put("evaluate_status", 0);
			params.put("evaluate_photos_no", -1);
			
			List<Evaluate> evaPhotos = this.evaluateService.queryPageList(params,0,2);
			
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
				
				List<Map> maps = JSON.parseArray(
						user.getCircle_attention_info(), Map.class);
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
			mv.addObject("uid", id);
			if (SecurityUserHolder.getCurrentUser() != null) {
				User currentUser = this.userService.selectByPrimaryKey(SecurityUserHolder.getCurrentUser().getId());
				mv.addObject("currentUser", currentUser);
			}
			params.clear();
			params.put("user_id", user.getId());
			
			List<UserDynamic> dynamics = this.dynamicService.queryPageList(params,0,1);
			
			if (dynamics.size() > 0) {
				mv.addObject("userDynamics", dynamics.get(0));
			}
			mv.addObject("snsTools", this.snsTools);
		} else {
			mv = new RedPigJModelAndView("error.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "您所访问的地址不存在");
			mv.addObject("url", CommUtil.getURL(request) + "/index");
		}
		return mv;
	}

	@RequestMapping({ "/sns/other_sns_lock" })
	public ModelAndView other_sns_lock(HttpServletRequest request,
			HttpServletResponse response, String id) {
		ModelAndView mv = new RedPigJModelAndView("sns/sns_lock.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		User otherUser = this.userService.selectByPrimaryKey(CommUtil.null2Long(id));
		mv.addObject("otherUser", otherUser);
		return mv;
	}
	/**
	 * 他人主页-头部
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping({ "/sns/other_sns_head" })
	public ModelAndView other_sns_head(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new RedPigJModelAndView("sns/sns_head.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		String otherUser_id = CommUtil.null2String(request.getAttribute("uid"));
		User user = this.userService.selectByPrimaryKey(CommUtil
				.null2Long(otherUser_id));
		int attsCount = this.snsTools.queryAtts(otherUser_id);
		int fansCount = this.snsTools.queryFans(otherUser_id);
		int favsCount = this.snsTools.queryfavCount(otherUser_id);
		mv.addObject("attsCount", Integer.valueOf(attsCount));
		mv.addObject("fansCount", Integer.valueOf(fansCount));
		mv.addObject("favsCount", Integer.valueOf(favsCount));
		mv.addObject("otherUser", user);
		mv.addObject("currentUser", SecurityUserHolder.getCurrentUser());
		mv.addObject("snsTools", this.snsTools);
		return mv;
	}
	/**
	 * 他人主页-导航
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping({ "/sns/other_sns_nav" })
	public ModelAndView other_sns_nav(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new RedPigJModelAndView("sns/sns_nav.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		String op = CommUtil.null2String(request.getAttribute("op"));
		mv.addObject("op", op);
		String uid = CommUtil.null2String(request.getAttribute("uid"));
		mv.addObject("uid", uid);
		return mv;
	}
	/**
	 * 他人主页 - 动态
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping({ "/buyer/other_sns_dynamic" })
	public ModelAndView other_sns_dynamic(HttpServletRequest request,
			HttpServletResponse response, String other_id) {
		ModelAndView mv = new RedPigJModelAndView("sns/other_sns_dynamic.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);

		Map<String, Object> params = Maps.newHashMap();
		params.put("user_id", CommUtil.null2Long(other_id));
		
		List<UserDynamic> userDynamics = this.userdynamicService.queryPageList(params,0, 12);
		
		mv.addObject("userDynamics", userDynamics);
		mv.addObject("snsTools", this.snsTools);
		User otherUser = this.userService.selectByPrimaryKey(CommUtil
				.null2Long(other_id));
		mv.addObject("other_id", other_id);
		mv.addObject("otherUser", otherUser);
		
		params.clear();
		params.put("fromUser_id", CommUtil.null2Long(other_id));
		
		List<SnsAttention> tempSnss = this.snsAttentionService.queryPageList(params,0, 6);
		
		List<Map<String, Object>> userAttsList = Lists.newArrayList();
		for (SnsAttention sns : tempSnss) {
			Map<String, Object> map = Maps.newHashMap();
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
		params.put("toUser_id", CommUtil.null2Long(other_id));
		
		tempSnss = this.snsAttentionService.queryPageList(params,0, 6);
		
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
		return mv;
	}

	@RequestMapping({ "/buyer/ajax_dynamic" })
	public ModelAndView ajax_dynamic(HttpServletRequest request,
			HttpServletResponse response, String count, String otherId) {
		ModelAndView mv = new RedPigJModelAndView("sns/sns_ajax_dynamic.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		User otherUser = this.userService.selectByPrimaryKey(CommUtil
				.null2Long(otherId));
		Map<String, Object> params = Maps.newHashMap();
		params.put("user_id", otherUser.getId());
		
		List<UserDynamic> userDynamics = this.userdynamicService.queryPageList(params,CommUtil.null2Int(count), 12);
		
		mv.addObject("userDynamics", userDynamics);
		mv.addObject("snsTools", this.snsTools);
		mv.addObject("otherUser", otherUser);
		return mv;
	}
	/**
	 * 他人主页-评价
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping({ "/sns/other_sns_evas" })
	public ModelAndView other_sns_evas(HttpServletRequest request,
			HttpServletResponse response, String other_id) throws IOException {
		ModelAndView mv = new RedPigJModelAndView("sns/other_sns_evas.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		User user = this.userService.selectByPrimaryKey(CommUtil.null2Long(other_id));
		if ((user != null) && (user.getWhether_attention() == 1)) {
			Map<String, Object> params = Maps.newHashMap();
			params.put("user_id", user.getId());
			params.put("evaluate_status", 0);
			params.put("evaluate_photos", -1);
			
			List<Evaluate> evas = this.evaluateService.queryPageList(params,0, 10);
			
			mv.addObject("evas", evas);
			mv.addObject("other_id", other_id);
		} else {
			mv = new RedPigJModelAndView("error.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "请求参数错误");
			mv.addObject("url", CommUtil.getURL(request) + "/index");
		}
		return mv;
	}
	/**
	 * 他人主页-评价ajax
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping({ "/sns/ajax_evas" })
	public ModelAndView sns_ajax_evas(HttpServletRequest request,
			HttpServletResponse response, String size, String other_id) {
		ModelAndView mv = new RedPigJModelAndView("sns/sns_ajax_evas.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		int current_size = CommUtil.null2Int(size);
		int begin = current_size * 5;
		int end = begin + 10;
		Map<String, Object> params = Maps.newHashMap();
		params.put("user_id", CommUtil.null2Long(other_id));
		params.put("evaluate_status", 0);
		params.put("evaluate_photos", -1);
		
		List<Evaluate> evas = this.evaluateService.queryPageList(params,begin, end);
		
		mv.addObject("evas", evas);
		return mv;
	}
	/**
	 * 他人主页-晒单
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping({ "/sns/other_sns_evaps" })
	public ModelAndView other_sns_evaps(HttpServletRequest request,
			HttpServletResponse response, String other_id) throws IOException {
		ModelAndView mv = new RedPigJModelAndView("sns/other_sns_evaps.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		User user = this.userService.selectByPrimaryKey(CommUtil.null2Long(other_id));
		if ((user != null) && (user.getWhether_attention() == 1)) {
			Map<String, Object> params = Maps.newHashMap();
			params.put("user_id", user.getId());
			params.put("evaluate_status", 0);
			params.put("evaluate_photos_no", -1);
			
			List<Evaluate> evaps = this.evaluateService.queryPageList(params, 0, 10);
			
			mv.addObject("evaps", evaps);
			mv.addObject("other_id", other_id);
			mv.addObject("evaluateViewTools", this.evaluateViewTools);
		} else {
			mv = new RedPigJModelAndView("error.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "请求参数错误");
			mv.addObject("url", CommUtil.getURL(request) + "/index");
		}
		return mv;
	}

	@RequestMapping({ "/sns/ajax_evaps" })
	public ModelAndView sns_ajax_evaps(HttpServletRequest request,
			HttpServletResponse response, String size, String id) {
		ModelAndView mv = new RedPigJModelAndView("sns/sns_ajax_evaps.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		int current_size = CommUtil.null2Int(size);
		int begin = current_size * 5;
		int end = begin + 10;
		Map<String, Object> params = Maps.newHashMap();
		params.put("user_id", CommUtil.null2Long(id));
		params.put("evaluate_status", 0);
		params.put("evaluate_photos_no", -1);
		
		List<Evaluate> evaps = this.evaluateService.queryPageList(params, begin, end);
		
		mv.addObject("evaps", evaps);
		mv.addObject("evaluateViewTools", this.evaluateViewTools);
		return mv;
	}
	/**
	 * 他人主页-分享
	 * 
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param other_id
	 * @return
	 */
	@RequestMapping({ "/sns/other_sns_share" })
	public ModelAndView other_sns_share(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String other_id) {
		ModelAndView mv = new RedPigJModelAndView("sns/other_sns_share.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		String url = this.configService.getSysConfig().getAddress();
		if ((url == null) || (url.equals(""))) {
			url = CommUtil.getURL(request);
		}
		String param = "";
		
		Map<String,Object> maps= this.redPigQueryTools.getParams(currentPage,"addTime", "desc");
        maps.put("user_id",CommUtil.null2Long(other_id));
        
		IPageList pList = this.userShareService.list(maps);
		CommUtil.saveIPageList2ModelAndView(url + "/sns/other_sns_share.html",
				"", param, pList, mv);
		mv.addObject("currentPage", currentPage);
		mv.addObject("other_id", other_id);
		return mv;
	}
	/**
	 * 他人主页-圈子
	 * 
	 * @param request
	 * @param response
	 * @param size
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	@RequestMapping({ "/sns/other_circle" })
	public ModelAndView other_circle(HttpServletRequest request,
			HttpServletResponse response, String type, String currentPage,
			String other_id) {
		ModelAndView mv = new RedPigJModelAndView("/sns/other_circle.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		User user = this.userService.selectByPrimaryKey(CommUtil.null2Long(other_id));
		if (user.getCircle_attention_info() != null) {
			List<Map> circle_list = JSON.parseArray(
					user.getCircle_attention_info(), Map.class);
			List<Circle> objs = Lists.newArrayList();
			for (Map map : circle_list) {
				Circle temp = this.circleService.selectByPrimaryKey(CommUtil.null2Long(map.get("id")));
				objs.add(temp);
			}
			mv.addObject("objs", objs);
		}
		mv.addObject("circleViewTools", this.circleViewTools);
		mv.addObject("uid", other_id);
		return mv;
	}
	/**
	 * 他人主页-帖子
	 * 
	 * @param request
	 * @param response
	 * @param size
	 * @return
	 */
	@RequestMapping({ "/sns/other_invitation" })
	public ModelAndView other_invitation(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String other_id) {
		ModelAndView mv = new RedPigJModelAndView("/sns/other_invitation.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		Map<String,Object> maps= this.redPigQueryTools.getParams(currentPage,"addTime", "desc");
        maps.put("user_id",CommUtil.null2Long(other_id));
        
		IPageList pList = this.invitationService.list(maps);
		CommUtil.saveIPageList2ModelAndView("", "", null, pList, mv);
		mv.addObject("uid", other_id);
		return mv;
	}
	/**
	 * 他人主页-收藏
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping({ "/sns/other_sns_fav" })
	public ModelAndView other_sns_fav(HttpServletRequest request,
			HttpServletResponse response, String other_id) {
		ModelAndView mv = new RedPigJModelAndView("sns/other_sns_favs.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		Map<String, Object> params = Maps.newHashMap();
		params.put("user_id", CommUtil.null2Long(other_id));
		params.put("type", 0);
		
		List<Favorite> favorites = this.favoriteService.queryPageList(params,0, 10);
		
		mv.addObject("favorites", favorites);
		mv.addObject("other_id", other_id);
		return mv;
	}
	/**
	 * 他人主页-收藏ajax
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping({ "/sns/ajax_favs" })
	public ModelAndView sns_ajax_favs(HttpServletRequest request,
			HttpServletResponse response, String size, String other_id) {
		ModelAndView mv = new RedPigJModelAndView("sns/sns_ajax_favs.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		int current_size = CommUtil.null2Int(size);
		int begin = current_size * 5;
		int end = begin + 10;
		Map<String, Object> params = Maps.newHashMap();
		params.put("user_id", CommUtil.null2Long(other_id));
		params.put("type", 0);
		
		List<Favorite> favorites = this.favoriteService.queryPageList(params,begin, end);
		
		mv.addObject("favorites", favorites);
		mv.addObject("other_id", other_id);
		return mv;
	}
	/**
	 * 他人主页-关注
	 * 
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param other_id
	 * @return
	 */
	@RequestMapping({ "/sns/other_sns_atts" })
	public ModelAndView other_sns_atts(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String other_id) {
		ModelAndView mv = new RedPigJModelAndView("/sns/other_sns_atts.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		String url = this.configService.getSysConfig().getAddress();
		if ((url == null) || (url.equals(""))) {
			url = CommUtil.getURL(request);
		}
		String param = "";
		
		Map<String,Object> maps= this.redPigQueryTools.getParams(currentPage,"addTime", "desc");
        maps.put("fromUser_id",CommUtil.null2Long(other_id));
        
		IPageList pList = this.snsAttentionService.list(maps);
		CommUtil.saveIPageList2ModelAndView(url + "/sns/other_sns_atts.html",
				"", param, pList, mv);
		mv.addObject("other_id", other_id);
		mv.addObject("snsTools", this.snsTools);
		return mv;
	}
	/**
	 * 他人主页-粉丝
	 * 
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param other_id
	 * @return
	 */
	@RequestMapping({ "/sns/other_sns_fans" })
	public ModelAndView other_sns_fans(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String other_id) {
		ModelAndView mv = new RedPigJModelAndView("/sns/other_sns_fans.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		String url = this.configService.getSysConfig().getAddress();
		if ((url == null) || (url.equals(""))) {
			url = CommUtil.getURL(request);
		}
		String param = "";
		
		Map<String,Object> maps= this.redPigQueryTools.getParams(currentPage,"addTime", "desc");
        maps.put("toUser_id",CommUtil.null2Long(other_id));
        
		IPageList pList = this.snsAttentionService.list(maps);
		CommUtil.saveIPageList2ModelAndView(url + "/sns/other_sns_fans.html",
				"", param, pList, mv);
		mv.addObject("snsTools", this.snsTools);
		mv.addObject("other_id", other_id);
		return mv;
	}
}
