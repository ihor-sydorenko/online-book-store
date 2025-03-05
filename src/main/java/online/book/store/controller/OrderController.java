package online.book.store.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OrderController {
    @PreAuthorize("hasRole('ROLE_USER')")
    // `POST: /api/orders` (placing an order)

    @PreAuthorize("hasRole('ROLE_USER')")
    // `GET: /api/orders` (retrieving the user's order history)

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    // `PATCH: /api/orders/{id}` (updating order status).

    @PreAuthorize("hasRole('ROLE_USER')")
    // `GET: /api/orders/{orderId}/items` (retrieving all `OrderItems` for a specific order).

    @PreAuthorize("hasRole('ROLE_USER')")
    // `GET: /api/orders/{orderId}/items/{itemId}` (retrieving specific OrderItem from an order).

}
