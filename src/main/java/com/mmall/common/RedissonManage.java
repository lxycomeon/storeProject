package com.mmall.common;

import com.mmall.util.PropertiesUtil;
import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.config.Config;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * Created with IntelliJ IDEA By lxy on 2018/11/3
 */
@Component
@Slf4j
public class RedissonManage {
	private Config config = new Config();

	private Redisson redisson = null;

	public Redisson getRedisson() {
		return redisson;
	}

	private static String ip1 = PropertiesUtil.getProperty("redis1.ip","127.0.0.1");
	private static Integer port1 = Integer.parseInt(PropertiesUtil.getProperty("redis1.port","6379"));
	private static String redis1Pass = PropertiesUtil.getProperty("redis1.pass",null);

	private static String ip2 = PropertiesUtil.getProperty("redis2.ip");
	private static Integer port2 = Integer.parseInt(PropertiesUtil.getProperty("redis2.port"));
	private static String redis2Pass = PropertiesUtil.getProperty("redis2.pass");

	//与静态块代码一样作用，构造后执行的方法注解
	@PostConstruct
	private void init(){
		try {
			config.useSingleServer().setAddress(new StringBuilder().append(ip1).append(":").append(port1).toString());
			redisson = (Redisson) Redisson.create(config);
			log.info("redisson 初始化结束");
		} catch (Exception e){
			log.error("redisson init error",e);
		}

	}

}
