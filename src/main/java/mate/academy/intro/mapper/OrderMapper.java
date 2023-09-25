package mate.academy.intro.mapper;

import mate.academy.intro.config.MapperConfig;
import mate.academy.intro.dto.order.CreateOrderRequestDto;
import mate.academy.intro.dto.order.OrderResponseDto;
import mate.academy.intro.dto.order.UpdateOrderRequestDto;
import mate.academy.intro.model.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapperConfig.class, uses = OrderItemMapper.class)
public interface OrderMapper {
    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "orderItems", target = "orderItems")
    OrderResponseDto toDto(Order order);

    Order toModel(UpdateOrderRequestDto requestDto);

    UpdateOrderRequestDto toUpdateDto(Order order);

    CreateOrderRequestDto toRequestDto(Order order);
}
