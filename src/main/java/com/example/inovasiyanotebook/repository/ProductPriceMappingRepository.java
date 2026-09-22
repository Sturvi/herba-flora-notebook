package com.example.inovasiyanotebook.repository;

import com.example.inovasiyanotebook.model.ProductPriceMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface ProductPriceMappingRepository extends JpaRepository<ProductPriceMapping, Long>, JpaSpecificationExecutor<ProductPriceMapping> {

    Optional<ProductPriceMapping> findByIncomingOrderPositionName(String incomingOrderPositionName);
}
