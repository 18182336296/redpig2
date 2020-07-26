package com.redpigmall.manage.admin.action.self.adminEva;

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
import com.redpigmall.manage.admin.action.base.BaseAction;

/**
 * 
 * <p>
 * Title: RedPigSelfConsultManageAction.java
 * </p>
 * 
 * <p>
 * Description:管理员服务评价
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
 * @date 2014-10-29
 * 
 * @version redpigmall_b2b2c 2016
 */
@Controller
public class RedPigSelfAdminEvaManageAction extends BaseAction {
	/**
	 * 
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @param request
	 * @param response
	 * @return
	 */
	@SecurityMapping(title = "管理员服务评价", value = "/admin_eva*", rtype = "admin", rname = "服务评价", rcode = "admin_evas", rgroup = "自营")
	@RequestMapping({ "/admin_eva" })
	public ModelAndView admin_eva(String currentPage, String orderBy,
			String orderType, HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new RedPigJModelAndView("admin/blue/admin_eva.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		
		Map<String,Object> params = this.redPigQueryTools.getParams(currentPage, orderBy, orderType);
		
		params.put("redPigUserRole1", "ADMIN");
		params.put("redPigUserRole2", "ADMIN_BUYER_SELLER");
		params.put("admin_sp_id", -1);
		
		IPageList pList = this.userService.list(params);
		String url = this.configService.getSysConfig().getAddress();
		if ((url == null) || (url.equals(""))) {
			url = CommUtil.getURL(request);
		}
		CommUtil.saveIPageList2ModelAndView(url + "/admin_eva.html", "",
				"", pList, mv);
		mv.addObject("userRole", "ADMIN");
		return mv;
	}
}
