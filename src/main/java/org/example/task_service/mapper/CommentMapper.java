package org.example.task_service.mapper;

import org.example.task_service.dto.task.CommentResponse;
import org.example.task_service.dto.task.UpsertCommentRequest;
import org.example.task_service.entity.Comment;
import org.example.task_service.repository.TaskRepository;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.ReportingPolicy;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public abstract class CommentMapper
{
    @Autowired
    protected TaskRepository taskRepository;

    @Mappings({
            @Mapping(target = "task",
                    expression = "java(taskRepository.findById(request.getTaskId()).orElseThrow(() ->\n" +
                            " new jakarta.persistence.EntityNotFoundException(\"messages.errors.task_not_found\")))")
    })
    public abstract Comment requestToComment(UpsertCommentRequest request);

    @Mappings({
            @Mapping(source = "user.id", target = "userId"),
            @Mapping(source = "task.id", target = "taskId"),
    })
    public abstract CommentResponse commentToResponse(Comment comment);
}
