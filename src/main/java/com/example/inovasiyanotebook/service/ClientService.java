package com.example.inovasiyanotebook.service;

import com.example.inovasiyanotebook.model.client.Client;
import com.example.inovasiyanotebook.repository.ClientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ClientService {
    private final ClientRepository clientRepository;


    public void save (Client client) {
        clientRepository.save(client);
    }

    public Integer getMaxSortOrder (){
        return clientRepository.findMaxSortOrder() == null ? 0 : clientRepository.findMaxSortOrder();
    }
}
