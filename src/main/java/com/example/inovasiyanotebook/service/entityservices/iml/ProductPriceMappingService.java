package com.example.inovasiyanotebook.service.entityservices.iml;

import com.example.inovasiyanotebook.model.ProductPriceMapping;
import com.example.inovasiyanotebook.repository.ProductPriceMappingRepository;
import com.example.inovasiyanotebook.repository.specification.ProductPriceMappingSpecifications;
import com.example.inovasiyanotebook.repository.specification.ProductPriceMappingSpecifications.PriceMappingGridFilter;
import com.example.inovasiyanotebook.service.entityservices.CRUDService;
import com.example.inovasiyanotebook.service.viewservices.common.GridQuerySupport;
import com.example.inovasiyanotebook.service.viewservices.common.GridQuerySupport.SortKey;
import com.vaadin.flow.data.provider.Query;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductPriceMappingService implements CRUDService<ProductPriceMapping> {
    private static final List<SortKey> DEFAULT_GRID_SORT = List.of(SortKey.asc("incomingOrderPositionName"));

    private final ProductPriceMappingRepository productPriceMappingRepository;

    @Transactional(readOnly = true)
    public List<ProductPriceMapping> fetchForGrid(PriceMappingGridFilter filter, Query<ProductPriceMapping, ?> query) {
        Specification<ProductPriceMapping> specification = ProductPriceMappingSpecifications.forGrid(filter)
                .and(GridQuerySupport.orderBy(GridQuerySupport.toSortKeys(query.getSortOrders(), DEFAULT_GRID_SORT)));
        return productPriceMappingRepository.findAll(specification, GridQuerySupport.toPageable(query)).getContent();
    }

    @Transactional(readOnly = true)
    public int countForGrid(PriceMappingGridFilter filter) {
        return (int) productPriceMappingRepository.count(ProductPriceMappingSpecifications.forGrid(filter));
    }

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
