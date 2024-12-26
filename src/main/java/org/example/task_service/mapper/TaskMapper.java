package org.example.task_service.mapper;

import org.example.task_service.dto.task.TaskResponse;
import org.example.task_service.dto.task.UpsertTaskRequest;
import org.example.task_service.entity.Task;
import org.example.task_service.repository.UserRepository;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.ReportingPolicy;
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
                    expression = "java(userRepository.findById(request.getResponsibleId()).orElseThrow(() ->\n" +
                            " new jakarta.persistence.EntityNotFoundException(\"Responsible user not found\")))")
    })
    public abstract Task requestToTask(UpsertTaskRequest request);
    @Mappings({
            @Mapping(source = "id", target = "taskId"),
            @Mapping(source = "author.id", target = "authorId"),
            @Mapping(source = "responsible.id", target = "responsibleId"),
    })
    public abstract TaskResponse taskToResponse(Task booking);
}
