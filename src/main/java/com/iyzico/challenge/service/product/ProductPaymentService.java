package com.iyzico.challenge.service.product;

import com.iyzico.challenge.entity.Product;
import com.iyzico.challenge.exception.InsufficientStockException;
import com.iyzico.challenge.exception.PaymentException;

import java.security.InvalidParameterException;
import java.util.concurrent.CompletableFuture;

/**
 * @author agtokty
 */
public interface ProductPaymentService {

    CompletableFuture<String> purchaseProduct(Product product, int quantity)
            throws InsufficientStockException, InvalidParameterException, PaymentException;
}
