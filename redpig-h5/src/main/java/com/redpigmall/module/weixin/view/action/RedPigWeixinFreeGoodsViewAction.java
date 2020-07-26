package com.redpigmall.module.weixin.view.action;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.redpigmall.api.annotation.SecurityMapping;
import com.redpigmall.api.mv.RedPigJModelAndView;
import com.redpigmall.api.sec.SecurityUserHolder;
import com.redpigmall.api.tools.CommUtil;
import com.redpigmall.api.tools.RedPigMaps;
import com.redpigmall.domain.Address;
import com.redpigmall.domain.FreeApplyLog;
import com.redpigmall.domain.FreeClass;
import com.redpigmall.domain.FreeGoods;
import com.redpigmall.domain.User;
import com.redpigmall.module.weixin.view.action.base.BaseAction;


/**
 * 
 * <p>
 * Title: RedPigWapFreeGoodsViewAction.java
 * </p>
 * 
 * <p>
 * Description: wap前台0元试用控制器
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
 * @date 2014-11-18
 * 
 * @version redpigmall_b2b2c v8.0 2016版 
 */
@Controller
public class RedPigWeixinFreeGoodsViewAction extends BaseAction {
	/**
	 * 免费试用首页
	 * @param request
	 * @param response
	 * @param cls
	 * @return
	 */
	@RequestMapping({ "/free/index" })
	public ModelAndView freegoods_list(HttpServletRequest request,
			HttpServletResponse response, String cls) {
		ModelAndView mv = new RedPigJModelAndView("weixin/free_index.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		Map<String, Object> map = Maps.newHashMap();
		map.put("freeStatus", Integer.valueOf(5));
		
		if ((cls != null) && (!cls.equals(""))) {
			mv.addObject("cls_id", cls);
			map.put("class_id", CommUtil.null2Long(cls));
			
		}
		List<FreeGoods> objs = this.freegoodsService.queryPageList(map, 0,12);
		
		List<FreeClass> fcls = this.freeClassService.queryPageList(RedPigMaps.newMap());
		
		mv.addObject("fcls", fcls);
		mv.addObject("freeTools", this.freeTools);
		mv.addObject("objs", objs);
		return mv;
	}
	
	/**
	 * 免费试用数据
	 * @param request
	 * @param response
	 * @param begin_count
	 * @param cls
	 * @return
	 */
	@RequestMapping({ "/free/data" })
	public ModelAndView freegoods_data(HttpServletRequest request,
			HttpServletResponse response, String begin_count, String cls) {
		ModelAndView mv = new RedPigJModelAndView("weixin/free_data.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		Map<String, Object> map = Maps.newHashMap();
		map.put("freeStatus", Integer.valueOf(5));
		
		if ((cls != null) && (!cls.equals(""))) {
			mv.addObject("cls_id", cls);
			map.put("class_id", CommUtil.null2Long(cls));
			
		}
		List<FreeGoods> objs = this.freegoodsService.queryPageList(map,CommUtil.null2Int(begin_count), 12);
		
		List<FreeClass> fcls = this.freeClassService.queryPageList(RedPigMaps.newMap());
		
		mv.addObject("fcls", fcls);
		mv.addObject("freeTools", this.freeTools);
		mv.addObject("objs", objs);
		return mv;
	}
	
	/**
	 * 试用
	 * @param id
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping({ "/free/view" })
	public ModelAndView free_view(String id, HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new RedPigJModelAndView("weixin/free_view.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		FreeGoods obj = this.freegoodsService.selectByPrimaryKey(CommUtil.null2Long(id));
		if (obj == null) {
			mv = new RedPigJModelAndView("weixin/error.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "您查看的商品已经下架");
			mv.addObject("url", CommUtil.getURL(request) + "/buyer/index");
			return mv;
		}
		if ((obj.getFreeStatus() == -5) || (obj.getFreeStatus() == 0)) {
			mv = new RedPigJModelAndView("weixin/error.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "参数错误，查看失败");
			mv.addObject("url", CommUtil.getURL(request) + "/index");
		}
		mv.addObject("freeTools", this.freeTools);
		mv.addObject("obj", obj);
		return mv;
	}
	
	/**
	 * 0元试用申请
	 * @param id
	 * @param request
	 * @param response
	 * @param addr_id
	 * @return
	 */
	@SecurityMapping(title = "0元试用申请", value = "/free_apply*", rtype = "buyer", rname = "0元试用申请", rcode = "free_apply", rgroup = "在线购物")
	@RequestMapping({ "/free_apply" })
	public ModelAndView free_apply(String id, HttpServletRequest request,
			HttpServletResponse response, String addr_id) {
		ModelAndView mv = new RedPigJModelAndView("weixin/free_apply.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		User user = SecurityUserHolder.getCurrentUser();
		Map<String, Object> params = Maps.newHashMap();
		params.put("user_id", user.getId());
		params.put("freegoods_id", CommUtil.null2Long(id));
		
		List<FreeApplyLog> fals1 = this.freeapplylogService.queryPageList(params);
		
		params.clear();
		params.put("user_id", user.getId());
		params.put("evaluate_status", 0);
		
		List<FreeApplyLog> fals2 = this.freeapplylogService.queryPageList(params);
		
		if ((fals1.size() > 0) || (fals2.size() > 0)) {
			mv = new RedPigJModelAndView("weixin/error.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "您有尚未评价的0元试用或您申请过此0元试用");
			mv.addObject("url", CommUtil.getURL(request) + "/free/index");
		} else {
			List<Address> addrs = Lists.newArrayList();
			if ((addr_id != null) && (!addr_id.equals(""))) {
				Address address = this.addressService.selectByPrimaryKey(CommUtil
						.null2Long(addr_id));
				addrs.add(address);
			} else {
				params.put("orderBy", "default_val desc,addTime");
				params.put("orderType", "desc");
				
				addrs = this.addressService.queryPageList(params, 0,1);
				
			}
			if (addrs.size() > 0) {
				mv.addObject("addr_id", ((Address) addrs.get(0)).getId());
			}
			mv.addObject("addrs", addrs);
			String apply_session = CommUtil.randomString(32);
			request.getSession(false).setAttribute("apply_session",
					apply_session);
			mv.addObject("apply_session", apply_session);
			mv.addObject("id", id);
		}
		return mv;
	}
	
	/**
	 * 0元试用申请
	 * @param id
	 * @param request
	 * @param response
	 * @param apply_reason
	 * @param apply_session
	 * @param addr_id
	 * @return
	 */
	@SecurityMapping(title = "0元试用申请", value = "/free_apply*", rtype = "buyer", rname = "0元试用申请", rcode = "free_apply", rgroup = "在线购物")
	@RequestMapping({ "/free_apply_save" })
	public ModelAndView free_apply_saveEntity(String id, HttpServletRequest request,
			HttpServletResponse response, String apply_reason,
			String apply_session, String addr_id) {
		ModelAndView mv = new RedPigJModelAndView("weixin/success.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		String apply_session1 = (String) request.getSession(false)
				.getAttribute("apply_session");
		if (apply_session1.equals(apply_session)) {
			User user = SecurityUserHolder.getCurrentUser();
			Map<String, Object> params = Maps.newHashMap();
			params.put("user_id", user.getId());
			params.put("freegoods_id", CommUtil.null2Long(id));
			
			List<FreeApplyLog> fals1 = this.freeapplylogService.queryPageList(params);
			
			params.clear();
			params.put("user_id", user.getId());
			params.put("evaluate_status", 0);
			
			List<FreeApplyLog> fals2 = this.freeapplylogService.queryPageList(params);
			
			if ((fals1.size() > 0) || (fals2.size() > 0)) {
				mv = new RedPigJModelAndView("weixin/error.html",
						this.configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 1, request,
						response);
				mv.addObject("op_title", "您有尚未评价的0元试用或您申请过此0元试用");
				mv.addObject("url", CommUtil.getURL(request)
						+ "/free/index");
			} else {
				FreeGoods fg = this.freegoodsService.selectByPrimaryKey(CommUtil
						.null2Long(id));
				Address addr = this.addressService.selectByPrimaryKey(CommUtil
						.null2Long(addr_id));
				if (fg != null) {
					FreeApplyLog fal = new FreeApplyLog();
					fal.setAddTime(new Date());
					fal.setFreegoods_id(fg.getId());
					fal.setWhether_self(fg.getFreeType());
					fal.setStore_id(fg.getStore_id());
					fal.setFreegoods_name(fg.getFree_name());

					fal.setReceiver_Name(addr.getTrueName());
					fal.setReceiver_area(addr.getArea().getParent().getParent()
							.getAreaName()
							+ addr.getArea().getParent().getAreaName()
							+ addr.getArea().getAreaName());
					fal.setReceiver_area_info(addr.getArea_info());
					fal.setReceiver_mobile(addr.getMobile());
					fal.setReceiver_telephone(addr.getTelephone());
					fal.setReceiver_zip(addr.getZip());
					fal.setUser_id(user.getId());
					fal.setUser_name(user.getUserName());
					this.freeapplylogService.saveEntity(fal);
					fg.setApply_count(fg.getApply_count() + 1);
					this.freegoodsService.updateById(fg);
					fal.setApply_reason(CommUtil.filterHTML(apply_reason));
					mv.addObject("op_title", "申请成功，请耐心等待审核");
					mv.addObject("url", CommUtil.getURL(request)
							+ "/buyer/free_list");
				} else {
					mv = new RedPigJModelAndView("weixin/error.html",
							this.configService.getSysConfig(),
							this.userConfigService.getUserConfig(), 1, request,
							response);
					mv.addObject("op_title", "您有尚未评价的0元试用或您申请过此0元试用");
					mv.addObject("url", CommUtil.getURL(request)
							+ "/free/index");
				}
			}
		} else {
			mv = new RedPigJModelAndView("weixin/error.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "参数错误");
			mv.addObject("url", CommUtil.getURL(request) + "/index");
		}
		return mv;
	}
}
