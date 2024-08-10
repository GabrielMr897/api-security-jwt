package com.api.security.services;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.api.security.DTO.Product.ProductRequestDTO;
import com.api.security.DTO.Product.ProductResponseDTO;
import com.api.security.exceptions.conflict.ProductAlreadyExistsException;
import com.api.security.exceptions.notFound.ProductNotFoundException;
import com.api.security.models.Product;
import com.api.security.repositories.ProductRepository;

import jakarta.transaction.Transactional;

import com.api.security.utils.PropertyUtils;

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

    @Transactional
    public List<ProductResponseDTO> findAll() {
        return productRepository.findAll().stream()
                .map(ProductResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public ProductResponseDTO getById(UUID id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product with ID " + id + " not found."));
        return ProductResponseDTO.fromEntity(product);
    }

    @Transactional
    public Page<ProductResponseDTO> search(String query, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Product> productPage = productRepository.findByCodeContainingIgnoreCase(query, pageable);
        return productPage.map(ProductResponseDTO::fromEntity);
    }


    @Transactional
    public ProductResponseDTO update(UUID id, ProductRequestDTO pr)
            throws IOException {
        
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product with ID " + id + " not found."));

        if (productRepository.existsByCode(pr.getCode())) {
            throw new ProductAlreadyExistsException("Product with code " + pr.getCode() + " already exists.");
        }

        BeanUtils.copyProperties(pr, product, PropertyUtils.getNullPropertyNames(pr));

        product = productRepository.save(product);
        return ProductResponseDTO.fromEntity(product);
    }

    @Transactional
    public String updateImage(UUID id, MultipartFile file)
            throws IOException {
        String urlFile = "";
        
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product with ID " + id + " not found."));
        
        if (file != null && !file.isEmpty()) {
            urlFile = "https://firebasestorage.googleapis.com/v0/b/api-security-23723.appspot.com/o/" + firebaseService.saveFile(file) + "?alt=media";
        }


        product.setImage(urlFile);

        product = productRepository.save(product);
        return urlFile;
    }

    @Transactional
    public void delete(UUID id) throws IOException  {
        if (!productRepository.existsById(id)) {
            throw new ProductNotFoundException("Product with ID " + id + " not found.");
        }
        productRepository.deleteById(id);
    }

}
