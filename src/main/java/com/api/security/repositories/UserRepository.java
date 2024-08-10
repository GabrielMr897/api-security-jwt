package com.api.security.repositories;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;

import com.api.security.models.User;



public interface UserRepository extends JpaRepository<User,UUID> {

    UserDetails findByLogin(String login);



    boolean existsByLogin(String login);
}