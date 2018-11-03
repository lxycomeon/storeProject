package com.mmall.task;

import com.mmall.service.IOrderService;
import com.mmall.util.PropertiesUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Created with IntelliJ IDEA By lxy on 2018/11/2
 * 定时关闭订单，并且把库存更新回去
 */
@Component
@Slf4j
public class CloseOrderTask {

	@Autowired
	IOrderService iOrderService;

	//每三分钟的定时任务
	@Scheduled(cron = "0 0/3 * * * ?")
	void closedNoPayOrder(){
		int hour = Integer.parseInt(PropertiesUtil.getProperty("close.order.time"));
		log.info("关闭未付款订单任务启动");
		iOrderService.closeOrder(hour);
	}


}
