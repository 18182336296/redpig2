package com.redpigmall.test;

import com.redpigmall.redis.RedisCache;

import redis.clients.jedis.Jedis;

/**
 * 
 * Title: TestRedisCache.java
 * 
 * Description:
 * 
 * Copyright: Copyright (c) 2018
 * 
 * Company: RedPigMall
 * 
 * @author redpig
 * 
 * @date 2018年7月29日 下午9:28:47
 * 
 * @version redpigmall_b2b2c v8.0 2018版  
 */
public class TestRedisCache {
	public static void main(String[] args) {
		Jedis jedis = RedisCache.getJedis();
		System.out.println(jedis);
	}
}

