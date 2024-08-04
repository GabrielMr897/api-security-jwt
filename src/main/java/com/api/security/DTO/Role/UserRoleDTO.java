package com.api.security.DTO.Role;

import com.api.security.enums.EnumRoles;
import com.api.security.models.Role;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserRoleDTO {

    @NotBlank
    private EnumRoles roleName;

    public UserRoleDTO(Role role) {
        this.roleName = role.getEnumRoles();
    }
}
