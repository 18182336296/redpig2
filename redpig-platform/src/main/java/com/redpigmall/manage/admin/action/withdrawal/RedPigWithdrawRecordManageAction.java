/**   
 * Copyright © 2018 eSunny Info. Tech Ltd. All rights reserved.
 * @Package: com.redpigmall.manage.admin.action.withdrawal 
 * @author: zxq@yihexinda.com  
 * @date: 2018年9月13日 下午4:01:01 
 */
package com.redpigmall.manage.admin.action.withdrawal;

import java.util.Date;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;
import com.redpigmall.api.annotation.SecurityMapping;
import com.redpigmall.api.mv.RedPigJModelAndView;
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.api.sec.SecurityUserHolder;
import com.redpigmall.api.tools.CommUtil;
import com.redpigmall.domain.WithawalRecord;
import com.redpigmall.manage.admin.action.base.BaseAction;
import com.redpigmall.service.RedPigWithdrawRecordService;

/**
 * 提现记录控制器
 * @ClassName RedPigWithdrawRecordManageAction
 * @author zxq@yihexinda.com
 * @date 2018年9月13日 下午4:01:01
 * @version 1.0
 * <p>Company: http://www.yihexinda.com</p>
 */
@Controller
public class RedPigWithdrawRecordManageAction extends BaseAction{
	@Autowired
	private RedPigWithdrawRecordService withdrawRecordService;
	public static final byte ACCEPT_STATUS = 2;
	
	/**
	 * 提现记录列表
	 * @Title: withDrawalView
	 * @param request
	 * @param response
	 * @param mobile 联系电话
	 * @param acceptStartTime 起始时间
	 * @param acceptEndTime 截止时间
	 * @param acceptStatus 受理状态
	 * @param currentPage 当前页
	 * @author zxq@yihexinda.com
	 * @date 2018年9月13日 下午4:08:36
	 * @return ModelAndView
	 */
	@SecurityMapping(title = "提现申请", value = "/withdrawal_record_list", rtype = "admin", rname = "提现申请", rcode = "direct_set", rgroup = "提现管理")
	@RequestMapping(value = "/withdrawal_record_list", method = RequestMethod.GET)
	public ModelAndView withDrawalView(HttpServletRequest request, HttpServletResponse response, Byte acceptStatus,
			String mobile, String acceptStartTime,String acceptEndTime, String currentPage) {
		// 定义返回识图
		ModelAndView model = new RedPigJModelAndView("admin/blue/withdrawal/withdrawal_record_apply.html",
				this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request, response);
		// TODO:获取参数
		Map<String, Object> params = this.redPigQueryTools.getParams(currentPage, "createTime", "desc");
		// TODO:查询条件-联系方式
		if (StringUtils.isNotBlank(mobile)) {
			params.put("mobile", mobile.trim());
			model.addObject("mobile", mobile.trim());
		}
		// TODO:查询条件-受理状态
		if (null != acceptStatus && 0 != acceptStatus) {
			params.put("acceptStatus", acceptStatus);
			model.addObject("acceptStatus", acceptStatus);
		}
		// TODO:查询条件-受理起始时间
		if (StringUtils.isNotBlank(acceptStartTime)) {
			params.put("acceptStartTime", acceptStartTime.trim());
			model.addObject("acceptStartTime", acceptStartTime.trim());
		}
		// TODO:查询条件-受理截止时间
		if (StringUtils.isNotBlank(acceptEndTime)) {
			params.put("acceptEndTime", acceptEndTime.trim());
			model.addObject("acceptEndTime", acceptEndTime.trim());
		}
		// TODO:获取分页参数列表
		IPageList pList = this.withdrawRecordService.list(params);
		// 向页面传参
		CommUtil.saveIPageList2ModelAndView("", "", null, pList, model);
		params.clear();
		return model;
	}
	/**
	 * 提现申请受理操作
	 * @Title: withDrawalView
	 * @param id 主键
	 * @param request
	 * @param response
	 * @author zxq@yihexinda.com
	 * @date 2018年9月14日 上午9:45:41
	 * @return ModelAndView
	 */
	@RequestMapping(value = "/withdrawal_record_accept", method = RequestMethod.GET)
	public ModelAndView withDrawalView(Long id,HttpServletRequest request, HttpServletResponse response) {
		// 定义返回识图
		String viewName = "admin/blue/error.html";
		//定义返回提示
		String message = "受理失败";
		//如果未查询到结果跳转到错误页面
		WithawalRecord query = this.withdrawRecordService.selectById(id);
		if (null != query) {
			//TODO:判断是否已受理
			if (null != query.getAcceptStatus() && ACCEPT_STATUS == query.getAcceptStatus()) {
				ModelAndView model = new RedPigJModelAndView(viewName, this.configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 0, request, response);
				model.addObject("op_title", "受理失败,改申请已被受理");
				model.addObject("list_url", CommUtil.getURL(request) + "/withdrawal_record_list");
				return model;
			}
			WithawalRecord record = new WithawalRecord();
			record.setId(id);
			// 受理人ID
			record.setAcceptId(SecurityUserHolder.getCurrentUser().getId());
			// 受理人名称
			record.setReceiver(SecurityUserHolder.getCurrentUser().getUserName());
			// 受理状态
			record.setAcceptStatus(ACCEPT_STATUS);
			// 受理时间
			record.setAcceptTime(new Date());
			//TODO:根据ID更新不为null的字段值
			this.withdrawRecordService.updateByPrimaryKeySelective(record);
			viewName = "admin/blue/success.html";
			message = "受理成功";
		}
		ModelAndView model = new RedPigJModelAndView(viewName, this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);

		model.addObject("op_title", message);
		model.addObject("list_url", CommUtil.getURL(request) + "/withdrawal_record_list");
		return model;
	}
}
