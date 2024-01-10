package com.example.inovasiyanotebook.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "instructions")
@Setter
@Getter
@SuperBuilder
@NoArgsConstructor
public class Instruction extends AbstractEntity{

    @OneToOne
    private Product product;

    @Column(columnDefinition="TEXT")
    private String text;
}
