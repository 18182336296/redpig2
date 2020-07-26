package com.redpigmall.view.web.tools;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.parser.Feature;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.redpigmall.api.sec.SecurityUserHolder;
import com.redpigmall.api.tools.CommUtil;
import com.redpigmall.api.tools.ObjectUtils;
import com.redpigmall.api.tools.StringUtils;
import com.redpigmall.domain.Accessory;
import com.redpigmall.domain.Activity;
import com.redpigmall.domain.ActivityGoods;
import com.redpigmall.domain.BuyGift;
import com.redpigmall.domain.CombinPlan;
import com.redpigmall.domain.EnoughReduce;
import com.redpigmall.domain.FootPoint;
import com.redpigmall.domain.Goods;
import com.redpigmall.domain.GoodsCart;
import com.redpigmall.domain.GoodsClass;
import com.redpigmall.domain.GoodsSpecProperty;
import com.redpigmall.domain.GoodsSpecification;
import com.redpigmall.domain.Store;
import com.redpigmall.domain.User;
import com.redpigmall.domain.UserGoodsClass;
import com.redpigmall.service.RedPigAccessoryService;
import com.redpigmall.service.RedPigActivityGoodsService;
import com.redpigmall.service.RedPigBuyGiftService;
import com.redpigmall.service.RedPigCombinPlanService;
import com.redpigmall.service.RedPigEnoughReduceService;
import com.redpigmall.service.RedPigFavoriteService;
import com.redpigmall.service.RedPigFootPointService;
import com.redpigmall.service.RedPigGoodsCartService;
import com.redpigmall.service.RedPigGoodsClassService;
import com.redpigmall.service.RedPigGoodsService;
import com.redpigmall.service.RedPigSysConfigService;
import com.redpigmall.service.RedPigUserGoodsClassService;

@SuppressWarnings({"unchecked","rawtypes"})
@Component
public class RedPigGoodsViewTools {
	@Autowired
	private RedPigGoodsService goodsService;
	@Autowired
	private RedPigSysConfigService configService;

	@Autowired
	private RedPigGoodsClassService goodsClassService;
	@Autowired
	private RedPigUserGoodsClassService userGoodsClassService;
	@Autowired
	private RedPigIntegralViewTools IntegralViewTools;
	@Autowired
	private RedPigBuyGiftService buyGiftService;

	@Autowired
	private RedPigActivityGoodsService activityGoodsService;
	@Autowired
	private RedPigEnoughReduceService enoughReduceService;
	@Autowired
	private RedPigCombinPlanService combinplanService;
	@Autowired
	private RedPigGoodsCartService goodscartService;
	@Autowired
	private RedPigFavoriteService favoriteService;
	@Autowired
	private RedPigAccessoryService accessoryService;
	@Autowired
	private RedPigFootPointService footPointService;

	public int getAbleBuyCountByGoodsAndUser(Goods goods, User user) {
		int count = -1;
		if ((goods.getGoods_limit() == 1) && (goods.getGoods_limit_count() > 0)) {
			count = goods.getGoods_limit_count();
			if (user != null) {
				String info = CommUtil.null2String(user
						.getBuy_goods_limit_info());
				if (!info.equals("")) {
					Map maps = JSON.parseObject(info);
					List<Map> list = (List) maps.get("data");
					for (Map map : list) {
						String gid = CommUtil.null2String(map.get("gid"));
						if (CommUtil.null2Int(gid) == goods.getId().longValue()) {
							count = goods.getGoods_limit_count()
									- CommUtil.null2Int(map.get("count"));
							if (count >= 1) {
								break;
							}
							count = 0;

							break;
						}
					}
				}
			}
		} else {
			count = -1;
		}
		return count;
	}

	/**
	 * 将商品属性归类,便于前台显示
	 * 
	 * @param id
	 * @return
	 */
	public List<GoodsSpecProperty> getGoodsSpaec(String id) {
		List<GoodsSpecProperty> specs = Lists.newArrayList();
		if ((id != null) && (!id.equals(""))) {
			Goods goods = this.goodsService.selectByPrimaryKey(Long.valueOf(Long.parseLong(id)));
			
			specs = goods.getGoods_specs();
		}
		return specs;
	}

	/**
	 * 将商品属性归类,便于前台显示
	 * 
	 * @param id
	 * @return
	 */
	public List<GoodsSpecification> generic_spec(String id) {
		List<GoodsSpecification> specs = Lists.newArrayList();
		if ((id != null) && (!id.equals(""))) {
			Goods goods = this.goodsService.selectByPrimaryKey(CommUtil.null2Long(id));
			for (GoodsSpecProperty gsp : goods.getGoods_specs()) {
				GoodsSpecification spec = gsp.getSpec();
				if (!specs.contains(spec)) {
					specs.add(spec);
				}
			}
			Collections.sort(specs, new Comparator<GoodsSpecification>() {
				public int compare(GoodsSpecification gs1,
						GoodsSpecification gs2) {
					return gs1.getSequence() - gs2.getSequence();
				}
			});
		}
		return specs;
	}

	/**
	 * 查询用户商品分类信息
	 * 
	 * @param pid
	 * @return
	 */
	public List<UserGoodsClass> query_user_class(String pid) {
		List<UserGoodsClass> list = Lists.newArrayList();
		if ((pid == null) || (pid.equals(""))) {
			Map<String, Object> params = Maps.newHashMap();
			params.put("user_id", SecurityUserHolder.getCurrentUser().getId());
			params.put("parent", -1);
			list = this.userGoodsClassService.queryPageList(params);
		} else {
			Map<String, Object> params = Maps.newHashMap();
			params.put("parent", Long.valueOf(Long.parseLong(pid)));
			params.put("user_id", SecurityUserHolder.getCurrentUser().getId());
			
			list = this.userGoodsClassService.queryPageList(params);
			
		}
		return list;
	}

	/**
	 * 根据商城分类查询对应的商品
	 * 
	 * @param gc_id
	 *            商城分类id
	 * @param count
	 *            需要查询的数量
	 * @return
	 */
	public List<Goods> query_with_gc(String gc_id, int count) {
		List<Goods> list = Lists.newArrayList();
		GoodsClass gc = this.goodsClassService.selectByPrimaryKey(CommUtil.null2Long(gc_id));
		
		if (gc != null) {
			Set<Long> ids = genericIds(gc);
			Map<String, Object> params = Maps.newHashMap();
			params.put("ids", ids);
			params.put("goods_status", Integer.valueOf(0));
			list = this.goodsService.queryPageList(params);
		}
		return list;
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

	public List<Goods> sort_sale_goods(String store_id, int count) {
		List<Goods> list = Lists.newArrayList();
		Map<String, Object> params = Maps.newHashMap();
		params.put("goods_store_id", CommUtil.null2Long(store_id));
		params.put("goods_status", Integer.valueOf(0));
		list = this.goodsService.queryPageList(params,0,count);

		return list;
	}

	public List<Goods> sort_collect_goods(String store_id, int count) {
		List<Goods> list = Lists.newArrayList();
		Map<String, Object> params = Maps.newHashMap();
		params.put("goods_store_id", CommUtil.null2Long(store_id));
		params.put("goods_status", Integer.valueOf(0));
		list = this.goodsService.queryPageList(params);
		return list;
	}

	public List<Goods> self_goods_sale(int goods_type, int count) {
		List<Goods> list = Lists.newArrayList();
		Map<String, Object> params = Maps.newHashMap();
		params.put("goods_type", Integer.valueOf(goods_type));
		params.put("goods_status", Integer.valueOf(0));
		list = this.goodsService.queryPageList(params , 0, count);
		return list;
	}

	public List<Goods> self_goods_collect(int goods_type, int count) {
		List<Goods> list = Lists.newArrayList();
		Map<String, Object> params = Maps.newHashMap();
		params.put("goods_type", Integer.valueOf(goods_type));
		params.put("goods_status", Integer.valueOf(0));
		list = this.goodsService.queryPageList(params,0, count);
		return list;
	}

	/**
	 * 直通车商品查询，查询当天的直通车商品，如果系统没有开启直通车，则查询系统推荐商品， size：需要查询的商品数量
	 */
	public List<Goods> query_Ztc_Goods(int size) {
		List<Goods> ztc_goods = Lists.newArrayList();
		if (this.configService.getSysConfig().getZtc_status()) {
			ztc_goods = randomZtcGoods(CommUtil.null2Int(Integer.valueOf(size)));
		} else {
			Map<String, Object> params = Maps.newHashMap();
			params.put("store_recommend", true);
			params.put("goods_status", 0);
			params.put("f_sale_type", 0);
			params.put("advance_sale_type", 0);
			ztc_goods = this.goodsService.queryPageList(params, 0, size);
		}
		return ztc_goods;
	}

	/**
	 * 随机显示当天的直通车商品，显示数量由count控制
	 * 
	 * @param count
	 * @return
	 */
	public List<Goods> randomZtcGoods(int count) {
		Map<String,Object> ztc_map = Maps.newHashMap();
		ztc_map.put("ztc_status", Integer.valueOf(3));
		ztc_map.put("now_date_less_thran_equal", new Date());
		ztc_map.put("ztc_gold_more_than", 0);
		List<Goods> goods = this.goodsService.queryPageList(ztc_map);
		
		Random random = new Random();
		int random_num = 0;
		int num = 0;
		if (goods.size() - count > 0) {
			num = goods.size() - count;
			random_num = random.nextInt(num);
		}
		ztc_map.clear();
		ztc_map.put("ztc_status", Integer.valueOf(3));
		ztc_map.put("now_date_less_than_equal", new Date());
		ztc_map.put("ztc_gold_more_than", Integer.valueOf(0));
		List<Goods> ztc_goods = this.goodsService.queryPageList(ztc_map , random_num, count);
		Collections.shuffle(ztc_goods);
		return ztc_goods;
	}

	/**
	 * 从list中随机显示若干个对象，数量由count控制
	 * 
	 * @param goods_list
	 * @param count
	 * @return
	 */
	public List<Goods> randomZtcGoods2(List<Goods> goods_list, int count) {
		List<Goods> ztc_goods = Lists.newArrayList();
		Random ran = new Random();
		for (int i = 0; i < count; i++) {
			if (i < goods_list.size()) {
				int ind = ran.nextInt(goods_list.size());
				boolean flag = true;
				for (Goods obj : ztc_goods) {
					if (obj.getId().equals(
							((Goods) goods_list.get(ind)).getId())) {
						flag = false;
					}
				}
				if (flag) {
					ztc_goods.add((Goods) goods_list.get(ind));
				} else {
					i--;
				}
			}
		}
		Collections.shuffle(ztc_goods);
		return ztc_goods;
	}

	/**
	 * 根据当前会员的会员等级，显示相应等级的名称
	 */
	public String query_user_level_name(String user_id) {
		String level_name = "";
		if ((user_id != null) && (!user_id.equals(""))) {
			level_name = CommUtil.null2String(this.IntegralViewTools
					.query_user_level(user_id).get("name"));
		}
		return level_name;
	}

	/**
	 * 查询LuceneVo的图片路径
	 */
	public List<String> query_LuceneVo_photos_url(String json) {
		List<String> list = Lists.newArrayList();
		if (!CommUtil.null2String(json).equals("")) {
			list = JSON.parseArray(json, String.class);
		}
		return list;
	}

	public Store query_LuceneVo_goods_store(String id) {
		Store store = null;
		Goods goods = this.goodsService.selectByPrimaryKey(CommUtil.null2Long(id));
		if (goods != null) {
			store = goods.getGoods_store();
		}
		return store;
	}

	public List<GoodsClass> query_GC_third(String gc_id, Set<String> list_gc) {
		List<GoodsClass> gcs = Lists.newArrayList();
		for (String gc_str : list_gc) {
			if ((gc_str.split("_")[0].equals(gc_id))
					&& (gc_str.split("_").length > 1)) {
				gcs.add(this.goodsClassService.selectByPrimaryKey(CommUtil.null2Long(gc_str.split("_")[1])));
			}
		}
		return gcs;
	}

	public BuyGift query_buyGift(String bg_id) {
		BuyGift bg = this.buyGiftService.selectByPrimaryKey(CommUtil.null2Long(bg_id));
		return bg;
	}

	public Goods query_Goods(String goods_id) {
		Goods goods = this.goodsService
				.selectByPrimaryKey(CommUtil.null2Long(goods_id));
		return goods;
	}

	public boolean checkEnoughReduce_status(String id) {
		boolean flag = false;
		if ((id != null) && (!"".equals(id))) {
			Goods obj = query_Goods(id);
			if ((obj != null) && (obj.getEnough_reduce() == 1)) {
				EnoughReduce er = this.enoughReduceService.selectByPrimaryKey(CommUtil
						.null2Long(obj.getOrder_enough_reduce_id()));
				if ((er != null) && (er.getErstatus() == 10)
						&& (er.getErbegin_time().before(new Date()))
						&& (er.getErend_time().after(new Date()))) {
					flag = true;
				} else if ((er != null)
						&& (er.getErend_time().before(new Date()))) {
					er.setErstatus(20);
					this.enoughReduceService.updateById(er);
					String goods_json = er.getErgoods_ids_json();
					List<String> goods_id_list = JSON.parseArray(goods_json,
							String.class);
					for (String goods_id : goods_id_list) {
						Goods ergood = this.goodsService.selectByPrimaryKey(CommUtil
								.null2Long(goods_id));
						ergood.setEnough_reduce(0);
						ergood.setOrder_enough_reduce_id(null);
						this.goodsService.update(ergood);
					}
				}
			}
		}
		return flag;
	}

	public boolean checkEnoughGive_status(String id) {
		boolean flag = false;
		if ((id != null) && (!"".equals(id))) {
			Goods obj = query_Goods(id);
			if ((obj != null) && (obj.getOrder_enough_give_status() == 1)) {
				BuyGift bg = this.buyGiftService
						.selectByPrimaryKey(obj.getBuyGift_id());
				if ((bg != null) && (bg.getGift_status() == 10)
						&& (bg.getBeginTime().before(new Date()))) {
					flag = true;
				}
				if ((bg != null) && (bg.getEndTime().before(new Date()))) {
					bg.setGift_status(20);
					this.buyGiftService.updateById(bg);
					List<Map> maps = (List) JSON.parseObject(bg.getGift_info(),
							new TypeReference() {
							}, new Feature[0]);

					maps.addAll((Collection) JSON.parseObject(
							bg.getGoods_info(), new TypeReference() {
							}, new Feature[0]));
					for (Map map : maps) {
						Goods goods = this.goodsService.selectByPrimaryKey(CommUtil
								.null2Long(map.get("goods_id")));
						if (goods != null) {
							goods.setOrder_enough_give_status(0);
							goods.setOrder_enough_if_give(0);
							goods.setBuyGift_id(null);
							this.goodsService.update(goods);
						}
					}
				}
			}
		}
		return flag;
	}

	/**
	 * 解析商品中的组合方案
	 * 
	 * @param goods_id
	 * @return
	 */
	public List<Map> getCombinPlans(String goods_id, String type) {
		List<Map> map_temps = Lists.newArrayList();
		Goods obj = this.goodsService.selectByPrimaryKey(CommUtil.null2Long(goods_id));
		if ((obj.getCombin_status() == 1)
				&& ((obj.getCombin_suit_id() != null) || (obj
						.getCombin_parts_id() != null))) {
			CombinPlan com = null;
			if ((type.equals("suit")) && (obj.getCombin_suit_id() != null)) {
				com = this.combinplanService
						.selectByPrimaryKey(obj.getCombin_suit_id());
			} else if ((type.equals("parts"))
					&& (obj.getCombin_parts_id() != null)) {
				com = this.combinplanService.selectByPrimaryKey(obj
						.getCombin_parts_id());
			}
			if (com != null) {
				boolean verify_date = false;
				Date now = new Date();
				
				if ((com.getBeginTime().before(now))
						&& (com.getEndTime().after(now))) {
					verify_date = true;
				}
				if ((verify_date) && (com.getCombin_status() == 1)) {
					map_temps = JSON.parseArray(com.getCombin_plan_info(),
							Map.class);
				}
			}
		}
		return map_temps;
	}

	public Map getSuitInfo(String cart_id) {
		Map<String, Object> map = null;
		GoodsCart gc = this.goodscartService.selectByPrimaryKey(CommUtil
				.null2Long(cart_id));
		if ((gc != null) && (gc.getCombin_suit_info() != null)) {
			map = JSON.parseObject(gc.getCombin_suit_info());
		}
		return map;
	}

	public List<Map> getCombinPlanGoods(Map map) {
		List<Map> map_temps = Lists.newArrayList();
		map_temps = (List) map.get("goods_list");
		return map_temps;
	}

	public String getCombinPlanGoodsIds(Map map) {
		String ids = "";
		List<Map> map_temps = (List) map.get("goods_list");
		for (Map map2 : map_temps) {
			if (ids.equals("")) {
				ids = CommUtil.null2String(map2.get("id"));
			} else {
				ids = ids + "," + CommUtil.null2String(map2.get("id"));
			}
		}
		return ids;
	}

	public List<Map> getsuitGoods(String web_url, String id) {
		List<Map> map_list = Lists.newArrayList();
		GoodsCart cart = this.goodscartService.selectByPrimaryKey(CommUtil
				.null2Long(id));
		if ((cart != null) && (cart.getCart_type() != null)
				&& (cart.getCart_type().equals("combin"))
				&& (cart.getCombin_main() == 1)) {
			String[] cart_ids = cart.getCombin_suit_ids().split(",");
			for (String cart_id : cart_ids) {
				if ((!cart_id.equals("")) && (!cart_id.equals(id))) {
					GoodsCart other = this.goodscartService.selectByPrimaryKey(CommUtil
							.null2Long(cart_id));
					if (other != null) {
						Map<String,Object> temp_map = Maps.newHashMap();
						
						temp_map.put("id", other.getId());
						temp_map.put("name", other.getGoods().getGoods_name());
						temp_map.put("price", other.getGoods()
								.getGoods_current_price());
						temp_map.put("count", Integer.valueOf(other.getCount()));
						temp_map.put("all_price", other.getPrice());
						temp_map.put("spec_info", other.getSpec_info());
						String goods_url = web_url + "/items_"
								+ other.getGoods().getId() + "";
						temp_map.put("url", goods_url);
						String img2 = web_url
								+ "/"
								+ this.configService.getSysConfig()
										.getGoodsImage().getPath()
								+ "/"
								+ this.configService.getSysConfig()
										.getGoodsImage().getName();
						if (other.getGoods().getGoods_main_photo() != null) {
							img2 =

							web_url
									+ "/"
									+ other.getGoods().getGoods_main_photo()
											.getPath()
									+ "/"
									+ other.getGoods().getGoods_main_photo()
											.getName()
									+ "_small."
									+ other.getGoods().getGoods_main_photo()
											.getExt();
						}
						temp_map.put("img", img2);
						map_list.add(temp_map);
					}
				}
			}
		}
		return map_list;
	}

	public String getsuitName(String suit_info) {
		String suit_name = "";
		if ((suit_info != null) && (!suit_info.equals(""))) {
			Map<String, Object> map = JSON.parseObject(suit_info);
			suit_name = CommUtil.null2String(map.get("suit_name"));
		}
		return suit_name;
	}

	public String query_activity_status(String id, String mark) {
		String str = "false";
		Map<String, Object> params = Maps.newHashMap();
		params.put("id", CommUtil.null2Long(id));
		
		Goods goods = this.goodsService.selectByPrimaryKey(CommUtil.null2Long(id));
		
		String methodName =StringUtils.getFieldName(mark);
		
		String status = CommUtil.null2String(ObjectUtils.getValueByMethodName(goods, Goods.class, methodName));
		if ((mark.equals("group_buy")) || (mark.equals("activity_status"))) {
			if (status.equals("2")) {
				str = "true";
			}
		} else if (((mark.equals("combin_status"))
				|| (mark.equals("order_enough_give_status"))
				|| (mark.equals("enough_reduce"))
				|| (mark.equals("f_sale_type")) || (mark
					.equals("advance_sale_type"))) && (status.equals("1"))) {
			str = "true";
		}
		return str;
	}

	public String query_goods_single_preferential(String id, String webUrl) {
		String str = "";
		Goods goods = this.goodsService.selectByPrimaryKey(CommUtil.null2Long(id));
		Date nowDate = new Date();
		if ((goods.getGroup_buy() == 2)
				&& (goods.getGroup().getBeginTime().before(nowDate))) {
			str = "/resources/style/system/front/default/images/tuan.png";
		}
		if (goods.getActivity_status() == 2) {
			ActivityGoods acg = this.activityGoodsService.selectByPrimaryKey(CommUtil
					.null2Long(goods.getActivity_goods_id()));
			if ((acg != null) && (acg.getAg_status() == 1)) {
				str = "/resources/style/system/front/default/images/c.png";
			}
		}
		if (goods.getOrder_enough_give_status() == 1) {
			BuyGift bg = this.buyGiftService.selectByPrimaryKey(goods.getBuyGift_id());
			if ((bg != null) && (bg.getBeginTime().before(nowDate))) {
				str = "/resources/style/system/front/default/images/s.png";
			}
		}
		if (goods.getEnough_reduce() == 1) {
			EnoughReduce er = this.enoughReduceService.selectByPrimaryKey(CommUtil
					.null2Long(goods.getOrder_enough_reduce_id()));
			if ((er != null) && (er.getErbegin_time().before(nowDate))) {
				str = "/resources/style/system/front/default/images/j.png";
			}
		}
		if (goods.getCombin_status() == 1) {
			str = "/resources/style/system/front/default/images/zu.png";
		}
		if (goods.getF_sale_type() == 1) {
			str = "/resources/style/system/front/default/images/f.png";
		}
		if (goods.getAdvance_sale_type() == 1) {
			str = "/resources/style/system/front/default/images/y.png";
		}
		if (goods.getGoods_limit() == 1) {
			str = "/resources/style/system/front/default/images/xian.png";
		}
		if (!str.equals("")) {
			str = webUrl + "/" + str;
		}
		return str;
	}

	public Map query_goods_preferential(long id) {
		Goods goods = this.goodsService.selectByPrimaryKey(Long.valueOf(id));
		String str = "";
		String info = "";
		Date nowDate = new Date();
		if ((goods.getGroup_buy() == 2)
				&& (goods.getGroup().getBeginTime().before(nowDate))) {
			str = "团购";
		}
		if (goods.getActivity_status() == 2) {
			Activity act = null;
			if (goods.getActivity_goods_id() != null) {
				ActivityGoods ag = this.activityGoodsService
						.selectByPrimaryKey(CommUtil.null2Long(goods
								.getActivity_goods_id()));
				if (ag != null) {
					act = ag.getAct();
				}
			} else {
				for (ActivityGoods ag : goods.getAg_goods_list()) {
					if (ag.getAg_goods().getId().equals(goods.getId())) {
						act = ag.getAct();
						break;
					}
				}
			}
			if ((act != null) && (act.getAc_begin_time().before(nowDate))
					&& (act.getAc_end_time().after(nowDate))) {
				str = "促销";
			}
		}
		if (goods.getOrder_enough_give_status() == 1) {
			BuyGift bg = this.buyGiftService.selectByPrimaryKey(goods.getBuyGift_id());
			if ((bg != null) && (bg.getBeginTime().before(nowDate))) {
				str = "满送";
				info = "活动商品购满" + bg.getCondition_amount() + "元，即可领取赠品";
			}
		}
		if (goods.getEnough_reduce() == 1) {
			EnoughReduce er = this.enoughReduceService.selectByPrimaryKey(CommUtil
					.null2Long(goods.getOrder_enough_reduce_id()));
			if (er.getErbegin_time().before(nowDate)) {
				str = "满减";
				info = "活动商品" + er.getErtag();
			}
		}
		if (goods.getCombin_status() == 1) {
			str = "组合";
			info = "点击查看组合套装完整信息";
		}
		if (goods.getAdvance_sale_type() == 1) {
			str = "预售";
			info = "预售商品，" + CommUtil.formatShortDate(goods.getAdvance_date())
					+ "开始发货";
		}
		if (goods.getF_sale_type() == 1) {
			str = "F码";
			info = "F码商品凭F码购买";
		}
		Map<String, Object> map = Maps.newHashMap();
		map.put("name", str);
		map.put("info", info);
		return map;
	}

	public String query_Store_userName(Long goods_id) {
		Goods goods = this.goodsService.selectByPrimaryKey(goods_id);
		if (goods.getGoods_type() != 0) {
			User user = goods.getGoods_store().getUser();
			return user.getUserName();
		}
		return null;
	}

	public List getGiftList(String gift_info) {
		List list = Lists.newArrayList();
		List<Map> gift_list = JSON.parseArray(gift_info, Map.class);
		for (Map map : gift_list) {
			if (map.get("storegoods_count").equals("0")) {
				if (Integer.parseInt(map.get("goods_count").toString()) > 0) {
					list.add(map);
				}
			} else if (query_Goods(map.get("goods_id").toString())
					.getGoods_inventory() > 0) {
				list.add(map);
			}
		}
		return list;
	}

	public int queryInventory(String id) {
		Goods goods = this.goodsService.selectByPrimaryKey(CommUtil.null2Long(id));
		int inventory = 0;
		if (goods != null) {
			inventory = goods.getGoods_inventory();
		}
		return inventory;
	}

	public boolean queryFavoriteStatus(String user_id, String goods_id) {
		if (CommUtil.null2String(user_id).equals("")) {
			return false;
		}
		Map<String, Object> params = Maps.newHashMap();
		params.put("user_id", CommUtil.null2Long(user_id));
		params.put("goods_id", CommUtil.null2Long(goods_id));
		int count = this.favoriteService.selectCount(params);
		return count > 0;
	}

	public List<Goods> get_recommand_goods2(Goods goods) {
		Map<String, Object> map = JSON.parseObject(goods.getGoods_tags());
		List<Goods> recommand_list = Lists.newArrayList();
		List list = (List) map.get("generated_tag");
		for (Object object : list) {
			if (!object.toString().equals("")) {
				Map<String, Object> params = Maps.newHashMap();
				List<Goods> list1 = null;
				if (goods.getGoods_type() == 0) {
					params.put("redpig_tag1", object);
					params.put("redpig_tag2", object);
					params.put("redpig_g_id", goods.getId());
					list1 = this.goodsService.queryPageList(params,0,10);
					
				} else {
					params.put("redpig_tag1", object);
					params.put("redpig_tag2", object);
					params.put("redpig_g_id", goods.getId());
					params.put("goods_store_id", goods.getGoods_store().getId());
					list1 = this.goodsService.queryPageList(params,0,10);
				}
				recommand_list.addAll(list1);
			}
		}
		list = (List) map.get("custom_tag");
		for (Object object : list) {
			if (!object.toString().equals("")) {
				List<Goods> list1 = null;
				Map<String, Object> params = Maps.newHashMap();
				if (goods.getGoods_type() == 0) {
					params.put("redpig_tag1", object);
					params.put("redpig_tag2", object);
					params.put("redpig_g_id", goods.getId());
					list1 = this.goodsService.queryPageList(params, 0, 10);
				} else {
					params.put("redpig_tag1", object);
					params.put("redpig_tag2", object);
					params.put("redpig_g_id", goods.getId());
					params.put("redpig_store_id", goods.getGoods_store().getId());
					list1 = this.goodsService.queryPageList(params, 0, 10);
				}
				recommand_list.addAll(list1);
			}
		}
		Collections.sort(recommand_list, new Comparator<Goods>() {
			public int compare(Goods o1, Goods o2) {
				return o1.getGoods_salenum() - o2.getGoods_salenum();
			}
		});
		if (recommand_list.size() > 10) {
			recommand_list = recommand_list.subList(0, 10);
		}
		return recommand_list;
	}

	public List<Map> analysis_goods_color_img(String id) {
		List<Map> image_list = Lists.newArrayList();
		Goods obj = this.goodsService.selectByPrimaryKey(CommUtil.null2Long(id));
		
		if ((obj.getGoods_color_json() != null)
				&& (!obj.getGoods_color_json().equals(""))) {
			List<Map> color_arr = (List) JSON.parseObject(
					obj.getGoods_color_json()).get("data");
			for (Map color : color_arr) {
				Map color_map = Maps.newHashMap();
				String color_id = CommUtil.null2String(color.get("color_id"));
				String img_ids = CommUtil.null2String(color.get("img_ids"));
				String[] ids = img_ids.split("-");
				List img_list = Lists.newArrayList();
				Map main_map = Maps.newHashMap();
				for (String img_id : ids) {
					if (!img_id.equals("")) {
						if (img_id.indexOf("m") >= 0) {
							String[] main_id = img_id.split("_");
							Accessory img = this.accessoryService
									.selectByPrimaryKey(CommUtil.null2Long(main_id[1]));
							if (img != null) {
								main_map.put("id", img_id);
								main_map.put("bigImg", img.getPath() + "/"
										+ img.getName());
							}
						} else {
							Accessory img = this.accessoryService
									.selectByPrimaryKey(CommUtil.null2Long(img_id));
							if (img != null) {
								Map temp_map = Maps.newHashMap();
								temp_map.put("id", img_id);
								temp_map.put("bigImg", img.getPath() + "/"
										+ img.getName());
								img_list.add(temp_map);
							}
						}
					}
				}
				img_list.add(0, main_map);
				color_map.put("color_id", color_id);
				color_map.put("img_list", img_list);
				image_list.add(color_map);
			}
		}
		return image_list;
	}

	public List<Accessory> analysis_goods_list_img(String goods_color_json) {
		List<Accessory> image_list = Lists.newArrayList();
		if ((goods_color_json != null) && (!goods_color_json.equals(""))) {
			List<Map> color_arr = (List) JSON.parseObject(goods_color_json)
					.get("data");

			for (Map color : color_arr) {
				String img_ids = CommUtil.null2String(color.get("img_ids"));
				String[] ids = img_ids.split("-");
				for (String img_id : ids) {
					if ((!img_id.equals("")) && (img_id.indexOf("m") >= 0)) {
						String[] main_id = img_id.split("_");
						Accessory img = this.accessoryService
								.selectByPrimaryKey(CommUtil.null2Long(main_id[1]));
						image_list.add(img);
					}
				}
			}

		}
		return image_list;
	}

	public String query_goods_color_img_json(String id) {
		String josn = "";
		Goods goods = this.goodsService.selectByPrimaryKey(Long.parseLong(id));
		
		if (goods!=null) {
			josn = CommUtil.null2String(goods.getGoods_color_json());
		}
		return josn;
	}

	
	public void record_footPoint(HttpServletRequest request, User current_user,
			Goods obj) {
		Map<String, Object> params = Maps.newHashMap();
		params.put("fp_date",
				CommUtil.formatDate(CommUtil.formatShortDate(new Date())));
		params.put("fp_user_id", current_user.getId());
		List<FootPoint> fps = this.footPointService.queryPageList(params);
		
		String url = CommUtil.getURL(request);
		if (!"".equals(CommUtil.null2String(this.configService.getSysConfig()
				.getImageWebServer()))) {
			url = this.configService.getSysConfig().getImageWebServer();
		}
		String goods_main_photo = url + "/"
				+ this.configService.getSysConfig().getGoodsImage().getPath()
				+ "/"
				+ this.configService.getSysConfig().getGoodsImage().getName();
		if (obj.getGoods_main_photo() != null) {
			goods_main_photo =

			url + "/" + obj.getGoods_main_photo().getPath() + "/"
					+ obj.getGoods_main_photo().getName() + "_middle."
					+ obj.getGoods_main_photo().getExt();
		}
		if (fps.size() == 0) {
			FootPoint fp = new FootPoint();
			fp.setAddTime(new Date());
			fp.setFp_date(new Date());
			fp.setFp_user_id(current_user.getId());
			fp.setFp_user_name(current_user.getUsername());
			fp.setFp_goods_count(1);
			Map<String, Object> map = Maps.newHashMap();
			map.put("goods_id", obj.getId());
			map.put("goods_name", obj.getGoods_name());
			map.put("goods_sale", Integer.valueOf(obj.getGoods_salenum()));
			map.put("goods_time", CommUtil.formatLongDate(new Date()));
			map.put("goods_img_path", goods_main_photo);
			map.put("goods_price", obj.getGoods_current_price());
			map.put("goods_class_id", CommUtil.null2Long(obj.getGc().getId()));
			map.put("goods_class_name",
					CommUtil.null2String(obj.getGc().getClassName()));
			List<Map> list = Lists.newArrayList();
			list.add(map);
			fp.setFp_goods_content(JSON.toJSONString(list));
			this.footPointService.saveEntity(fp);
		} else {
			FootPoint fp = (FootPoint) fps.get(0);
			List<Map> list = JSON.parseArray(fp.getFp_goods_content(),
					Map.class);
			boolean add = true;
			for (Map map : list) {
				if (CommUtil.null2Long(map.get("goods_id")).equals(obj.getId())) {
					add = false;
				}
			}
			if (add) {
				Map<String, Object> map = Maps.newHashMap();
				map.put("goods_id", obj.getId());
				map.put("goods_name", obj.getGoods_name());
				map.put("goods_sale", Integer.valueOf(obj.getGoods_salenum()));
				map.put("goods_time", CommUtil.formatLongDate(new Date()));
				map.put("goods_img_path", goods_main_photo);
				map.put("goods_price", obj.getGoods_current_price());
				map.put("goods_class_id",
						CommUtil.null2Long(obj.getGc().getId()));
				map.put("goods_class_name",
						CommUtil.null2String(obj.getGc().getClassName()));
				list.add(0, map);
				fp.setFp_goods_count(list.size());
				fp.setFp_goods_content(JSON.toJSONString(list));
				this.footPointService.updateById(fp);
			}
		}
	}
}
