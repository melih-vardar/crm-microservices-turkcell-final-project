package com.turkcell.customer_support_service;

import io.github.bothuany.security.encryption.EncryptionConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

@SpringBootApplication(exclude = {
        DataSourceAutoConfiguration.class,
        HibernateJpaAutoConfiguration.class
})
@EnableFeignClients
@Import(EncryptionConfig.class)
@ComponentScan(basePackages = {
        "com.turkcell.customer_support_service",
        "io.github.bothuany.security"
})
public class CustomerSupportServiceApplication {
    public static void main(String[] args) {
        // CI/CD test
        SpringApplication.run(CustomerSupportServiceApplication.class, args);
    }
}
