package com.example.inovasiyanotebook.repository;

import com.example.inovasiyanotebook.model.ProductPriceMapping;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductPriceMappingRepository extends JpaRepository<ProductPriceMapping, Long> {

    Optional<ProductPriceMapping> findByIncomingOrderPositionName(String incomingOrderPositionName);
}
