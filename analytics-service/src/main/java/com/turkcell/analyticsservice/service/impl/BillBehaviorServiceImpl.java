package com.turkcell.analyticsservice.service.impl;

import com.turkcell.analyticsservice.dto.dto.BillAnalyticsDto;
import com.turkcell.analyticsservice.kafka.AnalyticsPipeline;
import com.turkcell.analyticsservice.model.BillBehavior;
import com.turkcell.analyticsservice.repository.BillBehaviorRepository;
import com.turkcell.analyticsservice.service.BillBehaviorService;
import io.github.bothuany.event.analytics.BillAnalyticsEvent;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

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

    @Override
    public List<BillAnalyticsDto> getAllBillAnalytics() {
        List<BillBehavior> billBehaviors = billBehaviorRepository.findAll();
        return billBehaviors.stream().map(billBehavior -> new BillAnalyticsDto(
                billBehavior.getId(),
                billBehavior.getCustomerId(),
                billBehavior.getAmount(),
                billBehavior.getDueDate(),
                billBehavior.getCreatedAt()
        )).collect(Collectors.toList());
    }

    @Override
    public BillAnalyticsDto getBillAnalyticsById(String customerId) {
        BillBehavior billBehavior = billBehaviorRepository.findByCustomerId(customerId);
        BillAnalyticsDto billAnalyticsDto = new BillAnalyticsDto();
        billAnalyticsDto.setId(billBehavior.getId());
        billAnalyticsDto.setCustomerId(billBehavior.getCustomerId());
        billAnalyticsDto.setAmount(billBehavior.getAmount());
        billAnalyticsDto.setDueDate(billBehavior.getDueDate());
        billAnalyticsDto.setCreatedAt(billBehavior.getCreatedAt());
        return billAnalyticsDto;
    }


}
