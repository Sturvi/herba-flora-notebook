package com.example.inovasiyanotebook.repository;

import com.example.inovasiyanotebook.model.Note;
import com.example.inovasiyanotebook.model.Product;
import com.example.inovasiyanotebook.model.client.Category;
import com.example.inovasiyanotebook.model.client.Client;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface NoteRepository extends JpaRepository<Note, Long> {
    List<Note> findAllByClient(Client client);

    @Query("SELECT n FROM Note n WHERE n.client = :client ORDER BY n.isPinned DESC, n.createdAt DESC")
    Page<Note> getNotesByClient(Client client, Pageable pageable);

    @Query("SELECT n FROM Note n WHERE n.category IN :categories ORDER BY n.isPinned DESC, n.createdAt DESC")
    Page<Note> getNotesByCategories(Pageable pageable, Category... categories);

    @Query("SELECT n " +
            "FROM Note n " +
            "WHERE n.product = :product " +
            "OR (n.client = :client AND n.category = null) " +
            "OR (n.category IN :categories AND n.client = null ) " +
            "OR (n.client = :client AND n.category IN :categories)" +
            "ORDER BY n.isPinned DESC, n.createdAt DESC")
    Page<Note> getNotesByProductClientCategories(Product product, Client client, Collection<Category> categories, Pageable pageable);

    @Query("SELECT n " +
            "FROM Note n " +
            "WHERE n.product IN :products " +
            "OR (n.client IN :clients AND n.category = null) " +
            "OR (n.category IN :categories AND n.client = null ) " +
            "OR (n.client IN :clients AND n.category IN :categories)" +
            "ORDER BY n.isPinned DESC, n.createdAt DESC")
    Page<Note> getNotesByProductsClientsCategories(Collection<Product> products, Collection<Client> clients, Collection<Category> categories, Pageable pageable);
}
