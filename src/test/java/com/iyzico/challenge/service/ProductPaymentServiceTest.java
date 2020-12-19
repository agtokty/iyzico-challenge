package com.iyzico.challenge.service;

import com.iyzico.challenge.entity.Product;
import com.iyzico.challenge.exception.InsufficientStockException;
import com.iyzico.challenge.service.product.ProductPaymentService;
import com.iyzico.challenge.service.product.ProductService;
import lombok.SneakyThrows;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @author agtokty
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@EnableAsync
public class ProductPaymentServiceTest {

    @Autowired
    private ProductPaymentService productPaymentService;

    @Autowired
    private ProductService productService;

    private Product savedProduct1;

    @Before
    public void createProducts() {
        Product product1 = new Product();
        product1.setName("P1");
        product1.setDescription("P1");
        product1.setPrice(new BigDecimal("11.11"));
        product1.setStock(100);

        savedProduct1 = productService.save(product1);
    }

    @After
    public void deleteProducts() {
        productService.remove(savedProduct1);
    }

    @Test
    public void should_purchase_product_in_stock() throws Exception {
        productPaymentService.purchaseProduct(savedProduct1, 4).join();

        Product product = productService.findById(savedProduct1.getId()).get();
        Assert.assertEquals(96, product.getStock());
    }


    @Test(expected = InsufficientStockException.class)
    public void should_not_purchase_product_not_in_stock() throws Exception {
        productPaymentService.purchaseProduct(savedProduct1, 101).join();
    }

    @Test(expected = InvalidParameterException.class)
    public void should_not_purchase_with_invalid_count() throws Exception {
        productPaymentService.purchaseProduct(savedProduct1, 0).get();
    }

    @Test
    public void should_purchase_product_in_stock_sequential() throws Exception {
        List<CompletableFuture> futures = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            productPaymentService.purchaseProduct(savedProduct1, 2).join();
        }

        Product product = productService.findById(savedProduct1.getId()).get();
        int remainStock = 100 - 2 * 2;
        Assert.assertEquals(remainStock, product.getStock());
    }

    @Test
    public void should_purchase_product_in_stock_with_100_clients_together_2() throws Exception {
        ExecutorService executor = Executors.newFixedThreadPool(200);

        for (int i = 0; i < 100; i++) {
            executor.submit(new Runnable() {
                @SneakyThrows
                @Override
                public void run() {
                    productPaymentService.purchaseProduct(savedProduct1, 1).join();
                }
            });
        }
        executor.shutdown();
        executor.awaitTermination(15, TimeUnit.SECONDS);

        Product product = productService.findById(savedProduct1.getId()).get();
        Assert.assertEquals(0, product.getStock());
    }

    @Test
    public void should_purchase_product_in_stock_with_100_clients_together() throws Exception {
        List<CompletableFuture> futures = new ArrayList<>();
        for (int i = 0; i < 99; i++) {
            CompletableFuture<String> future = productPaymentService.purchaseProduct(savedProduct1, 1);
            futures.add(future);
        }
        futures.stream().forEach(f -> CompletableFuture.allOf(f).join());

        Product product = productService.findById(savedProduct1.getId()).get();
        Assert.assertEquals(1, product.getStock());
    }

    @Test(expected = InsufficientStockException.class)
    public void should_not_purchase_when_insufficient_stock_clients_together() throws Exception {
        List<CompletableFuture> futures = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            CompletableFuture<String> future = productPaymentService.purchaseProduct(savedProduct1, 40);
            futures.add(future);
        }
        futures.stream().forEach(f -> CompletableFuture.allOf(f).join());
    }
}
