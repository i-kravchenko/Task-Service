package org.example.task_service.service;

import org.example.task_service.dto.task.TaskRequest;
import org.example.task_service.dto.task.TaskResponse;
import org.example.task_service.dto.task.UpsertTaskRequest;
import org.example.task_service.entity.Status;

import java.util.List;

public interface TaskService
{
    TaskResponse createTask(UpsertTaskRequest request);
    TaskResponse editTask(Long taskId, UpsertTaskRequest request);
    TaskResponse findTask(Long taskId);
    List<TaskResponse> tasksList(TaskRequest request);
    void deleteTask(Long taskId);
    TaskResponse changeStatus(Long taskId, Status status);
}
