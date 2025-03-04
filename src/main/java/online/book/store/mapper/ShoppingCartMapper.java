package online.book.store.mapper;

import online.book.store.config.MapperConfig;
import online.book.store.dto.shoppingcart.ShoppingCartDto;
import online.book.store.model.ShoppingCart;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapperConfig.class, uses = CartItemMapper.class)
public interface ShoppingCartMapper {
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "cartItemsDto", source = "cartItems", qualifiedByName = "getCartItemsDtos")
    ShoppingCartDto toDto(ShoppingCart shoppingCart);

    ShoppingCart toModel(ShoppingCartDto shoppingCartDto);
}
