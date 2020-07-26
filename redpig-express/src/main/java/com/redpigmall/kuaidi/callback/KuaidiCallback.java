package com.redpigmall.kuaidi.callback;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.redpigmall.api.tools.CommUtil;
import com.redpigmall.api.tools.Md5Encrypt;
import com.redpigmall.kuaidi.pojo.LastResult;
import com.redpigmall.kuaidi.pojo.Result;
import com.redpigmall.kuaidi.pojo.ResultItem;
import com.redpigmall.domain.ExpressInfo;
import com.redpigmall.service.RedPigExpressInfoService;

/**
 * 
 * <p>
 * Title: KuaidiCallback.java
 * </p>
 * 
 * <p>
 * Description: 快递100收费接口主动推送快递信息到系统，系统接收保存
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
 * @date 2014-11-4
 * 
 * @version redpigmall_b2b2c 2016
 */
@Controller
public class KuaidiCallback {
	
	@Autowired
	private RedPigExpressInfoService expressInfoService;
	
	/**
	 * 快递回调
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping({ "/kuaidi_callback" })
	public void kuaidi_callback(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		JSONObject json = new JSONObject();
		json.put("result", Boolean.valueOf(false));
		json.put("returnCode", "500");
		json.put("message", "失败");
		try {
			String param = request.getParameter("param");
			Result result = (Result) JSON.parseObject(param, Result.class);
			LastResult lastResult = result.getLastResult();
			List<ResultItem> item_list = lastResult.getData();

			Long order_id = CommUtil
					.null2Long(request.getParameter("order_id"));
			int order_type = CommUtil.null2Int(request
					.getParameter("order_type"));
			
			Map<String, Object> params = Maps.newHashMap();
			params.put("order_type", Integer.valueOf(order_type));
			
			params.put("operation_property", "order_id");
			
			params.put("operation_symbol", "=");
			
			params.put("operation_value", order_id);
			
			List<ExpressInfo> objs = this.expressInfoService.selectObjByProperty(params);
			
			ExpressInfo obj = null;
			if(objs !=null && objs.size()>0){
				obj = objs.get(0);
			}
			
			String sign = CommUtil.null2String(request.getParameter("sign"));
			String salt = Md5Encrypt.md5(CommUtil.null2String(order_id)).substring(0, 16);
			String sign1 = Md5Encrypt.md5(param + salt);
			System.out.println("返回签名为：" + sign + ",计算的签名为：" + sign1);
			if (sign.equals(sign1)) {
				if (obj == null) {
					obj = new ExpressInfo();
					obj.setAddTime(new Date());
					obj.setOrder_id(order_id);
					obj.setOrder_express_id(lastResult.getNu());
					obj.setOrder_type(order_type);
					obj.setOrder_status(CommUtil.null2Int(lastResult.getState()));
					obj.setOrder_express_info(JSON.toJSONString(item_list));
					this.expressInfoService.saveEntity(obj);
				} else {
					obj.setOrder_id(order_id);
					obj.setOrder_express_id(lastResult.getNu());
					obj.setOrder_status(CommUtil.null2Int(lastResult.getState()));
					obj.setOrder_express_info(JSON.toJSONString(item_list));
					obj.setOrder_type(order_type);
					this.expressInfoService.updateById(obj);
				}
				json.put("result", Boolean.valueOf(true));
				json.put("returnCode", "200");
				json.put("message", "成功");
				response.getWriter().print(json);
			}
		} catch (Exception e) {
			response.getWriter().print(json);
		}
	}
}
