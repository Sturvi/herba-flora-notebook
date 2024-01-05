package com.example.inovasiyanotebook.model.order;

import com.example.inovasiyanotebook.model.AbstractEntity;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "orders")
@Setter
@Getter
@SuperBuilder
@NoArgsConstructor
public class Order extends AbstractEntity {

    private Integer orderNo;

    private LocalDateTime orderReceivedDateTime;

    private LocalDateTime orderCompletedDateTime;

    @Enumerated(EnumType.STRING)
    private OrderStatusEnum status;

    @Column(columnDefinition="TEXT")
    private String comment;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderPosition> orderPositions;
}
