package com.billing.system.publisher.repository;

import com.billing.system.publisher.model.DefaultPrice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DefaultPriceRepository extends JpaRepository<DefaultPrice, Long> {
}
