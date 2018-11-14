package com.mmall.rabbitmq;

import com.mmall.common.ServerResponse;
import com.mmall.service.IOrderService;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.ChannelAwareMessageListener;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created with IntelliJ IDEA By lxy on 2018/11/11
 */
@Service
@Slf4j
public class MQReceiver implements ChannelAwareMessageListener {

	@Autowired
	IOrderService iOrderService;

	private MessageConverter messageConverter = new Jackson2JsonMessageConverter();

	//消息监听
	@Override
	public void onMessage(Message message, Channel channel) throws Exception {

		log.info("Recive MiaoshaOrder Message:{}",message.getBody().toString());
		MiaoshaMessage miaoshaMessage = (MiaoshaMessage) messageConverter.fromMessage(message);//message.getBody();

		//业务处理,秒杀订单
		ServerResponse response = iOrderService.createMiaoshaOrder(miaoshaMessage.getUserId(),miaoshaMessage.getShippingId(),miaoshaMessage.getMiaoshaProductId());

		System.out.println(response);
		//确认消息回调，不然会一直重传
		channel.basicAck(message.getMessageProperties().getDeliveryTag(), true);
	}


}
