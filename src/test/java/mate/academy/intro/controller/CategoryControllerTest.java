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
import java.util.List;
import javax.sql.DataSource;
import lombok.SneakyThrows;
import mate.academy.intro.dto.book.BookDtoWithoutCategoryIds;
import mate.academy.intro.dto.category.CategoryDto;
import mate.academy.intro.dto.category.CreateCategoryRequestDto;
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
public class CategoryControllerTest {
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
                    new ClassPathResource(
                            "database/categories/add-three-default-categories-to-table.sql"
                    )
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
                    new ClassPathResource("database/categories/delete-all-categories.sql")
            );
        }
    }

    @Test
    @WithMockUser(username = "user")
    @DisplayName("""
            Get All Categories
            """)
    void getAll_GivenCategories_ShouldReturnAllCategories() throws Exception {
        //Given
        List<CategoryDto> expected = new ArrayList<>();
        expected.add(new CategoryDto()
                .setId(1L)
                .setName("Crime")
                .setDescription("Crime category"));
        expected.add(new CategoryDto()
                .setId(2L)
                .setName("Fantasy")
                .setDescription("Fantasy category"));
        expected.add(new CategoryDto()
                .setId(3L)
                .setName("Historical fiction")
                .setDescription("Historical fiction category"));
        //When
        MvcResult result = mockMvc.perform(get("/categories")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        //Then
        CategoryDto[] actual = objectMapper.readValue(result.getResponse()
                .getContentAsByteArray(), CategoryDto[].class);
        assertEquals(3, actual.length);
        assertEquals(expected, Arrays.stream(actual).toList());
    }

    @Test
    @WithMockUser(username = "user")
    @DisplayName("""
            Get Category by id
            """)
    void getCategoryById_ValidId_Success() throws Exception {
        Long categoryId = 3L;
        CategoryDto expected =
                new CategoryDto()
                        .setId(categoryId)
                        .setName("Historical fiction")
                        .setDescription("Historical fiction category");
        //When
        MvcResult result = mockMvc.perform(get("/categories/" + categoryId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        //Then
        CategoryDto actual =
                objectMapper.readValue(result.getResponse()
                        .getContentAsString(), CategoryDto.class);
        assertNotNull(actual);
        assertEquals(expected, actual);
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Sql(
            scripts = "classpath:database/categories/add-new-category-to-table.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    @DisplayName("""
            Delete category by id
            """)
    void deleteCategoryById_ValidId_Success() throws Exception {
        //Given
        long categoryId = 4L;
        // When
        MvcResult result = mockMvc.perform(delete("/categories/" + categoryId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent())
                .andReturn();
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("""
            Create a new Category
            """)
    void createCategory_ValidCreateCategoryRequestDto_Success() throws Exception {
        //Given
        CreateCategoryRequestDto createCategoryRequestDto =
                new CreateCategoryRequestDto()
                        .setName("Horror stories")
                        .setDescription("Horror stories category");

        CategoryDto expected =
                new CategoryDto()
                        .setName(createCategoryRequestDto.getName())
                        .setDescription(createCategoryRequestDto.getDescription());

        String jsonRequest = objectMapper.writeValueAsString(createCategoryRequestDto);
        //When
        MvcResult result = mockMvc.perform(post("/categories")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();
        //Then
        CategoryDto actual =
                objectMapper.readValue(result.getResponse()
                        .getContentAsString(), CategoryDto.class);
        assertNotNull(actual);
        assertNotNull(actual.getId());
        EqualsBuilder.reflectionEquals(expected, actual, "id");
        assertEquals(5L, actual.getId());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("""
            Update the Category
            """)
    void updateCategory_ValidUpdateCategoryRequestDto_Success() throws Exception {
        //Given
        CreateCategoryRequestDto createCategoryRequestDto =
                new CreateCategoryRequestDto()
                        .setName("Update Fantasy")
                        .setDescription("Update Fantasy category");
        CategoryDto expected =
                new CategoryDto()
                        .setId(2L)
                        .setName(createCategoryRequestDto.getName())
                        .setDescription(createCategoryRequestDto.getDescription());

        String jsonRequest = objectMapper.writeValueAsString(createCategoryRequestDto);
        //When
        MvcResult result = mockMvc.perform(put("/categories/2")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        //Then
        CategoryDto actual =
                objectMapper.readValue(result.getResponse()
                        .getContentAsString(), CategoryDto.class);
        assertEquals(expected, actual);
    }

    @Test
    @WithMockUser(username = "user")
    @Sql(
            scripts = "classpath:database/categories/add-new-book-to-book-table.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    @Sql(
            scripts = "classpath:database/categories/add-category-to-books-categories-table.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    @Sql(
            scripts = "classpath:database/categories/delete-all-from-books-categories-table.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
    )
    @Sql(
            scripts = "classpath:database/categories/delete-all-test-books.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
    )
    @DisplayName("""
            Get Book by CategoryId
            """)
    void getBooksByCategoryId_ValidBookCategoryId_Success() throws Exception {
        //Given
        Long categoryId = 3L;
        List<BookDtoWithoutCategoryIds> expected = new ArrayList<>();
        expected.add(new BookDtoWithoutCategoryIds()
                .setId(1L)
                .setTitle("Title 1")
                .setAuthor("Author 1")
                .setIsbn("ISBN-10: 1-596-52068-1")
                .setPrice(BigDecimal.valueOf(50))
                .setDescription("Description One")
                .setCoverImage("https://user@ukr.com/cover1.jpg"));
        //When
        MvcResult result = mockMvc.perform(get("/categories/3/books")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        //Then
        BookDtoWithoutCategoryIds[] actual = objectMapper.readValue(result.getResponse()
                .getContentAsByteArray(), BookDtoWithoutCategoryIds[].class);
        assertEquals(1, actual.length);
        assertEquals(expected, Arrays.stream(actual).toList());
    }
}
