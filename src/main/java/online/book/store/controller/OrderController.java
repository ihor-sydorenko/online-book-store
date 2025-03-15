package online.book.store.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import online.book.store.dto.order.OrderRequestDto;
import online.book.store.dto.order.OrderResponseDto;
import online.book.store.dto.order.UpdateOrderRequestDto;
import online.book.store.dto.orderitem.OrderItemResponseDto;
import online.book.store.model.User;
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

    @Operation(summary = "Placing an order",
            description = "Creating new order based on shopping cart")
    @PreAuthorize("hasRole('ROLE_USER')")
    @PostMapping
    public OrderResponseDto createOrder(@RequestBody @Valid OrderRequestDto requestDto,
                                        Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return orderService.placeOrder(requestDto, user.getId());
    }

    @Operation(summary = "Retrieving the user's order history",
            description = "Find all orders by user")
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping
    public Page<OrderResponseDto> getAllOrdersByUser(Authentication authentication,
                                                     Pageable pageable) {
        User user = (User) authentication.getPrincipal();
        return orderService.getAllOrdersByUser(user, pageable);
    }

    @Operation(summary = "Updating order status", description = "Updating order status")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PatchMapping("/{id}")
    public OrderResponseDto updateOrderStatus(@RequestBody @Valid UpdateOrderRequestDto requestDto,
                                              @PathVariable Long id) {
        return orderService.updateOrderStatusById(requestDto, id);
    }

    @Operation(summary = "Get all order items from order",
            description = "Retrieving all order items for a specific order")
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("/{orderId}/items")
    public List<OrderItemResponseDto> getAllOrderItemsFromOrder(@PathVariable Long orderId,
                                                                Pageable pageable,
                                                                Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return orderService.getAllOrderItemsByOrderId(orderId, user.getId());
    }

    @Operation(summary = "Get order item by id",
            description = "Retrieving specific order item from an order")
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("/{orderId}/items/{itemId}")
    public OrderItemResponseDto getOrderItemFromOrder(@PathVariable Long orderId,
                                                      @PathVariable Long itemId,
                                                      Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return orderService.getOrderItemByIdAndOrderIdAndUserId(orderId, itemId, user.getId());
    }
}
