package com.reactive.learning.crud.repository;

import com.reactive.learning.crud.entity.Customer;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

/**
 * @author : Houssam KOURDACHE
 */
public interface CustomerRepository extends ReactiveMongoRepository<Customer, String> {


}
