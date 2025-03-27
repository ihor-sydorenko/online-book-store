package online.book.store.controller;

import static online.book.store.config.TestUtil.createBookDto;
import static online.book.store.config.TestUtil.createBookRequestDto;
import static org.apache.commons.lang3.builder.EqualsBuilder.reflectionEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import online.book.store.config.TestUtil;
import online.book.store.dto.book.BookDto;
import online.book.store.dto.book.CreateBookRequestDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
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
class BookControllerTest {
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

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    @DisplayName("Create new book - return new book")
    void createBook_ValidRequestDto_Success() throws Exception {
        CreateBookRequestDto requestDto = createBookRequestDto();
        BookDto expected = createBookDto(1L);
        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        MvcResult result = mockMvc.perform(post("/books")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isCreated())
                .andReturn();

        BookDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), BookDto.class);
        assertNotNull(actual);
        assertNotNull(actual.getId());
        assertTrue(reflectionEquals(expected, actual, "id"));
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    @DisplayName("Create new book with invalid request dto - return Bad request status")
    void createBook_InvalidRequestDto_ReturnBadRequest() throws Exception {
        CreateBookRequestDto requestDto = createBookRequestDto();
        requestDto.setIsbn("");
        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        mockMvc.perform(post("/books")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest())
                .andReturn();
    }

    @WithMockUser(username = "user", roles = {"USER"})
    @Test
    @DisplayName("Get all books - return a list of all available books")
    void getAll_GivenBooksInCatalog_ReturnAllBooks() throws Exception {
        List<BookDto> expected = TestUtil.getExpectedListOfBooks();

        MvcResult result = mockMvc.perform(get("/books")
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();

        BookDto[] actual = objectMapper.readValue(
                result.getResponse().getContentAsByteArray(), BookDto[].class);
        assertEquals(3, actual.length);
        assertEquals(expected, Arrays.stream(actual).toList());
    }

    @WithMockUser(username = "user", roles = {"USER"})
    @Test
    @DisplayName("Find book by existing id - return a book by id")
    void getBookById_ExistingId_ReturnBook() throws Exception {
        Long bookId = 1L;
        MvcResult result = mockMvc.perform(get("/books/{id}", bookId)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();

        BookDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), BookDto.class);
        assertNotNull(actual);
        assertNotNull(actual.getId());
    }

    @WithMockUser(username = "user", roles = {"USER"})
    @Test
    @DisplayName("Find book by non-existing id - return not found status")
    void getBookById_NonExistingId_ReturnNotFound() throws Exception {
        Long bookId = 11L;
        mockMvc.perform(get("/books/{id}", bookId)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNotFound())
                .andReturn();
    }

    @WithMockUser(username = "user", roles = {"ADMIN"})
    @Test
    @DisplayName("Update book by id - should return updated book")
    void updateBook_ValidUpdateRequestDto_ReturnUpdatedBook() throws Exception {
        Long bookId = 1L;
        CreateBookRequestDto requestDto = createBookRequestDto();
        BookDto expected = createBookDto(bookId);
        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        MvcResult result = mockMvc.perform(put("/books/{id}", bookId)
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();

        BookDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), BookDto.class);
        assertNotNull(actual);
        assertNotNull(actual.getId());
        assertTrue(reflectionEquals(expected, actual, "id"));

    }

    @WithMockUser(username = "user", roles = {"ADMIN"})
    @Test
    @DisplayName("Update book by id with invalid request dto - should return Bad Request status")
    void updateBook_InvalidUpdateRequestDto_ReturnBadRequest() throws Exception {
        Long bookId = 1L;
        CreateBookRequestDto requestDto = createBookRequestDto();
        requestDto.setPrice(BigDecimal.valueOf(-19));
        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        mockMvc.perform(put("/books/{id}", bookId)
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest())
                .andReturn();
    }

    @WithMockUser(username = "user", roles = {"ADMIN"})
    @Test
    @DisplayName("Delete book by existing id - delete book")
    void deleteBook_ByExistingId_DeleteBook() throws Exception {
        Long bookId = 1L;
        mockMvc.perform(delete("/books/{id}", bookId)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNoContent());
    }

    @Sql(scripts = "classpath:database/add-one-book.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @WithMockUser(username = "user", roles = {"USER"})
    @Test
    @DisplayName("Search books with parameters - should return matching books")
    void searchBooks_ValidSearchParameters_ReturnsListOfBooks() throws Exception {
        MvcResult result = mockMvc.perform(get(
                        "/books/search")
                        .param("title", "Title")
                        .param("author", "Author")
                        .param("isbn", "99")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        BookDto[] actual = objectMapper.readValue(result.getResponse()
                .getContentAsByteArray(), BookDto[].class);
        assertNotNull(actual);
        assertEquals(1, actual.length);
        assertEquals("Title", actual[0].getTitle());
    }
}
