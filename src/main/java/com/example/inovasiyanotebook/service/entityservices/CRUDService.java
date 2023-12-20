package com.example.inovasiyanotebook.service.entityservices;

import java.util.List;
import java.util.Optional;

public interface CRUDService<T> {
    T create (T entity);

    Optional<T> getById(Long id);

    List<T> getAll();

    T update(T entity);

    void delete(T entity);
}

