package com.mmall.util;

import com.mmall.common.RedisPool;
import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Jedis;

/**
 * Created with IntelliJ IDEA By lxy on 2018/10/29
 */
@Slf4j
public class RedisPoolUtil {

	public static String set(String key,String value){
		Jedis jedis = null;
		String result = null;

		try {
			jedis = RedisPool.getJedis();
			result = jedis.set(key,value);
		}catch (Exception e){
			log.error("set key{} value:{} error",key,value,e);
			RedisPool.returnBrokenResource(jedis);
			return result;
		}
		RedisPool.returnResource(jedis);
		return result;
	}

	//setnx，只有set的时候，不存在这个key的话，才会set成功。
	public static Long setnx(String key,String value){
		Jedis jedis = null;
		Long result = null;

		try {
			jedis = RedisPool.getJedis();
			result = jedis.setnx(key,value);
		}catch (Exception e){
			log.error("setnx key{} value:{} error",key,value,e);
			RedisPool.returnBrokenResource(jedis);
			return result;
		}
		RedisPool.returnResource(jedis);
		return result;
	}

	//getset,set新值的同时获取旧值
	public static String getSet(String key,String value){
		Jedis jedis = null;
		String result = null;

		try {
			jedis = RedisPool.getJedis();
			result = jedis.getSet(key,value);
		}catch (Exception e){
			log.error("getset key{} value:{} error",key,value,e);
			RedisPool.returnBrokenResource(jedis);
			return result;
		}
		RedisPool.returnResource(jedis);
		return result;
	}

	//ex单位为s
	public static String setEx(String key,String value,int exTime){
		Jedis jedis = null;
		String result = null;

		try {
			jedis = RedisPool.getJedis();
			result = jedis.setex(key,exTime,value);
		}catch (Exception e){
			log.error("setEx key{} value:{} error",key,value,e);
			RedisPool.returnBrokenResource(jedis);
			return result;
		}
		RedisPool.returnResource(jedis);
		return result;
	}

	//重新设置key的有效期多久
	public static Long expire(String key,int exTime){
		Jedis jedis = null;
		Long result = null;

		try {
			if (key != null){
				jedis = RedisPool.getJedis();
				result = jedis.expire(key,exTime);
			}else {
				return null;
			}
		}catch (Exception e){
			log.error("setExpire key{} value:{} error",key,e);
			RedisPool.returnBrokenResource(jedis);
			return result;
		}
		RedisPool.returnResource(jedis);
		return result;
	}

	public static String get(String key){
		Jedis jedis = null;
		String result = null;
		try {
			if (key != null){
				jedis = RedisPool.getJedis();
				result = jedis.get(key);
			}else {
				return result;
			}
		}catch (Exception e){
			log.error("get key{} error",key,e);
			RedisPool.returnBrokenResource(jedis);
			return result;
		}
		RedisPool.returnResource(jedis);
		return result;
	}

	public static Long del(String key){
		Jedis jedis = null;
		Long result = null;
		try {
			if (key != null){
				jedis = RedisPool.getJedis();
				result = jedis.del(key);
			}else {
				return result;
			}
		}catch (Exception e){
			log.error("del key{} error",key,e);
			RedisPool.returnBrokenResource(jedis);
			return result;
		}
		RedisPool.returnResource(jedis);
		return result;
	}



}
