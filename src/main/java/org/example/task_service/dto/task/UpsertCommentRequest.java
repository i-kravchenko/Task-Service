package org.example.task_service.dto.task;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpsertCommentRequest
{
    @Schema(description = "Id задачи")
    @NotNull(message = "taskId is required")
    private Long taskId;
    @Schema(description = "Текст комментария")
    @NotBlank(message = "text is required")
    private String text;
}
