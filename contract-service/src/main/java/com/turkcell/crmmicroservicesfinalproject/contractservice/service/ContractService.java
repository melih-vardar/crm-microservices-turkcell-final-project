package com.turkcell.crmmicroservicesfinalproject.contractservice.service;

import io.github.bothuany.dtos.contract.ContractCreateDTO;
import io.github.bothuany.dtos.contract.ContractDetailedResponseDTO;
import io.github.bothuany.dtos.contract.ContractResponseDTO;

import java.util.List;
import java.util.UUID;

public interface ContractService {
    ContractResponseDTO createContract(ContractCreateDTO request);

    ContractResponseDTO getContract(UUID id);

    ContractDetailedResponseDTO getContractDetailed(UUID id);

    ContractResponseDTO updateContract(UUID id, ContractCreateDTO request);

    void deleteContract(UUID id);

    List<ContractResponseDTO> getContractsByCustomerId(UUID customerId);

    List<ContractDetailedResponseDTO> getDetailedContractsByCustomerId(UUID customerId);
}