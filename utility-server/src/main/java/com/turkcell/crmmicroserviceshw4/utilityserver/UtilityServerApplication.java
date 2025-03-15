package com.turkcell.crmmicroserviceshw4.utilityserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class UtilityServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(UtilityServerApplication.class, args);
    }

}
