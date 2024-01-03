package com.example.inovasiyanotebook.model.client;

import com.example.inovasiyanotebook.model.AbstractEntity;
import com.example.inovasiyanotebook.model.Note;
import com.example.inovasiyanotebook.model.Product;
import com.example.inovasiyanotebook.model.interfaces.NamedEntity;
import com.example.inovasiyanotebook.model.interfaces.Noteable;
import com.example.inovasiyanotebook.model.interfaces.ParentEntity;
import com.example.inovasiyanotebook.views.ViewsEnum;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "clients")
@Setter
@Getter
@SuperBuilder
@NoArgsConstructor
public class Client extends AbstractEntity implements ParentEntity, NamedEntity, Noteable {

    @Column(nullable = false, unique = true)
    private String name;

    private String phoneNumber;

    private String email;

    private String voen;

    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL)
    private List<Product> products;

    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL)
    private List<Note> note;

    @Column(nullable = false)
    private Integer sortOrder;

    @Override
    public ViewsEnum getViewEnum() {
        return ViewsEnum.CLIENT;
    }

    @Override
    public String toString() {
        return name;
    }
}
