package org.example.task_service.web.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.task_service.dto.auth.AuthRequest;
import org.example.task_service.dto.auth.AuthResponse;
import org.example.task_service.security.SecurityService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/login")
@RequiredArgsConstructor
public class AuthController
{
    private final SecurityService service;

    @PostMapping
    public AuthResponse login(@RequestBody @Valid AuthRequest request) {
        return service.login(request);
    }
}
