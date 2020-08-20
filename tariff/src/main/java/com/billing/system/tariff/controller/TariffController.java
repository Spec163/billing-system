package com.billing.system.tariff.controller;

// @checkstyle (HeaderCheck 1000 lines)
// @checkstyle (JavadocPackageCheck 1000 lines)


import java.time.LocalDateTime;
import java.util.List;
import com.billing.system.tariff.exceptions.TariffNotFoundException;
import com.billing.system.tariff.model.Tariff;
import com.billing.system.tariff.repository.TariffRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * This is controller for Tariffs.
 * @since 0.0
 */
@RestController
@RequestMapping("profile/tariff")
@CrossOrigin
public class TariffController {

    /**
     *
     */
    private final TariffRepository tariffRepository;

    /**
     *
     * @param tariffRepository
     */
    @Autowired
    public TariffController(final TariffRepository tariffRepository) {
        this.tariffRepository = tariffRepository;
    }

    @GetMapping
    public List<Tariff> getAllTariffs() {
        return this.tariffRepository.findAll();
    }

    @GetMapping("{id}")
    public Tariff getTariff(@PathVariable("id") final Long id) {
        return this.tariffRepository.findById(id).orElseThrow(() -> new TariffNotFoundException(id));
    }

    @PostMapping
    public Tariff createTariff(@RequestBody final Tariff tariff) {
        tariff.setCreationDate(LocalDateTime.now());

        return this.tariffRepository.save(tariff);
    }

    @PutMapping("{id}")
    public Tariff update(
        @PathVariable("id") final Long id,
        @RequestBody final Tariff newTariff
    ) {

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

    @DeleteMapping("{id}")
    public void delete(@PathVariable("id") final Long id) {
        this.tariffRepository.deleteById(id);
    }
}
