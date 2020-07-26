package com.redpigmall.view.web.action;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import com.redpigmall.api.tools.RedPigCommonUtil;
import com.redpigmall.api.tools.RedPigMaps;
import com.redpigmall.api.tools.WebForm;
import com.redpigmall.domain.Accessory;
import com.redpigmall.domain.Area;
import com.redpigmall.domain.GoodsClass;
import com.redpigmall.domain.MerchantServices;
import com.redpigmall.domain.Store;
import com.redpigmall.domain.StoreGrade;
import com.redpigmall.domain.StorePoint;
import com.redpigmall.domain.User;
import com.redpigmall.view.web.action.base.BaseAction;

/**
 * 
 * 
 * <p>
 * Title: RedPigSellerApplyAction.java
 * </p>
 * 
 * <p>
 * Description:商家入驻流程控制器，商城所有注册用户的都可以申请成为商家，需要完成相关申请流程，运营商审批通过后即可开通在线店铺入驻
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
 * @date 2014年4月25日
 * 
 * @version redpigmall_b2b2c v8.0 2016版
 */
@SuppressWarnings({ "unchecked", "rawtypes", "unused" })
@Controller
public class RedPigSellerApplyAction extends BaseAction{
	private static final boolean GoodsClass = false;
	
	/**
	 * 商家入驻申请
	 * @param request
	 * @param response
	 * @param op
	 * @param step
	 * @return
	 */
	@SecurityMapping(title = "商家入驻申请", value = "/seller_apply*", rtype = "buyer", rname = "商家入驻", rcode = "seller_apply", rgroup = "商家入驻")
	@RequestMapping({ "/seller_apply" })
	public ModelAndView seller_apply(HttpServletRequest request,
			HttpServletResponse response, String op, String step) {
		RedPigJModelAndView mv = null;
		
		if(SecurityUserHolder.getCurrentUser()==null){
			mv = new RedPigJModelAndView("login.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			
			mv.addObject("url", CommUtil.getURL(request) + "/index");
			return mv;
		}
		
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder
				.getCurrentUser().getId());
		
		boolean apply_flag = true;
		
		
		if (user.getUserRole().indexOf("ADMIN") >= 0) {
			mv = new RedPigJModelAndView("error.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "管理员不允许申请成为商家");
			mv.addObject("url", CommUtil.getURL(request) + "/index");
			return mv;
		}
		if (user.getStore_apply_step() > 7) {
			return seller_store_query(request, response);
		}
		if (!this.configService.getSysConfig().getStore_allow()) {
			mv = new RedPigJModelAndView("error.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "商城尚未开启商家入驻功能");
			mv.addObject("url", CommUtil.getURL(request) + "/index");
			return mv;
		}
		if ((CommUtil.null2Int(step) > 7) || (CommUtil.null2Int(step) < 0)) {
			step = CommUtil.null2String(Integer.valueOf(user
					.getStore_apply_step()));
		}
		if ((step != null) && (!CommUtil.null2String(step).equals(""))
				&& (user.getStore_apply_step() >= 0)) {
			if (CommUtil.null2Int(step) - user.getStore_apply_step() <= 1) {
				user.setStore_apply_step(CommUtil.null2Int(step));
				this.userService.updateById(user);
			}
		}
		if ((CommUtil.null2String(op).equals("back"))
				&& (user.getStore_apply_step() - CommUtil.null2Int(step) >= 1)) {
			if (CommUtil.null2Int(step) == 0) {
				return seller_apply_step0(request, response);
			}
			if (CommUtil.null2Int(step) == 1) {
				return seller_apply_step1(request, response);
			}
			if (CommUtil.null2Int(step) == 2) {
				return seller_apply_step2(request, response);
			}
			if (CommUtil.null2Int(step) == 3) {
				return seller_apply_step3(request, response);
			}
			if (CommUtil.null2Int(step) == 4) {
				return seller_apply_step4(request, response);
			}
			if (CommUtil.null2Int(step) == 5) {
				return seller_apply_step5(request, response);
			}
			if (CommUtil.null2Int(step) == 6) {
				return seller_apply_step6(request, response);
			}
			if (CommUtil.null2Int(step) == 7) {
				return seller_apply_step7(request, response);
			}
		} else {
			if (user.getStore_apply_step() == 0) {
				return seller_apply_step0(request, response);
			}
			if (user.getStore_apply_step() == 1) {
				return seller_apply_step1(request, response);
			}
			if (user.getStore_apply_step() == 2) {
				return seller_apply_step2(request, response);
			}
			if (user.getStore_apply_step() == 3) {
				return seller_apply_step3(request, response);
			}
			if (user.getStore_apply_step() == 4) {
				return seller_apply_step4(request, response);
			}
			if (user.getStore_apply_step() == 5) {
				return seller_apply_step5(request, response);
			}
			if (user.getStore_apply_step() == 6) {
				return seller_apply_step6(request, response);
			}
			if (user.getStore_apply_step() == 7) {
				return seller_apply_step7(request, response);
			}
		}
		return mv;
	}
	/**
	 * 商家协议确定
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	private ModelAndView seller_apply_step0(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new RedPigJModelAndView(
				"seller_apply/seller_apply_step0.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		mv.addObject("doc", this.documentService.getObjByProperty("mark","=", "apply_agreement"));
		return mv;
	}
	/**
	 * 商家入驻须知
	 * 
	 * @param request
	 * @param response
	 * @param id
	 * @param currentPage
	 * @return
	 */
	private ModelAndView seller_apply_step1(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new RedPigJModelAndView(
				"seller_apply/seller_apply_step1.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		mv.addObject("doc", this.documentService.getObjByProperty("mark","=", "apply_notice"));
		return mv;
	}
	/**
	 * 商家申请页面 商家由买家注册后申请
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	private ModelAndView seller_apply_step2(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new RedPigJModelAndView(
				"seller_apply/seller_apply_step2.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder
				.getCurrentUser().getId());
		Store store = user.getStore();
		if (store == null) {
			store = new Store();
			store.setAddTime(new Date());
			if (store.getStore_credit() == null)
				store.setStore_credit(0);

			this.storeService.saveEntity(store);
			StorePoint sp = new StorePoint();
			sp.setAddTime(new Date());
			sp.setStore(store);
			sp.setDescription_evaluate(BigDecimal.valueOf(0L));
			sp.setService_evaluate(BigDecimal.valueOf(0L));
			sp.setShip_evaluate(BigDecimal.valueOf(0L));
			this.storePointService.saveEntity(sp);
			user.setStore(store);
			this.userService.updateById(user);
		}
		mv.addObject("user", user);
		mv.addObject("store", store);
		return mv;
	}
	
	/**
	 * 入驻联系方式保存
	 * @param request
	 * @param response
	 * @param trueName
	 * @param telephone
	 * @param email
	 * @return
	 */
	@SecurityMapping(title = "入驻联系方式保存", value = "/seller_apply_step2_save*", rtype = "buyer", rname = "商家入驻", rcode = "seller_apply", rgroup = "商家入驻")
	@RequestMapping({ "/seller_apply_step2_save" })
	public String seller_apply_step2_saveEntity(HttpServletRequest request,
			HttpServletResponse response, String trueName, String telephone,
			String email) {
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder
				.getCurrentUser().getId());
		if ((!CommUtil.null2String(trueName).equals(""))
				&& (!CommUtil.null2String(telephone).equals(""))
				&& (!CommUtil.null2String(email).equals(""))) {
			user.setTrueName(trueName);
			user.setTelephone(telephone);
			user.setEmail(email);
			this.userService.updateById(user);
		}
		return "redirect:seller_apply?step=3";
	}
	/**
	 * 完善公司信息
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	private ModelAndView seller_apply_step3(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new RedPigJModelAndView(
				"seller_apply/seller_apply_step3.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder
				.getCurrentUser().getId());
		if (CommUtil.null2Int(user.getStore().getStore_status()) == 6) {
			Store store = user.getStore();
			store.setStore_status(0);
			this.storeService.updateById(store);
		}
		
		List<Area> areas = this.areaService.queryPageList(RedPigMaps.newParent(-1));
		
		mv.addObject("areas", areas);
		mv.addObject("store", user.getStore());
		mv.addObject("jsessionid", request.getSession().getId());
		return mv;
	}
	
	/**
	 * 入驻公司信息保存
	 * @param request
	 * @param response
	 * @param lid2
	 * @param cid2
	 * @param step
	 * @return
	 */
	@SecurityMapping(title = "入驻公司信息保存", value = "/seller_apply_step3_save*", rtype = "buyer", rname = "商家入驻", rcode = "seller_apply", rgroup = "商家入驻")
	@RequestMapping({ "/seller_apply_step3_save" })
	public String seller_apply_step3_saveEntity(HttpServletRequest request,
			HttpServletResponse response, String lid2, String cid2, String step) {
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder
				.getCurrentUser().getId());
		user.setStore_apply_step(CommUtil.null2Int(step));
		this.userService.updateById(user);
		WebForm wf = new WebForm();
		Store store = null;
		Store obj = user.getStore();
		store = (Store) WebForm.toPo(request, obj);
		store.setLicense_area(this.areaService.selectByPrimaryKey(CommUtil
				.null2Long(lid2)));
		store.setLicense_c_area(this.areaService.selectByPrimaryKey(CommUtil
				.null2Long(cid2)));
		this.storeService.updateById(store);
		return "redirect:seller_apply";
	}
	/**
	 * 完善税务以及银行信息
	 * 
	 * @param request
	 * @param response
	 * @param id
	 * @param currentPage
	 * @return
	 */
	private ModelAndView seller_apply_step4(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new RedPigJModelAndView(
				"seller_apply/seller_apply_step4.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder
				.getCurrentUser().getId());
		
		List<Area> areas = this.areaService.queryPageList(RedPigMaps.newParent(-1));
		
		mv.addObject("areas", areas);
		mv.addObject("store", user.getStore());
		mv.addObject("jsessionid", request.getSession().getId());
		return mv;
	}
	
	/**
	 * 入驻财务信息保存
	 * @param request
	 * @param response
	 * @param bid2
	 * @param step
	 * @param code_type
	 * @return
	 */
	@SecurityMapping(title = "入驻财务信息保存", value = "/seller_apply_step4_save*", rtype = "buyer", rname = "商家入驻", rcode = "seller_apply", rgroup = "商家入驻")
	@RequestMapping({ "/seller_apply_step4_save" })
	public String seller_apply_step4_saveEntity(HttpServletRequest request,
			HttpServletResponse response, String bid2, String step,
			String code_type) {
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder
				.getCurrentUser().getId());
		user.setStore_apply_step(CommUtil.null2Int(step));
		this.userService.updateById(user);
		WebForm wf = new WebForm();
		Store store = null;
		Store obj = user.getStore();
		store = (Store) WebForm.toPo(request, obj);
		store.setBank_area(this.areaService.selectByPrimaryKey(CommUtil.null2Long(bid2)));
		store.setCode_type(CommUtil.null2Int(code_type));
		this.storeService.updateById(store);
		return "redirect:seller_apply";
	}

	/**
	 * 完善店铺信息
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	private ModelAndView seller_apply_step5(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new RedPigJModelAndView(
				"seller_apply/seller_apply_step5.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder
				.getCurrentUser().getId());
		if (CommUtil.null2Int(user.getStore().getStore_status()) == 11) {
			Store store = user.getStore();
			store.setStore_status(0);
			this.storeService.updateById(store);
		}
		
		Map<String,Object> params = RedPigMaps.newParent(-1);
		List<Area> areas = this.areaService.queryPageList(params);
		
		params.clear();
		List<StoreGrade> storeGrades = this.storegradeService.queryPageList(params);
		params = RedPigMaps.newParent(-1);
		
		List<GoodsClass> goodsClass = this.goodsClassService.queryPageList(params);
		params.clear();
		
		List<MerchantServices> ms_list = this.merchantService.queryPageList(params);
		
		mv.addObject("ms_list", ms_list);
		mv.addObject("goodsClass", goodsClass);
		mv.addObject("storeGrades", storeGrades);
		mv.addObject("areas", areas);
		mv.addObject("store", user.getStore());
		return mv;
	}
	
	/**
	 * 入驻店铺信息保存
	 * @param request
	 * @param response
	 * @param aid2
	 * @param step
	 * @param storeGrades
	 * @param gc_main_id
	 * @param validity
	 * @param serve_id
	 * @return
	 */
	@SecurityMapping(title = "入驻店铺信息保存", value = "/seller_apply_step5_save*", rtype = "buyer", rname = "商家入驻", rcode = "seller_apply", rgroup = "商家入驻")
	@RequestMapping({ "/seller_apply_step5_save" })
	public String seller_apply_step5_saveEntity(HttpServletRequest request,
			HttpServletResponse response, String aid2, String step,
			String storeGrades, String gc_main_id, String validity,
			String serve_id) {
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder
				.getCurrentUser().getId());
		user.setStore_apply_step(CommUtil.null2Int(step));
		this.userService.updateById(user);
		Store obj = user.getStore();
		WebForm wf = new WebForm();
		Store store = null;
		store = (Store) WebForm.toPo(request, obj);
		if ((aid2 != null) && (!"".equals(aid2))) {
			store.setArea(this.areaService.selectByPrimaryKey(CommUtil.null2Long(aid2)));
		}
		store.setGrade(this.storegradeService.selectByPrimaryKey(CommUtil
				.null2Long(storeGrades)));
		store.setGc_main_id(CommUtil.null2Long(gc_main_id));
		store.setGc_detail_info(null);
		store.setValidity(CommUtil.formatDate(validity));
		if ((serve_id != null) && (!"".equals(serve_id))) {
			List list = Lists.newArrayList();
			String[] arr = serve_id.split(",");
			for (int i = 0; i < arr.length; i++) {
				String s_id = arr[i];
				Map m = Maps.newHashMap();
				MerchantServices ms = this.merchantService.selectByPrimaryKey(CommUtil
						.null2Long(s_id));
				m.put("id", ms.getId());
				m.put("name", ms.getServe_name());
				m.put("price", Integer.valueOf(ms.getServe_price()));
				if (ms.getService_img() != null) {
					m.put("img", ms.getService_img().getPath() + "/"
							+ ms.getService_img().getName());
				}
				list.add(i, m);
			}
			store.setStore_service_info(JSON.toJSONString(list));
		}
		this.storeService.updateById(store);
		return "redirect:seller_apply";
	}
	/**
	 * 店铺经营类目
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	private ModelAndView seller_apply_step6(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new RedPigJModelAndView(
				"seller_apply/seller_apply_step6.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder
				.getCurrentUser().getId());
		Store user_store = user.getStore();
		if (user_store.getGrade().getMain_limit() == 1) {
			GoodsClass gc = this.goodsClassService.selectByPrimaryKey(user_store
					.getGc_main_id());
			mv.addObject("gc", gc);
		} else {
			
			List<GoodsClass> goodsClass = this.goodsClassService.queryPageList(RedPigMaps.newParent(-1));
			
			mv.addObject("goodsClass", goodsClass);
			mv.addObject("gc", goodsClass.get(0));
		}
		mv.addObject("store", user_store);
		Set<GoodsClass> all_details_gcs = this.storeTools
				.query_store_DetailGc(user_store.getGc_detail_info());
		mv.addObject("details_gcs", all_details_gcs);
		return mv;
	}
	
	/**
	 * 入驻店铺类型保存
	 * @param request
	 * @param response
	 * @param goodsClass_main
	 * @param step
	 * @return
	 */
	@SecurityMapping(title = "入驻店铺类型保存", value = "/seller_apply_step6_save*", rtype = "buyer", rname = "商家入驻", rcode = "seller_apply", rgroup = "商家入驻")
	@RequestMapping({ "/seller_apply_step6_save" })
	public String seller_apply_step6_saveEntity(HttpServletRequest request,
			HttpServletResponse response, String goodsClass_main, String step) {
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder
				.getCurrentUser().getId());
		user.setStore_apply_step(CommUtil.null2Int(step));
		this.userService.updateById(user);
		Store store = user.getStore();
		this.storeService.updateById(store);
		return "redirect:seller_apply";
	}
	/**
	 * 确认入驻协议
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	private ModelAndView seller_apply_step7(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new RedPigJModelAndView(
				"seller_apply/seller_apply_step7.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		mv.addObject("doc", this.documentService.getObjByProperty("mark","=", "apply_agreement"));
		return mv;
	}
	
	/**
	 * 入驻佣金
	 * @param request
	 * @param response
	 * @return
	 */
	@SecurityMapping(title = "入驻佣金", value = "/seller_apply_money*", rtype = "buyer", rname = "商家入驻", rcode = "seller_apply", rgroup = "商家入驻")
	@RequestMapping({ "/seller_apply_money" })
	private ModelAndView seller_apply_money(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new RedPigJModelAndView(
				"seller_apply/seller_apply_money.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		
		List<GoodsClass> goodsClass = this.goodsClassService.queryPageList(RedPigMaps.newParent(-1));
		
		mv.addObject("goodsClass", goodsClass);
		return mv;
	}
	
	/**
	 * 入驻申请提交
	 * @param request
	 * @param response
	 * @return
	 */
	@SecurityMapping(title = "入驻申请提交", value = "/seller_store_wait*", rtype = "buyer", rname = "商家入驻", rcode = "seller_apply", rgroup = "商家入驻")
	@RequestMapping({ "/seller_store_wait" })
	public ModelAndView seller_store_wait(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new RedPigJModelAndView(
				"seller_apply/seller_apply_wait.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder
				.getCurrentUser().getId());
		if (user.getStore() != null
				&& CommUtil.null2Int(user.getStore().getStore_status()) > 0) {
			return seller_store_query(request, response);
		}
		Store store = user.getStore();
		store.setStore_status(5);
		user.setStore_apply_step(8);
		this.userService.updateById(user);
		this.storeService.updateById(store);
		return mv;
	}
	
	/**
	 * 入驻进度查询
	 * @param request
	 * @param response
	 * @return
	 */
	@SecurityMapping(title = "入驻进度查询", value = "/seller_store_query*", rtype = "buyer", rname = "商家入驻", rcode = "seller_apply", rgroup = "商家入驻")
	@RequestMapping({ "/seller_store_query" })
	public ModelAndView seller_store_query(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = null;
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder
				.getCurrentUser().getId());
		Store store = user.getStore();
		if ((CommUtil.null2Int(store.getStore_status()) == 5)
				|| (store.getStore_status() == 6)) {
			mv = new RedPigJModelAndView("seller_apply/seller_apply_wait1.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
		}
		if ((store.getStore_status() == 10) || (store.getStore_status() == 11)) {
			mv = new RedPigJModelAndView("seller_apply/seller_apply_wait2.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
		}
		if ((store.getStore_status() == 15) || (store.getStore_status() == 20)
				|| (store.getStore_status() == 25)) {
			mv = new RedPigJModelAndView("seller_apply/seller_apply_wait3.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
		}
		mv.addObject("store", store);
		return mv;
	}
	
	/**
	 * 重新申请入驻
	 * @param request
	 * @param response
	 * @param step
	 * @return
	 */
	@SecurityMapping(title = "重新申请入驻", value = "/seller_store_rewrite*", rtype = "buyer", rname = "商家入驻", rcode = "seller_apply", rgroup = "商家入驻")
	@RequestMapping({ "/seller_store_rewrite" })
	public ModelAndView seller_store_rewrite(HttpServletRequest request,
			HttpServletResponse response, String step) {
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder
				.getCurrentUser().getId());
		if (step.equals("3")) {
			user.setStore_apply_step(3);
		}
		if (step.equals("5")) {
			user.setStore_apply_step(5);
		}
		this.userService.updateById(user);
		return seller_apply(request, response, null, step);
	}
	
	/**
	 * 商品图片上传
	 * @param request
	 * @param response
	 * @param mark
	 * @param uid
	 * @param jsessionid
	 */
	@SecurityMapping(title = "商品图片上传", value = "/seller_apply_image_save*", rtype = "buyer", rname = "商品发布", rcode = "seller_apply", rgroup = "商家入驻")
	@RequestMapping({ "/seller_apply_image_save" })
	public void seller_apply_image_saveEntity(HttpServletRequest request,
			HttpServletResponse response, String mark, String uid,
			String jsessionid) {
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder
				.getCurrentUser().getId());
		Store store = user.getStore();

		String uploadFilePath = this.configService.getSysConfig()
				.getUploadFilePath();
		String saveFilePathName = request.getSession().getServletContext()
				.getRealPath("/")
				+ uploadFilePath
				+ File.separator
				+ "store"
				+ File.separator
				+ store.getId();
		if (!CommUtil.fileExist(saveFilePathName)) {
			CommUtil.createFolder(saveFilePathName);
		}
		Map<String, Object> map = Maps.newHashMap();
		Map json = Maps.newHashMap();
		if ("idCard".equals(mark)) {
			if (store.getLicense_legal_idCard_image() != null) {
				Long aid = store.getLicense_legal_idCard_image().getId();
				store.setLicense_legal_idCard_image(null);
				this.storeService.updateById(store);
				Accessory img = this.accessoryService.selectByPrimaryKey(CommUtil
						.null2Long(aid));
				this.accessoryService.deleteById(img.getId());
				boolean ret = true;
				
				if (ret) {
					RedPigCommonUtil.del_acc(request, img);
				}
			}
			try {
				map = CommUtil.saveFileToServer(request, mark + "_img_a",
						saveFilePathName, "", null);
				if (map.get("fileName") != "") {
					Accessory photo = new Accessory();
					photo.setName((String) map.get("fileName"));
					photo.setExt("." + (String) map.get("mime"));
					photo.setSize(BigDecimal.valueOf(CommUtil.null2Double(map
							.get("fileSize"))));
					photo.setPath(uploadFilePath + "/store/" + store.getId());
					photo.setAddTime(new Date());
					this.accessoryService.saveEntity(photo);
					store.setLicense_legal_idCard_image(photo);
					this.storeService.updateById(store);
					json.put("url", photo.getPath() + "/" + photo.getName());
					json.put("id", photo.getId());
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if ("license".equals(mark)) {
			if (store.getLicense_image() != null) {
				Long aid = store.getLicense_image().getId();
				store.setLicense_image(null);
				this.storeService.updateById(store);
				Accessory img = this.accessoryService.selectByPrimaryKey(CommUtil
						.null2Long(aid));
				this.accessoryService.deleteById(img.getId());
				boolean ret = true;
				
				if (ret) {
					RedPigCommonUtil.del_acc(request, img);
				}
			}
			try {
				map = CommUtil.saveFileToServer(request, mark + "_img_a",
						saveFilePathName, "", null);
				if (map.get("fileName") != "") {
					Accessory photo = new Accessory();
					photo.setName((String) map.get("fileName"));
					photo.setExt("." + (String) map.get("mime"));
					photo.setSize(BigDecimal.valueOf(CommUtil.null2Double(map
							.get("fileSize"))));
					photo.setPath(uploadFilePath + "/store/" + store.getId());
					photo.setAddTime(new Date());
					this.accessoryService.saveEntity(photo);
					store.setLicense_image(photo);
					this.storeService.updateById(store);
					json.put("url", photo.getPath() + "/" + photo.getName());
					json.put("id", photo.getId());
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if ("organization".equals(mark)) {
			if (store.getOrganization_image() != null) {
				Long aid = store.getOrganization_image().getId();
				store.setOrganization_image(null);
				this.storeService.updateById(store);
				Accessory img = this.accessoryService.selectByPrimaryKey(CommUtil
						.null2Long(aid));
				this.accessoryService.deleteById(img.getId());
				boolean ret = true;
				
				if (ret) {
					RedPigCommonUtil.del_acc(request, img);
				}
			}
			try {
				map = CommUtil.saveFileToServer(request, mark + "_img_a",
						saveFilePathName, "", null);
				if (map.get("fileName") != "") {
					Accessory photo = new Accessory();
					photo.setName((String) map.get("fileName"));
					photo.setExt("." + (String) map.get("mime"));
					photo.setSize(BigDecimal.valueOf(CommUtil.null2Double(map
							.get("fileSize"))));
					photo.setPath(uploadFilePath + "/store/" + store.getId());
					photo.setAddTime(new Date());
					this.accessoryService.saveEntity(photo);
					store.setOrganization_image(photo);
					this.storeService.updateById(store);
					json.put("url", photo.getPath() + "/" + photo.getName());
					json.put("id", photo.getId());
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if ("tax_reg".equals(mark)) {
			if (store.getTax_reg_card() != null) {
				Long aid = store.getTax_reg_card().getId();
				store.setTax_reg_card(null);
				this.storeService.updateById(store);
				Accessory img = this.accessoryService.selectByPrimaryKey(CommUtil
						.null2Long(aid));
				this.accessoryService.deleteById(img.getId());
				RedPigCommonUtil.del_acc(request, img);
			}
			try {
				map = CommUtil.saveFileToServer(request, mark + "_img_a",
						saveFilePathName, "", null);
				if (map.get("fileName") != "") {
					Accessory photo = new Accessory();
					photo.setName((String) map.get("fileName"));
					photo.setExt("." + (String) map.get("mime"));
					photo.setSize(BigDecimal.valueOf(CommUtil.null2Double(map
							.get("fileSize"))));
					photo.setPath(uploadFilePath + "/store/" + store.getId());
					photo.setAddTime(new Date());
					this.accessoryService.saveEntity(photo);
					store.setTax_reg_card(photo);
					this.storeService.updateById(store);
					json.put("url", photo.getPath() + "/" + photo.getName());
					json.put("id", photo.getId());
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if ("tax_general".equals(mark)) {
			if (store.getTax_general_card() != null) {
				Long aid = store.getTax_general_card().getId();
				store.setTax_general_card(null);
				this.storeService.updateById(store);
				Accessory img = this.accessoryService.selectByPrimaryKey(CommUtil
						.null2Long(aid));
				this.accessoryService.deleteById(img.getId());
				RedPigCommonUtil.del_acc(request, img);
			}
			try {
				map = CommUtil.saveFileToServer(request, mark + "_img_a",
						saveFilePathName, "", null);
				if (map.get("fileName") != "") {
					Accessory photo = new Accessory();
					photo.setName((String) map.get("fileName"));
					photo.setExt("." + (String) map.get("mime"));
					photo.setSize(BigDecimal.valueOf(CommUtil.null2Double(map
							.get("fileSize"))));
					photo.setPath(uploadFilePath + "/store/" + store.getId());
					photo.setAddTime(new Date());
					this.accessoryService.saveEntity(photo);
					store.setTax_general_card(photo);
					this.storeService.updateById(store);
					json.put("url", photo.getPath() + "/" + photo.getName());
					json.put("id", photo.getId());
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if ("bank_permit".equals(mark)) {
			if (store.getBank_permit_image() != null) {
				Long aid = store.getBank_permit_image().getId();
				store.setBank_permit_image(null);
				this.storeService.updateById(store);
				Accessory img = this.accessoryService.selectByPrimaryKey(CommUtil
						.null2Long(aid));
				this.accessoryService.deleteById(img.getId());
				RedPigCommonUtil.del_acc(request, img);
			}
			try {
				map = CommUtil.saveFileToServer(request, mark + "_img_a",
						saveFilePathName, "", null);
				if (map.get("fileName") != "") {
					Accessory photo = new Accessory();
					photo.setName((String) map.get("fileName"));
					photo.setExt("." + (String) map.get("mime"));
					photo.setSize(BigDecimal.valueOf(CommUtil.null2Double(map
							.get("fileSize"))));
					photo.setPath(uploadFilePath + "/store/" + store.getId());
					photo.setAddTime(new Date());
					this.accessoryService.saveEntity(photo);
					store.setBank_permit_image(photo);
					this.storeService.updateById(store);
					json.put("url", photo.getPath() + "/" + photo.getName());
					json.put("id", photo.getId());
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		String ret = JSON.toJSONString(json);
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
	 * 商家入驻照片删除
	 * @param request
	 * @param response
	 * @param mark
	 * @param uid
	 */
	@SecurityMapping(title = "商家入驻照片删除", value = "/seller_apply_image_del*", rtype = "buyer", rname = "商家入驻", rcode = "seller_apply", rgroup = "商家入驻")
	@RequestMapping({ "/seller_apply_image_del" })
	public void seller_apply_image_del(HttpServletRequest request,
			HttpServletResponse response, String mark, String uid) {
		User user = this.userService.selectByPrimaryKey(CommUtil.null2Long(uid));
		Store store = user.getStore();
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");

		Map json = Maps.newHashMap();
		Long aid = null;
		if ("idCard".equals(mark)) {
			aid = store.getLicense_legal_idCard_image().getId();
			store.setLicense_legal_idCard_image(null);
			this.storeService.updateById(store);
			Accessory img = this.accessoryService.selectByPrimaryKey(CommUtil
					.null2Long(aid));
			try {
				this.accessoryService.deleteById(img.getId());
				RedPigCommonUtil.del_acc(request, img);
				json.put("result", Boolean.valueOf(true));
				PrintWriter writer = response.getWriter();
				writer.print(JSON.toJSONString(json));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if ("license".equals(mark)) {
			aid = store.getLicense_image().getId();
			store.setLicense_image(null);
			this.storeService.updateById(store);
			Accessory img = this.accessoryService.selectByPrimaryKey(CommUtil
					.null2Long(aid));
			try {
				this.accessoryService.deleteById(img.getId());
				RedPigCommonUtil.del_acc(request, img);
				json.put("result", Boolean.valueOf(true));
				PrintWriter writer = response.getWriter();
				writer.print(JSON.toJSONString(json));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if ("organization".equals(mark)) {
			aid = store.getOrganization_image().getId();
			store.setOrganization_image(null);
			this.storeService.updateById(store);
			Accessory img = this.accessoryService.selectByPrimaryKey(CommUtil
					.null2Long(aid));
			try {
				this.accessoryService.deleteById(img.getId());
				RedPigCommonUtil.del_acc(request, img);
				json.put("result", Boolean.valueOf(true));
				PrintWriter writer = response.getWriter();
				writer.print(JSON.toJSONString(json));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if ("tax_reg".equals(mark)) {
			aid = store.getTax_reg_card().getId();
			store.setTax_reg_card(null);
			this.storeService.updateById(store);
			Accessory img = this.accessoryService.selectByPrimaryKey(CommUtil
					.null2Long(aid));
			try {
				this.accessoryService.deleteById(img.getId());
				RedPigCommonUtil.del_acc(request, img);
				json.put("result", Boolean.valueOf(true));
				PrintWriter writer = response.getWriter();
				writer.print(JSON.toJSONString(json));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if ("tax_general".equals(mark)) {
			aid = store.getTax_general_card().getId();
			store.setTax_general_card(null);
			this.storeService.updateById(store);
			Accessory img = this.accessoryService.selectByPrimaryKey(CommUtil
					.null2Long(aid));
			try {
				this.accessoryService.deleteById(img.getId());
				RedPigCommonUtil.del_acc(request, img);
				json.put("result", Boolean.valueOf(true));
				PrintWriter writer = response.getWriter();
				writer.print(JSON.toJSONString(json));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if ("bank_permit".equals(mark)) {
			aid = store.getBank_permit_image().getId();
			store.setBank_permit_image(null);
			this.storeService.updateById(store);
			Accessory img = this.accessoryService.selectByPrimaryKey(CommUtil
					.null2Long(aid));
			try {
				this.accessoryService.deleteById(img.getId());
				RedPigCommonUtil.del_acc(request, img);
				json.put("result", Boolean.valueOf(true));
				PrintWriter writer = response.getWriter();
				writer.print(JSON.toJSONString(json));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 所在地址下拉
	 * @param request
	 * @param response
	 * @param value
	 * @param type
	 * @return
	 */
	@SecurityMapping(title = "所在地址下拉", value = "/seller_license_area*", rtype = "buyer", rname = "商家入驻", rcode = "seller_apply", rgroup = "商家入驻")
	@RequestMapping({ "/seller_license_area" })
	public ModelAndView seller_license_area_ajax(HttpServletRequest request,
			HttpServletResponse response, String value, String type) {
		ModelAndView mv = new RedPigJModelAndView(
				"seller_apply/seller_store_ajax_select.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		Map<String, Object> params = Maps.newHashMap();
		params.put("id", value);
		Area area = this.areaService.selectByPrimaryKey(CommUtil.null2Long(value));
		if ("l".equals(type)) {
			if (area.getLevel() == 0) {
				mv.addObject("level", "城市");
				mv.addObject("mark", "lid1");
				mv.addObject("area", area);
			} else if (area.getLevel() == 1) {
				mv.addObject("level", "州县");
				mv.addObject("mark", "lid2");
				mv.addObject("area", area);
			}
		} else if ("c".equals(type)) {
			if (area.getLevel() == 0) {
				mv.addObject("level", "城市");
				mv.addObject("mark", "cid1");
				mv.addObject("area", area);
			} else if (area.getLevel() == 1) {
				mv.addObject("level", "州县");
				mv.addObject("mark", "cid2");
				mv.addObject("area", area);
			}
		} else if ("b".equals(type)) {
			if (area.getLevel() == 0) {
				mv.addObject("level", "城市");
				mv.addObject("mark", "bid1");
				mv.addObject("area", area);
			} else if (area.getLevel() == 1) {
				mv.addObject("level", "州县");
				mv.addObject("mark", "bid2");
				mv.addObject("area", area);
			}
		} else if ("a".equals(type)) {
			if (area.getLevel() == 0) {
				mv.addObject("level", "城市");
				mv.addObject("mark", "aid1");
				mv.addObject("area", area);
			} else if (area.getLevel() == 1) {
				mv.addObject("level", "州县");
				mv.addObject("mark", "aid2");
				mv.addObject("area", area);
			}
		}
		return mv;
	}
	
	/**
	 * 店铺经营类目
	 * @param request
	 * @param response
	 * @param cid
	 * @param uid
	 * @return
	 */
	@SecurityMapping(title = "店铺经营类目", value = "/seller_goodsclass_ajax*", rtype = "buyer", rname = "商家入驻", rcode = "seller_apply", rgroup = "商家入驻")
	@RequestMapping({ "/seller_goodsclass_ajax" })
	public ModelAndView seller_goodsclass_ajax(HttpServletRequest request,
			HttpServletResponse response, String cid, String uid) {
		ModelAndView mv = new RedPigJModelAndView(
				"seller_apply/seller_store_ajax_goodsClass.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		User user = this.userService.selectByPrimaryKey(CommUtil.null2Long(uid));
		Store user_store = user.getStore();
		GoodsClass goodsClass = this.goodsClassService.selectByPrimaryKey(CommUtil
				.null2Long(cid));
		Set<GoodsClass> gcs = Sets.newTreeSet();
		Set<GoodsClass> gcs1 = Sets.newTreeSet();
		for (GoodsClass gc : goodsClass.getChilds()) {
			gcs.add(gc);
			gcs1.add(gc);
		}
		for (GoodsClass gc : gcs1) {

			for (GoodsClass ugc : this.storeTools
					.query_store_DetailGc(user_store.getGc_detail_info())) {
				if (ugc.getId().equals(gc.getId())) {
					gcs.remove(ugc);
					break;
				}
			}
		}
		mv.addObject("gcs", gcs);
		return mv;
	}
	
	/**
	 * 店铺经营详细类目保存
	 * @param request
	 * @param response
	 * @param gc_ids
	 * @param uid
	 * @return
	 */
	@SecurityMapping(title = "店铺经营详细类目保存", value = "/seller_goodsclass_save*", rtype = "buyer", rname = "商家入驻", rcode = "seller_apply", rgroup = "商家入驻")
	@RequestMapping({ "/seller_goodsclass_save" })
	public ModelAndView seller_goodsclass_saveEntity(HttpServletRequest request,
			HttpServletResponse response, String gc_ids, String uid) {
		ModelAndView mv = new RedPigJModelAndView(
				"seller_apply/seller_store_gc_list.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		User user = this.userService.selectByPrimaryKey(CommUtil.null2Long(uid));
		Store store = user.getStore();
		String[] str = gc_ids.split(",");
		List<Map> list = Lists.newArrayList();
		Map<String, Object> map = Maps.newHashMap();
		List gc_list = Lists.newArrayList();
		GoodsClass parent = null;
		for (int i = 1; i < str.length; i++) {
			GoodsClass gc = this.goodsClassService.selectByPrimaryKey(CommUtil
					.null2Long(str[i]));
			parent = gc.getParent();
		}
		Map map_temp = this.storeTools
				.query_MainGc_Map(CommUtil.null2String(parent.getId()),
						store.getGc_detail_info());
		if (map_temp != null) {
			gc_list = (List) map_temp.get("gc_list");
			List<GoodsClass> gc_mains = this.storeTools
					.query_store_detail_MainGc(store.getGc_detail_info());
			for (GoodsClass gc_main : gc_mains) {
				if (!gc_main.getId().equals(parent.getId())) {
					Map map_main = this.storeTools.query_MainGc_Map(
							CommUtil.null2String(gc_main.getId()),
							store.getGc_detail_info());
					list.add(map_main);
				}
			}
		}
		if (map_temp == null) {
			List<GoodsClass> gc_mains = this.storeTools
					.query_store_detail_MainGc(store.getGc_detail_info());
			for (GoodsClass gc_main : gc_mains) {
				Map map_main = this.storeTools.query_MainGc_Map(
						CommUtil.null2String(gc_main.getId()),
						store.getGc_detail_info());
				list.add(map_main);
			}
		}
		for (int i = 1; i < str.length; i++) {
			GoodsClass gc = this.goodsClassService.selectByPrimaryKey(CommUtil
					.null2Long(str[i]));
			Set<GoodsClass> detail_info = this.storeTools
					.query_store_DetailGc(store.getGc_detail_info());
			if (((Set) detail_info).size() == 0) {
				gc_list.add(gc.getId());
			} else {
				int m = 0;
				for (GoodsClass gc2 : detail_info) {
					if (gc.getId().equals(gc2.getId())) {
						m++;
					}
				}
				if (m == ((Set) detail_info).size()) {
					gc_list.add(gc.getId());
				}
			}
			parent = gc.getParent();
		}
		map.put("m_id", parent.getId());
		map.put("gc_list", gc_list);
		list.add(map);
		store.setGc_detail_info(JSON.toJSONString(list));
		this.storeService.updateById(store);
		Set<GoodsClass> all_details_gcs = this.storeTools
				.query_store_DetailGc(store.getGc_detail_info());
		mv.addObject("details_gcs", all_details_gcs);
		return mv;
	}
	
	/**
	 * 店铺经营详细类目删除
	 * @param request
	 * @param response
	 * @param gc_id
	 * @return
	 */
	@SecurityMapping(title = "店铺经营详细类目删除", value = "/seller_goodsclass_del*", rtype = "buyer", rname = "商家入驻", rcode = "seller_apply", rgroup = "商家入驻")
	@RequestMapping({ "/seller_goodsclass_del" })
	public ModelAndView seller_goodsclass_del(HttpServletRequest request,
			HttpServletResponse response, String gc_id) {
		ModelAndView mv = new RedPigJModelAndView(
				"seller_apply/seller_store_gc_list.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder
				.getCurrentUser().getId());
		Store store = user.getStore();
		GoodsClass gc_detail = this.goodsClassService.selectByPrimaryKey(CommUtil
				.null2Long(gc_id));
		GoodsClass gc_main = this.goodsClassService.selectByPrimaryKey(gc_detail
				.getParent().getId());
		Map map_temp = this.storeTools.query_MainGc_Map(
				CommUtil.null2String(gc_main.getId()),
				store.getGc_detail_info());
		List<Integer> gc_list = (List) map_temp.get("gc_list");
		for (int i = 0; i < gc_list.size(); i++) {
			if (((Integer) gc_list.get(i)).longValue() == gc_detail.getId()
					.longValue()) {
				gc_list.remove(i);
				break;
			}
		}
		map_temp.put("m_id", gc_main.getId());
		map_temp.put("gc_list", gc_list);

		List<Map> list = Lists.newArrayList();
		List<GoodsClass> gc_mains = this.storeTools
				.query_store_detail_MainGc(store.getGc_detail_info());
		for (GoodsClass gc : gc_mains) {
			if (gc.getId().equals(gc_main.getId())) {
				if (gc_list.size() > 0) {
					list.add(map_temp);
				}
			} else {
				Map map_main = this.storeTools.query_MainGc_Map(
						CommUtil.null2String(gc.getId()),
						store.getGc_detail_info());
				list.add(map_main);
			}
		}
		store.setGc_detail_info(JSON.toJSONString(list));
		this.storeService.updateById(store);
		mv.addObject("details_gcs",
				this.storeTools.query_store_DetailGc(store.getGc_detail_info()));
		mv.addObject("store", store);
		return mv;
	}
}
