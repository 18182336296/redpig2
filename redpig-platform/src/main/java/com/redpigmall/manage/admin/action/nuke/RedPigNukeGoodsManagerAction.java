package com.redpigmall.manage.admin.action.nuke;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.redpigmall.api.annotation.SecurityMapping;
import com.redpigmall.api.tools.StringUtils;
import com.redpigmall.domain.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.redpigmall.api.mv.RedPigJModelAndView;
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.api.sec.SecurityUserHolder;
import com.redpigmall.api.tools.CommUtil;
import com.redpigmall.api.tools.RedPigMaps;
import com.redpigmall.api.tools.WebForm;
import com.redpigmall.manage.admin.action.base.BaseAction;

/**
 * 
 * Title: RedPigNukeGoodsManagerAction.java
 * 
 * Description:秒杀商品管理
 * 
 * Copyright: Copyright (c) 2018
 * 
 * Company: RedPigMall
 * 
 * @author redpig
 * 
 * @date 2018年7月30日 下午8:52:48
 * 
 * @version redpigmall_b2b2c v8.0 2018版  
 */
@Controller
public class RedPigNukeGoodsManagerAction extends BaseAction {
	
	/**
	 * 秒杀列表
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param ng_name
	 * @return
	 */
	@RequestMapping({ "/nuke_goods" })
	public ModelAndView nuke_goods(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String ng_name, String nuke_id,String ng_status,
								   String beginTime,String endTime) {
		ModelAndView mv = new RedPigJModelAndView("admin/blue/nuke/nuke_goods.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		
		Map<String,Object> params = this.redPigQueryTools.getParams(currentPage, "addTime", "desc");
		params.put("nuke_type", 0);
		
		if (!CommUtil.null2String(ng_name).equals("")) {
			params.put("ng_name_like", ng_name);
		}
		if (!CommUtil.null2String(nuke_id).equals("")) {
			params.put("nuke_id", nuke_id);
		}
		if (!CommUtil.null2String(ng_status).equals("")) {
			params.put("ng_status", ng_status);
		}
		if (!CommUtil.null2String(beginTime).equals("")) {
			params.put("add_Time_more_than_equal", CommUtil.formatDate(beginTime));
		}
		if (!CommUtil.null2String(endTime).equals("")) {

			params.put("add_Time_less_than_equal", CommUtil.formatDate(endTime));
		}

		IPageList pList = this.nukeGoodsService.list(params);
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		mv.addObject("ng_name", ng_name);
		mv.addObject("nuke_id", nuke_id);
		mv.addObject("ng_status", ng_status);
		mv.addObject("beginTime", beginTime);
		mv.addObject("endTime", endTime);
		mv.addObject("orderFormTools", this.redPigOrderFormTools);//用来查询付款订单数和付款人数
		// 筛选框
		List<Nuke> nukes = this.nukeService.queryPageList(null);
		mv.addObject("nukes", nukes);
		return mv;
	}
	

	/**
	 * 自营商品类团购商品添加
	 * @param request
	 * @param response
	 * @param nc_type
	 * @return
	 */
	@RequestMapping({ "/nuke_goods_add" })
	public ModelAndView nuke_goods_add(HttpServletRequest request,
			HttpServletResponse response, String nc_type) {
		ModelAndView mv = new RedPigJModelAndView("admin/blue/nuke/nuke_goods_add.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		Map<String, Object> params = Maps.newHashMap();
		
		List<Nuke> nukes = this.nukeService.queryPageList(params);
		List<NukeClass> ncs = this.nukeClassService.queryPageList(params);

		mv.addObject("ncs", ncs);
		mv.addObject("nukes", nukes);
		return mv;
	}

	/**
	 * 选择秒杀商品
	 * @param request
	 * @param response
	 * @return	
	 */
	@RequestMapping({ "/nuke_goods_select" })
	public ModelAndView nuke_goods_select(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new RedPigJModelAndView("admin/blue/nuke/nuke_goods_select.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		
		List<GoodsClass> gcs = this.goodsClassService.queryPageListWithNoRelations(RedPigMaps.newParent(-1));
		
		mv.addObject("gcs", gcs);
		mv.addObject("goodsViewTools", this.goodsViewTools);
		return mv;
	}

    /**
     * 选择商品规格
     * @param request
     * @param response
     * @return
     */
    @RequestMapping({ "/nuke_goods_spec_select" })
    public ModelAndView nuke_goods_spec_select(HttpServletRequest request,
                                          HttpServletResponse response,String goods_id) {
        ModelAndView mv = new RedPigJModelAndView("admin/blue/nuke/nuke_goods_spec_select.html",
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
	@RequestMapping({ "/nuke_goods_add_load" })
	public void nuke_goods_add_load(HttpServletRequest request,HttpServletResponse response, String goods_name, String gc_id) {
		
		Map<String, Object> params = Maps.newHashMap();
		params.put("goods_name_like", goods_name.trim());
		params.put("goods_type", 0);
		params.put("goods_status", 0);
		params.put("nuke_buy", 0);//筛选非秒杀商品
		params.put("group_buy", 0);//筛选没有团购的商品
		params.put("collage_buy", 0);//筛选没有拼团商品
		params.put("combin_status", 0);//筛选无组合销售商品
		params.put("order_enough_give_status", 0);
		params.put("enough_reduce", 0);
		params.put("f_sale_type", 0);
		params.put("advance_sale_type", 0);
		params.put("order_enough_if_give", 0);
		params.put("whether_free", 0);
		params.put("goods_limit", 0);//
		//暂时只能选择没有分库存的商品
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

	/**
	 * 通过用户筛选的商品，列出商品的详情
	 * @param request
	 * @param response
	 * @param goods_id
	 */
	@SuppressWarnings("rawtypes")
    @ResponseBody
	@RequestMapping({ "/getGoodsById" })
	public Map getGoodsById(HttpServletRequest request,HttpServletResponse response, String goods_id) {
		Map<String,Object> map = new HashMap<>();
		Goods goods = this.goodsService.selectByPrimaryKey(Long.parseLong(goods_id.trim()));
		// 商品的规格
        List<GoodsSpecification> specs = this.goodsViewTools.generic_spec(CommUtil.null2String(goods_id));

        // 商品的规格属性
		List<GoodsSpecProperty> goods_specs = goods.getGoods_specs();

		map.put("goods_specs",goods_specs);
		map.put("specs",specs);

		return map;
	}
	/**
	 * 通过商品id获取商品的规格，数量等信息
	 * @param request
	 * @param response
	 */
	@SuppressWarnings("rawtypes")
	@RequestMapping({ "/get_goods_inventory" })
	public void get_goods_inventory(HttpServletRequest request,HttpServletResponse response, String goods_id) {

		Map<String, Object> params = Maps.newHashMap();
		//暂时只能选择没有分库存的商品
		Goods goods = this.goodsService.selectByPrimaryKey(Long.parseLong(goods_id));
		List<GoodsSpecification> specs = this.goodsViewTools.generic_spec(CommUtil.null2String(goods.getId()));
		for (GoodsSpecification spec : specs) {
			for (GoodsSpecProperty prop : goods.getGoods_specs()) {
				if (prop.getSpec().getId().equals(spec.getId())) {
					break;
				}
			}
		}
		List<Map> list = Lists.newArrayList();
		/*for (Goods obj : goods) {
			Map<String, Object> map = Maps.newHashMap();
			map.put("id", obj.getId());
			map.put("store_price", obj.getGoods_current_price());
			map.put("goods_name", obj.getGoods_name());
			map.put("store_inventory",Integer.valueOf(obj.getGoods_inventory()));
			list.add(map);
		}*/
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
	
	/**
	 * 
	 * nuke_goods_save:保存秒杀商品. <br/>
	 *
	 * @author redpig
	 * @param request
	 * @param response
	 * @param id
	 * @param nuke_id
	 * @param goods_id
	 * @param nc_id
	 * @param ng_price
	 * @since JDK 1.8
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping({ "/nuke_goods_save" })
	public ModelAndView nuke_goods_save(HttpServletRequest request,
			HttpServletResponse response, String id, String nuke_id,String limit_number,
			String goods_id, String nc_id, String origin_price,String ng_price,String goods_spec_id) {
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		
		NukeGoods nukeGoods = null;
		if (id.equals("")) {
			nukeGoods = (NukeGoods) WebForm.toPo(request, NukeGoods.class);
			nukeGoods.setAddTime(new Date());
		} else {
			NukeGoods obj = this.nukeGoodsService.selectByPrimaryKey(CommUtil.null2Long(id));
			nukeGoods = (NukeGoods) WebForm.toPo(request, obj);
		}

		// 保存商品原价（2位小时）
		BigDecimal bd = new BigDecimal(origin_price);
		bd.setScale(2,BigDecimal.ROUND_HALF_UP);
		nukeGoods.setOrigin_price(new BigDecimal(origin_price).setScale(2,BigDecimal.ROUND_HALF_UP));
		// 设置秒杀信息
		nukeGoods.setNg_count(nukeGoods.getNg_count());// 设置秒杀库存量
		Nuke nuke = this.nukeService.selectByPrimaryKey(CommUtil.null2Long(nuke_id));
		nukeGoods.setNuke(nuke);
		Goods goods = this.goodsService.selectByPrimaryKey(CommUtil.null2Long(goods_id));
		nukeGoods.setNg_goods(goods);
		// 设置规格,如果不是统一规格，则设置规格的id
        if(!goods_spec_id.equals("统一规格")){
            nukeGoods.setGoods_spec_id(goods_spec_id);
        }
		// 设置秒杀类型
		NukeClass nc = this.nukeClassService.selectByPrimaryKey(CommUtil.null2Long(nc_id));
		nukeGoods.setNg_nc(nc);
		nukeGoods.setLimit_number(CommUtil.null2Int(limit_number));
		// 设置秒杀折扣率
		nukeGoods.setNg_rebate(BigDecimal.valueOf(CommUtil.mul(Integer.valueOf(10),Double.valueOf(CommUtil.div(ng_price, goods.getStore_price())))));
		String uploadFilePath = this.configService.getSysConfig().getUploadFilePath();
		String saveFilePathName = request.getSession().getServletContext()
				.getRealPath("/")
				+ uploadFilePath + File.separator + "nuke";
		Map<String, Object> map = Maps.newHashMap();
		// 以下是上传图片
		try {
			String fileName = nukeGoods.getNg_img() == null ? "" : nukeGoods.getNg_img().getName();
			map = CommUtil.saveFileToServer(request, "ng_acc",saveFilePathName, fileName, null);
			if (fileName.equals("")) {
				if (map.get("fileName") != "") {
					Accessory ng_img = new Accessory();
					ng_img.setName(CommUtil.null2String(map.get("fileName")));
					ng_img.setExt(CommUtil.null2String(map.get("mime")));
					ng_img.setSize(BigDecimal.valueOf(CommUtil.null2Double(map.get("fileSize"))));
					ng_img.setPath(uploadFilePath + "/nuke");
					ng_img.setWidth(CommUtil.null2Int(map.get("width")));
					ng_img.setHeight(CommUtil.null2Int(map.get("height")));
					ng_img.setAddTime(new Date());
					this.accessoryService.saveEntity(ng_img);
					nukeGoods.setNg_img(ng_img);
				}
			} else if (map.get("fileName") != "") {
				Accessory ng_img = nukeGoods.getNg_img();
				ng_img.setName(CommUtil.null2String(map.get("fileName")));
				ng_img.setExt(CommUtil.null2String(map.get("mime")));
				ng_img.setSize(BigDecimal.valueOf(CommUtil.null2Double(map.get("fileSize"))));
				ng_img.setPath(uploadFilePath + "/nuke");
				ng_img.setWidth(CommUtil.null2Int(map.get("width")));
				ng_img.setHeight(CommUtil.null2Int(map.get("height")));
				ng_img.setAddTime(new Date());
				this.accessoryService.updateById(ng_img);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		nukeGoods.setNg_rebate(BigDecimal.valueOf(CommUtil.div(Double.valueOf(CommUtil
				.mul(nukeGoods.getNg_price(), Integer.valueOf(10))), nukeGoods.getNg_goods()
				.getGoods_price())));

		// 设置秒杀的信息
		goods.setNuke(nuke);
		// 判断是否到了秒杀时间，如果到了时间，其库存和价格从Nukegoods里读取
		if ((new Date()).before(nuke.getBeginTime())) {
			//未开始
			nukeGoods.setNg_status(1);
			goods.setNuke_buy(4);
		} else if((new Date()).after(nuke.getBeginTime())&&(new Date()).before(nuke.getEndTime())){
			//正在进行中,设置秒杀商品的状态为开始
			nukeGoods.setNg_status(2);
			goods.setNuke_buy(2);
			/*// 如果d是统一规格，设置当前商品的价格为秒杀价
            if (goods_spec_id.equals("统一规格")){
                goods.setGoods_current_price(nukeGoods.getNg_price());
            }else{
                //如果分规格，则设置goods_inventory_detail的对应id为秒杀价
                List<HashMap> list = JSON.parseArray(CommUtil.null2String(goods.getGoods_inventory_detail()),HashMap.class);
                for (Map temp : list) {
                    String tempId = temp.get("id").toString();
                    if (tempId.equals(goods_spec_id.trim())){
                    	// 更新商品价格
						temp.put("price",nukeGoods.getNg_price());
						break;
                    }
                }
                // 保存秒杀商品详情
                goods.setGoods_inventory_detail(JSON.toJSONString(list));
            }*/
		}else if((new Date()).after(nuke.getEndTime())){
			//已过期
			nukeGoods.setNg_status(-2);
			goods.setNuke_buy(-2);
		}

		if (id.equals("")) {
			this.nukeGoodsService.saveEntity(nukeGoods);
		} else {
			this.nukeGoodsService.updateById(nukeGoods);
		}

		this.goodsService.updateById(goods);
		this.goodsTools.updateGoodsLucene(goods);
		// 返回页面
		ModelAndView mv = new RedPigJModelAndView("admin/blue/success.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		
		mv.addObject("op_title", "开通秒杀成功");
		mv.addObject("list_url", CommUtil.getURL(request) + "/nuke_goods");
		
		return mv;
	}


	/**
	 * 秒杀商品审核通过
	 * @param request
	 * @param response
	 * @param mulitId
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "秒杀商品审核保存", value = "/nuke_goods_audit*", rtype = "admin", rname = "秒杀管理", rcode = "nuke_admin", rgroup = "秒杀")
	@RequestMapping({ "/nuke_goods_audit" })
	public String group_lifegoods_audit(
			HttpServletRequest request,
			HttpServletResponse response,
			String mulitId,
			String status,
			String currentPage) {

		String[] ids = mulitId.split(",");
		for (String id : ids) {
			if (!id.equals("")) {
				NukeGoods nukeGoods = this.nukeGoodsService.selectByPrimaryKey(CommUtil.null2Long(id));
				//商品的活动信息
				Nuke nuke = nukeGoods.getNuke();
				//如果开始时间早于当前或者活动已经结束
				if ((nuke.getBeginTime().before(new Date()))||
						(CommUtil.formatShortDate(nuke.getBeginTime()).equals(CommUtil.formatShortDate(new Date())))) {
					nuke.setNg_status(1);
				} else {
					nuke.setNg_status(CommUtil.null2Int(status));
				}
				nukeGoods.setNuke(nuke);
				this.nukeGoodsService.updateById(nukeGoods);
				//nukeGoodsService
				//this.goodsTools.addGroupLifeLucene(gg);
			}
		}
		return "redirect:nuke_goods?currentPage=" + currentPage;
	}


	/**
	 * 
	 * verify_nuke_begintime:验证秒杀开始时间. <br/>
	 * 
	 * @author redpig
	 * @param request
	 * @param response
	 * @param beginTime
	 * @param nuke_id
	 * @since JDK 1.8
	 */
	@RequestMapping({ "/verify_nuke_begintime" })
	public void verify_nuke_begintime(HttpServletRequest request,HttpServletResponse response, 
			String beginTime, String nuke_id) {
		boolean ret = false;
		Nuke nuke = this.nukeService.selectByPrimaryKey(CommUtil.null2Long(nuke_id));
		Date date = CommUtil.formatDate(beginTime);
		if ((date.after(nuke.getBeginTime()))
				||
				(CommUtil.formatLongDate(date).equals(CommUtil.formatLongDate(nuke.getBeginTime())))) {
			ret = true;
		}
		
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
	 * 
	 * verify_nuke_endtime:验证秒杀结束时间. <br/>
	 *
	 * @author redpig
	 * @param request
	 * @param response
	 * @param endTime
	 * @param nuke_id
	 * @since JDK 1.8
	 */
	@RequestMapping({ "/verify_nuke_endtime" })
	public void verify_nuke_endtime(HttpServletRequest request,
			HttpServletResponse response, String endTime, String nuke_id) {
		boolean ret = false;
		Nuke nuke = this.nukeService.selectByPrimaryKey(CommUtil.null2Long(nuke_id));
		Date date = CommUtil.formatDate(endTime);
		if (date.before(nuke.getEndTime())) {
			ret = true;
		}
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
	 * 
	 * nuke_goods_edit:编辑秒杀商品. <br/>
	 *
	 * @author redpig
	 * @param request
	 * @param response
	 * @param id
	 * @return
	 * @since JDK 1.8
	 */
	@RequestMapping({ "/nuke_goods_edit" })
	public ModelAndView nuke_goods_edit(HttpServletRequest request,
			HttpServletResponse response, String id) {
		ModelAndView mv = new RedPigJModelAndView("admin/blue/nuke/nuke_goods_add.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);

		if ((id != null) && (!id.equals(""))) {
			NukeGoods nukeGoods = this.nukeGoodsService.selectByPrimaryKey(CommUtil.null2Long(id));
			mv.addObject("obj", nukeGoods);
			mv.addObject("edit", Boolean.valueOf(true));

			Goods goods = nukeGoods.getNg_goods();
			// 获取商品的库存
			// 如果是统一规格，加载现有的统一库存
			if (StringUtils.isNotBlank(nukeGoods.getGoods_spec_id())){
				if (goods.getInventory_type().equals("all")){
					mv.addObject("goods_cur_inventory", goods.getGoods_inventory());
				}else if(goods.getInventory_type().equals("spec")){
					//如果分规格，遍历商品规格表，找出goods_spec_id相同的库存
					List<HashMap> list = JSON.parseArray(CommUtil.null2String(goods.getGoods_inventory_detail()),HashMap.class);
					for (Map temp : list) {
						String tempId = temp.get("id").toString();
						if (tempId.equals(nukeGoods.getGoods_spec_id().trim())){
							// 加载该规格下的库存
							mv.addObject("goods_cur_inventory", temp.get("count").toString());
							break;
						}
					}
				}
			}
		}
		Map<String, Object> params = Maps.newHashMap();
		List<Nuke> nukes = this.nukeService.queryPageList(params);
		List<NukeClass> ncs = this.nukeClassService.queryPageList(params);
		mv.addObject("ncs", ncs);
		mv.addObject("nukes", nukes);

		return mv;
	}

	/**
	 *
	 * nuke_goods_edit:删除秒杀商品. <br/>
	 *
	 * @author redpig
	 * @param request
	 * @param response
	 * @return
	 * @since JDK 1.8
	 */
	@RequestMapping({ "/nuke_goods_del" })
	public String nuke_goods_del(HttpServletRequest request,
										HttpServletResponse response, String mulitId,String currentPage) {
		ModelAndView mv = new RedPigJModelAndView("admin/blue/nuke/nuke_goods.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);

		String[] ids = mulitId.split(",");
		List<Long> lists = Lists.newArrayList();
		for (String id : ids) {
			lists.add(Long.parseLong(id));
			// 设置的当前商品的状态为非秒杀
			NukeGoods nukeGoods = this.nukeGoodsService.selectByPrimaryKey(Long.parseLong(id));
			Goods goods = nukeGoods.getNg_goods();

			/*// 设置为默认价格
			goods.setGoods_current_price(goods.getStore_price());
			// 删除秒杀的商品后，商品恢复原价
			if(StringUtils.isNotBlank(nukeGoods.getGoods_spec_id())){
				if (nukeGoods.getGoods_spec_id().equals("统一规格")){
					goods.setGoods_current_price(nukeGoods.getOrigin_price());
				}else{
					//如果分规格，则设置goods_inventory_detail的对应id为原价
					List<HashMap> list = JSON.parseArray(CommUtil.null2String(goods.getGoods_inventory_detail()),HashMap.class);
					for (Map temp : list) {
						String tempId = temp.get("id").toString();
						if (tempId.equals(nukeGoods.getGoods_spec_id().trim())){
							// 更新商品价格
							temp.put("price",nukeGoods.getOrigin_price());
							break;
						}
					}
					// 保存秒杀商品详情
					goods.setGoods_inventory_detail(JSON.toJSONString(list));
				}
			}*/
			goods.setNuke(null);
			goods.setNuke_buy(0);
			this.goodsService.updateById(goods);
		}
		this.nukeGoodsService.batchDeleteByIds(lists);
		return "redirect:nuke_goods?currentPage=" + currentPage;
	}
}

