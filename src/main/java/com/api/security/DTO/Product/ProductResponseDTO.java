package com.api.security.DTO.Product;

import java.math.BigDecimal;

import com.api.security.models.Product;
import com.google.firebase.database.annotations.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProductResponseDTO {
    @NotNull
    private String code;

    @NotNull
    private String description;

    @NotNull
    private boolean isBlocked = false;
    
    @NotNull
    private BigDecimal value;

    @NotNull
    private String measurementUnit;

    private String image;

    public static ProductResponseDTO fromEntity(Product product) {
        return new ProductResponseDTO(
            product.getCode(),
            product.getDescription(),
            product.isBlocked(),
            product.getValue(),
            product.getMeasurementUnit(),
            product.getImage()
        );
    }
        
}
