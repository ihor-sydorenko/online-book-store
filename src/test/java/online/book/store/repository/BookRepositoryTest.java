package online.book.store.repository;

import java.util.Collections;
import online.book.store.model.Book;
import online.book.store.repository.book.BookRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.jdbc.Sql;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Sql(scripts = {"classpath:database/add-categories-to-categories-table.sql",
        "classpath:database/add-books-to-books-table.sql",
        "classpath:database/add-books-categories-relationship.sql"
}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "classpath:database/delete-all.sql",
        executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class BookRepositoryTest {
    @Autowired
    private BookRepository bookRepository;

    @Test
    @DisplayName("Find all books from specific existing category")
    void findAllByCategoriesId_CorrectCategoryId_ReturnList() {
        Long categoryId = 1L;
        Pageable pageable = PageRequest.of(0, 5);
        Page<Book> actual = bookRepository.findAllByCategoriesId(pageable, categoryId);
        Assertions.assertEquals(2, actual.toList().size());
    }

    @Test
    @DisplayName("Find all books from non existing category")
    void findAllByCategoriesId_IncorrectCategoryId_ReturnEmptyList() {
        Long categoryId = 3L;
        Pageable pageable = PageRequest.of(0, 5);
        Page<Book> actual = bookRepository.findAllByCategoriesId(pageable, categoryId);
        Assertions.assertEquals(Collections.emptyList(), actual.toList());
    }
}
