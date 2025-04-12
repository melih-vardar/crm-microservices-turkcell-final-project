package com.turkcell.billingservice.services;

import com.turkcell.billingservice.clients.*;
import com.turkcell.billingservice.dtos.*;
import com.turkcell.billingservice.entities.Bill;
import com.turkcell.billingservice.entities.Payment;
import com.turkcell.billingservice.exceptions.BusinessException;
import com.turkcell.billingservice.exceptions.ResourceNotFoundException;
import com.turkcell.billingservice.repositories.BillRepository;
import com.turkcell.billingservice.repositories.PaymentRepository;
import io.github.bothuany.event.analytics.BillAnalyticsEvent;
import io.github.bothuany.event.notification.EmailNotificationEvent;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BillingService {

    private final BillRepository billRepository;
    private final PaymentRepository paymentRepository;
    private final CustomerClient customerClient;
    private final ContractClient contractClient;
    private final PaymentClient paymentClient;
    private final StreamBridge streamBridge;
    private static final Logger logger = LoggerFactory.getLogger(BillingService.class);

    @Transactional
    public BillResponseDTO createBill(BillCreateDTO billCreateDTO) {
        // Müşteri kontrolü
        CustomerDTO customer = customerClient.getCustomerById(billCreateDTO.getCustomerId());
        if (customer == null) {
            throw new BusinessException("Customer not found");
        }

        // Sözleşme kontrolü
        ContractDTO contract = contractClient.getContractById(billCreateDTO.getContractId());
        if (contract == null || !contract.getStatus().equals("ACTIVE")) {
            throw new BusinessException("Contract not found or not active");
        }

        Bill bill = new Bill();
        bill.setCustomerId(billCreateDTO.getCustomerId());
        bill.setAmount(billCreateDTO.getAmount());
        bill.setDueDate(billCreateDTO.getDueDate());
        bill.setPaid(false);

        Bill savedBill = billRepository.save(bill);

        // Save bill notification sending
        try {
            EmailNotificationEvent emailNotificationEvent = new EmailNotificationEvent();
            emailNotificationEvent.setEmail(customer.getEmail());
            emailNotificationEvent.setSubject("New Bill Created");
            emailNotificationEvent.setMessage(String.format("A new bill of %s has been created with due date %s",
                    savedBill.getAmount(), savedBill.getDueDate()));

            logger.info("Sending email notification {}", emailNotificationEvent);
            streamBridge.send("emailNotification-out-0", emailNotificationEvent);
        } catch (Exception e) {
            logger.error("Error sending bill notification email: {}", e.getMessage(), e);
        }

        // Analytics sending
        try {
            BillAnalyticsEvent billAnalyticsEvent = new BillAnalyticsEvent();
            billAnalyticsEvent.setCustomerId(bill.getCustomerId());
            billAnalyticsEvent.setAmount(bill.getAmount());
            billAnalyticsEvent.setDueDate(bill.getDueDate());
            billAnalyticsEvent.setCreatedAt(bill.getCreatedAt());

            logger.info("Sending bill analytics event {}", billAnalyticsEvent);
            streamBridge.send("BillAnalytics-out-0", billAnalyticsEvent);
        } catch (Exception e) {
            logger.error("Error sending bill analytics data: {}", e.getMessage(), e);
        }

        return convertToBillResponseDTO(savedBill);
    }

    public BillResponseDTO getBillById(UUID id) {
        Bill bill = billRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bill not found with id: " + id));
        return convertToBillResponseDTO(bill);
    }

    public List<BillResponseDTO> getUnpaidBills() {
        return billRepository.findByPaidFalse()
                .stream()
                .map(this::convertToBillResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public PaymentResponseDTO processPayment(PaymentDTO paymentDTO) {
        Bill bill = billRepository.findById(paymentDTO.getBillId())
                .orElseThrow(() -> new ResourceNotFoundException("Bill not found with id: " + paymentDTO.getBillId()));

        if (bill.isPaid()) {
            throw new BusinessException("Bill is already paid");
        }

        if (!bill.getAmount().equals(paymentDTO.getAmount())) {
            throw new BusinessException("Payment amount does not match bill amount");
        }

        if (!getPaymentMethods().contains(paymentDTO.getPaymentMethod())) {
            throw new BusinessException("Invalid payment method");
        }

        // Ödeme servisi çağrısı
        PaymentRequestDTO paymentRequest = new PaymentRequestDTO(
                bill.getId().toString(),
                paymentDTO.getAmount(),
                paymentDTO.getPaymentMethod(),
                bill.getCustomerId());
        PaymentResponseDTO paymentResponse = paymentClient.processPayment(paymentRequest);

        Payment payment = new Payment();
        payment.setBill(bill);
        payment.setAmount(paymentDTO.getAmount());
        payment.setPaymentMethod(paymentDTO.getPaymentMethod());
        payment.setTransactionId(paymentResponse.getTransactionId());
        payment.setPaymentDate(LocalDateTime.now());

        Payment savedPayment = paymentRepository.save(payment);

        bill.setPaid(true);
        billRepository.save(bill);

        // Ödeme bildirimi gönder
        try {
            CustomerDTO customer = customerClient.getCustomerById(bill.getCustomerId());

            EmailNotificationEvent emailNotificationEvent = new EmailNotificationEvent();
            emailNotificationEvent.setEmail(customer.getEmail());
            emailNotificationEvent.setSubject("Payment Successful");
            emailNotificationEvent
                    .setMessage(String.format("Your payment of %s has been processed successfully. Transaction ID: %s",
                            payment.getAmount(), payment.getTransactionId()));

            logger.info("Sending email for payment notification {}", emailNotificationEvent);
            streamBridge.send("emailNotification-out-0", emailNotificationEvent);
        } catch (Exception e) {
            logger.error("Error sending payment notification email: {}", e.getMessage(), e);
        }

        return convertToPaymentResponseDTO(savedPayment);
    }

    public PaymentResponseDTO getPaymentById(UUID id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found with id: " + id));
        return convertToPaymentResponseDTO(payment);
    }

    public List<String> getPaymentMethods() {
        return Arrays.asList("CREDIT_CARD", "BANK_TRANSFER", "CASH");
    }

    private BillResponseDTO convertToBillResponseDTO(Bill bill) {
        return new BillResponseDTO(
                bill.getId(),
                bill.getCustomerId(),
                bill.getAmount(),
                bill.getDueDate(),
                bill.isPaid(),
                bill.getCreatedAt(),
                bill.getUpdatedAt());
    }

    private PaymentResponseDTO convertToPaymentResponseDTO(Payment payment) {
        return new PaymentResponseDTO(
                payment.getId(),
                payment.getBill().getId(),
                payment.getAmount(),
                payment.getPaymentMethod(),
                payment.getTransactionId(),
                payment.getPaymentDate(),
                payment.getCreatedAt(),
                payment.getUpdatedAt());
    }
}