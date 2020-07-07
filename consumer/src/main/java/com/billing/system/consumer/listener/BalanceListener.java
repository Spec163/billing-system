package com.billing.system.consumer.listener;

import com.billing.system.publisher.dto.BalanceChangerDto;
import com.billing.system.springsecurityjwt.entity.AccountInfo;
import com.billing.system.springsecurityjwt.repository.AccountInfoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class BalanceListener {

    private Logger logger = LoggerFactory.getLogger(BalanceListener.class);
    
    @Autowired
    private AccountInfoRepository accountInfoRepository;

    // СДЕЛАТЬ ОБРАБОТКУ ОШИБОК
    @RabbitListener(queues = "spec.balanceQueue")
    public void changeBalanceRabbit(BalanceChangerDto balanceChangerDto) {
        logger.warn("The balance is replenished by {} rub. on the phone number = {}",
                balanceChangerDto.getMoney(),
                balanceChangerDto.getPhoneNumber());

        AccountInfo accountInfo = accountInfoRepository
                .findByPhoneNumber(balanceChangerDto.getPhoneNumber());
        accountInfo.setBalance(accountInfo.getBalance() + balanceChangerDto.getMoney());
        accountInfoRepository.save(accountInfo);
    }
}
