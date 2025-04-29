package online.book.store.controller;

import static org.apache.commons.lang3.builder.EqualsBuilder.reflectionEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Set;
import online.book.store.config.TestUtil;
import online.book.store.dto.cartitem.CartItemRequestDto;
import online.book.store.dto.cartitem.CartItemResponseDto;
import online.book.store.dto.cartitem.UpdateCartItemRequestDto;
import online.book.store.dto.shoppingcart.ShoppingCartDto;
import online.book.store.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@ExtendWith(MockitoExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(scripts = {"classpath:database/shoppingcart/add-shopping-cart.sql",
        "classpath:database/book/add-books-to-books-table.sql",
        "classpath:database/shoppingcart/add-cart-item.sql"
}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "classpath:database/shoppingcart/delete-all.sql",
        executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class ShoppingCartControllerTest {
    protected static MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void beforeAll(
            @Autowired WebApplicationContext applicationContext) {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(applicationContext)
                .apply(springSecurity())
                .build();
    }

    @BeforeEach
    void setUser() {
        User user = new User()
                .setId(1L)
                .setEmail("ihor@gmail.com");

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(user, null,
                        List.of(new SimpleGrantedAuthority("ROLE_USER")));
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @WithMockUser(username = "user", roles = {"USER"})
    @Test
    @DisplayName("Find shopping cart by existing user - return a shopping cart")
    void getShoppingCartByUser_ExistingShoppingCart_ReturnShoppingCart() throws Exception {
        CartItemResponseDto expectedCartItem = TestUtil.getExpectedCartItem(3);

        MvcResult result = mockMvc.perform(get("/cart")
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();

        ShoppingCartDto actual = objectMapper.readValue(result.getResponse().getContentAsString(),
                ShoppingCartDto.class);

        ShoppingCartDto expected = TestUtil.getExpectedShoppingCart(Set.of(expectedCartItem));
        assertNotNull(actual);
        assertNotNull(actual.getId());
        assertTrue(reflectionEquals(expected, actual, "id"));
    }

    @WithMockUser(username = "user", roles = {"USER"})
    @Test
    @DisplayName("Add book to shopping cart with valid request dto - success")
    void addBookToShoppingCart_ValidRequestDto_Success() throws Exception {
        Long bookId = 2L;
        int quantity = 1;
        CartItemRequestDto requestDto = TestUtil.createCartItemRequestDto(bookId, quantity);
        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        MvcResult result = mockMvc.perform(post("/cart")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();

        ShoppingCartDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), ShoppingCartDto.class);

        Set<CartItemResponseDto> expectedCartItems = TestUtil.getExpectedSetOfCartItems();
        ShoppingCartDto expected = TestUtil.getExpectedShoppingCart(expectedCartItems);
        assertTrue(reflectionEquals(expected, actual, "id"));
        assertNotNull(actual);
        assertNotNull(actual.getId());
        assertNotNull(actual.getCartItemsDto());
    }

    @WithMockUser(username = "user", roles = {"USER"})
    @Test
    @DisplayName("Add book to shopping cart with incorrect request dto - return bad request")
    void addBookToShoppingCart_InvalidRequestDto_ReturnBadRequest() throws Exception {
        CartItemRequestDto requestDto = new CartItemRequestDto()
                .setQuantity(4);
        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        mockMvc.perform(post("/cart")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest())
                .andReturn();
    }

    @WithMockUser(username = "user", roles = {"USER"})
    @Test
    @DisplayName("Update book quantity with valid request dto - return updated shopping cart")
    void updateBookQuantity_ValidUpdateRequestDto_ReturnUpdatedShoppingCart() throws Exception {
        Long cartItemId = 1L;
        UpdateCartItemRequestDto requestDto = new UpdateCartItemRequestDto()
                .setQuantity(7);

        String jsonRequest = objectMapper.writeValueAsString(requestDto);
        MvcResult result = mockMvc.perform(put("/cart/cart-items/{id}", cartItemId)
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        ShoppingCartDto actual = objectMapper
                .readValue(result.getResponse().getContentAsString(), ShoppingCartDto.class);

        CartItemResponseDto expectedCartItem = TestUtil.getExpectedCartItem(7);
        ShoppingCartDto expected = TestUtil.getExpectedShoppingCart(Set.of(expectedCartItem));
        assertTrue(reflectionEquals(expected, actual, "id"));
        assertNotNull(actual);
        assertNotNull(actual.getId());
        assertNotNull(actual.getCartItemsDto());
    }

    @WithMockUser(username = "user", roles = {"USER"})
    @Test
    @DisplayName("Update cart item quantity when cart item not found should return not found")
    void updateBookQuantity_InvalidCartItemId_ReturnNotFound() throws Exception {
        UpdateCartItemRequestDto requestDto = new UpdateCartItemRequestDto()
                .setQuantity(7);

        String jsonRequest = objectMapper.writeValueAsString(requestDto);
        mockMvc.perform(put("/cart/cart-items/{id}", 100L)
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @WithMockUser(username = "user", roles = {"USER"})
    @Test
    @DisplayName("Delete cart item by existing id - delete cart item")
    void deleteCartItemById_ByExistingId_DeleteCartItem() throws Exception {
        Long cartItemId = 1L;
        mockMvc.perform(delete("/cart/cart-items/{id}", cartItemId))
                .andExpect(status().isNoContent());
    }
}
