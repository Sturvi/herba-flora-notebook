package com.example.inovasiyanotebook.service.entityservices.iml;

import com.example.inovasiyanotebook.model.Instruction;
import com.example.inovasiyanotebook.model.Product;
import com.example.inovasiyanotebook.repository.InstructionRepository;
import com.example.inovasiyanotebook.service.entityservices.CRUDService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class InstructionService implements CRUDService<Instruction> {
    private final InstructionRepository instructionRepository;

    @Override
    public Instruction create(Instruction entity) {
        return instructionRepository.save(entity);
    }

    @Override
    public Optional<Instruction> getById(Long id) {
        return instructionRepository.findById(id);
    }

    @Override
    public List<Instruction> getAll() {
        return instructionRepository.findAll();
    }

    @Override
    public Instruction update(Instruction entity) {
        return instructionRepository.save(entity);
    }

    @Override
    public void delete(Instruction entity) {
        instructionRepository.delete(entity);
    }

    public Optional<Instruction> findByProduct (Product product) {
        return instructionRepository.findByProduct(product);
    }
}
