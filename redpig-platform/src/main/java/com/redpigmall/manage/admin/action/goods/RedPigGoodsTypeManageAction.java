package com.redpigmall.manage.admin.action.goods;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
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
import com.redpigmall.api.tools.RedPigCommUtil;
import com.redpigmall.api.tools.WebForm;
import com.redpigmall.manage.admin.action.base.BaseAction;
import com.redpigmall.domain.GoodsBrand;
import com.redpigmall.domain.GoodsBrandCategory;
import com.redpigmall.domain.GoodsType;
import com.redpigmall.domain.GoodsTypeProperty;


/**
 * 
 * <p>
 * Title: RedPigGoodsTypeManageAction.java
 * </p>
 * 
 * <p>
 * Description: 商品类型管理类
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
@Controller
public class RedPigGoodsTypeManageAction extends BaseAction{
	/**
	 * 
	 * @param request
	 * @param response
	 * @param name
	 * @param id
	 */
	@RequestMapping({ "/goods_type_verify" })
	public void goods_type_verify(HttpServletRequest request,
			HttpServletResponse response, String name, String id) {
		boolean ret = true;
		Map<String, Object> params = Maps.newHashMap();
		params.put("name", name);
		params.put("redPig_id", CommUtil.null2Long(id));
		List<GoodsType> gts = this.redPigGoodsTypeService.queryPageList(params);
		
		if ((gts != null) && (gts.size() > 0)) {
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
	 * 商品类型AJAX更新
	 * @param request
	 * @param response
	 * @param id
	 * @param fieldName
	 * @param value
	 * @throws ClassNotFoundException
	 */
	@SecurityMapping(title = "商品类型AJAX更新", value = "/goods_type_ajax*", rtype = "admin", rname = "类型管理", rcode = "goods_type", rgroup = "商品")
	@RequestMapping({ "/goods_type_ajax" })
	public void ajax(HttpServletRequest request, HttpServletResponse response,
			String id, String fieldName, String value)
			throws ClassNotFoundException {
		GoodsType obj = this.redPigGoodsTypeService.selectByPrimaryKey(Long.parseLong(id));
		
		Field[] fields = GoodsType.class.getDeclaredFields();
		Object val = ObjectUtils.setValue(fieldName, value, obj, fields);
		this.redPigGoodsTypeService.update(obj);
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
	 * 商品类型属性AJAX删除
	 * @param request
	 * @param response
	 * @param id
	 */
	@SecurityMapping(title = "商品类型属性AJAX删除", value = "/goods_type_property_delete*", rtype = "admin", rname = "类型管理", rcode = "goods_type", rgroup = "商品")
	@RequestMapping({ "/goods_type_property_delete" })
	public void goods_type_property_delete(
			HttpServletRequest request,
			HttpServletResponse response, 
			String id) {
		boolean ret = true;
		if (!id.equals("")) {
			GoodsTypeProperty property = this.redPigGoodsTypePropertyService.selectByPrimaryKey(Long.parseLong(id));
			
			int d  = this.redPigGoodsTypePropertyService.delete(property.getId());
			if(d != 1){
				ret = false;
			}
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
	 * 商品类型删除
	 * @param request
	 * @param mulitId
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "商品类型删除", value = "/goods_type_del*", rtype = "admin", rname = "类型管理", rcode = "goods_type", rgroup = "商品")
	@RequestMapping({ "/goods_type_del" })
	public String goods_type_del(HttpServletRequest request, String mulitId,
			String currentPage) {
		if (mulitId == null) {
			mulitId = "";
		}
		String[] ids = mulitId.split(",");
		
		this.redPigGoodsTypeService.deleteGoodsTypeAndGoodsBrand(ids);
		
		this.redPigGoodsTypeService.delete(ids);
		
		return "redirect:goods_type_list?currentPage=" + currentPage;
	}
	
	
	/**
	 * 商品类型保存
	 * @param request
	 * @param response
	 * @param id
	 * @param cmd
	 * @param currentPage
	 * @param list_url
	 * @param add_url
	 * @param brand_ids
	 * @param count
	 * @return
	 */
	@SecurityMapping(title = "商品类型保存", value = "/goods_type_save*", rtype = "admin", rname = "类型管理", rcode = "goods_type", rgroup = "商品")
	@RequestMapping({ "/goods_type_save" })
	public ModelAndView goods_type_save(
			HttpServletRequest request,
			HttpServletResponse response, 
			String id, 
			String cmd,
			String currentPage, 
			String list_url, 
			String add_url,
			String brand_ids, 
			String count) 
	{
		
		GoodsType goodsType = null;
		if (id.equals("")) {
			goodsType = (GoodsType) WebForm.toPo(request, GoodsType.class);
			goodsType.setAddTime(new Date());
		} else {
			GoodsType obj = this.redPigGoodsTypeService.selectByPrimaryKey(Long.parseLong(id));
			
			goodsType = (GoodsType) WebForm.toPo(request, obj);
		}
		
		if (id.equals("")) {
			this.redPigGoodsTypeService.save(goodsType);
		} else {
			this.redPigGoodsTypeService.update(goodsType);
		}
		
		//先把goodsType的GoodsBrand给清空在重新添加
		List<Map<String, Long>> gbtIds = Lists.newArrayList();
		List<GoodsBrand> gbs = goodsType.getGbs();
		for (GoodsBrand gb : gbs) {
			Map<String,Long> maps = Maps.newHashMap();
			maps.put("brand_id", gb.getId());
			maps.put("type_id", goodsType.getId());
			gbtIds.add(maps);
		}
		
		this.redPigGoodsBrandService.deleteGoodsBrandAndGoodsType(gbtIds);
		
		String[] gb_ids = brand_ids.split(",");
		gbtIds.clear();
		
		for (String gb_id : gb_ids) {
			if(RedPigCommUtil.isNotNull(gb_id)){
				Map<String,Long> maps = Maps.newHashMap();
				maps.put("brand_id", Long.parseLong(gb_id.trim()));
				maps.put("type_id", goodsType.getId());
				gbtIds.add(maps);
			}
		}
		
		//将goodsType和goodsBrand关联起来
		this.redPigGoodsBrandService.saveGoodsBrandAndGoodsType(gbtIds);
		
		genericProperty(request, goodsType, count);
		ModelAndView mv = new RedPigJModelAndView("admin/blue/success.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request, response);
		
		mv.addObject("list_url", list_url + "?currentPage=" + currentPage);
		mv.addObject("op_title", "保存商品类型成功");
		if (add_url != null) {
			mv.addObject("add_url", add_url);
		}
		return mv;
	}
	
	public void genericProperty(HttpServletRequest request, GoodsType type,String count) {
		for (int i = 1; i <= CommUtil.null2Int(count); i++) {
			int sequence = CommUtil.null2Int(request
					.getParameter("gtp_sequence_" + i));
			String name = CommUtil.null2String(request.getParameter("gtp_name_"
					+ i));
			String value = CommUtil.null2String(request
					.getParameter("gtp_value_" + i));
			boolean display = CommUtil.null2Boolean(request
					.getParameter("gtp_display_" + i));
			String hc_value = CommUtil.null2String(request
					.getParameter("gtp_value_hc_" + i));
			if ((!name.equals("")) && (!value.equals(""))) {
				GoodsTypeProperty gtp = null;
				String id = CommUtil.null2String(request.getParameter("gtp_id_"
						+ i));
				if (id.equals("")) {
					gtp = new GoodsTypeProperty();
				} else {
					gtp = this.redPigGoodsTypePropertyService.selectByPrimaryKey(Long.parseLong(id));
				}
				gtp.setAddTime(new Date());
				gtp.setDisplay(display);
				gtp.setGoodsType(type);
				gtp.setName(name);
				gtp.setSequence(sequence);
				gtp.setValue(value);
				gtp.setHc_value(hc_value);
				if (id.equals("")) {
					this.redPigGoodsTypePropertyService.saveEntity(gtp);
				} else {
					this.redPigGoodsTypePropertyService.updateById(gtp);
				}
			}
		}
	}
	
	/**
	 * 商品类型编辑
	 * @param request
	 * @param response
	 * @param id
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "商品类型编辑", value = "/goods_type_edit*", rtype = "admin", rname = "类型管理", rcode = "goods_type", rgroup = "商品")
	@RequestMapping({ "/goods_type_edit" })
	public ModelAndView goods_type_edit(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage) {
		ModelAndView mv = new RedPigJModelAndView("admin/blue/goods_type_add.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request, response);
		
		if ((id != null) && (!id.equals(""))) {
			GoodsType goodsType = this.redPigGoodsTypeService.selectByPrimaryKey(Long.parseLong(id));
			Map<String,Object> params = Maps.newHashMap();
			
			List<GoodsBrandCategory> gbcs = this.redPigGoodsBrandCategoryService.queryPageList(params);
			
			mv.addObject("gbcs", gbcs);
			mv.addObject("shopTools", this.shopTools);
			mv.addObject("obj", goodsType);
			mv.addObject("edit", Boolean.valueOf(true));
		}
		return mv;
	}
	
	/**
	 * 商品类型添加
	 * @param request
	 * @param response
	 * @return
	 */
	@SecurityMapping(title = "商品类型添加", value = "/goods_type_add*", rtype = "admin", rname = "类型管理", rcode = "goods_type", rgroup = "商品")
	@RequestMapping({ "/goods_type_add" })
	public ModelAndView goods_type_add(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new RedPigJModelAndView("admin/blue/goods_type_add.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request, response);
		
		Map<String,Object> params = Maps.newHashMap();
		
		List<GoodsBrandCategory> gbcs = this.redPigGoodsBrandCategoryService.queryPageList(params);
		
		mv.addObject("gbcs", gbcs);
		mv.addObject("shopTools", this.shopTools);
		return mv;
	}
	
	/**
	 * 商品类型列表
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @return
	 */
	@SecurityMapping(title = "商品类型列表", value = "/goods_type_list*", rtype = "admin", rname = "类型管理", rcode = "goods_type", rgroup = "商品")
	@RequestMapping({ "/goods_type_list" })
	public ModelAndView goods_type_list(
			HttpServletRequest request,
			HttpServletResponse response, 
			String currentPage, 
			String orderBy,
			String orderType) 
	{
	
		ModelAndView mv = new RedPigJModelAndView("admin/blue/goods_type_list.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request, response);
		
		Map<String,Object> maps = redPigQueryTools.getParams(currentPage, "sequence asc,addTime", "desc");
		
		IPageList pList = this.redPigGoodsTypeService.list(maps);
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		return mv;
	}
}
