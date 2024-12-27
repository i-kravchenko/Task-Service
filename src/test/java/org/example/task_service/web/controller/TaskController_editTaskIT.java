package org.example.task_service.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.task_service.dto.task.UpsertTaskRequest;
import org.example.task_service.entity.Role;
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
class TaskController_editTaskIT {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    @DisplayName("Изменение задачи под администратором")
    void editTask_RequestedByAdmin_ReturnsSuccessResponse() throws Exception {
        // given
        var request = new UpsertTaskRequest();
        request.setTitle("New task");
        User user = new User(1L, "admin@mail.com", null, Set.of(Role.ROLE_ADMIN));
        var requestBuilder = MockMvcRequestBuilders
                .patch("/api/tasks/1")
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
                        jsonPath("$.taskId").value(1),
                        jsonPath("$.title").value("New task")
                );
    }

    @Test
    @DisplayName("Попытка изменения несуществующей задачи")
    void editTask_RequestedByAdmin_ReturnsNotFoundError() throws Exception {
        // given
        var request = new UpsertTaskRequest();
        request.setTitle("New task");
        User user = new User(1L, "admin@mail.com", null, Set.of(Role.ROLE_ADMIN));
        var requestBuilder = MockMvcRequestBuilders
                .patch("/api/tasks/10")
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
    @DisplayName("Попытка Изменение задачи рядовым пользователем")
    void editTask_RequestedByOrdinaryUser_ReturnsAccessDeniedError() throws Exception {
        // given
        var request = new UpsertTaskRequest();
        request.setTitle("New task");
        var requestBuilder = MockMvcRequestBuilders
                .patch("/api/tasks/1")
                .content(objectMapper.writeValueAsBytes(request))
                .contentType(MediaType.APPLICATION_JSON)
                .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_USER")));

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
    @DisplayName("Попытка Изменение задачи неавторизованным пользователем")
    void editTask_RequestedByUnauthorizedUser_ReturnsUnauthorizedError() throws Exception {
        // given
        var request = new UpsertTaskRequest();
        request.setTitle("New task");
        var requestBuilder = MockMvcRequestBuilders
                .patch("/api/tasks/1")
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