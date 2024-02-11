package com.example.inovasiyanotebook.repository;

import com.example.inovasiyanotebook.model.ProductMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductMappingRepository extends JpaRepository<ProductMapping, Long> {
    Optional<ProductMapping> findByIncomingOrderPositionName (String incomingOrderPositionName);
    boolean existsByIncomingOrderPositionName(String incomingOrderPositionName);
}
