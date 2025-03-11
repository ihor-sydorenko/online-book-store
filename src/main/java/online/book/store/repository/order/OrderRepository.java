package online.book.store.repository.order;

import java.util.Optional;
import online.book.store.model.Order;
import online.book.store.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
    Page<Order> findAllByUser(User user, Pageable pageable);

    Optional<Order> findOrderById(Long orderId);
}
