package com.iyzico.challenge.service.product.lock;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author agtokty
 */
public class ProductLockProvider {

    /**
     * global read write lock per product
     */
    private static ConcurrentMap<Long, ReentrantLock> PRODUCT_LOCKS = new ConcurrentHashMap<Long, ReentrantLock>();

    /**
     * Returns the product specific ReentrantLock
     * If ReadWriteLock does not exist in collection, creates it and put a new instance to PRODUCT_LOCKS collection.
     *
     * @param productId product id
     * @return product specific ReentrantLock
     */
    public static ReentrantLock getProductLock(Long productId) {
        if (!PRODUCT_LOCKS.containsKey(productId))
            PRODUCT_LOCKS.put(productId, new ReentrantLock());

        return PRODUCT_LOCKS.get(productId);
    }
}
