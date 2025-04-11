package com.turkcell.analyticsservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AnalyticsServiceApplication {
	public static void main(String[] args) {
		// CI/CD test
		SpringApplication.run(AnalyticsServiceApplication.class, args);
	}
}
