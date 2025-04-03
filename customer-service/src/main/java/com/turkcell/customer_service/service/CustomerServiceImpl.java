package com.turkcell.customer_service.service;

import com.turkcell.customer_service.client.BillingClient;
import com.turkcell.customer_service.client.ContractClient;
import com.turkcell.customer_service.entity.Customer;
import com.turkcell.customer_service.repository.CustomerRepository;
import com.turkcell.customer_service.rules.CustomerBusinessRules;
import io.github.bothuany.dtos.customer.CustomerResponseDTO;
import io.github.bothuany.dtos.customer.CustomerCreateDTO;
import io.github.bothuany.event.analytics.CreateExampleCustomerEvent;
import io.github.bothuany.event.notification.EmailNotificationEvent;
import io.github.bothuany.event.notification.PushNotificationEvent;
import io.github.bothuany.event.notification.SmsNotificationEvent;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerBusinessRules customerBusinessRules;
    private final StreamBridge streamBridge;
    private static final Logger logger = LoggerFactory.getLogger(CustomerServiceImpl.class);
    private final BillingClient billingClient;
    private final ContractClient contractClient;

    @Override
    @Transactional
    public CustomerResponseDTO createCustomer(CustomerCreateDTO request) {
        customerBusinessRules.checkIfEmailExists(request.getEmail());
        customerBusinessRules.checkIfPhoneExists(request.getPhone());

        Customer customer = new Customer();
        updateCustomerFromRequest(customer, request);

        // Email Bildirimi Gönder
        // sendEmailNotification(customer,"Welcome to Our Service!","Dear " +
        // customer.getFirstName() + " " + customer.getLastName() + ",\n\nThank you for
        // registering with us!");
        // SMS Bildirimi Gönder
        // sendSmsNotification(customer,"Welcome " + customer.getFirstName() + "! Thank
        // you for registering.");
        // Push Bildirimi Gönder
        // sendPushNotification(customer,"Welcome to Our Service!","Welcome, " +
        // customer.getFirstName() + "!");

        return convertToResponse(customerRepository.save(customer));
    }

    @Override
    public CustomerResponseDTO getCustomer(UUID id) {
        customerBusinessRules.checkIfCustomerExists(id);
        return convertToResponse(customerRepository.findById(id).get());
    }

    @Override
    @Transactional
    public CustomerResponseDTO updateCustomer(UUID id, CustomerCreateDTO request) {
        customerBusinessRules.checkIfCustomerExists(id);
        customerBusinessRules.checkIfEmailExistsForUpdate(id, request.getEmail());
        customerBusinessRules.checkIfPhoneExistsForUpdate(id, request.getPhone());
        Customer customer = customerRepository.findById(id).get();
        updateCustomerFromRequest(customer, request);

        // sendPushNotification(customer);
        // sendEmailNotification(customer,"Update the customer","Dear " +
        // customer.getFirstName() + " " + customer.getLastName() + ",\n\n Updated is
        // Succesfully!");

        return convertToResponse(customerRepository.save(customer));
    }

    @Override
    @Transactional
    public void deleteCustomer(UUID id) {
        customerBusinessRules.checkIfCustomerExists(id);
        Customer customer = customerRepository.findById(id).get();
        // sendEmailNotification(customer,"Delete the customer","Dear " +
        // customer.getFirstName() + " " + customer.getLastName() + ",\n\n Delete is
        // Succesfully!");
        customerRepository.deleteById(id);

    }

    public List<Object> getCustomerBills(UUID customerId) {
        return billingClient.getCustomerBills(customerId);
    }

    public List<Object> getCustomerContracts(UUID customerId) {
        return contractClient.getCustomerContracts(customerId);
    }
    private void updateCustomerFromRequest(Customer customer, CustomerCreateDTO request) {
        customer.setFirstName(request.getFirstName());
        customer.setLastName(request.getLastName());
        customer.setEmail(request.getEmail());
        customer.setPhone(request.getPhone());
        customer.setAddress(request.getAddress());
    }

    private CustomerResponseDTO convertToResponse(Customer customer) {
        CustomerResponseDTO response = new CustomerResponseDTO();
        response.setId(customer.getId());
        response.setFirstName(customer.getFirstName());
        response.setLastName(customer.getLastName());
        response.setEmail(customer.getEmail());
        response.setPhone(customer.getPhone());
        return response;
    }

    private void sendEmailNotification(Customer customer, String subject, String message) {
        EmailNotificationEvent emailNotificationEvent = new EmailNotificationEvent();
        emailNotificationEvent.setEmail(customer.getEmail());
        emailNotificationEvent.setSubject(subject);
        emailNotificationEvent.setMessage(message);
        logger.info("Sending email notification: {}", emailNotificationEvent);
        streamBridge.send("emailNotification-out-0", emailNotificationEvent);
    }

    private void sendSmsNotification(Customer customer, String message) {
        SmsNotificationEvent smsNotificationEvent = new SmsNotificationEvent();
        smsNotificationEvent.setPhoneNumber(customer.getPhone());
        smsNotificationEvent.setMessage(message);
        logger.info("Sending SMS notification: {}", smsNotificationEvent);
        streamBridge.send("smsNotification-out-0", smsNotificationEvent);
    }

    private void sendPushNotification(Customer customer, String title, String message) {
        PushNotificationEvent pushNotificationEvent = new PushNotificationEvent();
        pushNotificationEvent.setUserId(customer.getId());
        pushNotificationEvent.setTitle(title);
        // pushNotificationDTO.setDeviceToken("EXAMPLE_DEVICE_TOKEN"); // Gerçek token
        // buraya gelmeli
        pushNotificationEvent.setMessage(message);
        logger.info("Sending Push Notification to user: {} | Title: {} | Message: {}",
                pushNotificationEvent.getUserId(), pushNotificationEvent.getTitle(),
                pushNotificationEvent.getMessage());
        streamBridge.send("pushNotification-out-0", pushNotificationEvent);
    }

    private void sendCustomerAnalytics(Customer customer) {

        CreateExampleCustomerEvent createExampleCustomerEvent = new CreateExampleCustomerEvent();
        createExampleCustomerEvent.setCustomerId(customer.getId());
        createExampleCustomerEvent.setFirstname(customer.getFirstName());
        createExampleCustomerEvent.setLastname(customer.getLastName());
        createExampleCustomerEvent.setEmail(customer.getEmail());
        createExampleCustomerEvent.setEventType("CUSTOMER_CREATE");
        createExampleCustomerEvent.setEventTime(LocalDateTime.now());
        logger.info("Sending Customer Analytics event: {}", createExampleCustomerEvent);
        streamBridge.send("CreateCustomerAnalytics-out-0", createExampleCustomerEvent);

    }

}