package org.example.task_service.mapper;

import org.example.task_service.dto.task.TaskResponse;
import org.example.task_service.dto.task.UpsertTaskRequest;
import org.example.task_service.entity.Task;
import org.example.task_service.repository.UserRepository;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = {
                CommentMapper.class,
        }
)
public abstract class TaskMapper
{
    @Autowired
    protected UserRepository userRepository;

    @Mappings({
            @Mapping(target = "responsible",
                    expression = "java(request.getResponsibleId() == null ? null : userRepository.findById(request.getResponsibleId()).orElseThrow(() ->\n" +
                            " new jakarta.persistence.EntityNotFoundException(\"messages.errors.responsible_not_found\")))")
    })
    public abstract Task requestToTask(UpsertTaskRequest request);
    @Mappings({
            @Mapping(source = "id", target = "taskId"),
            @Mapping(source = "author.id", target = "authorId"),
            @Mapping(source = "responsible.id", target = "responsibleId"),
    })
    public abstract TaskResponse taskToResponse(Task booking);
}
