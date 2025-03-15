package online.book.store.dto.order;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;
import lombok.Data;
import online.book.store.dto.orderitem.OrderItemResponseDto;
import online.book.store.model.Order;

@Data
public class OrderResponseDto {
    private Long id;
    private Long userId;
    private Set<OrderItemResponseDto> orderItemsDto;
    private LocalDateTime orderDate;
    private BigDecimal total;
    private Order.Status status;
}
