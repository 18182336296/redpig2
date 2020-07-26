package com.redpigmall.boot;

import java.util.Set;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.server.Compression;
import org.springframework.boot.web.server.ConfigurableWebServerFactory;
import org.springframework.boot.web.server.ErrorPage;
import org.springframework.boot.web.server.WebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.util.WebAppRootListener;

import com.google.common.collect.Sets;

/**
 * Title: RedPigWebWap.java
 * 
 * Description:H5启动类
 * 
 * Copyright: Copyright (c) 2018
 * 
 * Company: RedPigMall
 * 
 * @author redpig
 * 
 * @date 2018年7月20日 上午11:32:47
 * 
 * @version redpigmall_b2b2c v8.0 2018版
 */
@ServletComponentScan(basePackages={"com.redpigmall"})
@ComponentScan({
	"com.redpigmall"})
@MapperScan("com.redpigmall.dao")
@EnableAutoConfiguration
@EnableTransactionManagement
@SuppressWarnings({ "rawtypes", "unchecked" })
public class RedPigWebWap extends SpringBootServletInitializer  {
	
	@Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(RedPigWebWap.class);
    }
	
	public static void main(String[] args) {
		SpringApplication.run(RedPigWebWap.class, args);
	}

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
