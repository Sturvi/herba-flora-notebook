package com.example.inovasiyanotebook.service.entityservices;

import com.example.inovasiyanotebook.model.ProductExtraInfo;
import com.example.inovasiyanotebook.repository.ProductExtraInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductExtraInfoService implements CRUDService<ProductExtraInfo> {
    private final ProductExtraInfoRepository productExtraInfoRepository;

    @Override
    public ProductExtraInfo create(ProductExtraInfo entity) {
        if (entity == null) {
            throw new IllegalArgumentException("Entity must not be null");
        }
        return productExtraInfoRepository.save(entity);
    }

    @Override
    public Optional<ProductExtraInfo> getById(Long id) {
        return productExtraInfoRepository.findById(id);
    }

    @Override
    public List<ProductExtraInfo> getAll() {
        return productExtraInfoRepository.findAll();
    }

    public List<ProductExtraInfo> getAllByProductId(Long productId) {
        return productExtraInfoRepository.findAllByProductId(productId);
    }

    @Transactional
    @Override
    public ProductExtraInfo update(ProductExtraInfo entity) {
        if (entity == null || entity.getId() == null) {
            throw new IllegalArgumentException("Entity or ID must not be null");
        }

        if (productExtraInfoRepository.existsById(entity.getId())) {
            return productExtraInfoRepository.save(entity);
        } else {
            throw new IllegalArgumentException("Entity with ID " + entity.getId() + " does not exist");
        }
    }

    @Override
    public void delete(ProductExtraInfo entity) {
        if (entity == null || entity.getId() == null) {
            throw new IllegalArgumentException("Entity or ID must not be null");
        }
        if (!productExtraInfoRepository.existsById(entity.getId())) {
            throw new IllegalArgumentException("Entity with ID " + entity.getId() + " does not exist");
        }
        productExtraInfoRepository.delete(entity);
    }
}
