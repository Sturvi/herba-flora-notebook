package com.example.inovasiyanotebook.service.entityservices.iml;

import com.example.inovasiyanotebook.model.changetask.ChangeItemStatus;
import com.example.inovasiyanotebook.model.changetask.ChangeTaskItem;
import com.example.inovasiyanotebook.repository.ChangeTaskItemRepository;
import com.example.inovasiyanotebook.service.entityservices.CRUDService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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
        if (entity.getStatus() == ChangeItemStatus.DONE && entity.getCompletedAt() == null) {
            entity.setCompletedAt(LocalDateTime.now());
        }

        if (entity.getStatus() == ChangeItemStatus.PENDING && entity.getCompletedAt() != null) {
            entity.setCompletedAt(null);
        }
        return repository.save(entity);
    }

    @Override
    public void delete(ChangeTaskItem entity) {
        repository.delete(entity);
    }
}
