package online.book.store.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import online.book.store.dto.category.CategoryDto;
import online.book.store.dto.category.CategoryRequestDto;
import online.book.store.exception.EntityNotFoundException;
import online.book.store.mapper.CategoryMapper;
import online.book.store.model.Category;
import online.book.store.repository.category.CategoryRepository;
import online.book.store.service.impl.CategoryServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class CategoryServiceImplTest {
    @InjectMocks
    private CategoryServiceImpl categoryService;
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private CategoryMapper categoryMapper;

    @Test
    @DisplayName("Verify findAll() method works")
    void findAll_ValidPageable_ReturnPageOfAllCategories() {
        Category category = new Category()
                .setId(1L)
                .setName("Category1")
                .setDescription("Description1");

        CategoryDto categoryDto = new CategoryDto()
                .setId(category.getId())
                .setName(category.getName())
                .setDescription(category.getDescription());

        Pageable pageable = PageRequest.of(0, 10);
        List<Category> categoryList = List.of(category);
        Page<Category> categoryPage = new PageImpl<>(categoryList, pageable, categoryList.size());

        when(categoryRepository.findAll(pageable)).thenReturn(categoryPage);
        when(categoryMapper.toDto(category)).thenReturn(categoryDto);

        Page<CategoryDto> actual = categoryService.findAll(pageable);

        assertThat(actual).hasSize(1);
        assertThat(actual.toList().get(0)).isEqualTo(categoryDto);
    }

    @Test
    @DisplayName("Verify getById() method works with existing category id")
    void getById_ExistingId_ReturnCategoryDto() {
        Long categoryId = 1L;
        Category category = new Category()
                .setId(1L)
                .setName("Category1")
                .setDescription("Description1");

        CategoryDto categoryDto = new CategoryDto()
                .setId(category.getId())
                .setName(category.getName())
                .setDescription(category.getDescription());

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
        when(categoryMapper.toDto(category)).thenReturn(categoryDto);

        CategoryDto actual = categoryService.getById(categoryId);

        assertNotNull(actual);
        assertEquals(categoryDto, actual);
    }

    @Test
    @DisplayName("Verify getById() method with non existing id, method throw exception")
    void getById_ENonExistingId_throwException() {
        Long categoryId = 18L;
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());
        String expected = "Can't find category by id: " + categoryId;
        EntityNotFoundException actual = assertThrows(EntityNotFoundException.class,
                () -> categoryService.getById(categoryId));
        assertEquals(expected, actual.getMessage());
    }

    @Test
    @DisplayName("Verify save() method works")
    void save_ValidCategoryRequestDto_ReturnCategoryDto() {
        CategoryRequestDto requestDto = new CategoryRequestDto()
                .setName("Category1")
                .setDescription("Description1");

        Category category = new Category()
                .setId(1L)
                .setName(requestDto.getName())
                .setDescription(requestDto.getDescription());

        CategoryDto categoryDto = new CategoryDto()
                .setId(category.getId())
                .setName(category.getName())
                .setDescription(category.getDescription());

        when(categoryMapper.toModel(requestDto)).thenReturn(category);
        when(categoryRepository.save(category)).thenReturn(category);
        when(categoryMapper.toDto(category)).thenReturn(categoryDto);

        CategoryDto actual = categoryService.save(requestDto);

        assertThat(actual).isEqualTo(categoryDto);
        verify(categoryMapper, times(1)).toModel(requestDto);
        verify(categoryRepository, times(1)).save(category);
        verify(categoryMapper, times(1)).toDto(category);
        verifyNoMoreInteractions(categoryRepository, categoryMapper);
    }

    @Test
    @DisplayName("Verify updateById() method with existing id")
    void updateById_ValidCategoryRequestDtoAndCorrectId_ReturnCategoryDto() {
        Long categoryId = 1L;
        CategoryRequestDto requestDto = new CategoryRequestDto()
                .setName("Category1")
                .setDescription("Description1");

        Category categoryForUpdating = new Category()
                .setId(categoryId)
                .setName(requestDto.getName())
                .setDescription(requestDto.getDescription());

        CategoryDto categoryDto = new CategoryDto()
                .setId(categoryId)
                .setName(categoryForUpdating.getName())
                .setDescription(categoryForUpdating.getDescription());

        categoryForUpdating.setName("newName");
        categoryDto.setName(categoryForUpdating.getName());

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(categoryForUpdating));
        doNothing().when(categoryMapper).updateCategoryFromDto(requestDto, categoryForUpdating);
        when(categoryRepository.save(categoryForUpdating)).thenReturn(categoryForUpdating);
        when(categoryMapper.toDto(categoryForUpdating)).thenReturn(categoryDto);

        CategoryDto actual = categoryService.updateById(categoryId, requestDto);

        assertThat(actual).isEqualTo(categoryDto);
    }

    @Test
    @DisplayName("Verify updateById() method with non existing id, method throw exception")
    void updateById_NonExistingId_throwException() {
        Long categoryId = 15L;

        CategoryRequestDto requestDto = new CategoryRequestDto()
                .setName("Category1")
                .setDescription("Description1");

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());
        String expected = "Can't find category by id: " + categoryId;
        EntityNotFoundException actual = assertThrows(EntityNotFoundException.class,
                () -> categoryService.updateById(categoryId, requestDto));
        assertEquals(expected, actual.getMessage());
    }

    @Test
    @DisplayName("Verify deleteById() method with correct id")
    void deleteById_CorrectId_DeleteCategory() {
        Long categoryId = 1L;
        doNothing().when(categoryRepository).deleteById(categoryId);

        categoryService.deleteById(categoryId);

        verify(categoryRepository, times(1)).deleteById(categoryId);
        verifyNoMoreInteractions(categoryRepository);
    }
}
