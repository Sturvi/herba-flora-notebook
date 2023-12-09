package com.example.inovasiyanotebook.repository;

import com.example.inovasiyanotebook.model.client.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {

    @Query("SELECT MAX(p.sortOrder) FROM Client p")
    Integer findMaxSortOrder();


}
