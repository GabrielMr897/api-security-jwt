package com.api.security.controller;

import java.io.IOException;
import java.net.URI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.api.security.DTO.Product.ProductRequestDTO;
import com.api.security.DTO.Product.ProductResponseDTO;
import com.api.security.Services.ProductService;
import com.api.security.exceptions.conflict.ProductAlreadyExistsException;
import com.api.security.exceptions.notFound.ProductNotFoundException;
import com.api.security.models.Product;
import com.api.security.repositories.ProductRepository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;


@RestController
@RequestMapping("/api/product")
@Tag(name = "Products", description = "Products Test")
public class ProductController {
    
    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRepository productRepository;

    @Operation(summary = "Create Product", responses = {
    @ApiResponse(responseCode = "201", description = "Successfully Register!", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Product.class))),
    @ApiResponse(responseCode = "400", ref = "BadRequest"),
    @ApiResponse(responseCode = "401", ref = "badcredentials"),
    @ApiResponse(responseCode = "403", ref = "forbidden"),
    @ApiResponse(responseCode = "422", ref = "unprocessableEntity"),
    @ApiResponse(responseCode = "500", ref = "internalServerError")
    })
    @PostMapping
    public ResponseEntity<Object> create(@RequestParam(required = false) @Parameter(description = "Optional file upload") MultipartFile file, @Valid @RequestBody ProductRequestDTO productDTO) throws IOException {
        try {
        ProductResponseDTO pr = productService.save(productDTO, file);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(productRepository.findByCode(productDTO.getCode()).getId()).toUri();

        return ResponseEntity.created(uri).body(pr);
        } catch (IOException | ProductNotFoundException | ProductAlreadyExistsException | MaxUploadSizeExceededException e) {
        return ResponseEntity.unprocessableEntity().body(HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }

}
