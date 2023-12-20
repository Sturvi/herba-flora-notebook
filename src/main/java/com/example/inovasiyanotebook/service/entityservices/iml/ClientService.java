package com.example.inovasiyanotebook.service.entityservices.iml;

import com.example.inovasiyanotebook.model.client.Client;
import com.example.inovasiyanotebook.repository.ClientRepository;
import com.example.inovasiyanotebook.service.entityservices.CRUDService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ClientService implements CRUDService<Client> {
    private final ClientRepository clientRepository;


    public Client create(Client client) {
        return clientRepository.save(client);
    }

    @Override
    public Optional<Client> getById(Long id) {
        return clientRepository.findById(id);
    }

    @Override
    public List<Client> getAll() {
        return clientRepository.findAll();
    }

    @Override
    public Client update(Client entity) {
        return clientRepository.save(entity);
    }

    @Override
    public void delete(Client entity) {
        clientRepository.delete(entity);
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

    public void deleteClient(Client client) {
        clientRepository.delete(client);
    }
}
