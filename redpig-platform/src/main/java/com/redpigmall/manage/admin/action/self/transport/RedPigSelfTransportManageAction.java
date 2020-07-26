package com.redpigmall.manage.admin.action.self.transport;

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
import com.redpigmall.api.tools.CommUtil;
import com.redpigmall.api.tools.RedPigMaps;
import com.redpigmall.api.tools.StringUtils;
import com.redpigmall.api.tools.WebForm;
import com.redpigmall.manage.admin.action.base.BaseAction;
import com.redpigmall.domain.Goods;
import com.redpigmall.domain.TransArea;
import com.redpigmall.domain.Transport;

/**
 * 
 * <p>
 * Title: RedPigSelfTransportManageAction.java
 * </p>
 * 
 * <p>
 * Description: 商城自营商品运费模板管理控制器，用来添加、复制、编辑管理运费模板信息
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
 * @author jingxinzhe
 * 
 * @date 2014-5-9
 * 
 * @version redpigmall_b2b2c v8.0 2016版 
 */
@Controller
public class RedPigSelfTransportManageAction extends BaseAction {
	/**
	 * 运费模板列表
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @return
	 */
	@SecurityMapping(title = "运费模板列表", value = "/transport_list*", rtype = "admin", rname = "运费模板", rcode = "transport_self", rgroup = "自营")
	@RequestMapping({ "/transport_list" })
	public ModelAndView transport_list(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,
			String orderType) {
		ModelAndView mv = new RedPigJModelAndView("admin/blue/transport_list.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		String url = this.configService.getSysConfig().getAddress();
		if ((url == null) || (url.equals(""))) {
			url = CommUtil.getURL(request);
		}
		String params = "";
		
		Map<String,Object> maps = this.redPigQueryTools.getParams(currentPage, orderBy, orderType);
		
		maps.put("trans_user", 0);
		
		maps.put("pageSize", 3);
		IPageList pList = this.transportService.list(maps);
		CommUtil.saveIPageList2ModelAndView("", "", params, pList, mv);
		mv.addObject("transportTools", this.transportTools);
		return mv;
	}
	
	/**
	 * 运费模板添加
	 * @param request
	 * @param response
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "运费模板添加", value = "/transport_add*", rtype = "admin", rname = "运费模板", rcode = "transport_self", rgroup = "自营")
	@RequestMapping({ "/transport_add" })
	public ModelAndView transport_add(HttpServletRequest request,
			HttpServletResponse response, String currentPage) {
		ModelAndView mv = new RedPigJModelAndView("admin/blue/transport_add.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		mv.addObject("currentPage", currentPage);
		return mv;
	}
	
	/**
	 * 运费模板编辑
	 * @param request
	 * @param response
	 * @param id
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "运费模板编辑", value = "/transport_edit*", rtype = "admin", rname = "运费模板", rcode = "transport_self", rgroup = "自营")
	@RequestMapping({ "/transport_edit" })
	public ModelAndView transport_edit(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage) {
		ModelAndView mv = new RedPigJModelAndView("admin/blue/transport_add.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		if ((id != null) && (!id.equals(""))) {
			Transport transport = this.transportService.selectByPrimaryKey(Long
					.valueOf(Long.parseLong(id)));
			mv.addObject("obj", transport);
			mv.addObject("currentPage", currentPage);
		}
		mv.addObject("transportTools", this.transportTools);
		return mv;
	}
	
	/**
	 * 运费模板复制
	 * @param request
	 * @param response
	 * @param id
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "运费模板复制", value = "/transport_copy*", rtype = "admin", rname = "运费模板", rcode = "transport_self", rgroup = "自营")
	@RequestMapping({ "/transport_copy" })
	public ModelAndView transport_copy(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage) {
		ModelAndView mv = new RedPigJModelAndView("admin/blue/transport_add.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		if ((id != null) && (!id.equals(""))) {
			Transport transport = this.transportService.selectByPrimaryKey(Long
					.valueOf(Long.parseLong(id)));
			Transport obj = new Transport();
			obj.setStore(transport.getStore());
			obj.setTrans_ems(transport.getTrans_ems());
			obj.setTrans_ems_info(transport.getTrans_ems_info());
			obj.setTrans_express(transport.getTrans_express());
			obj.setTrans_express_info(transport.getTrans_express_info());
			obj.setTrans_mail(transport.getTrans_mail());
			obj.setTrans_mail_info(transport.getTrans_mail_info());
			obj.setTrans_name(transport.getTrans_name());
			mv.addObject("obj", obj);
			mv.addObject("currentPage", currentPage);
		}
		mv.addObject("transportTools", this.transportTools);
		return mv;
	}
	
	/**
	 * 运费模板保存
	 * @param request
	 * @param response
	 * @param id
	 * @param currentPage
	 * @param trans_mail
	 * @param trans_express
	 * @param trans_ems
	 * @param mail_city_count
	 * @param express_city_count
	 * @param ems_city_count
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@SecurityMapping(title = "运费模板保存", value = "/transport_save*", rtype = "admin", rname = "运费模板", rcode = "transport_self", rgroup = "自营")
	@RequestMapping({ "/transport_save" })
	public String transport_saveEntity(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage,
			String trans_mail, String trans_express, String trans_ems,
			String mail_city_count, String express_city_count,
			String ems_city_count) {
		
		Transport transport = null;
		if (id.equals("")) {
			transport = (Transport) WebForm.toPo(request, Transport.class);
			
			transport.setAddTime(new Date());
		} else {
			Transport obj = this.transportService.selectByPrimaryKey(Long.valueOf(Long
					.parseLong(id)));
			transport = (Transport) WebForm.toPo(request, obj);
		}
		
		if(StringUtils.isNotBlank(trans_mail) && "true".equals(trans_mail)){
			transport.setTrans_mail(true);
		}else{
			transport.setTrans_mail(false);
		}
		
		if(StringUtils.isNotBlank(trans_express) && "true".equals(trans_express)){
			transport.setTrans_express(true);
		}else{
			transport.setTrans_express(false);
		}
		
		if(StringUtils.isNotBlank(trans_ems) && "true".equals(trans_ems)){
			transport.setTrans_ems(true);
		}else{
			transport.setTrans_ems(false);
		}
		
		if (CommUtil.null2Boolean(trans_mail)) {
			List<Map> trans_mail_info = Lists.newArrayList();
			Map<String, Object> map = Maps.newHashMap();
			map.put("city_id", "-1");
			map.put("city_name", "全国");
			map.put("trans_weight", Integer.valueOf(CommUtil.null2Int(request
					.getParameter("mail_trans_weight"))));
			map.put("trans_fee", Float.valueOf(CommUtil.null2Float(request
					.getParameter("mail_trans_fee"))));
			map.put("trans_add_weight", Integer.valueOf(CommUtil
					.null2Int(request.getParameter("mail_trans_add_weight"))));
			map.put("trans_add_fee", Float.valueOf(CommUtil.null2Float(request
					.getParameter("mail_trans_add_fee"))));
			trans_mail_info.add(map);
			for (int i = 1; i <= CommUtil.null2Int(mail_city_count); i++) {
				int trans_weight = CommUtil.null2Int(request
						.getParameter("mail_trans_weight" + i));
				String city_ids = CommUtil.null2String(request
						.getParameter("mail_city_ids" + i));
				if ((!city_ids.equals("")) && (trans_weight > 0)) {
					float trans_fee = CommUtil.null2Float(request
							.getParameter("mail_trans_fee" + i));
					int trans_add_weight = CommUtil.null2Int(request
							.getParameter("mail_trans_add_weight" + i));
					float trans_add_fee = CommUtil.null2Float(request
							.getParameter("mail_trans_add_fee" + i));
					String city_name = CommUtil.null2String(request
							.getParameter("mail_city_names" + i));
					Map map1 = Maps.newHashMap();
					map1.put("city_id", city_ids);
					map1.put("city_name", city_name);
					map1.put("trans_weight", Integer.valueOf(trans_weight));
					map1.put("trans_fee", Float.valueOf(trans_fee));
					map1.put("trans_add_weight",
							Integer.valueOf(trans_add_weight));
					map1.put("trans_add_fee", Float.valueOf(trans_add_fee));
					trans_mail_info.add(map1);
				}
			}
			transport.setTrans_mail_info(JSON.toJSONString(trans_mail_info));
		}
		if (CommUtil.null2Boolean(trans_express)) {
			List<Map> trans_express_info = Lists.newArrayList();
			Map<String, Object> map = Maps.newHashMap();
			map.put("city_id", "-1");
			map.put("city_name", "全国");
			map.put("trans_weight", Integer.valueOf(CommUtil.null2Int(request
					.getParameter("express_trans_weight"))));
			map.put("trans_fee", Float.valueOf(CommUtil.null2Float(request
					.getParameter("express_trans_fee"))));
			map.put("trans_add_weight",
					Integer.valueOf(CommUtil.null2Int(request
							.getParameter("express_trans_add_weight"))));
			map.put("trans_add_fee", Float.valueOf(CommUtil.null2Float(request
					.getParameter("express_trans_add_fee"))));
			trans_express_info.add(map);
			for (int i = 1; i <= CommUtil.null2Int(express_city_count); i++) {
				int trans_weight = CommUtil.null2Int(request
						.getParameter("express_trans_weight" + i));
				String city_ids = CommUtil.null2String(request
						.getParameter("express_city_ids" + i));
				if ((!city_ids.equals("")) && (trans_weight > 0)) {
					float trans_fee = CommUtil.null2Float(request
							.getParameter("express_trans_fee" + i));
					int trans_add_weight = CommUtil.null2Int(request
							.getParameter("express_trans_add_weight" + i));
					float trans_add_fee = CommUtil.null2Float(request
							.getParameter("express_trans_add_fee" + i));
					String city_name = CommUtil.null2String(request
							.getParameter("express_city_names" + i));
					Map map1 = Maps.newHashMap();
					map1.put("city_id", city_ids);
					map1.put("city_name", city_name);
					map1.put("trans_weight", Integer.valueOf(trans_weight));
					map1.put("trans_fee", Float.valueOf(trans_fee));
					map1.put("trans_add_weight",
							Integer.valueOf(trans_add_weight));
					map1.put("trans_add_fee", Float.valueOf(trans_add_fee));
					trans_express_info.add(map1);
				}
			}
			transport.setTrans_express_info(JSON
					.toJSONString(trans_express_info));
		}
		if (CommUtil.null2Boolean(trans_ems)) {
			List<Map> trans_ems_info = Lists.newArrayList();
			Map<String, Object> map = Maps.newHashMap();
			map.put("city_id", "-1");
			map.put("city_name", "全国");
			map.put("trans_weight", Integer.valueOf(CommUtil.null2Int(request
					.getParameter("ems_trans_weight"))));
			map.put("trans_fee", Float.valueOf(CommUtil.null2Float(request
					.getParameter("ems_trans_fee"))));
			map.put("trans_add_weight", Integer.valueOf(CommUtil
					.null2Int(request.getParameter("ems_trans_add_weight"))));
			map.put("trans_add_fee", Float.valueOf(CommUtil.null2Float(request
					.getParameter("ems_trans_add_fee"))));
			trans_ems_info.add(map);
			for (int i = 1; i <= CommUtil.null2Int(ems_city_count); i++) {
				int trans_weight = CommUtil.null2Int(request
						.getParameter("ems_trans_weight" + i));
				String city_ids = CommUtil.null2String(request
						.getParameter("ems_city_ids" + i));
				if ((!city_ids.equals("")) && (trans_weight > 0)) {
					float trans_fee = CommUtil.null2Float(request
							.getParameter("ems_trans_fee" + i));
					int trans_add_weight = CommUtil.null2Int(request
							.getParameter("ems_trans_add_weight" + i));
					float trans_add_fee = CommUtil.null2Float(request
							.getParameter("ems_trans_add_fee" + i));
					String city_name = CommUtil.null2String(request
							.getParameter("ems_city_names" + i));
					Map map1 = Maps.newHashMap();
					map1.put("city_id", city_ids);
					map1.put("city_name", city_name);
					map1.put("trans_weight", Integer.valueOf(trans_weight));
					map1.put("trans_fee", Float.valueOf(trans_fee));
					map1.put("trans_add_weight",
							Integer.valueOf(trans_add_weight));
					map1.put("trans_add_fee", Float.valueOf(trans_add_fee));
					trans_ems_info.add(map1);
				}
			}
			transport.setTrans_ems_info(JSON.toJSONString(trans_ems_info));
		}
		transport.setAddTime(new Date());
		if (id.equals("")) {
			this.transportService.saveEntity(transport);
		} else {
			this.transportService.updateById(transport);
		}
		return "redirect:transport_success?currentPage=" + currentPage;
	}
	
	/**
	 * 运费模板保存成功
	 * @param request
	 * @param response
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "运费模板保存成功", value = "/transport_success*", rtype = "admin", rname = "运费模板", rcode = "transport_self", rgroup = "自营")
	@RequestMapping({ "/transport_success" })
	public ModelAndView transport_success(HttpServletRequest request,
			HttpServletResponse response, String currentPage) {
		ModelAndView mv = new RedPigJModelAndView("admin/blue/success.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		mv.addObject("op_title", "运费模板保存成功");
		mv.addObject("list_url", CommUtil.getURL(request)
				+ "/transport_list?currentPage=" + currentPage);
		return mv;
	}
	
	/**
	 * 运费模板删除
	 * @param request
	 * @param response
	 * @param mulitId
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "运费模板删除", value = "/transport_del*", rtype = "admin", rname = "运费模板", rcode = "transport_self", rgroup = "自营")
	@RequestMapping({ "/transport_del" })
	public ModelAndView transport_del(HttpServletRequest request,
			HttpServletResponse response, String mulitId, String currentPage) {
		ModelAndView mv = new RedPigJModelAndView("admin/blue/transport_list.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		Map<String, Object> params = Maps.newHashMap();
		params.put("transport_id", CommUtil.null2Long(mulitId));
		
		List<Goods> goods_list = this.goodsService.queryPageList(params);
		
		
		if (goods_list.size() == 0) {
			this.transportService.deleteById(Long.valueOf(Long.parseLong(mulitId)));
			
			Map<String,Object> maps = this.redPigQueryTools.getParams(currentPage, null, null);
			
			maps.put("trans_user", 0);
			
			maps.put("pageSize", 3);
			IPageList pList = this.transportService.list(maps);
			CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
			mv.addObject("transportTools", this.transportTools);
		} else {
			mv = new RedPigJModelAndView("admin/blue/tip.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request,
					response);
			mv.addObject("op_tip", "该模板正在被使用，不可删除");
			mv.addObject("list_url", CommUtil.getURL(request)
					+ "/transport_list");
		}
		return mv;
	}
	
	/**
	 * 运费模板详细信息
	 * @param request
	 * @param response
	 * @param type
	 * @param id
	 * @return
	 */
	@SecurityMapping(title = "运费模板详细信息", value = "/transport_info*", rtype = "admin", rname = "运费模板", rcode = "transport_self", rgroup = "自营")
	@RequestMapping({ "/transport_info" })
	public ModelAndView transport_info(HttpServletRequest request,
			HttpServletResponse response, String type, String id) {
		if ((type == null) || (type.equals(""))) {
			type = CommUtil.null2String(request.getAttribute("type"));
		}
		if ((id == null) || (id.equals(""))) {
			id = CommUtil.null2String(request.getAttribute("id"));
		}
		if (CommUtil.null2String(type).equals("")) {
			type = "mail";
		}
		ModelAndView mv = new RedPigJModelAndView("admin/blue/transport_" + type
				+ ".html", this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		if ((id != null) && (!id.equals(""))) {
			Transport transport = this.transportService.selectByPrimaryKey(Long
					.valueOf(Long.parseLong(id)));
			mv.addObject("obj", transport);
			mv.addObject("transportTools", this.transportTools);
		}
		return mv;
	}
	
	/**
	 * "运费模板区域编辑
	 * @param request
	 * @param response
	 * @param id
	 * @param trans_city_type
	 * @param trans_index
	 * @return
	 */
	@SecurityMapping(title = "运费模板区域编辑", value = "/transport_area*", rtype = "admin", rname = "运费模板", rcode = "transport_self", rgroup = "自营")
	@RequestMapping({ "/transport_area" })
	public ModelAndView transport_area(HttpServletRequest request,
			HttpServletResponse response, String id, String trans_city_type,
			String trans_index) {
		ModelAndView mv = new RedPigJModelAndView("admin/blue/transport_area.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		
		List<TransArea> objs = this.transAreaService.queryPageList(RedPigMaps.newParent(-1));
		
		mv.addObject("objs", objs);
		mv.addObject("trans_city_type", trans_city_type);
		mv.addObject("trans_index", trans_index);
		return mv;
	}
}
