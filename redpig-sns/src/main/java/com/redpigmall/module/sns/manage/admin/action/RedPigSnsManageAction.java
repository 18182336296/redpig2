package com.redpigmall.module.sns.manage.admin.action;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.redpigmall.api.annotation.SecurityMapping;
import com.redpigmall.api.mv.RedPigJModelAndView;
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.api.tools.CommUtil;
import com.redpigmall.module.sns.manage.base.BaseAction;
import com.redpigmall.domain.UserDynamic;
import com.redpigmall.domain.UserShare;

/**
 * 
 * <p>
 * Title: RedPigSnsManageAction.java
 * </p>
 * 
 * <p>
 * Description:超级后台用户动态与分享内容列表
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
 * @date 2015-01-16
 * 
 * @version redpigmall_b2b2c 8.0
 */
@Controller
public class RedPigSnsManageAction extends BaseAction{
	
	/**
	 * sns分享列表
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param userName
	 * @return
	 */
	@SecurityMapping(title = "sns分享列表", value = "/sns_share_list*", rtype = "admin", rname = "分享列表", rcode = "sns_share", rgroup = "会员")
	@RequestMapping({ "/sns_share_list" })
	public ModelAndView sns_share_list(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String userName) {
		ModelAndView mv = new RedPigJModelAndView("admin/blue/sns_share_list.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		
		Map<String,Object> maps= this.redPigQueryTools.getParams(currentPage,"addTime", "desc");
		
		IPageList pList = this.usershareService.list(maps);
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		return mv;
	}
	
	/**
	 * sns分享删除
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param share_id
	 * @return
	 */
	@SecurityMapping(title = "sns分享删除", value = "/sns_share_del*", rtype = "admin", rname = "分享列表", rcode = "sns_share", rgroup = "会员")
	@RequestMapping({ "/sns_share_del" })
	public String sns_share_del(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String share_id) {
		UserShare userShare = this.usershareService.selectByPrimaryKey(CommUtil.null2Long(share_id));
		if (userShare != null) {
			this.usershareService.deleteById(CommUtil.null2Long(share_id));
		}
		return "redirect:sns_share_list?currentPage=" + currentPage;
	}
	
	/**
	 * sns动态列表
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param userName
	 * @return
	 */
	@SecurityMapping(title = "sns动态列表", value = "/sns_dynamic_list*", rtype = "admin", rname = "动态列表", rcode = "sns_hot", rgroup = "会员")
	@RequestMapping({ "/sns_dynamic_list" })
	public ModelAndView sns_dynamic_list(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String userName) {
		ModelAndView mv = new RedPigJModelAndView("admin/blue/sns_dynamic_list.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		
		Map<String,Object> maps= this.redPigQueryTools.getParams(currentPage,"addTime", "desc");
        
		IPageList pList = this.dynamicService.list(maps);
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		mv.addObject("snsTools", this.snsTools);
		return mv;
	}
	
	/**
	 * sns动态删除
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param dynamic_id
	 * @return
	 */
	@SecurityMapping(title = "sns动态删除", value = "/sns_dynamic_del*", rtype = "admin", rname = "动态列表", rcode = "sns_hot", rgroup = "会员")
	@RequestMapping({ "/sns_dynamic_del" })
	public String sns_dynamic_del(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String dynamic_id) {
		UserDynamic userDynamic = this.dynamicService.selectByPrimaryKey(CommUtil.null2Long(dynamic_id));
		if (userDynamic != null) {
			this.dynamicService.deleteById(CommUtil.null2Long(dynamic_id));
		}
		return "redirect:sns_dynamic_list?currentPage=" + currentPage;
	}
}
