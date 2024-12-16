package com.example.inovasiyanotebook.repository;

import com.example.inovasiyanotebook.model.ProductExtraInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductExtraInfoRepository extends JpaRepository<ProductExtraInfo, Long> {
    List<ProductExtraInfo> findAllByProductId(Long productId);
}
