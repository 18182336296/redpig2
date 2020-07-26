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
import com.redpigmall.api.tools.RedPigMaps;
import com.redpigmall.api.tools.WebForm;
import com.redpigmall.manage.admin.action.base.BaseAction;
import com.redpigmall.domain.Accessory;
import com.redpigmall.domain.CloudPurchaseClass;
import com.redpigmall.domain.CloudPurchaseGoods;
import com.redpigmall.domain.CloudPurchaseLottery;
import com.redpigmall.view.web.tools.RedPigResponseTools;

/**
 * 
 * <p>
 * Title: RedPigCloudPurchaseGoodsManageAction.java
 * </p>
 * 
 * <p>
 * Description: 云购商品管理
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
public class RedPigCloudPurchaseGoodsManageAction extends BaseAction {

	/**
	 * 云购商品列表
	 * 
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @param goods_name
	 * @param goods_status
	 * @param class_id
	 * @return
	 */
	@SecurityMapping(title = "云购商品列表", value = "/cloudpurchasegoods_list*", rtype = "admin", rname = "云购商品", rcode = "admin_cloudpurchase_goods", rgroup = "运营")
	@RequestMapping({ "/cloudpurchasegoods_list" })
	public ModelAndView list(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,
			String orderType, String goods_name, String goods_status,
			String class_id) {
		ModelAndView mv = new RedPigJModelAndView(
				"admin/blue/cloudpurchasegoods_list.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request,
				response);
		String url = this.redPigSysConfigService.getSysConfig().getAddress();
		if ((url == null) || (url.equals(""))) {
			url = CommUtil.getURL(request);
		}
		String params = "";
		Map<String, Object> maps = this.redPigQueryTools.getParams(currentPage,
				orderBy, orderType);

		if ((goods_name != null) && (!goods_name.equals(""))) {
			maps.put("goods_name_like", goods_name);

			mv.addObject("goods_name", goods_name);
		}

		if ((goods_status != null) && (!goods_status.equals(""))) {
			maps.put("goods_status", CommUtil.null2Int(goods_status));

			mv.addObject("goods_status", goods_status);
		}

		if ((class_id != null) && (!class_id.equals(""))) {
			maps.put("class_id", CommUtil.null2Long(class_id));

			mv.addObject("class_id", class_id);
		}

		IPageList pList = this.redPigCloudpurchasegoodsService.list(maps);

		List<CloudPurchaseClass> goodsclass = this.redPigCloudPurchaseClassService
				.queryPageList(RedPigMaps.newMap());

		mv.addObject("goodsclass", goodsclass);
		CommUtil.saveIPageList2ModelAndView(url
				+ "/cloudpurchasegoods_list.html", "", params, pList, mv);
		return mv;
	}

	/**
	 * 云购商品添加
	 * 
	 * @param request
	 * @param response
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "云购商品添加", value = "/cloudpurchasegoods_add*", rtype = "admin", rname = "云购商品", rcode = "admin_cloudpurchase_goods", rgroup = "运营")
	@RequestMapping({ "/cloudpurchasegoods_add" })
	public ModelAndView add(HttpServletRequest request,
			HttpServletResponse response, String currentPage) {
		ModelAndView mv = new RedPigJModelAndView(
				"admin/blue/cloudpurchasegoods_add.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request,
				response);
		List<CloudPurchaseClass> goodsclass = this.redPigCloudPurchaseClassService
				.queryPageList(RedPigMaps.newMap());
		mv.addObject("goodsclass", goodsclass);
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
	 * 云购商品编辑
	 * 
	 * @param request
	 * @param response
	 * @param id
	 * @param currentPage
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	@SecurityMapping(title = "云购商品编辑", value = "/cloudpurchasegoods_edit*", rtype = "admin", rname = "云购商品", rcode = "admin_cloudpurchase_goods", rgroup = "运营")
	@RequestMapping({ "/cloudpurchasegoods_edit" })
	public ModelAndView edit(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage) {
		ModelAndView mv = new RedPigJModelAndView(
				"admin/blue/cloudpurchasegoods_add.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request,
				response);
		if ((id != null) && (!id.equals(""))) {
			CloudPurchaseGoods cloudpurchasegoods = this.redPigCloudpurchasegoodsService
					.selectByPrimaryKey(Long.valueOf(Long.parseLong(id)));
			if ((cloudpurchasegoods != null)
					&& (!cloudpurchasegoods.getSecondary_photo().equals(""))) {
				List url_list = JSON.parseArray(
						cloudpurchasegoods.getSecondary_photo(), String.class);
				mv.addObject("photos", url_list);
			}
			mv.addObject("obj", cloudpurchasegoods);
			mv.addObject("currentPage", currentPage);
			mv.addObject("edit", Boolean.valueOf(true));
		}
		List<CloudPurchaseClass> goodsclass = this.redPigCloudPurchaseClassService
				.queryPageList(RedPigMaps.newMap());
		mv.addObject("goodsclass", goodsclass);

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
	 * 云购商品保存
	 * 
	 * @param request
	 * @param response
	 * @param id
	 * @param currentPage
	 * @param cmd
	 * @param list_url
	 * @param add_url
	 * @param primaryimg_url
	 * @param secondaryimg_url
	 * @param pay_type
	 * @return
	 */
	@SecurityMapping(title = "云购商品保存", value = "/cloudpurchasegoods_save*", rtype = "admin", rname = "云购商品", rcode = "admin_cloudpurchase_goods", rgroup = "运营")
	@RequestMapping({ "/cloudpurchasegoods_save" })
	public ModelAndView saveEntity(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage,
			String cmd, String list_url, String add_url, String primaryimg_url,
			String secondaryimg_url, String pay_type) {

		CloudPurchaseGoods cloudpurchasegoods = null;

		if (id.equals("")) {
			cloudpurchasegoods = (CloudPurchaseGoods) WebForm.toPo(request,
					CloudPurchaseGoods.class);
			cloudpurchasegoods.setAddTime(new Date());
		} else {
			CloudPurchaseGoods obj = this.redPigCloudpurchasegoodsService
					.selectByPrimaryKey(Long.valueOf(Long.parseLong(id)));

			int yuanNum = obj.getGoodsNum();
			cloudpurchasegoods = (CloudPurchaseGoods) WebForm
					.toPo(request, obj);
			int updNum = cloudpurchasegoods.getGoodsNum();
			if ((updNum == 0) && (yuanNum > 0)) {
				List<CloudPurchaseLottery> lottery_list = null;
				Map<String, Object> params = Maps.newHashMap();
				params.put("goods_id", cloudpurchasegoods.getId());
				params.put("status", 5);

				lottery_list = this.redPigCloudPurchaseLotteryService
						.queryPageList(params);

				if (lottery_list.size() > 0) {
					cloudpurchasegoods.setGoodsNum(yuanNum);
				}
			}
		}
		cloudpurchasegoods.setPrimary_photo(primaryimg_url);
		cloudpurchasegoods.setSecondary_photo(secondaryimg_url);
		cloudpurchasegoods.setPay_type(Integer.parseInt(pay_type));
		if ((CommUtil.null2Long(pay_type).longValue() == 0L)
				&& (cloudpurchasegoods.getLeast_rmb() == 10)
				&& (cloudpurchasegoods.getGoods_price() % 10 != 0)) {
			cloudpurchasegoods.setGoods_price(10 * (cloudpurchasegoods
					.getGoods_price() / 10 + 1));
		}
		if (id.equals("")) {
			this.redPigCloudpurchasegoodsService.saveEntity(cloudpurchasegoods);
		} else {
			this.redPigCloudpurchasegoodsService.updateById(cloudpurchasegoods);
		}
		if (cloudpurchasegoods.getGoodsNum() > 0) {
			List<CloudPurchaseLottery> lottery_list = null;
			Map<String, Object> params = Maps.newHashMap();
			params.put("goods_id", cloudpurchasegoods.getId());

			lottery_list = this.redPigCloudPurchaseLotteryService
					.queryPageList(params);

			if (lottery_list.size() == 0) {
				this.redPigCloudPurchaseLotteryService
						.newLottery(cloudpurchasegoods.getId());
			}
		}
		ModelAndView mv = new RedPigJModelAndView("admin/blue/success.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request,
				response);
		mv.addObject("list_url", list_url);
		mv.addObject("op_title", "保存云购商品成功");
		if (add_url != null) {
			mv.addObject("add_url", add_url + "?currentPage=" + currentPage);
		}
		return mv;
	}

	/**
	 * 云购商品上下架
	 * 
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @param id
	 * @param goods_status
	 * @return
	 */
	@SecurityMapping(title = "云购商品上下架", value = "/cloudpurchasegoods_carriage*", rtype = "admin", rname = "云购商品", rcode = "admin_cloudpurchase_goods", rgroup = "运营")
	@RequestMapping({ "/cloudpurchasegoods_carriage" })
	public String carriage(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,
			String orderType, String id, String goods_status) {
		CloudPurchaseGoods goods = this.redPigCloudpurchasegoodsService
				.selectByPrimaryKey(CommUtil.null2Long(id));
		goods.setGoods_status(CommUtil.null2Int(goods_status));
		this.redPigCloudpurchasegoodsService.updateById(goods);
		if (goods_status.equals("0")) {
			List<CloudPurchaseLottery> lottery_list = null;
			Map<String, Object> params = Maps.newHashMap();
			params.put("goods_id", CommUtil.null2Long(id));
			params.put("status", 5);
			lottery_list = this.redPigCloudPurchaseLotteryService
					.queryPageList(params);
			if (lottery_list.size() == 0) {
				this.redPigCloudPurchaseLotteryService
						.newLottery(goods.getId());
			}
		}
		return "redirect:cloudpurchasegoods_list?currentPage=" + currentPage;
	}

	/**
	 * 云购商品ajax
	 * 
	 * @param request
	 * @param response
	 * @param id
	 * @param fieldName
	 * @param value
	 * @throws ClassNotFoundException
	 */
	@SecurityMapping(title = "云购商品ajax", value = "/cloudpurchasegoods_ajax*", rtype = "admin", rname = "云购商品", rcode = "admin_cloudpurchase_goods", rgroup = "运营")
	@RequestMapping({ "/cloudpurchasegoods_ajax" })
	public void ajax(HttpServletRequest request, HttpServletResponse response,
			String id, String fieldName, String value)
			throws ClassNotFoundException {
		CloudPurchaseGoods obj = this.redPigCloudpurchasegoodsService
				.selectByPrimaryKey(Long.valueOf(Long.parseLong(id)));
		Field[] fields = CloudPurchaseGoods.class.getDeclaredFields();

		Object val = ObjectUtils.setValue(fieldName, value, obj, fields);

		this.redPigCloudpurchasegoodsService.updateById(obj);
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
	 * 云购发布图片上传
	 * 
	 * @param request
	 * @param response
	 * @param img_class
	 */
	@SecurityMapping(title = "云购发布图片上传", value = "/cloudpurchaserelease_upload*", rtype = "admin", rname = "云购商品", rcode = "admin_cloudpurchase_goods", rgroup = "运营")
	@RequestMapping({ "/cloudpurchaserelease_upload" })
	public void cloudpurchaseclass_upload(HttpServletRequest request,
			HttpServletResponse response, String img_class) {
		String uploadFilePath = this.redPigSysConfigService.getSysConfig()
				.getUploadFilePath();
		String saveFilePathName = request.getSession().getServletContext()
				.getRealPath("/")
				+ uploadFilePath + File.separator + "cloudpurchaserelease";
		Map<String, Object> map = Maps.newHashMap();
		Map<String, Object> map_app = Maps.newHashMap();
		try {
			map_app = CommUtil.saveFileToServer(request, img_class,
					saveFilePathName, "", null);
			if (map_app.get("fileName") != "") {
				Accessory photo = new Accessory();
				photo.setName(CommUtil.null2String(map_app.get("fileName")));
				photo.setExt(CommUtil.null2String(map_app.get("mime")));
				photo.setSize(BigDecimal.valueOf(CommUtil.null2Double(map_app
						.get("fileSize"))));
				photo.setPath(uploadFilePath + "/cloudpurchaserelease");
				photo.setWidth(CommUtil.null2Int(map_app.get("width")));
				photo.setHeight(CommUtil.null2Int(map_app.get("height")));
				photo.setAddTime(new Date());
				this.redPigAccessoryService.saveEntity(photo);
				map.put("src", CommUtil.getURL(request) + "/" + photo.getPath()
						+ "/" + photo.getName());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		RedPigResponseTools.sendJson(response, JSON.toJSONString(map));
	}
}
