package online.book.store.mapper;

import online.book.store.config.MapperConfig;
import online.book.store.dto.order.OrderResponseDto;
import online.book.store.model.Order;
import online.book.store.model.ShoppingCart;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapperConfig.class, uses = {UserMapper.class, OrderItemMapper.class})
public interface OrderMapper {
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "orderItemsDto", source = "orderItems",
            qualifiedByName = "setOrderItemsDtoFromOrderItems")
    OrderResponseDto toDto(Order order);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "orderItems", source = "cartItems",
            qualifiedByName = "setOrderItemsFromCartItems")
    Order toModelFromShoppingCart(ShoppingCart shoppingCart);
}
