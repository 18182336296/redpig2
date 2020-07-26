package com.redpigmall.manage.seller.action;

import java.io.File;
import java.io.IOException;
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

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.redpigmall.api.annotation.SecurityMapping;
import com.redpigmall.api.mv.RedPigJModelAndView;
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.api.sec.SecurityUserHolder;
import com.redpigmall.api.tools.CommUtil;
import com.redpigmall.api.tools.RedPigMaps;
import com.redpigmall.api.tools.WebForm;
import com.redpigmall.domain.Accessory;
import com.redpigmall.domain.Goods;
import com.redpigmall.domain.GoodsClass;
import com.redpigmall.domain.Information;
import com.redpigmall.domain.InformationClass;
import com.redpigmall.domain.Store;
import com.redpigmall.domain.User;
import com.redpigmall.manage.seller.action.base.BaseAction;


/**
 * 
 * <p>
 * Title: RedPigInformationManageAction.java
 * </p>
 * 
 * <p>
 * Description:资讯管理；发布，提交审核
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
 * @date 2014-12-4
 * 
 * @version redpigmall_b2b2c 8.0
 */
@SuppressWarnings("unchecked")
@Controller
public class RedPigInformationSellerAction extends BaseAction{
	
	/**
	 * 资讯列表
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param title
	 * @param author
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	@SecurityMapping(title = "资讯列表", value = "/information_list*", rtype = "seller", rname = "资讯管理", rcode = "information_seller", rgroup = "其他管理")
	@RequestMapping({ "/information_list" })
	public ModelAndView information_list(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String title,
			String author) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/sellercenter/information_list.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		
		Map<String,Object> maps= this.redPigQueryTools.getParams(currentPage,"addTime", "desc");
       
        
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		Store store = user.getStore();
		maps.put("store_id", store.getId());
		 
		if ((title != null) && (!title.equals(""))) {
			
			maps.put("title_like", title);
			
			mv.addObject("title", title);
		}
		if ((author != null) && (!author.equals(""))) {
			
			maps.put("author_like", author);
			mv.addObject("author", author);
		}
		IPageList pList = this.informationService.list(maps);
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		
		List<InformationClass> infoclass = this.informationClassService.queryPageList(RedPigMaps.newMap());
		
		Map map = Maps.newHashMap();
		for (InformationClass informationClass : infoclass) {
			map.put(informationClass.getId(), informationClass.getIc_name());
		}
		mv.addObject("classmap", map);
		return mv;
	}
	
	/**
	 * 资讯添加
	 * @param request
	 * @param response
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "资讯添加", value = "/information_add*", rtype = "seller", rname = "资讯管理", rcode = "information_seller", rgroup = "其他管理")
	@RequestMapping({ "/information_add" })
	public ModelAndView information_add(HttpServletRequest request,
			HttpServletResponse response, String currentPage) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/sellercenter/information_add.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		mv.addObject("currentPage", currentPage);
		
		Map<String,Object> maps = Maps.newHashMap();
        maps.put("ic_pid", -1);
		
		List<InformationClass> infoclass = this.informationClassService.queryPageList(maps);
		
		mv.addObject("cmsTools", this.cmsTools);
		mv.addObject("infoclass", infoclass);
		String goods_session = CommUtil.randomString(32);
		mv.addObject("goods_session", goods_session);
		request.getSession(false).setAttribute("goods_session", goods_session);
		return mv;
	}
	
	/**
	 * 资讯编辑
	 * @param request
	 * @param response
	 * @param id
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "资讯编辑", value = "/information_edit*", rtype = "seller", rname = "资讯管理", rcode = "information_seller", rgroup = "其他管理")
	@RequestMapping({ "/information_edit" })
	public ModelAndView information_edit(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/sellercenter/information_add.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		if ((id != null) && (!id.equals(""))) {
			Information information = this.informationService.selectByPrimaryKey(Long
					.valueOf(Long.parseLong(id)));
			User user = this.userService.selectByPrimaryKey(SecurityUserHolder
					.getCurrentUser().getId());
			user = user.getParent() == null ? user : user.getParent();
			Store store = user.getStore();
			if (information.getStore_id().equals(store.getId())) {
				mv.addObject("obj", information);
				mv.addObject("edit", Boolean.valueOf(true));
			}
			mv.addObject("currentPage", currentPage);

			Map<String,Object> maps = Maps.newHashMap();
	        maps.put("ic_pid", -1);
			
			List<InformationClass> infoclass = this.informationClassService.queryPageList(maps);
			
			mv.addObject("cmsTools", this.cmsTools);
			mv.addObject("imageTools", this.imageTools);
			mv.addObject("infoclass", infoclass);
		}
		String goods_session = CommUtil.randomString(32);
		mv.addObject("goods_session", goods_session);
		request.getSession(false).setAttribute("goods_session", goods_session);
		return mv;
	}
	
	/**
	 * 资讯保存
	 * @param request
	 * @param response
	 * @param id
	 * @param currentPage
	 * @param cmd
	 * @param list_url
	 * @param add_url
	 * @param goods_session
	 * @return
	 */
	@SecurityMapping(title = "资讯保存", value = "/information_save*", rtype = "seller", rname = "资讯管理", rcode = "information_seller", rgroup = "其他管理")
	@RequestMapping({ "/information_save" })
	public ModelAndView information_saveEntity(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage,
			String cmd, String list_url, String add_url, String goods_session) {
		ModelAndView mv = null;
		String goods_session1 = CommUtil.null2String(request.getSession(false)
				.getAttribute("goods_session"));
		if (goods_session1.equals("")) {
			mv = new RedPigJModelAndView(
					"user/default/sellercenter/seller_error.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request,
					response);
			mv.addObject("op_title", "禁止重复提交表单");
			mv.addObject("url", CommUtil.getURL(request)
					+ "/information_list");
		} else if (goods_session1.equals(goods_session)) {
			
			Information information = null;
			User user = this.userService.selectByPrimaryKey(SecurityUserHolder
					.getCurrentUser().getId());
			user = user.getParent() == null ? user : user.getParent();
			Store store = user.getStore();
			if (id.equals("")) {
				information = (Information) WebForm.toPo(request,
						Information.class);
				information.setAddTime(new Date());
				information.setAuthor(user.getUsername());
				information.setAuthor_id(user.getId());
				information.setStore_id(store.getId());
				information.setStore(store.getStore_name());
				information.setType(1);
				information.setStatus(0);
			} else {
				information = this.informationService.selectByPrimaryKey(Long
						.valueOf(Long.parseLong(id)));
				if (information.getStore_id().equals(store.getId())) {
					information = (Information) WebForm.toPo(request,
							information);
				} else {
					information = (Information) WebForm.toPo(request,
							Information.class);
					information.setAddTime(new Date());
					information.setAuthor(user.getUsername());
					information.setAuthor_id(user.getId());
					information.setStore_id(store.getId());
					information.setStore(store.getStore_name());
					information.setType(1);
					information.setStatus(0);
				}
			}
			String uploadFilePath = this.configService.getSysConfig()
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
					photo = this.accessoryService.selectByPrimaryKey(information
							.getCover());
					fileName = photo.getName();
				}
				map = CommUtil.saveFileToServer(request, "cover",
						saveFilePathName, fileName, null);
				if (fileName.equals("")) {
					if (map.get("fileName") != "") {
						photo = new Accessory();
						photo.setName(CommUtil.null2String(map.get("fileName")));
						photo.setExt(CommUtil.null2String(map.get("mime")));
						photo.setSize(BigDecimal.valueOf(CommUtil
								.null2Double(map.get("fileSize"))));
						photo.setPath(uploadFilePath + "/information_cover");
						photo.setWidth(CommUtil.null2Int(map.get("width")));
						photo.setHeight(CommUtil.null2Int(map.get("height")));
						photo.setAddTime(new Date());
						this.accessoryService.saveEntity(photo);
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
					this.accessoryService.updateById(photo);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			if (id.equals("")) {
				this.informationService.saveEntity(information);
			} else {
				this.informationService.updateById(information);
			}
			mv = new RedPigJModelAndView(
					"user/default/sellercenter/seller_success.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request,
					response);
			mv.addObject("op_title", "保存资讯成功");
			mv.addObject("url", CommUtil.getURL(request)
					+ "/information_list");
			request.getSession(false).removeAttribute("goods_session");
		} else {
			mv = new RedPigJModelAndView(
					"user/default/sellercenter/seller_error.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request,
					response);
			mv.addObject("op_title", "参数错误");
			mv.addObject("url", CommUtil.getURL(request) + "/items");
		}
		return mv;
	}
	
	/**
	 * 资讯提交审核
	 * @param request
	 * @param response
	 * @param id
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "资讯提交审核", value = "/information_apply*", rtype = "seller", rname = "资讯管理", rcode = "information_seller", rgroup = "其他管理")
	@RequestMapping({ "/information_apply" })
	public String information_apply(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage) {
		Information information = null;
		if ((id != null) && (!id.equals(""))) {
			information = this.informationService.selectByPrimaryKey(CommUtil
					.null2Long(id));
			User user = this.userService.selectByPrimaryKey(SecurityUserHolder
					.getCurrentUser().getId());
			user = user.getParent() == null ? user : user.getParent();
			Store store = user.getStore();
			if (information.getStore_id().equals(store.getId())) {
				information.setStatus(10);
				this.informationService.updateById(information);
			}
		}
		return "redirect:information_list?currentPage=" + currentPage;
	}
	
	/**
	 * 资讯删除
	 * @param request
	 * @param response
	 * @param mulitId
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "资讯删除", value = "/information_del*", rtype = "seller", rname = "资讯管理", rcode = "information_seller", rgroup = "其他管理")
	@RequestMapping({ "/information_del" })
	public String information_del(HttpServletRequest request,
			HttpServletResponse response, String mulitId, String currentPage) {
		String[] ids = mulitId.split(",");
		for (String id : ids) {
			if (!id.equals("")) {
				Information information = this.informationService
						.selectByPrimaryKey(Long.valueOf(Long.parseLong(id)));
				User user = this.userService.selectByPrimaryKey(SecurityUserHolder
						.getCurrentUser().getId());
				user = user.getParent() == null ? user : user.getParent();
				Store store = user.getStore();
				if (information.getStore_id().equals(store.getId())) {
					this.informationService.deleteById(Long.valueOf(Long
							.parseLong(id)));
				}
			}
		}
		return "redirect:information_list?currentPage=" + currentPage;
	}
	
	/**
	 * 资讯商品
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param gc_id
	 * @param goods_name
	 * @return
	 */
	@SecurityMapping(title = "资讯商品", value = "/information_goods*", rtype = "seller", rname = "资讯管理", rcode = "information_seller", rgroup = "其他管理")
	@RequestMapping({ "/information_goods" })
	public ModelAndView information_goods(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String gc_id,
			String goods_name) {
		ModelAndView mv = new RedPigJModelAndView(
				"admin/blue/information_goods.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		
		Map<String,Object> maps= this.redPigQueryTools.getParams(currentPage,5,"addTime","desc");
        maps.put("gc_type", 1);
        
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		
		maps.put("goods_status", 0);
		
		maps.put("goods_store_id", user.getStore().getId());
		
		if (!CommUtil.null2String(gc_id).equals("")) {
			Set<Long> ids = genericIds(this.goodsClassService
					.selectByPrimaryKey(CommUtil.null2Long(gc_id)));
			
			maps.put("gc_ids", ids);
			
			mv.addObject("gc_id", gc_id);
		}
		if (!CommUtil.null2String(goods_name).equals("")) {
			
			maps.put("goods_name_like", goods_name);
			mv.addObject("goods_name", goods_name);
		}
		IPageList pList = this.goodsService.list(maps);
		String photo_url = CommUtil.getURL(request)
				+ "/information_goods";
		mv.addObject("goods", pList.getResult());
		mv.addObject("gotoPageAjaxHTML", CommUtil.showPageAjaxHtml(photo_url,
				"", pList.getCurrentPage(), pList.getPages(),
				pList.getPageSize()));
		maps.clear();
		maps.put("parent",-1);
		maps.put("orderBy","sequence");
		maps.put("orderType","asc");
		
		List<GoodsClass> gcs = this.goodsClassService.queryPageList(maps);
		
		mv.addObject("gcs", gcs);
		return mv;
	}
	
	/**
	 * 商品图片
	 * @param request
	 * @param response
	 * @param goods_id
	 * @param currentPage
	 * @param gc_id
	 * @param goods_name
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	@SecurityMapping(title = "商品图片", value = "/information_goods_imgs*", rtype = "seller", rname = "资讯管理", rcode = "information_seller", rgroup = "其他管理")
	@RequestMapping({ "/information_goods_imgs" })
	public ModelAndView information_goods_imgs(HttpServletRequest request,
			HttpServletResponse response, String goods_id, String currentPage,
			String gc_id, String goods_name) {
		ModelAndView mv = new RedPigJModelAndView(
				"admin/blue/information_goods_imgs.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		Goods goods = this.goodsService
				.selectByPrimaryKey(CommUtil.null2Long(goods_id));
		List list = Lists.newArrayList();
		if ((goods != null) && (goods.getGoods_main_photo() != null)) {
			list.add(goods.getGoods_main_photo());
			list.addAll(goods.getGoods_photos());
		}
		mv.addObject("photos", list);
		mv.addObject("goods_id", goods_id);
		return mv;
	}
	
	/**
	 * 资讯预览
	 * @param request
	 * @param response
	 * @param id
	 * @return
	 */
	@SecurityMapping(title = "资讯预览", value = "/info_preview*", rtype = "seller", rname = "资讯管理", rcode = "information_seller", rgroup = "其他管理")
	@RequestMapping({ "/info_preview" })
	public ModelAndView info_preview(HttpServletRequest request,
			HttpServletResponse response, String id) {
		ModelAndView mv = new RedPigJModelAndView("/cms/detail.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		Information info = this.informationService.selectByPrimaryKey(CommUtil
				.null2Long(id));
		if (info != null) {
			User user = this.userService.selectByPrimaryKey(SecurityUserHolder
					.getCurrentUser().getId());
			user = user.getParent() == null ? user : user.getParent();
			if (info.getStore_id().equals(user.getStore().getId())) {
				mv.addObject("obj", info);
				mv.addObject("className", this.informationClassService
						.selectByPrimaryKey(Long.valueOf(info.getClassid()))
						.getIc_name());
				Map<String, Object> map = Maps.newHashMap();
				map.put("addTime_more_than", info.getAddTime());
				map.put("status",20);
				
				List<Information> before = this.informationService.queryPageList(map,0,1);
				
				if (before.size() > 0) {
					mv.addObject("before", before.get(0));
				}
				map.clear();
				map.put("addTime_less_than", info.getAddTime());
				map.put("status",20);
				
				List<Information> after = this.informationService.queryPageList(map,0,1);
				
				if (after.size() > 0) {
					mv.addObject("after", after.get(0));
				}
				map.clear();
				map.put("orderBy", "ic_sequence");
				map.put("orderType", "asc");
				
				List<InformationClass> infoclass = this.informationClassService.queryPageList(map);
				
				mv.addObject("infoclass", infoclass);
				map.clear();
				map.put("status", 20);
				map.put("orderBy", "sequence");
				map.put("orderType", "asc");
				
				List<Information> hotinfo = this.informationService.queryPageList(map,0,5);
				
				mv.addObject("hotinfo", hotinfo);
				mv.addObject("imageTools", this.imageTools);
			} else {
				mv = new RedPigJModelAndView(
						"user/default/sellercenter/seller_error.html",
						this.configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 0, request,
						response);
				mv.addObject("op_title", "参数不正确");
				mv.addObject("url", CommUtil.getURL(request)
						+ "/information_list");
			}
		} else {
			mv = new RedPigJModelAndView(
					"user/default/sellercenter/seller_error.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request,
					response);
			mv.addObject("op_title", "参数不正确");
			mv.addObject("url", CommUtil.getURL(request)
					+ "/information_list");
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
}
