package com.billing.system.springsecurityjwt.exception;

public class ServiceNotFoundException extends RuntimeException {
    public ServiceNotFoundException(Long id) {
        super("Could not find service with id = " + id);
    }
}
