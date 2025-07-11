package com.example.inovasiyanotebook.model.client;

import com.example.inovasiyanotebook.model.AbstractEntity;
import com.example.inovasiyanotebook.model.Note;
import com.example.inovasiyanotebook.model.Product;
import com.example.inovasiyanotebook.model.interfaces.HasParentEntity;
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
import java.util.Objects;

@Entity
@Table(name = "categories")
@Setter
@Getter
@SuperBuilder
@NoArgsConstructor
public class Category extends AbstractEntity implements ParentEntity, HasParentEntity, NamedEntity, Noteable {

    @Column(nullable = false, unique = true)
    private String name;

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL)
    private List<Product> products;

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL)
    private List<Note> note;

    @OneToMany(mappedBy = "parentCategory", cascade = CascadeType.ALL)
    private List<Category> subCategories;

    @ManyToOne
    private Category parentCategory;

    @Override
    public ParentEntity getParent() {
        return parentCategory;
    }

    @Override
    public boolean hasParent() {
        return parentCategory != null;
    }

    @Override
    public String toString() {
        return name;
    }

    public String getFullName() {
        return hasParent() ? parentCategory.name + ": " + getName() : getName();
    }

    @Override
    public ViewsEnum getViewEnum() {
        return ViewsEnum.CATEGORY;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Category category = (Category) o;
        return Objects.equals(name, category.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), name);
    }
}
