package org.example.task_service.dto.task;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.task_service.entity.Priority;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpsertTaskRequest
{
    @Schema(description = "Название задачи")
    @NotBlank(message = "title is required")
    private String title;
    @Schema(description = "Описание задачи")
    @NotBlank(message = "description is required")
    private String description;
    @Schema(description = "Id ответственного за задачу")
    @NotNull(message = "responsibleId is required")
    private Long responsibleId;
    @Schema(description = "Приоритет задачи", defaultValue = "LOW")
    private Priority priority;
}
