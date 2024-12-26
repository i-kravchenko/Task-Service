package org.example.task_service.dto.task;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentResponse
{
    private Long id;
    private Long taskId;
    private Long userId;
    private String text;
}
