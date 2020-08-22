package com.billing.system.publisher.controller;

import javax.servlet.http.HttpServletRequest;
import com.billing.system.publisher.dto.BalanceChangerDto;
import com.billing.system.publisher.dto.MaxServiceCost;
import com.billing.system.publisher.dto.TariffChangerDto;
import com.billing.system.publisher.model.OrderInfo;
import com.billing.system.publisher.service.BillingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping(value = "rabbitmq")
@EnableRabbit
@CrossOrigin
public class RabbitMQWebController {
    private static final Logger logger = LoggerFactory.getLogger(RabbitMQWebController.class);

    private final BillingService billingService;

    @Autowired
    public RabbitMQWebController(final BillingService billingService) {
        this.billingService = billingService;
    }


    @PostMapping(value = "billing")
    public void producer(
        @RequestBody final OrderInfo orderInfo,
        final HttpServletRequest request
    ) throws Exception {
        this.billingService.billingProducer(orderInfo, request);
    }

    @PostMapping(value = "change-tariff")
    public ResponseEntity<String> changeTariff(
        @RequestBody final TariffChangerDto tariffChangerDto,
        final HttpServletRequest request
    ) {
        return this.billingService.changeUserTariff(tariffChangerDto, request);
    }

    @PostMapping(value = "replenish")
    public ResponseEntity<String> replenishBalance(
        @RequestBody final BalanceChangerDto balanceChangerDto,
        final HttpServletRequest request
    ) {
        return this.billingService.replenishUserBalance(balanceChangerDto, request);
    }


    // Рассчёт максимальновозможного расходы каждой из услуг (остатки тарифа + баланс)
    @PostMapping(value = "expenses")
    public MaxServiceCost calculateMaximumExpenses(
        @RequestBody final MaxServiceCost maxServiceCost,
        final HttpServletRequest request
    ) {
        return this.billingService.calculateMaximumExpenses(maxServiceCost, request);
    }

}

