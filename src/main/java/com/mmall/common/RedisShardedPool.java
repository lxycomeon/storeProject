package com.mmall.common;

import com.mmall.util.PropertiesUtil;
import redis.clients.jedis.*;
import redis.clients.util.Hashing;
import redis.clients.util.Sharded;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA By lxy on 2018/10/30
 * 分片的jedis，分布式jedis连接池
 */
public class RedisShardedPool {
	private static ShardedJedisPool pool;
	private static Integer maxTotal = Integer.parseInt(PropertiesUtil.getProperty("redis.max.total","20"));//最大连接数
	private static Integer maxIdle = Integer.parseInt(PropertiesUtil.getProperty("redis.max.idle","10"));
	private static Integer minIdle = Integer.parseInt(PropertiesUtil.getProperty("redis.min.idle","2"));
	private static Boolean testOnBorrow = Boolean.parseBoolean(PropertiesUtil.getProperty("redis.test.borrow","true"));
	private static Boolean testOnReturn = Boolean.parseBoolean(PropertiesUtil.getProperty("redis.test.return","false"));

	private static String ip1 = PropertiesUtil.getProperty("redis1.ip","127.0.0.1");
	private static Integer port1 = Integer.parseInt(PropertiesUtil.getProperty("redis1.port","6379"));
	private static String redis1Pass = PropertiesUtil.getProperty("redis1.pass",null);

	private static String ip2 = PropertiesUtil.getProperty("redis2.ip");
	private static Integer port2 = Integer.parseInt(PropertiesUtil.getProperty("redis2.port"));
	private static String redis2Pass = PropertiesUtil.getProperty("redis2.pass");

	private static void initPoll(){
		JedisPoolConfig config = new JedisPoolConfig();
		config.setMaxIdle(maxIdle);
		config.setMinIdle(minIdle);
		config.setMaxTotal(maxTotal);

		config.setTestOnBorrow(testOnBorrow);
		config.setTestOnReturn(testOnReturn);

		config.setBlockWhenExhausted(true);//连接耗尽时是否阻塞，设置为false时候会抛出异常，true时会阻塞直到超时，默认为true

		JedisShardInfo info1 = new JedisShardInfo(ip1,port1,1000*2);
		//info1.setPassword(redis1Pass);
		JedisShardInfo info2 = new JedisShardInfo(ip2,port2,1000*2);
		//info2.setPassword(redis2Pass);

		List<JedisShardInfo> jedisShardInfoList = new ArrayList<JedisShardInfo>(2);
		jedisShardInfoList.add(info1);
		jedisShardInfoList.add(info2);

		pool = new ShardedJedisPool(config,jedisShardInfoList, Hashing.MURMUR_HASH, Sharded.DEFAULT_KEY_TAG_PATTERN);

	}
	static {
		initPoll();
	}

	public static ShardedJedis getJedis(){
		return pool.getResource();
	}

	public static void returnResource(ShardedJedis jedis){
		pool.returnResource(jedis);		//源码中已经判断了jedis为null的情况，所以这里不再判断了
	}

	public static void returnBrokenResource(ShardedJedis jedis){
		pool.returnBrokenResource(jedis);		//源码中已经判断了jedis为null的情况，所以这里不再判断了
	}

	public static void main(String[] args) {
		ShardedJedis jedis = pool.getResource();

		for (int i = 0;i<100;i++){
			jedis.set("key"+i,"value"+i);
		}
		for (int i = 0;i<100;i++){
			System.out.println(jedis.get("key"+i));
		}




		returnResource(jedis);
		//pool.destroy();
	}


}
