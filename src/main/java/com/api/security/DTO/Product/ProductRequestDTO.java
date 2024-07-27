package com.api.security.DTO.Product;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ProductRequestDTO {
    @NotBlank
    private String code;

    @NotBlank
    private String description;

    @JsonProperty("blocked")
    private boolean isBlocked = false;
    
    @NotBlank
    private BigDecimal value;

    @NotBlank
    private String measurementUnit;

}
