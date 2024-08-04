package com.api.security.DTO.Auth;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AuthenticationRequestDTO {
    
    @NotBlank
    public String login;

    @NotBlank
    public String password;
}
