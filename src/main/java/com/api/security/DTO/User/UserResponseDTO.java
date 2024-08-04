package com.api.security.DTO.User;

import java.util.ArrayList;
import java.util.List;

import com.api.security.DTO.Role.UserRoleDTO;
import com.api.security.models.User;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserResponseDTO {
    
    @NotBlank
    public String login;

    private List<UserRoleDTO> roles = new ArrayList<>();

    public UserResponseDTO(User user) {
        this.login = user.getUsername();
        if (user.getRole() != null && !user.getRole().isEmpty()) {
            user.getRole().stream().forEach(r -> roles.add(new UserRoleDTO(r)));
        }
    }
}
