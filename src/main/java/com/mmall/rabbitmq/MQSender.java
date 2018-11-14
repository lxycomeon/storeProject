package com.mmall.rabbitmq;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created with IntelliJ IDEA By lxy on 2018/11/11
 */
@Service
@Slf4j
public class MQSender {

	@Autowired
	AmqpTemplate amqpTemplate;

	public void sendMiaoshaMessage(MiaoshaMessage mm) {
		log.info("send Miaosha message:userId:{},miaoshaProductId:{}",mm.getUserId(),mm.getMiaoshaProductId());
		amqpTemplate.convertAndSend(MQConfig.MIAOSHA_QUEUE, mm);
	}

}
