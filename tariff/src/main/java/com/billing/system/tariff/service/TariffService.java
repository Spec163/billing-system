package com.billing.system.tariff.service;

import java.time.LocalDateTime;
import java.util.List;
import com.billing.system.tariff.exceptions.TariffNotFoundException;
import com.billing.system.tariff.model.Tariff;
import com.billing.system.tariff.repository.TariffRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TariffService {

    private final TariffRepository tariffRepository;

    /**
     * @param tariffRepository
     */
    @Autowired
    public TariffService(final TariffRepository tariffRepository) {
        this.tariffRepository = tariffRepository;
    }

    public List<Tariff> findAll() {
        return this.tariffRepository.findAll();
    }

    public Tariff findById(final Long id) {
        return this.tariffRepository.findById(id).orElseThrow(() -> new TariffNotFoundException(id));
    }

    public Tariff saveTariff(final Tariff tariff) {
        tariff.setCreationDate(LocalDateTime.now());

        return this.tariffRepository.save(tariff);
    }

    public Tariff updateTariff(final Long id, final Tariff newTariff) {

        return this.tariffRepository.findById(id)
            .map(tariff -> {
                tariff.setTitle(newTariff.getTitle());
                tariff.setPrice(newTariff.getPrice());
                tariff.setCall(newTariff.getCall());
                tariff.setSms(newTariff.getSms());
                tariff.setInternet(newTariff.getInternet());
                return this.tariffRepository.save(tariff);
            })
            .orElseGet(() -> {
                newTariff.setId(id);
                return this.tariffRepository.save(newTariff);
            });
    }

    public void deleteTariff(final Long id) {
        this.tariffRepository.deleteById(id);
    }
}
