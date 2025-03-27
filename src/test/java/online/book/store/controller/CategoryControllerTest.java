package online.book.store.controller;

import static online.book.store.config.TestUtil.createCategoryDto;
import static online.book.store.config.TestUtil.createCategoryRequestDto;
import static org.apache.commons.lang3.builder.EqualsBuilder.reflectionEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import online.book.store.config.CustomPageImpl;
import online.book.store.dto.book.BookDtoWithoutCategoryIds;
import online.book.store.dto.category.CategoryDto;
import online.book.store.dto.category.CategoryRequestDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@ExtendWith(MockitoExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(scripts = {"classpath:database/add-categories-to-categories-table.sql",
        "classpath:database/add-books-to-books-table.sql",
        "classpath:database/add-books-categories-relationship.sql"
}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "classpath:database/delete-all.sql",
        executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class CategoryControllerTest {
    protected static MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void beforeAll(
            @Autowired WebApplicationContext applicationContext) {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(applicationContext)
                .apply(springSecurity())
                .build();
    }

    @WithMockUser(username = "admin", roles = {"USER"})
    @Test
    @DisplayName("Get all categories - return page with all exist categories")
    void getAll_ReturnAllCategories() throws Exception {
        MvcResult result = mockMvc.perform(get("/categories")
                        .param("page", "0")
                        .param("size", "5")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        JavaType type = objectMapper.getTypeFactory()
                .constructParametricType(CustomPageImpl.class, CategoryDto.class);
        PageImpl<CategoryDto> actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), type);
        assertNotNull(actual);
        assertFalse(actual.getContent().isEmpty());
        assertEquals(2, actual.getContent().size());
    }

    @WithMockUser(username = "admin", roles = {"USER"})
    @Test
    @DisplayName("Find category by existing id - return a category by id")
    void getCategoryById_ByExistingId_ReturnCategory() throws Exception {
        Long categoryId = 1L;
        CategoryDto expected = createCategoryDto(categoryId);
        MvcResult result = mockMvc.perform(get("/categories/{id}", categoryId)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();

        CategoryDto actual = objectMapper
                .readValue(result.getResponse().getContentAsString(), CategoryDto.class);

        assertNotNull(actual);
        assertTrue(reflectionEquals(expected, actual, "id"));
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    @DisplayName("Create new category with valid request dto - return new category")
    void createCategory_ValidRequestDto_Success() throws Exception {
        CategoryRequestDto requestDto = createCategoryRequestDto();
        CategoryDto expected = createCategoryDto(1L);
        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        MvcResult result = mockMvc.perform(post("/categories")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isCreated())
                .andReturn();

        CategoryDto actual = objectMapper.readValue(result.getResponse().getContentAsString(),
                CategoryDto.class);
        assertNotNull(actual);
        assertNotNull(actual.getId());
        assertTrue(reflectionEquals(expected, actual, "id"));
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    @DisplayName("Create new category with invalid request dto - return Bad request status")
    void createCategory_InvalidRequestDto_ReturnBadRequest() throws Exception {
        CategoryRequestDto requestDto = createCategoryRequestDto();
        requestDto.setName("");
        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        mockMvc.perform(post("/categories")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest())
                .andReturn();
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    @DisplayName("Update category by id - should return updated category")
    void updateCategory_ValidUpdateRequest_ReturnUpdatedCategory() throws Exception {
        Long categoryId = 1L;
        CategoryRequestDto requestDto = createCategoryRequestDto();
        requestDto.setName("updatedName");
        CategoryDto expected = createCategoryDto(categoryId);
        expected.setName("updatedName");
        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        MvcResult result = mockMvc.perform(put("/categories/{id}", categoryId)
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto))
                )
                .andExpect(status().isOk())
                .andReturn();

        CategoryDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), CategoryDto.class);
        assertNotNull(actual);
        assertTrue(reflectionEquals(expected, actual, "id"));
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    @DisplayName("Delete category by existing id - delete category")
    void deleteCategory() throws Exception {
        Long categoryId = 1L;
        mockMvc.perform(delete("/categories/{id}", categoryId)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNoContent());
    }

    @WithMockUser(username = "admin", roles = {"USER"})
    @Test
    @DisplayName("Get books by category id - return page of all books in category")
    void getBooksByCategoryId_ExistingId_ReturnBookList() throws Exception {
        Long categoryId = 2L;

        MvcResult result = mockMvc.perform(get("/categories/{id}/books", categoryId)
                        .param("page", "0")
                        .param("size", "5")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        JavaType type = objectMapper.getTypeFactory()
                .constructParametricType(CustomPageImpl.class, BookDtoWithoutCategoryIds.class);
        PageImpl<BookDtoWithoutCategoryIds> actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), type);

        assertNotNull(actual);
        assertFalse(actual.getContent().isEmpty());
        assertEquals(1, actual.getContent().size());
    }
}
