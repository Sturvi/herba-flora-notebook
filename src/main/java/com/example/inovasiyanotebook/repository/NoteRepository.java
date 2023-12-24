package com.example.inovasiyanotebook.repository;

import com.example.inovasiyanotebook.model.Note;
import com.example.inovasiyanotebook.model.client.Category;
import com.example.inovasiyanotebook.model.client.Client;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NoteRepository extends JpaRepository<Note, Long> {
    List<Note> findAllByClient(Client client);

    @Query("SELECT n FROM Note n WHERE n.client = :client ORDER BY n.isPinned DESC, n.createdAt DESC")
    Page<Note> findAllByClientWithPaginationAndSorting(Client client, Pageable pageable);

    @Query("SELECT n FROM Note n WHERE n.category IN :categories ORDER BY n.isPinned DESC, n.createdAt DESC")
    Page<Note> findByCategoriesWithPaginationAndSorting(Pageable pageable, Category... categories);

}
