package com.redpigmall.manage.admin.action.operative.buyGift;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.redpigmall.api.annotation.SecurityMapping;
import com.redpigmall.api.mv.RedPigJModelAndView;
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.api.tools.CommUtil;
import com.redpigmall.api.tools.ObjectUtils;
import com.redpigmall.api.tools.WebForm;
import com.redpigmall.manage.admin.action.base.BaseAction;
import com.redpigmall.domain.BuyGift;
import com.redpigmall.domain.Goods;

/**
 * 
 * <p>
 * Title: RedPigBuyGiftManageAction.java
 * </p>
 * 
 * <p>
 * Description:满就送促销管理
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
 * @date 2014-9-23
 * 
 * @version redpigmall_b2b2c 2016
 */
@Controller
public class RedPigBuyGiftManageAction extends BaseAction{

	/**
	 * 满就送列表
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @param gift_status
	 * @param beginTime
	 * @param endTime
	 * @param store_name
	 * @return
	 */
	@SecurityMapping(title = "满就送列表", value = "/buygift_list*", rtype = "admin", rname = "满就送管理", rcode = "buygift_manage", rgroup = "运营")
	@RequestMapping({ "/buygift_list" })
	public ModelAndView buygift_list(
			HttpServletRequest request,
			HttpServletResponse response, 
			String currentPage, 
			String orderBy,
			String orderType, 
			String gift_status, 
			String beginTime,
			String endTime, 
			String store_name) {
		
		ModelAndView mv = new RedPigJModelAndView("admin/blue/buygift_list.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request, response);
		
		String url = this.redPigSysConfigService.getSysConfig().getAddress();
		if ((url == null) || (url.equals(""))) {
			url = CommUtil.getURL(request);
		}
		String params = "";
		Map<String,Object> maps = this.redPigQueryTools.getParams(currentPage, orderBy, orderType);
		
		if ((gift_status != null) && (!gift_status.equals(""))) {
			maps.put("gift_status", CommUtil.null2Int(gift_status));
			mv.addObject("gift_status", gift_status);
		}
		
		if ((beginTime != null) && (!beginTime.equals(""))) {
			maps.put("beginTime_more_than_equal", CommUtil.formatDate(beginTime));
			
			mv.addObject("beginTime", beginTime);
		}
		
		if ((endTime != null) && (!endTime.equals(""))) {
			maps.put("endTime_less_than_equal", CommUtil.formatDate(endTime));
			
			mv.addObject("endTime", endTime);
		}
		
		if ((store_name != null) && (!store_name.equals(""))) {
			maps.put("store_name_like", store_name);
			
			mv.addObject("store_name", store_name);
		}
		
		maps.put("gift_type", 1);
		
		IPageList pList = this.redPigBuygiftService.list(maps);
		CommUtil.saveIPageList2ModelAndView(url + "/buygift_list.html",
				"", params, pList, mv);
		return mv;
	}
	
	/**
	 * 满就送添加
	 * @param request
	 * @param response
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "满就送添加", value = "/buygift_add*", rtype = "admin", rname = "满就送管理", rcode = "buygift_manage", rgroup = "运营")
	@RequestMapping({ "/buygift_add" })
	public ModelAndView buygift_add(
			HttpServletRequest request,
			HttpServletResponse response, 
			String currentPage) {
		
		ModelAndView mv = new RedPigJModelAndView("admin/blue/buygift_add.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request, response);
		mv.addObject("currentPage", currentPage);
		return mv;
	}
	
	/**
	 * 满就送编辑
	 * @param request
	 * @param response
	 * @param id
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "满就送编辑", value = "/buygift_info*", rtype = "admin", rname = "满就送管理", rcode = "buygift_manage", rgroup = "运营")
	@RequestMapping({ "/buygift_info" })
	public ModelAndView buygift_edit(
			HttpServletRequest request,
			HttpServletResponse response, 
			String id, 
			String currentPage) {
		
		ModelAndView mv = new RedPigJModelAndView("admin/blue/buygift_info.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request, response);
		
		if ((id != null) && (!id.equals(""))) {
			BuyGift buygift = this.redPigBuygiftService.selectByPrimaryKey(Long.valueOf(Long.parseLong(id)));
			
			mv.addObject("obj", buygift);
			mv.addObject("currentPage", currentPage);
			mv.addObject("edit", Boolean.valueOf(true));
		}
		return mv;
	}
	
	/**
	 * 满就送保存
	 * @param request
	 * @param response
	 * @param id
	 * @param currentPage
	 * @param gift_status
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	@SecurityMapping(title = "满就送保存", value = "/buygift_save*", rtype = "admin", rname = "满就送管理", rcode = "buygift_manage", rgroup = "运营")
	@RequestMapping({ "/buygift_save" })
	public ModelAndView buygift_saveEntity(
			HttpServletRequest request,
			HttpServletResponse response, 
			String id, 
			String currentPage,
			String gift_status) {
		
		
		BuyGift buygift = null;
		if (id.equals("")) {
			buygift = (BuyGift) WebForm.toPo(request, BuyGift.class);
			buygift.setAddTime(new Date());
		} else {
			BuyGift obj = this.redPigBuygiftService.selectByPrimaryKey(Long.valueOf(Long.parseLong(id)));
			
			buygift = (BuyGift) WebForm.toPo(request, obj);
			buygift.setGift_status(CommUtil.null2Int(gift_status));
			if (CommUtil.null2Int(gift_status) == -10) {
				List<Map> list = Lists.newArrayList();
				if ((obj.getGoods_info() != null)
						&& (!obj.getGoods_info().equals(""))) {
					list = JSON.parseArray(obj.getGoods_info(), Map.class);
				}
				if ((obj.getGift_info() != null)
						&& (!obj.getGift_info().equals(""))) {
					list.add((Map) JSON.parseObject(obj.getGift_info(),
							Map.class));
				}
				for (Map map : list) {
					Goods goods = this.redPigGoodsService.selectByPrimaryKey(CommUtil
							.null2Long(map.get("goods_id")));
					if (goods != null) {
						goods.setOrder_enough_give_status(0);
						goods.setOrder_enough_if_give(0);
						this.redPigGoodsService.updateById(goods);
					}
				}
			}
			if (CommUtil.null2Int(gift_status) == 10) {
				List<Map> list = Lists.newArrayList();
				if ((obj.getGift_info() != null)
						&& (!obj.getGift_info().equals(""))) {
					list = JSON.parseArray(obj.getGift_info(), Map.class);
				}
				for (Map map : list) {
					if (CommUtil.null2Int(map.get("storegoods_count")) == 0) {
						Goods goods = this.redPigGoodsService.selectByPrimaryKey(CommUtil
								.null2Long(map.get("goods_id")));
						if (goods != null) {
							int count = CommUtil.null2Int(map
									.get("goods_count"));
							goods.setGoods_inventory(goods.getGoods_inventory()
									- count);
							goods.setOrder_enough_if_give(1);
							goods.setBuyGift_id(obj.getId());
							this.redPigGoodsService.updateById(goods);
						}
					}
				}
				list.clear();
				if ((obj.getGift_info() != null)
						&& (!obj.getGift_info().equals(""))) {
					list = JSON.parseArray(obj.getGoods_info(), Map.class);
				}
				for (Map map : list) {
					Goods goods = this.redPigGoodsService.selectByPrimaryKey(CommUtil
							.null2Long(map.get("goods_id")));
					if (goods != null) {
						goods.setOrder_enough_give_status(1);
						goods.setBuyGift_id(obj.getId());
						this.redPigGoodsService.updateById(goods);
					}
				}
			}
		}
		if (id.equals("")) {
			this.redPigBuygiftService.saveEntity(buygift);
		} else {
			this.redPigBuygiftService.updateById(buygift);
		}
		ModelAndView mv = new RedPigJModelAndView("admin/blue/success.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request, response);
		mv.addObject("list_url", CommUtil.getURL(request)
				+ "/buygift_list");
		mv.addObject("op_title", "审核满就送成功");
		return mv;
	}
	
	/**
	 * 满就送删除
	 * @param request
	 * @param response
	 * @param mulitId
	 * @param currentPage
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	@SecurityMapping(title = "满就送删除", value = "/buygift_del*", rtype = "admin", rname = "满就送管理", rcode = "buygift_manage", rgroup = "运营")
	@RequestMapping({ "/buygift_del" })
	public String buygift_del(
			HttpServletRequest request,
			HttpServletResponse response, 
			String mulitId, 
			String currentPage) {
		
		String[] ids = mulitId.split(",");
		for (String id : ids) {
			if (!id.equals("")) {
				BuyGift buygift = this.redPigBuygiftService.selectByPrimaryKey(Long.valueOf(Long.parseLong(id)));
				if (buygift != null) {
					List<Map> maps = JSON.parseArray(buygift.getGift_info(),Map.class);
					maps.addAll(JSON.parseArray(buygift.getGoods_info(),Map.class));
					for (Map map : maps) {
						Goods goods = this.redPigGoodsService.selectByPrimaryKey(CommUtil.null2Long(map.get("goods_id")));
						if (goods != null) {
							goods.setOrder_enough_give_status(0);
							goods.setOrder_enough_if_give(0);
							goods.setBuyGift_id(null);
							this.redPigGoodsService.updateById(goods);
						}
					}
					this.redPigBuygiftService.deleteById(Long.valueOf(Long.parseLong(id)));
				}
			}
		}
		return "redirect:buygift_list?currentPage=" + currentPage;
	}
	
	/**
	 * 满就送ajax
	 * @param request
	 * @param response
	 * @param id
	 * @param fieldName
	 * @param value
	 * @throws ClassNotFoundException
	 */
	@SecurityMapping(title = "满就送ajax", value = "/buygift_ajax*", rtype = "admin", rname = "满就送管理", rcode = "buygift_manage", rgroup = "运营")
	@RequestMapping({ "/buygift_ajax" })
	public void buygift_ajax(
			HttpServletRequest request,
			HttpServletResponse response, 
			String id, 
			String fieldName,
			String value) throws ClassNotFoundException {
		
		BuyGift obj = this.redPigBuygiftService.selectByPrimaryKey(Long.valueOf(Long.parseLong(id)));
		
		Field[] fields = BuyGift.class.getDeclaredFields();
		Object val = ObjectUtils.setValue(fieldName, value, obj, fields);
		
		this.redPigBuygiftService.updateById(obj);
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		try {
			PrintWriter writer = response.getWriter();
			writer.print(val.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
