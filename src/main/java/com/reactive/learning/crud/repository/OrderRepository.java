package com.reactive.learning.crud.repository;

import com.reactive.learning.crud.entity.Customer;
import com.reactive.learning.crud.entity.Order;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

/**
 * @author : Houssam KOURDACHE
 */
public interface OrderRepository extends ReactiveMongoRepository<Order, String> {

    Flux<Order> findByCustomerId(String customerId);
}
