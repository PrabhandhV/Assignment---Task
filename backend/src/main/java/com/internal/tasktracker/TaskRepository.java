package com.internal.tasktracker;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    // Search tasks by term and optional status filter
    @Query(value = "SELECT * FROM tasks WHERE archived = FALSE "
                 + "AND (LOWER(title) LIKE :term ESCAPE '\\' OR LOWER(description) LIKE :term ESCAPE '\\') "

                 + "AND (:status IS NULL OR status = :status) "
                 + "AND (:priority IS NULL OR priority = :priority) " 
                 + "AND (:assignee IS NULL OR assignee = :assignee) "
                 + "ORDER BY created_at DESC, id DESC",
           nativeQuery = true)
    List<Task> searchTasks (@Param("term") String term, 
                            @Param("status") String status,
                            @Param("priority") String priority,
                            @Param("assignee") String assignee);
}
