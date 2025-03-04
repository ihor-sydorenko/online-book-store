package online.book.store.service;

import online.book.store.dto.cartitem.CartItemRequestDto;
import online.book.store.dto.cartitem.UpdateCartItemRequestDto;
import online.book.store.dto.shoppingcart.ShoppingCartDto;
import online.book.store.model.User;

public interface ShoppingCartService {
    void createShoppingCart(User user);

    ShoppingCartDto getShoppingCartByUser();

    ShoppingCartDto addBookToShoppingCart(CartItemRequestDto requestDto);

    ShoppingCartDto updateCartItem(UpdateCartItemRequestDto requestDto, Long cartItemId);

    ShoppingCartDto deleteCartItemById(Long id);
}
