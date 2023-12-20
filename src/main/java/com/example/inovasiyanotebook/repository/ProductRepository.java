package com.example.inovasiyanotebook.repository;

import com.example.inovasiyanotebook.model.Product;
import com.example.inovasiyanotebook.model.client.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findAllByClient(Client client);
}
