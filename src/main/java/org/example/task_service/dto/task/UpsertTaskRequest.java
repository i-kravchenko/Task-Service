package org.example.task_service.dto.task;

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
    @NotBlank(message = "title is required")
    private String title;
    @NotBlank(message = "description is required")
    private String description;
    @NotNull(message = "responsibleId is required")
    private Long responsibleId;
    private Priority priority;
}
