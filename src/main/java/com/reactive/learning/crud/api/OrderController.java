package com.reactive.learning.crud.api;

import com.reactive.learning.crud.api.dto.OrderDto;
import com.reactive.learning.crud.service.OrderService;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

/**
 * @author : Houssam KOURDACHE
 */
@RestController
@RequestMapping("orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/create")
    public Mono<OrderDto> createOrder(@RequestBody OrderDto order ) {
        return this.orderService.createOrder(order);
    }

    @GetMapping("/{id}/infos")
    public Mono<OrderDto> getOrderById(@PathVariable("id") String id) {
        return this.orderService.getOrderById(id);
    }
}
