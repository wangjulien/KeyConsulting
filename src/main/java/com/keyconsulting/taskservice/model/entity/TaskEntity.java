package com.keyconsulting.taskservice.model.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
@Entity
@Table(name = "task")
public class TaskEntity implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    private long id;
    private String label;
    private String description;
    private boolean completed;

    public static TaskEntity of(String label, String description, boolean completed) {
        var taskEntity = new TaskEntity();
        taskEntity.setLabel(label);
        taskEntity.setDescription(description);
        taskEntity.setCompleted(completed);
        return taskEntity;
    }
}
