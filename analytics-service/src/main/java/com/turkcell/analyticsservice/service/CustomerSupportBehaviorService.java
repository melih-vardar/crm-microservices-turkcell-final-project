package com.turkcell.analyticsservice.service;

import com.turkcell.analyticsservice.dto.ForUserDto.TicketExampleDto;

public interface CustomerSupportBehaviorService {
    public void processTicketCreation(TicketExampleDto event);
    public void processTicketUpdate(TicketExampleDto event);
    public void processTicketClosure(TicketExampleDto event);

}
