package mate.academy.intro.service.order.impl;

import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import mate.academy.intro.dto.order.CreateOrderRequestDto;
import mate.academy.intro.dto.order.OrderResponseDto;
import mate.academy.intro.dto.order.UpdateOrderRequestDto;
import mate.academy.intro.dto.orderitem.OrderItemResponseDto;
import mate.academy.intro.exception.EntityNotFoundException;
import mate.academy.intro.mapper.OrderItemMapper;
import mate.academy.intro.mapper.OrderMapper;
import mate.academy.intro.model.CartItem;
import mate.academy.intro.model.Order;
import mate.academy.intro.model.OrderItem;
import mate.academy.intro.model.ShoppingCart;
import mate.academy.intro.model.User;
import mate.academy.intro.repository.order.OrderRepository;
import mate.academy.intro.repository.orderitem.OrderItemRepository;
import mate.academy.intro.service.order.OrderService;
import mate.academy.intro.service.shoppingcart.ShoppingCartService;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;
    private final ShoppingCartService shoppingCartService;

    @Override
    @Transactional
    public CreateOrderRequestDto createOrder(User user) {
        Order newOrder = new Order();
        newOrder.setUser(user);
        newOrder.setOrderDate(LocalDateTime.now());
        newOrder.setStatus(Order.Status.CREATED);
        newOrder.setShippingAddress(user.getShippingAddress());

        ShoppingCart shoppingCart = shoppingCartService.getShoppingCartById(user.getId());
        Set<OrderItem> orderItems = shoppingCart.getCartItems().stream()
                .map(c -> convertToOrderItem(c, newOrder)).collect(Collectors.toSet());
        newOrder.setOrderItems(orderItems);

        BigDecimal total = orderItems.stream()
                .map(o -> o.getPrice().multiply(BigDecimal.valueOf(o.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        newOrder.setTotal(total);

        orderRepository.save(newOrder);
        shoppingCartService.clearShoppingCart(shoppingCart);

        return orderMapper.toRequestDto(newOrder);
    }

    @Override
    public UpdateOrderRequestDto updateOrderStatus(Long id, UpdateOrderRequestDto orderDto) {
        Order orderFromDb = orderRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Can't find order by id " + id));
        Order model = orderMapper.toModel(orderDto);
        orderFromDb.setStatus(model.getStatus());
        orderRepository.save(orderFromDb);
        return orderMapper.toUpdateDto(orderFromDb);
    }

    @Override
    public OrderItemResponseDto getOrderItemByOrderIdAndItemId(
            Long userId,
            Long orderId,
            Long itemId
    ) {
        OrderItem orderItem = orderItemRepository.findByIdAndUserIdAndOrderId(orderId,
                userId, itemId).orElseThrow(() ->
                new EntityNotFoundException("Can't find order by orderId " + orderId
                        + " and itemId " + itemId));
        return orderItemMapper.toDto(orderItem);
    }

    @Override
    public List<OrderResponseDto> getOrders(Long userId) {
        List<Order> ordersByUserId = orderRepository.findAllByUserId(userId);
        return ordersByUserId.stream()
                .map(orderMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<OrderItemResponseDto> getOrderItemsByOrderId(Long userId, Long orderId) {
        Order order = orderRepository.findByIdAndUserId(orderId, userId).orElseThrow(
                () -> new EntityNotFoundException("Can't find order by id " + orderId));
        return order.getOrderItems().stream()
                .map(orderItemMapper::toDto)
                .collect(Collectors.toList());
    }

    private OrderItem convertToOrderItem(CartItem cartItem, Order order) {
        OrderItem orderItem = new OrderItem();
        orderItem.setBook(cartItem.getBook());
        orderItem.setPrice(cartItem.getBook().getPrice());
        orderItem.setQuantity(cartItem.getQuantity());
        orderItem.setOrder(order);
        return orderItem;
    }
}
