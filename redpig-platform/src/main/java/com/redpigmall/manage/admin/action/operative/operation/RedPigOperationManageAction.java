package com.redpigmall.manage.admin.action.operative.operation;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.google.common.collect.Maps;
import com.redpigmall.api.annotation.SecurityMapping;
import com.redpigmall.api.mv.RedPigJModelAndView;
import com.redpigmall.api.tools.CommUtil;
import com.redpigmall.manage.admin.action.base.BaseAction;
import com.redpigmall.domain.Navigation;
import com.redpigmall.domain.SysConfig;


/**
 * 
 * <p>
 * Title: RedPigOperationManageAction.java
 * </p>
 * 
 * <p>
 * Description: 超级后台运营基础功能管理器,主要功能: 1、运营基本设置 2、积分规则管理
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
public class RedPigOperationManageAction extends BaseAction{
	
	/**
	 * 基本设置
	 * @param request
	 * @param response
	 * @return
	 */
	@SecurityMapping(title = "基本设置", value = "/operation_base_set*", rtype = "admin", rname = "基本设置", rcode = "operation_base", rgroup = "运营")
	@RequestMapping({ "/operation_base_set" })
	public ModelAndView operation_base_set(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new RedPigJModelAndView(
				"admin/blue/operation_base_setting.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request, response);
		
		return mv;
	}
	
	
	/**
	 * 基本设置保存
	 * @param request
	 * @param response
	 * @param id
	 * @param integral
	 * @param integralStore
	 * @param voucher
	 * @param deposit
	 * @param gold
	 * @param goldMarketValue
	 * @param groupBuy
	 * @param group_meal_gold
	 * @param buygift_status
	 * @param buygift_meal_gold
	 * @param enoughreduce_status
	 * @param enoughreduce_meal_gold
	 * @param enoughreduce_max_count
	 * @param combin_amount
	 * @param combin_scheme_count
	 * @param combin_count
	 * @param combin_status
	 * @param whether_free
	 * @param cloudbuy
	 * @return
	 */
	@SecurityMapping(title = "基本设置保存", value = "/base_set_save*", rtype = "admin", rname = "基本设置", rcode = "operation_base", rgroup = "运营")
	@RequestMapping({ "/base_set_save" })
	public ModelAndView base_set_save(
			HttpServletRequest request,
			HttpServletResponse response, String id, String integral,
			String integralStore, String voucher, String deposit, String gold,
			String goldMarketValue, String groupBuy, String group_meal_gold,
			String buygift_status, String buygift_meal_gold,
			String enoughreduce_status, String enoughreduce_meal_gold,
			String enoughreduce_max_count, String combin_amount,
			String combin_scheme_count, String combin_count,
			String combin_status, String whether_free, String cloudbuy) {
		
		ModelAndView mv = new RedPigJModelAndView("admin/blue/success.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request, response);
		
		SysConfig config = this.redPigSysConfigService.getSysConfig();
		config.setIntegral(CommUtil.null2Boolean(integral));
		config.setIntegralStore(CommUtil.null2Boolean(integralStore));
		config.setVoucher(CommUtil.null2Boolean(voucher));
		config.setDeposit(CommUtil.null2Boolean(deposit));
		config.setGold(CommUtil.null2Boolean(gold));
		config.setGoldMarketValue(CommUtil.null2Int(goldMarketValue));
		config.setGroupBuy(CommUtil.null2Boolean(groupBuy));
		config.setGroup_meal_gold(CommUtil.null2Int(group_meal_gold));
		config.setBuygift_status(CommUtil.null2Int(buygift_status));
		config.setBuygift_meal_gold(CommUtil.null2Int(buygift_meal_gold));
		config.setEnoughreduce_status(CommUtil.null2Int(enoughreduce_status));
		config.setEnoughreduce_meal_gold(CommUtil.null2Int(enoughreduce_meal_gold));
		config.setEnoughreduce_max_count(CommUtil.null2Int(enoughreduce_max_count));
		config.setCombin_amount(CommUtil.null2Int(combin_amount));
		config.setCombin_scheme_count(CommUtil.null2Int(combin_scheme_count));
		config.setCombin_count(CommUtil.null2Int(combin_count));
		config.setWhether_free(CommUtil.null2Int(whether_free));
		config.setCloudbuy(CommUtil.null2Int(cloudbuy));
		config.setCombin_status(CommUtil.null2Int(combin_status));
		
		if (id.equals("")) {
			this.redPigSysConfigService.save(config);
		} else {
			this.redPigSysConfigService.update(config);
		}
		
		if (config.getIntegralStore()) {
			Map<String, Object> params = Maps.newHashMap();
			params.put("url", "integral/index");
			List<Navigation> navs = this.redPigNavigationService.queryPageList(params);
			
			if (navs.size() == 0) {
				Navigation nav = new Navigation();
				nav.setAddTime(new Date());
				nav.setDisplay(true);
				nav.setLocation(0);
				nav.setNew_win(1);
				nav.setSequence(2);
				nav.setSysNav(true);
				nav.setTitle("积分商城");
				nav.setType("diy");
				nav.setUrl("integral/index");
				nav.setOriginal_url("integral/index");
				this.redPigNavigationService.saveEntity(nav);
			}
		} else {
			Map<String, Object> params = Maps.newHashMap();
			params.put("url", "integral/index");
			List<Navigation> navs = this.redPigNavigationService.queryPageList(params);
			
			for (Navigation nav : navs) {
				this.redPigNavigationService.deleteById(nav.getId());
			}
		}
		if (config.getWhether_free() == 1) {
			Map<String, Object> params = Maps.newHashMap();
			params.put("url", "free/index");
			List<Navigation> navs = this.redPigNavigationService.queryPageList(params);
			if (navs.size() == 0) {
				Navigation nav = new Navigation();
				nav.setAddTime(new Date());
				nav.setDisplay(true);
				nav.setLocation(0);
				nav.setNew_win(1);
				nav.setSequence(7);
				nav.setSysNav(true);
				nav.setTitle("0元试用");
				nav.setType("diy");
				nav.setUrl("free/index");
				nav.setOriginal_url("free/index");
				this.redPigNavigationService.saveEntity(nav);
			}
		} else {
			Map<String, Object> params = Maps.newHashMap();
			params.put("url", "free/index");
			List<Navigation> navs = this.redPigNavigationService.queryPageList(params);
			for (Navigation nav : navs) {
				this.redPigNavigationService.deleteById(nav.getId());
			}
		}
		
		if (config.getGroupBuy()) {
			Map<String, Object> params = Maps.newHashMap();
			params.put("url", "group/index");
			List<Navigation> navs = this.redPigNavigationService.queryPageList(params);
			if (navs.size() == 0) {
				Navigation nav = new Navigation();
				nav.setAddTime(new Date());
				nav.setDisplay(true);
				nav.setLocation(0);
				nav.setNew_win(1);
				nav.setSequence(3);
				nav.setSysNav(true);
				nav.setTitle("团购");
				nav.setType("diy");
				nav.setUrl("group/index");
				nav.setOriginal_url("group/index");
				this.redPigNavigationService.saveEntity(nav);
				
			}
		} else {
			Map<String, Object> params = Maps.newHashMap();
			params.put("url", "group/index");
			List<Navigation> navs = this.redPigNavigationService.queryPageList(params);
			
			for (Navigation nav : navs) {
				this.redPigNavigationService.deleteById(nav.getId());
			}
		}
		mv.addObject("op_title", "保存基本设置成功");
		mv.addObject("list_url", CommUtil.getURL(request)
				+ "/operation_base_set");
		return mv;
	}
	
	/**
	 * 积分规则
	 * @param request
	 * @param response
	 * @return
	 */
	@SecurityMapping(title = "积分规则", value = "/operation_integral_rule*", rtype = "admin", rname = "积分规则", rcode = "integral_rule", rgroup = "运营")
	@RequestMapping({ "/operation_integral_rule" })
	public ModelAndView integral_rule(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new RedPigJModelAndView(
				"admin/blue/operation_integral_rule.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request, response);
		
		return mv;
	}
	
	/**
	 * 积分规则保存
	 * @param request
	 * @param response
	 * @param id
	 * @param memberRegister
	 * @param memberDayLogin
	 * @param indentComment
	 * @param consumptionRatio
	 * @param everyIndentLimit
	 * @param sign_integral
	 * @return
	 */
	@SecurityMapping(title = "积分规则保存", value = "/integral_rule_save*", rtype = "admin", rname = "积分规则", rcode = "integral_rule", rgroup = "运营")
	@RequestMapping({ "/integral_rule_save" })
	public ModelAndView integral_rule_save(
			HttpServletRequest request,
			HttpServletResponse response, 
			String id, 
			String memberRegister,
			String memberDayLogin, 
			String indentComment,
			String consumptionRatio, 
			String everyIndentLimit,
			String sign_integral) {
		
		ModelAndView mv = new RedPigJModelAndView("admin/blue/success.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request, response);
		
		SysConfig config = this.redPigSysConfigService.getSysConfig();
		config.setMemberRegister(CommUtil.null2Int(memberRegister));
		config.setMemberDayLogin(CommUtil.null2Int(memberDayLogin));
		config.setIndentComment(CommUtil.null2Int(indentComment));
		config.setConsumptionRatio(CommUtil.null2Int(consumptionRatio));
		config.setEveryIndentLimit(CommUtil.null2Int(everyIndentLimit));
		config.setSign_integral(CommUtil.null2Int(sign_integral));
		if (id.equals("")) {
			this.redPigSysConfigService.save(config);
		} else {
			this.redPigSysConfigService.update(config);
		}
		
		mv.addObject("op_title", "保存积分设置成功");
		mv.addObject("list_url", CommUtil.getURL(request)
				+ "/operation_integral_rule");
		return mv;
	}
}
