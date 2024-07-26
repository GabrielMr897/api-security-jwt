package com.api.security.DTO.Product;

import java.math.BigDecimal;


import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ProductRequestDTO {
    @NotBlank()
    private String code;

    @NotBlank
    private String description;


    private boolean isBlocked = false;
    
    @NotBlank
    private BigDecimal value;

    @NotBlank
    private String measurementUnit;

}
