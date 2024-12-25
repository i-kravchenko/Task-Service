package org.example.task_service.dto.task;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaskRequest
{
    private Long authorId;
    private Long responsibleId;
    private int pageSize = 5;
    private int pageNumber = 0;
}
