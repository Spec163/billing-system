package com.billing.system;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan(basePackages = {"com.billing.system.*"})
@EntityScan(basePackages = {"com.billing.system.*"})
@EnableJpaRepositories(basePackages = {"com.billing.system.*"})
public class BillingSystemApplication {
    public static void main(String[] args) {
        SpringApplication.run(BillingSystemApplication.class);
    }
}
