package com.example.inovasiyanotebook.model.client;

import com.example.inovasiyanotebook.service.ClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ClientMapper {
    private final ClientService clientService;

    public Client creatNewClient (String name, String email, String phoneNumber, String voen) {
        Integer maxSortOrder = clientService.getMaxSortOrder();

        return Client.builder()
                .name(name)
                .email(email)
                .phoneNumber(phoneNumber)
                .voen(voen)
                .sortOrder(maxSortOrder + 1)
                .build();
    }
}
