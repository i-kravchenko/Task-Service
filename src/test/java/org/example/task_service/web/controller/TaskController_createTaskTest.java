package org.example.task_service.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.task_service.dto.task.UpsertTaskRequest;
import org.example.task_service.entity.Priority;
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
class TaskController_createTaskTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    @DisplayName("Создание задачи под администратором")
    void createTask_RequestedByAdmin_ReturnsSuccessResponse() throws Exception {
        // given
        var request = new UpsertTaskRequest();
        request.setTitle("New task");
        request.setDescription("Task description");
        request.setResponsibleId(1L);
        User user = new User(1L, "admin@mail.com", null, Set.of(Role.ROLE_ADMIN));
        var requestBuilder = MockMvcRequestBuilders
                .post("/api/tasks")
                .content(objectMapper.writeValueAsBytes(request))
                .contentType(MediaType.APPLICATION_JSON)
                .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN")))
                .with(authentication(
                        new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities())));

        // when
        mockMvc.perform(requestBuilder)
                // then
                .andExpectAll(
                        status().isCreated(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON),
                        jsonPath("$.taskId").value(3),
                        jsonPath("$.priority").value(Priority.LOW.toString())
                );
    }

    @Test
    @DisplayName("Попытка создания задачи на несуществующего ползователя")
    void createTask_RequestedByAdmin_ReturnsNotFound() throws Exception {
        // given
        var request = new UpsertTaskRequest();
        request.setTitle("New task");
        request.setDescription("Task description");
        request.setResponsibleId(10L);
        User user = new User(1L, "admin@mail.com", null, Set.of(Role.ROLE_ADMIN));
        var requestBuilder = MockMvcRequestBuilders
                .post("/api/tasks")
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
    @DisplayName("Создание задачи с невалидными данными")
    void createTask_RequestedWithInvalidData_ReturnsBadRequestError() throws Exception {
        // given
        var request = new UpsertTaskRequest();
        User user = new User(1L, "admin@mail.com", null, Set.of(Role.ROLE_ADMIN));
        var requestBuilder = MockMvcRequestBuilders
                .post("/api/tasks")
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
    @DisplayName("Попытка создание задачи рядовым пользователем")
    void createTask_RequestedByOrdinaryUser_ReturnsAccessDeniedError() throws Exception {
        // given
        var request = new UpsertTaskRequest();
        request.setTitle("New task");
        request.setDescription("Task description");
        request.setResponsibleId(1L);
        var requestBuilder = MockMvcRequestBuilders
                .post("/api/tasks")
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
    @DisplayName("Попытка создание задачи неавторизованным пользователем")
    void createTask_RequestedByUnauthorizedUser_ReturnsUnauthorizedError() throws Exception {
        // given
        var request = new UpsertTaskRequest();
        request.setTitle("New task");
        request.setDescription("Task description");
        request.setResponsibleId(1L);
        var requestBuilder = MockMvcRequestBuilders
                .post("/api/tasks")
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