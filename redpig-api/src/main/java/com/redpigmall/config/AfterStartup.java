package com.redpigmall.config;

import java.io.File;
import java.util.Properties;

import org.apache.velocity.app.Velocity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;

import com.redpigmall.api.constant.Globals;
import com.redpigmall.redis.RedisCache;
import com.redpigmall.service.RedPigSysConfigService;

/**
 * 
 * Title: AfterStartup.java
 * 
 * Description:项目启动后执行一些操作
 * 
 * Copyright: Copyright (c) 2018
 * 
 * Company: RedPigMall
 * 
 * @author redpig
 * 
 * @date 2018年7月16日 下午3:57:36
 * 
 * @version redpigmall_b2b2c v8.0 2018版
 */
@Configuration
public class AfterStartup implements ApplicationListener<ContextRefreshedEvent> {
	
	@Autowired
	private RedPigSysConfigService sysConfigService;
	
	@Override
	public void onApplicationEvent(ContextRefreshedEvent evt) {
		// 防止启动两次

        if (evt.getApplicationContext().getParent() != null) {
        	try {
    			Properties prop = new Properties();
    			
    			prop.load(Globals.class.getResourceAsStream("/velocity.properties"));
    			// 指定模板文件存放位置
    			prop.put("file.resource.loader.path",sysConfigService.getSysConfig().getVm_folder() +  File.separator+"vm");
    			
    			Velocity.init(prop);
    			
    			RedisCache.getJedis();//让redis在项目启动后获取连接
    			
    		} catch (Exception e) {
    			e.printStackTrace();
    		}
        	
        	
        	
        }
	}

}
