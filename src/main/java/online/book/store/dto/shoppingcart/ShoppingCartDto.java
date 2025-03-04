package online.book.store.dto.shoppingcart;

import java.util.Set;
import lombok.Data;
import online.book.store.dto.cartitem.CartItemResponseDto;

@Data
public class ShoppingCartDto {
    private Long id;
    private Long userId;
    private Set<CartItemResponseDto> cartItemsDto;
}
