package com.example.inovasiyanotebook.repository;

import com.example.inovasiyanotebook.model.changetask.ChangeTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChangeTaskRepository extends JpaRepository<ChangeTask, Long> {
}
