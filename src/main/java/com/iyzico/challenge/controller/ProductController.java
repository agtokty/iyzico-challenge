package com.iyzico.challenge.controller;

import com.iyzico.challenge.entity.Product;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.List;

/**
 * @author agtokty
 */
@RestController
@RequestMapping("/api/v1")
public class ProductController extends BaseController {

    @GetMapping("/product")
    public List<Product> getAllProducts() {
        return productService.listAll();
    }

    @GetMapping("/product/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable(value = "id") Long productId)
            throws ResponseStatusException {

        Product product = productService.findById(productId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found " + productId));

        return ResponseEntity.ok().body(product);
    }

    @PostMapping("/product")
    public ResponseEntity<Product> create(@Valid @RequestBody Product product) {
        Product savedProduct = productService.save(product);

        return ResponseEntity.status(HttpStatus.CREATED).body(savedProduct);
    }

    @PutMapping("/product/{id}")
    public ResponseEntity<Product> update(@PathVariable(value = "id") Long productId, @Valid @RequestBody Product productModel)
            throws ResponseStatusException {
        Product product = productService
                .findById(productId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found " + productId));

        product.setName(productModel.getName());
        product.setDescription(productModel.getDescription());
        product.setPrice(productModel.getPrice());
        product.setStock(productModel.getStock());

        Product updatedProduct = productService.save(product);
        return ResponseEntity.ok(updatedProduct);
    }

    @DeleteMapping("/product/{id}")
    public ResponseEntity delete(@PathVariable(value = "id") Long productId) {
        Product product = productService
                .findById(productId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found " + productId));

        productService.remove(product);

        return ResponseEntity.ok().build();
    }

}
