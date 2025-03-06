package online.book.store.service;

import online.book.store.dto.order.OrderRequestDto;
import online.book.store.dto.order.OrderResponseDto;
import online.book.store.dto.order.UpdateOrderRequestDto;
import online.book.store.dto.orderitem.OrderItemResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderService {
    OrderResponseDto placeOrder(OrderRequestDto requestDto, Long userId);

    Page<OrderResponseDto> getAllOrdersByUserId(Long userId, Pageable pageable);

    OrderResponseDto updateOrderStatusById(UpdateOrderRequestDto requestDto, Long orderId);

    Page<OrderItemResponseDto> getAllOrderItemsByOrderId(Long orderId, Pageable pageable);

    OrderItemResponseDto getOrderItemByOrderIdAndItemId(Long orderId, Long itemId);
}
