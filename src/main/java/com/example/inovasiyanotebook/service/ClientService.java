package com.example.inovasiyanotebook.service;

import com.example.inovasiyanotebook.model.client.Client;
import com.example.inovasiyanotebook.repository.ClientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

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

    public List<Client> fetchAllClients () {
        return clientRepository.findAll();
    }

    public Optional<Client> findById(Long id) {
        return clientRepository.findById(id);
    }

    public void updateClientPosition(Client previousClient, Client client) {
        var clientsList = new ArrayList<>(fetchAllClients());

        client.setSortOrder(previousClient == null ? 1 : previousClient.getSortOrder() + 1);
        int currentCount = client.getSortOrder() + 1;

        clientsList.sort(Comparator.comparing(Client::getSortOrder, Comparator.nullsLast(Integer::compareTo)));

        for (Client cl : clientsList) {
            if (cl.getSortOrder() != null && cl.getSortOrder() >= client.getSortOrder() && !cl.equals(client)) {
                cl.setSortOrder(++currentCount);
                clientRepository.save(cl);
            }
        }

        clientRepository.save(client);
    }

}
