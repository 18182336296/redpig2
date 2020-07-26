package com.redpigmall.manage.timer;

import com.redpigmall.logic.service.RedPigDistributionQuartzService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * 此功能作为统计分销商用
 * @Auther: csh
 * @Date: 2018/9/3 11:32
 * @Description:
 */
@Configuration
public class CountDistribution {

    @Autowired
    private RedPigDistributionQuartzService quartzService;

    /**
     * 每月1号0时0分10秒
     */
    /*@Scheduled(cron = "10 0 0 1 * ?")*/
    @Scheduled(cron = "30 * * * * ?")
    public void countDisribution(){
        quartzService.countDistribution();
    }


}
