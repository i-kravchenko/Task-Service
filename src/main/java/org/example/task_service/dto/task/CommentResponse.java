package org.example.task_service.dto.task;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentResponse
{
    @Schema(description = "Id комментария")
    private Long id;
    @Schema(description = "Id задачи")
    private Long taskId;
    @Schema(description = "Id автора комментария")
    private Long userId;
    @Schema(description = "текст комментария")
    private String text;
}
