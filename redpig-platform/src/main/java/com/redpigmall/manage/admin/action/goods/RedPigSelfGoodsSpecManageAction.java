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

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.redpigmall.api.annotation.SecurityMapping;
import com.redpigmall.api.mv.RedPigJModelAndView;
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.api.sec.SecurityUserHolder;
import com.redpigmall.api.tools.CommUtil;
import com.redpigmall.api.tools.ObjectUtils;
import com.redpigmall.api.tools.RedPigCommonUtil;
import com.redpigmall.api.tools.WebForm;
import com.redpigmall.manage.admin.action.base.BaseAction;
import com.redpigmall.domain.Accessory;
import com.redpigmall.domain.GoodsClass;
import com.redpigmall.domain.GoodsSpecProperty;
import com.redpigmall.domain.GoodsSpecification;
import com.redpigmall.domain.SysConfig;
import com.redpigmall.domain.User;

/**
 * 
 * <p>
 * Title: RedPigSelfGoodsSpecManageAction.java
 * </p>
 * 
 * <p>
 * Description: 自营商品规格管理控制器，平台自营及商家可以自行管理规格属性，规格属性只在商品详细页显示并可以选择，
 * 平台搜索列表显示的规格属性为平台商品类型中的新增属性
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
 * @date 2014年4月25日
 * 
 * @version redpigmall_b2b2c 8.0
 */
@Controller
public class RedPigSelfGoodsSpecManageAction extends BaseAction{
	
	/**
	 * 商品规格大类AJAX添加
	 * @param request
	 * @param response
	 * @param gc_id
	 * @param name
	 * @param sequence
	 * @param type
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@SecurityMapping(title = "商品规格大类AJAX添加", value = "/goods_specification_ajax_add*", rtype = "admin", rname = "规格管理", rcode = "goods_spec", rgroup = "自营")
	@RequestMapping({ "/goods_specification_ajax_add" })
	public void goods_specification_ajax_add(
			HttpServletRequest request,
			HttpServletResponse response, 
			String gc_id, 
			String name,
			String sequence, 
			String type) {
		
		Map jsonmap = Maps.newHashMap();
		
		GoodsSpecification goodsSpecification = new GoodsSpecification();
		goodsSpecification.setAddTime(new Date());
		goodsSpecification.setSpec_type(0);
		goodsSpecification.setSequence(CommUtil.null2Int(sequence));
		goodsSpecification.setType(type);
		goodsSpecification.setName(name);
		
		GoodsClass gc = this.redPigGoodsclassService.selectByPrimaryKey(CommUtil.null2Long(gc_id));
		
		List<Map<String,Object>> gspgcIds = Lists.newArrayList();
		
		Map<String,Object> maps = Maps.newHashMap();
		if (gc.getLevel() == 2) {
			goodsSpecification.setGoodsclass(gc.getParent());
			maps.put("spec_gc_id", gc.getId());
		} else {
			goodsSpecification.setGoodsclass(gc);
		}
		
		if (this.redPigGoodsSpecificationService.save(goodsSpecification)) {
			//将goodsSpecification和spec_goodsClass_detail(goodsClass)之间关系
			maps.put("spec_id", goodsSpecification.getId());
			
			gspgcIds.add(maps);
			
			this.redPigGoodsSpecificationService.saveGoodsSpecificationGoodsClassDetail(gspgcIds);
			
			jsonmap.put("id", goodsSpecification.getId());
			jsonmap.put("name", name);
			jsonmap.put("sequence",Integer.valueOf(goodsSpecification.getSequence()));
			jsonmap.put("type", goodsSpecification.getType());
		}
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
	 * 商品规格AJAX添加
	 * @param request
	 * @param response
	 * @param id
	 * @param name
	 * @param type 0:文件规格 1:图片规格
	 * @throws ClassNotFoundException
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@SecurityMapping(title = "商品规格AJAX添加", value = "/goods_spec_ajax_add*", rtype = "admin", rname = "规格管理", rcode = "goods_spec", rgroup = "自营")
	@RequestMapping({ "/goods_spec_ajax_add2" })
	public void goods_spec_ajax_add2(HttpServletRequest request,
			HttpServletResponse response, String id, String name,String type)
			throws ClassNotFoundException {
		User user = this.redPigUserService.selectByPrimaryKey(SecurityUserHolder.getCurrentUser().getId());
		
		Map jsonmap = Maps.newHashMap();

		SysConfig config = this.redPigSysConfigService.getSysConfig();
		
		String path = "";
		String uploadFilePath = config.getUploadFilePath();
		
//		GoodsSpecification obj = this.redPigGoodsSpecificationService.selectByPrimaryKey(Long.parseLong(id));
		
		GoodsClass gc = this.redPigGoodsclassService.selectByPrimaryKey(Long.parseLong(id));
		/**
		 * 新建一个GoodsSpecification,和GoodsClass关联
		 */
		GoodsSpecification goodsSpecification = new GoodsSpecification();
		goodsSpecification.setSequence(gc.getSpec_detail().size()+1);
		goodsSpecification.setName(name);
		goodsSpecification.setType(type);
		goodsSpecification.setGoodsclass(gc);
		goodsSpecification.setSpec_type(0);// 规格类型，0为平台自营规格，1为商家规格，商家规格时需要有对应的店铺
		
		this.redPigGoodsSpecificationService.save(goodsSpecification);
		
		List<Map<String,Object>> gspgcIds = Lists.newArrayList();
		Map<String,Object> gspgcId = Maps.newHashMap();
		gspgcId.put("spec_gc_id", gc.getId());
		gspgcId.put("spec_id", goodsSpecification.getId());
		
		gspgcIds.add(gspgcId);
		
		this.redPigGoodsSpecificationService.saveGoodsSpecificationGoodsClassDetail(gspgcIds);
		
		GoodsSpecProperty property = new GoodsSpecProperty();
		property.setAddTime(new Date());
		property.setSequence(goodsSpecification.getProperties().size() + 1);
		property.setSpec(goodsSpecification);
		property.setValue(name);
		
		if ("1".equals(type)) {//1:图片规格
			try {
				String filePath = request.getSession().getServletContext()
						.getRealPath("/")
						+ uploadFilePath + File.separator + "spec";
				
				CommUtil.createFolder(filePath);
				path = uploadFilePath + "/spec";
				
				Map<String, Object> map = CommUtil.saveFileToServer(request,"gsp_add_img_" + id, filePath, null, null);
				Accessory image = new Accessory();
				image.setUser(user);
				image.setAddTime(new Date());
				image.setExt((String) map.get("mime"));
				image.setPath(path);
				image.setWidth(CommUtil.null2Int(map.get("width")));
				image.setHeight(CommUtil.null2Int(map.get("height")));
				image.setName(CommUtil.null2String(map.get("fileName")));
				image.setInfo("");
				this.redPigAccessoryService.save(image);
				property.setSpecImage(image);
				jsonmap.put("url", CommUtil.getURL(request) + "/" + path + "/" + image.getName());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		this.redPigGoodsSpecPropertyService.saveEntity(property);
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
	 * 商品规格AJAX添加
	 * @param request
	 * @param response
	 * @param id
	 * @param name
	 * @throws ClassNotFoundException
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@SecurityMapping(title = "商品规格AJAX添加", value = "/goods_spec_ajax_add*", rtype = "admin", rname = "规格管理", rcode = "goods_spec", rgroup = "自营")
	@RequestMapping({ "/goods_spec_ajax_add" })
	public void goods_spec_ajax_add(HttpServletRequest request,
			HttpServletResponse response, String id, String name)
			throws ClassNotFoundException {
		User user = this.redPigUserService.selectByPrimaryKey(SecurityUserHolder.getCurrentUser().getId());
		
		Map jsonmap = Maps.newHashMap();

		SysConfig config = this.redPigSysConfigService.getSysConfig();
		
		String path = "";
		String uploadFilePath = config.getUploadFilePath();

		GoodsSpecification obj = this.redPigGoodsSpecificationService.selectByPrimaryKey(Long.parseLong(id));
		
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
				this.redPigAccessoryService.save(image);
				property.setSpecImage(image);
				jsonmap.put("url", CommUtil.getURL(request) + "/" + path + "/"
						+ image.getName());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		this.redPigGoodsSpecPropertyService.saveEntity(property);
		
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
	 * 
	 * @param request
	 * @param response
	 * @param gc_id
	 * @param mark
	 * @param id
	 * @return
	 */
	@RequestMapping({ "/goods_spec_gc_load" })
	public ModelAndView spec_goodsclass_load(HttpServletRequest request,
			HttpServletResponse response, String gc_id, String mark, String id) {
		ModelAndView mv = new RedPigJModelAndView(
				"admin/blue/goods_spec_gc_load.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request, response);
		
		if ((mark != null) && (!mark.equals("")) && (mark.equals("pgc"))) {
			mv = new RedPigJModelAndView("admin/blue/goods_spec_pgc_load.html",
					this.redPigSysConfigService.getSysConfig(),
					this.redPigUserConfigService.getUserConfig(), 0, request,
					response);
		}
		
		GoodsClass gc = this.redPigGoodsclassService.selectByPrimaryKey(CommUtil.null2Long(gc_id));
		
		if ((id != null) && (!id.equals(""))) {
			GoodsSpecification gspec = this.redPigGoodsSpecificationService.selectByPrimaryKey(CommUtil.null2Long(id));
			mv.addObject("obj", gspec);
		}
		if (gc != null) {
			mv.addObject("gcs", gc.getChilds());
		}
		return mv;
	}
	
	/**
	 * 
	 * @param request
	 * @param response
	 * @param name
	 * @param id
	 */
	@RequestMapping({ "/goods_spec_verify" })
	public void goods_spec_verify(HttpServletRequest request,
			HttpServletResponse response, String name, String id) {
		boolean ret = true;
		Map<String, Object> params = Maps.newHashMap();
		params.put("name", name);
		params.put("spec_type", Integer.valueOf(0));
		params.put("redPig_id", CommUtil.null2Long(id));
		List<GoodsSpecification> gss = this.redPigGoodsSpecificationService.queryPageList(params);
		
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
	 * 商品规格AJAX更新
	 * @param request
	 * @param response
	 * @param id
	 * @param fieldName
	 * @param value
	 * @throws ClassNotFoundException
	 */
	@SecurityMapping(title = "商品规格AJAX更新", value = "/goods_spec_ajax*", rtype = "admin", rname = "规格管理", rcode = "goods_spec", rgroup = "自营")
	@RequestMapping({ "/goods_spec_ajax" })
	public void ajax(HttpServletRequest request, HttpServletResponse response,
			String id, String fieldName, String value)
			throws ClassNotFoundException {
		GoodsSpecification obj = this.redPigGoodsSpecificationService.selectByPrimaryKey(Long.parseLong(id));
		
		Field[] fields = GoodsSpecification.class.getDeclaredFields();
		Object val = ObjectUtils.setValue(fieldName, value, obj, fields);
		
		this.redPigGoodsSpecificationService.update(obj);
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
	 * 商品规格属性AJAX删除
	 * @param request
	 * @param response
	 * @param id
	 */
	@SecurityMapping(title = "商品规格属性AJAX删除", value = "/goods_property_delete*", rtype = "admin", rname = "规格管理", rcode = "goods_spec", rgroup = "自营")
	@RequestMapping({ "/goods_property_delete" })
	public void goods_property_delete(HttpServletRequest request,
			HttpServletResponse response, String id) {
		boolean ret = true;
		if (!id.equals("")) {
			this.databaseTools.execute("delete from redpigmall_goods_spec where spec_id="+id);
			this.databaseTools.execute("delete from redpigmall_cart_gsp where gsp_id=" +id);
			
			GoodsSpecProperty property = this.redPigGoodsSpecPropertyService.selectByPrimaryKey(Long.valueOf(Long.parseLong(id)));
			
			ret = this.redPigGoodsSpecPropertyService.delete(property.getId())==1?true:false;
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
	 * 商品规格删除
	 * @param request
	 * @param mulitId
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "商品规格删除", value = "/goods_spec_del*", rtype = "admin", rname = "规格管理", rcode = "goods_spec", rgroup = "自营")
	@RequestMapping({ "/goods_spec_del" })
	public String delete(HttpServletRequest request, String mulitId,
			String currentPage) {
		if (mulitId == null) {
			mulitId = "";
		}
		String[] ids = mulitId.split(",");

		for (String id : ids) {

			if (!id.equals("")) {
				GoodsSpecification obj = this.redPigGoodsSpecificationService.selectByPrimaryKey(Long.parseLong(id));
				
				this.redPigGoodsSpecificationService.update(obj);
				clearProperty(request, obj);
				obj.getSpec_goodsClass_detail().removeAll(
						obj.getSpec_goodsClass_detail());
				this.redPigGoodsSpecificationService.delete(Long.valueOf(Long.parseLong(id)));
			}
		}
		return "redirect:goods_spec_list?currentPage=" + currentPage;
	}
	
	private void clearProperty(HttpServletRequest request,
			GoodsSpecification spec) {
		for (GoodsSpecProperty property : spec.getProperties()) {
			this.databaseTools
					.execute("delete from redpigmall_goods_spec where spec_id="
							+

							property.getId());
			this.databaseTools
					.execute("delete from redpigmall_cart_gsp where gsp_id=" +

					property.getId());
			property.setSpec(null);
			Accessory img = property.getSpecImage();
			RedPigCommonUtil.del_acc(request, img);
			
			property.setSpecImage(null);
			
			this.redPigGoodsSpecPropertyService.delete(property.getId());
		}
	}
	
	/**
	 * 商品规格保存
	 * @param request
	 * @param response
	 * @param id
	 * @param cmd
	 * @param count
	 * @param add_url
	 * @param list_url
	 * @param currentPage
	 * @param gc_ids
	 * @param gc_id
	 * @return
	 */
	@SecurityMapping(title = "商品规格保存", value = "/goods_spec_save*", rtype = "admin", rname = "规格管理", rcode = "goods_spec", rgroup = "自营")
	@RequestMapping({ "/goods_spec_save" })
	public ModelAndView goods_spec_save(
			HttpServletRequest request,
			HttpServletResponse response, 
			String id, 
			String cmd, 
			String count,
			String add_url, 
			String list_url, 
			String currentPage, 
			String gc_ids,
			String gc_id) {
		
		GoodsSpecification goodsSpecification = null;
		//如果id为空做新增操作
		if (id.equals("")) {
			goodsSpecification = (GoodsSpecification) WebForm.toPo(request,
					GoodsSpecification.class);
			goodsSpecification.setAddTime(new Date());
			goodsSpecification.setSpec_type(0);
		} else {
			GoodsSpecification obj = this.redPigGoodsSpecificationService.selectByPrimaryKey(Long.parseLong(id));
			
			goodsSpecification = (GoodsSpecification) WebForm.toPo(request, obj);
		}
		List<GoodsClass> gc_list = Lists.newArrayList();
		if ((gc_ids != null) && (!gc_ids.equals(""))) {
			String[] ids = gc_ids.split(",");

			for (String c_id : ids) {

				GoodsClass gc_detail = this.redPigGoodsclassService.selectByPrimaryKey(CommUtil.null2Long(c_id));
				if (gc_detail != null) {
					gc_list.add(gc_detail);
				}
			}
			if (gc_list.size() > 0) {
				goodsSpecification.setSpec_goodsClass_detail(gc_list);
			}
		} else {
			
			goodsSpecification.getSpec_goodsClass_detail().removeAll(goodsSpecification.getSpec_goodsClass_detail());
			
		}
		if ((gc_id != null) && (!gc_id.equals(""))) {
			GoodsClass gc_main = this.redPigGoodsclassService.selectByPrimaryKey(CommUtil.null2Long(gc_id));
			goodsSpecification.setGoodsclass(gc_main);
		}
		
		if (id.equals("")) {
			this.redPigGoodsSpecificationService.save(goodsSpecification);
			
			List<GoodsClass> gcs = goodsSpecification.getSpec_goodsClass_detail();
			if(goodsSpecification.getSpec_goodsClass_detail()!=null && goodsSpecification.getSpec_goodsClass_detail().size()>0){
				List<Map<String,Object>> gspgcIds = Lists.newArrayList();
				for (GoodsClass goodsClass : gcs) {
					Map<String,Object> map = Maps.newHashMap();
					map.put("spec_id", goodsSpecification.getId());
					map.put("spec_gc_id", goodsClass.getId());
					gspgcIds.add(map);
				}
				
				this.redPigGoodsSpecificationService.saveGoodsSpecificationGoodsClassDetail(gspgcIds);
			}
		} else {
			
			GoodsSpecification gsf = this.redPigGoodsSpecificationService.selectByPrimaryKey(CommUtil.null2Long(id));
			
			List<GoodsClass> sgcds = gsf.getSpec_goodsClass_detail();
			List<Map<String,Object>> gspgcIds = Lists.newArrayList();
			
			for (GoodsClass specGoodsClassDetail : sgcds) {
				Map<String,Object> map = Maps.newHashMap();
				map.put("spec_id", goodsSpecification.getId());
				map.put("spec_gc_id", specGoodsClassDetail.getId());
				gspgcIds.add(map);
				
			}
			
			this.redPigGoodsSpecificationService.deleteGoodsSpecificationGoodsClassDetail(gspgcIds);
			
			this.redPigGoodsSpecificationService.update(goodsSpecification);
			
			if(gc_list!=null && gc_list.size()>0){
				gspgcIds = Lists.newArrayList();
				for (GoodsClass goodsClass : gc_list) {
					Map<String,Object> map = Maps.newHashMap();
					map.put("spec_id", goodsSpecification.getId());
					map.put("spec_gc_id", goodsClass.getId());
					gspgcIds.add(map);
				}
				
				this.redPigGoodsSpecificationService.saveGoodsSpecificationGoodsClassDetail(gspgcIds);
			}
			
		}
		
		genericProperty(request, goodsSpecification, count);
		
		ModelAndView mv = new RedPigJModelAndView("admin/blue/success.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request, response);
		mv.addObject("list_url", list_url + "?currentPage=" + currentPage);
		mv.addObject("op_title", "保存商品规格成功");
		if (add_url != null) {
			mv.addObject("add_url", add_url);
		}
		return mv;
	}
	
	/**
	 * 保存规格属性值
	 * @param request
	 * @param spec
	 * @param count
	 */
	@SuppressWarnings("unchecked")
	private void genericProperty(HttpServletRequest request,
			GoodsSpecification spec, String count) {
		for (int i = 1; i <= CommUtil.null2Int(count); i++) {
			Integer sequence = CommUtil.null2Int(request.getParameter("sequence_" + i));
			String value = CommUtil.null2String(request.getParameter("value_" + i));
			if ((sequence != null) && (!sequence.equals("")) && (value != null) && (!value.equals(""))) {
				String id = CommUtil.null2String(request.getParameter("id_" + i));
				GoodsSpecProperty property = null;
				if ((id != null) && (!id.equals(""))) {//ID不为空是编辑
					property = this.redPigGoodsSpecPropertyService.selectByPrimaryKey(Long.parseLong(id));
					
				} else {//ID为空是新增保存
					property = new GoodsSpecProperty();
				}
				property.setAddTime(new Date());
				property.setSequence(sequence.intValue());
				property.setSpec(spec);
				property.setValue(value);
				String uploadFilePath = this.redPigSysConfigService.getSysConfig().getUploadFilePath();
				String saveFilePathName = request.getSession()
						.getServletContext().getRealPath("/")
						+ uploadFilePath + File.separator + "spec";
				Map<String, Object> map = Maps.newHashMap();
				try {
					String fileName = property.getSpecImage() == null ? "" : property.getSpecImage().getName();
					map = CommUtil.saveFileToServer(request, "specImage_" + i,saveFilePathName, fileName, null);
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
							this.redPigAccessoryService.save(specImage);
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
						this.redPigAccessoryService.update(specImage);
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
				if (id.equals("")) {
					this.redPigGoodsSpecPropertyService.saveEntity(property);
				} else {
					this.redPigGoodsSpecPropertyService.update(property);
				}
			}
		}
	}
	
	/**
	 * 商品规格编辑
	 * @param request
	 * @param response
	 * @param id
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "商品规格编辑", value = "/goods_spec_edit*", rtype = "admin", rname = "规格管理", rcode = "goods_spec", rgroup = "自营")
	@RequestMapping({ "/goods_spec_edit" })
	public ModelAndView goods_spec_edit(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage) {
		ModelAndView mv = new RedPigJModelAndView("admin/blue/goods_spec_add.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request, response);
		
		if ((id != null) && (!id.equals(""))) {
			GoodsSpecification goodsSpecification = this.redPigGoodsSpecificationService.selectByPrimaryKey(Long.parseLong(id));
			
			mv.addObject("obj", goodsSpecification);
			mv.addObject("currentPage", currentPage);
			Map<String,Object> params = Maps.newHashMap();
			params.put("parent", -1);
			
			List<GoodsClass> pgcs = this.redPigGoodsclassService.queryPageList(params);
			
			GoodsClass main_gc = goodsSpecification.getGoodsclass();
			if (main_gc != null) {
				mv.addObject("gc_childs", main_gc.getChilds());
			}
			mv.addObject("gcs", main_gc.getParent().getChilds());
			mv.addObject("pgcs", pgcs);
			mv.addObject("edit", Boolean.valueOf(true));
		}
		return mv;
	}
	
	/**
	 * 商品规格添加
	 * @param request
	 * @param response
	 * @return
	 */
	@SecurityMapping(title = "商品规格添加", value = "/goods_spec_add*", rtype = "admin", rname = "规格管理", rcode = "goods_spec", rgroup = "自营")
	@RequestMapping({ "/goods_spec_add" })
	public ModelAndView goods_spec_add(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new RedPigJModelAndView("admin/blue/goods_spec_add.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request, response);
		Map<String,Object> params = Maps.newHashMap();
		params.put("parent", -1);
		params.put("orderBy", "sequence");
		params.put("orderType", "asc");
		
		List<GoodsClass> pgcs = this.redPigGoodsclassService.queryPageList(params);
		
		mv.addObject("pgcs", pgcs);
		return mv;
	}
	
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
	@SecurityMapping(title = "商品规格列表", value = "/goods_spec_list*", rtype = "admin", rname = "规格管理", rcode = "goods_spec", rgroup = "自营")
	@RequestMapping({ "/goods_spec_list" })
	public ModelAndView goods_spec_list(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,
			String orderType, String searchType, String searchText) {
		ModelAndView mv = new RedPigJModelAndView("admin/blue/goods_spec_list.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request, response);
		
		Map<String,Object> params = redPigQueryTools.getParams(currentPage, "sequence", "asc");
		
		if ((searchType != null) && (!searchType.equals(""))) {
			if (searchType.equals("1")) {
				params.put("redPig_remark_name", searchText);
			} else {
				params.put("redPig_name", searchText);
			}
		}
		
		params.put("spec_type", Integer.valueOf(0));
		
		IPageList pList = this.redPigGoodsSpecificationService.list(params);
		
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		mv.addObject("shopTools", this.shopTools);
		mv.addObject("searchType", searchType);
		mv.addObject("searchText", searchText);
		mv.addObject("currentPage", currentPage);
		return mv;
	}

}
