package com.mmall.common;

import com.mmall.util.PropertiesUtil;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * Created with IntelliJ IDEA By lxy on 2018/10/29
 */
public class RedisPool {
	private static JedisPool pool;
	private static Integer maxTotal = Integer.parseInt(PropertiesUtil.getProperty("redis.max.total","20"));//最大连接数
	private static Integer maxIdle = Integer.parseInt(PropertiesUtil.getProperty("redis.max.idle","10"));
	private static Integer minIdle = Integer.parseInt(PropertiesUtil.getProperty("redis.min.idle","2"));
	private static Boolean testOnBorrow = Boolean.parseBoolean(PropertiesUtil.getProperty("redis.test.borrow","true"));
	private static Boolean testOnReturn = Boolean.parseBoolean(PropertiesUtil.getProperty("redis.test.return","false"));

	private static String ip = PropertiesUtil.getProperty("redis.ip","127.0.0.1");
	private static Integer port = Integer.parseInt(PropertiesUtil.getProperty("redis.port","6379"));
	private static String redisPass = PropertiesUtil.getProperty("redis.pass",null);

	private static void initPoll(){
		JedisPoolConfig config = new JedisPoolConfig();
		config.setMaxIdle(maxIdle);
		config.setMinIdle(minIdle);
		config.setMaxTotal(maxTotal);

		config.setTestOnBorrow(testOnBorrow);
		config.setTestOnReturn(testOnReturn);

		config.setBlockWhenExhausted(true);//连接耗尽时是否阻塞，设置为false时候会抛出异常，true时会阻塞直到超时，默认为true

		pool = new JedisPool(config,ip,port,1000*2,redisPass);
	}
	static {
		initPoll();
	}

	public static Jedis getJedis(){
		return pool.getResource();
	}

	public static void returnResource(Jedis jedis){
		pool.returnResource(jedis);		//源码中已经判断了jedis为null的情况，所以这里不再判断了
	}

	public static void returnBrokenResource(Jedis jedis){
		pool.returnBrokenResource(jedis);		//源码中已经判断了jedis为null的情况，所以这里不再判断了
	}


}
