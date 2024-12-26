package org.example.task_service.service;

import org.example.task_service.dto.task.CommentResponse;
import org.example.task_service.dto.task.UpsertCommentRequest;

public interface CommentService
{
    CommentResponse addComment(UpsertCommentRequest request);
}
