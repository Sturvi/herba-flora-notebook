package com.example.inovasiyanotebook.repository;

import com.example.inovasiyanotebook.model.order.OrderPosition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderPositionRepository extends JpaRepository<OrderPosition, Long> {
}
