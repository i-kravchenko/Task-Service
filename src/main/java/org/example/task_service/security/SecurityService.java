package org.example.task_service.security;

import lombok.RequiredArgsConstructor;
import org.example.task_service.dto.auth.AuthRequest;
import org.example.task_service.dto.auth.AuthResponse;
import org.example.task_service.entity.User;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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
        try {
            User user = (User) userDetailsService.loadUserByUsername(request.getEmail());
            if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
                throw new BadCredentialsException("messages.errors.invalid_credentials");
            }
            String token = jwtProvider.generateToken(user);
            AuthResponse response = new AuthResponse();
            response.setToken(token);
            return response;
        } catch (UsernameNotFoundException ex) {
            throw new BadCredentialsException("messages.errors.invalid_credentials");
        }
    }
}
