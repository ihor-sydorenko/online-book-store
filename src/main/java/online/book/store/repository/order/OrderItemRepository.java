package online.book.store.repository.order;

import online.book.store.dto.orderitem.OrderItemResponseDto;
import online.book.store.model.Order;
import online.book.store.model.OrderItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    Page<OrderItemResponseDto> getAllByOrder(Order order, Pageable pageable);
}
