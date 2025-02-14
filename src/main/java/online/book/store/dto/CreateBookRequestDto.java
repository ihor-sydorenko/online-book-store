package online.book.store.dto;

import java.math.BigDecimal;
import lombok.Data;
import org.antlr.v4.runtime.misc.NotNull;

@Data
public class CreateBookRequestDto {
    @NotNull
    private String title;
    @NotNull
    private String author;
    @NotNull
    private String isbn;
    @NotNull
    private BigDecimal price;
    private String description;
    private String coverImage;
}
