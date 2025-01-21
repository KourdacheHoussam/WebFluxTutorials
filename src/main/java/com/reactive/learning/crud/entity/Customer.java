package com.reactive.learning.crud.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.UUID;

/**
 * @author : Houssam KOURDACHE
 */
@Document(collection = "customers")
@NoArgsConstructor
@Data
public class Customer {
    @Id
    private String id;

    private String firstName;

    private String lastName;

    private String email;

    public Customer(String firstName, String lastName, String email) {
        this.id = UUID.randomUUID().toString();
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }


}
