package com.redpigmall.manage.admin.action.self.group;

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

import com.google.common.collect.Maps;
import com.redpigmall.api.annotation.SecurityMapping;
import com.redpigmall.api.mv.RedPigJModelAndView;
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.api.tools.CommUtil;
import com.redpigmall.api.tools.ObjectUtils;
import com.redpigmall.api.tools.WebForm;
import com.redpigmall.manage.admin.action.base.BaseAction;
import com.redpigmall.domain.Accessory;
import com.redpigmall.domain.GroupClass;
import com.redpigmall.domain.GroupGoods;

/**
 * 
 * <p>
 * Title: RedPigGroupClassManageAction.java
 * </p>
 * 
 * <p>
 * Description: 团购分类管理
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
 * @date 2014年5月27日
 * 
 * @version redpigmall_b2b2c 8.0
 */
@SuppressWarnings({ "unchecked"  })
@Controller
public class RedPigGroupClassManageAction extends BaseAction{
	/**
	 * 团购分类列表
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @param type
	 * @return
	 */
	@SecurityMapping(title = "团购分类列表", value = "/group_class_list*", rtype = "admin", rname = "团购管理", rcode = "group_admin", rgroup = "运营")
	@RequestMapping({ "/group_class_list" })
	public ModelAndView list(HttpServletRequest request, HttpServletResponse response, String currentPage,
			String orderBy, String orderType, String type) {
		
		ModelAndView mv = new RedPigJModelAndView("admin/blue/group_class_list.html", this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		
		String url = this.configService.getSysConfig().getAddress();
		if ((url == null) || (url.equals(""))) {
			url = CommUtil.getURL(request);
		}
		String params = "";

		Map<String, Object> maps = this.redPigQueryTools.getParams(currentPage, "gc_sequence", "asc");

		if ("goods".equals(type)) {
			maps.put("gc_type", 0);
		} else {
			mv = new RedPigJModelAndView("admin/blue/group_lifeclass_list.html", this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request, response);
			maps.put("gc_type", 1);
		}
		maps.put("parent", -1);

		IPageList pList = this.groupclassService.queryPagesWithNoRelations(maps);
		CommUtil.saveIPageList2ModelAndView(url + "/group_class_list.html", "", params, pList, mv);
		mv.addObject("type", type);
		return mv;
	}
	
	/**
	 * 团购分类增加
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param pid
	 * @param type
	 * @return
	 */
	@SecurityMapping(title = "团购分类增加", value = "/group_class_add*", rtype = "admin", rname = "团购管理", rcode = "group_admin", rgroup = "运营")
	@RequestMapping({ "/group_class_add" })
	public ModelAndView add(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String pid,
			String type) {
		ModelAndView mv = new RedPigJModelAndView("admin/blue/group_class_add.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		Map<String,Object> maps = Maps.newHashMap();
		maps.put("gc_type", 0);
		maps.put("parent", -1);
		
		List<GroupClass> gcs = this.groupclassService.queryPageList(maps);
		
		if ("life".equals(type)) {
			mv = new RedPigJModelAndView("admin/blue/group_lifeclass_add.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request,
					response);
			maps.clear();
			maps.put("gc_type", 1);
			maps.put("parent", -1);
			
			gcs = this.groupclassService.queryPageList(maps);
			
		}
		GroupClass parent = this.groupclassService.selectByPrimaryKey(CommUtil.null2Long(pid));
		GroupClass obj = new GroupClass();
		obj.setParent(parent);
		mv.addObject("obj", obj);
		mv.addObject("gcs", gcs);
		mv.addObject("currentPage", currentPage);
		mv.addObject("type", type);
		return mv;
	}
	
	/**
	 * 团购分类编辑
	 * @param request
	 * @param response
	 * @param id
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "团购分类编辑", value = "/group_class_edit*", rtype = "admin", rname = "团购管理", rcode = "group_admin", rgroup = "运营")
	@RequestMapping({ "/group_class_edit" })
	public ModelAndView edit(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage) {
		ModelAndView mv = new RedPigJModelAndView("admin/blue/group_class_add.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		if ((id != null) && (!id.equals(""))) {
			GroupClass groupclass = this.groupclassService.selectByPrimaryKey(Long
					.valueOf(Long.parseLong(id)));
	         Map<String,Object> maps = Maps.newHashMap();
	         maps.put("gc_type", 0);
	         maps.put("parent", -1);

			List<GroupClass> gcs = this.groupclassService.queryPageList(maps);
			
			if (groupclass.getGc_type() == 1) {
				mv = new RedPigJModelAndView("admin/blue/group_lifeclass_add.html",
						this.configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 0, request,
						response);
				maps.clear();
				maps.put("gc_type", 1);
		        maps.put("parent", -1);
				
		        gcs = this.groupclassService.queryPageList(maps);
			}
			mv.addObject("gcs", gcs);
			mv.addObject("obj", groupclass);
			mv.addObject("currentPage", currentPage);
			mv.addObject("edit", Boolean.valueOf(true));
		}
		return mv;
	}
	
	/**
	 * 团购分类保存
	 * @param request
	 * @param response
	 * @param id
	 * @param currentPage
	 * @param cmd
	 * @param pid
	 * @param gc_type
	 * @param gc_recommend
	 * @return
	 */
	@SecurityMapping(title = "团购分类保存", value = "/group_class_save*", rtype = "admin", rname = "团购管理", rcode = "group_admin", rgroup = "运营")
	@RequestMapping({ "/group_class_save" })
	public ModelAndView saveEntity(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage,
			String cmd, String pid, String gc_type, String gc_recommend) {

		GroupClass groupclass = null;
		if (id.equals("")) {
			groupclass = (GroupClass) WebForm.toPo(request, GroupClass.class);
			groupclass.setAddTime(new Date());
		} else {
			GroupClass obj = this.groupclassService.selectByPrimaryKey(Long
					.valueOf(Long.parseLong(id)));
			groupclass = (GroupClass) WebForm.toPo(request, obj);
		}
		GroupClass parent = this.groupclassService.selectByPrimaryKey(CommUtil
				.null2Long(pid));
		if (parent != null) {
			groupclass.setParent(parent);
			groupclass.setGc_level(parent.getGc_level() + 1);
		}
		groupclass.setGc_type(CommUtil.null2Int(gc_type));

		String uploadFilePath = this.configService.getSysConfig()
				.getUploadFilePath();
		String saveFilePathName = request.getSession().getServletContext()
				.getRealPath("/")
				+ uploadFilePath + File.separator + "group";
		Map<String, Object> map = Maps.newHashMap();
		try {
			String fileName = groupclass.getGc_img() == null ? "" : groupclass
					.getGc_img().getName();
			map = CommUtil.saveFileToServer(request, "gc_image",
					saveFilePathName, fileName, null);
			if (fileName.equals("")) {
				if (map.get("fileName") != "") {
					Accessory gc_img = new Accessory();
					gc_img.setName(CommUtil.null2String(map.get("fileName")));
					gc_img.setExt(CommUtil.null2String(map.get("mime")));
					gc_img.setSize(BigDecimal.valueOf(CommUtil.null2Double(map
							.get("fileSize"))));
					gc_img.setPath(uploadFilePath + "/group");
					gc_img.setWidth(CommUtil.null2Int(map.get("width")));
					gc_img.setHeight(CommUtil.null2Int(map.get("height")));
					gc_img.setAddTime(new Date());
					this.accessoryService.saveEntity(gc_img);
					groupclass.setGc_img(gc_img);
				}
			} else if (map.get("fileName") != "") {
				Accessory gc_img = groupclass.getGc_img();
				gc_img.setName(CommUtil.null2String(map.get("fileName")));
				gc_img.setExt(CommUtil.null2String(map.get("mime")));
				gc_img.setSize(BigDecimal.valueOf(CommUtil.null2Double(map
						.get("fileSize"))));
				gc_img.setPath(uploadFilePath + "/group");
				gc_img.setWidth(CommUtil.null2Int(map.get("width")));
				gc_img.setHeight(CommUtil.null2Int(map.get("height")));
				gc_img.setAddTime(new Date());
				this.accessoryService.updateById(gc_img);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (id.equals("")) {
			this.groupclassService.saveEntity(groupclass);
		} else {
			this.groupclassService.updateById(groupclass);
		}
		String params = "goods";
		if (gc_type.equals("1")) {
			params = "life";
		}
		ModelAndView mv = new RedPigJModelAndView("admin/blue/success.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		mv.addObject("list_url", CommUtil.getURL(request)
				+ "/group_class_list?type=" + params);
		mv.addObject("op_title", "保存团购分类成功");
		mv.addObject("add_url", CommUtil.getURL(request)
				+ "/group_class_add" + "?currentPage=" + currentPage
				+ "&type=" + params);
		return mv;
	}
	
	/**
	 * 团购分类删除
	 * @param request
	 * @param response
	 * @param mulitId
	 * @param currentPage
	 * @param type
	 * @return
	 */
	@SecurityMapping(title = "团购分类删除", value = "/group_class_del*", rtype = "admin", rname = "团购管理", rcode = "group_admin", rgroup = "运营")
	@RequestMapping({ "/group_class_del" })
	public String deleteById(HttpServletRequest request,
			HttpServletResponse response, String mulitId, String currentPage,
			String type) {
		String[] ids = mulitId.split(",");
		for (String id : ids) {
			if (!id.equals("")) {
				GroupClass groupclass = this.groupclassService.selectByPrimaryKey(Long.valueOf(Long.parseLong(id)));
				for (GroupGoods gg : groupclass.getGgs()) {
					if (gg != null) {
						gg.setGg_gc(null);
						this.groupgoodsService.updateById(gg);
					}
				}
				this.groupclassService.deleteById(Long.valueOf(Long.parseLong(id)));
			}
		}
		
		
		
		String params = "goods";
		if (type.equals("1")) {
			params = "life";
		}
		return "redirect:group_class_list?type=" + params + "&currentPage="
				+ currentPage;
	}
	
	/**
	 * 团购分类Ajax更新
	 * @param request
	 * @param response
	 * @param id
	 * @param fieldName
	 * @param value
	 * @throws ClassNotFoundException
	 */
	@SecurityMapping(title = "团购分类Ajax更新", value = "/group_class_ajax*", rtype = "admin", rname = "团购管理", rcode = "group_admin", rgroup = "运营")
	@RequestMapping({ "/group_class_ajax" })
	public void ajax(HttpServletRequest request, HttpServletResponse response,
			String id, String fieldName, String value)
			throws ClassNotFoundException {
		GroupClass obj = this.groupclassService.selectByPrimaryKey(Long.valueOf(Long
				.parseLong(id)));
		Field[] fields = GroupClass.class.getDeclaredFields();
		
		Object val = ObjectUtils.setValue(fieldName, value, obj, fields);
		this.groupclassService.updateById(obj);
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
	 * 团购分类下级加载
	 * @param request
	 * @param response
	 * @param pid
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "团购分类下级加载", value = "/group_class_data*", rtype = "admin", rname = "分类管理", rcode = "goods_class", rgroup = "商品")
	@RequestMapping({ "/group_class_data" })
	public ModelAndView group_class_data(HttpServletRequest request,
			HttpServletResponse response, String pid, String currentPage) {
		ModelAndView mv = new RedPigJModelAndView("admin/blue/group_class_data.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		Map<String, Object> map = Maps.newHashMap();
		map.put("parent", CommUtil.null2Long(pid));
		
		List<GroupClass> gcs = this.groupclassService.queryPageList(map);
		
		mv.addObject("gcs", gcs);
		mv.addObject("currentPage", currentPage);
		return mv;
	}
	
	/**
	 * 
	 * @param request
	 * @param response
	 * @param gc_name
	 * @param id
	 * @param pid
	 */
	@RequestMapping({ "/group_class_verify" })
	public void group_class_verify(HttpServletRequest request,
			HttpServletResponse response, String gc_name, String id, String pid) {
		boolean ret = true;
		Map<String, Object> params = Maps.newHashMap();
		params.put("gc_name", gc_name);
		params.put("id_no", CommUtil.null2Long(id));
		params.put("parent", CommUtil.null2Long(pid));
		
		List<GroupClass> gcs = this.groupclassService.queryPageList(params);
		
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
	 * 
	 * @param request
	 * @param response
	 * @param id
	 */
	@RequestMapping({ "/group_class3_verify" })
	public void group_class3_verify(HttpServletRequest request,
			HttpServletResponse response, String id) {
		GroupClass gc = this.groupclassService.selectByPrimaryKey(CommUtil.null2Long(id));
		if (gc != null) {
			response.setContentType("text/plain");
			response.setHeader("Cache-Control", "no-cache");
			response.setCharacterEncoding("UTF-8");
			try {
				PrintWriter writer = response.getWriter();
				writer.print(gc.getGc_level());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
