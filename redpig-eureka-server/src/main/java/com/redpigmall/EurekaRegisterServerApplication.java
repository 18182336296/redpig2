package com.redpigmall;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

/**
 * 
 * 
 * Title: EurekaRegisterServerApplication.java
 * 
 * Description:@EurekaRegisterServerApplication标注为eureka注册中
 * 
 * Copyright: Copyright (c) 2018
 * 
 * Company: RedPigMall
 * 
 * @author redpig
 * 
 * @date 2018年7月10日 上午10:48:29
 * 
 * @version redpigmall_b2b2c v8.0 2018版
 */
@SpringBootApplication(exclude={DataSourceAutoConfiguration.class,HibernateJpaAutoConfiguration.class})
@EnableEurekaServer
public class EurekaRegisterServerApplication {
	public static void main(String[] args) {
		SpringApplication.run(EurekaRegisterServerApplication.class, args);
	}
}
