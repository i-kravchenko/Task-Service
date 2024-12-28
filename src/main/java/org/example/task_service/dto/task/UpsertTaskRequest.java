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
    @NotBlank(message = "messages.errors.title_required")
    private String title;
    @Schema(description = "Описание задачи")
    @NotBlank(message = "messages.errors.description_required")
    private String description;
    @Schema(description = "Id ответственного за задачу")
    @NotNull(message = "messages.errors.responsible_required")
    private Long responsibleId;
    @Schema(description = "Приоритет задачи", defaultValue = "LOW")
    private Priority priority;
}
