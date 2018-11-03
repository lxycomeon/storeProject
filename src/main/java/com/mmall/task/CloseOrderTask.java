package com.mmall.task;

import com.mmall.common.Const;
import com.mmall.common.RedissonManage;
import com.mmall.service.IOrderService;
import com.mmall.util.PropertiesUtil;
import com.mmall.util.RedisPoolUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.redisson.api.RLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import sun.security.krb5.Config;

import javax.annotation.PreDestroy;
import java.util.concurrent.TimeUnit;

/**
 * Created with IntelliJ IDEA By lxy on 2018/11/2
 * 定时关闭订单，并且把库存更新回去
 */
@Component
@Slf4j
public class CloseOrderTask {

	@Autowired
	IOrderService iOrderService;

	@Autowired
	RedissonManage redissonManage;

	//每三分钟的定时任务
	//@Scheduled(cron = "0 0/3 * * * ?")
	void closedNoPayOrder(){
		int hour = Integer.parseInt(PropertiesUtil.getProperty("close.order.time"));
		log.info("关闭未付款订单任务启动V1，不适用分布式tomcat集群");
		iOrderService.closeOrder(hour);
	}

	//V2考虑tomcat执行调度任务时候的分布式锁
	//每三分钟的定时任务
	//@Scheduled(cron = "0 0/3 * * * ?")
	void closedNoPayOrderV2(){
		log.info("关闭未付款订单任务启动V2");
		//分布式锁的时间，默认为5000ms
		long lockTimeout = Long.parseLong(PropertiesUtil.getProperty("lock.timeout","5000"));
		Long setnxResult = RedisPoolUtil.setnx(Const.REDIS_LOCK.CLOSE_OREDER_TASK_LOCK,String.valueOf(System.currentTimeMillis()+lockTimeout));

		if (setnxResult != null && setnxResult.intValue() == 1){
			//获取锁成功，返回值为1
			closeOrder(Const.REDIS_LOCK.CLOSE_OREDER_TASK_LOCK);

		}else {

		}


		int hour = Integer.parseInt(PropertiesUtil.getProperty("close.order.time"));
		iOrderService.closeOrder(hour);
	}
	//V3增加锁的健壮性，防止由于设置了锁，tomcat意外关闭，没有删除锁的key,也没有设置有效期，造成的死锁
	//每三分钟的定时任务
	@Scheduled(cron = "0 0/3 * * * ?")
	void closedNoPayOrderV3(){
		log.info("关闭未付款订单任务启动V3");
		long lockTimeout = Long.parseLong(PropertiesUtil.getProperty("lock.timeout","5000"));
		Long setnxResult = RedisPoolUtil.setnx(Const.REDIS_LOCK.CLOSE_OREDER_TASK_LOCK,String.valueOf(System.currentTimeMillis()+lockTimeout));
		if (setnxResult != null && setnxResult.intValue() == 1){
			closeOrder(Const.REDIS_LOCK.CLOSE_OREDER_TASK_LOCK);
		}else {
			String lockValueStr = RedisPoolUtil.get(Const.REDIS_LOCK.CLOSE_OREDER_TASK_LOCK);
			if (lockValueStr != null && System.currentTimeMillis() > Long.valueOf(lockValueStr) ){
				String getSetResult = RedisPoolUtil.getSet(Const.REDIS_LOCK.CLOSE_OREDER_TASK_LOCK,String.valueOf(System.currentTimeMillis()+lockTimeout));
				//再次用当前时间戳getset，返回一个给定的key的旧值，若旧值没有改变证明此时没有别的tomcat进程进入该锁，也没执行业务
				//此时就可以获取到了锁
				if (getSetResult == null || (getSetResult != null && StringUtils.equals(lockValueStr,getSetResult))) {
					//真正获取到了锁
					closeOrder(Const.REDIS_LOCK.CLOSE_OREDER_TASK_LOCK);
				}else {
					log.info("没有获取到分布式锁：{}",Const.REDIS_LOCK.CLOSE_OREDER_TASK_LOCK);
				}
			}else {
				log.info("没有获取到分布式锁：{}",Const.REDIS_LOCK.CLOSE_OREDER_TASK_LOCK);
			}
		}
	}

	//使用redisson框架实现分布式锁
//	@Scheduled(cron = "0 0/3 * * * ?")
	void closedNoPayOrderV4(){
		log.info("关闭未付款订单任务启动V4");
		long lockTimeout = Long.parseLong(PropertiesUtil.getProperty("lock.timeout","5000"));
		RLock lock = redissonManage.getRedisson().getLock(Const.REDIS_LOCK.CLOSE_OREDER_TASK_LOCK);
		boolean getLock = false;
		try {
			//等待时间waitTime，可以设置为0，也可以根据下面业务的执行时间，不等待，此时获取不到锁就直接下一步（，不然可能发生两边同时拿到分布式锁的情况）（这么长时间没有获得锁就继续执行不等待了）
			// 释放时间（redis中锁key的最长有效期）leaseTime
			if (getLock = lock.tryLock(0,50, TimeUnit.SECONDS)){
				log.info("redisson获取到分布式锁:{},threadName:{}",Const.REDIS_LOCK.CLOSE_OREDER_TASK_LOCK,Thread.currentThread().getName());
				int hour = Integer.parseInt(PropertiesUtil.getProperty("close.order.time"));
				iOrderService.closeOrder(hour);
			}else {
				log.info("redisson没有获取到分布式锁:{},threadName:{}",Const.REDIS_LOCK.CLOSE_OREDER_TASK_LOCK,Thread.currentThread().getName());
			}
		} catch (InterruptedException e) {
			log.error("Redisson分布式锁获取异常",e);
		} finally {
			if (!getLock){
				return;
			}
			//一定别忘记在finally中释放锁
			lock.unlock();	//unlock主动释放锁，50s超时也会自动释放
			log.info("Redisson分布式锁释放");
		}


	}

	private void closeOrder(String lockName){
		RedisPoolUtil.expire(lockName,5);//设置一个超时时间,防止死锁，这里要根据后面执行业务的时间，设置超时时间
		log.info("获取{}，threadName:{}",lockName,Thread.currentThread());
		int hour = Integer.parseInt(PropertiesUtil.getProperty("close.order.time"));
		iOrderService.closeOrder(hour);
		RedisPoolUtil.del(lockName);
		log.info("获取{}，threadName:{}",lockName,Thread.currentThread().getName());
	}

	//该注解就是，使用tomcat的shutdown命令关闭tomcat的时候，关闭之前会调用该注解中的方法执行，
	//以免关闭了tomcat而没有删除分布式锁，造成死锁对于V2版本的closedNoPayOrderV2
	@PreDestroy
	public void delLock(){
		RedisPoolUtil.del(Const.REDIS_LOCK.CLOSE_OREDER_TASK_LOCK);
	}


}


