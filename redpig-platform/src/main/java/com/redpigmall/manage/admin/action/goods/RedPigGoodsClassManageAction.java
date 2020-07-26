package com.redpigmall.manage.admin.action.goods;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
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
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.api.tools.CommUtil;
import com.redpigmall.api.tools.ObjectUtils;
import com.redpigmall.api.tools.WebForm;
import com.redpigmall.manage.admin.action.base.BaseAction;
import com.redpigmall.domain.Accessory;
import com.redpigmall.domain.AdvertPosition;
import com.redpigmall.domain.Goods;
import com.redpigmall.domain.GoodsBrand;
import com.redpigmall.domain.GoodsClass;
import com.redpigmall.domain.GoodsSpecification;
import com.redpigmall.domain.GoodsType;
import com.redpigmall.domain.ShowClass;

/**
 * 
 * <p>
 * Title: RedPigGoodsClassManageAction.java
 * </p>
 * 
 * <p>
 * Description: 商城商品分类管理控制器，用来管理商城商品分类信息，商城默认支持三级商品分类，遵循京东、一号店等B2B2C商城的商品分类模式
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
 * @date 2016-4-25
 * 
 * @version redpigmall_b2b2c v8.0 2016版
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
@Controller
public class RedPigGoodsClassManageAction extends BaseAction{
	
	/**
	 * 
	 * @param request
	 * @param response
	 * @param currentPage
	 * @return
	 */
	@RequestMapping({ "/goodsclass_loading_asyn" })
	public ModelAndView channel_floor_gc(HttpServletRequest request,
			HttpServletResponse response, String currentPage) {
		ModelAndView mv = new RedPigJModelAndView(
				"admin/blue/goodsclass_loading_asyn.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request, response);
		Map<String,Object> params = Maps.newHashMap();
		params.put("parent", -1);
		params.put("orderBy", "sequence");
		params.put("orderType", "desc");
		
		List gcs = this.redPigGoodsClassService.queryPageList(params);
		
		mv.addObject("gcs", gcs);
		return mv;
	}
	
	/**
	 * 
	 * @param request
	 * @param response
	 * @param currentPage
	 * @return
	 */
	@RequestMapping({ "/redpig_goodsclass_loading_asyn" })
	public ModelAndView redpig_channel_floor_gc(HttpServletRequest request,
			HttpServletResponse response, String currentPage) {
		ModelAndView mv = new RedPigJModelAndView(
				"admin/blue/redpig_goodsclass_loading_asyn.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request, response);
		Map<String,Object> params = Maps.newHashMap();
		params.put("parent", -1);
		params.put("orderBy", "sequence");
		params.put("orderType", "desc");
		
		List gcs = this.redPigGoodsClassService.queryPageList(params);
		
		mv.addObject("gcs", gcs);
		return mv;
	}
	
	/**
	 * 
	 * @param request
	 * @param response
	 * @param pid
	 */
	@RequestMapping({ "/showClass_load" })
	public void showClass_load(HttpServletRequest request,
			HttpServletResponse response, String pid) {
		Map<String, Object> params = Maps.newHashMap();
		params.put("parent", CommUtil.null2Long(pid));
		List<ShowClass> scs = this.redPigShowClassService.queryPageList(params);
		
		List<Map> list = Lists.newArrayList();
		for (ShowClass sc : scs) {
			Map<String, Object> map = Maps.newHashMap();
			map.put("id", sc.getId());
			map.put("showName", sc.getShowName());
			list.add(map);
		}
		String temp = JSON.toJSONString(list);
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		try {
			PrintWriter writer = response.getWriter();
			writer.print(temp);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 通过类型id查询相应品牌
	 * @param request
	 * @param response
	 * @param type_id
	 */
	@SecurityMapping(title = "通过类型id查询相应品牌", value = "/goods_goodsType_brand*", rtype = "admin", rname = "分类管理", rcode = "goods_class", rgroup = "商品")
	@RequestMapping({ "/goods_goodsType_brand" })
	public void goods_goodsType_brand(HttpServletRequest request,
			HttpServletResponse response, String type_id) {
		GoodsType gt = this.redPigGoodsTypeService.selectByPrimaryKey(CommUtil.null2Long(type_id));
		
		List<Map> list = Lists.newArrayList();
		if (gt != null) {
			for (GoodsBrand gb : gt.getGbs()) {
				Map<String, Object> map = Maps.newHashMap();
				map.put("id", gb.getId());
				map.put("name", gb.getName());
				list.add(map);
			}
		}
		String temp = JSON.toJSONString(list);
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		try {
			PrintWriter writer = response.getWriter();
			writer.print(temp);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 
	 * @param request
	 * @param response
	 * @param className
	 * @param id
	 * @param pid
	 */
	@RequestMapping({ "/goods_class_verify" })
	public void goods_class_verify(HttpServletRequest request,
			HttpServletResponse response, String className, String id,
			String pid) {
		boolean ret = true;
		Map<String, Object> params = Maps.newHashMap();
		params.put("className", className);
		params.put("id", CommUtil.null2Long(id));
		params.put("parent", CommUtil.null2Long(pid));
		List<GoodsClass> gcs = this.redPigGoodsClassService.queryPageList(params);
		
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
	 * 商品分类批量删除
	 * @param request
	 * @param mulitId
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "商品分类批量删除", value = "/goods_class_del*", rtype = "admin", rname = "分类管理", rcode = "goods_class", rgroup = "商品")
	@RequestMapping({ "/goods_class_del" })
	public String goods_class_del(HttpServletRequest request, String mulitId,
			String currentPage) {
		String[] ids = mulitId.split(",");
		for (String id : ids) {
			if (!id.equals("")) {
				Set<Long> list = genericIds(this.redPigGoodsClassService.selectByPrimaryKey(Long.parseLong(id)));
				
				Map<String, Object> params = Maps.newHashMap();
				params.put("gc_ids", list);
				
				List<Goods> goods_list = this.redPigGoodsService.queryGoodsByGoodsClassIds(params);
				
				if (goods_list.size() == 0) {
					List<GoodsClass> gcs = this.redPigGoodsClassService.queryGoodsClassByIds(params);
					
					for (GoodsClass gc : gcs) {
						GoodsType type = gc.getGoodsType();
						//将GoodsClass和GoodsType关系解除:GoodsClass表中的goodsType_id字段设置为空即可
						if (type != null) {
							Map<String,Object> maps = Maps.newHashMap();
							maps.put("goods_id", gc.getId());
							
							this.redPigGoodsClassService.removeGoodsType(maps);
						}
						for (GoodsSpecification gsp : gc.getSpec_detail()) {
							Map<String,Object> maps = Maps.newHashMap();
							maps.put("spec_gc_id", gc.getId());
							maps.put("spec_id", gsp.getId());
							
							this.redPigGoodsClassService.removeGoodsSpecification(maps);
						}
						//将子类删掉
						gc.setChilds(null);
						
						Map<String,Object> maps = Maps.newHashMap();
						maps.put("goods_id", gc.getId());
						
						this.redPigGoodsClassService.removeClilds(maps);
						
						this.redPigGoodsClassService.delete(gc.getId());
					}
				}
			}
		}
		return "redirect:goods_class_list?currentPage=" + currentPage;
	}
	
	
	/**
	 * 商品分类批量推荐
	 * @param request
	 * @param mulitId
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "商品分类批量推荐", value = "/goods_class_recommend*", rtype = "admin", rname = "分类管理", rcode = "goods_class", rgroup = "商品")
	@RequestMapping({ "/goods_class_recommend" })
	public String goods_class_recommend(HttpServletRequest request,
			String mulitId, String currentPage) {
		if (mulitId == null) {
			mulitId = "";
		}
		String[] ids = mulitId.split(",");
		for (String id : ids) {
			if (!id.equals("")) {
				GoodsClass gc = this.redPigGoodsClassService.selectByPrimaryKey(Long.valueOf(Long.parseLong(id)));
				
				gc.setRecommend(true);
				this.redPigGoodsClassService.update(gc);
			}
		}
		return "redirect:goods_class_list?currentPage=" + currentPage;
	}
	
	/**
	 * 商品分类Ajax更新
	 * @param request
	 * @param response
	 * @param id
	 * @param fieldName
	 * @param value
	 * @throws ClassNotFoundException
	 */
	@SecurityMapping(title = "商品分类Ajax更新", value = "/goods_class_ajax*", rtype = "admin", rname = "分类管理", rcode = "goods_class", rgroup = "商品")
	@RequestMapping({ "/goods_class_ajax" })
	public void goods_class_ajax(HttpServletRequest request,
			HttpServletResponse response, String id, String fieldName,
			String value) throws ClassNotFoundException {
		GoodsClass gc = this.redPigGoodsClassService.selectByPrimaryKey(Long.parseLong(id));
		
		Field[] fields = GoodsClass.class.getDeclaredFields();
		
		Object val = ObjectUtils.setValue(fieldName, value, gc, fields);
		
		this.redPigGoodsClassService.update(gc);
		
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
	 * 商品分类下级加载
	 * @param request
	 * @param response
	 * @param pid
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "商品分类下级加载", value = "/goods_class_data*", rtype = "admin", rname = "分类管理", rcode = "goods_class", rgroup = "商品")
	@RequestMapping({ "/goods_class_data" })
	public ModelAndView goods_class_data(
			HttpServletRequest request,
			HttpServletResponse response, 
			String pid, 
			String currentPage) 
	{

		ModelAndView mv = new RedPigJModelAndView("admin/blue/goods_class_data.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request, response);
		
		Map<String, Object> map = Maps.newHashMap();
		map.put("parent_id", Long.valueOf(Long.parseLong(pid)));
		map.put("orderBy", "sequence");
		map.put("orderType", "asc");
		List<GoodsClass> gcs = this.redPigGoodsClassService.queryPageList(map);
		
		mv.addObject("gcs", gcs);
		mv.addObject("currentPage", currentPage);
		mv.addObject("showClassTools", this.redPigShowClassTools);
		return mv;
	}
	
	/**
	 * 商品分类保存
	 * @param request
	 * @param response
	 * @param id
	 * @param pid
	 * @param goodsTypeId
	 * @param currentPage
	 * @param list_url
	 * @param add_url
	 * @param child_link
	 * @param commission_link
	 * @param commission_rate
	 * @param guarantee
	 * @param guarantee_link
	 * @param adv_type
	 * @param ap_id
	 * @param gc_color
	 * @param gbs_ids
	 * @param gc_adv_url
	 * @param showClass_id
	 * @return
	 */
	@SecurityMapping(title = "商品分类保存", value = "/goods_class_save*", rtype = "admin", rname = "分类管理", rcode = "goods_class", rgroup = "商品")
	@RequestMapping({ "/goods_class_save" })
	public ModelAndView goods_class_save(
			HttpServletRequest request,
			HttpServletResponse response, String id, String pid,
			String goodsTypeId, String currentPage, String list_url,
			String add_url, String child_link, String commission_link,
			String commission_rate, String guarantee, String guarantee_link,
			String adv_type, String ap_id, String gc_color, String gbs_ids,
			String gc_adv_url, String showClass_id) 
	{

		GoodsClass goodsClass = null;
		if (id.equals("")) {
			goodsClass = (GoodsClass) WebForm.toPo(request, GoodsClass.class);
			goodsClass.setAddTime(new Date());
		} else {
			GoodsClass obj = this.redPigGoodsClassService.selectByPrimaryKey(Long.parseLong(id));
			
			goodsClass = (GoodsClass) WebForm.toPo(request, obj);
		}
		GoodsClass parent = this.redPigGoodsClassService.selectByPrimaryKey(CommUtil.null2Long(pid));
		
		if (parent != null) {
			goodsClass.setParent(parent);
			goodsClass.setLevel(parent.getLevel() + 1);
		}
		GoodsType goodsType = this.redPigGoodsTypeService.selectByPrimaryKey(CommUtil.null2Long(goodsTypeId));
		
		goodsClass.setGoodsType(goodsType);
		Set<Long> ids = genericIds(goodsClass);
		if ((showClass_id != null) && (!"".equals(showClass_id))) {
			goodsClass.setShowClass_id(CommUtil.null2Long(showClass_id));
		}
		
		if (CommUtil.null2Boolean(child_link)) {
			for (Long gc_id : ids) {
				if (gc_id != null) {
					GoodsClass gc = this.redPigGoodsClassService.selectByPrimaryKey(gc_id);
					
					gc.setGoodsType(goodsType);
					this.redPigGoodsClassService.update(gc);
				}
			}
		}
		
		if (CommUtil.null2Boolean(commission_link)) {
			for (Long gc_id : ids) {
				if (gc_id != null) {
					GoodsClass gc = this.redPigGoodsClassService.selectByPrimaryKey(gc_id);
					
					gc.setCommission_rate(new BigDecimal(commission_rate));
					this.redPigGoodsClassService.update(gc);
				}
			}
		}
		if (CommUtil.null2Boolean(guarantee_link)) {
			for (Long gc_id : ids) {
				if (gc_id != null) {
					GoodsClass gc = this.redPigGoodsClassService.selectByPrimaryKey(gc_id);
					gc.setGuarantee(new BigDecimal(guarantee));
					this.redPigGoodsClassService.update(gc);
				}
			}
		}
		String uploadFilePath = this.redPigSysConfigService.getSysConfig().getUploadFilePath();
		
		String saveFilePathName = request.getSession().getServletContext()
				.getRealPath("/")
				+ uploadFilePath + File.separator + "class_icon";
		Map<String, Object> map = Maps.newHashMap();
		try {
			String fileName = goodsClass.getIcon_acc() == null ? ""
					: goodsClass.getIcon_acc().getName();
			map = CommUtil.saveFileToServer(request, "icon_acc",
					saveFilePathName, fileName, null);
			if (fileName.equals("")) {
				if (map.get("fileName") != "") {
					Accessory photo = new Accessory();
					photo.setName(CommUtil.null2String(map.get("fileName")));
					photo.setExt(CommUtil.null2String(map.get("mime")));
					photo.setSize(BigDecimal.valueOf(CommUtil.null2Double(map
							.get("fileSize"))));
					photo.setPath(uploadFilePath + "/class_icon");
					photo.setWidth(CommUtil.null2Int(map.get("width")));
					photo.setHeight(CommUtil.null2Int(map.get("height")));
					photo.setAddTime(new Date());
					this.redPigAccessoryService.save(photo);
					goodsClass.setIcon_acc(photo);
				}
			} else if (map.get("fileName") != "") {
				Accessory photo = goodsClass.getIcon_acc();
				photo.setName(CommUtil.null2String(map.get("fileName")));
				photo.setExt(CommUtil.null2String(map.get("mime")));
				photo.setSize(BigDecimal.valueOf(CommUtil.null2Double(map
						.get("fileSize"))));
				photo.setPath(uploadFilePath + "/class_icon");
				photo.setWidth(CommUtil.null2Int(map.get("width")));
				photo.setHeight(CommUtil.null2Int(map.get("height")));
				photo.setAddTime(new Date());
				this.redPigAccessoryService.update(photo);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		Map map2 = Maps.newHashMap();
		try {
			String fileName = "";
			map2 = CommUtil.saveFileToServer(request, "gc_adv",
					saveFilePathName, fileName, null);
			if (fileName.equals("")) {
				if (map2.get("fileName") != "") {
					Accessory photo = new Accessory();
					photo.setName(CommUtil.null2String(map2.get("fileName")));
					photo.setExt(CommUtil.null2String(map2.get("mime")));
					photo.setSize(BigDecimal.valueOf(CommUtil.null2Double(map2
							.get("fileSize"))));
					photo.setPath(uploadFilePath + "/class_icon");
					photo.setWidth(CommUtil.null2Int(map2.get("width")));
					photo.setHeight(CommUtil.null2Int(map2.get("height")));
					photo.setAddTime(new Date());
					this.redPigAccessoryService.save(photo);
					Map map_temp = Maps.newHashMap();
					map_temp.put("acc_id", photo.getId());
					map_temp.put("acc_url", gc_adv_url);
					goodsClass.setGc_advert(JSON.toJSONString(map_temp));
				} else {
					String gc_ad = goodsClass.getGc_advert();
					if ((gc_ad != null) && (!"".equals(gc_ad))) {
						Map map_t = JSON.parseObject(gc_ad);
						map_t.put("acc_url", gc_adv_url);
						goodsClass.setGc_advert(JSON.toJSONString(map_t));
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (CommUtil.null2Int(adv_type) == 0) {
			AdvertPosition ap = this.redPigAdvertPositionService.selectByPrimaryKey(CommUtil.null2Long(ap_id));
			Map map_temp = Maps.newHashMap();
			if (ap != null) {
				map_temp.put("adv_id", ap.getId());
			} else {
				map_temp.put("adv_id", "");
			}
			goodsClass.setGc_advert(JSON.toJSONString(map_temp));
		}
		if (goodsClass.getGc_advert() != null) {
			Map map_temp = JSON.parseObject(goodsClass.getGc_advert());
			map_temp.put("adv_type",
					Integer.valueOf(CommUtil.null2Int(adv_type)));
			goodsClass.setGc_advert(JSON.toJSONString(map_temp));
		} else {
			Map map_temp = Maps.newHashMap();
			map_temp.put("adv_type",
					Integer.valueOf(CommUtil.null2Int(adv_type)));
			goodsClass.setGc_advert(JSON.toJSONString(map_temp));
		}
		if ((gc_color != null) && (!gc_color.equals(""))) {
			goodsClass.setGc_color(gc_color);
		}
		if ((gbs_ids != null) && (!gbs_ids.equals(""))) {
			String[] gb_ids = gbs_ids.split(",");
			List list_temp = Lists.newArrayList();

			for (String gb_id : gb_ids) {

				if ((gb_id != null) && (!gb_id.equals(""))) {
					GoodsBrand gb = this.redPigGoodsbrandService.selectByPrimaryKey(CommUtil.null2Long(gb_id));
					
					Map map_temp = Maps.newHashMap();
					map_temp.put("id", gb.getId());
					map_temp.put("src", gb.getBrandLogo().getPath() + "/"
							+ gb.getBrandLogo().getName());
					map_temp.put("name", gb.getName());
					list_temp.add(map_temp);
				}
			}
			goodsClass.setGb_info(JSON.toJSONString(list_temp));
		} else {
			goodsClass.setGb_info(null);
		}

		if (id.equals("")) {
			this.redPigGoodsClassService.save(goodsClass);
		} else {
			this.redPigGoodsClassService.update(goodsClass);
		}
		ModelAndView mv = new RedPigJModelAndView("admin/blue/success.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request, response);
		
		mv.addObject("op_title", "保存商品分类成功");
		mv.addObject("list_url", list_url);
		if (add_url != null) {
			mv.addObject("add_url", add_url + "?pid=" + pid);
		}
		return mv;
	}
	
	private Set<Long> genericIds(GoodsClass gc) {
		Set<Long> ids = Sets.newHashSet();
		
		ids.add(gc.getId());
		for (GoodsClass child : gc.getChilds()) {
			Set<Long> cids = genericIds(child);
			for (Long cid : cids) {
				ids.add(cid);
			}
			ids.add(child.getId());
		}
		return ids;
	}
	
	/**
	 * 商品分类编辑
	 * @param request
	 * @param response
	 * @param id
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "商品分类编辑", value = "/goods_class_edit*", rtype = "admin", rname = "分类管理", rcode = "goods_class", rgroup = "商品")
	@RequestMapping({ "/goods_class_edit" })
	public ModelAndView goods_class_edit(
			HttpServletRequest request,
			HttpServletResponse response, 
			String id, 
			String currentPage) 
	{
	
		ModelAndView mv = new RedPigJModelAndView("admin/blue/goods_class_add.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request, response);
		
		if ((id != null) && (!id.equals(""))) {
			GoodsClass goodsClass = this.redPigGoodsClassService.selectByPrimaryKey(Long.parseLong(id));
			
			mv.addObject("obj", goodsClass);
			
			if ((goodsClass.getShowClass_id() != null)
					&& (!"".equals(goodsClass.getShowClass_id()))) {
				ShowClass showclass = this.redPigShowClassService.selectByPrimaryKey(goodsClass.getShowClass_id());
				
				mv.addObject("showclass", showclass);
			}
			
			Map<String,Object> params = Maps.newHashMap();
			params.put("parent", -1);
			params.put("redpig_id", goodsClass.getId());
			
			List<GoodsClass> gcs = this.redPigGoodsClassService.queryPageList(params);
			
			List<GoodsType> gts = this.redPigGoodsTypeService.queryPageList(params);
			
			params.clear();
			params.put("ap_status", Integer.valueOf(1));
			params.put("ap_type", "img");
			List<AdvertPosition> aps = this.redPigAdvertPositionService.queryPageList(params);
			
			if (goodsClass.getGc_advert() != null) {
				Map<String, Object> map = JSON.parseObject(goodsClass.getGc_advert());
				mv.addObject("adv_type",CommUtil.null2String(map.get("adv_type")));
				mv.addObject("adv_id", CommUtil.null2String(map.get("adv_id")));
				
				Accessory acc = this.redPigAccessoryService.selectByPrimaryKey(CommUtil.null2Long(map.get("acc_id")));
				
				if (acc != null) {
					mv.addObject("acc_img", CommUtil.getURL(request) + "/"
							+ acc.getPath() + "/" + acc.getName());
				}
				if (map.get("acc_url") != null) {
					mv.addObject("acc_url",
							CommUtil.null2String(map.get("acc_url")));
				}
			}
			if ((goodsClass.getGb_info() != null)
					&& (!goodsClass.getGb_info().equals(""))) {
				List<Map> map_list = JSON.parseArray(goodsClass.getGb_info(),Map.class);
				
				List<GoodsBrand> gbs = Lists.newArrayList();
				for (Map map : map_list) {
					GoodsBrand gb = this.redPigGoodsbrandService.selectByPrimaryKey(CommUtil.null2Long(map.get("id")));
					
					if (gb != null) {
						gbs.add(gb);
					}
				}
				mv.addObject("gbs", gbs);
			}
			params.clear();
			params.put("parent", -1);
			List<ShowClass> scs = this.redPigShowClassService.queryPageList(params);
			
			mv.addObject("scs", scs);
			mv.addObject("aps", aps);
			mv.addObject("gcs", gcs);
			mv.addObject("gts", gts);
			mv.addObject("currentPage", currentPage);
			mv.addObject("edit", Boolean.valueOf(true));
		}
		return mv;
	}
	
	/**
	 * 商品分类添加
	 * @param request
	 * @param response
	 * @param pid
	 * @return
	 */
	@SecurityMapping(title = "商品分类添加", value = "/goods_class_add*", rtype = "admin", rname = "分类管理", rcode = "goods_class", rgroup = "商品")
	@RequestMapping({ "/goods_class_add" })
	public ModelAndView goods_class_add(
			HttpServletRequest request,
			HttpServletResponse response, 
			String pid) {
		
		ModelAndView mv = new RedPigJModelAndView("admin/blue/goods_class_add.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request, response);
		
		Map<String,Object> maps = Maps.newHashMap();
		
		maps.put("parent", -1);
		
		List<GoodsClass> gcs = this.redPigGoodsClassService.queryPageList(maps);
		
		maps.clear();
		
		List<GoodsType> gts = this.redPigGgoodsTypeService.queryPageList(maps);
		
		maps.put("parent", -1);
		
		List<ShowClass> scs = this.redPigShowClassService.queryPageList(maps);
		
		if ((pid != null) && (!pid.equals(""))) {
			mv.addObject("pid", pid);
		}
		
		Map<String, Object> params = Maps.newHashMap();
		params.put("ap_status", Integer.valueOf(1));
		params.put("ap_type", "img");
		
		List<AdvertPosition> aps = this.redPigAdvertPositionService.queryPageList(params);
		
		mv.addObject("aps", aps);
		mv.addObject("gcs", gcs);
		mv.addObject("gts", gts);
		mv.addObject("scs", scs);
		return mv;
	}
	
	/**
	 * 商品分类列表
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @return
	 */
	@SecurityMapping(title = "商品分类列表", value = "/goods_class_list*", rtype = "admin", rname = "分类管理", rcode = "goods_class", rgroup = "商品")
	@RequestMapping({ "/goods_class_list" })
	public ModelAndView goods_class_list(
			HttpServletRequest request,
			HttpServletResponse response, 
			String currentPage, 
			String orderBy,
			String orderType) 
	{

		ModelAndView mv = new RedPigJModelAndView("admin/blue/goods_class_list.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request, response);
		
		Map<String,Object> params = this.redPigQueryTools.getParams(currentPage, "sequence", "asc");
		
		params.put("parent", -1);
		
		IPageList pList = this.redPigGoodsClassService.list(params);
		
		String url = this.redPigSysConfigService.getSysConfig().getAddress();
		if ((url == null) || (url.equals(""))) {
			url = CommUtil.getURL(request);
		}
		
		CommUtil.saveIPageList2ModelAndView(url + "/goods_class_list.html", "", "", pList, mv);
		mv.addObject("showClassTools", this.redPigShowClassTools);
		return mv;
	}
}
