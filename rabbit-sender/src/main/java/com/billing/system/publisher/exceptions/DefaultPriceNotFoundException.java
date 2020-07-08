package com.billing.system.publisher.exceptions;

public class DefaultPriceNotFoundException extends RuntimeException {
    public DefaultPriceNotFoundException(Long id) {
        super("Could not find tariff " + id);
    }
}
