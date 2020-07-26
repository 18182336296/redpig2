package com.redpigmall.manage.admin.action.withdrawal;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;

/**   
 * Copyright © 2018 eSunny Info. Tech Ltd. All rights reserved.
 * @Package: com.redpigmall.manage.admin.action.withdrawal 
 * @author: zxq@yihexinda.com  
 * @date: 2018年9月12日 下午5:19:40 
 */

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.redpigmall.api.annotation.SecurityMapping;
import com.redpigmall.api.mv.RedPigJModelAndView;
import com.redpigmall.api.tools.CommUtil;
import com.redpigmall.domain.WithdrawalSetting;
import com.redpigmall.manage.admin.action.base.BaseAction;
import com.redpigmall.service.RedPigWithDrawalManageService;

/**
 * 提现管理控制器
 * @ClassName RedPigWithDrawalManageAction
 * @author zxq@yihexinda.com
 * @date 2018年9月12日 下午5:19:40
 * @version 1.0
 * <p>Company: http://www.yihexinda.com</p>
 */
@Controller
public class RedPigWithDrawalManageAction extends BaseAction{
	@Autowired
	private RedPigWithDrawalManageService withDrawalManageService;
	
	/**
	 * 显示提现设置列表
	 * @Title: withDrawalView
	 * @param request
	 * @param response
	 * @author zxq@yihexinda.com
	 * @date 2018年9月12日 下午5:24:10
	 * @return ModelAndView
	 */
	@SecurityMapping(title = "提现设置", value = "/withdrawal_setting_list", rtype = "admin", rname = "提现设置", rcode = "direct_set", rgroup = "提现管理")
	@RequestMapping(value = "/withdrawal_setting_list", method = RequestMethod.GET)
	public ModelAndView withDrawalView(HttpServletRequest request,HttpServletResponse response) {
		ModelAndView model = new RedPigJModelAndView("admin/blue/withdrawal/withdrawal_setting.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		WithdrawalSetting withdrawalSetting=withDrawalManageService.selectOne();
		model.addObject("obj", withdrawalSetting);
		return model;
	}
	
	/**
	 * 提现设置保存
	 * @Title: saveDrawalSetting
	 * @param quota 提现限额
	 * @param withdrawalBeginDate 起始时间
	 * @param withdrawalEndDate 截止时间
	 * @param isDistribeWithdrawal 分销提现状态
	 * @param id 主键
	 * @author zxq@yihexinda.com
	 * @date 2018年9月12日 下午6:58:18
	 * @return ModelAndView
	 */
	@RequestMapping(value = "/withdrawal_setting_add",method=RequestMethod.POST)
	public ModelAndView saveDrawalSetting(BigDecimal quota,String withdrawalBeginDate,String withdrawalEndDate,
			Byte isDistribeWithdrawal,Long id,HttpServletRequest request,HttpServletResponse response) {
		ModelAndView model = new RedPigJModelAndView("admin/blue/withdrawal/withdrawal_setting.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		//获取提现方式的多选值
		String[] withdrawalWays=request.getParameterValues("withdrawalWay");
		//设置字符串的值
		WithdrawalSetting withDrawal=new WithdrawalSetting();
		withDrawal.setIsDistribeWithdrawal(isDistribeWithdrawal);
		withDrawal.setQuota(quota);
		//将字符串转换成日期
		withDrawal.setWithdrawalBeginDate(CommUtil.formatDate(withdrawalBeginDate));
		withDrawal.setWithdrawalEndDate(CommUtil.formatDate(withdrawalEndDate));
		// 提现方式拼接字符串
		if (null != withdrawalWays) {
			String withdrawalWay = Arrays.asList(withdrawalWays).stream().collect(Collectors.joining(","));
			withDrawal.setWithdrawalWay(withdrawalWay);
		}
		//如果是编辑操作设置ID
		if (null != id) {
			withDrawal.setId(id);
		}
		//查询数据库中是否存在一条记录
		int count = withDrawalManageService.selectCount(Collections.emptyMap());
		//不存在则插入否则更新
		if (0 == count) {
			withDrawalManageService.insert(withDrawal);
		}else {
			withDrawalManageService.updateByPrimaryKey(withDrawal);
		}
		//获取数据库中的记录显示在前端
		 model=withDrawalView(request, response);
		return model;
	}
	
}
