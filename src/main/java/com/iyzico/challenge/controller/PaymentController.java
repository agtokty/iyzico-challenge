package com.iyzico.challenge.controller;

import com.iyzico.challenge.controller.model.ProductPaymentRequest;
import com.iyzico.challenge.entity.Product;
import com.iyzico.challenge.exception.InsufficientStockException;
import com.iyzico.challenge.exception.PaymentException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

/**
 * @author agtokty
 */
@RestController
@RequestMapping("/api/v1")
public class PaymentController extends BaseController {

    @PostMapping("/payment")
    public ResponseEntity payment(@Valid @RequestBody ProductPaymentRequest productPaymentRequest) throws InterruptedException {

        Product product = checkNotNull(productService.findById(productPaymentRequest.getProductId()));

        try {
            productPaymentService.purchaseProduct(product, productPaymentRequest.getQuantity()).join();
        } catch (InsufficientStockException e) {
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.REQUESTED_RANGE_NOT_SATISFIABLE, "Not enough product not found ");
        } catch (PaymentException e) {
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Payment is not successful");
        }

        return ResponseEntity.ok().build();
    }
}
