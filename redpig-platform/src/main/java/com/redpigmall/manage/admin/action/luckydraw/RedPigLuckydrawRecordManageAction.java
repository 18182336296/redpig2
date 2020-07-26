package com.redpigmall.manage.admin.action.luckydraw;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.redpigmall.api.mv.RedPigJModelAndView;
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.api.sec.SecurityUserHolder;
import com.redpigmall.api.tools.CommUtil;
import com.redpigmall.api.tools.RedPigMaps;
import com.redpigmall.api.tools.StringUtils;
import com.redpigmall.api.tools.WebForm;
import com.redpigmall.domain.*;
import com.redpigmall.manage.admin.action.base.BaseAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 
 * <p>
 * Title: RedPigLuckydrawRecordManageAction.java
 * </p>
 * 
 * <p>
 * Description: 抽奖记录管理
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2018
 * </p>
 * 
 * <p>
 * Company: www.redpigmall.net
 * </p>
 * 
 * @author redpig
 * 
 * @date 2018年9月9日
 * 
 * @version redpigmall_b2b2c 8.0
 */
@Controller
public class RedPigLuckydrawRecordManageAction extends BaseAction{

	Logger logger = LoggerFactory.getLogger(RedPigLuckydrawRecordManageAction.class);
	/**
	 * 所有活动列表
	 * @param request
	 * @param response
	 * @param currentPage
	 * @return
	 */
	@RequestMapping({ "/luckydraw_record_list" })
	public ModelAndView luckydraw_record_list(HttpServletRequest request,
			HttpServletResponse response, String currentPage,
			String mobile,String beginTime,String endTime) {
		ModelAndView mv = new RedPigJModelAndView("admin/blue/luckydraw/luckydraw_record_list.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request,
				response);

		String url = this.redPigSysConfigService.getSysConfig().getAddress();
		Map<String,Object> params = this.redPigQueryTools.getParams(currentPage, "addTime", "desc");
		if ((url == null) || (url.equals(""))) {
			url = CommUtil.getURL(request);
		}
		if (!CommUtil.null2String(mobile).equals("")) {
			mobile = mobile.trim();
			Map<String, Object> map = new HashMap<>();
			map.put("mobile",mobile);
			List<User> users = this.userService.selectObjByProperty(map);
			if (users!=null&&!users.isEmpty()){
			    User user = users.get(0);
                params.put("user_id", user.getId());
            }
		}
		if (!CommUtil.null2String(beginTime).equals("")) {
			params.put("add_Time_more_than_equal", CommUtil.formatDate(beginTime));
		}
		if (!CommUtil.null2String(endTime).equals("")) {
			params.put("add_Time_less_than_equal", CommUtil.formatDate(endTime));
		}

        mv.addObject("mobile",mobile);
		mv.addObject("beginTime", beginTime);
		mv.addObject("endTime", endTime);
		IPageList pList = this.luckydrawRecordService.list(params);
		CommUtil.saveIPageList2ModelAndView(url + "/luckydraw/luckydraw_record_list.html", "","", pList, mv);
		return mv;
	}

	/**
	 * 新增活动
	 * @param request
	 * @param response
	 * @param currentPage
	 * @return
	 */
	@RequestMapping({ "/luckydraw_record_add" })
	public ModelAndView collage_add(
			HttpServletRequest request,
			HttpServletResponse response,
			String currentPage) {

		ModelAndView mv = new RedPigJModelAndView("admin/blue/luckydraw_record/luckydraw_record_add.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request,
				response);
		return mv;
	}


	/**
	 * 抽奖保存
	 * @param request
	 * @param response
	 * @param id
	 * @param currentPage
	 * @return
	 */
	@RequestMapping({ "/luckydraw_record_save" })
	public ModelAndView collage_save(
			HttpServletRequest request,HttpServletResponse response,
			String id, String currentPage,String luckydraw_name,String user_level_id,
			String beginTime,String endTime,String consume_integral,String limit_number,
			String win_rate,String remark) {
		// 返回页面
		ModelAndView mv = new RedPigJModelAndView("admin/blue/success.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request,
				response);

		User user = this.userService.selectByPrimaryKey(SecurityUserHolder.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();

		// 创建抽奖活动实体对象
		Luckydraw ld = null;
		if (CommUtil.null2String(id).equals("")) {
			ld = (Luckydraw) WebForm.toPo(request, Luckydraw.class);
			ld.setAddTime(new Date());
		} else {
			Luckydraw obj = this.luckydrawService.selectByPrimaryKey(Long.valueOf(Long.parseLong(id)));
			ld = (Luckydraw) WebForm.toPo(request, obj);
		}
		ld.setBeginTime(Timestamp.valueOf(beginTime));
		ld.setEndTime(Timestamp.valueOf(endTime));
		if (StringUtils.isNotBlank(remark)){
			ld.setRemark(remark);
		}
		ld.setLuckydraw_name(CommUtil.null2String(luckydraw_name));
		ld.setUserLevel(this.userlevelService.selectByPrimaryKey(Long.parseLong(user_level_id)));
		ld.setConsume_integral(CommUtil.null2Int(consume_integral));
		ld.setLimit_number(CommUtil.null2Int(limit_number));
		ld.setWin_rate(CommUtil.null2Int(win_rate));
		ld.setRemark(CommUtil.null2String(remark));

		// 判断活动是否开始，设置活动状态
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		Date currentDate = new Date();
		try {
			if (currentDate.compareTo(df.parse(beginTime))>0){
				ld.setStatus(2);
			}else{
				ld.setStatus(1);
			}
		}catch (ParseException e){
			logger.error("日期转换出错！");
		}

		// 保存商品信息？？？

		if (CommUtil.null2String(id).equals("")) {
			this.luckydrawService.saveEntity(ld);
		} else {
			this.luckydrawService.updateById(ld);
		}

		mv.addObject("list_url", CommUtil.getURL(request) + "/luckydraw_record_list");
		mv.addObject("op_title", "保存抽奖成功");
		mv.addObject("add_url", CommUtil.getURL(request) + "/luckydraw_record_add" + "?currentPage=" + currentPage);
		return mv;
	}

	/**
	 *
	 * nuke_edit:抽奖编辑. <br/>
	 *
	 * @author redpig
	 * @param request
	 * @param response
	 * @param id
	 * @return
	 * @since JDK 1.8
	 */
	@RequestMapping({ "/luckydraw_record_edit" })
	public ModelAndView collage_edit(HttpServletRequest request,HttpServletResponse response,String id) {

		ModelAndView mv = new RedPigJModelAndView("admin/blue/luckydraw_record/luckydraw_record_add.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request, response);

		if ((id != null) && (!id.equals(""))) {
			Luckydraw luckydraw= this.luckydrawService.selectByPrimaryKey(CommUtil.null2Long(id));
			mv.addObject("obj", luckydraw);
			mv.addObject("edit", Boolean.valueOf(true));
		}
		return mv;
	}

	/**
	 * 查看抽奖信息
	 * @param request
	 * @param response
	 * @param id
	 * @return
	 */
	@RequestMapping({ "/luckydraw_record_view" })
	public ModelAndView collage_view(HttpServletRequest request,HttpServletResponse response,String id) {

		ModelAndView mv = new RedPigJModelAndView("admin/blue/luckydraw_record/luckydraw_record_add.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request, response);

		if ((id != null) && (!id.equals(""))) {
			Luckydraw luckydraw = this.luckydrawService.selectByPrimaryKey(CommUtil.null2Long(id));
			mv.addObject("obj", luckydraw);
			mv.addObject("view", Boolean.valueOf(true));
		}
		return mv;
	}

	/**
	 *
	 * goods_class_del:抽奖活动批量删除. <br/>
	 *
	 * @author redpig
	 * @param request
	 * @param mulitId
	 * @param currentPage
	 * @return
	 * @since JDK 1.8
	 */
	@RequestMapping({ "/luckydraw_record_del" })
	public String collage_del(HttpServletRequest request, String mulitId,String currentPage) {
		String[] ids = mulitId.split(",");

		List<Long> idList = new ArrayList<>();
		for (String id:ids){
			Long collageId = Long.parseLong(id);
			idList.add(collageId);

			/*Goods goods = this.collageBuyService.selectByPrimaryKey(collageId).getGoods();
			goods.setCollage(null);
			goods.setCollage_buy(0);
			this.goodsService.updateById(goods);*/
		}
		this.luckydrawService.batchDeleteByIds(idList);
		return "redirect:luckydraw_list?currentPage=" + currentPage;
	}
}
