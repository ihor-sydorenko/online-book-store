package online.book.store.dto.cartitem;

import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class UpdateCartItemRequestDto {
    @Positive
    private int quantity;
}
