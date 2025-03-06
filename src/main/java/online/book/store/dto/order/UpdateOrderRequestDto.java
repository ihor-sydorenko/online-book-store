package online.book.store.dto.order;

import online.book.store.model.Order;

public record UpdateOrderRequestDto(Order.Status status) {
}
