package com.example.inovasiyanotebook.service.entityservices.iml;

import com.example.inovasiyanotebook.model.ProductPriceMapping;
import com.example.inovasiyanotebook.repository.ProductPriceMappingRepository;
import com.example.inovasiyanotebook.service.entityservices.CRUDService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductPriceMappingService implements CRUDService<ProductPriceMapping> {
    private final ProductPriceMappingRepository productPriceMappingRepository;

    @Override
    public ProductPriceMapping create(ProductPriceMapping entity) {
        return productPriceMappingRepository.save(entity);
    }

    @Override
    public Optional<ProductPriceMapping> getById(Long id) {
        return productPriceMappingRepository.findById(id);
    }

    @Override
    public List<ProductPriceMapping> getAll() {
        return productPriceMappingRepository.findAll();
    }

    @Override
    public ProductPriceMapping update(ProductPriceMapping entity) {
        return productPriceMappingRepository.save(entity);
    }

    @Override
    public void delete(ProductPriceMapping entity) {
        productPriceMappingRepository.delete(entity);
    }

    public Optional<ProductPriceMapping> findByIncomingOrderPositionName(String incomingOrderPositionName) {
        log.debug("Поиск ProductPriceMapping по имени позиции входящего заказа: {}", incomingOrderPositionName);
        return productPriceMappingRepository.findByIncomingOrderPositionName(incomingOrderPositionName);
    }
}
