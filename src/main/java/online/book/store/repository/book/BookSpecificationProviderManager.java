package online.book.store.repository.book;

import java.util.List;
import lombok.RequiredArgsConstructor;
import online.book.store.model.Book;
import online.book.store.repository.SpecificationProvider;
import online.book.store.repository.SpecificationProviderManager;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class BookSpecificationProviderManager implements SpecificationProviderManager<Book> {
    private final List<SpecificationProvider<Book>> bookSpecificationProviders;

    @Override
    public SpecificationProvider<Book> getSpecificationProvider(String key) {
        return bookSpecificationProviders.stream()
                .filter(p -> p.getKey().equals(key))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Can't find correct specification "
                        + "provider for key: " + key));
    }
}
