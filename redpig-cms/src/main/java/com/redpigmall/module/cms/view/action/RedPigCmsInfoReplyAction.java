package com.redpigmall.module.cms.view.action;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.redpigmall.api.annotation.SecurityMapping;
import com.redpigmall.api.mv.RedPigJModelAndView;
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.api.sec.SecurityUserHolder;
import com.redpigmall.api.tools.CommUtil;
import com.redpigmall.module.cms.manage.admin.action.base.BaseAction;
import com.redpigmall.domain.Accessory;
import com.redpigmall.domain.Information;
import com.redpigmall.domain.InformationReply;
import com.redpigmall.domain.User;


/**
 * 
 * <p>
 * Title: RedPigCmsInfoReplyAction.java
 * </p>
 * 
 * <p>
 * Description: 资讯回复控制器
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
 * @author redpig
 * 
 * @date 2015-2-6
 * 
 * @version redpigmall_b2b2c_2015
 */
@Controller
public class RedPigCmsInfoReplyAction extends BaseAction{
	
	/**
	 * 资讯回复保存
	 * @param request
	 * @param response
	 * @param content
	 * @param info_id
	 */
	@SecurityMapping(title = "资讯回复保存", value = "/cms/reply_save*", rtype = "buyer", rname = "资讯", rcode = "user_info", rgroup = "资讯")
	@RequestMapping({ "/cms/reply_save" })
	public void reply_saveEntity(HttpServletRequest request,
			HttpServletResponse response, String content, String info_id) {
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder
				.getCurrentUser().getId());
		InformationReply reply = new InformationReply();
		reply.setInfo_id(CommUtil.null2Long(info_id));
		reply.setAddTime(new Date());
		reply.setUserId(user.getId());
		reply.setUserName(user.getUserName());
		reply.setContent(content);
		Accessory acc = user.getPhoto();
		if (acc == null) {
			reply.setUserPhoto("resources/style/system/front/default/images/usercenter/base_person.jpg");
		} else {
			reply.setUserPhoto(user.getPhoto().getPath() + "/"
					+ user.getPhoto().getName());
		}
		this.replyService.saveEntity(reply);
		boolean ret = true;
		
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
	 * ajax回复
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param info_id
	 * @return
	 */
	@RequestMapping({ "/cms/reply_ajax" })
	public ModelAndView reply_ajax(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String info_id) {
		ModelAndView mv = new RedPigJModelAndView("/cms/reply_ajax.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		Information information = this.informationService.selectByPrimaryKey(CommUtil
				.null2Long(info_id));
		mv.addObject("information", information);
		
		Map<String,Object> maps= this.redPigQueryTools.getParams(currentPage,12, "addTime", "desc");
        maps.put("info_id", information.getId());
        
		IPageList pList = this.replyService.list(maps);
		CommUtil.saveIPageList2ModelAndView("", "", null, pList, mv);
		mv.addObject("replies", pList.getResult());
		return mv;
	}
}
