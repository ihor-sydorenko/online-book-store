package online.book.store.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import online.book.store.dto.book.BookDtoWithoutCategoryIds;
import online.book.store.dto.category.CategoryDto;
import online.book.store.dto.category.CategoryRequestDto;
import online.book.store.service.BookService;
import online.book.store.service.CategoryService;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Category management", description = "Endpoints for managing books categories")
@RequiredArgsConstructor
@RestController
@RequestMapping("/categories")
public class CategoryController {
    private final CategoryService categoryService;
    private final BookService bookService;

    @Operation(summary = "Get all categories", description = "Get a list of all categories")
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping
    public List<CategoryDto> getAll(Pageable pageable) {
        return categoryService.findAll(pageable);
    }

    @Operation(summary = "Get category by id", description = "Get category by id")
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("/{id}")
    public CategoryDto getCategoryById(@PathVariable Long id) {
        return categoryService.getById(id);
    }

    @Operation(summary = "Create new category", description = "Create new category")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public CategoryDto createCategory(@RequestBody @Valid CategoryRequestDto requestDto) {
        return categoryService.save(requestDto);
    }

    @Operation(summary = "Update category by id", description = "Update category by id")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("/{id}")
    public CategoryDto updateCategory(@RequestBody @Valid CategoryRequestDto requestDto,
                                      @PathVariable Long id) {
        return categoryService.updateById(id, requestDto);
    }

    @Operation(summary = "Delete category by id", description = "Delete category by id")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void deleteCategory(@PathVariable Long id) {
        categoryService.deleteById(id);
    }

    @Operation(summary = "Get all books from specific category",
            description = "Get a list of all books from specific category by id")
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("/{id}/books")
    public List<BookDtoWithoutCategoryIds> getBooksByCategoryId(Pageable pageable,
                                                                @PathVariable Long id) {
        return bookService.getBooksByCategoryId(pageable, id);
    }
}
