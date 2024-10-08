package com.keyconsulting.taskservice.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.keyconsulting.taskservice.IntegrationTest;
import com.keyconsulting.taskservice.model.dto.PatchTaskDTO;
import com.keyconsulting.taskservice.model.dto.TaskDTO;
import com.keyconsulting.taskservice.model.entity.TaskEntity;
import com.keyconsulting.taskservice.repository.TaskRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@IntegrationTest
@AutoConfigureMockMvc
public class TaskControllerIT {

    private static final String TASK_ENDPOINT = "/tasks";
    private static final String TASK_ID_ENDPOINT = "/tasks/{id}";
    private static final String IS_COMPLETED_PARAM = "isCompleted";

    private static final String TODO_TASK_LABEL_1 = "todoTaskLabel1";
    private static final String TODO_TASK_LABEL_2 = "todoTaskLabel2";
    private static final String COMPLETE_TASK_LABEL_1 = "completeTaskLabel1";
    private static final String COMPLETE_TASK_LABEL_2 = "completeTaskLabel2";

    @Autowired
    private MockMvc taskMockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TaskRepository taskRepository;

    private void initTasks() {
        // Prepare 2 ToDo tasks & 2 Completed tasks
        var tasks = List.of(TaskEntity.of(TODO_TASK_LABEL_1, UUID.randomUUID().toString(), false),
                TaskEntity.of(TODO_TASK_LABEL_2, UUID.randomUUID().toString(), false),
                TaskEntity.of(COMPLETE_TASK_LABEL_1, UUID.randomUUID().toString(), true),
                TaskEntity.of(COMPLETE_TASK_LABEL_2, UUID.randomUUID().toString(), true));
        taskRepository.saveAll(tasks);
    }

    private TaskEntity initTask() {
        return taskRepository.save(TaskEntity.of(TODO_TASK_LABEL_1, UUID.randomUUID().toString(), false));
    }

    @Test
    @Transactional
    void getAllTasksShouldSucceed() throws Exception {
        // Given
        initTasks();

        // Then
        var response = taskMockMvc
                .perform(get(TASK_ENDPOINT))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andReturn();
        var taskDTOs = objectMapper.readValue(response.getResponse().getContentAsString(),
                new TypeReference<List<TaskDTO>>() {
                });

        // Check
        assertThat(taskDTOs.size()).isEqualTo(4);
        // check todo tasks
        var todoTasks = taskDTOs.stream().filter(tk -> !tk.isCompleted()).toList();
        assertThat(todoTasks.size()).isEqualTo(2);
        assertThat(todoTasks.stream().map(TaskDTO::getLabel).toList())
                .containsExactlyInAnyOrderElementsOf(List.of(TODO_TASK_LABEL_1, TODO_TASK_LABEL_2));
        // check complete tasks
        var completeTasks = taskDTOs.stream().filter(TaskDTO::isCompleted).toList();
        assertThat(completeTasks.size()).isEqualTo(2);
        assertThat(completeTasks.stream().map(TaskDTO::getLabel).toList())
                .containsExactlyInAnyOrderElementsOf(List.of(COMPLETE_TASK_LABEL_1, COMPLETE_TASK_LABEL_2));
    }

    @Test
    @Transactional
    void getToDoTasksShouldSucceed() throws Exception {
        // Given
        initTasks();

        // Then
        var response = taskMockMvc
                .perform(get(TASK_ENDPOINT)
                        .param(IS_COMPLETED_PARAM, Boolean.FALSE.toString()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andReturn();
        var taskDTOs = objectMapper.readValue(response.getResponse().getContentAsString(),
                new TypeReference<List<TaskDTO>>() {
                });

        // Check
        assertThat(taskDTOs.size()).isEqualTo(2);
        // check todo tasks
        var todoTasks = taskDTOs.stream().filter(tk -> !tk.isCompleted()).toList();
        assertThat(todoTasks.size()).isEqualTo(2);
        assertThat(todoTasks.stream().map(TaskDTO::getLabel).toList())
                .containsExactlyInAnyOrderElementsOf(List.of(TODO_TASK_LABEL_1, TODO_TASK_LABEL_2));
        // check complete tasks
        var completeTasks = taskDTOs.stream().filter(TaskDTO::isCompleted).toList();
        assertThat(completeTasks).isEmpty();
    }

    @Test
    @Transactional
    void getExistTaskByIdShouldSucceed() throws Exception {
        // Given
        var task = initTask();

        // Then
        var response = taskMockMvc
                .perform(get(TASK_ID_ENDPOINT, task.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andReturn();
        var taskDTO = objectMapper.readValue(response.getResponse().getContentAsString(), TaskDTO.class);

        // Check
        assertThat(taskDTO).isNotNull();
        assertThat(taskDTO.getId()).isEqualTo(task.getId());
        assertThat(taskDTO.getLabel()).isEqualTo(task.getLabel());
        assertThat(taskDTO.getDescription()).isEqualTo(task.getDescription());
        assertThat(taskDTO.isCompleted()).isEqualTo(task.isCompleted());
    }

    @Test
    @Transactional
    void getNotExistTaskByIdShouldFailed() throws Exception {
        // Given
        var task = initTask();

        // Then
        var response = taskMockMvc
                .perform(get(TASK_ID_ENDPOINT, task.getId() + 100))     // give a not existing ID
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andReturn();
        var problemDetail = objectMapper.readValue(response.getResponse().getContentAsString(), ProblemDetail.class);

        // Check ProblemDetail content
        assertThat(problemDetail).isNotNull();
        assertThat(problemDetail.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @Test
    @Transactional
    void createTaskShouldSucceed() throws Exception {
        // Given
        var taskDTO = TaskDTO.builder()
                .label(UUID.randomUUID().toString())
                .description(UUID.randomUUID().toString())
                .completed(false)
                .build();

        // Then
        var response = taskMockMvc
                .perform(post(TASK_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(taskDTO)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andReturn();
        var taskCreated = objectMapper.readValue(response.getResponse().getContentAsString(), TaskDTO.class);

        // Check ProblemDetail content
        assertThat(taskDTO.getId()).isNull();

        assertThat(taskCreated).isNotNull();
        assertThat(taskCreated.getId()).isGreaterThan(0L);  // ID should be generated automatically
        assertThat(taskCreated.getLabel()).isEqualTo(taskDTO.getLabel());
        assertThat(taskCreated.getDescription()).isEqualTo(taskDTO.getDescription());
        // Check the DB
        var savedTask = taskRepository.findById(taskCreated.getId()).orElseThrow();
        assertThat(savedTask.getLabel()).isEqualTo(taskDTO.getLabel());
        assertThat(savedTask.getDescription()).isEqualTo(taskDTO.getDescription());
        assertThat(savedTask.isCompleted()).isFalse();
    }

    @Test
    @Transactional
    void patchTaskCompleteShouldSucceed() throws Exception {
        // Given
        var task = initTask();  // completed = false
        var patchTaskDTO = new PatchTaskDTO(task.getId(), !task.isCompleted()); // completed = true

        // Then
        var response = taskMockMvc
                .perform(patch(TASK_ID_ENDPOINT, task.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(patchTaskDTO)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andReturn();
        var taskPatched = objectMapper.readValue(response.getResponse().getContentAsString(), TaskDTO.class);

        // Check
        assertThat(taskPatched).isNotNull();
        assertThat(taskPatched.getId()).isEqualTo(task.getId());
        assertThat(taskPatched.getLabel()).isEqualTo(task.getLabel());
        assertThat(taskPatched.getDescription()).isEqualTo(task.getDescription());
        assertThat(taskPatched.isCompleted()).isTrue();
        // Check the DB
        var savedTask = taskRepository.findById(task.getId()).orElseThrow();
        assertThat(savedTask.isCompleted()).isTrue();
    }

    @Test
    @Transactional
    void patchTaskWithoutIdShouldFailed() throws Exception {
        // Given
        var task = initTask();

        // Then without ID
        var patchTaskDTO = new PatchTaskDTO();
        taskMockMvc
                .perform(patch(TASK_ID_ENDPOINT, task.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(patchTaskDTO)))
                .andExpect(status().isBadRequest());

        // Then ID not exists
        var notExistId = task.getId() + 100;
        patchTaskDTO = new PatchTaskDTO(notExistId, !task.isCompleted()); // ID not exists
        var response = taskMockMvc
                .perform(patch(TASK_ID_ENDPOINT, notExistId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(patchTaskDTO)))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andReturn();
        var problemDetail = objectMapper.readValue(response.getResponse().getContentAsString(), ProblemDetail.class);

        // Check ProblemDetail content
        assertThat(problemDetail).isNotNull();
        assertThat(problemDetail.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @Test
    @Transactional
    void patchTaskOtherFieldsShouldFailed() throws Exception {
        // Given
        var task = initTask();  // completed = false
        var patchTaskDTO = TaskDTO.builder()
                .id(task.getId())
                .label(UUID.randomUUID().toString())
                .description(UUID.randomUUID().toString())
                .completed(true)
                .build();

        // Then
        var response = taskMockMvc
                .perform(patch(TASK_ID_ENDPOINT, task.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(patchTaskDTO)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andReturn();
        var taskPatched = objectMapper.readValue(response.getResponse().getContentAsString(), TaskDTO.class);

        // Check
        assertThat(taskPatched).isNotNull();
        assertThat(taskPatched.getId()).isEqualTo(task.getId());
        assertThat(taskPatched.getLabel()).isEqualTo(task.getLabel());
        assertThat(taskPatched.getDescription()).isEqualTo(task.getDescription());
        assertThat(taskPatched.isCompleted()).isTrue();
        // Other attributes should not be modified
        var savedTask = taskRepository.findById(task.getId()).orElseThrow();
        assertThat(savedTask.getLabel()).isNotEqualTo(patchTaskDTO.getLabel());
        assertThat(savedTask.getDescription()).isNotEqualTo(patchTaskDTO.getDescription());
    }
}
