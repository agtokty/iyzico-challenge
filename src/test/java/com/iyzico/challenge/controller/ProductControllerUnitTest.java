package com.iyzico.challenge.controller;

import com.iyzico.challenge.entity.Product;
import com.iyzico.challenge.service.product.ProductService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

/**
 * @author agtokty
 */
@WebMvcTest(controllers = ProductController.class)
public class ProductControllerUnitTest {

    @InjectMocks
    private ProductController productController;

    @Mock
    private ProductService productService;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void should_create_product() {
        Product product = new Product();
        product.setName("P1");
        when(productService.save(product)).thenReturn(product);

        ResponseEntity<Product> productResponseEntity = productController.create(product);

        Assert.assertNotNull(productResponseEntity);
        Assert.assertNotNull(productResponseEntity.getBody());
        Assert.assertEquals(HttpStatus.CREATED, productResponseEntity.getStatusCode());

        Assert.assertEquals(product.getName(), productResponseEntity.getBody().getName());
    }

    @Test
    public void should_get_product_by_id() {
        Product product = new Product();
        product.setId(1l);
        when(productService.findById(1l)).thenReturn(Optional.of(product));

        ResponseEntity<Product> productResponseEntity = productController.getProductById(1L);

        Assert.assertNotNull(productResponseEntity);
        Assert.assertNotNull(productResponseEntity.getBody());
        Assert.assertEquals(HttpStatus.OK, productResponseEntity.getStatusCode());

        Assert.assertEquals(1l, productResponseEntity.getBody().getId().longValue());
    }

    @Test
    public void should_update_product() {
        Product product = new Product();
        product.setId(1l);
        product.setName("P1");

        Product product2 = product;
        product.setName("P2");
        when(productService.findById(1l)).thenReturn(Optional.of(product));
        when(productService.save(product)).thenReturn(product2);

        ResponseEntity<Product> productResponseEntity = productController.update(product.getId(), product);

        Assert.assertNotNull(productResponseEntity);
        Assert.assertNotNull(productResponseEntity.getBody());
        Assert.assertEquals(HttpStatus.OK, productResponseEntity.getStatusCode());

        Assert.assertEquals(product2.getName(), productResponseEntity.getBody().getName());
    }

    @Test
    public void should_get_all_products() {
        Product product1 = new Product();
        product1.setId(1l);
        product1.setName("P1");

        Product product2 = new Product();
        product1.setId(2l);
        product1.setName("P2");

        List<Product> productList = new ArrayList<>();
        productList.add(product1);
        productList.add(product2);

        when(productService.listAll()).thenReturn(productList);

        List<Product> productListResponse = productController.getAllProducts();

        Assert.assertNotNull(productListResponse);
        Assert.assertEquals(2, productListResponse.size());
    }

    @Test
    public void should_delete_product_by_id() {
        Product product = new Product();
        product.setId(1l);
        when(productService.findById(1l)).thenReturn(Optional.of(product));
        doNothing().when(productService).remove(product);
//        doThrow(new Exception()).doNothing().when(productService).remove();

        ResponseEntity productResponseEntity = productController.delete(1L);

        Assert.assertNotNull(productResponseEntity);
        Assert.assertNull(productResponseEntity.getBody());
        Assert.assertEquals(HttpStatus.OK, productResponseEntity.getStatusCode());
    }
}
