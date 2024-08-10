package com.api.security.services.auth;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.api.security.DTO.Auth.AuthenticationRequestDTO;
import com.api.security.DTO.Auth.AuthenticationResponseDTO;
import com.api.security.DTO.Role.UserRoleDTO;
import com.api.security.DTO.User.UserRequestDTO;
import com.api.security.DTO.User.UserResponseDTO;
import com.api.security.exceptions.conflict.UserAlreadyExistsException;
import com.api.security.exceptions.notFound.RoleNotFoundException;
import com.api.security.models.Role;
import com.api.security.models.User;
import com.api.security.repositories.RolesRepository;
import com.api.security.repositories.UserRepository;

import jakarta.security.auth.message.AuthException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthenticationService implements UserDetailsService {

    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private RolesRepository rolesRepository;

    @Lazy
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private TokenService tokenService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserDetails userDetails = userRepository.findByLogin(username);

        if (userDetails == null) {
            throw new UsernameNotFoundException("User with login " + username + " not found.");
        }
        return userDetails;
    }

    @Transactional
    public UserResponseDTO register(UserRequestDTO user)
            throws IOException {
        
        List<Role> roles = new ArrayList<>();

        Role role;


        if (userRepository.existsByLogin(user.getLogin())) {
            throw new UserAlreadyExistsException("User with login " + user.getLogin() + " already exists.");
        }
        for (UserRoleDTO roleDTO : user.getRoles()) {
            role = rolesRepository.findByEnumRoles(roleDTO.getRoleName())
                    .orElseThrow(() -> new RoleNotFoundException("Error: Role '"
                            + roleDTO.getRoleName() + "' not Found."));
            roles.add(role);
        }

        User userSave = User.builder()
                .login(user.getLogin())
                .password(passwordEncoder.encode(user.getPassword()))
                .role(roles)
                .build();
        User savedUser = userRepository.save(userSave);
        return new UserResponseDTO(savedUser);
    }

    
    @Transactional
    public AuthenticationResponseDTO login(AuthenticationRequestDTO a)
            throws AuthException {
            Authentication auth = this.authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                        a.getLogin(),
                        a.getPassword()));
            String jwtToken = tokenService.generateAccessToken((User) auth.getPrincipal());
            String jwtRefreshToken = tokenService.generateRefreshToken((User) auth.getPrincipal());
            tokenService.revokeAllTokenByUser((User) auth.getPrincipal());
            tokenService.saveUserToken(jwtToken, jwtRefreshToken, (User) auth.getPrincipal());
            return AuthenticationResponseDTO
                    .builder()
                    .token(jwtToken)
                    .refreshToken(jwtRefreshToken)
                    .build();

    }
    
    public AuthenticationResponseDTO refreshToken(
        HttpServletRequest request,
        HttpServletResponse response) throws AuthException {
    String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
        throw new AuthException("Authorization header is missing or invalid.");
    }

    String token = authHeader.substring(7);

    String username = tokenService.validateToken(token);

    UserDetails userDetails = userRepository.findByLogin(username);
    User user = (User) userDetails;

    if (tokenService.isValidRefreshToken(token, user)) {
        String accessToken = tokenService.generateAccessToken(user);
        String refreshToken = tokenService.generateRefreshToken(user);

        tokenService.revokeAllTokenByUser(user);
        tokenService.saveUserToken(accessToken, refreshToken, user);

        return  AuthenticationResponseDTO
                    .builder()
                    .token(token)
                    .refreshToken(refreshToken)
                    .build();
    } else {
        throw new AuthException("Invalid refresh token.");
    }
}



}
