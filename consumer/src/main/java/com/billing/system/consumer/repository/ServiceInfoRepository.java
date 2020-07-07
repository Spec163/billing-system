package com.billing.system.consumer.repository;

import com.billing.system.consumer.model.ServiceInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServiceInfoRepository extends JpaRepository<ServiceInfo, Long> {
}
