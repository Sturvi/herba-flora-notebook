package com.example.inovasiyanotebook.repository;

import com.example.inovasiyanotebook.model.order.RawOrderData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RawOrderDataRepository extends JpaRepository<RawOrderData, Long> {
}
