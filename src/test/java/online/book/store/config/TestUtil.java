package online.book.store.config;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import online.book.store.dto.book.BookDto;
import online.book.store.dto.book.CreateBookRequestDto;
import online.book.store.dto.cartitem.CartItemRequestDto;
import online.book.store.dto.cartitem.CartItemResponseDto;
import online.book.store.dto.category.CategoryDto;
import online.book.store.dto.category.CategoryRequestDto;
import online.book.store.dto.shoppingcart.ShoppingCartDto;
import online.book.store.model.Book;
import online.book.store.model.CartItem;
import online.book.store.model.ShoppingCart;
import online.book.store.model.User;

public class TestUtil {
    public static List<BookDto> getExpectedListOfBooks() {
        List<BookDto> expected = new ArrayList<>();
        expected.add(new BookDto().setId(1L).setTitle("Title1").setAuthor("Author1")
                .setIsbn("000.1").setPrice(BigDecimal.valueOf(19)).setDescription("Description1")
                .setCoverImage("CoverImage1").setCategoryIds(Set.of(1L)));
        expected.add(new BookDto().setId(2L).setTitle("Title2").setAuthor("Author2")
                .setIsbn("000.2").setPrice(BigDecimal.valueOf(29)).setDescription("Description2")
                .setCoverImage("CoverImage2").setCategoryIds(Set.of(1L)));
        expected.add(new BookDto().setId(3L).setTitle("Title3").setAuthor("Author3")
                .setIsbn("000.3").setPrice(BigDecimal.valueOf(39)).setDescription("Description3")
                .setCoverImage("CoverImage3").setCategoryIds(Set.of(2L)));
        return expected;
    }

    public static CreateBookRequestDto createBookRequestDto() {
        return new CreateBookRequestDto()
                .setTitle("Title1")
                .setAuthor("Author1")
                .setIsbn("0.001")
                .setPrice(BigDecimal.valueOf(19.5))
                .setDescription("Description1")
                .setCoverImage("CoverImage1")
                .setCategoryIds(Set.of(1L, 2L));
    }

    public static BookDto createBookDto(Long id) {
        CreateBookRequestDto requestDto = createBookRequestDto();
        return new BookDto()
                .setId(id)
                .setTitle(requestDto.getTitle())
                .setAuthor(requestDto.getAuthor())
                .setIsbn(requestDto.getIsbn())
                .setPrice(requestDto.getPrice())
                .setDescription(requestDto.getDescription())
                .setCoverImage(requestDto.getCoverImage())
                .setCategoryIds(requestDto.getCategoryIds());
    }

    public static CategoryRequestDto createCategoryRequestDto() {
        return new CategoryRequestDto()
                .setName("Category1")
                .setDescription("Description1");
    }

    public static CategoryDto createCategoryDto(Long id) {
        CategoryRequestDto requestDto = createCategoryRequestDto();
        return new CategoryDto()
                .setId(id)
                .setName(requestDto.getName())
                .setDescription(requestDto.getDescription());
    }

    public static CartItemResponseDto getExpectedCartItem(int quantity) {
        return new CartItemResponseDto()
                .setId(1L)
                .setBookId(1L)
                .setBookTitle("Title1")
                .setQuantity(quantity);
    }

    public static CartItemRequestDto createCartItemRequestDto(Long bookId, int quantity) {
        return new CartItemRequestDto()
                .setBookId(bookId)
                .setQuantity(quantity);
    }

    public static Set<CartItemResponseDto> getExpectedSetOfCartItems() {
        Set<CartItemResponseDto> expectedCartItems = new HashSet<>();
        expectedCartItems.add(new CartItemResponseDto()
                .setId(1L)
                .setBookId(1L)
                .setBookTitle("Title1")
                .setQuantity(3));
        expectedCartItems.add(new CartItemResponseDto()
                .setId(2L)
                .setBookId(2L)
                .setBookTitle("Title2")
                .setQuantity(1));
        return expectedCartItems;
    }

    public static ShoppingCartDto getExpectedShoppingCart(Set<CartItemResponseDto> cartItemsDto) {
        return new ShoppingCartDto()
                .setId(1L)
                .setUserId(1L)
                .setCartItemsDto(cartItemsDto);
    }

    public static ShoppingCartDto createEmptyExpectedShoppingCart(Long userId) {
        return new ShoppingCartDto()
                .setId(userId);
    }

    public static User createUser(Long userId) {
        return new User()
                .setId(userId)
                .setEmail("ihor@gmail.com");
    }

    public static Book createBook(Long bookId) {
        return new Book()
                .setId(bookId)
                .setTitle("Title1")
                .setAuthor("Author1")
                .setIsbn("000.1")
                .setPrice(BigDecimal.valueOf(19))
                .setDescription("Description1")
                .setCoverImage("CoverImage1");
    }

    public static ShoppingCart createShoppingCart(Long id, User user) {
        return new ShoppingCart()
                .setId(id)
                .setUser(user)
                .setCartItems(new HashSet<>());
    }

    public static CartItem createCartItem(Long cartItemId, ShoppingCart shoppingCart,
                                          Book book, int initialQuantity) {
        return new CartItem()
                .setId(cartItemId)
                .setShoppingCart(shoppingCart)
                .setBook(book)
                .setQuantity(initialQuantity);
    }
}
