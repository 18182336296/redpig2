package com.redpigmall.module.cloudpurchase.weixin.view.action;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import com.redpigmall.api.mv.RedPigJModelAndView;
import com.redpigmall.api.sec.SecurityUserHolder;
import com.redpigmall.api.tools.CommUtil;
import com.redpigmall.module.cloudpurchase.manage.admin.base.BaseAction;
import com.redpigmall.domain.CloudPurchaseClass;
import com.redpigmall.domain.CloudPurchaseLottery;
import com.redpigmall.domain.CloudPurchaseRecord;

/**
 * 
 * <p>
 * Title: RedPigWeixinCloudPurchaseViewAction.java
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
public class RedPigWeixinCloudPurchaseViewAction extends BaseAction{
	@RequestMapping({ "/cloudpurchase_goods_list" })
	public ModelAndView cloudpurchase_goods_list(HttpServletRequest request,
			HttpServletResponse response, String orderby, String ordertype,
			String class_id, String type, String begin_count) {
		ModelAndView mv = new RedPigJModelAndView(
				"weixin/cloudpurchase_goods_list.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		if ("ajax".equals(type)) {
			mv = new RedPigJModelAndView("weixin/cloudpurchase_goods_list_ajax.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
		}
		String url = this.configService.getSysConfig().getAddress();
		int begin = 0;
		if (StringUtils.isNotBlank(begin_count)) {
			begin = CommUtil.null2Int(begin_count);
		}
		if ((url == null) || (url.equals(""))) {
			url = CommUtil.getURL(request);
		}
		String order_by = orderby;
		String order_type = ordertype;
		if ((CommUtil.null2String(ordertype).equals(""))
				|| ("up".equals(ordertype))) {
			ordertype = "desc";
		}
		if ("down".equals(ordertype)) {
			ordertype = "asc";
		}
		if ((CommUtil.null2String(orderby).equals(""))
				|| ("renqi".equals(orderby))) {
			orderby = "popularity";
		}
		if ("zuixin".equals(orderby)) {
			orderby = "addTime";
		}
		if ("shengyu".equals(orderby)) {
			orderby = "purchased_times";
		}
		if ("zongxu".equals(orderby)) {
			orderby = "goods_price";
		}
		if (class_id != null) {
			CloudPurchaseClass cloudPurchaseClass = this.cloudPurchaseClassService.selectByPrimaryKey(CommUtil.null2Long(class_id));
			if (cloudPurchaseClass != null) {
				mv.addObject("cloudPurchaseClass", cloudPurchaseClass);
			}
		}
		Map<String,Object> params = Maps.newHashMap();
		params.put("orderBy", "sequence");
		params.put("orderType", "asc");
		
		List<CloudPurchaseClass> goodsclass = this.cloudPurchaseClassService.queryPageList(params);
		
		mv.addObject("goodsclass", goodsclass);
		List<CloudPurchaseLottery> cpls = this.cloudPurchaseLotteryService.query(class_id, orderby, ordertype, begin, 12);
		
		mv.addObject("cpls", cpls);
		mv.addObject("orderby", order_by);
		mv.addObject("ordertype", order_type);
		if (SecurityUserHolder.getCurrentUser() != null) {
			Map<String,Object> param = Maps.newHashMap();
			param.put("user_id", SecurityUserHolder.getCurrentUser().getId());
			
			mv.addObject("count", Integer.valueOf(this.cloudPurchaseCartService.selectCount(param)));
			
		}
		return mv;
	}

	@RequestMapping({ "/cloudpurchase_index" })
	public ModelAndView cloudpurchase_index(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new RedPigJModelAndView("weixin/cloudpurchase_index.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		if (SecurityUserHolder.getCurrentUser() != null) {
			Map<String,Object> param = Maps.newHashMap();
			param.put("user_id", SecurityUserHolder.getCurrentUser().getId());
			
			mv.addObject("count", Integer.valueOf(this.cloudPurchaseCartService.selectCount(param)));
		}
		Map<String,Object> params = Maps.newHashMap();
		params.put("status", 15);
		params.put("orderBy", "announced_date");
		params.put("orderType", "desc");
		
		List<CloudPurchaseLottery> newsLottery = this.cloudPurchaseLotteryService.queryPageList(params,0, 3);
		
		mv.addObject("newsLottery", newsLottery);
		List<CloudPurchaseLottery> objs = this.cloudPurchaseLotteryService.query("", "addTime", "desc", 0, 3);
		
		mv.addObject("objs", objs);
		List<CloudPurchaseLottery> cpls = this.cloudPurchaseLotteryService.query("", "popularity", "desc", 0, 12);
		mv.addObject("cpls", cpls);
		return mv;
	}

	@RequestMapping({ "/cloudpurchase_index_ajax" })
	public ModelAndView cloudpurchase_index_ajax(HttpServletRequest request,
			HttpServletResponse response, String begin_count) {
		ModelAndView mv = new RedPigJModelAndView(
				"weixin/cloudpurchase_index_ajax.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		List<CloudPurchaseLottery> cpls = this.cloudPurchaseLotteryService.query("","popularity", "desc",CommUtil.null2Int(begin_count), 12);
		mv.addObject("cpls", cpls);
		return mv;
	}

	@RequestMapping({ "/cloudpurchase_items" })
	public ModelAndView cloudpurchase_goods(HttpServletRequest request,
			HttpServletResponse response, String id, String type) {
		CloudPurchaseLottery obj = this.cloudPurchaseLotteryService
				.selectByPrimaryKey(CommUtil.null2Long(id));
		String view = "weixin/cloudpurchase_goods";
		boolean ret = "details".equals(type);
		if (ret) {
			view = "weixin/cloudpurchase_goods_details";
		}
		ModelAndView mv = new RedPigJModelAndView(view,
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		if (!ret) {
			List<String> pics = JSON.parseArray(obj.getCloudPurchaseGoods()
					.getSecondary_photo(), String.class);
			mv.addObject("pics", pics);
		}
		mv.addObject("obj", obj);
		
		Map<String, Object> params = Maps.newHashMap();
		params.put("lottery_id", CommUtil.null2Long(id));
		
		List<CloudPurchaseRecord> records = this.cloudPurchaseRecordService.queryPageList(params,0, 12);
		mv.addObject("records", records);
		if ((obj != null) && (obj.getStatus() == 15)) {
			CloudPurchaseLottery lottery = (CloudPurchaseLottery) this.cloudPurchaseLotteryService.selectByPrimaryKey(CommUtil.null2Long(id));
			params.clear();
			params.put("goods_id", lottery.getGoods_id());
			params.put("status", 5);
			
			List<CloudPurchaseLottery> lottery_list = this.cloudPurchaseLotteryService.queryPageList(params);
			CloudPurchaseLottery current = null;
			if(lottery_list != null && lottery_list.size()>0){
				current = lottery_list.get(0);
			}
			
			mv.addObject("current", current);
		}
		return mv;
	}

	@RequestMapping({ "/cloudpurchase_ago" })
	public ModelAndView cloudpurchase_ago(HttpServletRequest request,
			HttpServletResponse response, String id, String type,
			String begin_count) {
		String view = "weixin/cloudpurchase_ago";
		if ("ajax".equals(type)) {
			view = "weixin/cloudpurchase_ago_ajax";
		}
		int begin = 0;
		if (StringUtils.isNotBlank(begin_count)) {
			begin = CommUtil.null2Int(begin_count);
		}
		ModelAndView mv = new RedPigJModelAndView(view,
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		
		CloudPurchaseLottery lottery = (CloudPurchaseLottery) this.cloudPurchaseLotteryService.selectByPrimaryKey(CommUtil.null2Long(id));
		
		Map<String, Object> params = Maps.newHashMap();
		params.put("cloudPurchaseGoods_id", lottery.getGoods_id());
		params.put("status", 15);
		params.put("cloudPurchaseGoods_id", lottery.getGoods_id());
		params.put("orderBy", "announced_date");
		params.put("orderType", "desc");
		
		List<CloudPurchaseLottery> objs = this.cloudPurchaseLotteryService.queryPageList(params,begin, 12);
		
		mv.addObject("objs", objs);
		return mv;
	}
	
	@RequestMapping({ "/cloudpurchase_record_ajax" })
	public ModelAndView cloudpurchase_record_ajax(HttpServletRequest request,
			HttpServletResponse response, String id, String begin_count) {
		String view = "weixin/cloudpurchase_record_ajax";
		int begin = 0;
		if (StringUtils.isNotBlank(begin_count)) {
			begin = CommUtil.null2Int(begin_count);
		}
		ModelAndView mv = new RedPigJModelAndView(view,
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		
		Map<String, Object> params = Maps.newHashMap();
		params.put("cloudPurchaseLottery_id", CommUtil.null2Long(id));
		
		List<CloudPurchaseRecord> records = this.cloudPurchaseRecordService.queryPageList(params, begin, 12);
		
		mv.addObject("records", records);
		return mv;
	}
}
