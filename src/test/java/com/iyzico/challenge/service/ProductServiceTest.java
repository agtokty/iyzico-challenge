package com.iyzico.challenge.service;

import com.iyzico.challenge.BaseServiceTest;
import com.iyzico.challenge.entity.Product;
import com.iyzico.challenge.service.product.ProductService;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * @author agtokty
 */
//@EnableAutoConfiguration
@RunWith(SpringRunner.class)
@SpringBootTest
@EnableAsync
public class ProductServiceTest extends BaseServiceTest {

    @Autowired
    private ProductService productService;

    @After
    public void cleanUp() {
        List<Product> productList = productService.listAll();
        productList.forEach((product -> {
            productService.remove(product);
        }));
    }

    @Test
    public void should_save_product() {
        Product product = generateProduct();

        Product savedProduct = productService.save(product);

        Assert.assertNotNull(savedProduct);
        Assert.assertTrue(savedProduct.getId() != 0);
        Assert.assertEquals(product.getName(), savedProduct.getName());
        Assert.assertEquals(product.getDescription(), savedProduct.getDescription());
        Assert.assertEquals(product.getStock(), savedProduct.getStock());
        Assert.assertEquals(product.getPrice(), savedProduct.getPrice());
    }

    @Test
    public void should_update_product() {
        Product product = generateProduct();

        Product savedProduct = productService.save(product);

        savedProduct.setName(savedProduct.getName() + " Updated name");
        savedProduct.setDescription(savedProduct.getDescription() + " Updated description");
        savedProduct.setPrice(savedProduct.getPrice().add(new BigDecimal(888.99)));
        savedProduct.setStock(savedProduct.getStock() + 123);

        Product updatedProduct = productService.save(savedProduct);

        Assert.assertNotNull(updatedProduct);

        Assert.assertEquals(savedProduct.getId(), updatedProduct.getId());
        Assert.assertEquals(savedProduct.getName(), updatedProduct.getName());
        Assert.assertEquals(savedProduct.getDescription(), updatedProduct.getDescription());
        Assert.assertEquals(savedProduct.getStock(), updatedProduct.getStock());
        Assert.assertEquals(savedProduct.getPrice(), updatedProduct.getPrice());
    }


    @Test
    public void should_find_product_by_id_if_exist() {
        Product product = generateProduct();

        Product savedProduct = productService.save(product);
        Assert.assertNotNull(savedProduct);

        Product foundProduct = productService.findById(product.getId()).get();
        Assert.assertNotNull(foundProduct);
        Assert.assertEquals(savedProduct.getId(), foundProduct.getId());

        Assert.assertEquals(product.getName(), foundProduct.getName());
        Assert.assertEquals(product.getDescription(), foundProduct.getDescription());
        Assert.assertEquals(product.getStock(), foundProduct.getStock());
        Assert.assertEquals(product.getPrice(), foundProduct.getPrice());
    }

    @Test
    public void should_not_find_product_by_id_if_not_exist() {
        Product product = generateProduct();

        Product savedProduct = productService.save(product);
        Assert.assertNotNull(savedProduct);

        Optional<Product> foundProduct = productService.findById(new Long(3));
        Assert.assertFalse(foundProduct.isPresent());
    }

    @Test
    public void should_delete_product_ifExist() {
        Product product = generateProduct();
        Product savedProduct = productService.save(product);

        productService.remove(savedProduct);

        Optional<Product> foundProduct = productService.findById(product.getId());
        Assert.assertFalse(foundProduct.isPresent());
    }

    @Test
    public void should_list_products_with_correct_size() {
        int firstInsertCount = 105;
        for (int i = 0; i < firstInsertCount; i++) {
            Product product = generateProduct();
            productService.save(product);
        }

        List<Product> productList = productService.listAll();
        Assert.assertNotNull(productList);
        Assert.assertEquals(firstInsertCount, productList.size());

        int secondInsertCount = 55;
        for (int i = 0; i < secondInsertCount; i++) {
            Product product = generateProduct();
            productService.save(product);
        }

        productList = productService.listAll();
        Assert.assertEquals(firstInsertCount + secondInsertCount, productList.size());
    }

}
