package com.redpigmall.manage.admin.action.store;

import java.util.Date;
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
import com.redpigmall.api.tools.WebForm;
import com.redpigmall.manage.admin.action.base.BaseAction;
import com.redpigmall.domain.VatInvoice;
/**
 * 
 * <p>
 * Title: RedPigVatInvoiceManageAction.java
 * </p>
 * 
 * <p>
 * Description:增值税发票管理
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
 * @date 2014年5月27日
 * 
 * @version redpigmall_b2b2c 8.0
 */
@Controller
public class RedPigVatInvoiceManageAction extends BaseAction{
	
	/**
	 * 增值税发票信息列表页
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @return
	 */
	@SecurityMapping(title = "增值税发票信息列表页", value = "/vatinvoice_list*", rtype = "admin", rname = "增票管理", rcode = "admin_store_set", rgroup = "店铺")
	@RequestMapping({ "/vatinvoice_list" })
	public ModelAndView vatinvoice_list(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,
			String orderType) {
		ModelAndView mv = new RedPigJModelAndView("admin/blue/vatinvoice_list.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		String url = this.configService.getSysConfig().getAddress();
		if ((url == null) || (url.equals(""))) {
			url = CommUtil.getURL(request);
		}
		String params = "";
		
		Map<String,Object> maps= this.redPigQueryTools.getParams(currentPage, orderBy, orderType);
		maps.put("orderBy", "status asc,");
		maps.put("orderType", "addTime asc");
		
		IPageList pList = this.vatinvoiceService.list(maps);
		CommUtil.saveIPageList2ModelAndView(
				url + "/vatinvoice_list.html", "", params, pList, mv);
		return mv;
	}
	
	/**
	 * 增值税发票信息审核管理
	 * @param request
	 * @param response
	 * @param id
	 * @return
	 */
	@SecurityMapping(title = "增值税发票信息审核管理", value = "/vatinvoice_edit*", rtype = "admin", rname = "增票管理", rcode = "admin_store_set", rgroup = "店铺")
	@RequestMapping({ "/vatinvoice_edit" })
	public ModelAndView vatinvoice_edit(HttpServletRequest request,
			HttpServletResponse response, String id) {
		ModelAndView mv = new RedPigJModelAndView("admin/blue/vatinvoice_edit.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		if ((id != null) && (!id.equals(""))) {
			VatInvoice vatinvoice = this.vatinvoiceService.selectByPrimaryKey(Long
					.valueOf(Long.parseLong(id)));
			mv.addObject("obj", vatinvoice);
			mv.addObject("edit", Boolean.valueOf(true));
		}
		return mv;
	}
	
	/**
	 * 增值税发票信息保存管理
	 * @param request
	 * @param response
	 * @param id
	 * @param status
	 * @param list_url
	 * @param add_url
	 * @param remark
	 * @return
	 */
	@SecurityMapping(title = "增值税发票信息保存管理", value = "/vatinvoice_save*", rtype = "admin", rname = "增票管理", rcode = "admin_store_set", rgroup = "店铺")
	@RequestMapping({ "/vatinvoice_save" })
	public ModelAndView vatinvoice_saveEntity(HttpServletRequest request,
			HttpServletResponse response, String id, String status,
			String list_url, String add_url, String remark) {
		
		VatInvoice vatinvoice = null;
		if (id.equals("")) {
			vatinvoice = (VatInvoice) WebForm.toPo(request, VatInvoice.class);
			vatinvoice.setAddTime(new Date());
		} else {
			VatInvoice obj = this.vatinvoiceService.selectByPrimaryKey(Long.parseLong(id));
			vatinvoice = (VatInvoice) WebForm.toPo(request, obj);
		}
		vatinvoice.setStatus(CommUtil.null2Int(status));
		vatinvoice.setRemark(remark);
		if (id.equals("")) {
			this.vatinvoiceService.saveEntity(vatinvoice);
		} else {
			this.vatinvoiceService.updateById(vatinvoice);
		}
		ModelAndView mv = new RedPigJModelAndView("admin/blue/success.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		mv.addObject("list_url", list_url);
		mv.addObject("op_title", "审核增票资质成功");
		return mv;
	}
}
