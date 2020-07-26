package com.redpigmall.manage.seller.action;

import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.redpigmall.api.annotation.SecurityMapping;
import com.redpigmall.api.mv.RedPigJModelAndView;
import com.redpigmall.api.sec.SecurityUserHolder;
import com.redpigmall.api.tools.CommUtil;
import com.redpigmall.domain.GoodsBrand;
import com.redpigmall.domain.GoodsClass;
import com.redpigmall.domain.GoodsLog;
import com.redpigmall.domain.User;
import com.redpigmall.manage.seller.action.base.BaseAction;

/**
 * 
 * <p>
 * Title: RedPigGoodsStatSellerAction.java
 * </p>
 * 
 * <p>
 * Description: 店铺统计统计。只统计店铺统计商品
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
 * @date 2014-11-20
 * 
 * @version redpigmall_b2b2c 8.0
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
@Controller
public class RedPigGoodsStatSellerAction extends BaseAction{
	/**
	 * 商品统计
	 * @param request
	 * @param response
	 * @return
	 */
	@SecurityMapping(title = "商品统计", value = "/stat_items*", rtype = "seller", rname = "商品统计", rcode = "seller_stat_goods", rgroup = "店铺统计")
	@RequestMapping({ "/stat_items" })
	public ModelAndView stat_goods(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/sellercenter/stat_goods.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		Map<String,Object> maps = Maps.newHashMap();
        maps.put("orderBy", "sequence");
        maps.put("orderType", "asc");

		List<GoodsBrand> gbs = this.goodsBrandService.queryPageList(maps);
		maps.put("parent", -1);
		
		List<GoodsClass> gcs = this.goodsClassService.queryPageList(maps);
		
		mv.addObject("gcs", gcs);
		mv.addObject("gbs", gbs);
		mv.addObject("self", Integer.valueOf(1));
		return mv;
	}
	
	/**
	 * 有信息的商品
	 * @param request
	 * @param response
	 * @param beginTime
	 * @param endTime
	 * @param currentPage
	 * @param goods_name
	 * @param class_id
	 * @param brand_id
	 * @return
	 */
	@SecurityMapping(title = "有信息的商品", value = "/stat_goods_list*", rtype = "seller", rname = "商品统计", rcode = "seller_stat_goods", rgroup = "店铺统计")
	@RequestMapping({ "/stat_goods_list" })
	public ModelAndView stat_goods_list(HttpServletRequest request,
			HttpServletResponse response, String beginTime, String endTime,
			String currentPage, String goods_name, String class_id,
			String brand_id) {
		ModelAndView mv = null;
		if ((endTime != null) && (beginTime != null) && (!endTime.equals(""))
				&& (!beginTime.equals(""))) {
			mv = new RedPigJModelAndView(
					"user/default/sellercenter/stat_goods_list.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request,
					response);

			Date begin = CommUtil.formatDate(beginTime);
			Date end = CommUtil.formatDate(endTime);
			Map<String,Object> para = Maps.newHashMap();
			para.put("add_Time_more_than_equal", begin);
			para.put("add_Time_less_than_equal", end);
			para.put("store_id", SecurityUserHolder.getCurrentUser().getStore().getId());
			para.put("log_form", 1);
			
			if ((goods_name != null) && (!goods_name.equals(""))) {
				para.put("goods_name_like", goods_name);
			}
			
			if ((class_id != null) && (!class_id.equals(""))) {
				Set<Long> ids = genericIds(this.goodsClassService.selectByPrimaryKey(CommUtil.null2Long(class_id)));
				para.put("gc_id_ids", ids);
			}
			
			if ((brand_id != null) && (!brand_id.equals(""))) {
				para.put("goods_brand_id", CommUtil.null2Long(brand_id));
			}
			
			para.put("group_by", "goods_id");
			
			int length =  this.goodsLogService.selectCount(para);
			int pagesize = 18;

			List<GoodsLog>  newlist = null;

			int nowpage = CommUtil.null2Int(currentPage);
			int cp = nowpage;
			int pages = (int) Math.ceil(length / pagesize);
			if (nowpage == 0) {
				cp = 1;
				newlist = this.goodsLogService.queryPageList(para,0,pagesize);
			} else if (nowpage > pages) {
				cp = pages;
				newlist = this.goodsLogService.queryPageList(para, (pages - 1) * pagesize, pagesize);
			} else {
				newlist = this.goodsLogService.queryPageList(para, (nowpage - 1) * pagesize, pagesize);
			}
			mv.addObject("imageTools", this.imageTools);
			String url = CommUtil.getURL(request) + "/stat_goods_list";
			mv.addObject("goodslog_list", newlist);
			mv.addObject("goodslog_list_gotoPageAjaxHTML",
					CommUtil.showPageAjaxHtml(url, "", cp, pages, pagesize));
		}
		return mv;
	}
	
	/**
	 * 商品统计结果
	 * @param request
	 * @param response
	 * @param beginTime
	 * @param endTime
	 * @param ids
	 * @param statType
	 * @return
	 */
	@SecurityMapping(title = "商品统计结果", value = "/stat_goods_done*", rtype = "seller", rname = "商品统计", rcode = "seller_stat_goods", rgroup = "店铺统计")
	@RequestMapping({ "/stat_goods_done" })
	public ModelAndView stat_goods_done(HttpServletRequest request,
			HttpServletResponse response, String beginTime, String endTime,
			String ids, String statType) {
		ModelAndView mv = null;
		if ((ids != null) && (!ids.equals(""))) {
			String[] arr = ids.split(",");
			Date begin = CommUtil.formatDate(beginTime);
			Date end = CommUtil.formatDate(endTime);
			Map<String, Object> map = CommUtil.cal_time_space(begin, end);
			int day = CommUtil.null2Int(map.get("day"));
			if (day > 90) {
				statType = "bymonth";
			}
			int increaseType = 6;
			String timeFormater = "MM月dd日";
			if (statType.equals("bymonth")) {
				increaseType = 2;
				timeFormater = "yy年MM月";
			}
			int allclick = 0;
			int allsale = 0;
			if (arr.length == 1) {
				mv = new RedPigJModelAndView(
						"user/default/sellercenter/stat_single_goods_result.html",
						this.configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 0, request,
						response);
				if ((begin != null) && (end != null)) {
					Calendar cal = Calendar.getInstance();
					cal.setTime(begin);

					Map goodinfo = Maps.newHashMap();
					List<Map> stat = Lists.newArrayList();

					List times = Lists.newArrayList();
					List goods_click = Lists.newArrayList();
					List goods_collect = Lists.newArrayList();
					List goods_salenum = Lists.newArrayList();

					Map<String, Integer> goods_clickfrom = Maps.newHashMap();
					Map<String, Integer> goods_ordertype = Maps.newHashMap();
					Map<String, Integer> goods_spectype = Maps.newHashMap();

					Map<String, Object> params = Maps.newHashMap();
					params.put("goods_id", CommUtil.null2Long(ids));
					List<GoodsLog> log = null;
					while (!cal.getTime().after(end)) {
						Map statmap = Maps.newHashMap();
						params.put("add_Time_more_than_equal", cal.getTime());
						times.add(CommUtil.formatTime(timeFormater,
								cal.getTime()));
						statmap.put("time", CommUtil.formatTime(timeFormater,
								cal.getTime()));
						if (statType.equals("bymonth")) {
							cal.set(5, 1);
						}
						cal.add(increaseType, 1);
						if (statType.equals("bymonth")) {
							if (cal.getTime().before(end)) {
								params.put("add_Time_less_than_equal", cal.getTime());
							} else {
								cal.setTime(end);
								cal.add(6, 1);
								params.put("add_Time_less_than_equal", cal.getTime());
							}
						} else {
							params.put("add_Time_less_than_equal", cal.getTime());
						}
						log = this.goodsLogService.queryPageList(params);
						
						int clicknum = 0;
						int collectnum = 0;
						int salenum = 0;
						double price = 0.0D;
						List preferential = Lists.newArrayList();
						for (GoodsLog gl : log) {
							if (!goodinfo.containsKey("info")) {
								goodinfo.put("info", gl);
							}
							clicknum += gl.getGoods_click();
							allclick += gl.getGoods_click();
							collectnum += gl.getGoods_collect();
							salenum += gl.getGoods_salenum();
							allsale += gl.getGoods_salenum();
							price = price == 0.0D ? CommUtil.null2Double(gl
									.getPrice()) : (price + CommUtil
									.null2Double(gl.getPrice())) / 2.0D;
							if (!gl.getPreferential().equals("")) {
								if (preferential.size() == 0) {
									Map todaytpre = Maps.newHashMap();
									todaytpre.put(gl.getPreferential(),
											gl.getPreferential_info());
									preferential.add(todaytpre);
								} else {
									Map temppre = (Map) preferential
											.get(preferential.size() - 1);
									if ((!temppre.containsKey(gl
											.getPreferential()))
											||

											(!temppre
													.get(gl.getPreferential())
													.equals(gl
															.getPreferential_info()))) {
										Map todaytpre = Maps.newHashMap();
										todaytpre.put(gl.getPreferential(),
												gl.getPreferential_info());
										preferential.add(todaytpre);
									}
								}
							}
							String jsonstr = gl.getGoods_click_from();
							if ((jsonstr != null) && (!jsonstr.equals(""))) {
								Map<String, Integer> clickmap = (Map) JSON
										.parseObject(jsonstr, Map.class);
								for (String key : clickmap.keySet()) {
									if (goods_clickfrom.containsKey(key)) {
										goods_clickfrom
												.put(key,
														Integer.valueOf(((Integer) goods_clickfrom
																.get(key))
																.intValue()
																+ ((Integer) clickmap
																		.get(key))
																		.intValue()));
									} else {
										goods_clickfrom.put(key,
												(Integer) clickmap.get(key));
									}
								}
							}
							jsonstr = gl.getGoods_order_type();
							if ((jsonstr != null) && (!jsonstr.equals(""))) {
								Map<String, Integer> ordermap = (Map) JSON
										.parseObject(jsonstr, Map.class);

								for (String key : ordermap.keySet()) {
									String from = "PC网页";
									if (key.equals("weixin")) {
										from = "手机网页";
									}
									if (key.equals("android")) {
										from = "Android客户端";
									}
									if (key.equals("ios")) {
										from = "iOS客户端";
									}
									if (goods_ordertype.containsKey(from)) {
										goods_ordertype
												.put(from,
														Integer.valueOf(((Integer) goods_ordertype
																.get(from))
																.intValue()
																+ ((Integer) ordermap
																		.get(key))
																		.intValue()));
									} else {
										goods_ordertype.put(from,
												(Integer) ordermap.get(key));
									}
								}
							}
							jsonstr = gl.getGoods_sale_info();
							if ((jsonstr == null) || (jsonstr.equals(""))) {

							}
							Map<String, Integer> specmap = (Map) JSON
									.parseObject(jsonstr, Map.class);
							for (String key : specmap.keySet()) {

								if (goods_spectype.containsKey(key)) {
									goods_spectype.put(key, Integer
											.valueOf(((Integer) goods_spectype
													.get(key)).intValue()
													+ ((Integer) specmap
															.get(key))
															.intValue()));
								} else {
									goods_spectype.put(key,
											(Integer) specmap.get(key));
								}
							}
						}
						statmap.put("clicknum", Integer.valueOf(clicknum));
						statmap.put("collectnum", Integer.valueOf(collectnum));
						statmap.put("salenum", Integer.valueOf(salenum));
						if (price > 0.0D) {
							statmap.put("price",
									CommUtil.formatMoney(Double.valueOf(price)));
						} else {
							statmap.put("price", "无记录");
						}
						statmap.put("preferential", preferential);
						stat.add(statmap);
						goods_click.add(Integer.valueOf(clicknum));
						goods_collect.add(Integer.valueOf(collectnum));
						goods_salenum.add(Integer.valueOf(salenum));
					}
					Iterator<String> it = goods_clickfrom.keySet().iterator();
					StringBuilder clicksb = new StringBuilder();
					while (it.hasNext()) {
						String string = (String) it.next();
						clicksb.append("['").append(string).append("',")
								.append(goods_clickfrom.get(string))
								.append("],");
					}
					it = goods_ordertype.keySet().iterator();
					StringBuilder ordersb = new StringBuilder();
					while (it.hasNext()) {
						String string = (String) it.next();
						ordersb.append("['").append(string).append("',")
								.append(goods_ordertype.get(string))
								.append("],");
					}
					it = goods_spectype.keySet().iterator();
					StringBuilder specsb = new StringBuilder();
					while (it.hasNext()) {
						String string = (String) it.next();
						if (string.equals("")) {
							specsb.append("['").append("默认规格").append("',")
									.append(goods_spectype.get(string))
									.append("],");
						} else {
							specsb.append("['").append(string).append("',")
									.append(goods_spectype.get(string))
									.append("],");
						}
					}
					mv.addObject("allclick", Integer.valueOf(allclick));
					mv.addObject("allsale", Integer.valueOf(allsale));

					mv.addObject("begin", begin);
					mv.addObject("end", end);

					mv.addObject("goodinfo", goodinfo);
					mv.addObject("objs", stat);
					mv.addObject("imageTools", this.imageTools);

					mv.addObject("stat_title", "商城商品统计图");
					mv.addObject("times", JSON.toJSONString(times));
					mv.addObject("timeslength", Integer.valueOf(CommUtil
							.null2Int(Integer.valueOf(times.size() / 8))));
					mv.addObject("goods_click", JSON.toJSONString(goods_click));
					mv.addObject("goods_collect",
							JSON.toJSONString(goods_collect));
					mv.addObject("goods_salenum",
							JSON.toJSONString(goods_salenum));
					mv.addObject("goods_clickfrom", clicksb);
					mv.addObject("goods_ordertype", ordersb);
					mv.addObject("goods_spectype", specsb);
				}
			} else {
				mv = new RedPigJModelAndView(
						"user/default/sellercenter/stat_multi_goods_result.html",
						this.configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 0, request,
						response);
				if ((begin != null) && (end != null)) {
					Calendar cal = Calendar.getInstance();
					cal.setTime(begin);

					Map<Long, String> goods = Maps.newHashMap();

					Map<Date, Map<String, Map>> stat = new TreeMap();

					List times = Lists.newArrayList();
					Map<Long, List> goods_click = Maps.newHashMap();
					Map<Long, List> goods_collect = Maps.newHashMap();
					Map<Long, List> goods_salenum = Maps.newHashMap();

					Map<String, Integer> goods_clickfrom = Maps.newHashMap();
					Map<String, Integer> goods_ordertype = Maps.newHashMap();

					Map<Long, Integer> clickcount = Maps.newHashMap();
					Map<Long, Integer> salecount = Maps.newHashMap();

					Map<String, Object> params = Maps.newHashMap();
					List<GoodsLog> log = null;
					label3273: while (!cal.getTime().after(end)) {
						Date calday = cal.getTime();
						times.add(CommUtil.formatTime(timeFormater, calday));

						params.put("add_Time_more_than_equal", calday);
						if (statType.equals("bymonth")) {
							cal.set(5, 1);
						}
						cal.add(increaseType, 1);
						if (statType.equals("bymonth")) {
							if (cal.getTime().before(end)) {
								params.put("add_Time_less_than_equal", cal.getTime());
							} else {
								cal.setTime(end);
								cal.add(6, 1);
								params.put("add_Time_less_than_equal", cal.getTime());
							}
						} else {
							params.put("add_Time_less_than_equal", cal.getTime());
						}
						Map idgoodmap = Maps.newHashMap();
						for (String id : arr) {
							params.put("goods_id", CommUtil.null2Long(id));
							log = this.goodsLogService.queryPageList(params);
							
							long longid = CommUtil.null2Long(id).longValue();
							int clicknum = 0;
							int collectnum = 0;
							int salenum = 0;
							double price = 0.0D;
							List preferential = Lists.newArrayList();

							for (GoodsLog gl : log) {

								long tempid = CommUtil.null2Long(id)
										.longValue();
								if (!goods.containsKey(id)) {
									goods.put(Long.valueOf(tempid),
											gl.getGoods_name());
								}
								if (clickcount
										.containsKey(Long.valueOf(tempid))) {
									clickcount
											.put(Long.valueOf(tempid),
													Integer.valueOf(((Integer) clickcount.get(Long
															.valueOf(tempid)))
															.intValue()
															+ gl.getGoods_click()));
								} else {
									clickcount
											.put(Long.valueOf(tempid), Integer
													.valueOf(gl
															.getGoods_click()));
								}
								if (salecount.containsKey(Long.valueOf(tempid))) {
									salecount.put(Long.valueOf(tempid), Integer
											.valueOf(((Integer) salecount
													.get(Long.valueOf(tempid)))
													.intValue()
													+ gl.getGoods_salenum()));
								} else {
									salecount.put(Long.valueOf(tempid), Integer
											.valueOf(gl.getGoods_salenum()));
								}
								clicknum += gl.getGoods_click();
								allclick += gl.getGoods_click();
								collectnum += gl.getGoods_collect();
								salenum += gl.getGoods_salenum();
								allsale += gl.getGoods_salenum();
								price = price == 0.0D ? CommUtil.null2Double(gl
										.getPrice()) : (price + CommUtil
										.null2Double(gl.getPrice())) / 2.0D;
								if (!gl.getPreferential().equals("")) {
									if (preferential.size() == 0) {
										Map todaytpre = Maps.newHashMap();
										todaytpre.put(gl.getPreferential(),
												gl.getPreferential_info());
										preferential.add(todaytpre);
									} else {
										Map temppre = (Map) preferential
												.get(preferential.size() - 1);
										if ((!temppre.containsKey(gl
												.getPreferential()))
												||

												(!temppre
														.get(gl.getPreferential())
														.equals(gl
																.getPreferential_info()))) {
											Map todaytpre = Maps.newHashMap();
											todaytpre.put(gl.getPreferential(),
													gl.getPreferential_info());
											preferential.add(todaytpre);
										}
									}
								}
								String jsonstr = gl.getGoods_click_from();
								if ((jsonstr != null) && (!jsonstr.equals(""))) {
									Map<String, Integer> clickmap = (Map) JSON
											.parseObject(jsonstr, Map.class);
									for (String key : clickmap.keySet()) {

										if (goods_clickfrom.containsKey(key)) {
											goods_clickfrom
													.put(key,
															Integer.valueOf(((Integer) goods_clickfrom
																	.get(key))
																	.intValue()
																	+

																	((Integer) clickmap
																			.get(key))
																			.intValue()));
										} else {
											goods_clickfrom
													.put(key,
															(Integer) clickmap
																	.get(key));
										}
									}
								}
								jsonstr = gl.getGoods_order_type();
								if ((jsonstr == null) || (jsonstr.equals(""))) {
									break label3273;
								}
								Map<String, Integer> ordermap = (Map) JSON
										.parseObject(jsonstr, Map.class);

								for (String key : ordermap.keySet()) {

									String from = "PC网页";
									if (key.equals("weixin")) {
										from = "手机网页";
									}
									if (key.equals("android")) {
										from = "Android客户端";
									}
									if (key.equals("ios")) {
										from = "iOS客户端";
									}
									if (goods_ordertype.containsKey(from)) {
										goods_ordertype
												.put(from,
														Integer.valueOf(((Integer) goods_ordertype
																.get(from))
																.intValue()
																+

																((Integer) ordermap
																		.get(key))
																		.intValue()));
									} else {
										goods_ordertype.put(from,
												(Integer) ordermap.get(key));
									}
								}
							}
							Map statmap = Maps.newHashMap();
							statmap.put("clicknum", Integer.valueOf(clicknum));
							statmap.put("collectnum",
									Integer.valueOf(collectnum));
							statmap.put("salenum", Integer.valueOf(salenum));
							if (price > 0.0D) {
								statmap.put("price", CommUtil
										.formatMoney(Double.valueOf(price)));
							} else {
								statmap.put("price", "无记录");
							}
							statmap.put("preferential", preferential);

							idgoodmap.put(id, statmap);
							if (goods_click.containsKey(Long.valueOf(longid))) {
								((List) goods_click.get(Long.valueOf(longid)))
										.add(Integer.valueOf(clicknum));
							} else {
								List list = Lists.newArrayList();
								list.add(Integer.valueOf(clicknum));
								goods_click.put(Long.valueOf(longid), list);
							}
							if (goods_collect.containsKey(Long.valueOf(longid))) {
								((List) goods_collect.get(Long.valueOf(longid)))
										.add(Integer.valueOf(collectnum));
							} else {
								List list = Lists.newArrayList();
								list.add(Integer.valueOf(collectnum));
								goods_collect.put(Long.valueOf(longid), list);
							}
							if (goods_salenum.containsKey(Long.valueOf(longid))) {
								((List) goods_salenum.get(Long.valueOf(longid)))
										.add(Integer.valueOf(salenum));
							} else {
								List list = Lists.newArrayList();
								list.add(Integer.valueOf(salenum));
								goods_salenum.put(Long.valueOf(longid), list);
							}
						}
						stat.put(calday, idgoodmap);
					}
					Iterator<String> it = goods_clickfrom.keySet().iterator();
					StringBuilder clicksb = new StringBuilder();
					while (it.hasNext()) {
						String string = (String) it.next();
						clicksb.append("['").append(string).append("',")
								.append(goods_clickfrom.get(string))
								.append("],");
					}
					it = goods_ordertype.keySet().iterator();
					StringBuilder ordersb = new StringBuilder();
					while (it.hasNext()) {
						String string = (String) it.next();
						ordersb.append("['").append(string).append("',")
								.append(goods_ordertype.get(string))
								.append("],");
					}
					mv.addObject("allclick", Integer.valueOf(allclick));
					mv.addObject("allsale", Integer.valueOf(allsale));

					mv.addObject("clicksort", sort(clickcount, goods));
					mv.addObject("salesort", sort(salecount, goods));

					mv.addObject("begin", begin);
					mv.addObject("end", end);

					mv.addObject("goods", goods);
					mv.addObject("objs", stat);

					mv.addObject("times", JSON.toJSONString(times));
					mv.addObject("timeslength", Integer.valueOf(CommUtil
							.null2Int(Integer.valueOf(times.size() / 8))));
					mv.addObject("stat_title", "商城商品统计图");
					mv.addObject("goods_click", goods_click);
					mv.addObject("goods_collect", goods_collect);
					mv.addObject("goods_salenum", goods_salenum);
					mv.addObject("goods_clickfrom", clicksb);
					mv.addObject("goods_ordertype", ordersb);
				}
			}
		}
		return mv;
	}
	
	/**
	 * 全商城统计结果
	 * @param request
	 * @param response
	 * @param beginTime
	 * @param endTime
	 * @param goods_name
	 * @param class_id
	 * @param brand_id
	 * @param statType
	 * @return
	 */
	@SecurityMapping(title = "全商城统计结果", value = "/stat_all*", rtype = "seller", rname = "商品统计", rcode = "seller_stat_goods", rgroup = "店铺统计")
	@RequestMapping({ "/stat_all" })
	public ModelAndView stat_all(HttpServletRequest request,
			HttpServletResponse response, String beginTime, String endTime,
			String goods_name, String class_id, String brand_id, String statType) {
		ModelAndView mv = null;
		Date begin = CommUtil.formatDate(beginTime);
		Date end = CommUtil.formatDate(endTime);
		Map<String, Object> map = CommUtil.cal_time_space(begin, end);
		int day = CommUtil.null2Int(map.get("day"));
		if (day > 90) {
			statType = "bymonth";
		}
		int increaseType = 6;
		String timeFormater = "MM月dd日";
		if (statType.equals("bymonth")) {
			increaseType = 2;
			timeFormater = "yy年MM月";
		}
		mv = new RedPigJModelAndView(
				"user/default/sellercenter/stat_all_goods_result.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		if ((begin != null) && (end != null)) {
			List<Map> stat = Lists.newArrayList();

			List times = Lists.newArrayList();
			List<Integer> goods_click = Lists.newArrayList();
			List<Integer> goods_collect = Lists.newArrayList();
			List<Integer> goods_salenum = Lists.newArrayList();

			Map<String, Integer> goods_clickfrom = Maps.newHashMap();
			Map<String, Integer> goods_ordertype = Maps.newHashMap();

			Map<Long, String> goodsnames = Maps.newHashMap();
			Map<Long, Integer> clickcount = Maps.newHashMap();
			Map<Long, Integer> salecount = Maps.newHashMap();

			int allclick = 0;
			int allsale = 0;

			Calendar cal = Calendar.getInstance();
			cal.setTime(begin);

			Map<String, Object> params = Maps.newHashMap();
			User user = this.userService.selectByPrimaryKey(SecurityUserHolder
					.getCurrentUser().getId());
			user = user.getParent() == null ? user : user.getParent();
			params.put("store_id", user.getStore().getId());
			List<GoodsLog> log = null;
			while (!cal.getTime().after(end)) {
				Date today = cal.getTime();
				params.put("add_Time_more_than_equal", today);
				times.add(CommUtil.formatTime(timeFormater, today));
				Map<String,Object> todaymap = Maps.newHashMap();
				todaymap.put("date", CommUtil.formatTime(timeFormater, today));
				if (statType.equals("bymonth")) {
					cal.set(5, 1);
				}
				cal.add(increaseType, 1);
				if (statType.equals("bymonth")) {
					if (cal.getTime().before(end)) {
						params.put("add_Time_less_than_equal", cal.getTime());
					} else {
						cal.setTime(end);
						cal.add(6, 1);
						params.put("add_Time_less_than_equal", cal.getTime());
					}
				} else {
					params.put("add_Time_less_than_equal", cal.getTime());
				}
				
				if ((goods_name != null) && (!goods_name.equals(""))) {
					params.put("goods_name_like", goods_name);
				}
				
				if ((class_id != null) && (!class_id.equals(""))) {
					Set<Long> ids = genericIds(this.goodsClassService.selectByPrimaryKey(CommUtil.null2Long(class_id)));
					params.put("gc_id_ids", ids);
				}
				
				if ((brand_id != null) && (!brand_id.equals(""))) {
					params.put("goods_brand_id", CommUtil.null2Long(brand_id));
				}
				
				log = this.goodsLogService.queryPageList(params);
				
				int click = 0;
				int collect = 0;
				int sale = 0;

				for (GoodsLog gl : log) {

					click += gl.getGoods_click();
					allclick += gl.getGoods_click();
					collect += gl.getGoods_collect();
					sale += gl.getGoods_salenum();
					allsale += gl.getGoods_salenum();

					long id = gl.getGoods_id().longValue();
					if (clickcount.containsKey(Long.valueOf(id))) {
						clickcount.put(Long.valueOf(id), Integer
								.valueOf(((Integer) clickcount.get(Long
										.valueOf(id))).intValue()
										+ gl.getGoods_click()));
					} else {
						clickcount.put(Long.valueOf(id),
								Integer.valueOf(gl.getGoods_click()));
					}
					if (salecount.containsKey(Long.valueOf(id))) {
						salecount.put(Long.valueOf(id), Integer
								.valueOf(((Integer) salecount.get(Long
										.valueOf(id))).intValue()
										+ gl.getGoods_salenum()));
					} else {
						salecount.put(Long.valueOf(id),
								Integer.valueOf(gl.getGoods_salenum()));
					}
					if (!goodsnames.containsKey(Long.valueOf(id))) {
						goodsnames.put(Long.valueOf(id), gl.getGoods_name());
					}

					String jsonstr = gl.getGoods_click_from();
					if ((jsonstr != null) && (!jsonstr.equals(""))) {
						Map<String, Integer> clickmap = (Map) JSON.parseObject(
								jsonstr, Map.class);

						for (String key : clickmap.keySet()) {

							if (goods_clickfrom.containsKey(key)) {
								goods_clickfrom.put(key, Integer
										.valueOf(((Integer) goods_clickfrom
												.get(key)).intValue()
												+ ((Integer) clickmap.get(key))
														.intValue()));
							} else {
								goods_clickfrom.put(key,
										(Integer) clickmap.get(key));
							}
						}
					}
					jsonstr = gl.getGoods_order_type();
					if ((jsonstr == null) || (jsonstr.equals(""))) {
						Map<String, Integer> ordermap = (Map) JSON.parseObject(
								jsonstr, Map.class);

						for (String key : ordermap.keySet()) {

							String from = "PC网页";
							if (key.equals("weixin")) {
								from = "手机网页";
							}
							if (key.equals("android")) {
								from = "Android客户端";
							}
							if (key.equals("ios")) {
								from = "iOS客户端";
							}
							if (goods_ordertype.containsKey(from)) {
								goods_ordertype.put(from, Integer
										.valueOf(((Integer) goods_ordertype
												.get(from)).intValue()
												+ ((Integer) ordermap.get(key))
														.intValue()));
							} else {
								goods_ordertype.put(from,
										(Integer) ordermap.get(key));
							}
						}
					}
					
				}
				goods_click.add(Integer.valueOf(click));
				goods_collect.add(Integer.valueOf(collect));
				goods_salenum.add(Integer.valueOf(sale));

				todaymap.put("click", Integer.valueOf(click));
				todaymap.put("collect", Integer.valueOf(collect));
				todaymap.put("sale", Integer.valueOf(sale));
				stat.add(todaymap);
			}
			Iterator<String> it = goods_clickfrom.keySet().iterator();
			StringBuilder clicksb = new StringBuilder();
			while (it.hasNext()) {
				String string = (String) it.next();
				clicksb.append("['").append(string).append("',")
						.append(goods_clickfrom.get(string)).append("],");
			}
			it = goods_ordertype.keySet().iterator();
			StringBuilder ordersb = new StringBuilder();
			while (it.hasNext()) {
				String string = (String) it.next();
				ordersb.append("['").append(string).append("',")
						.append(goods_ordertype.get(string)).append("],");
			}
			mv.addObject("allclick", Integer.valueOf(allclick));
			mv.addObject("allsale", Integer.valueOf(allsale));

			mv.addObject("clicksort", sort(clickcount, goodsnames));
			mv.addObject("salesort", sort(salecount, goodsnames));

			mv.addObject("stat", stat);
			mv.addObject("stat_title", "商城总体统计图");
			mv.addObject("begin", begin);
			mv.addObject("times", JSON.toJSONString(times));
			mv.addObject("timeslength", Integer.valueOf(CommUtil
					.null2Int(Integer.valueOf(times.size() / 8))));
			mv.addObject("goods_click", JSON.toJSONString(goods_click));
			mv.addObject("goods_collect", JSON.toJSONString(goods_collect));
			mv.addObject("goods_salenum", JSON.toJSONString(goods_salenum));
			mv.addObject("goods_clickfrom", clicksb);
			mv.addObject("goods_ordertype", ordersb);
		}
		return mv;
	}

	private List<Map<String,Object>> sort(Map<Long, Integer> map, Map<Long, String> goodsnames) {
		List<Long> keylist = Lists.newLinkedList();
		List<Integer> valuelist = Lists.newLinkedList();

		Iterator<Long> it = map.keySet().iterator();
		while (it.hasNext()) {
			Long key = (Long) it.next();
			int count = ((Integer) map.get(key)).intValue();
			boolean add = true;

			int length = valuelist.size();
			for (int i = 0; i < length; i++) {
				int value = ((Integer) valuelist.get(i)).intValue();
				if (count > value) {
					valuelist.add(i, Integer.valueOf(count));
					keylist.add(i, key);
					add = false;
					break;
				}
			}
			if (add) {
				valuelist.add(Integer.valueOf(count));
				keylist.add(key);
			}
		}
		
		List<Map<String,Object>> result = Lists.newArrayList();
		int length = keylist.size();
		for (int i = 0; i < length; i++) {
			if (i == 150) {
				break;
			}
			long key = ((Long) keylist.get(i)).longValue();
			Map<String,Object> map2 = Maps.newHashMap();
			map2.put("name", goodsnames.get(Long.valueOf(key)));
			map2.put("data", map.get(Long.valueOf(key)));
			result.add(map2);
		}
		return result;
	}

	private Set<Long> genericIds(GoodsClass gc) {
		Set<Long> ids = Sets.newHashSet();
		ids.add(gc.getId());
		for (GoodsClass child : gc.getChilds()) {
			Set<Long> cids = genericIds(child);
			for (Long cid : cids) {
				ids.add(cid);
			}
			ids.add(child.getId());
		}
		return ids;
	}
}
