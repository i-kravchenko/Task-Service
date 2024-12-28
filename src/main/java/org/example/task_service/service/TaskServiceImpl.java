package org.example.task_service.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.example.task_service.configuration.properties.AppCacheProperties;
import org.example.task_service.dto.task.TaskRequest;
import org.example.task_service.dto.task.TaskResponse;
import org.example.task_service.dto.task.UpsertTaskRequest;
import org.example.task_service.entity.*;
import org.example.task_service.mapper.TaskMapper;
import org.example.task_service.repository.TaskRepository;
import org.example.task_service.specification.TaskSpecification;
import org.example.task_service.utils.BeanUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService
{
    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;
    private final MessageSource messageSource;

    @Override
    @PostFilter("hasRole('ROLE_ADMIN') or filterObject.responsibleId == principal.id")
    @Cacheable(value = AppCacheProperties.CacheNames.TASKS, key = "#request.hashCode")
    public List<TaskResponse> tasksList(TaskRequest request) {
        Page<Task> page = taskRepository.findAll(
                TaskSpecification.withRequest(request),
                PageRequest.of(request.getPageNumber(), request.getPageSize())
        );
        List<TaskResponse> list = page.getContent().stream().map(taskMapper::taskToResponse).toList();
        return new ArrayList<>(list);
    }

    @Override
    @PostAuthorize("hasRole('ROLE_ADMIN') or returnObject.responsibleId == principal.id")
    @Cacheable(value = AppCacheProperties.CacheNames.TASKS, key = "#taskId")
    public TaskResponse findTask(Long taskId) {
        Task task = taskRepository.findById(taskId).orElseThrow(() ->
                new EntityNotFoundException("messages.errors.task_not_found"));
        return taskMapper.taskToResponse(task);
    }

    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @CacheEvict(cacheNames = AppCacheProperties.CacheNames.TASKS, allEntries = true)
    public TaskResponse createTask(UpsertTaskRequest request) {
        User currentUser = (User)SecurityContextHolder
                .getContext().getAuthentication().getPrincipal();
        Task task = taskMapper.requestToTask(request);
        task.setStatus(Status.PENDING);
        if(task.getPriority() == null) {
            task.setPriority(Priority.LOW);
        }
        task.setAuthor(currentUser);
        return taskMapper.taskToResponse(taskRepository.save(task));
    }

    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @CacheEvict(cacheNames = AppCacheProperties.CacheNames.TASKS, allEntries = true)
    public TaskResponse editTask(Long taskId, UpsertTaskRequest request) {
        Task task = taskRepository.findById(taskId).orElseThrow(() ->
                new EntityNotFoundException("messages.errors.task_not_found"));
        BeanUtils.copyNonNullProperties(
                taskMapper.requestToTask(request),
                task
        );
        return taskMapper.taskToResponse(taskRepository.save(task));
    }

    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @CacheEvict(cacheNames = AppCacheProperties.CacheNames.TASKS, allEntries = true)
    public void deleteTask(Long taskId) {
        taskRepository.findById(taskId).map(Task::getId)
                .ifPresentOrElse(taskRepository::deleteById, () -> {throw new EntityNotFoundException("messages.errors.task_not_found");});
    }

    @Override
    @CacheEvict(cacheNames = AppCacheProperties.CacheNames.TASKS, allEntries = true)
    public TaskResponse changeStatus(Long taskId, Status status) {
        User currentUser = (User) SecurityContextHolder
                .getContext().getAuthentication().getPrincipal();
        Task task = taskRepository.findById(taskId).orElseThrow(() ->
                new EntityNotFoundException("messages.errors.task_not_found"));
        if(!currentUser.getRoles().contains(Role.ROLE_ADMIN) &&
                !currentUser.getId().equals(task.getResponsible().getId())) {
            Locale locale = LocaleContextHolder.getLocale();
            throw new AccessDeniedException(messageSource.getMessage(
                    "messages.errors.access_denied_modify_task",
                    new Object[]{task.getId()},
                    "You cant add comment to task: " + task.getId(),
                    locale
            ));
        }
        task.setStatus(status);
        return taskMapper.taskToResponse(taskRepository.save(task));
    }
}
