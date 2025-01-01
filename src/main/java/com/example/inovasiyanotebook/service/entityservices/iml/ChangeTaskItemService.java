package com.example.inovasiyanotebook.service.entityservices.iml;

import com.example.inovasiyanotebook.model.changetask.ChangeTaskItem;
import com.example.inovasiyanotebook.repository.ChangeTaskItemRepository;
import com.example.inovasiyanotebook.service.entityservices.CRUDService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChangeTaskItemService implements CRUDService<ChangeTaskItem> {
    private final ChangeTaskItemRepository repository;

    @Override
    public ChangeTaskItem create(ChangeTaskItem entity) {
        return repository.save(entity);
    }

    @Override
    public Optional<ChangeTaskItem> getById(Long id) {
        return repository.findById(id);
    }

    @Override
    public List<ChangeTaskItem> getAll() {
        return repository.findAll();
    }

    @Override
    public ChangeTaskItem update(ChangeTaskItem entity) {
        return repository.save(entity);
    }

    @Override
    public void delete(ChangeTaskItem entity) {
        repository.delete(entity);
    }
}
