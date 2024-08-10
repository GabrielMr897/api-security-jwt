package com.api.security.repositories;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.api.security.models.Product;


public interface ProductRepository extends JpaRepository<Product,UUID> {

    Product findByCode(String code);

    @Query("SELECT p FROM Product p WHERE LOWER(p.code) LIKE LOWER(CONCAT('%', :code, '%'))")
    Page<Product> findByCodeContainingIgnoreCase(@Param("code") String code, Pageable pageable);


    boolean existsByCode(String code);

    boolean existsById(UUID id);
}
