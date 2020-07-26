package com.redpigmall.redis;

import java.io.IOException;
import java.util.Properties;

import com.redpigmall.api.constant.Globals;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * 
 * <p>
 * Title: RedisCache.java
 * </p>
 * 
 * <p>
 * Description: 缓存管理器
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2018
 * </p>
 * 
 * <p>
 * Company: www.redpigmall.com
 * </p>
 * 
 * @author redpigmall
 * 
 * @date 2018年3月7日 上午6:05:38
 * 
 * @version redpigmall V3.0
 */
public class RedisCache  {
	
	private static Properties pps = new Properties();
	
	private static JedisPool jedisPool;
	
	public static JedisPool getJedisPool() {
		return RedisCache.jedisPool;
	}

	public static void setJedisPool(JedisPool jedisPool) {
		RedisCache.jedisPool = jedisPool;
	}

	/**
	 * 清空所有缓存
	 */
	public static void clear()  {
		getJedis().flushDB();
	}

	public static void putObject(Object key, Object value) {

		Jedis jedis = getJedis();

		jedis.set(SerializeUtil.serialize(key), SerializeUtil.serialize(value));
		
	}

	public static Object getObject(Object key) {

		byte[] bytes = SerializeUtil.serialize(key);
		byte[] value = getJedis().get(bytes);
		if (value == null) {
			return null;
		}
		return SerializeUtil.deserialize(value);
	}

	public static Object removeObject(Object key) {
		Jedis jedis = getJedis();

		byte[] bytes = jedis.get(SerializeUtil.serialize(key));

		jedis.del(SerializeUtil.serialize(key));

		return SerializeUtil.deserialize(bytes);
	}

	public static int getSize() {
		Long size = getJedis().dbSize();
		return size.intValue();
	}
	
	public static Jedis getJedis(){
		Jedis jedis = null;
		if(jedisPool == null){
			try {
				pps.load(Globals.class.getClassLoader().getResourceAsStream("config/redis.properties"));
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			System.out.println("redis.hostName:"+pps.getProperty("redis.hostName"));
			System.out.println("redis.port:"+pps.getProperty("redis.port"));
			System.out.println("redis.timeout:"+pps.getProperty("redis.timeout"));
			System.out.println("redis.password:"+pps.getProperty("redis.password"));
			
			JedisPoolConfig poolConfig = new JedisPoolConfig();
			poolConfig.setMaxIdle(Integer.valueOf(pps.getProperty("redis.maxIdle")));
			poolConfig.setTestOnBorrow(Boolean.valueOf(pps.getProperty("redis.testOnBorrow")));
			poolConfig.setMaxTotal(Integer.valueOf(pps.getProperty("redis.maxTotal")));
			
			JedisPool jp = new JedisPool(poolConfig, pps.getProperty("redis.hostName"), Integer.valueOf(pps.getProperty("redis.port")), Integer.valueOf(pps.getProperty("redis.timeout")), pps.getProperty("redis.password"));
			
			
			jedis =jp.getResource();
			jedisPool = jp;
		}else{
			jedis = jedisPool.getResource();
		}
		
		return jedis;
	}

}
