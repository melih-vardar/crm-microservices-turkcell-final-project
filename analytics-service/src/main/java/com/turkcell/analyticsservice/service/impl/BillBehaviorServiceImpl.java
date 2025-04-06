package com.turkcell.analyticsservice.service.impl;

import com.turkcell.analyticsservice.dto.ForUserDto.ExampleBillDto;
import com.turkcell.analyticsservice.kafka.AnalyticsPipeline;
import com.turkcell.analyticsservice.model.BillBehavior;
import com.turkcell.analyticsservice.repository.BillBehaviorRepository;
import com.turkcell.analyticsservice.service.BillBehaviorService;
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
    public void BillAnalyticsToCustomer(ExampleBillDto exampleBillDto) {
        logger.info("Bill Analytics to Customer");
        BillBehavior billBehavior = new BillBehavior();
        billBehavior.setCustomerId(exampleBillDto.getCustomerId());
        billBehavior.setAmount(exampleBillDto.getAmount());
        billBehavior.setDueDate(exampleBillDto.getDueDate());
        billBehavior.setCreatedAt(exampleBillDto.getCreatedAt());
        logger.info("converted BillBehavior {}", billBehavior);
        billBehaviorRepository.save(billBehavior);
        billMetrics.incrementBill();
    }
}
