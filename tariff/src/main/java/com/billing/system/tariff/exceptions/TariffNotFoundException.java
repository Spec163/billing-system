package com.billing.system.tariff.exceptions;

public class TariffNotFoundException extends RuntimeException {
    public TariffNotFoundException(final Long id) {
        super("Could not find tariff " + id);
    }
}
