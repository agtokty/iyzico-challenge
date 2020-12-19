package com.iyzico.challenge;

import com.iyzico.challenge.entity.Product;

import java.math.BigDecimal;

/**
 * @author agtokty
 */
public abstract class BaseServiceTest {

    protected final String BASE_API_PREFIX = "/api/v1/";
    protected final String PRODUCT_API_PREFIX = BASE_API_PREFIX + "product";
    protected final String PAYMENT_API_PREFIX = BASE_API_PREFIX + "payment";

    protected Product generateProduct() {
        return generateProduct(100);
    }

    protected Product generateProduct(int stock) {
        Product product = new Product();
        product.setName("Product-A");
        product.setDescription("product a");
        product.setStock(stock);
        product.setPrice(new BigDecimal("219.99"));

        return product;
    }
}
