package org.example.task_service.web.controller;

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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc(printOnlyOnFailure = false)
class TaskController_findTaskTest
{
    @Autowired
    MockMvc mockMvc;

    @Test
    @DisplayName("Получение задачи под администратором")
    void findTask_RequestedByAdmin_ReturnsSuccessResponse() throws Exception {
        // given
        var requestBuilder = MockMvcRequestBuilders
                .get("/api/tasks/1")
                .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN")));

        // when
        mockMvc.perform(requestBuilder)
                // then
                .andExpectAll(
                        status().isOk(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON),
                        jsonPath("taskId").value(1)
                );
    }

    @Test
    @DisplayName("Попытка получения несуществующей задачи")
    void findTask_RequestedByAdmin_ReturnsNotFoundError() throws Exception {
        // given
        var requestBuilder = MockMvcRequestBuilders
                .get("/api/tasks/10")
                .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN")));

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
    @DisplayName("Получение задачи под рядовым пользователем")
    void findTask_RequestedByOrdinaryUser_ReturnsSuccessResponse() throws Exception {
        // given
        User user = new User(2L, "user@mail.com", null, Set.of(Role.ROLE_USER));
        var requestBuilder = MockMvcRequestBuilders
                .get("/api/tasks/2")
                .with(authentication(
                        new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities())));

        // when
        mockMvc.perform(requestBuilder)
                // then
                .andExpectAll(
                        status().isOk(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON),
                        jsonPath("taskId").value(2)
                );
    }

    @Test
    @DisplayName("Попытка получения задачи без авторизации")
    void findTask_RequestedByUnauthorizedUser_ReturnsUnauthorizedError() throws Exception {
        // given
        var requestBuilder = MockMvcRequestBuilders
                .get("/api/tasks/1");

        // when
        mockMvc.perform(requestBuilder)
                // then
                .andExpectAll(
                        status().isUnauthorized(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON),
                        jsonPath("errorMessage").isString()
                );
    }

    @Test
    @DisplayName("Попытка получения чужой задачи под рядовым пользователем")
    void findTask_RequestedByOrdinaryUser_CanReadOnlySelfTask() throws Exception {
        // given
        User user = new User(2L, "user@mail.com", null, Set.of(Role.ROLE_USER));
        var requestBuilder = MockMvcRequestBuilders
                .get("/api/tasks/1")
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
}