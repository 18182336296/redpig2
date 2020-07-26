package com.redpigmall.manage.admin.action.self.image;

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
import com.redpigmall.manage.admin.action.base.BaseAction;
import com.redpigmall.domain.Accessory;
import com.redpigmall.domain.Album;
import com.redpigmall.domain.Goods;

/**
 * 
 * <p>
 * Title: RedPigSelfImageManageAction.java
 * </p>
 * 
 * <p>
 * Description:自营商品相册管理控制器
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
 * @version redp
 */
@Controller
public class RedPigSelfImageManageAction extends BaseAction{
	/**
	 * 自营相册列表
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @param user_name
	 * @return
	 */
	@SecurityMapping(title = "自营相册列表", value = "/imageself_list*", rtype = "admin", rname = "图片管理", rcode = "selfimg_manage", rgroup = "自营")
	@RequestMapping({ "/imageself_list" })
	public ModelAndView imageself_list(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,
			String orderType, String user_name) {
		ModelAndView mv = new RedPigJModelAndView("admin/blue/imageself_list.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		Map<String,Object> maps= this.redPigQueryTools.getParams(currentPage, 5,orderBy, orderType);
		
		if ((user_name != null) && (!user_name.trim().equals(""))) {
			
			maps.put("user_userName", user_name.trim());
			mv.addObject("user_name", user_name);
		}
		
		maps.put("redpig_user_userRole", "ADMIN");
		maps.put("redpig_user_userRole1", "ADMIN_SELLER");
		
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
	@SecurityMapping(title = "会员相册删除", value = "/imageself_del*", rtype = "admin", rname = "图片管理", rcode = "selfimg_manage", rgroup = "自营")
	@RequestMapping({ "/imageself_del" })
	public String imageself_del(HttpServletRequest request,
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
						this.accessoryService.delete(acc.getId());
					}
					this.albumService.deleteById(Long.valueOf(Long.parseLong(id)));
				}
			}
		}
		String url = "redirect:/imageself_list?currentPage="
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
	@SecurityMapping(title = "会员相册图片列表", value = "/selfpic_list*", rtype = "admin", rname = "图片管理", rcode = "selfimg_manage", rgroup = "自营")
	@RequestMapping({ "/selfpic_list" })
	public ModelAndView selfpic_list(HttpServletRequest request,
			HttpServletResponse response, String aid, String currentPage,
			String orderBy, String orderType) {
		ModelAndView mv = new RedPigJModelAndView("admin/blue/selfpic_list.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		
		
		Map<String,Object> maps= this.redPigQueryTools.getParams(currentPage,50, orderBy, orderType);
		
		
		
		IPageList pList = this.accessoryService.list(maps);
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
	@SecurityMapping(title = "会员相册图片删除", value = "/selfpic_del*", rtype = "admin", rname = "图片管理", rcode = "selfimg_manage", rgroup = "自营")
	@RequestMapping({ "/selfpic_del" })
	public String selfpic_del(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String mulitId,
			String aid) {
		String[] ids = mulitId.split(",");

		for (String id : ids) {

			boolean flag = false;
			Accessory obj = this.accessoryService.selectByPrimaryKey(CommUtil
					.null2Long(id));
			for (Goods goods : obj.getGoods_list()) {
				if (goods.getGoods_main_photo().getId().equals(obj.getId())) {
					goods.setGoods_main_photo(null);
					this.goodsService.updateById(goods);
				}
				goods.getGoods_photos().remove(obj);
			}
			this.accessoryService.deleteById(CommUtil.null2Long(id));
			flag = true;
			if (flag) {
				RedPigCommUtil.del_acc(request, obj);
			}
		}
		String url = "redirect:/selfpic_list?currentPage=" + currentPage
				+ "&aid=" + aid;
		return url;
	}
}
