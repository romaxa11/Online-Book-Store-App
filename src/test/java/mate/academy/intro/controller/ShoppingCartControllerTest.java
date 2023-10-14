package mate.academy.intro.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;
import javax.sql.DataSource;
import lombok.SneakyThrows;
import mate.academy.intro.dto.cartitem.CartItemResponseDto;
import mate.academy.intro.dto.cartitem.UpdateCartItemRequestDto;
import mate.academy.intro.dto.shoppingcart.ShoppingCartResponseDto;
import mate.academy.intro.model.Role;
import mate.academy.intro.model.User;
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
public class ShoppingCartControllerTest {
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
                            "database/users/add-user-with-shopping-cart-to-tables.sql"
                    )
            );
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource(
                            "database/books/add-three-default-books-to-table.sql"
                    )
            );
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource(
                            "database/cartitems/add-book-to-cartitems-table.sql"
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
                    new ClassPathResource(
                            "database/cartitems/delete-from-cartitems.sql"
                    )
            );
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource(
                            "database/users/delete-from-users-shoppingcarts.sql"
                    )
            );
        }
    }

    @Test
    @WithMockUser(username = "user")
    @DisplayName("""
            Get user's shopping_cart
            """)
    void getShoppingCartByUser_ValidUser_ReturnUserShoppingCart_Success() throws Exception {
        //Given
        User mockUser = getMockUser();
        ShoppingCartResponseDto expected = getShoppingCartResponseDtoWithOneCartItem();
        // When
        MvcResult result = mockMvc.perform(get("/cart")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(user(mockUser)))
                .andExpect(status().isOk())
                .andReturn();
        // Then
        ShoppingCartResponseDto actual = objectMapper.readValue(result.getResponse()
                .getContentAsString(), ShoppingCartResponseDto.class);
        assertEquals(expected, actual);
    }

    @Test
    @WithMockUser(username = "user")
    @Sql(
            scripts = "classpath:database/cartitems/delete-second-cart-item.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
    )
    @DisplayName("""
            Add book to user's shopping_cart
            """)
    void addBookToShoppingCart_ReturnsUpdatedShoppingCart() throws Exception {
        //Given
        User mockUser = getMockUser();
        CartItemResponseDto expected = new CartItemResponseDto();
        expected.setId(2L);
        expected.setBookId("2");
        expected.setBookTitle("Title 2");
        expected.setQuantity(1);

        String jsonRequest = objectMapper.writeValueAsString(expected);
        // When
        MvcResult result = mockMvc.perform(post("/cart")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(user(mockUser)))
                .andExpect(status().isOk())
                .andReturn();
        // Then
        ShoppingCartResponseDto actual = objectMapper.readValue(result.getResponse()
                .getContentAsString(), ShoppingCartResponseDto.class);
        assertNotNull(actual);
        assertThat(actual.getCartItems()).hasSize(2);
        assertEquals(actual.getCartItems().size(), 2);
        assertTrue(actual.getCartItems().stream()
                .anyMatch(c -> expected.getBookId().equals((c.getBookId()))
                        && expected.getQuantity() == c.getQuantity()));
    }

    @Test
    @WithMockUser(username = "user")
    @Sql(
            scripts = "classpath:database/cartitems/delete-update-item-quantity.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
    )
    @DisplayName("""
            Update item's quantity of user's shopping_cart
            """)
    void updateCartItemQuantity_ReturnsUpdatedShoppingCart() throws Exception {
        //Given
        User mockUser = getMockUser();
        long cartItemId = 1L;
        UpdateCartItemRequestDto expected = new UpdateCartItemRequestDto();
        expected.setQuantity(10);

        String jsonRequest = objectMapper.writeValueAsString(expected);
        // When
        MvcResult result = mockMvc.perform(put("/cart/cart-items/" + cartItemId)
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(user(mockUser)))
                .andExpect(status().isOk())
                .andReturn();
        // Then
        ShoppingCartResponseDto actual = objectMapper.readValue(result.getResponse()
                .getContentAsString(), ShoppingCartResponseDto.class);
        assertNotNull(actual);
        assertThat(actual.getCartItems()).hasSize(1);
        assertTrue(actual.getCartItems().stream()
                .anyMatch(c -> expected.getQuantity() == c.getQuantity()));
    }

    @Test
    @WithMockUser(username = "user")
    @DisplayName("""
            Delete cart item from user's shopping_cart
            """)
    void deleteCartItemFromShoppingCart_ReturnsUpdatedShoppingCart() throws Exception {
        //Given
        User mockUser = getMockUser();
        long cartItemId = 1L;
        // When
        MvcResult result = mockMvc.perform(delete("/cart/cart-items/" + cartItemId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(user(mockUser)))
                .andExpect(status().isOk())
                .andReturn();
        // Then
        ShoppingCartResponseDto actual = objectMapper.readValue(result.getResponse()
                .getContentAsString(), ShoppingCartResponseDto.class);
        assertNotNull(actual);
        assertEquals(actual.getCartItems().size(), 0);
    }

    private ShoppingCartResponseDto getShoppingCartResponseDtoWithOneCartItem() {
        CartItemResponseDto cartItemResponseDto = new CartItemResponseDto();
        cartItemResponseDto.setId(1L);
        cartItemResponseDto.setBookId("1");
        cartItemResponseDto.setBookTitle("Title 1");
        cartItemResponseDto.setQuantity(3);
        ShoppingCartResponseDto shoppingCartResponseDto = new ShoppingCartResponseDto();
        shoppingCartResponseDto.setId(1L);
        shoppingCartResponseDto.setUserId(1L);
        shoppingCartResponseDto.setCartItems(Set.of(cartItemResponseDto));
        return shoppingCartResponseDto;
    }

    private User getMockUser() {
        User user = new User();
        user.setId(1L);
        Set<Role> roles = new HashSet<>();
        Role role = new Role();
        role.setRoleName(Role.RoleName.USER);
        roles.add(role);
        user.setRoles(roles);
        return user;
    }
}
