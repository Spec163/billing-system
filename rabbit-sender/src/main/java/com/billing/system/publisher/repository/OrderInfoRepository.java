package com.billing.system.publisher.repository;

import com.billing.system.publisher.model.OrderInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderInfoRepository extends JpaRepository<OrderInfo, Long> {
}
