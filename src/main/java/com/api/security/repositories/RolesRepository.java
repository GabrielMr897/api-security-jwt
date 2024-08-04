package com.api.security.repositories;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.api.security.enums.EnumRoles;
import com.api.security.models.Role;

public interface RolesRepository extends JpaRepository<Role,UUID> {
    Optional<Role> findByEnumRoles(EnumRoles name);
}
