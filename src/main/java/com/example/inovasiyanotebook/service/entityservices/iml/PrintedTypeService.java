package com.example.inovasiyanotebook.service.entityservices.iml;

import com.example.inovasiyanotebook.model.order.PrintedType;
import com.example.inovasiyanotebook.repository.PrintedTypeRepository;
import com.example.inovasiyanotebook.service.entityservices.CRUDService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PrintedTypeService implements CRUDService<PrintedType> {
    private final PrintedTypeRepository printedTypeRepository;


    @Override
    public PrintedType create(PrintedType entity) {
        return printedTypeRepository.save(entity);
    }

    @Override
    public Optional<PrintedType> getById(Long id) {
        return printedTypeRepository.findById(id);
    }

    @Override
    public List<PrintedType> getAll() {
        return printedTypeRepository.findAll();
    }

    @Override
    public PrintedType update(PrintedType entity) {
        return printedTypeRepository.save(entity);
    }

    @Override
    public void delete(PrintedType entity) {
        printedTypeRepository.delete(entity);
    }
}
