package com.example.inovasiyanotebook.repository;

import com.example.inovasiyanotebook.model.Product;
import com.example.inovasiyanotebook.model.order.OrderPosition;
import com.example.inovasiyanotebook.model.order.OrderStatusEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface OrderPositionRepository extends JpaRepository<OrderPosition, Long> {

    Set<OrderPosition> getAllByProductAndStatus(Product product, OrderStatusEnum status);

    Set<OrderPosition> getAllByStatus(OrderStatusEnum orderStatusEnum);
}
