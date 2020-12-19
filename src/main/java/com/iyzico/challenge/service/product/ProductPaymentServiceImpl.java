package com.iyzico.challenge.service.product;

import com.iyzico.challenge.entity.Product;
import com.iyzico.challenge.exception.InsufficientStockException;
import com.iyzico.challenge.exception.PaymentException;
import com.iyzico.challenge.repository.product.ProductRepository;
import com.iyzico.challenge.service.PaymentServiceClients;
import com.iyzico.challenge.service.product.lock.ProductLockProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.security.InvalidParameterException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author agtokty
 */
@Service
@Slf4j
public class ProductPaymentServiceImpl implements ProductPaymentService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private PaymentServiceClients paymentServiceClients;

    @Override
    public CompletableFuture<String> purchaseProduct(Product product, int quantity)
            throws InsufficientStockException, InvalidParameterException, PaymentException {

        if (quantity < 1) {
            throw new InvalidParameterException("Purchase quantity is not valid. should be greater than 0.");
        }

        // reduce stock
        updateStockWithLock(product.getId(), quantity * -1);

        BigDecimal price = product.getPrice().multiply(new BigDecimal(quantity));
        try {
            return paymentServiceClients.call(price);
        } catch (Exception e) {
            //add back to stock
            updateStockWithLock(product.getId(), quantity);
            log.error("Payment is failed [{}] - [{}] error : {}", product, price, e);
            throw new PaymentException("Payment error", e);
        }
    }

    /**
     * Update product stock by a quantity in a thread safe way
     * Finds the actual stock amount of the product currently then reduce by quantity and update in the database.
     *
     * @param productId
     * @param quantity  this increase use positive number, to decrease use negative number
     * @throws InsufficientStockException if you try to decrease stock by a number that is bigger than current stock.
     */
    private void updateStockWithLock(Long productId, int quantity) throws InsufficientStockException {
        ReentrantLock lock = ProductLockProvider.getProductLock(productId);

        lock.lock();
        try {
            Product product = productRepository.findById(productId).get();
            if (quantity < 0 && product.getStock() < Math.abs(quantity))
                throw new InsufficientStockException();

            int oldStock = product.getStock();
            int newStock = oldStock + quantity;
            product.setStock(newStock);

            productRepository.save(product);
            log.info("Product stock updated from {} to {}", oldStock, newStock);
        } finally {
            lock.unlock();
        }
    }

}
