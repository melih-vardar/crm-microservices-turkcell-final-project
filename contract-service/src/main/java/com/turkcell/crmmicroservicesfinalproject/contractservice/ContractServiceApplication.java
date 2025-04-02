package com.turkcell.crmmicroservicesfinalproject.contractservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableFeignClients
@EnableScheduling
@ComponentScan(basePackages = {
		"com.turkcell.crmmicroservicesfinalproject.contractservice",
		"io.github.bothuany.security"
})
public class ContractServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ContractServiceApplication.class, args);
	}

}
