package com.redpigmall.manage.admin.action;

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
import com.google.common.collect.Maps;
import com.redpigmall.api.annotation.SecurityMapping;
import com.redpigmall.api.mv.RedPigJModelAndView;
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.api.tools.CommUtil;
import com.redpigmall.api.tools.ObjectUtils;
import com.redpigmall.api.tools.WebForm;
import com.redpigmall.manage.admin.action.base.BaseAction;
import com.redpigmall.domain.Accessory;
import com.redpigmall.domain.CloudPurchaseClass;
import com.redpigmall.domain.CloudPurchaseGoods;
import com.redpigmall.view.web.tools.RedPigResponseTools;

/**
 * 
 * <p>
 * Title: RedPigCloudPurchaseClassManageAction.java
 * </p>
 * 
 * <p>
 * Description: 云购分类管理
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
public class RedPigCloudPurchaseClassManageAction extends BaseAction {

	/**
	 * 云购分类列表
	 * 
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @return
	 */
	@SecurityMapping(title = "云购分类列表", value = "/cloudpurchaseclass_list*", rtype = "admin", rname = "云购分类", rcode = "admin_cloudpurchase_class", rgroup = "运营")
	@RequestMapping({ "/cloudpurchaseclass_list" })
	public ModelAndView list(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,
			String orderType) {
		ModelAndView mv = new RedPigJModelAndView(
				"admin/blue/cloudpurchaseclass_list.html",
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

		IPageList pList = this.redPigCloudpurchaseclassService.list(maps);
		CommUtil.saveIPageList2ModelAndView(url
				+ "/cloudpurchaseclass_list.html", "", params, pList, mv);
		return mv;
	}

	/**
	 * 云购分类添加
	 * 
	 * @param request
	 * @param response
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "云购分类添加", value = "/cloudpurchaseclass_add*", rtype = "admin", rname = "云购分类", rcode = "admin_cloudpurchase_class", rgroup = "运营")
	@RequestMapping({ "/cloudpurchaseclass_add" })
	public ModelAndView add(HttpServletRequest request,
			HttpServletResponse response, String currentPage) {
		ModelAndView mv = new RedPigJModelAndView(
				"admin/blue/cloudpurchaseclass_add.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request,
				response);
		mv.addObject("currentPage", currentPage);

		String[] strs = this.redPigSysConfigService.getSysConfig()
				.getImageSuffix().split("\\|");
		StringBuffer sb = new StringBuffer();

		for (String str : strs) {

			sb.append("." + str + ",");
		}
		mv.addObject("imageSuffix1", sb);
		return mv;
	}

	/**
	 * 云购分类编辑
	 * 
	 * @param request
	 * @param response
	 * @param id
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "云购分类编辑", value = "/cloudpurchaseclass_edit*", rtype = "admin", rname = "云购分类", rcode = "admin_cloudpurchase_class", rgroup = "运营")
	@RequestMapping({ "/cloudpurchaseclass_edit" })
	public ModelAndView edit(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage) {
		ModelAndView mv = new RedPigJModelAndView(
				"admin/blue/cloudpurchaseclass_add.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request,
				response);
		if ((id != null) && (!id.equals(""))) {
			CloudPurchaseClass cloudpurchaseclass = this.redPigCloudpurchaseclassService
					.selectByPrimaryKey(Long.valueOf(Long.parseLong(id)));
			mv.addObject("obj", cloudpurchaseclass);
			mv.addObject("currentPage", currentPage);
			mv.addObject("edit", Boolean.valueOf(true));
		}
		String[] strs = this.redPigSysConfigService.getSysConfig()
				.getImageSuffix().split("\\|");
		StringBuffer sb = new StringBuffer();

		for (String str : strs) {

			sb.append("." + str + ",");
		}
		mv.addObject("imageSuffix1", sb);
		return mv;
	}

	/**
	 * 云购分类保存
	 * 
	 * @param request
	 * @param response
	 * @param id
	 * @param currentPage
	 * @param cmd
	 * @param list_url
	 * @param add_url
	 * @param class_name
	 * @param img_url
	 * @return
	 */
	@SecurityMapping(title = "云购分类保存", value = "/cloudpurchaseclass_save*", rtype = "admin", rname = "云购分类", rcode = "admin_cloudpurchase_class", rgroup = "运营")
	@RequestMapping({ "/cloudpurchaseclass_save" })
	public ModelAndView saveEntity(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage,
			String cmd, String list_url, String add_url, String class_name,
			String img_url) {

		CloudPurchaseClass cloudpurchaseclass = null;
		if (id.equals("")) {
			cloudpurchaseclass = (CloudPurchaseClass) WebForm.toPo(request,
					CloudPurchaseClass.class);
			cloudpurchaseclass.setAddTime(new Date());
		} else {
			CloudPurchaseClass obj = this.redPigCloudpurchaseclassService
					.selectByPrimaryKey(Long.valueOf(Long.parseLong(id)));
			cloudpurchaseclass = (CloudPurchaseClass) WebForm
					.toPo(request, obj);
		}
		cloudpurchaseclass.setClass_name(class_name);
		cloudpurchaseclass.setImg_url(img_url);
		if (id.equals("")) {
			this.redPigCloudpurchaseclassService.saveEntity(cloudpurchaseclass);
		} else {
			this.redPigCloudpurchaseclassService.updateById(cloudpurchaseclass);
		}
		ModelAndView mv = new RedPigJModelAndView("admin/blue/success.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request,
				response);
		mv.addObject("list_url", list_url);
		mv.addObject("op_title", "保存云购分类成功");
		if (add_url != null) {
			mv.addObject("add_url", add_url + "?currentPage=" + currentPage);
		}
		return mv;
	}

	/**
	 * 云购分类删除
	 * 
	 * @param request
	 * @param response
	 * @param mulitId
	 * @param currentPage
	 */
	@SecurityMapping(title = "云购分类删除", value = "/cloudpurchaseclass_del*", rtype = "admin", rname = "云购分类", rcode = "admin_cloudpurchase_class", rgroup = "运营")
	@RequestMapping({ "/cloudpurchaseclass_del" })
	public void deleteById(HttpServletRequest request,
			HttpServletResponse response, String mulitId, String currentPage) {
		if (mulitId == null) {
			mulitId = "";
		}
		String[] ids = mulitId.split(",");

		for (String id : ids) {

			if (!id.equals("")) {
				CloudPurchaseClass cloudpurchaseclass = this.redPigCloudpurchaseclassService
						.selectByPrimaryKey(Long.valueOf(Long.parseLong(id)));
				Map<String, Object> map = Maps.newHashMap();
				map.put("class_id", cloudpurchaseclass.getId());
				List<CloudPurchaseGoods> goods_list = this.redPigCloudpurchaseGoodsService
						.queryPageList(map);

				int count = 0;
				if (goods_list.size() > 0) {
					count = CommUtil.null2Int(goods_list.get(0));
				}
				if (count == 0) {
					this.redPigCloudpurchaseclassService.deleteById(Long
							.valueOf(Long.parseLong(id)));
					response.setContentType("text/plain");
					response.setHeader("Cache-Control", "no-cache");
					response.setCharacterEncoding("UTF-8");
					try {
						PrintWriter writer = response.getWriter();
						writer.print("yes");
					} catch (IOException e) {
						e.printStackTrace();
					}
				} else {
					response.setContentType("text/plain");
					response.setHeader("Cache-Control", "no-cache");
					response.setCharacterEncoding("UTF-8");
					try {
						PrintWriter writer = response.getWriter();
						writer.print("no");
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	/**
	 * 云购分类ajax
	 * 
	 * @param request
	 * @param response
	 * @param id
	 * @param fieldName
	 * @param value
	 * @throws ClassNotFoundException
	 */
	@SecurityMapping(title = "云购分类ajax", value = "/cloudpurchaseclass_ajax*", rtype = "admin", rname = "云购分类", rcode = "admin_cloudpurchase_class", rgroup = "运营")
	@RequestMapping({ "/cloudpurchaseclass_ajax" })
	public void ajax(HttpServletRequest request, HttpServletResponse response,
			String id, String fieldName, String value)
			throws ClassNotFoundException {
		CloudPurchaseClass obj = this.redPigCloudpurchaseclassService
				.selectByPrimaryKey(Long.valueOf(Long.parseLong(id)));
		Field[] fields = CloudPurchaseClass.class.getDeclaredFields();

		Object val = ObjectUtils.setValue(fieldName, value, obj, fields);

		this.redPigCloudpurchaseclassService.updateById(obj);
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
	 * 云购分类图片上传
	 * 
	 * @param request
	 * @param response
	 * @param id
	 */
	@SecurityMapping(title = "云购分类图片上传", value = "/cloudpurchaseclass_upload*", rtype = "admin", rname = "云购分类", rcode = "admin_cloudpurchase_class", rgroup = "运营")
	@RequestMapping({ "/cloudpurchaseclass_upload" })
	public void cloudpurchaseclass_upload(HttpServletRequest request,
			HttpServletResponse response, String id) {
		String uploadFilePath = this.redPigSysConfigService.getSysConfig()
				.getUploadFilePath();
		String saveFilePathName = request.getSession().getServletContext()
				.getRealPath("/")
				+ uploadFilePath + File.separator + "cloudpurchaseclass";
		Map<String, Object> map = Maps.newHashMap();
		Map<String, Object> map_app = Maps.newHashMap();
		try {
			map_app = CommUtil.saveFileToServer(request, "class_img",saveFilePathName, "", null);
			if (map_app.get("fileName") != "") {
				Accessory photo = new Accessory();
				photo.setName(CommUtil.null2String(map_app.get("fileName")));
				photo.setExt(CommUtil.null2String(map_app.get("mime")));
				photo.setSize(BigDecimal.valueOf(CommUtil.null2Double(map_app
						.get("fileSize"))));
				photo.setPath(uploadFilePath + "/cloudpurchaseclass");
				photo.setWidth(CommUtil.null2Int(map_app.get("width")));
				photo.setHeight(CommUtil.null2Int(map_app.get("height")));
				photo.setAddTime(new Date());
				this.redPigAccessoryService.saveEntity(photo);
				map.put("src", this.redPigSysConfigService.getSysConfig().getImageWebServer() + "/" + photo.getPath() + "/" + photo.getName());
				CloudPurchaseClass cpc = this.redPigCloudpurchaseclassService.selectByPrimaryKey(Long.parseLong(id));
				cpc.setImg_url("/" + photo.getPath() + "/" + photo.getName());
				this.redPigCloudpurchaseclassService.updateById(cpc);
				
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		RedPigResponseTools.sendJson(response, JSON.toJSONString(map));
	}
}
