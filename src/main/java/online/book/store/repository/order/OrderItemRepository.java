package online.book.store.repository.order;

import java.util.Optional;
import online.book.store.model.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    Optional<OrderItem> findByIdAndOrderIdAndOrderUserId(Long itemId,
                                                         Long orderId,
                                                         Long userId);
}
