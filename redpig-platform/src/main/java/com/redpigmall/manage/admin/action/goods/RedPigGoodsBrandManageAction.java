package com.redpigmall.manage.admin.action.goods;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.math.BigDecimal;
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
import com.redpigmall.api.tools.CommUtil;
import com.redpigmall.api.tools.ObjectUtils;
import com.redpigmall.api.tools.WebForm;
import com.redpigmall.manage.admin.action.base.BaseAction;
import com.redpigmall.domain.Accessory;
import com.redpigmall.domain.GoodsBrand;
import com.redpigmall.domain.GoodsBrandCategory;
import com.redpigmall.domain.GoodsType;

/**
 * 
 * <p>
 * Title: RedPigGoodsBrandManageAction.java
 * </p>
 * 
 * <p>
 * Description: 系统品牌管理控制器，用来添加系统品牌及审核商家提交的品牌信息
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
 * @date 2016年5月27日
 * 
 * @version redpigmall_b2b2c 8.0
 */
@SuppressWarnings("unchecked")
@Controller
public class RedPigGoodsBrandManageAction extends BaseAction{
	/**
	 * 
	 * @param request
	 * @param response
	 * @param name
	 * @param id
	 */
	@RequestMapping({ "/goods_brand_verify" })
	public void goods_brand_verify(HttpServletRequest request,
			HttpServletResponse response, String name, String id) {
		boolean ret = true;
		Map<String, Object> params = Maps.newHashMap();
		params.put("name", name);
		params.put("redPig_id", CommUtil.null2Long(id));
		List<GoodsBrand> gcs = this.redPigGoodsBrandService.queryPageList(params);
		
		if ((gcs != null) && (gcs.size() > 0)) {
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
	 * 商品品牌AJAX更新
	 * @param request
	 * @param response
	 * @param id
	 * @param fieldName
	 * @param value
	 * @throws ClassNotFoundException
	 */
	@SecurityMapping(title = "商品品牌AJAX更新", value = "/goods_brand_ajax*", rtype = "admin", rname = "品牌管理", rcode = "goods_brand", rgroup = "商品")
	@RequestMapping({ "/goods_brand_ajax" })
	public void goods_brand_ajax(HttpServletRequest request,
			HttpServletResponse response, String id, String fieldName,
			String value) throws ClassNotFoundException {
		GoodsBrand obj = this.redPigGoodsBrandService.selectByPrimaryKey(Long.parseLong(id));
		
		Field[] fields = GoodsBrand.class.getDeclaredFields();
		
		Object val = ObjectUtils.setValue(fieldName, value, obj, fields);
		
		this.redPigGoodsBrandService.update(obj);
		
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
	 * 商品品牌删除
	 * @param request
	 * @param mulitId
	 * @param audit
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "商品品牌删除", value = "/goods_brand_del*", rtype = "admin", rname = "品牌管理", rcode = "goods_brand", rgroup = "商品")
	@RequestMapping({ "/goods_brand_del" })
	public String goods_brand_del(HttpServletRequest request, String mulitId,
			String audit, String currentPage) {
		if (mulitId == null) {
			mulitId = "";
		}
		String[] ids = mulitId.split(",");
		for (String id : ids) {
			if (!id.equals("")) {
				GoodsBrand brand = this.redPigGoodsBrandService.selectByPrimaryKey(Long.parseLong(id));
				Map<String, Object> params = Maps.newHashMap();
				params.put("goods_brand_id", brand.getId());
				//将品牌被商品使用的关联关系去掉
				this.redPigGoodsService.removeGoodsBrandByGoodsBrandId(params);
				
				//将品牌和商品类型关联关系(此处为多对多关系,删除中间表数据)去掉
				List<Map<String,Long>> gbtIds = Lists.newArrayList();
				
				for (GoodsType type : brand.getTypes()) {
					Map<String,Long> maps = Maps.newHashMap();
					maps.put("brand_id", brand.getId());
					maps.put("type_id", type.getId());
					gbtIds.add(maps);
				}
				
				this.redPigGoodsBrandService.deleteGoodsBrandAndGoodsType(gbtIds);
				
				this.redPigGoodsBrandService.delete(Long.parseLong(id));
				
			}
		}
		String returnUrl = "redirect:goods_brand_list?currentPage=" + currentPage;
		if ((audit != null) && (!audit.equals(""))) {
			returnUrl = "redirect:goods_brand_audit?currentPage=" + currentPage;
		}
		return returnUrl;
	}
	
	
	/**
	 * 商品品牌保存
	 * @param request
	 * @param response
	 * @param id
	 * @param cmd
	 * @param cat_name
	 * @param list_url
	 * @param add_url
	 * @param gc_id
	 * @return
	 */
	@SecurityMapping(title = "商品品牌保存", value = "/goods_band_save*", rtype = "admin", rname = "品牌管理", rcode = "goods_brand", rgroup = "商品")
	@RequestMapping({ "/goods_band_save" })
	public ModelAndView goods_band_save(
			HttpServletRequest request,
			HttpServletResponse response, 
			String id, 
			String cmd,
			String cat_name, 
			String list_url, 
			String add_url, 
			String gc_id) 
	{
	
		GoodsBrand goodsBrand = null;
		if (id.equals("")) {
			goodsBrand = (GoodsBrand) WebForm.toPo(request, GoodsBrand.class);
			goodsBrand.setAddTime(new Date());
			goodsBrand.setAudit(1);
			goodsBrand.setUserStatus(0);
		} else {
			GoodsBrand obj = this.redPigGoodsBrandService.selectByPrimaryKey(Long.parseLong(id));
			
			goodsBrand = (GoodsBrand) WebForm.toPo(request, obj);
		}
		Map<String,Object> params = Maps.newHashMap();
		params.put("name", cat_name);
		
		List<GoodsBrandCategory> cats = this.redPigGoodsBrandCategoryService.queryPageList(params);
				
		if (cats == null || cats.size() == 0) {
			GoodsBrandCategory cat = new GoodsBrandCategory();
			cat.setAddTime(new Date());
			cat.setName(cat_name);
			this.redPigGoodsBrandCategoryService.save(cat);
			goodsBrand.setCategory(cat);
		} else {
			goodsBrand.setCategory(cats.get(0));
		}
		String uploadFilePath = this.redPigSysConfigService.getSysConfig().getUploadFilePath();
		
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
					this.redPigAccessoryService.save(photo);
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
				this.redPigAccessoryService.update(photo);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		goodsBrand.setShow_index(true);
		if (id.equals("")) {
			this.redPigGoodsBrandService.saveEntity(goodsBrand);
		} else {
			this.redPigGoodsBrandService.updateById(goodsBrand);
		}
		ModelAndView mv = new RedPigJModelAndView("admin/blue/success.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request, response);
		
		mv.addObject("list_url", list_url);
		mv.addObject("op_title", "保存品牌成功");
		if (add_url != null) {
			mv.addObject("add_url", add_url);
		}
		return mv;
	}
	
	
	/**
	 * 商品品牌编辑
	 * @param request
	 * @param response
	 * @param id
	 * @return
	 */
	@SecurityMapping(title = "商品品牌编辑", value = "/goods_brand_edit*", rtype = "admin", rname = "品牌管理", rcode = "goods_brand", rgroup = "商品")
	@RequestMapping({ "/goods_brand_edit" })
	public ModelAndView goods_brand_edit(
			HttpServletRequest request,
			HttpServletResponse response, 
			String id) {

		ModelAndView mv = new RedPigJModelAndView("admin/blue/goods_brand_add.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request, response);
		
		if ((id != null) && (!id.equals(""))) {
			GoodsBrand goodsBrand = this.redPigGoodsBrandService.selectByPrimaryKey(Long.parseLong(id));
			
			mv.addObject("obj", goodsBrand);
		}
		
		Map<String,Object> params = Maps.newHashMap();
		
		List<GoodsBrandCategory> categorys = this.redPigGoodsBrandCategoryService.queryPageList(params);
		
		mv.addObject("categorys", categorys);
		mv.addObject("edit", Boolean.valueOf(true));
		return mv;
	}
	
	/**
	 * 商品品牌添加
	 * @param request
	 * @param response
	 * @return
	 */
	@SecurityMapping(title = "商品品牌添加", value = "/goods_brand_add*", rtype = "admin", rname = "品牌管理", rcode = "goods_brand", rgroup = "商品")
	@RequestMapping({ "/goods_brand_add" })
	public ModelAndView goods_brand_add(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new RedPigJModelAndView("admin/blue/goods_brand_add.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request, response);
		Map<String,Object> params = Maps.newHashMap();
		
		List<GoodsBrandCategory> categorys = this.redPigGoodsBrandCategoryService.queryPageList(params);
		
		mv.addObject("categorys", categorys);
		return mv;
	}
	
	/**
	 * 商品品牌审核拒绝
	 * @param request
	 * @param id
	 * @return
	 */
	@SecurityMapping(title = "商品品牌审核拒绝", value = "/goods_brands_refuse*", rtype = "admin", rname = "品牌管理", rcode = "goods_brand", rgroup = "商品")
	@RequestMapping({ "/goods_brands_refuse" })
	public String goods_brands_refuse(HttpServletRequest request, String id) {
		if (!id.equals("")) {
			GoodsBrand goodsBrand = this.redPigGoodsBrandService.selectByPrimaryKey(Long.parseLong(id));
			goodsBrand.setAudit(-1);
			this.redPigGoodsBrandService.update(goodsBrand);
		}
		return "redirect:goods_brand_audit";
	}
	
	/**
	 * 商品品牌审核通过
	 * @param request
	 * @param id
	 * @return
	 */
	@SecurityMapping(title = "商品品牌审核通过", value = "/goods_brands_pass*", rtype = "admin", rname = "品牌管理", rcode = "goods_brand", rgroup = "商品")
	@RequestMapping({ "/goods_brands_pass" })
	public String goods_brands_pass(HttpServletRequest request, String id) {
		if (!id.equals("")) {
			GoodsBrand goodsBrand = this.redPigGoodsBrandService.selectByPrimaryKey(Long.parseLong(id));
			goodsBrand.setAudit(1);
			this.redPigGoodsBrandService.update(goodsBrand);
		}
		return "redirect:goods_brand_audit";
	}
	
	/**
	 * 商品品牌待审核列表
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @param name
	 * @param category
	 * @return
	 */
	@SecurityMapping(title = "商品品牌待审核列表", value = "/goods_brand_audit*", rtype = "admin", rname = "品牌管理", rcode = "goods_brand", rgroup = "商品")
	@RequestMapping({ "/goods_brand_audit" })
	public ModelAndView goods_brand_audit(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,
			String orderType, String name, String category) {
		ModelAndView mv = new RedPigJModelAndView(
				"admin/blue/goods_brand_audit.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request, response);
		
		Map<String,Object> maps = this.redPigQueryTools.getParams(currentPage, orderBy, orderType);
		maps.put("audit", Integer.valueOf(0));
		
		maps.put("userStatus", Integer.valueOf(1));
		
		if (!CommUtil.null2String(name).equals("")) {
			
			maps.put("redPig_name", name.trim());
			
		}
		if (!CommUtil.null2String(category).equals("")) {
			
			maps.put("redPig_category_name", name.trim());
			
		}
		
		IPageList pList = this.redPigGoodsBrandService.list(maps);
		
		CommUtil.saveIPageList2ModelAndView("/goods_brand_audit.html",
				"", "", pList, mv);
		mv.addObject("name", name);
		mv.addObject("category", category);
		return mv;
	}
	
	/**
	 * 商品品牌列表
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @param name
	 * @param category
	 * @return
	 */
	@SecurityMapping(title = "商品品牌列表", value = "/goods_brand_list*", rtype = "admin", rname = "品牌管理", rcode = "goods_brand", rgroup = "商品")
	@RequestMapping({ "/goods_brand_list" })
	public ModelAndView goods_brand_list(
			HttpServletRequest request,
			HttpServletResponse response, 
			String currentPage, 
			String orderBy,
			String orderType, 
			String name, 
			String category) {
		
		ModelAndView mv = new RedPigJModelAndView("admin/blue/goods_brand_list.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request, response);
		
		Map<String,Object> maps = this.redPigQueryTools.getParams(currentPage, "sequence", "asc");
		maps.put("audit", Integer.valueOf(1));
		
		if (!CommUtil.null2String(name).equals("")) {
			maps.put("name_like", name.trim());
		}
		
		if (!CommUtil.null2String(category).equals("")) {
			
			maps.put("category_name_like", category.trim());
			
		}
		
		IPageList pList = this.redPigGoodsBrandService.list(maps);
		
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		mv.addObject("name", name);
		mv.addObject("category", category);
		return mv;
	}
}
