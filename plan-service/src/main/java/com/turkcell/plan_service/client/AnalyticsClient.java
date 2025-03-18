package com.turkcell.plan_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "analytics-service")
public class AnalyticsClient {

}
