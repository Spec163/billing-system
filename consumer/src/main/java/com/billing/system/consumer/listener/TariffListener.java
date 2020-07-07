package com.billing.system.consumer.listener;


import com.billing.system.publisher.dto.TariffChangerDto;
import com.billing.system.springsecurityjwt.entity.AccountInfo;
import com.billing.system.springsecurityjwt.repository.AccountInfoRepository;
import com.billing.system.tariff.exceptions.TariffNotFoundException;
import com.billing.system.tariff.model.Tariff;
import com.billing.system.tariff.repository.TariffRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TariffListener {

    private Logger logger = LoggerFactory.getLogger(TariffListener.class);

    @Autowired
    private TariffRepository tariffRepository;
    @Autowired
    private AccountInfoRepository accountInfoRepository;

    @RabbitListener(queues = "spec.tariffChangerQueue")
    public void tariffConsume(TariffChangerDto tariffChangerDto) {
        logger.warn("A new tariff has been set for phone number: {} ", tariffChangerDto.getPhoneNumber());
        Tariff tariff = tariffRepository
                .findById(tariffChangerDto.getId())
                .orElseThrow(() -> new TariffNotFoundException(tariffChangerDto.getId()));
        AccountInfo accountInfo = accountInfoRepository.findByPhoneNumber(tariffChangerDto.getPhoneNumber());

        accountInfo.setBalance(accountInfo.getBalance() - tariff.getPrice());
        accountInfo.setCall(tariff.getCall());
        accountInfo.setSms(tariff.getSms());
        accountInfo.setInternet(tariff.getInternet());
        accountInfo.setTitle(tariff.getTitle());
        accountInfo.setPrice(tariff.getPrice());

        accountInfoRepository.save(accountInfo);
    }
}
