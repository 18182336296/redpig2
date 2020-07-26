package com.redpigmall.manage.buyer.action;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.util.List;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.redpigmall.api.annotation.SecurityMapping;
import com.redpigmall.api.mv.RedPigJModelAndView;
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.api.sec.SecurityUserHolder;
import com.redpigmall.api.tools.CommUtil;
import com.redpigmall.domain.FootPoint;
import com.redpigmall.domain.Goods;
import com.redpigmall.domain.User;
import com.redpigmall.manage.buyer.action.base.BaseAction;


/**
 * 
 * <p>
 * Title: RedPigFootPointBuyerAction.java
 * </p>
 * 
 * <p>
 * Description: 用户中心，足迹管理控制器，显示、删除所有浏览过的足迹信息
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
 * @date 2017-04-16
 * 
 * @version redpigmall_b2b2c v8.0 2016版
 */
@Controller
public class RedPigFootPointBuyerAction extends BaseAction{
	
	/**
	 * 用户足迹记录
	 * @param request
	 * @param response
	 * @param currentPage
	 * @return
	 * @throws Exception
	 */
	@SecurityMapping(title = "用户足迹记录", value = "/buyer/foot_point*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping({ "/buyer/foot_point" })
	public ModelAndView foot_point(HttpServletRequest request,
			HttpServletResponse response, String currentPage) throws Exception {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/usercenter/foot_point.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		User user = SecurityUserHolder.getCurrentUser();
		
		Map<String,Object> maps= this.redPigQueryTools.getParams(currentPage,"addTime", "desc");
		maps.put("fp_user_id", user.getId());
		
		IPageList pList = this.footPointService.queryPagesWithNoRelations(maps);
		
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		
		mv.addObject("footPointTools", this.footPointTools);
		// 猜您喜欢 根据cookie商品的分类 销量查询 如果没有cookie则按销量查询
		List<Goods> your_like_goods = Lists.newArrayList();
		Long your_like_GoodsClass = null;
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			
			for (Cookie cookie : cookies) {
				
				if (cookie.getName().equals("goodscookie")) {
					String[] like_gcid = URLDecoder.decode(cookie.getValue(), "UTF-8").split(",", 2);
					Goods goods = this.goodsService.selectByPrimaryKey(CommUtil.null2Long(like_gcid[0]));
					if (goods == null) {
						break;
					}
					
					your_like_GoodsClass = goods.getGc().getId();
					Map<String,Object> params = Maps.newHashMap();
					params.put("goods_status", 0);
					params.put("gc_id", your_like_GoodsClass);
					params.put("id_no", goods.getId());
					params.put("orderBy", "goods_salenum");
					params.put("orderType", "desc");
					
					your_like_goods = this.goodsService.queryPageList(params,0,20);
					
					int gcs_size = your_like_goods.size();
					if (gcs_size >= 20) {
						break;
					}
					
					params.clear();
					params.put("goods_status", 0);
					params.put("id_no", goods.getId());
					params.put("orderBy", "goods_salenum");
					params.put("orderType", "desc");
					
					List<Goods> like_goods = this.goodsService.queryPageList(params,0, 20 - gcs_size);
					
					for (int i = 0; i < like_goods.size(); i++) {
						// 去除重复商品
						int k = 0;
						
						for (int j = 0; j < your_like_goods.size(); j++) {
							if (like_goods.get(i).getId().equals(your_like_goods.get(j).getId())) {
								k++;
							}
						}
						if (k == 0) {
							your_like_goods.add(like_goods.get(i));
						}
					}
					break;
				}else{
					Map<String,Object> params = Maps.newHashMap();
					params.put("goods_status", 0);
					params.put("orderBy", "goods_salenum");
					params.put("orderType", "desc");
					your_like_goods = this.goodsService.queryPageList(params, 0, 20);
				}
			}
		} else {
			Map<String,Object> params = Maps.newHashMap();
			params.put("goods_status", 0);
			params.put("orderBy", "goods_salenum");
			params.put("orderType", "desc");
			
			your_like_goods = this.goodsService.queryPageList(params, 0, 20);
			
		}
		
		mv.addObject("your_like_goods", your_like_goods);
		return mv;
	}
	
	/**
	 * 用户足迹记录删除
	 * @param request
	 * @param response
	 * @param date
	 * @param goods_id
	 */
	@SuppressWarnings({ "rawtypes" })
	@SecurityMapping(title = "用户足迹记录删除", value = "/buyer/foot_point_remove*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping({ "/buyer/foot_point_remove" })
	public void foot_point_remove(HttpServletRequest request,
			HttpServletResponse response, String date, String goods_id) {
		boolean ret = true;
		if ((!CommUtil.null2String(date).equals("")) && (CommUtil.null2String(goods_id).equals(""))) {
			Map<String, Object> params = Maps.newHashMap();
			params.put("fp_date", CommUtil.formatDate(date));
			params.put("fp_user_id", SecurityUserHolder.getCurrentUser().getId());
			
			List<FootPoint> fps = this.footPointService.queryPageList(params);
			
			for (FootPoint fp : fps) {
				this.footPointService.deleteById(fp.getId());
			}
		}
		
		if ((!CommUtil.null2String(date).equals("")) && (!CommUtil.null2String(goods_id).equals(""))) {// 删除某一个足迹
			
			Map<String, Object> params = Maps.newHashMap();
			params.put("fp_date", CommUtil.formatDate(date));
			params.put("fp_user_id", SecurityUserHolder.getCurrentUser().getId());
			
			List<FootPoint> fps = this.footPointService.queryPageList(params);
			
			for (FootPoint fp : fps) {
				List<Map> list = JSON.parseArray(fp.getFp_goods_content(),Map.class);
				for (Map map : list) {
					if (CommUtil.null2String(map.get("goods_id")).equals(
							goods_id)) {
						list.remove(map);
						break;
					}
				}
				fp.setFp_goods_content(JSON.toJSONString(list));
				fp.setFp_goods_count(list.size());
				this.footPointService.updateById(fp);
			}
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
}
