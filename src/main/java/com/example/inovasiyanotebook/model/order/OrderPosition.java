package com.example.inovasiyanotebook.model.order;

import com.example.inovasiyanotebook.model.AbstractEntity;
import com.example.inovasiyanotebook.model.Product;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "order_positions")
@Setter
@Getter
@SuperBuilder
@NoArgsConstructor
public class OrderPosition extends AbstractEntity {

    @ManyToOne
    @Column(nullable = false)
    private Order order;

    @ManyToOne
    @Column(nullable = false)
    private Product product;

    private LocalDateTime positionCompletedDateTime;

    @Enumerated(EnumType.STRING)
    private OrderStatusEnum status;

    private Integer count;

    @Column(columnDefinition="TEXT")
    private String comment;
}
