package com.redpigmall.manage.seller.action;

import java.awt.Font;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
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
import com.redpigmall.api.tools.RedPigCommonUtil;
import com.redpigmall.api.tools.WebForm;
import com.redpigmall.domain.Accessory;
import com.redpigmall.domain.Album;
import com.redpigmall.domain.Goods;
import com.redpigmall.domain.User;
import com.redpigmall.domain.WaterMark;
import com.redpigmall.manage.seller.action.base.BaseAction;

/**
 * 
 * <p>
 * Title: RedPigAlbumSellerAction.java
 * </p>
 * 
 * <p>
 * Description:卖家相册中心管理控制器
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
 * @date 2014-5-30
 * 
 * 
 * @version redpigmall_b2b2c v8.0 2016版
 */
@Controller
public class RedPigAlbumSellerAction extends BaseAction{
	/**
	 * 相册列表
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param album_name
	 * @return
	 */
	@SecurityMapping(title = "相册列表", value = "/album*", rtype = "seller", rname = "图片空间", rcode = "album_seller", rgroup = "其他管理")
	@RequestMapping({ "/album" })
	public ModelAndView album(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String album_name) {
		
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/sellercenter/album.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder.getCurrentUser().getId());
		
		user = user.getParent() == null ? user : user.getParent();
		
		Map<String,Object> maps= this.redPigQueryTools.getParams(currentPage, "album_sequence", "asc");
		
		if ((!"".equals(album_name)) && (album_name != null)) {
			
			maps.put("album_name_like", album_name);
			mv.addObject("album_name", album_name);
		}
		
		maps.put("user_id", user.getId());
		IPageList pList = this.albumService.list(maps);
		
		String url = this.configService.getSysConfig().getAddress();
		
		String path = this.storeTools
				.createUserFolder(request, user.getStore());
		double csize = CommUtil.fileSize(new File(path));
		double remainSpace = 0.0D;
		if (user.getStore().getGrade().getSpaceSize() > 0.0F) {
			remainSpace = CommUtil.div(
					Double.valueOf(user.getStore().getGrade().getSpaceSize()
							* 1024.0F - csize), Integer.valueOf(1024));
			mv.addObject("remainSpace", Double.valueOf(remainSpace));
		}
		if ((url == null) || (url.equals(""))) {
			url = CommUtil.getURL(request);
		}
		
		CommUtil.saveIPageList2ModelAndView(url + "/album", "", "", pList, mv);
		
		mv.addObject("albumViewTools", this.albumViewTools);
		Map<String,Object> params = Maps.newHashMap();
		
		params.put("user_id", user.getId());
		params.put("orderBy", "album_sequence");
		params.put("orderType", "asc");
		
		List<Album> albums = this.albumService.queryPageList(params);
		
		mv.addObject("albums", albums);
		
		String[] strs = this.configService.getSysConfig().getImageSuffix().split("\\|");
		
		StringBuffer sb = new StringBuffer();

		for (String str : strs) {

			sb.append("." + str + ",");
		}
		mv.addObject("imageSuffix1", sb);
		mv.addObject("currentPage", currentPage);
		return mv;
	}
	
	/**
	 * 新增相册
	 * @param request
	 * @param response
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "新增相册", value = "/album_add*", rtype = "seller", rname = "图片空间", rcode = "album_seller", rgroup = "其他管理")
	@RequestMapping({ "/album_add" })
	public ModelAndView album_add(HttpServletRequest request,
			HttpServletResponse response, String currentPage) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/sellercenter/album_add.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		mv.addObject("currentPage", currentPage);
		return mv;
	}
	
	/**
	 * 新增相册
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param id
	 * @param album_name
	 * @return
	 */
	@SecurityMapping(title = "新增相册", value = "/album_edit*", rtype = "seller", rname = "图片空间", rcode = "album_seller", rgroup = "其他管理")
	@RequestMapping({ "/album_edit" })
	public ModelAndView album_edit(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String id,
			String album_name) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/sellercenter/album_add.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		Album obj = this.albumService.selectByPrimaryKey(CommUtil.null2Long(id));
		mv.addObject("obj", obj);
		mv.addObject("currentPage", currentPage);
		mv.addObject("album_name1", album_name);
		return mv;
	}
	
	/**
	 * 相册保存
	 * @param request
	 * @param response
	 * @param id
	 * @param currentPage
	 */
	@SecurityMapping(title = "相册保存", value = "/album_save*", rtype = "seller", rname = "图片空间", rcode = "album_seller", rgroup = "其他管理")
	@RequestMapping({ "/album_save" })
	public void album_saveEntity(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage) {
		response.setCharacterEncoding("utf-8");
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		
		Album album = null;
		if (id.equals("")) {
			album = (Album) WebForm.toPo(request, Album.class);
			album.setAddTime(new Date());
		} else {
			Album obj = this.albumService.selectByPrimaryKey(Long.valueOf(Long
					.parseLong(id)));
			album = (Album) WebForm.toPo(request, obj);
		}
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		album.setUser(user);
		
		if (id.equals("")) {
			this.albumService.saveEntity(album);
		} else {
			this.albumService.updateById(album);
		}
		
		try {
			response.getWriter().print("ok");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 图片上传
	 * @param request
	 * @param response
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "图片上传", value = "/album_upload*", rtype = "seller", rname = "图片空间", rcode = "album_seller", rgroup = "其他管理")
	@RequestMapping({ "/album_upload" })
	public ModelAndView album_upload(HttpServletRequest request,
			HttpServletResponse response, String currentPage) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/sellercenter/album_upload.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		Map<String,Object> params = Maps.newHashMap();
		params.put("user_id", user.getId());
		params.put("orderBy", "album_sequence");
		params.put("orderType", "asc");
		
		List<Album> objs = this.albumService.queryPageList(params);
		
		mv.addObject("objs", objs);
		mv.addObject("currentPage", currentPage);
		mv.addObject("jsessionid", request.getSession().getId());
		mv.addObject("imageSuffix", this.storeViewTools
				.genericImageSuffix(this.configService.getSysConfig()
						.getImageSuffix()));

		String temp_begin = request.getSession().getId().toString()
				.substring(0, 5);
		String temp_end = CommUtil.randomInt(5);
		String user_id = CommUtil.null2String(SecurityUserHolder
				.getCurrentUser().getId());
		mv.addObject("session_u_id", temp_begin + user_id + temp_end);
		return mv;
	}
	
	/**
	 * 相册删除
	 * @param request
	 * @param mulitId
	 * @return
	 */
	@SecurityMapping(title = "相册删除", value = "/album_del*", rtype = "seller", rname = "图片空间", rcode = "album_seller", rgroup = "其他管理")
	@RequestMapping({ "/album_del" })
	public String album_del(HttpServletRequest request, String mulitId) {
		
		String[] ids = mulitId.split(",");
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder.getCurrentUser().getId());
		
		user = user.getParent() == null ? user : user.getParent();
		
		for (String id : ids) {

			if (!id.equals("")) {
				Album album = this.albumService.selectByPrimaryKey(CommUtil.null2Long(id));
				if (album != null) {
					Map<String,Object> params = Maps.newHashMap();
					params.put("album_id", album.getId());
					
					List<Accessory> accs = this.accessoryService.queryPageList(params);
					
					for (Accessory acc : accs) {
						RedPigCommonUtil.del_acc(request, acc);
						
						for (Goods goods : acc.getGoods_main_list()) {
							goods.setGoods_main_photo(null);
							this.goodsService.updateById(goods);
						}
						
						for (Goods goods1 : acc.getGoods_list()) {
							goods1.getGoods_photos().remove(acc);
							this.goodsService.batchDeleteGoodsPhotos(goods1.getId(), Lists.newArrayList(acc));
//							this.goodsService.updateById(goods1);
						}
						
						if (acc.getAlbum().getAlbum_cover() != null) {
							if (acc.getAlbum().getAlbum_cover().getId().equals(acc.getId())) {
								album.setAlbum_cover(null);
								this.albumService.updateById(album);
							}
						}
						this.accessoryService.deleteById(acc.getId());
					}
					this.albumService.deleteById(Long.valueOf(Long.parseLong(id)));
				}
			}
		}
		return "redirect:album";
	}
	
	/**
	 * 相册封面设置
	 * @param request
	 * @param album_id
	 * @param id
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "相册封面设置", value = "/album_cover*", rtype = "seller", rname = "图片空间", rcode = "album_seller", rgroup = "其他管理")
	@RequestMapping({ "/album_cover" })
	public String album_cover(HttpServletRequest request, String album_id,
			String id, String currentPage) {
		
		Accessory album_cover = this.accessoryService.selectByPrimaryKey(Long.valueOf(Long.parseLong(id)));
		Album album = this.albumService.selectByPrimaryKey(Long.valueOf(Long.parseLong(album_id)));
		album.setAlbum_cover(album_cover);
		this.albumService.updateById(album);
		
		return "redirect:album_image?id=" + album_id + "&currentPage=" + currentPage;
	}
	
	/**
	 * 相册转移
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param album_id
	 * @param id
	 * @return
	 */
	@SecurityMapping(title = "相册转移", value = "/album_transfer*", rtype = "seller", rname = "图片空间", rcode = "album_seller", rgroup = "其他管理")
	@RequestMapping({ "/album_transfer" })
	public ModelAndView album_transfer(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String album_id,
			String id) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/sellercenter/album_transfer.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder.getCurrentUser().getId());
		
		
		user = user.getParent() == null ? user : user.getParent();
		
		Map<String,Object> params = Maps.newHashMap();
		params.put("user_id", user.getId());
		params.put("album_id", CommUtil.null2Long(album_id));
		params.put("orderBy", "album_sequence");
		params.put("orderType", "asc");
		
		List<Album> objs = this.albumService.queryPageList(params);
		
		mv.addObject("objs", objs);
		mv.addObject("currentPage", currentPage);
		mv.addObject("album_id", album_id);
		mv.addObject("mulitId", id);
		return mv;
	}
	
	/**
	 * 图片转移相册
	 * @param request
	 * @param mulitId
	 * @param album_id
	 * @param to_album_id
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "图片转移相册", value = "/album_transfer_save*", rtype = "seller", rname = "图片空间", rcode = "album_seller", rgroup = "其他管理")
	@RequestMapping({ "/album_transfer_save" })
	public String album_transfer_saveEntity(HttpServletRequest request,
			String mulitId, String album_id, String to_album_id,
			String currentPage) {
		
		String[] ids = mulitId.split(",");
		
		for (String id : ids) {
			if (!id.equals("")) {
				Accessory acc = this.accessoryService.selectByPrimaryKey(Long.valueOf(Long.parseLong(id)));
				Album to_album = this.albumService.selectByPrimaryKey(Long.valueOf(Long.parseLong(to_album_id)));
				acc.setAlbum(to_album);
				this.accessoryService.updateById(acc);
			}
		}
		return "redirect:album_image?id=" + album_id + "&currentPage=" + currentPage;
	}
	
	/**
	 * 图片列表
	 * @param request
	 * @param response
	 * @param id
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "图片列表", value = "/album_image*", rtype = "seller", rname = "图片空间", rcode = "album_seller", rgroup = "其他管理")
	@RequestMapping({ "/album_image" })
	public ModelAndView album_image(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage) {
		
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/sellercenter/album_image.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		
		Album album = this.albumService.selectByPrimaryKey(Long.valueOf(Long.parseLong(id)));
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		
		Map<String,Object> maps= this.redPigQueryTools.getParams(currentPage,25, "addTime", "desc");
		
		if ((id != null) && (!id.equals(""))) {
			maps.put("album_id", id);
		} else {
			maps.put("album_id", -1);
		}
		
		maps.put("album_user_id", user.getId());
		
		IPageList pList = this.accessoryService.list(maps);
		
		String url = this.configService.getSysConfig().getAddress();
		if ((url == null) || (url.equals(""))) {
			url = CommUtil.getURL(request);
		}
		String path = this.storeTools
				.createUserFolder(request, user.getStore());
		double csize = CommUtil.fileSize(new File(path));
		double remainSpace = 0.0D;
		if (user.getStore().getGrade().getSpaceSize() > 0.0F) {
			remainSpace = CommUtil.div(
					Double.valueOf(user.getStore().getGrade().getSpaceSize()
							* 1024.0F - csize), Integer.valueOf(1024));
			mv.addObject("remainSpace", Double.valueOf(remainSpace));
		}
		CommUtil.saveIPageList2ModelAndView(url + "/album_image", "", "&id=" + id, pList, mv);
		Map<String,Object> params = Maps.newHashMap();
		
		params.put("user_id", user.getId());
		params.put("orderBy", "album_sequence");
		params.put("orderType", "asc");
		
		List<Album> albums = this.albumService.queryPageListWithNoRelations(params);
		
		mv.addObject("albums", albums);
		mv.addObject("album", album);
		return mv;
	}
	
	/**
	 * 图片幻灯查看
	 * @param request
	 * @param response
	 * @param album_id
	 * @param id
	 * @return
	 */
	@SecurityMapping(title = "图片幻灯查看", value = "/image_slide*", rtype = "seller", rname = "图片空间", rcode = "album_seller", rgroup = "其他管理")
	@RequestMapping({ "/image_slide" })
	public ModelAndView image_slide(HttpServletRequest request,
			HttpServletResponse response, String album_id, String id) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/sellercenter/image_slide.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		
		Album album = this.albumService.selectByPrimaryKey(CommUtil.null2Long(album_id));
		mv.addObject("album", album);
		Accessory current_img = this.accessoryService.selectByPrimaryKey(CommUtil.null2Long(id));
		mv.addObject("current_img", current_img);
		mv.addObject("ImageTools", this.ImageTools);
		return mv;
	}
	
	/**
	 * 相册内图片删除
	 * @param request
	 * @param mulitId
	 * @param album_id
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "相册内图片删除", value = "/album_img_del*", rtype = "seller", rname = "图片空间", rcode = "album_seller", rgroup = "其他管理")
	@RequestMapping({ "/album_img_del" })
	public String album_img_del(HttpServletRequest request, String mulitId,
			String album_id, String currentPage) {
		if (mulitId == null) {
			mulitId = "";
		}
		String[] ids = mulitId.split(",");
		
		for (String id : ids) {
			
			if (!id.equals("")) {
				Accessory acc = this.accessoryService.selectByPrimaryKey(Long.valueOf(Long.parseLong(id)));
				if (acc != null) {
					String middle_path = request.getSession()
							.getServletContext().getRealPath("/")
							+ acc.getPath()
							+ File.separator
							+ acc.getName()
							+ "_middle." + acc.getExt();
					
					String small_path = request.getSession()
							.getServletContext().getRealPath("/")
							+ acc.getPath()
							+ File.separator
							+ acc.getName()
							+ "_small." + acc.getExt();
					
					CommUtil.deleteFile(middle_path);
					CommUtil.deleteFile(small_path);
					
					RedPigCommonUtil.del_acc(request, acc);
					
					try {
						for (Goods goods : acc.getGoods_main_list()) {
							goods.setGoods_main_photo(null);
							this.goodsService.updateById(goods);
						}
						for (Goods goods : acc.getGoods_list()) {
							goods.getGoods_photos().remove(acc);
							this.goodsSerivce.batchDeleteGoodsPhotos(goods.getId(), Lists.newArrayList(acc));
							this.goodsService.updateById(goods);
						}
						if ((acc.getAlbum() != null)
								&& (acc.getAlbum().getAlbum_cover().getId() == acc.getId())) {
							
							acc.getAlbum().setAlbum_cover(null);
							this.albumService.updateById(acc.getAlbum());
						}
					} catch (Exception localException) {
					}
					this.accessoryService.deleteById(acc.getId());
				}
			}
		}
		return "redirect:album_image?id=" + album_id + "&currentPage="
				+ currentPage;
	}
	
	/**
	 * 图片转移相册
	 * @param request
	 * @param mulitId
	 * @param album_id
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "图片转移相册", value = "/album_watermark*", rtype = "seller", rname = "图片空间", rcode = "album_seller", rgroup = "其他管理")
	@RequestMapping({ "/album_watermark" })
	public String album_watermark(HttpServletRequest request, String mulitId,
			String album_id, String currentPage) {
		Long store_id = null;
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		if (user.getStore() != null) {
			store_id = user.getStore().getId();
		}
		if (store_id != null) {
			WaterMark waterMark = this.waterMarkService.getObjByProperty("store_id","=",  store_id);
			
			if ((waterMark != null) && (mulitId != null)) {
				String[] ids = mulitId.split(",");

				for (String id : ids) {

					if (!id.equals("")) {
						Accessory acc = this.accessoryService.selectByPrimaryKey(Long.valueOf(Long.parseLong(id)));
						
						String path = request.getSession().getServletContext()
								.getRealPath("/")
								+ acc.getPath()
								+ File.separator
								+ acc.getName();
						
						String path_middle = request.getSession()
								.getServletContext().getRealPath("/")
								+ acc.getPath()
								+ File.separator
								+ acc.getName() + "_middle." + acc.getExt();
						
						String path_small = request.getSession()
								.getServletContext().getRealPath("/")
								+ acc.getPath()
								+ File.separator
								+ acc.getName() + "_small." + acc.getExt();
						
						path = path.replace("|\\", File.separator).replace("/",File.separator);
						
						path_middle = path_middle
								.replace("|\\", File.separator).replace("/",
										File.separator);
						path_small = path_small.replace("|\\", File.separator)
								.replace("/", File.separator);
						
						if ((waterMark.getWm_image_open())
								&& (waterMark.getWm_image() != null)) {
							String wm_path = request.getSession()
									.getServletContext().getRealPath("/")
									+ waterMark.getWm_image().getPath()
									+ File.separator
									+ waterMark.getWm_image().getName();
							CommUtil.waterMarkWithImage(wm_path, path,
									waterMark.getWm_image_pos(),
									waterMark.getWm_image_alpha());
						}
						
						if (waterMark.getWm_text_open()) {
							Font font = new Font(waterMark.getWm_text_font(),
									1, waterMark.getWm_text_font_size());
							CommUtil.waterMarkWithText(path, path,
									waterMark.getWm_text(),
									waterMark.getWm_text_color(), font,
									waterMark.getWm_text_pos(), 100.0F);
						}
						CommUtil.createSmall(path, path_small,
								this.configService.getSysConfig()
										.getSmallWidth(), this.configService
										.getSysConfig().getSmallHeight());

						CommUtil.createSmall(path, path_middle,
								this.configService.getSysConfig()
										.getMiddleWidth(), this.configService
										.getSysConfig().getMiddleHeight());
					}
				}
			}
		}
		return "redirect:album_image?id=" + album_id + "&currentPage="
				+ currentPage;
	}
	
	/**
	 * 相册图片上传
	 * @param request
	 * @param response
	 * @param album_id
	 * @param ajaxUploadMark
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@SecurityMapping(title = "相册图片上传", value = "/album_image_upload*", rtype = "seller", rname = "图片空间", rcode = "album_seller", rgroup = "其他管理")
	@RequestMapping({ "/album_image_upload" })
	public void album_image_upload(HttpServletRequest request,
			HttpServletResponse response, String album_id, String ajaxUploadMark) {
		Boolean html5Uploadret = Boolean.valueOf(false);
		Map ajaxUploadInfo = null;
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder.getCurrentUser().getId());
		
		user = user.getParent() == null ? user : user.getParent();
		String path = this.storeTools
				.createUserFolder(request, user.getStore());
		String url = this.storeTools.createUserFolderURL(user.getStore());
		double csize = CommUtil.fileSize(new File(path));
		double img_remain_size = 0.0D;
		if (user.getStore().getGrade().getSpaceSize() > 0.0F) {
			img_remain_size = CommUtil.div(
					Double.valueOf(user.getStore().getGrade().getSpaceSize()
							* 1024.0F - csize), Integer.valueOf(1024));
		}
		if (img_remain_size >= 0.0D) {
			try {
				Map map = CommUtil.saveFileToServer(request, "fileImage", path, null, null);
				Map<String,Object> params = Maps.newHashMap();
				params.put("store_id", user.getStore().getId());
				
				List<WaterMark> wms = this.waterMarkService.queryPageList(params);
				
				if (wms.size() > 0) {
					WaterMark mark = (WaterMark) wms.get(0);
					if (mark.getWm_image_open()) {
						String pressImg = request.getSession()
								.getServletContext().getRealPath("")
								+ File.separator
								+ mark.getWm_image().getPath()
								+ File.separator + mark.getWm_image().getName();
						String targetImg = path + File.separator
								+ map.get("fileName");
						int pos = mark.getWm_image_pos();
						float alpha = mark.getWm_image_alpha();
						CommUtil.waterMarkWithImage(pressImg, targetImg, pos, alpha);
					}
					if (mark.getWm_text_open()) {
						String targetImg = path + File.separator
								+ map.get("fileName");
						int pos = mark.getWm_text_pos();
						String text = mark.getWm_text();
						String markContentColor = mark.getWm_text_color();
						CommUtil.waterMarkWithText(
								targetImg,
								targetImg,
								text,
								markContentColor,
								new Font(mark.getWm_text_font(), 1, mark
										.getWm_text_font_size()), pos, 100.0F);
					}
				}
				Accessory image = new Accessory();
				image.setAddTime(new Date());
				image.setExt((String) map.get("mime"));
				image.setPath(url);
				image.setWidth(CommUtil.null2Int(map.get("width")));
				image.setHeight(CommUtil.null2Int(map.get("height")));
				image.setName(CommUtil.null2String(map.get("fileName")));
				image.setUser(user);
				Album album = null;
				if ((album_id != null) && (!album_id.equals(""))) {
					album = this.albumService.selectByPrimaryKey(CommUtil
							.null2Long(album_id));
				} else {
					album = this.albumService.getDefaultAlbum(user.getId());
					if (album == null) {
						album = new Album();
						album.setAddTime(new Date());
						album.setAlbum_name("默认相册");
						album.setAlbum_sequence(55536);
						album.setAlbum_default(true);
						this.albumService.saveEntity(album);
					}
				}
				image.setAlbum(album);
				this.accessoryService.saveEntity(image);
				html5Uploadret = true;
				
				if ((html5Uploadret.booleanValue()) && (ajaxUploadMark != null)) {
					ajaxUploadInfo = Maps.newHashMap();
					ajaxUploadInfo.put("url",
							image.getPath() + "/" + image.getName());
				}
				String ext = image.getExt().indexOf(".") < 0 ? "."
						+ image.getExt() : image.getExt();
				String source = request.getSession().getServletContext()
						.getRealPath("/")
						+ image.getPath() + File.separator + image.getName();
				String target = source + "_small" + ext;
				CommUtil.createSmall(source, target, this.configService
						.getSysConfig().getSmallWidth(), this.configService
						.getSysConfig().getSmallHeight());

				String midext = image.getExt().indexOf(".") < 0 ? "." + image.getExt() : image.getExt();
				String midtarget = source + "_middle" + midext;
				CommUtil.createSmall(source, midtarget, this.configService
						.getSysConfig().getMiddleWidth(), this.configService
						.getSysConfig().getMiddleHeight());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		try {
			PrintWriter writer = response.getWriter();
			if (ajaxUploadMark != null) {
				writer.print(JSON.toJSONString(ajaxUploadInfo));
			} else {
				writer.print(html5Uploadret);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 搜索图片相册
	 * @param request
	 * @param response
	 * @param album_name
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@SecurityMapping(title = "搜索图片相册", value = "/album_name*", rtype = "seller", rname = "图片空间", rcode = "album_seller", rgroup = "其他管理")
	@RequestMapping({ "/album_name" })
	public void album_name(HttpServletRequest request,
			HttpServletResponse response, String album_name) {
		String album_json = "";
		response.setCharacterEncoding("utf-8");
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		
		Map<String,Object> params = Maps.newHashMap();
		
		params.put("user_id", SecurityUserHolder.getCurrentUser().getId());
		params.put("album_name_like", album_name);
		params.put("orderBy", "album_sequence");
		params.put("orderType", "asc");
		
		List<Album> albums = this.albumService.queryPageList(params);
		
		List<Map> new_album = Lists.newArrayList();
		if (albums.size() > 0) {
			for (Album album : albums) {
				Map map = Maps.newHashMap();
				map.put("id", album.getId());
				map.put("album_name", album.getAlbum_name());
				if (album.getAlbum_cover() != null) {
					map.put("img_url", this.configService.getSysConfig().getImageWebServer() + "/"
							+ album.getAlbum_cover().getPath() + "/"
							+ album.getAlbum_cover().getName() + "_small."
							+ album.getAlbum_cover().getExt());
					
				} else {
					map.put("img_url",CommUtil.getURL(request) + "/resources/style/system/front/default/images/user_photo/phone.png");
				}
				new_album.add(map);
			}
		}
		album_json = JSON.toJSONString(new_album);
		try {
			response.getWriter().print(album_json);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
