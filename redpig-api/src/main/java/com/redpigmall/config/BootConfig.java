package com.redpigmall.config;

import java.util.Properties;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.ui.velocity.VelocityEngineFactoryBean;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.view.velocity.VelocityConfigurer;
import org.springframework.web.servlet.view.velocity.VelocityView;
import org.springframework.web.servlet.view.velocity.VelocityViewResolver;

/**
 * 
 * Title: BootConfig.java
 * 
 * Description:
 * 
 * Copyright: Copyright (c) 2018
 * 
 * Company: RedPigMall
 * 
 * @author redpig
 * 
 * @date 2018年5月30日 上午12:35:16
 * 
 * @version redpigmall_b2b2c v8.0 2018版  
 */
@SuppressWarnings("deprecation")
@Configuration
public class BootConfig {

	//文件上传设置
	@Bean
	public CommonsMultipartResolver multipartResolver(){
		
		CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver();
		//文件上传最大为10M
		multipartResolver.setMaxUploadSize(10485760);
		//maxUploadSize：设置允许上传的最大文件大小，以字节为单位计算。当设为-1时表示无限制，默认是-1。 
		//maxInMemorySize：设置在文件上传时允许写到内存中的最大值，以字节为单位计算，默认是10240
		multipartResolver.setMaxInMemorySize(10485760);
		
		
		return multipartResolver;
		
	}
	
	@Bean
	public VelocityConfigurer velocityCongfig() {
		
		VelocityConfigurer velocityCongfig = new VelocityConfigurer();
		velocityCongfig.setResourceLoaderPath("/");
		Properties velocityProperties = new Properties();
		velocityProperties.setProperty("input.encoding", "UTF-8");
		velocityProperties.setProperty("output.encoding", "UTF-8");
		velocityProperties.setProperty("directive.set.null.allowed", "true");
		velocityProperties.setProperty("velocimacro.library.autoreload", "false");
		//开发需要设置为false
		velocityProperties.setProperty("file.resource.loader.cache", "false");
		//load的间隔时间：其实若无动态修改的需求, 此处可改为-1，即只在启动时load一次, 此后不再load
		velocityProperties.setProperty("file.resource.loader.modificationCheckInterval", "-1");
		
		//resource.manager.defaultcache.size=0表示不限制cache大小
		velocityProperties.setProperty("resource.manager.defaultcache.size", "0");
		
		//自定义标签多个用,分割
//		velocityProperties.setProperty("userdirective", "com.redpigmall.api.cache.velocity.CacheDirective");
		velocityProperties.setProperty("userdirective.cache.cachetime", "1");
		velocityProperties.setProperty("userdirective.cache.region", "redpigmall_velocity");
		velocityProperties.setProperty("userdirective.cache.flush", "true");
		
		velocityCongfig.setVelocityProperties(velocityProperties);
		
		return velocityCongfig;
	}
	
	

	@Bean
	public VelocityViewResolver viewResolver() {
		
		VelocityViewResolver viewResolver = new VelocityViewResolver();
		
		viewResolver.setViewClass(VelocityView.class);
		
		viewResolver.setContentType("text/html;charset=UTF-8");
		
		return viewResolver;
	}
	
	@Bean
	public VelocityEngineFactoryBean velocityEngine(){
		VelocityEngineFactoryBean velocityEngine = new VelocityEngineFactoryBean();
		velocityEngine.setResourceLoaderPath("/velocity/");
		
		return velocityEngine;
	}
	
	
}

