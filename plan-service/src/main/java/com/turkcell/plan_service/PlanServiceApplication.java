package com.turkcell.plan_service;

import io.github.bothuany.security.encryption.EncryptionConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@EnableFeignClients
@Import(EncryptionConfig.class)
@ComponentScan(basePackages = {
        "com.turkcell.plan_service",
        "io.github.bothuany.security"
})
public class PlanServiceApplication {
    public static void main(String[] args) {
        // CI/CD test
        SpringApplication.run(PlanServiceApplication.class, args);
    }
}
