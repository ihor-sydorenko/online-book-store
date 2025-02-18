package online.book.store.repository.book.spec;

import java.util.Arrays;
import online.book.store.model.Book;
import online.book.store.repository.SpecificationProvider;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class PriceSpecificationProvider implements SpecificationProvider<Book> {
    public static final String PRICE = "price";

    @Override
    public String getKey() {
        return PRICE;
    }

    @Override
    public Specification<Book> getSpecification(String[] params) {
        return (root, query, criteriaBuilder) ->
                root.get(PRICE).in(Arrays.stream(params).toArray());
    }
}
