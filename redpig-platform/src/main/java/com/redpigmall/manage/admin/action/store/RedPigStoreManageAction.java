package com.redpigmall.manage.admin.action.store;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.redpigmall.api.annotation.SecurityMapping;
import com.redpigmall.api.beans.BeanWrapper;
import com.redpigmall.api.mv.RedPigJModelAndView;
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.api.tools.ClusterSyncTools;
import com.redpigmall.api.tools.CommUtil;
import com.redpigmall.api.tools.ObjectUtils;
import com.redpigmall.api.tools.WebForm;
import com.redpigmall.lucene.RedPigLuceneUtil;
import com.redpigmall.manage.admin.action.base.BaseAction;
import com.redpigmall.msg.SpelTemplate;
import com.redpigmall.domain.Accessory;
import com.redpigmall.domain.Album;
import com.redpigmall.domain.Area;
import com.redpigmall.domain.ComplaintGoods;
import com.redpigmall.domain.Coupon;
import com.redpigmall.domain.CouponInfo;
import com.redpigmall.domain.Evaluate;
import com.redpigmall.domain.Favorite;
import com.redpigmall.domain.GoldLog;
import com.redpigmall.domain.GoldRecord;
import com.redpigmall.domain.Goods;
import com.redpigmall.domain.GoodsCart;
import com.redpigmall.domain.GoodsClass;
import com.redpigmall.domain.GoodsSpecProperty;
import com.redpigmall.domain.GoodsSpecification;
import com.redpigmall.domain.GroupInfo;
import com.redpigmall.domain.GroupLifeGoods;
import com.redpigmall.domain.Menu;
import com.redpigmall.domain.MerchantServices;
import com.redpigmall.domain.Message;
import com.redpigmall.domain.PayoffLog;
import com.redpigmall.domain.Role;
import com.redpigmall.domain.Store;
import com.redpigmall.domain.StoreGrade;
import com.redpigmall.domain.StorePoint;
import com.redpigmall.domain.SysConfig;
import com.redpigmall.domain.Template;
import com.redpigmall.domain.User;
import com.redpigmall.domain.WaterMark;
import com.redpigmall.domain.ZTCGoldLog;

/**
 * 
 * <p>
 * Title: StoreManageAction.java
 * </p>
 * 
 * <p>
 * Description: 运营商店铺管理控制器，用来管理店铺，可以添加、修改、删除店铺，运营商所有对店铺的操作均通过该管理控制器完成
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
 * @date 2014-5-12
 * 
 * @version redpigmall_b2b2c v8.0 2016版
 */
@Controller
public class RedPigStoreManageAction extends BaseAction {

  /**
  	 * 
  	 * @param request
  	 * @param response
  	 * @param currentPage
  	 * @param orderBy
  	 * @param orderType
  	 * @param store_status
  	 * @param store_name
  	 * @param grade_id
  	 * @param mark
  	 * @return
  	 */
	@SecurityMapping(title = "店铺列表", value = "/store_list*", rtype = "admin", rname = "店铺管理", rcode = "admin_store_set", rgroup = "店铺")
	@RequestMapping({ "/store_list" })
	public ModelAndView store_list(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,
			String orderType, String store_status, String store_name,
			String grade_id, String mark) {
		
		ModelAndView mv = new RedPigJModelAndView("admin/blue/store_list.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		
		Map<String,Object> params = redPigQueryTools.getParams(currentPage, "addTime", "desc");
		
		if (store_status == null) {
			if ("no".equals(mark)) {
				params.put("redpig_store_status_less", Integer.valueOf(15));
				mv.addObject("mark", mark);
			} else {
				params.put("redpig_store_status_more", Integer.valueOf(10));
			}
		}
		
		if ((store_status != null) && (!"".equals(store_status))) {
			if (mark == "") {
				
				params.put("store_status", Integer.valueOf(CommUtil.null2Int(store_status)));
			} else {
				if ("no".equals(mark)) {
					if ("".equals(store_status)) {
						
						params.put("redpig_store_status_less", Integer.valueOf(15));
						
					} else {
						
						params.put("store_status", Integer.valueOf(CommUtil.null2Int(store_status)));
						
					}
				}
				mv.addObject("mark", mark);
			}
		}
		if (("".equals(store_status)) && ("no".equals(mark))) {
			
			params.put("redpig_store_status_less", Integer.valueOf(15));
			
			mv.addObject("mark", mark);
		}
		if (("".equals(store_status)) && (!"no".equals(mark))) {
			
			params.put("redpig_store_status_more", Integer.valueOf(10));
			
		}
		mv.addObject("mark", mark);
		mv.addObject("store_status", store_status);
		if ((store_name != null) && (!store_name.equals(""))) {
			
			params.put("redpig_store_name_like", CommUtil.null2String(store_name));
			
			mv.addObject("store_name", store_name);
		}
		
		if ((grade_id != null) && (!grade_id.equals(""))) {
			
			params.put("redpig_grade_id", CommUtil.null2Long(grade_id));
			
			mv.addObject("grade_id", grade_id);
		}
		
		IPageList pList = this.storeService.list(params);
		
		CommUtil.saveIPageList2ModelAndView("", "", null, pList, mv);
		params.clear();
		
		List<StoreGrade> grades = this.storeGradeService.queryPageList(params);
		
		mv.addObject("grades", grades);
		
		return mv;
	}

	/**
	 * 店铺添加1
	 * @param request
	 * @param response
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "店铺添加1", value = "/store_add*", rtype = "admin", rname = "店铺管理", rcode = "admin_store_set", rgroup = "店铺")
	@RequestMapping({ "/store_add" })
	public ModelAndView store_add(HttpServletRequest request,
			HttpServletResponse response, String currentPage) {
		ModelAndView mv = new RedPigJModelAndView("admin/blue/store_add.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		mv.addObject("currentPage", currentPage);
		return mv;
	}
  
	 
	/**
	 * 店铺添加2
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param userName
	 * @param list_url
	 * @param add_url
	 * @return
	 */
	
	@SecurityMapping(title = "店铺添加2", value = "/store_new*", rtype = "admin", rname = "店铺管理", rcode = "admin_store_set", rgroup = "店铺")
	@RequestMapping({ "/store_new" })
	public ModelAndView store_new(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String userName,
			String list_url, String add_url) {
		ModelAndView mv = new RedPigJModelAndView("admin/blue/store_new.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		
		Map<String,Object> params = Maps.newHashMap();
		params.put("userName", userName);
		
		User user = this.userService.queryByProperty(params);
		
		Store store = null;
		String cart_session = CommUtil.randomString(32);
		request.getSession(false).setAttribute("cart_session", cart_session);
		mv.addObject("cart_session", cart_session);
		if (user != null) {
			params.clear();
			params.put("user_id", user.getId());
			
			store = this.storeService.queryByProperty(params);
		}
		if (user == null) {
			mv = new RedPigJModelAndView("admin/blue/tip.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request,
					response);
			
			mv.addObject("op_tip", "不存在该用户");
			mv.addObject("list_url", list_url);
		} else if (store == null) {
			if (user.getUserRole().indexOf("ADMIN") >= 0) {
				mv = new RedPigJModelAndView("admin/blue/tip.html",
						this.configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 0, request,
						response);
				
				mv.addObject("op_tip", "管理员不允许申请成为商家！");
				mv.addObject("list_url", list_url);
				return mv;
			}
			
			params.clear();
			params.put("parent_id", -1);
			
			List<Area> areas = this.areaService.queryPageList(params);
			
			params.clear();
			
			List<StoreGrade> grades = this.storeGradeService.queryPageList(params);
			
			mv.addObject("grades", grades);
			mv.addObject("areas", areas);
			mv.addObject("currentPage", currentPage);
			mv.addObject("user", user);
			
			params.clear();
			params.put("parent", -1);
			
			List<GoodsClass> gcs = this.goodsclassService.queryPageList(params);
			
			params.clear();
			
			List<MerchantServices> ms_list = this.merchantServices.queryPageList(params);
			
			mv.addObject("ms_list", ms_list);
			mv.addObject("goodsClass", gcs);
		} else {
			mv = new RedPigJModelAndView("admin/blue/tip.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request,
					response);
			mv.addObject("op_tip", "该用户已经开通店铺");
			mv.addObject("list_url", add_url);
		}
		return mv;
	}
	
	
	
	/**
	 * 店铺经营类目Ajax加载
	 * @param request
	 * @param response
	 * @param cid
	 * @return
	 */
	@SecurityMapping(title = "店铺经营类目Ajax加载", value = "/store_gc_ajax*", rtype = "admin", rname = "店铺管理", rcode = "admin_store_set", rgroup = "店铺")
	@RequestMapping({ "/store_gc_ajax" })
	public ModelAndView store_goodsclass_dialog(
			HttpServletRequest request,
			HttpServletResponse response, 
			String cid) {
		
		ModelAndView mv = new RedPigJModelAndView(
				"admin/blue/store_gc_ajax.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		
		if ((cid != null) && (!cid.equals(""))) {
			GoodsClass goodsClass = this.goodsclassService.selectByPrimaryKey(CommUtil.null2Long(cid));
			
			List<GoodsClass> gcs = goodsClass.getChilds();
			mv.addObject("gcs", gcs);
		}
		return mv;
	}

	/**
	 * 店铺公司信息查看
	 * @param request
	 * @param response
	 * @param id
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "店铺公司信息查看", value = "/store_company*", rtype = "admin", rname = "店铺管理", rcode = "admin_store_set", rgroup = "店铺")
	@RequestMapping({ "/store_company" })
	public ModelAndView store_company(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage) {
		ModelAndView mv = new RedPigJModelAndView("admin/blue/store_company.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		Store store = this.storeService.selectByPrimaryKey(CommUtil.null2Long(id));
		
		mv.addObject("store", store);
		Area area1 = store.getLicense_area();
		mv.addObject("license_area_info",this.areaManageTools.generic_area_info(area1));
		Area area2 = store.getLicense_c_area();
		mv.addObject("license_c_area_info",this.areaManageTools.generic_area_info(area2));
		Area area3 = store.getBank_area();
		mv.addObject("bank_area_info",this.areaManageTools.generic_area_info(area3));
		return mv;
	}
  
	
	/**
	 * 店铺编辑
	 * @param request
	 * @param response
	 * @param id
	 * @param currentPage
	 * @return
	 */
	@SuppressWarnings({ "rawtypes" })
	@SecurityMapping(title = "店铺编辑", value = "/store_edit*", rtype = "admin", rname = "店铺管理", rcode = "admin_store_set", rgroup = "店铺")
	@RequestMapping({ "/store_edit" })
	public ModelAndView store_edit(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage) {
		ModelAndView mv = new RedPigJModelAndView("admin/blue/store_edit.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		Map<String,Object> params = Maps.newHashMap();
		String cart_session = CommUtil.randomString(32);
		request.getSession(false).setAttribute("cart_session", cart_session);
		mv.addObject("cart_session", cart_session);
		if ((id != null) && (!id.equals(""))) {
			Store store = this.storeService.selectByPrimaryKey(Long.parseLong(id));
			
			if (store.getStore_service_info() != null) {
				List mes = JSON.parseArray(store.getStore_service_info());
				mv.addObject("mes", mes);
			}
			
			List<MerchantServices> ms_list = this.merchantServices.queryPageList(params);
			
			mv.addObject("ms_list", ms_list);
			params.clear();
			params.put("parent_id", -1);
			
			List<Area> areas = this.areaService.queryPageList(params);
			
			mv.addObject("areas", areas);
			
			mv.addObject("store", store);
			mv.addObject("currentPage", currentPage);
			mv.addObject("edit", Boolean.valueOf(true));
			mv.addObject("goodsClass_main",
					this.goodsclassService.selectByPrimaryKey(store.getGc_main_id()));
			
			mv.addObject("goodsClass_detail", this.storeTools.query_store_DetailGc(store.getGc_detail_info()));
			
			if (store.getArea() != null) {
				String info = this.areaManageTools.generic_area_info(store.getArea());
				mv.addObject("area_info", info);
				mv.addObject("area_id", store.getArea().getId());
			}
			
			params.clear();
			
			List<StoreGrade> sg_list = this.storeGradeService.queryPageList(params);
			
			params.put("level", 0);
			
			List<GoodsClass> gc_list = this.goodsclassService.queryPageList(params);
			
			mv.addObject("gc_list", gc_list);
			mv.addObject("sg_list", sg_list);
			params.clear();
			
			List<StoreGrade> grades = this.storeGradeService.queryPageList(params);
			mv.addObject("grades", grades);
		}
		return mv;
	}
  
	/**
	 * 店铺保存
	 * @param request
	 * @param response
	 * @param id
	 * @param store_status
	 * @param currentPage
	 * @param cmd
	 * @param list_url
	 * @param add_url
	 * @param user_id
	 * @param grade_id
	 * @param area_id
	 * @param validity
	 * @param gc_main_id_clone
	 * @param gc_detail_ids
	 * @param gc_detail_info
	 * @param serve_ids
	 * @param cart_session
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@SecurityMapping(title = "店铺保存", value = "/store_save*", rtype = "admin", rname = "店铺管理", rcode = "admin_store_set", rgroup = "店铺")
	@RequestMapping({ "/store_save" })
	public ModelAndView store_save(
			HttpServletRequest request,
			HttpServletResponse response, 
			String id, String store_status,
			String currentPage, String cmd, String list_url, String add_url,
			String user_id, String grade_id, String area_id, String validity,
			String gc_main_id_clone, String gc_detail_ids,
			String gc_detail_info, String serve_ids, String cart_session)
			throws Exception {
		ModelAndView mv = null;
		Store store = null;
		String cart_session1 = (String) request.getSession(false).getAttribute(
				"cart_session");
		if (cart_session.equals(cart_session1)) {
			request.getSession(false).removeAttribute("cart_session");
			if (id.equals("")) {
				store = (Store) WebForm.toPo(request, Store.class);
				store.setAddTime(new Date());
			} else {
				Store obj = this.storeService.selectByPrimaryKey(Long.valueOf(Long.parseLong(id)));
				
				store = (Store) WebForm.toPo(request, obj);
			}
			if ((store_status != null) && (!store_status.equals(""))) {
				int i;
				if ((user_id != null) && (!user_id.equals(""))) {
					User user = this.userService.selectByPrimaryKey(CommUtil.null2Long(user_id));
					
					store.setUser(user);
					Area area = this.areaService.selectByPrimaryKey(CommUtil.null2Long(area_id));
					store.setArea(area);
					StoreGrade grade = this.storeGradeService.selectByPrimaryKey(CommUtil.null2Long(grade_id));
					System.out.println(store.getGrade());
					store.setGrade(grade);
					store.setGc_main_id(CommUtil.null2Long(gc_main_id_clone));
					store.setValidity(CommUtil.formatDate(validity));
					if(StringUtils.isNotBlank(gc_detail_info)){
						store.setGc_detail_info(gc_detail_info);
					}
					if ((serve_ids != null) && (!"".equals(serve_ids))) {
						List list = Lists.newArrayList();
						String[] arr = serve_ids.split(",");
						for (i = 0; i < arr.length; i++) {
							String s_id = arr[i];
							Map<String,Object> m = Maps.newHashMap();
							MerchantServices ms = this.merchantServices.selectByPrimaryKey(CommUtil.null2Long(s_id));
							m.put("id", ms.getId());
							m.put("name", ms.getServe_name());
							m.put("price", Integer.valueOf(ms.getServe_price()));
							if (ms.getService_img() != null) {
								m.put("img", ms.getService_img().getPath()
										+ "/" + ms.getService_img().getName());
							}
							list.add(i, m);
						}
						store.setStore_service_info(JSON.toJSONString(list));
					}
					
					if(store.getId() == null){
						this.storeService.saveEntity(store);
					}else{
						this.storeService.updateById(store);
					}
					
					
					if (store.getPoint() == null) {
						StorePoint sp = new StorePoint();
						sp.setAddTime(new Date());
						sp.setStore(store);
						sp.setStore_evaluate(BigDecimal.valueOf(0L));
						sp.setDescription_evaluate(BigDecimal.valueOf(0L));
						sp.setService_evaluate(BigDecimal.valueOf(0L));
						sp.setShip_evaluate(BigDecimal.valueOf(0L));
						this.storePointService.saveEntity(sp);
					}
					user.setStore(store);
					this.userService.updateById(user);
				}
				if ((store_status.equals("5")) || (store_status.equals("10"))) {
					store.setStore_status(CommUtil.null2Int(store_status));
				} else if ((store_status.equals("6"))
						|| (store_status.equals("11"))) {
					store.setStore_status(CommUtil.null2Int(store_status));
					send_site_msg(request,
							"msg_toseller_store_update_refuse_notify", store);
				} else if (store_status.equals("15")) {
					String store_user_id = CommUtil.null2String(store.getUser()
							.getId());
					if ((store_user_id != null) && (!store_user_id.equals(""))) {
						User store_user = this.userService.selectByPrimaryKey(Long.parseLong(store_user_id));
						store_user.setStore(store);
						if (store_user.getUserRole().equals("ADMIN")) {
							store_user.setUserRole("ADMIN_SELLER");
						} else {
							store_user.setUserRole("SELLER");
						}
						Map params = Maps.newHashMap();
						params.put("type", "SELLER");
						List<Role> roles = this.roleService.queryPageList(params);
						for (Role role : roles) {
							store_user.getRoles().add(role);
						}
						store_user.getRoles().addAll(roles);
//						this.userService.updateById(store_user);
						this.userService.deleteUserRole(store_user.getId(), store_user.getRoles());
						
						this.userService.saveUserRole(store_user.getId(), store_user.getRoles());
						
						if (store.getStore_start_time() == null) {
							store.setStore_start_time(new Date());
							send_site_msg(request,"msg_toseller_store_update_allow_notify",store);
						}
						
						
						
						
					}
					store.setStore_status(CommUtil.null2Int(store_status));
				} else if (store_status.equals("20")) {
					store.setStore_status(CommUtil.null2Int(store_status));
					if ((!id.equals(""))
							&& (CommUtil.null2Int(store.getStore_status()) == 20)) {
						send_site_msg(request,
								"msg_toseller_store_closed_notify", store);
					}
				}
			}
			
			if (store.getStore_recommend()) {
				store.setStore_recommend_time(new Date());
			} else {
				store.setStore_recommend_time(null);
			}
			
			this.storeService.updateById(store);
			Map<String,Object> params = Maps.newHashMap();
			//1、搜索出所有父菜单
			params.clear();
			params.put("parent",-1);
			params.put("type","SELLER");
			
			User user = store.getUser();
			List<Menu> menus = this.menuService.queryPageList(params);
			List<Menu> ms = Lists.newArrayList();
			//2、通过父菜单获取所有子菜单
			for (Menu menu : menus) {
				ms.add(menu);
				ms.addAll(menu.getAllChilds());
			}
			
			this.userService.deleteUserMenu(user.getId());
			
			this.userService.saveUserMenu(user.getId(), ms);
			
			mv = new RedPigJModelAndView("admin/blue/success.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request,
					response);
			mv.addObject("list_url", list_url);
			mv.addObject("op_title", "保存店铺成功");
			if (add_url != null) {
				mv.addObject("add_url", add_url + "?currentPage=" + currentPage);
			}
		} else {
			mv = new RedPigJModelAndView("admin/blue/error.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request,
					response);
			mv.addObject("op_title", "不能重复提交");
		}
		return mv;
	}
	/**
	 * 
	 * send_site_msg:发送短信. <br/>
	 *
	 * @author redpig
	 * @param request
	 * @param mark
	 * @param store
	 * @since JDK 1.8
	 */
	
	private void send_site_msg(HttpServletRequest request, String mark,Store store) {
		Map<String, Object> params = Maps.newHashMap();
		params.put("mark", mark);

		List<Template> templates = this.templateService.queryPageList(params);
		Template template = templates.get(0);

		if ((template != null) && (template.getOpen()) && (store.getUser() != null)) {
			ExpressionParser exp = new SpelExpressionParser();
			EvaluationContext context = new StandardEvaluationContext();
			context.setVariable("reason", store.getViolation_reseaon());
			context.setVariable("user", store.getUser());
			context.setVariable("store", store);
			context.setVariable("config", this.configService.getSysConfig());
			context.setVariable("send_time",
					CommUtil.formatLongDate(new Date()));
			Expression ex = exp.parseExpression(template.getContent(),
					new SpelTemplate());
			String content = (String) ex.getValue(context, String.class);
			params = Maps.newHashMap();
			params.put("userName", "admin");
			params.put("userRole", "ADMIN");
			List<User> fromUsers = this.userService.queryPageList(params, 0, 1);
			if (fromUsers.size() > 0) {
				Message msg = new Message();
				msg.setAddTime(new Date());
				msg.setContent(content);
				msg.setFromUser((User) fromUsers.get(0));
				msg.setTitle(template.getTitle());
				msg.setToUser(store.getUser());
				msg.setType(0);
				this.messageService.saveEntity(msg);
			}
		}
	}
	
	/**
	 * 店铺删除
	 * @param request
	 * @param mulitId
	 * @return
	 * @throws Exception
	 */
	
	@SuppressWarnings("unused")
	@SecurityMapping(title = "店铺删除", value = "/store_del*", rtype = "admin", rname = "店铺管理", rcode = "admin_store_set", rgroup = "店铺")
	@RequestMapping({ "/store_del" })
	public String store_del(HttpServletRequest request, String mulitId)
			throws Exception {
		String[] ids = mulitId.split(",");

		for (String id : ids) {

			if (!id.equals("")) {
				Store store = this.storeService.selectByPrimaryKey(CommUtil.null2Long(id));
				Map<String, Object> params = Maps.newHashMap();
				
				if (store.getUser() != null) {
					User user = store.getUser();
					
					if (user != null) {
						List<Role> roles = user.getRoles();
						List<Role> new_roles = Lists.newArrayList();
						for (Role role : roles) {
							if (!role.getType().equals("SELLER")) {
								new_roles.add(role);
							}
						}
						if (user.getUserRole().indexOf("ADMIN") >= 0) {
							user.setUserRole("ADMIN");
						} else {
							user.setUserRole("BUYER");
						}
						user.setStore(null);
						user.getRoles().clear();
						//删除用户角色
						this.userService.deleteUserRole(user.getId(), user.getRoles());
						user.getRoles().addAll(new_roles);
						//添加新角色
						this.userService.saveUserRole(user.getId(), new_roles);
						user.setStore_apply_step(0);
						this.userService.updateById(user);
						for (User u : user.getChilds()) {
							roles = u.getRoles();
							List<Role> new_roles2 = Lists.newArrayList();
							
							for (Role role : roles) {
								if (!role.getType().equals("SELLER")) {
									new_roles2.add(role);
								}
							}
							if (u.getUserRole().indexOf("ADMIN") >= 0) {
								u.setUserRole("ADMIN");
							} else {
								u.setUserRole("BUYER");
							}
							u.setStore(null);
							u.getRoles().clear();
							//删除用户角色
							this.userService.deleteUserRole(u.getId(), u.getRoles());
							u.getRoles().addAll(new_roles2);
							//添加新角色
							this.userService.saveUserRole(u.getId(), new_roles2);
							u.setStore_apply_step(0);
							this.userService.updateById(u);
						}
					}
					params.clear();
					params.put("store_id", store.getId());
					List<Coupon> coupons = this.couponService.queryPageList(params);
					//先删除店铺优惠券的详情,再删除店铺优惠券
					for (Coupon coupon : coupons) {
						List<CouponInfo> cis = coupon.getCouponinfos();
						for (CouponInfo couponInfo : cis) {
							this.couponInfoService.deleteById(couponInfo.getId());
						}
						this.couponService.deleteById(coupon.getId());
					}
					//删除金币记录
					for (GoldRecord gr : user.getGold_record()) {
						this.grService.deleteById(gr.getId());
					}
					params.clear();
					params.put("gl_user_id", user.getId());
					List<GoldLog> gls = this.glService.queryPageList(params);
					//删除金币日志
					for (GoldLog gl : gls) {
						this.glService.deleteById(gl.getId());
					}
					//删除用户金币记录
					for (GoldRecord gr : user.getGold_record()) {
						this.grService.deleteById(gr.getId());
					}
					//先删除生活够信息在删除生活够
					for (GroupLifeGoods glg : user.getGrouplifegoods()) {
						for (GroupInfo gi : glg.getGroupInfos()) {
							this.groupinfoService.deleteById(gi.getId());
						}
						glg.getGroupInfos().removeAll(glg.getGroupInfos());
						
						this.grouplifegoodsService.deleteById(CommUtil.null2Long(glg.getId()));
					}

					for (PayoffLog log : user.getPaylogs()) {
						this.paylogService.deleteById(log.getId());
					}

					for (Album album : user.getAlbums()) {
						album.setAlbum_cover(null);
						this.albumService.updateById(album);
						params.clear();
						params.put("album_id", album.getId());
						List<Accessory> accs = this.accessoryService.queryPageList(params);
						
						for (Accessory acc : accs) {
							// CommUtil.del_acc(request, acc);
							this.accessoryService.delete(acc.getId());
						}
						this.albumService.deleteById(album.getId());
					}
				}
				for (Goods goods : store.getGoods_list()) {
					goods.setGoods_main_photo(null);
					goods.setGoods_brand(null);
					this.goodsService.update(goods);
					goods.getGoods_photos().clear();
					goods.getGoods_specs().clear();
					goods.getGoods_ugcs().clear();
				}
				List<Favorite> favs;
				for (Goods goods : store.getGoods_list()) {
					for (GoodsCart gc : goods.getCarts()) {
						this.goodsCartService.deleteById(gc.getId());
					}
					List<Evaluate> evaluates = goods.getEvaluates();
					for (Evaluate e : evaluates) {
						this.evaluateService.deleteById(e.getId());
					}

					for (ComplaintGoods cg : goods.getCgs()) {
						this.complaintGoodsService.deleteById(cg.getId());
					}
					params.clear();
					params.put("goods_id", goods.getId());
					favs = this.favoriteService.queryPageList(params);
					for (Favorite obj : favs) {
						this.favoriteService.deleteById(obj.getId());
					}
					goods.getCarts().removeAll(goods.getCarts());
					goods.getEvaluates().removeAll(goods.getEvaluates());
					goods.getCgs().removeAll(goods.getCgs());
					params.clear();
					params.put("zgl_goods_id", goods.getId());
					List<ZTCGoldLog> ztcgls = this.ztcglService
							.queryPageList(params);
					for (ZTCGoldLog ztc : ztcgls) {
						this.ztcglService.deleteById(ztc.getId());
					}
					this.goodsService.delete(goods.getId());

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
					lucene.delete_index(CommUtil.null2String(id));
				}
				store.getGoods_list().removeAll(store.getGoods_list());
				for (GoodsSpecification spec : store.getSpecs()) {
					for (GoodsSpecProperty pro : spec.getProperties()) {
						this.specpropertyService.delete(pro.getId());
					}
					spec.getProperties().removeAll(spec.getProperties());
				}
				String path = request.getSession().getServletContext()
						.getRealPath("/")
						+ this.configService.getSysConfig().getUploadFilePath()
						+ File.separator
						+ "store"
						+ File.separator
						+ store.getId();
//				CommUtil.deleteFolder(path);

				params.clear();
				params.put("redpig_store_id", CommUtil.null2Long(id));
				List<WaterMark> wm = this.waterMarkService.queryPageList(
						params, 0, 1);

				if (wm.size() > 0) {
					this.waterMarkService.deleteById(wm.get(0).getId());
				}

				params.clear();
				params.put("store_id", CommUtil.null2Long(id));
				favs = this.favoriteService.queryPageList(params);

				for (Favorite obj : favs) {
					this.favoriteService.deleteById(obj.getId());
				}
				this.storeService.deleteById(CommUtil.null2Long(id));
				send_site_msg(request, "msg_toseller_store_delete_notify",
						store);
			}
		}
		return "redirect:store_list";
	}
	
	/**
	 * 店铺AJAX更新
	 * @param request
	 * @param response
	 * @param id
	 * @param fieldName
	 * @param value
	 * @throws ClassNotFoundException
	 */
	@SecurityMapping(title = "店铺AJAX更新", value = "/store_ajax*", rtype = "admin", rname = "店铺管理", rcode = "admin_store_set", rgroup = "店铺")
	@RequestMapping({ "/store_ajax" })
	public void store_ajax(
			HttpServletRequest request,
			HttpServletResponse response, 
			String id, 
			String fieldName,
			String value)  {
		Store obj = this.storeService.selectByPrimaryKey(Long.parseLong(id));
		Field[] fields = Store.class.getDeclaredFields();

		Object val = ObjectUtils.setValue(fieldName, value, obj, fields);

		if (fieldName.equals("store_recommend")) {
			if (obj.getStore_recommend()) {
				obj.setStore_recommend_time(new Date());
			} else {
				obj.setStore_recommend_time(null);
			}
		}
		this.storeService.updateById(obj);
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		try {
			PrintWriter writer = response.getWriter();
			writer.print(val.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 商家服务ajax
	 * @param request
	 * @param response
	 * @param info
	 */
	@SecurityMapping(title="商家服务ajax", value="/price_ajax*", rtype="admin", rname="店铺管理", rcode="admin_price_ajax", rgroup="店铺")
	@RequestMapping({"/price_ajax"})
	public void price_ajax(
			HttpServletRequest request, 
			HttpServletResponse response, 
			String info){
		int price = 0;
		if ((info != null) && (!"".equals(info)))
		{
			String[] arr = info.split(",");

			for (String id:arr)
			{

				MerchantServices ms = this.merchantServices.selectByPrimaryKey(CommUtil.null2Long(id));
				int ms_price = ms.getServe_price();
				price = ms_price + price;
			}
		}
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		try
		{
			PrintWriter writer = response.getWriter();
			writer.print(price);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * 入驻管理
	 * @param request
	 * @param response
	 * @return
	 */
	@SecurityMapping(title = "入驻管理", value = "/store_base*", rtype = "admin", rname = "入驻管理", rcode = "admin_store_base", rgroup = "店铺")
	@RequestMapping({ "/store_base" })
	public ModelAndView store_base(
			HttpServletRequest request,
			HttpServletResponse response) {
		
		ModelAndView mv = new RedPigJModelAndView(
				"admin/blue/store_base_set.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		return mv;
	}
	
	/**
	 * 卖家信用保存
	 * @param request
	 * @param response
	 * @param id
	 * @param list_url
	 * @param store_allow
	 * @return
	 */
	@SecurityMapping(title = "卖家信用保存", value = "/store_set_save*", rtype = "admin", rname = "入驻管理", rcode = "admin_store_base", rgroup = "店铺")
	@RequestMapping({ "/store_set_save" })
	public ModelAndView store_set_save(
			HttpServletRequest request,
			HttpServletResponse response, 
			String id, 
			String list_url,
			String store_allow) {
		
		ModelAndView mv = new RedPigJModelAndView("admin/blue/success.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		SysConfig sc = this.configService.getSysConfig();
		sc.setStore_allow(CommUtil.null2Boolean(store_allow));
		if (id.equals("")) {
			this.configService.save(sc);
		} else {
			this.configService.update(sc);
		}
		mv.addObject("list_url", list_url);
		mv.addObject("op_title", "保存店铺设置成功");
		return mv;
	}
	
	/**
	 * 开店申请Ajax更新
	 * @param request
	 * @param response
	 * @param fieldName
	 * @throws ClassNotFoundException
	 */
	@SecurityMapping(title = "开店申请Ajax更新", value = "/store_base_ajax*", rtype = "admin", rname = "入驻管理", rcode = "admin_store_base", rgroup = "店铺")
	@RequestMapping({ "/store_base_ajax" })
	public void integral_goods_ajax(HttpServletRequest request,
			HttpServletResponse response, String fieldName)
			throws ClassNotFoundException {
		SysConfig sc = this.configService.getSysConfig();
		Field[] fields = SysConfig.class.getDeclaredFields();
		BeanWrapper wrapper = new BeanWrapper(sc);
		Object val = null;
		for (Field field:fields) {
			if (field.getName().equals(fieldName)) {
				val = Boolean.valueOf(!CommUtil.null2Boolean(wrapper.getPropertyValue(fieldName)));
				wrapper.setPropertyValue(fieldName, val);
			}
		}
		
		
		
		this.configService.update(sc);
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		try {
			PrintWriter writer = response.getWriter();
			writer.print(val.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 等级限制时可选的类
	 * @param request
	 * @param response
	 * @param storeGrade_id
	 * @param goodsClass_id
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@SecurityMapping(title = "等级限制时可选的类目", value = "/sg_limit_gc*", rtype = "admin", rname = "店铺管理", rcode = "admin_store_set", rgroup = "店铺")
	@RequestMapping({ "/sg_limit_gc" })
	public void storeGrade_limit_goodsClass(
			HttpServletRequest request,
			HttpServletResponse response, 
			String storeGrade_id,
			String goodsClass_id) {
		String jsonList = "";
		StoreGrade storeGrade = this.storeGradeService.selectByPrimaryKey(CommUtil.null2Long(storeGrade_id));
		
		if ((storeGrade != null) && (storeGrade.getMain_limit() != 0)) {
			GoodsClass goodsClass = this.goodsclassService.selectByPrimaryKey(CommUtil.null2Long(goodsClass_id));
			if (goodsClass != null) {
				List<Map<String, String>> gcList = Lists.newArrayList();
				for (GoodsClass gc : goodsClass.getChilds()) {
					Map map = Maps.newHashMap();
					map.put("gc_id", gc.getId());
					map.put("gc_name", gc.getClassName());
					gcList.add(map);
				}
				jsonList = JSON.toJSONString(gcList);
			}
		}
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		try {
			PrintWriter writer = response.getWriter();
			writer.print(jsonList);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 新增详细经营类目
	 * @param request
	 * @param response
	 * @param did
	 * @param gc_detail_info
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@SecurityMapping(title = "新增详细经营类目", value = "/add_gc_detail*", rtype = "admin", rname = "店铺管理", rcode = "admin_store_set", rgroup = "店铺")
	@RequestMapping({ "/add_gc_detail" })
	public ModelAndView addStore_GoodsClass_detail(
			HttpServletRequest request,
			HttpServletResponse response, 
			String did, 
			String gc_detail_info) {
		
		ModelAndView mv = new RedPigJModelAndView(
				"admin/blue/store_detailgc_ajax.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		
		GoodsClass gc = this.goodsclassService.selectByPrimaryKey(CommUtil
				.null2Long(did));
		List<Map> list = null;
		if (gc != null) {
			GoodsClass parent = gc.getParent();
			if ((gc_detail_info != null) && (!gc_detail_info.equals(""))) {
				String listJson;
				if (this.storeTools.query_MainGc_Map(parent.getId().toString(),
						gc_detail_info) == null) {
					list = JSON.parseArray(gc_detail_info, Map.class);
					List<Integer> gc_list = Lists.newArrayList();
					Map map = Maps.newHashMap();
					gc_list.add(Integer.valueOf(CommUtil.null2Int(did)));
					map.put("gc_list", gc_list);
					map.put("m_id", parent.getId());
					list.add(map);
					listJson = JSON.toJSONString(list);
					mv.addObject("gc_detail_info", listJson);
					mv.addObject("gcs",
							this.storeTools.query_store_DetailGc(listJson));
				} else {
					List<Map> oldList = JSON.parseArray(gc_detail_info,
							Map.class);
					list = Lists.newArrayList();
					for (Map map : oldList) {
						if (CommUtil.null2Long(map.get("m_id")).equals(
								parent.getId())) {
							List<Integer> gc_list = (List) map.get("gc_list");
							gc_list.add(Integer.valueOf(CommUtil.null2Int(did)));
							Map map2 = Maps.newHashMap();
							HashSet set = new HashSet(gc_list);
							gc_list = new ArrayList(set);
							map2.put("gc_list", gc_list);
							map2.put("m_id", parent.getId());
							list.add(map2);
						} else {
							list.add(map);
						}
					}
					listJson = JSON.toJSONString(list);
					mv.addObject("gc_detail_info", listJson);
					mv.addObject("gcs",
							this.storeTools.query_store_DetailGc(listJson));
				}
			} else {
				list = Lists.newArrayList();
				Map map = Maps.newHashMap();
				List<Integer> gc_list = Lists.newArrayList();
				gc_list.add(Integer.valueOf(CommUtil.null2Int(did)));
				map.put("gc_list", gc_list);
				map.put("m_id", parent.getId());
				list.add(map);
				String listJson = JSON.toJSONString(list);
				mv.addObject("gc_detail_info", listJson);
				mv.addObject("gcs",
						this.storeTools.query_store_DetailGc(listJson));
			}
		}
		return mv;
	}
	
	/**
	 * 删除详细经营类目
	 * @param request
	 * @param response
	 * @param did
	 * @param gc_detail_info
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@SecurityMapping(title = "删除详细经营类目", value = "/del_gc_detail*", rtype = "admin", rname = "店铺管理", rcode = "admin_store_set", rgroup = "店铺")
	@RequestMapping({ "/del_gc_detail" })
	public ModelAndView delStore_GoodsClass_detail(
			HttpServletRequest request,
			HttpServletResponse response, 
			String did, 
			String gc_detail_info) {
		
		ModelAndView mv = new RedPigJModelAndView(
				"admin/blue/store_detailgc_ajax.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		
		GoodsClass gc = this.goodsclassService.selectByPrimaryKey(CommUtil.null2Long(did));
		
		if ((gc_detail_info != null) && (!gc_detail_info.equals(""))
				&& (gc != null)) {
			GoodsClass parent = gc.getParent();
			List<Map> oldList = JSON.parseArray(gc_detail_info, Map.class);
			List<Map> list = Lists.newArrayList();
			for (Map oldMap : oldList) {
				if (!CommUtil.null2Long(oldMap.get("m_id")).equals(
						parent.getId())) {
					list.add(oldMap);
				} else {
					List<Integer> gc_list = (List) oldMap.get("gc_list");
					for (Integer integer : gc_list) {
						if (integer.equals(Integer.valueOf(CommUtil
								.null2Int(did)))) {
							gc_list.remove(integer);
							break;
						}
					}
					if (gc_list.size() > 0) {
						Map<String, Object> map = Maps.newHashMap();
						map.put("gc_list", gc_list);
						map.put("m_id", parent.getId());
						list.add(oldMap);
					}
				}
			}
			if (list.size() > 0) {
				String listJson = JSON.toJSONString(list);
				mv.addObject("gc_detail_info", listJson);
				mv.addObject("gcs",
						this.storeTools.query_store_DetailGc(listJson));
			}
		}
		return mv;
	}
}
