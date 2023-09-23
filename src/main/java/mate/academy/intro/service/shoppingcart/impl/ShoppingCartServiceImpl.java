package mate.academy.intro.service.shoppingcart.impl;

import java.util.HashSet;
import lombok.RequiredArgsConstructor;
import mate.academy.intro.dto.cartitem.CreateCartItemRequestDto;
import mate.academy.intro.dto.cartitem.UpdateCartItemRequestDto;
import mate.academy.intro.dto.shoppingcart.ShoppingCartResponseDto;
import mate.academy.intro.exception.DuplicateEntityException;
import mate.academy.intro.exception.EntityNotFoundException;
import mate.academy.intro.mapper.CartItemMapper;
import mate.academy.intro.mapper.ShoppingCartMapper;
import mate.academy.intro.model.CartItem;
import mate.academy.intro.model.ShoppingCart;
import mate.academy.intro.repository.cartitem.CartItemRepository;
import mate.academy.intro.repository.shoppingcart.ShoppingCartRepository;
import mate.academy.intro.service.shoppingcart.ShoppingCartService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ShoppingCartServiceImpl implements ShoppingCartService {
    private final ShoppingCartRepository shoppingCartRepository;
    private final ShoppingCartMapper shoppingCartMapper;
    private final CartItemRepository cartItemRepository;
    private final CartItemMapper cartItemMapper;

    @Override
    public ShoppingCartResponseDto getById(Long userId) {
        return shoppingCartMapper.toDto(getShoppingCartById(userId));
    }

    @Override
    public ShoppingCartResponseDto addCartItem(
            Long userId,
            CreateCartItemRequestDto requestDto
    ) {
        ShoppingCart shoppingCart = getShoppingCartById(userId);
        CartItem cartItem = cartItemMapper.toEntity(requestDto);
        if (shoppingCart.getCartItems().contains(cartItem)) {
            throw new DuplicateEntityException("Book has been already added");
        }
        cartItem.setShoppingCart(shoppingCart);
        cartItemRepository.save(cartItem);
        return shoppingCartMapper.toDto(getShoppingCartById(userId));
    }

    @Override
    public ShoppingCartResponseDto updateCartItem(
            Long userId,
            Long cartItemId,
            UpdateCartItemRequestDto requestDto
    ) {
        CartItem cartItemFromDb = cartItemRepository.findById(cartItemId).orElseThrow(
                () -> new EntityNotFoundException("Can't find cart item by id " + cartItemId));
        CartItem cartItem = cartItemMapper.toEntity(requestDto);
        cartItemFromDb.setQuantity(cartItem.getQuantity());
        cartItemRepository.save(cartItemFromDb);
        return shoppingCartMapper.toDto(getShoppingCartById(userId));
    }

    @Override
    public ShoppingCartResponseDto deleteCartItem(Long userId, Long cartItemId) {
        cartItemRepository.deleteById(cartItemId);
        return shoppingCartMapper.toDto(getShoppingCartById(userId));
    }

    @Override
    public void clearShoppingCart(ShoppingCart shoppingCart) {
        shoppingCart.setCartItems(new HashSet<>());
        cartItemRepository.deleteByShoppingCart_Id(shoppingCart.getId());
    }

    public ShoppingCart getShoppingCartById(Long userId) {
        return shoppingCartRepository.findById(userId)
                .orElseThrow(() ->
                        new EntityNotFoundException("Can't find shopping cart by userId "
                                + userId));

    }
}
