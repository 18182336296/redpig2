package com.redpigmall.test;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.redpigmall.domain.SysConfig;
import com.redpigmall.service.RedPigSysConfigService;

public class UserServiceTest extends BaseJunitTest {
	
	
	@Autowired
	private RedPigSysConfigService sysConfigService;
	
	@Test  
    public void test0(){
		SysConfig sysConfig = sysConfigService.selectByPrimaryKey(1L);
		
		System.out.println(sysConfig);
		
    }  
	
}
