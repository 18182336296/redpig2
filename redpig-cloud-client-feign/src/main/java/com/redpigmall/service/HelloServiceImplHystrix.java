package com.redpigmall.service;

import org.springframework.stereotype.Component;

@Component
public class HelloServiceImplHystrix implements HelloService {

	@Override
	public String hello(String name) {
		return "Feign访问服务器异常：参数->name="+name;
	}

}
