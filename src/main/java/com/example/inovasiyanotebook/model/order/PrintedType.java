package com.example.inovasiyanotebook.model.order;

import com.example.inovasiyanotebook.model.AbstractEntity;
import com.example.inovasiyanotebook.model.interfaces.NamedEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "printed_type")
@Setter
@Getter
@SuperBuilder
@NoArgsConstructor
public class PrintedType extends AbstractEntity implements NamedEntity {

    @Column(unique = true)
    private String name;

    @Override
    public String toString() {
        return name;
    }
}
