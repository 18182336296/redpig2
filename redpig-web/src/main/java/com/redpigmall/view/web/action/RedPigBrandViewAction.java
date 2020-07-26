package com.redpigmall.view.web.action;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.redpigmall.api.mv.RedPigJModelAndView;
import com.redpigmall.api.tools.ClusterSyncTools;
import com.redpigmall.api.tools.CommUtil;
import com.redpigmall.domain.Goods;
import com.redpigmall.domain.GoodsBrand;
import com.redpigmall.domain.GoodsBrandCategory;
import com.redpigmall.domain.Store;
import com.redpigmall.lucene.LuceneResult;
import com.redpigmall.lucene.RedPigLuceneUtil;
import com.redpigmall.view.web.action.base.BaseAction;

/**
 * 
 * <p>
 * Title: RedPigBrandViewAction.java
 * </p>
 * 
 * <p>
 * Description: 品牌相关控制器
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
 * @date 2016-4-28
 * 
 * @version redpigmall_b2b2c V1.0
 */
@Controller
public class RedPigBrandViewAction extends BaseAction {
	 
	 /**
	  * 品牌首页
	  * @param request
	  * @param response
	  * @return
	  */
	  @RequestMapping({"/brand/index"})
	  public ModelAndView brand(HttpServletRequest request, HttpServletResponse response){
		//跳转页面
	    ModelAndView mv = new RedPigJModelAndView("brand.html", 
	      this.configService.getSysConfig(), 
	      this.userConfigService.getUserConfig(), 1, request, response);
	    
		Map<String, Object> params = Maps.newHashMap();
		params.put("recommend", true);
		params.put("audit", 1);
		params.put("orderBy", "sequence");
		params.put("orderType", "asc");
		
		//查询商品品牌第一页
		List<GoodsBrand> gbs1 = this.goodsBrandService.queryPageList(params, 0, 21);
		
		mv.addObject("gbs1", gbs1);
		
		params.clear();
		params.put("recommend", true);
		params.put("audit", 1);
		params.put("orderBy", "sequence");
		params.put("orderType", "asc");
		//查询商品品牌第二页
		List<GoodsBrand> gbs2 = this.goodsBrandService.queryPageList(params, 21, 21);
		
	    if (gbs2.size() >= 21) {
	      mv.addObject("gbs2", gbs2);
	    }
	    
		params.clear();
		params.put("recommend", true);
		params.put("audit", 1);
		params.put("orderBy", "sequence");
		params.put("orderType", "asc");
		//查询商品品牌第三页
		List<GoodsBrand> gbs3 = this.goodsBrandService.queryPageList(params, 42, 21);
		
	    if (gbs3.size() >= 21) {
	      mv.addObject("gbs3", gbs3);
	    }
	    
		List<GoodsBrand> brands = Lists.newArrayList();
		params.clear();
		params.put("audit", 1);
		params.put("orderBy", "sequence");
		params.put("orderType", "asc");
		
		brands = this.goodsBrandService.queryPageList(params);
	    
		List<Map<String, Object>> all_list = Lists.newArrayList();
		String list_word = "A,B,C,D,E,F,G,H,I,J,K,L,M,N,O,P,Q,R,S,T,U,V,W,X,Y,Z";
		String[] words = list_word.split(",");
		for (String word : words) {
			Map<String, Object> brand_map = Maps.newHashMap();
			List<GoodsBrand> brand_list = Lists.newArrayList();
			for (GoodsBrand gb : brands) {
				if ((!CommUtil.null2String(gb.getFirst_word()).equals(""))
						&& (word.equals(gb.getFirst_word().toUpperCase()))) {
					brand_list.add(gb);
				}
			}
			brand_map.put("brand_list", brand_list);
			brand_map.put("word", word);
			all_list.add(brand_map);
		}
	    
	    mv.addObject("all_list", all_list);
	    
	    List<Map<String, Object>> list = Lists.newArrayList();
	    List<GoodsBrandCategory> gbcs = this.goodsBrandCategorySerivce.queryPageList(params, 0, 8);
		
		if (gbcs.size() < 7) {
	      return mv;
	    }
	    
		for (GoodsBrandCategory gbc : gbcs) {
			if ((gbc.getName() != null) 
					&& (!gbc.getName().equals(""))
					&& (list.size() < 7)) {
				Map<String, Object> gbc_map = Maps.newHashMap();
				List<Map<String, Object>> gbc_list = Lists.newArrayList();
				
				params.clear();
				params.put("audit", 1);
				params.put("category_id", gbc.getId());
				params.put("orderBy", "sequence");
				params.put("orderType", "asc");
				
				List<GoodsBrand> gbs = this.goodsBrandService.queryPageList(params, 0, 9);
				
				for (GoodsBrand goodsBrand : gbs) {
					Map<String, Object> map = Maps.newHashMap();
					map.put("id", goodsBrand.getId());
					if (goodsBrand.getBrandLogo() != null) {
						map.put("logo", 
								goodsBrand.getBrandLogo().getPath() 
								+ "/" + 
								goodsBrand.getBrandLogo().getName());
					}
					gbc_list.add(map);
				}
				gbc_map.put("name", gbc.getName());
				gbc_map.put("brands", gbc_list);
				list.add(gbc_map);
			}
		}
		
	    mv.addObject("list", list);
	    return mv;
	  }
	  
	  
	/**
	 * 品牌首页
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping({ "/brand/index_bak" })
	public ModelAndView brand_bak(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new RedPigJModelAndView("brand.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		
		Map<String, Object> params = Maps.newHashMap();
		params.put("recommend", true);
		params.put("audit", 1);
		params.put("orderBy", "sequence");
		params.put("orderType", "asc");
		
		List<GoodsBrand> gbs1 = this.goodsBrandService.queryPageList(params, 0, 21);
		
		mv.addObject("gbs1", gbs1);
		params.clear();
		params.put("recommend", true);
		params.put("audit", 1);
		params.put("orderBy", "sequence");
		params.put("orderType", "asc");
		
		List<GoodsBrand> gbs2 = this.goodsBrandService.queryPageList(params, 21, 21);
		
		if (gbs2.size() >= 21) {
			mv.addObject("gbs2", gbs2);
		}
		
		params.clear();
		params.put("recommend", true);
		params.put("audit", 1);
		params.put("orderBy", "sequence");
		params.put("orderType", "asc");
		List<GoodsBrand> gbs3 = this.goodsBrandService.queryPageList(params, 42, 21);
		
		if (gbs3.size() >= 21) {
			mv.addObject("gbs3", gbs3);
		}
		
		List<GoodsBrand> brands = Lists.newArrayList();
		params.clear();
		params.put("audit", 1);
		params.put("orderBy", "sequence");
		params.put("orderType", "asc");
		
		brands = this.goodsBrandService.queryPageList(params);
		
		List<Map<String, Object>> all_list = Lists.newArrayList();
		String list_word = "A,B,C,D,E,F,G,H,I,J,K,L,M,N,O,P,Q,R,S,T,U,V,W,X,Y,Z";
		String[] words = list_word.split(",");
		for (String word : words) {
			Map<String, Object> brand_map = Maps.newHashMap();
			List<GoodsBrand> brand_list = Lists.newArrayList();
			for (GoodsBrand gb : brands) {
				if ((!CommUtil.null2String(gb.getFirst_word()).equals(""))
						&& (word.equals(gb.getFirst_word().toUpperCase()))) {
					brand_list.add(gb);
				}
			}
			brand_map.put("brand_list", brand_list);
			brand_map.put("word", word);
			all_list.add(brand_map);
		}
		mv.addObject("all_list", all_list);
		
		List<Map<String, Object>> list = Lists.newArrayList();
		
		params.clear();
		params.put("audit", 1);
		params.put("orderBy", "addTime");
		params.put("orderType", "desc");
		
		List<GoodsBrandCategory> gbcs = this.goodsBrandCategorySerivce.queryPageList(params, 0, 8);
		
		if (gbcs.size() < 7) {
			return mv;
		}
		
		for (GoodsBrandCategory gbc : gbcs) {
			if ((gbc.getName() != null) && (!gbc.getName().equals(""))
					&& (list.size() < 7)) {
				Map<String, Object> gbc_map = Maps.newHashMap();
				List<Map<String, Object>> gbc_list = Lists.newArrayList();

				params.clear();
				params.put("audit", 1);
				params.put("category_id", gbc.getId());
				params.put("orderBy", "sequence");
				params.put("orderType", "asc");
				
				List<GoodsBrand> gbs = this.goodsBrandService.queryPageList(params, 0, 9);
				
				for (GoodsBrand goodsBrand : gbs) {
					Map<String, Object> map = Maps.newHashMap();
					map.put("id", goodsBrand.getId());
					if (goodsBrand.getBrandLogo() != null) {
						map.put("logo", goodsBrand.getBrandLogo().getPath()
								+ "/" + goodsBrand.getBrandLogo().getName());
					}
					gbc_list.add(map);
				}
				gbc_map.put("name", gbc.getName());
				gbc_map.put("brands", gbc_list);
				list.add(gbc_map);
			}
		}
		mv.addObject("list", list);
		return mv;
	}
	
	/**
	 * 根据品牌列表商品
	 * 
	 * @param request
	 * @param response
	 * @param id
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @param goods_inventory
	 * @param goods_type
	 * @param goods_transfee
	 * @param goods_cod
	 * @param goods_current_price_floor
	 * @param goods_current_price_ceiling
	 * @param group_buy
	 * @param enough_reduce
	 * @param order_enough_give_status
	 * @param activity_status
	 * @param combin_status
	 * @param advance_sale_type
	 * @param f_sale_type
	 * @param area_id
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping({ "/brand_goods" })
	public ModelAndView brand_goods(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage,
			String orderBy, String orderType, String goods_inventory,
			String goods_type, String goods_transfee, String goods_cod,
			String goods_current_price_floor,
			String goods_current_price_ceiling, String group_buy,
			String enough_reduce, String order_enough_give_status,
			String activity_status, String combin_status,
			String advance_sale_type, String f_sale_type, String area_id) {
		
		ModelAndView mv = new RedPigJModelAndView("brand_goods.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		
		GoodsBrand gb = this.goodsBrandService.selectByPrimaryKey(CommUtil.null2Long(id));
		
		if (gb != null) {
			mv.addObject("gb", gb);
			mv.addObject("id", id);
			String path = ClusterSyncTools.getClusterRoot() + File.separator + "luence" + File.separator + "goods";
			RedPigLuceneUtil lucene = RedPigLuceneUtil.instance();
			RedPigLuceneUtil.setIndex_path(path);
			Sort sort = null;
			boolean order_type = true;
			String order_by = "";
			// 处理排序方式
			if (CommUtil.null2String(orderBy).equals("goods_salenum")) {
				order_by = "goods_salenum";
				sort = new Sort(new SortField(order_by, SortField.Type.INT,order_type));
			}
			
			if (CommUtil.null2String(orderBy).equals("goods_collect")) {
				order_by = "goods_collect";
				sort = new Sort(new SortField(order_by, SortField.Type.INT,order_type));
			}
			if (CommUtil.null2String(orderBy).equals("well_evaluate")) {
				order_by = "well_evaluate";
				sort = new Sort(new SortField(order_by, SortField.Type.DOUBLE,
						order_type));
			}
			if (CommUtil.null2String(orderType).equals("asc")) {
				order_type = false;
			}
			if (CommUtil.null2String(orderType).equals("")) {
				orderType = "desc";
			}
			if (CommUtil.null2String(orderBy).equals("goods_current_price")) {
				order_by = "store_price";
				sort = new Sort(new SortField(order_by, SortField.Type.DOUBLE,
						order_type));
			}
			if ((area_id != null) && (!area_id.equals(""))) {
				this.areaViewTools.setDefaultArea(request, area_id);
				mv.addObject("area_id", area_id);
			}
			
			this.areaViewTools.getUserAreaInfo(request, mv);
			
			String salesStr = getSalesWayLuceneString(group_buy, enough_reduce,
					order_enough_give_status, activity_status, combin_status,
					advance_sale_type, f_sale_type);
			
			LuceneResult pList = lucene.search(null,
					CommUtil.null2Int(currentPage), goods_inventory,
					goods_type, null, goods_transfee, goods_cod, sort,
					gb.getName(), null, salesStr, goods_current_price_floor,
					goods_current_price_ceiling, area_id);
			
			String url = CommUtil.getURL(request) + "/brand_goods_ajax";
			mv.addObject("objs", pList.getVo_list());
			mv.addObject("brand_goods_gotoPageAjaxHTML", CommUtil
					.showPageAjaxHtml(url, "", pList.getCurrentPage(),
							pList.getPages(), pList.getPageSize()));
			mv.addObject("allCount", Integer.valueOf(pList.getRows()));
			mv.addObject("stores", search_stores_seo(gb.getName()));
			mv.addObject("more_gbs", more_gb(gb.getCategory()));
		}
		more_goods(mv);
		mv.addObject("orderBy", orderBy);
		mv.addObject("orderType", orderType);
		if (!CommUtil.null2String(goods_type).equals("")) {
			mv.addObject("goods_type", goods_type);
		}
		if (!CommUtil.null2String(goods_inventory).equals("")) {
			mv.addObject("goods_inventory", goods_inventory);
		}
		if (!CommUtil.null2String(goods_transfee).equals("")) {
			mv.addObject("goods_transfee", goods_transfee);
		}
		if (!CommUtil.null2String(goods_cod).equals("")) {
			mv.addObject("goods_cod", goods_cod);
		}
		if (!CommUtil.null2String(group_buy).equals("")) {
			mv.addObject("group_buy", group_buy);
		}
		if (!CommUtil.null2String(enough_reduce).equals("")) {
			mv.addObject("enough_reduce", enough_reduce);
		}
		if (!CommUtil.null2String(order_enough_give_status).equals("")) {
			mv.addObject("order_enough_give_status", order_enough_give_status);
		}
		if (!CommUtil.null2String(activity_status).equals("")) {
			mv.addObject("activity_status", activity_status);
		}
		if (!CommUtil.null2String(combin_status).equals("")) {
			mv.addObject("combin_status", combin_status);
		}
		if (!CommUtil.null2String(advance_sale_type).equals("")) {
			mv.addObject("advance_sale_type", advance_sale_type);
		}
		if (!CommUtil.null2String(f_sale_type).equals("")) {
			mv.addObject("f_sale_type", f_sale_type);
		}
		if (!CommUtil.null2String(goods_current_price_floor).equals("")) {
			if (!CommUtil.null2String(goods_current_price_ceiling).equals("")) {
				mv.addObject("goods_current_price_floor",goods_current_price_floor);
				mv.addObject("goods_current_price_ceiling",goods_current_price_ceiling);
			}
		}
		// 处理系统商品对比信息
		List<Goods> goods_compare_list = (List) request.getSession(false).getAttribute("goods_compare_cart");
		// 计算商品对比中第一间商品的分类，只允许对比同一个分类的商品
		if (goods_compare_list == null) {
			goods_compare_list = Lists.newArrayList();
		}
		
		mv.addObject("goods_compare_list", goods_compare_list);
		mv.addObject("goodsViewTools", this.goodsViewTools);
		mv.addObject("userTools", this.userTools);
		return mv;
	}

	/**
	 * 根据店铺SEO关键字，查出经营该品牌的店铺
	 * 
	 * @param request
	 * @param response
	 * @param id
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @param goods_inventory
	 * @param goods_type
	 * @param goods_transfee
	 * @param goods_cod
	 * @param goods_current_price_floor
	 * @param goods_current_price_ceiling
	 * @param group_buy
	 * @param enough_reduce
	 * @param order_enough_give_status
	 * @param activity_status
	 * @param combin_status
	 * @param advance_sale_type
	 * @param f_sale_type
	 * @param area_id
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping({ "/brand_goods_ajax" })
	public ModelAndView brand_goods_ajax(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage,
			String orderBy, String orderType, String goods_inventory,
			String goods_type, String goods_transfee, String goods_cod,
			String goods_current_price_floor,
			String goods_current_price_ceiling, String group_buy,
			String enough_reduce, String order_enough_give_status,
			String activity_status, String combin_status,
			String advance_sale_type, String f_sale_type, String area_id) {
		ModelAndView mv = new RedPigJModelAndView("brand_goods_ajax.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		GoodsBrand gb = this.goodsBrandService.selectByPrimaryKey(CommUtil
				.null2Long(id));
		boolean order_type;
		if (gb != null) {
			mv.addObject("gb", gb);
			String path = ClusterSyncTools.getClusterRoot() + File.separator + "luence" + File.separator + "goods";
			
			RedPigLuceneUtil lucene = RedPigLuceneUtil.instance();
			RedPigLuceneUtil.setIndex_path(path);
			Sort sort = null;
			order_type = true;
			String order_by = "";
			if (CommUtil.null2String(orderBy).equals("goods_salenum")) {
				order_by = "goods_salenum";
				sort = new Sort(new SortField(order_by, SortField.Type.INT,order_type));
			}
			if (CommUtil.null2String(orderBy).equals("goods_collect")) {
				order_by = "goods_collect";
				sort = new Sort(new SortField(order_by, SortField.Type.INT, order_type));
			}
			if (CommUtil.null2String(orderBy).equals("well_evaluate")) {
				order_by = "well_evaluate";
				sort = new Sort(new SortField(order_by, SortField.Type.DOUBLE,
						order_type));
			}
			if (CommUtil.null2String(orderType).equals("asc")) {
				order_type = false;
			}
			if (CommUtil.null2String(orderType).equals("")) {
				orderType = "desc";
			}
			if (CommUtil.null2String(orderBy).equals("goods_current_price")) {
				order_by = "store_price";
				sort = new Sort(new SortField(order_by, SortField.Type.DOUBLE,
						order_type));
			}
			String salesStr = getSalesWayLuceneString(group_buy, enough_reduce,
					order_enough_give_status, activity_status, combin_status,
					advance_sale_type, f_sale_type);
			
			LuceneResult pList = lucene.search(null,
					CommUtil.null2Int(currentPage), goods_inventory,
					goods_type, null, goods_transfee, goods_cod, sort,
					gb.getName(), null, salesStr, goods_current_price_floor,
					goods_current_price_ceiling, area_id);
			
			String url = CommUtil.getURL(request) + "/brand_goods_ajax";
			mv.addObject("objs", pList.getVo_list());
			mv.addObject("brand_goods_gotoPageAjaxHTML", CommUtil
					.showPageAjaxHtml(url, "", pList.getCurrentPage(),
							pList.getPages(), pList.getPageSize()));
		}
		mv.addObject("goodsViewTools", this.goodsViewTools);
		mv.addObject("userTools", this.userTools);
		List<Goods> goods_compare_list = (List) request.getSession(false)
				.getAttribute("goods_compare_cart");
		if (goods_compare_list == null) {
			goods_compare_list = Lists.newArrayList();
		}
		int compare_goods_flag = 0;
		for (Goods compare_goods : goods_compare_list) {
			if (compare_goods != null) {
				compare_goods = this.goodsService.selectByPrimaryKey(compare_goods
						.getId());
				if (!compare_goods.getGc().getParent().getParent().getId()
						.equals(gb.getGc().getId())) {
					compare_goods_flag = 1;
				}
			}
		}
		mv.addObject("compare_goods_flag", compare_goods_flag);
		mv.addObject("goods_compare_list", goods_compare_list);
		return mv;
	}

	public List<Store> search_stores_seo(String gb_name) {
		Map<String, Object> params = Maps.newHashMap();
		params.put("keyword1", gb_name);
		params.put("keyword2", gb_name);
		params.put("keyword3", gb_name);
		params.put("keyword4", gb_name);
		
		List<Store> stores = this.storeService.queryPageList(params,0,3);
		
		return stores;
	}

	/**
	 * 品牌主页上方“发现更多品牌”
	 * 
	 * @param gbc
	 * @return
	 */
	public List<GoodsBrand> more_gb(GoodsBrandCategory gbc) {
		if ((gbc != null) && (gbc.getBrands().size() > 5)) {
			return gbc.getBrands();
		}
		Map<String, Object> params = Maps.newHashMap();
		params.put("recommend", true);
		params.put("audit", 1);
//		params.put("orderBy", "gc_sequence");
//		params.put("orderType", "asc");
		
		List<GoodsBrand> gbs = this.goodsBrandService.queryPageList(params,  0, 6);
		
		return gbs;
	}

	/**
	 * 品牌主页左侧推广商品
	 * 
	 * @param mv
	 */
	@SuppressWarnings("rawtypes")
	public void more_goods(ModelAndView mv) {
		if (this.configService.getSysConfig().getZtc_status()) {
			List<Goods> left_ztc_goods = null;
			Map<String, Object> ztc_map = Maps.newHashMap();
			ztc_map.put("ztc_status", Integer.valueOf(3));
			ztc_map.put("ztc_begin_time_less_than_equal", new Date());
			ztc_map.put("ztc_gold_more_than", 0);
			ztc_map.put("orderBy", "ztc_dredge_price");
			ztc_map.put("orderType", "desc");
			
			List<Goods> all_left_ztc_goods = this.goodsService.queryPageListWithNoRelations(ztc_map);
			
			left_ztc_goods = this.goodsService.queryPageList(ztc_map,0,all_left_ztc_goods.size());
			
			left_ztc_goods = this.goodsViewTools.randomZtcGoods2(left_ztc_goods, 8);
			
			mv.addObject("left_ztc_goods", left_ztc_goods);
		} else {
			Map<String, Object> params2 = Maps.newHashMap();
			params2.clear();
			params2.put("store_recommend", true);
			params2.put("goods_status", 0);
			params2.put("orderBy", "goods_salenum");
			params2.put("orderType", "desc");
			
			List all_goods = this.goodsService.queryPageListWithNoRelations(params2);
			
			List<Goods> left_ztc_goods = this.goodsService.queryPageList(params2,  0, all_goods.size());
			
			left_ztc_goods = this.goodsViewTools.randomZtcGoods2(left_ztc_goods, 8);
			
			mv.addObject("left_ztc_goods", left_ztc_goods);
		}
	}

	/**
	 * 生成lucene查询条件
	 * 
	 * @param group_buy
	 * @param enough_reduce
	 * @param order_enough_give_status
	 * @param activity_status
	 * @param combin_status
	 * @param advance_sale_type
	 * @param f_sale_type
	 * @return
	 */
	private String getSalesWayLuceneString(String group_buy,
			String enough_reduce, String order_enough_give_status,
			String activity_status, String combin_status,
			String advance_sale_type, String f_sale_type) {
		StringBuilder sbSql = new StringBuilder("(");
		if (!CommUtil.null2String(group_buy).equals("")) {
			sbSql.append("whether_active:1 OR ");
		}
		if (!CommUtil.null2String(enough_reduce).equals("")) {
			sbSql.append("whether_active:4 OR ");
		}
		if (!CommUtil.null2String(order_enough_give_status).equals("")) {
			sbSql.append("whether_active:3 OR ");
		}
		if (!CommUtil.null2String(activity_status).equals("")) {
			sbSql.append("whether_active:2 OR ");
		}
		if (!CommUtil.null2String(combin_status).equals("")) {
			sbSql.append("whether_active:5 OR ");
		}
		if (!CommUtil.null2String(advance_sale_type).equals("")) {
			sbSql.append("whether_active:7 OR ");
		}
		if (!CommUtil.null2String(f_sale_type).equals("")) {
			sbSql.append("whether_active:6 OR ");
		}
		if (sbSql.length() > 1) {
			sbSql.setLength(sbSql.length() - 3);
			sbSql.append(")");
			return sbSql.toString();
		}
		return null;
	}
}
