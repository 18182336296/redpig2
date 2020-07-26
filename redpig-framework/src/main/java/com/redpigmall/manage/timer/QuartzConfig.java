package com.redpigmall.manage.timer;

import com.redpigmall.logic.service.RedPigActivityQuartzService;
import com.redpigmall.logic.service.RedPigDistributionQuartzService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

import com.redpigmall.logic.service.RedPigLuceneClusterService;

/**
 * 
 * Title: QuartzConfig.java
 * 
 * Description: 定时器
 * 
 * Copyright: Copyright (c) 2018
 * 
 * Company: RedPigMall
 * 
 * @author redpig
 * 
 * @date 2018年8月10日 下午4:49:20
 * 
 * @version redpigmall_b2b2c v8.0 2018版  
 */
@Configuration
public class QuartzConfig {
	@Autowired
	private RedPigLuceneClusterService quartzService;

	@Autowired
	private RedPigActivityQuartzService activityQuartzService;

	private Logger logger = LoggerFactory.getLogger(QuartzConfig.class);
	/**
	 * timer:5分钟执行一次. <br/>
	 * 
	 * @author redpig
	 * @since JDK 1.8
	 */
	@Scheduled(cron = "0 0/5 * * * ?")
    public void timer(){
		System.out.println("spring....timer.....");
		quartzService.updateLuceneCluster();

    }

	/**
	 * 每隔2s钟查看有没有商品到了秒杀时间，如果到了，则设置商品价格为秒杀价；
	 */
	@Scheduled(cron = "*/2 * * * * *")
	public void updateActivity(){
		System.out.println("activity....timer.....");

		activityQuartzService.updateNuke();
		activityQuartzService.updateCollage();
		try{
			activityQuartzService.updateTimeoutNukeOrders();
			activityQuartzService.updateTimeoutCollageOrders();
		}catch (Exception e){
			e.printStackTrace();
			logger.error("取消超时活动订单失败！");
		}
	}
}





















