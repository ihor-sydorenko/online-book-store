package online.book.store.mapper;

import online.book.store.config.MapperConfig;
import online.book.store.dto.category.CategoryDto;
import online.book.store.dto.category.CategoryRequestDto;
import online.book.store.model.Category;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(config = MapperConfig.class)
public interface CategoryMapper {
    CategoryDto toDto(Category category);

    Category toModel(CategoryRequestDto requestDto);

    void updateCategoryFromDto(CategoryRequestDto requestDto,
                               @MappingTarget Category existingCategory);
}
