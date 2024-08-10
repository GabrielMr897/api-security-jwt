package com.api.security.repositories;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.api.security.models.Token;

public interface TokenRepository extends JpaRepository<Token,UUID> {

    List<Token> findAllAccessTokensByUserId(UUID id);

    Optional<Token> findByAccessToken(String token);

    Optional<Token> findByRefreshToken(String token);
    
}
