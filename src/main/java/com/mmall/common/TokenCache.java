package com.mmall.common;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * Created with IntelliJ IDEA
 * Created By lxy
 * 缓存
 * Date: 2018/10/14
 * Time: 18:51
 */
public class TokenCache {
	private static Logger logger = LoggerFactory.getLogger(TokenCache.class);	//日志
	//initialCapacity初始容量，maximumSize最大容量，超过最大容量就使用LRU算法进行移除，expireAfterAccess设置有效时间为12h
	private static LoadingCache<String,String> localCache = CacheBuilder.newBuilder().initialCapacity(1000)
			.maximumSize(10000).expireAfterAccess(12, TimeUnit.HOURS).build(new CacheLoader<String, String>() {
				@Override
				public String load(String s) throws Exception {
					return "null";
				}
			});

	public  static void  setKey(String key,String value){
		localCache.put(key, value);
	}

	public static String getKey(String key){
		String value = null;
		try {
			value = localCache.get(key);
			if("null".equals(value)){
				return null;
			}
			return value;
		}catch (Exception e){
			logger.error("local cache get error",e);
		}
		return null;
	}

}
