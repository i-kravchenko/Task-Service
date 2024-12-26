package org.example.task_service.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.task_service.dto.auth.AuthRequest;
import org.example.task_service.dto.auth.AuthResponse;
import org.example.task_service.security.SecurityService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/login")
@RequiredArgsConstructor
@Tag(name = "Security")
public class AuthController
{
    private final SecurityService service;

    @PostMapping
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Пользователь успешно аутентифицирован"),
            @ApiResponse(responseCode = "403", description = "Ошибка аутентификации")
    })
    @Operation(summary = "Запрос на авторизацию",  description = "Метод для получения токена авторизации")
    public AuthResponse login(@RequestBody @Valid AuthRequest request) {
        return service.login(request);
    }
}
