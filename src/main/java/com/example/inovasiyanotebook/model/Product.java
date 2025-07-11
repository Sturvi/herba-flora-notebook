package com.example.inovasiyanotebook.model;

import com.example.inovasiyanotebook.model.client.Category;
import com.example.inovasiyanotebook.model.client.Client;
import com.example.inovasiyanotebook.model.interfaces.NamedEntity;
import com.example.inovasiyanotebook.model.interfaces.Noteable;
import com.example.inovasiyanotebook.model.interfaces.ParentEntity;
import com.example.inovasiyanotebook.model.order.OrderPosition;
import com.example.inovasiyanotebook.views.ViewsEnum;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Entity
@Table(name = "products")
@Setter
@Getter
@SuperBuilder
@NoArgsConstructor
public class Product extends AbstractEntity implements ParentEntity, NamedEntity, Noteable {

    @Column(nullable = false, unique = true)
    private String name;

    private String ts;

    private String barcode;

    private String weight;

    private String shelfLife = "";

    @ManyToOne
    private Client client;

    @ManyToOne
    private Category category;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    private List<OrderPosition> orderPositions;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductExtraInfo> extraInfo;

    private String price;

    @Override
    public ViewsEnum getViewEnum() {
        return ViewsEnum.PRODUCT;
    }

    @Override
    public String toString() {
        return name;
    }
}
