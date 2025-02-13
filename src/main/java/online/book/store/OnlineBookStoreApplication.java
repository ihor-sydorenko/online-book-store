package online.book.store;

import java.math.BigDecimal;
import online.book.store.model.Book;
import online.book.store.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class OnlineBookStoreApplication {
    @Autowired
    private BookService bookService;

    public static void main(String[] args) {
        SpringApplication.run(OnlineBookStoreApplication.class, args);
    }

    @Bean
    CommandLineRunner commandLineRunner() {
        return args -> {
            Book book = new Book();
            book.setTitle("12 Years a Slave");
            book.setAuthor("David Wilson");
            book.setDescription("Slave narrative as told by Solomon Northup");
            book.setPrice(BigDecimal.valueOf(299));
            book.setIsbn("40027");

            bookService.save(book);

            bookService.findAll().forEach(System.out::println);
        };
    }

}
