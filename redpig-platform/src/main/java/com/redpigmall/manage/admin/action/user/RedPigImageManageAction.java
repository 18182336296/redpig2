package com.redpigmall.manage.admin.action.user;

import java.io.File;
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
import com.redpigmall.api.tools.RedPigCommUtil;
import com.redpigmall.api.tools.StringUtils;
import com.redpigmall.manage.admin.action.base.BaseAction;
import com.redpigmall.domain.Accessory;
import com.redpigmall.domain.Album;
import com.redpigmall.domain.Goods;

/**
 * 
 * <p>
 * Title: RedPigImageManageAction.java
 * </p>
 * 
 * <p>
 * Description: 平台图片管理控制器，删除数据的同时也删除物理文件
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
 * 
 * @date 2014年5月27日
 * 
 * @version redpigmall_b2b2c 8.0
 */
@Controller
public class RedPigImageManageAction extends BaseAction{
	/**
	 * 会员相册列表
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @param store_name
	 * @return
	 */
	@SecurityMapping(title = "会员相册列表", value = "/user_photo_list*", rtype = "admin", rname = "图片管理", rcode = "user_image", rgroup = "会员")
	@RequestMapping({ "/user_photo_list" })
	public ModelAndView user_album_list(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,
			String orderType, String store_name,String user_userName) {
		ModelAndView mv = new RedPigJModelAndView("admin/blue/photo_list.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		
		Map<String,Object> maps= this.redPigQueryTools.getParams(currentPage,15, orderBy, orderType);
		
		if ((store_name != null) && (!store_name.trim().equals(""))) {
			
			mv.addObject("store_name", store_name);
			maps.put("store_name_like", store_name.trim());
		}
		
		if(StringUtils.isNotBlank(user_userName)){
			mv.addObject("user_userName", store_name);
			maps.put("user_userName", user_userName.trim());
		}
		
		maps.put("redpig_userRole_single", store_name);
		
		IPageList pList = this.albumService.list(maps);
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		mv.addObject("ImageTools", this.ImageTools);
		return mv;
	}
	
	/**
	 * 会员相册删除
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param mulitId
	 * @return
	 */
	@SecurityMapping(title = "会员相册删除", value = "/user_photo_del*", rtype = "admin", rname = "图片管理", rcode = "user_image", rgroup = "会员")
	@RequestMapping({ "/user_photo_del" })
	public String user_album_del(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String mulitId) {
		if (mulitId == null) {
			mulitId = "";
		}
		String[] ids = mulitId.split(",");
		for (String id : ids) {
			if (!id.equals("")) {
				Album album = this.albumService.selectByPrimaryKey(CommUtil
						.null2Long(id));
				if (album != null) {
					Map<String,Object> params = Maps.newHashMap();
					params.put("album_id", album.getId());
					
					List<Accessory> accs = this.accessoryService.queryPageList(params);
					for (Accessory acc : accs) {
						RedPigCommUtil.del_acc(request, acc);
						for (Goods goods : acc.getGoods_main_list()) {
							goods.setGoods_main_photo(null);
							this.goodsService.updateById(goods);
						}
						for (Goods goods1 : acc.getGoods_list()) {
							goods1.getGoods_photos().remove(acc);
							this.goodsService.updateById(goods1);
						}
						this.accessoryService.deleteById(acc.getId());
					}
					this.albumService.deleteById(Long.valueOf(Long.parseLong(id)));
				}
			}
		}
		String url = "redirect:/user_photo_list?currentPage="
				+ currentPage;
		return url;
	}
	
	/**
	 * 会员相册图片列表
	 * @param request
	 * @param response
	 * @param aid
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @return
	 */
	@SecurityMapping(title = "会员相册图片列表", value = "/user_pic_list*", rtype = "admin", rname = "图片管理", rcode = "user_image", rgroup = "会员")
	@RequestMapping({ "/user_pic_list" })
	public ModelAndView user_pic_list(HttpServletRequest request,
			HttpServletResponse response, String aid, String currentPage,
			String orderBy, String orderType) {
		ModelAndView mv = new RedPigJModelAndView("admin/blue/pic_list.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		
		Map<String,Object> maps= this.redPigQueryTools.getParams(currentPage,50, orderBy, orderType);
		maps.put("album_id", CommUtil.null2Long(aid) );
		
		IPageList pList = this.accessoryService.queryPagesWithNoRelations(maps);
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		Album album = this.albumService.selectByPrimaryKey(CommUtil.null2Long(aid));
		mv.addObject("album", album);
		return mv;
	}
	
	/**
	 * 会员相册图片删除
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param mulitId
	 * @param aid
	 * @return
	 */
	@SecurityMapping(title = "会员相册图片删除", value = "/user_pic_del*", rtype = "admin", rname = "图片管理", rcode = "user_image", rgroup = "会员")
	@RequestMapping({ "/user_pic_del" })
	public String user_pic_del(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String mulitId,
			String aid) {
		String[] ids = mulitId.split(",");
		for (String id : ids) {
			Accessory acc = this.accessoryService.selectByPrimaryKey(CommUtil
					.null2Long(id));
			if (acc != null) {
				String middle_path = request.getSession().getServletContext()
						.getRealPath("/")
						+ acc.getPath()
						+ File.separator
						+ acc.getName()
						+ "_middle." + acc.getExt();
				String small_path = request.getSession().getServletContext()
						.getRealPath("/")
						+ acc.getPath()
						+ File.separator
						+ acc.getName()
						+ "_small." + acc.getExt();
				RedPigCommUtil.deleteFile(middle_path);
				RedPigCommUtil.deleteFile(small_path);
				RedPigCommUtil.del_acc(request, acc);
				try {
					for (Goods goods : acc.getGoods_main_list()) {
						goods.setGoods_main_photo(null);
						this.goodsService.updateById(goods);
					}
					for (Goods goods : acc.getGoods_list()) {
						goods.getGoods_photos().remove(acc);
						this.goodsService.updateById(goods);
					}
					if ((acc.getAlbum() != null)
							&& (acc.getAlbum().getAlbum_cover().getId() == acc
									.getId())) {
						acc.getAlbum().setAlbum_cover(null);
						this.albumService.updateById(acc.getAlbum());
					}
				} catch (Exception localException) {
				}
				this.accessoryService.deleteById(acc.getId());
			}
		}
		String url = "redirect:/user_pic_list?currentPage=" + currentPage
				+ "&aid=" + aid;
		return url;
	}
}
