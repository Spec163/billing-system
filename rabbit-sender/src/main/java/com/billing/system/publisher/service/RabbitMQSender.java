package com.billing.system.publisher.service;

import com.billing.system.publisher.dto.BalanceChangerDto;
import com.billing.system.publisher.dto.TariffChangerDto;
import com.billing.system.publisher.model.OrderInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class RabbitMQSender {

	Logger logger = LoggerFactory.getLogger(RabbitMQSender.class);
	
	private final AmqpTemplate amqpTemplate;
	
	@Value("${example.rabbitmq.exchange}")
	private String exchange;
	
	@Value("${example.rabbitmq.routingKey}")
	private String routingKey;

	@Value("${example.rabbitmq.billing}")
	private String billingQueue;

	@Value("${example.rabbitmq.tariff}")
	private String tariffChangerQueue;

	@Value("${example.rabbitmq.balance}")
	private String balanceQueue;

	@Autowired
	public RabbitMQSender(AmqpTemplate amqpTemplate) {
		this.amqpTemplate = amqpTemplate;
	}

	public void sendOrder(OrderInfo orderInfo) {
		amqpTemplate.convertAndSend(billingQueue, orderInfo);
		logger.warn("Send msg to billingQueue = {}", orderInfo);
	}

	public void sendTariff(TariffChangerDto tariffChangerDto) {
		amqpTemplate.convertAndSend(tariffChangerQueue, tariffChangerDto);
		logger.warn("Send msg to tariffChangerQueue phone number = {}", tariffChangerDto);
	}

	public void changeBalance(BalanceChangerDto balanceChangerDto) {
		amqpTemplate.convertAndSend(balanceQueue, balanceChangerDto);
		logger.warn("Attempt to replenish the balance of = {} rub", balanceChangerDto.getMoney());
	}

}