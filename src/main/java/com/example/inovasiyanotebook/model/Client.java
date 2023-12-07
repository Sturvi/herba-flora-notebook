package com.example.inovasiyanotebook.model;

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
public class Client extends AbstractEntity implements ParentEntity{

    @Column(nullable = false, unique = true)
    private String name;

    private String phoneNumber;

    private String email;

    private String voen;

    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL)
    private List<Product> products;

    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL)
    private List<Note> note;
}
