package com.example.inovasiyanotebook.repository;

import com.example.inovasiyanotebook.model.changetask.ChangeTaskItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChangeTaskItemRepository extends JpaRepository<ChangeTaskItem, Long> {
}
