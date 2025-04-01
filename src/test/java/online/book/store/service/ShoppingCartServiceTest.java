package online.book.store.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Optional;
import online.book.store.dto.cartitem.CartItemRequestDto;
import online.book.store.dto.cartitem.UpdateCartItemRequestDto;
import online.book.store.dto.shoppingcart.ShoppingCartDto;
import online.book.store.exception.EntityNotFoundException;
import online.book.store.mapper.CartItemMapper;
import online.book.store.mapper.ShoppingCartMapper;
import online.book.store.model.Book;
import online.book.store.model.CartItem;
import online.book.store.model.ShoppingCart;
import online.book.store.model.User;
import online.book.store.repository.book.BookRepository;
import online.book.store.repository.cartitem.CartItemRepository;
import online.book.store.repository.shoppingcart.ShoppingCartRepository;
import online.book.store.service.impl.ShoppingCartServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ShoppingCartServiceTest {
    @InjectMocks
    private ShoppingCartServiceImpl shoppingCartService;
    @Mock
    private ShoppingCartRepository shoppingCartRepository;
    @Mock
    private ShoppingCartMapper shoppingCartMapper;
    @Mock
    private BookRepository bookRepository;
    @Mock
    private CartItemRepository cartItemRepository;
    @Mock
    private CartItemMapper cartItemMapper;

    @Test
    @DisplayName("Get shopping cart by valid user id - return shopping card")
    void getShoppingCartByUserId_ValidUserId_ReturnShoppingCart() {
        Long userId = 1L;
        User user = new User()
                .setId(userId)
                .setEmail("ihor@gmail.com");

        ShoppingCart shoppingCart = new ShoppingCart()
                .setId(userId)
                .setUser(user)
                .setCartItems(new HashSet<>());

        ShoppingCartDto expected = new ShoppingCartDto();
        expected.setId(userId);

        when(shoppingCartRepository.findShoppingCartByUserId(userId))
                .thenReturn(Optional.of(shoppingCart));
        when(shoppingCartMapper.toDto(shoppingCart)).thenReturn(expected);

        ShoppingCartDto actual = shoppingCartService.getShoppingCartByUserId(userId);

        assertNotNull(actual);
        assertEquals(expected, actual);
        verify(shoppingCartRepository).findShoppingCartByUserId(userId);
        verify(shoppingCartMapper).toDto(shoppingCart);
    }

    @Test
    @DisplayName("Get shopping cart by invalid user id - throw exception")
    void getShoppingCartByUserId_NonExistingUserId_ThrowException() {
        Long userId = 15L;

        when(shoppingCartRepository.findShoppingCartByUserId(userId)).thenReturn(Optional.empty());
        String expected = "Can't find shopping cart by id: " + userId;

        EntityNotFoundException actual = assertThrows(EntityNotFoundException.class,
                () -> shoppingCartService.getShoppingCartByUserId(userId));

        assertEquals(expected, actual.getMessage());
        verify(shoppingCartRepository).findShoppingCartByUserId(userId);
        verifyNoMoreInteractions(shoppingCartRepository);
    }

    @Test
    @DisplayName("Add book to shopping card by valid book id - add cart item to sc")
    void addBookToShoppingCart_ValidBookAndCart_AddCartItemToShippingCart() {
        Long userId = 1L;
        Long bookId = 1L;
        int quantity = 1;

        final User user = new User()
                .setId(userId)
                .setEmail("ihor@gmail.com");

        Book book = new Book()
                .setId(bookId)
                .setTitle("Title1")
                .setAuthor("Author1")
                .setIsbn("000.1")
                .setPrice(BigDecimal.valueOf(19))
                .setDescription("Description1")
                .setCoverImage("CoverImage1");

        ShoppingCartDto expected = new ShoppingCartDto();
        expected.setId(userId);

        CartItemRequestDto requestDto = new CartItemRequestDto()
                .setBookId(bookId)
                .setQuantity(quantity);

        CartItem cartItem = new CartItem();
        cartItem.setBook(book);
        cartItem.setQuantity(quantity);

        ShoppingCart shoppingCart = new ShoppingCart()
                .setId(userId)
                .setUser(user)
                .setCartItems(new HashSet<>());

        when(cartItemMapper.toModel(requestDto)).thenReturn(cartItem);
        when(shoppingCartRepository.findShoppingCartByUserId(userId))
                .thenReturn(Optional.of(shoppingCart));
        when(bookRepository.findById(requestDto.getBookId())).thenReturn(Optional.of(book));
        when(shoppingCartMapper.toDto(shoppingCart)).thenReturn(expected);

        ShoppingCartDto actual = shoppingCartService.addBookToShoppingCart(requestDto, userId);

        assertNotNull(actual);
        assertEquals(expected, actual);
        verify(cartItemMapper).toModel(requestDto);
        verify(shoppingCartRepository).findShoppingCartByUserId(userId);
        verify(bookRepository).findById(bookId);
        verify(shoppingCartMapper).toDto(shoppingCart);
    }

    @Test
    @DisplayName("Update quantity of cart item in shopping cart by valid cart item id "
            + "- update quantity of cart item")
    void updateCartItem_ValidCartAndItem_UpdatesQuantity() {
        Long userId = 1L;
        Long cartItemId = 1L;
        Long bookId = 1L;
        int initialQuantity = 5;

        User user = new User()
                .setId(userId)
                .setEmail("ihor@gmail.com");

        Book book = new Book()
                .setId(bookId)
                .setTitle("Title1")
                .setAuthor("Author1")
                .setIsbn("000.1")
                .setPrice(BigDecimal.valueOf(19))
                .setDescription("Description1")
                .setCoverImage("CoverImage1");

        ShoppingCart shoppingCart = new ShoppingCart()
                .setId(userId)
                .setUser(user)
                .setCartItems(new HashSet<>());

        CartItem cartItem = new CartItem()
                .setId(cartItemId)
                .setShoppingCart(shoppingCart)
                .setBook(book)
                .setQuantity(initialQuantity);

        shoppingCart.getCartItems().add(cartItem);

        ShoppingCartDto expected = new ShoppingCartDto();
        expected.setId(userId);

        UpdateCartItemRequestDto requestDto = new UpdateCartItemRequestDto()
                .setQuantity(7);

        when(shoppingCartRepository.findShoppingCartByUserId(userId))
                .thenReturn(Optional.of(shoppingCart));
        when(cartItemRepository.findCartItemByIdAndShoppingCartId(cartItemId, shoppingCart.getId()))
                .thenReturn(Optional.of(cartItem));
        when(shoppingCartMapper.toDto(shoppingCart)).thenReturn(expected);

        ShoppingCartDto actual = shoppingCartService
                .updateCartItem(requestDto, cartItemId, userId);

        assertNotNull(actual);
        assertEquals(expected, actual);
        assertEquals(7, cartItem.getQuantity());
        verify(shoppingCartRepository).findShoppingCartByUserId(userId);
        verify(cartItemRepository)
                .findCartItemByIdAndShoppingCartId(cartItemId, shoppingCart.getId());
        verify(shoppingCartMapper).toDto(shoppingCart);
    }

    @Test
    @DisplayName("Update quantity of cart item in shopping cart by invalid cart item id "
            + "- throw exception")
    void updateCartItem_NotFoundShoppingCard_ThrowException() {
        Long userId = 15L;
        Long cartItemId = 1L;
        UpdateCartItemRequestDto requestDto = new UpdateCartItemRequestDto().setQuantity(2);

        when(shoppingCartRepository.findShoppingCartByUserId(userId)).thenReturn(Optional.empty());
        String expected = "Can't find shopping cart by id: " + userId;

        EntityNotFoundException actual = assertThrows(EntityNotFoundException.class,
                () -> shoppingCartService.updateCartItem(requestDto, cartItemId, userId));

        assertEquals(expected, actual.getMessage());
        verify(shoppingCartRepository).findShoppingCartByUserId(userId);
        verifyNoMoreInteractions(shoppingCartRepository);
    }

    @Test
    @DisplayName("Delete cart item by invalid id - throw exception")
    void deleteCartItemById_NonExistingCartItem_ThrowException() {
        Long userId = 1L;
        Long cartItemId = 22L;

        ShoppingCart shoppingCart = new ShoppingCart()
                .setId(userId);

        String expected = "Can't find cartItem by id: " + cartItemId;

        when(shoppingCartRepository.findShoppingCartByUserId(userId))
                .thenReturn(Optional.of(shoppingCart));
        when(cartItemRepository.findCartItemByIdAndShoppingCartId(cartItemId,
                shoppingCart.getId())).thenReturn(Optional.empty());

        EntityNotFoundException actual = assertThrows(EntityNotFoundException.class,
                () -> shoppingCartService.deleteCartItemById(cartItemId, userId));

        assertEquals(expected, actual.getMessage());
        verify(shoppingCartRepository).findShoppingCartByUserId(userId);
        verify(cartItemRepository)
                .findCartItemByIdAndShoppingCartId(cartItemId, shoppingCart.getId());
        verifyNoMoreInteractions(shoppingCartRepository, cartItemRepository);
    }
}
