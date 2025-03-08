package com.turkcell.billingservice.services;

import io.github.bothuany.dtos.billing.BillCreateDTO;
import io.github.bothuany.dtos.billing.BillResponseDTO;
import io.github.bothuany.dtos.billing.PaymentDTO;
import com.turkcell.billingservice.entities.Invoice;
import com.turkcell.billingservice.entities.Payment;
import com.turkcell.billingservice.repositories.InvoiceRepository;
import com.turkcell.billingservice.repositories.PaymentRepository;
import com.turkcell.billingservice.rules.BillingBusinessRules;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BillingService {
    private final InvoiceRepository invoiceRepository;
    private final PaymentRepository paymentRepository;
    private final BillingBusinessRules businessRules;

    public BillResponseDTO createInvoice(BillCreateDTO createDTO) {
        businessRules.validateAmount(createDTO.getAmount());
        businessRules.validateDueDate(createDTO.getDueDate());

        Invoice invoice = new Invoice();
        invoice.setCustomerId(createDTO.getCustomerId());
        invoice.setAmount(createDTO.getAmount());
        invoice.setDueDate(createDTO.getDueDate());
        invoice.setPaid(false);

        Invoice savedInvoice = invoiceRepository.save(invoice);
        return convertToResponseDTO(savedInvoice);
    }

    public List<BillResponseDTO> getCustomerInvoices(UUID customerId) {
        return invoiceRepository.findByCustomerId(customerId)
                .stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    public List<BillResponseDTO> getUnpaidInvoices(UUID customerId) {
        return invoiceRepository.findByCustomerIdAndIsPaid(customerId, false)
                .stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public BillResponseDTO processPayment(PaymentDTO paymentDTO) {
        businessRules.checkIfInvoiceExists(paymentDTO.getBillId());
        businessRules.checkIfInvoiceAlreadyPaid(paymentDTO.getBillId());
        businessRules.validatePaymentAmount(paymentDTO.getBillId(), paymentDTO.getAmount());

        Invoice invoice = invoiceRepository.findById(paymentDTO.getBillId())
                .orElseThrow(() -> new RuntimeException("Fatura bulunamadı"));

        Payment payment = new Payment();
        payment.setInvoice(invoice);
        payment.setAmount(paymentDTO.getAmount());
        payment.setPaymentMethod(paymentDTO.getPaymentMethod());
        payment.setTransactionId(generateTransactionId());
        
        paymentRepository.save(payment);

        invoice.setPaid(true);
        Invoice updatedInvoice = invoiceRepository.save(invoice);

        return convertToResponseDTO(updatedInvoice);
    }

    public List<Payment> getInvoicePayments(UUID invoiceId) {
        businessRules.checkIfInvoiceExists(invoiceId);
        return paymentRepository.findByInvoiceId(invoiceId);
    }

    private BillResponseDTO convertToResponseDTO(Invoice invoice) {
        return new BillResponseDTO(
                invoice.getId(),
                invoice.getCustomerId(),
                invoice.getAmount(),
                invoice.getDueDate(),
                invoice.isPaid()
        );
    }

    private String generateTransactionId() {
        return "TRX-" + System.currentTimeMillis();
    }
}