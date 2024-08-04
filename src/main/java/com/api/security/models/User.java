package com.api.security.models;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.api.security.enums.EnumRoles;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.util.stream.Stream;
import lombok.NoArgsConstructor;

@Table(name = "users")
@Entity
@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class User extends AbstractEntity implements UserDetails {

    @Column(unique = true)
    @Size(max = 50)
    private String login;

    @Size(max = 100)
    private String password;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "user_roles",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private List<Role> role;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
            
        return role.stream()
        .flatMap(role -> {
            if (role.getEnumRoles() == EnumRoles.ROLE_ADMIN) {
                return Stream.of(
                    new SimpleGrantedAuthority(role.getEnumRoles().name()),
                    new SimpleGrantedAuthority("ROLE_USER")
                );
            }
            return Stream.of(new SimpleGrantedAuthority(role.getEnumRoles().name()));
        })
        .collect(Collectors.toList());
    }

    @Override
    public String getUsername() {
        return login;
    }

    
}
