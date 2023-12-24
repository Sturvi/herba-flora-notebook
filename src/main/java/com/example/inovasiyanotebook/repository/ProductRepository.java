package com.example.inovasiyanotebook.repository;

import com.example.inovasiyanotebook.model.Note;
import com.example.inovasiyanotebook.model.Product;
import com.example.inovasiyanotebook.model.client.Category;
import com.example.inovasiyanotebook.model.client.Client;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findAllByClient(Client client);

    List<Product> findAllByCategory(Category category);

    @Query("SELECT p FROM Product p WHERE p.category = :category OR p.category IN (SELECT c.id FROM Category c WHERE c.parentCategory = :category)")
    List<Product>findAllByCategoryAndHisSubCategory(Category category);

}
