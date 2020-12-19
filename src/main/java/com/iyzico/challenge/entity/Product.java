package com.iyzico.challenge.entity;

import lombok.Data;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.math.BigDecimal;

/**
 * @author agtokty
 */
@Data
@Entity
@Table(name="product")
@EntityListeners(AuditingEntityListener.class)
public class Product {

    @Id
    @GeneratedValue
    private Long id;
    private BigDecimal price;
    private String name;
    private String description;
    private int stock;
}
