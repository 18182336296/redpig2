package com.redpigmall.manage.buyer.action;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;
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
import com.redpigmall.api.sec.SecurityUserHolder;
import com.redpigmall.api.tools.CommUtil;
import com.redpigmall.api.tools.WebForm;
import com.redpigmall.manage.buyer.action.base.BaseAction;
import com.redpigmall.domain.Accessory;
import com.redpigmall.domain.User;
import com.redpigmall.domain.VatInvoice;
/**
 * 
 * <p>
 * Title: RedPigRefundApplyFormBuyerAction.java
 * </p>
 * 
 * <p>
 * Description: 增票资质管理
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
 * @date 2017-04-16
 * 
 * @version redpigmall_b2b2c v8.0 2016版
 */
@Controller
public class RedPigVatinvoiceBuyerAction extends BaseAction{
	
	/**
	 * 增值税发票信息申请
	 * @param request
	 * @param response
	 * @return
	 */
	@SecurityMapping(title = "增值税发票信息申请", value = "/buyer/vatinvoice_apply*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping({ "/buyer/vatinvoice_apply" })
	public ModelAndView vatinvoice_apply(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/usercenter/vatinvoice_apply.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		User user = SecurityUserHolder.getCurrentUser();
		
		if (user != null) {
			Map<String, Object> map = Maps.newHashMap();
			map.put("user_id", user.getId());
			List<VatInvoice> va_list = this.vatinvoiceService.queryPageList(map);
			
			if (va_list.size() > 0) {
				VatInvoice obj = va_list.get(0);
				mv.addObject("obj", obj);
			}
		}
		
		String[] strs = this.configService.getSysConfig().getImageSuffix().split("\\|");
		StringBuffer sb = new StringBuffer();
		for (String str : strs) {
			sb.append("." + str + ",");
		}
		mv.addObject("imageSuffix1", sb);
		return mv;
	}
	
	/**
	 * 增值税发票信息申请保存
	 * @param request
	 * @param response
	 * @param id
	 * @param companyName
	 * @param registerAddress
	 * @param taxpayerCode
	 * @param registerPhone
	 * @param registerbankName
	 * @param registerbankAccount
	 * @param va_img1
	 * @param va_img2
	 * @param va_img3
	 * @param va_img4
	 */
	@SecurityMapping(title = "增值税发票信息申请保存", value = "/buyer/vatinvoice_save*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping({ "/buyer/vatinvoice_save" })
	public void vatinvoice_saveEntity(HttpServletRequest request,
			HttpServletResponse response, String id, String companyName,
			String registerAddress, String taxpayerCode, String registerPhone,
			String registerbankName, String registerbankAccount,
			String va_img1, String va_img2, String va_img3, String va_img4) {
		
		VatInvoice vatinvoice = null;
		boolean ret = false;
		User user = SecurityUserHolder.getCurrentUser();
		if (user != null) {
			if ((id == null) || ("".equals(id))) {
				vatinvoice = (VatInvoice) WebForm.toPo(request,
						VatInvoice.class);
			} else {
				VatInvoice obj = this.vatinvoiceService.selectByPrimaryKey(Long
						.valueOf(Long.parseLong(id)));
				vatinvoice = (VatInvoice) WebForm.toPo(request, obj);
			}
			if (vatinvoice.getStatus() != 1) {
				vatinvoice.setAddTime(new Date());
				vatinvoice.setStatus(0);
				vatinvoice.setUser_id(user.getId());
				vatinvoice.setUser_name(user.getUserName());
				vatinvoice.setCompanyName(companyName);
				vatinvoice.setRegisterAddress(registerAddress);
				vatinvoice.setTaxpayerCode(taxpayerCode);
				vatinvoice.setRegisterPhone(registerPhone);
				vatinvoice.setRegisterbankName(registerbankName);
				vatinvoice.setRegisterbankAccount(registerbankAccount);
				Accessory acc1 = this.accessoryService.selectByPrimaryKey(CommUtil
						.null2Long(va_img1));
				if (acc1 != null) {
					vatinvoice.setTax_reg_card(acc1);
				}
				Accessory acc2 = this.accessoryService.selectByPrimaryKey(CommUtil
						.null2Long(va_img2));
				if (acc2 != null) {
					vatinvoice.setTax_general_card(acc2);
				}
				Accessory acc3 = this.accessoryService.selectByPrimaryKey(CommUtil
						.null2Long(va_img3));
				if (acc3 != null) {
					vatinvoice.setUser_license(acc3);
				}
				Accessory acc4 = this.accessoryService.selectByPrimaryKey(CommUtil
						.null2Long(va_img4));
				if (acc4 != null) {
					vatinvoice.setBank_permit_image(acc4);
				}
				if ((id == null) || ("".equals(id))) {
					this.vatinvoiceService.saveEntity(vatinvoice);
					ret = true;
				} else {
					this.vatinvoiceService.updateById(vatinvoice);
					ret = true;
				}
			}
		}
		response.setCharacterEncoding("utf-8");
		response.setHeader("Cache-Control", "no-cache");
		response.setContentType("text/plain");
		try {
			response.getWriter().print(ret);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 增值税发票信息删除
	 * @param request
	 * @param response
	 * @param id
	 * @return
	 */
	@SecurityMapping(title = "增值税发票信息删除", value = "/buyer/vatinvoice_del*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping({ "/buyer/vatinvoice_del" })
	public String vatinvoice_del(HttpServletRequest request,
			HttpServletResponse response, String id) {
		if (!id.equals("")) {
			VatInvoice vatinvoice = this.vatinvoiceService.selectByPrimaryKey(Long.valueOf(Long.parseLong(id)));
			User user = SecurityUserHolder.getCurrentUser();
			if ((vatinvoice != null) 
					&& (user != null)
					&& (user.getId().equals(vatinvoice.getUser_id()))) {
				this.vatinvoiceService.deleteById(vatinvoice.getId());
			}
		}
		return "redirect:/buyer/vatinvoice_apply";
	}
	
	/**
	 * 增值税发票证明图片加载
	 * @param request
	 * @param response
	 * @param img_name
	 */
	@SuppressWarnings("unchecked")
	@SecurityMapping(title = "增值税发票证明图片加载", value = "/buyer/vatinvoice_photo_load*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping({ "/buyer/vatinvoice_photo_load" })
	public void vatinvoice_photo_load(HttpServletRequest request,
			HttpServletResponse response, String img_name) {
		Map<String,Object> temp_map = Maps.newHashMap();
		String uploadFilePath = this.configService.getSysConfig()
				.getUploadFilePath();
		String saveFilePathName = request.getSession().getServletContext()
				.getRealPath("/")
				+ uploadFilePath + File.separator + "vatinvoice";
		Map<String, Object> map = Maps.newHashMap();
		String fileName = "";
		String path = CommUtil.getURL(request) + "/";
		String temp = "";
		
		try {
			map = CommUtil.saveFileToServer(request, img_name,
					saveFilePathName, "", null);
			if ((fileName.equals("")) && (map.get("fileName") != "")) {
				Accessory photo = new Accessory();
				photo.setName(CommUtil.null2String(map.get("fileName")));
				photo.setExt(CommUtil.null2String(map.get("mime")));
				photo.setSize(BigDecimal.valueOf(CommUtil.null2Double(map
						.get("fileSize"))));
				photo.setPath(uploadFilePath + "/vatinvoice");
				photo.setWidth(CommUtil.null2Int(map.get("width")));
				photo.setHeight(CommUtil.null2Int(map.get("height")));
				photo.setAddTime(new Date());
				this.accessoryService.saveEntity(photo);
				temp_map.put("type", "img");
				temp_map.put("id", photo.getId());
				temp_map.put("img_url",
						path + photo.getPath() + "/" + photo.getName());
				temp = JSON.toJSONString(temp_map);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		response.setCharacterEncoding("utf-8");
		response.setHeader("Cache-Control", "no-cache");
		response.setContentType("text/plain");
		try {
			response.getWriter().print(temp);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
