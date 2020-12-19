package com.iyzico.challenge.repository.product;

import com.iyzico.challenge.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author agtokty
 */
public interface ProductRepository extends JpaRepository<Product, Long> {
}
