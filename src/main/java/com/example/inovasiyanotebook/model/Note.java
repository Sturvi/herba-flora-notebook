package com.example.inovasiyanotebook.model;

import com.example.inovasiyanotebook.model.client.Category;
import com.example.inovasiyanotebook.model.client.Client;
import com.example.inovasiyanotebook.model.interfaces.HasParentEntity;
import com.example.inovasiyanotebook.model.interfaces.ParentEntity;
import com.example.inovasiyanotebook.model.user.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "notes")
@Setter
@Getter
@SuperBuilder
@NoArgsConstructor
public class Note extends AbstractEntity {

    @ManyToOne
    private Client client;

    @ManyToOne
    private Product product;

    @ManyToOne
    private Category category;

    @Column(columnDefinition="TEXT")
    private String text;

    @Column(nullable = false)
    private boolean isPinned = false;

    @ManyToOne
    private User addedBy;

    @ManyToOne
    private User updatedBy;


    public List<ParentEntity> getParents() {
        List<ParentEntity> parents = new ArrayList<>();

        if (client != null) {
            parents.add(client);
        }
        if (product != null) {
            parents.add(product);
        }
        if (category != null) {
            parents.add(category);
        }

        return parents;
    }
}