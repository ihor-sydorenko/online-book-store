package online.book.store.mapper;

import java.util.stream.Collectors;
import online.book.store.config.MapperConfig;
import online.book.store.dto.book.BookDto;
import online.book.store.dto.book.BookDtoWithoutCategoryIds;
import online.book.store.dto.book.CreateBookRequestDto;
import online.book.store.model.Book;
import online.book.store.model.Category;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(config = MapperConfig.class)
public interface BookMapper {
    BookDto toDto(Book book);

    Book toModel(CreateBookRequestDto requestDto);

    BookDtoWithoutCategoryIds toDtoWithoutCategories(Book book);

    @AfterMapping
    default void setCategoryIds(@MappingTarget BookDto bookDto, Book book) {
        bookDto.setCategoryIds(book.getCategories().stream()
                .map(Category::getId)
                .collect(Collectors.toSet()));
    }

    void updateBookFromDto(CreateBookRequestDto requestDto, @MappingTarget Book existingBook);
}
