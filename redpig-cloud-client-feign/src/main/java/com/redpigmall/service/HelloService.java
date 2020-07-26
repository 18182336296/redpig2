package com.redpigmall.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 
 * 
 * Title: HelloService.java
 * 
 * Description:
 * @FeignClient(value="SERVICE-PROVIDER")
 * 这里同样需要大写
 * Feign不需要添加断路器依赖，Feign自身就带有断路器的会掉fallback
 * 
 * Copyright: Copyright (c) 2018
 * 
 * Company: RedPigMall
 * 
 * @author redpig
 * 
 * @date 2018年7月10日 上午11:22:05
 * 
 * @version redpigmall_b2b2c v8.0 2018版
 */
@FeignClient(value="REDPIG-CLOUD-PROVIDER",fallback=HelloServiceImplHystrix.class)
public interface HelloService {
	
	@RequestMapping(value="/prvider",method=RequestMethod.GET)
	String hello(@RequestParam(value="name") String name);
	
}
