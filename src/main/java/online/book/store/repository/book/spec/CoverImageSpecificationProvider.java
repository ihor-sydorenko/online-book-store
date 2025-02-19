package online.book.store.repository.book.spec;

import java.util.Arrays;
import online.book.store.model.Book;
import online.book.store.repository.SpecificationProvider;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class CoverImageSpecificationProvider implements SpecificationProvider<Book> {
    public static final String COVER_IMAGE = "coverImage";

    @Override
    public String getKey() {
        return COVER_IMAGE;
    }

    @Override
    public Specification<Book> getSpecification(String[] params) {
        return (root, query, criteriaBuilder) ->
                root.get(COVER_IMAGE).in(Arrays.stream(params).toArray());
    }
}
