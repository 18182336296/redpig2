package com.redpigmall.manage.admin.action.collage;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.redpigmall.api.annotation.SecurityMapping;
import com.redpigmall.api.mv.RedPigJModelAndView;
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.api.sec.SecurityUserHolder;
import com.redpigmall.api.tools.*;
import com.redpigmall.domain.*;
import com.redpigmall.manage.admin.action.base.BaseAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 
 * <p>
 * Title: RedPigLimitSellingManageAction.java
 * </p>
 * 
 * <p>
 * Description: 拼团活动管理
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
 * @date 2018年9月5日
 * 
 * @version redpigmall_b2b2c 8.0
 */
@Controller
public class RedPigCollageManageAction extends BaseAction{

	Logger logger = LoggerFactory.getLogger(RedPigCollageManageAction.class);
	/**
	 * 所有活动列表
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @return
	 */
	@RequestMapping({ "/collage_list" })
	public ModelAndView collage_list(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,String status,
			String collage_name,String cg_status,String beginTime,String endTime,String orderType) {
		ModelAndView mv = new RedPigJModelAndView("admin/blue/collage/collage_list.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request,
				response);
		
		String url = this.redPigSysConfigService.getSysConfig().getAddress();
		Map<String,Object> params = this.redPigQueryTools.getParams(currentPage, "addTime", "desc");
		if ((url == null) || (url.equals(""))) {
			url = CommUtil.getURL(request);
		}
		if (!CommUtil.null2String(collage_name).equals("")) {
			params.put("collage_name_like", collage_name);
		}
		if (!CommUtil.null2String(cg_status).equals("")) {
			params.put("cg_status", cg_status);
		}
		if (!CommUtil.null2String(beginTime).equals("")) {
			params.put("add_Time_more_than_equal", CommUtil.formatDate(beginTime));
		}
		if (!CommUtil.null2String(endTime).equals("")) {
			params.put("add_Time_less_than_equal", CommUtil.formatDate(endTime));
		}
		
		mv.addObject("status",status);
		mv.addObject("collage_name", collage_name);
		mv.addObject("cg_status", cg_status);
		mv.addObject("beginTime", beginTime);
		mv.addObject("endTime", endTime);
		mv.addObject("orderFormTools", this.redPigOrderFormTools);//用来查询付款订单数和付款人数
		IPageList pList = this.collageBuyService.list(params);
		CommUtil.saveIPageList2ModelAndView(url + "/collage/collage_list.html", "","", pList, mv);
		return mv;
	}
	
	/**
	 * 新增活动
	 * @param request
	 * @param response
	 * @param currentPage
	 * @return
	 */
	@RequestMapping({ "/collage_add" })
	public ModelAndView collage_add(
			HttpServletRequest request,
			HttpServletResponse response, 
			String currentPage) {
		
		ModelAndView mv = new RedPigJModelAndView("admin/blue/collage/collage_add.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request,
				response);
		return mv;
	}
	
	
	/**
	 * 拼团保存
	 * @param request
	 * @param response
	 * @param id
	 * @param currentPage
	 * @return
	 */
	@RequestMapping({ "/collage_save" })
	public ModelAndView collage_save(
			HttpServletRequest request,HttpServletResponse response, 
			String id, String currentPage,String beginTime,String endTime,String origin_price,
			String remark,String limit_number,String timeout,String cg_price,String goods_id,String goods_spec_id,
			String cg_total_count,String collage_person_set) {
		// 返回页面
		ModelAndView mv = new RedPigJModelAndView("admin/blue/success.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request,
				response);//系统配置管理类,包括系统基础信息redPigSysConfigService.getSysConfig()
		//用户类，所有用户均使用该类进行管理，包括普通用户、管理员、商家等redPigUserConfigService.getUserConfig()
		//根据已登录的用户查询用户信息
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder.getCurrentUser().getId());
		// 如果为卖家子账号，则该属性不为空，通过该属性获取卖家子账号对应的店铺信息
		user = user.getParent() == null ? user : user.getParent();

		// 创建实体对象
		CollageBuy collageBuy = null;
		if (CommUtil.null2String(id).equals("")) {
			//WebForm.toPo表单对象和POJO转换类，该类可以将表单对象转换为POJO，也可以将查询表单转为查询对象QueryObject 
			collageBuy = (CollageBuy) WebForm.toPo(request, CollageBuy.class);
			collageBuy.setAddTime(new Date());
		} else {
			//拼团实体类  //Long.valueOf是将参数转换成long的包装类  //方法解析的字符串参数s作为一个符号的十进制长。 
			//
			CollageBuy obj = this.collageBuyService.selectByPrimaryKey(Long.valueOf(Long.parseLong(id)));
			//将请求参数赋值到对象上
			collageBuy = (CollageBuy) WebForm.toPo(request, obj);
		}
		collageBuy.setBeginTime(Timestamp.valueOf(beginTime));
		collageBuy.setEndTime(Timestamp.valueOf(endTime));
		if (StringUtils.isNotBlank(remark)){
			collageBuy.setRemark(remark);
		}
		collageBuy.setLimit_number(CommUtil.null2Int(limit_number));////限购份数
		collageBuy.setTimeout(CommUtil.null2Int(timeout));

		// 保存商品拼团信息
		collageBuy.setCg_price(new BigDecimal(cg_price));// 拼团价
		collageBuy.setCg_total_count(Integer.valueOf(cg_total_count));//拼团数量
		collageBuy.setCollage_person_set(Integer.valueOf(collage_person_set));//至少多少人拼
		collageBuy.setLimit_number(Integer.valueOf(limit_number));//限购数量
		collageBuy.setTimeout(Integer.valueOf(timeout));//超市订单取消
		collageBuy.setRemark(remark);//备注
		collageBuy.setOrigin_price(new BigDecimal(origin_price));//原价保存
		collageBuy.setGoods_spec_id(goods_spec_id);//商品规格

		// 保存商品信息
		Goods goods = this.goodsService.selectByPrimaryKey(CommUtil.null2Long(goods_id));
		goods.setCollage(collageBuy);
		collageBuy.setGoods(goods);

		// 判断是否到了拼团时间，如果到了，修改状态，前端显示相应规格时，直接从拼团活动表中加载库存和价格
		if ((new Date()).before(collageBuy.getBeginTime())) {
			//未开始
			collageBuy.setCg_status(1);
			goods.setCollage_buy(4);
		} else if((new Date()).after(collageBuy.getBeginTime())&&(new Date()).before(collageBuy.getEndTime())){
			//正在进行中
			collageBuy.setCg_status(2);//设置秒杀商品的状态为开始
			goods.setCollage_buy(2);
		}else if((new Date()).after(collageBuy.getEndTime())){
			//已过期
			collageBuy.setCg_status(-2);
			goods.setCollage_buy(-2);
		}

		if (CommUtil.null2String(id).equals("")) {
			logger.info("新增拼团活动:"+collageBuy.getCg_name());
			this.collageBuyService.saveEntity(collageBuy);
		} else {
			logger.info("修改拼团活动:"+collageBuy.getCg_name());
			this.collageBuyService.updateById(collageBuy);
		}

		this.goodsService.updateById(goods);

		mv.addObject("list_url", CommUtil.getURL(request) + "/collage_list");
		mv.addObject("op_title", "保存拼团成功");
		mv.addObject("add_url", CommUtil.getURL(request) + "/collage_add" + "?currentPage=" + currentPage);
		return mv;
	}

	/**
	 * 
	 * nuke_edit:拼团编辑数据回显. <br/>
	 *
	 * @author redpig
	 * @param request
	 * @param response
	 * @param id
	 * @return
	 * @since JDK 1.8
	 */
	@RequestMapping({ "/collage_edit" })
	public ModelAndView collage_edit(HttpServletRequest request,HttpServletResponse response,String id) {
	
		ModelAndView mv = new RedPigJModelAndView("admin/blue/collage/collage_add.html",
				this.redPigSysConfigService.getSysConfig(),// 系统配置管理类,包括系统基础信息、系统邮箱发送配置、手机短息发送配置、殴飞充值接口配置等所有系统基础相关内容
				this.redPigUserConfigService.getUserConfig(), 0, request, response);//得到用户信息

		if ((id != null) && (!id.equals(""))) {//如果商品ID不为空
			CollageBuy collageBuy = this.collageBuyService.selectByPrimaryKey(CommUtil.null2Long(id));//拼团实体类根据拼团商品id到拼团活动表里查出当前拼团x
			mv.addObject("obj", collageBuy);
			mv.addObject("edit", Boolean.valueOf(true));

			Goods goods = collageBuy.getGoods();
			// 如果是统一规格，加载现有的统一库存
			if (StringUtils.isNotBlank(collageBuy.getGoods_spec_id())){
				if (goods.getInventory_type().equals("all")){
					mv.addObject("goods_cur_inventory", goods.getGoods_inventory());
				}else if(goods.getInventory_type().equals("spec")){
					//如果分规格，遍历商品规格表，找出goods_spec_id相同的库存
					List<HashMap> list = JSON.parseArray(CommUtil.null2String(goods.getGoods_inventory_detail()),HashMap.class);
					for (Map temp : list) {
						String tempId = temp.get("id").toString();
						if (tempId.equals(collageBuy.getGoods_spec_id().trim())){
							// 加载该规格下的库存
							mv.addObject("goods_cur_inventory", temp.get("count").toString());
							break;
						}
					}
				}
			}
		}
		return mv;
	}

	/**
	 * 查看拼团信息
	 * @param request
	 * @param response
	 * @param id
	 * @return
	 */
	@RequestMapping({ "/collage_view" })
	public ModelAndView collage_view(HttpServletRequest request,HttpServletResponse response,String id) {

		ModelAndView mv = new RedPigJModelAndView("admin/blue/collage/collage_add.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request, response);

		if ((id != null) && (!id.equals(""))) {
			CollageBuy collageBuy = this.collageBuyService.selectByPrimaryKey(CommUtil.null2Long(id));
			mv.addObject("obj", collageBuy);
			mv.addObject("view", Boolean.valueOf(true));
		}
		return mv;
	}
	
	/**
	 * 
	 * goods_class_del:拼团活动批量删除. <br/>
	 *
	 * @author redpig
	 * @param request
	 * @param mulitId
	 * @param currentPage
	 * @return
	 * @since JDK 1.8
	 */
	@RequestMapping({ "/collage_del" })
	public String collage_del(HttpServletRequest request, String mulitId,String currentPage) {
		String[] ids = mulitId.split(",");

		List<Long> idList = new ArrayList<>();
		for (String id:ids){
			Long collageId = Long.parseLong(id);
			idList.add(collageId);

			Goods goods = this.collageBuyService.selectByPrimaryKey(collageId).getGoods();
			goods.setCollage(null);
			goods.setCollage_buy(0);
			this.goodsService.updateById(goods);
		}
		this.collageBuyService.batchDeleteByIds(idList);
		return "redirect:collage_list?currentPage=" + currentPage;
	}

	/**
	 * 选择拼团商品
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping({ "/collage_goods_select" })
	public ModelAndView collage_goods_select(HttpServletRequest request,
										  HttpServletResponse response) {
		ModelAndView mv = new RedPigJModelAndView("admin/blue/collage/collage_goods_select.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);

		List<GoodsClass> gcs = this.goodsClassService.queryPageListWithNoRelations(RedPigMaps.newParent(-1));

		mv.addObject("gcs", gcs);
		return mv;
	}

	/**
	 * 选择商品规格
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping({ "/collage_goods_spec_select" })
	public ModelAndView nuke_goods_spec_select(HttpServletRequest request,
											   HttpServletResponse response,String goods_id) {
		ModelAndView mv = new RedPigJModelAndView("admin/blue/collage/collage_goods_spec_select.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);

		List<GoodsClass> gcs = this.goodsClassService.queryPageListWithNoRelations(RedPigMaps.newParent(-1));
		Goods goods = this.goodsService.selectByPrimaryKey(Long.parseLong(goods_id));

		mv.addObject("obj",goods);
		mv.addObject("gcs", gcs);
		mv.addObject("goodsViewTools", this.goodsViewTools);
		return mv;
	}
	/**
	 * 商品加载
	 * @param request
	 * @param response
	 * @param goods_name
	 * @param gc_id
	 */
	@SuppressWarnings("rawtypes")
	@RequestMapping({ "/collage_goods_add_load" })
	public void collage_goods_add_load(HttpServletRequest request,HttpServletResponse response, String goods_name, String gc_id) {

		Map<String, Object> params = Maps.newHashMap();
		params.put("goods_name_like", goods_name.trim());
		params.put("goods_type", 0);
		params.put("goods_status", 0);
		params.put("nuke_buy", 0);//筛选非秒杀商品
		params.put("group_buy", 0);//筛选没有团购的商品
		params.put("collage_buy", 0);//筛选没有拼团商品
		params.put("combin_status", 0);//筛选无组合销售商品
		params.put("order_enough_give_status", 0);
		params.put("enough_reduce", 0);//没有满减
		params.put("f_sale_type", 0);//没有f码
		params.put("advance_sale_type", 0);
		params.put("order_enough_if_give", 0);
		params.put("whether_free", 0);
		params.put("goods_limit", 0);//
		//params.put("inventory_type", "all");

		if ((gc_id != null) && (!gc_id.equals(""))) {
			GoodsClass gc = this.goodsClassService.selectByPrimaryKey(CommUtil.null2Long(gc_id));
			Set<Long> ids = genericGcIds(gc);
			params.put("gc_ids", ids);
		}

		List<Goods> goods = this.goodsService.queryPageList(params);
		List<Map> list = Lists.newArrayList();
		for (Goods obj : goods) {
			Map<String, Object> map = Maps.newHashMap();
			map.put("id", obj.getId());
			map.put("store_price", obj.getGoods_current_price());
			map.put("goods_name", obj.getGoods_name());
			map.put("store_inventory",Integer.valueOf(obj.getGoods_inventory()));
			list.add(map);
		}
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		try {
			PrintWriter writer = response.getWriter();
			writer.print(JSON.toJSONString(list));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private Set<Long> genericGcIds(GoodsClass gc) {
		Set<Long> ids = Sets.newHashSet();
		if (gc != null) {
			ids.add(gc.getId());
			for (GoodsClass child : gc.getChilds()) {
				Set<Long> cids = genericGcIds(child);
				for (Long cid : cids) {
					ids.add(cid);
				}
				ids.add(child.getId());
			}
		}
		return ids;
	}
}
