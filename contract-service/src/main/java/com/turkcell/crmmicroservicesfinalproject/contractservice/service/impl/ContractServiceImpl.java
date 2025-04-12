package com.turkcell.crmmicroservicesfinalproject.contractservice.service.impl;

import com.turkcell.crmmicroservicesfinalproject.contractservice.client.CustomerServiceClient;
import com.turkcell.crmmicroservicesfinalproject.contractservice.client.PlanServiceClient;
import com.turkcell.crmmicroservicesfinalproject.contractservice.entity.Contract;
import com.turkcell.crmmicroservicesfinalproject.contractservice.repository.ContractRepository;
import com.turkcell.crmmicroservicesfinalproject.contractservice.service.ContractService;
import io.github.bothuany.dtos.contract.ContractCreateDTO;
import io.github.bothuany.dtos.contract.ContractDetailedResponseDTO;
import io.github.bothuany.dtos.contract.ContractResponseDTO;
import io.github.bothuany.dtos.customer.CustomerResponseDTO;
import io.github.bothuany.dtos.plan.PlanResponseDTO;
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
    private final CustomerServiceClient customerServiceClient;
    private final PlanServiceClient planServiceClient;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Override
    @Transactional
    public ContractResponseDTO createContract(ContractCreateDTO request) {
        // Validate dates
        validateDates(request);

        // Validate plan exists
        try {
            planServiceClient.getPlanById(request.getPlanId());
        } catch (Exception e) {
            throw new BusinessException("Plan not found with id: " + request.getPlanId());
        }

        // Check if customer already has an active contract with same plan
        List<Contract> existingContracts = contractRepository.findByCustomerId(request.getCustomerId());
        boolean hasSamePlan = existingContracts.stream()
                .anyMatch(c -> c.getPlanId().equals(request.getPlanId()) && c.isActive());

        if (hasSamePlan) {
            throw new BusinessException(
                    "Customer already has an active contract with this plan");
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
    public ContractDetailedResponseDTO getContractDetailed(UUID id) {
        Contract contract = contractRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Contract not found with id: " + id));

        return convertToDetailedResponseDTO(contract);
    }

    @Override
    @Transactional
    public ContractResponseDTO updateContract(UUID id, ContractCreateDTO request) {
        // Validate dates
        validateDates(request);

        // Validate plan exists
        try {
            planServiceClient.getPlanById(request.getPlanId());
        } catch (Exception e) {
            throw new BusinessException("Plan not found with id: " + request.getPlanId());
        }

        Contract contract = contractRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Contract not found with id: " + id));

        // For updates, check other contracts (not including this one)
        List<Contract> existingContracts = contractRepository.findByCustomerId(request.getCustomerId());
        boolean hasSamePlan = existingContracts.stream()
                .filter(c -> !c.getId().equals(id)) // Exclude current contract
                .anyMatch(c -> c.getPlanId().equals(request.getPlanId()) && c.isActive());

        if (hasSamePlan) {
            throw new BusinessException(
                    "Customer already has another active contract with this plan");
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

    @Override
    public List<ContractDetailedResponseDTO> getDetailedContractsByCustomerId(UUID customerId) {
        return contractRepository.findByCustomerId(customerId).stream()
                .map(this::convertToDetailedResponseDTO)
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
    }

    private void updateContractFromRequest(Contract contract, ContractCreateDTO request) {
        contract.setCustomerId(request.getCustomerId());
        contract.setPlanId(request.getPlanId());
        contract.setStartDate(LocalDate.parse(request.getStartDate(), DATE_FORMATTER));
        contract.setEndDate(LocalDate.parse(request.getEndDate(), DATE_FORMATTER));
        contract.setActive(true);
    }

    private ContractResponseDTO convertToResponseDTO(Contract contract) {
        ContractResponseDTO responseDTO = new ContractResponseDTO();
        responseDTO.setId(contract.getId());
        responseDTO.setCustomerId(contract.getCustomerId());
        responseDTO.setPlanId(contract.getPlanId());
        responseDTO.setStartDate(contract.getStartDate().format(DATE_FORMATTER));
        responseDTO.setEndDate(contract.getEndDate().format(DATE_FORMATTER));
        responseDTO.setActive(contract.isActive());

        return responseDTO;
    }

    private ContractDetailedResponseDTO convertToDetailedResponseDTO(Contract contract) {
        ContractDetailedResponseDTO detailedResponseDTO = new ContractDetailedResponseDTO();
        detailedResponseDTO.setId(contract.getId());

        // Fetch customer details from customer service
        CustomerResponseDTO customer = customerServiceClient.getCustomerById(contract.getCustomerId());
        detailedResponseDTO.setCustomer(customer);

        // Fetch plan details from plan service
        PlanResponseDTO plan = planServiceClient.getPlanById(contract.getPlanId());
        detailedResponseDTO.setPlan(plan);

        detailedResponseDTO.setStartDate(contract.getStartDate().format(DATE_FORMATTER));
        detailedResponseDTO.setEndDate(contract.getEndDate().format(DATE_FORMATTER));
        detailedResponseDTO.setActive(contract.isActive());

        return detailedResponseDTO;
    }
}