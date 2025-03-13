package com.turkcell.customer_support_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class CustomerSupportServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(CustomerSupportServiceApplication.class, args);
    }

}
