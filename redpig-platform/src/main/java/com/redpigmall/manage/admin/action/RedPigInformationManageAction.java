package com.redpigmall.manage.admin.action;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
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
import com.redpigmall.api.tools.ObjectUtils;
import com.redpigmall.api.tools.WebForm;
import com.redpigmall.manage.admin.action.base.BaseAction;
import com.redpigmall.domain.Accessory;
import com.redpigmall.domain.CircleClass;
import com.redpigmall.domain.CmsIndexTemplate;
import com.redpigmall.domain.FreeClass;
import com.redpigmall.domain.Goods;
import com.redpigmall.domain.GoodsClass;
import com.redpigmall.domain.Information;
import com.redpigmall.domain.InformationClass;
import com.redpigmall.domain.User;

/**
 * 
 * <p>
 * Title: RedPigInformationManageAction.java
 * </p>
 * 
 * <p>
 * Description:资讯管理；审核，发布，
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
 * @date 2014-12-4
 * 
 * @version redpigmall_b2b2c 8.0
 */
@SuppressWarnings({ "rawtypes", "unused", "unchecked" })
@Controller
public class RedPigInformationManageAction extends BaseAction {

	/**
	 * 资讯首页头部编辑列表
	 * 
	 * @param request
	 * @param response
	 * @param type
	 * @param currentPage
	 * @param title
	 * @param author
	 * @param classid
	 * @return
	 */

	@SecurityMapping(title = "资讯首页头部编辑列表", value = "/information_head_list*", rtype = "admin", rname = "资讯管理", rcode = "information_admin", rgroup = "网站")
	@RequestMapping({ "/information_head_list" })
	public ModelAndView information_head_list(HttpServletRequest request,
			HttpServletResponse response, String type, String currentPage,
			String title, String author, String classid) {
		ModelAndView mv = new RedPigJModelAndView(
				"admin/blue/information_head_list.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request,
				response);
		Map<String, Object> params = this.redPigQueryTools.getParams(
				currentPage, "sequence,addTime", "desc");
		params.put("status", 20);

		if ((title != null) && (!title.equals(""))) {
			params.put("title_like", title);

			mv.addObject("title", title);
		}

		if ((author != null) && (!author.equals(""))) {
			params.put("author_like", author);

			mv.addObject("author", author);
		}

		if ((classid != null) && (!classid.equals(""))) {
			Map<String, Object> map = Maps.newHashMap();
			map.put("ic_pid", CommUtil.null2Long(classid));
			List<InformationClass> informationClasses = this.redPigInformationClassService
					.queryPageList(map);

			List<Long> ids = Lists.newArrayList();
			for (InformationClass ic : informationClasses) {
				ids.add(ic.getId());
			}

			ids.add(CommUtil.null2Long(classid));

			params.put("classIds", ids);

			mv.addObject("classid", classid);
		}

		IPageList pList = this.redPigInformationService.list(params);
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		params.clear();
		params.put("ic_pid", -1);
		List<InformationClass> infoclass = this.redPigInformationClassService
				.queryPageList(params);

		mv.addObject("infoclass", infoclass);
		mv.addObject("cmsTools", this.cmsTools);
		return mv;
	}

	/**
	 * 资讯首页头部编辑保存
	 * 
	 * @param request
	 * @param response
	 * @param id
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "资讯首页头部编辑保存", value = "/information_head_save*", rtype = "admin", rname = "资讯管理", rcode = "information_admin", rgroup = "网站")
	@RequestMapping({ "/information_head_save" })
	public ModelAndView information_head_saveEntity(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage) {
		ModelAndView mv = null;
		Information infor = this.redPigInformationService
				.selectByPrimaryKey(CommUtil.null2Long(id));
		if (infor != null) {
			Map<String, Object> map = Maps.newHashMap();
			map.put("status", Integer.valueOf(20));
			map.put("recommend", Integer.valueOf(1));
			List<Information> informations = this.redPigInformationService
					.queryPageList(map);

			for (Information information : informations) {
				information.setRecommend(0);
				this.redPigInformationService.updateById(information);
			}
			infor.setRecommend(1);
			this.redPigInformationService.updateById(infor);
			mv = new RedPigJModelAndView("admin/blue/success.html",
					this.redPigSysConfigService.getSysConfig(),
					this.redPigUserConfigService.getUserConfig(), 0, request,
					response);
			mv.addObject("list_url", CommUtil.getURL(request)
					+ "/cms_template_list");
			mv.addObject("op_title", "设置首页头部成功");
			mv.addObject("add_url", CommUtil.getURL(request)
					+ "/information_head_list?currentPage=" + currentPage);
		} else {
			mv = new RedPigJModelAndView("admin/blue/error.html",
					this.redPigSysConfigService.getSysConfig(),
					this.redPigUserConfigService.getUserConfig(), 0, request,
					response);
			mv.addObject("list_url", CommUtil.getURL(request)
					+ "/cms_template_list");
			mv.addObject("op_title", "参数错误！");
		}
		return mv;
	}

	/**
	 * 资讯列表
	 * 
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param title
	 * @param author
	 * @param classid
	 * @return
	 */
	@SecurityMapping(title = "资讯列表", value = "/information_list*", rtype = "admin", rname = "资讯管理", rcode = "information_admin", rgroup = "网站")
	@RequestMapping({ "/information_list" })
	public ModelAndView information_list(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String title,
			String author, String classid) {
		ModelAndView mv = new RedPigJModelAndView(
				"admin/blue/information_list.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request,
				response);
		Map<String, Object> params = this.redPigQueryTools.getParams(
				currentPage, "sequence,addTime", "desc");

		params.put("status", 20);

		if ((title != null) && (!title.equals(""))) {
			params.put("title_like", title);

			mv.addObject("title", title);
		}
		if ((author != null) && (!author.equals(""))) {
			params.put("author_like", author);
			mv.addObject("author", author);
		}
		if (!CommUtil.null2String(classid).equals("")) {
			Map<String, Object> map = Maps.newHashMap();
			map.put("ic_pid", CommUtil.null2Long(classid));
			List<InformationClass> informationClasses = this.redPigInformationClassService
					.queryPageList(params);

			List<Long> ids = Lists.newArrayList();
			for (InformationClass ic : informationClasses) {
				ids.add(ic.getId());
			}
			ids.add(CommUtil.null2Long(classid));

			params.put("classIds", ids);

			mv.addObject("classid", classid);
		}
		IPageList pList = this.redPigInformationService.list(params);
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		params.clear();
		params.put("ic_pid", -1);
		List<InformationClass> infoclass = this.redPigInformationClassService
				.queryPageList(params);

		mv.addObject("infoclass", infoclass);
		mv.addObject("cmsTools", this.cmsTools);
		return mv;
	}

	/**
	 * 资讯待审核列表
	 * 
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param title
	 * @param author
	 * @param classid
	 * @return
	 */
	@SecurityMapping(title = "资讯待审核列表", value = "/information_verifylist*", rtype = "admin", rname = "资讯管理", rcode = "information_admin", rgroup = "网站")
	@RequestMapping({ "/information_verifylist" })
	public ModelAndView information_verifylist(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String title,
			String author, String classid) {
		ModelAndView mv = new RedPigJModelAndView(
				"admin/blue/information_verifylist.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request,
				response);
		Map<String, Object> params = this.redPigQueryTools.getParams(
				currentPage, "addTime", "desc");

		params.put("type", 1);

		params.put("status", 10);

		if ((title != null) && (!title.equals(""))) {
			params.put("title_like", title);

			mv.addObject("title", title);
		}
		if ((author != null) && (!author.equals(""))) {
			params.put("author_like", author);

			mv.addObject("author", author);
		}

		if ((classid != null) && (!classid.equals(""))) {
			params.put("classid", CommUtil.null2Long(classid));

			mv.addObject("classid", classid);
		}

		IPageList pList = this.redPigInformationService.list(params);

		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		params.clear();
		params.put("ic_pid", -1);
		List<InformationClass> infoclass = this.redPigInformationClassService
				.queryPageList(params);

		mv.addObject("infoclass", infoclass);
		mv.addObject("cmsTools", this.cmsTools);
		return mv;
	}

	/**
	 * 资讯添加
	 * 
	 * @param request
	 * @param response
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "资讯添加", value = "/information_add*", rtype = "admin", rname = "资讯管理", rcode = "information_admin", rgroup = "网站")
	@RequestMapping({ "/information_add" })
	public ModelAndView information_add(HttpServletRequest request,
			HttpServletResponse response, String currentPage) {
		ModelAndView mv = new RedPigJModelAndView(
				"admin/blue/information_add.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request,
				response);

		Map<String, Object> params = Maps.newHashMap();
		params.put("ic_pid", -1);

		List<InformationClass> infoclass = this.redPigInformationClassService
				.queryPageList(params);

		mv.addObject("infoclass", infoclass);
		mv.addObject("currentPage", currentPage);
		mv.addObject("imageTools", this.imageTools);
		mv.addObject("cmsTools", this.cmsTools);
		return mv;
	}

	/**
	 * 资讯编辑
	 * 
	 * @param request
	 * @param response
	 * @param id
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "资讯编辑", value = "/information_edit*", rtype = "admin", rname = "资讯管理", rcode = "information_admin", rgroup = "网站")
	@RequestMapping({ "/information_edit" })
	public ModelAndView information_edit(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage) {
		ModelAndView mv = new RedPigJModelAndView(
				"admin/blue/information_add.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request,
				response);
		if ((id != null) && (!id.equals(""))) {
			Information information = this.redPigInformationService
					.selectByPrimaryKey(Long.valueOf(Long.parseLong(id)));
			Map<String, Object> params = Maps.newHashMap();
			params.put("ic_pid", -1);

			List<InformationClass> infoclass = this.redPigInformationClassService
					.queryPageList(params);

			mv.addObject("infoclass", infoclass);
			mv.addObject("obj", information);
			mv.addObject("currentPage", currentPage);
			mv.addObject("edit", Boolean.valueOf(true));
			mv.addObject("imageTools", this.imageTools);
			mv.addObject("cmsTools", this.cmsTools);
		}
		return mv;
	}

	/**
	 * 资讯保存
	 * 
	 * @param request
	 * @param response
	 * @param id
	 * @param currentPage
	 * @param cmd
	 * @param list_url
	 * @param add_url
	 * @return
	 */
	@SecurityMapping(title = "资讯保存", value = "/information_save*", rtype = "admin", rname = "资讯管理", rcode = "information_admin", rgroup = "网站")
	@RequestMapping({ "/information_save" })
	public ModelAndView information_saveEntity(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage,
			String cmd, String list_url, String add_url) {
		WebForm wf = new WebForm();
		Information information = null;
		if (id.equals("")) {
			information = (Information) WebForm
					.toPo(request, Information.class);
			information.setAddTime(new Date());
			User user = SecurityUserHolder.getCurrentUser();
			information.setAuthor(user.getUsername());
			information.setAuthor_id(user.getId());
			information.setType(0);
			information.setStatus(20);
		} else {
			Information obj = this.redPigInformationService
					.selectByPrimaryKey(Long.valueOf(Long.parseLong(id)));
			information = (Information) WebForm.toPo(request, obj);
		}
		String uploadFilePath = this.redPigSysConfigService.getSysConfig()
				.getUploadFilePath();
		String saveFilePathName = request.getSession().getServletContext()
				.getRealPath("/")
				+ uploadFilePath + File.separator + "information_cover";
		Map<String, Object> map = Maps.newHashMap();
		try {
			String fileName = "";
			Accessory photo = null;
			if ((information.getCover() != null)
					&& (information.getCover().longValue() != 0L)) {
				photo = this.redPigAccessoryService
						.selectByPrimaryKey(information.getCover());
				fileName = photo.getName();
			}
			map = CommUtil.saveFileToServer(request, "cover", saveFilePathName,
					fileName, null);
			if (fileName.equals("")) {
				if (map.get("fileName") != "") {
					photo = new Accessory();
					photo.setName(CommUtil.null2String(map.get("fileName")));
					photo.setExt(CommUtil.null2String(map.get("mime")));
					photo.setSize(BigDecimal.valueOf(CommUtil.null2Double(map
							.get("fileSize"))));
					photo.setPath(uploadFilePath + "/information_cover");
					photo.setWidth(CommUtil.null2Int(map.get("width")));
					photo.setHeight(CommUtil.null2Int(map.get("height")));
					photo.setAddTime(new Date());
					this.redPigAccessoryService.saveEntity(photo);
					information.setCover(photo.getId());
				}
			} else if (map.get("fileName") != "") {
				photo.setName(CommUtil.null2String(map.get("fileName")));
				photo.setExt(CommUtil.null2String(map.get("mime")));
				photo.setSize(BigDecimal.valueOf(CommUtil.null2Double(map
						.get("fileSize"))));
				photo.setPath(uploadFilePath + "/information_cover");
				photo.setWidth(CommUtil.null2Int(map.get("width")));
				photo.setHeight(CommUtil.null2Int(map.get("height")));
				photo.setAddTime(new Date());
				this.redPigAccessoryService.updateById(photo);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (id.equals("")) {
			this.redPigInformationService.saveEntity(information);
		} else {
			this.redPigInformationService.updateById(information);
		}
		ModelAndView mv = new RedPigJModelAndView("admin/blue/success.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request,
				response);
		mv.addObject("list_url", list_url);
		mv.addObject("op_title", "保存资讯成功");
		if (add_url != null) {
			mv.addObject("add_url", add_url + "?currentPage=" + currentPage);
		}
		return mv;
	}

	/**
	 * 资讯审核
	 * 
	 * @param request
	 * @param response
	 * @param id
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "资讯审核", value = "/information_verify*", rtype = "admin", rname = "资讯管理", rcode = "information_admin", rgroup = "网站")
	@RequestMapping({ "/information_verify" })
	public ModelAndView information_verify(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage) {
		ModelAndView mv = new RedPigJModelAndView(
				"admin/blue/information_verify.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request,
				response);
		if ((id != null) && (!id.equals(""))) {
			Information obj = this.redPigInformationService
					.selectByPrimaryKey(Long.valueOf(Long.parseLong(id)));
			mv.addObject("className", this.redPigInformationClassService
					.selectByPrimaryKey(Long.valueOf(obj.getClassid()))
					.getIc_name());
			mv.addObject("obj", obj);
			mv.addObject("currentPage", currentPage);
			mv.addObject("imageTools", this.imageTools);
		}
		return mv;
	}

	/**
	 * 资讯审核保存
	 * 
	 * @param request
	 * @param response
	 * @param id
	 * @param currentPage
	 * @param status
	 * @param failreason
	 * @return
	 */
	@SecurityMapping(title = "资讯审核保存", value = "/information_verify_save*", rtype = "admin", rname = "资讯管理", rcode = "information_admin", rgroup = "网站")
	@RequestMapping({ "/information_verify_save" })
	public String verify_saveEntity(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage,
			String status, String failreason) {
		if ((id != null) && (!id.equals(""))) {
			Information information = this.redPigInformationService
					.selectByPrimaryKey(Long.valueOf(Long.parseLong(id)));
			information.setStatus(CommUtil.null2Int(status));
			if (CommUtil.null2Int(status) == 10) {
				information.setFailreason("");
			} else {
				information.setFailreason(failreason);
			}
			this.redPigInformationService.updateById(information);
		}
		return "redirect:information_verifylist?currentPage=" + currentPage;
	}

	/**
	 * 资讯删除
	 * 
	 * @param request
	 * @param response
	 * @param mulitId
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "资讯删除", value = "/information_del*", rtype = "admin", rname = "资讯管理", rcode = "information_admin", rgroup = "网站")
	@RequestMapping({ "/information_del" })
	public String information_del(HttpServletRequest request,
			HttpServletResponse response, String mulitId, String currentPage) {
		if (mulitId == null) {
			mulitId = "";
		}
		String[] ids = mulitId.split(",");
		for (String id : ids) {
			if (!id.equals("")) {
				Information information = this.redPigInformationService
						.selectByPrimaryKey(Long.valueOf(Long.parseLong(id)));
				this.redPigInformationService.deleteById(Long.valueOf(Long
						.parseLong(id)));
			}
		}
		return "redirect:information_list?currentPage=" + currentPage;
	}

	/**
	 * 资讯置顶
	 * 
	 * @param request
	 * @param response
	 * @param id
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "资讯置顶", value = "/stick*", rtype = "admin", rname = "资讯管理", rcode = "information_admin", rgroup = "网站")
	@RequestMapping({ "/stick" })
	public String stick(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage) {
		if ((id != null) && (!id.equals(""))) {
			Information information = this.redPigInformationService
					.selectByPrimaryKey(Long.valueOf(Long.parseLong(id)));
			if (information != null) {
				if (information.getSequence() == -10) {
					information.setSequence(0);
				} else {
					information.setSequence(-10);
				}
			}
		}
		return "redirect:information_list?currentPage=" + currentPage;
	}

	/**
	 * 资讯ajax
	 * 
	 * @param request
	 * @param response
	 * @param id
	 * @param fieldName
	 * @param value
	 * @throws ClassNotFoundException
	 */
	@SecurityMapping(title = "资讯ajax", value = "/information_ajax*", rtype = "admin", rname = "资讯管理", rcode = "information_admin", rgroup = "网站")
	@RequestMapping({ "/information_ajax" })
	public void ajax(HttpServletRequest request, HttpServletResponse response,
			String id, String fieldName, String value)
			throws ClassNotFoundException {
		Information obj = this.redPigInformationService.selectByPrimaryKey(Long
				.valueOf(Long.parseLong(id)));
		Field[] fields = Information.class.getDeclaredFields();

		Object val = ObjectUtils.setValue(fieldName, value, obj, fields);

		this.redPigInformationService.updateById(obj);
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
	 * 资讯商品
	 * 
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param gc_id
	 * @param goods_name
	 * @return
	 */
	@SecurityMapping(title = "资讯商品", value = "/information_goods*", rtype = "admin", rname = "资讯管理", rcode = "information_admin", rgroup = "网站")
	@RequestMapping({ "/information_goods" })
	public ModelAndView information_goods(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String gc_id,
			String goods_name) {
		ModelAndView mv = new RedPigJModelAndView(
				"admin/blue/information_goods.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request,
				response);

		Map<String, Object> params = this.redPigQueryTools.getParams(
				currentPage, 5, "addTime", "desc");
		params.put("goods_status", 0);
		params.put("goods_type", 0);

		if (!CommUtil.null2String(gc_id).equals("")) {
			Set<Long> ids = genericIds(this.redPigGoodsClassService
					.selectByPrimaryKey(CommUtil.null2Long(gc_id)));

			params.put("redPig_gc_ids", ids);

			mv.addObject("gc_id", gc_id);
		}
		if (!CommUtil.null2String(goods_name).equals("")) {
			params.put("goods_name_like", goods_name);

			mv.addObject("goods_name", goods_name);
		}
		IPageList pList = this.redPigGoodsService.list(params);
		String photo_url = CommUtil.getURL(request)
				+ "/information_items";
		mv.addObject("goods", pList.getResult());
		mv.addObject("gotoPageAjaxHTML", CommUtil.showPageAjaxHtml(photo_url,
				"", pList.getCurrentPage(), pList.getPages(),
				pList.getPageSize()));
		params.clear();
		params.put("parent", -1);
		List<GoodsClass> gcs = this.redPigGoodsClassService
				.queryPageList(params);

		mv.addObject("gcs", gcs);
		return mv;
	}

	/**
	 * 商品图片
	 * 
	 * @param request
	 * @param response
	 * @param goods_id
	 * @param currentPage
	 * @param gc_id
	 * @param goods_name
	 * @return
	 */
	@SecurityMapping(title = "商品图片", value = "/information_goods_imgs*", rtype = "admin", rname = "资讯管理", rcode = "information_admin", rgroup = "网站")
	@RequestMapping({ "/information_goods_imgs" })
	public ModelAndView information_goods_imgs(HttpServletRequest request,
			HttpServletResponse response, String goods_id, String currentPage,
			String gc_id, String goods_name) {
		ModelAndView mv = new RedPigJModelAndView(
				"admin/blue/information_goods_imgs.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request,
				response);
		Goods goods = this.redPigGoodsService.selectByPrimaryKey(CommUtil
				.null2Long(goods_id));
		List list = Lists.newArrayList();
		if (goods.getGoods_main_photo() != null) {
			list.add(goods.getGoods_main_photo());
			list.addAll(goods.getGoods_photos());
		}
		mv.addObject("photos", list);
		mv.addObject("goods_id", goods_id);
		return mv;
	}

	/**
	 * 资讯预览
	 * 
	 * @param request
	 * @param response
	 * @param id
	 * @return
	 */
	@SecurityMapping(title = "资讯预览", value = "/info_preview*", rtype = "admin", rname = "资讯管理", rcode = "information_admin", rgroup = "网站")
	@RequestMapping({ "/info_preview" })
	public ModelAndView info_preview(HttpServletRequest request,
			HttpServletResponse response, String id) {
		ModelAndView mv = new RedPigJModelAndView("/cms/detail.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 1, request,
				response);
		Information info = this.redPigInformationService
				.selectByPrimaryKey(CommUtil.null2Long(id));
		if (info != null) {
			mv.addObject("obj", info);
			Map<String, Object> map = Maps.newHashMap();
			map.put("addTime_more_than", info.getAddTime());
			map.put("status", 20);

			List<Information> before = this.redPigInformationService
					.queryPageList(map, 0, 1);

			if (before.size() > 0) {
				mv.addObject("before", before.get(0));
			}
			map.clear();
			map.put("addTime_less_than", info.getAddTime());
			map.put("status", 20);
			List<Information> after = this.redPigInformationService
					.queryPageList(map, 0, 1);

			if (after.size() > 0) {
				mv.addObject("after", after.get(0));
			}
			mv.addObject("className", this.redPigInformationClassService
					.selectByPrimaryKey(Long.valueOf(info.getClassid()))
					.getIc_name());
			map.clear();

			List<InformationClass> infoclass = this.redPigInformationClassService
					.queryPageList(map);

			mv.addObject("infoclass", infoclass);
			map.put("status", 20);
			List<Information> hotinfo = this.redPigInformationService
					.queryPageList(map, 0, 5);

			mv.addObject("hotinfo", hotinfo);
		} else {
			mv = new RedPigJModelAndView("error.html",
					this.redPigSysConfigService.getSysConfig(),
					this.redPigUserConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "参数错误，资讯查看失败");
			mv.addObject("url", CommUtil.getURL(request) + "/cms/index");
		}
		return mv;
	}

	/**
	 * 资讯首页管理
	 * 
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param title
	 * @param author
	 * @param classid
	 * @return
	 */
	@SecurityMapping(title = "资讯首页管理", value = "/cms_template_list*", rtype = "admin", rname = "资讯管理", rcode = "information_admin", rgroup = "网站")
	@RequestMapping({ "/cms_template_list" })
	public ModelAndView cms_template_list(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String title,
			String author, String classid) {
		ModelAndView mv = new RedPigJModelAndView(
				"admin/blue/cms_template_list.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request,
				response);
		String url = this.redPigSysConfigService.getSysConfig().getAddress();
		if ((url == null) || (url.equals(""))) {
			url = CommUtil.getURL(request);
		}
		String params = "";

		Map<String, Object> maps = this.redPigQueryTools.getParams(currentPage,
				"sequence", "asc");

		IPageList pList = this.redPigCmsIndexTemplateService.list(maps);
		CommUtil.saveIPageList2ModelAndView(url
				+ "/cms_template_list.html", "", params, pList, mv);

		Map<String, Object> map = Maps.newHashMap();
		map.put("status", Integer.valueOf(20));
		map.put("recommend", Integer.valueOf(1));

		List<Information> informations = this.redPigInformationService
				.queryPageList(map);

		if (informations.size() > 0) {
			mv.addObject("information", informations.get(0));
		}
		return mv;
	}

	/**
	 * 楼层编辑
	 * 
	 * @param request
	 * @param response
	 * @param id
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "楼层编辑", value = "/cms_template_edit*", rtype = "admin", rname = "资讯楼层", rcode = "information_floor", rgroup = "网站")
	@RequestMapping({ "/cms_template_edit" })
	public ModelAndView cms_template_edit(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage) {
		ModelAndView mv = new RedPigJModelAndView(
				"admin/blue/cms_template_add.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request,
				response);
		if ((id != null) && (!id.equals(""))) {
			CmsIndexTemplate cmsindextemplate = this.redPigCmsIndexTemplateService
					.selectByPrimaryKey(Long.valueOf(Long.parseLong(id)));
			mv.addObject("obj", cmsindextemplate);
			mv.addObject("currentPage", currentPage);
			mv.addObject("edit", Boolean.valueOf(true));
		}
		return mv;
	}

	/**
	 * 楼层保存
	 * 
	 * @param request
	 * @param response
	 * @param id
	 * @param currentPage
	 * @param cmd
	 * @param list_url
	 * @param add_url
	 * @return
	 */
	@SecurityMapping(title = "楼层保存", value = "/cms_template_save*", rtype = "admin", rname = "资讯楼层", rcode = "information_floor", rgroup = "网站")
	@RequestMapping({ "/cms_template_save" })
	public ModelAndView cms_template_saveEntity(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage,
			String cmd, String list_url, String add_url) {
		WebForm wf = new WebForm();
		CmsIndexTemplate cmsindextemplate = null;
		if (id.equals("")) {
			cmsindextemplate = (CmsIndexTemplate) WebForm.toPo(request,
					CmsIndexTemplate.class);
			cmsindextemplate.setAddTime(new Date());
		} else {
			CmsIndexTemplate obj = this.redPigCmsIndexTemplateService
					.selectByPrimaryKey(Long.valueOf(Long.parseLong(id)));
			cmsindextemplate = (CmsIndexTemplate) WebForm.toPo(request, obj);
		}
		if (id.equals("")) {
			this.redPigCmsIndexTemplateService.saveEntity(cmsindextemplate);
		} else {
			this.redPigCmsIndexTemplateService.updateById(cmsindextemplate);
		}
		ModelAndView mv = new RedPigJModelAndView("admin/blue/success.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request,
				response);
		mv.addObject("list_url", list_url);
		mv.addObject("op_title", "保存楼层成功成功");
		if (add_url != null) {
			mv.addObject("add_url", add_url + "?currentPage=" + currentPage);
		}
		return mv;
	}

	/**
	 * 资讯首页楼层添加
	 * 
	 * @param request
	 * @param response
	 * @param type
	 * @return
	 */
	@SecurityMapping(title = "资讯首页楼层添加", value = "/cms_template_add*", rtype = "admin", rname = "资讯楼层", rcode = "information_floor", rgroup = "网站")
	@RequestMapping({ "/cms_template_add" })
	public ModelAndView cms_template_add(HttpServletRequest request,
			HttpServletResponse response, String type) {
		ModelAndView mv = new RedPigJModelAndView(
				"admin/blue/cms_template_add.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request,
				response);
		mv.addObject("type", type);
		return mv;
	}

	/**
	 * 资讯首页楼层添加
	 * 
	 * @param request
	 * @param response
	 * @param id
	 * @return
	 */
	@SecurityMapping(title = "资讯首页楼层添加", value = "/cms_template_draw*", rtype = "admin", rname = "资讯楼层", rcode = "information_floor", rgroup = "网站")
	@RequestMapping({ "/cms_template_draw" })
	public ModelAndView cms_template_draw(HttpServletRequest request,
			HttpServletResponse response, String id) {
		ModelAndView mv = new RedPigJModelAndView(
				"admin/blue/cms_template_draw.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request,
				response);
		if ((id != null) && (!id.equals(""))) {
			CmsIndexTemplate cmsindextemplate = this.redPigCmsIndexTemplateService
					.selectByPrimaryKey(Long.valueOf(Long.parseLong(id)));
			mv.addObject("obj", cmsindextemplate);
			mv.addObject("gf_tools", this.redPigGf_tools);
			mv.addObject("imageTools", this.imageTools);
			mv.addObject("circleViewTools", this.redPigCircleViewTools);
		}
		return mv;
	}

	/**
	 * 资讯首页楼层删除
	 * 
	 * @param request
	 * @param response
	 * @param mulitId
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "资讯首页楼层删除", value = "/cms_template_del*", rtype = "admin", rname = "资讯楼层", rcode = "information_floor", rgroup = "网站")
	@RequestMapping({ "/cms_template_del" })
	public String cms_template_del(HttpServletRequest request,
			HttpServletResponse response, String mulitId, String currentPage) {
		String[] ids = mulitId.split(",");
		for (String id : ids) {
			if (!id.equals("")) {
				CmsIndexTemplate cmsindextemplate = this.redPigCmsIndexTemplateService
						.selectByPrimaryKey(Long.valueOf(Long.parseLong(id)));
				this.redPigCmsIndexTemplateService.deleteById(Long.valueOf(Long
						.parseLong(id)));
			}
		}
		return "redirect:cms_template_list?currentPage=" + currentPage;
	}

	/**
	 * 楼层模板商品分类编辑
	 * 
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param id
	 * @return
	 */
	@SecurityMapping(title = "楼层模板商品分类编辑", value = "/cms_template_class*", rtype = "admin", rname = "资讯楼层", rcode = "information_floor", rgroup = "网站")
	@RequestMapping({ "/cms_template_class" })
	public ModelAndView cms_template_class(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String id) {
		ModelAndView mv = new RedPigJModelAndView(
				"admin/blue/cms_template_class.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request,
				response);
		CmsIndexTemplate cmsindextemplate = this.redPigCmsIndexTemplateService
				.selectByPrimaryKey(Long.valueOf(Long.parseLong(id)));

		Map<String, Object> params = Maps.newHashMap();

		params.put("parent", -1);

		List<GoodsClass> gcs = this.redPigGoodsClassService
				.queryPageList(params);
		mv.addObject("gcs", gcs);
		mv.addObject("obj", cmsindextemplate);
		mv.addObject("gf_tools", this.redPigGf_tools);
		mv.addObject("currentPage", currentPage);
		return mv;
	}

	/**
	 * 楼层模板商品分类编辑
	 * 
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param gc_id
	 * @return
	 */
	@SecurityMapping(title = "楼层模板商品分类编辑", value = "/cms_template_class_load*", rtype = "admin", rname = "资讯楼层", rcode = "information_floor", rgroup = "网站")
	@RequestMapping({ "/cms_template_class_load" })
	public ModelAndView cms_template_class_load(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String gc_id) {
		ModelAndView mv = new RedPigJModelAndView(
				"admin/blue/cms_template_class_load.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request,
				response);
		GoodsClass gc = this.redPigGoodsClassService
				.selectByPrimaryKey(CommUtil.null2Long(gc_id));
		mv.addObject("gc", gc);
		return mv;
	}

	/**
	 * 楼层模板商品分类编辑
	 * 
	 * @param request
	 * @param response
	 * @param id
	 * @param ids
	 * @return
	 */
	@SecurityMapping(title = "楼层模板商品分类编辑", value = "/cms_template_class_save*", rtype = "admin", rname = "资讯楼层", rcode = "information_floor", rgroup = "网站")
	@RequestMapping({ "/cms_template_class_save" })
	public String cms_template_class_saveEntity(HttpServletRequest request,
			HttpServletResponse response, String id, String ids) {
		CmsIndexTemplate obj = this.redPigCmsIndexTemplateService
				.selectByPrimaryKey(Long.valueOf(Long.parseLong(id)));
		List gf_gc_list = Lists.newArrayList();
		String[] id_list = ids.split(",pid:");
		for (int i = 0; i < id_list.length; i++) {
			String t_id = id_list[i];
			String[] c_id_list = t_id.split(",");
			Map<String, Object> map = Maps.newHashMap();
			for (String c_id : c_id_list) {
				if (c_id.indexOf("cid") < 0) {
					map.put("pid", c_id);
				} else {
					map.put("gc_id" + i, c_id.substring(4));
				}
			}
			map.put("gc_count", Integer.valueOf(c_id_list.length - 1));
			if (!map.get("pid").toString().equals("")) {
				gf_gc_list.add(map);
			}
		}
		obj.setFloor_info2(JSON.toJSONString(gf_gc_list));
		this.redPigCmsIndexTemplateService.updateById(obj);
		return "redirect:cms_template_draw?id=" + id;
	}

	/**
	 * 楼层模板商品
	 * 
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param id
	 * @param count
	 * @return
	 */
	@SecurityMapping(title = "楼层模板商品", value = "/cms_template_goods*", rtype = "admin", rname = "资讯楼层", rcode = "information_floor", rgroup = "网站")
	@RequestMapping({ "/cms_template_goods" })
	public ModelAndView cms_template_goods(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String id,
			String count) {
		ModelAndView mv = new RedPigJModelAndView(
				"admin/blue/cms_template_goods.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request,
				response);
		CmsIndexTemplate cmsindextemplate = this.redPigCmsIndexTemplateService
				.selectByPrimaryKey(Long.valueOf(Long.parseLong(id)));

		Map<String, Object> params = Maps.newHashMap();
		params.put("parent", -1);

		List<GoodsClass> gcs = this.redPigGoodsClassService
				.queryPageList(params);
		mv.addObject("gcs", gcs);
		mv.addObject("obj", cmsindextemplate);
		mv.addObject("gf_tools", this.redPigGf_tools);
		mv.addObject("currentPage", currentPage);
		mv.addObject("count", count);
		return mv;
	}

	/**
	 * 楼层模板商品
	 * 
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param gc_id
	 * @param goods_name
	 * @param page
	 * @param module_id
	 * @return
	 */
	@SecurityMapping(title = "楼层模板商品", value = "/cms_template_goods_load*", rtype = "admin", rname = "资讯楼层", rcode = "information_floor", rgroup = "网站")
	@RequestMapping({ "/cms_template_goods_load" })
	public ModelAndView cms_template_goods_load(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String gc_id,
			String goods_name, String page, String module_id) {
		ModelAndView mv = new RedPigJModelAndView(
				"admin/blue/cms_template_goods_load.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request,
				response);
		if (!CommUtil.null2String(page).equals("")) {
			mv = new RedPigJModelAndView("admin/blue/" + page + ".html",
					this.redPigSysConfigService.getSysConfig(),
					this.redPigUserConfigService.getUserConfig(), 0, request,
					response);
		}

		Map<String, Object> params = this.redPigQueryTools.getParams(
				currentPage, 14, null, null);

		if (!CommUtil.null2String(gc_id).equals("")) {
			Set<Long> ids = genericIds(this.redPigGoodsClassService
					.selectByPrimaryKey(CommUtil.null2Long(gc_id)));

			params.put("redPig_gc_ids", ids);

		}
		if (!CommUtil.null2String(goods_name).equals("")) {
			params.put("goods_name_like", goods_name);

		}

		params.put("goods_status", 0);

		IPageList pList = this.redPigGoodsService.list(params);
		CommUtil.saveIPageList2ModelAndView(CommUtil.getURL(request)
				+ "/cms_template_goods_load.html", "", "&gc_id=" + gc_id
				+ "&goods_name=" + goods_name, pList, mv);
		mv.addObject("module_id", module_id);
		return mv;
	}

	/**
	 * 楼层模板商品
	 * 
	 * @param request
	 * @param response
	 * @param list_title
	 * @param id
	 * @param ids
	 * @return
	 */
	@SecurityMapping(title = "楼层模板商品", value = "/cms_template_goods_load*", rtype = "admin", rname = "资讯楼层", rcode = "information_floor", rgroup = "网站")
	@RequestMapping({ "/cms_template_goods_save" })
	public String cms_template_goods_saveEntity(HttpServletRequest request,
			HttpServletResponse response, String list_title, String id,
			String ids) {
		CmsIndexTemplate obj = this.redPigCmsIndexTemplateService
				.selectByPrimaryKey(Long.valueOf(Long.parseLong(id)));
		String[] id_list = ids.split(",");
		Map<String, Object> map = Maps.newHashMap();
		map.put("list_title", list_title);
		for (int i = 0; i < id_list.length; i++) {
			if (!id_list[i].equals("")) {
				map.put("goods_id" + i, id_list[i]);
			}
		}
		if ("goods-class".equals(obj.getType())) {
			obj.setFloor_info1(JSON.toJSONString(map));
		}
		if ("goods".equals(obj.getType())) {
			obj.setFloor_info1(JSON.toJSONString(map));
		}
		if ("info-info-goods-brand".equals(obj.getType())) {
			obj.setFloor_info3(JSON.toJSONString(map));
		}
		if ("goods-free-circle".equals(obj.getType())) {
			obj.setFloor_info1(JSON.toJSONString(map));
		}
		this.redPigCmsIndexTemplateService.updateById(obj);
		return "redirect:cms_template_draw?id=" + id;
	}

	/**
	 * 楼层模板资讯
	 * 
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param id
	 * @param count
	 * @param type
	 * @return
	 */
	@SecurityMapping(title = "楼层模板资讯", value = "/cms_template_info*", rtype = "admin", rname = "资讯楼层", rcode = "information_floor", rgroup = "网站")
	@RequestMapping({ "/cms_template_info" })
	public ModelAndView cms_template_info(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String id,
			String count, String type) {
		ModelAndView mv = new RedPigJModelAndView(
				"admin/blue/cms_template_info.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request,
				response);
		CmsIndexTemplate cmsindextemplate = this.redPigCmsIndexTemplateService
				.selectByPrimaryKey(Long.valueOf(Long.parseLong(id)));
		Map<String, Object> params = Maps.newHashMap();
		params.put("ic_pid", -1);

		List<InformationClass> ics = this.redPigInformationClassService
				.queryPageList(params);
		mv.addObject("ics", ics);
		mv.addObject("obj", cmsindextemplate);
		mv.addObject("gf_tools", this.redPigGf_tools);
		mv.addObject("currentPage", currentPage);
		mv.addObject("count", count);
		mv.addObject("type", type);
		mv.addObject("imageTools", this.imageTools);
		mv.addObject("cmsTools", this.cmsTools);
		return mv;
	}

	/**
	 * 楼层模板资讯
	 * 
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param ic_id
	 * @param info_name
	 * @param page
	 * @param module_id
	 * @return
	 */
	@SecurityMapping(title = "楼层模板资讯", value = "/cms_template_info_load*", rtype = "admin", rname = "资讯楼层", rcode = "information_floor", rgroup = "网站")
	@RequestMapping({ "/cms_template_info_load" })
	public ModelAndView cms_template_info_load(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String ic_id,
			String info_name, String page, String module_id) {
		ModelAndView mv = new RedPigJModelAndView(
				"admin/blue/cms_template_info_load.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request,
				response);
		if (!CommUtil.null2String(page).equals("")) {
			mv = new RedPigJModelAndView("admin/blue/" + page + ".html",
					this.redPigSysConfigService.getSysConfig(),
					this.redPigUserConfigService.getUserConfig(), 0, request,
					response);
		}

		Map<String, Object> params = this.redPigQueryTools.getParams(
				currentPage, 14, "addTime", "desc");

		if (!CommUtil.null2String(ic_id).equals("")) {
			Set<Long> ids = genericIds(this.redPigInformationClassService
					.selectByPrimaryKey(CommUtil.null2Long(ic_id)));
			params.put("classIds", ids);
		}
		if (!CommUtil.null2String(info_name).equals("")) {
			params.put("title_like", info_name);

		}

		params.put("status", 20);

		IPageList pList = this.redPigInformationService.list(params);
		CommUtil.saveIPageList2ModelAndView(CommUtil.getURL(request)
				+ "/cms_template_info_load.html", "", "&ic_id=" + ic_id
				+ "&info_name=" + info_name, pList, mv);
		mv.addObject("module_id", module_id);
		mv.addObject("imageTools", this.imageTools);
		return mv;
	}

	/**
	 * 楼层模板资讯
	 * 
	 * @param request
	 * @param response
	 * @param list_title
	 * @param id
	 * @param ids
	 * @param type
	 * @return
	 */
	@SecurityMapping(title = "楼层模板资讯", value = "/cms_template_info_save*", rtype = "admin", rname = "资讯楼层", rcode = "information_floor", rgroup = "网站")
	@RequestMapping({ "/cms_template_info_save" })
	public String cms_template_info_saveEntity(HttpServletRequest request,
			HttpServletResponse response, String list_title, String id,
			String ids, String type) {
		CmsIndexTemplate obj = this.redPigCmsIndexTemplateService
				.selectByPrimaryKey(Long.valueOf(Long.parseLong(id)));

		String[] id_list = ids.split(",");
		Map<String, Object> map = Maps.newHashMap();
		map.put("list_title", list_title);
		for (int i = 0; i < id_list.length; i++) {
			if (!id_list[i].equals("")) {
				map.put("info" + i, id_list[i]);
			}
		}
		if ("floor_info1".equals(type)) {
			obj.setFloor_info1(JSON.toJSONString(map));
		}
		if ("floor_info2".equals(type)) {
			obj.setFloor_info2(JSON.toJSONString(map));
		}
		if ("floor_info3".equals(type)) {
			obj.setFloor_info3(JSON.toJSONString(map));
		}
		if ("floor_info4".equals(type)) {
			obj.setFloor_info4(JSON.toJSONString(map));
		}
		this.redPigCmsIndexTemplateService.updateById(obj);
		return "redirect:cms_template_draw?id=" + id;
	}

	/**
	 * 楼层模板品牌
	 * 
	 * @param request
	 * @param response
	 * @param id
	 * @return
	 */
	@SecurityMapping(title = "楼层模板品牌", value = "/cms_template_brand*", rtype = "admin", rname = "资讯楼层", rcode = "information_floor", rgroup = "网站")
	@RequestMapping({ "/cms_template_brand" })
	public ModelAndView goods_floor_brand(HttpServletRequest request,
			HttpServletResponse response, String id) {
		ModelAndView mv = new RedPigJModelAndView(
				"admin/blue/cms_template_brand.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request,
				response);
		CmsIndexTemplate obj = this.redPigCmsIndexTemplateService
				.selectByPrimaryKey(Long.valueOf(Long.parseLong(id)));

		Map<String, Object> params = this.redPigQueryTools.getParams("1",
				"sequence", "asc");

		params.put("audit", 1);

		IPageList pList = this.redPigGoodsBrandService.list(params);
		CommUtil.saveIPageList2ModelAndView(CommUtil.getURL(request)
				+ "/cms_template_brand_load.html", "", "", pList, mv);
		mv.addObject("obj", obj);
		mv.addObject("gf_tools", this.redPigGf_tools);
		return mv;
	}

	/**
	 * 楼层模板品牌保存
	 * 
	 * @param request
	 * @param response
	 * @param id
	 * @param ids
	 * @return
	 */
	@SecurityMapping(title = "楼层模板品牌保存", value = "/cms_template_brand_save*", rtype = "admin", rname = "资讯楼层", rcode = "information_floor", rgroup = "网站")
	@RequestMapping({ "/cms_template_brand_save" })
	public String goods_floor_brand_saveEntity(HttpServletRequest request,
			HttpServletResponse response, String id, String ids) {
		CmsIndexTemplate obj = this.redPigCmsIndexTemplateService
				.selectByPrimaryKey(Long.valueOf(Long.parseLong(id)));
		String[] id_list = ids.split(",");
		Map<String, Object> map = Maps.newHashMap();
		for (int i = 0; i < id_list.length; i++) {
			if (!id_list[i].equals("")) {
				map.put("brand_id" + i, id_list[i]);
			}
		}
		obj.setFloor_info4(JSON.toJSONString(map));
		this.redPigCmsIndexTemplateService.updateById(obj);
		return "redirect:cms_template_draw?id=" + id;
	}

	/**
	 * 楼层模板品牌加载
	 * 
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param name
	 * @return
	 */
	@SecurityMapping(title = "楼层模板品牌加载", value = "/cms_template_brand_load*", rtype = "admin", rname = "资讯楼层", rcode = "information_floor", rgroup = "网站")
	@RequestMapping({ "/cms_template_brand_load" })
	public ModelAndView cms_template_brand_load(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String name) {
		ModelAndView mv = new RedPigJModelAndView(
				"admin/blue/cms_template_brand_load.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request,
				response);

		Map<String, Object> params = this.redPigQueryTools.getParams(
				currentPage, "sequence", "asc");

		params.put("audit", 1);

		if (!CommUtil.null2String(name).equals("")) {

			params.put("name_like", name.trim());

		}

		IPageList pList = this.redPigGoodsBrandService.list(params);
		CommUtil.saveIPageList2ModelAndView(CommUtil.getURL(request)
				+ "/cms_template_brand_load.html", "", "&name="
				+ CommUtil.null2String(name), pList, mv);
		return mv;
	}

	/**
	 * 楼层模板0元试用
	 * 
	 * @param request
	 * @param response
	 * @param id
	 * @param currentPage
	 * @param count
	 * @return
	 */
	@SecurityMapping(title = "楼层模板0元试用", value = "/cms_template_free*", rtype = "admin", rname = "资讯楼层", rcode = "information_floor", rgroup = "网站")
	@RequestMapping({ "/cms_template_free" })
	public ModelAndView cms_template_free(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage,
			String count) {
		ModelAndView mv = new RedPigJModelAndView(
				"admin/blue/cms_template_free.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request,
				response);
		CmsIndexTemplate cmsindextemplate = this.redPigCmsIndexTemplateService
				.selectByPrimaryKey(Long.valueOf(Long.parseLong(id)));

		Map<String, Object> params = Maps.newHashMap();

		List<FreeClass> fcs = this.redPigFreeClassService.queryPageList(params);

		mv.addObject("fcs", fcs);
		mv.addObject("obj", cmsindextemplate);
		mv.addObject("gf_tools", this.redPigGf_tools);
		mv.addObject("currentPage", currentPage);
		mv.addObject("count", count);
		return mv;
	}

	/**
	 * 楼层模板0元试用
	 * 
	 * @param request
	 * @param response
	 * @param id
	 * @param ids
	 * @return
	 */
	@SecurityMapping(title = "楼层模板0元试用", value = "/cms_template_free_save*", rtype = "admin", rname = "资讯楼层", rcode = "information_floor", rgroup = "网站")
	@RequestMapping({ "/cms_template_free_save" })
	public String cms_template_free_saveEntity(HttpServletRequest request,
			HttpServletResponse response, String id, String ids) {
		CmsIndexTemplate obj = this.redPigCmsIndexTemplateService
				.selectByPrimaryKey(Long.valueOf(Long.parseLong(id)));
		String[] id_list = ids.split(",");
		Map<String, Object> map = Maps.newHashMap();
		for (int i = 0; i < id_list.length; i++) {
			if (!id_list[i].equals("")) {
				map.put("free_id" + i, id_list[i]);
			}
		}
		obj.setFloor_info2(JSON.toJSONString(map));
		this.redPigCmsIndexTemplateService.updateById(obj);
		return "redirect:cms_template_draw?id=" + id;
	}

	/**
	 * 楼层模板0元试用
	 * 
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param name
	 * @param page
	 * @param fc_id
	 * @param free_name
	 * @param module_id
	 * @return
	 */
	@SecurityMapping(title = "楼层模板0元试用", value = "/cms_template_free_load*", rtype = "admin", rname = "资讯楼层", rcode = "information_floor", rgroup = "网站")
	@RequestMapping({ "/cms_template_free_load" })
	public ModelAndView cms_template_free_load(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String name,
			String page, String fc_id, String free_name, String module_id) {
		ModelAndView mv = new RedPigJModelAndView(
				"admin/blue/cms_template_free_load.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request,
				response);
		if (!CommUtil.null2String(page).equals("")) {
			mv = new RedPigJModelAndView("admin/blue/" + page + ".html",
					this.redPigSysConfigService.getSysConfig(),
					this.redPigUserConfigService.getUserConfig(), 0, request,
					response);
		}
		Map<String, Object> params = this.redPigQueryTools.getParams(
				currentPage, 14, null, null);

		if (!CommUtil.null2String(fc_id).equals("")) {
			params.put("class_id", CommUtil.null2Long(fc_id));
		}

		if (!CommUtil.null2String(free_name).equals("")) {
			params.put("free_name_like", free_name);
		}
		params.put("freeStatus", 5);

		IPageList pList = this.redPigFreeGoodsService.list(params);

		CommUtil.saveIPageList2ModelAndView(CommUtil.getURL(request)
				+ "/cms_template_free_load.html", "", "&fc_id=" + fc_id
				+ "&free_name=" + free_name, pList, mv);
		mv.addObject("module_id", module_id);
		return mv;
	}

	/**
	 * 楼层模板圈子
	 * 
	 * @param request
	 * @param response
	 * @param id
	 * @param currentPage
	 * @param count
	 * @return
	 */
	@SecurityMapping(title = "楼层模板圈子", value = "/cms_template_circle*", rtype = "admin", rname = "资讯楼层", rcode = "information_floor", rgroup = "网站")
	@RequestMapping({ "/cms_template_circle" })
	public ModelAndView cms_template_circle(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage,
			String count) {
		ModelAndView mv = new RedPigJModelAndView(
				"admin/blue/cms_template_circle.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request,
				response);
		CmsIndexTemplate cmsindextemplate = this.redPigCmsIndexTemplateService
				.selectByPrimaryKey(Long.valueOf(Long.parseLong(id)));

		Map<String, Object> params = Maps.newHashMap();

		List<CircleClass> ccs = this.redPigCircleClassService
				.queryPageList(params);

		mv.addObject("ccs", ccs);
		mv.addObject("obj", cmsindextemplate);
		mv.addObject("gf_tools", this.redPigGf_tools);
		mv.addObject("currentPage", currentPage);
		mv.addObject("count", count);
		mv.addObject("circleViewTools", this.redPigCircleViewTools);
		return mv;
	}

	/**
	 * 楼层模板圈子
	 * 
	 * @param request
	 * @param response
	 * @param id
	 * @param ids
	 * @return
	 */
	@SecurityMapping(title = "楼层模板圈子", value = "/cms_template_circle_save*", rtype = "admin", rname = "资讯楼层", rcode = "information_floor", rgroup = "网站")
	@RequestMapping({ "/cms_template_circle_save" })
	public String cms_template_circle_saveEntity(HttpServletRequest request,
			HttpServletResponse response, String id, String ids) {
		CmsIndexTemplate obj = this.redPigCmsIndexTemplateService
				.selectByPrimaryKey(Long.valueOf(Long.parseLong(id)));
		String[] id_list = ids.split(",");
		Map<String, Object> map = Maps.newHashMap();
		for (int i = 0; i < id_list.length; i++) {
			if (!id_list[i].equals("")) {
				map.put("circle_id" + i, id_list[i]);
			}
		}
		obj.setFloor_info3(JSON.toJSONString(map));
		this.redPigCmsIndexTemplateService.updateById(obj);
		return "redirect:cms_template_draw?id=" + id;
	}

	/**
	 * 楼层模板圈子
	 * 
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param name
	 * @param page
	 * @param cc_id
	 * @param circle_name
	 * @param module_id
	 * @return
	 */
	@SecurityMapping(title = "楼层模板圈子", value = "/cms_template_circle_load*", rtype = "admin", rname = "资讯楼层", rcode = "information_floor", rgroup = "网站")
	@RequestMapping({ "/cms_template_circle_load" })
	public ModelAndView cms_template_circle_load(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String name,
			String page, String cc_id, String circle_name, String module_id) {
		ModelAndView mv = new RedPigJModelAndView(
				"admin/blue/cms_template_circle_load.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request,
				response);
		if (!CommUtil.null2String(page).equals("")) {
			mv = new RedPigJModelAndView("admin/blue/" + page + ".html",
					this.redPigSysConfigService.getSysConfig(),
					this.redPigUserConfigService.getUserConfig(), 0, request,
					response);
		}

		Map<String, Object> params = this.redPigQueryTools.getParams(
				currentPage, 14, null, null);

		if (!CommUtil.null2String(cc_id).equals("")) {
			params.put("class_id", CommUtil.null2Long(cc_id));
		}

		if (!CommUtil.null2String(circle_name).equals("")) {
			params.put("title_like", circle_name);
		}
		params.put("status", 5);

		IPageList pList = this.redPigCircleService.list(params);
		CommUtil.saveIPageList2ModelAndView(CommUtil.getURL(request)
				+ "/cms_template_free_load.html", "", "&cc_id=" + cc_id
				+ "&circle_name=" + circle_name, pList, mv);
		mv.addObject("module_id", module_id);
		mv.addObject("circleViewTools", this.redPigCircleViewTools);
		return mv;
	}

	private Set<Long> genericIds(InformationClass ic) {
		Set<Long> ids = new HashSet();
		ids.add(ic.getId());
		Map<String, Object> params = Maps.newHashMap();
		params.put("ic_pid", ic.getId());

		List<InformationClass> ics = this.redPigInformationClassService
				.queryPageList(params);

		for (InformationClass child : ics) {
			Set<Long> cids = genericIds(child);
			for (Long cid : cids) {
				ids.add(cid);
			}
			ids.add(child.getId());
		}
		return ids;
	}

	private Set<Long> genericIds(GoodsClass gc) {
		Set<Long> ids = new HashSet();
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
