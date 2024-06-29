package com.example.inovasiyanotebook.service.entityservices.iml;

import com.example.inovasiyanotebook.model.order.RawOrderData;
import com.example.inovasiyanotebook.repository.RawOrderDataRepository;
import com.example.inovasiyanotebook.service.entityservices.CRUDService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RawOrderDataService implements CRUDService<RawOrderData> {
    private final RawOrderDataRepository repository;


    @Override
    public RawOrderData create(RawOrderData entity) {
        return repository.save(entity);
    }

    @Override
    public Optional<RawOrderData> getById(Long id) {
        return repository.findById(id);
    }

    @Override
    public List<RawOrderData> getAll() {
        return repository.findAll();
    }

    @Override
    public RawOrderData update(RawOrderData entity) {
        return repository.save(entity);
    }

    @Override
    public void delete(RawOrderData entity) {
        repository.delete(entity);
    }
}
