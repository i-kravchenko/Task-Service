package org.example.task_service.dto.task;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.task_service.entity.Priority;
import org.example.task_service.entity.Status;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaskResponse
{
    private Long taskId;
    private String title;
    private String description;
    private Priority priority;
    private Status status;
    private Long responsibleId;
    private Long authorId;
    private List<CommentResponse> comments;
}
