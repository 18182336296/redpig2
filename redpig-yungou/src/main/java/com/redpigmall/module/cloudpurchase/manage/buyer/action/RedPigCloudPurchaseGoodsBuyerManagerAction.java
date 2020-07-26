package com.redpigmall.module.cloudpurchase.manage.buyer.action;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.redpigmall.api.annotation.SecurityMapping;
import com.redpigmall.api.mv.RedPigJModelAndView;
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.api.sec.SecurityUserHolder;
import com.redpigmall.api.tools.CommUtil;
import com.redpigmall.api.tools.RedPigMaps;
import com.redpigmall.module.cloudpurchase.manage.admin.base.BaseAction;
import com.redpigmall.domain.Address;
import com.redpigmall.domain.Area;
import com.redpigmall.domain.CloudPurchaseLottery;
import com.redpigmall.domain.CloudPurchaseRecord;
import com.redpigmall.domain.User;

/**
 * <p>
 * Title: RedPigCloudPurchaseGoodsBuyerManagerAction.java
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
public class RedPigCloudPurchaseGoodsBuyerManagerAction extends BaseAction{
	
	/**
	 * 云购订单列表
	 * @param request
	 * @param response
	 * @param status
	 * @param currentPage
	 * @param beginTime
	 * @param endTime
	 * @param lottery_id
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@SecurityMapping(title = "云购订单列表", value = "/buyer/cloudbuy_order*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping({ "/buyer/cloudbuy_order" })
	public ModelAndView user(HttpServletRequest request,
			HttpServletResponse response, String status, String currentPage,
			String beginTime, String endTime, String lottery_id) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/usercenter/cloudbuy_orderlist.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder
				.getCurrentUser().getId());
		
		String url = this.configService.getSysConfig().getAddress();
		if ((url == null) || (url.equals(""))) {
			url = CommUtil.getURL(request);
		}
		String params = "";
		Map<String,Object> maps= this.redPigQueryTools.getParams(currentPage,null,null);
        
		if ((user.getId() != null) && (!"".equals(user))) {
			Map<String, Object> map = Maps.newHashMap();
			map.put("id", CommUtil.null2Long(user.getId()));
			if (CommUtil.null2Int(status) == 0) {
				
				maps.put("user_id",user.getId());
			} else {
				map.put("status", Integer.valueOf(CommUtil.null2Int(status)));
				
				maps.put("user_id",user.getId());
				
				maps.put("cloudPurchaseLottery_status",user.getId());
			}
			IPageList pList = this.cloudPurchaseRecordService.list(maps);
			List<CloudPurchaseRecord> record_list_1 = pList.getResult();
			List<CloudPurchaseRecord> record_list = Lists.newArrayList();
			if (record_list_1.size() > 0) {
				CloudPurchaseRecord up_record = (CloudPurchaseRecord) record_list_1.get(0);
				int purchased_times = up_record.getPurchased_times();
				for (int i = 1; i < record_list_1.size(); i++) {
					CloudPurchaseRecord cloudPurchaseRecord = (CloudPurchaseRecord) record_list_1.get(i);
					if (cloudPurchaseRecord
							.getCloudPurchaseLottery()
							.getPeriod()
							.equals(up_record.getCloudPurchaseLottery()
									.getPeriod())) {
						purchased_times = purchased_times + cloudPurchaseRecord.getPurchased_times();
					} else {
						up_record.setTotal_purchased_times(purchased_times);
						record_list.add(up_record);
						purchased_times = cloudPurchaseRecord
								.getPurchased_times();
						up_record = cloudPurchaseRecord;
					}
				}
				up_record.setTotal_purchased_times(purchased_times);
				record_list.add(up_record);
			}
			mv.addObject("record_list", record_list);
			CommUtil.saveIPageList2ModelAndView(url
					+ "/buyer/cloudbuy_order.html", "", params, pList, mv);
		}
		mv.addObject("status", status);
		return mv;
	}
	
	/**
	 * 云购订单收货地址
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param id
	 * @return
	 */
	@SecurityMapping(title = "云购订单收货地址", value = "/buyer/cloudpurchase_address_add*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping({ "/buyer/cloudpurchase_address_add" })
	public ModelAndView cloudpurchase_address_add(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String id) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/usercenter/cloudpurchase_address_add.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		
		List<Area> areas = this.areaService.queryPageList(RedPigMaps.newParent(-1));

		Map<String, Object> params = Maps.newHashMap();
		params.put("user_id", SecurityUserHolder.getCurrentUser().getId());
		params.put("default_val", Integer.valueOf(1));
		
		List<Address> addrs = this.addressService.queryPageList(params);
		
		if (addrs.size() > 0) {
			Address obj = (Address) addrs.get(0);
			if (obj.getUser().getId()
					.equals(SecurityUserHolder.getCurrentUser().getId())) {
				mv.addObject("obj", obj);
			}
		}
		mv.addObject("areas", areas);
		mv.addObject("currentPage", currentPage);
		mv.addObject("lottery_id", id);
		mv.addObject("currentPage", currentPage);
		return mv;
	}
	
	/**
	 * 云购订单收货地址保存
	 * @param request
	 * @param response
	 * @param lottery_id
	 * @param currentPage
	 * @param trueName
	 * @param area_info
	 * @param mobile
	 * @param area_id
	 * @return
	 */
	@SecurityMapping(title = "云购订单收货地址保存", value = "/buyer/cloudpurchase_address_save*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping({ "/buyer/cloudpurchase_address_save" })
	public String cloudpurchase_address_saveEntity(HttpServletRequest request,
			HttpServletResponse response, String lottery_id,
			String currentPage, String trueName, String area_info,
			String mobile, String area_id) {
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder
				.getCurrentUser().getId());
		Map<String, Object> map = Maps.newHashMap();
		map.put("user_id", CommUtil.null2Long(user.getId()));
		map.put("status", 1);
		
		List<CloudPurchaseRecord> record_list = this.cloudPurchaseRecordService.queryPageList(map);
		
		if (record_list.size() > 0) {
			CloudPurchaseLottery lottery = this.cloudPurchaseLotteryService
					.selectByPrimaryKey(CommUtil.null2Long(lottery_id));
			if (CommUtil
					.null2Int(Integer.valueOf(lottery.getDelivery_status())) == -1) {
				lottery.setLucky_truename(trueName);
				Area area = this.areaService.selectByPrimaryKey(CommUtil
						.null2Long(area_id));
				lottery.setLucky_address(area.getParent().getParent()
						.getAreaName()
						+ area.getParent().getAreaName()
						+ area.getAreaName()
						+ area_info);

				lottery.setLucky_phone(mobile);

				lottery.setDelivery_status(0);
				this.cloudPurchaseLotteryService.saveEntity(lottery);
			}
		}
		return "redirect:/buyer/cloudbuy_order?currentPage=" + currentPage;
	}
	
	/**
	 * 云购订单确认收货
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param id
	 * @return
	 */
	@SecurityMapping(title = "云购订单确认收货", value = "/buyer/cloudpurchase_delivery_status*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping({ "/buyer/cloudpurchase_delivery_status" })
	public String cloudpurchase_delivery_status(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String id) {
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder
				.getCurrentUser().getId());
		Map<String, Object> map = Maps.newHashMap();
		map.put("user_id", CommUtil.null2Long(user.getId()));
		map.put("status", 1);
		
		List<CloudPurchaseRecord> record_list = this.cloudPurchaseRecordService.queryPageList(map);
		
		if (record_list.size() > 0) {
			CloudPurchaseLottery lottery = this.cloudPurchaseLotteryService.selectByPrimaryKey(CommUtil.null2Long(id));
			if (CommUtil.null2Int(Integer.valueOf(lottery.getDelivery_status())) == 1) {
				lottery.setDelivery_status(2);
				this.cloudPurchaseLotteryService.updateById(lottery);
			}
		}
		return "redirect:/buyer/cloudbuy_order?currentPage=" + currentPage;
	}
}
