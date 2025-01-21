package com.reactive.learning.crud.service;

import com.reactive.learning.crud.api.dto.OrderDto;
import com.reactive.learning.crud.entity.Order;
import com.reactive.learning.crud.mapping.OrderMapper;
import com.reactive.learning.crud.repository.OrderRepository;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Objects;

/**
 * @author : Houssam KOURDACHE
 */
@Service
public class OrderService {
    private final ReactiveMongoTemplate mongoTemplate;
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;

    public OrderService(ReactiveMongoTemplate mongoTemplate,
                        OrderMapper orderMapper,
                        OrderRepository orderRepository) {
        this.mongoTemplate = mongoTemplate;
        this.orderMapper = orderMapper;
        this.orderRepository = orderRepository;
    }

    public Mono<OrderDto> createOrder(final OrderDto order) {
        assert orderMapper != null;
        Order oEntity = orderMapper.toEntity(order);
        return mongoTemplate.save(Mono.just(oEntity)).map(orderMapper::toDto);
    }

    public Mono<OrderDto> getOrderById(final String id) {
        assert !Objects.equals(id, "");
        return orderRepository.findById(id).map(orderMapper::toDto);
    }
}
