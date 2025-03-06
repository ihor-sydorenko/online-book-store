package online.book.store.repository.order;

import java.util.Optional;
import online.book.store.model.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
    //    @EntityGraph(attributePaths = "users")
    Page<Order> findAllByUserId(Pageable pageable, Long userId);

    Optional<Order> findOrderById(Long orderId, Pageable pageable);

    Optional<Order> findOrderById(Long orderId);
}
