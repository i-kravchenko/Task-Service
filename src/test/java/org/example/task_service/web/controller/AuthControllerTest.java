package org.example.task_service.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.task_service.dto.auth.AuthRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest
{
    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    @DisplayName("Получение токена пользователя")
    void login_SendValidCredentials_ReturnSuccessResponse() throws Exception {
        AuthRequest request = new AuthRequest();
        request.setEmail("admin@mail.com");
        request.setPassword("admin");
        var requestBuilder = MockMvcRequestBuilders
                .post("/api/login")
                .content(objectMapper.writeValueAsBytes(request))
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc
                .perform(requestBuilder)
                .andExpectAll(
                        status().isOk(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON),
                        jsonPath("$.token").isString()
                );
    }

    @Test
    @DisplayName("Попытка получения токена с невалидными данными")
    void login_SendInvalidCredentials_ReturnBadRequestError() throws Exception {
        AuthRequest request = new AuthRequest();
        request.setEmail("admin@");
        request.setPassword("admin");
        var requestBuilder = MockMvcRequestBuilders
                .post("/api/login")
                .content(objectMapper.writeValueAsBytes(request))
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc
                .perform(requestBuilder)
                .andExpectAll(
                        status().isBadRequest(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON),
                        jsonPath("errorMessage").isString()
                );
    }

    @Test
    @DisplayName("Попытка получения токена несуществующим пользователем")
    void login_SendNoExistingCredentials_ReturnAccessDeniedError() throws Exception {
        AuthRequest request = new AuthRequest();
        request.setEmail("test@mail.com");
        request.setPassword("test");
        var requestBuilder = MockMvcRequestBuilders
                .post("/api/login")
                .content(objectMapper.writeValueAsBytes(request))
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc
                .perform(requestBuilder)
                .andExpectAll(
                        status().isForbidden(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON),
                        jsonPath("errorMessage").isString()
                );
    }
}