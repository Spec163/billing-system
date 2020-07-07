package com.billing.system.tariff.controller;


import com.billing.system.tariff.exceptions.TariffNotFoundException;
import com.billing.system.tariff.model.Tariff;
import com.billing.system.tariff.repository.TariffRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("admin/tariff")
public class TariffController {

    private final TariffRepository tariffRepository;

    @Autowired
    public TariffController(TariffRepository tariffRepository) {
        this.tariffRepository = tariffRepository;
    }

    @GetMapping
    public List<Tariff> getAllTariffs() {
        return tariffRepository.findAll();
    }

    @GetMapping("{id}")
    public Tariff getTariff(@PathVariable("id") Long id) {
        return tariffRepository.findById(id).orElseThrow(() -> new TariffNotFoundException(id));
    }

    @PostMapping
    public Tariff createTariff(@RequestBody Tariff tariff) {
        tariff.setCreationDate(LocalDateTime.now());

        return tariffRepository.save(tariff);
    }

    @PutMapping("{id}")
    public Tariff update(
            @PathVariable("id") Long id,
            @RequestBody Tariff newTariff
    ) {

        return tariffRepository.findById(id)
                .map(tariff -> {
                    tariff.setTitle(newTariff.getTitle());
                    tariff.setPrice(newTariff.getPrice());
                    tariff.setCall(newTariff.getCall());
                    tariff.setSms(newTariff.getSms());
                    tariff.setInternet(newTariff.getInternet());
                    return tariffRepository.save(tariff);
                })
                .orElseGet(() -> {
                    newTariff.setId(id);
                    return tariffRepository.save(newTariff);
                });
    }

    @DeleteMapping("{id}")
    public void delete(@PathVariable("id") Long id) {
        tariffRepository.deleteById(id);
    }
}
