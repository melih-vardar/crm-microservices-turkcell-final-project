package com.turkcell.analyticsservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {
		"com.turkcell.analyticsservice",
		"io.github.bothuany.security"
})
public class AnalyticsServiceApplication {
	public static void main(String[] args) {
		// CI/CD test
		SpringApplication.run(AnalyticsServiceApplication.class, args);
	}
}
