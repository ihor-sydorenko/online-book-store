package online.book.store.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import online.book.store.dto.book.BookDto;
import online.book.store.dto.book.CreateBookRequestDto;
import online.book.store.exception.EntityNotFoundException;
import online.book.store.mapper.BookMapper;
import online.book.store.model.Book;
import online.book.store.model.Category;
import online.book.store.repository.book.BookRepository;
import online.book.store.service.impl.BookServiceImpl;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {
    @InjectMocks
    private BookServiceImpl bookService;
    @Mock
    private BookRepository bookRepository;
    @Mock
    private BookMapper bookMapper;

    @Test
    @DisplayName("Verify save() method works")
    void save_ValidCreateBookRequestDto_ReturnBookDto() {
        CreateBookRequestDto requestDto = new CreateBookRequestDto()
                .setTitle("Title1")
                .setAuthor("Author1")
                .setIsbn("000.1")
                .setPrice(BigDecimal.valueOf(12))
                .setDescription("Description1")
                .setCoverImage("CoverImage1")
                .setCategoryIds(Set.of(1L));

        Book book = getBook();
        BookDto bookDto = getBookDto();

        when(bookMapper.toModel(requestDto)).thenReturn(book);
        when(bookRepository.save(book)).thenReturn(book);
        when(bookMapper.toDto(book)).thenReturn(bookDto);

        BookDto actual = bookService.save(requestDto);

        assertThat(actual).isEqualTo(bookDto);
        verify(bookMapper, times(1)).toModel(requestDto);
        verify(bookRepository, times(1)).save(book);
        verify(bookMapper, times(1)).toDto(book);
        verifyNoMoreInteractions(bookRepository, bookMapper);
    }

    @Test
    @DisplayName("Verify findAll() method works")
    void findAll_ValidPageable_ReturnListOfAllBooks() {
        Book book = getBook();
        BookDto bookDto = getBookDto();

        Pageable pageable = PageRequest.of(0, 10);
        List<Book> bookList = List.of(book);
        Page<Book> bookPage = new PageImpl<>(bookList, pageable, bookList.size());

        when(bookRepository.findAll(pageable)).thenReturn(bookPage);
        when(bookMapper.toDto(book)).thenReturn(bookDto);

        List<BookDto> actual = bookService.findAll(pageable);

        assertThat(actual).hasSize(1);
        assertThat(actual.get(0)).isEqualTo(bookDto);
    }

    @Test
    @DisplayName("Verify findById() method works with existing book id")
    void findById_ExistingId_ReturnBookDto() {
        Long bookId = 1L;
        Book book = getBook();
        BookDto bookDto = getBookDto();

        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
        when(bookMapper.toDto(book)).thenReturn(bookDto);

        BookDto actual = bookService.findById(bookId);

        assertNotNull(actual);
        assertEquals(bookDto, actual);
    }

    @Test
    @DisplayName("Verify findById() method with non existing id, method throw exception")
    void findById_NonExistingId_ThrowException() {
        Long bookId = 15L;
        when(bookRepository.findById(bookId)).thenReturn(Optional.empty());
        String expected = "Can't find book by id: " + bookId;
        EntityNotFoundException actual = assertThrows(EntityNotFoundException.class,
                () -> bookService.findById(bookId));
        assertEquals(expected, actual.getMessage());
    }

    @Test
    @DisplayName("Verify updateById() method with existing id")
    void updateById_ValidCreateBookRequestDtoAndCorrectId_ReturnBookDto() {
        CreateBookRequestDto requestDto = new CreateBookRequestDto()
                .setTitle("Title1")
                .setAuthor("Author1")
                .setIsbn("000.1")
                .setPrice(BigDecimal.valueOf(12))
                .setDescription("Description1")
                .setCoverImage("CoverImage1")
                .setCategoryIds(Set.of(1L));

        Book bookForUpdating = getBook();
        bookForUpdating.setTitle("updatedTitle");
        BookDto bookDto = getBookDto();
        bookDto.setTitle("updatedTitle");
        Long bookId = 1L;

        doNothing().when(bookMapper).updateBookFromDto(requestDto, bookForUpdating);
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(bookForUpdating));
        when(bookMapper.toDto(bookForUpdating)).thenReturn(bookDto);

        BookDto actual = bookService.updateById(requestDto, bookId);

        assertThat(actual).isEqualTo(bookDto);
    }

    @Test
    @DisplayName("Verify updateById() method with non existing id, method throw exception")
    void updateById_NonExistingId_throwException() {
        Long bookId = 15L;

        CreateBookRequestDto requestDto = new CreateBookRequestDto()
                .setTitle("Title1")
                .setAuthor("Author1")
                .setIsbn("000.1")
                .setPrice(BigDecimal.valueOf(12))
                .setDescription("Description1")
                .setCoverImage("CoverImage1")
                .setCategoryIds(Set.of(1L));

        when(bookRepository.findById(bookId)).thenReturn(Optional.empty());
        String expected = "Can't find book with id: " + bookId;
        EntityNotFoundException actual = assertThrows(EntityNotFoundException.class,
                () -> bookService.updateById(requestDto, bookId));
        assertEquals(expected, actual.getMessage());
    }

    @Test
    @DisplayName("Verify deleteById() method with correct id")
    void deleteById_CorrectId_DeleteBook() {
        Long bookId = 1L;
        doNothing().when(bookRepository).deleteById(bookId);

        bookService.deleteById(bookId);

        verify(bookRepository, times(1)).deleteById(bookId);
        verifyNoMoreInteractions(bookRepository);
    }

    @NotNull
    private static BookDto getBookDto() {
        return new BookDto()
                .setId(1L)
                .setTitle("Title1")
                .setAuthor("Author1")
                .setIsbn("000.1")
                .setPrice(BigDecimal.valueOf(12))
                .setDescription("Description1")
                .setCoverImage("CoverImage1")
                .setCategoryIds(Set.of(1L));
    }

    @NotNull
    private static Book getBook() {
        Category category = new Category();
        category.setId(1L);
        category.setName("Category1");
        category.setDescription("CategoryDescription1");

        return new Book()
                .setId(1L)
                .setTitle("Title1")
                .setAuthor("Author1")
                .setIsbn("000.1")
                .setPrice(BigDecimal.valueOf(12))
                .setDescription("Description1")
                .setCoverImage("CoverImage1")
                .setCategories(Set.of(category));
    }
}
