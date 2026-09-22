package com.example.inovasiyanotebook.repository;

import com.example.inovasiyanotebook.model.Product;
import com.example.inovasiyanotebook.model.order.OrderPosition;
import com.example.inovasiyanotebook.model.order.OrderStatusEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface OrderPositionRepository extends JpaRepository<OrderPosition, Long> {
    Set<OrderPosition> getAllByProductAndStatus(Product product, OrderStatusEnum status);

    List<OrderPosition> getAllByStatus(OrderStatusEnum orderStatusEnum);

    /**
     * Позиции с заданным статусом у заказов с заданным статусом, вместе со всем графом,
     * который читает страница открытых заказов (заказ, продукт, категория и её родитель, клиент, тип печати).
     */
    @Query("""
            SELECT DISTINCT op FROM OrderPosition op
            JOIN FETCH op.order o
            JOIN FETCH op.product p
            LEFT JOIN FETCH p.category c
            LEFT JOIN FETCH c.parentCategory
            LEFT JOIN FETCH p.client
            LEFT JOIN FETCH op.printedType
            WHERE op.status = :positionStatus AND o.status = :orderStatus
            """)
    List<OrderPosition> findAllByStatusesWithGraph(@Param("positionStatus") OrderStatusEnum positionStatus,
                                                   @Param("orderStatus") OrderStatusEnum orderStatus);
}
