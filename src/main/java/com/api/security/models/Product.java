package com.api.security.models;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "products")
@Data
@EqualsAndHashCode(callSuper=false)
public class Product extends AbstractEntity {
    @Column(nullable = false, unique = true)
    private String code;

    @Column(nullable = false)
    private String description;

    @Builder.Default
    @Column(name = "is_Blocked", nullable = false, columnDefinition = "boolean default false")
    private boolean isBlocked = false;
    
    @Column(nullable = false)
    private BigDecimal value;

    @Column(nullable = false)
    private String measurementUnit;

    @Column
    private String image;
        

}
