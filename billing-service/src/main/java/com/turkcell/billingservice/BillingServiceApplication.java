package com.turkcell.billingservice;

import io.github.bothuany.security.encryption.EncryptionConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@EnableFeignClients
@EnableDiscoveryClient
@Import(EncryptionConfig.class)
@ComponentScan(basePackages = {
        "com.turkcell.billingservice",
        "io.github.bothuany.security"
})
public class BillingServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(BillingServiceApplication.class, args);
    }
}
