package com.example.inovasiyanotebook.service.entityservices.iml;

import com.example.inovasiyanotebook.model.changetask.ChangeTask;
import com.example.inovasiyanotebook.repository.ChangeTaskRepository;
import com.example.inovasiyanotebook.service.entityservices.CRUDService;
import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

    @Transactional
    public Optional<ChangeTask> getByIdWithItems(Long id) {
        var entityOpt = repository.findById(id);
        entityOpt.ifPresent(entity -> {
            entity.getItems().forEach(Hibernate::initialize);
        });
        return entityOpt;
    }

    @Override
    public List<ChangeTask> getAll() {
        return repository.findAll();
    }

    @Transactional
    public List<ChangeTask> getAllWithItems() {
        return repository.findAll().stream()
                .peek(changeTask -> Hibernate.initialize(changeTask.getItems()))
                .toList();
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
