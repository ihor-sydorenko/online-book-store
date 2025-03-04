package online.book.store.service.impl;

import java.util.List;
import lombok.RequiredArgsConstructor;
import online.book.store.dto.book.BookDto;
import online.book.store.dto.book.BookDtoWithoutCategoryIds;
import online.book.store.dto.book.BookSearchParametersDto;
import online.book.store.dto.book.CreateBookRequestDto;
import online.book.store.exception.EntityNotFoundException;
import online.book.store.mapper.BookMapper;
import online.book.store.model.Book;
import online.book.store.repository.book.BookRepository;
import online.book.store.repository.book.BookSpecificationBuilder;
import online.book.store.service.BookService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@RequiredArgsConstructor
@Service
public class BookServiceImpl implements BookService {
    private final BookRepository bookRepository;
    private final BookMapper bookMapper;
    private final BookSpecificationBuilder bookSpecificationBuilder;

    @Override
    public BookDto save(CreateBookRequestDto requestDto) {
        Book book = bookMapper.toModel(requestDto);
        return bookMapper.toDto(bookRepository.save(book));
    }

    @Override
    public List<BookDto> findAll(Pageable pageable) {
        return bookRepository.findAll(pageable).stream()
                .map(bookMapper::toDto)
                .toList();
    }

    @Override
    public BookDto findById(Long id) {
        Book book = bookRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Can't find book by id: " + id)
        );
        return bookMapper.toDto(book);
    }

    @Override
    public BookDto updateById(CreateBookRequestDto requestDto, Long id) {
        Book existingBook = bookRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Can't find book with id: " + id));
        bookMapper.updateBookFromDto(requestDto, existingBook);
        return bookMapper.toDto(existingBook);
    }

    @Override
    public void deleteById(Long id) {
        bookRepository.deleteById(id);
    }

    @Override
    public List<BookDto> search(BookSearchParametersDto parametersDto, Pageable pageable) {
        Specification<Book> bookSpecification = bookSpecificationBuilder.build(parametersDto);
        return bookRepository.findAll(bookSpecification, pageable).stream()
                .map(bookMapper::toDto)
                .toList();
    }

    @Override
    public Page<BookDtoWithoutCategoryIds> getBooksByCategoryId(Pageable pageable, Long id) {
        return bookRepository.findAllByCategoriesId(pageable, id)
                .map(bookMapper::toDtoWithoutCategories);
    }
}
