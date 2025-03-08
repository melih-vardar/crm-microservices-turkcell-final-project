package com.turkcell.notificationservice.service;

import com.twilio.Twilio;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import com.twilio.rest.api.v2010.account.Message;

@Service
public class TwilioService {

    @Value("${twilio.accountSid}")
    private String accountSid;

    @Value("${twilio.authToken}")
    private String authToken;

    @Value("${twilio.phoneNumber}")
    private String twilioPhoneNumber;

    public void sendSms(String toPhoneNumber, String messageBody) {
        Twilio.init(accountSid, authToken);

        Message message = Message.creator(
                new com.twilio.type.PhoneNumber(toPhoneNumber),
                new com.twilio.type.PhoneNumber(twilioPhoneNumber),
                messageBody
        ).create();

        System.out.println("Message sent with twilioPhoneNumber: " + twilioPhoneNumber);
    }
}
