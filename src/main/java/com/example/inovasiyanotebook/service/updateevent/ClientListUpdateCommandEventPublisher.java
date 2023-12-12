package com.example.inovasiyanotebook.service.updateevent;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ClientListUpdateCommandEventPublisher {

    private final ApplicationEventPublisher applicationEventPublisher;

    public void updateClientList() {
        applicationEventPublisher.publishEvent(new ClientListUpdateCommandEvent());
    }

}
