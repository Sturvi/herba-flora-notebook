package com.example.inovasiyanotebook.service.entityservices.iml;

import com.example.inovasiyanotebook.model.Note;
import com.example.inovasiyanotebook.model.client.Client;
import com.example.inovasiyanotebook.repository.NoteRepository;
import com.example.inovasiyanotebook.service.entityservices.CRUDService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

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

    public List<Note> getAllByClient (Client client) {
        return noteRepository.findAllByClient(client);
    }

    public Page<Note> getAllByClientWithPagination(Client client, int pageNumber) {
        // Здесь '10' - это размер страницы
        return noteRepository.findAllByClientWithPaginationAndSorting(client, PageRequest.of(pageNumber, 10));
    }
}
