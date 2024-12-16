package com.example.inovasiyanotebook.repository;

import com.example.inovasiyanotebook.model.ProductExtraInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductExtraInfoRepository extends JpaRepository<ProductExtraInfo, Long> {

    @Query("SELECT p FROM ProductExtraInfo p WHERE p.product.id = :productId ORDER BY p.sortOrder ASC")
    List<ProductExtraInfo> findAllByProductId(Long productId);

}
