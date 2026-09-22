package com.example.inovasiyanotebook.service.entityservices.iml;

import com.example.inovasiyanotebook.dto.ChangeTaskSummaryDTO;
import com.example.inovasiyanotebook.model.changetask.ChangeTask;
import com.example.inovasiyanotebook.repository.ChangeTaskRepository;
import com.example.inovasiyanotebook.service.entityservices.CRUDService;
import com.vaadin.flow.data.provider.Query;
import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.data.domain.PageRequest;
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

    /**
     * Страница строк грида задач; агрегаты по позициям и порядок считаются в БД.
     */
    @Transactional(readOnly = true)
    public List<ChangeTaskSummaryDTO> fetchSummaries(String term, Query<ChangeTaskSummaryDTO, ?> query) {
        return repository.findSummaries(likePattern(term), PageRequest.of(query.getPage(), query.getPageSize()));
    }

    @Transactional(readOnly = true)
    public int countSummaries(String term) {
        return (int) repository.countByTaskTypeContainingIgnoreCase(term == null ? "" : term.trim());
    }

    private static String likePattern(String term) {
        return "%" + (term == null ? "" : term.trim().toLowerCase()) + "%";
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
