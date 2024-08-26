package com.example.inovasiyanotebook.repository;

import com.example.inovasiyanotebook.model.ProductMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductMappingRepository extends JpaRepository<ProductMapping, Long> {
    Optional<ProductMapping> findByIncomingOrderPositionName (String incomingOrderPositionName);
    boolean existsByIncomingOrderPositionName(String incomingOrderPositionName);

    @Query("SELECT COUNT(p) FROM ProductMapping p WHERE p.incomingOrderPositionName IN :names AND p.product IS NOT NULL")
    long countByIncomingOrderPositionNamesWithNonNullProduct(@Param("names") List<String> names);

}
