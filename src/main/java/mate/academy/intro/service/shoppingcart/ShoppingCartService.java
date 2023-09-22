package mate.academy.intro.service.shoppingcart;

import mate.academy.intro.dto.cartitem.CreateCartItemRequestDto;
import mate.academy.intro.dto.cartitem.UpdateCartItemRequestDto;
import mate.academy.intro.dto.shoppingcart.ShoppingCartResponseDto;
import mate.academy.intro.model.ShoppingCart;

public interface ShoppingCartService {
    ShoppingCartResponseDto getById(Long userId);

    ShoppingCartResponseDto addCartItem(Long userId, CreateCartItemRequestDto requestDto);

    ShoppingCartResponseDto updateCartItem(Long userId, Long cartItemId,
                                           UpdateCartItemRequestDto requestDto);

    ShoppingCartResponseDto deleteCartItem(Long userId, Long cartItemId);

    ShoppingCart getShoppingCartById(Long userId);

    void clearShoppingCart(ShoppingCart shoppingCart);
}
