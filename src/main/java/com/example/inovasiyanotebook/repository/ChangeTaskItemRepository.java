package com.example.inovasiyanotebook.repository;

import com.example.inovasiyanotebook.model.changetask.ChangeItemStatus;
import com.example.inovasiyanotebook.model.changetask.ChangeTaskItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChangeTaskItemRepository extends JpaRepository<ChangeTaskItem, Long> {
    List<ChangeTaskItem> findAllByStatus(ChangeItemStatus status);
}
