package online.book.store.service;

import java.util.List;
import online.book.store.dto.category.CategoryDto;
import online.book.store.dto.category.CategoryRequestDto;
import org.springframework.data.domain.Pageable;

public interface CategoryService {
    List<CategoryDto> findAll(Pageable pageable);

    CategoryDto getById(Long id);

    CategoryDto save(CategoryRequestDto requestDto);

    CategoryDto updateById(Long id, CategoryRequestDto requestDto);

    void deleteById(Long id);
}
