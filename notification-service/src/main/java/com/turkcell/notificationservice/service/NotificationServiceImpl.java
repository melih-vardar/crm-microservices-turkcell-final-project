

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
    //private final TwilioService twilioService;
    //private final OneSignalService oneSignalService;

    public NotificationServiceImpl(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
        //this.twilioService = twilioService;
        //this.oneSignalService = oneSignalService;
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
            logger.info(" java mailSender ustu");
            // E-mail gönderimi
            //org.springframework.mail.MailSendException: Failed messages: org.eclipse.angus.mail.smtp.SMTPSendFailedException: 530-5.7.0 Must issue a STARTTLS command first. For more information, go to
            //530-5.7.0  https://support.google.com/a/answer/3221692 and review RFC 3207
            //530 5.7.0 specifications. 5b1f17b1804b1-43bdd8da097sm130643865e9.17 - gsmtp
            javaMailSender.send(mimeMessage);
            logger.info(" java mailSender altı");
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
        //String NewPhoneNumber = "+9" + smsNotificationDTO.getPhoneNumber();

        //twilioService.sendSms(smsNotificationDTO.getPhoneNumber(), smsNotificationDTO.getMessage());
    }

    @Override
    public void sendPushNotification(PushNotificationDTO pushNotificationDTO) {
        // OneSignalService kullanarak push bildirimi gönderme
        try {
            logger.info("Sending Push Notification to user: {} | Title: {} | Message: {}",
                    pushNotificationDTO.getUserId(),
                    pushNotificationDTO.getTitle(),
                    pushNotificationDTO.getMessage());

            // OneSignal ile push bildirim gönderiyoruz
            //oneSignalService.sendPushNotification(pushNotificationDTO);
        } catch (Exception e) {
            logger.error("Failed to send push notification: {}", pushNotificationDTO, e);
        }
        // firebase gibi yapılar kullanılabilir fakat son gelen güncellemeyle desteklememeye başlamış localde çalıştırabiliriz.
        // administration json olarak bir dosya iniyor bunu path olarak vermemiz gerekli container işlemleri yüzünden pek mantıklı gelmedi.
        // dockerda volume de tutabiliriz değeri. başka bilgisayarlarda çalışırken sıkıntı olabilirç

    }
}