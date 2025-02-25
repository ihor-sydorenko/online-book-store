package online.book.store.mapper;

import online.book.store.config.MapperConfig;
import online.book.store.dto.book.BookDto;
import online.book.store.dto.book.CreateBookRequestDto;
import online.book.store.model.Book;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(config = MapperConfig.class)
public interface BookMapper {
    BookDto toDto(Book book);

    Book toModel(CreateBookRequestDto requestDto);

    void updateBookFromDto(CreateBookRequestDto requestDto, @MappingTarget Book existingBook);
}
