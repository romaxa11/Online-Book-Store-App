package mate.academy.intro.service.order;

import java.util.List;
import mate.academy.intro.dto.order.CreateOrderRequestDto;
import mate.academy.intro.dto.order.OrderResponseDto;
import mate.academy.intro.dto.order.UpdateOrderRequestDto;
import mate.academy.intro.dto.orderitem.OrderItemResponseDto;
import mate.academy.intro.model.User;

public interface OrderService {
    CreateOrderRequestDto createOrder(User user);

    UpdateOrderRequestDto updateOrderStatus(Long id, UpdateOrderRequestDto orderDto);

    OrderItemResponseDto getOrderItemByOrderIdAndItemId(Long userId, Long orderId, Long itemId);

    List<OrderResponseDto> getOrders(Long userId);

    List<OrderItemResponseDto> getOrderItemsByOrderId(Long userId, Long orderId);
}

