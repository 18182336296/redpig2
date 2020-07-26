package com.redpigmall.manage.admin.action.self.consult;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.redpigmall.api.annotation.SecurityMapping;
import com.redpigmall.api.mv.RedPigJModelAndView;
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.api.sec.SecurityUserHolder;
import com.redpigmall.api.tools.CommUtil;
import com.redpigmall.manage.admin.action.base.BaseAction;
import com.redpigmall.domain.Consult;
import com.redpigmall.domain.User;

/**
 * 
 * <p>
 * Title: RedPigSelfConsultManageAction.java
 * </p>
 * 
 * <p>
 * Description:自营咨询管理
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
 * @date 2014-10-29
 * 
 * @version redpigmall_b2b2c 2016
 */
@Controller
public class RedPigSelfConsultManageAction extends BaseAction {
	/**
	 * 自营商品咨询列表
	 * @param request
	 * @param response
	 * @param reply
	 * @param currentPage
	 * @param consult_user_userName
	 * @return
	 */
	@SecurityMapping(title = "自营商品咨询列表", value = "/consult_self*", rtype = "admin", rname = "商品咨询", rcode = "consult_self_manage", rgroup = "自营")
	@RequestMapping({ "/consult_self" })
	public ModelAndView consult(HttpServletRequest request,
			HttpServletResponse response, String reply, String currentPage,
			String consult_user_userName) {
		ModelAndView mv = new RedPigJModelAndView(
				"admin/blue/consult_self_list.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		
		Map<String,Object> params = this.redPigQueryTools.getParams(currentPage, "addTime", "desc");
		
		if (!CommUtil.null2String(reply).equals("")) {
			
			params.put("reply", CommUtil.null2Boolean(reply));
		}
		
		if ((consult_user_userName != null) && (!consult_user_userName.equals(""))) {
			params.put("consult_user_name", CommUtil.null2String(consult_user_userName).trim());
		}
		
		params.put("whether_self", 1);
		params.put("pageSize", 2);
		IPageList pList = this.consultService.list(params);
		
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		mv.addObject("reply", CommUtil.null2String(reply));
		mv.addObject("consult_user_userName", consult_user_userName);
		return mv;
	}
	
	/**
	 * 自营商品咨询回复
	 * @param request
	 * @param response
	 * @param id
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "自营商品咨询回复", value = "/consult_self_reply*", rtype = "admin", rname = "商品咨询", rcode = "consult_self_manage", rgroup = "自营")
	@RequestMapping({ "/consult_self_reply" })
	public ModelAndView consult_reply(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage) {
		ModelAndView mv = new RedPigJModelAndView(
				"admin/blue/consult_self_reply.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		Consult obj = this.consultService.selectByPrimaryKey(CommUtil.null2Long(id));
		mv.addObject("obj", obj);
		mv.addObject("currentPage", currentPage);
		return mv;
	}
	
	/**
	 * 自营商品咨询回复保存
	 * @param request
	 * @param response
	 * @param id
	 * @param consult_reply
	 * @param currentPage
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("rawtypes")
	@SecurityMapping(title = "自营商品咨询回复保存", value = "/consult_reply_self_save*", rtype = "admin", rname = "商品咨询", rcode = "consult_self_manage", rgroup = "自营")
	@RequestMapping({ "/consult_reply_self_save" })
	public String consult_reply_saveEntity(HttpServletRequest request,
			HttpServletResponse response, String id, String consult_reply,
			String currentPage) throws Exception {
		Consult obj = this.consultService.selectByPrimaryKey(CommUtil.null2Long(id));
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder.getCurrentUser().getId());
		obj.setConsult_reply(consult_reply);
		obj.setReply_time(new Date());
		obj.setReply_user_id(user.getId());
		obj.setReply_user_name("平台运营商");
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
			this.msgTools.sendEmailFree(CommUtil.getURL(request),
					"email_tobuyer_cousult_reply_notify", this.userService
							.selectByPrimaryKey(obj.getConsult_user_id())
							.getEmail(), json, null);
		}
		return "redirect:consult_self?currentPage=" + currentPage;
	}
	
	/**
	 * 自营商品咨询删除
	 * @param request
	 * @param response
	 * @param mulitId
	 * @param consult_reply
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "自营商品咨询删除", value = "/consult_self_del*", rtype = "admin", rname = "商品咨询", rcode = "consult_self_manage", rgroup = "自营")
	@RequestMapping({ "/consult_self_del" })
	public String consult_del(
			HttpServletRequest request,
			HttpServletResponse response, 
			String mulitId, 
			String consult_reply,
			String currentPage) {
		String[] ids = mulitId.split(",");
		List<Long> lists = Lists.newArrayList();
		
		for (String id : ids) {
			lists.add(Long.parseLong(id));
		}
		if(lists!=null && lists.size()>0){
			this.consultService.batchDeleteByIds(lists);
		}
		return "redirect:consult_self?currentPage=" + currentPage;
	}
}
