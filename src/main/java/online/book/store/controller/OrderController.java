package online.book.store.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import online.book.store.dto.order.OrderRequestDto;
import online.book.store.dto.order.OrderResponseDto;
import online.book.store.dto.order.UpdateOrderRequestDto;
import online.book.store.dto.orderitem.OrderItemResponseDto;
import online.book.store.model.User;
import online.book.store.service.OrderItemService;
import online.book.store.service.OrderService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Order management", description = "Endpoints for managing order and order item")
@RequiredArgsConstructor
@RestController
@RequestMapping("/orders")
public class OrderController {
    private final OrderService orderService;
    private final OrderItemService orderItemService;

    @Operation
    @PreAuthorize("hasRole('ROLE_USER')")
    @PostMapping()
    public OrderResponseDto createOrder(@RequestBody @Valid OrderRequestDto requestDto,
                                        Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return orderService.placeOrder(requestDto, user.getId());
    }

    @Operation
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping
    public Page<OrderResponseDto> getAllOrdersByUser(Authentication authentication,
                                                     Pageable pageable) {
        User user = (User) authentication.getPrincipal();
        return orderService.getAllOrdersByUserId(user.getId(), pageable);
    }

    @Operation
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PatchMapping("/{id}")
    public OrderResponseDto updateOrderStatus(@RequestBody UpdateOrderRequestDto requestDto,
                                              @PathVariable Long id) {
        return orderService.updateOrderStatusById(requestDto, id);
    }

    @Operation
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("/{orderId}/items")
    public Page<OrderItemResponseDto> getAllOrderItemsFromOrder(@PathVariable Long orderId,
                                                                Pageable pageable) {
        return orderService.getAllOrderItemsByOrderId(orderId, pageable);
    }

    @Operation
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("/{orderId}/items/{itemId}")
    // `GET: /api/orders/{orderId}/items/{itemId}` (retrieving specific OrderItem from an order).
    public OrderItemResponseDto getOrderItemFromOrder(@PathVariable Long orderId,
                                                      @PathVariable Long itemId) {
        return orderService.getOrderItemByOrderIdAndItemId(orderId, itemId);
    }
}
