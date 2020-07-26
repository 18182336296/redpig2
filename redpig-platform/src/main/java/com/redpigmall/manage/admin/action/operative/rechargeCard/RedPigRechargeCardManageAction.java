package com.redpigmall.manage.admin.action.operative.rechargeCard;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.google.common.collect.Maps;
import com.redpigmall.api.annotation.SecurityMapping;
import com.redpigmall.api.mv.RedPigJModelAndView;
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.api.sec.SecurityUserHolder;
import com.redpigmall.api.tools.CommUtil;
import com.redpigmall.manage.admin.action.base.BaseAction;
import com.redpigmall.domain.RechargeCard;
import com.redpigmall.domain.SysConfig;

/**
 * 
 * <p>
 * Title: RechargeCardManageAction.java
 * </p>
 * 
 * <p>
 * Description: 系统充值卡管理控制器，用来显示、添加、删除等充值卡信息操作
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
 * @date 2014-10-15
 * 
 * @version redpigmall_b2b2c 2016
 */
@SuppressWarnings("unchecked")
@Controller
public class RedPigRechargeCardManageAction extends BaseAction{
	
	/**
	 * 充值接口设置
	 * @param request
	 * @param response
	 * @return
	 */
	@SecurityMapping(title = "充值接口设置", value = "/set_ofcard*", rtype = "admin", rname = "接口设置", rcode = "admin_set_ofcard", rgroup = "交易")
	@RequestMapping({ "/set_ofcard" })
	public ModelAndView set_ofcard(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new RedPigJModelAndView(
				"admin/blue/set_ofcard_setting.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request, response);
		return mv;
	}
	
	/**
	 * 充值接口
	 * @param request
	 * @param response
	 * @param id
	 * @param ofcard
	 * @param ofcard_userid
	 * @param ofcard_userpws
	 * @param ofcard_mobile_profit
	 * @return
	 */
	@SecurityMapping(title = "充值接口", value = "/set_ofcard*", rtype = "admin", rname = "接口设置", rcode = "admin_set_ofcard", rgroup = "交易")
	@RequestMapping({ "/set_ofcard_save" })
	public ModelAndView set_ofcard_save(
			HttpServletRequest request,
			HttpServletResponse response, 
			String id, 
			String ofcard,
			String ofcard_userid, 
			String ofcard_userpws,
			String ofcard_mobile_profit) {
		
		ModelAndView mv = new RedPigJModelAndView("admin/blue/success.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request, response);
		
		SysConfig config = this.redPigSysConfigService.getSysConfig();
		
		config.setOfcard(CommUtil.null2Boolean(ofcard));
		config.setOfcard_userid(ofcard_userid);
		config.setOfcard_userpws(ofcard_userpws);
		config.setOfcard_mobile_profit(BigDecimal.valueOf(CommUtil.null2Double(ofcard_mobile_profit)));
		this.redPigSysConfigService.update(config);
		
		mv.addObject("op_title", "充值接口保存成功");
		mv.addObject("list_url", CommUtil.getURL(request) + "/set_ofcard");
		return mv;
	}
	
	/**
	 * 充值卡列表
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @param rc_sn
	 * @param rc_mark
	 * @param rc_status
	 * @return
	 */
	@SecurityMapping(title = "充值卡列表", value = "/recharge_card_list*", rtype = "admin", rname = "平台充值卡", rcode = "recharge_card_admin", rgroup = "运营")
	@RequestMapping({ "/recharge_card_list" })
	public ModelAndView recharge_card_list(
			HttpServletRequest request,
			HttpServletResponse response, 
			String currentPage, 
			String orderBy,
			String orderType, 
			String rc_sn, 
			String rc_mark, 
			String rc_status) {
		
		ModelAndView mv = new RedPigJModelAndView(
				"admin/blue/recharge_card_list.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request, response);
		
		Map<String,Object> params = this.redPigQueryTools.getParams(currentPage, orderBy, orderType);
		
		if (!CommUtil.null2String(rc_sn).equals("")) {
			
			params.put("rc_sn_like", CommUtil.null2String(rc_sn));
			
		}
		
		if (!CommUtil.null2String(rc_mark).equals("")) {
			params.put("rc_mark", CommUtil.null2String(rc_mark));
			
		}
		
		if (!CommUtil.null2String(rc_status).equals("")) {
			
			params.put("rc_status", CommUtil.null2Int(rc_status));
		}
		
		IPageList pList = this.redPigRechargeCardService.list(params);
		mv.addObject("rc_sn", rc_sn);
		mv.addObject("rc_mark", rc_mark);
		mv.addObject("rc_status", rc_status);
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		return mv;
	}
	
	/**
	 * 充值卡新增
	 * @param request
	 * @param response
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "充值卡新增", value = "/recharge_card_add*", rtype = "admin", rname = "平台充值卡", rcode = "recharge_card_admin", rgroup = "运营")
	@RequestMapping({ "/recharge_card_add" })
	public ModelAndView recharge_card_add(HttpServletRequest request,
			HttpServletResponse response, String currentPage) {
		ModelAndView mv = new RedPigJModelAndView(
				"admin/blue/recharge_card_add.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request, response);
		
		mv.addObject("currentPage", currentPage);
		return mv;
	}
	
	/**
	 * 充值卡列表保存
	 * @param request
	 * @param response
	 * @param type
	 * @param rc_count
	 * @param rc_prefix
	 * @param rc_amount
	 * @param rc_mark
	 * @param rc_sns
	 * @return
	 * @throws IOException
	 */
	@SecurityMapping(title = "充值卡列表保存", value = "/recharge_card_save*", rtype = "admin", rname = "平台充值卡", rcode = "recharge_card_admin", rgroup = "运营")
	@RequestMapping({ "/recharge_card_save" })
	public ModelAndView recharge_card_save(
			HttpServletRequest request,
			HttpServletResponse response, 
			String type, 
			String rc_count,
			String rc_prefix, 
			String rc_amount, 
			String rc_mark, 
			String rc_sns)
			throws IOException {
		
		String rc_sn;
		if (type.equals("auto")) {
			for (int i = 0; i < CommUtil.null2Int(rc_count); i++) {
				rc_sn = CommUtil.null2String(rc_prefix)
						+ CommUtil.null2String(UUID.randomUUID()).replaceAll(
								"-", "");
				Map<String, Object> params = Maps.newHashMap();
				params.put("rc_sn", rc_sn);
				List<RechargeCard> rc_list = this.redPigRechargeCardService.queryPageList(params);
				
				if (rc_list.size() == 0) {
					RechargeCard rc = new RechargeCard();
					rc.setAddTime(new Date());
					rc.setRc_amount(BigDecimal.valueOf(CommUtil.null2Double(rc_amount)));
					rc.setRc_mark(rc_mark);
					rc.setRc_pub_user_id(SecurityUserHolder.getCurrentUser().getId());
					rc.setRc_pub_user_name(SecurityUserHolder.getCurrentUser().getUserName());
					rc.setRc_sn(rc_sn);
					this.redPigRechargeCardService.saveEntity(rc);
				}
			}
		}
		if (type.equals("hand")) {
			for (String rc_sn1 : CommUtil.str2list(rc_sns)) {
				if (!CommUtil.null2String(rc_sn1).equals("")) {
					Map<String, Object> params = Maps.newHashMap();
					params.put("rc_sn", rc_sn1);
					List<RechargeCard> rc_list = this.redPigRechargeCardService.queryPageList(params);
					
					if (rc_list.size() == 0) {
						RechargeCard rc = new RechargeCard();
						rc.setAddTime(new Date());
						rc.setRc_amount(BigDecimal.valueOf(CommUtil
								.null2Double(rc_amount)));
						rc.setRc_mark(rc_mark);
						rc.setRc_pub_user_id(SecurityUserHolder
								.getCurrentUser().getId());
						rc.setRc_pub_user_name(SecurityUserHolder
								.getCurrentUser().getUserName());
						rc.setRc_sn(rc_sn1);
						this.redPigRechargeCardService.saveEntity(rc);
					}
				}
			}
		}
		if (type.equals("import")) {
			String uploadFilePath = this.redPigSysConfigService.getSysConfig()
					.getUploadFilePath();
			String saveFilePathName = request.getSession().getServletContext()
					.getRealPath("/")
					+ uploadFilePath + File.separator + "card";
			Map<String, Object> map = CommUtil.saveFileToServer(request,
					"card_txt", saveFilePathName, "", new String[] { "txt" });
			if (map.get("fileName") != "") {
				String file_name = CommUtil.null2String(map.get("fileName"));

				String path = saveFilePathName + File.separator + file_name;
				BufferedReader br = new BufferedReader(new FileReader(path));
				String rc_sn1 = "";
				while ((rc_sn1 = br.readLine()) != null) {
					if (!CommUtil.null2String(rc_sn1).equals("")) {
						Map<String, Object> params = Maps.newHashMap();
						params.put("rc_sn", rc_sn1);
						List<RechargeCard> rc_list = this.redPigRechargeCardService.queryPageList(params);
						if (rc_list.size() == 0) {
							RechargeCard rc = new RechargeCard();
							rc.setAddTime(new Date());
							rc.setRc_amount(BigDecimal.valueOf(CommUtil
									.null2Double(rc_amount)));
							rc.setRc_mark(rc_mark);
							rc.setRc_pub_user_id(SecurityUserHolder
									.getCurrentUser().getId());
							rc.setRc_pub_user_name(SecurityUserHolder
									.getCurrentUser().getUserName());
							rc.setRc_sn(rc_sn1);
							this.redPigRechargeCardService.saveEntity(rc);
						}
					}
				}
				br.close();
				CommUtil.deleteFile(path);
			}
		}
		ModelAndView mv = new RedPigJModelAndView("admin/blue/success.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request, response);
		mv.addObject("list_url", CommUtil.getURL(request)
				+ "/recharge_card_list");
		mv.addObject("op_title", "充值卡保存成功");
		return mv;
	}
	
	/**
	 * 充值卡列表删除
	 * @param request
	 * @param response
	 * @param mulitId
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "充值卡列表删除", value = "/recharge_card_del*", rtype = "admin", rname = "平台充值卡", rcode = "recharge_card_admin", rgroup = "运营")
	@RequestMapping({ "/recharge_card_del" })
	public String recharge_card_del(
			HttpServletRequest request,
			HttpServletResponse response, 
			String mulitId, 
			String currentPage) {
		
		String[] ids = mulitId.split(",");
		for (String id : ids) {

			if (!id.equals("")) {
				RechargeCard obj = this.redPigRechargeCardService.selectByPrimaryKey(CommUtil.null2Long(id));
				
				if (obj.getRc_status() == 0) {
					this.redPigRechargeCardService.deleteById(CommUtil.null2Long(id));
				}
			}
		}
		return "redirect:recharge_card_list?currentPage=" + currentPage;
	}
}
