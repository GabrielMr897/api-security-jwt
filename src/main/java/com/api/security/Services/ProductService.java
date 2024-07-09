package com.api.security.Services;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.api.security.exceptions.notFound.ProductNotFoundException;
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
    public Product save(Product pr, MultipartFile file)
            throws ProductNotFoundException, IOException {
        
        String urlFile = "";
        

        if (!file.isEmpty()) {
            urlFile = "https://firebasestorage.googleapis.com/v0/b/api-security-23723.appspot.com/o/" + firebaseService.saveFile(file) + "?alt=media";
        }

        Product product = Product.builder()
            .code(pr.getCode())
            .description(pr.getDescription())
            .image(urlFile)
            .isBlocked(false)
            .measurementUnit(pr.getMeasurementUnit())
            .build();
        product = this.productRepository.save(product);
        return product;
    }
}
