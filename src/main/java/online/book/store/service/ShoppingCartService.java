package online.book.store.service;

import online.book.store.dto.cartitem.CartItemRequestDto;
import online.book.store.dto.cartitem.UpdateCartItemRequestDto;
import online.book.store.dto.shoppingcart.ShoppingCartDto;
import online.book.store.model.User;

public interface ShoppingCartService {
    void createShoppingCart(User user);

    ShoppingCartDto getShoppingCartByUserId(Long userId);

    ShoppingCartDto addBookToShoppingCart(CartItemRequestDto requestDto, Long userId);

    ShoppingCartDto updateCartItem(UpdateCartItemRequestDto requestDto,
                                   Long cartItemId,
                                   Long userId);

    void deleteCartItemById(Long id, Long userId);
}
