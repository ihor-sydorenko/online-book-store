package online.book.store.repository;

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

import static org.junit.jupiter.api.Assertions.*;

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
        User user = new User()
        .setId(1L)
        .setEmail("ihor@gmail.com")
        .setPassword("password")
        .setFirstName("Ihor")
        .setLastName("Sydorenko");

        ShoppingCart expected = new ShoppingCart()
                .setId(user.getId());

        Optional<ShoppingCart> actual =
                shoppingCartRepository.findShoppingCartByUserId(user.getId());

        assertNotNull(actual);
        assertTrue(actual.get().getCartItems().isEmpty());
        assertEquals(expected.getId(), actual.get().getId());
    }
}
