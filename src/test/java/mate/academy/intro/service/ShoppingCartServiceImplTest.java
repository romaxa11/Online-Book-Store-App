package mate.academy.intro.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import mate.academy.intro.dto.cartitem.CartItemResponseDto;
import mate.academy.intro.dto.cartitem.CreateCartItemRequestDto;
import mate.academy.intro.dto.cartitem.UpdateCartItemRequestDto;
import mate.academy.intro.dto.shoppingcart.ShoppingCartResponseDto;
import mate.academy.intro.mapper.CartItemMapper;
import mate.academy.intro.mapper.ShoppingCartMapper;
import mate.academy.intro.model.Book;
import mate.academy.intro.model.CartItem;
import mate.academy.intro.model.Role;
import mate.academy.intro.model.ShoppingCart;
import mate.academy.intro.model.User;
import mate.academy.intro.repository.cartitem.CartItemRepository;
import mate.academy.intro.repository.shoppingcart.ShoppingCartRepository;
import mate.academy.intro.service.shoppingcart.impl.ShoppingCartServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ShoppingCartServiceImplTest {
    @InjectMocks
    private ShoppingCartServiceImpl shoppingCartServiceImpl;
    @Mock
    private ShoppingCartRepository shoppingCartRepository;
    @Mock
    private ShoppingCartMapper shoppingCartMapper;
    @Mock
    private CartItemMapper cartItemMapper;
    @Mock
    private CartItemRepository cartItemRepository;

    @Test
    @DisplayName("""
            Verify get shopping cart by user id method
            """)
    void getShoppingCartById_ValidUserId_ReturnShoppingCartResponseDto_Success() {
        // Given
        ShoppingCart shoppingCart = getShoppingCartWithOneCartItem();
        Long userId = shoppingCart.getUser().getId();

        ShoppingCartResponseDto expected = getShoppingCartResponseDtoByShoppingCart(shoppingCart);
        Mockito.when(shoppingCartRepository.findById(userId)).thenReturn(Optional.of(shoppingCart));
        Mockito.when(shoppingCartMapper.toDto(shoppingCart)).thenReturn(expected);
        // When
        ShoppingCartResponseDto actual =
                shoppingCartServiceImpl.getById(userId);
        // Then
        assertEquals(expected, actual);
        verify(shoppingCartRepository, times(1)).findById(userId);
        verify(shoppingCartMapper, times(1)).toDto(shoppingCart);
        verifyNoMoreInteractions(shoppingCartRepository, shoppingCartMapper);
    }

    @Test
    @DisplayName("""
            Verify add cart_item to shopping cart method
            """)
    void addCartItemToShoppingCart_ValidCartItem_UpdateShoppingCart_Success() {
        //Given
        ShoppingCart shoppingCart = getShoppingCartWithOneCartItem();
        final Long userId = shoppingCart.getUser().getId();

        ShoppingCart shoppingCartWithNewItem = new ShoppingCart();
        shoppingCartWithNewItem.setId(shoppingCart.getId());
        shoppingCartWithNewItem.setUser(shoppingCart.getUser());
        Set<CartItem> updatedCartItemsSet = new HashSet<>(shoppingCart.getCartItems());
        shoppingCartWithNewItem.setCartItems(updatedCartItemsSet);

        CartItem cartItem = getNewCartItem(shoppingCart);
        shoppingCartWithNewItem.getCartItems().add(cartItem);

        ShoppingCartResponseDto expected = getShoppingCartResponseDtoByShoppingCart(shoppingCart);
        CreateCartItemRequestDto createCartItemRequestDto =
                getCreateCartItemRequestDtoByCartItem(cartItem);

        Mockito.when(cartItemMapper.toEntity(createCartItemRequestDto)).thenReturn(cartItem);
        Mockito.when(shoppingCartRepository.findById(userId)).thenReturn(
                Optional.of(shoppingCart), Optional.of(shoppingCartWithNewItem));
        Mockito.when(shoppingCartMapper.toDto(shoppingCartWithNewItem)).thenReturn(expected);
        // When
        ShoppingCartResponseDto actual =
                shoppingCartServiceImpl.addCartItem(userId, createCartItemRequestDto);
        // Then
        assertEquals(expected, actual);
        verify(shoppingCartRepository, times(2)).findById(userId);
        verify(shoppingCartMapper, times(1)).toDto(shoppingCartWithNewItem);
        verify(cartItemMapper, times(1)).toEntity(createCartItemRequestDto);
        verify(cartItemRepository, times(1)).save(cartItem);
        verifyNoMoreInteractions(shoppingCartRepository, shoppingCartMapper, cartItemMapper,
                cartItemRepository);
    }

    @Test
    @DisplayName("""
            Verify update cart item in shopping cart method
            """)
    void updateCartItemInShoppingCart_ValidCartItem_UpdateShoppingCart_Success() {
        //Given
        ShoppingCart shoppingCart = getShoppingCartWithOneCartItem();
        final Long userId = shoppingCart.getUser().getId();

        final Long cartItemId = 1L;

        UpdateCartItemRequestDto updateCartItemRequestDto = new UpdateCartItemRequestDto();
        updateCartItemRequestDto.setQuantity(7);

        CartItem updateCartItem = new CartItem();
        updateCartItem.setQuantity(updateCartItemRequestDto.getQuantity());

        shoppingCart.getCartItems().forEach(
                s -> s.setQuantity(updateCartItemRequestDto.getQuantity()));

        ShoppingCartResponseDto expected = getShoppingCartResponseDtoByShoppingCart(shoppingCart);

        CartItem cartItemFromDb = shoppingCart.getCartItems().stream()
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Can't get CartItem"));

        Mockito.when(cartItemRepository.findById(cartItemId))
                .thenReturn(Optional.of(cartItemFromDb));
        Mockito.when(cartItemMapper.toEntity(updateCartItemRequestDto)).thenReturn(updateCartItem);
        Mockito.when(shoppingCartRepository.findById(userId)).thenReturn(Optional.of(shoppingCart));
        Mockito.when(shoppingCartMapper.toDto(shoppingCart)).thenReturn(expected);

        // When
        ShoppingCartResponseDto actual =
                shoppingCartServiceImpl.updateCartItem(userId, cartItemId,
                        updateCartItemRequestDto);
        // Then
        assertEquals(expected, actual);
        verify(cartItemRepository, times(1)).save(any());
        verify(cartItemMapper, times(1)).toEntity(updateCartItemRequestDto);
        verify(shoppingCartRepository, times(1)).findById(userId);
        verify(shoppingCartMapper, times(1)).toDto(shoppingCart);
        verifyNoMoreInteractions(shoppingCartRepository, shoppingCartMapper, cartItemMapper,
                cartItemRepository);
    }

    @Test
    @DisplayName("""
            Verify delete cart item method
            """)
    void deleteCartItem_ValidCartItemId_ReturnsShoppingCartResponseDtoWithoutExcludedCartItem() {
        // Given
        Long cartItemId = 1L;
        ShoppingCart shoppingCart = getShoppingCartWithOneCartItem();
        shoppingCart.getCartItems().clear();
        Long userId = shoppingCart.getUser().getId();

        ShoppingCartResponseDto expected = getShoppingCartResponseDtoByShoppingCart(shoppingCart);

        Mockito.when(shoppingCartRepository.findById(userId)).thenReturn(Optional.of(shoppingCart));
        Mockito.when(shoppingCartMapper.toDto(shoppingCart)).thenReturn(expected);
        // When
        ShoppingCartResponseDto actual =
                shoppingCartServiceImpl.deleteCartItem(userId, cartItemId);
        // Then
        assertEquals(expected, actual);
        verify(cartItemRepository, times(1)).deleteById(cartItemId);
        verify(shoppingCartRepository, times(1)).findById(userId);
        verify(shoppingCartMapper, times(1)).toDto(shoppingCart);
        verifyNoMoreInteractions(cartItemRepository, shoppingCartRepository, shoppingCartMapper);
    }

    @Test
    @DisplayName("""
            Verify clear shopping cart method
            """)
    void clearShoppingCart_ValidShoppingCart_DoesNotThrowException() {
        //Given
        ShoppingCart shoppingCart = getShoppingCartWithOneCartItem();
        // Then
        assertDoesNotThrow(() -> shoppingCartServiceImpl.clearShoppingCart(shoppingCart));
    }

    private ShoppingCartResponseDto getShoppingCartResponseDtoByShoppingCart(
            ShoppingCart shoppingCart
    ) {
        Set<CartItemResponseDto> cartItemResponseDtoSet = shoppingCart
                .getCartItems()
                .stream()
                .map(c -> new CartItemResponseDto()
                        .setId(c.getId())
                        .setBookId(String.valueOf(c.getBook().getId()))
                        .setBookTitle(c.getBook().getTitle())
                        .setQuantity(c.getQuantity()))
                .collect(Collectors.toSet());
        ShoppingCartResponseDto shoppingCartResponseDto = new ShoppingCartResponseDto();
        shoppingCartResponseDto.setUserId(shoppingCart.getUser().getId());
        shoppingCartResponseDto.setId(shoppingCart.getId());
        shoppingCartResponseDto.setCartItems(cartItemResponseDtoSet);
        return shoppingCartResponseDto;
    }

    private ShoppingCart getShoppingCartWithOneCartItem() {
        Book book = getBook();
        CartItem cartItem = new CartItem();
        cartItem.setId(1L);
        cartItem.setQuantity(1);
        cartItem.setBook(book);

        Set<CartItem> cartItems = new HashSet<>();
        cartItems.add(cartItem);

        ShoppingCart shoppingCart = new ShoppingCart();
        User user = getUser();
        shoppingCart.setUser(user);
        shoppingCart.setId(user.getId());
        shoppingCart.setCartItems(cartItems);
        return shoppingCart;
    }

    private CreateCartItemRequestDto getCreateCartItemRequestDtoByCartItem(CartItem cartItem) {
        return new CreateCartItemRequestDto()
                .setBookId(cartItem.getBook().getId())
                .setQuantity(cartItem.getQuantity());
    }

    private CartItem getNewCartItem(ShoppingCart shoppingCart) {
        Book book = new Book();
        book.setId(3L);
        book.setTitle("Title 3");

        CartItem cartItem = new CartItem();
        cartItem.setShoppingCart(shoppingCart);
        cartItem.setQuantity(4);
        cartItem.setBook(book);
        return cartItem;
    }

    private Book getBook() {
        Book book = new Book();
        book.setId(1L);
        book.setAuthor("Author 1");
        book.setTitle("Title 1");
        book.setIsbn("ISBN-10: 1-596-52068-1");
        book.setPrice(BigDecimal.valueOf(20));
        book.setDescription("Description One");
        book.setCoverImage("https://user@ukr.com/cover1.jpg");
        book.setCategories(new HashSet<>());
        return book;
    }

    private User getUser() {
        User user = new User();
        user.setId(1L);
        user.setEmail("romaxa@gmail.com");
        user.setPassword("12345");
        user.setFirstName("Riko");
        user.setLastName("Garcia");
        user.setShippingAddress("Mexico, Republic str.17");
        Role role = new Role();
        role.setRoleName(Role.RoleName.USER);
        user.setRoles(Set.of(role));
        return user;
    }
}
