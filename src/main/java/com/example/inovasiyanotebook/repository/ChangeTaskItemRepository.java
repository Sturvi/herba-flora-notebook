package com.example.inovasiyanotebook.repository;

import com.example.inovasiyanotebook.model.changetask.ChangeItemStatus;
import com.example.inovasiyanotebook.model.changetask.ChangeTaskItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChangeTaskItemRepository extends JpaRepository<ChangeTaskItem, Long> {

    @Query("SELECT i FROM ChangeTaskItem i JOIN FETCH i.task JOIN FETCH i.product WHERE i.status = :status")
    List<ChangeTaskItem> findAllByStatusWithTaskAndProduct(@Param("status") ChangeItemStatus status);
}
