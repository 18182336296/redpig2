package com.redpigmall.module.weixin.view.action;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.redpigmall.api.mv.RedPigJModelAndView;
import com.redpigmall.api.tools.CommUtil;
import com.redpigmall.domain.Area;
import com.redpigmall.domain.Goods;
import com.redpigmall.domain.GoodsBrand;
import com.redpigmall.module.weixin.view.action.base.BaseAction;

/**
 * 
 * 
 * <p>
 * Title:WapBrandViewAction.java
 * </p>
 * 
 * <p>
 * Description: 移动端品牌页
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2014
 * </p>
 * 
 * <p>
 * Company: www.redpigmall.net
 * </p>
 * 
 * @author redpig
 * 
 * @date 2014年8月20日
 * 
 * @version redpigmall_b2b2c_2015
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
@Controller
public class RedPigWeixinBrandViewAction extends BaseAction{
	
	/**
	 * 移动端品牌页请求
	 * @param request
	 * @param response
	 * @param gc_id
	 * @return
	 */
	@RequestMapping({ "/brand" })
	public ModelAndView brand(HttpServletRequest request,
			HttpServletResponse response, String gc_id) {
		ModelAndView mv = new RedPigJModelAndView("weixin/brand.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		Map<String, Object> params = Maps.newHashMap();
		params.put("audit", Integer.valueOf(1));
		params.put("orderBy", "sequence");
		params.put("orderType", "asc");
		
		List<GoodsBrand> brands = this.goodsBrandService.queryPageList(params);
		
		List all_list = Lists.newArrayList();
		Set set = Sets.newHashSet();
		String list_word = "A,B,C,D,E,F,G,H,I,J,K,L,M,N,O,P,Q,R,S,T,U,V,W,X,Y,Z";
		String[] words = list_word.split(",");
		for (String word : words) {
			Map brand_map = Maps.newHashMap();
			List brand_list = Lists.newArrayList();
			for (GoodsBrand gb : brands) {
				if ((!CommUtil.null2String(gb.getFirst_word()).equals(""))
						&& (word.equals(gb.getFirst_word().toUpperCase()))) {
					Map<String, Object> map = Maps.newHashMap();
					map.put("name", gb.getName());
					map.put("id", gb.getId());
					map.put("photo", CommUtil.getURL(request) + "/"
							+ gb.getBrandLogo().getPath() + "/"
							+ gb.getBrandLogo().getName());
					brand_list.add(map);
					set.add(gb.getFirst_word());
				}
			}
			if (brand_list.size() > 0) {
				brand_map.put("brand_list", brand_list);
				brand_map.put("word", word);
				all_list.add(brand_map);
			}
		}
		String currentCityId = CommUtil.null2String(request.getSession(false)
				.getAttribute("currentCityId"));
		Area area = this.areaService.selectByPrimaryKey(CommUtil
				.null2Long(currentCityId));
		if (area != null) {
			mv.addObject("area", area);
		}
		Arrays.sort(set.toArray());
		mv.addObject("words", set);
		mv.addObject("all_list", all_list);
		return mv;
	}
	
	
	/**
	 * 根据商城品牌列表商品
	 * @param request
	 * @param response
	 * @param gb_id
	 * @param orderBy
	 * @param orderType
	 * @return
	 */
	@RequestMapping({ "/brand_goods" })
	public ModelAndView brand_goods(HttpServletRequest request,
			HttpServletResponse response, String gb_id, String orderBy,
			String orderType) {
		ModelAndView mv = new RedPigJModelAndView("weixin/brand_goods.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		if ((orderBy == null) || (orderBy.equals(""))) {
			orderBy = "goods_salenum";
		}
		if ((orderType == null) || (orderType.equals(""))) {
			orderType = "desc";
		}
		List<Goods> goods_list = null;
		Map<String, Object> params = Maps.newHashMap();
		params.put("goods_status", Integer.valueOf(0));
		params.put("goods_brand_id", CommUtil.null2Long(gb_id));
		
		if (orderBy.equals("goods_collect")) {
			params.put("orderBy", "goods_collect");
			params.put("orderType", "desc");
			
			goods_list = this.goodsService.queryPageList(params, 0, 12);
			
		}
		if (orderBy.equals("goods_salenum")) {
			params.put("orderBy", "goods_salenum");
			params.put("orderType", "desc");
			
			goods_list = this.goodsService.queryPageList(params, 0, 12);
			
		}
		
		if (orderBy.equals("store_price")) {
			params.put("orderBy", "store_price");
			params.put("orderType", orderType);
			
			goods_list = this.goodsService.queryPageList(params, 0, 12);
			
		}
		mv.addObject("objs", goods_list);
		mv.addObject("gb_id", gb_id);
		mv.addObject("orderBy", orderBy);
		mv.addObject("orderType", orderType);
		mv.addObject("goodsViewTools", this.goodsViewTools);
		return mv;
	}
	
	/**
	 * 
	 * @param request
	 * @param response
	 * @param gb_id
	 * @param orderBy
	 * @param orderType
	 * @return
	 */
	@RequestMapping({ "/brand_items" })
	public ModelAndView brand_items(HttpServletRequest request,
			HttpServletResponse response, String gb_id, String orderBy,
			String orderType) {
		ModelAndView mv = new RedPigJModelAndView("weixin/brand_goods.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		if ((orderBy == null) || (orderBy.equals(""))) {
			orderBy = "goods_salenum";
		}
		if ((orderType == null) || (orderType.equals(""))) {
			orderType = "desc";
		}
		List<Goods> goods_list = null;
		Map<String, Object> params = Maps.newHashMap();
		params.put("goods_status", Integer.valueOf(0));
		params.put("goods_brand_id", CommUtil.null2Long(gb_id));
		
		if (orderBy.equals("goods_collect")) {
			params.put("orderBy", "goods_collect");
			params.put("orderType", "desc");
			
			goods_list = this.goodsService.queryPageList(params, 0, 12);
			
		}
		if (orderBy.equals("goods_salenum")) {
			params.put("orderBy", "goods_salenum");
			params.put("orderType", "desc");
			
			goods_list = this.goodsService.queryPageList(params, 0, 12);
			
		}
		if (orderBy.equals("store_price")) {
			params.put("orderBy", "store_price");
			params.put("orderType", orderType);
			
			goods_list = this.goodsService.queryPageList(params, 0, 12);
			
		}
		mv.addObject("objs", goods_list);
		mv.addObject("gb_id", gb_id);
		mv.addObject("orderBy", orderBy);
		mv.addObject("orderType", orderType);
		mv.addObject("goodsViewTools", this.goodsViewTools);
		return mv;
	}
	
	/**
	 * ajax
	 * @param request
	 * @param response
	 * @param begin_count
	 * @param orderBy
	 * @param orderType
	 * @param gb_id
	 * @return
	 */
	@RequestMapping({ "/brand_goods_ajax" })
	public ModelAndView brand_goods_ajax(HttpServletRequest request,
			HttpServletResponse response, String begin_count, String orderBy,
			String orderType, String gb_id) {
		ModelAndView mv = new RedPigJModelAndView("weixin/brand_goods_data.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		Map<String, Object> params = Maps.newHashMap();
		params.put("goods_status", Integer.valueOf(0));
		params.put("goods_brand_id", CommUtil.null2Long(gb_id));
		
		List<Goods> goods_list = null;
		mv.addObject("orderBy", orderBy);
		mv.addObject("orderType", orderType);
		if (orderBy.equals("goods_collect")) {
			params.put("orderBy", "goods_collect");
			params.put("orderType", "desc");
			
			goods_list = this.goodsService.queryPageList(params, CommUtil.null2Int(begin_count), 6);
			
		}
		if (orderBy.equals("goods_salenum")) {
			params.put("orderBy", "goods_salenum");
			params.put("orderType", "desc");
			
			goods_list = this.goodsService.queryPageList(params, CommUtil.null2Int(begin_count), 6);
			
		}
		if (orderBy.equals("store_price")) {
			params.put("orderBy", "store_price");
			params.put("orderType", orderType);
			
			goods_list = this.goodsService.queryPageList(params, CommUtil.null2Int(begin_count), 6);
			
		}
		mv.addObject("objs", goods_list);
		return mv;
	}
}
