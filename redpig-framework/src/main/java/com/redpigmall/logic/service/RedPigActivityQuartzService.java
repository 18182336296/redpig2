package com.redpigmall.logic.service;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.redpigmall.api.sec.SecurityUserHolder;
import com.redpigmall.api.tools.ClusterSyncTools;
import com.redpigmall.api.tools.CommUtil;
import com.redpigmall.api.tools.StringUtils;
import com.redpigmall.domain.*;
import com.redpigmall.lucene.LuceneThread;
import com.redpigmall.lucene.LuceneUtil;
import com.redpigmall.lucene.LuceneVo;
import com.redpigmall.lucene.tools.RedPigLuceneVoTools;
import com.redpigmall.manage.admin.tools.RedPigOrderFormTools;
import com.redpigmall.msg.RedPigMsgTools;
import com.redpigmall.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.DataFormatException;

/**
 * <p>
 * Title: RedPigLuceneClusterServiceImpl.java
 * </p>
 * 
 * <p>
 * Description:秒杀、拼团商品定时修改状态类
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
 * @date 2018-9-3
 * 
 * @version redpigmall_b2b2c 2018
 */
@SuppressWarnings({ "rawtypes", "unchecked", "unused" })
@Service
@Transactional
public class RedPigActivityQuartzService {

	private Logger logger = LoggerFactory.getLogger(RedPigDistributionQuartzService.class);

	@Autowired
	private RedPigNukeGoodsService nukeGoodsService;
	@Autowired
	private RedPigGoodsService goodsService;
	@Autowired
	private RedPigCollageBuyService collageBuyService;
	@Autowired
	protected RedPigOrderFormService orderFormService;
	@Autowired
	private RedPigOrderFormTools orderFormTools;
	@Autowired
	private RedPigNukeService nukeService;
	@Autowired
	private RedPigCouponInfoService couponInfoService;
	@Autowired
	protected RedPigOrderFormLogService orderFormLogService;
	@Autowired
	protected RedPigUserService userService;
	@Autowired
	protected RedPigSysConfigService configService;
	@Autowired
	protected RedPigMsgTools msgTools;
	/**
	 * 每隔一段时间查看有没有商品到了秒杀时间，如果到了，则设置其状态为秒杀，此后其库存和价格从Nukegoods里读取；
	 */
	@Transactional(readOnly=false)
	public void updateNuke(){
		Map<String,Object> params = new HashMap<>();
		params.put("ng_status_more_than_equal", 1);
		List<NukeGoods> nukeList = this.nukeGoodsService.queryPageList(params);
		if (nukeList!=null&&nukeList.size()>0){
			for (NukeGoods nukeGoods:nukeList){
				Nuke nuke = nukeGoods.getNuke();
				Goods goods = nukeGoods.getNg_goods();
				// 未开始
				if((new Date()).before(nuke.getBeginTime())){
					nukeGoods.setNg_status(1);
					goods.setNuke_buy(4);
					this.nukeGoodsService.updateById(nukeGoods);
					this.goodsService.updateById(goods);
				}else if ((new Date()).after(nuke.getBeginTime())&&(new Date()).before(nuke.getEndTime())){
					//已开始：修改商品为秒杀开始状态，价格从秒杀活动表中取
					nukeGoods.setNg_status(2);
					goods.setNuke_buy(2);
					goods.setNuke(nuke);
					this.nukeGoodsService.updateById(nukeGoods);
					this.goodsService.updateById(goods);
				}else if((new Date()).after(nuke.getEndTime())){
					//已过期：无秒杀，恢复商品状态
					nukeGoods.setNg_status(-2);
					goods.setNuke_buy(0);
					goods.setNuke(null);
					this.nukeGoodsService.updateById(nukeGoods);
					this.goodsService.updateById(goods);
				}

			}
		}
	}

	/**
	 * 每隔一段时间查看有没有商品到了拼团时间，如果到了，则设置商品价格为拼团价；
	 */
	@Transactional(readOnly=false)
	public void updateCollage(){
		// 查找未过期和在进行的活动
		Map<String,Object> params = new HashMap<>();
		params.put("cg_status_more_than_equal", 1);
		List<CollageBuy> collageBuyList = this.collageBuyService.queryPageList(params);
		if (collageBuyList!=null&&collageBuyList.size()>0){
			for (CollageBuy collageBuy:collageBuyList){
				Goods goods = collageBuy.getGoods();
				// 判断是否到了拼团时间，如果到了，修改商品价格为拼团价格
				if ((new Date()).before(collageBuy.getBeginTime())) {
					//未开始
					collageBuy.setCg_status(1);
					goods.setCollage_buy(4);
					this.collageBuyService.updateById(collageBuy);
					this.goodsService.updateById(goods);
				} else if((new Date()).after(collageBuy.getBeginTime())&&(new Date()).before(collageBuy.getEndTime())){
					//正在进行中
					collageBuy.setCg_status(2);//设置拼团商品的状态为开始
					goods.setCollage_buy(2);
					goods.setCollage(collageBuy);
					this.collageBuyService.updateById(collageBuy);
					this.goodsService.updateById(goods);
				}else if((new Date()).after(collageBuy.getEndTime())){
					//已过期，商品恢复原状态
					collageBuy.setCg_status(-2);
					goods.setCollage_buy(0);
					goods.setCollage(null);
					this.collageBuyService.updateById(collageBuy);
					this.goodsService.updateById(goods);
				}
			}
		}

	}

	/**
	 * 每隔一段时间查看有没有超时的秒杀订单，如果到了，则取消订单
	 */
	@Transactional(readOnly=false)
	public void updateTimeoutNukeOrders() throws Exception{
		Map<String, Object> params = new HashMap<>();
		params.put("order_cat",3);
		params.put("order_status",10);
		List<OrderForm> orderFormList = this.orderFormService.list(params).getResult();
		SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd hh:mm");
		if (orderFormList==null||orderFormList.isEmpty()){
			return;
		}
		// 遍历所有秒杀订单
		for (OrderForm obj : orderFormList){
			// 查询订单商品信息，秒杀一个单只能有一件商品
			int timeout = obj.getNuke().getTimeout();
			if(!CommUtil.null2String(timeout).isEmpty()&&timeout>0){
				long between = Math.abs(new Date().getTime() - obj.getAddTime().getTime())/1000/60;
				// 如果超时，取消订单
				if (between>timeout){
					Date nowDate = new Date();
					if (1 == obj.getOrder_form()) {
						obj.setOrder_status(0);
						if ((obj.getCoupon_info() != null)&& (!"".equals(obj.getCoupon_info()))) {
							Map m = JSON.parseObject(obj.getCoupon_info());
							CouponInfo cpInfo = this.couponInfoService.selectByPrimaryKey(CommUtil.null2Long(m.get("couponinfo_id")));
							if (cpInfo != null) {
								if (nowDate.before(cpInfo.getEndDate())) {
									cpInfo.setStatus(0);
								} else {
									cpInfo.setStatus(-1);
								}
								this.couponInfoService.updateById(cpInfo);
							}
						}
						this.orderFormService.updateById(obj);
						OrderFormLog ofl = new OrderFormLog();
						ofl.setAddTime(new Date());
						ofl.setLog_info("取消订单");
						ofl.setLog_user_id(1l);//设置1为取消订单的Id

						ofl.setLog_user_name("admin");
						ofl.setOf(obj);
						ofl.setState_info("秒杀订单超时未付款，系统自动取消订单！");

						this.orderFormLogService.saveEntity(ofl);
						User buyer = this.userService.selectByPrimaryKey(CommUtil.null2Long(obj.getUser_id()));
						Map<String, Object> map = Maps.newHashMap();
						map.put("buyer_id", buyer.getId().toString());
						map.put("self_goods", this.configService.getSysConfig().getTitle());
						map.put("order_id", obj.getId());
						String json = JSON.toJSONString(map);
						this.msgTools.sendEmailFree("","email_tobuyer_selforder_cancel_notify", buyer.getEmail(),json, null);
						this.msgTools.sendEmailFree("","sms_tobuyer_selforder_cancel_notify", buyer.getMobile(),json, null);
					}
				}
			}
		}
	}

	/**
	 * 每隔一段时间查看有没有超时的拼团订单，如果到了，则取消订单
	 */
	@Transactional(readOnly=false)
	public void updateTimeoutCollageOrders() throws Exception{
		Map<String, Object> params = new HashMap<>();
		params.put("order_cat",4);
		params.put("order_status",10);
		List<OrderForm> orderFormList = this.orderFormService.list(params).getResult();
		SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd hh:mm");
		if (orderFormList==null||orderFormList.isEmpty()){
			return;
		}
		// 遍历所有拼团订单
		for (OrderForm obj : orderFormList){
			// 查询订单商品信息，拼团一个单只能有一件商品
			int timeout = obj.getCollage().getTimeout();
			if(!CommUtil.null2String(timeout).isEmpty()&&timeout>0){
				long between = Math.abs(new Date().getTime() - obj.getAddTime().getTime())/1000/60;
				// 如果超时，取消订单
				if (between>timeout){
					Date nowDate = new Date();
					if (1 == obj.getOrder_form()) {
						obj.setOrder_status(0);
						if ((obj.getCoupon_info() != null)&& (!"".equals(obj.getCoupon_info()))) {
							Map m = JSON.parseObject(obj.getCoupon_info());
							CouponInfo cpInfo = this.couponInfoService.selectByPrimaryKey(CommUtil.null2Long(m.get("couponinfo_id")));
							if (cpInfo != null) {
								if (nowDate.before(cpInfo.getEndDate())) {
									cpInfo.setStatus(0);
								} else {
									cpInfo.setStatus(-1);
								}
								this.couponInfoService.updateById(cpInfo);
							}
						}
						this.orderFormService.updateById(obj);
						OrderFormLog ofl = new OrderFormLog();
						ofl.setAddTime(new Date());
						ofl.setLog_info("取消订单");
						ofl.setLog_user_id(1l);
                        ofl.setLog_user_name("admin");
						ofl.setOf(obj);
						ofl.setState_info("拼团订单超时未付款，系统自动取消订单！");

						this.orderFormLogService.saveEntity(ofl);
						User buyer = this.userService.selectByPrimaryKey(CommUtil.null2Long(obj.getUser_id()));
						Map<String, Object> map = Maps.newHashMap();
						map.put("buyer_id", buyer.getId().toString());
						map.put("self_goods", this.configService.getSysConfig().getTitle());
						map.put("order_id", obj.getId());
						String json = JSON.toJSONString(map);
						this.msgTools.sendEmailFree("","email_tobuyer_selforder_cancel_notify", buyer.getEmail(),json, null);
						this.msgTools.sendEmailFree("","sms_tobuyer_selforder_cancel_notify", buyer.getMobile(),json, null);
					}
				}
			}
		}
	}
}
