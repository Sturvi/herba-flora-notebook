package com.example.inovasiyanotebook.repository;

import com.example.inovasiyanotebook.model.Product;
import com.example.inovasiyanotebook.model.order.Order;
import com.example.inovasiyanotebook.model.order.OrderStatusEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    @Query("SELECT DISTINCT o FROM Order o JOIN o.orderPositions op WHERE op.product = :product")
    Set<Order> findAllByProducts(Product product);

    boolean existsByOrderNoAndOrderReceivedDate(Integer orderNo, LocalDate orderReceivedDate);

    @Query("SELECT o.id FROM Order o WHERE o.orderNo = :orderNo AND o.orderReceivedDate = :orderReceivedDate")
    Optional<Long> findIdByOrderNoAndOrderReceivedDate(Integer orderNo, LocalDate orderReceivedDate);

    @Query("SELECT o FROM Order o WHERE o.status = com.example.inovasiyanotebook.model.order.OrderStatusEnum.OPEN AND NOT EXISTS (" +
            " SELECT op FROM OrderPosition op WHERE op.order = o AND op.status NOT IN (com.example.inovasiyanotebook.model.order.OrderStatusEnum.COMPLETE, com.example.inovasiyanotebook.model.order.OrderStatusEnum.CANCELED)" +
            ")")
    Set<Order> findOpenOrdersWithAllPositionsClosedOrCanceled();


}
