package com.turkcell.crmmicroservicesfinalproject.contractservice.controller;

import com.turkcell.crmmicroservicesfinalproject.contractservice.service.ContractService;
import io.github.bothuany.dtos.contract.ContractCreateDTO;
import io.github.bothuany.dtos.contract.ContractResponseDTO;
import io.github.bothuany.exception.BusinessException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/contracts")
@RequiredArgsConstructor
@Tag(name = "Contract API", description = "Endpoints for managing customer contracts")
public class ContractController {

    private final ContractService contractService;

    @PostMapping
    @Operation(summary = "Create a new contract")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Contract created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid contract data or business rule violation")
    })
    public ResponseEntity<ContractResponseDTO> createContract(@Valid @RequestBody ContractCreateDTO request) {
        ContractResponseDTO response = contractService.createContract(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a contract by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Contract found"),
            @ApiResponse(responseCode = "404", description = "Contract not found")
    })
    public ResponseEntity<ContractResponseDTO> getContract(@PathVariable UUID id) {
        ContractResponseDTO response = contractService.getContract(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing contract")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Contract updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid contract data or business rule violation"),
            @ApiResponse(responseCode = "404", description = "Contract not found")
    })
    public ResponseEntity<ContractResponseDTO> updateContract(
            @PathVariable UUID id,
            @Valid @RequestBody ContractCreateDTO request) {
        ContractResponseDTO response = contractService.updateContract(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deactivate a contract")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Contract deactivated successfully"),
            @ApiResponse(responseCode = "404", description = "Contract not found")
    })
    public ResponseEntity<Void> deleteContract(@PathVariable UUID id) {
        contractService.deleteContract(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/customer/{customerId}")
    @Operation(summary = "Get all contracts for a customer")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Contracts retrieved successfully")
    })
    public ResponseEntity<List<ContractResponseDTO>> getContractsByCustomerId(@PathVariable UUID customerId) {
        List<ContractResponseDTO> contracts = contractService.getContractsByCustomerId(customerId);
        return ResponseEntity.ok(contracts);
    }
}