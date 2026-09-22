package com.example.inovasiyanotebook.repository.specification;

import com.example.inovasiyanotebook.model.Note;
import com.example.inovasiyanotebook.model.Product;
import com.example.inovasiyanotebook.model.client.Category;
import com.example.inovasiyanotebook.model.client.Client;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Заметки, относящиеся к набору продуктов: те же правила, что и в {@code NoteRepository.getNotesByProductsClientsCategories},
 * но пустые коллекции не ломают запрос.
 */
public final class NoteSpecifications {

    private NoteSpecifications() {
    }

    /**
     * note.product IN products
     * OR (note.client IN clients AND note.category IS NULL)
     * OR (note.category IN categories AND note.client IS NULL)
     * OR (note.client IN clients AND note.category IN categories)
     */
    public static Specification<Note> forProductsClientsCategories(Collection<Product> products,
                                                                   Collection<Client> clients,
                                                                   Collection<Category> categories) {
        return (root, query, cb) -> {
            List<Predicate> any = new ArrayList<>();
            if (!products.isEmpty()) {
                any.add(root.get("product").in(products));
            }
            if (!clients.isEmpty()) {
                any.add(cb.and(root.get("client").in(clients), cb.isNull(root.get("category"))));
            }
            if (!categories.isEmpty()) {
                any.add(cb.and(root.get("category").in(categories), cb.isNull(root.get("client"))));
            }
            if (!clients.isEmpty() && !categories.isEmpty()) {
                any.add(cb.and(root.get("client").in(clients), root.get("category").in(categories)));
            }
            return any.isEmpty() ? cb.disjunction() : cb.or(any.toArray(Predicate[]::new));
        };
    }
}
