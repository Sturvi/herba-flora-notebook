package com.example.inovasiyanotebook.service.entityservices.iml;

import com.example.inovasiyanotebook.model.changetask.ChangeTask;
import com.example.inovasiyanotebook.repository.ChangeTaskRepository;
import com.example.inovasiyanotebook.service.entityservices.CRUDService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChangeTaskService implements CRUDService<ChangeTask> {

    private final ChangeTaskRepository repository;


    @Override
    public ChangeTask create(ChangeTask entity) {
        return repository.save(entity);
    }

    @Override
    public Optional<ChangeTask> getById(Long id) {
        return repository.findById(id);
    }

    @Override
    public List<ChangeTask> getAll() {
        return repository.findAll();
    }

    @Override
    public ChangeTask update(ChangeTask entity) {
        return repository.save(entity);
    }

    @Override
    public void delete(ChangeTask entity) {
        repository.delete(entity);
    }



}
