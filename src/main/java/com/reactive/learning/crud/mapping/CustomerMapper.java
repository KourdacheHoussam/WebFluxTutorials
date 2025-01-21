package com.reactive.learning.crud.mapping;

import com.reactive.learning.crud.api.dto.CustomerDto;
import com.reactive.learning.crud.entity.Customer;
import org.mapstruct.Mapper;

/**
 * @author : Houssam KOURDACHE
 */
@Mapper(componentModel = "spring")
public interface CustomerMapper {

    CustomerDto toDto(Customer customer);

    Customer toEntity(CustomerDto customerDto);
}