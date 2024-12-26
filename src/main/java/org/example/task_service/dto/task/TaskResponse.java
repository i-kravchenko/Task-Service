package org.example.task_service.dto.task;

import io.swagger.v3.oas.annotations.media.Schema;
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
    @Schema(description = "Id задачи")
    private Long taskId;
    @Schema(description = "Название задачи")
    private String title;
    @Schema(description = "Описание задачи")
    private String description;
    @Schema(description = "Приоритет задачи")
    private Priority priority;
    @Schema(description = "Статус задачи")
    private Status status;
    @Schema(description = "Id ответственного за задачу")
    private Long responsibleId;
    @Schema(description = "Id автора задачи")
    private Long authorId;
    @Schema(description = "Комментарии к задаче")
    private List<CommentResponse> comments;
}
