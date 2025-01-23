package com.reactive.learning.crud.api.dto;

/**
 * @author : Houssam KOURDACHE
 */
public record OrderDto(String id,
                       String customerId,
                       Double total,
                       Double discount) {
}