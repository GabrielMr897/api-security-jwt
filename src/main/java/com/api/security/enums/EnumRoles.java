package com.api.security.enums;

public enum EnumRoles {

    ROLE_ADMIN("ROLE_ADMIN"),
    ROLE_USER("ROLE_USER");

    private String role;

    EnumRoles(String role) {
        this.role = role;
    }


    public String getRole() {
        return role;
    }
}
