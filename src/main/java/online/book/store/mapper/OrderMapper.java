package online.book.store.mapper;

import java.time.LocalDateTime;
import online.book.store.config.MapperConfig;
import online.book.store.dto.order.OrderResponseDto;
import online.book.store.model.Order;
import online.book.store.model.ShoppingCart;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

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

    @AfterMapping
    default void setStatusAndOrderDate(@MappingTarget Order order) {
        order.setStatus(Order.Status.NEW);
        order.setOrderDate(LocalDateTime.now());
    }
}
