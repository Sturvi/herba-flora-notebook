package com.example.inovasiyanotebook.service.entityservices.iml;

import com.example.inovasiyanotebook.model.ProductMapping;
import com.example.inovasiyanotebook.repository.ProductMappingRepository;
import com.example.inovasiyanotebook.service.entityservices.CRUDService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductMappingService implements CRUDService<ProductMapping> {
    private final ProductMappingRepository productMappingRepository;


    @Override
    public ProductMapping create(ProductMapping entity) {
        return productMappingRepository.save(entity);
    }

    @Override
    public Optional<ProductMapping> getById(Long id) {
        return productMappingRepository.findById(id);
    }

    @Override
    public List<ProductMapping> getAll() {
        return productMappingRepository.findAll();
    }

    @Override
    public ProductMapping update(ProductMapping entity) {
        return productMappingRepository.save(entity);
    }

    @Override
    public void delete(ProductMapping entity) {
        productMappingRepository.delete(entity);
    }

    public Optional<ProductMapping> findByIncomingOrderPositionName (String incomingName) {
        return productMappingRepository.findByIncomingOrderPositionName(incomingName);
    }

    public boolean existsByIncomingOrderPositionName(String incomingOrderPositionName) {
        return productMappingRepository.existsByIncomingOrderPositionName(incomingOrderPositionName);
    }
}
