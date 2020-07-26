package com.redpigmall.manage.admin.action.operative.limit_selling;

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

import com.redpigmall.api.annotation.SecurityMapping;
import com.redpigmall.api.mv.RedPigJModelAndView;
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.api.tools.CommUtil;
import com.redpigmall.api.tools.WebForm;
import com.redpigmall.manage.admin.action.base.BaseAction;
import com.redpigmall.domain.GoodsBrand;
import com.redpigmall.domain.GoodsClass;
import com.redpigmall.domain.LimitSelling;

/**
 * 
 * <p>
 * Title: RedPigLimitSellingOperativeManageAction.java
 * </p>
 * 
 * <p>
 * Description: 秒杀活动运营管理
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
public class RedPigLimitSellingOperativeManageAction extends BaseAction{
	/**
	 * 
	 * list_selling_list:秒杀运营管理. <br/>
	 *
	 * @author redpig
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @return
	 * @since JDK 1.8
	 */
	@SecurityMapping(title = "秒杀运营管理", value = "/limit_selling_list*", rtype = "admin", rname = "秒杀运营管理", rcode = "limit_selling_admin", rgroup = "运营")
	@RequestMapping({ "/limit_selling_list" })
	public ModelAndView limit_selling_list(
			HttpServletRequest request,
			HttpServletResponse response,
			String currentPage,
			String orderBy,
			String orderType) {
		ModelAndView mv = new RedPigJModelAndView("admin/blue/limit_selling_list.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request,
				response);
		
		String url = this.redPigSysConfigService.getSysConfig().getAddress();
		
		if ((url == null) || (url.equals(""))) {
			url = CommUtil.getURL(request);
		}
		
		String params = "";
		
		Map<String,Object> maps = this.redPigQueryTools.getParams(currentPage, orderBy, orderType);
		
		IPageList pList = this.limitSellingService.list(maps);
		CommUtil.saveIPageList2ModelAndView(url + "/limit_selling_list.html", "",params, pList, mv);
		return mv;
	}
	
	/**
	 * 
	 * limit_selling_add:秒杀增加. <br/>
	 *
	 * @author redpig
	 * @param request
	 * @param response
	 * @param currentPage
	 * @return
	 * @since JDK 1.8
	 */
	@SecurityMapping(title = "秒杀增加", value = "/limit_selling_add*", rtype = "admin", rname = "秒杀管理", rcode = "limit_selling_admin", rgroup = "运营")
	@RequestMapping({ "/limit_selling_add" })
	public ModelAndView limit_selling_add(
			HttpServletRequest request,
			HttpServletResponse response, 
			String currentPage) {
		
		ModelAndView mv = new RedPigJModelAndView("admin/blue/limit_selling_add.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request,
				response);
		
		return mv;
	}
	
	/**
	 * limit_selling_save:秒杀保存 <br/>
	 * 
	 * @author redpig
	 * @param request
	 * @param response
	 * @param id
	 * @param currentPage
	 * @param cmd
	 * @param begin_hour
	 * @param end_hour
	 * @param join_hour
	 * @return
	 * @since JDK 1.8
	 */
	@SuppressWarnings("deprecation")
	@SecurityMapping(title = "秒杀保存", value = "/limit_selling_save*", rtype = "admin", rname = "秒杀管理", rcode = "limit_selling_admin", rgroup = "运营")
	@RequestMapping({ "/limit_selling_save" })
	public ModelAndView limit_selling_save(
			HttpServletRequest request,
			HttpServletResponse response, 
			String id, 
			String currentPage,
			String cmd, 
			String begin_hour, 
			String end_hour, 
			String join_hour) {
		
		LimitSelling limitSelling = null;
		if (id.equals("")) {
			limitSelling = (LimitSelling) WebForm.toPo(request, LimitSelling.class);
			limitSelling.setAddTime(new Date());
		} else {
			LimitSelling obj = this.limitSellingService.selectByPrimaryKey(Long.valueOf(Long.parseLong(id)));
			limitSelling = (LimitSelling) WebForm.toPo(request, obj);
		}
		Date beginTime = limitSelling.getBeginTime();
		beginTime.setHours(CommUtil.null2Int(begin_hour));
		limitSelling.setBeginTime(beginTime);
		Date endTime = limitSelling.getEndTime();
		endTime.setHours(CommUtil.null2Int(end_hour));
		limitSelling.setEndTime(endTime);
		limitSelling.setLimit_status(1);
		if (id.equals("")) {
			this.limitSellingService.saveEntity(limitSelling);
		} else {
			this.limitSellingService.updateById(limitSelling);
		}
		ModelAndView mv = new RedPigJModelAndView("admin/blue/success.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request,
				response);
		mv.addObject("list_url", CommUtil.getURL(request) + "/limit_selling_list");
		mv.addObject("op_title", "保存秒杀成功");
		mv.addObject("add_url", CommUtil.getURL(request) + "/limit_selling_add" + "?currentPage=" + currentPage);
		return mv;
	}
	
	/**
	 * 
	 * ajax_update:update. <br/>
	 *
	 * @author redpig
	 * @param request
	 * @param response
	 * @param id
	 * @since JDK 1.8
	 */
	@RequestMapping({ "/limit_selling_ajax" })
	public void limit_selling_ajax(HttpServletRequest request,
			HttpServletResponse response, String id,String fieldName,String value)  {
		LimitSelling obj = this.limitSellingService.selectByPrimaryKey(Long.valueOf(Long.parseLong(id)));
		boolean data = true;
		
		int status = obj.getLimit_status();
		
		if(1 == status) {
			obj.setLimit_status(0);
			data = false;
		}else {
			obj.setLimit_status(1);
			data = true;
		}
		
		this.limitSellingService.updateById(obj);
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		try {
			PrintWriter writer = response.getWriter();
			writer.print(data);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 
	 * limit_selling_goods_list:秒杀商品管理. <br/>
	 *
	 * @author redpig
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @param class_id
	 * @param brand_id
	 * @param goods_name
	 * @return
	 * @since JDK 1.8
	 */
	@SecurityMapping(title = "秒杀商品", value = "/limit_selling_goods_list*", rtype = "admin", rname = "秒杀商品", rcode = "limit_selling_goods_list", rgroup = "秒杀管理")
	@RequestMapping({ "/limit_selling_goods_list" })
	public ModelAndView limit_selling_goods_list(
			HttpServletRequest request, HttpServletResponse response,
			 String currentPage, String orderBy, String orderType,
			String class_id, String brand_id, String goods_name,String limitSelling_id) {
		ModelAndView mv = new RedPigJModelAndView("admin/blue/limit_selling_goods_list.html",
				this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request, response);
		
		Map<String, Object> params = this.redPigQueryTools.getParams(currentPage, orderBy, orderType);
		
		params.put("goods_type", 0);
		
		params.put("limitSelling_id", limitSelling_id);
		
		if ((goods_name != null) && (!goods_name.equals(""))) {
			
			params.put("goods_name_like", goods_name);
			
			mv.addObject("goods_name", goods_name);
			
		}
		
		// 分类查询
		if ((class_id != null) && (!class_id.equals(""))) {
			params.put("rgc_id", CommUtil.null2Long(class_id));
			params.put("rgc_parent_id", CommUtil.null2Long(class_id));
			params.put("rgc_parent_parent_id", CommUtil.null2Long(class_id));
			
			mv.addObject("class_id", class_id);
			
		}
		if ((brand_id != null) && (!brand_id.equals(""))) {

			params.put("goods_brand_id", CommUtil.null2Long(brand_id));

			mv.addObject("brand_id", brand_id);

		}
		
		IPageList pList = this.goodsService.list(params);
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		params.clear();
		params.put("orderBy", "sequence");
		params.put("orderType", "asc");

		List<GoodsBrand> gbs = this.goodsBrandService.queryPageList(params);
		params.clear();

		params.put("parent", -1);

		List<GoodsClass> gcs = this.goodsClassService.queryPageListWithNoRelations(params);

		mv.addObject("gcs", gcs);
		mv.addObject("gbs", gbs);
		return mv;
	}
	
	
}
