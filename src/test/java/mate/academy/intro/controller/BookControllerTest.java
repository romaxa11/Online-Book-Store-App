package mate.academy.intro.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import javax.sql.DataSource;
import lombok.SneakyThrows;
import mate.academy.intro.dto.book.BookDto;
import mate.academy.intro.dto.book.CreateBookRequestDto;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class BookControllerTest {
    protected static MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @BeforeAll
    static void beforeAll(
            @Autowired DataSource dataSource,
            @Autowired WebApplicationContext applicationContext
    ) throws SQLException {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(applicationContext)
                .apply(springSecurity())
                .build();
        teardown(dataSource);
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource("database/books/add-three-default-books-to-table.sql")
            );
        }
    }

    @AfterAll
    static void afterAll(
            @Autowired DataSource dataSource
    ) {
        teardown(dataSource);
    }

    @SneakyThrows
    static void teardown(DataSource dataSource) {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource("database/books/delete-all-test-books.sql")
            );
        }
    }

    @Test
    @WithMockUser(username = "user")
    @DisplayName("""
            Get All Books
            """)
    void getAll_GivenBooksInCatalog_ShouldReturnAllBooks() throws Exception {
        //Given
        List<BookDto> expected = new ArrayList<>();
        expected.add(new BookDto()
                .setId(1L)
                .setTitle("Title 1")
                .setAuthor("Author 1")
                .setIsbn("ISBN-10: 1-596-52068-1")
                .setPrice(BigDecimal.valueOf(50))
                .setDescription("Description One")
                .setCoverImage("https://user@ukr.com/cover1.jpg")
                .setCategoriesIds(new HashSet<>())
        );
        expected.add(new BookDto()
                .setId(2L)
                .setTitle("Title 2")
                .setAuthor("Author 2")
                .setIsbn("ISBN-10: 2-596-52068-2")
                .setPrice(BigDecimal.valueOf(70))
                .setDescription("Description Two")
                .setCoverImage("https://user@ukr.com/cover2.jpg")
                .setCategoriesIds(new HashSet<>())
        );
        expected.add(new BookDto()
                .setId(3L)
                .setTitle("Title 3")
                .setAuthor("Author 3")
                .setIsbn("ISBN-10: 3-596-52068-3")
                .setPrice(BigDecimal.valueOf(90))
                .setDescription("Description Three")
                .setCoverImage("https://user@ukr.com/cover3.jpg")
                .setCategoriesIds(new HashSet<>())
        );
        // When
        MvcResult result = mockMvc.perform(get("/books")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        // Then
        BookDto[] actual = objectMapper.readValue(result.getResponse()
                .getContentAsByteArray(), BookDto[].class);
        assertEquals(3, actual.length);
        assertEquals(expected, Arrays.stream(actual).toList());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Sql(
            scripts = "classpath:database/books/delete-book-one-from-books-table.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
    )
    @DisplayName("""
            Create a new Book
            """)
    void createBook_ValidCreateBookRequestDto_Success() throws Exception {
        // Given
        CreateBookRequestDto createBookRequestDto =
                new CreateBookRequestDto()
                        .setAuthor("Author 0")
                        .setTitle("Title 0")
                        .setIsbn("ISBN-10: 0-596-52068-5")
                        .setPrice(BigDecimal.valueOf(20))
                        .setDescription("This is a sample book description.")
                        .setCoverImage("https://user@ukr.com/cover0.jpg")
                        .setCategoriesIds(new HashSet<>());

        BookDto expected =
                new BookDto()
                        .setAuthor(createBookRequestDto.getAuthor())
                        .setTitle(createBookRequestDto.getTitle())
                        .setIsbn(createBookRequestDto.getIsbn())
                        .setPrice(createBookRequestDto.getPrice())
                        .setDescription(createBookRequestDto.getDescription())
                        .setCoverImage(createBookRequestDto.getCoverImage())
                        .setCategoriesIds(createBookRequestDto.getCategoriesIds());

        String jsonRequest = objectMapper.writeValueAsString(createBookRequestDto);
        // When
        MvcResult result = mockMvc.perform(post("/books")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();
        // Then
        BookDto actual = objectMapper
                .readValue(result.getResponse().getContentAsString(), BookDto.class);
        assertNotNull(actual);
        assertNotNull(actual.getId());
        assertEquals(expected.getTitle(), actual.getTitle());
        assertEquals(expected.getTitle(), actual.getTitle());
        assertEquals(expected.getAuthor(), actual.getAuthor());
        assertEquals(expected.getIsbn(), actual.getIsbn());
        assertEquals(expected.getPrice(), actual.getPrice());
        assertEquals(expected.getDescription(), actual.getDescription());
        assertEquals(expected.getCoverImage(), actual.getCoverImage());
        EqualsBuilder.reflectionEquals(expected, actual, "id");
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("""
            Update the Book
            """)
    void updateBook_ValidUpdateBookRequestDto_Success() throws Exception {
        //Given
        CreateBookRequestDto createBookRequestDto =
                new CreateBookRequestDto()
                        .setAuthor("Update Author 2")
                        .setTitle("Update Title 2")
                        .setIsbn("ISBN-10: 2-596-52068-2")
                        .setPrice(BigDecimal.valueOf(20))
                        .setDescription("This is a sample book update.")
                        .setCoverImage("https://user@ukr.com/cover2.jpg")
                        .setCategoriesIds(new HashSet<>());

        BookDto expected =
                new BookDto()
                        .setId(2L)
                        .setAuthor(createBookRequestDto.getAuthor())
                        .setTitle(createBookRequestDto.getTitle())
                        .setIsbn(createBookRequestDto.getIsbn())
                        .setPrice(createBookRequestDto.getPrice())
                        .setDescription(createBookRequestDto.getDescription())
                        .setCoverImage(createBookRequestDto.getCoverImage())
                        .setCategoriesIds(createBookRequestDto.getCategoriesIds());

        String jsonRequest = objectMapper.writeValueAsString(createBookRequestDto);
        // When
        MvcResult result = mockMvc.perform(put("/books/2")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        //Then
        BookDto actual = objectMapper
                .readValue(result.getResponse().getContentAsString(), BookDto.class);
        assertEquals(expected, actual);
    }

    @Test
    @WithMockUser(username = "user")
    @DisplayName("""
            Get book by id
            """)
    void getBookById_ValidId_Success() throws Exception {
        //Given
        Long bookId = 3L;
        BookDto expected =
                new BookDto()
                        .setId(bookId)
                        .setAuthor("Author 3")
                        .setTitle("Title 3")
                        .setIsbn("ISBN-10: 3-596-52068-3")
                        .setPrice(BigDecimal.valueOf(90))
                        .setDescription("Description Three")
                        .setCoverImage("https://user@ukr.com/cover3.jpg")
                        .setCategoriesIds(new HashSet<>());
        // When
        MvcResult result = mockMvc.perform(get("/books/" + bookId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        // Then
        BookDto actual =
                objectMapper.readValue(result.getResponse().getContentAsString(), BookDto.class);
        assertNotNull(actual);
        assertEquals(expected, actual);
    }

    @Test
    @WithMockUser(username = "user")
    @DisplayName("""
            Get all books by author
            """)
    void getBookByAuthor_ValidAuthor_Success() throws Exception {
        //Given
        String author = "Author 3";
        BookDto expected =
                new BookDto()
                        .setId(3L)
                        .setAuthor(author)
                        .setTitle("Title 3")
                        .setIsbn("ISBN-10: 3-596-52068-3")
                        .setPrice(BigDecimal.valueOf(90))
                        .setDescription("Description Three")
                        .setCoverImage("https://user@ukr.com/cover3.jpg")
                        .setCategoriesIds(new HashSet<>());
        // When
        MvcResult result = mockMvc.perform(get("/books/author?author=" + author)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        // Then
        BookDto[] actual = objectMapper.readValue(result.getResponse()
                .getContentAsByteArray(), BookDto[].class);
        assertEquals(1, actual.length);
        assertEquals(expected, actual[0]);
    }

    @Test
    @WithMockUser(username = "user")
    @DisplayName("""
            Get all books by price
            """)
    void findAllByPrice_ValidPrice_Success() throws Exception {
        //Given
        BigDecimal from = BigDecimal.valueOf(80);
        BigDecimal to = BigDecimal.valueOf(90);
        BookDto expected =
                new BookDto()
                        .setId(3L)
                        .setAuthor("Author 3")
                        .setTitle("Title 3")
                        .setIsbn("ISBN-10: 3-596-52068-3")
                        .setPrice(BigDecimal.valueOf(90))
                        .setDescription("Description Three")
                        .setCoverImage("https://user@ukr.com/cover3.jpg")
                        .setCategoriesIds(new HashSet<>());
        // When
        MvcResult result = mockMvc.perform(get("/books/price?from=" + from + "&to=" + to)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        //Then
        BookDto[] actual = objectMapper.readValue(result.getResponse()
                .getContentAsByteArray(), BookDto[].class);
        assertEquals(1, Arrays.stream(actual).toList().size());
        assertEquals(expected, Arrays.stream(actual).toList().get(0));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("""
            Delete book by id
            """)
    void deleteBookById_ValidId_Success() throws Exception {
        //Given
        long bookId = 3L;
        // When
        MvcResult result = mockMvc.perform(delete("/books/" + bookId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent())
                .andReturn();
    }

    @Test
    @WithMockUser(username = "user")
    @DisplayName("""
            Get all books by price
            """)
    void searchBookByParameterAuthorTitle_ValidParameters_Success() throws Exception {
        //Given
        BookDto expected =
                new BookDto()
                        .setId(3L)
                        .setAuthor("Author 3")
                        .setTitle("Title 3")
                        .setIsbn("ISBN-10: 3-596-52068-3")
                        .setPrice(BigDecimal.valueOf(90))
                        .setDescription("Description Three")
                        .setCoverImage("https://user@ukr.com/cover3.jpg")
                        .setCategoriesIds(new HashSet<>());
        //When
        MvcResult result = mockMvc.perform(get("/books/search?titles=Title 3&authors=Author 3")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        //Then
        BookDto[] actual = objectMapper.readValue(result.getResponse()
                .getContentAsByteArray(), BookDto[].class);
        assertEquals(1, Arrays.stream(actual).toList().size());
        assertEquals(expected, Arrays.stream(actual).toList().get(0));
    }
}
