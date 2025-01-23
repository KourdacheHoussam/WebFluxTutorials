package com.reactive.learning.crud.api;

import com.reactive.learning.crud.api.dto.CustomerDto;
import com.reactive.learning.crud.entity.Customer;
import com.reactive.learning.crud.service.CustomerService;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Map;

/**
 * @author : Houssam KOURDACHE
 */
@RestController
@RequestMapping("/customer")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @PostMapping("/create")
    public Mono<CustomerDto> createCustomer(@RequestBody Customer customer) {
        return customerService.createCustomer(customer);
    }

    @GetMapping(value = "/all", produces = "text/event-stream")
    public Flux<CustomerDto> getCustomers() {
        return customerService.getCustomers();
    }

    @GetMapping("/{id}/infos")
    public Mono<CustomerDto> getCustomerById(@PathVariable String id) {
        return customerService.getCustomerById(id);
    }

    @GetMapping("/{id}/expenses")
    public Mono<String> computeExpensesOfCustomer(@PathVariable String id) {
        return customerService.computeCustomerExpenses(id);
    }

    @GetMapping("/expenses")
    public Mono<Map<String, String>> customerExpenses(){
        return customerService.customersExpenses();
    }

    @GetMapping(value = "/simulate/event-stream", produces = "text/event-stream")
    public Flux<Long> simulateEventStream() {
        return Flux.interval(Duration.ofSeconds(1));
    }



}
