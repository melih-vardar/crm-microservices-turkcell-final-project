package com.turkcell.billingservice.services.impl;

import com.turkcell.billingservice.clients.CustomerClient;
import com.turkcell.billingservice.dtos.BillResponseDTO;
import com.turkcell.billingservice.dtos.PaymentRequest;
import com.turkcell.billingservice.dtos.PaymentResponseDTO;
import com.turkcell.billingservice.entities.Bill;
import com.turkcell.billingservice.entities.BillStatus;
import com.turkcell.billingservice.entities.Payment;
import com.turkcell.billingservice.entities.PaymentMethod;
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

        Bill bill = new Bill();
        bill.setId(billResponseDTO.getId());
        bill.setContractId(billResponseDTO.getContractId());
        bill.setCustomerId(billResponseDTO.getCustomerId());
        bill.setAmount(billResponseDTO.getAmount());
        bill.setDueDate(billResponseDTO.getDueDate());
        bill.setBillStatus(BillStatus.valueOf(billResponseDTO.getBillStatus()));
        bill.setCreatedAt(billResponseDTO.getCreatedAt());
        // Payment listesi varsa onları da dönüştürmeniz gerek. Örneğin:
        List<Payment> payments = new ArrayList<>();
        if (billResponseDTO.getPayments() != null) {
            for (PaymentResponseDTO paymentDTO : billResponseDTO.getPayments()) {
                Payment payment = new Payment();
                payment.setId(paymentDTO.getId());
                payment.setAmount(paymentDTO.getAmount());
                payment.setPaymentDate(paymentDTO.getPaymentDate());
                payment.setBill(bill); // iki yönlü ilişkiyi korumak için

                payments.add(payment);
            }
        }
        bill.setPayments(payments);
        // Payment kaydı oluştur
        Payment payment = new Payment();
        payment.setBill(bill);
        payment.setAmount(bill.getAmount());
        payment.setPaymentMethod(paymentRequest.getPaymentMethod());
        payment.setTransactionId(transactionId);
        payment.setSuccess(isSuccess);
        payment.setCardLastFour(paymentRequest.getCardNumber().substring(12));
        paymentRepository.save(payment);
        // Basit bir simülasyon
        updateBillStatus(bill, isSuccess);

        boolean paymentSuccess = simulatePayment(paymentRequest);

        if (paymentSuccess) {
            bill.setBillStatus(BillStatus.PAID);
            bill.setPaid(true);
            bill.setPaymentDate(LocalDateTime.now());
        } else {
            bill.setBillStatus(BillStatus.FAILED);
        }
        PaymentResponseDTO paymentResponseDTO = new PaymentResponseDTO();
        paymentResponseDTO.setId(payment.getId());
        paymentResponseDTO.setBillId(bill.getId());
        paymentResponseDTO.setAmount(payment.getAmount());
        paymentResponseDTO.setPaymentMethod(payment.getPaymentMethod().toString());
        paymentResponseDTO.setTransactionId(payment.getTransactionId());
        paymentResponseDTO.setPaymentDate(payment.getPaymentDate());
        paymentResponseDTO.setPaymentDate(payment.getPaymentDate());
        return paymentResponseDTO;
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
            CustomerResponseDTO customer = customerClient.getCustomerById(bill.getCustomerId());
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
        }


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
                .billId(payment.getBill().getId())
                .build();
    }
}
