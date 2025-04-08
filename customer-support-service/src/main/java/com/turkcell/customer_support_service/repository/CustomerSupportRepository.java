package com.turkcell.customer_support_service.repository;

import com.turkcell.customer_support_service.entity.CustomerSupport;
import io.github.bothuany.dtos.support.TicketResponseDTO;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


public interface CustomerSupportRepository extends MongoRepository<CustomerSupport, UUID> {
    Optional<CustomerSupport> findById(UUID id);

    List<TicketResponseDTO> getAllByCustomerId(UUID customerId);
}
