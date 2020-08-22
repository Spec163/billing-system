package com.billing.system.tariff.controller;

// @checkstyle (HeaderCheck 1000 lines)
// @checkstyle (JavadocPackageCheck 1000 lines)


import java.util.List;
import com.billing.system.tariff.model.Tariff;
import com.billing.system.tariff.service.TariffService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger logger = LoggerFactory.getLogger(TariffController.class);


    private final TariffService tariffService;

    @Autowired
    public TariffController(final TariffService tariffService) {
        this.tariffService = tariffService;
    }

    @GetMapping
    public List<Tariff> getAllTariffs() {
        return this.tariffService.findAll();
    }

    @GetMapping("{id}")
    public Tariff getTariff(@PathVariable("id") final Long id) {
        return this.tariffService.findById(id);
    }

    @PostMapping
    public Tariff createTariff(@RequestBody final Tariff tariff) {

        return this.tariffService.saveTariff(tariff);
    }

    @PutMapping("{id}")
    public Tariff update(
        @PathVariable("id") final Long id,
        @RequestBody final Tariff newTariff
    ) {
        return this.tariffService.updateTariff(id, newTariff);
    }

    @DeleteMapping("{id}")
    public void delete(@PathVariable("id") final Long id) {
        this.tariffService.deleteTariff(id);
    }
}
