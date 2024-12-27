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

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc(printOnlyOnFailure = false)
class TaskController_taskListTest
{
    @Autowired
    MockMvc mockMvc;

    @Test
    @DisplayName("Получение списка задач под администратором")
    void taskList_RequestedByAdmin_ReturnsAllTasks() throws Exception {
        // given
        var requestBuilder = MockMvcRequestBuilders
                .get("/api/tasks")
                .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN")));

        // when
        mockMvc.perform(requestBuilder)
                // then
                .andExpectAll(
                        status().isOk(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON),
                        jsonPath("$", hasSize(2))
                );
    }

    @Test
    @DisplayName("Рядовой пользователь может видеть только свои задачи")
    void taskList_RequestedByOrdinaryUser_ReturnsTasksByResponsible() throws Exception {
        // given
        User user = new User(2L, "user@mail.com", null, Set.of(Role.ROLE_USER));
        var requestBuilder = MockMvcRequestBuilders
                .get("/api/tasks")
                .with(authentication(
                        new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities())));

        // when
        mockMvc.perform(requestBuilder)
                // then
                .andExpectAll(
                        status().isOk(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON),
                        jsonPath("$[?(@.responsibleId != 2)]").doesNotExist()
                );
    }

    @Test
    @DisplayName("Получение списка задач без авторизации")
    void taskList_RequestedByUnauthorizedUser_ReturnsUnauthorizedError() throws Exception {
        // given
        var requestBuilder = MockMvcRequestBuilders
                .get("/api/tasks");

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