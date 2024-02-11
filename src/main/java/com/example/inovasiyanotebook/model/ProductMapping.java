package com.example.inovasiyanotebook.model;

import com.example.inovasiyanotebook.model.order.PrintedType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "products_mapping")
@Setter
@Getter
@SuperBuilder
@NoArgsConstructor
public class ProductMapping extends AbstractEntity {

    @Column(nullable = false, unique = true)
    private String incomingOrderPositionName;

    @ManyToOne
    private Product product;

    @ManyToOne
    private PrintedType printedType;

    private String comment;

}
