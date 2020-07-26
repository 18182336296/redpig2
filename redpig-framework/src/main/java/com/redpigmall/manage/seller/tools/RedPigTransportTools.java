package com.redpigmall.manage.seller.tools;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.redpigmall.api.domain.virtual.CglibBean;
import com.redpigmall.api.domain.virtual.SysMap;
import com.redpigmall.api.sec.SecurityUserHolder;
import com.redpigmall.api.tools.CommUtil;
import com.redpigmall.domain.Area;
import com.redpigmall.domain.ExpressCompanyCommon;
import com.redpigmall.domain.Goods;
import com.redpigmall.domain.GoodsCart;
import com.redpigmall.domain.Store;
import com.redpigmall.domain.Transport;
import com.redpigmall.domain.User;
import com.redpigmall.service.RedPigAreaService;
import com.redpigmall.service.RedPigExpressCompanyCommonService;
import com.redpigmall.service.RedPigGoodsService;
import com.redpigmall.service.RedPigTransportService;
import com.redpigmall.service.RedPigUserService;

/**
 * 
 * <p>
 * Title: TransportTools.java
 * </p>
 * 
 * <p>
 * Description:运费模板工具类，用来处理运费模板相关信息
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
 * @date 2014-11-14
 * 
 * @version redpigmall_b2b2c 2016
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
@Component
public class RedPigTransportTools {
	@Autowired
	private RedPigTransportService transportService;
	@Autowired
	private RedPigAreaService areaService;
	@Autowired
	private RedPigGoodsService goodsService;
	@Autowired
	private RedPigUserService userService;

	@Autowired
	private RedPigExpressCompanyCommonService expressCompanyCommonService;

	/**
	 * 根据json查询对应的运费数据信息
	 * 
	 * @param json
	 * @param mark
	 * @return
	 */
	public String query_transprot(String json, String mark) {
		String ret = "";
		if ((json != null) && (!"".equals(json))) {
			List<Map> list = JSON.parseArray(json, Map.class);
			if ((list != null) && (list.size() > 0)) {
				for (Map map : list) {
					if (CommUtil.null2String(map.get("city_id")).equals("-1")) {
						ret = CommUtil.null2String(map.get(mark));
					}
				}
			}
		}
		return ret;
	}

	/**
	 * 解析出运费模板列表
	 * 
	 * @param json
	 *            运费json数据
	 * @param type
	 *            0为解析所有信息（包含全国配送），1为解析所有区域配送信息
	 * @return 运费模板列表信息
	 * @throws ClassNotFoundException
	 */
	
	public List<CglibBean> query_all_transprot(String json, int type)
			throws ClassNotFoundException {
		List<CglibBean> cbs = Lists.newArrayList();
		if ((json != null) && (!json.equals(""))) {
			List<Map> list = JSON.parseArray(CommUtil.null2String(json),
					Map.class);
			if ((list != null) && (list.size() > 0)) {
				if (type == 0) {
					for (Map map : list) {
						Map<String,Object> propertyMap = Maps.newHashMap();
						propertyMap.put("city_id",
								Class.forName("java.lang.String"));
						propertyMap.put("city_name",
								Class.forName("java.lang.String"));
						propertyMap.put("trans_weight",
								Class.forName("java.lang.String"));
						propertyMap.put("trans_fee",
								Class.forName("java.lang.String"));
						propertyMap.put("trans_add_weight",
								Class.forName("java.lang.String"));
						propertyMap.put("trans_add_fee",
								Class.forName("java.lang.String"));
						CglibBean cb = new CglibBean(propertyMap);
						cb.setValue("city_id",
								CommUtil.null2String(map.get("city_id")));
						cb.setValue("city_name",
								CommUtil.null2String(map.get("city_name")));
						cb.setValue("trans_weight",
								CommUtil.null2String(map.get("trans_weight")));
						cb.setValue("trans_fee",
								CommUtil.null2String(map.get("trans_fee")));
						cb.setValue("trans_add_weight", CommUtil
								.null2String(map.get("trans_add_weight")));
						cb.setValue("trans_add_fee",
								CommUtil.null2String(map.get("trans_add_fee")));
						cbs.add(cb);
					}
				}
				if (type == 1) {
					for (Map map : list) {
						if (!CommUtil.null2String(map.get("city_id")).equals(
								"-1")) {
							HashMap propertyMap = Maps.newHashMap();
							propertyMap.put("city_id",
									Class.forName("java.lang.String"));
							propertyMap.put("city_name",
									Class.forName("java.lang.String"));
							propertyMap.put("trans_weight",
									Class.forName("java.lang.String"));
							propertyMap.put("trans_fee",
									Class.forName("java.lang.String"));
							propertyMap.put("trans_add_weight",
									Class.forName("java.lang.String"));
							propertyMap.put("trans_add_fee",
									Class.forName("java.lang.String"));
							CglibBean cb = new CglibBean(propertyMap);
							cb.setValue("city_id",
									CommUtil.null2String(map.get("city_id")));
							cb.setValue("city_name",
									CommUtil.null2String(map.get("city_name")));
							cb.setValue("trans_weight", CommUtil
									.null2String(map.get("trans_weight")));
							cb.setValue("trans_fee",
									CommUtil.null2String(map.get("trans_fee")));
							cb.setValue("trans_add_weight", CommUtil
									.null2String(map.get("trans_add_weight")));
							cb.setValue("trans_add_fee", CommUtil
									.null2String(map.get("trans_add_fee")));
							cbs.add(cb);
						}
					}
				}
			}
		}
		return cbs;
	}

	/**
	 * 根据运费模板信息、商品重量及配送城市计算商品运费，配送城市根据IP自动获取
	 * 
	 * @param json
	 * @param weight
	 * @param city_name
	 * @return 商品的配送费用
	 */
	public float cal_goods_trans_fee(String trans_id, String type,
			String weight, String volume, String city_name) {
		Transport trans = this.transportService.selectByPrimaryKey(CommUtil
				.null2Long(trans_id));
		String json = "";
		if (type.equals("mail")) {
			json = trans.getTrans_mail_info();
		}
		if (type.equals("express")) {
			json = trans.getTrans_express_info();
		}
		if (type.equals("ems")) {
			json = trans.getTrans_ems_info();
		}
		float fee = 0.0F;
		boolean cal_flag = false;
		if (json != null) {
			List<Map> list = JSON.parseArray(CommUtil.null2String(json),
					Map.class);
			if ((list != null) && (list.size() > 0)) {
				for (Map map : list) {
					String[] city_list = CommUtil.null2String(
							map.get("city_name")).split("、");
					for (String city : city_list) {
						if ((city_name.indexOf(city) >= 0)
								|| (city.equals(city_name))) {
							cal_flag = true;
							float trans_weight = CommUtil.null2Float(map
									.get("trans_weight"));
							float trans_fee = CommUtil.null2Float(map
									.get("trans_fee"));
							float trans_add_weight = CommUtil.null2Float(map
									.get("trans_add_weight"));
							float trans_add_fee = CommUtil.null2Float(map
									.get("trans_add_fee"));
							if (trans.getTrans_type() == 0) {
								fee = trans_fee;
							}
							if (trans.getTrans_type() == 1) {
								float goods_weight = CommUtil
										.null2Float(weight);
								if (goods_weight > 0.0F) {
									fee = trans_fee;
									float other_price = 0.0F;
									if (trans_add_weight > 0.0F) {
										other_price = (float) (trans_add_fee * Math
												.ceil(CommUtil.subtract(
														Float.valueOf(goods_weight),
														Float.valueOf(trans_weight))
														/ trans_add_weight));
									}
									fee += other_price;
								}
							}
							if (trans.getTrans_type() != 2) {
								break;
							}
							float goods_volume = CommUtil.null2Float(volume);
							if (goods_volume <= 0.0F) {
								break;
							}
							fee = trans_fee;
							float other_price = 0.0F;
							if (trans_add_weight > 0.0F) {
								other_price = (float) (trans_add_fee * Math
										.ceil(CommUtil.subtract(
												Float.valueOf(goods_volume),
												Float.valueOf(trans_weight))
												/ trans_add_weight));
							}
							fee += other_price;

							break;
						}
					}
				}
				if (!cal_flag) {
					for (Map map : list) {
						String[] city_list = CommUtil.null2String(
								map.get("city_name")).split("、");
						for (String city : city_list) {
							if (city.equals("全国")) {
								float trans_weight = CommUtil.null2Float(map
										.get("trans_weight"));
								float trans_fee = CommUtil.null2Float(map
										.get("trans_fee"));
								float trans_add_weight = CommUtil
										.null2Float(map.get("trans_add_weight"));
								float trans_add_fee = CommUtil.null2Float(map
										.get("trans_add_fee"));
								if (trans.getTrans_type() == 0) {
									fee = trans_fee;
								}
								if (trans.getTrans_type() == 1) {
									float goods_weight = CommUtil
											.null2Float(weight);
									if (goods_weight > 0.0F) {
										fee = trans_fee;
										float other_price = 0.0F;
										if (trans_add_weight > 0.0F) {
											other_price = (float) (trans_add_fee * Math
													.ceil(CommUtil.subtract(
															Float.valueOf(goods_weight),
															Float.valueOf(trans_weight))
															/ trans_add_weight));
										}
										fee += other_price;
									}
								}
								if (trans.getTrans_type() != 2) {
									break;
								}
								float goods_volume = CommUtil
										.null2Float(volume);
								if (goods_volume <= 0.0F) {
									break;
								}
								fee = trans_fee;
								float other_price = 0.0F;
								if (trans_add_weight > 0.0F) {
									other_price = (float) (trans_add_fee * Math
											.ceil(CommUtil.subtract(
													Float.valueOf(goods_volume),
													Float.valueOf(trans_weight))
													/ trans_add_weight));
								}
								fee += other_price;

								break;
							}
						}
					}
				}
			}
		}
		return fee;
	}

	public List<SysMap> query_cart_trans(List<GoodsCart> carts, String area_id) {
		List<SysMap> sms = Lists.newArrayList();

		List<List<GoodsCart>> list = group_carts(carts);
		if ((area_id != null) && (!area_id.equals(""))) {
			Area area = this.areaService.selectByPrimaryKey(CommUtil.null2Long(area_id)).getParent();
			String city_name = area.getAreaName();

			float mail_fee = 0.0F;
			float express_fee = 0.0F;
			float ems_fee = 0.0F;
			String fee_tip = "商家承担";
			for (List<GoodsCart> cart_single_template : list) {
				Transport transport = null;
				BigDecimal weight = new BigDecimal(0);
				BigDecimal volume = new BigDecimal(0);
				BigDecimal price = new BigDecimal(0);
				int count = 0;
				for (GoodsCart gc : cart_single_template) {
					Goods goods = this.goodsService.selectByPrimaryKey(gc
							.getGoods().getId());
					if (goods.getGoods_transfee() == 0) {
						if (goods.getTransport() != null) {
							transport = goods.getTransport();

							weight = weight.add(new BigDecimal(CommUtil
									.null2Double(goods.getGoods_weight()
											.multiply(new BigDecimal(gc.getCount())))));
							
							volume = volume.add(new BigDecimal(
									CommUtil.null2Double(goods.getGoods_volume()
											.multiply(new BigDecimal(gc.getCount())))));
							count += gc.getCount();
							price = price.add(gc.getPrice().multiply(new BigDecimal(gc.getCount())));
						} else {
							mail_fee = mail_fee
									+ CommUtil.null2Float(goods
											.getMail_trans_fee());

							express_fee = express_fee
									+ CommUtil.null2Float(goods
											.getExpress_trans_fee());

							ems_fee = ems_fee
									+ CommUtil.null2Float(goods
											.getEms_trans_fee());
						}
					}
				}
				if (transport != null) {
					if ((transport.getFree_postage_status() == 1) && (price.compareTo(transport.getFree_postage()) >= 0)) {
						fee_tip = "包邮";
					} else {
						mail_fee = mail_fee
								+ cal_order_trans(
										transport.getTrans_mail_info(),
										transport.getTrans_type(), weight,
										volume, city_name, count);

						express_fee = express_fee
								+ cal_order_trans(
										transport.getTrans_express_info(),
										transport.getTrans_type(), weight,
										volume, city_name, count);

						ems_fee = ems_fee
								+ cal_order_trans(
										transport.getTrans_ems_info(),
										transport.getTrans_type(), weight,
										volume, city_name, count);
					}
				}
			}
			if ((mail_fee == 0.0F) && (express_fee == 0.0F) && (ems_fee == 0.0F)) {
				sms.add(new SysMap(fee_tip, Integer.valueOf(0)));
			} else if (carts.size() == 1) {
				if (carts.get(0).getGoods().getTransport() != null) {
					if (carts.get(0).getGoods().getTransport().getTrans_mail()) {
						sms.add(new SysMap("平邮[" + mail_fee + "元]", Float.valueOf(mail_fee)));
					}
					if ( carts.get(0).getGoods().getTransport() != null) {
						if (carts.get(0).getGoods().getTransport().getTrans_express()) {
							sms.add(new SysMap("快递[" + express_fee + "元]",Float.valueOf(express_fee)));
						}
					}
					if (carts.get(0).getGoods().getTransport() != null) {
						if (((GoodsCart) carts.get(0)).getGoods().getTransport().getTrans_ems()) {
							sms.add(new SysMap("EMS[" + ems_fee + "元]", Float.valueOf(ems_fee)));
						}
					}
				} else {
					if (mail_fee > 0.0F) {
						sms.add(new SysMap("平邮[" + mail_fee + "元]", Float.valueOf(mail_fee)));
					}
					if (express_fee > 0.0F) {
						sms.add(new SysMap("快递[" + express_fee + "元]", Float.valueOf(express_fee)));
					}
					if (ems_fee > 0.0F) {
						sms.add(new SysMap("EMS[" + ems_fee + "元]", Float.valueOf(ems_fee)));
					}
				}
			} else {
				if (mail_fee > 0.0F) {
					sms.add(new SysMap("平邮[" + mail_fee + "元]", Float.valueOf(mail_fee)));
				}
				if (express_fee > 0.0F) {
					sms.add(new SysMap("快递[" + express_fee + "元]", Float.valueOf(express_fee)));
				}
				if (ems_fee > 0.0F) {
					sms.add(new SysMap("EMS[" + ems_fee + "元]", Float.valueOf(ems_fee)));
				}
			}
		}
		return sms;
	}

	
	public List<SysMap> query_cart_trans(List<GoodsCart> carts,
			Map<Long, List<GoodsCart>> er_goods,
			Map<Goods, List<GoodsCart>> ac_goods, String area_id) {
		if (er_goods != null) {
			for (Long id : er_goods.keySet()) {
				List<GoodsCart> list = (List) er_goods.get(id);
				carts.addAll(list);
			}
		}
		if (ac_goods != null) {
			for (Goods id : ac_goods.keySet()) {
				List<GoodsCart> list = (List) ac_goods.get(id);
				carts.addAll(list);
			}
		}
		return query_cart_trans(carts, area_id);
	}

	private float cal_order_trans(String trans_json, int trans_type,
			Object goods_weight, Object goods_volume, String city_name,
			int count) {
		float fee = 0.0F;
		boolean cal_flag = false;
		if ((trans_json != null) && (!trans_json.equals(""))) {
			List<Map> list = JSON.parseArray(trans_json, Map.class);
			if ((list != null) && (list.size() > 0)) {
				for (Map map : list) {
					String[] city_list = CommUtil.null2String(
							map.get("city_name")).split("、");
					for (String city : city_list) {
						if ((city.equals(city_name))
								|| (city_name.indexOf(city) == 0)) {
							cal_flag = true;
							float trans_weight = CommUtil.null2Float(map
									.get("trans_weight"));
							float trans_fee = CommUtil.null2Float(map
									.get("trans_fee"));
							float trans_add_weight = CommUtil.null2Float(map
									.get("trans_add_weight"));
							float trans_add_fee = CommUtil.null2Float(map
									.get("trans_add_fee"));
							if (trans_type == 0) {
								if (CommUtil.null2Int(Integer.valueOf(count)) > (int) trans_weight) {
									fee = trans_fee;
									float other_price = 0.0F;
									if (trans_add_weight > 0.0F) {
										other_price = trans_add_fee
												* (float) Math
														.round(Math.ceil(CommUtil.subtract(
																Integer.valueOf(count),
																Float.valueOf(trans_weight))
																/ trans_add_weight));
									}
									fee += other_price;
								} else {
									fee = trans_fee;
								}
							}
							if (trans_type == 1) {
								double total_weight = CommUtil
										.null2Double(goods_weight);
								if (CommUtil.null2Double(Double
										.valueOf(total_weight)) > CommUtil
										.null2Double(Float
												.valueOf(trans_weight))) {
									fee = trans_fee;
									float other_price = 0.0F;
									if (trans_add_weight > 0.0F) {
										other_price = trans_add_fee
												* (float) Math
														.round(Math.ceil(CommUtil.subtract(
																Double.valueOf(total_weight),
																Float.valueOf(trans_weight))
																/ trans_add_weight));
									}
									fee += other_price;
								} else {
									fee = trans_fee;
								}
							}
							if (trans_type != 2) {
								break;
							}
							double total_volume = CommUtil
									.null2Double(goods_volume);
							if (CommUtil.null2Double(Double
									.valueOf(total_volume)) > CommUtil
									.null2Double(Float.valueOf(trans_weight))) {
								fee = trans_fee;
								float other_price = 0.0F;
								if (trans_add_weight > 0.0F) {
									other_price = trans_add_fee
											* (float) Math
													.round(Math.ceil(CommUtil.subtract(
															Double.valueOf(total_volume),
															Float.valueOf(trans_weight))
															/ trans_add_weight));
								}
								fee += other_price;
								break;
							}
							fee = trans_fee;

							break;
						}
					}
				}
				if (!cal_flag) {
					for (Map map : list) {
						String[] city_list = CommUtil.null2String(
								map.get("city_name")).split("、");
						for (String city : city_list) {
							if (city.equals("全国")) {
								float trans_weight = CommUtil.null2Float(map
										.get("trans_weight"));
								float trans_fee = CommUtil.null2Float(map
										.get("trans_fee"));
								float trans_add_weight = CommUtil
										.null2Float(map.get("trans_add_weight"));
								float trans_add_fee = CommUtil.null2Float(map
										.get("trans_add_fee"));
								if (trans_type == 0) {
									if (CommUtil.null2Int(Integer
											.valueOf(count)) > (int) trans_weight) {
										fee = trans_fee;
										float other_price = 0.0F;
										if (trans_add_weight > 0.0F) {
											other_price = trans_add_fee
													* (float) Math
															.round(Math.ceil(CommUtil.subtract(
																	Integer.valueOf(count),
																	Float.valueOf(trans_weight))
																	/ trans_add_weight));
										}
										fee += other_price;
									} else {
										fee = trans_fee;
									}
								}
								if (trans_type == 1) {
									double total_weight = CommUtil
											.null2Double(goods_weight);
									if (CommUtil.null2Float(Double
											.valueOf(total_weight)) > CommUtil
											.null2Double(Float
													.valueOf(trans_weight))) {
										fee = trans_fee;
										float other_price = 0.0F;
										if (trans_add_weight > 0.0F) {
											other_price = trans_add_fee
													* (float) Math
															.round(Math.ceil(CommUtil.subtract(
																	Double.valueOf(total_weight),
																	Float.valueOf(trans_weight))
																	/ trans_add_weight));
										}
										fee += other_price;
									} else {
										fee = trans_fee;
									}
								}
								if (trans_type != 2) {
									break;
								}
								double total_volume = CommUtil
										.null2Double(goods_volume);
								if (CommUtil.null2Float(Double
										.valueOf(total_volume)) > CommUtil
										.null2Double(Float
												.valueOf(trans_weight))) {
									fee = trans_fee;
									float other_price = 0.0F;
									if (trans_add_weight > 0.0F) {
										other_price = trans_add_fee
												* (float) Math
														.round(Math.ceil(CommUtil.subtract(
																Double.valueOf(total_volume),
																Float.valueOf(trans_weight))
																/ trans_add_weight));
									}
									fee += other_price;
									break;
								}
								fee = trans_fee;

								break;
							}
						}
					}
				}
			}
		}
		return fee;
	}

	public int query_common_ec(String id) {
		int ret = 0;
		if (!CommUtil.null2String(id).equals("")) {
			Map<String, Object> params = Maps.newHashMap();
			User user = this.userService.selectByPrimaryKey(SecurityUserHolder
					.getCurrentUser().getId());
			user = user.getParent() == null ? user : user.getParent();
			Store store = user.getStore();
			List<ExpressCompanyCommon> eccs = Lists.newArrayList();
			if ((store != null) && (user.getUserRole().indexOf("SELLER") >= 0)) {
				params.put("ecc_type", Integer.valueOf(0));
				params.put("ecc_store_id", store.getId());
				eccs = this.expressCompanyCommonService.queryPageList(params);

				for (ExpressCompanyCommon ecc : eccs) {
					if (ecc.getEcc_ec_id().equals(CommUtil.null2Long(id))) {
						ret = 1;
					}
				}
			} else {
				params.put("ecc_type", Integer.valueOf(1));
				eccs = this.expressCompanyCommonService.queryPageList(params);

				for (ExpressCompanyCommon ecc : eccs) {
					if (ecc.getEcc_ec_id().equals(CommUtil.null2Long(id))) {
						ret = 1;
					}
				}
			}
		}
		return ret;
	}

	private List<List<GoodsCart>> group_carts(List<GoodsCart> cart_list) {
		List<GoodsCart> carts = (List) ((ArrayList) cart_list).clone();
		List<List<GoodsCart>> list = Lists.newArrayList();
		for (int i = 0; i < carts.size();) {
			GoodsCart myObject = (GoodsCart) carts.get(i);
			carts.remove(myObject);
			List<GoodsCart> gcs = Lists.newArrayList();
			gcs.add(myObject);
			for (int j = 0; j < carts.size();) {
				GoodsCart _myObject = (GoodsCart) carts.get(j);
				if ((_myObject.getGoods().getTransport() != null)
						&& (myObject.getGoods().getTransport() != null)) {
					if (_myObject.getGoods().getTransport().getId()
							.equals(myObject.getGoods().getTransport().getId())) {
						gcs.add(_myObject);
						carts.remove(_myObject);
						continue;
					}
				}
				j++;
			}
			list.add(gcs);
		}
		return list;
	}
}
