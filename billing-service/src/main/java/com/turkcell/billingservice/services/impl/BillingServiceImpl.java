package com.turkcell.billingservice.services.impl;

import com.turkcell.billingservice.clients.ContractClient;
import com.turkcell.billingservice.clients.CustomerClient;
import com.turkcell.billingservice.dtos.*;
import com.turkcell.billingservice.entities.Bill;
import com.turkcell.billingservice.entities.BillStatus;
import com.turkcell.billingservice.exceptions.BusinessException;
import com.turkcell.billingservice.repositories.BillRepository;
import com.turkcell.billingservice.rules.BillingBusinessRules;
import com.turkcell.billingservice.services.BilingService;
import io.github.bothuany.dtos.contract.ContractDetailedResponseDTO;
import io.github.bothuany.dtos.customer.CustomerResponseDTO;
import io.github.bothuany.event.analytics.BillAnalyticsEvent;
import io.github.bothuany.event.notification.EmailNotificationEvent;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BillingServiceImpl implements BilingService {

    private final BillRepository billRepository;
    private final BillingBusinessRules billingBusinessRules;
    private final CustomerClient customerClient;
    private final ContractClient contractClient;
    private final StreamBridge streamBridge;
    private static final Logger logger = LoggerFactory.getLogger(BillingServiceImpl.class);
    @Transactional
    public BillResponseDTO createBill(String customerId,String contractId){

        CustomerResponseDTO customer = customerClient.getCustomer(UUID.fromString(customerId));
        billingBusinessRules.checkIfCustomerExists(customer);

        ContractDetailedResponseDTO contractDetailedResponseDTO = contractClient.getContractDetailed(UUID.fromString(contractId));
        billingBusinessRules.checkIfContractIsActive(contractDetailedResponseDTO);

        Bill bill = new Bill();
        bill.setContractId(contractDetailedResponseDTO.getId());
        bill.setCustomerId(customer.getId().toString());
        bill.setAmount(BigDecimal.valueOf(contractDetailedResponseDTO.getPlan().getPrice()));
        bill.setDueDate(LocalDateTime.now().plusDays(30));
        bill.setPaid(false);
        bill.setCreatedAt(LocalDateTime.now());
        bill.setBillStatus(BillStatus.PENDING);//bill starter mode
        billRepository.save(bill);

        // Fatura oluşturuldu bildirimi gönder
        EmailNotificationEvent emailNotificationEvent = new EmailNotificationEvent();
        emailNotificationEvent.setEmail(customer.getEmail());
        emailNotificationEvent.setSubject("new bill created");
        emailNotificationEvent.setMessage(String.format("A new bill has been created for you. Amount: %s, Due Date: %s",
                bill.getAmount(), bill.getDueDate()));
        logger.info("Sending email notification {}", emailNotificationEvent);
        streamBridge.send("emailNotification-out-0", emailNotificationEvent);


        BillAnalyticsEvent billAnalyticsEvent = new BillAnalyticsEvent();
        billAnalyticsEvent.setCustomerId(bill.getCustomerId());
        billAnalyticsEvent.setAmount(bill.getAmount());
        billAnalyticsEvent.setDueDate(bill.getDueDate());
        billAnalyticsEvent.setCreatedAt(bill.getCreatedAt());

        logger.info("Sending bill analytics event {}", billAnalyticsEvent);
        streamBridge.send("BillAnalytics-out-0", billAnalyticsEvent);

        return getBillResponseDTO(bill);
    }
    public BillResponseDTO getBillById(UUID billId){
        Bill bill = billRepository.findById(billId)
                .orElseThrow(() -> new BusinessException("Bill not found with id: " + billId));
        return getBillResponseDTO(bill);
    }
    public List<BillResponseDTO> getBillsByCustomerId(String customerId) {
        // Müşteri var mı kontrolü
        CustomerResponseDTO customer = customerClient.getCustomer(UUID.fromString(customerId));
        if (customer == null) {
            throw new BusinessException("Customer not found with id: " + customerId);
        }
        List<Bill> bills = billRepository.findByCustomerId(customerId);
        return bills.stream()
                .map(this::getBillResponseDTO)
                .collect(Collectors.toList());
    }
    @Transactional
    @Override
    public BillResponseDTO updateBill(UUID billId, BillUpdateDTO billUpdateDTO) {
        // 1. Fatura var mı kontrol et
        Bill existingBill = billRepository.findById(billId)
                .orElseThrow(() -> new BusinessException("Bill not found with id: " + billId));

        // 2. Kontrat validasyonu
        ContractDetailedResponseDTO contract = contractClient.getContractDetailed(existingBill.getContractId());
        if (!contract.isActive()) {
            throw new BusinessException("Cannot update bill for inactive contract");
        }

        // 3. Güncellenebilir alanları kontrol et ve güncelle
        if (billUpdateDTO.getAmount() != null && billUpdateDTO.getAmount().compareTo(BigDecimal.ZERO) > 0) {
            existingBill.setAmount(billUpdateDTO.getAmount());
        }

        if (billUpdateDTO.getDueDate() != null && billUpdateDTO.getDueDate().isAfter(LocalDateTime.now())) {
            existingBill.setDueDate(billUpdateDTO.getDueDate());
        }

        // 4. Eğer ödendiyse güncellemeye izin verme
        if (existingBill.isPaid()) {
            throw new BusinessException("Paid bills cannot be updated");
        }

        // 5. Kaydet ve dönüştür
        Bill updatedBill = billRepository.save(existingBill);
        return getBillResponseDTO(updatedBill);
    }

    @Override
    public void deleteBill(UUID billId) {
        Bill bill = billRepository.findById(billId)
                .orElseThrow(() -> new BusinessException("Bill not found with id: " + billId));
        // 2. Ödenmiş faturaları silme
        if (bill.isPaid()) {
            throw new BusinessException("Cannot delete paid bills");

        }
        billRepository.deleteById(billId);
    }

    @Override
    public void saveBill(Bill bill) {
        billRepository.save(bill);
    }

    @Override
    public List<BillResponseDTO> getAllBills() {
        List<Bill> bills = billRepository.findAll();
        return bills.stream()
                .map(this::getBillResponseDTO)
                .collect(Collectors.toList());
    }

    private BillResponseDTO getBillResponseDTO(Bill bill) {
        BillResponseDTO dto = new BillResponseDTO();
        dto.setId(bill.getId());
        dto.setContractId(bill.getContractId());
        dto.setCustomerId(bill.getCustomerId());
        dto.setAmount(bill.getAmount());
        dto.setDueDate(bill.getDueDate());
        dto.setBillStatus(bill.getBillStatus().name());
        dto.setCreatedAt(bill.getCreatedAt());
        dto.setPaymentDate(bill.getPaymentDate());
        dto.setPaid(bill.isPaid());
        return dto;
    }


}
