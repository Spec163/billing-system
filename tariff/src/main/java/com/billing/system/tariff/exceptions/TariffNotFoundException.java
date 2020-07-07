package com.billing.system.tariff.exceptions;

public class TariffNotFoundException extends RuntimeException {
    public TariffNotFoundException(Long id) {
        super("Could not find tariff " + id);
    }
}
