package org.example.task_service.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.task_service.dto.task.UpsertCommentRequest;
import org.example.task_service.entity.Role;
import org.example.task_service.entity.Status;
import org.example.task_service.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc(printOnlyOnFailure = false)
class TaskController_addCommentIT {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    @DisplayName("Добавление комментария к задаче под администратором")
    void addComment_RequestedByAdmin_ReturnsSuccessResponse() throws Exception {
        // given
        UpsertCommentRequest request = new UpsertCommentRequest();
        request.setTaskId(1L);
        request.setText("New comment");
        User user = new User(1L, "admin@mail.com", null, Set.of(Role.ROLE_ADMIN));
        var requestBuilder = MockMvcRequestBuilders
                .post("/api/tasks/add-comment")
                .content(objectMapper.writeValueAsBytes(request))
                .contentType(MediaType.APPLICATION_JSON)
                .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN")))
                .with(authentication(
                        new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities())));

        // when
        mockMvc.perform(requestBuilder)
                // then
                .andExpectAll(
                        status().isOk(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON),
                        jsonPath("$.taskId").value(1L),
                        jsonPath("$.userId").value(1L),
                        jsonPath("$.text").value("New comment")
                );
    }

    @Test
    @DisplayName("Попытка добавления комментария к несуществующей задаче")
    void addComment_RequestedByAdmin_ReturnsNotFoundError() throws Exception {
        // given
        UpsertCommentRequest request = new UpsertCommentRequest();
        request.setTaskId(10L);
        request.setText("New comment");
        User user = new User(1L, "admin@mail.com", null, Set.of(Role.ROLE_ADMIN));
        var requestBuilder = MockMvcRequestBuilders
                .post("/api/tasks/add-comment")
                .content(objectMapper.writeValueAsBytes(request))
                .contentType(MediaType.APPLICATION_JSON)
                .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN")))
                .with(authentication(
                        new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities())));

        // when
        mockMvc.perform(requestBuilder)
                // then
                .andExpectAll(
                        status().isNotFound(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON),
                        jsonPath("errorMessage").isString()
                );
    }

    @Test
    @DisplayName("Попытка добавления невалидного комментария к задаче")
    void addComment_RequestedByAdmin_ReturnsBadRequestError() throws Exception {
        // given
        UpsertCommentRequest request = new UpsertCommentRequest();
        request.setTaskId(1L);
        User user = new User(1L, "admin@mail.com", null, Set.of(Role.ROLE_ADMIN));
        var requestBuilder = MockMvcRequestBuilders
                .post("/api/tasks/add-comment")
                .content(objectMapper.writeValueAsBytes(request))
                .contentType(MediaType.APPLICATION_JSON)
                .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN")))
                .with(authentication(
                        new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities())));

        // when
        mockMvc.perform(requestBuilder)
                // then
                .andExpectAll(
                        status().isBadRequest(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON),
                        jsonPath("errorMessage").isString()
                );
    }

    @Test
    @DisplayName("Добавление комментария к задаче рядовым пользователем")
    void addComment_RequestedByOrdinaryUser_ReturnsSuccessResponse() throws Exception {
        // given
        UpsertCommentRequest request = new UpsertCommentRequest();
        request.setTaskId(2L);
        request.setText("New comment");
        User user = new User(2L, "user@mail.com", null, Set.of(Role.ROLE_USER));
        var requestBuilder = MockMvcRequestBuilders
                .post("/api/tasks/add-comment")
                .content(objectMapper.writeValueAsBytes(request))
                .contentType(MediaType.APPLICATION_JSON)
                .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_USER")))
                .with(authentication(
                        new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities())));

        // when
        mockMvc.perform(requestBuilder)
                // then
                .andExpectAll(
                        status().isOk(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON),
                        jsonPath("$.taskId").value(2L),
                        jsonPath("$.userId").value(2L),
                        jsonPath("$.text").value("New comment")
                );
    }

    @Test
    @DisplayName("Попытка добавления к чужой задаче рядовым пользователем")
    void addComment_RequestedByOrdinaryUser_ReturnsAccessDeniedError() throws Exception {
        // given
        UpsertCommentRequest request = new UpsertCommentRequest();
        request.setTaskId(1L);
        request.setText("New comment");
        User user = new User(2L, "user@mail.com", null, Set.of(Role.ROLE_USER));
        var requestBuilder = MockMvcRequestBuilders
                .post("/api/tasks/add-comment")
                .content(objectMapper.writeValueAsBytes(request))
                .contentType(MediaType.APPLICATION_JSON)
                .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_USER")))
                .with(authentication(
                        new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities())));

        // when
        mockMvc.perform(requestBuilder)
                // then
                .andExpectAll(
                        status().isForbidden(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON),
                        jsonPath("errorMessage").isString()
                );
    }

    @Test
    @DisplayName("Попытка добавления комментария к задаче неавторизованным пользователем")
    void addComment_RequestedByUnauthorizedUser_ReturnsUnauthorizedError() throws Exception {
        // given
        UpsertCommentRequest request = new UpsertCommentRequest();
        request.setTaskId(1L);
        request.setText("New comment");
        var requestBuilder = MockMvcRequestBuilders
                .post("/api/tasks/add-comment")
                .content(objectMapper.writeValueAsBytes(request))
                .contentType(MediaType.APPLICATION_JSON);

        // when
        mockMvc.perform(requestBuilder)
                // then
                .andExpectAll(
                        status().isUnauthorized(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON),
                        jsonPath("errorMessage").isString()
                );
    }
}