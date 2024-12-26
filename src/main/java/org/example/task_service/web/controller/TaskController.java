package org.example.task_service.web.controller;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.task_service.dto.task.*;
import org.example.task_service.entity.Status;
import org.example.task_service.service.CommentService;
import org.example.task_service.service.TaskService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController
{
    private final TaskService taskService;
    private final CommentService commentService;

    @GetMapping
    public List<TaskResponse> taskList(@RequestBody(required = false) TaskRequest request) {
        return taskService.tasksList(request == null ? new TaskRequest() : request);
    }

    @PostMapping("/add-comment")
    public CommentResponse addComment(@RequestBody @Valid UpsertCommentRequest request) {
        return commentService.addComment(request);
    }

    @GetMapping("/{taskId}")
    public TaskResponse findTask(@PathVariable Long taskId) {
        return taskService.findTask(taskId);
    }

    @PostMapping
    public TaskResponse createTask(
            @RequestBody @Valid UpsertTaskRequest request,
            HttpServletResponse response
    ) {
        TaskResponse task = taskService.createTask(request);
        response.setStatus(HttpServletResponse.SC_CREATED);
        return task;
    }

    @PatchMapping("/{taskId}")
    public TaskResponse editTask(@PathVariable Long taskId, @RequestBody UpsertTaskRequest request) {
        return taskService.editTask(taskId, request);
    }

    @DeleteMapping("/{taskId}")
    public void deleteTask(@PathVariable Long taskId, HttpServletResponse response) {
        taskService.deleteTask(taskId);
        response.setStatus(HttpServletResponse.SC_NO_CONTENT);
    }

    @PatchMapping("/{taskId}/change-status")
    public TaskResponse changeStatus(@PathVariable Long taskId, @RequestParam Status status) {
        return taskService.changeStatus(taskId, status);
    }
}
