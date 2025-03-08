package io.github.bothuany.dtos.notification;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SmsNotificationDTO {
    @NotBlank(message = "Phone number cannot be empty")
    @Pattern(regexp = "^(\\+90|0)?[0-9]{10}$", message = "Please provide a valid Turkish phone number")
    private String phoneNumber;

    @NotBlank(message = "Message cannot be empty")
    @Size(min = 1, max = 160, message = "Message must be between 1 and 160 characters")
    private String message;
}