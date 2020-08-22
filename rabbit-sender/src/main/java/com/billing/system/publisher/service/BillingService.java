package com.billing.system.publisher.service;

import javax.servlet.http.HttpServletRequest;
import com.billing.system.publisher.dto.BalanceChangerDto;
import com.billing.system.publisher.dto.MaxServiceCost;
import com.billing.system.publisher.dto.TariffChangerDto;
import com.billing.system.publisher.exceptions.DefaultPriceNotFoundException;
import com.billing.system.publisher.model.DefaultPrice;
import com.billing.system.publisher.model.OrderInfo;
import com.billing.system.publisher.repository.DefaultPriceRepository;
import com.billing.system.springsecurityjwt.entity.AccountInfo;
import com.billing.system.springsecurityjwt.service.AccountService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class BillingService {

    private static final Logger logger = LoggerFactory.getLogger(BillingService.class);

    private final DefaultPriceRepository defaultPriceRepository;
    private final RabbitMQSender rabbitMQSender;
    private final AccountService accountService;

    @Autowired
    public BillingService(
        final RabbitMQSender rabbitMQSender,
        final AccountService accountService,
        final DefaultPriceRepository defaultPriceRepository
    ) {
        this.rabbitMQSender = rabbitMQSender;
        this.accountService = accountService;
        this.defaultPriceRepository = defaultPriceRepository;
    }

    public void billingProducer(
        final OrderInfo orderInfo,
        final HttpServletRequest request
    ) throws Exception {

        if (orderInfo.getServiceId() == 0) throw new Exception("BAD REQUEST !!!");

        if (orderInfo.getPhoneNumber() == null) {
            orderInfo.setPhoneNumber(this.accountService.getPhoneNumberFromRequest(request));
        } else if (this.accountService.findAccountByPhoneNumber(orderInfo.getPhoneNumber()) == null) {
            orderInfo.setPhoneNumber(this.accountService.getPhoneNumberFromRequest(request));
        }

        this.rabbitMQSender.sendOrder(orderInfo);
    }

    public ResponseEntity<String> changeUserTariff(
        final TariffChangerDto tariffChangerDto,
        final HttpServletRequest request
    ) {
        if (tariffChangerDto.getId() != null && tariffChangerDto.getId() > 0) {
            // Изменять тарифф пользователя может только сам пользователь
            tariffChangerDto.setPhoneNumber(this.accountService.getPhoneNumberFromRequest(request));
            this.rabbitMQSender.sendTariff(tariffChangerDto);
            logger.warn("User tariff was changed");
            return ResponseEntity.ok("Your tariff has been changed");
        } else {
            logger.error("Tariff ID Error");
            return ResponseEntity.status(400).body("Incorrectly tariff");
        }
    }

    public ResponseEntity<String> replenishUserBalance(
        final BalanceChangerDto balanceChangerDto,
        final HttpServletRequest request
    ) {
        String phoneNumber = balanceChangerDto.getPhoneNumber();
        if (balanceChangerDto.getPhoneNumber() == null) {
            phoneNumber = this.accountService.getPhoneNumberFromRequest(request);
        } else if (this.accountService.findAccountByPhoneNumber(balanceChangerDto.getPhoneNumber()) == null) {
            phoneNumber = this.accountService.getPhoneNumberFromRequest(request); // сомнительное действие !!!
        }

        balanceChangerDto.setPhoneNumber(phoneNumber);

        // проверки на валидность номера не нужны, тк он будет браться из токена
        if (balanceChangerDto.getMoney() < 1 || balanceChangerDto.getMoney() == null) {
            logger.error("Incorrect amount: {}", balanceChangerDto.getMoney());
            return ResponseEntity.status(403).body("Incorrect amount");
        }
        this.rabbitMQSender.changeBalance(balanceChangerDto);
        return ResponseEntity.ok("Your balance has been replenished!");
    }

    public MaxServiceCost calculateMaximumExpenses(
        final MaxServiceCost maxServiceCost,
        final HttpServletRequest request
    ) {
        final DefaultPrice defaultPrice = this.defaultPriceRepository
            .findById(1L).orElseThrow(() -> new DefaultPriceNotFoundException(1L));
        AccountInfo accountInfo = this.accountService.findAccountByPhoneNumber(maxServiceCost.getPhoneNumber());

        if (accountInfo == null) {
            maxServiceCost.setPhoneNumber(this.accountService.getPhoneNumberFromRequest(request));
            accountInfo = this.accountService.findAccountByPhoneNumber(maxServiceCost.getPhoneNumber());
        }

        maxServiceCost.setMaxCall(accountInfo.getCall()
            + accountInfo.getBalance() / defaultPrice.getCallCost());
        maxServiceCost.setMaxSms(accountInfo.getSms()
            + accountInfo.getBalance() / defaultPrice.getSmsCost());
        maxServiceCost.setMaxInternet(accountInfo.getInternet()
            + accountInfo.getBalance() / defaultPrice.getInternetCost() * 100);

        return maxServiceCost;
    }
}
