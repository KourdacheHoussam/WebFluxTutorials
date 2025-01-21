package com.reactive.learning.crud.api.dto;

import lombok.Data;

/**
 * @author : Houssam KOURDACHE
 */
@Data
public class OrderDto {
    private String id;
    private String customerId;
    private Double total;
    private Double discount;
}