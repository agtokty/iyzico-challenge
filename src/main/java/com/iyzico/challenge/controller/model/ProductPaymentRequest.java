package com.iyzico.challenge.controller.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * @author agtokty
 */
@Data
@AllArgsConstructor
public class ProductPaymentRequest {
    @Min(1)
    @NotNull
    private Long productId;
    @Min(1)
    private int quantity;

    public ProductPaymentRequest() {
    }
}
