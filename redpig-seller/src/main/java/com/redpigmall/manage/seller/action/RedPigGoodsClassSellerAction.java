package com.redpigmall.manage.seller.action;

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
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.api.sec.SecurityUserHolder;
import com.redpigmall.api.tools.CommUtil;
import com.redpigmall.api.tools.WebForm;
import com.redpigmall.domain.Goods;
import com.redpigmall.domain.User;
import com.redpigmall.domain.UserGoodsClass;
import com.redpigmall.manage.seller.action.base.BaseAction;

/**
 * 
 * <p>
 * Title: RedPigGoodsClassSellerAction.java
 * </p>
 * 
 * <p>
 * Description: 商家后台商品分类管理
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
 * @date 2014-10-19
 * 
 * @version redpigmall_b2b2c 2016
 */
@Controller
public class RedPigGoodsClassSellerAction extends BaseAction {
	/**
	 * 卖家商品分类列表
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @return
	 */
	@SecurityMapping(title = "卖家商品分类列表", value = "/goods_class_list*", rtype = "seller", rname = "商品分类", rcode = "goods_class_seller", rgroup = "商品管理")
	@RequestMapping({ "/goods_class_list" })
	public ModelAndView goods_class_list(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,
			String orderType) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/sellercenter/goods_class_list.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		Map<String,Object> maps= this.redPigQueryTools.getParams(currentPage,20, orderBy, orderType);
		maps.put("parent", -1);
		maps.put("user_id", user.getId());
		maps.put("orderBy", "sequence");
		maps.put("orderType", "asc");
		
		IPageList pList = this.usergoodsclassService.list(maps);
		
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		return mv;
	}
	
	/**
	 * 卖家商品分类保存
	 * @param request
	 * @param response
	 * @param id
	 * @param pid
	 * @return
	 */
	@SecurityMapping(title = "卖家商品分类保存", value = "/goods_class_save*", rtype = "seller", rname = "商品分类", rcode = "goods_class_seller", rgroup = "商品管理")
	@RequestMapping({ "/goods_class_save" })
	public String goods_class_saveEntity(HttpServletRequest request,
			HttpServletResponse response, String id, String pid) {
		
		UserGoodsClass usergoodsclass = null;
		if (id.equals("")) {
			usergoodsclass = (UserGoodsClass) WebForm.toPo(request,
					UserGoodsClass.class);
			usergoodsclass.setAddTime(new Date());
		} else {
			UserGoodsClass obj = this.usergoodsclassService.selectByPrimaryKey(Long.valueOf(Long.parseLong(id)));
			usergoodsclass = (UserGoodsClass) WebForm.toPo(request, obj);
//			usergoodsclass.setDisplay(Boolean.valueOf(request.getParameter("display")));
		}
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		usergoodsclass.setUser_id(user.getId().longValue());
		
		if (!pid.equals("")) {
			UserGoodsClass parent = this.usergoodsclassService.selectByPrimaryKey(Long.valueOf(Long.parseLong(pid)));
			usergoodsclass.setParent(parent);
		}
		if (id.equals("")) {
			this.usergoodsclassService.saveEntity(usergoodsclass);
		} else {
			this.usergoodsclassService.updateById(usergoodsclass);
		}
		return "redirect:/goods_class_list";
	}
	
	/**
	 * 卖家商品分类删除
	 * @param request
	 * @param mulitId
	 * @return
	 */
	@SecurityMapping(title = "卖家商品分类删除", value = "/goods_class_del*", rtype = "seller", rname = "商品分类", rcode = "goods_class_seller", rgroup = "商品管理")
	@RequestMapping({ "/goods_class_del" })
	public String goods_class_del(HttpServletRequest request, String mulitId) {
		String[] ids = mulitId.split(",");
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		for (String id : ids) {
			if (!id.equals("")) {
				UserGoodsClass uc = this.usergoodsclassService.selectByPrimaryKey(Long.valueOf(Long.parseLong(id)));
				if ((uc != null)
						&& (uc.getUser_id() == user.getId().longValue())) {
					clean_ugc(uc);
					this.usergoodsclassService.deleteById(uc.getId());
				}
			}
		}
		return "redirect:goods_class_list";
	}

	private void clean_ugc(UserGoodsClass ugc) {
		for (UserGoodsClass child : ugc.getChilds()) {
			clean_ugc(child);
			this.usergoodsclassService.deleteById(child.getId());
		}
		
		ugc.getChilds().removeAll(ugc.getChilds());
		for (Goods goods : ugc.getGoods_list()) {
			goods.getGoods_ugcs().remove(ugc);
			this.redPigGoodsService.batchDeleteUserGoodsClass(goods.getId(), Lists.newArrayList(ugc));
		}
		
		ugc.getGoods_list().removeAll(ugc.getGoods_list());
	}
	
	/**
	 * 新增卖家商品分类
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param pid
	 * @return
	 */
	@SecurityMapping(title = "新增卖家商品分类", value = "/goods_class_add*", rtype = "seller", rname = "商品分类", rcode = "goods_class_seller", rgroup = "商品管理")
	@RequestMapping({ "/goods_class_add" })
	public ModelAndView goods_class_add(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String pid) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/sellercenter/goods_class_add.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		Map<String, Object> map = Maps.newHashMap();
		map.put("user_id", user.getId());
		map.put("parent", -1);
		map.put("orderBy", "sequence");
		map.put("orderType", "asc");
		
		List<UserGoodsClass> ugcs = this.usergoodsclassService.queryPageList(map);
		
		if (!CommUtil.null2String(pid).equals("")) {
			UserGoodsClass parent = this.usergoodsclassService.selectByPrimaryKey(CommUtil.null2Long(pid));
			UserGoodsClass obj = new UserGoodsClass();
			obj.setParent(parent);
			mv.addObject("obj", obj);
		}
		mv.addObject("ugcs", ugcs);
		mv.addObject("currentPage", currentPage);
		return mv;
	}
	
	/**
	 * 编辑卖家商品分类
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param id
	 * @return
	 */
	@SecurityMapping(title = "编辑卖家商品分类", value = "/goods_class_edit*", rtype = "seller", rname = "商品分类", rcode = "goods_class_seller", rgroup = "商品管理")
	@RequestMapping({ "/goods_class_edit" })
	public ModelAndView goods_class_edit(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String id) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/sellercenter/goods_class_add.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		Map<String, Object> map = Maps.newHashMap();
		map.put("user_id", user.getId());
		map.put("parent", -1);
		map.put("orderBy", "sequence");
		map.put("orderType", "asc");
		
		List<UserGoodsClass> ugcs = this.usergoodsclassService.queryPageList(map);
		
		UserGoodsClass obj = this.usergoodsclassService.selectByPrimaryKey(CommUtil
				.null2Long(id));
		mv.addObject("obj", obj);
		mv.addObject("ugcs", ugcs);
		mv.addObject("currentPage", currentPage);
		return mv;
	}
}
