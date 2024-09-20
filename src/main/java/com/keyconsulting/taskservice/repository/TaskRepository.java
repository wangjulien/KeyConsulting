package com.keyconsulting.taskservice.repository;

import com.keyconsulting.taskservice.model.entity.TaskEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<TaskEntity, Long> {
    List<TaskEntity> findAllByCompleted(Boolean isCompleted);
}
