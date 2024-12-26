package org.example.task_service.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthRequest
{
    @NotBlank(message = "email is required")
    @Email(message = "incorrect email")
    @Schema(description = "Электронная почта пользователя", example = "example@mail.com")
    private String email;
    @NotBlank(message = "password is required")
    @Schema(description = "Пароль пользователя", example = "password")
    private String password;
}
