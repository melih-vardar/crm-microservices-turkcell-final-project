package com.turkcell.notificationservice.service;

import com.twilio.Twilio;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

/*@Service
public class TwilioService {

    private static final Logger logger = LoggerFactory.getLogger(TwilioService.class);


    public TwilioService() {
        Twilio.init(accountSid, authToken);
    }

    public void sendSms(String to, String message) {
        try {
            Message sms = Message.creator(
                    new PhoneNumber(to),
                    new PhoneNumber(twilioPhoneNumber),
                    message
            ).create();

            logger.info("SMS sent successfully to {}. Message SID: {}", to, sms.getSid());
        } catch (Exception e) {
            logger.error("Failed to send SMS to {}. Error: {}", to, e.getMessage(), e);
        }
    }
}
*/