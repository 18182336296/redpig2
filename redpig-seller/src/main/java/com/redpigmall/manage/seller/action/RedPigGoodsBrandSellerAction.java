package com.redpigmall.manage.seller.action;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
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
import com.redpigmall.api.tools.RedPigCommonUtil;
import com.redpigmall.api.tools.RedPigMaps;
import com.redpigmall.api.tools.WebForm;
import com.redpigmall.domain.Accessory;
import com.redpigmall.domain.GoodsBrand;
import com.redpigmall.domain.GoodsBrandCategory;
import com.redpigmall.domain.User;
import com.redpigmall.manage.seller.action.base.BaseAction;

/**
 * 
 * <p>
 * Title: RedPigGoodsBrandSellerAction.java
 * </p>
 * 
 * <p>
 * Description:商家品牌管理控制器，所有商家都可以申请自己的平台，平台管理员审核通过后即可在前端展示品牌信息
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
 * @date 2014-6-10
 * 
 * 
 * @version redpigmall_b2b2c v8.0 2016版
 */
@Controller
public class RedPigGoodsBrandSellerAction extends BaseAction{
	/**
	 * 卖家品牌列表
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @return
	 */
	@SecurityMapping(title = "卖家品牌列表", value = "/goods_brand_list*", rtype = "seller", rname = "品牌申请", rcode = "goods_brand_seller", rgroup = "商品管理")
	@RequestMapping({ "/goods_brand_list" })
	public ModelAndView goods_brand_list(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,
			String orderType) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/sellercenter/goods_brand_list.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		Map<String,Object> maps= this.redPigQueryTools.getParams(currentPage, "addTime", "desc");
		
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		
		maps.put("store_id", user.getStore().getId());
		
		IPageList pList = this.goodsBrandService.list(maps);
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		return mv;
	}
	
	/**
	 * 卖家品牌申请
	 * @param request
	 * @param response
	 * @return
	 */
	@SecurityMapping(title = "卖家品牌申请", value = "/goods_brand_add*", rtype = "seller", rname = "品牌申请", rcode = "goods_brand_seller", rgroup = "商品管理")
	@RequestMapping({ "/goods_brand_add" })
	public ModelAndView goods_brand_add(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/sellercenter/goods_brand_add.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		Map<String,Object> maps = Maps.newHashMap();
		maps.put("name_no", "''");
		
		List<GoodsBrandCategory> categorys = this.goodsBrandCategoryService.queryPageList(maps);
		
		mv.addObject("categorys", categorys);
		return mv;
	}
	
	/**
	 * 卖家品牌编辑
	 * @param request
	 * @param response
	 * @param id
	 * @return
	 */
	@SecurityMapping(title = "卖家品牌编辑", value = "/goods_brand_edit*", rtype = "seller", rname = "品牌申请", rcode = "goods_brand_seller", rgroup = "商品管理")
	@RequestMapping({ "/goods_brand_edit" })
	public ModelAndView goods_brand_edit(HttpServletRequest request,
			HttpServletResponse response, String id) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/sellercenter/goods_brand_add.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		if ((id != null) && (!id.equals(""))) {
			GoodsBrand goodsBrand = this.goodsBrandService.selectByPrimaryKey(Long.valueOf(Long.parseLong(id)));
			if ((goodsBrand != null)
					&& (user.getStore() != null)
					&& (goodsBrand.getStore_id() == user.getStore().getId()
							.longValue())) {
				mv.addObject("obj", goodsBrand);
				mv.addObject("edit", Boolean.valueOf(true));
				
				List<GoodsBrandCategory> categorys = this.goodsBrandCategoryService.queryPageList(RedPigMaps.newMap());
				
				mv.addObject("categorys", categorys);
			} else {
				mv = new RedPigJModelAndView(
						"user/default/sellercenter/seller_error.html",
						this.configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 0, request,
						response);
				mv.addObject("op_title", "参数不正确");
				mv.addObject("url", CommUtil.getURL(request)
						+ "/goods_brand_list");
			}
		}
		return mv;
	}
	
	/**
	 * 卖家品牌删除
	 * @param request
	 * @param id
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "卖家品牌删除", value = "/goods_brand_delete*", rtype = "seller", rname = "品牌申请", rcode = "goods_brand_seller", rgroup = "商品管理")
	@RequestMapping({ "/goods_brand_delete" })
	public String goods_brand_deleteById(HttpServletRequest request, String id,
			String currentPage) {
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		if (!id.equals("")) {
			GoodsBrand brand = this.goodsBrandService.selectByPrimaryKey(Long
					.valueOf(Long.parseLong(id)));
			if ((brand != null)
					&& (user.getStore() != null)
					&& (brand.getStore_id() == user.getStore().getId()
							.longValue()) && (brand.getAudit() != 1)) {

				RedPigCommonUtil.del_acc(request, brand.getBrandLogo());
				this.goodsBrandService.deleteById(Long.valueOf(Long.parseLong(id)));
			}
		}
		return "redirect:goods_brand_list?currentPage=" + currentPage;
	}
	
	/**
	 * 卖家品牌保存
	 * @param request
	 * @param response
	 * @param id
	 * @param cmd
	 * @param cat_name
	 * @param list_url
	 * @param add_url
	 * @param gc_id
	 */
	@SuppressWarnings("unchecked")
	@SecurityMapping(title = "卖家品牌保存", value = "/goods_brand_save*", rtype = "seller", rname = "品牌申请", rcode = "goods_brand_seller", rgroup = "商品管理")
	@RequestMapping({ "/goods_brand_save" })
	public void goods_brand_saveEntity(HttpServletRequest request,
			HttpServletResponse response, String id, String cmd,
			String cat_name, String list_url, String add_url, String gc_id) {
		GoodsBrand goodsBrand = null;
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		if ((id.equals("")) || (id == null)) {
			goodsBrand = (GoodsBrand) WebForm.toPo(request, GoodsBrand.class);
			goodsBrand.setAddTime(new Date());
			goodsBrand.setAudit(0);
			goodsBrand.setUserStatus(1);
			goodsBrand.setStore_id(user.getStore().getId().longValue());
			goodsBrand.setShow_index(true);
			goodsBrand.setRecommend(false);
			goodsBrand.setSequence(0);
			
		} else {
			GoodsBrand obj = this.goodsBrandService.selectByPrimaryKey(Long
					.valueOf(Long.parseLong(id)));
			goodsBrand = (GoodsBrand) WebForm.toPo(request, obj);
		}
		String uploadFilePath = this.configService.getSysConfig()
				.getUploadFilePath();
		String saveFilePathName = request.getSession().getServletContext()
				.getRealPath("/")
				+ uploadFilePath + File.separator + "brand";
		Map<String, Object> map = Maps.newHashMap();
		try {
			String fileName = goodsBrand.getBrandLogo() == null ? ""
					: goodsBrand.getBrandLogo().getName();
			map = CommUtil.saveFileToServer(request, "brandLogo",
					saveFilePathName, fileName, null);
			if (fileName.equals("")) {
				if (map.get("fileName") != "") {
					Accessory photo = new Accessory();
					photo.setName(CommUtil.null2String(map.get("fileName")));
					photo.setExt(CommUtil.null2String(map.get("mime")));
					photo.setSize(BigDecimal.valueOf(CommUtil.null2Double(map
							.get("fileSize"))));
					photo.setPath(uploadFilePath + "/brand");
					photo.setWidth(CommUtil.null2Int(map.get("width")));
					photo.setHeight(CommUtil.null2Int(map.get("height")));
					photo.setAddTime(new Date());
					this.accessoryService.saveEntity(photo);
					goodsBrand.setBrandLogo(photo);
				}
			} else if (map.get("fileName") != "") {
				Accessory photo = goodsBrand.getBrandLogo();
				photo.setName(CommUtil.null2String(map.get("fileName")));
				photo.setExt(CommUtil.null2String(map.get("mime")));
				photo.setSize(BigDecimal.valueOf(CommUtil.null2Double(map
						.get("fileSize"))));
				photo.setPath(uploadFilePath + "/brand");
				photo.setWidth(CommUtil.null2Int(map.get("width")));
				photo.setHeight(CommUtil.null2Int(map.get("height")));
				photo.setAddTime(new Date());
				this.accessoryService.updateById(photo);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		if ((cat_name != null) && (!cat_name.equals(""))) {
			GoodsBrandCategory cat = this.goodsBrandCategoryService.getObjByProperty("name","=",  cat_name);
			goodsBrand.setCategory(cat);
		} else {
			goodsBrand.setCategory(null);
		}
		if (id.equals("")) {
			this.goodsBrandService.saveEntity(goodsBrand);
		} else {
			this.goodsBrandService.updateById(goodsBrand);
		}
		Map<String,Object> json = Maps.newHashMap();
		json.put("ret", Boolean.valueOf(true));
		json.put("op_title", "品牌申请成功");
		json.put("url", CommUtil.getURL(request) + "/goods_brand_list");
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		try {
			PrintWriter writer = response.getWriter();
			writer.print(JSON.toJSONString(json));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
