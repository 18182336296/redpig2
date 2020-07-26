package com.redpigmall.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.redpigmall.service.HelloService;

/**
 * 
 * 
 * Title: FeignController.java
 * 
 * Description:Controller
 * 
 * Copyright: Copyright (c) 2018
 * 
 * Company: RedPigMall
 * 
 * @author redpig
 * 
 * @date 2018年7月10日 上午11:21:53
 * 
 * @version redpigmall_b2b2c v8.0 2018版
 */
@RestController
public class FeignController {
	
	@Autowired
	HelloService helloService;
	
	@RequestMapping("feignClient")
	public String feignClient(String name) {
		return helloService.hello(name);
	}
	
}
