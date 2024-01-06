package com.example.inovasiyanotebook.service.entityservices.iml;

import com.example.inovasiyanotebook.model.Note;
import com.example.inovasiyanotebook.model.Product;
import com.example.inovasiyanotebook.model.client.Category;
import com.example.inovasiyanotebook.model.client.Client;
import com.example.inovasiyanotebook.model.order.Order;
import com.example.inovasiyanotebook.repository.NoteRepository;
import com.example.inovasiyanotebook.service.entityservices.CRUDService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class NoteService implements CRUDService<Note> {
    private final NoteRepository noteRepository;

    @Override
    public Note create(Note entity) {
        return noteRepository.save(entity);
    }

    @Override
    public Optional<Note> getById(Long id) {
        return noteRepository.findById(id);
    }

    @Override
    public List<Note> getAll() {
        return noteRepository.findAll();
    }

    @Override
    public Note update(Note entity) {
        return noteRepository.save(entity);
    }

    @Override
    public void delete(Note entity) {
        noteRepository.delete(entity);
    }

    public List<Note> getAllByClient(Client client) {
        return noteRepository.findAllByClient(client);
    }

    public Page<Note> getAllByClientWithPagination(Client client, int pageNumber) {
        // Здесь '10' - это размер страницы
        return noteRepository.getNotesByClient(client, PageRequest.of(pageNumber, 10));
    }

    public Page<Note> getAllByCategoryWithPagination(Category category, int pageNumber) {
        if (category.hasParent()){
            return noteRepository.getNotesByCategories(PageRequest.of(pageNumber, 10), category, category.getParentCategory());
        } else {
            return noteRepository.getNotesByCategories(PageRequest.of(pageNumber, 10), category);
        }
    }

    public Page<Note> getAllByProductWithPagination (Product product, int pageNumber) {
        var categoriesList = new ArrayList<Category>();
        if (product.getCategory() != null) categoriesList.add(product.getCategory());
        if (product.getCategory() != null && product.getCategory().getParent() != null) categoriesList.add(product.getCategory().getParentCategory());

        return noteRepository.getNotesByProductClientCategories(product, product.getClient(), categoriesList,PageRequest.of(pageNumber, 10));
    }

    public Page<Note> getAllByOrderWithPagination(Order order, int pageNumber) {
        var products = order.getProducts();
        var clients = products
                .stream()
                .map(Product::getClient)
                .distinct()
                .toList();
        var categories = products.stream()
                .flatMap(product -> {
                    Category category = product.getCategory();
                    return category.getParent() != null
                            ? Stream.of(category, (Category) category.getParent())
                            : Stream.of(category);
                })
                .distinct()
                .toList();

        return noteRepository.getNotesByProductsClientsCategories(products, clients, categories, PageRequest.of(pageNumber, 10));
    }

}
