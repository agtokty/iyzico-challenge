package com.iyzico.challenge.controller;

import com.iyzico.challenge.controller.model.ProductPaymentRequest;
import com.iyzico.challenge.entity.Product;
import com.iyzico.challenge.exception.InsufficientStockException;
import com.iyzico.challenge.service.product.ProductPaymentService;
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
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

/**
 * @author agtokty
 */
@WebMvcTest(controllers = PaymentController.class)
public class PaymentControllerUnitTest {

    @InjectMocks
    private PaymentController paymentController;

    @Mock
    private ProductService productService;

    @Mock
    private ProductPaymentService productPaymentService;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    @Test(expected = ResponseStatusException.class)
    public void should_not_pay_for_non_exist_product() throws Exception {
        when(productService.findById(1l)).thenReturn(Optional.empty());

        ResponseEntity productResponseEntity = paymentController.payment(new ProductPaymentRequest(1l, 5));

        Assert.assertNotNull(productResponseEntity);
        Assert.assertNull(productResponseEntity.getBody());
        Assert.assertEquals(HttpStatus.NOT_FOUND, productResponseEntity.getStatusCode());
    }

    @Test
    public void should_pay_product_that_has_stock() throws Exception {
        Product product = new Product();
        product.setId(1l);
        product.setName("P1");
        product.setStock(10);
        when(productService.findById(1l)).thenReturn(Optional.of(product));

        when(productPaymentService.purchaseProduct(product, 6)).thenReturn(CompletableFuture.completedFuture("completed"));

        paymentController.payment(new ProductPaymentRequest(1l, 6));
    }

    @Test(expected = ResponseStatusException.class)
    public void should_not_pay_product_that_has_not_stock() throws Exception {
        Product product = new Product();
        product.setId(1l);
        product.setName("P1");
        product.setStock(10);
        when(productService.findById(1l)).thenReturn(Optional.of(product));

        doThrow(new InsufficientStockException())
                .when(productPaymentService).purchaseProduct(product, 15);

        paymentController.payment(new ProductPaymentRequest(1l, 15));
    }
}
