package com.api.security.repositories;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.api.security.models.Product;

public interface ProductRepository extends JpaRepository<Product,UUID> {

    
}
