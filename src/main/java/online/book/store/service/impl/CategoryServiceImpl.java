package online.book.store.service.impl;

import java.util.List;
import lombok.RequiredArgsConstructor;
import online.book.store.dto.category.CategoryDto;
import online.book.store.dto.category.CategoryRequestDto;
import online.book.store.exception.EntityNotFoundException;
import online.book.store.mapper.CategoryMapper;
import online.book.store.model.Category;
import online.book.store.repository.category.CategoryRepository;
import online.book.store.service.CategoryService;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Override
    public List<CategoryDto> findAll(Pageable pageable) {
        return categoryRepository.findAll(pageable).stream()
                .map(categoryMapper::toDto)
                .toList();
    }

    @Override
    public CategoryDto getById(Long id) {
        return categoryMapper.toDto(categoryRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(
                        String.format("Can't find category by id: %s", id)))
        );
    }

    @Override
    public CategoryDto save(CategoryRequestDto requestDto) {
        return categoryMapper.toDto(categoryRepository.save(categoryMapper.toModel(requestDto)));
    }

    @Override
    public CategoryDto updateById(Long id, CategoryRequestDto requestDto) {
        Category existingCategory = categoryRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(
                        String.format("Can't find category by id: %s", id))
        );
        categoryMapper.updateCategoryFromDto(requestDto, existingCategory);
        return categoryMapper.toDto(categoryRepository.save(existingCategory));
    }

    @Override
    public void deleteById(Long id) {
        categoryRepository.deleteById(id);
    }
}
