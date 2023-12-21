package com.example.inovasiyanotebook.repository;

import com.example.inovasiyanotebook.model.Note;
import com.example.inovasiyanotebook.model.client.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NoteRepository extends JpaRepository<Note, Long> {
    List<Note> findAllByClient(Client client);
}
