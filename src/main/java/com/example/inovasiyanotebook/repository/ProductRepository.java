package com.example.inovasiyanotebook.repository;

import com.example.inovasiyanotebook.dto.ProductOpenInfoDTO;
import com.example.inovasiyanotebook.model.Product;
import com.example.inovasiyanotebook.model.changetask.ChangeItemStatus;
import com.example.inovasiyanotebook.model.client.Category;
import com.example.inovasiyanotebook.model.client.Client;
import com.example.inovasiyanotebook.model.order.OrderStatusEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findAllByClient(Client client);

    List<Product> findAllByCategory(Category category);

    @Query("SELECT p FROM Product p WHERE p.category = :category OR p.category IN (SELECT c.id FROM Category c WHERE c.parentCategory = :category)")
    List<Product> findAllByCategoryAndHisSubCategory(Category category);

    @Query("SELECT p FROM Product p LEFT JOIN FETCH p.extraInfo WHERE p.id = :id")
    Optional<Product> findByIdWithExtraInfo(@Param("id") Long id);

    @Query("SELECT p FROM Product p WHERE LOWER(p.client.name) = LOWER(:clientName)")
    List<Product> findAllByClientNameIgnoreCase(@Param("clientName") String clientName);

    @Query("SELECT COUNT(p) FROM Product p WHERE p.category = :category AND LOWER(p.client.name) = LOWER(:clientName)")
    long countProductsByCategoryAndClientName(@Param("category") Category category, String clientName);

}
