package com.example.inovasiyanotebook.service.entityservices.iml;

import com.example.inovasiyanotebook.repository.specification.NoteSpecifications;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;
import java.util.stream.Collectors;
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

    /**
     * Заметки для набора продуктов одним запросом, сгруппированные по id продукта.
     * Для каждого продукта действуют те же правила, что и в {@link #getAllByProductWithPagination}:
     * заметка самого продукта, заметка его клиента без категории, заметка его категории (или родительской) без клиента,
     * заметка клиента и категории вместе. Внутри группы - закреплённые сверху, затем по дате создания вниз.
     */
    @Transactional(readOnly = true)
    public Map<Long, List<Note>> getNotesGroupedByProduct(Collection<Product> products) {
        Map<Long, List<Note>> result = new HashMap<>();
        if (products.isEmpty()) {
            return result;
        }
        Set<Client> clients = products.stream().map(Product::getClient).filter(Objects::nonNull).collect(Collectors.toSet());
        Set<Category> categories = products.stream().flatMap(p -> categoriesOf(p).stream()).collect(Collectors.toSet());

        List<Note> notes = noteRepository.findAll(
                NoteSpecifications.forProductsClientsCategories(products, clients, categories),
                Sort.by(Sort.Order.desc("isPinned"), Sort.Order.desc("createdAt")));

        for (Product product : products) {
            List<Category> productCategories = categoriesOf(product);
            List<Note> matching = notes.stream()
                    .filter(note -> matchesProduct(note, product, productCategories))
                    .toList();
            result.put(product.getId(), matching);
        }
        return result;
    }

    private static List<Category> categoriesOf(Product product) {
        List<Category> categories = new ArrayList<>();
        if (product.getCategory() != null) {
            categories.add(product.getCategory());
            if (product.getCategory().getParentCategory() != null) {
                categories.add(product.getCategory().getParentCategory());
            }
        }
        return categories;
    }

    private static boolean matchesProduct(Note note, Product product, List<Category> categories) {
        if (note.getProduct() != null) {
            return note.getProduct().equals(product);
        }
        boolean sameClient = note.getClient() != null && note.getClient().equals(product.getClient());
        boolean inCategories = note.getCategory() != null && categories.contains(note.getCategory());
        if (note.getClient() != null && note.getCategory() == null) {
            return sameClient;
        }
        if (note.getCategory() != null && note.getClient() == null) {
            return inCategories;
        }
        return sameClient && inCategories;
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
