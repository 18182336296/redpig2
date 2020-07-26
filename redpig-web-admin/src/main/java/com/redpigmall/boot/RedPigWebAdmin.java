package com.redpigmall.boot;

import com.google.common.collect.Sets;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.server.*;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.util.WebAppRootListener;

import java.util.Set;

/**
 * Title: RedPigWebAdmin.java
 * 
 * Description:RedPigWebAdmin
 * 
 * Copyright: Copyright (c) 2018
 * 
 * Company: RedPigMall
 * 
 * @author redpig
 * 
 * @date 2018年7月13日 下午5:45:52
 * 
 * @version redpigmall_b2b2c v8.0 2018版
 */
@ServletComponentScan(basePackages={"com.redpigmall"})
@ComponentScan(basePackages={"com.redpigmall"})
@MapperScan("com.redpigmall.dao")
@EnableAutoConfiguration
@EnableScheduling
@EnableTransactionManagement
public class RedPigWebAdmin extends SpringBootServletInitializer  {
	
	/**
	 * 
	 * 配置tomcat启动SpringBoot
	 * @see org.springframework.boot.web.servlet.support.SpringBootServletInitializer#configure(org.springframework.boot.builder.SpringApplicationBuilder)
	 */
	@Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(RedPigWebAdmin.class);
    }
	/**
	 * 
	 * main:
	 *
	 * @author redpig
	 * @param args
	 * @since JDK 1.8
	 */
	public static void main(String[] args) {
		SpringApplication.run(RedPigWebAdmin.class, args);
	}

	/**
	 * 配置spring上下文
	 * 
	 * @author redpig
	 * @return
	 * @since JDK 1.8
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Bean
	public ServletRegistrationBean dispatcherRegistration() {
		//注解扫描上下文
		AnnotationConfigWebApplicationContext applicationContext = new AnnotationConfigWebApplicationContext();

		//通过构造函数指定dispatcherServlet的上下文
		DispatcherServlet rest_dispatcherServlet = new DispatcherServlet(applicationContext);

		//用ServletRegistrationBean包装servlet
		ServletRegistrationBean registrationBean = new ServletRegistrationBean(rest_dispatcherServlet);
		registrationBean.setLoadOnStartup(1);
		//指定urlmapping
		registrationBean.addUrlMappings("/");
		//指定name，如果不指定默认为dispatcherServlet
		registrationBean.setName("redpigmall");
		return registrationBean;


	}
	@Bean
	public WebAppRootListener webAppRootListener() {
		return new WebAppRootListener();
	}

	@SuppressWarnings("rawtypes")
	@Bean
	public WebServerFactoryCustomizer containerCustomizer() {
		return new WebServerFactoryCustomizer() {

			@Override
			public void customize(WebServerFactory factory) {
				
				ConfigurableWebServerFactory f = (ConfigurableWebServerFactory)factory;
				ErrorPage error404Page = new ErrorPage(HttpStatus.NOT_FOUND, "/404");
				ErrorPage error500Page = new ErrorPage(HttpStatus.INTERNAL_SERVER_ERROR, "/500");

				Set<ErrorPage> errorPages = Sets.newHashSet();
				errorPages.add(error404Page);
				errorPages.add(error500Page);
				
				f.setErrorPages(errorPages);
				
				
				Compression compression = new Compression();
				String[] mimeTypes = {"application/msword","application/msexcel",
						"application/pdf","application/zip",
						"application/rar","application/txt",
						"application/mshelp","text/html;charset=utf-8",
						"application/vnd.iphone","application/vnd.android.package-archive"};
				compression.setMimeTypes(mimeTypes);
				
				f.setCompression(compression);
				
			}};
	}

}
