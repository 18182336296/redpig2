package com.redpigmall.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.redpigmall.domain.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.api.sec.SecurityUserHolder;
import com.redpigmall.api.tools.CommUtil;
import com.redpigmall.dao.UserMapper;
import com.redpigmall.manage.admin.tools.RedPigCartTools;
import com.redpigmall.dao.BuyGiftMapper;
import com.redpigmall.dao.CombinPlanMapper;
import com.redpigmall.dao.GoodsCartMapper;
import com.redpigmall.dao.GoodsMapper;
import com.redpigmall.service.RedPigGoodsCartService;
import com.redpigmall.service.RedPigGoodsSpecPropertyService;
import com.redpigmall.service.base.BaseService;
import com.redpigmall.view.web.tools.RedPigGoodsViewTools;

@SuppressWarnings({ "unchecked", "rawtypes" })
@Service
@Transactional(readOnly = true)
public class RedPigGoodsCartService extends BaseService<GoodsCart> {
	@Autowired
	private GoodsCartMapper goodsCartMapper;
	@Autowired
	private GoodsMapper goodsMapper;
	@Autowired
	private UserMapper userMapper;
	@Autowired
	private RedPigCartTools cartTools;
	@Autowired
	private RedPigGoodsSpecPropertyService goodsSpecPropertyService;
	@Autowired
	private CombinPlanMapper combinPlanMapper;
	@Autowired
	private RedPigGoodsViewTools goodsViewTools;


	@Transactional(readOnly = false)
	public void batchDelObjs(List<GoodsCart> objs) {
		if (objs != null && objs.size() > 0) {
			goodsCartMapper.batchDelete(objs);
		}
	}


	public GoodsCart getObjByProperty(String key, String operation_symbol, Object value) {
		Map<String, Object> maps = Maps.newHashMap();
		maps.put("operation_property", key);
		maps.put("operation_symbol", operation_symbol);
		maps.put("operation_value", value);
		List<GoodsCart> objs = goodsCartMapper.selectObjByProperty(maps);
		if (objs != null && objs.size() > 0) {
			return objs.get(0);
		}
		return null;
	}


	public List<GoodsCart> selectObjByProperty(Map<String, Object> maps) {
		return goodsCartMapper.selectObjByProperty(maps);
	}


	public List<GoodsCart> queryPages(Map<String, Object> params) {
		return goodsCartMapper.queryPages(params);
	}


	public List<GoodsCart> queryPageListWithNoRelations(Map<String, Object> param) {
		return goodsCartMapper.queryPageListWithNoRelations(param);
	}


	public List<GoodsCart> queryPagesWithNoRelations(Map<String, Object> params, Integer currentPage,
			Integer pageSize) {
		return goodsCartMapper.queryPagesWithNoRelations(params);
	}


	public IPageList queryPagesWithNoRelations(Map<String, Object> params) {
		return super.queryPagesWithNoRelations(params);
	}


	public IPageList list(Map<String, Object> params) {
		return super.listPages(params);
	}


	@Transactional(readOnly = false)
	public void batchDeleteByIds(List<Long> ids) {
		goodsCartMapper.batchDeleteByIds(ids);
	}


	@Transactional(readOnly = false)
	public void saveEntity(GoodsCart obj) {
		goodsCartMapper.saveEntity(obj);
	}


	@Transactional(readOnly = false)
	public void updateById(GoodsCart obj) {
		goodsCartMapper.updateById(obj);
	}


	@Transactional(readOnly = false)
	public void deleteById(Long id) {
		goodsCartMapper.deleteById(id);
	}


	public GoodsCart selectByPrimaryKey(Long id) {
		return goodsCartMapper.selectByPrimaryKey(id);
	}


	public List<GoodsCart> queryPageList(Map<String, Object> params, Integer begin, Integer max) {
		return super.queryPageList(params, begin, max);
	}


	public List<GoodsCart> queryPageList(Map<String, Object> params) {
		return this.queryPageList(params, null, null);
	}


	public int selectCount(Map<String, Object> params) {
		Integer c = goodsCartMapper.selectCount(params);
		if (c == null) {
			return 0;
		}

		return c;

	}
	
	/**
	 * 把当前购物车全部列出来
	 * @param request
	 * @param app_user_id app id
	 * @param app_user_token 
	 * @param app_cart_ids 
	 * @param show_advance
	 * @return
	 */

	@Transactional(readOnly = false)
	public List<GoodsCart> cart_list(HttpServletRequest request, Long app_user_id, String app_user_token,
			String app_cart_ids, boolean show_advance) {
		
		List<GoodsCart> carts_list = Lists.newArrayList();
		List<GoodsCart> carts_cookie = Lists.newArrayList();
		List<GoodsCart> carts_app = Lists.newArrayList();
		List<GoodsCart> carts_user = Lists.newArrayList();
		
		User user = null;
		if (app_user_id != null) {//目前本系统无原生APP,所以这里置废
			Map cart_map = Maps.newHashMap();
			Set mark_ids = Sets.newTreeSet();
			if ((app_user_id != null) && (!app_user_id.equals("")) && (app_user_token != null)) {
				user = this.userMapper.selectByPrimaryKey(app_user_id);
				if (user != null) {
					if (!user.getApp_login_token().equals(app_user_token.toLowerCase())) {
						user = null;
					}
				}
			}
			if ((app_cart_ids == null) || (app_cart_ids.equals(""))) {
				app_cart_ids = "0";
			}
			String[] mobile_ids = app_cart_ids.split(",");
			for (String mobile_id : mobile_ids) {
				if (!mobile_id.equals("")) {
					mark_ids.add(CommUtil.null2Long(mobile_id));
				}
			}
			
			if (user != null) {
				cart_map.clear();
				cart_map.put("ids", mark_ids);
				cart_map.put("cart_status", Integer.valueOf(0));
				carts_app = this.goodsCartMapper.queryPageList(cart_map);

				if (user.getStore() != null) {
					for (GoodsCart gc : carts_app) {
						if (gc.getGoods().getGoods_type() == 1) {
							if (gc.getGoods().getGoods_store().getId().equals(user.getStore().getId())) {
								this.goodsCartMapper.deleteById(gc.getId());
							}
						}
					}
				}
				cart_map.clear();
				cart_map.put("user_id", user.getId());
				cart_map.put("cart_status", Integer.valueOf(0));
				carts_user = this.goodsCartMapper.queryPageList(cart_map);
			} else {
				cart_map.clear();
				cart_map.put("ids", mark_ids);
				cart_map.put("cart_status", 0);
				carts_app = this.goodsCartMapper.queryPageList(cart_map);
			}
			if (user != null) {
				for (GoodsCart ugc : carts_user) {
					carts_list.add(ugc);
				}

				for (GoodsCart mobile : carts_app) {
					boolean add = true;
					for (GoodsCart gc2 : carts_user) {
						if (mobile.getGoods().getId().equals(gc2.getGoods().getId())) {
							if ((mobile.getSpec_info().equals(gc2.getSpec_info()))
									&& ((!"combin".equals(mobile.getCart_type())) || (mobile.getCombin_main() != 1))) {
								add = false;
								this.goodsCartMapper.deleteById(mobile.getId());
							}
						}
					}
					if (add) {
						mobile.setCart_mobile_id(null);
						mobile.setUser(user);
						this.goodsCartMapper.updateById(mobile);
						carts_list.add(mobile);
					}
				}
			} else {
				for (GoodsCart mobile : carts_app) {
					carts_list.add(mobile);
				}
			}
		} else {
			
			String cart_session_id = "";//当前临时购物车
			Cookie[] cookies = request.getCookies();
			if (cookies != null) {
				for (Cookie cookie : cookies) {
					if (cookie.getName().equals("cart_session_id")) {
						cart_session_id = CommUtil.null2String(cookie.getValue());
					}
				}
			}
			/**
			 * 如果临时购物车id为空,创建一个临时购物车
			 */
			if (cart_session_id.equals("")) {
				//这里创建一个cart_session_id
				cart_session_id = UUID.randomUUID().toString();
				Cookie cookie = new Cookie("cart_session_id", cart_session_id);
				cookie.setDomain(CommUtil.generic_domain(request));
				//这里在确认一次有没有cart_session_id，如果已经有了，就删除
				if (!CommUtil.null2String(request.getSession(false).getAttribute("cart_session_id")).equals("")) {
					cart_session_id = CommUtil.null2String(request.getSession(false).getAttribute("cart_session_id"));
					request.getSession(false).removeAttribute("cart_session_id");
				}
			}
			
			user = SecurityUserHolder.getCurrentUser();
			Map cart_map = Maps.newHashMap();
			if (user != null) {
				user = this.userMapper.selectByPrimaryKey(user.getId());
				if (!cart_session_id.equals("")) {
					cart_map.clear();
					cart_map.put("cart_session_id", cart_session_id);
					cart_map.put("cart_status", 0);
					carts_cookie = this.goodsCartMapper.queryPageList(cart_map);
					//这里如果买家也是商家,目前系统是禁止商家作为买家
					if (user.getStore() != null) {
						for (GoodsCart gc : carts_cookie) {
							if (gc.getGoods().getGoods_type() == 1) {
								if (gc.getGoods().getGoods_store().getId().equals(user.getStore().getId())) {
									this.goodsCartMapper.deleteById(gc.getId());
								}
							}
						}
					}
					
					cart_map.clear();
					cart_map.put("user_id", user.getId());
					cart_map.put("cart_status", 0);
					carts_user = this.goodsCartMapper.queryPageList(cart_map);
				} else {
					cart_map.clear();
					cart_map.put("user_id", user.getId());
					cart_map.put("cart_status", Integer.valueOf(0));
					carts_user = this.goodsCartMapper.queryPageList(cart_map);
				}
			} else if (!cart_session_id.equals("")) {
				cart_map.clear();
				cart_map.put("cart_session_id", cart_session_id);
				cart_map.put("cart_status", 0);
				carts_cookie = this.goodsCartMapper.queryPageList(cart_map);
			}
			
			boolean add;
			
			if (user != null) {
				for (GoodsCart ugc : carts_user) {
					carts_list.add(ugc);
				}
				cart_map.clear();
				cart_map.put("cart_session_id", cart_session_id);
				cart_map.put("cart_status", 0);
				carts_cookie = this.goodsCartMapper.queryPageList(cart_map);
				
				for (GoodsCart cookie : carts_cookie) {
					add = true;
					for (GoodsCart gc2 : carts_user) {
						if (cookie.getGoods().getId().equals(gc2.getGoods().getId())) {
							if ((cookie.getSpec_info().equals(gc2.getSpec_info()))
									&& ((!"combin".equals(cookie.getCart_type())) || (cookie.getCombin_main() != 1))) {
								add = false;
								this.goodsCartMapper.deleteById(cookie.getId());
							}
						}
					}
					if (add) {
						cookie.setCart_session_id(null);
						cookie.setUser(user);
						this.goodsCartMapper.updateById(cookie);
						carts_list.add(cookie);
					}
				}
			} else {
				String temp_val = "";
				for (GoodsCart cookie : carts_cookie) {
					String val = "," + CommUtil.null2String(cookie.getGoods().getId()) + "_" + cookie.getSpec_info()
							+ "_" + cookie.getCart_type() + "_" + cookie.getCombin_version();
					if (temp_val.indexOf(val) < 0) {
						carts_list.add(cookie);
						temp_val = temp_val + "," + val;
					} else {
						this.goodsCartMapper.deleteById(cookie.getId());
					}
				}
			}
		}
		if (!show_advance) {
			List<GoodsCart> removeList = Lists.newArrayList();
			for (GoodsCart gc : carts_list) {
				if ((gc.getGoods() != null) && (gc.getGoods().getAdvance_sale_type() == 1)) {
					removeList.add(gc);
				}
			}
			carts_list.removeAll(removeList);
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
		
		for (GoodsCart gc : carts_list) {
			if ("limit".equals(gc.getCart_type())) {
				int gc_count = gc.getCount();
				int goods_limit_count = gc.getGoods().getGoods_limit_count();
				if (gc_count > goods_limit_count) {
					gc.setCount(goods_limit_count);
					gc.setRemain_count(0);
					this.goodsCartMapper.updateById(gc);

				}
			}
		}
		//删除重复cart
		carts_list = RemoveRepetition(carts_list);
		return carts_list;

	}
	
	/**
	 * 删除重复cart,这里通过Comparator
	 * @param carts_list
	 * @return
	 */
	@Transactional(readOnly = false)
	public List<GoodsCart> RemoveRepetition(List<GoodsCart> carts_list) {
		List<GoodsCart> set = Lists.newArrayList();
		for (GoodsCart goodsCart : carts_list) {
			set.add(goodsCart);
		}
		for (GoodsCart goodsCart : set) {
			if (goodsCart.getGoods() == null) {//如果购物车没有商品就删除购物车
				carts_list.remove(goodsCart);
				this.goodsCartMapper.deleteById(goodsCart.getId());
			}
		}
		return carts_list;
	}
	
	/**
	 * 添加到购物车
	 */
	@Transactional(readOnly = false)
	public Map addGoodsCart(HttpServletRequest request, HttpServletResponse response, String id, String count,
			String gsp, String buy_type, String combin_ids, String combin_version, String app_user_id,
			String app_user_token, String cart_mobile_ids, String app_area_id) {
		
		// 0为添加成功，-3库存不足, -1添加失败，-2商品下架，添加失败，0普通商品添加,1组合配件添加，2组合套装添加
		int code = 0;
		
		int add_type = 0;
		boolean next = true;
		int limit_count = 0;
		Map<String, Object> json_map = Maps.newHashMap();
		
		Goods goods = this.goodsMapper.selectByPrimaryKey(CommUtil.null2Long(id));
		User user = SecurityUserHolder.getCurrentUser();
		if ((app_user_id != null) && (!app_user_id.equals(""))) {
			user = this.userMapper.selectByPrimaryKey(CommUtil.null2Long(app_user_id));
		}
		
		Map<String, Object> cart_map = Maps.newHashMap();
		if (goods.getGoods_status() != 0) {
			next = false;
			code = -2;
		}
		
		if (next) {
			if (CommUtil.null2String(gsp).equals("")) {
				gsp = this.cartTools.getGoodsDefaultGsp(goods);
			}
			int goods_inventory = goods.getGoods_inventory();
			if (app_user_id != null) {
				goods_inventory = CommUtil.null2Int(this.cartTools.generic_goods_default_Info(goods, gsp, app_area_id, app_user_id).get("count"));
			} else {
				goods_inventory = CommUtil.null2Int(this.cartTools.getGoodsDefaultInfo(request, goods, gsp).get("count"));
			}
			if (goods_inventory <= 0) {
				next = false;
				code = -3;
			}
		}
		
		if ((next) && (goods.getGoods_limit() == 1) && (goods.getGoods_limit_count() > 0)) {
			Map limit_map = this.cartTools.handle_limit_cart(goods, app_user_id);

			int limit_code = CommUtil.null2Int(limit_map.get("limit_code"));
			if (limit_code == 0) {
				limit_count = CommUtil.null2Int(limit_map.get("limit_count"));
				add_type = 3;
			}
			if ((limit_code == -1) || (limit_code == -2) || (limit_code == -4)) {
				next = false;
				code = -4;
			}
			if (limit_code == -3) {
				next = false;
				code = -5;
			}
		}
		if ((next) && (goods.getAdvance_sale_type() == 1)) {
			add_type = 4;
		}
		if (CommUtil.null2Int(count) <= 0) {
			next = false;
			code = -1;
		}
		if (next) {
			List<GoodsCart> carts_list = Lists.newArrayList();
			List<GoodsCart> carts_cookie = Lists.newArrayList();
			List<GoodsCart> carts_user = Lists.newArrayList();
			List<GoodsCart> carts_app = Lists.newArrayList();

			String cart_session_id = "";
			Cookie[] cookies = request.getCookies();
			if (cookies != null) {
				for (Cookie cookie : cookies) {
					if (cookie.getName().equals("cart_session_id")) {
						cart_session_id = CommUtil.null2String(cookie.getValue());
					}
				}
			}
			if (cart_session_id.equals("")) {
				cart_session_id = UUID.randomUUID().toString();
				Cookie cookie = new Cookie("cart_session_id", cart_session_id);
				cookie.setDomain(CommUtil.generic_domain(request));
				response.addCookie(cookie);
				request.getSession(false).setAttribute("cart_session_id", cart_session_id);
			} else {
				request.getSession(false).removeAttribute("cart_session_id");
			}
			Set<Long> mark_ids = Sets.newTreeSet();

			if ((cart_mobile_ids != null) && (!cart_mobile_ids.equals(""))) {
				String[] mobile_ids = cart_mobile_ids.split(",");
				for (String mobile_id : mobile_ids) {
					if (!(mobile_id).equals("")) {
						mark_ids.add(CommUtil.null2Long(mobile_id));
					}
				}
			}
			if (mark_ids.size() > 0) {
				cart_map.clear();
				cart_map.put("mark_ids", mark_ids);
				cart_map.put("cart_status", Integer.valueOf(0));
				carts_app = this.goodsCartMapper.queryPageList(cart_map);

			}
			if (!cart_session_id.equals("")) {
				cart_map.clear();
				cart_map.put("cart_session_id", cart_session_id);
				cart_map.put("cart_status", Integer.valueOf(0));

				carts_cookie = this.goodsCartMapper.queryPageList(cart_map);

			}
			if (user != null) {
				user = (User) this.userMapper.selectByPrimaryKey(user.getId());

				cart_map.clear();
				cart_map.put("user_id", user.getId());
				cart_map.put("cart_status", 0);
				carts_user = this.goodsCartMapper.queryPageList(cart_map);
				
				if (user.getStore() != null) {

					for (GoodsCart gc : carts_cookie) {
						if (gc.getGoods().getGoods_type() == 1) {
							if (gc.getGoods().getGoods_store().getId().equals(user.getStore().getId())) {
								this.goodsCartMapper.deleteById(gc.getId());
							}
						}
					}
				}
				carts_list.addAll(carts_user);
				for (GoodsCart cookie : carts_cookie) {
					boolean add = true;
					for (GoodsCart gc2 : carts_user) {
						if (cookie.getGoods().getId().equals(gc2.getGoods().getId())) {
							if (cookie.getSpec_info().equals(gc2.getSpec_info())) {
								add = false;
								this.goodsCartMapper.deleteById(cookie.getId());
							}
						}
					}
					if (add) {
						cookie.setCart_session_id(null);
						cookie.setUser(user);
						this.goodsCartMapper.updateById(cookie);
						carts_list.add(cookie);
					}
				}
				for (GoodsCart app : carts_app) {
					boolean add = true;
					for (GoodsCart gc2 : carts_list) {
						if ((app.getGoods().getId().equals(gc2.getGoods().getId()))
								&& (app.getSpec_info().equals(gc2.getSpec_info()))) {
							add = false;
							this.goodsCartMapper.deleteById(app.getId());
						}
					}
					if (add) {
						app.setCart_session_id(null);
						app.setUser(user);
						this.goodsCartMapper.updateById(app);
						carts_list.add(app);
					}
				}
			} else {
				carts_list.addAll(carts_cookie);
				carts_list.addAll(carts_app);
			}
			boolean new_cart = true;
			if (("parts".equals(buy_type)) && (combin_ids != null) && (!combin_ids.equals(""))) {
				add_type = 1;
				new_cart = true;
			}
			if (("suit".equals(buy_type)) && (combin_ids != null) && (!combin_ids.equals(""))) {
				add_type = 2;
				new_cart = false;
			}
			String[] gsp_ids = CommUtil.null2String(gsp).split(",");
			Arrays.sort(gsp_ids);
			String[] gsp_ids1 = null;
			int inventory;
			for (GoodsCart gc : carts_list) {
				
				if ((gsp_ids != null) && (gsp_ids.length > 0) && (gc.getGsps().size() > 0)) {
					gsp_ids1 = new String[gc.getGsps().size()];
					for (int i = 0; i < gc.getGsps().size(); i++) {
						gsp_ids1[i] = (gc.getGsps().get(i) != null
								? ((GoodsSpecProperty) gc.getGsps().get(i)).getId().toString() : "");
					}
					Arrays.sort(gsp_ids1);
				}
				if (((gc.getGoods().getId().toString().equals(id)) && (Arrays.equals(gsp_ids, gsp_ids1)))
						|| ((gc.getGoods().getId().toString().equals(id)) && (gsp_ids1 == null)
								&& (!"combin".equals(gc.getCart_type())))) {
					new_cart = false;
					if (add_type == 3) {
						int temp_count = CommUtil.null2Int(count) + gc.getCount();
						this.cartTools.calcul_limit_count(gc, temp_count, limit_count, app_user_id);
					}
					if (add_type == 4) {
						gc.setCount(CommUtil.null2Int(count));
					}
					if (add_type == 0) {
						inventory = CommUtil
								.null2Int(this.cartTools.getGoodsDefaultInfo(request, goods, gsp).get("count"));
						if (gc.getCount() + CommUtil.null2Int(count) >= inventory) {
							gc.setCount(inventory);
						} else {
							gc.setCount(gc.getCount() + CommUtil.null2Int(count));
						}
					}
					this.goodsCartMapper.updateById(gc);
					json_map.put("cart_id", gc.getId());
					break;
				}
			}
			
			if ((add_type != 2) && (new_cart)) {
				GoodsCart obj = new GoodsCart();
				obj.setCart_gsp(gsp);
				obj.setAddTime(new Date());
				obj.setCount(CommUtil.null2Int(count));
				String price = this.cartTools.getGoodsPriceByGsp(gsp, id);
				obj.setPrice(BigDecimal.valueOf(CommUtil.null2Double(price)));
				obj.setGoods(goods);
				obj.setRemain_count(-1);
				setGoodsCartSpec(goods, obj, gsp_ids);
				if (user == null) {
					obj.setCart_session_id(cart_session_id);
				} else {
					obj.setUser(user);
				}
				if (add_type == 3) {
					obj.setRemain_count(limit_count - obj.getCount());
					obj.setCart_type("limit");
				}
				if (add_type == 4) {
					obj.setCart_type("advance");
				}
				this.goodsCartMapper.saveEntity(obj);
				saveGoodsCartAndGsps(obj);
				json_map.put("cart_id", obj.getId());
			}
			
			boolean part_add;
			String part_price;
			if (add_type == 1) {
				String[] part_ids = combin_ids.split(",");
				for (String part_id : part_ids) {
					if (!part_id.equals("")) {
						Goods part_goods = this.goodsMapper.selectByPrimaryKey(CommUtil.null2Long(part_id));

						GoodsCart part_cart = new GoodsCart();
						part_add = true;
						part_cart.setAddTime(new Date());
						String temp_gsp_parts = null;
						temp_gsp_parts = this.cartTools.getGoodsDefaultGsp(part_goods);
						String[] part_gsp_ids = CommUtil.null2String(temp_gsp_parts).split(",");
						Arrays.sort(part_gsp_ids);
						for (GoodsCart gc : carts_list) {
							if ((part_gsp_ids != null) && (part_gsp_ids.length > 0) && (gc.getGsps() != null)
									&& (gc.getGsps().size() > 0)) {
								String[] gsp_ids2 = new String[gc.getGsps().size()];
								for (int i = 0; i < gc.getGsps().size(); i++) {
									gsp_ids2[i] = (gc.getGsps().get(i) != null
											? ((GoodsSpecProperty) gc.getGsps().get(i)).getId().toString() : "");
								}
								Arrays.sort(gsp_ids2);
								if (gc.getGoods().getId().toString().equals(part_id)) {
									if (Arrays.equals(part_gsp_ids, gsp_ids2)) {
										part_add = false;
									}
								}
							} else if (gc.getGoods().getId().toString().equals(part_id)) {
								part_add = false;
							}
						}
						if (part_add) {
							part_cart.setAddTime(new Date());
							part_cart.setCount(CommUtil.null2Int(Integer.valueOf(1)));
							part_price = this.cartTools.getGoodsPriceByGsp(temp_gsp_parts, part_id);
							part_cart.setPrice(BigDecimal.valueOf(CommUtil.null2Double(part_price)));
							part_cart.setGoods(part_goods);
							setGoodsCartSpec(part_goods, part_cart, part_gsp_ids);
							if (user == null) {
								part_cart.setCart_session_id(cart_session_id);
							} else {
								part_cart.setUser(user);
							}
							this.goodsCartMapper.saveEntity(part_cart);
							
							saveGoodsCartAndGsps(part_cart);
							
						}
					}
				}
				code = 1;
			}
			if (add_type == 2) {
				boolean suit_add = true;
				Map<String, Object> params = Maps.newHashMap();
				params.put("combin_main", 1);
				params.put("cart_type", "combin");
				params.put("goods_id", goods.getId());
				
				if (user != null) {
					params.put("user_id", user.getId());
				} else {
					params.put("cart_session_id", cart_session_id);
				}
				params.put("goods_id", goods.getId());
				
				List<GoodsCart> suit_carts = this.goodsCartMapper.queryPageList(params);
				
				if (suit_carts.size() > 0) {
					if (suit_carts.get(0).getCombin_version()
							.contains(CommUtil.null2String(combin_version))) {
						suit_add = false;
					}
				}
				
				if (suit_add) {
					Map<String, Object> suit_map = null;
					params.clear();
					params.put("main_goods_id", CommUtil.null2Long(id));
					params.put("combin_type", Integer.valueOf(0));
					params.put("combin_status", Integer.valueOf(1));
					List<CombinPlan> suits = this.combinPlanMapper.queryPageList(params);

					for (CombinPlan plan : suits) {
						List<Map> map_list = JSON.parseArray(plan.getCombin_plan_info(), Map.class);
						for (Map temp_map : map_list) {
							String ids = this.goodsViewTools.getCombinPlanGoodsIds(temp_map);
							if (ids.equals(combin_ids)) {
								suit_map = temp_map;
								break;
							}
						}
					}
					String combin_mark = "combin" + UUID.randomUUID();
					if (suit_map != null) {
						String suit_ids = "";
						List<Map> goods_list = (List) suit_map.get("goods_list");
						for (Map good_map : goods_list) {
							Goods suit_goods = (Goods) this.goodsMapper
									.selectByPrimaryKey(CommUtil.null2Long(good_map.get("id")));
							GoodsCart cart = new GoodsCart();
							cart.setAddTime(new Date());
							cart.setGoods(suit_goods);
							String[] temp_gsp_ids = CommUtil.null2String(this.cartTools.getGoodsDefaultGsp(goods))
									.split(",");
							setGoodsCartSpec(suit_goods, cart, temp_gsp_ids);
							cart.setCombin_mark(combin_mark);
							cart.setCart_type("combin");
							cart.setPrice(
									BigDecimal.valueOf(CommUtil.null2Double(suit_goods.getGoods_current_price())));
							cart.setCount(1);
							if (user == null) {
								cart.setCart_session_id(cart_session_id);
							} else {
								cart.setUser(user);
							}
							this.goodsCartMapper.saveEntity(cart);
							
							saveGoodsCartAndGsps(cart);
							
							suit_ids = suit_ids + "," + CommUtil.null2String(cart.getId());
						}
						GoodsCart obj = new GoodsCart();
						String combin_main_default_gsp = this.cartTools.getGoodsDefaultGsp(goods);
						obj.setCart_gsp(combin_main_default_gsp);
						obj.setAddTime(new Date());
						obj.setCount(CommUtil.null2Int(count));
						String price = this.cartTools.getGoodsPriceByGsp(gsp, id);
						obj.setPrice(BigDecimal.valueOf(CommUtil.null2Double(price)));
						obj.setGoods(goods);
						if (user == null) {
							obj.setCart_session_id(cart_session_id);
						} else {
							obj.setUser(user);
						}
						obj.setCombin_suit_ids(suit_ids);
						obj.setCombin_version("【套装" + combin_version + "】");
						obj.setCombin_main(1);
						obj.setCount(CommUtil.null2Int(count));
						obj.setPrice(BigDecimal.valueOf(CommUtil.null2Double(suit_map.get("plan_goods_price"))));
						obj.setCombin_mark(combin_mark);
						obj.setCart_type("combin");
						String[] temp_gsp_ids = CommUtil.null2String(this.cartTools.getGoodsDefaultGsp(goods))
								.split(",");
						setGoodsCartSpec(goods, obj, temp_gsp_ids);
						obj.setCart_gsp(this.cartTools.getGoodsDefaultGsp(goods));
						suit_map.put("suit_count", Integer.valueOf(CommUtil.null2Int(count)));
						String suit_all_price = CommUtil
								.formatMoney(Double.valueOf(CommUtil.mul(Integer.valueOf(CommUtil.null2Int(count)),
										Double.valueOf(CommUtil.null2Double(suit_map.get("plan_goods_price"))))));
						suit_map.put("suit_all_price", suit_all_price);
						suit_map.put("suit_name", "[套装" + combin_version + "]");
						obj.setCombin_suit_info(JSON.toJSONString(suit_map));
						this.goodsCartMapper.saveEntity(obj);
						
						saveGoodsCartAndGsps(obj);
						
						code = 2;
					} else {
						code = -1;
					}
				} else {
					GoodsCart update_cart = suit_carts.get(0);
					Map<String, Object> temp_map = JSON.parseObject(update_cart.getCombin_suit_info());
					temp_map.put("suit_count", (update_cart.getCount() + 1));
					update_cart.setCombin_suit_info(JSON.toJSONString(temp_map));
					update_cart.setCount(update_cart.getCount() + 1);
					this.goodsCartMapper.updateById(update_cart);
					code = 2;
				}
			}
		}
		json_map.put("code", Integer.valueOf(code));
		return json_map;
	}

	private void saveGoodsCartAndGsps(GoodsCart obj) {
		
		if(obj!=null 
				&& obj.getId()!=null 
				&& obj.getGsps()!=null 
				&& obj.getGsps().size()>0){
			
			Map<String,Object> goodsCartGsps = Maps.newHashMap();
			
			goodsCartGsps.put("cartId",obj.getId());
			goodsCartGsps.put("gsps", obj.getGsps());
			
			this.goodsCartMapper.saveGoodsCartAndGsps(goodsCartGsps);
		}
		
	}
	
	@Transactional(readOnly = false)
	private void setGoodsCartSpec(Goods goods, GoodsCart obj, String[] gsp_ids) {
		String spec_info = "";
		List<Map> goods_specs_info = goods.getGoods_specs_info() == null ? new ArrayList() : JSON.parseArray(goods.getGoods_specs_info(), Map.class);
		for (String gsp_id : gsp_ids) {
			GoodsSpecProperty spec_property = this.goodsSpecPropertyService.selectByPrimaryKey(CommUtil.null2Long(gsp_id));
			if (spec_property != null) {
				obj.getGsps().add(spec_property);
				Map<String, Object> params = Maps.newHashMap();
				List<Object> spec_propertyIds = Lists.newArrayList();
				spec_propertyIds.add(spec_property.getId());
				
				params.put("goods_id", goods.getId());
				params.put("gsps", spec_propertyIds);
				
				this.goodsMapper.batchInsertGoodsSpecs(params);
				
				spec_info = spec_info + spec_property.getSpec().getName() + "：";

				if (goods_specs_info.size() > 0) {
					for (Map map : goods_specs_info) {
						if (CommUtil.null2Long(map.get("id")).equals(spec_property.getId())) {
							spec_info = spec_info + map.get("name").toString();
						}
					}
				} else {
					spec_info = spec_info + spec_property.getValue();
				}
				spec_info = spec_info + "<br>";
			}
		}
		obj.setSpec_info(spec_info);
	}

	@Autowired
	private BuyGiftMapper buyGiftMapper;

	@Transactional(readOnly = false)
	public Map adjustGoodscartCount(HttpServletRequest request, HttpServletResponse response, String gc_id,
			String count, String gcs, String gift_id, String app_user_id, String app_user_token, String app_cart_ids) {
		List<GoodsCart> carts = null;
		if ((app_user_id == null) || (app_user_token == null) || (app_cart_ids == null)) {
			carts = cart_list(request, null, null, null, false);
		} else {
			carts = cart_list(request, CommUtil.null2Long(app_user_id), app_user_token, app_cart_ids, false);
		}
		Map map = Maps.newHashMap();
		String code = "100";
		double gc_price = 0.0D;
		double total_price = 0.0D;
		String cart_type = "";
		Goods goods = null;
		if ((gc_id != null) && (!gc_id.equals(""))) {
			GoodsCart gc = (GoodsCart) this.goodsCartMapper.selectByPrimaryKey(CommUtil.null2Long(gc_id));

			if (CommUtil.null2String(count).length() <= 9) {
				cart_type = CommUtil.null2String(gc.getCart_type());
				goods = gc.getGoods();
				if (cart_type.equals("")) {
					if (goods.getGroup_buy() == 2) {
						GroupGoods gg = new GroupGoods();
						for (GroupGoods gg1 : goods.getGroup_goods_list()) {
							if (gg1.getGg_goods().getId().equals(goods.getId())) {
								gg = gg1;
								break;
							}
						}
						if (gg.getGg_count() >= CommUtil.null2Int(count)) {
							gc.setPrice(BigDecimal.valueOf(CommUtil.null2Double(gg.getGg_price())));
							gc_price = CommUtil.mul(gg.getGg_price(), count);
							gc.setCount(CommUtil.null2Int(count));
							this.goodsCartMapper.updateById(gc);
						} else {
							code = "300";
						}
					} else if (goods.getF_sale_type() == 1) {
						code = "500";
					} else {
						String gsp = "";
						for (GoodsSpecProperty gs : gc.getGsps()) {
							gsp = gs.getId() + "," + gsp;
						}
						int inventory = goods.getGoods_inventory();
						if (app_user_id != null) {
							inventory = CommUtil.null2Int(this.cartTools
									.generic_goods_default_Info(goods, gsp, null, app_user_id).get("count"));
						} else {
							inventory = CommUtil
									.null2Int(this.cartTools.getGoodsDefaultInfo(request, goods, gsp).get("count"));
						}
						if ((inventory >= CommUtil.null2Int(count)) && (CommUtil.null2String(count).length() <= 9)
								&& (gc.getGoods().getGroup_buy() != 2)) {
							if (gc.getId().toString().equals(gc_id)) {
								gc.setCount(CommUtil.null2Int(count));
								this.goodsCartMapper.updateById(gc);
								gc_price = CommUtil.mul(gc.getPrice(), count);
							}
						} else {
							gc.setCount(inventory);
							this.goodsCartMapper.updateById(gc);
							code = "200";
						}
					}
				}
				boolean ret;
				if ("limit".equals(cart_type)) {
					Map limit_map = this.cartTools.handle_limit_cart(goods, app_user_id);

					int limit_code = CommUtil.null2Int(limit_map.get("limit_code"));
					int limit_count = CommUtil.null2Int(limit_map.get("limit_count"));
					if (limit_code == 0) {
						int temp_count = CommUtil.null2Int(count);
						ret = this.cartTools.calcul_limit_count(gc, temp_count, limit_count, app_user_id);
						if (!ret) {
							code = "400";
						}
						this.goodsCartMapper.updateById(gc);
					} else {
						code = "400";
					}
					gc_price = CommUtil.mul(gc.getPrice(), count);
				}
				if (("combin".equals(cart_type)) && (gc.getCombin_main() == 1)) {
					if (goods.getGoods_inventory() >= CommUtil.null2Int(count)) {
						gc.setCount(CommUtil.null2Int(count));
						this.goodsCartMapper.updateById(gc);
						String suit_all_price = "0.00";
						GoodsCart suit = gc;
						Map suit_map = JSON.parseObject(suit.getCombin_suit_info());
						suit_map.put("suit_count", CommUtil.null2Int(count));
						suit_all_price = CommUtil.formatMoney(CommUtil.mul(
								CommUtil.null2Int(count),
								CommUtil.null2Double(suit_map.get("plan_goods_price"))));
						suit_map.put("suit_all_price", suit_all_price);
						String new_json = JSON.toJSONString(suit_map);
						suit.setCombin_suit_info(new_json);
						suit.setCount(CommUtil.null2Int(count));
						this.goodsCartMapper.updateById(suit);
						gc_price = CommUtil.null2Double(suit_all_price);
					} else {
						code = "200";
					}
				}
				if (gift_id != null) {
					BuyGift bg = (BuyGift) this.buyGiftMapper.selectByPrimaryKey(CommUtil.null2Long(gift_id));

					Set<Long> bg_ids = Sets.newHashSet();
					if (bg != null) {
						bg_ids.add(bg.getId());
					}
					List<GoodsCart> g_carts = Lists.newArrayList();

					if (CommUtil.null2String(gcs).equals("")) {
						for (GoodsCart gCart : carts) {
							if ((gCart.getGoods().getOrder_enough_give_status() == 1)
									&& (gCart.getGoods().getBuyGift_id() != null)) {
								bg_ids.add(gCart.getGoods().getBuyGift_id());
							}
						}
						g_carts = carts;
					} else {
						String[] gc_ids = gcs.split(",");
						int j = gc_ids.length;
						for (int i = 0; i < j; i++) {
							String g_id = gc_ids[i];
							GoodsCart goodsCart = this.goodsCartMapper.selectByPrimaryKey(CommUtil.null2Long(g_id));
							if ((goodsCart != null) && (goodsCart.getGoods().getOrder_enough_give_status() == 1)) {
								if (goodsCart.getGoods().getBuyGift_id() != null) {
									bg_ids.add(goodsCart.getGoods().getBuyGift_id());
									g_carts.add(goodsCart);
								}
							}
						}
					}
					Map<Long, List> gc_map = Maps.newHashMap();
					for (Long id : bg_ids) {
						gc_map.put(id, new ArrayList());
					}
					
					for (GoodsCart cart : g_carts) {
						
						if ((cart.getGoods().getOrder_enough_give_status() != 1)
								|| (cart.getGoods().getBuyGift_id() == null)) {
							break;
						}
						
						Iterator<Entry<Long, List>>  ite = gc_map.entrySet().iterator();
//						Object entry = (Map.Entry) ((Iterator) ite).next();
//						if (cart.getGoods().getBuyGift_id().equals(((Map.Entry) entry).getKey())) {
//							((List) ((Map.Entry) entry).getValue()).add(cart);
//						}
						
						while(ite.hasNext()){
							Entry entry = ite.next();
							if (cart.getGoods().getBuyGift_id().equals(entry.getKey())) {
								((List) entry.getValue()).add(cart);
							}
						}
						
					}
					
					List<String> enough_bg_ids = Lists.newArrayList();

					Object entry = gc_map.entrySet().iterator();
					while (((Iterator) entry).hasNext()) {
						entry = (Entry) ((Iterator) entry).next();
						BuyGift buyGift = (BuyGift) this.buyGiftMapper
								.selectByPrimaryKey((Long) ((Entry) entry).getKey());

						List<GoodsCart> arrs = (List) ((Entry) entry).getValue();
						BigDecimal bd = new BigDecimal("0.00");
						for (GoodsCart arr : arrs) {
							bd = bd.add(
									BigDecimal.valueOf(CommUtil.mul(arr.getPrice(), Integer.valueOf(arr.getCount()))));
						}
						if (bd.compareTo(buyGift.getCondition_amount()) >= 0) {
							enough_bg_ids.add(buyGift.getId().toString());
						}
					}
					map.put("bg_ids", enough_bg_ids);
				}
			} else {
				code = "200";
			}
			map.put("count", Integer.valueOf(gc.getCount()));
		}
		total_price = this.cartTools.getPriceByCarts(carts, gcs);
		map.put("gc_price", CommUtil.formatMoney(Double.valueOf(gc_price)));
		map.put("total_price", CommUtil.formatMoney(Double.valueOf(total_price)));
		map.put("code", code);
		Map price_map = this.cartTools.getEnoughReducePriceByCarts(carts, gcs);
		Map<Long, String> erMap = (Map) price_map.get("erString");
		map.put("enough_reduce_price", CommUtil.formatMoney(price_map.get("reduce")));
		map.put("before", CommUtil.formatMoney(price_map.get("all")));
		for (Iterator<Long> gc_map = erMap.keySet().iterator(); gc_map.hasNext();) {
			long k = ((Long) gc_map.next()).longValue();
			map.put("erString" + k, erMap.get(Long.valueOf(k)));
		}
		System.out.println("===================map:" + map);
		return map;
	}

}
