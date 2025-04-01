package online.book.store.repository;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Optional;
import online.book.store.model.ShoppingCart;
import online.book.store.model.User;
import online.book.store.repository.shoppingcart.ShoppingCartRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Sql(scripts = "classpath:database/shoppingcart/add-shopping-cart.sql",
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "classpath:database/shoppingcart/delete-shopping-cart.sql",
        executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class ShoppingCartRepositoryTest {
    @Autowired
    private ShoppingCartRepository shoppingCartRepository;

    @Test
    @DisplayName("Find users shopping cart by existing user id")
    void findShoppingCartByUserId_CorrectUserId_ReturnShoppingCart() {
        Long userId = 1L;
        User user = new User();
        user.setId(userId);
        user.setEmail("ihor@gmail.com");
        user.setPassword("password");
        user.setFirstName("Ihor");
        user.setLastName("Sydorenko");
        Optional<ShoppingCart> shoppingCart =
                shoppingCartRepository.findShoppingCartByUserId(userId);
        assertNotNull(shoppingCart);
    }
}
