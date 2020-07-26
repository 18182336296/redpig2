package com.redpigmall.manage.admin.action;

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
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.api.tools.CommUtil;
import com.redpigmall.manage.admin.action.base.BaseAction;
import com.redpigmall.domain.CloudPurchaseLottery;
import com.redpigmall.domain.CloudPurchaseRecord;

/**
 * 
 * <p>
 * Title: RedPigCloudPurchaseLotteryManageAction.java
 * </p>
 * 
 * <p>
 * Description: 云购分类管理
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
 * @date 2016年5月27日
 * 
 * @version redpigmall_b2b2c 8.0
 */
@Controller
public class RedPigCloudPurchaseLotteryManageAction extends BaseAction{
	
	/**
	 * 云购彩票列表
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @param goods_name
	 * @param status
	 * @return
	 */
	@SecurityMapping(title = "云购彩票列表", value = "/cloudpurchaselottery_list*", rtype = "admin", rname = "云购期号", rcode = "admin_cloudpurchase_lottery", rgroup = "运营")
	@RequestMapping({ "/cloudpurchaselottery_list" })
	public ModelAndView list(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,
			String orderType, String goods_name, String status) {
		ModelAndView mv = new RedPigJModelAndView(
				"admin/blue/cloudpurchaselottery_list.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		String url = this.configService.getSysConfig().getAddress();
		if ((url == null) || (url.equals(""))) {
			url = CommUtil.getURL(request);
		}
		String params = "";
		Map<String,Object> maps= this.redPigQueryTools.getParams(currentPage, orderBy, orderType);
        
        
		if ((goods_name != null) && (!goods_name.equals(""))) {
			
			maps.put("cloudPurchaseGoods_goods_name_like",goods_name);
			mv.addObject("goods_name", goods_name);
		}
		if ((status != null) && (!status.equals(""))) {
			
			maps.put("status",CommUtil.null2Int(status));
			mv.addObject("status", status);
		}
		IPageList pList = this.cloudpurchaselotteryService.list(maps);
		CommUtil.saveIPageList2ModelAndView(url
				+ "/cloudpurchaselottery_list.html", "", params, pList,
				mv);
		return mv;
	}
	
	/**
	 * 云购彩票中奖发货信息
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param id
	 * @return
	 */
	@SecurityMapping(title = "云购彩票中奖发货信息", value = "/cloudpurchaselottery_delivery*", rtype = "admin", rname = "云购期号", rcode = "admin_cloudpurchase_lottery", rgroup = "运营")
	@RequestMapping({ "/cloudpurchaselottery_delivery" })
	public ModelAndView delivery(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String id) {
		ModelAndView mv = new RedPigJModelAndView(
				"admin/blue/cloudpurchaselottery_shipping.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);

		CloudPurchaseLottery lottery = this.cloudpurchaselotteryService
				.selectByPrimaryKey(CommUtil.null2Long(id));

		Map<String, Object> map = Maps.newHashMap();
		map.put("user_id", CommUtil.null2Long(lottery.getLucky_userid()));
		map.put("status", 1);
		
		List<CloudPurchaseRecord> record_list = this.cloudPurchaseRecordService.queryPageList(map);
		
		if (record_list.size() > 0) {
			mv.addObject("obj", lottery);
		} else {
			mv.addObject("obj", new CloudPurchaseLottery());
		}
		mv.addObject("currentPage", currentPage);
		return mv;
	}
	
	/**
	 * 云购彩票中奖发货信息保存
	 * @param request
	 * @param response
	 * @param id
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "云购彩票中奖发货信息保存", value = "/cloudpurchaselotterydelivery_update*", rtype = "admin", rname = "云购期号", rcode = "admin_cloudpurchase_lottery", rgroup = "运营")
	@RequestMapping({ "/cloudpurchaselotterydelivery_update" })
	public String delivery_updateById(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage) {
		
		CloudPurchaseLottery lottery = this.cloudpurchaselotteryService.selectByPrimaryKey(CommUtil.null2Long(id));
		lottery.setDelivery_status(1);
		this.cloudpurchaselotteryService.updateById(lottery);
		return "redirect:/cloudpurchaselottery_list?currentPage=" + currentPage;
	}
}
