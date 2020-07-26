package com.redpigmall.module.weixin.manage.buyer.action;

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
import com.google.common.collect.Maps;
import com.redpigmall.api.annotation.SecurityMapping;
import com.redpigmall.api.mv.RedPigJModelAndView;
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.api.sec.SecurityUserHolder;
import com.redpigmall.api.tools.CommUtil;
import com.redpigmall.module.weixin.view.action.base.BaseAction;
import com.redpigmall.domain.FootPoint;
import com.redpigmall.domain.User;

/**
 * <p>
 * Title: RedPig用户中心足迹
 * </p>
 * 
 * <p>
 * Description: RedPigWeixinUserFootPointAction
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
 * @date 2015年1月26日
 * 
 * @version b2b2c_2015
 */
@Controller
public class RedPigWeixinUserFootPointAction extends BaseAction {
	
//	/**
//	 * 用户中心足迹
//	 * @param request
//	 * @param response
//	 * @param currentPage
//	 * @param orderBy
//	 * @param orderType
//	 * @return
//	 */
//	@SecurityMapping(title = "用户中心足迹", value = "/buyer/foot_point*", rtype = "buyer", rname = "移动端用户中心", rcode = "wap_user_center", rgroup = "移动端用户中心")
//	@RequestMapping({ "/buyer/foot_point" })
//	public ModelAndView foot_point(HttpServletRequest request,
//			HttpServletResponse response, String currentPage, String orderBy,
//			String orderType) {
//		ModelAndView mv = null;
//		User user = SecurityUserHolder.getCurrentUser();
//		if (user != null) {
//			mv = new RedPigJModelAndView(
//					"user/default/usercenter/weixin/foot_point.html",
//					this.configService.getSysConfig(),
//					this.userConfigService.getUserConfig(), 0, request,
//					response);
//			
//			Map<String,Object> maps= this.redPigQueryTools.getParams("1", orderBy, orderType);
//	        maps.put("fp_user_id", user.getId());
//	        maps.put("fp_user_id", user.getId());
//	        
//			IPageList pList = this.footPointService.list(maps);
//			mv.addObject("objs", pList.getResult());
//			mv.addObject("totalPage", Integer.valueOf(pList.getPages()));
//			mv.addObject("footPointTools", this.footPointTools);
//		} else {
//			mv = new RedPigJModelAndView("weixin/login.html",
//					this.configService.getSysConfig(),
//					this.userConfigService.getUserConfig(), 1, request,
//					response);
//		}
//		return mv;
//	}
	
	/**
	 * 用户中心足迹
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @return
	 */
	@SecurityMapping(title = "用户中心足迹", value = "/buyer/foot_point*", rtype = "buyer", rname = "移动端用户中心", rcode = "wap_user_center", rgroup = "移动端用户中心")
	@RequestMapping({ "/buyer/foot_point" })
	public ModelAndView foot_point(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,
			String orderType) {
		ModelAndView mv = null;
		User user = SecurityUserHolder.getCurrentUser();
		if (user != null) {
			mv = new RedPigJModelAndView(
					"user/default/usercenter/weixin/foot_point.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request,
					response);
			
			Map<String,Object> maps= this.redPigQueryTools.getParams("1", orderBy, orderType);
	        maps.put("fp_user_id", user.getId());
	        maps.put("fp_user_id", user.getId());
	        
			IPageList pList = this.footPointService.list(maps);
			mv.addObject("objs", pList.getResult());
			mv.addObject("totalPage", Integer.valueOf(pList.getPages()));
			mv.addObject("footPointTools", this.footPointTools);
		} else {
			mv = new RedPigJModelAndView("weixin/login.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
		}
		return mv;
	}
	
	/**
	 * 足迹删除
	 * @param request
	 * @param response
	 * @param date
	 * @param goods_id
	 */
	@SuppressWarnings({"rawtypes" })
	@SecurityMapping(title = "足迹删除", value = "/buyer/foot_point_remove*", rtype = "buyer", rname = "移动端用户中心", rcode = "wap_user_center", rgroup = "移动端用户中心")
	@RequestMapping({ "/buyer/foot_point_remove" })
	public void foot_point_remove(HttpServletRequest request,
			HttpServletResponse response, String date, String goods_id) {
		boolean ret = false;
		if ((CommUtil.null2String(date).equals(""))
				&& (CommUtil.null2String(goods_id).equals(""))) {
			Map<String, Object> params = Maps.newHashMap();
			params.put("fp_user_id", SecurityUserHolder.getCurrentUser()
					.getId());
			
			List<FootPoint> fps = this.footPointService.queryPageList(params);
			
			for (FootPoint fp : fps) {
				this.footPointService.deleteById(fp.getId());
			}
			ret = true;
		}
		if ((!CommUtil.null2String(date).equals(""))
				&& (CommUtil.null2String(goods_id).equals(""))) {
			Map<String, Object> params = Maps.newHashMap();
			params.put("fp_date", CommUtil.formatDate(date));
			params.put("fp_user_id", SecurityUserHolder.getCurrentUser().getId());
			
			List<FootPoint> fps = this.footPointService.queryPageList(params);
			
			for (FootPoint fp : fps) {
				this.footPointService.deleteById(fp.getId());
			}
			ret = true;
		}
		if ((!CommUtil.null2String(date).equals(""))
				&& (!CommUtil.null2String(goods_id).equals(""))) {
			Map<String, Object> params = Maps.newHashMap();
			params.put("fp_date", CommUtil.formatDate(date));
			params.put("fp_user_id", SecurityUserHolder.getCurrentUser()
					.getId());
			
			List<FootPoint> fps = this.footPointService.queryPageList(params);
			
			for (FootPoint fp : fps) {
				List<Map> list = JSON.parseArray(fp.getFp_goods_content(),
						Map.class);
				for (Map map : list) {
					if (CommUtil.null2String(map.get("goods_id")).equals(
							goods_id)) {
						list.remove(map);
						break;
					}
				}
				fp.setFp_goods_content(JSON.toJSONString(list));
				fp.setFp_goods_count(list.size());
				this.footPointService.updateById(fp);
			}
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
	 * 足迹下拉加载
	 * @param request
	 * @param response
	 * @param goods_id
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @return
	 */
	@SecurityMapping(title = "足迹下拉加载", value = "/buyer/foot_point_ajax*", rtype = "buyer", rname = "移动端用户中心", rcode = "wap_user_center", rgroup = "移动端用户中心")
	@RequestMapping({ "/buyer/foot_point_ajax" })
	public ModelAndView foot_point_ajax(HttpServletRequest request,
			HttpServletResponse response, String goods_id, String currentPage,
			String orderBy, String orderType) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/usercenter/weixin/class_goods_foot.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder.getCurrentUser().getId());
		
		Map<String,Object> maps= this.redPigQueryTools.getParams(currentPage, orderBy, orderType);
        maps.put("fp_user_id", user.getId());
        
		IPageList pList = this.footPointService.list(maps);
		mv.addObject("objs", pList.getResult());
		mv.addObject("totalPage", Integer.valueOf(pList.getPages()));
		mv.addObject("footPointTools", this.footPointTools);
		return mv;
	}
}
