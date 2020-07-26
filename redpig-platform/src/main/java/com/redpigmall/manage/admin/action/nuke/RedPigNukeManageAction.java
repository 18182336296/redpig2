package com.redpigmall.manage.admin.action.nuke;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.redpigmall.api.tools.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.google.common.collect.Maps;
import com.redpigmall.api.annotation.SecurityMapping;
import com.redpigmall.api.mv.RedPigJModelAndView;
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.api.tools.CommUtil;
import com.redpigmall.api.tools.ObjectUtils;
import com.redpigmall.api.tools.WebForm;
import com.redpigmall.domain.Nuke;
import com.redpigmall.manage.admin.action.base.BaseAction;

/**
 * 
 * <p>
 * Title: RedPigLimitSellingManageAction.java
 * </p>
 * 
 * <p>
 * Description: 秒杀活动管理
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
 * @date 2018年4月17日
 * 
 * @version redpigmall_b2b2c 8.0
 */
@Controller
public class RedPigNukeManageAction extends BaseAction{

	/**
	 * 
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @return
	 */
	@RequestMapping({ "/nuke" })
	public ModelAndView nuke(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,
			String orderType) {
		ModelAndView mv = new RedPigJModelAndView("admin/blue/nuke/nuke_list.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request,
				response);
		
		String url = this.redPigSysConfigService.getSysConfig().getAddress();
		
		if ((url == null) || (url.equals(""))) {
			url = CommUtil.getURL(request);
		}
		
		String params = "";
		
		Map<String,Object> maps = this.redPigQueryTools.getParams(currentPage, orderBy, orderType);
		
		IPageList pList = this.nukeService.list(maps);
		CommUtil.saveIPageList2ModelAndView(url + "/nuke/nuke_list.html", "",params, pList, mv);
		return mv;
	}
	
	/**
	 * 秒杀增加
	 * @param request
	 * @param response
	 * @param currentPage
	 * @return
	 */
	@RequestMapping({ "/nuke_add" })
	public ModelAndView nuke_add(
			HttpServletRequest request,
			HttpServletResponse response, 
			String currentPage) {
		
		ModelAndView mv = new RedPigJModelAndView("admin/blue/nuke/nuke_add.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request,
				response);
		Nuke goods_last = null;
		Nuke life_last = null;
		Map<String, Object> params = Maps.newHashMap();
		params.put("nuke_type", Integer.valueOf(0));
		params.put("status1", Integer.valueOf(-1));
		params.put("status2", Integer.valueOf(-2));
		
		List<Nuke> goods_Nuke_lasts = this.nukeService.queryPageList(params,0,1);
		
		params.clear();
		params.put("nuke_type", Integer.valueOf(1));
		params.put("status1", Integer.valueOf(-1));
		params.put("status2", Integer.valueOf(-2));
		List<Nuke> life_Nuke_lasts = this.nukeService.queryPageList(params,0,1);
		
		if (goods_Nuke_lasts.size() > 0) {
			goods_last = (Nuke) goods_Nuke_lasts.get(0);
			mv.addObject("goods_last_time",CommUtil.formatShortDate(goods_last.getEndTime()));
		}
		if (life_Nuke_lasts.size() > 0) {
			life_last = (Nuke) life_Nuke_lasts.get(0);
			mv.addObject("life_last_time",CommUtil.formatShortDate(life_last.getEndTime()));
		}
		mv.addObject("currentPage", currentPage);
		return mv;
	}
	
	
	/**
	 * 秒杀保存
	 * @param request
	 * @param response
	 * @param id
	 * @param currentPage
	 * @param cmd
	 * @return
	 */
	@SuppressWarnings("deprecation")
	@RequestMapping({ "/nuke_save" })
	public ModelAndView nuke_save(
			HttpServletRequest request,HttpServletResponse response, 
			String id, String currentPage,String cmd, String beginTime,String endTime,
			String remark,String timeout) {
		
		Nuke nuke = null;
		if (id.equals("")) {
			nuke = (Nuke) WebForm.toPo(request, Nuke.class);
			nuke.setAddTime(new Date());
		} else {
			Nuke obj = this.nukeService.selectByPrimaryKey(Long.valueOf(Long.parseLong(id)));
			nuke = (Nuke) WebForm.toPo(request, obj);
		}
		nuke.setBeginTime(Timestamp.valueOf(beginTime));
		nuke.setEndTime(Timestamp.valueOf(endTime));
		if (StringUtils.isNotBlank(remark)){
			nuke.setRemark(remark);
		}
		nuke.setTimeout(CommUtil.null2Int(timeout));

		DateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		Date currentDate = new Date();
		if (nuke.getBeginTime().getTime()>=currentDate.getTime()){
			nuke.setStatus(1);
		}else{
			nuke.setStatus(0);
		}

		if (id.equals("")) {
			this.nukeService.saveEntity(nuke);
		} else {
			this.nukeService.updateById(nuke);
		}

		ModelAndView mv = new RedPigJModelAndView("admin/blue/success.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request,
				response);
		
		mv.addObject("list_url", CommUtil.getURL(request) + "/nuke_list");
		mv.addObject("op_title", "保存秒杀成功");
		mv.addObject("add_url", CommUtil.getURL(request) + "/nuke_add" + "?currentPage=" + currentPage);
		return mv;
	}
	
	/**
	 * 秒杀管理的列表
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @return
	 */
	@RequestMapping({ "/nuke_list" })
	public ModelAndView nuke_list(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,
			String orderType) {
		ModelAndView mv = new RedPigJModelAndView("admin/blue/nuke/nuke_list.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request,
				response);

		String url = this.redPigSysConfigService.getSysConfig().getAddress();
		if ((url == null) || (url.equals(""))) {
			url = CommUtil.getURL(request);
		}
		String params = "";
		
		Map<String,Object> maps = this.redPigQueryTools.getParams(currentPage, orderBy, orderType);
		
		IPageList pList = this.nukeService.list(maps);
		CommUtil.saveIPageList2ModelAndView(url + "/nuke/nuke_list.html", "",params, pList, mv);
		return mv;
	}

	/**
	 * 
	 * goods_class_ajax:秒杀活动. <br/>
	 *
	 * @author redpig
	 * @param request
	 * @param response
	 * @param id
	 * @param fieldName
	 * @param value
	 * @throws ClassNotFoundException
	 * @since JDK 1.8
	 */
	@SecurityMapping(title = "秒杀活动Ajax更新", value = "/goods_class_ajax*", rtype = "admin", rname = "分类管理", rcode = "goods_class", rgroup = "商品")
	@RequestMapping({ "/nuke_ajax" })
	public void nuke_ajax(HttpServletRequest request,
			HttpServletResponse response, String id, String fieldName,
			String value) throws ClassNotFoundException {
		Nuke gc = this.nukeService.selectByPrimaryKey(Long.parseLong(id));
		
		Field[] fields = Nuke.class.getDeclaredFields();
		
		Object val = ObjectUtils.setValue(fieldName, value, gc, fields);
		
		this.nukeService.updateById(gc);
		
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
	 * 
	 * nuke_edit:秒杀编辑. <br/>
	 *
	 * @author redpig
	 * @param request
	 * @param response
	 * @param id
	 * @return
	 * @since JDK 1.8
	 */
	@RequestMapping({ "/nuke_edit" })
	public ModelAndView nuke_edit(HttpServletRequest request,HttpServletResponse response,String id) {
	
		ModelAndView mv = new RedPigJModelAndView("admin/blue/nuke/nuke_add.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request, response);

		if ((id != null) && (!id.equals(""))) {
			Nuke nuke = this.nukeService.selectByPrimaryKey(CommUtil.null2Long(id));
			mv.addObject("obj", nuke);
			mv.addObject("edit", Boolean.valueOf(true));
		}
		return mv;
	}
	
	/**
	 * 
	 * goods_class_del:秒杀活动批量删除. <br/>
	 *
	 * @author redpig
	 * @param request
	 * @param mulitId
	 * @param currentPage
	 * @return
	 * @since JDK 1.8
	 */
	@RequestMapping({ "/nuke_del" })
	public String nuke_del(HttpServletRequest request, String mulitId,String currentPage) {
		String[] ids = mulitId.split(",");
		
		this.nukeService.batchDeleteByIds(ids);
		
		return "redirect:nuke_list?currentPage=" + currentPage;
	}
	
	/**
	 * 
	 * nuke_close:秒杀活动批量删除. <br/>
	 * 
	 * @author redpig
	 * @param request
	 * @param mulitId
	 * @param currentPage
	 * @return
	 * @since JDK 1.8
	 */
	@RequestMapping({ "/update_status" })
	public String update_status(HttpServletRequest request, String mulitId,
			String currentPage) {
		String[] ids = mulitId.split(",");
		
		
		for (String id : ids) {
			
			Nuke nuke = this.nukeService.selectByPrimaryKey(CommUtil.null2Long(id));
			if(nuke.getStatus()==0){
				nuke.setStatus(-1);
			}else{
				nuke.setStatus(0);
			}
			
			this.nukeService.updateById(nuke);
			
			
		}
		
		
		return "redirect:nuke_list?currentPage=" + currentPage;
	}


    /**
     * 验证时间是否合理
     * @param request
     * @param time
     * @return
     */
	@RequestMapping({"/verify_time"})
	@ResponseBody
	public String verify_time(HttpServletRequest request, String time,String id){

	    String flag = "true";
	    if (StringUtils.isBlank(time)) return "false";

        //验证是否在已有活动的区间
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        List<Nuke> nukeList = this.nukeService.queryPageListWithNoRelations(null);

        if (nukeList!=null&&!nukeList.isEmpty()){
            //如果是修改，则要把自己的秒杀活动区间排除
            Nuke nuke = new Nuke();
            if (StringUtils.isNotBlank(id)){
                nuke = this.nukeService.selectByPrimaryKey(CommUtil.null2Long(id));
            }
            for (Nuke tempNuke:nukeList){
                if (nuke!=null && nuke.getId()==tempNuke.getId()) continue;
                try {
                    Date date = sf.parse(time);
                    //如果是修改，则要把自己的秒杀活动区间排除
                    if (date.compareTo(tempNuke.getBeginTime())>0&&date.compareTo(tempNuke.getEndTime())<0){
                        flag = "false";
                        break;
                    }
                }catch (ParseException e){
                    e.printStackTrace();
                }
            }
        }
        return flag;
	}
	
}
