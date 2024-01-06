package com.example.inovasiyanotebook.model.order;

import com.example.inovasiyanotebook.model.AbstractEntity;
import com.example.inovasiyanotebook.model.Product;
import com.example.inovasiyanotebook.model.interfaces.Noteable;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "orders")
@Setter
@Getter
@SuperBuilder
@NoArgsConstructor
public class Order extends AbstractEntity implements Noteable {

    private Integer orderNo;

    private LocalDateTime orderReceivedDateTime;

    private LocalDateTime orderCompletedDateTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatusEnum status;

    @Column(columnDefinition="TEXT")
    private String comment;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<OrderPosition> orderPositions;

    public String getProductsString() {
        Set<String> uniqueProductNames = new TreeSet<>();

        for (OrderPosition position : orderPositions) {
            uniqueProductNames.add(position.getProduct().getName());
        }

        return String.join(", ", uniqueProductNames);
    }

    public List<Product> getProducts() {
        return orderPositions.stream()
                .map(OrderPosition::getProduct)
                .distinct()
                .toList();
    }

}
