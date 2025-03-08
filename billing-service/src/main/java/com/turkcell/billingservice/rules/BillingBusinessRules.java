package com.turkcell.billingservice.rules;

import com.turkcell.billingservice.entities.Invoice;
import com.turkcell.billingservice.exception.types.BusinessException;
import com.turkcell.billingservice.repositories.InvoiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BillingBusinessRules {
    private final InvoiceRepository invoiceRepository;

    public void checkIfInvoiceExists(UUID id) {
        if (!invoiceRepository.existsById(id)) {
            throw new BusinessException("Fatura bulunamadı: " + id);
        }
    }

    public void checkIfInvoiceAlreadyPaid(UUID id) {
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Fatura bulunamadı"));
        if (invoice.isPaid()) {
            throw new BusinessException("Fatura zaten ödenmiş");
        }
    }

    public void validatePaymentAmount(UUID invoiceId, double paymentAmount) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new BusinessException("Fatura bulunamadı"));
        if (paymentAmount != invoice.getAmount()) {
            throw new BusinessException("Ödeme tutarı fatura tutarı ile eşleşmiyor");
        }
    }

    public void validateDueDate(String dueDate) {
        LocalDate date = LocalDate.parse(dueDate);
        if (date.isBefore(LocalDate.now())) {
            throw new BusinessException("Son ödeme tarihi geçmiş tarih olamaz");
        }
    }

    public void validateAmount(double amount) {
        if (amount <= 0) {
            throw new BusinessException("Fatura tutarı sıfır veya negatif olamaz");
        }
    }
}