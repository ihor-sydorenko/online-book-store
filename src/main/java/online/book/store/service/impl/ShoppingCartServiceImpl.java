package online.book.store.service.impl;

import java.util.Set;
import lombok.RequiredArgsConstructor;
import online.book.store.dto.cartitem.CartItemRequestDto;
import online.book.store.dto.cartitem.UpdateCartItemRequestDto;
import online.book.store.dto.shoppingcart.ShoppingCartDto;
import online.book.store.exception.DataProcessingException;
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
import online.book.store.service.ShoppingCartService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@RequiredArgsConstructor
@Service
public class ShoppingCartServiceImpl implements ShoppingCartService {
    private final BookRepository bookRepository;
    private final ShoppingCartRepository shoppingCartRepository;
    private final CartItemRepository cartItemRepository;
    private final ShoppingCartMapper shoppingCartMapper;
    private final CartItemMapper cartItemMapper;

    @Override
    public void createShoppingCart(User user) {
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setUser(user);
        shoppingCartRepository.save(shoppingCart);
    }

    @Override
    public ShoppingCartDto getShoppingCartByUserId(Long userId) {
        return shoppingCartMapper.toDto(findShoppingCartByUserId(userId));
    }

    @Override
    public ShoppingCartDto addBookToShoppingCart(CartItemRequestDto requestDto, Long userId) {
        Book book = bookRepository.findById(requestDto.getBookId()).orElseThrow(
                () -> new EntityNotFoundException(
                        String.format("Can't find cartItem by id: %s", requestDto.getBookId()))
        );
        ShoppingCart shoppingCart = findShoppingCartByUserId(userId);
        checkBookInShoppingCart(shoppingCart.getCartItems(), book.getId());
        CartItem cartItem = cartItemMapper.toModel(requestDto);
        cartItem.setShoppingCart(shoppingCart);
        cartItem.setBook(book);
        shoppingCart.getCartItems().add(cartItem);
        shoppingCartRepository.save(shoppingCart);
        return shoppingCartMapper.toDto(shoppingCart);
    }

    @Override
    public ShoppingCartDto updateCartItem(UpdateCartItemRequestDto requestDto,
                                          Long cartItemId,
                                          Long userId) {
        ShoppingCart shoppingCart = findShoppingCartByUserId(userId);
        CartItem cartItem = cartItemRepository.findCartItemByIdAndShoppingCartId(
                cartItemId, shoppingCart.getId()).orElseThrow(() -> new EntityNotFoundException(
                String.format("Can't find cartItem by id: %s", cartItemId))
        );
        cartItem.setQuantity(requestDto.getQuantity());
        cartItemRepository.save(cartItem);
        return shoppingCartMapper.toDto(shoppingCart);
    }

    @Override
    public void deleteCartItemById(Long cartItemId, Long userId) {
        ShoppingCart shoppingCart = findShoppingCartByUserId(userId);
        CartItem cartItem = cartItemRepository.findCartItemByIdAndShoppingCartId(
                cartItemId, shoppingCart.getId()).orElseThrow(
                        () -> new EntityNotFoundException(
                        String.format("Can't find cartItem by id: %s", cartItemId))
        );
        cartItemRepository.delete(cartItem);
        shoppingCart.getCartItems().remove(cartItem);
    }

    private ShoppingCart findShoppingCartByUserId(Long userId) {
        return shoppingCartRepository.findShoppingCartByUserId(userId).orElseThrow(
                () -> new EntityNotFoundException(
                        String.format("Can't find shopping cart by id: %s", userId))
        );
    }

    private void checkBookInShoppingCart(Set<CartItem> cartItems, Long bookId) {
        cartItems.stream()
                .map(cartItem -> cartItem.getBook().getId())
                .filter(bookId::equals)
                .findFirst()
                .ifPresent(b -> {
                    throw new DataProcessingException(String.format(
                            "Book with id: %s is already in the shopping cart!", bookId));
                });
    }
}
