package com.turkcell.crmmicroservicesfinalproject.userservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableFeignClients
@EnableScheduling
@ComponentScan(basePackages = {
        "com.turkcell.crmmicroservicesfinalproject.userservice",
        "io.github.bothuany.security",
        "io.github.bothuany.security.jwt"
})
public class UserServiceApplication {
    public static void main(String[] args) {
        // CI/CD test
        SpringApplication.run(UserServiceApplication.class, args);
    }
}
