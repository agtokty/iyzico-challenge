package com.iyzico.challenge.controller;

import com.iyzico.challenge.service.product.ProductPaymentService;
import com.iyzico.challenge.service.product.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

/**
 * @author agtokty
 */
public abstract class BaseController {

    @Autowired
    protected ProductService productService;

    @Autowired
    protected ProductPaymentService productPaymentService;

    <T> T checkNotNull(Optional<T> reference) throws ResponseStatusException {
        if (!reference.isPresent()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Requested item wasn't found!");
        }
        return reference.get();
    }
}
