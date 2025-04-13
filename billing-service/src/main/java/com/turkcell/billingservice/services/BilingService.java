package com.turkcell.billingservice.services;

import com.turkcell.billingservice.dtos.BillResponseDTO;
import com.turkcell.billingservice.dtos.BillUpdateDTO;
import com.turkcell.billingservice.entities.Bill;

import java.util.List;
import java.util.UUID;

public interface BilingService {
    BillResponseDTO createBill(String customerId,String contractId);
    List<BillResponseDTO> getBillsByCustomerId(String customerId);
    BillResponseDTO getBillById(UUID billId);
    BillResponseDTO updateBill(UUID billId, BillUpdateDTO billUpdateDTO);
    void deleteBill(UUID billId);
    void saveBill(Bill bill);
}

