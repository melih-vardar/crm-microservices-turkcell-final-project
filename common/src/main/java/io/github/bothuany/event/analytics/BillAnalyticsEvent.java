package io.github.bothuany.event.analytics;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class BillAnalyticsEvent {
    String customerId;
    BigDecimal amount;
    LocalDateTime dueDate;
    private LocalDateTime createdAt;
}
