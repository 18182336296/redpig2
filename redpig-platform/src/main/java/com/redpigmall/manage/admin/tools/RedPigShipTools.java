package com.redpigmall.manage.admin.tools;

import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.parser.Feature;
import com.google.common.collect.Maps;
import com.redpigmall.api.tools.CommUtil;
import com.redpigmall.domain.ExpressInfo;
import com.redpigmall.domain.IntegralGoodsOrder;
import com.redpigmall.domain.OrderForm;
import com.redpigmall.domain.ReturnGoodsLog;
import com.redpigmall.domain.virtual.TransContent;
import com.redpigmall.domain.virtual.TransInfo;
import com.redpigmall.service.RedPigExpressInfoService;
import com.redpigmall.service.RedPigIntegralGoodsOrderService;
import com.redpigmall.service.RedPigOrderFormService;
import com.redpigmall.service.RedPigReturnGoodsLogService;
import com.redpigmall.service.RedPigSysConfigService;

/**
 * 
 * 
 * <p>
 * Ship
 * </p>
 * 
 * <p>
 * Description:物流查询工具类
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
 * @date 2014年5月23日
 * 
 * @version redpigmall_b2b2c 8.0
 */
@SuppressWarnings({ "unchecked", "rawtypes", "deprecation" })
@Component
public class RedPigShipTools {
	@Autowired
	private RedPigOrderFormService orderFormService;
	@Autowired
	private RedPigSysConfigService configService;
	
	@Autowired
	private RedPigExpressInfoService expressInfoService;
	@Autowired
	private RedPigReturnGoodsLogService returnGoodsLogService;
	@Autowired
	private RedPigIntegralGoodsOrderService goodsOrderService;

	public TransInfo query_Ordership_getData(String id) {
		TransInfo info = new TransInfo();
		OrderForm obj = this.orderFormService.selectByPrimaryKey(CommUtil
				.null2Long(id));
		if ((obj != null)
				&& (!CommUtil.null2String(obj.getShipCode()).equals(""))) {
			Map express_company = JSON.parseObject(obj.getExpress_info());
			String express_company_mark = CommUtil.null2String(express_company
					.get("express_company_mark"));
			String url = "";
			if (this.configService.getSysConfig().getKuaidi_type() == 0) {
				url =

				"http://api.kuaidi.com/openapi?id="
						+ this.configService.getSysConfig().getKuaidi_id()
						+ "&com="
						+ (express_company_mark != null ? express_company_mark
								: "") + "&nu=" + obj.getShipCode()
						+ "&show=0&muti=0&order=desc";
				String param = null;
				String content = sendGet(url, param);
				info = (TransInfo) JSON.parseObject(content, TransInfo.class);
			} else {

				Map<String, Object> params = Maps.newHashMap();
				params.put("order_type", 0);
				params.put("order_id", obj.getId());

				List<ExpressInfo> eis = this.expressInfoService
						.queryPageList(params);

				if (eis != null && eis.size() > 0) {
					ExpressInfo ei = eis.get(0);
					if ((ei.getOrder_express_info() != null)
							&& (!"".equals(ei.getOrder_express_info()))) {
						List<TransContent> data = (List) JSON
								.parseObject(CommUtil.null2String(ei));

						info.setData(data);
						int status = ei.getOrder_status() + 3;
						info.setStatus(CommUtil.null2String(Integer
								.valueOf(status)));
					} else {
						info.setStatus("5");
						info.setReason("快递公司参数异常：单号不存在或者已经过期 ");
					}
				}
			}
		}
		return info;
	}

	public TransInfo query_Returnship_getData(String id) {
		TransInfo info = new TransInfo();
		ReturnGoodsLog obj = this.returnGoodsLogService
				.selectByPrimaryKey(CommUtil.null2Long(id));
		if (this.configService.getSysConfig().getKuaidi_type() == 0) {
			Map express_company = JSON
					.parseObject(obj.getReturn_express_info());
			String express_company_mark = CommUtil.null2String(express_company
					.get("express_company_mark"));
			String url = "";
			if (this.configService.getSysConfig().getKuaidi_type() == 0) {
				url =

				"http://api.kuaidi.com/openapi?id="
						+ this.configService.getSysConfig().getKuaidi_id()
						+ "&com="
						+ (express_company_mark != null ? express_company_mark
								: "") + "&nu=" + obj.getExpress_code()
						+ "&show=0&muti=0&order=desc";
				String param = null;
				String content = sendGet(url, param);
				info = (TransInfo) JSON.parseObject(content, TransInfo.class);
			} else {
				Map<String, Object> params = Maps.newHashMap();
				params.put("order_type", 0);
				params.put("order_id", obj.getId());

				List<ExpressInfo> eis = this.expressInfoService
						.queryPageList(params);

				if (eis != null && eis.size() > 0) {
					ExpressInfo ei = eis.get(0);
					if ((ei.getOrder_express_info() != null)
							&& (!"".equals(ei.getOrder_express_info()))) {
						List<TransContent> data = (List) JSON
								.parseObject(CommUtil.null2String(ei
										.getOrder_express_info()),
										new TypeReference() {
										}, new Feature[0]);

						info.setData(data);
						int status = ei.getOrder_status() + 3;
						info.setStatus(CommUtil.null2String(Integer
								.valueOf(status)));
					} else {
						info.setStatus("5");
						info.setReason("快递公司参数异常：单号不存在或者已经过期 ");
					}
				}
			}
		}
		return info;
	}

	public TransInfo query_IntegralOrdership_getData(String id) {
		TransInfo info = new TransInfo();
		IntegralGoodsOrder obj = this.goodsOrderService
				.selectByPrimaryKey(CommUtil.null2Long(id));
		if ((obj != null)
				&& (!CommUtil.null2String(obj.getIgo_ship_code()).equals(""))) {
			Map express_company = JSON.parseObject(obj.getIgo_express_info());
			String express_company_mark = CommUtil.null2String(express_company
					.get("express_company_mark"));
			String url = "";
			if (this.configService.getSysConfig().getKuaidi_type() == 0) {
				url =

				"http://api.kuaidi.com/openapi?id="
						+ this.configService.getSysConfig().getKuaidi_id()
						+ "&com="
						+ (express_company_mark != null ? express_company_mark
								: "") + "&nu=" + obj.getIgo_ship_code()
						+ "&show=0&muti=0&order=desc";
				String param = null;
				String content = sendGet(url, param);
				info = (TransInfo) JSON.parseObject(content, TransInfo.class);
			} else {
				Map<String, Object> params = Maps.newHashMap();
				params.put("order_type", 0);
				params.put("order_id", obj.getId());

				List<ExpressInfo> eis = this.expressInfoService
						.queryPageList(params);

				if (eis != null && eis.size() > 0) {
					ExpressInfo ei = eis.get(0);
					if ((ei.getOrder_express_info() != null)
							&& (!"".equals(ei.getOrder_express_info()))) {
						List<TransContent> data = (List) JSON
								.parseObject(CommUtil.null2String(ei
										.getOrder_express_info()),
										new TypeReference() {
										}, new Feature[0]);

						info.setData(data);
						int status = ei.getOrder_status() + 3;
						info.setStatus(CommUtil.null2String(Integer
								.valueOf(status)));
					} else {
						info.setStatus("5");
						info.setReason("快递公司参数异常：单号不存在或者已经过期 ");
					}
				}
			}
		}
		return info;
	}

	@SuppressWarnings("resource")
	public static String sendGet(String url, String param) {
		try {
			// 创建HttpClient实例
			HttpClient httpclient = new DefaultHttpClient();
			// 创建Get方法实例
			HttpGet method = new HttpGet(url);
			HttpResponse result = httpclient.execute(method);
			return EntityUtils.toString(result.getEntity());
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e.getMessage());
		}

	}

	@SuppressWarnings("resource")
	public static String Post(String url, String param) {

		try {
			DefaultHttpClient httpClient = new DefaultHttpClient();
			HttpPost method = new HttpPost(url);
			StringEntity entity = new StringEntity(param, "utf-8");// 解决中文乱码问题
			entity.setContentEncoding("UTF-8");
			entity.setContentType("application/json");
			method.setEntity(entity);
			HttpResponse result = httpClient.execute(method);
			// 请求结束，返回结果
			return EntityUtils.toString(result.getEntity());
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e.getMessage());
		}

	}
}
