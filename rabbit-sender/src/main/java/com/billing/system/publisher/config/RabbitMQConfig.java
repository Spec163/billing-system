package com.billing.system.publisher.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

	@Value("${example.rabbitmq.billing}")
	String billingQueue;

	@Value("${example.rabbitmq.tariff}")
	String tariffChangerQueue;

	@Value("${example.rabbitmq.balance}")
	String balanceQueue;

	@Value("${example.rabbitmq.exchange}")
	String exchange;

	@Value("${example.rabbitmq.routingKey}")
	private String routingKey;

	@Bean
	Queue billingQueue() {
		return new Queue(billingQueue, false);
	}

	@Bean
	Queue tariffChangerQueue() {
		return new Queue(tariffChangerQueue, false);
	}

	@Bean
	Queue balanceQueue() {
		return new Queue(balanceQueue, false);
	}

	@Bean
	DirectExchange exchange() {
		return new DirectExchange(exchange);
	}

	@Bean
	Binding bindingBillingQueue(@Qualifier("billingQueue") Queue queue, DirectExchange exchange) {
		return BindingBuilder.bind(queue).to(exchange).with(routingKey);
	}

	@Bean
	Binding bindingTariffChangerQueue(@Qualifier("tariffChangerQueue") Queue queue, DirectExchange exchange) {
		return BindingBuilder.bind(queue).to(exchange).with(routingKey);
	}

	@Bean
	Binding bindingBalanceQueue(@Qualifier("balanceQueue") Queue queue, DirectExchange exchange) {
		return BindingBuilder.bind(queue).to(exchange).with(routingKey);
	}

	@Bean
	public MessageConverter jsonMessageConverter() {
		return new Jackson2JsonMessageConverter();
	}

	public AmqpTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
		final RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
		rabbitTemplate.setMessageConverter(jsonMessageConverter());
		return rabbitTemplate;
	}
}
