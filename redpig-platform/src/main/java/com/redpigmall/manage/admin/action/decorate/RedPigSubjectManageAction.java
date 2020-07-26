package com.redpigmall.manage.admin.action.decorate;

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
import com.redpigmall.domain.Goods;
import com.redpigmall.domain.GoodsClass;
import com.redpigmall.domain.Subject;

/**
 * 
 * <p>
 * Title: RedPigSubjectManageAction.java
 * </p>
 * 
 * <p>
 * Description: 平台专题管理控制器
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2014
 * </p>
 * 
 * <p>
 * Company: www.redpigmall.net
 * </p>
 * 
 * @date 2015-1-6
 * 
 * @version redpigmall_b2b2c_2015
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
@Controller
public class RedPigSubjectManageAction extends BaseAction{
	
	/**
	 * 专题列表
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @return
	 */
	@SecurityMapping(title = "专题列表", value = "/subject_list*", rtype = "admin", rname = "专题管理", rcode = "subject_admin", rgroup = "装修")
	@RequestMapping({ "/subject_list" })
	public ModelAndView subject_list(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,
			String orderType) {
		ModelAndView mv = new RedPigJModelAndView("admin/blue/subject_list.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request, response);
		Map<String, Object> maps = this.redPigQueryTools.getParams(currentPage,12, "sequence", "asc"); 
		
		IPageList pList = this.redPigSubjectService.list(maps);
		CommUtil.saveIPageList2ModelAndView("", "", null, pList, mv);
		return mv;
	}
	
	/**
	 * 专题添加
	 * @param request
	 * @param response
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "专题添加", value = "/subject_list*", rtype = "admin", rname = "专题管理", rcode = "subject_admin", rgroup = "装修")
	@RequestMapping({ "/subject_add" })
	public ModelAndView subject_add(HttpServletRequest request,
			HttpServletResponse response, String currentPage) {
		ModelAndView mv = new RedPigJModelAndView("admin/blue/subject_add.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		mv.addObject("currentPage", currentPage);
		return mv;
	}
	
	/**
	 * 专题编辑
	 * @param request
	 * @param response
	 * @param id
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "专题编辑", value = "/subject_edit*", rtype = "admin", rname = "专题管理", rcode = "subject_admin", rgroup = "装修")
	@RequestMapping({ "/subject_edit" })
	public ModelAndView subject_edit(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage) {
		ModelAndView mv = new RedPigJModelAndView("admin/blue/subject_add.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		if ((id != null) && (!id.equals(""))) {
			Subject subject = this.subjectService.selectByPrimaryKey(Long.valueOf(Long
					.parseLong(id)));
			mv.addObject("obj", subject);
			mv.addObject("currentPage", currentPage);
			mv.addObject("edit", Boolean.valueOf(true));
		}
		return mv;
	}
	
	/**
	 * 专题保存
	 * @param request
	 * @param response
	 * @param id
	 * @return
	 */
	@SecurityMapping(title = "专题保存", value = "/subject_save*", rtype = "admin", rname = "专题管理", rcode = "subject_admin", rgroup = "装修")
	@RequestMapping({ "/subject_save" })
	public ModelAndView subject_saveEntity(HttpServletRequest request,
			HttpServletResponse response, String id) {
		
		Subject subject = null;
		if (id.equals("")) {
			subject = (Subject) WebForm.toPo(request, Subject.class);
			subject.setAddTime(new Date());
		} else {
			Subject obj = this.subjectService.selectByPrimaryKey(Long.valueOf(Long
					.parseLong(id)));
			subject = (Subject) WebForm.toPo(request, obj);
		}
		String uploadFilePath = this.configService.getSysConfig()
				.getUploadFilePath();
		String saveFilePathName = request.getSession().getServletContext()
				.getRealPath("/")
				+ uploadFilePath + File.separator + "subject";
		Map<String, Object> map = Maps.newHashMap();
		try {
			String fileName = subject.getBanner() == null ? "" : subject.getBanner().getName();
			map = CommUtil.saveFileToServer(request, "image", saveFilePathName,
					fileName, null);
			if (fileName.equals("")) {
				if (map.get("fileName") != "") {
					Accessory photo = new Accessory();
					photo.setName(CommUtil.null2String(map.get("fileName")));
					photo.setExt(CommUtil.null2String(map.get("mime")));
					photo.setSize(BigDecimal.valueOf(CommUtil.null2Double(map.get("fileSize"))));
					photo.setPath(uploadFilePath + "/subject");
					photo.setWidth(CommUtil.null2Int(map.get("width")));
					photo.setHeight(CommUtil.null2Int(map.get("height")));
					photo.setAddTime(new Date());
					this.accessoryService.saveEntity(photo);
					subject.setBanner(photo);
				}
			} else if (map.get("fileName") != "") {
				Accessory photo = subject.getBanner();
				photo.setName(CommUtil.null2String(map.get("fileName")));
				photo.setExt(CommUtil.null2String(map.get("mime")));
				photo.setSize(BigDecimal.valueOf(CommUtil.null2Double(map
						.get("fileSize"))));
				photo.setPath(uploadFilePath + "/subject");
				photo.setWidth(CommUtil.null2Int(map.get("width")));
				photo.setHeight(CommUtil.null2Int(map.get("height")));
				this.accessoryService.updateById(photo);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (id.equals("")) {
			this.subjectService.saveEntity(subject);
		} else {
			this.subjectService.updateById(subject);
		}
		ModelAndView mv = new RedPigJModelAndView("admin/blue/success.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		mv.addObject("list_url", CommUtil.getURL(request)
				+ "/subject_list");
		mv.addObject("op_title", "专题保存成功");
		return mv;
	}
	
	/**
	 * 专题删除
	 * @param request
	 * @param response
	 * @param mulitId
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "专题删除", value = "/subject_del*", rtype = "admin", rname = "专题管理", rcode = "subject_admin", rgroup = "装修")
	@RequestMapping({ "/subject_del" })
	public String subject_del(HttpServletRequest request,
			HttpServletResponse response, String mulitId, String currentPage) {
		if (mulitId == null) {
			mulitId = "";
		}
		String[] ids = mulitId.split(",");
		for (String id : ids) {
			if (!id.equals("")) {
				this.subjectService.deleteById(Long.valueOf(Long.parseLong(id)));
			}
		}
		return "redirect:subject_list?currentPage=" + currentPage;
	}
	
	/**
	 * 专题Ajax更新
	 * @param request
	 * @param response
	 * @param id
	 * @param fieldName
	 * @param value
	 * @throws ClassNotFoundException
	 */
	@SecurityMapping(title = "专题Ajax更新", value = "/subject_list_ajax*", rtype = "admin", rname = "专题管理", rcode = "subject_admin", rgroup = "装修")
	@RequestMapping({ "/subject_list_ajax" })
	public void subject_list_ajax(HttpServletRequest request,
			HttpServletResponse response, String id, String fieldName,
			String value) throws ClassNotFoundException {
		Subject obj = this.subjectService.selectByPrimaryKey(Long.valueOf(Long
				.parseLong(id)));
		Field[] fields = Subject.class.getDeclaredFields();
		
		Object val = ObjectUtils.setValue(fieldName, value, obj, fields);
		this.subjectService.updateById(obj);
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
	 * 专题设计
	 * @param request
	 * @param response
	 * @param id
	 * @return
	 */
	@SecurityMapping(title = "专题设计", value = "/subject_set*", rtype = "admin", rname = "专题管理", rcode = "subject_admin", rgroup = "装修")
	@RequestMapping({ "/subject_set" })
	public ModelAndView subject_set(HttpServletRequest request,
			HttpServletResponse response, String id) {
		ModelAndView mv = new RedPigJModelAndView("admin/blue/subject_set.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		Subject obj = this.subjectService.selectByPrimaryKey(CommUtil.null2Long(id));
		mv.addObject("obj", obj);
		if ((obj != null) && (obj.getSubject_detail() != null)) {
			List<Map> objs = JSON.parseArray(obj.getSubject_detail(), Map.class);
			mv.addObject("objs", objs);
		}
		mv.addObject("SubjectTools", this.SubjectTools);
		return mv;
	}
	
	/**
	 * 专题图片保存
	 * @param request
	 * @param response
	 * @param id
	 * @param img_id
	 */
	
	@SecurityMapping(title = "专题图片保存", value = "/subject_img_upload*", rtype = "admin", rname = "专题管理", rcode = "subject_admin", rgroup = "装修")
	@RequestMapping({ "/subject_img_upload" })
	public void subject_img_upload(HttpServletRequest request,
			HttpServletResponse response, String id, String img_id) {
		Subject obj = this.subjectService.selectByPrimaryKey(CommUtil.null2Long(id));

		String uploadFilePath = this.configService.getSysConfig()
				.getUploadFilePath();
		String saveFilePathName = request.getSession().getServletContext()
				.getRealPath("/")
				+ uploadFilePath + File.separator + "subject";
		Map<String, Object> map = Maps.newHashMap();
		String fileName = "";
		
		Accessory img = null;
		if ((img_id != null) && (!img_id.equals("undefined"))) {
			img = this.accessoryService.selectByPrimaryKey(CommUtil.null2Long(img_id));
			fileName = img.getName();
		}
		try {
			map = CommUtil.saveFileToServer(request, "img", saveFilePathName,"", null);
			if (fileName.equals("")) {
				if (map.get("fileName") != "") {
					Accessory photo = new Accessory();
					photo.setName(CommUtil.null2String(map.get("fileName")));
					photo.setExt(CommUtil.null2String(map.get("mime")));
					photo.setSize(BigDecimal.valueOf(CommUtil.null2Double(map
							.get("fileSize"))));
					photo.setPath(uploadFilePath + "/subject");
					photo.setWidth(CommUtil.null2Int(map.get("width")));
					photo.setHeight(CommUtil.null2Int(map.get("height")));
					photo.setAddTime(new Date());
					this.accessoryService.saveEntity(photo);
					List<Map> subject_detail = Lists.newArrayList();
					if ((obj.getSubject_detail() != null)
							&& (!obj.getSubject_detail().equals(""))) {
						subject_detail = JSON.parseArray(
								obj.getSubject_detail(), Map.class);
					}
					Map<String,Object> temp_map = Maps.newHashMap();
					temp_map.put("type", "img");
					temp_map.put("id", photo.getId());
					temp_map.put("img_url",
							photo.getPath() + "/" + photo.getName());
					subject_detail.add(temp_map);
					obj.setSubject_detail(JSON.toJSONString(subject_detail));
					this.subjectService.updateById(obj);
				}
			} else if (map.get("fileName") != "") {
				Accessory photo = img;
				photo.setName(CommUtil.null2String(map.get("fileName")));
				photo.setExt(CommUtil.null2String(map.get("mime")));
				photo.setSize(BigDecimal.valueOf(CommUtil.null2Double(map
						.get("fileSize"))));
				photo.setPath(uploadFilePath + "/subject");
				photo.setWidth(CommUtil.null2Int(map.get("width")));
				photo.setHeight(CommUtil.null2Int(map.get("height")));
				photo.setAddTime(new Date());
				this.accessoryService.updateById(photo);
				if (obj.getSubject_detail() != null) {
					List<Map> subject_detail = JSON.parseArray(
							obj.getSubject_detail(), Map.class);
					for (Map temp_map : subject_detail) {
						if (CommUtil.null2String(temp_map.get("id")).equals(
								photo.getId().toString())) {
							temp_map.put("img_url", photo.getPath() + "/"
									+ photo.getName());
							break;
						}
					}
					obj.setSubject_detail(JSON.toJSONString(subject_detail));
					this.subjectService.updateById(obj);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		try {
			PrintWriter writer = response.getWriter();
			writer.print("true");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 专题设置图片链接
	 * @param request
	 * @param response
	 * @param id
	 * @param img_id
	 * @param img_href
	 */
	@SecurityMapping(title = "专题设置图片链接", value = "/subject_img_href*", rtype = "admin", rname = "专题管理", rcode = "subject_admin", rgroup = "装修")
	@RequestMapping({ "/subject_img_href" })
	public void subject_img_href(HttpServletRequest request,
			HttpServletResponse response, String id, String img_id,
			String img_href) {
		Subject obj = this.subjectService.selectByPrimaryKey(CommUtil.null2Long(id));
		if (obj.getSubject_detail() != null) {
			List<Map> objs = JSON
					.parseArray(obj.getSubject_detail(), Map.class);
			for (Map temp_map : objs) {
				if (CommUtil.null2String(temp_map.get("type")).equals("img")) {
					if (CommUtil.null2String(temp_map.get("id")).equals(img_id)) {
						if ((img_href == null) || (img_href.equals(""))) {
							break;
						}
						if (img_href.indexOf("http://") < 0) {
							img_href = "http://" + img_href;
						}
						temp_map.put("img_href", img_href);
						break;
					}
				}
			}
			obj.setSubject_detail(JSON.toJSONString(objs));
			this.subjectService.updateById(obj);
		}
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		try {
			PrintWriter writer = response.getWriter();
			writer.print("true");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 专题图片详情删除
	 * @param request
	 * @param response
	 * @param id
	 * @param dele_id
	 * @param type
	 */
	@SecurityMapping(title = "专题图片详情删除", value = "/subject_img_dele*", rtype = "admin", rname = "专题管理", rcode = "subject_admin", rgroup = "装修")
	@RequestMapping({ "/subject_img_dele" })
	public void subject_img_dele(HttpServletRequest request,
			HttpServletResponse response, String id, String dele_id, String type) {
		Subject obj = this.subjectService.selectByPrimaryKey(CommUtil.null2Long(id));
		if (obj.getSubject_detail() != null) {
			List<Map> subject_detail = JSON.parseArray(obj.getSubject_detail(),
					Map.class);
			List<Map> temp_maps = Lists.newArrayList();
			for (Map temp_map : subject_detail) {
				temp_maps.add(temp_map);
				if (CommUtil.null2String(temp_map.get("type")).equals(type)) {
					if (CommUtil.null2String(temp_map.get("id"))
							.equals(dele_id)) {
						temp_maps.remove(temp_map);
					}
				}
			}
			obj.setSubject_detail(JSON.toJSONString(temp_maps));
			this.subjectService.updateById(obj);
		}
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		try {
			PrintWriter writer = response.getWriter();
			writer.print("true");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 专题设置图片热点信息
	 * @param request
	 * @param response
	 * @param id
	 * @param img_id
	 * @param coords
	 * @param coords_url
	 * @param edit
	 */
	@SecurityMapping(title = "专题设置图片热点信息", value = "/subject_img_coords*", rtype = "admin", rname = "专题管理", rcode = "subject_admin", rgroup = "装修")
	@RequestMapping({ "/subject_img_coords" })
	public void subject_img_coords(HttpServletRequest request,
			HttpServletResponse response, String id, String img_id,
			String coords, String coords_url, String edit) {
		Subject obj = this.subjectService.selectByPrimaryKey(CommUtil.null2Long(id));
		if (obj.getSubject_detail() != null) {
			List<Map> objs = JSON
					.parseArray(obj.getSubject_detail(), Map.class);
			for (Map temp_map : objs) {
				if (CommUtil.null2String(temp_map.get("type")).equals("img")) {
					if (CommUtil.null2String(temp_map.get("id")).equals(img_id)) {
						if ((coords == null) || (coords.equals(""))
								|| (coords_url == null)
								|| (coords_url.equals(""))) {
							break;
						}
						if (coords_url.indexOf("http://") < 0) {
							coords_url = "http://" + coords_url;
						}
						if (coords_url.indexOf("]") >= 0) {
							coords_url = coords_url.replaceAll("]", "&");
						}
						String areaInfo = "";
						if ((temp_map.get("areaInfo") != null)
								&& (!temp_map.get("areaInfo").equals(""))) {
							areaInfo = CommUtil.null2String(temp_map
									.get("areaInfo"));
							String temp_dele_info = "";
							if (areaInfo.indexOf(coords) >= 0) {
								String[] areainfos = areaInfo.split("-");

								for (String info : areainfos) {

									if (info.indexOf(coords) >= 0) {
										temp_dele_info = info;
									}
								}
								areaInfo = areaInfo.replace(temp_dele_info, "");
							}
						}
						areaInfo = areaInfo + coords + "==" + coords_url + "-";
						temp_map.put("areaInfo", areaInfo);

						break;
					}
				}
			}
			obj.setSubject_detail(JSON.toJSONString(objs));
			this.subjectService.updateById(obj);
		}
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		try {
			PrintWriter writer = response.getWriter();
			writer.print("true");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 专题详情热点删除
	 * @param request
	 * @param response
	 * @param coords
	 * @param url
	 * @param id
	 */
	@SecurityMapping(title = "专题详情热点删除", value = "/subject_img_coords_dele*", rtype = "admin", rname = "专题管理", rcode = "subject_admin", rgroup = "装修")
	@RequestMapping({ "/subject_img_coords_dele" })
	public void subject_img_coords_dele(HttpServletRequest request,
			HttpServletResponse response, String coords, String url, String id) {
		Subject obj = this.subjectService.selectByPrimaryKey(CommUtil.null2Long(id));
		boolean ret = false;
		if (obj.getSubject_detail() != null) {
			List<Map> subject_detail = JSON.parseArray(obj.getSubject_detail(),
					Map.class);
			coords = coords.replace(",", "_");
			String temp_info = coords + "==" + url + "-";
			List<Map> temp_maps = Lists.newArrayList();
			for (Map temp_map : subject_detail) {
				if (temp_map.get("areaInfo") != null) {
					String areaInfo = CommUtil.null2String(temp_map
							.get("areaInfo"));
					areaInfo = areaInfo.replace(temp_info, "");
					temp_map.put("areaInfo", areaInfo);
					ret = true;
				}
				temp_maps.add(temp_map);
			}
			obj.setSubject_detail(JSON.toJSONString(temp_maps));
			this.subjectService.updateById(obj);
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
	 * 专题详情商品添加
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param id
	 * @param goods_ids
	 * @return
	 */
	@SecurityMapping(title = "专题详情商品添加", value = "/subject_items*", rtype = "admin", rname = "专题管理", rcode = "subject_admin", rgroup = "装修")
	@RequestMapping({ "/subject_items" })
	public ModelAndView subject_goods(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String id,
			String goods_ids) {
		ModelAndView mv = new RedPigJModelAndView("admin/blue/subject_goods.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		if ((goods_ids != null) && (!goods_ids.equals(""))) {
			List<Goods> goods_list = Lists.newArrayList();
			String[] ids = goods_ids.split(",");

			for (String gid : ids) {

				if (!gid.equals("")) {
					Goods obj = this.goodsService.selectByPrimaryKey(CommUtil.null2Long(gid));
					goods_list.add(obj);
				}
			}
			mv.addObject("goods_list", goods_list);
		}
		mv.addObject("goods_ids", goods_ids);
		mv.addObject("id", id);
		return mv;
	}
	
	/**
	 * 专题详情商品加载
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param gc_id
	 * @param goods_name
	 * @param activity_name
	 * @return
	 */
	@SecurityMapping(title = "专题详情商品加载", value = "/subject_goods_load*", rtype = "admin", rname = "专题管理", rcode = "subject_admin", rgroup = "装修")
	@RequestMapping({ "/subject_goods_load" })
	public ModelAndView subject_goods_load(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String gc_id,
			String goods_name, String activity_name) {
		ModelAndView mv = new RedPigJModelAndView(
				"admin/blue/subject_goods_load.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		
		Map<String,Object> maps= this.redPigQueryTools.getParams(currentPage,7,"addTime","desc");
		
		if (!CommUtil.null2String(gc_id).equals("")) {
			Set<Long> ids = genericIds(this.goodsClassService.selectByPrimaryKey(CommUtil.null2Long(gc_id)));
			maps.put("ids", ids);
		}
		
		if (!CommUtil.null2String(goods_name).equals("")) {
			maps.put("goods_name_like", goods_name);
		}
		
		if ((activity_name != null) && (!"".equals(activity_name))) {
			List<String> str_list = Lists.newArrayList();
			str_list.add(activity_name);
			this.queryTools.queryActivityGoods(maps, str_list);
		}
		
		maps.put("goods_status", 0);
		IPageList pList = this.goodsService.list(maps);
		
		CommUtil.saveIPageList2ModelAndView(CommUtil.getURL(request)
				+ "/subject_goods_load.html", "", "", pList, mv);
		return mv;
	}
	
	/**
	 * 专题详情商品保存
	 * @param request
	 * @param response
	 * @param id
	 * @param ids
	 * @param old_ids
	 * @return
	 */
	@SecurityMapping(title = "专题详情商品保存", value = "/subject_goods_save*", rtype = "admin", rname = "专题管理", rcode = "subject_admin", rgroup = "装修")
	@RequestMapping({ "/subject_goods_save" })
	public String subject_goods_saveEntity(HttpServletRequest request,
			HttpServletResponse response, String id, String ids, String old_ids) {
		Subject obj = this.subjectService.selectByPrimaryKey(CommUtil.null2Long(id));
		List<Map> maps = Lists.newArrayList();
		if ((obj.getSubject_detail() != null)
				&& (!obj.getSubject_detail().equals(""))) {
			maps = JSON.parseArray(obj.getSubject_detail(), Map.class);
		}
		if ((old_ids != null) && (!old_ids.equals(""))) {
			for (Map temp_map : maps) {
				if (CommUtil.null2String(temp_map.get("type")).equals("goods")) {
					if (CommUtil.null2String(temp_map.get("goods_ids")).equals(
							old_ids)) {
						maps.remove(temp_map);
						break;
					}
				}
			}
		}
		Map<String, Object> map = Maps.newHashMap();
		map.put("type", "goods");
		map.put("goods_ids", ids);
		maps.add(map);
		String json = JSON.toJSONString(maps);
		obj.setSubject_detail(json);
		this.subjectService.updateById(obj);
		return "redirect:/subject_set?id=" + id;
	}
	
	/**
	 * 专题商品删除
	 * @param request
	 * @param response
	 * @param id
	 * @param goods_ids
	 * @param type
	 */
	@SecurityMapping(title = "专题商品删除", value = "/subject_goods_dele*", rtype = "admin", rname = "专题管理", rcode = "subject_admin", rgroup = "装修")
	@RequestMapping({ "/subject_goods_dele" })
	public void subject_goods_dele(HttpServletRequest request,
			HttpServletResponse response, String id, String goods_ids,
			String type) {
		Subject obj = this.subjectService.selectByPrimaryKey(CommUtil.null2Long(id));
		if (obj.getSubject_detail() != null) {
			List<Map> subject_detail = JSON.parseArray(obj.getSubject_detail(),
					Map.class);
			List<Map> temp_maps = Lists.newArrayList();
			for (Map temp_map : subject_detail) {
				temp_maps.add(temp_map);
				if (CommUtil.null2String(temp_map.get("type")).equals(type)) {
					if (CommUtil.null2String(temp_map.get("goods_ids")).equals(
							goods_ids)) {
						temp_maps.remove(temp_map);
						break;
					}
				}
			}
			obj.setSubject_detail(JSON.toJSONString(temp_maps));
			this.subjectService.updateById(obj);
		}
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		try {
			PrintWriter writer = response.getWriter();
			writer.print("true");
		} catch (IOException e) {
			e.printStackTrace();
		}
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
}
