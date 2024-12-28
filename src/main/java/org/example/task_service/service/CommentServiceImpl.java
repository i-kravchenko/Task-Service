package org.example.task_service.service;

import lombok.RequiredArgsConstructor;
import org.example.task_service.dto.task.CommentResponse;
import org.example.task_service.dto.task.UpsertCommentRequest;
import org.example.task_service.entity.Comment;
import org.example.task_service.entity.Role;
import org.example.task_service.entity.User;
import org.example.task_service.mapper.CommentMapper;
import org.example.task_service.repository.CommentRepository;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService
{
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;
    private final MessageSource messageSource;

    @Override
    public CommentResponse addComment(UpsertCommentRequest request) {
        User currentUser = (User) SecurityContextHolder
                .getContext().getAuthentication().getPrincipal();
        Comment comment = commentMapper.requestToComment(request);
        if(!currentUser.getRoles().contains(Role.ROLE_ADMIN) &&
                !currentUser.getId().equals(comment.getTask().getResponsible().getId())) {
            Locale locale = LocaleContextHolder.getLocale();
            throw new AccessDeniedException(messageSource.getMessage(
                    "messages.errors.access_denied_modify_task",
                    new Object[]{comment.getTask().getId()},
                    "You cant add comment to task: " + comment.getTask().getId(),
                  locale
            ));
        }
        comment.setUser(currentUser);
        return commentMapper.commentToResponse(commentRepository.save(comment));
    }
}
