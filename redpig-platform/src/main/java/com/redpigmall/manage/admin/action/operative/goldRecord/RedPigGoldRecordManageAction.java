package com.redpigmall.manage.admin.action.operative.goldRecord;

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
import com.redpigmall.api.tools.WebForm;
import com.redpigmall.manage.admin.action.base.BaseAction;
import com.redpigmall.domain.GoldLog;
import com.redpigmall.domain.GoldRecord;
import com.redpigmall.domain.User;


/**
 * 
 * <p>
 * Title: RedPigGoldRecordManageAction.java
 * </p>
 * 
 * <p>
 * Description: 系统金币管理类
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
 * @date 2014年5月27日
 * 
 * @version 1.0
 */
@Controller
public class RedPigGoldRecordManageAction extends BaseAction{
	
	/**
	 * 金币购买记录
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @param beginTime
	 * @param endTime
	 * @param beginCount
	 * @param endCount
	 * @return
	 */
	@SecurityMapping(title = "金币购买记录", value = "/gold_record*", rtype = "admin", rname = "金币管理", rcode = "gold_record_admin", rgroup = "运营")
	@RequestMapping({ "/gold_record" })
	public ModelAndView gold_record(
			HttpServletRequest request,
			HttpServletResponse response, 
			String currentPage, String orderBy,
			String orderType, String beginTime, String endTime,
			String beginCount, String endCount) {
		ModelAndView mv = new RedPigJModelAndView("admin/blue/gold_record.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request, response);
		
		if (this.redPigSysConfigService.getSysConfig().getGold()) {
			
			Map<String,Object> params = redPigQueryTools.getParams(currentPage, orderBy, orderType);
			
			if (!CommUtil.null2String(beginTime).equals("")) {
				
				params.put("addTime_more_thran_equal", CommUtil.formatDate(beginTime));
				
			}
			if (!CommUtil.null2String(endTime).equals("")) {
				
				params.put("addTime_less_thran_equal", CommUtil.formatDate(endTime));
				
			}
			if (!CommUtil.null2String(beginCount).equals("")) {
				
				params.put("gold_count_more_thran_equal", CommUtil.null2Int(beginCount));
				
			}
			if (!CommUtil.null2String(endCount).equals("")) {
				
				params.put("gold_count_less_thran_equal", CommUtil.null2Int(endCount));
				
			}
			
			IPageList pList = this.redPigGoldrecordService.list(params);
			CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
			mv.addObject("beginTime", beginTime);
			mv.addObject("endTime", endTime);
			mv.addObject("beginCount", beginCount);
			mv.addObject("endCount", endCount);
		} else {
			mv = new RedPigJModelAndView("admin/blue/error.html",
					this.redPigSysConfigService.getSysConfig(),
					this.redPigUserConfigService.getUserConfig(), 0, request,
					response);
			mv.addObject("op_title", "系统未开启金币");
			mv.addObject("list_url", CommUtil.getURL(request)
					+ "/operation_base_set");
		}
		return mv;
	}
	
	/**
	 * 金币日志列表
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @param beginTime
	 * @param endTime
	 * @return
	 */
	@SecurityMapping(title = "金币日志列表", value = "/gold_log*", rtype = "admin", rname = "金币管理", rcode = "gold_record_admin", rgroup = "运营")
	@RequestMapping({ "/gold_log" })
	public ModelAndView gold_log(
			HttpServletRequest request,
			HttpServletResponse response, 
			String currentPage, 
			String orderBy,
			String orderType, 
			String beginTime, 
			String endTime) {
		
		ModelAndView mv = new RedPigJModelAndView("admin/blue/gold_log.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request, response);
		
		if (this.redPigSysConfigService.getSysConfig().getGold()) {
			
			Map<String,Object> params = redPigQueryTools.getParams(currentPage, orderBy, orderType);
			
			if (!CommUtil.null2String(beginTime).equals("")) {
				
				params.put("addTime_more_thran_equal", CommUtil.formatDate(beginTime));
				
			}
			if (!CommUtil.null2String(endTime).equals("")) {
				
				params.put("addTime_less_thran_equal", CommUtil.formatDate(endTime));
				
			}
			
			
			IPageList pList = this.redPigGoldLogService.list(params);
			CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
			mv.addObject("beginTime", beginTime);
			mv.addObject("endTime", endTime);
		} else {
			mv = new RedPigJModelAndView("admin/blue/error.html",
					this.redPigSysConfigService.getSysConfig(),
					this.redPigUserConfigService.getUserConfig(), 0, request,
					response);
			
			mv.addObject("op_title", "系统未开启金币");
			mv.addObject("list_url", CommUtil.getURL(request)
					+ "/operation_base_set");
		}
		return mv;
	}
	
	/**
	 * 金币购买记录编辑
	 * @param request
	 * @param response
	 * @param id
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "金币购买记录编辑", value = "/gold_record_edit*", rtype = "admin", rname = "金币管理", rcode = "gold_record_admin", rgroup = "运营")
	@RequestMapping({ "/gold_record_edit" })
	public ModelAndView gold_record_edit(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage) {
		ModelAndView mv = new RedPigJModelAndView("admin/blue/gold_record_edit.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request, response);
		if (this.redPigSysConfigService.getSysConfig().getGold()) {
			if ((id != null) && (!id.equals(""))) {
				GoldRecord goldrecord = this.redPigGoldrecordService.selectByPrimaryKey(Long.valueOf(Long.parseLong(id)));
				
				if (goldrecord.getGold_status() == 0) {
					mv.addObject("obj", goldrecord);
					mv.addObject("currentPage", currentPage);
				} else {
					mv = new RedPigJModelAndView("admin/blue/error.html",
							this.redPigSysConfigService.getSysConfig(),
							this.redPigUserConfigService.getUserConfig(), 0, request,
							response);
					mv.addObject("op_title", "参数错误，编辑失败");
					mv.addObject("list_url", CommUtil.getURL(request)
							+ "/gold_record");
				}
			}
		} else {
			mv = new RedPigJModelAndView("admin/blue/error.html",
					this.redPigSysConfigService.getSysConfig(),
					this.redPigUserConfigService.getUserConfig(), 0, request,
					response);
			mv.addObject("op_title", "系统未开启金币");
			mv.addObject("list_url", CommUtil.getURL(request)
					+ "/operation_base_set");
		}
		return mv;
	}
	
	/**
	 * 金币购买记录
	 * @param request
	 * @param response
	 * @param id
	 * @param currentPage
	 * @param cmd
	 * @param list_url
	 * @return
	 */
	@SecurityMapping(title = "金币购买记录", value = "/gold_record_save*", rtype = "admin", rname = "金币管理", rcode = "gold_record_admin", rgroup = "运营")
	@RequestMapping({ "/gold_record_save" })
	public ModelAndView gold_record_save(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage,
			String cmd, String list_url) {
		ModelAndView mv = new RedPigJModelAndView("admin/blue/success.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request, response);
		if (this.redPigSysConfigService.getSysConfig().getGold()) {
			GoldRecord goldrecord = null;
			if (id.equals("")) {
				goldrecord = (GoldRecord) WebForm.toPo(request,
						GoldRecord.class);
				goldrecord.setAddTime(new Date());
			} else {
				GoldRecord obj = this.redPigGoldrecordService.selectByPrimaryKey(Long.valueOf(Long.parseLong(id)));
				goldrecord = (GoldRecord) WebForm.toPo(request, obj);
			}
			if (goldrecord.getGold_pay_status() == 2) {
				goldrecord.setGold_status(1);
			}
			
			goldrecord.setGold_admin(SecurityUserHolder.getCurrentUser());
			goldrecord.setGold_admin_info("管理员审核金币");
			goldrecord.setGold_admin_time(new Date());
			if (id.equals("")) {
				this.redPigGoldrecordService.saveEntity(goldrecord);
			} else {
				this.redPigGoldrecordService.updateById(goldrecord);
			}
			if (goldrecord.getGold_pay_status() == 2) {
				User user = goldrecord.getGold_user();
				user.setGold(user.getGold() + goldrecord.getGold_count());
				this.redPigUserService.updateById(user);
				GoldLog log = new GoldLog();
				log.setAddTime(new Date());
				log.setGl_admin(SecurityUserHolder.getCurrentUser());
				log.setGl_admin_content(goldrecord.getGold_admin_info());
				log.setGl_admin_time(new Date());
				log.setGl_content("管理员审核金币记录");
				log.setGl_count(goldrecord.getGold_count());
				log.setGl_money(goldrecord.getGold_money());
				log.setGl_payment(goldrecord.getGold_payment());
				log.setGl_type(0);
				log.setGl_user(goldrecord.getGold_user());
				this.redPigGoldLogService.saveEntity(log);
			}
			mv.addObject("list_url", list_url);
			mv.addObject("op_title", "编辑金币记录成功");
		} else {
			mv = new RedPigJModelAndView("admin/blue/error.html",
					this.redPigSysConfigService.getSysConfig(),
					this.redPigUserConfigService.getUserConfig(), 0, request,
					response);
			mv.addObject("op_title", "系统未开启金币");
			mv.addObject("list_url", CommUtil.getURL(request)
					+ "/operation_base_set");
		}
		return mv;
	}
	
	/**
	 * 金币购买记录删除
	 * @param request
	 * @param response
	 * @param mulitId
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "金币购买记录删除", value = "/gold_record_del*", rtype = "admin", rname = "金币管理", rcode = "gold_record_admin", rgroup = "运营")
	@RequestMapping({ "/gold_record_del" })
	public String gold_record_del(
			HttpServletRequest request,
			HttpServletResponse response, String mulitId, String currentPage) {
		if (this.redPigSysConfigService.getSysConfig().getGold()) {
			String[] ids = mulitId.split(",");
			for (String id : ids) {
				if (!id.equals("")) {
					this.redPigGoldrecordService.deleteById(Long.valueOf(Long.parseLong(id)));
				}
			}
		}
		return "redirect:gold_record?currentPage=" + currentPage;
	}

	/**
	 * 金币购买记录
	 * @param request
	 * @param response
	 * @param id
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "金币购买记录", value = "/gold_record_view*", rtype = "admin", rname = "金币管理", rcode = "gold_record_admin", rgroup = "运营")
	@RequestMapping({ "/gold_record_view" })
	public ModelAndView gold_record_view(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage) {
		ModelAndView mv = new RedPigJModelAndView("admin/blue/gold_record_view.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request, response);
		if (this.redPigSysConfigService.getSysConfig().getGold()) {
			if ((id != null) && (!id.equals(""))) {
				GoldRecord goldrecord = this.redPigGoldrecordService.selectByPrimaryKey(Long.valueOf(Long.parseLong(id)));
				
				if (goldrecord.getGold_status() != 0) {
					mv.addObject("obj", goldrecord);
					mv.addObject("currentPage", currentPage);
				} else {
					mv = new RedPigJModelAndView("admin/blue/error.html",
							this.redPigSysConfigService.getSysConfig(),
							this.redPigUserConfigService.getUserConfig(), 0, request,
							response);
					mv.addObject("op_title", "参数错误，编辑失败");
					mv.addObject("list_url", CommUtil.getURL(request)
							+ "/gold_record");
				}
			}
		} else {
			mv = new RedPigJModelAndView("admin/blue/error.html",
					this.redPigSysConfigService.getSysConfig(),
					this.redPigUserConfigService.getUserConfig(), 0, request,
					response);
			mv.addObject("op_title", "系统未开启金币");
			mv.addObject("list_url", CommUtil.getURL(request)
					+ "/operation_base_set");
		}
		return mv;
	}
}
