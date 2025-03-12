package com.turkcell.customer_service.service;

import com.turkcell.customer_service.entity.Customer;
import com.turkcell.customer_service.repository.CustomerRepository;
import com.turkcell.customer_service.rules.CustomerBusinessRules;
import io.github.bothuany.dtos.customer.CustomerResponseDTO;
import io.github.bothuany.dtos.customer.CustomerCreateDTO;
import io.github.bothuany.dtos.notification.EmailNotificationDTO;
import io.github.bothuany.dtos.notification.PushNotificationDTO;
import io.github.bothuany.dtos.notification.SmsNotificationDTO;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerBusinessRules customerBusinessRules;
    private final StreamBridge streamBridge;
    private static final Logger logger = LoggerFactory.getLogger(CustomerServiceImpl.class);

    @Override
    @Transactional
    public CustomerResponseDTO createCustomer(CustomerCreateDTO request) {
        customerBusinessRules.checkIfEmailExists(request.getEmail());
        customerBusinessRules.checkIfPhoneExists(request.getPhone());

        Customer customer = new Customer();
        updateCustomerFromRequest(customer, request);

        // Email Bildirimi Gönder
        //sendEmailNotification(customer);
        // SMS Bildirimi Gönder
        //sendSmsNotification(customer);
        // Push Bildirimi Gönder
        //sendPushNotification(customer);

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
        sendPushNotification(customer);
        updateCustomerFromRequest(customer, request);
        return convertToResponse(customerRepository.save(customer));
    }

    @Override
    @Transactional
    public void deleteCustomer(UUID id) {
        customerBusinessRules.checkIfCustomerExists(id);
        customerRepository.deleteById(id);
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
    private void sendEmailNotification(Customer customer) {
        EmailNotificationDTO emailNotificationDTO = new EmailNotificationDTO();
        emailNotificationDTO.setEmail(customer.getEmail());
        emailNotificationDTO.setSubject("Welcome to Our Service!");
        emailNotificationDTO.setMessage("Dear " + customer.getFirstName() + " " + customer.getLastName() + ",\n\nThank you for registering with us!");
        logger.info("Sending email notification: {}", emailNotificationDTO);
        streamBridge.send("emailNotification-out-0", emailNotificationDTO);
    }

    private void sendSmsNotification(Customer customer) {
        SmsNotificationDTO smsNotificationDTO = new SmsNotificationDTO();
        smsNotificationDTO.setPhoneNumber(customer.getPhone());
        smsNotificationDTO.setMessage("Welcome " + customer.getFirstName() + "! Thank you for registering.");
        logger.info("Sending SMS notification: {}", smsNotificationDTO);
        streamBridge.send("smsNotification-out-0", smsNotificationDTO);
    }

    private void sendPushNotification(Customer customer) {
        PushNotificationDTO pushNotificationDTO = new PushNotificationDTO();
        pushNotificationDTO.setUserId(customer.getId());
        pushNotificationDTO.setTitle("Welcome to Our Service!");
        //pushNotificationDTO.setDeviceToken("EXAMPLE_DEVICE_TOKEN"); // Gerçek token buraya gelmeli
        pushNotificationDTO.setMessage("Welcome, " + customer.getFirstName() + "!");
        logger.info("Sending Push Notification to user: {} | Title: {} | Message: {}",
                pushNotificationDTO.getUserId(), pushNotificationDTO.getTitle(), pushNotificationDTO.getMessage());
        streamBridge.send("pushNotification-out-0", pushNotificationDTO);
    }

}