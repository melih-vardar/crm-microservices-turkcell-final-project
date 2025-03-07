package com.turkcell.notificationservice.service;

import io.github.bothuany.dtos.notification.EmailNotificationDTO;
import io.github.bothuany.dtos.notification.PushNotificationDTO;
import io.github.bothuany.dtos.notification.SmsNotificationDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class NotificationServiceImpl implements NotificationService {

    private static final Logger logger = LoggerFactory.getLogger(NotificationServiceImpl.class);
    private final JavaMailSender javaMailSender;
    private final TwilioService twilioService;
    public NotificationServiceImpl(JavaMailSender javaMailSender, TwilioService twilioService) {
        this.javaMailSender = javaMailSender;
        this.twilioService = twilioService;
    }

    @Override
    public void sendEmail(EmailNotificationDTO emailNotificationDTO) {

        try {
            // E-mail gönderimi için MimeMessage oluşturuluyor
            var mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);

            // E-mail bilgileri ayarlanıyor
            helper.setTo(emailNotificationDTO.getEmail());
            helper.setSubject(emailNotificationDTO.getSubject());
            helper.setText(emailNotificationDTO.getMessage(), true);

            // E-mail gönderimi
            javaMailSender.send(mimeMessage);

            logger.info("Email sent to: {} | Subject: {} | Message: {}",
                    emailNotificationDTO.getEmail(),
                    emailNotificationDTO.getSubject(),
                    emailNotificationDTO.getMessage());

        } catch (Exception e) {
            logger.error("Failed to send email to: {}", emailNotificationDTO.getEmail(), e);
        }
    }

    @Override
    public void sendSms(SmsNotificationDTO smsNotificationDTO) {
        //"phoneNumber": "+905555555555" example phone number value
        logger.info("Sending SMS to: {} | Message: {}",
                smsNotificationDTO.getPhoneNumber(),
                smsNotificationDTO.getMessage());

        String NewPhoneNumber = "+" + smsNotificationDTO.getPhoneNumber();

        twilioService.sendSms(smsNotificationDTO.getPhoneNumber(), NewPhoneNumber);
    }

    @Override
    public void sendPushNotification(PushNotificationDTO pushNotificationDTO) {
        String message = " notification sent to user: " + pushNotificationDTO.getUserId() +
                " | Title: " + pushNotificationDTO.getTitle() +
                " | Message: " + pushNotificationDTO.getMessage();

        logger.info(message);

        // firebase gibi yapılar kullanılabilir fakat son gelen güncellemeyle desteklememeye başlamış localde çalıştırabiliriz.
        // administration json olarak bir dosya iniyor bunu path olarak vermemiz gerekli container işlemleri yüzünden pek mantıklı gelmedi.
        // dockerda volume de tutabiliriz değeri. başka bilgisayarlarda çalışırken sıkıntı olabilirç

    }
}
