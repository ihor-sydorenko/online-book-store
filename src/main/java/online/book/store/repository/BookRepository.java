package online.book.store.repository;

import java.util.List;
import online.book.store.model.Book;

public interface BookRepository {
    Book save(Book book);

    List<Book> findAll();
}
