package com.reactive.learning.crud.service;

import com.reactive.learning.crud.api.dto.CustomerDto;
import com.reactive.learning.crud.entity.Customer;
import com.reactive.learning.crud.entity.Order;
import com.reactive.learning.crud.mapping.CustomerMapper;
import com.reactive.learning.crud.repository.CustomerRepository;
import com.reactive.learning.crud.repository.OrderRepository;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;
import reactor.util.function.Tuple2;

import java.time.Duration;
import java.util.Map;

/**
 * @author : Houssam KOURDACHE
 */
@Service
public class CustomerService {

    private final ReactiveMongoTemplate mongoTemplate;
    private final CustomerMapper customerMapper;
    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;

    public CustomerService(ReactiveMongoTemplate mongoTemplate,
                           CustomerMapper customerMapper,
                           OrderRepository orderRepository,
                           CustomerRepository customerRepository) {
        this.mongoTemplate = mongoTemplate;
        this.customerMapper = customerMapper;
        this.orderRepository = orderRepository;
        this.customerRepository = customerRepository;
    }

    public Mono<CustomerDto> createCustomer(Customer customer) {
        return mongoTemplate.save(customer).map(this.customerMapper::toDto);
    }


    Sinks.Many<CustomerDto> customerEventBroadcast = Sinks.many().multicast().directBestEffort();
    /**
     *  Sink is useful when you need to broadcast events to multiple consumers and want to handle slow consumers
     *  by dropping events for them rather than buffering or blocking the entire stream
     *
     *  Multicast: The Sink can have multiple subscribers, allowing the same events to be sent to multiple consumers
     *  Direct Best Effort: This strategy handles backpressure by dropping events for slow subscribers
     *  without terminating them.
     *
     *  Example of usage :
     *  Suppose we receive from Kafka a list of new customers, and we need to forward that data to the Angular application
     *  to show alerts :
     *  1 - each time we receive a new data from kafka :
     *              Flux<CustomerDto>.doOnNext(customer -> customerEventBroadcast.tryEmitNext(customer))
     *  2 - and we create an endpoint for the frontend : /new/customers, who will produce 'text/event-stream'.
     *      In that method, we can return customerEventBroadcast as a Flux :
     *      return customerEventBroadcast.asFlux()...map()...
     */

    public Flux<CustomerDto> getCustomers() {
        return mongoTemplate.findAll(Customer.class).map(this.customerMapper::toDto)
            .delayElements(Duration.ofSeconds(1));
    }

    public Mono<CustomerDto> getCustomerById(String id) {
        return mongoTemplate.findById(id, Customer.class).map(this.customerMapper::toDto);
    }

    public Mono<String> computeCustomerExpenses(String id) {
        return orderRepository.findByCustomerId(id)
                .map(Order::getTotal)
                .reduce(0.0, Double::sum)
                .map(String::valueOf);
    }

    public Mono<Map<String, String>> customersExpenses() {
        // find all customer, and for each one compute his total amount of expenses
        return mongoTemplate.findAll(Customer.class)
                .flatMap(c ->
                        Mono.zip(Mono.just(c), computeCustomerExpenses(c.getId())))
                .collectMap(c -> c.getT1().getFirstName(), Tuple2::getT2);
    }




}
