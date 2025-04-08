package com.turkcell.analyticsservice.service.impl;

import com.turkcell.analyticsservice.kafka.AnalyticsPipeline;
import com.turkcell.analyticsservice.model.BillBehavior;
import com.turkcell.analyticsservice.repository.BillBehaviorRepository;
import com.turkcell.analyticsservice.service.BillBehaviorService;
import io.github.bothuany.event.analytics.BillAnalyticsEvent;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BillBehaviorServiceImpl implements BillBehaviorService {

    private static final Logger logger = LoggerFactory.getLogger(AnalyticsPipeline.class);
    private final BillBehaviorRepository billBehaviorRepository;
    private final BillMetrics billMetrics;

    @Override
    public void BillAnalyticsToCustomer(BillAnalyticsEvent billAnalyticsEvent) {
        logger.info("Bill Analytics to Customer");
        BillBehavior billBehavior = new BillBehavior();
        billBehavior.setCustomerId(billAnalyticsEvent.getCustomerId());
        billBehavior.setAmount(billAnalyticsEvent.getAmount());
        billBehavior.setDueDate(billAnalyticsEvent.getDueDate());
        billBehavior.setCreatedAt(billAnalyticsEvent.getCreatedAt());
        logger.info("converted BillBehavior {}", billBehavior);
        billBehaviorRepository.save(billBehavior);
        billMetrics.incrementBill();
    }




}
