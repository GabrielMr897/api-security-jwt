package com.api.security.controller;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.api.security.DTO.Product.ProductRequestDTO;
import com.api.security.DTO.Product.ProductResponseDTO;
import com.api.security.repositories.ProductRepository;
import com.api.security.services.ProductService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;


@RestController
@RequestMapping("/api/product")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Products", description = "Products Test")
public class ProductController {
    
    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRepository productRepository;

    @Operation(summary = "Create Product", responses = {
    @ApiResponse(responseCode = "200", description = "Successfully Register!", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProductResponseDTO.class))),
    @ApiResponse(responseCode = "400", ref = "BadRequest"),
    @ApiResponse(responseCode = "401", ref = "badcredentials"),
    @ApiResponse(responseCode = "403", ref = "forbidden"),
    @ApiResponse(responseCode = "422", ref = "unprocessableEntity"),
    @ApiResponse(responseCode = "404", description = "Product Not Found"),
    @ApiResponse(responseCode = "500", ref = "internalServerError")
    })
    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Object> create(@RequestParam(required = false) @Parameter(description = "Optional file upload") MultipartFile file, @Valid @RequestPart ProductRequestDTO product) throws IOException {
        ProductResponseDTO pr = productService.save(product, file);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(productRepository.findByCode(product.getCode()).getId()).toUri();

        return ResponseEntity.created(uri).body(pr);
    }

       @Operation(summary = "Get All Products", responses = {
        @ApiResponse(responseCode = "200", description = "Successfully Retrieved!", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProductResponseDTO.class))),
        @ApiResponse(responseCode = "401", ref = "badcredentials"),
        @ApiResponse(responseCode = "403", ref = "forbidden"),
        @ApiResponse(responseCode = "422", ref = "unprocessableEntity"),
        @ApiResponse(responseCode = "404", description = "Product Not Found"),
        @ApiResponse(responseCode = "500", ref = "internalServerError")
    })
    @GetMapping
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<List<ProductResponseDTO>> findAll() throws IOException {
        List<ProductResponseDTO> products = productService.findAll();
        return ResponseEntity.ok(products);
    }

    @Operation(summary = "Get Product By ID", responses = {
        @ApiResponse(responseCode = "200", description = "Successfully Retrieved!", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProductResponseDTO.class))),
        @ApiResponse(responseCode = "401", ref = "badcredentials"),
        @ApiResponse(responseCode = "403", ref = "forbidden"),
        @ApiResponse(responseCode = "422", ref = "unprocessableEntity"),
        @ApiResponse(responseCode = "404", description = "Product Not Found"),
        @ApiResponse(responseCode = "500", ref = "internalServerError")
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<ProductResponseDTO> getById(@PathVariable UUID id) throws IOException {
        ProductResponseDTO product = productService.getById(id);
        return ResponseEntity.ok(product);
    }

    @Operation(summary = "Search Products with Pagination", responses = {
        @ApiResponse(responseCode = "200", description = "Successfully Retrieved!", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProductResponseDTO.class))),
        @ApiResponse(responseCode = "401", ref = "badcredentials"),
        @ApiResponse(responseCode = "403", ref = "forbidden"),
        @ApiResponse(responseCode = "422", ref = "unprocessableEntity"),
        @ApiResponse(responseCode = "404", description = "Product Not Found"),
        @ApiResponse(responseCode = "500", ref = "internalServerError")
    })
    @GetMapping("/search")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<Page<ProductResponseDTO>> search(@RequestParam String query, @RequestParam int page, @RequestParam int size) throws IOException {
        Page<ProductResponseDTO> products = productService.search(query, page, size);
        return ResponseEntity.ok(products);
    }

    @Operation(summary = "Update Product", responses = {
        @ApiResponse(responseCode = "200", description = "Successfully Updated!", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProductResponseDTO.class))),
        @ApiResponse(responseCode = "401", ref = "badcredentials"),
        @ApiResponse(responseCode = "403", ref = "forbidden"),
        @ApiResponse(responseCode = "422", ref = "unprocessableEntity"),
        @ApiResponse(responseCode = "404", description = "Product Not Found"),
        @ApiResponse(responseCode = "500", ref = "internalServerError")
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ProductResponseDTO> update(@Valid @PathVariable UUID id, @Valid @RequestBody ProductRequestDTO product) throws IOException {
        ProductResponseDTO updatedProduct = productService.update(id, product);
        return ResponseEntity.ok(updatedProduct);
    }

    @Operation(summary = "Delete Product", responses = {
        @ApiResponse(responseCode = "200", description = "Successfully Delete!"),
        @ApiResponse(responseCode = "401", ref = "badcredentials"),
        @ApiResponse(responseCode = "403", ref = "forbidden"),
        @ApiResponse(responseCode = "422", ref = "unprocessableEntity"),
        @ApiResponse(responseCode = "404", description = "Product Not Found"),
        @ApiResponse(responseCode = "500", ref = "internalServerError")
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Object> delete(@Valid @PathVariable UUID id) throws IOException {
        productService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Update Image Product", responses = {
        @ApiResponse(responseCode = "200", description = "Successfully Updated!", content = @Content(mediaType = "application/json")),
        @ApiResponse(responseCode = "401", ref = "badcredentials"),
        @ApiResponse(responseCode = "403", ref = "forbidden"),
        @ApiResponse(responseCode = "422", ref = "unprocessableEntity"),
        @ApiResponse(responseCode = "404", description = "Product Not Found"),
        @ApiResponse(responseCode = "500", ref = "internalServerError")
    })
    @PutMapping("/image/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Object> updateImage(@Valid @PathVariable UUID id,@Valid @RequestPart MultipartFile file) throws IOException {
        String urlFile = productService.updateImage(id, file);
        return ResponseEntity.ok(urlFile);
    }

}
