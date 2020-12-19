package com.iyzico.challenge.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.iyzico.challenge.BaseServiceTest;
import com.iyzico.challenge.controller.model.ProductPaymentRequest;
import com.iyzico.challenge.entity.Product;
import lombok.SneakyThrows;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author agtokty
 */
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@EnableAutoConfiguration()
@SpringBootTest
public class PaymentControllerIntegrationTest extends BaseServiceTest {

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

    @Before
    public void beforeUp() throws Exception {
        Product product = generateProduct(100);
        String resultJson = mvc.perform(post(PRODUCT_API_PREFIX)
                .content(objectMapper.writeValueAsBytes(product))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();

        savedProduct = objectMapper.readValue(resultJson, Product.class);
    }

    @Test
    public void should_pay_product()
            throws Exception {

        ProductPaymentRequest request = new ProductPaymentRequest();
        request.setProductId(savedProduct.getId());
        request.setQuantity(2);

        mvc.perform(post(PAYMENT_API_PREFIX)
                .content(objectMapper.writeValueAsBytes(request))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void should_not_pay_product_with_insufficient_stock()
            throws Exception {

        ProductPaymentRequest request = new ProductPaymentRequest();
        request.setProductId(savedProduct.getId());
        request.setQuantity(105);

        mvc.perform(post(PAYMENT_API_PREFIX)
                .content(objectMapper.writeValueAsBytes(request))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isRequestedRangeNotSatisfiable());
    }

    @Test
    public void should_not_pay_for_non_existing_product()
            throws Exception {

        ProductPaymentRequest request = new ProductPaymentRequest();
        request.setProductId(6l);
        request.setQuantity(15);

        mvc.perform(post(PAYMENT_API_PREFIX)
                .content(objectMapper.writeValueAsBytes(request))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}
