package com.keyconsulting.taskservice.service;

import com.keyconsulting.taskservice.model.dto.PatchTaskDTO;
import com.keyconsulting.taskservice.model.dto.TaskDTO;
import com.keyconsulting.taskservice.model.entity.TaskEntity;
import com.keyconsulting.taskservice.repository.TaskRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
@Transactional
public class TaskService {
    private final TaskRepository taskRepository;
    private final ModelMapper modelMapper;

    /**
     * Get tasks by condition (completed = true/false),
     * if no isCompleted condition, get all tasks from DB
     *
     * @param isCompleted - null means get all tasks
     * @return list of tasks DTO
     */
    public List<TaskDTO> getTasks(Boolean isCompleted) {
        var tasks = null == isCompleted
                ? taskRepository.findAll()
                : taskRepository.findAllByCompleted(isCompleted);

        return tasks.stream().map(this::mapEntityToDto).toList();
    }

    public TaskDTO getTaskById(Long id) {
        return taskRepository.findById(id).map(this::mapEntityToDto)
                .orElseThrow(() -> new EntityNotFoundException("Task with ID " + id + " can not be found"));
    }

    public TaskDTO createOrUpdateTask(TaskDTO createTaskDTO) {
        var taskEntity = modelMapper.map(createTaskDTO, TaskEntity.class);
        // Created task should NOT be completed by default
        if (null == createTaskDTO.getId()) {
            taskEntity.setCompleted(false);
        }
        return mapEntityToDto(taskRepository.save(taskEntity));
    }

    public TaskDTO patchTaskStatus(PatchTaskDTO updateTaskDTO) {
        var taskEntity = taskRepository.findById(updateTaskDTO.getId())
                .orElseThrow(() -> new EntityNotFoundException("Task with ID " + updateTaskDTO.getId() + " can not be found"));
        taskEntity.setCompleted(updateTaskDTO.getCompleted());

        return mapEntityToDto(taskEntity);
    }

    private TaskDTO mapEntityToDto(TaskEntity task) {
        return modelMapper.map(task, TaskDTO.class);
    }
}
