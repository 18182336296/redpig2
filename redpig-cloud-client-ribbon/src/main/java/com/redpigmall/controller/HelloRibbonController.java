package com.redpigmall.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.redpigmall.service.HelloService;

@RestController
public class HelloRibbonController {
	
	@Autowired
	HelloService helloService;
	
	@RequestMapping("/client")
	public String client(String name) {
		return helloService.hello(name);
	}
	
}
