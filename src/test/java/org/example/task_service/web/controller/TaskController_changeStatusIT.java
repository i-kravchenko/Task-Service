package org.example.task_service.web.controller;

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
class TaskController_changeStatusIT {
    @Autowired
    MockMvc mockMvc;

    @Test
    @DisplayName("Изменение статуса задачи под администратором")
    void changeStatus_RequestedByAdmin_ReturnsSuccessResponse() throws Exception {
        // given
        User user = new User(1L, "admin@mail.com", null, Set.of(Role.ROLE_ADMIN));
        var requestBuilder = MockMvcRequestBuilders
                .patch("/api/tasks/1/change-status")
                .param("status", Status.COMPLETE.toString())
                .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN")))
                .with(authentication(
                        new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities())));

        // when
        mockMvc.perform(requestBuilder)
                // then
                .andExpectAll(
                        status().isOk(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON),
                        jsonPath("$.status").value(Status.COMPLETE.toString())
                );
    }

    @Test
    @DisplayName("Попытка изменения статуса несуществующей задачи")
    void changeStatus_RequestedByAdmin_ReturnsNotFoundError() throws Exception {
        // given
        User user = new User(1L, "admin@mail.com", null, Set.of(Role.ROLE_ADMIN));
        var requestBuilder = MockMvcRequestBuilders
                .patch("/api/tasks/10/change-status")
                .param("status", Status.COMPLETE.toString())
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
    @DisplayName("Изменение статуса задачи рядовым пользователем")
    void changeStatus_RequestedByOrdinaryUser_ReturnsSuccessResponse() throws Exception {
        // given
        User user = new User(2L, "user@mail.com", null, Set.of(Role.ROLE_USER));
        var requestBuilder = MockMvcRequestBuilders
                .patch("/api/tasks/2/change-status")
                .param("status", Status.COMPLETE.toString())
                .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_USER")))
                .with(authentication(
                        new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities())));

        // when
        mockMvc.perform(requestBuilder)
                // then
                .andExpectAll(
                        status().isOk(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON),
                        jsonPath("$.status").value(Status.COMPLETE.toString())
                );
    }

    @Test
    @DisplayName("Попытка изменение статуса чужой задачи рядовым пользователем")
    void changeStatus_RequestedByOrdinaryUser_ReturnsAccessDeniedError() throws Exception {
        // given
        User user = new User(2L, "user@mail.com", null, Set.of(Role.ROLE_USER));
        var requestBuilder = MockMvcRequestBuilders
                .patch("/api/tasks/1/change-status")
                .param("status", Status.COMPLETE.toString())
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
    @DisplayName("Попытка Изменение статуса задачи неавторизованным пользователем")
    void changeStatus_RequestedByUnauthorizedUser_ReturnsUnauthorizedError() throws Exception {
        // given
        var requestBuilder = MockMvcRequestBuilders
                .patch("/api/tasks/1/change-status")
                .param("status", Status.COMPLETE.toString());

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