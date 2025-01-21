package com.reactive.learning.crud.api.dto;

/**
 * @author : Houssam KOURDACHE
 */
import lombok.Data;

@Data
public class CustomerDto {
    private String id;

    private String firstName;

    private String lastName;

    private String email;
}