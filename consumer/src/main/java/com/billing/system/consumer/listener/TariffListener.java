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

    private static final Logger logger = LoggerFactory.getLogger(TariffListener.class);

    @Autowired
    private TariffRepository tariffRepository;
    @Autowired
    private AccountInfoRepository accountInfoRepository;

    @RabbitListener(queues = "spec.tariffChangerQueue")
    public void tariffConsume(final TariffChangerDto tariffChangerDto) {
        logger.warn("A new tariff has been set for phone number: {} ", tariffChangerDto.getPhoneNumber());
        final Tariff tariff = this.tariffRepository
            .findById(tariffChangerDto.getId())
            .orElseThrow(() -> new TariffNotFoundException(tariffChangerDto.getId()));
        final AccountInfo accountInfo = this.accountInfoRepository.findByPhoneNumber(tariffChangerDto.getPhoneNumber());

        accountInfo.setBalance(accountInfo.getBalance() - tariff.getPrice());
        accountInfo.setCall(tariff.getCall());
        accountInfo.setSms(tariff.getSms());
        accountInfo.setInternet(tariff.getInternet());
        accountInfo.setTitle(tariff.getTitle());
        accountInfo.setPrice(tariff.getPrice());

        this.accountInfoRepository.save(accountInfo);
    }
}
