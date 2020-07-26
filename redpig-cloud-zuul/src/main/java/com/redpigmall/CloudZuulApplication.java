package com.redpigmall;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;

/**
 * 
 * 
 * Title: CloudZuulApplication.java
 * 
 * Description:路由器： @EnableZuulProxy标识为路由器
 * 
 * Copyright: Copyright (c) 2018
 * 
 * Company: RedPigMall
 * 
 * @author redpig
 * 
 * @date 2018年7月10日 上午11:11:05
 * 
 * @version redpigmall_b2b2c v8.0 2018版
 */
@SpringBootApplication(exclude={DataSourceAutoConfiguration.class,HibernateJpaAutoConfiguration.class})
@EnableEurekaClient
@EnableZuulProxy
public class CloudZuulApplication {
	public static void main(String[] args) {
		SpringApplication.run(CloudZuulApplication.class, args);
	}
}
