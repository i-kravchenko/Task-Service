package org.example.task_service.security;

import lombok.RequiredArgsConstructor;
import org.example.task_service.dto.auth.AuthRequest;
import org.example.task_service.dto.auth.AuthResponse;
import org.example.task_service.entity.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SecurityService
{
    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    public AuthResponse login(AuthRequest request) {
        User user = (User) userDetailsService.loadUserByUsername(request.getUsername());
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }
        String token = jwtProvider.generateToken(user);
        AuthResponse response = new AuthResponse();
        response.setToken(token);
        return response;
    }
}
