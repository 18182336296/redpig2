package com.redpigmall.module.cloudpurchase.weixin.buyer.action;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.google.common.collect.Maps;
import com.redpigmall.api.annotation.SecurityMapping;
import com.redpigmall.api.mv.RedPigJModelAndView;
import com.redpigmall.api.sec.SecurityUserHolder;
import com.redpigmall.api.tools.CommUtil;
import com.redpigmall.api.tools.RedPigMaps;
import com.redpigmall.module.cloudpurchase.manage.admin.base.BaseAction;
import com.redpigmall.domain.Address;
import com.redpigmall.domain.CloudPurchaseLottery;
import com.redpigmall.domain.CloudPurchaseRecord;

/**
 * 
 * <p>
 * Title: RedPigWeixinUserCloudPurchaseAction.java
 * </p>
 * 
 * <p>
 * Description: 
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2012-2014
 * </p>
 * 
 * <p>
 * Company: www.redpigmall.net
 * </p>
 * 
 * @author redpig
 * 
 * @date 2014-4-28
 * 
 * @version redpigmall_b2b2c v8.0 2016版
 */
@Controller
public class RedPigWeixinUserCloudPurchaseAction extends BaseAction{
	
	/**
	 * 移动端用户中心云购
	 * @param request
	 * @param response
	 * @param begin_count
	 * @param status
	 * @param type
	 * @param cloudPurchaseLottery_id
	 * @return
	 */
	@SecurityMapping(title = "移动端用户中心云购", value = "/buyer/cloud_order_list", rtype = "buyer", rname = "移动端用户中心云购", rcode = "wap_cloud_buyer", rgroup = "移动端用户中心云购")
	@RequestMapping({ "/buyer/cloud_order_list" })
	public ModelAndView cloud_order_list(HttpServletRequest request,
			HttpServletResponse response, String begin_count, String status,
			String type, String cloudPurchaseLottery_id) {
		String view = "user/default/usercenter/weixin/cloud_order_list";
		boolean ret = "ajax".equals(type);
		if (ret) {
			view = "user/default/usercenter/weixin/cloud_order_list_ajax";
		}
		ModelAndView mv = new RedPigJModelAndView(view,
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		int begin = 0;
		if (StringUtils.isNotBlank(begin_count)) {
			begin = CommUtil.null2Int(begin_count);
		}
		int record_status = 5;
		if ("will".equals(status)) {
			record_status = 10;
		}
		if ("end".equals(status)) {
			record_status = 15;
		}
		Long user_id = SecurityUserHolder.getCurrentUser().getId();
		Map<String,Object> maps = RedPigMaps.newMap();
        maps.put("user_id", user_id);
        maps.put("record_status", record_status);
        
		List<CloudPurchaseRecord> records = this.cloudPurchaseRecordService.queryPageList(maps, begin, 12);
		mv.addObject("records", records);
		mv.addObject("status", status);
		Map<String, Object> params = Maps.newHashMap();
		params.put("user_id", user_id);
		params.put("default_val", Integer.valueOf(1));
		
		List<Address> addrs = this.addressService.queryPageList(params);
		
		mv.addObject("addrs", addrs);
		if ((StringUtils.isNotBlank(cloudPurchaseLottery_id))
				&& (addrs != null) && (addrs.size() > 0)) {
			CloudPurchaseLottery lottery = this.cloudPurchaseLotteryService.selectByPrimaryKey(CommUtil.null2Long(cloudPurchaseLottery_id));
			if (CommUtil.null2Long(lottery.getLucky_userid()).equals(user_id)) {
				Address add = (Address) addrs.get(0);
				lottery.setDelivery_status(0);
				lottery.setLucky_phone(add.getMobile());
				lottery.setLucky_address(add.getArea().getParent().getParent()
						.getAreaName()
						+ add.getArea().getParent().getAreaName()
						+ add.getArea().getAreaName() + add.getArea_info());
				this.cloudPurchaseLotteryService.updateById(lottery);
			}
		}
		return mv;
	}
	
	/**
	 * 移动端用户中心云购
	 * @param request
	 * @param response
	 * @param id
	 * @param status
	 * @return
	 */
	@SecurityMapping(title = "移动端用户中心云购", value = "/buyer/cloud_order_confirm", rtype = "buyer", rname = "移动端用户中心云购", rcode = "wap_cloud_buyer", rgroup = "移动端用户中心云购")
	@RequestMapping({ "/buyer/cloud_order_confirm" })
	public String cloud_order_confirm(HttpServletRequest request,
			HttpServletResponse response, String id, String status) {
		CloudPurchaseLottery obj = this.cloudPurchaseLotteryService
				.selectByPrimaryKey(CommUtil.null2Long(id));
		if ((CommUtil.null2Long(obj.getLucky_userid())
				.equals(SecurityUserHolder.getCurrentUser().getId()))
				&& (obj.getDelivery_status() == 1)) {
			obj.setDelivery_status(2);
			this.cloudPurchaseLotteryService.updateById(obj);
		}
		return "redirect:cloud_order_list?status" + status;
	}
}
