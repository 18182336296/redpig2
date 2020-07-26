package com.redpigmall.manage.admin.tools;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.criteria.Order;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.redpigmall.api.tools.StringUtils;
import com.redpigmall.domain.*;
import com.redpigmall.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.parser.Feature;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.redpigmall.api.mv.RedPigJModelAndView;
import com.redpigmall.api.sec.SecurityUserHolder;
import com.redpigmall.api.tools.CommUtil;
import com.redpigmall.msg.RedPigMsgTools;
import com.redpigmall.msg.SpelTemplate;
import com.redpigmall.domain.virtual.TransContent;
import com.redpigmall.domain.virtual.TransInfo;

/**
 * 
 * <p>
 * Title: RedPigOrderFormTools.java
 * </p>
 * 
 * <p>
 * Description: 订单解析工具，解析订单中json数据
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
 * @date 2016-5-4
 * 
 * @version redpigmall_b2b2c v8.0 2016版
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
@Component
public class RedPigOrderFormTools {

	@Autowired
	private RedPigOrderFormService redPigOrderFormService;
	@Autowired
	private RedPigSysConfigService redPigSysConfigService;
	@Autowired
	private RedPigExpressCompanyService redPigExpressCompanyService;
	@Autowired
	private RedPigExpressInfoService redPigExpressInfoService;
	@Autowired
	private RedPigSysConfigService configService;
	@Autowired
	private RedPigUserConfigService userConfigService;
	@Autowired
	private RedPigUserService userService;
	@Autowired
	private RedPigOrderFormService orderFormService;
	@Autowired
	private RedPigGoodsService goodsService;
	@Autowired
	private RedPigGoodsSpecPropertyService gspService;
	@Autowired
	private RedPigStoreService storeService;
	
	@Autowired
	private RedPigEvaluateService evaluateService;
	
	@Autowired
	private RedPigTemplateService templateService;
	@Autowired
	private RedPigMsgTools msgTools;
	
	@Autowired
	private RedPigGroupGoodsService ggService;
	
	@Autowired
	private RedPigBuyGiftService buyGiftService;
	
	@Autowired
	private RedPigGoodsTools goodsTools;
	@Autowired
	private RedPigAddressService addrService;
	@Autowired
	private RedPigInventoryService inventoryService;
	
	@Autowired
	private RedPigCartTools cartTools;
	@Autowired
	private RedPigInventoryOperationService inventoryOperationService;
	@Autowired
	private RedPigSnapshotService snapshotService;

	@Autowired
	private RedPigNukeService nukeService;

    @Autowired
    private RedPigNukeGoodsService nukeGoodsService;
	/**
	 * 通过商品获得佣金比例
	 * @param json
	 * @param goods
	 * @return
	 */
	public double CommRateOfOrderByGoods(String json, Goods goods) {
		double rate = 0.0D;
		List<Map> map_list = Lists.newArrayList();
		if ((json != null) && (!json.equals(""))) {
			map_list = JSON.parseArray(json, Map.class);
		}
		for (Map map : map_list) {
			if (CommUtil.null2String(map.get("goods_id")).equals(
					CommUtil.null2String(goods.getId()))) {
				rate = CommUtil.null2Double(map.get("goods_commission_rate"));
				break;
			}
		}
		return rate;
	}
	
	/**
	 * 把商品json转换成map
	 * @param json
	 * @return
	 */
	public List<Map> queryGoodsInfo(String json) {
		List<Map> map_list = Lists.newArrayList();
		if ((json != null) && (!json.equals(""))) {
			map_list = JSON.parseArray(json, Map.class);
		}
		return map_list;
	}

	/**
	 * 把订单中所有的商品json转换成map
	 * @param order
	 * @return
	 */
	public List<Map> queryAllGoodsInfo(OrderForm order) {
		List<Map> map_list = Lists.newArrayList();
		if ((order.getGoods_info() != null)
				&& (!order.getGoods_info().equals(""))) {
			map_list = JSON.parseArray(order.getGoods_info(), Map.class);
		}
		if (!CommUtil.null2String(order.getChild_order_detail()).equals("")) {
			List<Map> order_maps = queryGoodsInfo(order.getChild_order_detail());
			for (Map map : order_maps) {
				OrderForm child_order = this.orderFormService
						.selectByPrimaryKey(CommUtil.null2Long(map
								.get("order_id")));
				List<Map> child_map_list = queryGoodsInfo(child_order
						.getGoods_info());
				map_list.addAll(child_map_list);
			}
		}
		return map_list;
	}
	
	/**
	 * 根据订单id查询该订单中所有商品,包括子订单中的商品
	 * @param main_order
	 * @return
	 */
	public List<Goods> queryOfGoods(OrderForm main_order) {
		List<Map> map_list = queryGoodsInfo(main_order.getGoods_info());
		List<Goods> goods_list = Lists.newArrayList();
		for (Map map : map_list) {
			Goods goods = this.goodsService.selectByPrimaryKey(CommUtil
					.null2Long(map.get("goods_id")));
			goods_list.add(goods);
		}
		if (!CommUtil.null2String(main_order.getChild_order_detail())
				.equals("")) {
			List<Map> maps = queryGoodsInfo(main_order.getChild_order_detail());

			for (Map<String, Object> map : maps) {
				OrderForm child_order = this.orderFormService
						.selectByPrimaryKey(CommUtil.null2Long(map
								.get("order_id")));
				map_list.clear();
				map_list = queryGoodsInfo(child_order.getGoods_info());
				for (Map map1 : map_list) {
					Goods good = this.goodsService.selectByPrimaryKey(CommUtil
							.null2Long(map1.get("goods_id")));
					goods_list.add(good);
				}
			}
		}
		return goods_list;
	}
	
	/**
	 * 根据订单id查询该订单中所有商品的价格总和
	 * @param order_id
	 * @return
	 */
	public double queryOfGoodsPrice(String order_id) {
		double price = 0.0D;
		OrderForm of = this.orderFormService.selectByPrimaryKey(CommUtil
				.null2Long(order_id));
		List<Map> map_list = queryGoodsInfo(of.getGoods_info());
		for (Map map : map_list) {
			price += CommUtil.null2Double(map.get("goods_all_price"));
		}
		return price;
	}
	
	/**
	 * 根据订单id和商品id查询该商品在该订单中的数量
	 * @param order_id
	 * @param goods_id
	 * @return
	 */
	public int queryOfGoodsCount(String order_id, String goods_id) {
		int count = 0;
		OrderForm of = this.orderFormService.selectByPrimaryKey(CommUtil
				.null2Long(order_id));
		List<Map> map_list = queryGoodsInfo(of.getGoods_info());
		for (Map map : map_list) {
			if (CommUtil.null2String(map.get("goods_id")).equals(goods_id)) {
				count = CommUtil.null2Int(map.get("goods_count"));
				break;
			}
		}
		if ((count == 0)
				&& (!CommUtil.null2String(of.getChild_order_detail())
						.equals(""))) { // 主订单无数量信息，继续从子订单中查询
			List<Map> maps = queryGoodsInfo(of.getChild_order_detail());
			for (Map map : maps) {
				OrderForm child_order = this.orderFormService
						.selectByPrimaryKey(CommUtil.null2Long(map
								.get("order_id")));
				map_list.clear();
				map_list = queryGoodsInfo(child_order.getGoods_info());
				for (Map map1 : map_list) {
					if (CommUtil.null2String(map1.get("goods_id")).equals(
							goods_id)) {
						count = CommUtil.null2Int(map1.get("goods_count"));
						break;
					}
				}
			}
		}
		return count;
	}

	public int queryOfGoodsCount(String order_id, String goods_id,
			String goods_gsp_ids) {
		int count = 0;
		OrderForm of = this.orderFormService.selectByPrimaryKey(CommUtil
				.null2Long(order_id));
		List<Map> map_list = queryGoodsInfo(of.getGoods_info());
		for (Map map : map_list) {
			if (CommUtil.null2String(map.get("goods_gsp_ids")) != null) {
				if (CommUtil.null2String(map.get("goods_id")).equals(goods_id)) {
					if (CommUtil.null2String(map.get("goods_gsp_ids")).equals(
							goods_gsp_ids)) {
						count = CommUtil.null2Int(map.get("goods_count"));
						break;
					}
				}
			} else if (CommUtil.null2String(map.get("goods_id")).equals(
					goods_id)) {
				count = CommUtil.null2Int(map.get("goods_count"));
				break;
			}
		}
		if ((count == 0)
				&& (!CommUtil.null2String(of.getChild_order_detail())
						.equals(""))) {
			List<Map> maps = queryGoodsInfo(of.getChild_order_detail());
			for (Map map : maps) {
				OrderForm child_order = this.orderFormService
						.selectByPrimaryKey(CommUtil.null2Long(map
								.get("order_id")));
				map_list.clear();
				map_list = queryGoodsInfo(child_order.getGoods_info());
				for (Map map1 : map_list) {
					if (CommUtil.null2String(map1.get("goods_gsp_ids")) != null) {
						if (CommUtil.null2String(map1.get("goods_id")).equals(
								goods_id)) {
							if (CommUtil.null2String(map1.get("goods_gsp_ids"))
									.equals(goods_gsp_ids)) {
								count = CommUtil.null2Int(map1
										.get("goods_count"));
								break;
							}
						}
					} else if (CommUtil.null2String(map1.get("goods_id"))
							.equals(goods_id)) {
						count = CommUtil.null2Int(map1.get("goods_count"));
						break;
					}
				}
			}
		}
		return count;
	}
	
	/**
	 * 根据订单id和商品id查询该商品在该订单中的规格
	 * @param order_id
	 * @param goods_id
	 * @return
	 */
	public List<GoodsSpecProperty> queryOfGoodsGsps(String order_id,
			String goods_id) {
		List<GoodsSpecProperty> list = Lists.newArrayList();
		String goods_gsp_ids = "";
		OrderForm of = this.orderFormService.selectByPrimaryKey(CommUtil
				.null2Long(order_id));
		List<Map> map_list = queryGoodsInfo(of.getGoods_info());
		boolean add = false;

		for (Map map : map_list) {
			if (CommUtil.null2String(map.get("goods_id")).equals(goods_id)) {
				goods_gsp_ids = CommUtil.null2String(map.get("goods_gsp_ids"));
				String[] gsp_ids = goods_gsp_ids.split(",");
				Arrays.sort(gsp_ids);

				for (String id : gsp_ids) {

					if (!id.equals("")) {
						GoodsSpecProperty gsp = this.gspService
								.selectByPrimaryKey(CommUtil.null2Long(id));
						list.add(gsp);
						add = true;
					}
				}
			}
		}
		if ((!add)
				&& (!CommUtil.null2String(of.getChild_order_detail())
						.equals(""))) { // 如果主订单中添加失败，则从子订单中添加
			List<Map> maps = queryGoodsInfo(of.getChild_order_detail());
			for (Map child_map : maps) {
				OrderForm child_order = this.orderFormService
						.selectByPrimaryKey(CommUtil.null2Long(child_map
								.get("order_id")));
				map_list.clear();
				map_list = queryGoodsInfo(child_order.getGoods_info());

				for (Map<String, Object> map : map_list) {
					if (CommUtil.null2String(map.get("goods_id")).equals(
							goods_id)) {
						goods_gsp_ids = CommUtil.null2String(map
								.get("goods_gsp_ids"));
						String[] child_gsp_ids = goods_gsp_ids.split(",");
						for (String id : child_gsp_ids) {

							if (!id.equals("")) {
								GoodsSpecProperty gsp = this.gspService
										.selectByPrimaryKey(CommUtil
												.null2Long(id));
								list.add(gsp);
								add = true;
							}
						}
					}
				}
			}
		}
		return list;
	}
	
	/**
	 * 解析订单优惠券信息json数据
	 * @param json
	 * @return
	 */
	public Map queryCouponInfo(String json) {
		Map<String, Object> map = Maps.newHashMap();
		if ((json != null) && (!json.equals(""))) {
			map = JSON.parseObject(json);
		}
		return map;
	}
	
	/**
	 * 解析生活类团购订单json数据
	 * @param json
	 * @return
	 */
	public Map queryGroupInfo(String json) {
		Map<String, Object> map = Maps.newHashMap();
		if ((json != null) && (!json.equals(""))) {
			map = JSON.parseObject(json);
		}
		return map;
	}
	
	/**
	 * 根据订单id查询订单信息
	 * @param id
	 * @return
	 */
	public OrderForm query_order(String id) {
		return this.orderFormService.selectByPrimaryKey(CommUtil.null2Long(id));
	}
	
	/**
	 * 查询订单的状态，用在买家中心的订单列表中，多商家复合订单中只有全部商家都已经发货，卖家中心才会出现确认收货按钮
	 * @param order_id
	 * @return
	 */
	public int query_order_status(String order_id) {
		int order_status = 0;
		OrderForm order = this.orderFormService.selectByPrimaryKey(CommUtil
				.null2Long(order_id));
		if (order != null) {
			if (order.getOrder_status() == 25) {
				order_status = 30;
			}
			if (order.getOrder_status() >= 30) {
				order_status = order.getOrder_status();
			}
			if (order.getOrder_status() == 0) {
				order_status = 0;
			} else {
				order_status = order.getOrder_status();
			}
			if (order.getOrder_main() == 1) {
				if (!CommUtil.null2String(order.getChild_order_detail())
						.equals("")) {
					List<Map> maps = queryGoodsInfo(order
							.getChild_order_detail());
					for (Map child_map : maps) {
						OrderForm child_order = this.orderFormService
								.selectByPrimaryKey(CommUtil
										.null2Long(child_map.get("order_id")));
						if (order_status == 0) {
							if (child_order.getOrder_status() == 0) {
								order_status = 0;
							} else {
								order_status = child_order.getOrder_status();
							}
						}
						if ((child_order.getOrder_status() <= 21)
								&& (child_order.getOrder_status() > 0)) {
							order_status = child_order.getOrder_status();
						}
						if (child_order.getOrder_status() >= 40) {
							order_status = child_order.getOrder_status();
						}
					}
				}
			}
			if (order.getOrder_status() == 25) {
				if (CommUtil.null2String(order.getChild_order_detail()).equals(
						"")) {
					order_status = 25;
				}
			}
		}
		return order_status;
	}
	
	/**
	 * 查询订单总价格（如果包含子订单，将子订单价格与主订单价格相加）
	 * @param order_id
	 * @return
	 */
	public double query_order_price(String order_id) {
		double all_price = 0.0D;
		OrderForm order = this.orderFormService.selectByPrimaryKey(CommUtil.null2Long(order_id));
		if (order != null) {
			all_price = CommUtil.null2Double(order.getTotalPrice());
			if ((order.getChild_order_detail() != null)
					&& (!order.getChild_order_detail().equals(""))) {
				List<Map> maps = queryGoodsInfo(order.getChild_order_detail());
				for (Map map : maps) {
					OrderForm child_order = this.orderFormService
							.selectByPrimaryKey(CommUtil.null2Long(map.get("order_id")));

					all_price = all_price + CommUtil.null2Double(child_order.getTotalPrice());
				}
			}
		}
		return all_price;
	}
	/**
	 * 
	 * query_order_pay_price:查询订单实际价格. <br/>
	 *
	 * @author redpig
	 * @param order_id 订单号
	 * @return
	 * @since JDK 1.8
	 */
	public double query_order_pay_price(String order_id) {
		double all_price = 0.0D;
		if ((order_id != null) && (!order_id.equals(""))) {
			OrderForm order = this.orderFormService.selectByPrimaryKey(CommUtil.null2Long(order_id));
			if (order.getOrder_status() > 0) {
				if ("advance".equals(order.getOrder_special())) {
					List<Map> maps = queryGoodsInfo(order.getGoods_info());
					for (Map obj : maps) {
						if (order.getOrder_status() == 10) {
							all_price = CommUtil.null2Double(obj.get("advance_din"));
							all_price = all_price + CommUtil.null2Double(order.getShip_price());
						}
						if (order.getOrder_status() == 11) {
							all_price = CommUtil.null2Double(obj.get("advance_wei"));
						}
					}
				} else {
					all_price = query_order_price(order_id);
				}
			}
		}
		return all_price;
	}

	public double query_order_goods(String order_id) {
		double all_goods = 0.0D;
		OrderForm order = this.orderFormService.selectByPrimaryKey(CommUtil
				.null2Long(order_id));
		if (order != null) {
			all_goods = CommUtil.null2Double(order.getGoods_amount());
			if ((order.getChild_order_detail() != null)
					&& (!order.getChild_order_detail().equals(""))) {
				List<Map> maps = queryGoodsInfo(order.getChild_order_detail());
				for (Map map : maps) {
					OrderForm child_order = this.orderFormService
							.selectByPrimaryKey(CommUtil.null2Long(map
									.get("order_id")));

					all_goods = all_goods
							+ CommUtil.null2Double(child_order
									.getGoods_amount());
				}
			}
		}
		return all_goods;
	}
	
	/**
	 * 解析订单中组合套装详情
	 * @param goods_info
	 * @return
	 */
	public Map query_order_suitinfo(String goods_info) {
		Map<String, Object> map = JSON.parseObject(goods_info);
		return map;
	}
	
	/**
	 * 解析订单中组合套装详情
	 * @param suit_map
	 * @return
	 */
	public List<Map> query_order_suitgoods(Map suit_map) {
		List<Map> map_list = Lists.newArrayList();
		if (suit_map != null) {
			map_list = (List) suit_map.get("goods_list");
		}
		return map_list;
	}
	
	/**
	 * 根据店铺id查询是否开启了二级域名。
	 * 
	 * @param id为参数
	 *            type为store时查询store type为goods时查询商品
	 * @return
	 */
	public Store goods_second_domain(String id, String type) {
		Store store = null;
		if (type.equals("store")) {
			store = this.storeService
					.selectByPrimaryKey(CommUtil.null2Long(id));
		}
		if (type.equals("goods")) {
			Goods goods = this.goodsService.selectByPrimaryKey(CommUtil
					.null2Long(id));
			if ((goods != null) && (goods.getGoods_type() == 1)) {
				store = goods.getGoods_store();
			}
		}
		return store;
	}
	
	/**
	 * 解析订单中自提点信息
	 * @param delivery_info
	 * @return
	 */
	public Map query_order_delivery(String delivery_info) {
		Map<String, Object> map = JSON.parseObject(delivery_info);
		return map;
	}
	
	/**
	 * 查询订单中所以商品数量
	 * @param order_id
	 * @return
	 */
	public int query_goods_count(String order_id) {
		OrderForm orderForm = query_order(order_id);

		int count = 0;
		if (orderForm != null) {
			List<Map> list_map = queryGoodsInfo(orderForm.getGoods_info());
			for (Map map : list_map) {
				count += CommUtil.null2Int(map.get("goods_count"));
			}
			if (CommUtil.null2Int(orderForm.getOrder_main()) == 1) {
				if (!CommUtil.null2String(orderForm.getChild_order_detail())
						.equals("")) {
					list_map = queryGoodsInfo(orderForm.getChild_order_detail());

					for (Map<String, Object> map : list_map) {
						List<Map> list_map1 = Lists.newArrayList();
						list_map1 = queryGoodsInfo(map.get("order_goods_info")
								.toString());
						for (Map map2 : list_map1) {
							count = count
									+ CommUtil
											.null2Int(map2.get("goods_count"));
						}
					}
				}
			}
		}
		return count;
	}
	
	/**
	 * 查询订单中所有团购数量
	 * @param order_id
	 * @return
	 */
	public int query_group_count(String order_id) {
		OrderForm orderForm = query_order(order_id);
		Map<String, Object> map = Maps.newHashMap();
		int count = 0;
		if (orderForm != null) {
			map = queryGroupInfo(orderForm.getGroup_info());
			count = CommUtil.null2Int(map.get("goods_count"));
		}
		return count;
	}
	
	/**
	 * 查询订单中所有积分商品数量
	 * @param json
	 * @return
	 */
	public List<Map> query_integral_goodsinfo(String json) {
		List<Map> maps = Lists.newArrayList();
		if ((json != null) && (!json.equals(""))) {
			maps = JSON.parseArray(json, Map.class);
		}
		
		for (Map map : maps) {
			if(map.containsKey("ig_goods_img")){
				String ig_goods_img = (String) map.get("ig_goods_img");
				ig_goods_img = configService.getSysConfig().getImageWebServer() + ig_goods_img.substring(ig_goods_img.indexOf("/upload"));
				map.put("ig_goods_img", ig_goods_img);
			}
		}
		
		return maps;
	}
	
	/**
	 * 查询订单中某件是否评价
	 * @param order_id
	 * @param goods_id
	 * @return
	 */
	public Evaluate query_order_evaluate(Object order_id, Object goods_id) {
		Map para = Maps.newHashMap();
		para.put("of_id", CommUtil.null2Long(order_id));
		para.put("evaluate_goods_id", CommUtil.null2Long(goods_id));
		List<Evaluate> list = this.evaluateService.queryPageList(para);
		if (list.size() > 0) {
			return (Evaluate) list.get(0);
		}
		return null;
	}

	/**
	 * 判断是否可修改评价
	 * @param date
	 * @return
	 */
	public int evaluate_able(Date date) {
		if (date != null) {
			long begin = date.getTime();
			long end = new Date().getTime();
			SysConfig config = this.configService.getSysConfig();
			long day = (end - begin) / 86400000L;
			if (day <= config.getEvaluate_edit_deadline()) {
				return 1;
			}
		}
		return 0;
	}
	
	/**
	 * 判断是否可追加评价
	 * @param date
	 * @return
	 */
	public int evaluate_add_able(Date date) {
		if (date != null) {
			long begin = date.getTime();
			long end = new Date().getTime();
			SysConfig config = this.configService.getSysConfig();
			long day = (end - begin) / 86400000L;
			if (day <= config.getEvaluate_add_deadline()) {
				return 1;
			}
		}
		return 0;
	}
	
	/**
	 * 计算今天到指定时间天数
	 * @param date
	 * @return
	 */
	public int how_soon(Date date) {
		if (date != null) {
			long begin = date.getTime();
			long end = new Date().getTime();
			long day = (end - begin) / 86400000L;
			return CommUtil.null2Int(Long.valueOf(day));
		}
		return 999;
	}
	
	/**
	 * 验证订单中商品库存是否充足，是否可以支付订单，在选择支付方式请求中验证、在选择支付方式后支付中验证,返回true说明验证成功，
	 * 返回false说明验证失败
	 * @param order
	 * @return
	 */
	public boolean verify_goods_Inventory(OrderForm order) {
		boolean verify = true;
		List<Map> all_goods_maps = queryAllGoodsInfo(order);
		for (Map goods_map : all_goods_maps) {
			Goods obj = this.goodsService.selectByPrimaryKey(CommUtil
					.null2Long(goods_map.get("goods_id")));
			int order_goods_count = CommUtil.null2Int(goods_map.get("count"));
			String order_goods_gsp_ids = CommUtil.null2String(goods_map
					.get("goods_gsp_ids"));

			Address addr = this.addrService.selectByPrimaryKey(CommUtil
					.null2Long(order.getReceiver_addr_id()));
			Map real_map = this.cartTools.generic_goods_default_Info(obj,
					order_goods_gsp_ids,
					CommUtil.null2String(addr.getArea().getId()), "");
			int real_goods_count = CommUtil.null2Int(real_map.get("count"));
			if (order_goods_count > real_goods_count) {
				verify = false;
				break;
			}
		}
		return verify;
	}
	
	
	public boolean verify_goods_exist(OrderForm order) {
		boolean verify = true;
		List<Goods> objs = queryOfGoods(order);
		for (Goods obj : objs) {
			if (obj == null) {
				verify = false;
			} else if (obj.getGoods_status() != 0) {
				verify = false;
			}
		}
		return verify;
	}
	
	/**
	 * 
	 * send_groupInfo_sms:发送团购码. <br/>
	 *
	 * @author redpig
	 * @param url
	 * @param order
	 * @param mobile
	 * @param mark
	 * @param codes
	 * @param buyer_id
	 * @param seller_id
	 * @throws Exception
	 * @since JDK 1.8
	 */
	@Async
	@Transactional
	public void send_groupInfo_sms(String url, OrderForm order, String mobile,
			String mark, List<String> codes, String buyer_id, String seller_id)
			throws Exception {
		Map<String, Object> params = Maps.newHashMap();
		params.put("mark", mark);
		Template template = (Template) this.templateService.queryPageList(
				params).get(0);

		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < codes.size(); i++) {
			sb.append((String) codes.get(i) + ",");
		}
		String code = sb.toString();
		if ((template != null) && (template.getOpen())) {
			ExpressionParser exp = new SpelExpressionParser();
			EvaluationContext context = new StandardEvaluationContext();
			context.setVariable("buyer", this.userService
					.selectByPrimaryKey(CommUtil.null2Long(buyer_id)));
			context.setVariable("seller", this.userService
					.selectByPrimaryKey(CommUtil.null2Long(seller_id)));
			context.setVariable("config", this.configService.getSysConfig());
			context.setVariable("send_time",
					CommUtil.formatLongDate(new Date()));
			context.setVariable("webPath", url);
			context.setVariable("order", order);
			Map<String, Object> map = JSON.parseObject(order.getGroup_info());
			context.setVariable("group_info", map.get("goods_name"));
			context.setVariable("code", code);
			Expression ex = exp.parseExpression(template.getContent(),
					new SpelTemplate());
			String content = (String) ex.getValue(context, String.class);
			this.msgTools.sendSMS(mobile, content);
			System.out.println("发送消息接口被关闭,线上需要打开。。。。。类:RedPigOrderFormTools.send_groupInfo_sms()");
		}
	}
	
	/**
	 * 通过订单ID查询订单状态
	 * @param order
	 * @return
	 */
	public int queryOrderStatusByOrder(OrderForm order) {
		int order_status = 0;
		if (order != null) {
			if (order.getOrder_status() == 0) {
				order_status = 0;
			} else {
				order_status = order.getOrder_status();
			}
			if (order.getOrder_main() == 1) {
				if (!CommUtil.null2String(order.getChild_order_detail())
						.equals("")) {
					List<Map> maps = queryGoodsInfo(order
							.getChild_order_detail());
					for (Map child_map : maps) {
						OrderForm child_order = this.orderFormService
								.selectByPrimaryKey(CommUtil
										.null2Long(child_map.get("order_id")));
						if (order_status == 0) {
							if (child_order.getOrder_status() == 0) {
								order_status = 0;
							} else {
								order_status = child_order.getOrder_status();
							}
						}
					}
				}
			}
		}
		return order_status;
	}
	
	/**
	 * 订单处理完发送消息
	 * @param url
	 * @param order
	 * @param buyer_mark
	 * @param seller_mark
	 */
	public void sendMsgWhenHandleOrder(String url, OrderForm order,
			String buyer_mark, String seller_mark) {
		List<OrderForm> order_list = Lists.newArrayList();
		int order_status = queryOrderStatusByOrder(order);
		if ((order_status == 0) || (order_status == 10) || (order_status == 16)) {
			order_list.add(order);
			if (order.getOrder_main() == 1) {
				if (!CommUtil.null2String(order.getChild_order_detail())
						.equals("")) {
					List<Map> maps = queryGoodsInfo(order
							.getChild_order_detail());
					for (Map child_map : maps) {
						OrderForm child_order = this.orderFormService
								.selectByPrimaryKey(CommUtil
										.null2Long(child_map.get("order_id")));
						order_list.add(child_order);
					}
				}
			}
		} else if ((order.getOrder_status() > 30)
				&& (order.getOrder_status() < 50)) {
			order_list.add(order);
		} else if (order_status == 20) {
			if (order.getOrder_status() == 20) {
				order_list.add(order);
			}
			if (order.getOrder_main() == 1) {
				if (!CommUtil.null2String(order.getChild_order_detail())
						.equals("")) {
					List<Map> maps = queryGoodsInfo(order
							.getChild_order_detail());
					for (Map child_map : maps) {
						OrderForm child_order = this.orderFormService
								.selectByPrimaryKey(CommUtil
										.null2Long(child_map.get("order_id")));
						if (child_order.getOrder_status() == 20) {
							order_list.add(child_order);
						}
					}
				}
			}
		}
		User buyer = this.userService.selectByPrimaryKey(CommUtil
				.null2Long(order.getUser_id()));
		String buyer_sms_mark = null;
		String buyer_email_mark = null;
		String seller_sms_mark = null;
		String seller_email_mark = null;
		if ((buyer_mark != null) && (!buyer_mark.equals(""))) {
			buyer_sms_mark = "sms_" + buyer_mark;
			buyer_email_mark = "email_" + buyer_mark;
		}
		if ((seller_mark != null) && (!seller_mark.equals(""))) {
			seller_sms_mark = "sms_" + seller_mark;
			seller_email_mark = "email_" + seller_mark;
		}
		try {
			if ((buyer_mark != null) && (!buyer_mark.equals(""))) {
				if (order.getOrder_form() == 0) {
					this.msgTools.sendEmailCharge(url, buyer_email_mark,
							buyer.getEmail(), null,
							CommUtil.null2String(order.getId()),
							order.getStore_id());
					this.msgTools.sendSmsCharge(url, buyer_sms_mark,
							buyer.getMobile(), null,
							CommUtil.null2String(order.getId()),
							order.getStore_id());
				} else {
					this.msgTools.sendEmailFree(url, buyer_email_mark,
							buyer.getEmail(), null,
							CommUtil.null2String(order.getId()));
					this.msgTools.sendSmsFree(url, buyer_sms_mark,
							buyer.getMobile(), null,
							CommUtil.null2String(order.getId()));
				}
			}
			if ((seller_mark != null) && (!seller_mark.equals(""))) {
				for (OrderForm obj : order_list) {
					if (obj.getOrder_form() == 0) {
						Store store = this.storeService
								.selectByPrimaryKey(CommUtil.null2Long(obj
										.getStore_id()));
						if (store != null) {
							this.msgTools.sendSmsCharge(url, seller_sms_mark,
									store.getUser().getMobile(), null,
									CommUtil.null2String(order.getId()),
									order.getStore_id());
							this.msgTools.sendEmailCharge(url,
									seller_email_mark, store.getUser()
											.getEmail(), null, CommUtil
											.null2String(order.getId()), order
											.getStore_id());
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 更新库存
	 * @param order
	 */
	public void updateGoodsInventory(OrderForm order) {
		try {
			boolean ret = false;
			int update_time = 0;
			if (order.getOrder_status() == 10) {
				update_time = 0;
			}
			if (order.getOrder_status() == 20) {
				update_time = 1;
			}
			if (order.getOrder_cat() != 2) {
				ret = updateOneOrderGoodsInventory(order, update_time);
			}
			if (ret) {
				if ((order.getChild_order_detail() != null)
						&& (!order.getChild_order_detail().equals(""))) {
					List<Map> order_maps = queryGoodsInfo(order
							.getChild_order_detail());
					for (Map temp_order : order_maps) {
						OrderForm child_order = this.orderFormService.selectByPrimaryKey(CommUtil.null2Long(temp_order.get("order_id")));
						updateOneOrderGoodsInventory(child_order, update_time);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 更新库存
	 * @param order
	 * @param update_time
	 * @return
	 */
	public boolean updateOneOrderGoodsInventory(OrderForm order, int update_time) {
		boolean ret = false;
		try {
			
			List<Goods> goods_list = queryOfGoods(order);
			List<Map> maps = queryGoodsInfo(order.getGoods_info());
			for (Map order_map : maps) {
				String goods_gsp_ids = CommUtil.null2String(order_map.get("goods_gsp_ids"));
				Goods goods = this.goodsService.selectByPrimaryKey(CommUtil.null2Long(order_map.get("goods_id")));
				if (goods.getInventory_set() == update_time) {
					List<GoodsSpecProperty> gsps = Lists.newArrayList();
					List<String> gsps_strs = Lists.newArrayList();
					int goods_count = 0;
					if (goods_gsp_ids != null) {
						goods_count = queryOfGoodsCount(
								CommUtil.null2String(order.getId()),
								CommUtil.null2String(goods.getId()),
								CommUtil.null2String(order_map
										.get("goods_gsp_ids")));
						
						String[] gsp_ids = CommUtil.null2String(order_map.get("goods_gsp_ids")).split(",");
						for (String temp_gsp_id : gsp_ids) {
							if (!temp_gsp_id.equals("")) {
								GoodsSpecProperty gsp = this.gspService.selectByPrimaryKey(CommUtil.null2Long(temp_gsp_id));
								gsps.add(gsp);
							}
						}
					} else {
						goods_count = queryOfGoodsCount(
								CommUtil.null2String(order.getId()),
								CommUtil.null2String(goods.getId()));
					}
					int combin_count;
					if (order_map.get("combin_suit_info") != null) {
						Map suit_info = JSON.parseObject(CommUtil.null2String(order_map.get("combin_suit_info")));
						combin_count = CommUtil.null2Int(suit_info.get("suit_count"));
						List<Map> combin_goods = query_order_suitgoods(suit_info);
						for (Map temp_combin_goods : combin_goods) {
							
							for (Goods temp : goods_list) {
								if (!CommUtil.null2String(
										temp_combin_goods.get("id")).equals(
										temp.getId().toString())) {
									Goods com_goods = this.goodsService.selectByPrimaryKey(CommUtil.null2Long(temp_combin_goods.get("id")));
									int goods_inventory = com_goods.getGoods_inventory() - combin_count;
									com_goods.setGoods_inventory(goods_inventory > 0 ? goods_inventory: 0);
									this.goodsService.updateById(com_goods);
								}
							}
						}
					}
					int goods_inventory;
					if ((goods.getGroup() != null) && (goods.getGroup_buy() == 2)) {
						for (GroupGoods gg : goods.getGroup_goods_list()) {
							if (gg.getGroup().getId().equals(goods.getGroup().getId())) {
								goods_inventory = gg.getGg_count() - goods_count;
								gg.setGg_count(goods_inventory > 0 ? goods_inventory : 0);
								gg.setGg_selled_count(gg.getGg_selled_count() + goods_count);
								if ((gg.getGroup().getId().equals(goods.getGroup().getId()))&& (gg.getGg_count() == 0)) {
									goods.setGroup_buy(3);
								}
								this.ggService.updateById(gg);
								
								this.goodsTools.updateGoodsLucene(goods);
							}
						}
					}
					if (goods.getOrder_enough_give_status() == 1) {
						BuyGift bg = this.buyGiftService.selectByPrimaryKey(goods.getBuyGift_id());
						if ((bg != null) && (bg.getEndTime().after(new Date()))
								&& (bg.getGift_status() == 10)) {
							update_gift_invoke(goods.getId(), order);
						}
					}
					String spectype = "";
					for (GoodsSpecProperty gsp : gsps) {
						gsps_strs.add(gsp.getId().toString());
						spectype = spectype + gsp.getSpec().getName() + ":"
								+ gsp.getValue() + " ";
					}
					String[] gsp_list = new String[gsps.size()];
					gsps_strs.toArray(gsp_list);
					this.goodsTools.save_salenum_goodsLog(order, goods,
							goods_count, spectype);
					String inventory_type = goods.getInventory_type() == null ? "all"
							: goods.getInventory_type();
					boolean inventory_warn = false;
					String[] temp_ids = null;

					// 如果商品只有一个规格(all)，只需要减goods_inventory的数量
					if (inventory_type.equals("all")) {
						goods_inventory = goods.getGoods_inventory()- goods_count;
						goods.setGoods_inventory(goods_inventory > 0 ? goods_inventory: 0);
						if (goods.getGoods_inventory() <= goods.getGoods_warn_inventory()) {
							inventory_warn = true;
						}
					} else {
						// 如果商品有多个规格，则找出商品所在的规格类型，找到库存数量，并执行库存扣减操作
						List<HashMap> list = JSON.parseArray(CommUtil.null2String(goods.getGoods_inventory_detail()),HashMap.class);
						for (Map temp : list) {
							temp_ids = CommUtil.null2String(temp.get("id")).split("_");
							Arrays.sort(temp_ids);
							Arrays.sort(gsp_list);
							if (Arrays.equals(temp_ids, gsp_list)) {
								goods_inventory = CommUtil.null2Int(temp.get("count")) - goods_count;
								temp.put("count",Integer.valueOf(goods_inventory > 0 ? goods_inventory: 0));
								if (CommUtil.null2Int(temp.get("count")) <= CommUtil.null2Int(temp.get("supp"))) {
									inventory_warn = true;
								}
							}
						}
						
						goods.setGoods_inventory_detail(JSON.toJSONString(list));
						//库存总和=各个规格等库相加总和
						List<HashMap> goodsInventoryDetailList = JSON.parseArray(goods.getGoods_inventory_detail(), HashMap.class);
						int inventory = 0;
						for (Map temp : goodsInventoryDetailList) {
							inventory += CommUtil.null2Int(temp.get("count"));
						}
						goods.setGoods_inventory(inventory);
						
					}
					//判断是否属于秒杀商品，如果是，执行秒杀库存扣减操作
					if (goods.getNuke_buy()==2&&goods.getNuke()!=null){
						Map<String,Object> map = new HashMap<>();
						map.put("ng_goods_id",goods.getId());
						map.put("nuke_id",goods.getNuke().getId());
						List<NukeGoods>nukeGoodsList = this.nukeGoodsService.selectObjByProperty(map);
						if (nukeGoodsList!=null&&nukeGoodsList.size()>0){
							NukeGoods nukeGoods = nukeGoodsList.get(0);
							int count = nukeGoods.getNg_nuke_count()+goods_count;
							nukeGoods.setNg_nuke_count(count);
							// 如果tag=0，表明已经卖完
							int tag = nukeGoods.getNg_count() - count;
							if(tag<=0){
								goods.setNuke_buy(3);
							}
							this.goodsService.updateById(goods);
							this.nukeGoodsService.updateById(nukeGoods);
						}
					}
					if (inventory_warn) {
						goods.setWarn_inventory_status(-1);
					}
					this.goodsService.updateById(goods);
					
					this.goodsTools.updateGoodsLucene(goods);
					if (goods.getGoods_type() == 0) {
						String[] sp = goods_gsp_ids.split(",");
						Arrays.sort(sp);
						String spec_ids = "";
						if(temp_ids!=null && temp_ids.length>0){
							for (String s : temp_ids) {
								spec_ids = spec_ids + s + ",";
							}
						}
						if (spec_ids.length() > 0) {
							spec_ids = spec_ids.substring(0,
									spec_ids.length() - 1);
						}
						Map<String, Object> params = Maps.newHashMap();
						
						params.put("goods_id", goods.getId());
						params.put("spec_id", spec_ids);
						
						List<Inventory> inventorys = this.inventoryService.queryPageList(params, 0, 1);
						
						if (inventorys.size() > 0) {
							Inventory inven = (Inventory) inventorys.get(0);
							goods_inventory = inven.getCount() - goods_count;
							inven.setCount(goods_inventory > 0 ? goods_inventory
									: 0);
							this.inventoryService.updateById(inven);

							InventoryOperation inventoryOperation = new InventoryOperation();
							inventoryOperation.setAddTime(new Date());
							inventoryOperation.setCount(goods_count);
							inventoryOperation.setGoods_name(inven
									.getGoods_name());
							inventoryOperation.setStorehouse_name(inven
									.getStorehouse_name());
							inventoryOperation.setInventory_id(inven.getId());
							inventoryOperation.setSpec_name(inven.getSpec_name());
							inventoryOperation.setType(2);
							inventoryOperation.setOperation_info("商品出售更新库存");
							this.inventoryOperationService.saveEntity(inventoryOperation);
						}
					}
				}
			}
			ret = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ret;
	}
	
	/**
	 * 更新销量
	 * @param order
	 */
	public void updateOrderGoodsSaleNum(OrderForm order) {
		List<Map> maps = queryGoodsInfo(order.getGoods_info());
		for (Map goods_map : maps) {
			Goods goods = this.goodsService.selectByPrimaryKey(CommUtil.null2Long(goods_map.get("goods_id")));
			int goods_count = CommUtil.null2Int(goods_map.get("goods_count"));
			int combin_count;
			if (goods_map.get("combin_suit_info") != null) {
				Map suit_info = JSON.parseObject(CommUtil.null2String(goods_map.get("combin_suit_info")));
				combin_count = CommUtil.null2Int(suit_info.get("suit_count"));
				List<Map> combin_goods = query_order_suitgoods(suit_info);
				for (Map temp_goods : combin_goods) {
					if (!CommUtil.null2String(temp_goods.get("id")).equals(CommUtil.null2String(goods_map.get("goods_id")))) {
						Goods com_goods = this.goodsService.selectByPrimaryKey(CommUtil.null2Long(temp_goods.get("id")));
						com_goods.setGoods_salenum(com_goods.getGoods_salenum() + combin_count);
						this.goodsService.updateById(com_goods);
					}
				}
			}
			
			if ((goods.getGroup() != null) && (goods.getGroup_buy() == 2)) {
				for (GroupGoods gg : goods.getGroup_goods_list()) {
					if (gg.getGroup().getId().equals(goods.getGroup().getId())) {
						gg.setGg_selled_count(gg.getGg_selled_count() + goods_count);
						this.ggService.updateById(gg);
					}
				}
			}
			
			goods.setGoods_salenum(goods.getGoods_salenum() + goods_count);
			this.goodsService.updateById(goods);
			
			this.goodsTools.updateGoodsLucene(goods);
		}
		if ((order.getChild_order_detail() != null) && (!order.getChild_order_detail().equals(""))) {
			List<Map> order_maps = queryGoodsInfo(order.getChild_order_detail());
			for (Map temp_order : order_maps) {
				OrderForm child_order = this.orderFormService.selectByPrimaryKey(CommUtil.null2Long(temp_order.get("order_id")));
				updateOrderGoodsSaleNum(child_order);
			}
		}
	}
	
	/**
	 * 
	 * @param goods_id
	 * @param order
	 */
	private void update_gift_invoke(Long goods_id, OrderForm order) {
		if ((order != null) && (order.getGift_infos() != null)
				&& (!order.getGift_infos().equals(""))) {
			List<Map> maps = JSON.parseArray(order.getGift_infos(), Map.class);
			for (Map map : maps) {
				if (CommUtil.null2String(goods_id).equals(
						CommUtil.null2String(map.get("goods_main_id")))) {
					BuyGift bg = this.buyGiftService
							.selectByPrimaryKey(CommUtil.null2Long(map
									.get("buyGify_id")));
					if ((bg != null) && (bg.getGift_status() == 10)) {
						List<Map> gifts = JSON.parseArray(bg.getGift_info(),
								Map.class);
						for (Map gift : gifts) {
							if (CommUtil.null2String(gift.get("goods_id"))
									.equals(map.get("goods_id").toString())) {

								if (CommUtil.null2String(
										gift.get("storegoods_count")).equals(
										"1")) {
									Goods goods = this.goodsService
											.selectByPrimaryKey(CommUtil
													.null2Long(gift
															.get("goods_id")));
									if (goods.getGoods_inventory() <= 1) {
										goods.setGoods_inventory(0);
										bg.setGift_status(20);
										List<Map> g_maps = JSON.parseArray(
												bg.getGift_info(), Map.class);
										maps.addAll(JSON.parseArray(
												bg.getGift_info(), Map.class));
										for (Map m : g_maps) {
											Goods g_goods = this.goodsService
													.selectByPrimaryKey(CommUtil.null2Long(m
															.get("goods_id")));
											if (g_goods != null) {
												g_goods.setOrder_enough_give_status(0);
												g_goods.setOrder_enough_if_give(0);
												g_goods.setBuyGift_id(null);
												this.goodsService
														.updateById(g_goods);
											}
										}
									} else {
										goods.setGoods_inventory(goods
												.getGoods_inventory() - 1);
									}
									this.goodsService.updateById(goods);
								} else if (gift.get("goods_count") != null) {
									if (CommUtil.null2Int(gift
											.get("goods_count")) > 1) {
										gift.put(
												"goods_count",
												Integer.valueOf(CommUtil.null2Int(gift
														.get("goods_count")) - 1));
									} else {
										gift.put("goods_count",
												Integer.valueOf(0));
										bg.setGift_status(20);
										List<Map> g_maps = JSON.parseArray(
												bg.getGift_info(), Map.class);
										maps.addAll(JSON.parseArray(
												bg.getGift_info(), Map.class));
										for (Map m : g_maps) {
											Goods g_goods = this.goodsService
													.selectByPrimaryKey(CommUtil.null2Long(m
															.get("goods_id")));
											if (g_goods != null) {
												g_goods.setOrder_enough_give_status(0);
												g_goods.setOrder_enough_if_give(0);
												g_goods.setBuyGift_id(null);
												this.goodsService
														.updateById(g_goods);
											}
										}
									}
								}
							}
						}
						bg.setGift_info(JSON.toJSONString(gifts));
						this.buyGiftService.updateById(bg);
					}
				}
			}
		}
	}
	
	/**
	 * 创建订单快照
	 * @param order
	 * @param localPath
	 * @param buyer
	 */
	public void createOrderGoodsSnapshot(OrderForm order, String localPath,
			User buyer) {
		List<Map> goods_maps = queryGoodsInfo(order.getGoods_info());
		for (Map obj : goods_maps) {
			Goods goods = this.goodsService.selectByPrimaryKey(CommUtil
					.null2Long(obj.get("goods_id")));
			String goods_id = CommUtil.null2String(obj.get("goods_id"));
			String goods_name = CommUtil.null2String(obj.get("goods_name"));
			String goods_price = CommUtil.null2String(obj.get("goods_price"));
			String goods_info = goods.getGoods_details();
			String Good_num = goods.getGoods_serial();
			String Good_pack_details = goods.getPack_details();
			String Good_service = goods.getGoods_service();
			String fee = CommUtil.null2String(Integer.valueOf(goods
					.getGoods_transfee()));
			String goods_cod = CommUtil.null2String(Integer.valueOf(goods
					.getGoods_cod()));
			Snapshot snap = new Snapshot();
			snap.setGood_id(goods_id);
			snap.setOrder_id(CommUtil.null2String(order.getId()));
			snap.setGood_name(goods_name);
			snap.setGood_price(goods_price);
			snap.setGood_info(goods_info);
			snap.setAddTime(new Date());
			snap.setGood_num(Good_num);
			snap.setGood_pack_details(Good_pack_details);
			snap.setGoods_service(Good_service);
			snap.setFee(fee);
			snap.setUser_id(CommUtil.null2Long(order.getUser_id()));
			snap.setGood_cod(goods_cod);
			snap.setOrder_id(CommUtil.null2String(order.getId()));
			if ((goods.getDelivery_area() != null)
					&& (!"".equals(goods.getDelivery_area()))) {
				String good_add = goods.getDelivery_area();
				snap.setAddress(good_add);
			} else {
				snap.setAddress("无发货地址");
			}
			List<Map> store_list = Lists.newArrayList();
			Map store_map = Maps.newHashMap();
			if (goods.getGoods_store() != null) {
				if (goods.getGoods_store().getPoint() != null) {
					store_map.put("store_name", goods.getGoods_store()
							.getStore_name());
					store_map.put("store_point", goods.getGoods_store()
							.getPoint().getStore_evaluate());
					store_map.put("des_point", goods.getGoods_store()
							.getPoint().getDescription_evaluate());
					store_map.put("ser_point", goods.getGoods_store()
							.getPoint().getService_evaluate());
					store_map.put("ship_point", goods.getGoods_store()
							.getPoint().getShip_evaluate());
				}
				store_map.put("company_name", goods.getGoods_store()
						.getLicense_c_name());
				Area area = goods.getGoods_store().getLicense_area();
				if (area != null) {
					String good_add = area.getParent().getParent()
							.getAreaName()
							+ " "
							+ area.getParent().getAreaName()
							+ " "
							+ area.getAreaName();
					store_map.put("store_add", good_add);
				} else {
					store_map.put("store_add", "");
				}
				store_list.add(store_map);
				String store_json = JSON.toJSONString(store_list);
				snap.setStore_info(store_json);
				store_map.put("store_id", goods.getGoods_store().getId());
			} else {
				snap.setStore_info("自营店铺");
				store_map.put("store_id", "no");
			}
			if (goods.getGc() != null) {
				System.out.println("商品分类存在" + goods.getGc().getId());
				GoodsClass gc = goods.getGc();
				System.out.println(gc.getClassName());
			} else {
				System.out.println("商品分类不存在");
			}
			snap.setGc(goods.getGc().getClassName());
			snap.setGood_source(String.valueOf(goods.getGoods_type()));
			if (goods.getActivity_status() == 2) {
				snap.setActivity_status("1");
			}
			if (goods.getGroup_buy() == 2) {
				snap.setActivity_status("2");
			}
			if ((goods.getBuyGift_id() != null)
					&& (goods.getOrder_enough_give_status() == 1)) {
				snap.setActivity_status("3");
			}
			Accessory main_img = goods.getGoods_main_photo();
			String timeStamp;
			if (main_img != null) {
				String uploadFilePath = this.configService.getSysConfig()
						.getUploadFilePath();
				String saveFilePathName = localPath + uploadFilePath
						+ File.separator + "snapshot";
				CommUtil.createFolder(saveFilePathName);
				timeStamp = buyer.getId() + CommUtil.randomInt(10);
				String target = saveFilePathName + File.separator + timeStamp
						+ main_img.getName();
				String source = localPath + main_img.getPath() + File.separator
						+ main_img.getName();
				target = target.replace("/", "\\");
				source = source.replace("/", "\\");
				CommUtil.copyFile(source, target);
				snap.setGood_mian_img(uploadFilePath + "/" + "snapshot/"
						+ timeStamp + main_img.getName());
			} else {
				snap.setGood_mian_img(null);
			}
			this.snapshotService.saveEntity(snap);
			if (order.getChild_order_detail() != null) {
				List<Map> child_maps = queryGoodsInfo(order
						.getChild_order_detail());
				for (Map c_map : child_maps) {
					OrderForm c_order = this.orderFormService
							.selectByPrimaryKey(CommUtil.null2Long(c_map
									.get("order_id")));
					createOrderGoodsSnapshot(c_order, localPath, buyer);
				}
			}
		}
	}

	public String queryOrderGoodsSnapshot(String order_id, String goods_id) {
		String path = "";
		Map<String, Object> params = Maps.newHashMap();
		params.put("order_id", order_id);
		params.put("good_id", goods_id);
		params.put("user_id", SecurityUserHolder.getCurrentUser().getId());
		List<Snapshot> Snapshots = this.snapshotService.queryPageList(params,
				0, 1);
		if (Snapshots.size() > 0) {
			path = CommUtil.null2String(((Snapshot) Snapshots.get(0)).getId());
		}
		return path;
	}

	public double queryOrderShipPrice(String order_id) {
		double all_price = 0.0D;
		OrderForm order = this.orderFormService.selectByPrimaryKey(CommUtil
				.null2Long(order_id));
		if (order != null) {
			all_price = CommUtil.null2Double(order.getShip_price());
			if ((order.getChild_order_detail() != null)
					&& (!order.getChild_order_detail().equals(""))) {
				List<Map> maps = queryGoodsInfo(order.getChild_order_detail());
				for (Map map : maps) {
					OrderForm child_order = this.orderFormService
							.selectByPrimaryKey(CommUtil.null2Long(map
									.get("order_id")));

					all_price = all_price
							+ CommUtil.null2Double(child_order.getShip_price());
				}
			}
		}
		return all_price;
	}

	public double queryOrderCancleShipPrice(String order_id) {
		double all_price = 0.0D;
		OrderForm order = this.orderFormService.selectByPrimaryKey(CommUtil
				.null2Long(order_id));
		if (order != null) {
			if (order.getOrder_status() > 0) {
				all_price = CommUtil.null2Double(order.getShip_price());
			}
			if ((order.getChild_order_detail() != null)
					&& (!order.getChild_order_detail().equals(""))) {
				List<Map> maps = queryGoodsInfo(order.getChild_order_detail());
				for (Map map : maps) {
					OrderForm child_order = this.orderFormService
							.selectByPrimaryKey(CommUtil.null2Long(map
									.get("order_id")));
					if (child_order.getOrder_status() > 0) {
						all_price = all_price
								+ CommUtil.null2Double(child_order
										.getShip_price());
					}
				}
			}
		}
		return all_price;
	}

	public List<Map> getGoodsAdvanceInfo(String goods_id) {
		List<Map> ad_list = Lists.newArrayList();
		if ((goods_id != null) && (!"".equals(goods_id))) {
			Goods g = this.goodsService.selectByPrimaryKey(CommUtil
					.null2Long(goods_id));
			if ((g != null) && (g.getAdvance_sale_type() == 1)
					&& (g.getAdvance_sale_info() != null)
					&& (!"".equals(g.getAdvance_sale_info()))) {
				ad_list = JSON.parseArray(g.getAdvance_sale_info(), Map.class);
			}
		}
		return ad_list;
	}

	public void updateOrderStatus(String id, String status) {
		if ((id != null) && (!"".equals(id))) {
			OrderForm of = this.orderFormService.selectByPrimaryKey(CommUtil
					.null2Long(id));
			if (of != null) {
				List<Goods> goods_list = queryOfGoods(of);
				List<Map> advance_list = Lists.newArrayList();
				List list;
				for (Goods goods : goods_list) {
					list = getGoodsAdvanceInfo(CommUtil.null2String(goods
							.getId()));
					advance_list.addAll(list);
				}
				if ((status.equals("1")) && (advance_list.size() > 0)) {
					if (of.getOrder_status() == 11) {
						of.setOrder_status(0);
					} else if (of.getOrder_status() == 10) {
						of.setOrder_status(0);
					}
				} else if (advance_list.size() == 0) {
					if (of.getChild_order_detail() != null) {
						List<Map> maps = JSON.parseArray(
								of.getChild_order_detail(), Map.class);
						for (Map m : maps) {
							OrderForm ch = this.orderFormService
									.selectByPrimaryKey(CommUtil.null2Long(m
											.get("order_id")));
							if ((ch != null) && (ch.getOrder_status() == 10)) {
								ch.setOrder_status(0);
							}
							this.orderFormService.updateById(ch);
						}
					}
					if (of.getOrder_status() == 10) {
						of.setOrder_status(0);
					}
				}
				this.orderFormService.updateById(of);
			}
		}
	}

	public Boolean updateAdvanceStatus(OrderForm main_order) {
		Date nowDate = new Date();
		Boolean flag = Boolean.valueOf(false);
		if (main_order != null) {
			List<Goods> goods_list = queryOfGoods(main_order);
			List<Map> advance_list = Lists.newArrayList();
			for (Goods goods : goods_list) {
				List list = getGoodsAdvanceInfo(CommUtil.null2String(goods
						.getId()));
				advance_list.addAll(list);
			}
			if (advance_list.size() > 0) {
				for (Map map : advance_list) {
					Date startDate = CommUtil.formatDate(CommUtil
							.null2String(map.get("rest_start_date")));
					Date endDate = CommUtil.formatDate(CommUtil.null2String(map
							.get("rest_end_date")));
					if (main_order.getOrder_status() == 10) {
//						if ((nowDate.getTime() - startDate.getTime()) >= 1800000L) {
//							main_order.setOrder_status(0);
//							flag = Boolean.valueOf(true);
//						}
					} else if (main_order.getOrder_status() == 11) {
						if (nowDate.after(endDate)) {
							main_order.setOrder_status(0);
							flag = Boolean.valueOf(true);
						} else if (nowDate.before(startDate)) {
							flag = Boolean.valueOf(true);
						}
					}
				}
				this.orderFormService.updateById(main_order);
			}
		}
		return flag;
	}
	
	/**
	 * 
	 * orderForm_verify:订单校验. <br/>
	 *
	 * @author redpig
	 * @param request
	 * @param response
	 * @param mv
	 * @param order 订单
	 * @param buyer 买家
	 * @return
	 * @since JDK 1.8
	 */
	public Map<Boolean, ModelAndView> orderForm_verify(
			HttpServletRequest request, HttpServletResponse response,
			ModelAndView mv, OrderForm order, User buyer) {
		Map<Boolean, ModelAndView> map = Maps.newHashMap();
		String return_mv = "error.html";
		String return_url = CommUtil.getURL(request) + "/buyer/order";
		String user_agent = request.getHeader("User-Agent").toLowerCase();
		if ((user_agent.indexOf("iphone") >= 0)
				|| (user_agent.indexOf("android") >= 0)) {
			return_mv = "weixin/error";
			return_url = CommUtil.getURL(request) + "/buyer/order_list";
		}
		SysConfig obj = this.configService.getSysConfig();
		int minute = obj.getAuto_order_cancel() * 60;
		Date nowDate = new Date();
		int order_status = query_order_status(CommUtil.null2String(order.getId()));
		if ((order == null) || (!order.getUser_id().equals(buyer.getId().toString()))) {
			mv = new RedPigJModelAndView(return_mv,
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "参数错误，该订单已失效");
			mv.addObject("url", return_url);
			map.put(Boolean.valueOf(false), mv);
			return map;
		}
		if (order_status < 10) {
			mv = new RedPigJModelAndView(return_mv,
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "该订单已经取消");
			mv.addObject("url", return_url);
			map.put(Boolean.valueOf(false), mv);
			return map;
		}
		if (order_status >= 16) {
			mv = new RedPigJModelAndView(return_mv,
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "该订单已付款");
			mv.addObject("url", return_url);
			map.put(Boolean.valueOf(false), mv);
			return map;
		}
		if (((order.getOrder_special() == null) || (order.getOrder_special().equals("")))
				&& (nowDate.getTime() > (order.getAddTime().getTime() + minute * 60 * 1000))) {
			updateOrderStatus(CommUtil.null2String(order.getId()), "2");
			mv = new RedPigJModelAndView(return_mv,
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "该订单支付超时已关闭");
			mv.addObject("url", return_url);
			map.put(Boolean.valueOf(false), mv);
			return map;
		}
		if ((order.getOrder_special() != null)
				&& (order.getOrder_special().equals("advance"))
				&& (updateAdvanceStatus(order).booleanValue())) {
			mv = new RedPigJModelAndView(return_mv,
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "该订单支付超时已关闭");
			mv.addObject("url", return_url);
			map.put(Boolean.valueOf(false), mv);
			return map;
		}
		if (((order_status == 10) || (order_status == 11))
				&& (order.getOrder_cat() != 1)) {
			boolean exist = verify_goods_exist(order);
			if (!exist) {
				mv = new RedPigJModelAndView(return_mv,
						this.configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 1, request,
						response);
				mv.addObject("op_title", "订单中商品已被删除，请重新下单");
				mv.addObject("url", return_url);
				map.put(Boolean.valueOf(false), mv);
				return map;
			}
			boolean inventory_very = verify_goods_Inventory(order);
			if (!inventory_very) {
				mv = new RedPigJModelAndView(return_mv,
						this.configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 1, request,
						response);
				mv.addObject("op_title", "订单中商品库存不足，请重新下单");
				mv.addObject("url", return_url);
				map.put(Boolean.valueOf(false), mv);
				return map;
			}
		}
		map.put(Boolean.valueOf(true), mv);
		return map;
	}

	public String queryExInfo(String json, String key) {
		Map<String, Object> map = Maps.newHashMap();
		if ((json != null) && (!json.equals(""))) {
			map = JSON.parseObject(json);
		}
		return CommUtil.null2String(map.get(key));
	}

	private ExpressCompany queryExpressCompany(String json) {
		ExpressCompany ec = null;
		if ((json != null) && (!json.equals(""))) {
			Map<String, Object> map = JSON.parseObject(json);
			ec = this.redPigExpressCompanyService.selectByPrimaryKey(CommUtil
					.null2Long(map.get("express_company_id")));
		}
		return ec;
	}

	public TransInfo query_ship_getData(String id) {
		TransInfo info = new TransInfo();
		OrderForm obj = this.redPigOrderFormService.selectByPrimaryKey(CommUtil
				.null2Long(id));

		if ((obj != null)
				&& (!CommUtil.null2String(obj.getShipCode()).equals(""))) {

			if (this.redPigSysConfigService.getSysConfig().getKuaidi_type() == 0) {
				try {
					ExpressCompany ec = queryExpressCompany(obj
							.getExpress_info());

					String query_url = "http://api.kuaidi100.com/api?id="
							+ this.redPigSysConfigService.getSysConfig()
									.getKuaidi_id() + "&com="
							+ (ec != null ? ec.getCompany_mark() : "") + "&nu="
							+ obj.getShipCode() + "&show=0&muti=1&order=asc";

					URL url = new URL(query_url);
					URLConnection con = url.openConnection();
					con.setAllowUserInteraction(false);
					InputStream urlStream = url.openStream();
					String type = URLConnection
							.guessContentTypeFromStream(urlStream);

					String charSet = null;
					if (type == null) {
						type = con.getContentType();
					}

					if ((type == null) || (type.trim().length() == 0)
							|| (type.trim().indexOf("text/html") < 0)) {
						return info;
					}

					if (type.indexOf("charset=") > 0) {
						charSet = type.substring(type.indexOf("charset=") + 8);
					}

					byte[] b = new byte[10000];
					int numRead = urlStream.read(b);
					String content = new String(b, 0, numRead, charSet);
					while (numRead != -1) {
						numRead = urlStream.read(b);
						if (numRead != -1) {
							String newContent = new String(b, 0, numRead,
									charSet);
							content = content + newContent;
						}
					}

					info = (TransInfo) JSON.parseObject(content,
							TransInfo.class);

					urlStream.close();
				} catch (MalformedURLException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			if (this.redPigSysConfigService.getSysConfig().getKuaidi_type() == 1) {
				Map<String, Object> maps = Maps.newHashMap();
				maps.put("order_id", obj.getId());
				maps.put("order_type", 0);

				List<ExpressInfo> eis = this.redPigExpressInfoService
						.queryPageList(maps);

				if (eis != null && eis.size() > 0) {
					List<TransContent> data = (List) JSON.parseObject(CommUtil
							.null2String(eis.get(0).getOrder_express_info()),
							new TypeReference() {
							}, new Feature[0]);

					info.setData(data);
					info.setStatus("1");
				}
			}
		}
		return info;
	}

	// 根据user_id查询user
	public User queryUserById(String id) {
		return this.userService.selectByPrimaryKey(CommUtil.null2Long(id));
	}

	// 根据goodsid查询goods
	public Goods queryGoodsById(String id) {
		return this.goodsService.selectByPrimaryKey(CommUtil.null2Long(id));
	}

	// 根据nukeid查询nuke
	public Nuke queryNukeById(String id) {
		return this.nukeService.selectByPrimaryKey(CommUtil.null2Long(id));
	}

	/**
	 * 根据条件查询秒杀订单数目
	 * @param goods_id
	 * @param nuke_id
	 * @return
	 */
	public String queryNukeOrderCountByCondition(String goods_id,String nuke_id){
		Long count = 0l;
		Map<String,Object> params = new HashMap<>();
		params.put("order_status_more_than_equal",20);//已付款订单*/
		//params.put("goods_info_like","\"goods_id\":\"\"");//商品信息类似于如此
		params.put("order_cat",3);//秒杀订单*/
		List<OrderForm> orderForms = this.orderFormService.queryPageListWithNoRelations(params);
		// 查询得到订单，遍历订单，找到商品,判断是否参与了活动
		for (OrderForm orderForm:orderForms){
			String goodsInfo = orderForm.getGoods_info();
			// 解析商品json
			List<Map> maps = this.queryGoodsInfo(goodsInfo);
			for (Map map:maps){
				System.out.println(map.get("goods_id"));
				String goodsId = map.get("goods_id").toString();
				// 一个订单有多个商品，查找我们需要的商品，看有没有参加秒杀活动
				if (StringUtils.isNotBlank(goodsId)&&goods_id.equals(goodsId)){
					Goods goods = this.goodsService.selectByPrimaryKey(Long.parseLong(goodsId));
					// 如果参与了+1
					if ((goods.getNuke().getId().toString()).equals(nuke_id)){
						count++;
						break;
					}
				}
			}
		}
		return count.toString();
	}

	/**
	 * 根据条件查询买家人数
	 * @param goods_id
	 * @param nuke_id
	 * @return
	 */
	public String  queryUserCountByCondition(String goods_id,String nuke_id){
		Long count = 0l;
		Map<String,Object> params = new HashMap<>();
		params.put("order_status_more_than_equal",20);//已付款订单
		//params.put("goods_info_like","'goods_id':"+goods_id);//商品信息类似于如此
		params.put("order_cat",3);//秒杀订单
		count = Long.valueOf(this.orderFormService.selectCount(params));
		return count.toString();
	}

	/**
	 *通过商品和拼团信息查找订单信息
	 * @param goods_id
	 * @param collage_id
	 * @return
	 */
	public Map<String,Object> queryCollageOrderInfoByCondition(String goods_id,String collage_id){
		Map<String,Object> params = new HashMap<>();
		Long order_count = 0l;
		BigDecimal total_price = new BigDecimal("0.00");
		params.put("order_status_more_than_equal",20);//已付款订单
		params.put("order_cat",4);//拼团订单
		List<OrderForm> orderForms = this.orderFormService.queryPageListWithNoRelations(params);
		// 找到商品，看是否参与了拼团活动
		if (orderForms!=null&&!orderForms.isEmpty()){
			for (OrderForm orderForm:orderForms){
				// 解析商品json
				String goodsInfo = orderForm.getGoods_info();
				List<Map> maps = this.queryGoodsInfo(goodsInfo);
				for (Map map:maps){
					System.out.println(map.get("goods_id"));
					String goodsId = map.get("goods_id").toString();
					// 一个订单有多个商品，查找我们需要的商品，看有没有参加秒杀活动
					if (StringUtils.isNotBlank(goodsId)&&goods_id.equals(goodsId)){
						Goods goods = this.goodsService.selectByPrimaryKey(Long.parseLong(goodsId));
						// 如果参与了+1
						if (goods.getCollage().getId()==Long.parseLong(collage_id)){
							order_count++;
							total_price = total_price.add(orderForm.getTotalPrice());
							break;
						}
					}
				}
			}
		}
		params.put("order_count",order_count);
		params.put("order_total_price",total_price);
		return params;
	}
}
