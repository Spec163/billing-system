package com.billing.system.springsecurityjwt.repository;

import com.billing.system.springsecurityjwt.entity.AccountInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountInfoRepository extends JpaRepository<AccountInfo, Long> {
    AccountInfo findByPhoneNumber(String phoneNumber);
}
