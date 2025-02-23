package com.example.inovasiyanotebook.service.entityservices.iml;

import com.example.inovasiyanotebook.model.client.Category;
import com.example.inovasiyanotebook.repository.CategoryRepository;
import com.example.inovasiyanotebook.service.entityservices.CRUDService;
import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
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
        return categoryRepository.findById(id);
    }

    public List<Category> getAll() {
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

    @Transactional
    public List<Category> getAllParentCategoriesWithSubCategories() {
        // Получаем родительские категории
        List<Category> categories = categoryRepository.findByParentCategoryIsNull();
        // Инициализируем их подкатегории
        categories.forEach(this::initializeSubCategories);
        return categories;
    }


    private void initializeSubCategories(Category category) {
        if (category.getSubCategories() != null) {
            Hibernate.initialize(category.getSubCategories());
            // Рекурсивно инициализируем подкатегории текущей категории
            category.getSubCategories().forEach(this::initializeSubCategories);
        }
    }

    @Transactional
    public List<Category> getAllSortingByParent() {
        List<Category> allParentCategories = getAllParentCategories();
        List<Category> categories = new ArrayList<>();
        allParentCategories.stream()
                .sorted(Comparator.comparing(Category::getName))
                .forEach(category -> {
                    categories.add(category);
                    category.getSubCategories().stream()
                            .sorted(Comparator.comparing(Category::getName))
                            .forEach(categories::add);
                });

        return categories;
    }
}
