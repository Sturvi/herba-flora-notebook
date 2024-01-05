package com.example.inovasiyanotebook.repository;

import com.example.inovasiyanotebook.model.order.PrintedType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PrintedTypeRepository extends JpaRepository<PrintedType, Long> {
}
