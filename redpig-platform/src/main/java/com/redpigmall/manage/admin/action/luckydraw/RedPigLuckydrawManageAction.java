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
 * Description: 抽奖活动管理
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
public class RedPigLuckydrawManageAction extends BaseAction{

	Logger logger = LoggerFactory.getLogger(RedPigLuckydrawManageAction.class);
	/**
	 * 所有活动列表
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @return
	 */
	@RequestMapping({ "/luckydraw_list" })
	public ModelAndView collage_list(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,String status,
			String luckydraw_name,String beginTime,String endTime,String orderType) {
		ModelAndView mv = new RedPigJModelAndView("admin/blue/luckydraw/luckydraw_list.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request,
				response);

		String url = this.redPigSysConfigService.getSysConfig().getAddress();
		Map<String,Object> params = this.redPigQueryTools.getParams(currentPage, "addTime", "desc");
		if ((url == null) || (url.equals(""))) {
			url = CommUtil.getURL(request);
		}
		if (!CommUtil.null2String(luckydraw_name).equals("")) {
			params.put("luckydraw_name_like", luckydraw_name);
		}
		if (!CommUtil.null2String(status).equals("")) {
			params.put("status", status);
		}
		if (!CommUtil.null2String(beginTime).equals("")) {
			params.put("add_Time_more_than_equal", CommUtil.formatDate(beginTime));
		}
		if (!CommUtil.null2String(endTime).equals("")) {
			params.put("add_Time_less_than_equal", CommUtil.formatDate(endTime));
		}

		mv.addObject("status",status);
		mv.addObject("luckydraw_name", luckydraw_name);
		mv.addObject("beginTime", beginTime);
		mv.addObject("endTime", endTime);
		/*mv.addObject("orderFormTools", this.redPigOrderFormTools);//用来查询付款订单数和付款人数*/
		IPageList pList = this.luckydrawService.list(params);
		CommUtil.saveIPageList2ModelAndView(url + "/luckydraw/luckydraw_list.html", "","", pList, mv);
		return mv;
	}

	/**
	 * 新增活动
	 * @param request
	 * @param response
	 * @param currentPage
	 * @return
	 */
	@RequestMapping({ "/luckydraw_add" })
	public ModelAndView collage_add(
			HttpServletRequest request,
			HttpServletResponse response,
			String currentPage) {

		ModelAndView mv = new RedPigJModelAndView("admin/blue/luckydraw/luckydraw_add.html",
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
	@RequestMapping({ "/luckydraw_save" })
	public ModelAndView luckydraw_save(
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
		if(StringUtils.isNotBlank(user_level_id)){
			ld.setUserLevel(this.userlevelService.selectByPrimaryKey(Long.parseLong(user_level_id)));
		}
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

		mv.addObject("list_url", CommUtil.getURL(request) + "/luckydraw_list");
		mv.addObject("op_title", "保存抽奖成功");
		mv.addObject("add_url", CommUtil.getURL(request) + "/luckydraw_add" + "?currentPage=" + currentPage);
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
	@RequestMapping({ "/luckydraw_edit" })
	public ModelAndView luckydraw_edit(HttpServletRequest request,HttpServletResponse response,String id) {

		ModelAndView mv = new RedPigJModelAndView("admin/blue/luckydraw/luckydraw_add.html",
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
	@RequestMapping({ "/luckydraw_view" })
	public ModelAndView luckydraw_view(HttpServletRequest request,HttpServletResponse response,String id) {

		ModelAndView mv = new RedPigJModelAndView("admin/blue/luckydraw/luckydraw_add.html",
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
	@RequestMapping({ "/luckydraw_del" })
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

	/**
	 * 选择抽奖商品
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping({ "/luckydraw_goods_select" })
	public ModelAndView collage_goods_select(HttpServletRequest request,
										  HttpServletResponse response) {
		ModelAndView mv = new RedPigJModelAndView("admin/blue/luckydraw/luckydraw_goods_select.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);

		List<GoodsClass> gcs = this.goodsClassService.queryPageListWithNoRelations(RedPigMaps.newParent(-1));

		mv.addObject("gcs", gcs);
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
	@RequestMapping({ "/luckydraw_goods_add_load" })
	public void luckydraw_goods_add_load(HttpServletRequest request,HttpServletResponse response, String goods_name, String gc_id) {

		Map<String, Object> params = Maps.newHashMap();
		params.put("goods_name_like", goods_name.trim());
		params.put("goods_type", 0);
		params.put("goods_status", 0);
		params.put("nuke_buy", 0);//筛选非秒杀商品
		params.put("group_buy", 0);//筛选没有团购的商品
		params.put("collage_buy", 0);//筛选没有抽奖商品
		params.put("combin_status", 0);//筛选无组合销售商品
		params.put("order_enough_give_status", 0);
		params.put("enough_reduce", 0);
		params.put("f_sale_type", 0);
		params.put("advance_sale_type", 0);
		params.put("order_enough_if_give", 0);
		params.put("whether_free", 0);
		params.put("goods_limit", 0);//
		//params.put("inventory_type", "all");//

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
