package com.redpigmall.manage.admin.action;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;
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
import com.redpigmall.api.sec.SecurityUserHolder;
import com.redpigmall.api.tools.CommUtil;
import com.redpigmall.api.tools.WebForm;
import com.redpigmall.manage.admin.action.base.BaseAction;
import com.redpigmall.domain.Accessory;
import com.redpigmall.domain.ReplyContent;
import com.redpigmall.domain.User;

/**
 * <p>
 * Title: RedPigReplyContentManageAction.java
 * </p>
 * 
 * <p>
 * Description:后台微商城管理
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
 * @date 2014-11-20
 * 
 * @version redpigmall_b2b2c 8.0
 */
@Controller
public class RedPigReplyContentManageAction extends BaseAction {

	/**
	 * 素材管理列表
	 * 
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @param title_info
	 * @param way_info
	 * @return
	 */
	@SecurityMapping(title = "素材管理列表", value = "/reply_content_list*", rtype = "admin", rname = "微信素材管理", rcode = "admin_reply_content", rgroup = "运营")
	@RequestMapping({ "/reply_content_list" })
	public ModelAndView reply_content_list(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,
			String orderType, String title_info, String way_info) {

		ModelAndView mv = new RedPigJModelAndView(
				"admin/blue/reply_content_list.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		String url = this.configService.getSysConfig().getAddress();
		if ((url == null) || (url.equals(""))) {
			url = CommUtil.getURL(request);
		}
		String params = "";

		Map<String, Object> maps = this.redPigQueryTools.getParams(currentPage,
				10, "sequence", "asc");

		if ((way_info != null) && (!"".equals(way_info))) {
			if ("0".equals(way_info)) {
				maps.put("way", CommUtil.null2Int(way_info));
			} else {
				maps.put("way", CommUtil.null2Int(way_info));
			}
			mv.addObject("way_info", way_info);
		}

		if ((title_info != null) && (!"".equals(title_info))) {
			maps.put("title_like", title_info);

			mv.addObject("title_info", title_info);
		}

		IPageList pList = this.replycontentService.list(maps);
		CommUtil.saveIPageList2ModelAndView(url
				+ "/reply_content_list.html", "", params, pList, mv);
		return mv;
	}

	/**
	 * 添加素材管理
	 * 
	 * @param request
	 * @param response
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "添加素材管理", value = "/reply_content_add*", rtype = "admin", rname = "微信素材管理", rcode = "admin_reply_content", rgroup = "运营")
	@RequestMapping({ "/reply_content_add" })
	public ModelAndView reply_content_add(HttpServletRequest request,
			HttpServletResponse response, String currentPage) {

		ModelAndView mv = new RedPigJModelAndView(
				"admin/blue/reply_content_add.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		mv.addObject("currentPage", currentPage);
		return mv;
	}

	/**
	 * 素材编辑管理
	 * 
	 * @param request
	 * @param response
	 * @param id
	 * @param currentPage
	 * @param way
	 * @param title
	 * @param content
	 * @param author
	 * @return
	 */
	@SecurityMapping(title = "素材编辑管理", value = "/reply_content_edit*", rtype = "admin", rname = "微信素材管理", rcode = "admin_reply_content", rgroup = "运营")
	@RequestMapping({ "/reply_content_edit" })
	public ModelAndView reply_content_edit(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage,
			String way, String title, String content, String author) {

		ModelAndView mv = new RedPigJModelAndView(
				"admin/blue/reply_content_add.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);

		if ((id != null) && (!id.equals(""))) {
			ReplyContent replycontent = this.replycontentService
					.selectByPrimaryKey(Long.parseLong(id));
			mv.addObject("way", way);
			mv.addObject("obj", replycontent);
			mv.addObject("currentPage", currentPage);
			mv.addObject("edit", Boolean.valueOf(true));
		}
		return mv;
	}

	/**
	 * 素材保存管理
	 * 
	 * @param request
	 * @param response
	 * @param id
	 * @param currentPage
	 * @param cmd
	 * @param title
	 * @param content
	 * @param url
	 * @param way
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@SecurityMapping(title = "素材保存管理", value = "/reply_content_save*", rtype = "admin", rname = "微信素材管理", rcode = "admin_reply_content", rgroup = "运营")
	@RequestMapping({ "/reply_content_save" })
	public ModelAndView reply_content_saveEntity(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage,
			String cmd, String title, String content, String url, int way) {

		ReplyContent replycontent = null;
		if (id.equals("")) {
			replycontent = (ReplyContent) WebForm.toPo(request,
					ReplyContent.class);
			replycontent.setAddTime(new Date());
		} else {
			ReplyContent obj = this.replycontentService.selectByPrimaryKey(Long
					.valueOf(Long.parseLong(id)));
			replycontent = (ReplyContent) WebForm.toPo(request, obj);
		}
		if (way == 1) {
			String uploadFilePath = this.configService.getSysConfig()
					.getUploadFilePath();
			String saveFilePathName = request.getSession().getServletContext()
					.getRealPath("/")
					+ uploadFilePath + File.separator + "system";
			ReplyContent rc = this.replycontentService
					.selectByPrimaryKey(CommUtil.null2Long(id));
			Map<String, Object> map = Maps.newHashMap();
			Map map_1 = Maps.newHashMap();
			String fileName = null;
			String fileName_1 = null;
			try {
				if ((id != null) && (!"".equals(id)) && (rc.getImg() != null)
						&& (!"".equals(rc.getImg())) && (rc.getImg_1() != null)
						&& (!"".equals(rc.getImg_1()))) {
					fileName = rc.getImg().getName();
					map = CommUtil.saveFileToServer(request, "img",
							saveFilePathName, fileName, null);
					fileName_1 = rc.getImg_1().getName();
					map_1 = CommUtil.saveFileToServer(request, "img_1",
							saveFilePathName, fileName_1, null);
					if (map.get("fileName") != "") {
						Accessory img = replycontent.getImg();
						img.setName(CommUtil.null2String(map.get("fileName")));
						img.setExt((String) map.get("mime"));
						img.setSize(BigDecimal.valueOf(Double.parseDouble(map
								.get("fileSize").toString())));
						img.setPath(uploadFilePath + "/system");
						img.setWidth(((Integer) map.get("width")).intValue());
						img.setHeight(((Integer) map.get("height")).intValue());
						img.setAddTime(new Date());
						this.accessoryService.saveEntity(img);
						replycontent.setImg(img);
					}
					if (map_1.get("fileName") != "") {
						Accessory img_1 = replycontent.getImg_1();
						img_1.setName(CommUtil.null2String(map_1
								.get("fileName")));
						img_1.setExt((String) map_1.get("mime"));
						img_1.setSize(BigDecimal.valueOf(Double
								.parseDouble(map_1.get("fileSize").toString())));
						img_1.setPath(uploadFilePath + "/system");
						img_1.setWidth(((Integer) map.get("width")).intValue());
						img_1.setHeight(((Integer) map.get("height"))
								.intValue());
						img_1.setAddTime(new Date());
						this.accessoryService.saveEntity(img_1);
						replycontent.setImg_1(img_1);
					}
				} else {
					fileName = "";
					map = CommUtil.saveFileToServer(request, "img",
							saveFilePathName, fileName, null);
					fileName_1 = "";
					map_1 = CommUtil.saveFileToServer(request, "img_1",
							saveFilePathName, fileName_1, null);
					if (map.get("fileName") != "") {
						Accessory img = new Accessory();
						img.setName(CommUtil.null2String(map.get("fileName")));
						img.setExt(CommUtil.null2String(map.get("mime")));
						img.setSize(BigDecimal.valueOf(Double.parseDouble(map
								.get("fileSize").toString())));
						img.setPath(uploadFilePath + "/system");
						img.setWidth(CommUtil.null2Int(map.get("width")));
						img.setHeight(CommUtil.null2Int(map.get("height")));
						this.accessoryService.saveEntity(img);
						replycontent.setImg(img);
					}
					if (map_1.get("fileName") != "") {
						Accessory img_1 = new Accessory();
						img_1.setName(CommUtil.null2String(map_1
								.get("fileName")));
						img_1.setExt((String) map_1.get("mime"));
						img_1.setSize(BigDecimal.valueOf(Double
								.parseDouble(map_1.get("fileSize").toString())));
						img_1.setPath(uploadFilePath + "/system");
						img_1.setWidth(((Integer) map.get("width")).intValue());
						img_1.setHeight(((Integer) map.get("height"))
								.intValue());
						img_1.setAddTime(new Date());
						this.accessoryService.saveEntity(img_1);
						replycontent.setImg_1(img_1);
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			if (id.equals("")) {
				User user = SecurityUserHolder.getCurrentUser();
				replycontent.setAuthor(user.getUserName());
				this.replycontentService.saveEntity(replycontent);
			} else if ((replycontent.getAuthor() == null)
					|| ("".equals(replycontent.getAuthor()))) {
				User user = SecurityUserHolder.getCurrentUser();
				if ((user != null) && (!"".equals(user))) {
					replycontent.setAuthor(user.getUserName());
				}
				this.replycontentService.updateById(replycontent);
			}
		} else if (id.equals("")) {
			User user = SecurityUserHolder.getCurrentUser();
			if ((user != null) && (!"".equals(user))) {
				replycontent.setAuthor(user.getUserName());
			}
			this.replycontentService.saveEntity(replycontent);
		} else {
			System.out.println(replycontent.getAuthor());
			if ((replycontent.getAuthor() == null)
					|| ("".equals(replycontent.getAuthor()))) {
				User user = SecurityUserHolder.getCurrentUser();
				if ((user != null) && (!"".equals(user))) {
					replycontent.setAuthor(user.getUserName());
				}
			}
			this.replycontentService.updateById(replycontent);
		}
		ModelAndView mv = new RedPigJModelAndView("admin/blue/success.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		mv.addObject("list_url", CommUtil.getURL(request)
				+ "/reply_content_list");
		mv.addObject("op_title", "保存素材成功");
		return mv;
	}

	/**
	 * 删除素材
	 * 
	 * @param request
	 * @param response
	 * @param id
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "删除素材", value = "/reply_content_delete*", rtype = "admin", rname = "微信素材管理", rcode = "admin_reply_content", rgroup = "运营")
	@RequestMapping({ "/reply_content_del" })
	public String reply_content_deleteById(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage) {

		if (!id.equals("")) {
			this.replycontentService
					.deleteById(Long.valueOf(Long.parseLong(id)));
		}
		return "redirect:reply_content_list?currentPage=" + currentPage;
	}
}
