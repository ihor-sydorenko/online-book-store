package online.book.store.service;

import java.util.List;
import online.book.store.dto.BookDto;
import online.book.store.dto.BookSearchParametersDto;
import online.book.store.dto.CreateBookRequestDto;

public interface BookService {
    BookDto save(CreateBookRequestDto requestDto);

    List<BookDto> findAll();

    BookDto findById(Long id);

    BookDto updateById(CreateBookRequestDto requestDto, Long id);

    void deleteById(Long id);

    List<BookDto> search(BookSearchParametersDto parametersDto);
}
