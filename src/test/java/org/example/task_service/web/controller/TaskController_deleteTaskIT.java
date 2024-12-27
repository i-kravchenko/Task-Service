package org.example.task_service.web.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc(printOnlyOnFailure = false)
class TaskController_deleteTaskIT {
    @Autowired
    MockMvc mockMvc;

    @Test
    @DisplayName("Удаление задачи под администратором")
    void deleteTask_RequestedByAdmin_ReturnsSuccessResponse() throws Exception {
        // given
        var requestBuilder = MockMvcRequestBuilders
                .delete("/api/tasks/1")
                .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN")));

        // when
        mockMvc.perform(requestBuilder)
                // then
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Попытка удаления несуществующей задачи")
    void deleteTask_RequestedByAdmin_ReturnsNotFoundError() throws Exception {
        // given
        var requestBuilder = MockMvcRequestBuilders
                .delete("/api/tasks/10")
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
    @DisplayName("Попытка Удаление задачи рядовым пользователем")
    void deleteTask_RequestedByOrdinaryUser_ReturnsAccessDeniedError() throws Exception {
        // given
        var requestBuilder = MockMvcRequestBuilders
                .delete("/api/tasks/1")
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
    @DisplayName("Попытка Удаление задачи неавторизованным пользователем")
    void deleteTask_RequestedByUnauthorizedUser_ReturnsUnauthorizedError() throws Exception {
        // given
        var requestBuilder = MockMvcRequestBuilders.delete("/api/tasks/1");
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