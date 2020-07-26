package com.redpigmall.module.weixin.view.action;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.util.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.redpigmall.domain.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.redpigmall.api.ip.IPSeeker;
import com.redpigmall.api.mv.RedPigJModelAndView;
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.api.sec.SecurityUserHolder;
import com.redpigmall.api.tools.ClusterSyncTools;
import com.redpigmall.api.tools.CommUtil;
import com.redpigmall.lucene.RedPigLuceneUtil;
import com.redpigmall.module.weixin.view.action.base.BaseAction;


/**
 * 
 * 
 * <p>
 * Title: RedPigWeixinGoodsViewAction.java
 * </p>
 * 
 * <p>
 * Description: 手机客户端商城前台商品请求控制器
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
 * @date 2014年8月20日
 * 
 * @version redpigmall_b2b2c_2015
 */
@SuppressWarnings({ "unchecked", "rawtypes", "unused" })
@Controller
public class RedPigWeixinGoodsViewAction extends BaseAction{

	private Long id;

	/**
	 * 商品评论
	 * @param response
	 * @param id
	 */
	@RequestMapping({ "/add_goods_favorite" })
	public void add_goods_favorite(HttpServletResponse response, String id) {
		Long goods_id = CommUtil.null2Long(id);
		int ret = 0;
		if (SecurityUserHolder.getCurrentUser() == null) {
			ret = 2;
		} else {
			Map<String, Object> params = Maps.newHashMap();
			params.put("user_id", SecurityUserHolder.getCurrentUser().getId());
			params.put("goods_id", goods_id);
			
			List list = this.favoriteService.queryPageList(params);
			
			if (list.size() == 0) {
				Goods goods = this.goodsService.selectByPrimaryKey(goods_id);
				Favorite obj = new Favorite();
				obj.setAddTime(new Date());
				obj.setType(0);
				User user = SecurityUserHolder.getCurrentUser();
				obj.setUser_name(user.getUserName());
				obj.setUser_id(user.getId());
				obj.setGoods_id(goods.getId());
				obj.setGoods_name(goods.getGoods_name());
				obj.setGoods_photo(goods.getGoods_main_photo().getPath() + "/"
						+ goods.getGoods_main_photo().getName());
				obj.setGoods_photo_ext(goods.getGoods_main_photo().getExt());
				obj.setGoods_store_id(goods.getGoods_store() == null ? null
						: goods.getGoods_store().getId());
				obj.setGoods_type(goods.getGoods_type());
				obj.setGoods_current_price(goods.getGoods_current_price());
				if (this.configService.getSysConfig().getSecond_domain_open()) {
					Store store = this.storeService.selectByPrimaryKey(obj
							.getStore_id());
					obj.setGoods_store_second_domain(store
							.getStore_second_domain());
				}
				this.favoriteService.saveEntity(obj);
				goods.setGoods_collect(goods.getGoods_collect() + 1);
				this.goodsService.updateById(goods);
				GoodsLog todayGoodsLog = this.goodsTools
						.getTodayGoodsLog(goods_id.longValue());
				todayGoodsLog
						.setGoods_collect(todayGoodsLog.getGoods_collect() + 1);
				this.goodsLogService.updateById(todayGoodsLog);
				this.goodsTools.updateGoodsLucene(goods);
			} else {
				ret = 1;
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
	
	/**
	 * 搜藏商品删除
	 * @param response
	 * @param id
	 */
	@RequestMapping({ "/add_goods_favorite_del" })
	public void add_goods_favorite_del(HttpServletResponse response, String id) {
		Long goods_id = CommUtil.null2Long(id);
		int ret = 0;
		if (SecurityUserHolder.getCurrentUser() == null) {
			ret = 2;
		} else {
			Map<String, Object> params = Maps.newHashMap();
			params.put("user_id", SecurityUserHolder.getCurrentUser().getId());
			params.put("goods_id", goods_id);
			
			List<Favorite> list = this.favoriteService.queryPageList(params);
			
			if (list.size() != 0) {
				Favorite favorite = (Favorite) list.get(0);
				if (favorite.getGoods_id() != null) {
					String goods_lucene_path = ClusterSyncTools
							.getClusterRoot()
							+ File.separator
							+ "luence"
							+ File.separator + "goods";
					File file = new File(goods_lucene_path);
					if (!file.exists()) {
						CommUtil.createFolder(goods_lucene_path);
					}
					RedPigLuceneUtil lucene = RedPigLuceneUtil.instance();
					RedPigLuceneUtil.setIndex_path(goods_lucene_path);
					Goods goods = this.goodsService.selectByPrimaryKey(favorite
							.getGoods_id());

					goods.setGoods_collect(goods.getGoods_collect() - 1);
					this.goodsService.updateById(goods);
					this.favoriteService.deleteById(favorite.getId());
					goods.setGoods_collect(goods.getGoods_collect() - 1);
					this.goodsService.updateById(goods);
					GoodsLog todayGoodsLog = this.goodsTools
							.getTodayGoodsLog(goods_id.longValue());
					todayGoodsLog.setGoods_collect(todayGoodsLog
							.getGoods_collect() + 1);
					this.goodsLogService.updateById(todayGoodsLog);
					this.goodsTools.updateGoodsLucene(goods);
				}
				ret = 1;
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
	
	/**
	 * 
	 * @param request
	 * @param response
	 * @param goods_id
	 * @param currentPage
	 * @param goods_eva
	 * @return
	 */
	@RequestMapping({ "/goods_eva" })
	public ModelAndView goods_evaluation(HttpServletRequest request,
			HttpServletResponse response, String goods_id, String currentPage,
			String goods_eva) {
		ModelAndView mv = new RedPigJModelAndView("weixin/goods_eva.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		if (CommUtil.null2Int(currentPage) > 1) {
			mv = new RedPigJModelAndView("weixin/goods_eva_data.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
		}
		
		Map<String,Object> maps= this.redPigQueryTools.getParams(currentPage,10,"addTime", "desc");
		
		maps.put("evaluate_goods_id", CommUtil.null2Long(goods_id));
		
		maps.put("evaluate_type", "goods");
		
		maps.put("evaluate_status", 0);
		
		if (!CommUtil.null2String(goods_eva).equals("")) {
			if (goods_eva.equals("100")) {
				maps.put("evaluate_photos_no", "");
			} else {
				maps.put("evaluate_buyer_val", CommUtil.null2Int(goods_eva));
			}
		}
		
		IPageList eva_pList = this.evaluateService.list(maps);
		List list = eva_pList.getResult();

		mv.addObject("eva_objs", list);
		mv.addObject("evaluateViewTools", this.evaluateViewTools);
		mv.addObject("goodsViewTools", this.goodsViewTools);
		mv.addObject("activityViewTools", this.activityViewTools);
		mv.addObject("goods_id", goods_id);
		mv.addObject("goods_eva", goods_eva);
		CommUtil.saveIPageList2ModelAndView("", "", "", eva_pList, mv);
		return mv;
	}
	
	@RequestMapping({ "/free_eva" })
	public ModelAndView free_eva(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String id,
			String type) {
		ModelAndView mv = new RedPigJModelAndView("weixin/free_eva.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		if (CommUtil.null2Int(currentPage) > 1) {
			mv = new RedPigJModelAndView("weixin/free_eva_data.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
		}
		
		Map<String,Object> maps= this.redPigQueryTools.getParams(currentPage,10, "addTime", "desc");
		Map fmap = Maps.newHashMap();
		fmap.put("goods_id", CommUtil.null2Long(id));
		
		List<FreeGoods> list = this.freeGoodsService.queryPageList(fmap);
		
		Long fal_id = CommUtil.null2Long("");
		if (list.size() > 0) {
			FreeGoods fg = (FreeGoods) list.get(0);
			if ((fg != null) && (!"".equals(fg))) {
				fal_id = fg.getId();
			}
		}
		maps.put("freegoods_id", fal_id);
		
		IPageList free_pList = this.freeApplyLogService.list(maps);
		mv.addObject("free_objs", free_pList.getResult());
		mv.addObject("id", id);
		mv.addObject("type", type);
		CommUtil.saveIPageList2ModelAndView("", "", "", free_pList, mv);
		return mv;
	}

	@RequestMapping({ "/order_record" })
	public ModelAndView order_record(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String id) {
		ModelAndView mv = new RedPigJModelAndView("weixin/order_record.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		if (CommUtil.null2Int(currentPage) > 1) {
			mv = new RedPigJModelAndView("weixin/order_record_data.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
		}
		
		Map<String,Object> maps= this.redPigQueryTools.getParams(currentPage,10, "addTime", "desc");
		
		maps.put("evaluate_goods_id", CommUtil.null2Long(id));
		
		IPageList order_eva_pList = this.evaluateService.list(maps);
		mv.addObject("order_objs", order_eva_pList.getResult());
		mv.addObject("id", id);
		CommUtil.saveIPageList2ModelAndView("", "", "", order_eva_pList, mv);
		return mv;
	}

	@RequestMapping({ "/combin_items" })
	public ModelAndView combin_goods(HttpServletRequest request,
			HttpServletResponse response, String id, String gsp) {
		ModelAndView mv = new RedPigJModelAndView("weixin/combin_goods.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		Goods goods = this.goodsService.selectByPrimaryKey(CommUtil.null2Long(id));
		mv.addObject("obj", goods);
		mv.addObject("id", id);
		mv.addObject("goodsViewTools", this.goodsViewTools);
		if (SecurityUserHolder.getCurrentUser() != null) {
			User user = this.userService.selectByPrimaryKey(SecurityUserHolder
					.getCurrentUser().getId());
			mv.addObject("user", user);
		}
		mv.addObject("gsp", gsp);
		return mv;
	}

	@RequestMapping({ "/consult" })
	public ModelAndView consult(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String id) {
		ModelAndView mv = new RedPigJModelAndView("weixin/consult.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		if (CommUtil.null2Int(currentPage) > 1) {
			mv = new RedPigJModelAndView("weixin/consult_data.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
		}
		
		Map<String,Object> maps= this.redPigQueryTools.getParams(currentPage,10,"addTime", "desc");
		
		maps.put("goods_id", CommUtil.null2Long(id));
		
		IPageList pList = this.consultService.list(maps);
		mv.addObject("consult_objs", pList.getResult());
		mv.addObject("consultViewTools", this.consultViewTools);
		mv.addObject("id", id);
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		return mv;
	}

	/**
	 * 查询商品库存
	 * @param request
	 * @param response
	 * @param city_name
	 * @param goods_id
	 * @param city_id
	 * @param gsp
	 */
	@RequestMapping({ "/goods_inventory" })
	public void goods_inventory(HttpServletRequest request,
			HttpServletResponse response, String city_name, String goods_id,
			String city_id, String gsp) {
		Map<String, Object> map = Maps.newHashMap();
		Goods goods = this.goodsService
				.selectByPrimaryKey(CommUtil.null2Long(goods_id));
		int inventory = this.inventoryService.queryGoodsInventory(goods_id,
				city_id, gsp);
		this.areaViewTools.setDefaultArea(request, city_id);
		float mail_fee = 0.0F;
		float express_fee = 0.0F;
		float ems_fee = 0.0F;
		if ((goods != null) && (goods.getTransport() != null)) {
			mail_fee = this.transportTools.cal_goods_trans_fee(
					CommUtil.null2String(goods.getTransport().getId()), "mail",
					CommUtil.null2String(goods.getGoods_weight()),
					CommUtil.null2String(goods.getGoods_volume()), city_name);
			express_fee = this.transportTools.cal_goods_trans_fee(
					CommUtil.null2String(goods.getTransport().getId()),
					"express", CommUtil.null2String(goods.getGoods_weight()),
					CommUtil.null2String(goods.getGoods_volume()), city_name);
			ems_fee = this.transportTools.cal_goods_trans_fee(
					CommUtil.null2String(goods.getTransport().getId()), "ems",
					CommUtil.null2String(goods.getGoods_weight()),
					CommUtil.null2String(goods.getGoods_volume()), city_name);
		}
		map.put("inventory", Integer.valueOf(inventory));
		map.put("mail_fee", Float.valueOf(mail_fee));
		map.put("express_fee", Float.valueOf(express_fee));
		map.put("ems_fee", Float.valueOf(ems_fee));
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		try {
			PrintWriter writer = response.getWriter();
			writer.print(JSON.toJSONString(map));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * wap端查看商品详情
	 * @param request
	 * @param response
	 * @param id
	 * @return
	 * @throws Exception
	 */
	@RequestMapping({ "/items" })
	public ModelAndView goods(HttpServletRequest request,
							  HttpServletResponse response, String id) throws Exception {
		ModelAndView mv = null;
		Goods obj = this.goodsService.selectByPrimaryKey(CommUtil.null2Long(id));

		Cookie[] cookies = request.getCookies();
		Cookie goodscookie = null;
		int k = 0;
		String[] new_ids;
		if (cookies != null) {

			for (Cookie cookie:cookies) {

				if (cookie.getName().equals("goodscookie")) {
					String goods_ids = URLDecoder.decode(cookie.getValue(), "UTF-8");
					int m = 6;
					int n = goods_ids.split(",").length;
					if (m > n) {
						m = n + 1;
					}
					String[] new_goods_ids = goods_ids.split(",", m);
					for (int i = 0; i < new_goods_ids.length; i++) {
						if ("".equals(new_goods_ids[i])) {
							for (int j = i + 1; j < new_goods_ids.length; j++) {
								new_goods_ids[i] = new_goods_ids[j];
							}
						}
					}
					new_ids = new String[6];
					for (int i = 0; i < m - 1; i++) {
						if (id.equals(new_goods_ids[i])) {
							k++;
						}
					}
					if (k == 0) {
						new_ids[0] = id;
						for (int j = 1; j < m; j++) {
							new_ids[j] = new_goods_ids[(j - 1)];
						}
						goods_ids = id + ",";
						if (m == 2) {
							for (int i = 1; i <= m - 1; i++) {
								goods_ids = goods_ids + new_ids[i] + ",";
							}
						} else {
							for (int i = 1; i < m; i++) {
								goods_ids = goods_ids + new_ids[i] + ",";
							}
						}
						goodscookie = new Cookie("goodscookie", goods_ids);
					} else {
						new_ids = new_goods_ids;
						goods_ids = "";
						for (int i = 0; i < m - 1; i++) {
							goods_ids = goods_ids + new_ids[i] + ",";
						}
						goodscookie = new Cookie("goodscookie", goods_ids);
					}
					goodscookie.setMaxAge(604800);
					goodscookie.setDomain(CommUtil.generic_domain(request));
					response.addCookie(goodscookie);
					break;
				}
				goodscookie = new Cookie("goodscookie", id + ",");
				goodscookie.setMaxAge(604800);
				goodscookie.setDomain(CommUtil.generic_domain(request));
			//	response.addCookie(goodscookie);
			}
		} else {
			goodscookie = new Cookie("goodscookie", id + ",");
			goodscookie.setMaxAge(604800);
			goodscookie.setDomain(CommUtil.generic_domain(request));
			response.addCookie(goodscookie);
		}
		User current_user = SecurityUserHolder.getCurrentUser();
		boolean admin_view = false;
		if (current_user != null) {
			this.goodsViewTools.record_footPoint(request, current_user, obj);
			current_user = this.userService.selectByPrimaryKey(current_user.getId());
			if (current_user.getUserRole().equals("ADMIN")) {
				admin_view = true;
			}
		}
		this.goodsTools.save_click_goodsLog(request, obj);
		int falcount = 0;
		if (((obj != null) && (obj.getGoods_status() == 0)) || (admin_view)) {
			String cart_session_id;
			List<GoodsCart> carts_user;
			String current_city;
			// 如果是自营商品
			if (obj.getGoods_type() == 0) {
				mv = new RedPigJModelAndView("weixin/goods_details.html",
						this.configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 1, request,
						response);
				obj.setGoods_click(obj.getGoods_click() + 1);
				if ((this.configService.getSysConfig().getZtc_status())
						&& (obj.getZtc_status() == 2)) {
					obj.setZtc_click_num(obj.getZtc_click_num() + 1);
				}
				int count = -1;
				if ((obj.getGoods_limit() == 1)
						&& (obj.getGoods_limit_count() > 0)) {
					count = obj.getGoods_limit_count();
					if (current_user != null) {
						String info = CommUtil.null2String(current_user
								.getBuy_goods_limit_info());
						if (!info.equals("")) {
							Map maps = JSON.parseObject(CommUtil
									.null2String(info));
							List<Map> list = (List) maps.get("data");

							for (Iterator ite = list.iterator(); ite.hasNext();) {
								Map<String, Object> map = (Map) ite.next();
								String gid = CommUtil.null2String(map
										.get("gid"));
								if (CommUtil.null2Int(gid) == obj.getId()
										.longValue()) {
									count = obj.getGoods_limit_count()
											- CommUtil.null2Int(map
											.get("count"));
									if (count < 1) {
										count = 0;
									}
								}
							}
						}
					}
				} else {
					count = -1;
				}
				mv.addObject("count", Integer.valueOf(count));
				// 如果当前活动状态为待审核或者已经审核完成
				if ((obj.getActivity_status() == 1)|| (obj.getActivity_status() == 2)) {
					if (!CommUtil.null2String(obj.getActivity_goods_id()).equals("")) {
						ActivityGoods ag = this.actgoodsService.selectByPrimaryKey(obj.getActivity_goods_id());
						if ((ag != null)&& (ag.getAct().getAc_end_time().before(new Date()))) {
							ag.setAg_status(-2);
							this.actgoodsService.updateById(ag);
							obj.setActivity_status(0);
							obj.setActivity_goods_id(null);
						}
					}
				}
				// 如果是团购
				if ((obj.getGroup() != null) && (obj.getGroup_buy() == 2)) {
					Group group = obj.getGroup();
					if (group.getEndTime().before(new Date())) {
						obj.setGroup(null);
						obj.setGroup_buy(0);
						obj.setGoods_current_price(obj.getStore_price());
					}
				}
				// 如果是秒杀
				if ((obj.getNuke_buy() == 2)) {
					// 获取该商品参与的秒杀活动，一个商品在同一时段内只能有一个秒杀活动
					Map<String,Object> paras = new HashMap<>();
					paras.put("ng_goods_id",obj.getId());
					List<NukeGoods> nukeGoodsList = this.nukeGoodsService.selectObjByProperty(paras);
					if (nukeGoodsList!=null&&nukeGoodsList.size()>0){
						NukeGoods nukeGoods = nukeGoodsList.get(0);
						mv.addObject("nukeGoods",nukeGoods);
						// 获取用户是否已经购买过该商品，返回购买的数量
						//mv.addObject("goods_spec_id",nukeGoods.getGoods_spec_id());
						// 获取活动信息
						Nuke nuke = this.nukeService.selectByPrimaryKey(nukeGoods.getNuke().getId());
						//如果当前活动结束时间已经过了，则设置秒杀状态为0，价格恢复当前店铺价格
						if (nuke.getEndTime().before(new Date())) {
							obj.setNuke_buy(0);
							obj.setGoods_current_price(obj.getStore_price());
						}
					}
				}
				// 如果是拼团
				if ((obj.getCollage_buy() == 2)) {
					Map<String,Object> paras = new HashMap<>();
					/**
					 * 商品ID
					 */
					paras.put("goods_id",obj.getId());
					List<CollageBuy> collageBuys = this.collageBuyService.selectObjByProperty(paras);
					/**
					 * 拼团实现层拼团实现层
					 */
					List<UserCollageList> selectObjByCollage = this.collageListService.selectObjByCollage(paras);
					Integer cg_status=null;
					Long r=null;
					Long group_Id=null;

					for(UserCollageList s:selectObjByCollage ){
						cg_status=s.getCollageBuy().getCg_status();
						r=s.getId();
						group_Id=s.getGroup().getId();
					}

					//还差多少人拼成团统计

					//int oo=this.collageListService.selectCountGroup(group_Id);

					/**
					 * 拼团对象查询
					 */

					//this.collageListService.selectByPrimaryKeys(r);
					/**
					 * 得到团长对象
					 */

					//this.collageListService.selectByGroupHostPrimdaryKey(1);
					/**
					 * 拼团列表
					 */

					List<UserCollageBuyInfo> selectCollageBuyList = this.collageListService.selectUserCollageBuyInfo(paras);
					if(selectCollageBuyList!=null&&selectCollageBuyList.size()>0) {
						UserCollageBuyInfo selectCollageBuyLists=selectCollageBuyList.get(0);
						mv.addObject("selectCollageBuyList",selectCollageBuyLists);
					}

					for(UserCollageBuyInfo s:selectCollageBuyList){

						System.out.println(s.getCollageBuy().getUser().getUserName()+"----------------");
					}

					/**
					 * 拼团人数统计
					 */
					Long h = obj.getId();
					int goods_id=Integer.parseInt(String.valueOf(h));
					Map<String,Integer> pa = new HashMap<>();
					pa.put("cg_status",cg_status);
					pa.put("goods_id",goods_id);


					//得到拼团人数
					int s=this.collageListService.selectCounts(pa);
					mv.addObject("cg_status",s);




					if(selectObjByCollage!=null&&selectObjByCollage.size()>0) {
						UserCollageList userCollageList=selectObjByCollage.get(0);
						mv.addObject("userCollageList",userCollageList);
					}

					if (collageBuys!=null&&collageBuys.size()>0){
						CollageBuy collageBuy = collageBuys.get(0);
						//如果当前活动结束时间已经过了，则设置秒杀状态为0，价格恢复当前店铺价格
						if (collageBuy.getEndTime().before(new Date())) {
							obj.setCollage_buy(0);
							obj.setGoods_current_price(obj.getStore_price());
						}
					}
				}
				// 如果是组合销售
				if (obj.getCombin_status() == 1) {
					Map<String, Object> params = Maps.newHashMap();
					params.put("endTime_less_than_equal", new Date());
					params.put("main_goods_id", obj.getId());

					List<CombinPlan> combins = this.combinplanService.queryPageList(params);

					if (combins.size() > 0) {
						for (CombinPlan com : combins) {
							if (com.getCombin_type() == 0) {
								if (obj.getCombin_suit_id().equals(com.getId())) {
									obj.setCombin_suit_id(null);
								}
							} else if (obj.getCombin_parts_id().equals(
									com.getId())) {
								obj.setCombin_parts_id(null);
							}
							obj.setCombin_status(-2);
						}
					}
				}
				Goods goods;
				// 如果是满就送商品
				if (obj.getOrder_enough_give_status() == 1) {
					BuyGift bg = this.buyGiftService.selectByPrimaryKey(obj
							.getBuyGift_id());
					if ((bg != null) && (bg.getEndTime().before(new Date()))) {
						bg.setGift_status(20);
						List<Map> maps = JSON.parseArray(bg.getGift_info(),
								Map.class);
						maps.addAll(JSON.parseArray(bg.getGoods_info(),
								Map.class));
						for (Map map : maps) {
							goods = this.goodsService.selectByPrimaryKey(CommUtil
									.null2Long(map.get("goods_id")));
							if (goods != null) {
								goods.setOrder_enough_give_status(0);
								goods.setOrder_enough_if_give(0);
								goods.setBuyGift_id(null);
							}
						}
						this.buyGiftService.updateById(bg);
					}
					if ((bg != null) && (bg.getGift_status() == 10)
							&& (bg.getBeginTime().before(new Date()))) {
						mv.addObject("isGift", Boolean.valueOf(true));
					}
				}
				if (obj.getOrder_enough_if_give() == 1) {
					BuyGift bg = this.buyGiftService.selectByPrimaryKey(obj
							.getBuyGift_id());
					if ((bg != null) && (bg.getGift_status() == 10)) {
						mv.addObject("isGive", Boolean.valueOf(true));
					}
				}
				// 如果是满减商品
				if (obj.getEnough_reduce() == 1) {
					EnoughReduce er = this.enoughReduceService
							.selectByPrimaryKey(CommUtil.null2Long(obj
									.getOrder_enough_reduce_id()));
					if ((er.getErstatus() == 10)
							&& (er.getErbegin_time().before(new Date()))
							&& (er.getErend_time().after(new Date()))) {
						mv.addObject("enoughreduce", er);
					} else if (er.getErend_time().before(new Date())) {
						er.setErstatus(20);
						this.enoughReduceService.updateById(er);
						String goods_json = er.getErgoods_ids_json();
						List<String> goods_id_list = JSON.parseArray(
								goods_json, String.class);
						for (String goods_id : goods_id_list) {
							Goods ergood = this.goodsService
									.selectByPrimaryKey(CommUtil.null2Long(goods_id));
							ergood.setEnough_reduce(0);
							ergood.setOrder_enough_reduce_id(null);
							this.goodsService.updateById(ergood);
						}
					}
				}
				this.goodsService.updateById(obj);
				// 如果有0元试用
				if (obj.getWhether_free() == 1) {
					Map fmap = Maps.newHashMap();
					fmap.put("goods_id", CommUtil.null2Long(id));

					List<FreeGoods> list = this.freeGoodsService.queryPageList(fmap);

					if (list.size() > 0) {
						FreeGoods fg = (FreeGoods) list.get(0);
						if ((fg != null) && (!"".equals(fg))) {
							Map<String, Object> map = Maps.newHashMap();
							map.put("freegoods_id", fg.getId());

							List<FreeApplyLog> free_list = this.freeApplyLogService.queryPageList(map);

							mv.addObject("free_list", free_list);
							if (free_list.size() >= 1) {
								falcount = free_list.size();
							}
						}
					}
				}
				Map cmap = Maps.newHashMap();
				cmap.put("goods_id", CommUtil.null2Long(id));

				List<Consult> consult_list = this.consultService.queryPageList(cmap);

				mv.addObject("consul_count",Integer.valueOf(consult_list.size()));
				mv.addObject("free_count", Integer.valueOf(falcount));
				mv.addObject("obj", obj);
				mv.addObject("goodsViewTools", this.goodsViewTools);
				mv.addObject("transportTools", this.transportTools);
				cmap.clear();
				cmap.put("parent", -1);

				List<Area> areas = this.areaService.queryPageList(cmap);

				mv.addObject("areas", areas);
				mv.addObject("userTools", this.userTools);
				mv.addObject("goodsViewTools", this.goodsViewTools);
				mv.addObject("activityViewTools", this.activityViewTools);

				this.areaViewTools.getUserAreaInfo(request, mv);

				String type = CommUtil
						.null2String(request.getAttribute("type"));
				cart_session_id = "";
				Cookie[] cookies1 = request.getCookies();
				if (cookies1 != null) {
					for (Cookie cookie : cookies1) {
						if (cookie.getName().equals("cart_session_id")) {
							cart_session_id = CommUtil.null2String(cookie
									.getValue());
						}
					}
				}
				if (cart_session_id.equals("")) {
					cart_session_id = UUID.randomUUID().toString();
					Cookie cookie = new Cookie("cart_session_id",
							cart_session_id);
					cookie.setDomain(CommUtil.generic_domain(request));
				}
				List<GoodsCart> carts_list = Lists.newArrayList();
				List<GoodsCart> carts_cookie = Lists.newArrayList();
				carts_user = Lists.newArrayList();
				User user = SecurityUserHolder.getCurrentUser();
				Map cart_map = Maps.newHashMap();
				if (user != null) {
					user = this.userService.selectByPrimaryKey(user.getId());
					if (!cart_session_id.equals("")) {
						cart_map.clear();
						cart_map.put("cart_session_id", cart_session_id);
						cart_map.put("cart_status", Integer.valueOf(0));

						carts_cookie = this.goodsCartService.queryPageList(cart_map);

						if (user.getStore() != null) {
							for (GoodsCart gc : carts_cookie) {
								if (gc.getGoods().getGoods_type() == 1) {
									if (gc.getGoods().getGoods_store().getId()
											.equals(user.getStore().getId())) {
										this.goodsCartService
												.deleteById(gc.getId());
									}
								}
							}
						}
						cart_map.clear();
						cart_map.put("user_id", user.getId());
						cart_map.put("cart_status", Integer.valueOf(0));

						carts_user = this.goodsCartService.queryPageList(cart_map);

					} else {
						cart_map.clear();
						cart_map.put("user_id", user.getId());
						cart_map.put("cart_status", Integer.valueOf(0));

						carts_user = this.goodsCartService.queryPageList(cart_map);

					}
				} else if (!cart_session_id.equals("")) {
					cart_map.clear();
					cart_map.put("cart_session_id", cart_session_id);
					cart_map.put("cart_status", Integer.valueOf(0));

					carts_cookie = this.goodsCartService.queryPageList(cart_map);

				}
				boolean add;
				if (user != null) {
					for (GoodsCart cookie : carts_cookie) {
						add = true;
						for (GoodsCart gc2 : carts_user) {
							if (cookie.getGoods().getId()
									.equals(gc2.getGoods().getId())) {
								if (cookie.getSpec_info().equals(
										gc2.getSpec_info())) {
									add = false;
									this.goodsCartService
											.deleteById(cookie.getId());
								}
							}
						}
						if (add) {
							cookie.setCart_session_id(null);
							cookie.setUser(user);
							this.goodsCartService.updateById(cookie);
							carts_list.add(cookie);
						}
					}
				} else {
					for (GoodsCart gc : carts_cookie) {
						carts_list.add(gc);
					}
				}
				for (GoodsCart gc : carts_user) {
					carts_list.add(gc);
				}
				List<GoodsCart> combin_carts_list = Lists.newArrayList();
				for (GoodsCart gc : carts_list) {
					if ((gc.getCart_type() != null)
							&& (gc.getCart_type().equals("combin"))
							&& (gc.getCombin_main() != 1)) {
						combin_carts_list.add(gc);
					}
				}
				if (combin_carts_list.size() > 0) {
					carts_list.removeAll(combin_carts_list);
				}
				mv.addObject("carts", carts_list);
				//否则，是第三方商家
			} else {
				mv = new RedPigJModelAndView("weixin/goods_details.html",
						this.configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 1, request,
						response);
				obj.setGoods_click(obj.getGoods_click() + 1);
				if ((this.configService.getSysConfig().getZtc_status())
						&& (obj.getZtc_status() == 2)) {
					obj.setZtc_click_num(obj.getZtc_click_num() + 1);
				}
				int count = -1;
				if ((obj.getGoods_limit() == 1) && (obj.getGoods_limit_count() > 0)) {
					count = obj.getGoods_limit_count();
					if (current_user != null) {
						String info = CommUtil.null2String(current_user.getBuy_goods_limit_info());
						if (!info.equals("")) {
							Map maps = JSON.parseObject(CommUtil.null2String(info));
							List<Map> list = (List) maps.get("data");
							for (Map map : list) {
								String gid = CommUtil.null2String(map.get("gid"));
								if (CommUtil.null2Int(gid) == obj.getId().longValue()) {
									count = obj.getGoods_limit_count() - CommUtil.null2Int(map.get("count"));
									if (count < 1) {
										count = 0;
									}
								}
							}
						}
					}
				} else {
					count = -1;
				}
				mv.addObject("count", Integer.valueOf(count));
				if ((obj.getActivity_status() == 1)
						|| (obj.getActivity_status() == 2)) {
					if (!CommUtil.null2String(obj.getActivity_goods_id())
							.equals("")) {
						ActivityGoods ag = this.actgoodsService.selectByPrimaryKey(obj
								.getActivity_goods_id());
						if ((ag != null)
								&& (ag.getAct().getAc_end_time()
								.before(new Date()))) {
							ag.setAg_status(-2);
							this.actgoodsService.updateById(ag);
							obj.setActivity_status(0);
							obj.setActivity_goods_id(null);
						}
					}
				}
				if ((obj.getGroup() != null) && (obj.getGroup_buy() == 2)) {
					Group group = obj.getGroup();
					if (group.getEndTime().before(new Date())) {
						obj.setGroup(null);
						obj.setGroup_buy(0);
						obj.setGoods_current_price(obj.getStore_price());
					}
				}
				if (obj.getCombin_status() == 1) {
					Map<String, Object> params = Maps.newHashMap();
					params.put("endTime_less_than_equal", new Date());
					params.put("main_goods_id", obj.getId());

					List<CombinPlan> combins = this.combinplanService.queryPageList(params);

					if (combins.size() > 0) {
						for (CombinPlan com : combins) {
							if (com.getCombin_type() == 0) {
								if (obj.getCombin_suit_id().equals(com.getId())) {
									obj.setCombin_suit_id(null);
								}
							} else if (obj.getCombin_parts_id().equals(
									com.getId())) {
								obj.setCombin_parts_id(null);
							}
							obj.setCombin_status(0);
						}
					}
				}
				Goods goods;
				if (obj.getOrder_enough_give_status() == 1) {
					BuyGift bg = this.buyGiftService.selectByPrimaryKey(obj
							.getBuyGift_id());
					if ((bg != null) && (bg.getEndTime().before(new Date()))) {
						bg.setGift_status(20);
						List<Map> maps = JSON.parseArray(bg.getGift_info(),
								Map.class);
						maps.addAll(JSON.parseArray(bg.getGoods_info(),
								Map.class));
						for (Map map : maps) {
							goods = this.goodsService.selectByPrimaryKey(CommUtil
									.null2Long(map.get("goods_id")));
							if (goods != null) {
								goods.setOrder_enough_give_status(0);
								goods.setOrder_enough_if_give(0);
								goods.setBuyGift_id(null);
								this.goodsService.updateById(goods);
							}
						}
						this.buyGiftService.updateById(bg);
					}
					if ((bg != null) && (bg.getGift_status() == 10)) {
						mv.addObject("isGift", Boolean.valueOf(true));
					}
				}
				if (obj.getOrder_enough_if_give() == 1) {
					BuyGift bg = this.buyGiftService.selectByPrimaryKey(obj
							.getBuyGift_id());
					if ((bg != null) && (bg.getGift_status() == 10)) {
						mv.addObject("isGive", Boolean.valueOf(true));
					}
				}
				if (obj.getEnough_reduce() == 1) {
					EnoughReduce er = this.enoughReduceService
							.selectByPrimaryKey(CommUtil.null2Long(obj
									.getOrder_enough_reduce_id()));
					if ((er.getErstatus() == 10)
							&& (er.getErbegin_time().before(new Date()))
							&& (er.getErend_time().after(new Date()))) {
						mv.addObject("enoughreduce", er);
					} else if (er.getErend_time().before(new Date())) {
						er.setErstatus(20);
						this.enoughReduceService.updateById(er);
						String goods_json = er.getErgoods_ids_json();
						List<String> goods_id_list = JSON.parseArray(
								goods_json, String.class);
						for (String goods_id : goods_id_list) {
							Goods ergood = this.goodsService
									.selectByPrimaryKey(CommUtil.null2Long(goods_id));
							ergood.setEnough_reduce(0);
							ergood.setOrder_enough_reduce_id(null);
							this.goodsService.updateById(ergood);
						}
					}
				}
				this.goodsService.updateById(obj);

				mv.addObject("obj", obj);
				mv.addObject("store", obj.getGoods_store());
				mv.addObject("goodsViewTools", this.goodsViewTools);
				mv.addObject("transportTools", this.transportTools);

				String current_ip = CommUtil.getIpAddr(request);
				if (CommUtil.isIp(current_ip)) {
					IPSeeker ip = new IPSeeker(null, null);
					current_city = ip.getIPLocation(current_ip).getCountry();
					mv.addObject("current_city", current_city);
				} else {
					mv.addObject("current_city", "未知地区");
				}
				Map<String,Object> maps = Maps.newHashMap();
				maps.put("parent", -1);

				List<Area> areas = this.areaService.queryPageList(maps);

				mv.addObject("areas", areas);
				generic_evaluate(obj.getGoods_store(), mv);

				mv.addObject("userTools", this.userTools);
				mv.addObject("goodsViewTools", this.goodsViewTools);
				mv.addObject("activityViewTools", this.activityViewTools);
			}
			if (SecurityUserHolder.getCurrentUser() != null) {
				Map<String,Object> map = Maps.newHashMap();
				map.put("goods_id", obj.getId());
				map.put("user_id", SecurityUserHolder.getCurrentUser().getId());

				List<Favorite> favorites = this.favoriteService.queryPageList(map);

				if (favorites.size() > 0) {
					mv.addObject("mark", Integer.valueOf(1));
				}
			}
			Object service_list = Lists.newArrayList();
			if (obj.getMerchantService_info() != null) {
				List<String> ms_list = JSON.parseArray(
						obj.getMerchantService_info(), String.class);
				for (String ms_id : ms_list) {
					MerchantServices ms = this.merchantServicesService
							.selectByPrimaryKey(CommUtil.null2Long(ms_id));
					Map m = Maps.newHashMap();
					m.put("id", ms_id);
					m.put("name", ms.getServe_name());
					m.put("img", ms.getService_img());
					m.put("path", ms.getService_img() != null ? ms
							.getService_img().getPath()
							+ "/"
							+ ms.getService_img().getName() : "");
					((List) service_list).add(m);
				}
			}
			List<Map> advance_list = Lists.newArrayList();
			if ((obj.getAdvance_sale_type() == 1)
					&& (obj.getAdvance_sale_info() != null)) {
				advance_list = JSON.parseArray(obj.getAdvance_sale_info(),
						Map.class);
			}
			mv.addObject("advance_list", advance_list);

			int evaluates_count = this.evaluateViewTools.queryByEva(
					obj.getId().toString(), "all").size();
			int eva_count = evaluates_count + falcount;
			mv.addObject("eva_count", Integer.valueOf(eva_count));
			mv.addObject("evaluates_count", Integer.valueOf(evaluates_count));
			mv.addObject("whether_free", Integer.valueOf(obj.getWhether_free()));
			int limit_count = -1;
			if ((obj.getGoods_limit() == 1) && (obj.getGoods_limit_count() > 0)) {
				limit_count = obj.getGoods_limit_count();
				if (current_user != null) {
					String info = CommUtil.null2String(current_user
							.getBuy_goods_limit_info());
					if (!info.equals("")) {
						Map maps = JSON.parseObject(CommUtil.null2String(info));
						List<Map> list = (List) maps.get("data");
						for (Map map : list) {
							String gid = CommUtil.null2String(map.get("gid"));
							if (CommUtil.null2Int(gid) == obj.getId().longValue()) {
								limit_count = obj.getGoods_limit_count()
										- CommUtil.null2Int(map.get("count"));
								if (limit_count < 1) {
									limit_count = 0;
								}
							}
						}
					}
				}
			} else {
				limit_count = -1;
			}
			mv.addObject("limit_count", Integer.valueOf(limit_count));
		} else {
			mv = new RedPigJModelAndView("weixin/error.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "参数错误，商品查看失败");
			mv.addObject("url", CommUtil.getURL(request) + "/index");
		}
		return mv;
	}

	@RequestMapping({ "/goods_list" })
	public ModelAndView goods_list(HttpServletRequest request,
			HttpServletResponse response, String type, String store_id,
			String begin_count, String orderBy, String orderType) {
		ModelAndView mv = new RedPigJModelAndView("weixin/goods_list.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		mv.addObject("orderBy", orderBy);
		mv.addObject("orderType", orderType);
		Map<String,Object> maps = Maps.newHashMap();
		
		if (CommUtil.null2String(orderBy).equals("goods_collect")) {
			maps.put("orderBy", "goods_collect");
			maps.put("orderType", "desc");
		}
		
		if (CommUtil.null2String(orderBy).equals("goods_salenum")) {
			maps.put("orderBy", "goods_salenum");
			maps.put("orderType", "desc");
		}
		
		if (CommUtil.null2String(orderBy).equals("store_price")) {
			maps.put("orderBy", "store_price");
			maps.put("orderType", orderType);
		}
		Map<String,Object> params = Maps.newHashMap();
		if ((type != null) && (type.equals("h"))) {
			maps.put("mobile_hot", Integer.valueOf(1));
			maps.put("goods_status", Integer.valueOf(0));
			
			params.put("mobile_hot", Integer.valueOf(1));
			params.put("goods_status", Integer.valueOf(0));
			params.put("orderBy", "mobile_hotTime");
			params.put("orderType", "desc");
			
			if (!CommUtil.null2String(orderBy).equals("")) {
				List<Goods> goods_hots = this.goodsService.queryPageList(maps,0,6);
				
				mv.addObject("goods", goods_hots);
				mv.addObject("type", "mobile_hot");
			} else {
				List<Goods> goods_hots = this.goodsService.queryPageList(params,0,6);
				mv.addObject("goods", goods_hots);
				mv.addObject("type", "mobile_hot");
			}
		}
		if ((type != null) && (type.equals("r"))) {
			
			maps.put("mobile_recommend", Integer.valueOf(1));
			maps.put("goods_status", Integer.valueOf(0));
			
			params.put("mobile_recommend", Integer.valueOf(1));
			params.put("goods_status", Integer.valueOf(0));
			
			if (!CommUtil.null2String(orderBy).equals("")) {
				List<Goods> goods_recommends = this.goodsService.queryPageList(maps,0,6);
				mv.addObject("goods", goods_recommends);
				mv.addObject("type", "mobile_recommend");
			} else {
				List<Goods> goods_recommends = this.goodsService.queryPageList(params,0,6);
				
				mv.addObject("goods", goods_recommends);
				mv.addObject("type", "mobile_recommend");
			}
		}
		if (!CommUtil.null2String(store_id).equals("")) {
			params.clear();
			params.put("store_id", CommUtil.null2Long(store_id));
			params.put("goods_status", Integer.valueOf(0));
			if (!CommUtil.null2String(orderBy).equals("")) {
				
				List<Goods> store_goods = this.goodsService.queryPageList(params,0,6);
				
				mv.addObject("goods", store_goods);
				mv.addObject("store_id", store_id);
				mv.addObject("type", "store_id");
			} else {
				List<Goods> store_goods = this.goodsService.queryPageList(params,0,6);
				
				mv.addObject("goods", store_goods);
				mv.addObject("store_id", store_id);
				mv.addObject("type", "store_id");
			}
		}
		mv.addObject("goodsViewTools", this.goodsViewTools);
		return mv;
	}

	@RequestMapping({ "/goods_trans_fee" })
	public void goods_trans_fee(HttpServletRequest request,
			HttpServletResponse response, String current_city, String goods_id) {
		boolean verify = CommUtil.null2Boolean(request.getHeader("verify"));
		Map json_map = Maps.newHashMap();
		if (verify) {
			Goods obj = this.goodsService.selectByPrimaryKey(CommUtil
					.null2Long(goods_id));

			String trans_information = "商家承担";
			if (obj.getGoods_transfee() == 0) {
				if (obj.getTransport() != null) {
					String main_info = "平邮(¥"
							+ this.transportTools
									.cal_goods_trans_fee(obj.getTransport()
											.getId().toString(), "mail",
											CommUtil.null2String(obj
													.getGoods_weight()),
											CommUtil.null2String(obj
													.getGoods_volume()),
											current_city);
					String express_info = "快递(¥"
							+ this.transportTools
									.cal_goods_trans_fee(obj.getTransport()
											.getId().toString(), "express",
											CommUtil.null2String(obj
													.getGoods_weight()),
											CommUtil.null2String(obj
													.getGoods_volume()),
											current_city);
					String ems_info = "EMS(¥"
							+ this.transportTools
									.cal_goods_trans_fee(obj.getTransport()
											.getId().toString(), "ems",
											CommUtil.null2String(obj
													.getGoods_weight()),
											CommUtil.null2String(obj
													.getGoods_volume()),
											current_city);
					trans_information = main_info + ") | " + express_info
							+ ") | " + ems_info + ")";
				} else {
					trans_information =

					"平邮(¥" + CommUtil.null2Float(obj.getMail_trans_fee())
							+ ") | " + "快递(¥"
							+ CommUtil.null2Float(obj.getExpress_trans_fee())
							+ ") | " + "EMS(¥"
							+ CommUtil.null2Float(obj.getEms_trans_fee()) + ")";
				}
			}
			json_map.put("trans_information", trans_information);
		} else {
			verify = false;
		}
		json_map.put("ret", CommUtil.null2String(Boolean.valueOf(verify)));
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		try {
			PrintWriter writer = response.getWriter();
			writer.print(JSON.toJSONString(json_map));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@RequestMapping({ "/load_goods_gsp" })
	public void load_goods_gsp(HttpServletRequest request,
			HttpServletResponse response, String gsp, String id) {
		Goods goods = this.goodsService.selectByPrimaryKey(CommUtil.null2Long(id));
		Map<String, Object> map = Maps.newHashMap();
		double price = 0.0D;
		if ((goods.getGroup() != null) && (goods.getGroup_buy() == 2)) {
			for (GroupGoods gg : goods.getGroup_goods_list()) {
				if (gg.getGroup().getId().equals(goods.getGroup().getId())) {
					price = CommUtil.null2Double(gg.getGg_price());
				}
			}
		} else {
			price = CommUtil.null2Double(goods.getStore_price());
			if (goods.getInventory_type().equals("spec")) {
				List<HashMap> list = JSON.parseArray(
						goods.getGoods_inventory_detail(), HashMap.class);
				String[] gsp_ids = gsp.split(",");
				for (Map temp : list) {
					String[] temp_ids = CommUtil.null2String(temp.get("id"))
							.split("_");
					Arrays.sort(gsp_ids);
					Arrays.sort(temp_ids);
					if (Arrays.equals(gsp_ids, temp_ids)) {
						price = CommUtil.null2Double(temp.get("price"));
					}
				}
			}
		}
		// 如果是秒杀，修改显示价格为秒杀价
        if(goods.getNuke_buy()==2&&goods.getNuke()!=null){
            Map<String,Object> para = new HashMap<>();
            para.put("ng_goods_id",goods.getId());
            para.put("nuke_id",goods.getNuke().getId());
            String []spcArray = gsp.split(",");
            String goods_spec_id = "";
            for (String str:spcArray){
                goods_spec_id+=(str+"_");
            }
            para.put("goods_spec_id",goods_spec_id);
            List<NukeGoods>nukeGoodsList = this.nukeGoodsService.selectObjByProperty(para);
            if (nukeGoodsList!=null&&nukeGoodsList.size()>0){
                NukeGoods nukeGoods = nukeGoodsList.get(0);
                price = CommUtil.null2Double(nukeGoods.getNg_price());
            }
        }
		User user = SecurityUserHolder.getCurrentUser();
		if ((goods.getActivity_status() == 2) && (user != null)) {
			Map act_map = this.activityViewTools.getActivityGoodsInfo(id,
					CommUtil.null2String(user.getId()));
			price = CommUtil.null2Double(act_map.get("rate_price"));
		}
		map.put("price", CommUtil.formatMoney(Double.valueOf(price)));
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		try {
			PrintWriter writer = response.getWriter();
			writer.print(JSON.toJSONString(map));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@RequestMapping({ "/goods_introduce" })
	public ModelAndView goods_introduce(HttpServletRequest request,
			HttpServletResponse response, String id) {
		ModelAndView mv = new RedPigJModelAndView("weixin/goods_introduce.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		Goods obj = this.goodsService.selectByPrimaryKey(CommUtil.null2Long(id));
		mv.addObject("obj", obj);
		mv.addObject("id", id);
		return mv;
	}

	@RequestMapping({ "/goods_consult" })
	public ModelAndView goods_consult(HttpServletRequest request,
			HttpServletResponse response, String goods_id, String consult_type,
			String currentPage) {
		ModelAndView mv = new RedPigJModelAndView("default/goods_consult.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		Map<String,Object> maps= this.redPigQueryTools.getParams(currentPage,10, "addTime", "desc");
		
		maps.put("goods_id", CommUtil.null2Long(goods_id));
		
		if (!CommUtil.null2String(consult_type).equals("")) {
			maps.put("consult_type", consult_type);
		}
		
		IPageList pList = this.consultService.list(maps);
		String url2 = CommUtil.getURL(request) + "/goods_consult";
		mv.addObject("consult_objs", pList.getResult());
		mv.addObject("consult_gotoPageAjaxHTML", CommUtil.showPageAjaxHtml(
				url2, "", pList.getCurrentPage(), pList.getPages(),
				pList.getPageSize()));

		mv.addObject("goods_id", goods_id);
		mv.addObject("consultViewTools", this.consultViewTools);
		mv.addObject("consult_type", CommUtil.null2String(consult_type));
		return mv;
	}

	@RequestMapping({ "/goods_consult_save" })
	public ModelAndView goods_consult_saveEntity(HttpServletRequest request,
			HttpServletResponse response, String goods_id,
			String consult_content, String consult_type, String consult_code) {
		String verify_code = CommUtil.null2String(request.getSession(false)
				.getAttribute("consult_code"));
		boolean visit_consult = true;
		if ((!this.configService.getSysConfig().getVisitorConsult())
				&& (SecurityUserHolder.getCurrentUser() == null)) {
			visit_consult = false;
		}
		boolean save_ret = true;
		if ((visit_consult)
				&& (CommUtil.null2String(consult_code).equals(verify_code))) {
			Consult obj = new Consult();
			Goods goods = this.goodsService.selectByPrimaryKey(CommUtil
					.null2Long(goods_id));
			obj.setAddTime(new Date());
			obj.setConsult_type(consult_type);
			obj.setConsult_content(consult_content);
			User user = SecurityUserHolder.getCurrentUser();
			if (user != null) {
				obj.setConsult_user_id(user.getId());
				obj.setConsult_user_name(user.getUserName());
				obj.setConsult_email(user.getEmail());
			} else {
				obj.setConsult_user_name("游客");
			}
			List<Map> maps = Lists.newArrayList();
			Map<String, Object> map = Maps.newHashMap();
			map.put("goods_id", goods.getId());
			map.put("goods_name", goods.getGoods_name());
			map.put("goods_main_photo", goods.getGoods_main_photo().getPath()
					+ "/" + goods.getGoods_main_photo().getName() + "_small."
					+ goods.getGoods_main_photo().getExt());
			map.put("goods_price", goods.getGoods_current_price());
			String goods_domainPath = CommUtil.getURL(request) + "/items_"
					+ goods.getId() + "";
			if ((this.configService.getSysConfig().getSecond_domain_open())
					&& (goods.getGoods_store() != null)
					&& (goods.getGoods_store().getStore_second_domain() != "")
					&& (goods.getGoods_type() == 1)) {
				String store_second_domain = "http://"
						+ goods.getGoods_store().getStore_second_domain() + "."
						+ CommUtil.generic_domain(request);
				goods_domainPath = store_second_domain + "/items_"
						+ goods.getId() + "";
			}
			map.put("goods_domainPath", goods_domainPath);
			maps.add(map);
			obj.setGoods_info(JSON.toJSONString(maps));
			obj.setGoods_id(goods.getId());
			if (goods.getGoods_store() != null) {
				obj.setStore_id(goods.getGoods_store().getId());
				obj.setStore_name(goods.getGoods_store().getStore_name());
			} else {
				obj.setWhether_self(1);
			}
			this.consultService.saveEntity(obj);
			save_ret = true;
			request.getSession(false).removeAttribute("consult_code");
		}
		ModelAndView mv = new RedPigJModelAndView("default/goods_consult.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		
		Map<String,Object> maps= this.redPigQueryTools.getParams("1","addTime","desc");
		maps.put("goods_id", CommUtil.null2Long(goods_id));
		
		if (!CommUtil.null2String(consult_type).equals("")) {
			maps.put("consult_type", consult_type);
		}
		
		IPageList pList = this.consultService.list(maps);
		CommUtil.saveIPageList2ModelAndView(CommUtil.getURL(request)
				+ "/goods_consult.html", "", "", pList, mv);
		mv.addObject("goods_id", goods_id);
		mv.addObject("consultViewTools", this.consultViewTools);
		mv.addObject("consult_type", CommUtil.null2String(consult_type));
		return mv;
	}

	@RequestMapping({ "/recomend_items" })
	public ModelAndView recomend_goods(HttpServletRequest request,
			HttpServletResponse response, String currentPage) {
		ModelAndView mv = new RedPigJModelAndView("weixin/recomend_goods.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		
		Map<String,Object> maps= this.redPigQueryTools.getParams(currentPage,4,"weixin_recommendTime", "desc");
		maps.put("weixin_recommend", 1);
		maps.put("goods_status", 0);
		
		IPageList pList = this.goodsService.list(maps);
		if (pList.getPages() >= CommUtil.null2Int(currentPage)) {
			CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		}
		return mv;
	}

	private void generic_evaluate(Store store, ModelAndView mv) {
		double description_result = 0.0D;
		double service_result = 0.0D;
		double ship_result = 0.0D;
		GoodsClass gc = this.goodsClassService
				.selectByPrimaryKey(store.getGc_main_id());
		if ((store != null) && (gc != null) && (store.getPoint() != null)) {
			float description_evaluate = CommUtil.null2Float(gc
					.getDescription_evaluate());
			float service_evaluate = CommUtil.null2Float(gc
					.getService_evaluate());
			float ship_evaluate = CommUtil.null2Float(gc.getShip_evaluate());

			float store_description_evaluate = CommUtil.null2Float(store
					.getPoint().getDescription_evaluate());
			float store_service_evaluate = CommUtil.null2Float(store.getPoint()
					.getService_evaluate());
			float store_ship_evaluate = CommUtil.null2Float(store.getPoint()
					.getShip_evaluate());

			description_result = CommUtil.div(
					Float.valueOf(store_description_evaluate
							- description_evaluate),
					Float.valueOf(description_evaluate));
			service_result = CommUtil.div(
					Float.valueOf(store_service_evaluate - service_evaluate),
					Float.valueOf(service_evaluate));
			ship_result = CommUtil.div(
					Float.valueOf(store_ship_evaluate - ship_evaluate),
					Float.valueOf(ship_evaluate));
		}
		if (description_result > 0.0D) {
			mv.addObject("description_css", "value_strong");
			mv.addObject(
					"description_result",

					CommUtil.null2String(Double.valueOf(CommUtil.mul(
							Double.valueOf(description_result),
							Integer.valueOf(100)) > 100.0D ? 100.0D : CommUtil
							.mul(Double.valueOf(description_result),
									Integer.valueOf(100))))
							+ "%");
		}
		if (description_result == 0.0D) {
			mv.addObject("description_css", "value_normal");
			mv.addObject("description_result", "-----");
		}
		if (description_result < 0.0D) {
			mv.addObject("description_css", "value_light");
			mv.addObject(
					"description_result",
					CommUtil.null2String(Double.valueOf(CommUtil.mul(
							Double.valueOf(-description_result),
							Integer.valueOf(100))))
							+ "%");
		}
		if (service_result > 0.0D) {
			mv.addObject("service_css", "value_strong");
			mv.addObject(
					"service_result",
					CommUtil.null2String(Double.valueOf(CommUtil.mul(
							Double.valueOf(service_result),
							Integer.valueOf(100))))
							+ "%");
		}
		if (service_result == 0.0D) {
			mv.addObject("service_css", "value_normal");
			mv.addObject("service_result", "-----");
		}
		if (service_result < 0.0D) {
			mv.addObject("service_css", "value_light");
			mv.addObject(
					"service_result",
					CommUtil.null2String(Double.valueOf(CommUtil.mul(
							Double.valueOf(-service_result),
							Integer.valueOf(100))))
							+ "%");
		}
		if (ship_result > 0.0D) {
			mv.addObject("ship_css", "value_strong");
			mv.addObject(
					"ship_result",
					CommUtil.null2String(Double.valueOf(CommUtil.mul(
							Double.valueOf(ship_result), Integer.valueOf(100))))
							+ "%");
		}
		if (ship_result == 0.0D) {
			mv.addObject("ship_css", "value_normal");
			mv.addObject("ship_result", "-----");
		}
		if (ship_result < 0.0D) {
			mv.addObject("ship_css", "value_light");
			mv.addObject(
					"ship_result",
					CommUtil.null2String(Double.valueOf(CommUtil.mul(
							Double.valueOf(-ship_result), Integer.valueOf(100))))
							+ "%");
		}
	}

	@RequestMapping({ "/goods_list_bottom" })
	public ModelAndView goods_list_bottom(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView mv = new RedPigJModelAndView("weixin/goods_list_bottom.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);

		List<Goods> your_like_goods = Lists.newArrayList();
		Long your_like_GoodsClass = null;
		Cookie[] cookies = request.getCookies();
		Goods goods;
		int gcs_size;
		List<Goods> like_goods;
		if (cookies != null) {
			
			
			for (Cookie cookie : cookies) {
				
				if (cookie.getName().equals("goodscookie")) {
					String[] like_gcid = URLDecoder.decode(cookie.getValue(), "UTF-8").split(",", 2);
					goods = this.goodsService.selectByPrimaryKey(CommUtil
							.null2Long(like_gcid[0]));
					if (goods == null) {
						break;
					}
					your_like_GoodsClass = goods.getGc().getId();
					Map<String,Object> maps = Maps.newHashMap();
					maps.put("goods_status", 0);
					maps.put("gc_id", your_like_GoodsClass);
					maps.put("id_no", goods.getId());
					maps.put("orderBy", "goods_salenum");
					maps.put("orderType", "desc");
					
					your_like_goods = this.goodsService.queryPageList(maps, 0,6);
					
					gcs_size = your_like_goods.size();
					if (gcs_size >= 6) {
						break;
					}
					maps.clear();
					maps.put("goods_status", 0);
					
					maps.put("id_no", goods.getId());
					maps.put("orderBy", "goods_salenum");
					maps.put("orderType", "desc");
					
					
					like_goods = this.goodsService.queryPageList(maps,0, 6 - gcs_size);
					
					for (int i = 0; i < like_goods.size(); i++) {
						int k = 0;
						for (int j = 0; j < your_like_goods.size(); j++) {
							if (((Goods) like_goods.get(i)).getId().equals(
									((Goods) your_like_goods.get(j)).getId())) {
								k++;
							}
						}
						if (k == 0) {
							your_like_goods.add((Goods) like_goods.get(i));
						}
					}
					break;
				}
				Map<String,Object> maps = Maps.newHashMap();
				maps.put("goods_status", 0);
				maps.put("orderBy", "goods_salenum");
				maps.put("orderType", "desc");
				
				your_like_goods = this.goodsService.queryPageList(maps, 0, 6);
				
			}
		} else {
			Map<String,Object> maps = Maps.newHashMap();
			maps.put("goods_status", 0);
			maps.put("orderBy", "goods_salenum");
			maps.put("orderType", "desc");
			
			
			your_like_goods = this.goodsService.queryPageList(maps, 0, 6);
		}
		mv.addObject("your_like_goods", your_like_goods);
		List<Goods> goods_last = Lists.newArrayList();
		Cookie[] cookies_last = request.getCookies();
		Map<String,Object> params = Maps.newHashMap();
		
		Set<Long> ids = Sets.newHashSet();
		
		if (cookies_last != null) {
			for (Cookie co : cookies_last) {
				if (co.getName().equals("goodscookie")) {
					String[] goods_id = co.getValue().split(",");
					int j = 4;
					if (j > goods_id.length) {
						j = goods_id.length;
					}
					for (int i = 0; i < j; i++) {
						ids.add(CommUtil.null2Long(goods_id[i]));
					}
				}
			}
		}
		if (!ids.isEmpty()) {
			params.put("ids", ids);
			
			goods_last = this.goodsService.queryPageList(params);
			
		}
		mv.addObject("goods_last", goods_last);
		return mv;
	}
}
