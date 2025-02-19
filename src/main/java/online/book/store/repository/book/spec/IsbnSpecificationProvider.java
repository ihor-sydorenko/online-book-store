package online.book.store.repository.book.spec;

import java.util.Arrays;
import online.book.store.model.Book;
import online.book.store.repository.SpecificationProvider;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class IsbnSpecificationProvider implements SpecificationProvider<Book> {
    public static final String ISBN = "isbn";

    @Override
    public String getKey() {
        return ISBN;
    }

    @Override
    public Specification<Book> getSpecification(String[] params) {
        return (root, query, criteriaBuilder) ->
                root.get(ISBN).in(Arrays.stream(params).toArray());
    }
}
