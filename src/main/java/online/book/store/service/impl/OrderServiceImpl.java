package online.book.store.service.impl;

import java.math.BigDecimal;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import online.book.store.dto.order.OrderRequestDto;
import online.book.store.dto.order.OrderResponseDto;
import online.book.store.dto.order.UpdateOrderRequestDto;
import online.book.store.dto.orderitem.OrderItemResponseDto;
import online.book.store.exception.EntityNotFoundException;
import online.book.store.mapper.OrderItemMapper;
import online.book.store.mapper.OrderMapper;
import online.book.store.model.Order;
import online.book.store.model.OrderItem;
import online.book.store.model.ShoppingCart;
import online.book.store.repository.order.OrderItemRepository;
import online.book.store.repository.order.OrderRepository;
import online.book.store.repository.shoppingcart.ShoppingCartRepository;
import online.book.store.service.OrderService;
import online.book.store.service.ShoppingCartService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@RequiredArgsConstructor
@Service
public class OrderServiceImpl implements OrderService {
    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;
    private final ShoppingCartService shoppingCartService;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ShoppingCartRepository shoppingCartRepository;

    @Override
    public OrderResponseDto placeOrder(OrderRequestDto requestDto, Long userId) {
        ShoppingCart shoppingCart = shoppingCartRepository.
                findShoppingCartByUserId(userId).orElseThrow(
                        () -> new EntityNotFoundException(""));
        Order order = createOrder(shoppingCart, requestDto);
        shoppingCartService.clearShoppingCart();
        return orderMapper.toDto(orderRepository.save(order));
    }

    @Override
    public Page<OrderResponseDto> getAllOrdersByUserId(Long userId, Pageable pageable) {
        return orderRepository.findAllByUserId(pageable, userId).map(orderMapper::toDto);
    }

    @Override
    public OrderResponseDto updateOrderStatusById(UpdateOrderRequestDto requestDto, Long orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(
                () -> new EntityNotFoundException("Can't find order"));
        order.setStatus(Order.Status.valueOf(requestDto.status().name()));
        return orderMapper.toDto(orderRepository.save(order));
    }

    @Override
    public Page<OrderItemResponseDto> getAllOrderItemsByOrderId(Long orderId, Pageable pageable) {
        Order order = orderRepository.findOrderById(orderId, pageable).orElseThrow(
                () -> new EntityNotFoundException(""));
        return orderItemRepository.getAllByOrder(order, pageable);
    }

    @Override
    public OrderItemResponseDto getOrderItemByOrderIdAndItemId(Long orderId, Long itemId) {
        Order order = orderRepository.findOrderById(orderId).orElseThrow(
                () -> new EntityNotFoundException(""));
        return order.getOrderItems().stream()
                .filter(orderItem -> orderItem.getId().equals(itemId))
                .findFirst()
                .map(orderItemMapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException(""));
    }

    private Order createOrder(ShoppingCart shoppingCart, OrderRequestDto requestDto) {
        Order order = orderMapper.toModelFromShoppingCart(shoppingCart);
        order.setTotal(getTotalOrderItemsPrice(order));
        order.setShippingAddress(requestDto.getShippingAddress());
        setOrderToOrderItems(order);
        return order;
    }

    private BigDecimal getTotalOrderItemsPrice(Order order) {
        return order.getOrderItems().stream()
                .map(OrderItem::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private void setOrderToOrderItems(Order order) {
        Set<OrderItem> orderItems = order.getOrderItems().stream()
                .peek(orderItem -> orderItem.setOrder(order))
                .collect(Collectors.toSet());
        order.setOrderItems(orderItems);
    }
}
