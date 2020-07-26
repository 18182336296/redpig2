package com.redpigmall;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * Title: ClientFeignApplication.java
 * 
 * Description:
 * @EnableDiscoveryClient:发现服务
 * @EnableFeignClients:标识Feign
 * 
 * Copyright: Copyright (c) 2018
 * 
 * Company: RedPigMall
 * 
 * @author redpig
 * 
 * @date 2018年7月10日 上午11:19:56
 * 
 * @version redpigmall_b2b2c v8.0 2018版
 */
@SpringBootApplication(exclude={DataSourceAutoConfiguration.class,HibernateJpaAutoConfiguration.class})
@EnableDiscoveryClient
@EnableFeignClients
public class ClientFeignApplication {
	public static void main(String[] args) {
		SpringApplication.run(ClientFeignApplication.class, args);
	}
}
