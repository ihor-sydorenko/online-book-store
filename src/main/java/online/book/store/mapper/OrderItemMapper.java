package online.book.store.mapper;

import java.math.BigDecimal;
import java.util.Set;
import java.util.stream.Collectors;
import online.book.store.config.MapperConfig;
import online.book.store.dto.orderitem.OrderItemResponseDto;
import online.book.store.model.CartItem;
import online.book.store.model.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(config = MapperConfig.class, uses = BookMapper.class)
public interface OrderItemMapper {
    @Mapping(target = "bookId", source = "book.id")
    OrderItemResponseDto toDto(OrderItem orderItem);

    @Mapping(target = "id", ignore = true)
    OrderItem toModelFromCartItem(CartItem cartItem);

    @Named("setOrderItemsFromCartItems")
    default Set<OrderItem> setOrderItemsFromCartItems(Set<CartItem> cartItems) {
        return cartItems.stream()
                .map(cartItem -> {
                    OrderItem orderItem = toModelFromCartItem(cartItem);
                    orderItem.setPrice(cartItem.getBook().getPrice()
                            .multiply(BigDecimal.valueOf(cartItem.getQuantity())));
                    return orderItem;
                })
                .collect(Collectors.toSet());
    }

    @Named("setOrderItemsDtoFromOrderItems")
    default Set<OrderItemResponseDto> setOrderItemsDtoFromOrderItems(Set<OrderItem> orderItems) {
        return orderItems.stream()
                .map(this::toDto)
                .collect(Collectors.toSet());
    }
}
