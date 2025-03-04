package online.book.store.mapper;

import java.util.Set;
import java.util.stream.Collectors;
import online.book.store.config.MapperConfig;
import online.book.store.dto.cartitem.CartItemRequestDto;
import online.book.store.dto.cartitem.CartItemResponseDto;
import online.book.store.model.CartItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(config = MapperConfig.class, uses = BookMapper.class)
public interface CartItemMapper {
    @Mapping(target = "bookId", source = "book.id")
    @Mapping(target = "bookTitle", source = "book.title")
    CartItemResponseDto toDto(CartItem cartItem);

    @Mapping(target = "book", source = "bookId", qualifiedByName = "bookFromId")
    CartItem toModel(CartItemRequestDto requestDto);

    @Named("getCartItemsDtos")
    default Set<CartItemResponseDto> getCartItemsDtos(Set<CartItem> cartItems) {
        return cartItems.stream()
                .map(this::toDto)
                .collect(Collectors.toSet());
    }
}
