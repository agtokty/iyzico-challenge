package com.iyzico.challenge.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.iyzico.challenge.BaseServiceTest;
import com.iyzico.challenge.entity.Product;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author agtokty
 */
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@EnableAutoConfiguration()
@SpringBootTest
public class ProductControllerIntegrationTest extends BaseServiceTest {

    private ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private MockMvc mvc;


    private Product savedProduct;

    @After
    public void cleanUp() {
        if (savedProduct != null) {
            try {
                mvc.perform(delete(PRODUCT_API_PREFIX + "/" + savedProduct.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Test
    public void should_create_product()
            throws Exception {

        Product product = generateProduct();

        String resultJson = mvc.perform(post(PRODUCT_API_PREFIX)
                .content(objectMapper.writeValueAsBytes(product))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value(product.getName()))
                .andExpect(jsonPath("$.description").value(product.getDescription()))
                .andExpect(jsonPath("$.price").value(product.getPrice()))
                .andExpect(jsonPath("$.stock").value(product.getStock()))
                .andReturn().getResponse().getContentAsString();

        savedProduct = objectMapper.readValue(resultJson, Product.class);
    }

    @Test
    public void should_update_product()
            throws Exception {

        Product product = generateProduct();

        String resultJson = mvc.perform(post(PRODUCT_API_PREFIX)
                .content(objectMapper.writeValueAsBytes(product))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();

        savedProduct = objectMapper.readValue(resultJson, Product.class);
        Assert.assertNotNull(savedProduct);

        savedProduct.setName(savedProduct.getName() + " Updated name");
        savedProduct.setDescription(savedProduct.getDescription() + " Updated description");
        savedProduct.setPrice(savedProduct.getPrice().add(new BigDecimal(888.99)));
        savedProduct.setStock(savedProduct.getStock() + 123);

        mvc.perform(put(PRODUCT_API_PREFIX + "/" + savedProduct.getId())
                .content(objectMapper.writeValueAsBytes(savedProduct))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.id").value(savedProduct.getId()))
                .andExpect(jsonPath("$.name").value(savedProduct.getName()))
                .andExpect(jsonPath("$.description").value(savedProduct.getDescription()))
                .andExpect(jsonPath("$.price").value(savedProduct.getPrice()))
                .andExpect(jsonPath("$.stock").value(savedProduct.getStock()));
    }

    @Test
    public void should_get_product_by_id()
            throws Exception {

        Product product = generateProduct();

        String resultJson = mvc.perform(post(PRODUCT_API_PREFIX)
                .content(objectMapper.writeValueAsBytes(product))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();

        savedProduct = objectMapper.readValue(resultJson, Product.class);
        Assert.assertNotNull(savedProduct);

        mvc.perform(get(PRODUCT_API_PREFIX + "/" + savedProduct.getId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.id").value(savedProduct.getId()))
                .andExpect(jsonPath("$.name").value(savedProduct.getName()))
                .andExpect(jsonPath("$.description").value(savedProduct.getDescription()))
                .andExpect(jsonPath("$.price").value(savedProduct.getPrice()))
                .andExpect(jsonPath("$.stock").value(savedProduct.getStock()));
    }

    @Test
    public void should_delete_product_by_id()
            throws Exception {

        Product product = generateProduct();

        String resultJson = mvc.perform(post(PRODUCT_API_PREFIX)
                .content(objectMapper.writeValueAsBytes(product))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();

        Product savedProduct = objectMapper.readValue(resultJson, Product.class);
        Assert.assertNotNull(savedProduct);

        mvc.perform(delete(PRODUCT_API_PREFIX + "/" + savedProduct.getId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void should_get_product_list()
            throws Exception {
        List<Product> savedProducts = new ArrayList<>();

        for (int i = 0; i < 101; i++) {
            Product product1 = generateProduct();
            String resultJson = mvc.perform(post(PRODUCT_API_PREFIX)
                    .content(objectMapper.writeValueAsBytes(product1))
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isCreated())
                    .andReturn().getResponse().getContentAsString();

            savedProducts.add(objectMapper.readValue(resultJson, Product.class));
        }


        String resultJson = mvc.perform(get(PRODUCT_API_PREFIX)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        TypeReference typeReference = new TypeReference<List<Product>>() {
        };
        List<Product> productList = objectMapper.readValue(resultJson, typeReference);

        Assert.assertNotNull(productList);
        Assert.assertEquals(101, productList.size());

        for (Product product : productList) {
            mvc.perform(delete(PRODUCT_API_PREFIX + "/" + product.getId())
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());
        }
    }

    @Test
    public void should_not_get_not_existing_product()
            throws Exception {
        mvc.perform(get(PRODUCT_API_PREFIX + "/123")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void should_not_update_not_existing_product()
            throws Exception {
        Product product = generateProduct();

        mvc.perform(put(PRODUCT_API_PREFIX + "/123")
                .content(objectMapper.writeValueAsBytes(product))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void should_not_delete_not_existing_product()
            throws Exception {
        mvc.perform(delete(PRODUCT_API_PREFIX + "/123")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

}
