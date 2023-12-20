package com.example.inovasiyanotebook.service.entityservices.iml;

import com.example.inovasiyanotebook.model.client.Category;
import com.example.inovasiyanotebook.repository.CategoryRepository;
import com.example.inovasiyanotebook.service.entityservices.CRUDService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CategoryService implements CRUDService<Category> {
    private final CategoryRepository categoryRepository;

    @Override
    public Category create(Category entity) {
        return categoryRepository.save(entity);
    }

    @Override
    public Optional<Category> getById(Long id) {
        return Optional.empty();
    }

    public List<Category> getAll () {
        return categoryRepository.findAll();
    }

    @Override
    public Category update(Category entity) {
        return categoryRepository.save(entity);
    }

    @Override
    public void delete(Category entity) {
        categoryRepository.delete(entity);
    }

    public List<Category> getAllParentCategories() {
        return categoryRepository.findByParentCategoryIsNull();
    }
}
