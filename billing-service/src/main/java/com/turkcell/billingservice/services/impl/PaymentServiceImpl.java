package com.turkcell.billingservice.services.impl;

import com.turkcell.billingservice.clients.CustomerClient;
import com.turkcell.billingservice.dtos.BillResponseDTO;
import com.turkcell.billingservice.dtos.PaymentRequest;
import com.turkcell.billingservice.dtos.PaymentResponseDTO;
import com.turkcell.billingservice.entities.Bill;
import com.turkcell.billingservice.entities.BillStatus;
import com.turkcell.billingservice.entities.Payment;
import com.turkcell.billingservice.entities.PaymentMethod;
import com.turkcell.billingservice.repositories.BillRepository;
import com.turkcell.billingservice.repositories.PaymentRepository;
import com.turkcell.billingservice.services.BilingService;
import com.turkcell.billingservice.services.PaymentService;
import io.github.bothuany.dtos.customer.CustomerResponseDTO;
import io.github.bothuany.event.notification.EmailNotificationEvent;
import io.github.bothuany.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final StreamBridge streamBridge;
    private final CustomerClient customerClient;
    private static final Logger logger = LoggerFactory.getLogger(PaymentServiceImpl.class);
    private final BilingService billingService;

    @Transactional
    public PaymentResponseDTO processPayment(UUID billId, PaymentRequest paymentRequest){
        // Ödeme işlemleri burada yapılır
        // Gerçek bir ödeme entegrasyonu için Stripe, PayPal vs. kullanılabilir
        // Validasyon
        validatePaymentRequest(paymentRequest);
        // Simülasyon: %70 başarı şansı
        boolean isSuccess = Math.random() < 0.75;
        String transactionId = "SIM-PAY-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        BillResponseDTO billResponseDTO = billingService.getBillById(billId);
        // BillResponseDTO'yu doğrudan Entity'ye çeviren bir metod kullanalım
        Bill bill = convertToBill(billResponseDTO);
        // Payment kaydı oluştur
        Payment payment = new Payment();
        payment.setBillId(billId);
        payment.setAmount(bill.getAmount());
        payment.setPaymentMethod(paymentRequest.getPaymentMethod());
        payment.setTransactionId(transactionId);
        payment.setSuccess(isSuccess);
        payment.setPaymentDate(LocalDateTime.now());
        payment.setCreatedAt(LocalDateTime.now());
        payment.setCardLastFour(paymentRequest.getCardNumber().substring(12));
        // Basit bir simülasyon
        updateBillStatus(bill, isSuccess);

        paymentRepository.save(payment);

        return convertToDTO(payment);
    }

    @Override
    public PaymentResponseDTO getPaymentById(UUID paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new BusinessException("Payment not found with id: " + paymentId));
        return convertToDTO(payment);
    }

    @Override
    public List<PaymentResponseDTO> getAllPayments() {
        return paymentRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    public PaymentResponseDTO getPaymentByBillId(UUID billId) {
        Payment payment = paymentRepository.findByBillId(billId)
                .orElseThrow(() -> new BusinessException("not found payment for bill: " + billId));

        return convertToDTO(payment);
    }
    public List<PaymentResponseDTO> getPaymentsByStatus(boolean isSuccess) {
        return paymentRepository.findBySuccess(isSuccess).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    public List<PaymentResponseDTO> getPaymentsByMethod(PaymentMethod method) {
        return paymentRepository.findByPaymentMethod(method).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private void validatePaymentRequest(PaymentRequest request) {
        if (request.getPaymentMethod() == PaymentMethod.BANK_TRANSFER &&
                (request.getCardNumber() != null || request.getCvv() != null)) {
            throw new BusinessException("Banka transferi için kart bilgileri gerekmez");
        }
    }
    private void updateBillStatus(Bill bill, boolean isSuccess) {
        if (isSuccess) {
            bill.setBillStatus(BillStatus.PAID);
            bill.setPaid(true);
            CustomerResponseDTO customer = customerClient.getCustomer(UUID.fromString(bill.getCustomerId()));
            // Fatura oluşturuldu bildirimi gönder
            EmailNotificationEvent emailNotificationEventpaid = new EmailNotificationEvent();
            emailNotificationEventpaid.setEmail(customer.getEmail());
            emailNotificationEventpaid.setSubject("new bill created");
            emailNotificationEventpaid.setMessage(String.format("A new bill has been created for you. Amount: %s, Due Date: %s",
                    bill.getAmount(), bill.getDueDate()));
            logger.info("Sending email notification {}", emailNotificationEventpaid);
            streamBridge.send("emailNotification-out-0", emailNotificationEventpaid);

            bill.setPaymentDate(LocalDateTime.now());

        } else {
            bill.setBillStatus(BillStatus.FAILED);
            logger.warn("Ödeme başarısız - Fatura ID: {}", bill.getId());
            bill.setPaid(false);
        }
        billingService.saveBill(bill);

    }
    private boolean simulatePayment(PaymentRequest paymentRequest) {
        // Burada gerçek ödeme işlemi yapılır
        // Demo amaçlı her zaman başarılı dönüyor
        return true;
    }
    private PaymentResponseDTO convertToDTO(Payment payment) {
        return PaymentResponseDTO.builder()
                .id(payment.getId())
                .amount(payment.getAmount())
                .paymentMethod(payment.getPaymentMethod().name())
                .transactionId(payment.getTransactionId())
                .paymentDate(payment.getPaymentDate())
                .billId(payment.getBillId())
                .createdAt(LocalDateTime.now())
                .build();
    }
    private Bill convertToBill(BillResponseDTO dto) {
        Bill bill = new Bill();
        bill.setId(dto.getId());
        bill.setContractId(dto.getContractId());
        bill.setCustomerId(dto.getCustomerId());
        bill.setAmount(dto.getAmount());
        bill.setDueDate(dto.getDueDate());
        bill.setBillStatus(BillStatus.valueOf(dto.getBillStatus()));
        bill.setCreatedAt(dto.getCreatedAt());
        bill.setPaymentDate(dto.getPaymentDate());
        bill.setPaid(dto.isPaid());
        return bill;
    }

}
