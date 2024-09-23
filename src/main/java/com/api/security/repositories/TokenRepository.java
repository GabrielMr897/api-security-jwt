package com.api.security.repositories;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.api.security.models.Token;

public interface TokenRepository extends JpaRepository<Token,UUID> {

    List<Token> findAllAccessTokensByUserId(UUID id);

  @Query("SELECT t FROM Token t " +
           "WHERE (CASE WHEN :isRefreshToken = true THEN t.refreshToken ELSE t.accessToken END) = :token")
    Optional<Token> findByToken(@Param("token") String token, @Param("isRefreshToken") boolean isRefreshToken);
    
}
