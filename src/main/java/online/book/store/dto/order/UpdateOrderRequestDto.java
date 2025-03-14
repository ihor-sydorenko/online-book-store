package online.book.store.dto.order;

import lombok.Data;
import online.book.store.model.Order;

@Data
public class UpdateOrderRequestDto {
    private Order.Status status;
}
