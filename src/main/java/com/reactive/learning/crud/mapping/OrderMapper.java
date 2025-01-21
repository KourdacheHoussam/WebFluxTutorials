package com.reactive.learning.crud.mapping;

import com.reactive.learning.crud.api.dto.OrderDto;
import com.reactive.learning.crud.entity.Order;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * @author : Houssam KOURDACHE
 */
@Mapper(componentModel = "spring")
public interface OrderMapper {
    OrderMapper INSTANCE = Mappers.getMapper(OrderMapper.class);

    OrderDto toDto(Order order);

    Order toEntity(OrderDto orderDto);
}