package com.api.security.DTO.User;

import java.util.ArrayList;
import java.util.List;

import com.api.security.DTO.Role.UserRoleDTO;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserRequestDTO {
    @NotBlank
    public String login;

    @NotBlank
    public String password;

    @NotBlank
    private List<UserRoleDTO> roles = new ArrayList<>();
}
