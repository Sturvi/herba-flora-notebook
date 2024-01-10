package com.example.inovasiyanotebook.repository;

import com.example.inovasiyanotebook.model.Instruction;
import com.example.inovasiyanotebook.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InstructionRepository extends JpaRepository<Instruction, Long> {

    Optional<Instruction> findByProduct (Product product);
}
