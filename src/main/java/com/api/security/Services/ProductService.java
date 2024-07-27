package com.api.security.Services;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.api.security.DTO.Product.ProductRequestDTO;
import com.api.security.DTO.Product.ProductResponseDTO;
import com.api.security.exceptions.conflict.ProductAlreadyExistsException;
import com.api.security.models.Product;
import com.api.security.repositories.ProductRepository;

import jakarta.transaction.Transactional;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private FirebaseService firebaseService;
    
    @Transactional
    public ProductResponseDTO save(ProductRequestDTO pr, MultipartFile file)
            throws IOException {
        
        String urlFile = "";
        if (productRepository.existsByCode(pr.getCode())) {
            throw new ProductAlreadyExistsException("Product with code " + pr.getCode() + " already exists.");
        }

        if (file != null && !file.isEmpty()) {
            urlFile = "https://firebasestorage.googleapis.com/v0/b/api-security-23723.appspot.com/o/" + firebaseService.saveFile(file) + "?alt=media";
        }

        Product product = Product.builder()
            .code(pr.getCode())
            .description(pr.getDescription())
            .image(urlFile)
            .value(pr.getValue())
            .isBlocked(false)
            .measurementUnit(pr.getMeasurementUnit())
            .build();
        product = this.productRepository.save(product);
        return ProductResponseDTO.fromEntity(product);
    }
}
