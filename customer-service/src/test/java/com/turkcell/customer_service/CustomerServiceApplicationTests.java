package com.turkcell.customer_service;

import com.turkcell.customer_service.entity.Customer;
import com.turkcell.customer_service.repository.CustomerRepository;
import com.turkcell.customer_service.rules.CustomerBusinessRules;
import com.turkcell.customer_service.service.CustomerServiceImpl;
import io.github.bothuany.dtos.customer.CustomerCreateDTO;
import io.github.bothuany.dtos.customer.CustomerResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CustomerServiceApplicationTests {

	@Mock
	private CustomerRepository customerRepository;

	@Mock
	private CustomerBusinessRules customerBusinessRules;

	@InjectMocks
	private CustomerServiceImpl customerService;

	private UUID customerId;
	private CustomerCreateDTO customerCreateDTO;
	private Customer customer;

	@BeforeEach
	public void setUp() {
		customerId = UUID.randomUUID();
		customerCreateDTO = new CustomerCreateDTO(
				"John",
				"Doe",
				"john.doe@example.com",
				"5551234567",
				"Test Address 123");
		customer = new Customer();
		customer.setId(customerId);
		customer.setFirstName(customerCreateDTO.getFirstName());
		customer.setLastName(customerCreateDTO.getLastName());
		customer.setEmail(customerCreateDTO.getEmail());
		customer.setPhone(customerCreateDTO.getPhone());
		customer.setAddress(customerCreateDTO.getAddress());
		customer.setCreatedAt(LocalDateTime.now());
		customer.setUpdatedAt(LocalDateTime.now());
	}

	@Test
	public void testCreateCustomer() {
		when(customerRepository.save(any(Customer.class))).thenReturn(customer);

		CustomerResponseDTO response = customerService.createCustomer(customerCreateDTO);

		assertNotNull(response);
		assertEquals(customerCreateDTO.getFirstName(), response.getFirstName());
		assertEquals(customerCreateDTO.getLastName(), response.getLastName());
		assertEquals(customerCreateDTO.getEmail(), response.getEmail());
		assertEquals(customerCreateDTO.getPhone(), response.getPhone());
		verify(customerRepository, times(1)).save(any(Customer.class));
	}

	@Test
	public void testGetCustomerById() {
		when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));

		CustomerResponseDTO response = customerService.getCustomer(customerId);

		assertNotNull(response);
		assertEquals(customer.getFirstName(), response.getFirstName());
		assertEquals(customer.getLastName(), response.getLastName());
		assertEquals(customer.getEmail(), response.getEmail());
		assertEquals(customer.getPhone(), response.getPhone());
		verify(customerRepository, times(1)).findById(customerId);
	}

	@Test
	public void testUpdateCustomer() {
		when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));
		when(customerRepository.save(any(Customer.class))).thenReturn(customer);

		CustomerCreateDTO updatedDTO = new CustomerCreateDTO(
				"John Updated",
				"Doe Updated",
				"john.updated@example.com",
				"5559876543",
				"Updated Address 456");
		CustomerResponseDTO response = customerService.updateCustomer(customerId, updatedDTO);

		assertNotNull(response);
		assertEquals(updatedDTO.getFirstName(), response.getFirstName());
		assertEquals(updatedDTO.getLastName(), response.getLastName());
		assertEquals(updatedDTO.getEmail(), response.getEmail());
		assertEquals(updatedDTO.getPhone(), response.getPhone());
		verify(customerRepository, times(1)).findById(customerId);
		verify(customerRepository, times(1)).save(any(Customer.class));
	}

	@Test
	public void testDeleteCustomer() {
		when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));
		doNothing().when(customerBusinessRules).checkIfCustomerExists(customerId);

		customerService.deleteCustomer(customerId);

		verify(customerRepository, times(1)).findById(customerId);
		verify(customerRepository, times(1)).deleteById(customerId);
	}
}
