package com.reactive.learning.crud.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.UUID;

/**
 * @author : Houssam KOURDACHE
 */
@Document(collection = "orders")
@NoArgsConstructor
@Data
public class Order {
    @Id
    private String id;
    private String customerId;
    private Double total;
    private Double discount;


}
