package com.billing.system.publisher.exceptions;

public class DefaultPriceNotFoundException extends RuntimeException {
    public DefaultPriceNotFoundException(final Long id) {
        super("Could not find tariff " + id);
    }
}
