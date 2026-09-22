package com.example.inovasiyanotebook.service.entityservices.iml;

import com.example.inovasiyanotebook.model.ProductMapping;
import com.example.inovasiyanotebook.repository.ProductMappingRepository;
import com.example.inovasiyanotebook.repository.specification.ProductMappingSpecifications;
import com.example.inovasiyanotebook.repository.specification.ProductMappingSpecifications.ProductMappingGridFilter;
import com.example.inovasiyanotebook.service.entityservices.CRUDService;
import com.example.inovasiyanotebook.service.viewservices.common.GridQuerySupport;
import com.example.inovasiyanotebook.service.viewservices.common.GridQuerySupport.SortKey;
import com.vaadin.flow.data.provider.Query;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductMappingService implements CRUDService<ProductMapping> {
    /** Несопоставленные (product IS NULL) сверху, как и раньше. */
    private static final List<SortKey> DEFAULT_GRID_SORT = List.of(new SortKey("product.name", true, true));

    private final ProductMappingRepository productMappingRepository;

    @Transactional(readOnly = true)
    public List<ProductMapping> fetchForGrid(ProductMappingGridFilter filter, Query<ProductMapping, ?> query) {
        Specification<ProductMapping> specification = ProductMappingSpecifications.forGrid(filter)
                .and(GridQuerySupport.orderBy(GridQuerySupport.toSortKeys(query.getSortOrders(), DEFAULT_GRID_SORT)));
        return productMappingRepository.findAll(specification, GridQuerySupport.toPageable(query)).getContent();
    }

    @Transactional(readOnly = true)
    public int countForGrid(ProductMappingGridFilter filter) {
        return (int) productMappingRepository.count(ProductMappingSpecifications.forGrid(filter));
    }


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

    public boolean doAllExist(List<String> incomingOrderPositionNames) {
        return productMappingRepository.countByIncomingOrderPositionNamesWithNonNullProduct(incomingOrderPositionNames) == incomingOrderPositionNames.size();
    }
}
