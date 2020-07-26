package com.redpigmall.test;

import org.junit.Test;

import com.redpigmall.redis.RedisCache;

public class UserServiceTest extends BaseJunitTest {
	
	
	@Test  
    public void test0(){
		for(int i=0;i<2000;i++){
			System.out.println(RedisCache.getJedis());
		}
			
		
    }  
	
}
