package online.book.store.repository.user;

import java.util.Optional;
import online.book.store.model.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    @EntityGraph(attributePaths = "roles")

    Optional<User> findUserByEmail(String email);

    boolean existsUserByEmail(String email);
}
