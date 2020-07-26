package com.redpigmall.manage.seller.action;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.util.Date;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.redpigmall.api.annotation.SecurityMapping;
import com.redpigmall.api.mv.RedPigJModelAndView;
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.api.sec.SecurityUserHolder;
import com.redpigmall.api.tools.CommUtil;
import com.redpigmall.api.tools.ObjectUtils;
import com.redpigmall.domain.GoodsTypeProperty;
import com.redpigmall.domain.User;
import com.redpigmall.manage.seller.action.base.BaseAction;

/**
 * 
 * <p>
 * Title: RedPigGoodsTypePropertySellerAction.java
 * </p>
 * 
 * <p>
 * Description: 商家类型属性管理类，主要用于商家自己添加类型属性，只用于商品详情页显示，不适用于列表页筛选
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
 * @date 2015-6-15
 * 
 * @version redpigmall_b2b2c v8.0 2016版
 */
@Controller
public class RedPigGoodsTypePropertySellerAction extends BaseAction{
	/**
	 * 商品类型属性列表
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @return
	 */
	@SecurityMapping(title = "商品类型属性列表", value = "/gtp_list*", rtype = "seller", rname = "属性管理", rcode = "gtp_seller", rgroup = "商品管理")
	@RequestMapping({ "/gtp_list" })
	public ModelAndView list(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,
			String orderType) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/sellercenter/gtp_list.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		
		Map<String,Object> maps= this.redPigQueryTools.getParams(currentPage, "sequence", "asc");
		
		maps.put("store_id", user.getStore().getId());
		
		IPageList pList = this.gtpService.list(maps);
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		return mv;
	}
	
	/**
	 * 类型属性添加
	 * @param request
	 * @param response
	 * @return
	 */
	@SecurityMapping(title = "类型属性添加", value = "/gtp_add*", rtype = "seller", rname = "属性管理", rcode = "gtp_seller", rgroup = "商品管理")
	@RequestMapping({ "/gtp_add" })
	public ModelAndView add(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/sellercenter/gtp_add.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		return mv;
	}
	
	/**
	 * 类型属性保存
	 * @param request
	 * @param response
	 * @param count
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "类型属性保存", value = "/gtp_save*", rtype = "seller", rname = "属性管理", rcode = "gtp_seller", rgroup = "商品管理")
	@RequestMapping({ "/gtp_save" })
	public String saveEntity(HttpServletRequest request,
			HttpServletResponse response, String count, String currentPage) {
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		for (int i = 1; i <= CommUtil.null2Int(count); i++) {
			int sequence = CommUtil.null2Int(request
					.getParameter("gtp_sequence_" + i));
			String name = CommUtil.null2String(request.getParameter("gtp_name_"
					+ i));
			String value = CommUtil.null2String(request
					.getParameter("gtp_value_" + i));
			if ((!name.equals("")) && (!value.equals(""))) {
				GoodsTypeProperty gtp = new GoodsTypeProperty();
				String id = CommUtil.null2String(request.getParameter("gtp_id_"
						+ i));
				if (id.equals("")) {
					gtp = new GoodsTypeProperty();
				} else {
					gtp = this.gtpService.selectByPrimaryKey(Long.valueOf(Long
							.parseLong(id)));
				}
				gtp.setAddTime(new Date());
				gtp.setStore_id(user.getStore().getId());
				gtp.setName(name);
				gtp.setSequence(sequence);
				gtp.setValue(value);
				if (id.equals("")) {
					this.gtpService.saveEntity(gtp);
				} else {
					this.gtpService.updateById(gtp);
				}
			}
		}
		request.getSession(false).setAttribute("url",
				CommUtil.getURL(request) + "/gtp_list");
		request.getSession(false).setAttribute("op_title", "添加成功");

		return "redirect:/success";
	}
	
	/**
	 * 商品属性删除
	 * @param request
	 * @param mulitId
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "商品属性删除", value = "/gtp_del*", rtype = "seller", rname = "属性管理", rcode = "gtp_seller", rgroup = "商品管理")
	@RequestMapping({ "/gtp_del" })
	public String deleteById(HttpServletRequest request, String mulitId,
			String currentPage) {
		String[] ids = mulitId.split(",");
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();

		for (String id : ids) {

			if (!id.equals("")) {
				GoodsTypeProperty obj = this.gtpService.selectByPrimaryKey(Long.valueOf(Long.parseLong(id)));
				if ((obj != null)
						&& (obj.getStore_id().equals(user.getStore().getId()))) {
					this.gtpService.deleteById(Long.valueOf(Long.parseLong(id)));
				}
			}
		}
		return "redirect:gtp_list?currentPage=" + currentPage;
	}
	
	/**
	 * 属性Ajax更新
	 * @param request
	 * @param response
	 * @param id
	 * @param fieldName
	 * @param value
	 * @throws ClassNotFoundException
	 */
	@SecurityMapping(title = "属性Ajax更新", value = "/gtp_update_ajax*", rtype = "seller", rname = "属性管理", rcode = "gtp_seller", rgroup = "商品")
	@RequestMapping({ "/gtp_update_ajax" })
	public void gtp_ajax(HttpServletRequest request,
			HttpServletResponse response, String id, String fieldName,
			String value) throws ClassNotFoundException {
		GoodsTypeProperty gtp = this.gtpService.selectByPrimaryKey(Long.valueOf(Long
				.parseLong(id)));
		Field[] fields = GoodsTypeProperty.class.getDeclaredFields();
		
		Object val = ObjectUtils.setValue(fieldName, value, gtp, fields);
		
		this.gtpService.updateById(gtp);
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
}
