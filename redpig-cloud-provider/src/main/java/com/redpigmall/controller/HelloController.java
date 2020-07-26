package com.redpigmall.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {
	
	
	@RequestMapping("/prvider")
	public String provider(String name) {
		return "这里是服务端的消息。。。。接收到的参数："+name;
	}

}
