package com.example.inovasiyanotebook.model;

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

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "notes")
@Setter
@Getter
@SuperBuilder
@NoArgsConstructor
public class Note extends AbstractEntity implements HasParentEntity {

    @ManyToOne
    private Client client;

    @ManyToOne
    private Product product;

    private String text;

    @Column(nullable = false)
    private boolean isPinned = false;

    @ManyToOne
    private User addedBy;

    @Override
    public ParentEntity getParent() {
        return client != null ? client : product;
    }

    @Override
    public boolean hasParent() {
        return true;
    }

}