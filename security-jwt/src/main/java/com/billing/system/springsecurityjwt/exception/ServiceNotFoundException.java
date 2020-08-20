package com.billing.system.springsecurityjwt.exception;

public class ServiceNotFoundException extends RuntimeException {
    public ServiceNotFoundException(final Long id) {
        super("Could not find service with id = " + id);
    }
}
