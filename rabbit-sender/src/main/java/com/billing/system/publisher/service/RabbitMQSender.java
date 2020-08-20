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

    private static final Logger logger = LoggerFactory.getLogger(RabbitMQSender.class);

    private final AmqpTemplate amqpTemplate;

    @Autowired
    public RabbitMQSender(final AmqpTemplate amqpTemplate) {
        this.amqpTemplate = amqpTemplate;
    }

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


    public void sendOrder(final OrderInfo orderInfo) {
        this.amqpTemplate.convertAndSend(this.billingQueue, orderInfo);
        logger.warn("Send msg to billingQueue = {}", orderInfo);
    }

    public void sendTariff(final TariffChangerDto tariffChangerDto) {
        this.amqpTemplate.convertAndSend(this.tariffChangerQueue, tariffChangerDto);
        logger.warn("Send msg to tariffChangerQueue phone number = {}", tariffChangerDto);
    }

    public void changeBalance(final BalanceChangerDto balanceChangerDto) {
        this.amqpTemplate.convertAndSend(this.balanceQueue, balanceChangerDto);
        logger.warn("Attempt to replenish the balance of = {} rub", balanceChangerDto.getMoney());
    }

}