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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class ShoppingCartServiceImpl implements ShoppingCartService {
    private final BookRepository bookRepository;
    private final ShoppingCartRepository shoppingCartRepository;
    private final ShoppingCartMapper shoppingCartMapper;
    private final CartItemMapper cartItemMapper;
    private final CartItemRepository cartItemRepository;

    @Override
    public void createShoppingCart(User user) {
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setUser(user);
        shoppingCartRepository.save(shoppingCart);
    }

    @Transactional
    @Override
    public ShoppingCartDto getShoppingCartByUser() {
        return shoppingCartMapper.toDto(findShoppingCartByUser());
    }

    @Transactional
    @Override
    public ShoppingCartDto addBookToShoppingCart(CartItemRequestDto requestDto) {
        Book book = bookRepository.findById(requestDto.getBookId()).orElseThrow(
                () -> new EntityNotFoundException(
                        String.format("Can't find cartItem by id: %s", requestDto.getBookId()))
        );
        ShoppingCart shoppingCart = findShoppingCartByUser();
        checkBookInShoppingCart(shoppingCart.getCartItems(), book.getId());
        CartItem cartItem = cartItemMapper.toModel(requestDto);
        cartItem.setShoppingCart(shoppingCart);
        cartItem.setBook(book);
        shoppingCart.getCartItems().add(cartItem);
        shoppingCartRepository.save(shoppingCart);
        return shoppingCartMapper.toDto(shoppingCart);
    }

    @Transactional
    @Override
    public ShoppingCartDto updateCartItem(UpdateCartItemRequestDto requestDto, Long cartItemId) {
        CartItem cartItem = cartItemRepository.findCartItemById(cartItemId).orElseThrow(
                () -> new EntityNotFoundException(
                        String.format("Can't find cartItem by id: %s", cartItemId))
        );
        cartItem.setQuantity(requestDto.getQuantity());
        cartItemRepository.save(cartItem);
        return shoppingCartMapper.toDto(findShoppingCartByUser());
    }

    @Override
    public ShoppingCartDto deleteCartItemById(Long cartItemId) {
        CartItem cartItem = cartItemRepository.findCartItemByIdAndShoppingCartId(
                cartItemId, findShoppingCartByUser().getId());
        cartItemRepository.delete(cartItem);
        return shoppingCartMapper.toDto(findShoppingCartByUser());
    }

    private ShoppingCart findShoppingCartByUser() {
        User user = getUserFromSecurityContextHolder();
        return shoppingCartRepository.findShoppingCartByUserId(user.getId()).orElseGet(
                () -> {
                    ShoppingCart shoppingCart = new ShoppingCart();
                    shoppingCart.setUser(user);
                    return shoppingCartRepository.save(shoppingCart);
                });
    }

    private User getUserFromSecurityContextHolder() {
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
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
