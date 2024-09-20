package com.keyconsulting.taskservice.controller;

import com.keyconsulting.taskservice.model.dto.PatchTaskDTO;
import com.keyconsulting.taskservice.model.dto.TaskDTO;
import com.keyconsulting.taskservice.service.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/tasks")
public class TaskController {

    private final TaskService taskService;


    @GetMapping
    public ResponseEntity<List<TaskDTO>> getAllTasks(@RequestParam(required = false) Boolean isCompleted) {
        log.info("Get tasks by condition {}", isCompleted);
        var tasks = taskService.getTasks(isCompleted);
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskDTO> getTaskById(@PathVariable Long id) {
        log.info("Get one task by id {}", id);
        var task = taskService.getTaskById(id);
        return ResponseEntity.ok(task);
    }


    @PostMapping
    public ResponseEntity<TaskDTO> createTask(@Valid @RequestBody TaskDTO taskDTO) {
        log.info("Create task {}", taskDTO);
        var task = taskService.createTask(taskDTO);
        log.info("Task {} created", task.getId());
        return ResponseEntity.ok(task);
    }

    @PatchMapping
    public ResponseEntity<TaskDTO> patchTask(@Valid @RequestBody PatchTaskDTO taskDTO) {
        log.info("Patch task {}", taskDTO);
        var task = taskService.patchTaskStatus(taskDTO);
        return ResponseEntity.ok(task);
    }
}
