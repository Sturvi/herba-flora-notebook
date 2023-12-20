package com.example.inovasiyanotebook.model;

import com.example.inovasiyanotebook.model.client.Category;
import com.example.inovasiyanotebook.model.client.Client;
import com.example.inovasiyanotebook.model.interfaces.ParentEntity;
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
@Table(name = "products")
@Setter
@Getter
@SuperBuilder
@NoArgsConstructor
public class Product extends AbstractEntity implements ParentEntity {

    @Column(nullable = false, unique = true)
    private String name;

    private String ts;

    private String barcode;

    private String weight;

    @ManyToOne
    private Client client;

    @ManyToOne
    private Category category;
}
