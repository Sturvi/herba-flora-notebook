package com.example.inovasiyanotebook.repository;

import com.example.inovasiyanotebook.dto.ChangeTaskSummaryDTO;
import com.example.inovasiyanotebook.model.changetask.ChangeTask;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChangeTaskRepository extends JpaRepository<ChangeTask, Long> {

    /**
     * Строки грида задач: агрегаты по позициям считаются в БД.
     * Порядок: незавершённые сверху, затем по дате (у завершённых - последнее completedAt, у остальных - createdAt) по убыванию.
     */
    @Query("""
            SELECT new com.example.inovasiyanotebook.dto.ChangeTaskSummaryDTO(
                t.id, t.taskType, t.createdAt,
                COUNT(i),
                SUM(CASE WHEN i.status = com.example.inovasiyanotebook.model.changetask.ChangeItemStatus.DONE THEN 1L ELSE 0L END),
                SUM(CASE WHEN i.status = com.example.inovasiyanotebook.model.changetask.ChangeItemStatus.PENDING THEN 1L ELSE 0L END),
                MAX(i.completedAt))
            FROM ChangeTask t LEFT JOIN t.items i
            WHERE LOWER(t.taskType) LIKE :pattern
            GROUP BY t.id, t.taskType, t.createdAt
            ORDER BY
                CASE WHEN SUM(CASE WHEN i.status = com.example.inovasiyanotebook.model.changetask.ChangeItemStatus.PENDING THEN 1L ELSE 0L END) = 0 THEN 1 ELSE 0 END ASC,
                CASE WHEN SUM(CASE WHEN i.status = com.example.inovasiyanotebook.model.changetask.ChangeItemStatus.PENDING THEN 1L ELSE 0L END) = 0
                     THEN MAX(i.completedAt) ELSE t.createdAt END DESC NULLS LAST,
                t.id DESC
            """)
    List<ChangeTaskSummaryDTO> findSummaries(@Param("pattern") String pattern, Pageable pageable);

    long countByTaskTypeContainingIgnoreCase(String taskType);
}
