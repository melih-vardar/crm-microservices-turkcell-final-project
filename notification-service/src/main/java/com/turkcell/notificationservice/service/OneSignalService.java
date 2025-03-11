package com.turkcell.notificationservice.service;


import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.bothuany.dtos.notification.PushNotificationDTO;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


import java.util.HashMap;
import java.util.Map;

/*@Service
@RequiredArgsConstructor
public class OneSignalService {

    private static final Logger logger = LoggerFactory.getLogger(OneSignalService.class);


    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;


    public void sendPushNotification(PushNotificationDTO pushNotificationDTO) {
        try {
            // Parametreyi array (liste) olarak gönderiyoruz.
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("app_id", oneSignalAppId);

            // 'include_external_user_ids' array olarak gönderiliyor.
            requestBody.put("include_external_user_ids", new String[] { pushNotificationDTO.getUserId().toString() });

            requestBody.put("headings", Map.of("en", pushNotificationDTO.getTitle()));
            requestBody.put("contents", Map.of("en", pushNotificationDTO.getMessage()));

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Basic " + oneSignalApiKey);

            HttpEntity<String> request = new HttpEntity<>(objectMapper.writeValueAsString(requestBody), headers);

            ResponseEntity<String> response = restTemplate.exchange(oneSignalApiUrl, HttpMethod.POST, request, String.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                logger.info("Push notification sent successfully to user: {}", pushNotificationDTO.getUserId());
            } else {
                logger.error("Failed to send push notification. Response: {}", response.getBody());
            }
        } catch (Exception e) {
            logger.error("Error sending push notification: {}", e.getMessage(), e);
        }
    }
}*/