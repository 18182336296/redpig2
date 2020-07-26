package com.redpigmall;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

/**
 * 
 * 
 * Title: RedpigCloudProviderApplication.java
 * 
 * Description:无论是服务提供方还是服务调用方、相对于注册中心都是客户端
 * 
 * Copyright: Copyright (c) 2018
 * 
 * Company: RedPigMall
 * 
 * @author redpig
 * 
 * @date 2018年7月10日 上午11:15:14
 * 
 * @version redpigmall_b2b2c v8.0 2018版
 */
@SpringBootApplication(exclude={DataSourceAutoConfiguration.class,HibernateJpaAutoConfiguration.class})
@EnableEurekaClient
public class RedpigCloudProviderApplication {
	public static void main(String[] args) {
		SpringApplication.run(RedpigCloudProviderApplication.class, args);
	}
}
