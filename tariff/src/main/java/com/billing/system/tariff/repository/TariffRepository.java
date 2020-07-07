package com.billing.system.tariff.repository;

import com.billing.system.tariff.model.Tariff;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TariffRepository extends JpaRepository<Tariff, Long> {
    Tariff findByTitle(String title);
}
