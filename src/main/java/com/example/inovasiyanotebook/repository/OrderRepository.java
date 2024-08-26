package com.example.inovasiyanotebook.repository;

import com.example.inovasiyanotebook.model.Product;
import com.example.inovasiyanotebook.model.order.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Set;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    @Query("SELECT DISTINCT o FROM Order o JOIN o.orderPositions op WHERE op.product = :product")
    Set<Order> findAllByProducts(Product product);

    boolean existsByOrderNoAndOrderReceivedDate(Integer orderNo, LocalDate orderReceivedDate);

}
