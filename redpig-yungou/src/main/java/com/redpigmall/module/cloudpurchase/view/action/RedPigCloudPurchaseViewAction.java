package com.redpigmall.module.cloudpurchase.view.action;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.redpigmall.api.mv.RedPigJModelAndView;
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.api.sec.SecurityUserHolder;
import com.redpigmall.api.tools.CommUtil;
import com.redpigmall.api.tools.RedPigMaps;
import com.redpigmall.module.cloudpurchase.manage.admin.base.BaseAction;
import com.redpigmall.domain.CloudPurchaseClass;
import com.redpigmall.domain.CloudPurchaseGoods;
import com.redpigmall.domain.CloudPurchaseLottery;
import com.redpigmall.domain.CloudPurchaseRecord;
import com.redpigmall.domain.User;

/**
 * 
 * <p>
 * Title: RedPigCloudPurchaseViewAction.java
 * </p>
 * 
 * <p>
 * Description: 云购商品管理
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
public class RedPigCloudPurchaseViewAction extends BaseAction{
	
	@RequestMapping({ "/cloudbuy/head" })
	public ModelAndView head(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new RedPigJModelAndView("clouds/cloudpurchase_head.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		User user = SecurityUserHolder.getCurrentUser();
		if (user != null) {
			Map<String,Object> maps = Maps.newHashMap();
	        maps.put("user_id",user.getId());
	        
			int cart_count = this.cloudPurchaseCartService.selectCount(maps);
			
			mv.addObject("cart_count", Integer.valueOf(cart_count));
		} else {
			mv.addObject("cart_count", Integer.valueOf(0));
		}
		return mv;
	}

	@RequestMapping({ "/cloudbuy/nav1" })
	public ModelAndView nav1(HttpServletRequest request,
			HttpServletResponse response, String nav) {
		ModelAndView mv = new RedPigJModelAndView("clouds/cloudpurchase_nav.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		mv.addObject("nav", nav);
		Map<String,Object> maps = Maps.newHashMap();
		mv.addObject("classes", this.cloudPurchaseClassService.queryPageList(maps));
		
		return mv;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping({ "/cloudbuy/index" })
	public ModelAndView index(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new RedPigJModelAndView("clouds/cloudpurchase_index.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		Map<String,Object> maps = Maps.newHashMap();
		List<CloudPurchaseClass> classes = this.cloudPurchaseClassService.queryPageList(maps);
		
		List lottery_list = Lists.newArrayList();
		for (CloudPurchaseClass cloudPurchaseClass : classes) {
			lottery_list.add(this.cloudPurchaseLotteryService.query(
					cloudPurchaseClass.getId().toString(), "popularity",
					"desc", 0, 5));
		}
		mv.addObject("classes", classes);
		mv.addObject("lottery_list", lottery_list);
		
		List<CloudPurchaseLottery> recommend_list = this.cloudPurchaseLotteryService.query("", "addTime", "", 0, 1);
		
		mv.addObject(
				"recommend",
				recommend_list != null && recommend_list.size() > 0 ? (CloudPurchaseLottery) recommend_list.get(0) : null);
		
		maps.clear();
        maps.put("status",15);
        maps.put("orderBy", "announced_date");
        maps.put("orderType", "desc");
        
		List<CloudPurchaseLottery> announced_list = this.cloudPurchaseLotteryService.queryPageList(maps,0, 4);
		
		mv.addObject("announced_list", announced_list);

		return mv;
	}

	@RequestMapping({ "/cloudbuy/list" })
	public ModelAndView list(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderby,
			String ordertype, String class_id, String keyword) {
		ModelAndView mv = new RedPigJModelAndView("clouds/cloudpurchase_list.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		String url = this.configService.getSysConfig().getAddress();
		if ((url == null) || (url.equals(""))) {
			url = CommUtil.getURL(request);
		}
		if (CommUtil.null2String(ordertype).equals("")) {
			ordertype = "desc";
		}
		if (CommUtil.null2String(orderby).equals("")) {
			orderby = "popularity";
		}
		if (class_id != null) {
			CloudPurchaseClass cloudPurchaseClass = this.cloudPurchaseClassService
					.selectByPrimaryKey(CommUtil.null2Long(class_id));
			if (cloudPurchaseClass != null) {
				mv.addObject("cloudPurchaseClass", cloudPurchaseClass);
			}
		}
		
		List<CloudPurchaseClass> goodsclass = this.cloudPurchaseClassService.queryPageList(RedPigMaps.newMap());
		
		
		mv.addObject("goodsclass", goodsclass);
		
		Map<String,Object> maps= this.redPigQueryTools.getParams(currentPage,"addTime", "desc");
        maps.put("cloudPurchaseGoods_goodsNum_more_than",0);
        maps.put("status",5);
        
		if ((class_id != null) && (!class_id.equals(""))) {
			
			maps.put("cloudPurchaseGoods_class_id",CommUtil.null2Long(class_id));
			
			mv.addObject("class_id", class_id);
		}
		if ((keyword != null) && (!keyword.equals(""))) {
			
			maps.put("cloudPurchaseGoods_goods_name_like",keyword);
			mv.addObject("keyword", keyword);
		}
//		if (orderby != null) {
//			if (orderby.equals("goods_price")) {
//				qo.setOrderBy("cloudPurchaseGoods.goods_price");
//				maps.put("orderBy", "gc_sequence");
//		        maps.put("orderType", "asc");
//		        
//				if ("asc".equals(ordertype)) {
//					qo.setOrderType("asc");
//				}
//			} else if (orderby.equals("addTime")) {
//				qo.setOrderBy("addTime");
//			} else if (orderby.equals("purchased_times")) {
//				qo.setOrderBy("purchased_left_times");
//				qo.setOrderType("asc");
//			} else {
//				orderby = "popularity";
//				qo.setOrderBy("cloudPurchaseGoods.sell_num");
//			}
//		}
		IPageList pList = this.cloudPurchaseLotteryService.list(maps);
		CommUtil.saveIPageList2ModelAndView(url + "/cloudbuy/list.html", "",
				"", pList, mv);
		mv.addObject("count", Integer.valueOf(pList.getRowCount()));

		mv.addObject("orderby", orderby);
		mv.addObject("ordertype", ordertype);
		return mv;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping({ "/cloudbuy/items" })
	public ModelAndView goods(HttpServletRequest request,
			HttpServletResponse response, String id) {
		ModelAndView mv = new RedPigJModelAndView("clouds/cloudpurchase_goods.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		CloudPurchaseLottery lottery = this.cloudPurchaseLotteryService
				.selectByPrimaryKey(CommUtil.null2Long(id));
		mv.addObject("lottery", lottery);
		if (lottery != null) {
			CloudPurchaseGoods goods = this.cloudPurchaseGoodsService
					.selectByPrimaryKey(CommUtil.null2Long(lottery.getGoods_id()));
			mv.addObject("goods", goods);
			CloudPurchaseClass cloudPurchaseClass = this.cloudPurchaseClassService
					.selectByPrimaryKey(CommUtil.null2Long(goods.getClass_id()));
			if (cloudPurchaseClass != null) {
				mv.addObject("cloudPurchaseClass", cloudPurchaseClass);
			}
			List url_list = JSON.parseArray(goods.getSecondary_photo(),
					String.class);
			mv.addObject("secondary_photo", url_list);
			if (lottery.getAnnounced_date() != null) {
				mv.addObject("announced_date",
						Long.valueOf(lottery.getAnnounced_date().getTime()));
			}
		}
		User current_user = SecurityUserHolder.getCurrentUser();
		if (current_user != null) {
			mv.addObject("order", "asd");
		}
		User user = SecurityUserHolder.getCurrentUser();
		if (user != null) {
			
			Map<String, Object> params = Maps.newHashMap();
			params.put("cloudPurchaseLottery_id", lottery.getId());
			params.put("user_id", user.getId());
			
			List<CloudPurchaseRecord> record_list = this.cloudPurchaseRecordService.queryPageList(params);
			
			List list2 = Lists.newArrayList();
			for (CloudPurchaseRecord cloudPurchaseRecord : record_list) {
				List list3 = (List) JSON.parse(cloudPurchaseRecord.getPurchased_codes());
				list2.addAll(list3);
			}
			
			List<CloudPurchaseRecord> list = list2;
			
			mv.addObject("code_list", list);
			mv.addObject("count", Integer.valueOf(list.size()));
		}
		return mv;
	}

	@RequestMapping({ "/cloudbuy/record" })
	public ModelAndView record(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage) {
		ModelAndView mv = new RedPigJModelAndView("clouds/cloudpurchase_record.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		
		Map<String,Object> maps= this.redPigQueryTools.getParams(currentPage,"addTime", "desc");
        maps.put("cloudPurchaseLottery_id",CommUtil.null2Long(id));
        
		IPageList pList = this.cloudPurchaseRecord.list(maps);
		String url = CommUtil.getURL(request) + "/cloudbuy/record";
		mv.addObject("objs", pList.getResult());
		mv.addObject("record_gotoPageAjaxHTML", 
				CommUtil.showPageAjaxHtml(url,"", pList.getCurrentPage(), pList.getPages(),pList.getPageSize()));
		
		CommUtil.saveIPageList2ModelAndView(url + "/cloudbuy/record.html", "","", pList, mv);
		mv.addObject("goodsViewTools", this.goodsViewTools);
		return mv;
	}

	@RequestMapping({ "/cloudbuy/history" })
	public ModelAndView history(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage) {
		ModelAndView mv = new RedPigJModelAndView(
				"clouds/cloudpurchase_history.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		CloudPurchaseLottery lottery = this.cloudPurchaseLotteryService
				.selectByPrimaryKey(CommUtil.null2Long(id));
		
		Map<String,Object> maps= this.redPigQueryTools.getParams(currentPage,"addTime", "desc");
        maps.put("goods_id",lottery.getGoods_id());
        maps.put("status",15);
        
		IPageList pList = this.cloudPurchaseLotteryService.list(maps);
		String url = CommUtil.getURL(request) + "/cloudbuy/history";
		mv.addObject("objs", pList.getResult());
		mv.addObject("history_gotoPageAjaxHTML", CommUtil.showPageAjaxHtml(url,
				"", pList.getCurrentPage(), pList.getPages(),
				pList.getPageSize()));
		CommUtil.saveIPageList2ModelAndView(url + "/cloudbuy/history.html", "",
				"", pList, mv);
		return mv;
	}

	@RequestMapping({ "/cloudbuy/ten" })
	public ModelAndView ten(HttpServletRequest request,
			HttpServletResponse response, String currentPage) {
		ModelAndView mv = new RedPigJModelAndView("clouds/cloudpurchase_ten.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		String url = this.configService.getSysConfig().getAddress();
		if ((url == null) || (url.equals(""))) {
			url = CommUtil.getURL(request);
		}
		
		Map<String,Object> maps= this.redPigQueryTools.getParams(currentPage,"addTime", "desc");
        maps.put("status",5);
        maps.put("cloudPurchaseGoods_goodsNum_more_than",0);
        maps.put("cloudPurchaseGoods_least_rmb",10);
        
		IPageList pList = this.cloudPurchaseLotteryService.list(maps);
		CommUtil.saveIPageList2ModelAndView(url + "/cloudbuy/ten.html", "", "",
				pList, mv);

		return mv;
	}

	@RequestMapping({ "/cloudbuy/getresult" })
	public void getresult(HttpServletRequest request,
			HttpServletResponse response, String lottery_id) {
		this.cloudPurchaseLotteryService.runALottery();
		
		CloudPurchaseLottery lottery = this.cloudPurchaseLotteryService
				.selectByPrimaryKey(CommUtil.null2Long(lottery_id));
		Map<String, Object> map = Maps.newHashMap();
		map.put("status", Integer.valueOf(lottery.getStatus()));
//		AppResponse appResponse = new AppResponse(AppResponse.SUCCESS,AppResponse.DATA_MAP, "", map);
//		AppResponseTools.sendJson(response, appResponse.toString());
	}
	
	@RequestMapping({ "/cloudbuy/new" })
	public String new_goods(HttpServletRequest request,
			HttpServletResponse response, String id) {
		
		CloudPurchaseLottery lottery = this.cloudPurchaseLotteryService.selectByPrimaryKey(CommUtil.null2Long(id));
		if (lottery.getStatus() != 5) {
			
			Map<String, Object> params = Maps.newHashMap();
			params.put("cloudPurchaseGoods_id", lottery.getGoods_id());
			params.put("status", 5);
			
			List<CloudPurchaseLottery> lottery_list = this.cloudPurchaseLotteryService.queryPageList(params);
			
			if (lottery_list.size() > 0) {
				lottery = (CloudPurchaseLottery) lottery_list.get(0);
			}
			
		}
		return "redirect:/cloudbuy/items?id=" + lottery.getId();
	}
}
