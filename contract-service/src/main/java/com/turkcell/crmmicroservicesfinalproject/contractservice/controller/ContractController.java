package com.turkcell.crmmicroservicesfinalproject.contractservice.controller;

import com.turkcell.crmmicroservicesfinalproject.contractservice.service.ContractService;
import io.github.bothuany.dtos.contract.ContractCreateDTO;
import io.github.bothuany.dtos.contract.ContractDetailedResponseDTO;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/contracts")
@RequiredArgsConstructor
@Tag(name = "Contract API", description = "Endpoints for managing customer contracts")
@Slf4j
public class ContractController {

    private final ContractService contractService;

    @GetMapping("/test")
    @Operation(summary = "Test endpoint to verify routing")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Test successful")
    })
    public ResponseEntity<Map<String, String>> test() {
        log.info("Test endpoint accessed in contract-service");
        Map<String, String> response = new HashMap<>();
        response.put("message", "Contract service test endpoint is working");
        response.put("service", "contract-service");
        return ResponseEntity.ok(response);
    }

    @PostMapping
    @Operation(summary = "Create a new contract")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Contract created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid contract data or business rule violation")
    })
    @PreAuthorize("hasRole('CUSTOMER_REPRESENTATIVE') or hasRole('ADMIN')")
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
    @PreAuthorize("hasRole('CUSTOMER_REPRESENTATIVE') or hasRole('ADMIN')")
    public ResponseEntity<ContractResponseDTO> getContract(@PathVariable UUID id) {
        ContractResponseDTO response = contractService.getContract(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}/detailed")
    @Operation(summary = "Get a detailed contract by ID with customer and plan information")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Detailed contract found"),
            @ApiResponse(responseCode = "404", description = "Contract not found")
    })
    @PreAuthorize("hasRole('CUSTOMER_REPRESENTATIVE') or hasRole('ADMIN')")
    public ResponseEntity<ContractDetailedResponseDTO> getContractDetailed(@PathVariable UUID id) {
        ContractDetailedResponseDTO response = contractService.getContractDetailed(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing contract")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Contract updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid contract data or business rule violation"),
            @ApiResponse(responseCode = "404", description = "Contract not found")
    })
    @PreAuthorize("hasRole('CUSTOMER_REPRESENTATIVE') or hasRole('ADMIN')")
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
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteContract(@PathVariable UUID id) {
        contractService.deleteContract(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/customer/{customerId}")
    @Operation(summary = "Get contracts by customer ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Contracts found"),
            @ApiResponse(responseCode = "404", description = "Customer not found")
    })
    @PreAuthorize("hasRole('CUSTOMER_REPRESENTATIVE') or hasRole('ADMIN')")
    public ResponseEntity<List<ContractResponseDTO>> getContractsByCustomerId(@PathVariable UUID customerId) {
        List<ContractResponseDTO> response = contractService.getContractsByCustomerId(customerId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/customer/{customerId}/detailed")
    @Operation(summary = "Get detailed contracts by customer ID with customer and plan information")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Detailed contracts found"),
            @ApiResponse(responseCode = "404", description = "Customer not found")
    })
    @PreAuthorize("hasRole('CUSTOMER_REPRESENTATIVE') or hasRole('ADMIN')")
    public ResponseEntity<List<ContractDetailedResponseDTO>> getDetailedContractsByCustomerId(
            @PathVariable UUID customerId) {
        List<ContractDetailedResponseDTO> response = contractService.getDetailedContractsByCustomerId(customerId);
        return ResponseEntity.ok(response);
    }
}