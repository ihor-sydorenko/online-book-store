package online.book.store.service;

import java.util.List;
import online.book.store.dto.order.OrderRequestDto;
import online.book.store.dto.order.OrderResponseDto;
import online.book.store.dto.order.UpdateOrderRequestDto;
import online.book.store.dto.orderitem.OrderItemResponseDto;
import online.book.store.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderService {
    OrderResponseDto placeOrder(OrderRequestDto requestDto, Long userId);

    Page<OrderResponseDto> getAllOrdersByUser(User user, Pageable pageable);

    OrderResponseDto updateOrderStatusById(UpdateOrderRequestDto requestDto, Long orderId);

    List<OrderItemResponseDto> getAllOrderItemsByOrderId(Long orderId, Long userId);

    OrderItemResponseDto getOrderItemByIdAndOrderIdAndUserId(Long orderId,
                                                             Long itemId,
                                                             Long userId);
}
