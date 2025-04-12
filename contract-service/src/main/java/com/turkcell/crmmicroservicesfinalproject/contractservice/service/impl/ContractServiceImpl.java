package com.turkcell.crmmicroservicesfinalproject.contractservice.service.impl;

import com.turkcell.crmmicroservicesfinalproject.contractservice.client.PlanClient;
import com.turkcell.crmmicroservicesfinalproject.contractservice.entity.Contract;
import com.turkcell.crmmicroservicesfinalproject.contractservice.repository.ContractRepository;
import com.turkcell.crmmicroservicesfinalproject.contractservice.service.ContractService;
import io.github.bothuany.dtos.contract.ContractCreateDTO;
import io.github.bothuany.dtos.contract.ContractResponseDTO;
import io.github.bothuany.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ContractServiceImpl implements ContractService {

    private final ContractRepository contractRepository;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private final PlanClient planClient;

    @Override
    @Transactional
    public ContractResponseDTO createContract(ContractCreateDTO request) {
        // Validate dates
        validateDates(request);
        // Check if customer already has an active contract with same plan type
        List<Contract> existingContracts = contractRepository.findByCustomerId(request.getCustomerId());
        boolean hasSamePlanType = existingContracts.stream()
                .anyMatch(c -> c.getPlanType().equals(request.getPlanType()) && c.isActive());

        if (hasSamePlanType) {
            throw new BusinessException(
                    "Customer already has an active contract with plan type: " + request.getPlanType());
        }

        Contract contract = new Contract();
        updateContractFromRequest(contract, request);
        return convertToResponseDTO(contractRepository.save(contract));
    }

    @Override
    public ContractResponseDTO getContract(UUID id) {
        Contract contract = contractRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Contract not found with id: " + id));
        return convertToResponseDTO(contract);
    }

    @Override
    @Transactional
    public ContractResponseDTO updateContract(UUID id, ContractCreateDTO request) {
        // Validate dates
        validateDates(request);

        Contract contract = contractRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Contract not found with id: " + id));

        // For updates, check other contracts (not including this one)
        List<Contract> existingContracts = contractRepository.findByCustomerId(request.getCustomerId());
        boolean hasSamePlanType = existingContracts.stream()
                .filter(c -> !c.getId().equals(id))
                .anyMatch(c -> c.getPlanType().equals(request.getPlanType()) && c.isActive());

        if (hasSamePlanType) {
            throw new BusinessException(
                    "Customer already has another active contract with plan type: " + request.getPlanType());
        }

        updateContractFromRequest(contract, request);
        return convertToResponseDTO(contractRepository.save(contract));
    }

    @Override
    @Transactional
    public void deleteContract(UUID id) {
        Contract contract = contractRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Contract not found with id: " + id));
        contract.setActive(false);
        contractRepository.save(contract);
    }

    @Override
    public List<ContractResponseDTO> getContractsByCustomerId(UUID customerId) {
        return contractRepository.findByCustomerId(customerId).stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    private void validateDates(ContractCreateDTO request) {
        LocalDate startDate;
        LocalDate endDate;

        try {
            startDate = LocalDate.parse(request.getStartDate(), DATE_FORMATTER);
            endDate = LocalDate.parse(request.getEndDate(), DATE_FORMATTER);
        } catch (DateTimeParseException e) {
            throw new BusinessException("Invalid date format. Use YYYY-MM-DD format.");
        }

        // Start date must be before or equal to end date
        if (startDate.isAfter(endDate)) {
            throw new BusinessException("Start date cannot be after end date.");
        }

        // Start date should not be in the past
        if (startDate.isBefore(LocalDate.now())) {
            throw new BusinessException("Start date cannot be in the past.");
        }

        // For MONTHLY plan, contract duration must be at least 1 month but not more
        // than 12 months
        // For YEARLY plan, contract duration must be at least 12 months
        int monthsBetween = (endDate.getYear() - startDate.getYear()) * 12 + endDate.getMonthValue()
                - startDate.getMonthValue();

        if (request.getPlanType().toString().equals("MONTHLY")) {
            if (monthsBetween < 1) {
                throw new BusinessException("Monthly plan must be at least 1 month.");
            }
            if (monthsBetween > 12) {
                throw new BusinessException("Monthly plan cannot exceed 12 months.");
            }
        } else if (request.getPlanType().toString().equals("YEARLY")) {
            if (monthsBetween < 12) {
                throw new BusinessException("Yearly plan must be at least 12 months.");
            }
        }
    }

    private void updateContractFromRequest(Contract contract, ContractCreateDTO request) {
        contract.setCustomerId(request.getCustomerId());
        contract.setPlanType(request.getPlanType());
        contract.setStartDate(LocalDate.parse(request.getStartDate(), DATE_FORMATTER));
        contract.setEndDate(LocalDate.parse(request.getEndDate(), DATE_FORMATTER));
        contract.setActive(true);
    }

    private ContractResponseDTO convertToResponseDTO(Contract contract) {
        ContractResponseDTO responseDTO = new ContractResponseDTO();
        responseDTO.setId(contract.getId());
        responseDTO.setCustomerId(contract.getCustomerId());
        responseDTO.setPlanType(contract.getPlanType());
        responseDTO.setStartDate(contract.getStartDate().format(DATE_FORMATTER));
        responseDTO.setEndDate(contract.getEndDate().format(DATE_FORMATTER));
        responseDTO.setActive(contract.isActive());
        return responseDTO;
    }
}