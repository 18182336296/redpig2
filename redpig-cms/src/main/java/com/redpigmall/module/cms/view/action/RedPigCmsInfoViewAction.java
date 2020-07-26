package com.redpigmall.module.cms.view.action;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import com.redpigmall.api.annotation.SecurityMapping;
import com.redpigmall.api.mv.RedPigJModelAndView;
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.api.sec.SecurityUserHolder;
import com.redpigmall.api.tools.CommUtil;
import com.redpigmall.module.cms.manage.admin.action.base.BaseAction;
import com.redpigmall.domain.Goods;
import com.redpigmall.domain.Information;
import com.redpigmall.domain.User;

/**
 * 
 * <p>
 * Title: RedPigCmsInfoViewAction.java
 * </p>
 * 
 * <p>
 * Description: 咨询详情页相关操作均在此控制器中处理
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
 * @date 2015-2-3
 * 
 * @version redpigmall_b2b2c_2015
 */
@Controller
public class RedPigCmsInfoViewAction extends BaseAction {
	
	/**
	 * cms详情
	 * @param request
	 * @param response
	 * @param id
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	@RequestMapping({ "/cms/detail" })
	public ModelAndView detail(HttpServletRequest request,
			HttpServletResponse response, String id) {
		ModelAndView mv = new RedPigJModelAndView("/cms/detail.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		Information obj = this.informationService.selectByPrimaryKey(CommUtil
				.null2Long(id));
		boolean flag = false;
		if (obj != null) {
			User user = SecurityUserHolder.getCurrentUser();
			if (obj.getStatus() < 20) {
				if ((user != null) && (user.getUserRole().equals("ADMIN"))) {
					flag = true;
				}
			} else {
				flag = true;
			}
			if (flag) {
				obj.setClicktimes(obj.getClicktimes() + 1);
				mv.addObject("obj", obj);
				if ((obj.getInfoIconData() == null)
						|| (obj.getInfoIconData().equals(""))) {
					obj.setInfoIconData(JSON
							.toJSONString(getNewInfoIconDataMap()));
				}
				this.informationService.updateById(obj);
				Map map = getViewInfoIconDataMap(JSON.parseObject(obj
						.getInfoIconData()));
				mv.addObject("IconDataMap", map);
				
				Map<String,Object> maps= this.redPigQueryTools.getParams(null,12, "addTime", "desc");
				maps.put("info_id", obj.getId());
				
				IPageList pList = this.replyService.list(maps);
				CommUtil.saveIPageList2ModelAndView("", "", null, pList, mv);
				mv.addObject("replies", pList.getResult());
				mv.addObject("count", Integer.valueOf(pList.getRowCount()));
				mv.addObject(
						"className",
						this.classService.selectByPrimaryKey(
								Long.valueOf(obj.getClassid())).getIc_name());
				mv.addObject("imageTools", this.imageTools);

				Map<String,Object> map2 = Maps.newHashMap();
				map2.put("classid", Long.valueOf(obj.getClassid()));
				map2.put("orderBy", "clicktimes");
				map2.put("orderType", "desc");
				
				List<Information> hot_infors = this.informationService.queryPageList(map2,0,6);
				
				mv.addObject("hot_infors", hot_infors);
				if (this.configService.getSysConfig().getZtc_status()) {
					
					List<Goods> ztc_goods = null;
					Map<String,Object> ztc_map = Maps.newHashMap();
					ztc_map.put("ztc_status", Integer.valueOf(3));
					ztc_map.put("ztc_begin_time_less_than_equal", new Date());
					ztc_map.put("ztc_gold_more_than", Integer.valueOf(0));
					ztc_map.put("orderBy", "ztc_dredge_price");
					ztc_map.put("orderType", "desc");
					
					if (this.configService.getSysConfig().getZtc_goods_view() == 0) {
						ztc_goods = this.goodsService.queryPageList(ztc_map,0,6);
					}
					
					mv.addObject("ztc_goods", ztc_goods);
				} else {
					Map<String, Object> params = Maps.newHashMap();
					params.put("store_recommend", Boolean.valueOf(true));
					params.put("goods_status", Integer.valueOf(0));
					params.put("orderBy", "goods_salenum");
					params.put("orderType", "desc");
					
					List<Goods> ztc_goods = this.goodsService.queryPageList(params,0,6);
					
					mv.addObject("ztc_goods", ztc_goods);
				}
			}
		}
		if (!flag) {
			mv = new RedPigJModelAndView("error.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "参数错误，资讯查看失败");
			mv.addObject("url", CommUtil.getURL(request) + "/cms/index");
		}
		return mv;
	}
	
	/***
	 * 选择表情
	 * @param request
	 * @param response
	 * @param type
	 * @param user_id
	 * @param info_id
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@SecurityMapping(title = "选择表情", value = "/cms/info_icon*", rtype = "buyer", rname = "资讯", rcode = "user_info", rgroup = "资讯")
	@RequestMapping({ "/cms/info_icon" })
	public void info_icon(HttpServletRequest request,
			HttpServletResponse response, String type, String user_id,
			String info_id) {
		Information information = this.informationService.selectByPrimaryKey(CommUtil
				.null2Long(info_id));
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder
				.getCurrentUser().getId());
		Map map = null;
		Boolean ret = Boolean.valueOf(true);
		if ((information.getInfoUserData() != null)
				&& (!information.getInfoUserData().equals(""))) {
			map = JSON.parseObject(information.getInfoUserData());
			if (map.containsKey(user.getId().toString())) {
				ret = Boolean.valueOf(false);
			} else {
				map.put(user.getId(), type);
				information.setInfoUserData(JSON.toJSONString(map));
			}
		} else {
			map = Maps.newHashMap();
			map.put(user.getId(), type);
			information.setInfoUserData(JSON.toJSONString(map));
		}
		if (ret.booleanValue()) {
			if ((information.getInfoIconData() != null)
					&& (!information.getInfoIconData().equals(""))) {
				map = JSON.parseObject(information.getInfoIconData());
			} else {
				map = getNewInfoIconDataMap();
			}
			map.put(type,
					Integer.valueOf(((Integer) map.get(type)).intValue() + 1));
			information.setInfoIconData(JSON.toJSONString(map));
			this.informationService.saveEntity(information);
		}
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		try {
			PrintWriter writer = response.getWriter();
			writer.print(ret);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public Map<String, Integer> getNewInfoIconDataMap() {
		Map<String, Integer> map = Maps.newHashMap();
		map.put("hehe", Integer.valueOf(0));
		map.put("kaixin", Integer.valueOf(0));
		map.put("deyi", Integer.valueOf(0));
		map.put("nanguo", Integer.valueOf(0));
		map.put("fennu", Integer.valueOf(0));
		map.put("gandong", Integer.valueOf(0));
		map.put("henzan", Integer.valueOf(0));
		return map;
	}

	@SuppressWarnings("rawtypes")
	public Map<String, Integer> getViewInfoIconDataMap(Map tempMap) {
		Map<String, Integer> map = Maps.newHashMap();
		float max = getInfoIconDataMax(tempMap);
		int countTemp = CommUtil.null2Int(tempMap.get("hehe"));
		map.put("hehe_count", Integer.valueOf(countTemp));
		map.put("hehe_price", Integer.valueOf((int) (countTemp / max * 100.0F)));
		countTemp = CommUtil.null2Int(tempMap.get("kaixin"));
		map.put("kaixin_count", Integer.valueOf(countTemp));
		map.put("kaixin_price",
				Integer.valueOf((int) (countTemp / max * 100.0F)));
		countTemp = CommUtil.null2Int(tempMap.get("deyi"));
		map.put("deyi_count", Integer.valueOf(countTemp));
		map.put("deyi_price", Integer.valueOf((int) (countTemp / max * 100.0F)));
		countTemp = CommUtil.null2Int(tempMap.get("nanguo"));
		map.put("nanguo_count", Integer.valueOf(countTemp));
		map.put("nanguo_price",
				Integer.valueOf((int) (countTemp / max * 100.0F)));
		countTemp = CommUtil.null2Int(tempMap.get("fennu"));
		map.put("fennu_count", Integer.valueOf(countTemp));
		map.put("fennu_price",
				Integer.valueOf((int) (countTemp / max * 100.0F)));
		countTemp = CommUtil.null2Int(tempMap.get("gandong"));
		map.put("gandong_count", Integer.valueOf(countTemp));
		map.put("gandong_price",
				Integer.valueOf((int) (countTemp / max * 100.0F)));
		countTemp = CommUtil.null2Int(tempMap.get("henzan"));
		map.put("henzan_count", Integer.valueOf(countTemp));
		map.put("henzan_price",
				Integer.valueOf((int) (countTemp / max * 100.0F)));
		return map;
	}

	@SuppressWarnings("rawtypes")
	public int getInfoIconDataMax(Map tempMap) {
		int max = 0;
		int countTemp = CommUtil.null2Int(tempMap.get("hehe"));
		if (countTemp > max) {
			max = countTemp;
		}
		countTemp = CommUtil.null2Int(tempMap.get("kaixin"));
		if (countTemp > max) {
			max = countTemp;
		}
		countTemp = CommUtil.null2Int(tempMap.get("deyi"));
		if (countTemp > max) {
			max = countTemp;
		}
		countTemp = CommUtil.null2Int(tempMap.get("nanguo"));
		if (countTemp > max) {
			max = countTemp;
		}
		countTemp = CommUtil.null2Int(tempMap.get("fennu"));
		if (countTemp > max) {
			max = countTemp;
		}
		countTemp = CommUtil.null2Int(tempMap.get("gandong"));
		if (countTemp > max) {
			max = countTemp;
		}
		countTemp = CommUtil.null2Int(tempMap.get("henzan"));
		if (countTemp > max) {
			max = countTemp;
		}
		return max;
	}
}
