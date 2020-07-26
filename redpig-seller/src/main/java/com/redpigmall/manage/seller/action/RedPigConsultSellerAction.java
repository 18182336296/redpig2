package com.redpigmall.manage.seller.action;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import com.redpigmall.api.annotation.SecurityMapping;
import com.redpigmall.api.mv.RedPigJModelAndView;
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.api.sec.SecurityUserHolder;
import com.redpigmall.api.tools.CommUtil;
import com.redpigmall.domain.Consult;
import com.redpigmall.domain.User;
import com.redpigmall.manage.seller.action.base.BaseAction;

/**
 * 
 * <p>
 * Title: RedPigConsultSellerAction.java
 * </p>
 * 
 * <p>
 * Description:卖家咨询管理器，显示所有卖家咨询信息，卖家在这里可以回复买家的咨询及其他咨询信息的操作
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
 * @date 2014-9-29
 * 
 * @version redpigmall_b2b2c 2016
 */
@Controller
public class RedPigConsultSellerAction extends BaseAction {
	/**
	 * 卖家咨询列表
	 * @param request
	 * @param response
	 * @param reply
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "卖家咨询列表", value = "/consult*", rtype = "seller", rname = "咨询管理", rcode = "consult_seller", rgroup = "客户服务")
	@RequestMapping({ "/consult" })
	public ModelAndView consult(HttpServletRequest request,
			HttpServletResponse response, String reply, String currentPage) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/sellercenter/consult.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		
		Map<String,Object> maps= this.redPigQueryTools.getParams(currentPage,"addTime", "desc");
		if (!CommUtil.null2String(reply).equals("")) {
			
			maps.put("reply", CommUtil.null2Boolean(reply));
		}
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		
		maps.put("store_id", user.getStore().getId());
		IPageList pList = this.consultService.list(maps);
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		mv.addObject("reply", CommUtil.null2String(reply));
		mv.addObject("GoodsViewTools", this.GoodsViewTools);
		return mv;
	}
	
	/**
	 * 卖家咨询回复
	 * @param request
	 * @param response
	 * @param id
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "卖家咨询回复", value = "/consult_reply*", rtype = "seller", rname = "咨询管理", rcode = "consult_seller", rgroup = "客户服务")
	@RequestMapping({ "/consult_reply" })
	public ModelAndView consult_reply(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/sellercenter/consult_reply.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		Consult obj = this.consultService.selectByPrimaryKey(CommUtil.null2Long(id));
		mv.addObject("obj", obj);
		mv.addObject("currentPage", currentPage);
		return mv;
	}
	
	/**
	 * 卖家咨询回复保存
	 * @param request
	 * @param response
	 * @param id
	 * @param consult_reply
	 * @param currentPage
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("rawtypes")
	@SecurityMapping(title = "卖家咨询回复保存", value = "/consult_reply_save*", rtype = "seller", rname = "咨询管理", rcode = "consult_seller", rgroup = "客户服务")
	@RequestMapping({ "/consult_reply_save" })
	public String consult_reply_saveEntity(HttpServletRequest request,
			HttpServletResponse response, String id, String consult_reply,
			String currentPage) throws Exception {
		Consult obj = this.consultService.selectByPrimaryKey(CommUtil.null2Long(id));
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		obj.setConsult_reply(consult_reply);
		obj.setReply_time(new Date());
		obj.setReply_user_id(user.getId());
		obj.setReply_user_name(user.getUserName());
		obj.setReply(true);
		this.consultService.updateById(obj);
		if ((this.configService.getSysConfig().getEmailEnable())
				&& (obj.getConsult_user_id() != null)) {
			Map<String, Object> map = Maps.newHashMap();
			map.put("buyer_id", CommUtil.null2String(obj.getConsult_user_id()));
			List<Map> maps = CommUtil.Json2List(obj.getGoods_info());
			for (Map m : maps) {
				map.put("goods_id", m.get("goods_id").toString());
			}
			String json = JSON.toJSONString(map);
			this.msgTools.sendEmailCharge(CommUtil.getURL(request),
					"email_tobuyer_cousult_reply_notify", this.userService
							.selectByPrimaryKey(obj.getConsult_user_id()).getEmail(),
					json, null, CommUtil.null2String(user.getStore().getId()));
		}
		return "redirect:consult?currentPage=" + currentPage;
	}
	
	/**
	 * 卖家咨询删除
	 * @param request
	 * @param response
	 * @param mulitId
	 * @param consult_reply
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "卖家咨询删除", value = "/consult_del*", rtype = "seller", rname = "咨询管理", rcode = "consult_seller", rgroup = "客户服务")
	@RequestMapping({ "/consult_del" })
	public String consult_del(HttpServletRequest request,
			HttpServletResponse response, String mulitId, String consult_reply,
			String currentPage) {
		String[] ids = mulitId.split(",");
		for (String id : ids) {
			if (!id.equals("")) {
				Consult obj = this.consultService.selectByPrimaryKey(CommUtil.null2Long(id));
				User user = this.userService.selectByPrimaryKey(SecurityUserHolder.getCurrentUser().getId());
				user = user.getParent() == null ? user : user.getParent();
				if (obj.getStore_id().equals(user.getStore().getId())) {
					this.consultService.deleteById(CommUtil.null2Long(id));
				}
			}
		}
		return "redirect:consult?currentPage=" + currentPage;
	}
}
