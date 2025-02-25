package online.book.store.repository.book;

import lombok.RequiredArgsConstructor;
import online.book.store.dto.book.BookSearchParametersDto;
import online.book.store.model.Book;
import online.book.store.repository.SpecificationBuilder;
import online.book.store.repository.SpecificationProviderManager;
import online.book.store.repository.book.spec.AuthorSpecificationProvider;
import online.book.store.repository.book.spec.CoverImageSpecificationProvider;
import online.book.store.repository.book.spec.DescriptionSpecificationProvider;
import online.book.store.repository.book.spec.IsbnSpecificationProvider;
import online.book.store.repository.book.spec.PriceSpecificationProvider;
import online.book.store.repository.book.spec.TitleSpecificationProvider;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class BookSpecificationBuilder implements SpecificationBuilder<Book> {
    private final SpecificationProviderManager<Book> bookSpecificationProviderManager;

    @Override
    public Specification<Book> build(BookSearchParametersDto searchParametersDto) {
        Specification<Book> specification = Specification.where(null);
        if (searchParametersDto.titles() != null && searchParametersDto.titles().length > 0) {
            specification = specification.and(bookSpecificationProviderManager
                    .getSpecificationProvider(TitleSpecificationProvider.TITLE)
                    .getSpecification(searchParametersDto.titles()));
        }
        if (searchParametersDto.authors() != null && searchParametersDto.authors().length > 0) {
            specification = specification.and(bookSpecificationProviderManager
                    .getSpecificationProvider(AuthorSpecificationProvider.AUTHOR)
                    .getSpecification(searchParametersDto.authors()));
        }
        if (searchParametersDto.isbns() != null && searchParametersDto.isbns().length > 0) {
            specification = specification.and(bookSpecificationProviderManager
                    .getSpecificationProvider(IsbnSpecificationProvider.ISBN)
                    .getSpecification(searchParametersDto.isbns()));
        }
        if (searchParametersDto.prices() != null && searchParametersDto.prices().length > 0) {
            specification = specification.and(bookSpecificationProviderManager
                    .getSpecificationProvider(PriceSpecificationProvider.PRICE)
                    .getSpecification(searchParametersDto.prices()));
        }
        if (searchParametersDto.descriptions() != null
                && searchParametersDto.descriptions().length > 0) {
            specification = specification.and(bookSpecificationProviderManager
                    .getSpecificationProvider(DescriptionSpecificationProvider.DESCRIPTION)
                    .getSpecification(searchParametersDto.descriptions()));
        }
        if (searchParametersDto.coverImages() != null
                && searchParametersDto.coverImages().length > 0) {
            specification = specification.and(bookSpecificationProviderManager
                    .getSpecificationProvider(CoverImageSpecificationProvider.COVER_IMAGE)
                    .getSpecification(searchParametersDto.coverImages()));
        }
        return specification;
    }
}
