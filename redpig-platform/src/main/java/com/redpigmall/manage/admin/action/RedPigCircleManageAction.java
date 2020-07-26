package com.redpigmall.manage.admin.action;

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
import com.redpigmall.api.tools.WebForm;
import com.redpigmall.manage.admin.action.base.BaseAction;
import com.redpigmall.domain.Circle;
import com.redpigmall.domain.CircleClass;
import com.redpigmall.domain.CircleInvitation;
import com.redpigmall.domain.Message;
import com.redpigmall.domain.Navigation;
import com.redpigmall.domain.SysConfig;
import com.redpigmall.domain.User;

/**
 * 
 * <p>
 * Title: RedPigCircleManageAction.java
 * </p>
 * 
 * <p>
 * Description:
 * 商城圈子管理类，用户可以申请圈子，由平台审核，审核通过后该用户成为该圈子管理员，其他用户可以进入该圈子发布帖子，帖子由圈子管理员审核，
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
@SuppressWarnings({ "unchecked", "rawtypes" })
@Controller
public class RedPigCircleManageAction extends BaseAction {

	/**
	 * 圈子设置
	 * 
	 * @param request
	 * @param response
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "圈子设置", value = "/circle_set*", rtype = "admin", rname = "圈子管理", rcode = "circle_admin", rgroup = "网站")
	@RequestMapping({ "/circle_set" })
	public ModelAndView circle_set(HttpServletRequest request,
			HttpServletResponse response, String currentPage) {
		ModelAndView mv = new RedPigJModelAndView("admin/blue/circle_set.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request, response);
		List<Map> list = this.redpigIntegralViewTools.query_all_level();
		mv.addObject("list", list);
		return mv;
	}

	/**
	 * 圈子设置
	 * 
	 * @param request
	 * @param response
	 * @param id
	 * @return
	 */
	@SecurityMapping(title = "圈子设置", value = "/circle_set_save*", rtype = "admin", rname = "圈子管理", rcode = "circle_admin", rgroup = "网站")
	@RequestMapping({ "/circle_set_save" })
	public ModelAndView circle_set_saveEntity(HttpServletRequest request,
			HttpServletResponse response, String id) {
		ModelAndView mv = new RedPigJModelAndView("admin/blue/success.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request, response);
		SysConfig obj = this.redPigSysConfigService.getSysConfig();

		SysConfig sysConfig = null;
		if (id.equals("")) {
			sysConfig = (SysConfig) WebForm.toPo(request, SysConfig.class);
			sysConfig.setAddTime(new Date());
		} else {
			sysConfig = (SysConfig) WebForm.toPo(request, obj);
		}
		if (sysConfig.getCircle_open() == 1) {
			Map<String, Object> params = Maps.newHashMap();
			params.put("url", "circle/index");

			List<Navigation> navs = this.navigationService
					.queryPageList(params);

			if (navs.size() == 0) {
				Navigation nav = new Navigation();
				nav.setAddTime(new Date());
				nav.setDisplay(true);
				nav.setLocation(0);
				nav.setNew_win(1);
				nav.setSequence(10);
				nav.setSysNav(true);
				nav.setTitle("圈子");
				nav.setType("diy");
				nav.setUrl("circle/index");
				nav.setOriginal_url("circle/index");
				this.navigationService.saveEntity(nav);
			} else {
				((Navigation) navs.get(0)).setDisplay(true);
				this.navigationService.saveEntity((Navigation) navs.get(0));
			}
		} else {
			Map<String, Object> params = Maps.newHashMap();
			params.put("url", "circle/index");

			List<Navigation> navs = this.navigationService
					.queryPageList(params);

			if (navs.size() == 1) {
				((Navigation) navs.get(0)).setDisplay(false);
				this.navigationService.saveEntity((Navigation) navs.get(0));
			}
		}
		if (id.equals("")) {
			this.redPigSysConfigService.saveEntity(sysConfig);
		} else {
			this.redPigSysConfigService.updateById(sysConfig);
		}
		mv.addObject("op_title", "圈子设置成功");
		mv.addObject("list_url", CommUtil.getURL(request) + "/circle_set");
		return mv;
	}

	/**
	 * 圈子列表
	 * 
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param status
	 * @param class_id
	 * @param title
	 * @param userName
	 * @return
	 */
	@SecurityMapping(title = "圈子列表", value = "/circle_list*", rtype = "admin", rname = "圈子管理", rcode = "circle_admin", rgroup = "网站")
	@RequestMapping({ "/circle_list" })
	public ModelAndView circle_list(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String status,
			String class_id, String title, String userName) {
		ModelAndView mv = new RedPigJModelAndView(
				"admin/blue/circle_list.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request, response);
		Map<String, Object> params = this.redPigQueryTools.getParams(
				currentPage, "addTime", "desc");

		if ((title != null) && (!title.equals(""))) {
			params.put("title_like", CommUtil.null2String(title));

			mv.addObject("title", title);
		}

		if ((class_id != null) && (!class_id.equals(""))) {
			params.put("class_id", CommUtil.null2Long(class_id));

			mv.addObject("class_id", class_id);
		}

		if ((status != null) && (!status.equals(""))) {
			params.put("status", CommUtil.null2Int(status));

			mv.addObject("status", status);
		} else {
			params.put("status", 0);

		}

		if ((userName != null) && (!userName.equals(""))) {
			params.put("userName", CommUtil.null2String(userName));

			mv.addObject("userName", userName);
		}

		mv.addObject("status", status);
		IPageList pList = this.circleService.list(params);
		CommUtil.saveIPageList2ModelAndView("", "", null, pList, mv);
		params.clear();

		List<CircleClass> ccs = this.circleclassService.queryPageList(params);

		mv.addObject("ccs", ccs);
		return mv;
	}

	/**
	 * 圈子编辑
	 * 
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param id
	 * @return
	 */
	@SecurityMapping(title = "圈子编辑", value = "/circle_edit*", rtype = "admin", rname = "圈子管理", rcode = "circle_admin", rgroup = "网站")
	@RequestMapping({ "/circle_edit" })
	public ModelAndView circle_edit(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String id) {
		ModelAndView mv = new RedPigJModelAndView(
				"admin/blue/circle_edit.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request, response);
		Circle obj = this.circleService.selectByPrimaryKey(CommUtil
				.null2Long(id));
		mv.addObject("obj", obj);
		return mv;
	}

	/**
	 * 圈子保存
	 * 
	 * @param request
	 * @param response
	 * @param id
	 * @param status
	 * @param refuseMsg
	 * @return
	 */
	@SecurityMapping(title = "圈子保存", value = "/circle_save*", rtype = "admin", rname = "圈子管理", rcode = "circle_admin", rgroup = "网站")
	@RequestMapping({ "/circle_save" })
	public ModelAndView circle_saveEntity(HttpServletRequest request,
			HttpServletResponse response, String id, String status,
			String refuseMsg) {
		Circle obj = this.circleService.selectByPrimaryKey(Long.valueOf(Long
				.parseLong(id)));
		obj.setStatus(CommUtil.null2Int(status));
		this.circleService.updateById(obj);
		User fromuser = this.userService.selectByPrimaryKey(SecurityUserHolder
				.getCurrentUser().getId());
		User touser = this.userService.selectByPrimaryKey(Long.valueOf(obj
				.getUser_id()));
		if (status.equals("5")) {
			String content = "您申请的圈子“" + obj.getTitle() + "”已经审核通过，感谢您对"
					+ this.redPigSysConfigService.getSysConfig().getTitle() + "的支持！";
			Message msg = new Message();
			msg.setAddTime(new Date());
			msg.setContent(content);
			msg.setFromUser(fromuser);
			msg.setToUser(touser);
			msg.setType(0);
			this.messageService.saveEntity(msg);
		}
		if (status.equals("-1")) {
			String content = "您申请的圈子“" + obj.getTitle() + "”审核没有通过，拒绝理由：“"
					+ refuseMsg + "”！";
			Message msg = new Message();
			msg.setAddTime(new Date());
			msg.setContent(content);
			msg.setFromUser(fromuser);
			msg.setToUser(touser);
			msg.setType(0);
			this.messageService.saveEntity(msg);
		}
		ModelAndView mv = new RedPigJModelAndView("admin/blue/success.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request, response);
		mv.addObject("list_url", "/circle_list");
		mv.addObject("op_title", "圈子审核成功");
		return mv;
	}

	/**
	 * 圈子删除
	 * 
	 * @param request
	 * @param response
	 * @param mulitId
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "圈子删除", value = "/circle_del*", rtype = "admin", rname = "圈子管理", rcode = "circle_admin", rgroup = "网站")
	@RequestMapping({ "/circle_del" })
	public String circle_del(HttpServletRequest request,
			HttpServletResponse response, String mulitId, String currentPage) {
		String[] ids = mulitId.split(",");
		User fromuser = this.userService.selectByPrimaryKey(SecurityUserHolder
				.getCurrentUser().getId());
		for (String id : ids) {
			if (!id.equals("")) {
				Circle circle = this.circleService.selectByPrimaryKey(Long
						.valueOf(Long.parseLong(id)));
				if (circle != null) {
					User touser = this.userService.selectByPrimaryKey(Long
							.valueOf(circle.getUser_id()));
					List<Map> maps = JSON.parseArray(
							touser.getCircle_create_info(), Map.class);
					for (Map map : maps) {
						if (CommUtil.null2String(map.get("id")).equals(id)) {
							maps.remove(map);
							break;
						}
					}
					touser.setCircle_create_info(JSON.toJSONString(maps));
					this.userService.updateById(touser);

					Map<String, Object> params = Maps.newHashMap();
					params.put("circle_id", circle.getId());

					List<CircleInvitation> objs = this.invitationService
							.queryPageList(params);

					for (CircleInvitation obj : objs) {
						this.invitationService.deleteById(obj.getId());
					}
					String content = "由于您创建的圈子“" + circle.getTitle()
							+ "”违反平台相关规定，管理员已经将其删除，如有疑问请联系平台管理员！";
					Message msg = new Message();
					msg.setAddTime(new Date());
					msg.setContent(content);
					msg.setFromUser(fromuser);
					msg.setTitle("您创建的圈子已经被删除");
					msg.setToUser(touser);
					msg.setType(0);
					this.messageService.saveEntity(msg);
				}
				this.circleService.deleteById(Long.valueOf(Long.parseLong(id)));
			}
		}
		return "redirect:circle_list?currentPage=" + currentPage;
	}

	/**
	 * 圈子删除
	 * 
	 * @param request
	 * @param response
	 * @param id
	 * @param fieldName
	 * @param value
	 * @throws ClassNotFoundException
	 */
	@SecurityMapping(title = "圈子删除", value = "/circle_ajax*", rtype = "admin", rname = "圈子管理", rcode = "circle_admin", rgroup = "网站")
	@RequestMapping({ "/circle_ajax" })
	public void circle_ajax(HttpServletRequest request,
			HttpServletResponse response, String id, String fieldName,
			String value) throws ClassNotFoundException {
		Circle obj = this.circleService.selectByPrimaryKey(Long.valueOf(Long
				.parseLong(id)));
		if (fieldName.equals("recommend")) {
			if (obj.getRecommend() == 1) {
				obj.setRecommend(0);
			} else {
				obj.setRecommend(1);
			}
		}
		this.circleService.updateById(obj);
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		try {
			PrintWriter writer = response.getWriter();
			writer.print(obj.getRecommend());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 帖子列表
	 * 
	 * @param request
	 * @param response
	 * @param cid
	 * @param user_name
	 * @param title
	 * @return
	 */
	@SecurityMapping(title = "帖子列表", value = "/circle_invitation*", rtype = "admin", rname = "圈子管理", rcode = "circle_admin", rgroup = "网站")
	@RequestMapping({ "/circle_invitation" })
	public ModelAndView circle_invitation(HttpServletRequest request,
			HttpServletResponse response, String cid, String user_name,
			String title) {
		ModelAndView mv = new RedPigJModelAndView(
				"admin/blue/circle_invitation.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request, response);
		
		Map<String, Object> params = this.redPigQueryTools.getParams(null,
				"addTime", "desc");

		params.put("circle_id", CommUtil.null2Long(cid));

		if ((user_name != null) && (!user_name.equals(""))) {
			
			params.put("userName_like", CommUtil.null2String(user_name));

			mv.addObject("user_name", user_name);
		}
		if ((title != null) && (!title.equals(""))) {

			params.put("title_like", CommUtil.null2String(title));

			mv.addObject("title", title);
		}
		IPageList pList = this.invitationService.list(params);
		CommUtil.saveIPageList2ModelAndView("", "", null, pList, mv);
		mv.addObject("cid", cid);
		return mv;
	}

	/**
	 * 帖子删除
	 * 
	 * @param request
	 * @param response
	 * @param mulitId
	 * @param currentPage
	 * @param cid
	 * @return
	 */
	@SecurityMapping(title = "帖子删除", value = "/circle_invitation_delete*", rtype = "admin", rname = "圈子管理", rcode = "circle_admin", rgroup = "网站")
	@RequestMapping({ "/circle_invitation_delete" })
	public String circle_invitation_deleteById(HttpServletRequest request,
			HttpServletResponse response, String mulitId, String currentPage,
			String cid) {
		String[] ids = mulitId.split(",");
		List temp_ids = Lists.newArrayList();
		for (String id : ids) {
			if (!id.equals("")) {
				Map<String, Object> params = Maps.newHashMap();
				params.put("invitation_id", CommUtil.null2Long(id));

				List reply_ids = this.invitationReplyService
						.queryPageList(params);

				List dele_ids = Lists.newArrayList();
				for (Object temp_id : reply_ids) {
					dele_ids.add(CommUtil.null2Long(temp_id));
				}
				this.invitationReplyService.batchDeleteByIds(dele_ids);
				temp_ids.add(CommUtil.null2Long(id));
			}
		}
		this.invitationService.batchDeleteByIds(temp_ids);
		Circle obj = this.circleService.selectByPrimaryKey(CommUtil
				.null2Long(cid));
		obj.setInvitation_count(obj.getInvitation_count() - temp_ids.size());
		this.circleService.updateById(obj);
		return "redirect:circle_invitation?currentPage=" + currentPage
				+ "&cid=" + cid;
	}
}
