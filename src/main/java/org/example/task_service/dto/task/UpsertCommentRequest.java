package org.example.task_service.dto.task;

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
    @NotNull(message = "taskId is required")
    private Long taskId;
    @NotBlank(message = "text is required")
    private String text;
}
