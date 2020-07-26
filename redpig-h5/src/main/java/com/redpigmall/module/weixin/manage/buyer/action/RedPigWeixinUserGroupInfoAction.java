package com.redpigmall.module.weixin.manage.buyer.action;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.redpigmall.api.annotation.SecurityMapping;
import com.redpigmall.api.mv.RedPigJModelAndView;
import com.redpigmall.api.sec.SecurityUserHolder;
import com.redpigmall.api.tools.CommUtil;
import com.redpigmall.module.weixin.manage.base.BaseAction;
import com.redpigmall.domain.GroupInfo;

/**
 * <p>
 * Title: RedPigWapUserGroupInfoAction.java
 * </p>
 * 
 * <p>
 * Description: wap端用户中心团购
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
 * @author zw
 * 
 * @date 2015年1月6日
 * 
 * @version b2b2c_2015
 */
@Controller
public class RedPigWeixinUserGroupInfoAction extends BaseAction{
	
	/**
	 * 移动端户中心团购列表
	 * @param request
	 * @param response
	 * @param status
	 * @param begin_num
	 * @return
	 */
	@SecurityMapping(title = "移动端户中心团购列表", value = "/buyer/groupinfo*", rtype = "buyer", rname = "移动端用户中心", rcode = "wap_user_center", rgroup = "移动端户中心团购")
	@RequestMapping({ "/buyer/groupinfo" })
	public ModelAndView groupinfo(HttpServletRequest request,
			HttpServletResponse response, String status, String begin_num) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/usercenter/groupinfo.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		
		Map<String, Object> map = Maps.newHashMap();
		if (!CommUtil.null2String(status).equals("")) {
			if (CommUtil.null2String(status).equals("357")) {
				Set<Integer> set = Sets.newTreeSet();
				set.add(3);
				set.add(5);
				set.add(7);
				map.put("statuss", set);
				
			} else {
				map.put("status", Integer.valueOf(CommUtil.null2Int(status)));
			}
		}
		mv.addObject("status", status);
		map.put("user_id", SecurityUserHolder.getCurrentUser().getId());
		int begin = 1;
		if (!CommUtil.null2String(begin_num).equals("")) {
			begin = CommUtil.null2Int(begin_num);
		}
		if (begin != 1) {
			mv = new RedPigJModelAndView("user/usercenter/groupinfo_data.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request,
					response);
		}
		
		List<GroupInfo> groupInfos = this.groupInfoService.queryPageList(map, begin, 12);
		
		mv.addObject("objs", groupInfos);
		return mv;
	}
}
