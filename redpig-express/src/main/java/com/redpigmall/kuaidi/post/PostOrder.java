package com.redpigmall.kuaidi.post;

import java.util.HashMap;

import com.google.common.collect.Maps;
import com.redpigmall.kuaidi.pojo.TaskRequest;
import com.redpigmall.kuaidi.pojo.TaskResponse;

/**
 * 
 * <p>
 * Title: PostOrder.java
 * </p>
 * 
 * <p>
 * Description: 快递100接口测试类
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
 * @date 2014-12-9
 * 
 * @version redpigmall_b2b2c 2016
 */
public class PostOrder {
	public static void testmai(String[] args) {
		TaskRequest req = new TaskRequest();
		req.setCompany("yuantong");
		req.setFrom("上海浦东新区");
		req.setTo("广东深圳南山区");
		req.setNumber("12345678");
		req.getParameters().put("callbackurl",
				"http://www.yourdmain.com/kuaidi");
		req.setKey("PaSQwmsf4785");

		HashMap<String, String> p = Maps.newHashMap();
		p.put("schema", "json");
		p.put("param", JacksonHelper.toJSON(req));
		try {
			String ret = HttpRequest.postData("http://www.kuaidi100.com/poll",
					p, "UTF-8");
			TaskResponse resp = (TaskResponse) JacksonHelper.fromJSON(ret,
					TaskResponse.class);
			if (resp.getResult().booleanValue()) {
				System.out.println("订阅成功");
			} else {
				System.out.println("订阅失败");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
