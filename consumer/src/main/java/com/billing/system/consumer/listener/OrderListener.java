package com.billing.system.consumer.listener;

import com.billing.system.publisher.exceptions.DefaultPriceNotFoundException;
import com.billing.system.publisher.model.DefaultPrice;
import com.billing.system.consumer.model.ServiceInfo;
import com.billing.system.publisher.repository.DefaultPriceRepository;
import com.billing.system.consumer.repository.ServiceInfoRepository;
import com.billing.system.publisher.model.OrderInfo;
import com.billing.system.publisher.repository.OrderInfoRepository;
import com.billing.system.springsecurityjwt.entity.AccountInfo;
import com.billing.system.springsecurityjwt.exception.ServiceNotFoundException;
import com.billing.system.springsecurityjwt.repository.AccountInfoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class OrderListener {

    private Logger logger = LoggerFactory.getLogger(OrderListener.class);

    @Autowired
    private AccountInfoRepository accountInfoRepository;
    @Autowired
    private ServiceInfoRepository serviceInfoRepository;
    @Autowired
    private OrderInfoRepository orderInfoRepository;
    @Autowired
    private DefaultPriceRepository defaultPriceRepository;

    // СДЕЛАТЬ ОБРАБОТКУ ОШИБОК
    // как-то надо словить ошибку, если ID услуги не найден
    @RabbitListener(queues = "spec.billingQueue")
    public void billingConsume(OrderInfo orderInfo) {
        logger.warn("Information about the service provided to {} RECEIVED", orderInfo.getPhoneNumber());
        ServiceInfo serviceInfo = serviceInfoRepository
                .findById(orderInfo.getServiceId())
                .orElseThrow(() -> new ServiceNotFoundException(orderInfo.getServiceId()));
        String service = serviceInfo.getService();
        AccountInfo accountInfo = accountInfoRepository.findByPhoneNumber(orderInfo.getPhoneNumber());
        Long difference = 0L;
        DefaultPrice defaultPrice = defaultPriceRepository
                .findById(1L).orElseThrow(() -> new DefaultPriceNotFoundException(1L));
        switch (service){
            case "call":
                difference = accountInfo.getCall() - orderInfo.getExpenses();
                if (difference > -1 || difference == null) {
                    accountInfo.setCall(difference);
                } else {
                    accountInfo.setCall(0L);
                    accountInfo.setBalance(accountInfo
                            .getBalance() - (-difference * defaultPrice.getCallCost()));
                }
                break;
            case "sms":
                difference = accountInfo.getSms() - orderInfo.getExpenses();
                if (difference > -1 || difference == null) {
                    accountInfo.setSms(difference);
                } else {
                    accountInfo.setSms(0L);
                    accountInfo.setBalance(accountInfo
                            .getBalance() - (-difference * defaultPrice.getSmsCost()));
                }
                break;
            case "internet":
                difference = accountInfo.getInternet() - orderInfo.getExpenses();
                if (difference > -1 || difference == null) {
                    accountInfo.setInternet(difference);
                } else {
                    accountInfo.setInternet(0L);
                    Long consumption = (accountInfo
                            .getBalance() - (-difference * defaultPrice.getInternetCost() / 100));
                    accountInfo.setBalance(consumption);
                }
                break;
            default:
                throw new ServiceNotFoundException(orderInfo.getId());
        }
        accountInfoRepository.save(accountInfo);
        orderInfoRepository.save(orderInfo);

        logger.warn("{} --- ", serviceInfo);
    }
}
