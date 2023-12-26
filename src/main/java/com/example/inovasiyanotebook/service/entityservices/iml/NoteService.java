package com.example.inovasiyanotebook.service.entityservices.iml;

import com.example.inovasiyanotebook.model.Note;
import com.example.inovasiyanotebook.model.Product;
import com.example.inovasiyanotebook.model.client.Category;
import com.example.inovasiyanotebook.model.client.Client;
import com.example.inovasiyanotebook.repository.NoteRepository;
import com.example.inovasiyanotebook.service.entityservices.CRUDService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
        return noteRepository.findAllByClientWithPaginationAndSorting(client, PageRequest.of(pageNumber, 10));
    }

    public Page<Note> getAllByCategoryWithPagination(Category category, int pageNumber) {
        if (category.hasParent()){
            return noteRepository.findByCategoriesWithPaginationAndSorting(PageRequest.of(pageNumber, 10), category, category.getParentCategory());
        } else {
            return noteRepository.findByCategoriesWithPaginationAndSorting(PageRequest.of(pageNumber, 10), category);
        }
    }

    public Page<Note> getAllByProductWithPagination (Product product, int pageNumber) {
        var categoriesList = new ArrayList<Category>();
        if (product.getCategory() != null) categoriesList.add(product.getCategory());
        if (product.getCategory() != null && product.getCategory().getParent() != null) categoriesList.add(product.getCategory().getParentCategory());

        return noteRepository.findByProductClientAndCategoriesWithPaginationAndSorting(product, product.getClient(), categoriesList,PageRequest.of(pageNumber, 10));
    }
}
