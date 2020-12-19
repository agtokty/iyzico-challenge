package com.iyzico.challenge.service.product;

import com.iyzico.challenge.entity.Product;

import java.util.List;
import java.util.Optional;

/**
 * @author agtokty
 */
public interface ProductService {

    Optional<Product> findById(Long productId);

    Product save(Product product);

    void remove(Product product);

    List<Product> listAll();
}
