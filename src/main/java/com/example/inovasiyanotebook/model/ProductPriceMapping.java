package com.example.inovasiyanotebook.model;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "products_price_mapping")
@Setter
@Getter
@SuperBuilder
@NoArgsConstructor
public class ProductPriceMapping extends AbstractEntity {

    private String incomingOrderPositionName;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;
}
