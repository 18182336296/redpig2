package com.redpigmall.manage.seller.action;

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
import com.redpigmall.api.annotation.SecurityMapping;
import com.redpigmall.api.mv.RedPigJModelAndView;
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.api.sec.SecurityUserHolder;
import com.redpigmall.api.tools.CommUtil;
import com.redpigmall.api.tools.RedPigCommonUtil;
import com.redpigmall.api.tools.WebForm;
import com.redpigmall.domain.Accessory;
import com.redpigmall.domain.Goods;
import com.redpigmall.domain.GoodsCart;
import com.redpigmall.domain.GoodsClass;
import com.redpigmall.domain.GoodsSpecProperty;
import com.redpigmall.domain.GoodsSpecification;
import com.redpigmall.domain.Store;
import com.redpigmall.domain.SysConfig;
import com.redpigmall.domain.User;
import com.redpigmall.manage.seller.action.base.BaseAction;

/**
 * 
 * <p>
 * Title: RedPigGoodsSpecSellerAction.java
 * </p>
 * 
 * <p>
 * Description: 商家商品规格管理控制器，商家可以自行管理规格属性，发不商品时商家选择自己添加的规格属性，规格属性只在商品详细页显示并可以选择，
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
 * @date 2014-5-7
 * 
 * @version redpigmall_b2b2c v8.0 2016版
 */
@Controller
public class RedPigGoodsSpecSellerAction extends BaseAction{
	
	/**
	 * 商品规格列表
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @param searchType
	 * @param searchText
	 * @return
	 */
	@SecurityMapping(title = "商品规格列表", value = "/goods_spec_list*", rtype = "seller", rname = "规格管理", rcode = "spec_seller", rgroup = "商品管理")
	@RequestMapping({ "/goods_spec_list" })
	public ModelAndView list(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,
			String orderType, String searchType, String searchText) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/sellercenter/goods_spec_list.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		
		Map<String,Object> maps= this.redPigQueryTools.getParams(currentPage, "sequence", "asc");
		
		maps.put("store_id", user.getStore().getId());
		if ((searchType != null) && (!searchType.equals(""))) {
			if (searchType.equals("1")) {
				maps.put("remark_name_like", searchText);
			} else {
				maps.put("name_like", searchText);
			}
		}
		
		IPageList pList = this.goodsSpecService.list(maps);
		
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		mv.addObject("shopTools", this.shopTools);
		mv.addObject("searchType", searchType);
		mv.addObject("searchText", searchText);
		mv.addObject("currentPage", currentPage);
		return mv;
	}
	
	/**
	 * 商品规格添加
	 * @param request
	 * @param response
	 * @return
	 */
	@SecurityMapping(title = "商品规格添加", value = "/goods_spec_add*", rtype = "seller", rname = "规格管理", rcode = "spec_seller", rgroup = "商品管理")
	@RequestMapping({ "/goods_spec_add" })
	public ModelAndView add(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/sellercenter/goods_spec_add.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		mv.addObject("store_id", user.getStore().getId());
		Set<GoodsClass> gcs = this.shopTools.query_store_DetailGc(user.getStore().getGc_detail_info());
		if (gcs.size() > 0) {
			mv.addObject("gcs", gcs);
		} else {
			GoodsClass main_gc = this.goodsClassService.selectByPrimaryKey(user.getStore().getGc_main_id());
			mv.addObject("gcs", main_gc.getChilds());
		}
		return mv;
	}
	
	/**
	 * 商品规格编辑
	 * @param request
	 * @param response
	 * @param id
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "商品规格编辑", value = "/goods_spec_edit*", rtype = "seller", rname = "规格管理", rcode = "spec_seller", rgroup = "商品管理")
	@RequestMapping({ "/goods_spec_edit" })
	public ModelAndView edit(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/sellercenter/goods_spec_add.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		if ((id != null) && (!id.equals(""))) {
			GoodsSpecification goodsSpecification = this.goodsSpecService
					.selectByPrimaryKey(Long.valueOf(Long.parseLong(id)));
			if (goodsSpecification != null) {
				if (goodsSpecification.getStore().getUser().getId()
						.equals(user.getId())) {
					mv.addObject("obj", goodsSpecification);
					mv.addObject("currentPage", currentPage);
					mv.addObject("store_id", user.getStore().getId());
					mv.addObject("edit", Boolean.valueOf(true));
					Store store = user.getStore();
					Set<GoodsClass> gcs = this.shopTools.query_store_DetailGc(user.getStore().getGc_detail_info());
					GoodsClass main_gc = this.goodsClassService.selectByPrimaryKey(store.getGc_main_id());
					if (gcs.size() > 0) {
						mv.addObject("gcs", gcs);
					} else {
						mv.addObject("gcs", main_gc.getChilds());
					}
					mv.addObject("gc_details", goodsSpecification.getGoodsclass().getChilds());
				} else {
					mv = new RedPigJModelAndView("error.html",
							this.configService.getSysConfig(),
							this.userConfigService.getUserConfig(), 1, request,
							response);
					mv.addObject("url", CommUtil.getURL(request)
							+ "/goods_spec_list?currentPage="
							+ currentPage);
					mv.addObject("op_title", "您所访问的地址不存在");
				}
			} else {
				mv = new RedPigJModelAndView("error.html",
						this.configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 1, request,
						response);
				mv.addObject("url", CommUtil.getURL(request)
						+ "/goods_spec_list?currentPage=" + currentPage);
				mv.addObject("op_title", "您所访问的地址不存在");
			}
		}
		return mv;
	}	
	
	/**
	 * 商品规保存
	 * @param request
	 * @param response
	 * @param id
	 * @param count
	 * @param currentPage
	 * @param gc_id
	 * @param goodsClass_detail_ids
	 * @return
	 * @throws InterruptedException 
	 */
	@SecurityMapping(title = "商品规保存", value = "/goods_spec_save*", rtype = "seller", rname = "规格管理", rcode = "spec_seller", rgroup = "商品管理")
	@RequestMapping({ "/goods_spec_save" })
	public String goods_spec_save(HttpServletRequest request,
			HttpServletResponse response, String id, String count,
			String currentPage, String gc_id, String goodsClass_detail_ids) throws InterruptedException {
		
		GoodsSpecification goodsSpecification = null;
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		if (id.equals("")) {
			goodsSpecification = (GoodsSpecification) WebForm.toPo(request,GoodsSpecification.class);
			goodsSpecification.setAddTime(new Date());
		} else {
			GoodsSpecification obj = this.goodsSpecService.selectByPrimaryKey(Long.valueOf(Long.parseLong(id)));
			obj.getName();
			goodsSpecification = (GoodsSpecification) WebForm.toPo(request, obj);
		}
		
		goodsSpecification.setSpec_type(1);
		goodsSpecification.setStore(user.getStore());
		
		if ((gc_id != null) && (!gc_id.equals(""))) {
			GoodsClass gc_main = this.goodsClassService.selectByPrimaryKey(CommUtil.null2Long(gc_id));
			gc_main.getId();
			goodsSpecification.setGoodsclass(gc_main);
		}
		
		if ((goodsClass_detail_ids != null) && (!goodsClass_detail_ids.equals(""))) {
			String[] ids = goodsClass_detail_ids.split(",");
			List<GoodsClass> gc_list = Lists.newArrayList();
			
			for (String c_id : ids) {
				
				GoodsClass gc_detail = this.goodsClassService.selectByPrimaryKey(CommUtil.null2Long(c_id));
				if (gc_detail != null) {
					gc_detail.getId();
					gc_list.add(gc_detail);
				}
			}
			if (gc_list.size() > 0) {
				
				goodsSpecification.getSpec_goodsClass_detail().addAll(gc_list);
			}
		} else {
			goodsSpecification.getSpec_goodsClass_detail().removeAll(goodsSpecification.getSpec_goodsClass_detail());
		}
		if (id.equals("")) {
			this.goodsSpecService.saveEntity(goodsSpecification);
			List<Map<String,Object>> gsgcds = Lists.newArrayList();
			
			for (GoodsClass goodsClass : goodsSpecification.getSpec_goodsClass_detail()) {
				Map<String,Object> maps = Maps.newHashMap();
				maps.put("spec_id", goodsSpecification.getId());
				maps.put("spec_gc_id", goodsClass.getId());
				gsgcds.add(maps);
			}
			
			this.redPigGoodsSpecificationService.deleteGoodsSpecificationGoodsClassDetail(gsgcds);
			
			this.redPigGoodsSpecificationService.saveGoodsSpecificationGoodsClassDetail(gsgcds);
		} else {
			this.goodsSpecService.updateById(goodsSpecification);
			
			List<Map<String,Object>> gsgcds = Lists.newArrayList();
			
			for (GoodsClass goodsClass : goodsSpecification.getSpec_goodsClass_detail()) {
				Map<String,Object> maps = Maps.newHashMap();
				maps.put("spec_id", goodsSpecification.getId());
				maps.put("spec_gc_id", goodsClass.getId());
				gsgcds.add(maps);
			}
			
			this.redPigGoodsSpecificationService.deleteGoodsSpecificationGoodsClassDetail(gsgcds);
			
			this.redPigGoodsSpecificationService.saveGoodsSpecificationGoodsClassDetail(gsgcds);
			
		}
		genericProperty(request, goodsSpecification, count);
		request.getSession(false).setAttribute("url",CommUtil.getURL(request) + "/goods_spec_list");
		request.getSession(false).setAttribute("op_title", "规格添加成功");
		return "redirect:/success";
	}

	private void clearProperty(HttpServletRequest request,
			GoodsSpecification spec) {
		for (GoodsSpecProperty property : spec.getProperties()) {
			Accessory img = property.getSpecImage();
			
			RedPigCommonUtil.del_acc(request, img);
			for (Goods goods : property.getGoods_list()) {
				goods.getGoods_specs().remove(property);
				redPigGoodsService.batchDeleteGoodsSpecProperty(goods.getId(), Lists.newArrayList(property));
			}
			
			for (GoodsCart gc : property.getCart_list()) {
				gc.getGsps().remove(property);
				this.redPigGoodsSpecPropertyService.deleteGoodsCartAndGoodsSpecProperty(gc.getId(), Lists.newArrayList(property));
			}
			this.goodsSpecPropertyService.deleteById(property.getId());
		}
	}
	
	@SuppressWarnings("unchecked")
	private void genericProperty(HttpServletRequest request,GoodsSpecification spec, String count) {
		for (int i = 1; i <= CommUtil.null2Int(count); i++) {
			Integer sequence = Integer.valueOf(CommUtil.null2Int(request.getParameter("sequence_" + i)));
			String value = CommUtil.null2String(request.getParameter("value_"+ i));
			if ((sequence != null) && (value != null) && (!value.equals(""))) {
				String id = CommUtil.null2String(request.getParameter("id_" + i));
				GoodsSpecProperty property = null;
				if ((id != null) && (!id.equals(""))) {
					property = this.goodsSpecPropertyService.selectByPrimaryKey(Long.valueOf(Long.parseLong(id)));
				} else {
					property = new GoodsSpecProperty();
				}
				
				property.setAddTime(new Date());
				property.setSequence(sequence.intValue());
				property.setSpec(spec);
				property.setValue(value);
				String uploadFilePath = this.configService.getSysConfig().getUploadFilePath();
				String saveFilePathName = request.getSession().getServletContext().getRealPath("/")
						+ uploadFilePath + File.separator + "spec";
				
				Map<String, Object> map = Maps.newHashMap();
				try {
					String fileName = property.getSpecImage() == null ? "" : property.getSpecImage().getName();
					map = CommUtil.saveFileToServer(request, "specImage_" + i, saveFilePathName, fileName, null);
					if (fileName.equals("")) {
						if (map.get("fileName") != "") {
							Accessory specImage = new Accessory();
							specImage.setName(CommUtil.null2String(map
									.get("fileName")));
							specImage.setExt(CommUtil.null2String(map
									.get("mime")));
							specImage.setSize(BigDecimal.valueOf(CommUtil
									.null2Double(map.get("fileSize"))));
							specImage.setPath(uploadFilePath + "/spec");
							specImage.setWidth(CommUtil.null2Int(map
									.get("width")));
							specImage.setHeight(CommUtil.null2Int(map
									.get("height")));
							specImage.setAddTime(new Date());
							this.accessoryService.saveEntity(specImage);
							property.setSpecImage(specImage);
						}
					} else if (map.get("fileName") != "") {
						Accessory specImage = property.getSpecImage();
						specImage.setName(CommUtil.null2String(map
								.get("fileName")));
						specImage.setExt(CommUtil.null2String(map.get("mime")));
						specImage.setSize(BigDecimal.valueOf(CommUtil
								.null2Double(map.get("fileSize"))));
						specImage.setPath(uploadFilePath + "/spec");
						specImage.setWidth(CommUtil.null2Int(map.get("width")));
						specImage
								.setHeight(CommUtil.null2Int(map.get("height")));
						specImage.setAddTime(new Date());
						this.accessoryService.updateById(specImage);
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
				if (id.equals("")) {
					this.goodsSpecPropertyService.saveEntity(property);
				} else {
					this.goodsSpecPropertyService.updateById(property);
				}
			}
		}
	}
	
	/**
	 * 商品规格删除
	 * @param request
	 * @param mulitId
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "商品规格删除", value = "/goods_spec_del*", rtype = "seller", rname = "规格管理", rcode = "spec_seller", rgroup = "商品管理")
	@RequestMapping({ "/goods_spec_del" })
	public String deleteById(HttpServletRequest request, String mulitId,
			String currentPage) {
		if (mulitId == null) {
			mulitId = "";
		}
		String[] ids = mulitId.split(",");
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();

		for (String id : ids) {

			if (!id.equals("")) {
				GoodsSpecification obj = this.goodsSpecService.selectByPrimaryKey(Long
						.valueOf(Long.parseLong(id)));
				if ((obj != null)
						&& (obj.getStore().getUser().getId().equals(user
								.getId()))) {
					clearProperty(request, obj);
					this.goodsSpecService.deleteById(Long.valueOf(Long
							.parseLong(id)));
				}
			}
		}
		return "redirect:goods_spec_list?currentPage=" + currentPage;
	}
	
	/**
	 * 商品规格Ajax删除
	 * @param request
	 * @param response
	 * @param id
	 */
	@SecurityMapping(title = "商品规格Ajax删除", value = "/goods_property_delete*", rtype = "seller", rname = "规格管理", rcode = "spec_seller", rgroup = "商品管理")
	@RequestMapping({ "/goods_property_delete" })
	public void goods_property_deleteById(HttpServletRequest request,
			HttpServletResponse response, String id) {
		boolean ret = true;
		if ((id != null) && (!id.equals(""))) {
			this.databaseTools
					.execute("delete from redpigmall_goods_spec where spec_id="
							+

							id);
			this.databaseTools
					.execute("delete from redpigmall_cart_gsp where gsp_id=" +

					id);
			GoodsSpecProperty property = this.goodsSpecPropertyService
					.selectByPrimaryKey(Long.valueOf(Long.parseLong(id)));
			property.setSpec(null);
			Accessory img = property.getSpecImage();
			
			RedPigCommonUtil.del_acc(request, img);
			
			property.setSpecImage(null);
			
			this.goodsSpecPropertyService.deleteById(property.getId());
			ret = true;
		}
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
	 * 规格名称验证
	 * @param request
	 * @param response
	 * @param name
	 * @param id
	 * @param store_id
	 */
	@SecurityMapping(title = "规格名称验证", value = "/goods_spec_verify*", rtype = "seller", rname = "规格管理", rcode = "spec_seller", rgroup = "商品管理")
	@RequestMapping({ "/goods_spec_verify" })
	public void goods_spec_verify(HttpServletRequest request,
			HttpServletResponse response, String name, String id,
			String store_id) {
		boolean ret = true;
		Map<String, Object> params = Maps.newHashMap();
		params.put("name", name);
		params.put("store_id", CommUtil.null2Long(store_id));
		params.put("id_no", CommUtil.null2Long(id));
		List<GoodsSpecification> gss = this.goodsSpecService.queryPageList(params);
		
		if ((gss != null) && (gss.size() > 0)) {
			ret = false;
		}
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
	 * 规格新增分类加载
	 * @param request
	 * @param response
	 * @param gc_id
	 * @param id
	 * @return
	 */
	@SecurityMapping(title = "规格新增分类加载", value = "/spec_gc_load*", rtype = "seller", rname = "规格管理", rcode = "spec_seller", rgroup = "商品管理")
	@RequestMapping({ "/spec_gc_load" })
	public ModelAndView spec_gc_load(HttpServletRequest request,
			HttpServletResponse response, String gc_id, String id) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/sellercenter/spec_gc_load.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		if ((gc_id != null) && (!gc_id.equals(""))) {
			GoodsClass gc = this.goodsClassService.selectByPrimaryKey(CommUtil
					.null2Long(gc_id));
			mv.addObject("gcs", gc.getChilds());
		}
		if ((id != null) && (!id.equals(""))) {
			GoodsSpecification obj = this.goodsSpecService.selectByPrimaryKey(CommUtil
					.null2Long(id));
			mv.addObject("obj", obj);
		}
		return mv;
	}
	
	/**
	 * 商品规格AJAX添加
	 * @param request
	 * @param response
	 * @param id
	 * @param name
	 * @throws ClassNotFoundException
	 */
	@SuppressWarnings("unchecked")
	@SecurityMapping(title = "商品规格AJAX添加", value = "/goods_spec_ajax_add*", rtype = "seller", rname = "规格管理", rcode = "spec_seller", rgroup = "商品管理")
	@RequestMapping({ "/goods_spec_ajax_add" })
	public void goods_spec_ajax_add(HttpServletRequest request,
			HttpServletResponse response, String id, String name)
			throws ClassNotFoundException {
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder
				.getCurrentUser().getId());
		Map<String,Object> jsonmap = Maps.newHashMap();
		
		SysConfig config = this.configService.getSysConfig();
		String path = "";
		String uploadFilePath = config.getUploadFilePath();

		GoodsSpecification obj = this.goodsSpecService.selectByPrimaryKey(Long
				.valueOf(Long.parseLong(id)));
		GoodsSpecProperty property = new GoodsSpecProperty();
		property.setAddTime(new Date());
		property.setSequence(obj.getProperties().size() + 1);
		property.setSpec(obj);
		property.setValue(name);
		if (obj.getType().equals("img")) {
			try {
				String filePath = request.getSession().getServletContext()
						.getRealPath("/")
						+ uploadFilePath + File.separator + "spec";
				CommUtil.createFolder(filePath);
				path = uploadFilePath + "/spec";
				Map<String, Object> map = CommUtil.saveFileToServer(request,
						"gsp_add_img_" + id, filePath, null, null);
				Accessory image = new Accessory();
				image.setUser(user);
				image.setAddTime(new Date());
				image.setExt((String) map.get("mime"));
				image.setPath(path);
				image.setWidth(CommUtil.null2Int(map.get("width")));
				image.setHeight(CommUtil.null2Int(map.get("height")));
				image.setName(CommUtil.null2String(map.get("fileName")));
				image.setInfo("");
				this.accessoryService.saveEntity(image);
				property.setSpecImage(image);
				jsonmap.put("url", CommUtil.getURL(request) + "/" + path + "/"
						+ image.getName());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		this.goodsSpecPropertyService.saveEntity(property);
		
		jsonmap.put("id", property.getId());
		jsonmap.put("name", property.getValue());
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		try {
			PrintWriter writer = response.getWriter();
			writer.print(JSON.toJSONString(jsonmap));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 商品规格大类AJAX添加
	 * @param request
	 * @param response
	 * @param gc_id
	 * @param name
	 * @param sequence
	 * @param type
	 * @throws ClassNotFoundException
	 */
	@SecurityMapping(title = "商品规格大类AJAX添加", value = "/goods_specification_ajax_add*", rtype = "seller", rname = "规格管理", rcode = "spec_seller", rgroup = "商品管理")
	@RequestMapping({ "/goods_specification_ajax_add" })
	public void goods_specification_ajax_add(HttpServletRequest request,
			HttpServletResponse response, String gc_id, String name,
			String sequence, String type) throws ClassNotFoundException {
		Map<String,Object> jsonmap = Maps.newHashMap();
		
		GoodsSpecification goodsSpecification = new GoodsSpecification();
		goodsSpecification.setAddTime(new Date());
		goodsSpecification.setSpec_type(1);
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		goodsSpecification.setStore(user.getStore());
		goodsSpecification.setSequence(CommUtil.null2Int(sequence));
		goodsSpecification.setType(type);
		goodsSpecification.setName(name);
		GoodsClass gc = this.goodsclassService.selectByPrimaryKey(CommUtil
				.null2Long(gc_id));
		goodsSpecification.getSpec_goodsClass_detail().removeAll(
				goodsSpecification.getSpec_goodsClass_detail());
		if ((gc != null) && (gc.getLevel() == 2)) {
			goodsSpecification.setGoodsclass(gc.getParent());
			List<GoodsClass> list = Lists.newArrayList();
			list.add(gc);
			goodsSpecification.setSpec_goodsClass_detail(list);
		} else {
			goodsSpecification.setGoodsclass(gc);
		}
		this.goodsSpecService.saveEntity(goodsSpecification);
		jsonmap.put("id", goodsSpecification.getId());
		jsonmap.put("name", name);
		jsonmap.put("sequence",Integer.valueOf(goodsSpecification.getSequence()));
		jsonmap.put("type", goodsSpecification.getType());
		
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		try {
			PrintWriter writer = response.getWriter();
			writer.print(JSON.toJSONString(jsonmap));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
