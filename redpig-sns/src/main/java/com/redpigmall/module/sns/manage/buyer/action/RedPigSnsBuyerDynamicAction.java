package com.redpigmall.module.sns.manage.buyer.action;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
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
import com.redpigmall.api.tools.WebForm;
import com.redpigmall.module.sns.manage.base.BaseAction;
import com.redpigmall.domain.Accessory;
import com.redpigmall.domain.Goods;
import com.redpigmall.domain.SnsAttention;
import com.redpigmall.domain.User;
import com.redpigmall.domain.UserDynamic;



/**
 * 
 * <p>
 * Title: RedPigSnsBuyerDynamicAction.java
 * </p>
 * 
 * <p>
 * Description: 用户sns动态功能控制器
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
 * @date 2015-1-21
 * 
 * @version redpigmall_b2b2c_2015
 */
@Controller
public class RedPigSnsBuyerDynamicAction extends BaseAction{
	
	/**
	 * 买家sns动态
	 * @param request
	 * @param response
	 * @return
	 */
	@SecurityMapping(title = "买家sns动态", value = "/buyer/my_sns_dynamic*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping({ "/buyer/my_sns_dynamic" })
	public ModelAndView my_sns_dynamic(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/usercenter/sns/my_sns_dynamic.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder.getCurrentUser().getId());
		
		Map<String, Object> params = Maps.newHashMap();
		params.put("user_id", user.getId());
		
		List<UserDynamic> userDynamics = this.userdynamicService.queryPageList(params,0, 12);
		
		mv.addObject("userDynamics", userDynamics);
		mv.addObject("snsTools", this.snsTools);
		
		Map<String,Object> maps= this.redPigQueryTools.getParams(null,4,null,null);
		maps.put("type",0);
		maps.put("user_id",user.getId());
		
		IPageList pList = this.favoriteService.list(maps);
		
		mv.addObject("fav_objs", pList.getResult());
		
		maps= this.redPigQueryTools.getParams(null,4,null,null);
		maps.put("user_id",user.getId());
		maps.put("info","eva_img");
		
		pList = this.accessoryService.list(maps);
		mv.addObject("eva_objs", pList.getResult());
		
		params.clear();
		params.put("fromUser_id", user.getId());
		
		List<SnsAttention> tempSnss = this.snsAttentionService.queryPageList(params,0, 6);
		
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
		params.put("toUser_id", user.getId());
		
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
	
	/**
	 * 
	 * @param request
	 * @param response
	 * @param count
	 * @return
	 */
	@RequestMapping({ "/buyer/my_sns_dynamic_ajax" })
	public ModelAndView my_sns_dynamic_ajax(HttpServletRequest request,
			HttpServletResponse response, String count) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/usercenter/sns/dynamic_ajax.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder
				.getCurrentUser().getId());

		Map<String, Object> params = Maps.newHashMap();
		params.put("user_id", user.getId());
		
		List<UserDynamic> userDynamics = this.userdynamicService.queryPageList(params, CommUtil.null2Int(count), 12);
		
		mv.addObject("userDynamics", userDynamics);
		mv.addObject("snsTools", this.snsTools);
		return mv;
	}
	
	/**
	 * 买家sns动态保存
	 * @param request
	 * @param response
	 * @param dynamic_content
	 * @param img_info
	 */
	@SecurityMapping(title = "买家sns动态保存", value = "/buyer/my_sns_dynamic_save*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping({ "/buyer/my_sns_dynamic_save" })
	public void my_sns_dynamic_saveEntity(HttpServletRequest request,
			HttpServletResponse response, String dynamic_content,
			String img_info) {
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder
				.getCurrentUser().getId());
		
		UserDynamic userdynamic = (UserDynamic) WebForm.toPo(request,UserDynamic.class);
		
		userdynamic.setAddTime(new Date());
		userdynamic.setUser_id(user.getId());
		userdynamic.setUser_name(user.getUserName());
		
		this.userdynamicService.saveEntity(userdynamic);
		boolean ret = true;
		
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
	 * 买家sns动态删除
	 * @param request
	 * @param response
	 * @param dynamic_id
	 */
	@SecurityMapping(title = "买家sns动态删除", value = "/buyer/my_sns_dynamic_del*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping({ "/buyer/my_sns_dynamic_del" })
	public void my_sns_dynamic_del(HttpServletRequest request,
			HttpServletResponse response, String dynamic_id) {
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder
				.getCurrentUser().getId());
		boolean ret = true;
		UserDynamic userdynamic = this.userdynamicService.selectByPrimaryKey(CommUtil.null2Long(dynamic_id));
		
		if (userdynamic.getUser_id().equals(user.getId())) {
			this.userdynamicService.deleteById(userdynamic.getId());
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
	 * 动态中收藏商品ajax
	 * @param request
	 * @param response
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "动态中收藏商品ajax", value = "/buyer/dynamic_fav_ajax*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping({ "/buyer/dynamic_fav_ajax" })
	public ModelAndView dynamic_fav_ajax(HttpServletRequest request,
			HttpServletResponse response, String currentPage) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/usercenter/sns/dynamic_fav_ajax.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		User user = SecurityUserHolder.getCurrentUser();
		Map<String,Object> maps= this.redPigQueryTools.getParams(currentPage,4, null, null);
        maps.put("type",0);
        maps.put("user_id",user.getId());
		
		IPageList pList = this.favoriteService.list(maps);
		if (CommUtil.null2Int(currentPage) < 1) {
			currentPage = "1";
		}
		if (CommUtil.null2Int(currentPage) > pList.getPages()) {
			currentPage = Integer.toString(pList.getPages());
		}
		mv.addObject("fav_objs", pList.getResult());
		mv.addObject("goPage",
				Integer.valueOf(CommUtil.null2Int(currentPage) + 1));
		mv.addObject("backPage",
				Integer.valueOf(CommUtil.null2Int(currentPage) - 1));
		return mv;
	}
	
	/**
	 * 动态中晒单图片ajax
	 * @param request
	 * @param response
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "动态中晒单图片ajax", value = "/buyer/dynamic_eva_ajax*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping({ "/buyer/dynamic_eva_ajax" })
	public ModelAndView dynamic_eva_ajax(HttpServletRequest request,
			HttpServletResponse response, String currentPage) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/usercenter/sns/dynamic_eva_ajax.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		User user = SecurityUserHolder.getCurrentUser();
		
		Map<String,Object> maps= this.redPigQueryTools.getParams(currentPage,4, null, null);
		maps.put("user_id", user.getId());
		maps.put("info", "eva_img");
		
		IPageList pList = this.accessoryService.list(maps);
		if (CommUtil.null2Int(currentPage) < 1) {
			currentPage = "1";
		}
		if (CommUtil.null2Int(currentPage) > pList.getPages()) {
			currentPage = Integer.toString(pList.getPages());
		}
		mv.addObject("eva_objs", pList.getResult());
		mv.addObject("goPage",
				Integer.valueOf(CommUtil.null2Int(currentPage) + 1));
		mv.addObject("backPage",
				Integer.valueOf(CommUtil.null2Int(currentPage) - 1));
		return mv;
	}

	@SuppressWarnings("rawtypes")
	@RequestMapping({ "/buyer/dynamic_add_img" })
	public void dynamic_add_img(HttpServletRequest request,
			HttpServletResponse response, String id, String img_info,
			String type) {
		List<Map> list = null;
		int num = 0;
		if ((img_info != null) && (!img_info.equals(""))) {
			list = JSON.parseArray(img_info, Map.class);
			num = CommUtil.null2Int(((Map) list.get(list.size() - 1))
					.get("num")) + 1;
		} else {
			list = Lists.newArrayList();
		}
		Accessory acc = null;
		if ("eva".equals(type)) {
			acc = this.accessoryService.selectByPrimaryKey(CommUtil.null2Long(id));
		}
		if ("fav".equals(type)) {
			Goods goods = this.goodsService.selectByPrimaryKey(CommUtil.null2Long(id));
			acc = goods.getGoods_main_photo();
		}
		Map<String, Object> map = Maps.newHashMap();
		map.put("id", id);
		map.put("num", Integer.valueOf(num));
		map.put("img", acc.getPath() + "/" + acc.getName());
		list.add(map);
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
	
	@SuppressWarnings("rawtypes")
	@RequestMapping({ "/buyer/dynamic_del_img" })
	public void dynamic_del_img(HttpServletRequest request,
			HttpServletResponse response, String num, String img_info) {
		List<Map> list = null;
		if ((img_info != null) && (!img_info.equals(""))) {
			list = JSON.parseArray(img_info, Map.class);
			for (Map map : list) {
				if (map.get("num").equals(
						Integer.valueOf(CommUtil.null2Int(num)))) {
					list.remove(map);
					break;
				}
			}
		}
		String jsonStr = "";
		if (list.size() > 0) {
			jsonStr = JSON.toJSONString(list);
		}
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		try {
			PrintWriter writer = response.getWriter();
			writer.print(jsonStr);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
